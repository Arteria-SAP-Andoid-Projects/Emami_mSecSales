package com.arteriatech.emami.socreate;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.main.MainMenu;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.mbo.DmsDivQryBean;
import com.arteriatech.emami.mbo.MyTargetsBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by e10526 on 1/24/2017.
 *
 */

public class DaySummaryActivity  extends AppCompatActivity {
    private boolean mBooleanRemoveScrollViews = true;
    private ArrayList<MyTargetsBean> alMyTargets = null;
    private String mStrSpGuid = "";
    private ArrayList<MyTargetsBean> alKpiList = null;
    Map<String,Double> mapBTD=new HashMap<>();
    private Map<String,MyTargetsBean> mapMyTargetVal=new HashMap<>();
    private TextView tv_traget_out_visit,tv_actual_out_visited,tv_total_order_value,tv_total_order_val_label,tv_actual_out_visited_adhoc;
    private int mIntBalVisit = 0;
    private String mStrTotalOrderVal = "0";
    private String mDeviceTLSD = "0",mDeviceBillCut = "0",mOffLineDBBillCut="0",mOffLineDBTLSD="0";
    private double mDoubInvGrossAmt = 0.0;
    Map<String,Double> mapMonthTarget=new HashMap<>();
    Map<String,Double> mapMonthAchived=new HashMap<>();
    MyTargetsBean salesKpi=null,tlsdKPI=null,billCutKPI=null;
    DmsDivQryBean dmsDivQryBean=new DmsDivQryBean();
    private String mStrCustomerName = "";
    private String mStrUID = "";
    private String mStrCustomerId = "";
    private String mStrBundleCpGuid = "";
    private String mStrComingFrom = "";
    private String mStrRouteGuid = "";
    private String mStrRouteName = "";
    private String mStrVisitCatId = "";
    private String mStrCurrency="";
    private String mStrConfigVal="";
            public static String mStrCPDMSDI="";
    Map<String, MyTargetsBean> treeMap=new TreeMap<>();
private ProgressDialog pdLoadDialog;
    private String mStrVisitRetCount = "0",mStrVisitTargetRetCount="0",mStrTargetOtherBeatRetCount="0",mStrActualOtherBeatRetCount="0";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.lbl_day_summary));

        setContentView(R.layout.activity_day_summary);
        if (!Constants.restartApp(DaySummaryActivity.this)) {
            bundleValues();
            initUI();
        }
    }

    private void bundleValues(){
        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mStrCustomerName = extras.getString(Constants.RetailerName);
                mStrUID = extras.getString(Constants.CPUID);
                mStrCustomerId = extras.getString(Constants.CPNo);
                mStrComingFrom = extras.getString(Constants.comingFrom);
                mStrBundleCpGuid = extras.getString(Constants.CPGUID) != null ? extras.getString(Constants.CPGUID) : "";
                mStrRouteName = extras.getString(Constants.OtherRouteName) != null ? extras.getString(Constants.OtherRouteName) : "";
                mStrRouteGuid = extras.getString(Constants.OtherRouteGUID) != null ? extras.getString(Constants.OtherRouteGUID) : "";
                mStrVisitCatId = extras.getString(Constants.VisitCatID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Initializes UI*/
    void initUI(){
        tv_traget_out_visit = (TextView)findViewById(R.id.tv_traget_out_visit);
        tv_actual_out_visited = (TextView)findViewById(R.id.tv_actual_out_visited);
        tv_actual_out_visited_adhoc = (TextView)findViewById(R.id.tv_actual_out_visited_adhoc);
        tv_total_order_value = (TextView)findViewById(R.id.tv_total_order_value);
        tv_total_order_val_label = (TextView)findViewById(R.id.tv_total_order_val_label);
        displayToastMsgAutoSyncStarted();
        loadAsyncTask();

    }

    private void loadAsyncTask(){
        try {
            new GetDaySummaryData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*AsyncTask to get Achieved Percentage*/
    private class GetDaySummaryData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(DaySummaryActivity.this,R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            Constants.alRetailers.clear();
            Constants.alRetailersGuid.clear();
            Constants.SS_INV_RET_QRY = "";
            mStrVisitTargetRetCount =  Constants.getVisitTargetForToday();

            String[][] mArrayDistributors = Constants.getDistributors();
            try {
                mStrSpGuid = mArrayDistributors[8][0];
                mStrCurrency =  mArrayDistributors[10][0];
            } catch (Exception e) {
                mStrSpGuid ="";
            }
            mStrActualOtherBeatRetCount =  Constants.getOtherBeatRetailerCount();

            mStrVisitRetCount = Constants.getVisitedRetailerCount(Constants.alTodayBeatRet);

            mIntBalVisit = getBalanceVisit(Constants.alTodayBeatRet);
            dmsDivQryBean = Constants.getDMSDIV("");

            mStrTotalOrderVal =Constants.getTotalOrderValue(DaySummaryActivity.this,UtilConstants.getNewDate(), Constants.alTodayBeatRet);
            mDeviceTLSD = getDeviceTLSD(Constants.alRetailers);
            mDeviceBillCut = getDeviceBillCut(Constants.alRetailers);
            mOffLineDBBillCut = getUniqueBillCut();
            mOffLineDBTLSD = getDeviceTLSD("");

//            mStrTotalOrderVal =Constants.getTotalInvValue(DaySummaryActivity.this,UtilConstants.getNewDate(), Constants.alTodayBeatRet);
//            mDeviceTLSD = getDeviceInvTLSD(Constants.alRetailers);
//            mDeviceBillCut = getDeviceInvBillCut(Constants.alRetailers);
//            mOffLineDBBillCut = getInvoiceUniqueBillCut();
//            mOffLineDBTLSD = getDeviceInvoiceTLSD("");


            mDoubInvGrossAmt = getInvoiceAmtByRetailer(Constants.alRetailers,dmsDivQryBean.getDMSDivisionSSInvQry());
            getSystemKPI(UtilConstants.getCurrentMonth(), UtilConstants.getCurrentYear());
            getMyTargetsList();

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
            setValuesToUI();
            displayMyTargetsValues();
        }
    }

    public static double getInvoiceAmtByRetailer(ArrayList<String> alTodaysRetailers, String dmsDivQry){
        double mTotInvVal = 0.0;
        String mInvVal="0";
        if(alTodaysRetailers!=null && alTodaysRetailers.size()>0) {
            String cpQry = Constants.makeCPQry(alTodaysRetailers,Constants.SoldToID);
            mInvVal =Constants.getTotalOrderValueByCurrentMonth(Constants.getFirstDateOfCurrentMonth(),cpQry,dmsDivQry);
        }

        mTotInvVal =  Double.parseDouble(mInvVal);

        return mTotInvVal;
    }

    private String getUniqueBillCut() {
        String mStrOfflineBillCut = "0";
        if (!Constants.SS_INV_RET_QRY.equalsIgnoreCase("")) {
            try {
                mStrOfflineBillCut = OfflineManager.getUniqueCountFromDatabase(Constants.SSSOs + "?$select="+Constants.SoldToId+" &$filter= " + Constants.OrderDate + " eq datetime'"
                        + UtilConstants.getNewDate() + "' and " + Constants.OrderType + " eq '" + Constants.getSOOrderType() + "' and " + Constants.SS_INV_RET_QRY + " ",Constants.SoldToId);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        return mStrOfflineBillCut;
    }

    private String getInvoiceUniqueBillCut() {
        String mStrOfflineBillCut = "0";
        if (!Constants.SS_INV_RET_QRY.equalsIgnoreCase("")) {
            try {
                mStrOfflineBillCut = OfflineManager.getUniqueCountFromDatabase(Constants.SSInvoices + "?$select="+Constants.SoldToID+" &$filter= " + Constants.InvoiceDate + " eq datetime'"
                        + UtilConstants.getNewDate() + "' and " + Constants.SS_INV_RET_QRY + " ",Constants.SoldToID);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        return mStrOfflineBillCut;
    }

    public static  String getDeviceTLSD(String mStrSoldToID){
        String mStrQry = "",mStrOfflineTLSD="0";
        if (!Constants.SS_INV_RET_QRY.equalsIgnoreCase("")) {
            try {
                    mStrQry = OfflineManager.makeSSSOQry(Constants.SSSOs + "?$filter= " + Constants.OrderDate +
                            " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.OrderType + " eq '" + Constants.getSOOrderType() + "' and " + Constants.SS_INV_RET_QRY + " ", Constants.SSSOGuid);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        if(!mStrQry.equalsIgnoreCase("")){
            try {
                mStrOfflineTLSD = OfflineManager.getCountTLSDFromDatabase(Constants.SSSoItemDetails + "?$filter="+Constants.IsfreeGoodsItem+" ne '"+Constants.X+"' and "+mStrQry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }


        Double mDouTotalTLSD =  Double.parseDouble(mStrOfflineTLSD);

        return mDouTotalTLSD+"";
    }

    public static  String getDeviceInvoiceTLSD(String mStrSoldToID){
        String mStrQry = "",mStrOfflineTLSD="0";
        if (!Constants.SS_INV_RET_QRY.equalsIgnoreCase("")) {
            try {
                mStrQry = OfflineManager.makeSSSOQry(Constants.SSInvoices + "?$filter= " + Constants.InvoiceDate +
                        " eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.SS_INV_RET_QRY + " ", Constants.InvoiceGUID);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        if(!mStrQry.equalsIgnoreCase("")){
            try {
                mStrOfflineTLSD = OfflineManager.getCountTLSDFromDatabase(Constants.SSInvoiceItemDetails + "?$filter="+mStrQry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }


        Double mDouTotalTLSD =  Double.parseDouble(mStrOfflineTLSD);

        return mDouTotalTLSD+"";
    }

    public static int getBalanceVisit(ArrayList<CustomerBean> alTodaysRetailers){
        int mIntBalVisitRet = 0;
               String mStrRetQry = "";
        if(alTodaysRetailers!=null && alTodaysRetailers.size()>0){
            for(int i=0;i<alTodaysRetailers.size();i++){
                if (i == 0 && i == alTodaysRetailers.size() - 1) {
                    mStrRetQry = mStrRetQry
                            + "("+Constants.VisitCPGUID+"%20eq%20'"
                            + alTodaysRetailers.get(i).getCpGuidStringFormat().toUpperCase() + "')";

                } else if (i == 0) {
                    mStrRetQry = mStrRetQry
                            + "("+Constants.VisitCPGUID+"%20eq%20'"
                            + alTodaysRetailers.get(i).getCpGuidStringFormat().toUpperCase() + "'";

                } else if (i == alTodaysRetailers.size() - 1) {
                    mStrRetQry = mStrRetQry
                            + "%20or%20"+Constants.VisitCPGUID+"%20eq%20'"
                            + alTodaysRetailers.get(i).getCpGuidStringFormat().toUpperCase() + "')";
                } else {
                    mStrRetQry = mStrRetQry
                            + "%20or%20"+Constants.VisitCPGUID+"%20eq%20'"
                            + alTodaysRetailers.get(i).getCpGuidStringFormat().toUpperCase() + "'";
                }
            }
        }

        if(!mStrRetQry.equalsIgnoreCase("")){
            String mStrBalVisitQry = Constants.RouteSchedulePlans + "?$filter = "+mStrRetQry+" ";
            try {
                mIntBalVisitRet = OfflineManager.getBalanceRetVisitRoute(mStrBalVisitQry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }else{
            mIntBalVisitRet = 0;
        }

        return mIntBalVisitRet;
    }
    private String getDeviceTLSD(ArrayList<String> alTodaysRetailers){

        double mDoubleDevTLSD=0.0;
        if(alTodaysRetailers.size()>0) {
            try {
                mDoubleDevTLSD = OfflineManager.getTLSD(Constants.SOList, DaySummaryActivity.this,
                        UtilConstants.getNewDate(), alTodaysRetailers);
            } catch (Exception e) {
                mDoubleDevTLSD = 0.0;
            }
        }
        return mDoubleDevTLSD+"";
    }

    private String getDeviceInvTLSD(ArrayList<String> alTodaysRetailers){

        double mDoubleDevTLSD=0.0;
        if(alTodaysRetailers.size()>0) {
            try {
                mDoubleDevTLSD = OfflineManager.getInvoiceTLSD(Constants.InvList, DaySummaryActivity.this,
                        UtilConstants.getNewDate(), alTodaysRetailers);
            } catch (Exception e) {
                mDoubleDevTLSD = 0.0;
            }
        }
        return mDoubleDevTLSD+"";
    }

    private String getDeviceBillCut(ArrayList<String> alTodaysRetailers){

        int mIntDevBillCut=0;
        if(alTodaysRetailers.size()>0) {
            try {
                mIntDevBillCut = OfflineManager.getUniqueBillCut(Constants.SOList, DaySummaryActivity.this,
                        UtilConstants.getNewDate(), alTodaysRetailers);
            } catch (Exception e) {
                mIntDevBillCut = 0;
            }
        }
        return mIntDevBillCut+"";
    }

    private String getDeviceInvBillCut(ArrayList<String> alTodaysRetailers){

        int mIntDevBillCut=0;
        if(alTodaysRetailers.size()>0) {
            try {
                mIntDevBillCut = OfflineManager.getInvoiceUniqueBillCut(Constants.InvList, DaySummaryActivity.this,
                        UtilConstants.getNewDate(), alTodaysRetailers);
            } catch (Exception e) {
                mIntDevBillCut = 0;
            }
        }
        return mIntDevBillCut+"";
    }



    private void setValuesToUI(){
        tv_traget_out_visit.setText(mStrVisitTargetRetCount);
        tv_actual_out_visited.setText(mStrVisitRetCount);
        tv_actual_out_visited_adhoc.setText(mStrActualOtherBeatRetCount);
        tv_total_order_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mStrTotalOrderVal)+" "+mStrCurrency);

        if(!mStrConfigVal.equalsIgnoreCase(Constants.X)){
            tv_total_order_val_label.setText(R.string.lbl_total_order_val);
        }else{
            tv_total_order_val_label.setText(R.string.lbl_total_bill_val);
        }

    }

    /*Gets kpiList for selected month and year*/
    private void getSystemKPI(String month, String mStrCurrentYear) {
        try {
            alKpiList =new ArrayList<>();

            salesKpi = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter = "+Constants.ValidTo+" ge datetime'" + UtilConstants.getNewDate() + "' and "+Constants.Periodicity+" eq '02' and "+Constants.KPICategory+" eq '06' and "+Constants.CalculationBase+" eq '02' ",dmsDivQryBean.getCVGValueQry());

            tlsdKPI  = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter = "+Constants.ValidTo+" ge datetime'" + UtilConstants.getNewDate() + "' and "+Constants.Periodicity+" eq '02' and "+Constants.KPICategory+" eq '07' and "+Constants.CalculationBase+" eq '04'",dmsDivQryBean.getCVGValueQry());

            billCutKPI  = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter = "+Constants.ValidTo+" ge datetime'" + UtilConstants.getNewDate() + "' and "+Constants.Periodicity+" eq '02' and "+Constants.KPICategory+" eq '07' and "+Constants.CalculationBase+" eq '05'",dmsDivQryBean.getCVGValueQry());



            if(salesKpi!=null){
                alKpiList.add(salesKpi);
            }

            if(tlsdKPI!=null){
                alKpiList.add(tlsdKPI);
            }

            if(billCutKPI!=null){
                alKpiList.add(billCutKPI);
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

    }

    /*Get targets for sales person  based on query*/
    private void getMyTargetsList() {
        try {
            if (alKpiList !=null && alKpiList.size()>0) {
                alMyTargets = OfflineManager.getTargetsDaySummary(alKpiList, mStrSpGuid);
            }
            mapMyTargetVal = getALMyTargetList(alMyTargets);

        } catch (OfflineODataStoreException e) {

            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

        if(mapMyTargetVal!=null && !mapMyTargetVal.isEmpty()){
            treeMap = new TreeMap<>(mapMyTargetVal);
        }
    }

    //ToDo sum of actual and target quantity/Value based on kpi code and assign to map table
    private Map<String,MyTargetsBean> getALMyTargetList(ArrayList<MyTargetsBean> alMyTargets){
        Map<String,MyTargetsBean> mapMyTargetBean=new HashMap<>();
        if(alMyTargets!=null && alMyTargets.size()>0){
            for(MyTargetsBean bean:alMyTargets)
                if(mapBTD.containsKey(bean.getKPICode())) {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) +  mapMonthTarget.get(bean.getKPICode());
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) +  mapMonthAchived.get(bean.getKPICode());

                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);

                    double mDoubBTD = Double.parseDouble(bean.getBTD())+  mapBTD.get(bean.getKPICode());
                    mapBTD.put(bean.getKPICode(), mDoubBTD);
                    mapMyTargetBean.put(bean.getKPICode(),bean);
                }else {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) ;
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) ;
                    double mDoubBTD = Double.parseDouble(bean.getBTD());

                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);

                    mapBTD.put(bean.getKPICode(), mDoubBTD);
                    mapMyTargetBean.put(bean.getKPICode(),bean);
                }
        }
        return mapMyTargetBean;
    }

    /*Displays Target values*/
    @SuppressLint("InflateParams")
    private void displayMyTargetsValues() {

        ScrollView scroll_my_stock_list = (ScrollView) findViewById(R.id.scroll_day_summary_list);
        if (!mBooleanRemoveScrollViews) {
            scroll_my_stock_list.removeAllViews();
        }

        mBooleanRemoveScrollViews = false;

        @SuppressLint("InflateParams")
        TableLayout tlMyTargets = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);


        LinearLayout llMyTargets;

        if(!treeMap.isEmpty()){
//        if(!mapMyTargetVal.isEmpty()){

            Iterator iterator = treeMap.keySet().iterator();
//            Iterator iterator = mapMyTargetVal.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next().toString();

                llMyTargets = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.day_summary_list_item,
                                null, false);

                final MyTargetsBean myTargetsBean = treeMap.get(key);
//                final MyTargetsBean myTargetsBean = mapMyTargetVal.get(key);

                ((TextView) llMyTargets.findViewById(R.id.tv_kpi_value))
                        .setText(myTargetsBean.getKPIName());

                TextView tv_target = (TextView)llMyTargets.findViewById(R.id.tv_day_target_value);

                TextView tv_achived = (TextView)llMyTargets.findViewById(R.id.tv_day_achieved);

                if( myTargetsBean.getCalculationBase().equalsIgnoreCase("02")){

//                    double BTDPer = OfflineManager.getBTD(mapMonthTarget.get(key).toString(),(mapMonthAchived.get(key)+mDoubInvGrossAmt)+"");
                    double BTDPer = OfflineManager.getBTD(mapMonthTarget.get(key).toString(),(mDoubInvGrossAmt)+"");

                    Double mDoubTargAchi=0.0;
                    try {
                        mDoubTargAchi = BTDPer/mIntBalVisit;
                    } catch (Exception e) {
                        mDoubTargAchi = 0.0;
                    }

                    if(mDoubTargAchi.isNaN() || mDoubTargAchi.isInfinite()){
                        mDoubTargAchi = 0.0;
                    }

                    tv_target.setText(UtilConstants.removeLeadingZerowithTwoDecimal((mDoubTargAchi>0?mDoubTargAchi:0.00)+""+""));

                    tv_achived.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mStrTotalOrderVal));

                    getTodayAchivedPer(key,mDoubTargAchi+"");
                }else if( myTargetsBean.getCalculationBase().equalsIgnoreCase("05")){


                    tv_target.setText(UtilConstants.removeLeadingZeroVal(mapMonthTarget.get(key).toString()+""));
                    Double mDouAcived=0.0;
                    try {
                            mDouAcived = mapMonthAchived.get(key) + Double.parseDouble(mDeviceBillCut) + Double.parseDouble(mOffLineDBBillCut);

                    } catch (NumberFormatException e) {
                        mDouAcived = 0.0;
                    }
                    if(mDouAcived.isNaN() || mDouAcived.isInfinite()){
                        mDouAcived = 0.0;
                    }

                    tv_achived.setText(UtilConstants.removeLeadingZeroVal(mDouAcived+""));
                }else if( myTargetsBean.getCalculationBase().equalsIgnoreCase("04")){
                    tv_target.setText(UtilConstants.removeLeadingZeroVal(mapMonthTarget.get(key).toString()+""));
                    Double mDouAcived=0.0;
                    try {
                            mDouAcived = mapMonthAchived.get(key) + Double.parseDouble(mDeviceTLSD) + Double.parseDouble(mOffLineDBTLSD);

                    } catch (NumberFormatException e) {
                        mDouAcived = 0.0;
                    }
                    if(mDouAcived.isNaN() || mDouAcived.isInfinite()){
                        mDouAcived = 0.0;
                    }

                    tv_achived.setText(UtilConstants.removeLeadingZeroVal(mDouAcived+""));
                }





                tlMyTargets.addView(llMyTargets);

            }

        } else {

            LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.empty_layout, null);

            tlMyTargets.addView(llEmptyLayout);
        }


        scroll_my_stock_list.addView(tlMyTargets);
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

    @Override
    public void onBackPressed() {
        if(mStrUID.equalsIgnoreCase("")){
            Intent intentMainmenu = new Intent(DaySummaryActivity.this,
                    MainMenu.class);
            intentMainmenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentMainmenu);
        }else{
            Constants.ComingFromCreateSenarios = Constants.X;
            Intent intDaySummary = new Intent(this,
                    RetailersDetailsActivity.class);
            intDaySummary.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intDaySummary.putExtra(Constants.CPNo, mStrCustomerId);
            intDaySummary.putExtra(Constants.RetailerName, mStrCustomerName);
            intDaySummary.putExtra(Constants.CPUID, mStrUID);
            intDaySummary.putExtra(Constants.comingFrom, mStrComingFrom);
            intDaySummary.putExtra(Constants.CPGUID, mStrBundleCpGuid);
            if(!Constants.OtherRouteNameVal.equalsIgnoreCase("")){
                intDaySummary.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
                intDaySummary.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
            }
            startActivity(intDaySummary);
        }
    }

    private void getTodayAchivedPer(String key,String mDoubTargAchi){
        Constants.TodayAchivedPer = OfflineManager.getAchivedPer(mDoubTargAchi+"",(mapMonthAchived.get(key) + Double.parseDouble(mStrTotalOrderVal))+"")+"";
    }
    private void displayToastMsgAutoSyncStarted(){
        if(Constants.iSAutoSync){
            Toast.makeText(DaySummaryActivity.this, getString(R.string.data_refresh_in_progress),
                    Toast.LENGTH_LONG).show();
        }
    }
}
