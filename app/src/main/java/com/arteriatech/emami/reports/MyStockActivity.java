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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by ${e10526} on ${02-05-2016}.
 *
 */
public class MyStockActivity extends AppCompatActivity {
    private ArrayList<MyStockBean> alMyStock = null;
    private boolean mBooleanRemoveScrollViews = true;
    TextView simStockVal, rcvStockVal;
    EditText mat_search;

    private ArrayList<MyStockBean> filteredArraylist = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_my_stock));

        setContentView(R.layout.activity_my_stock);
        if (!Constants.restartApp(MyStockActivity.this)) {
            initUI();
        }

    }

    /*Initializes UI*/
    void initUI() {
        simStockVal = (TextView) findViewById(R.id.tv_sim_stock);
        rcvStockVal = (TextView) findViewById(R.id.tv_rcv_stock);
        mat_search = (EditText) findViewById(R.id.ed_my_stock_search);


        getMyStockList();


        mat_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                filteredArraylist = new ArrayList<>();
                for (int i = 0; i < alMyStock.size(); i++) {
                    MyStockBean item = alMyStock.get(i);
                    if (item.getMaterialDesc().toLowerCase()
                            .contains(cs.toString().toLowerCase().trim())) {
                        filteredArraylist.add(item);

                    }

                }

                displayMyStockValues(filteredArraylist);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /*gets stock for sales person*/
    private void getMyStockList() {
        try {
            String mStrMyStockQry = Constants.SPStockItems;
            alMyStock = OfflineManager.getMyStockList(mStrMyStockQry, MyStockActivity.this, "");

            if(alMyStock!=null && alMyStock.size()>0) {
                simStockVal.setText(Constants.removeLeadingZero(String.valueOf(Constants.RCVStockValueDouble)));
                rcvStockVal.setText(Constants.removeLeadingZero(Constants.SIMStockValue + "")+" "+alMyStock.get(0).getCurrency());
            }
            displayMyStockValues(alMyStock);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    /*Displays sales person stock in list*/
    @SuppressLint("InflateParams")
    private void displayMyStockValues(ArrayList<MyStockBean> filteredArraylist) {

        ScrollView scroll_my_stock_list = (ScrollView) findViewById(R.id.scroll_my_stock_list);
        if (!mBooleanRemoveScrollViews) {
            try {
                scroll_my_stock_list.removeAllViews();
            }
            catch (Exception e){
                LogManager.writeLogError(e.getMessage());
            }
        }

        mBooleanRemoveScrollViews = false;

        @SuppressLint("InflateParams")
        TableLayout tlMyStock = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);

        LinearLayout llMyStock;

        if (filteredArraylist != null) {
            if (!filteredArraylist.isEmpty()
                    && filteredArraylist.size() > 0) {


                final ImageView[] ivSerialNoSel;
                ivSerialNoSel = new ImageView[filteredArraylist.size()];


                for (int i = 0; i < filteredArraylist.size(); i++) {
                    final MyStockBean myStockBean = filteredArraylist.get(i);

                    llMyStock = (LinearLayout) LayoutInflater.from(this)
                            .inflate(R.layout.my_stock_list_item,
                                    null, false);

                    ((TextView) llMyStock.findViewById(R.id.tv_mat_desc_value))
                            .setText(filteredArraylist.get(i).getMaterialDesc());

                    ((TextView) llMyStock
                            .findViewById(R.id.tv_stock_qty_value))
                            .setText(Constants.removeLeadingZero(String.valueOf(Double.parseDouble(filteredArraylist.get(i).getUnrestrictedQty()))));

                    ivSerialNoSel[i] = (ImageView) llMyStock
                            .findViewById(R.id.iv_serial_selection);

                    ivSerialNoSel[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intentMatSerialNosActivity = new Intent(MyStockActivity.this,
                                    MaterialSerialNumbersActivity.class);
                            intentMatSerialNosActivity.putExtra(Constants.SPStockItemGUID, myStockBean.getSPStockItemGUID());
                            intentMatSerialNosActivity.putExtra(Constants.TotalQty, Constants.removeLeadingZero(String.valueOf(Double.parseDouble(myStockBean.getUnrestrictedQty()))));
                            intentMatSerialNosActivity.putExtra(Constants.MaterialNo, myStockBean.getMaterialNo());
                            intentMatSerialNosActivity.putExtra(Constants.MaterialDesc, myStockBean.getMaterialDesc());
                            intentMatSerialNosActivity.putExtra(Constants.UOM, myStockBean.getUOM());
                            startActivity(intentMatSerialNosActivity);

                        }
                    });
                    tlMyStock.addView(llMyStock);

                }
            } else {

                llMyStock = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.no_data_found_ll,
                                null, false);

                tlMyStock.addView(llMyStock);
            }
        } else {

            llMyStock = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.no_data_found_ll,
                            null, false);

            tlMyStock.addView(llMyStock);
        }

        try {
            scroll_my_stock_list.addView(tlMyStock);
            scroll_my_stock_list.requestLayout();
        }
        catch (Exception e){
            LogManager.writeLogError(e.getMessage());
        }
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
