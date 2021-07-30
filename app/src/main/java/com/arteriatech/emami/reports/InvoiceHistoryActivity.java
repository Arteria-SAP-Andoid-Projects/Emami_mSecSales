package com.arteriatech.emami.reports;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.emami.adapter.ViewPagerTabAdapter;
import com.arteriatech.emami.asyncTask.OpenOfflineStoreAsyncTask;
import com.arteriatech.emami.asyncTask.SyncFromDataValtAsyncTask;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.UpdateListener;
import com.arteriatech.emami.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

/**
 * Created by e10604 on 27/4/2016.
 *
 */
public class InvoiceHistoryActivity extends AppCompatActivity implements  UIListener, UpdateListener,MessageWithBooleanCallBack {

    private String mStrBundleRetID = "",mStrBundleCPGUID="";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";

    //new
    String concatCollectionStr = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    ProgressDialog syncProgDialog = null;
    boolean dialogCancelled = false;

    private TabLayout tabLayout;
    //This is viewPager for Invoice sections device/normal
    private ViewPager viewPager;
    //new
    TextView tv_last_sync_time_value;
    private Bundle bundleExtras;

    MenuItem menu_refresh,menu_sync;
    Menu menu;

    ViewPagerTabAdapter viewPagerAdapter;
    InvoiceHistoryFragment historyFragment = null;
    DeviceInvoiceFragment deviceFragment = null;

    //new
    public static UpdateListener updateListener = null;

    //for single screen
    private FrameLayout flInvoiceHist = null;
    private LinearLayout llInvoiceHistTab = null;
    private boolean isCreateEnabled = false;

    TextView tvRetName = null, tvUID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_invoiceHistory));

        setContentView(R.layout.activity_invoice_history);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

         bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
        }
        if (!Constants.restartApp(InvoiceHistoryActivity.this)) {
//            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
//            String sharedVal = sharedPreferences.getString(Constants.isInvoiceCreateKey, "");
//            if (sharedVal.equalsIgnoreCase(Constants.isInvoiceTcode))
                isCreateEnabled = true;

            initUI();
        }
    }

    void initUI(){
        tvRetName = (TextView) findViewById(R.id.tv_reatiler_name);
        tvUID = (TextView) findViewById(R.id.tv_reatiler_id);
        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetUID);

        tv_last_sync_time_value = (TextView)findViewById(R.id.tv_last_sync_time_value);
        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE,Constants.Collections,Constants.SSINVOICES,Constants.TimeStamp,this));
        if( menu_refresh!=null && menu_sync!=null){
            menu_sync.setVisible(false);
            menu_refresh.setVisible(true);

        }
        llInvoiceHistTab = (LinearLayout) findViewById(R.id.ll_inv_hist_tab);
        flInvoiceHist = (FrameLayout) findViewById(R.id.fl_inv_hist);
        if(isCreateEnabled) {
            llInvoiceHistTab.setVisibility(View.VISIBLE);
            flInvoiceHist.setVisibility(View.GONE);
            tabInitialize();
        }
        else {
            llInvoiceHistTab.setVisibility(View.GONE);
            flInvoiceHist.setVisibility(View.VISIBLE);
            historyFragment = new InvoiceHistoryFragment();
            Bundle bundleVisit = new Bundle();
            bundleVisit.putString(Constants.CPGUID, mStrBundleCPGUID);
            bundleVisit.putString(Constants.CPNo, mStrBundleRetID);
            bundleVisit.putString(Constants.RetailerName, mStrBundleRetName);
            bundleVisit.putString(Constants.CPUID, mStrBundleRetUID);
            historyFragment.setArguments(bundleVisit);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_inv_hist, historyFragment).commit();
        }

    }
    /*Initialize tab for collections*/
    private  void tabInitialize(){

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        tabLayout.setupWithViewPager(viewPager);
    }

    /*Setting up ViewPager for Tabs*/
    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());

        historyFragment = new InvoiceHistoryFragment();
        deviceFragment = new DeviceInvoiceFragment();

        Bundle bundleVisit = new Bundle();
        bundleVisit.putString(Constants.CPGUID, mStrBundleCPGUID);
        bundleVisit.putString(Constants.CPNo, mStrBundleRetID);
        bundleVisit.putString(Constants.RetailerName, mStrBundleRetName);
        bundleVisit.putString(Constants.CPUID, mStrBundleRetUID);
        historyFragment.setArguments(bundleVisit);
        deviceFragment.setArguments(bundleVisit);

        viewPagerAdapter.addFrag(historyFragment,Constants.History);
        viewPagerAdapter.addFrag(deviceFragment,Constants.PendingSync);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

               /* if (menu_refresh != null && menu_sync != null) {
                    if (position == 0) {
                        menu_sync.setVisible(false);
                        menu_refresh.setVisible(true);
                    } else if (position == 1) {
                        menu_sync.setVisible(true);
                        menu_refresh.setVisible(false);
                    }
                }*/
                if (menu_refresh != null && menu_sync != null) {
                    if (position == 0) {
                        menu_sync.setVisible(false);
                        menu_refresh.setVisible(true);
                    } else if (position == 1) {
                        if (checkDeviceCollectionsAvailable()) {
                            menu_sync.setVisible(true);
                            menu_refresh.setVisible(false);
                        } else {
                            menu_sync.setVisible(false);
                            menu_refresh.setVisible(false);
                        }

                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_coll_his_list, menu);

        menu_refresh = menu.findItem(R.id.menu_refresh_coll);
        menu_sync = menu.findItem(R.id.menu_sync_coll);

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_coll_his_list, menu);
        menu_refresh = menu.findItem(R.id.menu_refresh_coll);
        menu_sync = menu.findItem(R.id.menu_sync_coll);
        menu_refresh.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.menu_sync_coll:
                onSync();
                break;
            case R.id.menu_refresh_coll:
                onRefresh();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    /*onSync of post device collection and refresh collections*/
    private void onSync(){

        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress), InvoiceHistoryActivity.this);
            } else {

                onSyncCollection();
            }
        } else {
            UtilConstants.showAlert(getString(R.string.no_network_conn), InvoiceHistoryActivity.this);
        }
        /*if(deviceFragment.tempInvDevList !=null && deviceFragment.tempInvDevList.length>0) {

            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                updateListener = InvoiceHistoryActivity.this;
                //post device collections
                deviceFragment.postDeviceInvoices();
            } else {
                UtilConstants.showAlert(getString(R.string.no_network_conn),InvoiceHistoryActivity.this);
            }
        }*/
    }
    public boolean checkDeviceCollectionsAvailable() {
        try {
            alInvoiceBean = OfflineManager.getDevInvoiceHistoryList(InvoiceHistoryActivity.this, mStrBundleRetID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (alInvoiceBean != null && alInvoiceBean.size() > 0) {

            return true;
        } else {

            return false;

        }
    }
    public String[] tempCollDevList = null;
    public int isFromWhere = 0;
    public int pendingCollVal = 0;
    public int penCollReqCount = 0;
    public int mError = 0;
    private ArrayList<InvoiceHistoryBean> alInvoiceBean;
    private void onSyncCollection() {

        try {
            mError = 0;
            isFromWhere = 3;
            alInvoiceBean.clear();

            Constants.Entity_Set.clear();
            Constants.AL_ERROR_MSG.clear();

            if(!OfflineManager.isOfflineStoreOpen()) {
                try {
                    Constants.isSync = true;
                    syncProgDialog = Constants.showProgressDialog(InvoiceHistoryActivity.this, "", getString(R.string.app_loading));
                    new OpenOfflineStoreAsyncTask(InvoiceHistoryActivity.this, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                alInvoiceBean = OfflineManager.getDevInvoiceHistoryList(InvoiceHistoryActivity.this, mStrBundleRetID);
                if (!alInvoiceBean.isEmpty()) {

                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {

                        alAssignColl.clear();
                        if (!alAssignColl.contains(Constants.SSINVOICES)) {
                            alAssignColl.add(Constants.SSInvoiceItemDetails);
                            alAssignColl.add(Constants.SSINVOICES);
                        }
                        if (!alAssignColl.contains(Constants.FinancialPostings)) {
                            alAssignColl.add(Constants.FinancialPostings);
                            alAssignColl.add(Constants.FinancialPostingItemDetails);
                        }

                        if (!alAssignColl.contains(Constants.OutstandingInvoices)) {
                            alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
                            alAssignColl.add(Constants.OutstandingInvoices);
                        }

                        pendingCollVal = 0;
                        if (tempCollDevList != null) {
                            tempCollDevList = null;
                            penCollReqCount = 0;
                        }

                        Constants.mBoolIsReqResAval = true;
                        Constants.mBoolIsNetWorkNotAval = false;

                        if (alInvoiceBean != null && alInvoiceBean.size() > 0) {
                            tempCollDevList = new String[alInvoiceBean.size()];

                            for (InvoiceHistoryBean returnOrderBean : alInvoiceBean) {
                                tempCollDevList[pendingCollVal] = returnOrderBean.getDeviceNo();
                                pendingCollVal++;
                            }
                            syncProgDialog = Constants.showProgressDialog(InvoiceHistoryActivity.this, "", getString(R.string.msg_sync_progress_msg_plz_wait));
                            new SyncFromDataValtAsyncTask(InvoiceHistoryActivity.this, tempCollDevList, this, this).execute();
                        }
                    } else {
                        UtilConstants.showAlert(getString(R.string.no_network_conn), this);
                    }


                }
            }


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }
    /*Refresh Invoice list from backEnd*/
    void onRefresh()
    {
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            alAssignColl.clear();
            concatCollectionStr="";
            alAssignColl.add(Constants.SSInvoiceItemDetails);
            alAssignColl.add(Constants.SSINVOICES);
            for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
                if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                } else if (incVal == 0) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                } else if (incVal == alAssignColl.size() - 1) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                } else {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                }
            }

            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress),InvoiceHistoryActivity.this);
            } else {
                try {
                    Constants.isSync = true;
                    dialogCancelled = false;
                    new LoadingData().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            UtilConstants.showAlert(getString(R.string.no_network_conn),InvoiceHistoryActivity.this);
        }
    }

    /*AsyncTask to refresh Invoices from backend*/
    public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(InvoiceHistoryActivity.this,R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(false);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    InvoiceHistoryActivity.this,R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;
                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    try {
                                                        syncProgDialog
                                                                .show();
                                                        syncProgDialog
                                                                .setCancelable(true);
                                                        syncProgDialog
                                                                .setCanceledOnTouchOutside(false);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    dialogCancelled = false;
                                                }
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(500);
                if(!OfflineManager.isOfflineStoreOpen()) {
                    try {
                        OfflineManager.openOfflineStore(InvoiceHistoryActivity.this, InvoiceHistoryActivity.this);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                }else {
                    try {

                        OfflineManager.refreshStoreSync(getApplicationContext(), InvoiceHistoryActivity.this, Constants.Fresh, concatCollectionStr);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /*@Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception,InvoiceHistoryActivity.this);
        if (errorBean.hasNoError()) {
            if (operation == Operation.OfflineRefresh.getValue()) {
                try {
                    String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                    for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {

                        String colName = alAssignColl.get(incReq);
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }

                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName, Constants.TimeStamp, syncTime
                        );
                    }
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                }

                closeProgDialog();
                Constants.isSync = false;
                UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), InvoiceHistoryActivity.this);


            }else if (operation == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                closeProgDialog();
                UtilConstants.showAlert(getString(R.string.msg_offline_store_failure),
                        InvoiceHistoryActivity.this);
            }
        }else{
            Constants.isSync = false;
            closeProgDialog();
            if(errorBean.isStoreFailed()) {
                if (!OfflineManager.isOfflineStoreOpen()) {
                    try {
                        Constants.isSync = true;
                        new LoadingData().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Constants.displayMsgReqError(errorBean.getErrorCode(), InvoiceHistoryActivity.this);
                }
            }else{
                Constants.displayMsgReqError(errorBean.getErrorCode(), InvoiceHistoryActivity.this);
            }
        }

    }*/

    @Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception,InvoiceHistoryActivity.this);
        if (errorBean.hasNoError()) {
            penCollReqCount++;
            mError++;
            Constants.mBoolIsReqResAval = true;
            if ((operation == Operation.Create.getValue()) && (penCollReqCount == pendingCollVal)) {
                Constants.isSync = false;
                displayErrorDialog(errorBean.getErrorMsg());
                getRefreshFragment(1);

            } else if (operation == Operation.OfflineRefresh.getValue()) {

                Constants.isSync = false;
                closingPrgDialog();
                displayErrorDialog(errorBean.getErrorMsg());
                if (isFromWhere == 3) {
                    getRefreshFragment(1);
                } else {
                    getRefreshFragment(0);
                }
            }else if (operation == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                closingPrgDialog();
                UtilConstants.showAlert(getString(R.string.msg_offline_store_failure),
                        InvoiceHistoryActivity.this);
            }
        } else {
            Constants.isSync = false;
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = true;
            closingPrgDialog();

            if(errorBean.isStoreFailed()) {
                if (!OfflineManager.isOfflineStoreOpen()) {
                    try {
                        Constants.isSync = true;
                        dialogCancelled = false;
                        new LoadingData().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Constants.displayMsgReqError(errorBean.getErrorCode(), InvoiceHistoryActivity.this);
                    if (isFromWhere == 3) {
                        getRefreshFragment(1);
                    } else {
                        getRefreshFragment(0);
                    }
                }
            }else{
                Constants.displayMsgReqError(errorBean.getErrorCode(), InvoiceHistoryActivity.this);
                if (isFromWhere == 3) {
                    getRefreshFragment(1);
                } else {
                    getRefreshFragment(0);
                }
            }


        }
    }

    private void closeProgDialog(){
        try {
            syncProgDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
            if (operation == Operation.OfflineRefresh.getValue()) {
                try {
                    String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                    for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
                        String colName = alAssignColl.get(incReq);
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }

                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName,Constants.TimeStamp, syncTime
                        );
                    }
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                }


                tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE,Constants.Collections,Constants.SSINVOICES,Constants.TimeStamp,this));

                closeProgDialog();
                Constants.isSync = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            InvoiceHistoryActivity.this,R.style.MyTheme);
                    builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {

                                            //TODO
                                            refreshData();
                                            *//*getInvStatus();
                                            getStatus();*//*
                                        }
                                    });

                    builder.show();
            }else if (operation == Operation.GetStoreOpen.getValue() &&   OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(InvoiceHistoryActivity.this);
                closeProgDialog();
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        InvoiceHistoryActivity.this,R.style.MyTheme);
                builder.setMessage(getString(R.string.msg_offline_store_success))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        refreshData();
                                    }
                                });

                builder.show();
            }
    }*/

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {

        if ((operation == Operation.Create.getValue()) && pendingCollVal > 0) {
            Constants.mBoolIsReqResAval = true;
            Constants.removeDeviceDocNoFromSharedPref(InvoiceHistoryActivity.this, Constants.SSInvoices, tempCollDevList[penCollReqCount]);
            UtilDataVault.storeInDataVault(tempCollDevList[penCollReqCount], "");
            penCollReqCount++;
        }
        if ((operation == Operation.Create.getValue()) && (penCollReqCount == pendingCollVal)) {
            if (UtilConstants.isNetworkAvailable(InvoiceHistoryActivity.this)) {
                try {
                    concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                    OfflineManager.refreshRequests(InvoiceHistoryActivity.this, concatCollectionStr, this);
                } catch (OfflineODataStoreException e) {
                    TraceLog.e(Constants.SyncOnRequestSuccess, e);
                }
            } else {
                Constants.isSync = false;
                closingPrgDialog();
                UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), InvoiceHistoryActivity.this);
                getRefreshFragment(1);
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(alAssignColl);
            tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.FinancialPostings, Constants.TimeStamp, this));
            closingPrgDialog();
            Constants.isSync = false;

            if (mError == 0) {
                UtilConstants.showAlert(getString(R.string.msg_sync_successfully_completed), InvoiceHistoryActivity.this);
            } else {
                displayErrorDialog(getString(R.string.error_occured_during_post));
            }

            if (isFromWhere == 3) {
                getRefreshFragment(1);
            } else {
                getRefreshFragment(0);
            }
        }else if (operation == Operation.GetStoreOpen.getValue() &&   OfflineManager.isOfflineStoreOpen()) {
//                Constants.ReIntilizeStore =false;
            try {
                OfflineManager.getAuthorizations(getApplicationContext());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(InvoiceHistoryActivity.this);
            closingPrgDialog();
            UtilConstants.showAlert(getString(R.string.msg_offline_store_success),
                    InvoiceHistoryActivity.this);
        }
    }

    private void refreshData() {
        if(historyFragment!=null){
            historyFragment.getInvStatus();
            historyFragment.getStatus();
        }
    }

    /*refresh pager and fragments*/
    public void getRefreshFragment(int position){

        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        historyFragment = new InvoiceHistoryFragment();
        deviceFragment = new DeviceInvoiceFragment();

        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE,Constants.Collections,Constants.SSInvoices,Constants.TimeStamp,this));

        Bundle bundleVisit = new Bundle();
        bundleVisit.putString(Constants.CPGUID, mStrBundleCPGUID);
        bundleVisit.putString(Constants.CPNo, mStrBundleRetID);
        bundleVisit.putString(Constants.RetailerName, mStrBundleRetName);
        bundleVisit.putString(Constants.CPUID, mStrBundleRetUID);
        historyFragment.setArguments(bundleVisit);
        deviceFragment.setArguments(bundleVisit);

        viewPagerAdapter.addFrag(historyFragment,Constants.Invoices);
        viewPagerAdapter.addFrag(deviceFragment,Constants.DeviceInvoices);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(position);

    }
    @Override
    public void onUpdate() {
        getRefreshFragment(1);
    }

    @Override
    public void clickedStatus(boolean clickedStatus, String err_msg,ErrorBean errorBean) {
        if (!clickedStatus) {
            closingPrgDialog();
            UtilConstants.showAlert(err_msg, InvoiceHistoryActivity.this);
        }
    }

    private void closingPrgDialog() {
        try {
            syncProgDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayErrorDialog(String errMsg){
        String mErrorMsg = "";
        if (Constants.AL_ERROR_MSG.size() > 0) {
            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
        }
        if (mErrorMsg.equalsIgnoreCase("")) {
            UtilConstants.showAlert(errMsg.equalsIgnoreCase("")?getString(R.string.msg_error_occured_during_sync):errMsg, InvoiceHistoryActivity.this);
        } else {
            Constants.customAlertDialogWithScroll(InvoiceHistoryActivity.this, mErrorMsg);
        }
    }
}
