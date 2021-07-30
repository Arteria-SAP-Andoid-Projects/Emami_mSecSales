package com.arteriatech.emami.reports;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.retailerStock.RetailerStockBean;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by ${e10526} on ${04-05-2016}.
 *
 */
public class RetailerStockActivity extends AppCompatActivity {
    private ArrayList<RetailerStockBean> alRetailerStock =null;
    private boolean mBooleanRemoveScrollViews = true;
    private String mStrBundleRetailerNo = "";
    private String mStrBundleRetailerName = "";
    private String mStrBundleCPGUID = "";
    private String statusStr[]={Constants.Desc,Constants.Code};
    EditText mat_search;
    Spinner filterType;
    String  selectedType = "";

    private ArrayList<RetailerStockBean> filteredArraylist=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_retailer_stock));

        setContentView(R.layout.activity_retailer_stock);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle bundleExtras = getIntent().getExtras();

        if (bundleExtras != null) {
            mStrBundleRetailerNo = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetailerName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID= bundleExtras.getString(Constants.CPGUID);
        }
        if (!Constants.restartApp(RetailerStockActivity.this)) {
            initUI();
        }
    }

    /*Initializes UI*/
    void initUI(){
        filterType=(Spinner)findViewById(R.id.sp_ret_stock_status_type);
        mat_search = (EditText)findViewById(R.id.ed_ret_stock_inputSearch);

        TextView  retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);

        retName.setText(mStrBundleRetailerName);
        retId.setText(Constants.getRetMobileNo(mStrBundleRetailerNo));
        getRetailerStockList();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, statusStr);
        adapter.setDropDownViewResource(R.layout.spinnerinside);
        filterType.setAdapter(adapter);

        filterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                selectedType = statusStr[position];
                mat_search.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        mat_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {

                filteredArraylist = new ArrayList<RetailerStockBean>();
                if(alRetailerStock != null){

                    for (int i = 0; i < alRetailerStock.size(); i++) {
                        RetailerStockBean item = alRetailerStock.get(i);
                        if(selectedType.equalsIgnoreCase(Constants.Desc)){
                            if (item.getMaterialDesc().toLowerCase()
                                    .contains(cs.toString().toLowerCase().trim())) {
                                filteredArraylist.add(item);

                            }
                        }else{
                            if(item.getMaterialNo().toLowerCase().contains(cs.toString().toLowerCase().trim())){
                                filteredArraylist.add(item);
                            }
                        }
                    }
                }

                displayRetailerStockValues(filteredArraylist);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /*Gets Retailer Stock as list*/
    private  void getRetailerStockList(){
        try {
            String mStrMyStockQry= Constants.CPStockItems+"?$filter="+Constants.CPGUID +"eq '"+mStrBundleCPGUID.toUpperCase()+"' ";
            alRetailerStock = OfflineManager.getRetailerStockList(mStrMyStockQry);
            displayRetailerStockValues(alRetailerStock);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    /*Displays retailer stock values*/
    @SuppressLint("InflateParams")
    private void displayRetailerStockValues(ArrayList<RetailerStockBean> filteredArraylist) {

        ScrollView scroll_retailer_stock_list = (ScrollView) findViewById(R.id.scroll_retailer_stock_list);
        if (!mBooleanRemoveScrollViews) {
            scroll_retailer_stock_list.removeAllViews();
        }

        mBooleanRemoveScrollViews = false;

        @SuppressLint("InflateParams")
        TableLayout tlMyStock = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);
        LinearLayout llRetailerStock;
        if(filteredArraylist !=null){
            if (!filteredArraylist.isEmpty()
                    && filteredArraylist.size() > 0 ) {

                final ImageView[] ivSerialNoSel;
                ivSerialNoSel = new ImageView[filteredArraylist.size()];

                for (int i = 0; i < filteredArraylist.size(); i++) {
                    final RetailerStockBean retailerStockBean = filteredArraylist.get(i);

                    llRetailerStock = (LinearLayout) LayoutInflater.from(this)
                            .inflate(R.layout.retailer_stock_line_item,
                                    null,false);

                    ((TextView) llRetailerStock.findViewById(R.id.tv_mat_desc_value))
                            .setText(filteredArraylist.get(i).getMaterialDesc());

                    ((TextView) llRetailerStock.findViewById(R.id.tv_mat_code_value))
                            .setText(filteredArraylist.get(i).getMaterialNo());

                    ((TextView) llRetailerStock
                            .findViewById(R.id.tv_stock_qty_value))
                            .setText(UtilConstants.removeLeadingZeroVal(filteredArraylist.get(i).getQAQty()));

                    ((TextView) llRetailerStock
                            .findViewById(R.id.tv_stock_value))
                            .setText(Constants.removeLeadingZero(filteredArraylist.get(i).getStockValue()));

                    ivSerialNoSel[i] =  (ImageView)llRetailerStock
                            .findViewById(R.id.iv_serial_selection);

                    ivSerialNoSel[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intentMatSerialNosActivity = new Intent(RetailerStockActivity.this,
                                    RetailerMaterialSerialNoActivity.class);
                            intentMatSerialNosActivity.putExtra(Constants.CPStockItemGUID, retailerStockBean.getCPStockItemGUID());
                            intentMatSerialNosActivity.putExtra(Constants.TotalQty, retailerStockBean.getQAQty());
                            intentMatSerialNosActivity.putExtra(Constants.TotalValue, retailerStockBean.getStockValue());
                            intentMatSerialNosActivity.putExtra(Constants.MaterialNo, retailerStockBean.getMaterialNo());
                            intentMatSerialNosActivity.putExtra(Constants.MaterialDesc, retailerStockBean.getMaterialDesc());
                            intentMatSerialNosActivity.putExtra(Constants.UOM, retailerStockBean.getUom());
                            intentMatSerialNosActivity.putExtra(Constants.Currency, retailerStockBean.getCurrency());
                            startActivity(intentMatSerialNosActivity);

                        }
                    });
                    tlMyStock.addView(llRetailerStock);
                }

            }else{

                llRetailerStock = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.no_data_found_ll,
                                null,false);

                tlMyStock.addView(llRetailerStock);
            }
        }else{

            llRetailerStock = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.no_data_found_ll,
                            null,false);

            tlMyStock.addView(llRetailerStock);
        }



        scroll_retailer_stock_list.addView(tlMyStock);
        scroll_retailer_stock_list.requestLayout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }



}
