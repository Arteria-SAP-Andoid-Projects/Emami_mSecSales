package com.arteriatech.emami.reports;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.MyTargetsBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by e10526 on 05-05-2016.
 */
public class MyPerformanceActivity extends AppCompatActivity {
    private ArrayList<MyPerformanceBean> alMyStock = null;
    private boolean mBooleanRemoveScrollViews = true;
    TextView mon1, mon2, mon3;
    private int mNaturalOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

    int numDays;
    int currentDay;
    String curr_month_No = "";
    private String[][] mArrayKpiCategory = null;
    private ArrayList<MyTargetsBean> alMyTargets = null;
    private String[][] mArrayDistributors;
    private String mStrExcludedSystemKpi = "", mStrSPGuid = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_my_performance));

        setContentView(R.layout.activity_my_performnce);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!Constants.restartApp(MyPerformanceActivity.this)) {
            initUI();
        }

    }

    /*Initializes UI*/
    void initUI() {
        getDistributors();
        Calendar cal = Calendar.getInstance();

        int intFromMnt = cal.get(Calendar.MONTH) + 1;
        if (intFromMnt < 10)
            curr_month_No = getString(R.string.Zero_0) + intFromMnt;
        else
            curr_month_No = "" + intFromMnt;

        numDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        currentDay = cal.get(Calendar.DAY_OF_MONTH);


        String mStrCurrentYear = cal.get(Calendar.YEAR) + "";
        getSystemKPI();
        displayHeaderValues();

        String mStrTargetQry = getTargetQry(mStrExcludedSystemKpi, curr_month_No, mStrCurrentYear);
        getMyTargetsList(mStrTargetQry);


        mon1 = (TextView) findViewById(R.id.perform_mon1);
        mon2 = (TextView) findViewById(R.id.perform_mon2);
        mon3 = (TextView) findViewById(R.id.perform_mon3);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        mon1.setText(Constants.ORG_MONTHS[c.get(Calendar.MONTH)].toUpperCase());
        c.add(Calendar.MONTH, -1);
        mon2.setText(Constants.ORG_MONTHS[c.get(Calendar.MONTH)].toUpperCase());
        c.add(Calendar.MONTH, -1);
        mon3.setText(Constants.ORG_MONTHS[c.get(Calendar.MONTH)].toUpperCase());

    }


    @SuppressLint("InflateParams")
    private void displayMyPerfValues(ArrayList<MyTargetsBean> filteredArraylist) {

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

                    llMyStock = (LinearLayout) LayoutInflater.from(this)
                            .inflate(R.layout.my_performnce_list_item,
                                    null, false);

                    ((TextView) llMyStock.findViewById(R.id.tv_mat_desc_value))
                            .setText(filteredArraylist.get(i).getKPIName());

                    ((TextView) llMyStock.findViewById(R.id.tv_lmtd_value))
                            .setText(UtilConstants.removeLeadingZeroVal(filteredArraylist.get(i).getAmtLMTD()));

                    ((TextView) llMyStock.findViewById(R.id.tv_mtd_value))
                            .setText(UtilConstants.removeLeadingZeroVal(filteredArraylist.get(i).getAmtMTD()));

                    ((TextView) llMyStock.findViewById(R.id.tv_gr_value))
                            .setText(UtilConstants.removeLeadingZeroVal(filteredArraylist.get(i).getGrPer()));

                    ((TextView) llMyStock.findViewById(R.id.tv_m1_value))
                            .setText(UtilConstants.removeLeadingZeroVal(filteredArraylist.get(i).getAmtMonth1PrevPerf()));

                    ((TextView) llMyStock.findViewById(R.id.tv_m2_value))
                            .setText(UtilConstants.removeLeadingZeroVal(filteredArraylist.get(i).getAmtMonth2PrevPerf()));

                    ((TextView) llMyStock.findViewById(R.id.tv_m3_value))
                            .setText(UtilConstants.removeLeadingZeroVal(filteredArraylist.get(i).getAmtMonth3PrevPerf()));


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

    /*We may need this code later*/
    /*sets orientation for screen*/
    private void setDefaultOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        Display display;
        display = getWindow().getWindowManager().getDefaultDisplay();
        int rotation = display.getRotation();
        int width = 0;
        int height = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                width = display.getWidth();
                height = display.getHeight();
                break;
            case Surface.ROTATION_180:
                width = display.getWidth();
                height = display.getHeight();
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                width = display.getHeight();
                height = display.getWidth();
                break;
            default:
                break;
        }

        if (width > height) {
            mNaturalOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else {
            mNaturalOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        setRequestedOrientation(mNaturalOrientation);
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

    /*Get KPIs category from value help collection*/
    private void getSystemKPI() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'SystemKPI' &$orderby=" + Constants.ID + "%20asc";
            mArrayKpiCategory = OfflineManager.getConfigList(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }
    }

    private void displayHeaderValues() {


        String segMatURl = "";

        if (mArrayKpiCategory != null && mArrayKpiCategory[0].length > 0) {


            for (int i = 0; i < mArrayKpiCategory[0].length; i++) {

                if (i == 0 && i == mArrayKpiCategory[0].length - 1) {
                    segMatURl = segMatURl
                            + "(" + Constants.KPICode + "%20ne%20'"
                            + mArrayKpiCategory[0][i] + "')";

                } else if (i == 0) {
                    segMatURl = segMatURl
                            + "(" + Constants.KPICode + "%20ne%20'"
                            + mArrayKpiCategory[0][i] + "'";

                } else if (i == mArrayKpiCategory[0].length - 1) {
                    segMatURl = segMatURl
                            + "%20and%20" + Constants.KPICode + "%20ne%20'"
                            + mArrayKpiCategory[0][i] + "')";
                } else {
                    segMatURl = segMatURl
                            + "%20and%20" + Constants.KPICode + "%20ne%20'"
                            + mArrayKpiCategory[0][i] + "'";
                }
            }
        } else {

        }
        mStrExcludedSystemKpi = segMatURl;

    }

    /*Gets Target for sales person for selected month and year*/
    private String getTargetQry(String otherSystemKpi, String month, String mStrCurrentYear) {
        String kpiGuidQuery = "";
        try {
            String mStrMyStockQry = "";
            if (!otherSystemKpi.equalsIgnoreCase("")) {
                mStrMyStockQry = Constants.KPISet + "?$select=" + Constants.KPIGUID + " &$filter = " + Constants.Month + " eq '" + month + "' " +
                        "and " + Constants.Year + " eq '" + mStrCurrentYear + "' and  " + otherSystemKpi + " ";
            } else {
                mStrMyStockQry = Constants.KPISet + "?$select=" + Constants.KPIGUID + " &$filter = " + Constants.Month + " eq '" + month + "' " +
                        "and " + Constants.Year + " eq '" + mStrCurrentYear + "'  ";
            }
            kpiGuidQuery = OfflineManager.getKpiSetGuidPerfQuery(mStrMyStockQry);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }

        return kpiGuidQuery;
    }

    /*Gets target list for sales person based on query*/
    private void getMyTargetsList(String mStrTargetQry) {
        try {
            if (!mStrTargetQry.equalsIgnoreCase("")) {
                alMyTargets = OfflineManager.getMyPerfList(mStrTargetQry, mStrSPGuid);
            }

            displayMyPerfValues(alMyTargets);
        } catch (OfflineODataStoreException e) {
            displayMyPerfValues(alMyTargets);
            LogManager.writeLogError("Error : " + e.getMessage());
        }
    }

    /*Gets salesperson details*/
    private void getDistributors() {

        String qryStr = Constants.SalesPersons ;
        try {
            mArrayDistributors = OfflineManager.getDistributorList(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayDistributors == null) {
            mArrayDistributors = new String[7][1];
            mArrayDistributors[0][0] = "";
            mArrayDistributors[1][0] = "";
            mArrayDistributors[2][0] = "";
            mArrayDistributors[3][0] = "";
            mArrayDistributors[4][0] = "";
            mArrayDistributors[5][0] = "";
            mArrayDistributors[6][0] = "";
        } else {
            if (mArrayDistributors[0].length > 0) {
                mStrSPGuid = mArrayDistributors[8][0];
            }
        }
    }
}
