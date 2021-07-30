package com.arteriatech.emami.reports;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by e10526 on 2/16/2017.
 *
 */

public class FOSRetailerTrendsActivity extends AppCompatActivity {
    TextView trend_mon1,trend_mon2,trend_mon3;
    HorizontalScrollView svHeader = null, svItem = null;
    TextView tvRetName = null, tvUID = null;

    // TODO below code further will use ful
    private String mStrBundleRetID = "",mStrBundleCPGUID="";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private String typevalue="";
    TextView tv_crs_sku_label;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ret_trends);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_retailer_trends));

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
        }
        if (!Constants.restartApp(FOSRetailerTrendsActivity.this)) {
            initUI();
            setValuesToUI();
            getTypeValue();
            getRetailerTrendList();
        }
    }


    private void getTypeValue() {


        tv_crs_sku_label= (TextView) findViewById(R.id.tv_crs_sku_label);

        typevalue=Constants.getTypesetValueForSkugrp(FOSRetailerTrendsActivity.this);
        if(typevalue.equalsIgnoreCase(Constants.SKUGROUP)){
            tv_crs_sku_label.setText(Constants.SKUGROUP);
            // etSKUDescSearch.setHint(R.string.lbl_Search_by_skugroup);
        }else{
            tv_crs_sku_label.setText(Constants.CRSSKUGROUP);
            //  etSKUDescSearch.setHint(R.string.lbl_Search_by_crsskugroup);
        }
    }
    void initUI() {

        tvRetName = (TextView) findViewById(R.id.tv_reatiler_name);
        tvUID = (TextView) findViewById(R.id.tv_reatiler_id);
        trend_mon1 = (TextView) findViewById(R.id.trend_mon1);
        trend_mon2 = (TextView) findViewById(R.id.trend_mon2);
        trend_mon3 = (TextView) findViewById(R.id.trend_mon3);



    }
    private void setValuesToUI(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        trend_mon1.setText(Constants.ORG_MONTHS[c.get(Calendar.MONTH)].toUpperCase());
        c.add(Calendar.MONTH, -1);
        trend_mon2.setText(Constants.ORG_MONTHS[c.get(Calendar.MONTH)].toUpperCase());
        c.add(Calendar.MONTH, -1);
        trend_mon3.setText(Constants.ORG_MONTHS[c.get(Calendar.MONTH)].toUpperCase());
        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetUID);

    }
    private  void getRetailerTrendList(){
        try {
            // TODO PerformanceTypeID equal to  '000002' To get Retailer reports
            String mStrRetTrendQry= Constants.Performances+"?$filter="+ Constants.CPGUID+" eq '"+mStrBundleCPGUID.replace("-","").toUpperCase()+"' " +
                    "and "+ Constants.PerformanceTypeID+" eq '000002' ";
            ArrayList<com.arteriatech.emami.mbo.MyPerformanceBean> alRetTrends = OfflineManager.getRetTrendsList(mStrRetTrendQry, mStrBundleCPGUID.replace("-", "").toUpperCase());
            displayRetTrendsValues(alRetTrends);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initScroll();
    }

    void initScroll(){
        svHeader = (HorizontalScrollView) findViewById(R.id.sv_header);
        svItem = (HorizontalScrollView) findViewById(R.id.sv_item);

        svHeader.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    svHeader.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(View view, int scrollX, int scrollY, int i2, int i3) {
                            svItem.scrollTo(scrollX,scrollY);
                        }
                    });
                }
                return false;
            }
        });

        svItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("Tag1",String.valueOf(getWindow().getCurrentFocus()));
                svItem.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        Log.e("Tag2",String.valueOf(getWindow().getCurrentFocus()));
                        int scrollY = svItem.getScrollY(); // For ScrollView
                        int scrollX = svItem.getScrollX(); // For HorizontalScrollView
                        // DO SOMETHING WITH THE SCROLL COORDINATES
                        svHeader.scrollTo(scrollX,scrollY);
                        Log.e("Tag3",String.valueOf(getWindow().getCurrentFocus()));
                    }
                });
                return false;
            }
        });
    }

    @SuppressLint("InflateParams")
    private void displayRetTrendsValues(ArrayList<com.arteriatech.emami.mbo.MyPerformanceBean> filteredArraylist) {

        TableLayout tlCRSList = (TableLayout) findViewById(R.id.crs_sku);
        TableLayout tlReportList = (TableLayout) findViewById(R.id.report_table);

        tlCRSList.removeAllViews();
        tlReportList.removeAllViews();
        LinearLayout llLineItemVal;
        LinearLayout llCRSKUGroup;

        if (filteredArraylist != null && filteredArraylist.size() > 0) {
            for (final com.arteriatech.emami.mbo.MyPerformanceBean performanceBean : filteredArraylist) {
                llLineItemVal = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.ll_trends_scroll_line_item, null, false);
                llCRSKUGroup = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.ll_trends_crs_sku_line_item, null, false);

                TextView tvSKUGroupName = (TextView) llCRSKUGroup.findViewById(R.id.tv_item_trends_sku_grp);

                tvSKUGroupName.setText(performanceBean.getMaterialDesc());

                TextView tv_m3_value = (TextView) llLineItemVal.findViewById(R.id.tv_m3_value);
                TextView tv_m2_value = (TextView) llLineItemVal.findViewById(R.id.tv_m2_value);
                TextView tv_m1_value = (TextView) llLineItemVal.findViewById(R.id.tv_m1_value);
                TextView tv_avg_lst_mnt = (TextView) llLineItemVal.findViewById(R.id.tv_avg_lst_mnt);
                TextView tv_ly_sm_ach = (TextView) llLineItemVal.findViewById(R.id.tv_ly_sm_ach);
                TextView tv_cm_tgt = (TextView) llLineItemVal.findViewById(R.id.tv_cm_tgt);

                TextView tv_cy_mtd = (TextView) llLineItemVal.findViewById(R.id.tv_cy_mtd);
                TextView tv_bal_to_do = (TextView) llLineItemVal.findViewById(R.id.tv_bal_to_do);
                TextView tv_ach_per = (TextView) llLineItemVal.findViewById(R.id.tv_ach_per);
                TextView tv_ly_growth_per = (TextView) llLineItemVal.findViewById(R.id.tv_ly_growth_per);

                tv_m3_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAmtMonth3PrevPerf()));
                tv_m2_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAmtMonth2PrevPerf()));
                tv_m1_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAmtMonth1PrevPerf()));
                tv_avg_lst_mnt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAvgLstThreeMonth()));
                tv_ly_sm_ach.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAmtLMTD()));
                tv_cm_tgt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getCMTarget()));
                tv_cy_mtd.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAmtMTD()));
                tv_bal_to_do.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getBalToDo()));
                tv_ach_per.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getAchivedPer()));
                tv_ly_growth_per.setText(UtilConstants.removeLeadingZerowithTwoDecimal(performanceBean.getGrPer()));

                tlReportList.addView(llLineItemVal);
                tlCRSList.addView(llCRSKUGroup);
            }
        }else{
            tlReportList = (TableLayout) findViewById(R.id.report_table);

            LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(FOSRetailerTrendsActivity.this)
                    .inflate(R.layout.ll_so_create_empty_layout, null);

            tlReportList.addView(llEmptyLayout);
        }

    }
}
