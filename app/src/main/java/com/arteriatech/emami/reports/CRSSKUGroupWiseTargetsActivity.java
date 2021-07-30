package com.arteriatech.emami.reports;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by e10526 on 1/18/2017.
 *
 */

public class CRSSKUGroupWiseTargetsActivity extends AppCompatActivity {
    private boolean mBooleanRemoveScrollViews = true;
    private String mStrBundleKpiCode ="",mStrBundleKpiName="",mStrBundleKpiGUID="",mStrBundleRollup="",
            mStrBundleKpiFor="",mStrBundleCalBased="",mStrBundleCalSource="",mStrParnerGuid="",mStrBundleKPICat="";

    private Map<String,MyTargetsBean> mapMyTargetVal=new HashMap<>();
    private Map<String,MyTargetsBean> mapMyTargetValByCRSSKU=new HashMap<>();
    Map<String,Double> mapMonthTarget=new HashMap<>();
    Map<String,Double> mapMonthAchived=new HashMap<>();
    Map<String,Double> mapOrderValAch=new HashMap<>();
    MyTargetsBean salesKpi=null;
    private TextView headerName;
    ArrayList<MyTargetsBean> alTarget=new ArrayList<>();
    private String mStrCPDMSDIV="",mStrCPDMSDIVKPIQry="" ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_sku_grp_targets));
        setContentView(R.layout.activity_crssku_group_wise_targets);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleKpiCode = bundleExtras.getString(Constants.KPICode);
            mStrBundleKpiName = bundleExtras.getString(Constants.KPIName);
            mStrBundleKpiGUID = bundleExtras.getString(Constants.KPIGUID);
            mStrBundleCalBased = bundleExtras.getString(Constants.CalculationBase);
            mStrBundleKpiFor = bundleExtras.getString(Constants.KPIFor);
            mStrBundleRollup = bundleExtras.getString(Constants.RollUpTo);
            mStrBundleCalSource= bundleExtras.getString(Constants.CalculationSource);
            mStrBundleKPICat= bundleExtras.getString(Constants.KPICategory);
            mStrParnerGuid = bundleExtras.getString(Constants.PartnerMgrGUID);
        }
        if (!Constants.restartApp(CRSSKUGroupWiseTargetsActivity.this)) {
            initUI();
        }
    }
    private void getSalesKPI(){
        try {
            salesKpi = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter ="+Constants.ValidTo+" ge datetime'" + UtilConstants.getNewDate() + "' and "+Constants.Periodicity+" eq '02' and "+Constants.KPICategory+" eq '06' and "+Constants.CalculationBase+" eq '02' ",mStrCPDMSDIVKPIQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
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
    private ProgressDialog pdLoadDialog;
    /*AsyncTask to get Achieved Percentage*/
    private class GetMyTargetData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(CRSSKUGroupWiseTargetsActivity.this,R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            DmsDivQryBean dmsDivQryBean = Constants.getDMSDIV("");
            mStrCPDMSDIV = dmsDivQryBean.getDMSDivisionSSInvQry();
            mStrCPDMSDIVKPIQry = dmsDivQryBean.getCVGValueQry();
            getSalesKPI();
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



    /*Get targets for sales person and kpi code based on query*/
    private void getMyTargetsList() {
        try {

            String qryTargets = Constants.Targets+ "?$filter=" +Constants.KPIGUID+ " eq guid'"
                    + mStrBundleKpiGUID+"'" ;
            ArrayList<MyTargetsBean> alMyTargets = OfflineManager.getMyTargetsList(qryTargets, mStrParnerGuid,
                    mStrBundleKpiName, mStrBundleKpiCode, mStrBundleKpiGUID,
                    mStrBundleCalBased, mStrBundleKpiFor,
                    mStrBundleCalSource, mStrBundleRollup,mStrBundleKPICat,true);

            ArrayList<MyTargetsBean> alOrderValByOrderMatGrp = OfflineManager.getActualTargetByOrderMatGrp(CRSSKUGroupWiseTargetsActivity.this,mStrCPDMSDIV);
            mapMyTargetValByCRSSKU = getALOrderVal(alOrderValByOrderMatGrp);
            mapMyTargetVal = getALMyTargetList(alMyTargets);
            sortingValues();
        } catch (OfflineODataStoreException e) {
            sortingValues();
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    //ToDo sum of actual and target quantity/Value based on crs sku group and assign to map table
    private Map<String,MyTargetsBean> getALMyTargetList(ArrayList<MyTargetsBean> alMyTargets){
        Map<String,MyTargetsBean> mapMyTargetBean=new HashMap<>();
        if(alMyTargets!=null && alMyTargets.size()>0){

            for(MyTargetsBean bean:alMyTargets)
                if(mapMonthTarget.containsKey(bean.getOrderMaterialGroupID())) {
                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) +  mapMonthTarget.get(bean.getOrderMaterialGroupID());

                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) +  mapMonthAchived.get(bean.getOrderMaterialGroupID()) ;

                    mapMonthTarget.put(bean.getOrderMaterialGroupID(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getOrderMaterialGroupID(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getOrderMaterialGroupID(),bean);
                }else {


                    double mDoubMonthTarget = Double.parseDouble(bean.getMonthTarget()) ;
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) /*+ mDouOrderVal*/ ;

                    mapMonthTarget.put(bean.getOrderMaterialGroupID(), mDoubMonthTarget);
                    mapMonthAchived.put(bean.getOrderMaterialGroupID(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getOrderMaterialGroupID(),bean);
                }
        }


        return mapMyTargetBean;
    }

    //ToDo sum of actual quantity/Value based on crs sku group and assign to map table
    private Map<String,MyTargetsBean> getALOrderVal(ArrayList<MyTargetsBean> alMyTargets){
        Map<String,MyTargetsBean> mapMyTargetBean=new HashMap<>();
        if(alMyTargets!=null && alMyTargets.size()>0){

            for(MyTargetsBean bean:alMyTargets)
                if(mapOrderValAch.containsKey(bean.getOrderMaterialGroupID())) {
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) +  mapOrderValAch.get(bean.getOrderMaterialGroupID());
                    mapOrderValAch.put(bean.getOrderMaterialGroupID(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getOrderMaterialGroupID(),bean);
                }else {
                    double mDoubMonthAchived = Double.parseDouble(bean.getMTDA()) ;
                    mapOrderValAch.put(bean.getOrderMaterialGroupID(), mDoubMonthAchived);
                    mapMyTargetBean.put(bean.getOrderMaterialGroupID(),bean);
                }
        }


        return mapMyTargetBean;
    }

    private void sortingValues(){
        MyTargetsBean myTarTemp;
        if(!mapOrderValAch.isEmpty() || !mapMonthTarget.isEmpty()){
            if(!mapOrderValAch.isEmpty()) {
                Iterator iter = mapOrderValAch.keySet().iterator();
                while (iter.hasNext()) {
                    String key = iter.next().toString();
                    myTarTemp = new MyTargetsBean();

                    MyTargetsBean myTargetsBean = mapMyTargetValByCRSSKU.get(key);

                    String orderMatDesc = "";
                    if (myTargetsBean.getOrderMaterialGroupDesc().equalsIgnoreCase("")) {
                        try {
                            orderMatDesc = OfflineManager.getValueByColumnName(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupDesc + " &$filter = "
                                    + Constants.OrderMaterialGroupID + " eq '" + key + "'", Constants.OrderMaterialGroupDesc);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    } else {
                        orderMatDesc = myTargetsBean.getOrderMaterialGroupDesc();

                    }

                    myTarTemp.setOrderMaterialGroupDesc(orderMatDesc);

                    String mDouMonthTarget = "0";
                    try {
                        mDouMonthTarget = mapMonthTarget.get(key).toString();
                    } catch (Exception e) {
                        mDouMonthTarget = "0";
                    }

                    Double mDouOrderVal = 0.0;
                    try {
                        if (mapOrderValAch.containsKey(key)) {
                            mDouOrderVal = mapOrderValAch.get(key);
                        } else {
                            mDouOrderVal = 0.0;
                        }
                    } catch (NumberFormatException e) {
                        mDouOrderVal = 0.0;
                    }

                    if (mDouOrderVal.isInfinite() || mDouOrderVal.isNaN()) {
                        mDouOrderVal = 0.0;
                    }
                    if (mDouOrderVal == null) {
                        mDouOrderVal = 0.0;
                    }

                    Double achivedVal = mDouOrderVal.doubleValue();

                    myTarTemp.setMonthTarget(mDouMonthTarget + "");
                    myTarTemp.setMTDA(achivedVal + "");

                    double achivedPer = OfflineManager.getAchivedPer(mDouMonthTarget, achivedVal.toString());

                    double BTDPer = OfflineManager.getBTD(mDouMonthTarget, achivedVal.toString());

                    myTarTemp.setBTD(BTDPer + "");
                    myTarTemp.setAchivedPercentage(achivedPer + "");
                    myTarTemp.setOrderMaterialGroupID(key);
                    alTarget.add(myTarTemp);
                }
            }

            if(!mapMonthTarget.isEmpty()) {
                Iterator iterMapTarget = mapMonthTarget.keySet().iterator();
                while (iterMapTarget.hasNext()) {
                    myTarTemp =new MyTargetsBean();
                    String key = iterMapTarget.next().toString();
                    MyTargetsBean myTargetsBean = mapMyTargetVal.get(key);
                    if(!mapOrderValAch.containsKey(key)){
                        String orderMatDesc = "";
                        if (myTargetsBean.getOrderMaterialGroupDesc().equalsIgnoreCase("")) {
                            try {
                                orderMatDesc = OfflineManager.getValueByColumnName(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupDesc + " &$filter = "
                                        + Constants.OrderMaterialGroupID + " eq '" + key + "'", Constants.OrderMaterialGroupDesc);
                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                            }
                        } else {
                            orderMatDesc = myTargetsBean.getOrderMaterialGroupDesc();
                        }

                        myTarTemp.setOrderMaterialGroupDesc(orderMatDesc);

                        String mDouMonthTarget = "0";
                        try {
                            mDouMonthTarget = mapMonthTarget.get(key).toString();
                        } catch (Exception e) {
                            mDouMonthTarget = "0";
                        }
                        Double mDoubleMAPAch = 0.0;


                        myTarTemp.setMonthTarget(mDouMonthTarget+"");
                        myTarTemp.setMTDA(mDoubleMAPAch+"");

                        double achivedPer = OfflineManager.getAchivedPer(mDouMonthTarget,mDoubleMAPAch.toString());

                        double BTDPer = OfflineManager.getBTD(mDouMonthTarget,mDoubleMAPAch.toString());

                        myTarTemp.setBTD(BTDPer+"");
                        myTarTemp.setAchivedPercentage(achivedPer+"");
                        myTarTemp.setOrderMaterialGroupID(key);
                        alTarget.add(myTarTemp);
                    }
                }
            }
        }

        if(alTarget!=null && alTarget.size()>0){
            Collections.sort(alTarget, new Comparator<MyTargetsBean>() {
                public int compare(MyTargetsBean one, MyTargetsBean other) {
                    return one.getOrderMaterialGroupDesc().compareTo(other.getOrderMaterialGroupDesc());
                }
            });
        }

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

        if(alTarget!=null && alTarget.size()>0){
                for(MyTargetsBean myTargetsBean:alTarget){
                    llMyTargets = (LinearLayout) LayoutInflater.from(this)
                            .inflate(R.layout.crs_sku_target_line_item,
                                    null, false);
                    String orderMatDesc="";
                    if(myTargetsBean.getOrderMaterialGroupDesc().equalsIgnoreCase("")) {
                        try {
                            orderMatDesc = OfflineManager.getValueByColumnName(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupDesc + " &$filter = "
                                    + Constants.OrderMaterialGroupID + " eq '" + myTargetsBean.getOrderMaterialGroupID() + "'", Constants.OrderMaterialGroupDesc);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    }else{
                        orderMatDesc = myTargetsBean.getOrderMaterialGroupDesc();
                    }

                    ((TextView) llMyTargets.findViewById(R.id.tv_kpi_value))
                            .setText(orderMatDesc);

                    ((TextView) llMyTargets
                            .findViewById(R.id.tv_month_target_value))
                            .setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getMonthTarget()));

                    ((TextView) llMyTargets
                            .findViewById(R.id.tv_mtda_value))
                            .setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getMTDA()));

                    ((TextView) llMyTargets
                            .findViewById(R.id.tv_achived_per_value))
                            .setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getAchivedPercentage()+""));

                    ((TextView) llMyTargets
                            .findViewById(R.id.tv_btd_val))
                            .setText(UtilConstants.removeLeadingZerowithTwoDecimal(myTargetsBean.getBTD()));


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


}

