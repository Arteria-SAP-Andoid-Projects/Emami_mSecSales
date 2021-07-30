package com.arteriatech.emami.reports;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by ${e10526} on ${09-05-2016}.
 *
 */
public class RetailerMaterialSerialNoActivity extends AppCompatActivity {
    private  String mStrSPStockItemGUID="",mStrMatNo="",mStrTotalQty="",mStrMatDesc="",mStrTotalValue="",mStrUom="",mStrCurrency="";
    private ArrayList<RetailerStockBean> alSerialNoList = null;
    EditText searchSerialNo;
    ArrayList<RetailerStockBean> filteredArraylist;
    private boolean mBooleanRemoveScrollViews = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_material_serial_numbers));

        setContentView(R.layout.activity_retailer_material_serial_number);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrSPStockItemGUID = bundleExtras.getString(Constants.CPStockItemGUID);
            mStrTotalQty = bundleExtras.getString(Constants.TotalQty);
            mStrTotalValue = bundleExtras.getString(Constants.TotalValue);
            mStrMatNo = bundleExtras.getString(Constants.MaterialNo);
            mStrMatDesc = bundleExtras.getString(Constants.MaterialDesc);
            mStrUom = bundleExtras.getString(Constants.UOM);
            mStrCurrency = bundleExtras.getString(Constants.Currency);
        }
        if (!Constants.restartApp(RetailerMaterialSerialNoActivity.this)) {
            initUI();
        }
    }

    /*Initializes UI*/
    void initUI(){
        searchSerialNo = (EditText)findViewById(R.id.edit_stock_search);
        TextView tv_mat_desc=(TextView)findViewById(R.id.tv_mat_desc);
        TextView tv_mat_code=(TextView)findViewById(R.id.tv_mat_code);
        TextView tv_total_qty=(TextView)findViewById(R.id.tv_total_qty);
        TextView tv_total_value=(TextView)findViewById(R.id.tv_total_value);
        tv_mat_desc.setText(mStrMatDesc);
        tv_mat_code.setText(mStrMatNo);
        tv_total_qty.setText(Constants.removeLeadingZero(String.valueOf(Double.parseDouble(mStrTotalQty))));
        tv_total_value.setText(getString(R.string.str_rupee_symbol)+" "+ Constants.removeLeadingZero(mStrTotalValue));


        try {
            alSerialNoList = OfflineManager.getRetailerStockSerialNo(Constants.CPStockItemSnos + "?$filter=" + Constants.CPStockItemGUID
                    + " eq guid'" + mStrSPStockItemGUID + "' and  " + Constants.Option + " eq 'BT' and "+ Constants.StockTypeID+" eq '1' ", "BT");

            displaySerialNos(alSerialNoList);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }

        searchSerialNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filteredArraylist = new ArrayList<>();

                if (s.length()==0) {
                    displaySerialNos(alSerialNoList);
                } else {
                    for (int i = 0; i < alSerialNoList.size(); i++) {

                        RetailerStockBean bean = alSerialNoList.get(i);
                        String inputNo = s.toString();
                        String fromNo = UtilConstants.removeAlphanumeric(bean.getSerialNoFrom());
                        String ToNo = UtilConstants.removeAlphanumeric(bean.getSerialNoTo());
                        if (!inputNo.equalsIgnoreCase("")) {
                            BigInteger mFromNo = new BigInteger(fromNo);
                            BigInteger mToNo = new BigInteger(ToNo);
                            if (bean.getSerialNoFrom().contains(s.toString())) {
                                filteredArraylist.add(bean);
                            } else if (bean.getSerialNoTo().contains(s.toString())) {
                                filteredArraylist.add(bean);
                            } else if (UtilConstants.isInRangeBetweenNoAval(inputNo, mFromNo, mToNo)) {
                                filteredArraylist.add(bean);
                            }

                        }

                    }
                    displaySerialNos(filteredArraylist);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 0) {

                }
            }
        });

    }

    /*Displays serial numbers in list*/
    private void displaySerialNos(ArrayList<RetailerStockBean> filteredArraylist) {

        ScrollView scroll_my_stock_list = (ScrollView) findViewById(R.id.scroll_retailer_stock_list);

        if (!mBooleanRemoveScrollViews) {
            scroll_my_stock_list.removeAllViews();
        }
        mBooleanRemoveScrollViews = false;

        if(filteredArraylist!=null && filteredArraylist.size()>0) {
            Collections.sort(filteredArraylist, new Comparator<RetailerStockBean>() {
                @Override
               /* public int compare(RetailerStockBean firstBean, RetailerStockBean seondBean) {
                    return firstBean.getSerialNoFrom().compareTo(seondBean.getSerialNoFrom());
                }*/

                public int compare(RetailerStockBean one, RetailerStockBean other) {
                    BigInteger i1=null;
                    BigInteger i2=null;
                    try {
                        i1 =new BigInteger(one.getSerialNoFrom());
                    } catch (NumberFormatException e) {
                    }

                    try {
                        i2 =new BigInteger(other.getSerialNoFrom());
                    } catch (NumberFormatException e) {
                    }

                    if(i1!=null && i2!=null){
                        return i1.compareTo(i2);
                    }else{
                        return one.getSerialNoFrom().compareTo(other.getSerialNoFrom());
                    }
                }
            });
        }

        @SuppressLint("InflateParams")
        TableLayout tlSerialNoList = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);


        LinearLayout llSerialNoList;
        if( filteredArraylist !=null && filteredArraylist.size()>0) {
            for (int i = 0; i < filteredArraylist.size(); i++) {

                llSerialNoList = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.my_stock_from_to_line_item,
                                null, false);
                ((TextView) llSerialNoList.findViewById(R.id.tv_from_my_stock))
                        .setText(filteredArraylist.get(i).getSerialNoFrom());

                ((TextView) llSerialNoList.findViewById(R.id.tv_to_my_stock))
                        .setText(filteredArraylist.get(i).getSerialNoTo());

                tlSerialNoList.addView(llSerialNoList);
            }
        }else{
            llSerialNoList = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.no_data_found_ll,
                            null,false);

            tlSerialNoList.addView(llSerialNoList);
        }

        scroll_my_stock_list.addView(tlSerialNoList);
        scroll_my_stock_list.requestLayout();
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
