package com.arteriatech.emami.reports;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
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
 * Created by e10526 on 2/15/2017.
 *
 */

public class FOSTargetsActivity extends AppCompatActivity {
    private ArrayList<MyTargetsBean> alMyTargets = null;
    private boolean mBooleanRemoveScrollViews = true;
    private String mStrSPGuid = "";
    private String mStrTotalOrderVal = "0";
    private ArrayList<MyTargetsBean> alKpiList = null;
//    ArrayList<CustomerBean> alRetailerList;
    Map<String,Double> mapMonthTarget=new HashMap<>();
    Map<String,Double> mapMonthAchived=new HashMap<>();
    private Map<String,MyTargetsBean> mapMyTargetVal=new HashMap<>();
    MyTargetsBean salesKpi=null;
    private TextView headerName;
    private String mStrTLSDVal = "0",mStrUniueBillCut = "0",mStrECOCount="0";
    private ProgressDialog pdLoadDialog;
    Map<String, MyTargetsBean> treeMap=new TreeMap<>();
    String mStrCPDMSDI="";
    DmsDivQryBean dmsDivQryBean = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_my_targets));

        setContentView(R.layout.activity_fos_targets);
        if (!Constants.restartApp(FOSTargetsActivity.this)) {
            initUI();
        }
    }

    /*Initializes UI*/
    void initUI(){
        headerName = (TextView)findViewById(R.id.tv_main_menu_header);
        headerName.setText(Constants.getSalesPersonName());
        loadAsyncTask();
    }


    private void loadAsyncTask(){
        try {
            new GetMyTargetData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*AsyncTask to get Achieved Percentage*/
    private class GetMyTargetData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(FOSTargetsActivity.this,R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            dmsDivQryBean = Constants.getDMSDIV("");
            getSalesKPI();
            //TODO Get Total Order value by retailers
            mStrTotalOrderVal =Constants.getTotalOrderValueByCurrentMonth(Constants.getFirstDateOfCurrentMonth(),"",dmsDivQryBean.getDMSDivisionSSInvQry());
            mStrTLSDVal = getDeviceAndDataVaultTLSD("",dmsDivQryBean.getDMSDivisionSSInvQry());
            mStrUniueBillCut = getUniqueBillCut(dmsDivQryBean.getDMSDivisionSSInvQry());
            mStrECOCount = getECOCount(dmsDivQryBean.getDMSDivisionSSInvQry());
            getSystemKPI(UtilConstants.getCurrentMonth(), UtilConstants.getCurrentYear(),dmsDivQryBean.getCVGValueQry());
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
            displayMyTargetsValues();
        }
    }

    public static  String getDeviceAndDataVaultTLSD(String mStrSoldToID,String mStrCPDMSDIVQry){
        String mStrQry = "",mStrOfflineTLSD="0";
        try {
            if(mStrSoldToID.equalsIgnoreCase("")){
                mStrQry = OfflineManager.makeSSSOQry(Constants.SSInvoices + "?$filter= "+ Constants.InvoiceDate +
                        " ge datetime'" + Constants.getFirstDateOfCurrentMonth() + "' and "+Constants.InvoiceDate+" lt datetime'"+ UtilConstants.getNewDate() +"' and "+mStrCPDMSDIVQry+" and "+Constants.InvoiceTypeID+" ne '"+Constants.getSampleInvoiceTypeID()+"' ",Constants.InvoiceGUID);
            }else{
                mStrQry = OfflineManager.makeSSSOQry(Constants.SSInvoices + "?$filter= "+ Constants.InvoiceDate +
                        " ge datetime'" + Constants.getFirstDateOfCurrentMonth() + "' and "+Constants.InvoiceDate+" lt datetime'"+ UtilConstants.getNewDate() +"' and "+Constants.SoldToID+" eq '"+mStrSoldToID+"' and "+mStrCPDMSDIVQry+" and "+Constants.InvoiceTypeID+" ne '"+Constants.getSampleInvoiceTypeID()+"' ",Constants.InvoiceGUID);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if(!mStrQry.equalsIgnoreCase("")){
            try {
                mStrOfflineTLSD = OfflineManager.getCountTLSDFromDatabase(Constants.SSInvoiceItemDetails + "?$filter= "+mStrQry);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }

        Double mDouDeviceTLSD = 0.0;

        Double mDouTotalTLSD = mDouDeviceTLSD + Double.parseDouble(mStrOfflineTLSD);

        return mDouTotalTLSD+"";
    }

    private String getECOCount(String mStrCPDMSDIVQry){
        String mStrOfflineECOCount="0";
        try {
            mStrOfflineECOCount = OfflineManager.getECORetailerVisitCount(Constants.SSInvoices + "?$filter= "+
                    Constants.InvoiceDate + " ge datetime'" + Constants.getFirstDateOfCurrentMonth() + "' and "+Constants.InvoiceDate+" lt datetime'"+ UtilConstants.getNewDate() +"' and "+mStrCPDMSDIVQry+" and "+Constants.InvoiceTypeID+" ne '"+Constants.getSampleInvoiceTypeID()+"' ",Constants.SoldToCPGUID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mStrOfflineECOCount;
    }

    private String getUniqueBillCut(String mStrCPDMSDIVQry){
        String mStrOfflineBillCut="0";
        try {
            mStrOfflineBillCut = OfflineManager.getCountTLSDFromDatabase(Constants.SSInvoices + "?$filter= "+ Constants.InvoiceDate + " ge datetime'"
                    + Constants.getFirstDateOfCurrentMonth() + "' and "+Constants.InvoiceDate+" lt datetime'"+ UtilConstants.getNewDate() +"' and "+mStrCPDMSDIVQry+" and "+Constants.InvoiceTypeID+" ne '"+Constants.getSampleInvoiceTypeID()+"'");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        return mStrOfflineBillCut;
    }


    private void getSalesKPI(){
        try {

            salesKpi = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter = "+Constants.ValidTo+" ge datetime'" + UtilConstants.getNewDate() + "' and "+Constants.Periodicity+" eq '02' and "+Constants.KPICategory+" eq '06' and "+Constants.CalculationBase+" eq '02' ",dmsDivQryBean.getCVGValueQry());
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }


    /*Get targets for sales person  based on query*/
    private void getMyTargetsList() {
        try {
            if (alKpiList !=null && alKpiList.size()>0) {
                Constants.alRetailersGuid.clear();
                alMyTargets = OfflineManager.getMyTargets(alKpiList, mStrSPGuid);
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
                if(mapMonthTarget.containsKey(bean.getKPICode())) {
                    double mDoubMonthAchived=0.0;
                    mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) + mapMonthAchived.get(bean.getKPICode());
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) +  mapMonthTarget.get(bean.getKPICode());

                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getKPICode(),bean);
                }else {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) ;
                    double mDoubMonthAchived=0.0;
                    mDoubMonthAchived = Double.parseDouble(bean.getMTDA());

                    mapMonthTarget.put(bean.getKPICode(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getKPICode(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getKPICode(),bean);
                }
        }

        return mapMyTargetBean;
    }


    /*Displays Target values*/
    @SuppressLint("InflateParams")
    private void displayMyTargetsValues() {

        ScrollView scroll_my_stock_list = (ScrollView) findViewById(R.id.scroll_my_targets_list);
        if (!mBooleanRemoveScrollViews) {
            scroll_my_stock_list.removeAllViews();
        }

        mBooleanRemoveScrollViews = false;

        @SuppressLint("InflateParams")
        TableLayout tlMyTargets = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);


        LinearLayout llMyTargets;

        if(!treeMap.isEmpty()){

            Iterator iterator = treeMap.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next().toString();

                llMyTargets = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.fos_target_line_item,
                                null, false);

                final MyTargetsBean myTargetsBean = treeMap.get(key);

                ((TextView) llMyTargets.findViewById(R.id.tv_kpi_value))
                        .setText(myTargetsBean.getKPIName());

                ((TextView) llMyTargets
                        .findViewById(R.id.tv_month_target_value))
                        .setText(UtilConstants.removeLeadingZerowithTwoDecimal(mapMonthTarget.get(key).toString()));

                ImageView iv_arraow_sel = (ImageView) llMyTargets
                        .findViewById(R.id.iv_arraow_value);

                double mDoubMonthAchived=0.0;

                if(salesKpi!=null) {
                    if (UtilConstants.removeLeadingZeros(myTargetsBean.getKPICode()).equalsIgnoreCase(salesKpi.getKPICode())) {
                        double mdoubAchivedVal=0.0;
                        mDoubMonthAchived = Double.parseDouble(mStrTotalOrderVal) + mdoubAchivedVal;
                    } else {

                        if(!myTargetsBean.getKPICategory().equalsIgnoreCase(Constants.str_04)){
                            if(myTargetsBean.getCalculationBase().equalsIgnoreCase("04")){
                                mDoubMonthAchived = Double.parseDouble(mStrTLSDVal);
                            }else if(myTargetsBean.getCalculationBase().equalsIgnoreCase("05")){ //Bill Cut
                                mDoubMonthAchived = Double.parseDouble(mStrUniueBillCut);
                            }else{
                                mDoubMonthAchived = 0;
                            }
                        }else{
                            mDoubMonthAchived = Double.parseDouble(mStrECOCount);
                        }


                        if (!isFindMoreThanOneTargetItemsRecord(myTargetsBean)){
                            iv_arraow_sel.setImageDrawable(null);
                        }
                    }
                }else{
                    mDoubMonthAchived = mapMonthAchived.get(key);
                }

                String mStrAcivedTrimVal = "";
                if(!myTargetsBean.getKPICategory().equalsIgnoreCase(Constants.str_06)){
                    mStrAcivedTrimVal = UtilConstants.removeLeadingZeroVal(mDoubMonthAchived+"");
                }else{
                    mStrAcivedTrimVal = UtilConstants.removeLeadingZerowithTwoDecimal(mDoubMonthAchived+"");
                }

                ((TextView) llMyTargets
                        .findViewById(R.id.tv_mtda_value))
                        .setText(mStrAcivedTrimVal);

                double achivedPer = OfflineManager.getAchivedPer(mapMonthTarget.get(key).toString(),mDoubMonthAchived+"");

                double BTDPer = OfflineManager.getBTD(mapMonthTarget.get(key).toString(),mDoubMonthAchived+"");

                ((TextView) llMyTargets
                        .findViewById(R.id.tv_achived_per_value))
                        .setText(UtilConstants.removeLeadingZerowithTwoDecimal(achivedPer+""));

                ((TextView) llMyTargets
                        .findViewById(R.id.tv_btd_val))
                        .setText(UtilConstants.removeLeadingZerowithTwoDecimal((BTDPer>0?BTDPer:0)+""));



                LinearLayout ll_navigate_layout = (LinearLayout) llMyTargets
                        .findViewById(R.id.ll_navigate_layout);

                ll_navigate_layout.setVisibility(View.VISIBLE);




                iv_arraow_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intentCrsSkuGrp = new Intent(FOSTargetsActivity.this, CRSSKUGroupWiseTargetsActivity.class);
                        intentCrsSkuGrp.putExtra(Constants.KPICode, myTargetsBean.getKPICode());
                        intentCrsSkuGrp.putExtra(Constants.KPIName, myTargetsBean.getKPIName());
                        intentCrsSkuGrp.putExtra(Constants.KPIGUID, myTargetsBean.getKpiGuid());
                        intentCrsSkuGrp.putExtra(Constants.CalculationBase, myTargetsBean.getCalculationBase());
                        intentCrsSkuGrp.putExtra(Constants.KPIFor, myTargetsBean.getKPIFor());
                        intentCrsSkuGrp.putExtra(Constants.RollUpTo, myTargetsBean.getRollUpTo());
                        intentCrsSkuGrp.putExtra(Constants.KPICategory, myTargetsBean.getKPICategory());
                        intentCrsSkuGrp.putExtra(Constants.CalculationSource, myTargetsBean.getCalculationSource());
                        intentCrsSkuGrp.putExtra(Constants.PartnerMgrGUID, mStrSPGuid);
                        startActivity(intentCrsSkuGrp);
                    }
                });
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


    private boolean isFindMoreThanOneTargetItemsRecord(MyTargetsBean myTargetsBean){
        boolean isTargetItemRecAvalible = false;
        String qryTargets = Constants.Targets+ "?$filter=" +Constants.KPIGUID+ " eq guid'"
                + myTargetsBean.getKpiGuid().toUpperCase()+"'" ;
        try {
            ArrayList<MyTargetsBean> alMyTargets = OfflineManager.getMyTargetsList(qryTargets, mStrSPGuid,
                    myTargetsBean.getKPIName(), myTargetsBean.getKPICode(), myTargetsBean.getKpiGuid().toUpperCase(),
                    myTargetsBean.getCalculationBase(), myTargetsBean.getKPIFor(),
                    myTargetsBean.getCalculationSource(), myTargetsBean.getRollUpTo(),myTargetsBean.getKPICategory(),true);
            if(alMyTargets!=null && alMyTargets.size()>0){
                if(alMyTargets.size()>1){
                    isTargetItemRecAvalible = true;
                }else{
                    isTargetItemRecAvalible = false;
                }
            }else{
                isTargetItemRecAvalible =false;
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        return  isTargetItemRecAvalible;
    }


    /*Gets kpiList for selected month and year*/
    private void getSystemKPI(String month, String mStrCurrentYear,String mStrCPDMSDIVQry) {
        try {
            String mStrMyStockQry;
            mStrMyStockQry = Constants.KPISet + "?$filter = "+Constants.ValidTo+" ge datetime'" + UtilConstants.getNewDate() + "'  ";

            alKpiList = OfflineManager.getKpiSetGuidList(mStrMyStockQry,mStrCPDMSDIVQry);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
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
