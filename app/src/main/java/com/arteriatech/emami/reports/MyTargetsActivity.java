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
 * Created by ${e10526} on ${30-04-2016}.
 *
 */
public class MyTargetsActivity extends AppCompatActivity {
    TextView month = null;
    private ArrayList<MyTargetsBean> alMyTargets = null;
    private boolean mBooleanRemoveScrollViews = true, mBooleanRemoveHederScrollViews = true;
    int numDays;
    int currentDay;
    private String[][] mArrayKpiCategory = null;
    private String mStrExcludedSystemKpi = "", mStrSPGuid = "";
    private String[][] mArrayDistributors;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_my_targets));

        setContentView(R.layout.activity_my_targets);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!Constants.restartApp(MyTargetsActivity.this)) {
            initUI();
        }
    }

    /*Initializes UI*/
    void initUI(){
        month = (TextView) findViewById(R.id.tv_traget_month);

        String curr_month_No = "";
        Calendar cal = Calendar.getInstance();

        int intFromMnt = cal.get(Calendar.MONTH) + 1;
        if (intFromMnt < 10)
            curr_month_No = "0" + intFromMnt;
        else
            curr_month_No = "" + intFromMnt;

        numDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        currentDay = cal.get(Calendar.DAY_OF_MONTH);

        getDistributors();
        String mStrCurrentYear = cal.get(Calendar.YEAR) + "";
        month.setText(Constants.ORG_MONTHS[cal.get(Calendar.MONTH)].toUpperCase());
        getSystemKPI();
        displayHeaderValues(curr_month_No, mStrCurrentYear);

        String mStrTargetQry = getTargetQry(mStrExcludedSystemKpi, curr_month_No, mStrCurrentYear);
        getMyTargetsList(mStrTargetQry);
    }

    /*Gets target for sales person  based on query*/
    private void getMyTargetsList(String mStrTargetQry) {
        try {
            if (!mStrTargetQry.equalsIgnoreCase("")) {
                alMyTargets = OfflineManager.getMyTargetsItemsList(mStrTargetQry, mStrSPGuid);
            }
            displayMyTargetsValues(alMyTargets);
        } catch (OfflineODataStoreException e) {
            displayMyTargetsValues(alMyTargets);
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    /*Displays header values*/
    @SuppressLint("InflateParams")
    private void displayHeaderValues(String curr_month_No, String mStrCurrentYear) {

        ScrollView scroll_my_targets_header = (ScrollView) findViewById(R.id.scroll_my_targets_header);
        if (!mBooleanRemoveHederScrollViews) {
            scroll_my_targets_header.removeAllViews();
        }

        mBooleanRemoveHederScrollViews = false;

        @SuppressLint("InflateParams")
        TableLayout tlMyStock = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);


        LinearLayout llMyStock;

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

                String mStrSysKPIAvalibleQry = Constants.KPISet + "?$select=" + Constants.KPIGUID + " &$filter = " + Constants.Month + " eq '" + curr_month_No + "' " +
                        "and " + Constants.Year + " eq '" + mStrCurrentYear + "' and " + Constants.KPICode + " eq '" + mArrayKpiCategory[0][i] + "' ";
                try {
                    if (OfflineManager.getVisitStatusForCustomer(mStrSysKPIAvalibleQry)) {

                        llMyStock = (LinearLayout) LayoutInflater.from(this)
                                .inflate(R.layout.target_header_list_item,
                                        null, false);

                        ((TextView) llMyStock.findViewById(R.id.tv_inv_value_mtd_data_heading))
                                .setText(Constants.getName(Constants.KPISet, Constants.KPIName, Constants.KPICode, mArrayKpiCategory[0][i]));


                        String mStrCalBase = Constants.getName(Constants.KPISet, Constants.CalculationBase, Constants.KPICode, mArrayKpiCategory[0][i]).equalsIgnoreCase("01") ? "" : getString(R.string.str_rupee_symbol);

                        ((TextView) llMyStock
                                .findViewById(R.id.tv_inv_value_mtd_data))
                                .setText(mStrCalBase + " " + UtilConstants.removeLeadingZeroVal(getKPIVal(mArrayKpiCategory[0][i], curr_month_No, mStrCurrentYear)));


                        tlMyStock.addView(llMyStock);
                    }

                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

            }
        } else {

            llMyStock = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.no_data_found_ll,
                            null, false);

            ((TextView) llMyStock.findViewById(R.id.tv_no_records_found))
                    .setText(getString(R.string.lbl_system_no_records));

            tlMyStock.addView(llMyStock);
        }

        mStrExcludedSystemKpi = segMatURl;
        scroll_my_targets_header.addView(tlMyStock);
        scroll_my_targets_header.requestLayout();
    }

    /*Displays Target values for sales person*/
    @SuppressLint("InflateParams")
    private void displayMyTargetsValues(ArrayList<MyTargetsBean> filteredArraylist) {

        ScrollView scroll_my_stock_list = (ScrollView) findViewById(R.id.scroll_my_targets_list);
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
                            .inflate(R.layout.my_targets_list_item,
                                    null, false);

                    ((TextView) llMyStock.findViewById(R.id.tv_kpi_value))
                            .setText(filteredArraylist.get(i).getKPIName());

                    ((TextView) llMyStock
                            .findViewById(R.id.tv_month_target_value))
                            .setText(UtilConstants.removeLeadingZeroVal(filteredArraylist.get(i).getMonthTarget()));

                    ((TextView) llMyStock
                            .findViewById(R.id.tv_mtda_value))
                            .setText(UtilConstants.removeLeadingZeroVal(filteredArraylist.get(i).getMTDA()));

                    double mDouCrr = 0.0, mDouArr = 0.0;
                    try {
                        mDouCrr = Double.parseDouble(filteredArraylist.get(i).getMTDA()) / currentDay;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }


                    try {
                        double mDoubAmount = Double.parseDouble(filteredArraylist.get(i).getMonthTarget()) - Double.parseDouble(filteredArraylist.get(i).getMTDA());
                        int noOfDays = numDays - currentDay;
                        mDouArr = mDoubAmount / noOfDays;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    ((TextView) llMyStock
                            .findViewById(R.id.tv_crr_value))
                            .setText(UtilConstants.removeLeadingZeroVal(mDouCrr + ""));

                    ((TextView) llMyStock
                            .findViewById(R.id.tv_arr_value))
                            .setText(UtilConstants.removeLeadingZeroVal(mDouArr + ""));

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

    /*Gets types of KPIs*/
    private void getSystemKPI() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'SystemKPI' &$orderby=" + Constants.ID + "%20asc";
            mArrayKpiCategory = OfflineManager.getConfigList(mStrConfigQry);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    /*gets KPI Values for selected month and year*/
    private String getKPIVal(String mStrKpiID, String month, String mStrCurrentYear) {
        String amountVal = "0";
        try {
            String mStrMyStockQry = Constants.KPISet + "?$select=" + Constants.KPIGUID + " &$filter = " + Constants.Month + " eq '" + month + "' " +
                    "and " + Constants.Year + " eq '" + mStrCurrentYear + "' and " + Constants.KPICode + " eq '" + mStrKpiID + "' ";
            amountVal = OfflineManager.getInvAmmountByMonthYear(mStrMyStockQry);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

        return amountVal;
    }

    /*Gets target for selected month and year*/
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

            kpiGuidQuery = OfflineManager.getKpiSetGuidQuery(mStrMyStockQry);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

        return kpiGuidQuery;
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


    /*Gets sales person details*/
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
