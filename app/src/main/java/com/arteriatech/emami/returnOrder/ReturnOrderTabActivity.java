package com.arteriatech.emami.returnOrder;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.emami.adapter.ViewPagerTabAdapter;
import com.arteriatech.emami.asyncTask.OpenOfflineStoreAsyncTask;
import com.arteriatech.emami.asyncTask.RefreshAsyncTask;
import com.arteriatech.emami.asyncTask.SyncFromDataValtAsyncTask;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

public class ReturnOrderTabActivity extends AppCompatActivity implements UIListener, MessageWithBooleanCallBack {

    public String[] tempRODevList = null;
    ArrayList<String> alAssignColl = new ArrayList<>();
    ArrayList<ReturnOrderBean> returnOrderBeanList = new ArrayList<>();
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleCPGUID = "";
    private TextView retId;
    private TextView retName;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ReturnOrderListFragment returnOrderListFragmentLeft;
    private ReturnOrderListFragment returnOrderListFragmentRight;
    private ViewPagerTabAdapter viewPagerAdapter;
    private int comingFrom = 0;
    private String actionBarTitle = "";
    private String tabLeftTitle = "";
    private String tabRightTitle = "";
    private MenuItem menu_refresh = null;
    private MenuItem menu_sync = null;
    private String concatCollectionStr = "";
    private ProgressDialog progressDialog = null;
    private int penROReqCount = 0;
    private int mError = 0;
    private int pendingROVal = 0;
    private int isFromWhere = 0;
    public static boolean isRefresh = false;
    private int tabPosition = 0;
    private static final String TAG = "ReturnOrderTabActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_history);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            comingFrom = bundleExtras.getInt(Constants.comingFrom, 0);

        }
        if (!Constants.restartApp(ReturnOrderTabActivity.this)) {
            if (comingFrom == Constants.RETURN_ORDER_POS) {
                actionBarTitle = getString(R.string.title_ac_return_order_list);
                tabLeftTitle = Constants.RETURN_ORDER_TAB_TITLE_1;
                tabRightTitle = Constants.RETURN_ORDER_TAB_TITLE_2;
            } else if (comingFrom == Constants.SSS_ORDER_POS) {
                actionBarTitle = getString(R.string.title_ssso_order_list);
                tabLeftTitle = Constants.SSSO_TAB_TITLE_1;
                tabRightTitle = Constants.RETURN_ORDER_TAB_TITLE_2;
            }
            ActionBarView.initActionBarView(this, true, actionBarTitle);

            retName = (TextView) findViewById(R.id.tv_reatiler_name);
            retId = (TextView) findViewById(R.id.tv_reatiler_id);
            retName.setText(mStrBundleRetName);
            retId.setText(mStrBundleRetUID);
            tabInitialize();
        }
    }

    /*Initialize tab*/
    private void tabInitialize() {
        if (menu_refresh != null && menu_sync != null) {
            menu_sync.setVisible(false);
            menu_refresh.setVisible(true);
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabPosition = position;
                if (menu_refresh != null && menu_sync != null) {
                    if (position == 0) {
                        menu_sync.setVisible(false);
                        menu_refresh.setVisible(true);
                    } else if (position == 1) {
                        if (comingFrom == Constants.RETURN_ORDER_POS) {
                            if (checkReturnOrderAvailable()) {
                                menu_sync.setVisible(true);
                                menu_refresh.setVisible(false);
                            } else {
                                menu_sync.setVisible(false);
                                menu_refresh.setVisible(false);
                            }
                        } else if (comingFrom == Constants.SSS_ORDER_POS) {
                            if (checkSSSOrderAvailable()) {
                                menu_sync.setVisible(true);
                                menu_refresh.setVisible(false);
                            } else {
                                menu_sync.setVisible(false);
                                menu_refresh.setVisible(false);
                            }
                        }


                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private boolean checkReturnOrderAvailable() {
        boolean countNotZero = false;
        try {
            countNotZero = OfflineManager.getROListFromDataValt(ReturnOrderTabActivity.this, mStrBundleCPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return countNotZero;
    }

    private boolean checkSSSOrderAvailable() {
        boolean countNotZero = false;
        try {
            countNotZero = OfflineManager.getSSSoListFromDataValt(ReturnOrderTabActivity.this, mStrBundleCPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return countNotZero;
    }

    /*set up view page fragment*/
    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        returnOrderListFragmentLeft = new ReturnOrderListFragment();
        returnOrderListFragmentRight = new ReturnOrderListFragment();
        Bundle bundleLeft = new Bundle();
        bundleLeft.putString(Constants.CPGUID, mStrBundleCPGUID);
        bundleLeft.putString(Constants.CPNo, mStrBundleRetID);
        bundleLeft.putString(Constants.RetailerName, mStrBundleRetName);
        bundleLeft.putString(Constants.CPUID, mStrBundleRetUID);
        bundleLeft.putInt(Constants.EXTRA_TAB_POS, Constants.TAB_POS_1);
        bundleLeft.putInt(Constants.comingFrom, comingFrom);
        Bundle bundleRight = new Bundle();
        bundleRight.putString(Constants.CPGUID, mStrBundleCPGUID);
        bundleRight.putString(Constants.CPNo, mStrBundleRetID);
        bundleRight.putString(Constants.RetailerName, mStrBundleRetName);
        bundleRight.putString(Constants.CPUID, mStrBundleRetUID);
        bundleRight.putInt(Constants.EXTRA_TAB_POS, Constants.TAB_POS_2);
        bundleRight.putInt(Constants.comingFrom, comingFrom);
        returnOrderListFragmentLeft.setArguments(bundleLeft);
        returnOrderListFragmentRight.setArguments(bundleRight);
        viewPagerAdapter.addFrag(returnOrderListFragmentLeft, tabLeftTitle);
        viewPagerAdapter.addFrag(returnOrderListFragmentRight, tabRightTitle);
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                if (Constants.iSAutoSync) {
                    UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress),ReturnOrderTabActivity.this);
                } else {
                    syncRO();
                }
                break;
            case R.id.menu_refresh_coll:
                refreshRO();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }
    private void refreshRO(){
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    Constants.isSync = true;
                    progressDialog = Constants.showProgressDialog(ReturnOrderTabActivity.this, "", getString(R.string.app_loading));
                    new OpenOfflineStoreAsyncTask(ReturnOrderTabActivity.this, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (comingFrom == Constants.RETURN_ORDER_POS) {
                    onRefreshReturnOrder();
                } else if (comingFrom == Constants.SSS_ORDER_POS) {
                    onRefreshSSSOrder();
                }
            }
        }else{
            UtilConstants.showAlert(getString(R.string.no_network_conn), ReturnOrderTabActivity.this);
        }
    }
    private void syncRO(){
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    Constants.isSync = true;
                    progressDialog = Constants.showProgressDialog(ReturnOrderTabActivity.this, "", getString(R.string.app_loading));
                    new OpenOfflineStoreAsyncTask(ReturnOrderTabActivity.this, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                postPendingReq();
            }
        }else{
            UtilConstants.showAlert(getString(R.string.no_network_conn), ReturnOrderTabActivity.this);
        }
    }
    private void postPendingReq(){
        Constants.isSync = true;
        if (comingFrom == Constants.RETURN_ORDER_POS) {
            onSyncReturnOrder();
        } else if (comingFrom == Constants.SSS_ORDER_POS) {
            onSyncSSSOrder();
        }
    }

    private void onSyncReturnOrder() {

        try {
            isFromWhere = 3;
            mError =0;
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = false;
            Constants.Entity_Set.clear();
            Constants.AL_ERROR_MSG.clear();

            returnOrderBeanList.clear();
            returnOrderBeanList = OfflineManager.getROListFromDataValt(ReturnOrderTabActivity.this, mStrBundleCPGUID, returnOrderBeanList);
            if (!returnOrderBeanList.isEmpty()) {

                if (UtilConstants.isNetworkAvailable(getApplicationContext())) {

                    alAssignColl.clear();
                    alAssignColl.add(Constants.SSROs);
                    alAssignColl.add(Constants.SSROItemDetails);
                    pendingROVal = 0;
                    if (tempRODevList != null) {
                        tempRODevList = null;
                        penROReqCount = 0;
                    }

                    if (returnOrderBeanList != null && returnOrderBeanList.size() > 0) {
                        tempRODevList = new String[returnOrderBeanList.size()];

                        for (ReturnOrderBean returnOrderBean : returnOrderBeanList) {
                            tempRODevList[pendingROVal] = returnOrderBean.getDeviceNo();
                            pendingROVal++;
                        }
                        progressDialog = Constants.showProgressDialog(ReturnOrderTabActivity.this, "", getString(R.string.msg_sync_progress_msg_plz_wait));
                        new SyncFromDataValtAsyncTask(ReturnOrderTabActivity.this, tempRODevList, this, this).execute();
                    }
                } else {
                    UtilConstants.showAlert(getString(R.string.no_network_conn), this);
                }


            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onSyncSSSOrder() {
        isFromWhere = 4;
        mError =0;
        Constants.mBoolIsReqResAval = true;
        Constants.mBoolIsNetWorkNotAval = false;
        Constants.Entity_Set.clear();
        Constants.AL_ERROR_MSG.clear();
        try {
            returnOrderBeanList.clear();
            returnOrderBeanList = OfflineManager.getSSSoListFromDataValt(ReturnOrderTabActivity.this, mStrBundleCPGUID, returnOrderBeanList,mStrBundleRetID);
            if (!returnOrderBeanList.isEmpty()) {

                if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                    alAssignColl.clear();
                    alAssignColl.add(Constants.SSSOs);
                    alAssignColl.add(Constants.SSSoItemDetails);
                    pendingROVal = 0;
                    if (tempRODevList != null) {
                        tempRODevList = null;
                        penROReqCount = 0;
                    }

                    if (returnOrderBeanList != null && returnOrderBeanList.size() > 0) {
                        tempRODevList = new String[returnOrderBeanList.size()];

                        for (ReturnOrderBean returnOrderBean : returnOrderBeanList) {
                            tempRODevList[pendingROVal] = returnOrderBean.getDeviceNo();
                            pendingROVal++;
                        }
                        progressDialog = Constants.showProgressDialog(ReturnOrderTabActivity.this, "", getString(R.string.msg_sync_progress_msg_plz_wait));
                        new SyncFromDataValtAsyncTask(ReturnOrderTabActivity.this, tempRODevList, this, this).execute();
                    }
                } else {
                    UtilConstants.showAlert(getString(R.string.no_network_conn), this);
                }


            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void onRefreshSSSOrder() {
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            isFromWhere = 2;
            mError =0;
            Constants.Entity_Set.clear();
            Constants.AL_ERROR_MSG.clear();
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.SSSOs);
            alAssignColl.add(Constants.SSSoItemDetails);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress), ReturnOrderTabActivity.this);
            } else {
                try {
                    Constants.isSync = true;
                    progressDialog = Constants.showProgressDialog(ReturnOrderTabActivity.this, "", getString(R.string.msg_sync_progress_msg_plz_wait));
                    new RefreshAsyncTask(ReturnOrderTabActivity.this, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            UtilConstants.showAlert(getString(R.string.no_network_conn), ReturnOrderTabActivity.this);
        }
    }

    private void onRefreshReturnOrder() {
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            isFromWhere = 1;
            mError =0;
            Constants.Entity_Set.clear();
            Constants.AL_ERROR_MSG.clear();
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.SSROs);
            alAssignColl.add(Constants.SSROItemDetails);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress), ReturnOrderTabActivity.this);
            } else {
                try {
                    Constants.isSync = true;
                    progressDialog = Constants.showProgressDialog(ReturnOrderTabActivity.this, "", getString(R.string.msg_sync_progress_msg_plz_wait));
                    new RefreshAsyncTask(ReturnOrderTabActivity.this, concatCollectionStr, this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            UtilConstants.showAlert(getString(R.string.no_network_conn), ReturnOrderTabActivity.this);
        }

    }

    @Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception,ReturnOrderTabActivity.this);
        if (errorBean.hasNoError()) {
            mError ++;
            if (isFromWhere == 1 || isFromWhere == 2) {
                if (operation == Operation.OfflineRefresh.getValue()) {
                    Constants.isSync = false;
                    closingPrgDialog();
                    displayErrorDialog(errorBean.getErrorMsg());
                    refreshScreen();
                }else if (operation == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    closingPrgDialog();
                    UtilConstants.showAlert(getString(R.string.msg_offline_store_failure),
                            ReturnOrderTabActivity.this);
                }


            } else {
                Constants.mBoolIsReqResAval = true;
                penROReqCount++;
                if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                    Constants.isSync = false;
                    closingPrgDialog();
                    displayErrorDialog(errorBean.getErrorMsg());
                    refreshScreen();
                }
               if (operation == Operation.OfflineRefresh.getValue()) {
                   Constants.isSync = false;
                   closingPrgDialog();
                   displayErrorDialog(errorBean.getErrorMsg());
                   refreshScreen();
                }else if (operation == Operation.GetStoreOpen.getValue()) {
                   Constants.isSync = false;
                   closingPrgDialog();
                   UtilConstants.showAlert(getString(R.string.msg_offline_store_failure),
                           ReturnOrderTabActivity.this);
               }
            }
        }else{
            Constants.isSync = false;
            Constants.mBoolIsReqResAval = true;
            Constants.mBoolIsNetWorkNotAval = true;
            closingPrgDialog();

            if(errorBean.isStoreFailed()) {
                if (!OfflineManager.isOfflineStoreOpen()) {
                    refreshRO();
                }else{
                    Constants.displayMsgReqError(errorBean.getErrorCode(), ReturnOrderTabActivity.this);
                    refreshScreen();
                }
            }else{
                Constants.displayMsgReqError(errorBean.getErrorCode(), ReturnOrderTabActivity.this);
                refreshScreen();
            }

        }


    }

    private void refreshScreen(){
        if (isFromWhere == 3 || isFromWhere == 4) {
            getRefreshFragment(1);
        } else {
            getRefreshFragment(0);
        }
    }

    private void displayErrorDialog(String errMsg){
        String mErrorMsg = "";
        if (Constants.AL_ERROR_MSG.size() > 0) {
            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
        }
        if (mErrorMsg.equalsIgnoreCase("")) {
            UtilConstants.showAlert(errMsg.equalsIgnoreCase("")?getString(R.string.msg_error_occured_during_sync):errMsg, ReturnOrderTabActivity.this);
        } else {
            Constants.customAlertDialogWithScroll(ReturnOrderTabActivity.this, mErrorMsg);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (isRefresh) {
            isRefresh = false;
            if (comingFrom == Constants.SSS_ORDER_POS && (tabPosition+1 )== Constants.TAB_POS_1) {
                getRefreshFragment(0);
            }else if(comingFrom == Constants.SSS_ORDER_POS && (tabPosition+1) == Constants.TAB_POS_2){
                getRefreshFragment(1);
            }
        }
    }
    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.GetStoreOpen.getValue() &&   OfflineManager.isOfflineStoreOpen()) {
            try {
                OfflineManager.getAuthorizations(getApplicationContext());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(ReturnOrderTabActivity.this);
            closingPrgDialog();
            UtilConstants.showAlert(getString(R.string.msg_offline_store_success),
                    ReturnOrderTabActivity.this);
        }else{
            if (comingFrom == Constants.RETURN_ORDER_POS) {
                if (operation == Operation.OfflineRefresh.getValue() && isFromWhere == 1) {
                    Constants.updateLastSyncTimeToTable(alAssignColl);
                    Constants.isSync = false;
                    closingPrgDialog();
                    UtilConstants.showAlert(getString(R.string.msg_sync_successfully_completed), ReturnOrderTabActivity.this);
                    getRefreshFragment(0);
                }
                if (isFromWhere == 3) {
                    if (operation == Operation.Create.getValue() && pendingROVal > 0) {
                        Constants.mBoolIsReqResAval = true;
                        Constants.removeDeviceDocNoFromSharedPref(ReturnOrderTabActivity.this, Constants.ROList, tempRODevList[penROReqCount]);
                        UtilDataVault.storeInDataVault(tempRODevList[penROReqCount], "");
                        penROReqCount++;
                    }
                    if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                        if (UtilConstants.isNetworkAvailable(ReturnOrderTabActivity.this)) {
                            try {
                                concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                                OfflineManager.refreshRequests(ReturnOrderTabActivity.this, concatCollectionStr, this);
                            } catch (OfflineODataStoreException e) {
                                TraceLog.e(Constants.SyncOnRequestSuccess, e);
                            }
                        }else{
                            Constants.isSync = false;
                            closingPrgDialog();
                            UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), ReturnOrderTabActivity.this);
                            getRefreshFragment(1);
                        }
                    } else if (operation == Operation.OfflineRefresh.getValue()) {
                        Constants.updateLastSyncTimeToTable(alAssignColl);
                        Constants.isSync = false;
                        closingPrgDialog();
                        if (mError == 0) {
                            UtilConstants.showAlert(getString(R.string.msg_sync_successfully_completed), ReturnOrderTabActivity.this);
                        } else {
                            displayErrorDialog("");
                        }
                        getRefreshFragment(1);
                    } else {
                        Constants.isSync = false;
                        closingPrgDialog();
                    }
                }
            } else if (comingFrom == Constants.SSS_ORDER_POS) {
                if (operation == Operation.OfflineRefresh.getValue() && isFromWhere == 2) {
                    Constants.updateLastSyncTimeToTable(alAssignColl);
                    Constants.isSync = false;
                    closingPrgDialog();
                    UtilConstants.showAlert(getString(R.string.msg_sync_successfully_completed), ReturnOrderTabActivity.this);
                    getRefreshFragment(0);
                }
                if (isFromWhere == 4) {
                    if (operation == Operation.Create.getValue() && pendingROVal > 0) {
                        Constants.mBoolIsReqResAval = true;
                        Constants.removeDeviceDocNoFromSharedPref(ReturnOrderTabActivity.this, Constants.SOList, tempRODevList[penROReqCount]);
                        UtilDataVault.storeInDataVault(tempRODevList[penROReqCount], "");
                        penROReqCount++;
                    }
                    if ((operation == Operation.Create.getValue()) && (penROReqCount == pendingROVal)) {
                        if (UtilConstants.isNetworkAvailable(ReturnOrderTabActivity.this)) {
                            try {
                                concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                                OfflineManager.refreshRequests(ReturnOrderTabActivity.this, concatCollectionStr, this);
                            } catch (OfflineODataStoreException e) {
                                TraceLog.e(Constants.SyncOnRequestSuccess, e);
                            }
                        }else{
                            Constants.isSync = false;
                            closingPrgDialog();
                            UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), ReturnOrderTabActivity.this);
                            getRefreshFragment(1);
                        }
                    } else if (operation == Operation.OfflineRefresh.getValue()) {
                        Constants.updateLastSyncTimeToTable(alAssignColl);
                        Constants.isSync = false;
                        closingPrgDialog();
                        if (mError == 0) {
                            UtilConstants.showAlert(getString(R.string.msg_sync_successfully_completed), ReturnOrderTabActivity.this);
                        } else {
                            displayErrorDialog("");
                        }
                        getRefreshFragment(1);

                    } else {
                        Constants.isSync = false;
                        closingPrgDialog();
                    }
                }
            }
        }

    }

    private void closingPrgDialog(){
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refres Return Order pending list when pending record deleted.
        if(Constants.Is_Return_Order_Tab_Delete){
            Constants.Is_Return_Order_Tab_Delete = false;
            getRefreshFragment(1);
        }
    }

    /*refresh pager and fragments*/
    public void getRefreshFragment(int position) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        returnOrderListFragmentLeft = new ReturnOrderListFragment();
        returnOrderListFragmentRight = new ReturnOrderListFragment();
        Bundle bundleLeft = new Bundle();
        bundleLeft.putString(Constants.CPGUID, mStrBundleCPGUID);
        bundleLeft.putString(Constants.CPNo, mStrBundleRetID);
        bundleLeft.putString(Constants.RetailerName, mStrBundleRetName);
        bundleLeft.putString(Constants.CPUID, mStrBundleRetUID);
        bundleLeft.putInt(Constants.EXTRA_TAB_POS, Constants.TAB_POS_1);
        bundleLeft.putInt(Constants.comingFrom, comingFrom);
        Bundle bundleRight = new Bundle();
        bundleRight.putString(Constants.CPGUID, mStrBundleCPGUID);
        bundleRight.putString(Constants.CPNo, mStrBundleRetID);
        bundleRight.putString(Constants.RetailerName, mStrBundleRetName);
        bundleRight.putString(Constants.CPUID, mStrBundleRetUID);
        bundleRight.putInt(Constants.EXTRA_TAB_POS, Constants.TAB_POS_2);
        bundleRight.putInt(Constants.comingFrom, comingFrom);
        returnOrderListFragmentLeft.setArguments(bundleLeft);
        returnOrderListFragmentRight.setArguments(bundleRight);
        viewPagerAdapter.addFrag(returnOrderListFragmentLeft, tabLeftTitle);
        viewPagerAdapter.addFrag(returnOrderListFragmentRight, tabRightTitle);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(position);

    }

    @Override
    public void clickedStatus(boolean clickedStatus,String err_msg,ErrorBean errorBean) {
        if (!clickedStatus) {
            closingPrgDialog();
            UtilConstants.showAlert(err_msg, ReturnOrderTabActivity.this);
        }
    }
}
