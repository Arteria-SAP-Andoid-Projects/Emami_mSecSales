package com.arteriatech.emami.reports;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.mbo.DmsDivQryBean;
import com.arteriatech.emami.mbo.MyTargetsBean;
import com.arteriatech.emami.mbo.VisitSummaryBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10526 on 2/4/2017.
 *
 */

public class VisitSummaryActivity extends AppCompatActivity {
    private ArrayList<VisitSummaryBean> alVisitSummary =null;
    HorizontalScrollView svHeader = null, svItem = null;
    TextView tv_visit_start_time = null, tv_visit_end_time = null,tv_order_val_label=null;

    MyTargetsBean salesKpi=null,tlsdKPI=null;
    String mStrVisitStartTime = "",mStrVisitEndTime="",mStrConfigTypeVal="";
    private ProgressDialog pdLoadDialog;
//    private String mStrCPDMSDIV;
    DmsDivQryBean dmsDivQryBean = new DmsDivQryBean();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_summary);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_visit_summary));

        if (!Constants.restartApp(VisitSummaryActivity.this)) {
            initUI();
            setValuesToUI();
        }
    }

    void initUI() {
        tv_visit_start_time = (TextView) findViewById(R.id.tv_visit_start_time);
        tv_visit_end_time = (TextView) findViewById(R.id.tv_visit_end_time);
        tv_order_val_label = (TextView) findViewById(R.id.tv_order_val_label);
        displayToastMsgAutoSyncStarted();
    }
    private void setValuesToUI(){

        loadAsyncTask();


    }

    private void loadAsyncTask(){
        try {
            new GetVisitSummaryData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*AsyncTask to get Achieved Percentage*/
    private class GetVisitSummaryData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(VisitSummaryActivity.this,R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            getVisitStartEndTime();
            dmsDivQryBean = Constants.getDMSDIV("");
            getSystemKPI(UtilConstants.getCurrentMonth(), UtilConstants.getCurrentYear());
            ArrayList<CustomerBean> alVisitedRet=null;
           alVisitedRet = Constants.getVisitedRetFromVisit();
            getVisitSummaryValues(alVisitedRet);

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            tv_visit_start_time.setText(Constants.get12HoursFromat(mStrVisitStartTime));
            tv_visit_end_time.setText(Constants.get12HoursFromat(mStrVisitEndTime));

            if(!mStrConfigTypeVal.equalsIgnoreCase(Constants.X)){
                tv_order_val_label.setText(R.string.lbl_order_val);
            }else{
                tv_order_val_label.setText(R.string.lbl_bill_value);
            }
            displayVisitSummary(alVisitSummary);
        }
    }

    private void getVisitStartEndTime(){

        try {

            ArrayList<String> alVisittimes =  OfflineManager.getVisitTime(Constants.Visits+"?$filter = "
                    +Constants.STARTDATE+" eq datetime'" + UtilConstants.getNewDate()+"' and "+Constants.ENDDATE+" ne null and " +
                    "("+Constants.VisitCatID+" eq '01' or "+Constants.VisitCatID+" eq '02')",Constants.EndTime,Constants.StartTime);
            if(alVisittimes!=null && alVisittimes.size()>1){
                try {
                    mStrVisitStartTime = alVisittimes.get(0);
                    mStrVisitEndTime = alVisittimes.get(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                mStrVisitStartTime = "00:00";
                mStrVisitEndTime = "00:00";
            }

//            mStrVisitStartTime = OfflineManager.getVisitTime(Constants.Visits+"?$top=1 &$filter = "
//                    +Constants.STARTDATE+" eq datetime'" + UtilConstants.getNewDate()+"' and ("
//                    +Constants.VisitCatID+" eq '"+Constants.str_01+"' or "+Constants.VisitCatID+" eq '"+Constants.str_02+"')" +
//                    " &$orderby=" + Constants.StartTime+ "%20asc",Constants.StartTime,"asc");

//            mStrVisitStartTime = OfflineManager.getVisitTime(Constants.Visits+"?$top=1 &$filter = "
//                    +Constants.STARTDATE+" eq datetime'" + UtilConstants.getNewDate()+"' and ("
//                    +Constants.VisitCatID+" eq '"+Constants.str_01+"' or "+Constants.VisitCatID+" eq '"+Constants.str_02+"')" +
//                    " &$orderby=" + Constants.StartTime+ "%20asc",Constants.StartTime,"asc");

//            mStrVisitEndTime = OfflineManager.getVisitTime(Constants.Visits+"?$top=1 &$filter = "
//                    +Constants.STARTDATE+" eq datetime'" + UtilConstants.getNewDate()+"' and ("
//                    +Constants.VisitCatID+" eq '"+Constants.str_01+"' or "+Constants.VisitCatID+" eq '"+Constants.str_02+"') and "+Constants.ENDDATE+" ne null &$orderby=" + Constants.EndTime+ "%20desc",Constants.EndTime);

//            mStrVisitEndTime = OfflineManager.getVisitTime(Constants.Visits+"?$orderby= " + Constants.EndTime+ " desc &$filter = "
//                    +Constants.STARTDATE+" eq datetime'" + UtilConstants.getNewDate()+"' and "+Constants.ENDDATE+" ne null ",Constants.EndTime,"desc");
        } catch (Exception e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

    }
    /*Gets kpiList for selected month and year*/
    private void getSystemKPI(String month, String mStrCurrentYear) {
        try {
            salesKpi = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter = "+Constants.ValidTo+" ge datetime'" + UtilConstants.getNewDate() + "' and "+Constants.Periodicity+" eq '02' and "+Constants.KPICategory+" eq '06' and "+Constants.CalculationBase+" eq '02' ",dmsDivQryBean.getCVGValueQry());


            tlsdKPI  = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter = "+Constants.ValidTo+" ge datetime'" + UtilConstants.getNewDate() + "' and "+Constants.Periodicity+" eq '02' and "+Constants.KPICategory+" eq '07' and "+Constants.CalculationBase+" eq '04'",dmsDivQryBean.getCVGValueQry());

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

    }

    private void getVisitSummaryValues(ArrayList<CustomerBean> alTodayRetailers){
        try {
            if (alTodayRetailers !=null && alTodayRetailers.size()>0) {
                alVisitSummary = OfflineManager.getVisitSummaryVal(VisitSummaryActivity.this,alTodayRetailers,
                        salesKpi,tlsdKPI,Constants.getSOOrderType(),dmsDivQryBean.getDMSDivisionSSInvQry());
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
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
    private void displayVisitSummary(ArrayList<VisitSummaryBean> filteredArraylist) {

        TableLayout tlvisitSummList = (TableLayout) findViewById(R.id.crs_sku);
        TableLayout tlReportList = (TableLayout) findViewById(R.id.report_table);

        tlvisitSummList.removeAllViews();
        tlReportList.removeAllViews();
        LinearLayout llLineItemVal;
        LinearLayout llRetName;

        if (filteredArraylist != null && filteredArraylist.size() > 0) {
            for (VisitSummaryBean visitSummaryBean : filteredArraylist) {

                llLineItemVal = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.ll_visit_summary_scroll_item, null, false);
                llRetName = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.ll_visit_summary_ret_name, null, false);
                TextView tvRetName = (TextView) llRetName.findViewById(R.id.tv_ret_name_visit_summary);
                tvRetName.setText(visitSummaryBean.getRetailerName());
                Constants.setFontSizeByMaxText(tvRetName);

                TextView tv_time_taken_value = (TextView) llLineItemVal.findViewById(R.id.tv_time_taken_value);
                TextView tv_order_value = (TextView) llLineItemVal.findViewById(R.id.tv_order_value);
                TextView tv_day_target_value = (TextView) llLineItemVal.findViewById(R.id.tv_day_target_value);
                TextView tv_today_tlsd = (TextView) llLineItemVal.findViewById(R.id.tv_today_tlsd);
                TextView tv_tlsd_till_date_val = (TextView) llLineItemVal.findViewById(R.id.tv_tlsd_till_date_val);
                TextView tv_month_tgt = (TextView) llLineItemVal.findViewById(R.id.tv_month_tgt);
                TextView tv_ach_mtd = (TextView) llLineItemVal.findViewById(R.id.tv_ach_mtd);
                TextView tv_mtd_per = (TextView) llLineItemVal.findViewById(R.id.tv_mtd_per);

                tv_time_taken_value.setText(visitSummaryBean.getTimeTaken());
                tv_order_value.setText(visitSummaryBean.getOrderValue());
                tv_day_target_value.setText(visitSummaryBean.getDayTarget());
                tv_today_tlsd.setText(visitSummaryBean.getTodayTlsd());
                tv_tlsd_till_date_val.setText(visitSummaryBean.getTlsdTilldate());
                tv_month_tgt.setText(visitSummaryBean.getMonthTarget());
                tv_ach_mtd.setText(visitSummaryBean.getAchMTD());
                tv_mtd_per.setText(visitSummaryBean.getMTDPer());

                tlReportList.addView(llLineItemVal);
                tlvisitSummList.addView(llRetName);
            }
        } else {
            tlReportList = (TableLayout) findViewById(R.id.report_table);

            LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(VisitSummaryActivity.this)
                    .inflate(R.layout.ll_so_create_empty_layout, null);

            tlReportList.addView(llEmptyLayout);
        }
    }


    private void displayToastMsgAutoSyncStarted(){
        if(Constants.iSAutoSync){
            Toast.makeText(VisitSummaryActivity.this, getString(R.string.data_refresh_in_progress),
                    Toast.LENGTH_LONG).show();
        }
    }

}
