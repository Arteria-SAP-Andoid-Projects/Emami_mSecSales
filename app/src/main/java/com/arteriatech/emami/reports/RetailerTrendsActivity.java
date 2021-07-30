package com.arteriatech.emami.reports;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.WindowManager;
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by e10526 on 04-05-2016.
 *
 */
public class RetailerTrendsActivity extends AppCompatActivity {
    private ArrayList<MyPerformanceBean> alMyStock = null;
    private boolean mBooleanRemoveScrollViews = true;
    TextView trend_mon1, trend_mon2, trend_mon3;
    private String mStrBundleRetailerNo = "";
    private String mStrBundleRetailerName = "";
    private String mStrBundleCPGUID = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_retailer_trends));

        setContentView(R.layout.activity_retailer_trends);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetailerNo = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetailerName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
        }
        if (!Constants.restartApp(RetailerTrendsActivity.this)) {
            initUI();
        }
    }

    /*Initializes UI*/
    void initUI() {
        trend_mon1 = (TextView) findViewById(R.id.trend_mon1);
        trend_mon2 = (TextView) findViewById(R.id.trend_mon2);
        trend_mon3 = (TextView) findViewById(R.id.trend_mon3);

        TextView tv_trends_reatiler_id = (TextView) findViewById(R.id.tv_reatiler_id);
        TextView tv_trends_reatiler_name = (TextView) findViewById(R.id.tv_reatiler_name);
        tv_trends_reatiler_name.setText(mStrBundleRetailerName);
        tv_trends_reatiler_id.setText(Constants.getRetMobileNo(mStrBundleRetailerNo));
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        trend_mon1.setText(Constants.ORG_MONTHS[c.get(Calendar.MONTH)].toUpperCase());
        c.add(Calendar.MONTH, -1);
        trend_mon2.setText(Constants.ORG_MONTHS[c.get(Calendar.MONTH)].toUpperCase());
        c.add(Calendar.MONTH, -1);
        trend_mon3.setText(Constants.ORG_MONTHS[c.get(Calendar.MONTH)].toUpperCase());

        getRetailerTrendList();
    }

    /*Gets Retailer Performance List*/
    private void getRetailerTrendList() {
        try {
            // PerformanceTypeID equals to  '000002' To get Retailer reports
            String mStrMyStockQry = Constants.Performances + "?$filter=" + Constants.CPGUID + " eq '" + mStrBundleCPGUID.toUpperCase() + "' " +
                    "and " + Constants.PerformanceTypeID + " eq '000002' ";
            alMyStock = OfflineManager.getMyPerfomnceList(mStrMyStockQry);

            if (alMyStock != null && alMyStock.size() > 0) {
                Collections.sort(alMyStock, new Comparator<MyPerformanceBean>() {
                    public int compare(MyPerformanceBean one, MyPerformanceBean other) {
                        BigInteger i1 = null;
                        BigInteger i2 = null;
                        try {
                            i1 = new BigInteger(one.getAmtMTD());
                        } catch (NumberFormatException e) {
                        }

                        try {
                            i2 = new BigInteger(other.getAmtMTD());
                        } catch (NumberFormatException e) {
                        }

                        if (i1 != null && i2 != null) {
                            return i1.compareTo(i2);
                        } else {
                            return one.getAmtMTD().compareTo(other.getAmtMTD());
                        }
                    }
                });


            }

            displayMyPerfValues(alMyStock);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    /*Displays retailer performance*/
    @SuppressLint("InflateParams")
    private void displayMyPerfValues(ArrayList<MyPerformanceBean> filteredArraylist) {

        ScrollView scroll_my_stock_list = (ScrollView) findViewById(R.id.scroll_my_stock_list);
        if (!mBooleanRemoveScrollViews) {
            scroll_my_stock_list.removeAllViews();
        }

        mBooleanRemoveScrollViews = false;

        @SuppressLint("InflateParams")
        TableLayout tlMyStock = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);
        LinearLayout llMyStock;
        if (filteredArraylist != null) {
            if (!filteredArraylist.isEmpty()
                    && filteredArraylist.size() > 0) {

                for (int i = 0; i < filteredArraylist.size(); i++) {
                    final MyPerformanceBean myStockBean = filteredArraylist.get(i);

                    llMyStock = (LinearLayout) LayoutInflater.from(this)
                            .inflate(R.layout.trends_line_item,
                                    null, false);

                    ((TextView) llMyStock.findViewById(R.id.tv_mat_desc_value))
                            .setText(filteredArraylist.get(i).getMaterialDesc());

                    ((TextView) llMyStock.findViewById(R.id.tv_mat_code_value))
                            .setText(filteredArraylist.get(i).getMaterialNo());

                    ((TextView) llMyStock.findViewById(R.id.tv_lmtd_value))
                            .setText(filteredArraylist.get(i).getAmtLMTD());

                    ((TextView) llMyStock.findViewById(R.id.tv_mtd_value))
                            .setText(filteredArraylist.get(i).getAmtMTD());

                    ((TextView) llMyStock.findViewById(R.id.tv_gr_value))
                            .setText(filteredArraylist.get(i).getGrPer());

                    ((TextView) llMyStock.findViewById(R.id.tv_m1_value))
                            .setText(filteredArraylist.get(i).getAmtMonth1PrevPerf());

                    ((TextView) llMyStock.findViewById(R.id.tv_m2_value))
                            .setText(filteredArraylist.get(i).getAmtMonth2PrevPerf());

                    ((TextView) llMyStock.findViewById(R.id.tv_m3_value))
                            .setText(filteredArraylist.get(i).getAmtMonth3PrevPerf());


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

        scroll_my_stock_list.addView(tlMyStock);
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
