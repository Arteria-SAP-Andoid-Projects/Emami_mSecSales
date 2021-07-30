package com.arteriatech.emami.sync;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.emami.store.OnlineStoreCacheListner;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.emami.asyncTask.SyncMustSellAsyncTask;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.finance.InvoiceBean;
import com.arteriatech.emami.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.notification.NotificationSetClass;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.store.OnlineManager;
import com.arteriatech.emami.store.OnlineODataStoreException;
import com.arteriatech.emami.store.OnlineStoreListener;
import com.arteriatech.emami.store.OnlineSynLogListener;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreContext;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.mobile.lib.parser.IODataError;
import com.sap.mobile.lib.parser.ParserException;
import com.sap.mobile.lib.request.BaseRequest;
import com.sap.mobile.lib.request.INetListener;
import com.sap.mobile.lib.request.IRequest;
import com.sap.mobile.lib.request.IRequestManager;
import com.sap.mobile.lib.request.IRequestStateElement;
import com.sap.mobile.lib.request.IResponse;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataContractViolationException;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class shows selection type of sync icons in grid manner.
 */


@SuppressLint("NewApi")
public class SyncSelectionActivity extends AppCompatActivity implements UIListener, INetListener, MessageWithBooleanCallBack, OnlineODataInterface {
    GridView grid_main;
    String iconName[] = Constants.syncMenu;
    int OriginalStatus[] = {1, 1, 1, 1};
    int TempStatus[] = {1, 1, 1, 1};
    Context context;
    String[][] invKeyValues;

    ProgressDialog syncProgDialog;
    private boolean dialogCancelled = false;
    Hashtable dbHeadTable;
    ArrayList<HashMap<String, String>> arrtable;
    ArrayList<HashMap<String, String>> arrtable1;
    Hashtable<String, ArrayList<InvoiceBean>> hashTableItemSerialNos;
    private int penReqCount = 0;
    String endPointURL = "";
    String appConnID = "";
    int mError = 0;
    private Handler mHandler = null;
    int mIntPendingCollVal = 0;
    AllSyncBackgroundService mService;
    public static PendingIntent pendingIntent;
    public static AlarmManager alarmManager;
    public static boolean mBound = false;

    public static boolean isDataAvailable = false;
    private boolean tokenFlag = false;
    public static IRequestManager mRequestmanager = null;

    ArrayList<String> alAssignColl = new ArrayList<>();
    ArrayList<String> alFlushColl = new ArrayList<>();
    String concatCollectionStr = "";
    String concatFlushCollStr = "";
    private boolean mBoolIsReqResAval = false;
    private boolean mBoolIsNetWorkNotAval = false;
    private boolean isBatchReqs = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.syncmenu));
        setContentView(R.layout.activity_sync_selction);
        if (!Constants.restartApp(SyncSelectionActivity.this)) {
            mHandler = new Handler();

            setIconVisibility();

            initUI();
        }
    }

    /*Initializes UI*/
    void initUI() {
        grid_main = (GridView) findViewById(R.id.gv_sync_sel);
        grid_main.setAdapter(new ImageAdapter(this));
    }

    /*Sets visibility of menu icons*/
    void setIconVisibility() {
        OriginalStatus[0] = 1;
        OriginalStatus[1] = 1;
        OriginalStatus[2] = 1;
        OriginalStatus[3] = 1;

        int countStatus = 0;
        int len = OriginalStatus.length;
        for (int countOriginalStatus = 0; countOriginalStatus < len; countOriginalStatus++) {
            if (OriginalStatus[countOriginalStatus] == 1) {
                TempStatus[countStatus] = countOriginalStatus;
                countStatus++;
            }
        }
    }

    @Override
    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
        if (clickedStatus) {
            stopService(SyncSelectionActivity.this);
            Constants.isDayStartSyncEnbled = false;
            String mStrPopUpText = "";
            if (!dialogCancelled) {
                try {
                    syncProgDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Constants.iSAutoSync = false;
                mStrPopUpText = getString(R.string.msg_error_occured_during_sync);
                if (Constants.mErrorCount == 0) {
                    mStrPopUpText = getString(R.string.msg_sync_successfully_completed);
                    UtilConstants.dialogBoxWithCallBack(SyncSelectionActivity.this, "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            AppUpgradeConfig.getUpdateAvailability(OfflineManager.offlineStore, SyncSelectionActivity.this, "", false);
                        }
                    });
                } else {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        mStrPopUpText = errorMsg;
                    }
                    Constants.dialogBoxWithButton(SyncSelectionActivity.this, "", mStrPopUpText, getString(R.string.ok), "", null);
                }

            }
        }
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        String type = bundle != null ? bundle.getString(Constants.BUNDLE_RESOURCE_PATH) : "";
        Log.d("responseSuccess", "responseSuccess: " + type);
        if (!isBatchReqs) {
            switch (type) {
                case Constants.CPMarketSet:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onRequestSuccess(Operation.Update.getValue(), "");
                        }
                    });
                    break;
            }
            isBatchReqs = true;
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {
        if (!isBatchReqs) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRequestError(Operation.Update.getValue(), null);
                }
            });
            isBatchReqs = true;
        }
    }

    /**
     * This adapter show icons and text in grid view manner.
     */
    public class ImageAdapter extends BaseAdapter {
        Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            int countTemp = 0;
            int len = OriginalStatus.length;
            for (int countStatus = 0; countStatus < len; countStatus++) {
                if (OriginalStatus[countStatus] == 1) {
                    countTemp++;
                }
            }
            return countTemp;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int iconposition = TempStatus[position];
            View view;
            if (convertView == null) {
                LayoutInflater li = getLayoutInflater();
                view = li.inflate(R.layout.retailer_menu_inside, null);
                view.requestFocus();
                final LinearLayout ll_icon_area_sel = (LinearLayout) view
                        .findViewById(R.id.ll_main_menu_icon_sel);
                TextView tvIconName = (TextView) view.findViewById(R.id.icon_text);
                tvIconName.setTextColor(getResources().getColor(R.color.icon_text_blue));
                tvIconName.setText(iconName[iconposition]);
                ImageView ivIcon = (ImageView) view.findViewById(R.id.ib_must_sell);
                if (iconposition == 0) {
                    ivIcon.setImageResource(R.drawable.sync);
                    ivIcon.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            onAllSync();
                        }
                    });
                    tvIconName.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onAllSync();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onAllSync();
                        }
                    });
                } else if (iconposition == 1) {
                    ivIcon.setImageResource(R.drawable.sync);
                    ivIcon.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            onFreshSync();
                        }
                    });
                    tvIconName.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onFreshSync();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onFreshSync();
                        }
                    });
                } else if (iconposition == 2) {
                    ivIcon.setImageResource(R.drawable.sync);
                    ivIcon.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            GetAsyncTask();
                        }
                    });
                    tvIconName.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GetAsyncTask();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GetAsyncTask();
                        }
                    });
                } else if (iconposition == 3) {
                    ivIcon.setImageResource(R.drawable.ic_sync_history);
                    ivIcon.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            onSyncHist();
                        }
                    });
                    tvIconName.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSyncHist();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSyncHist();
                        }
                    });
                }
                view.setId(position);
            } else {
                view = convertView;
            }
            return view;
        }

    }

    private void GetAsyncTask() {

        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            if (!Constants.iSAutoSync) {
                Constants.Entity_Set.clear();
                Constants.AL_ERROR_MSG.clear();
                mBoolIsNetWorkNotAval = false;
                syncProgDialog = null;
                mBoolIsReqResAval = true;
                Constants.isSync = true;
                Constants.onlineStore = null;
                Constants.SyncTypeID = Constants.str_03;
                isBatchReqs = false;

                try {
                    if (!OfflineManager.isOfflineStoreOpen()) {
                        try {
                            new OpenOfflineStore().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            new GetValueFromDataVault().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress), SyncSelectionActivity.this);
            }
        } else {
            UtilConstants.showAlert(getString(R.string.no_network_conn), SyncSelectionActivity.this);
        }


    }

    /*AsyncTask to get vales from datavault*/
    private class GetValueFromDataVault extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.app_loading));
            syncProgDialog.setCancelable(false);
            syncProgDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            checkPendingReqIsAval();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            onUpdateSync();
        }
    }

    private void closingProgDialog() {
        try {
            syncProgDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPendingReqIsAval() {
        try {
            penReqCount = 0;
            mIntPendingCollVal = 0;
            ArrayList<Object> objectArrayLists = getPendingInvList(SyncSelectionActivity.this);
            if (!objectArrayLists.isEmpty()) {
                mIntPendingCollVal = (int) objectArrayLists.get(0);
                invKeyValues = (String[][]) objectArrayLists.get(1);
            }

            if (mIntPendingCollVal > 0) {

            } else {
                mIntPendingCollVal = 0;
                invKeyValues = null;
                ArrayList<Object> objectArrayList = getPendingCollList(SyncSelectionActivity.this);
                if (!objectArrayList.isEmpty()) {
                    mIntPendingCollVal = (int) objectArrayList.get(0);
                    invKeyValues = (String[][]) objectArrayList.get(1);
                }

            }
            penReqCount = 0;


            alAssignColl.clear();
            alFlushColl.clear();
            concatCollectionStr = "";
            concatFlushCollStr = "";
            ArrayList<String> allAssignColl = getRefreshList();
            if (!allAssignColl.isEmpty()) {
                alAssignColl.addAll(allAssignColl);
                alFlushColl.addAll(allAssignColl);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*Gets list of collection to refresh*/
    public static ArrayList<String> getRefreshList() {
        ArrayList<String> alAssignColl = new ArrayList<>();
        try {
            if (OfflineManager.getVisitStatusForCustomer(Constants.ChannelPartners + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.ChannelPartners);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.CPDMSDivisions + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.CPDMSDivisions);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.Attendances + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.Attendances);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.Visits + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.Visits);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.CompetitorInfos + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.CompetitorInfos);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.MerchReviews + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.MerchReviews);
                alAssignColl.add(Constants.MerchReviewImages);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.VisitActivities + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.VisitActivities);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.Complaints + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.Complaints);
            }

            if (OfflineManager.getVisitStatusForCustomer(Constants.Alerts + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.Alerts);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.ExpenseDocuments + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.Expenses);
                alAssignColl.add(Constants.ExpenseItemDetails);
                alAssignColl.add(Constants.ExpenseDocuments);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.SchemeCPs + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.SchemeCPs);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.SchemeCPDocuments + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.SchemeCPDocuments);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.Claims + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.Claims);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.ClaimDocuments + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.ClaimDocuments);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.CPStockItems + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.CPStockItems);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.CPStockItemSnos + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.CPStockItemSnos);
            }
            if (OfflineManager.getVisitStatusForCustomer(Constants.Customers + "?$filter= sap.islocal() ")) {
                alAssignColl.add(Constants.Customers);
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }
        return alAssignColl;
    }

    private static int getPendingListSize(Context mContext) {
        int size = 0;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

        Set<String> set = new HashSet<>();

        set = sharedPreferences.getStringSet(Constants.CollList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        set = sharedPreferences.getStringSet(Constants.SOList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.ROList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.FeedbackList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.SampleDisbursement, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.CPList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }
        set = sharedPreferences.getStringSet(Constants.SSInvoices, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        set = sharedPreferences.getStringSet(Constants.outletSurvery, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        set = sharedPreferences.getStringSet(Constants.OutletSurveyUpdate, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        return size;
    }


    private int getPendingInvListSize() {
        int size = 0;
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        Set<String> set = new HashSet<>();
        set = sharedPreferences.getStringSet(Constants.InvList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        return size;
    }

    private void getPendingCollList() {
        penReqCount = 0;
        mIntPendingCollVal = 0;
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(Constants.CollList, null);
        invKeyValues = new String[getPendingListSize(SyncSelectionActivity.this)][2];
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.CollList;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.SOList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SOList;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.ROList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.ROList;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.FeedbackList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.FeedbackList;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.SampleDisbursement, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SampleDisbursement;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.Expenses;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.outletSurvery, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.outletSurvery;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.OutletSurveyUpdate, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.OutletSurveyUpdate;
                mIntPendingCollVal++;
            }
        }

        if (mIntPendingCollVal > 0) {
            Arrays.sort(invKeyValues, new ArrayComarator());
        }

    }

    private void getPendingInvList() {
        penReqCount = 0;
        mIntPendingCollVal = 0;
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        invKeyValues = new String[getPendingInvListSize()][2];
        set = sharedPreferences.getStringSet(Constants.InvList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.InvList;
                mIntPendingCollVal++;
            }
        }

        if (mIntPendingCollVal > 0) {
            Arrays.sort(invKeyValues, new ArrayComarator());
        }

    }

    public static ArrayList<Object> getPendingCollList(Context mContext) {
        ArrayList<Object> objectsArrayList = new ArrayList<>();
        int mIntPendingCollVal = 0;
        String[][] invKeyValues = null;
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        set = sharedPreferences.getStringSet(Constants.CollList, null);
        invKeyValues = new String[getPendingListSize(mContext)][2];
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.CollList;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.SOList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SOList;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.ROList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.ROList;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.FeedbackList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.FeedbackList;
                mIntPendingCollVal++;
            }
        }
        set = sharedPreferences.getStringSet(Constants.SampleDisbursement, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SampleDisbursement;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.SSInvoices, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.SSInvoices;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.Expenses, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.Expenses;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.CPList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.CPList;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.outletSurvery, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.outletSurvery;
                mIntPendingCollVal++;
            }
        }

        set = sharedPreferences.getStringSet(Constants.OutletSurveyUpdate, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.OutletSurveyUpdate;
                mIntPendingCollVal++;
            }
        }

        if (mIntPendingCollVal > 0) {
            Arrays.sort(invKeyValues, new ArrayComarator());
            objectsArrayList.add(mIntPendingCollVal);
            objectsArrayList.add(invKeyValues);
        }

        return objectsArrayList;

    }

    public static ArrayList<Object> getPendingInvList(Context mContext) {
        ArrayList<Object> objectsArrayList = new ArrayList<>();
        int mIntPendingCollVal = 0;
        String[][] invKeyValues = null;
        Set<String> set = new HashSet<>();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        invKeyValues = new String[getPendingInvListSize(mContext)][2];
        set = sharedPreferences.getStringSet(Constants.InvList, null);
        if (set != null && !set.isEmpty()) {
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                invKeyValues[mIntPendingCollVal][0] = itr.next().toString();
                invKeyValues[mIntPendingCollVal][1] = Constants.InvList;
                mIntPendingCollVal++;
            }
        }

        if (mIntPendingCollVal > 0) {
            Arrays.sort(invKeyValues, new ArrayComarator());
            objectsArrayList.add(mIntPendingCollVal);
            objectsArrayList.add(invKeyValues);
        }
        return objectsArrayList;
    }

    private static int getPendingInvListSize(Context mContext) {
        int size = 0;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
        Set<String> set = new HashSet<>();
        set = sharedPreferences.getStringSet(Constants.InvList, null);
        if (set != null && !set.isEmpty()) {
            size = size + set.size();
        }

        return size;
    }

    /**
     * This method update pending requests.
     */
    private void onUpdateSync() {
        try {
            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.openOfflineStore(SyncSelectionActivity.this, SyncSelectionActivity.this);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            } else {
                updatePendingReq();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class OpenOfflineStore extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.app_loading));
            syncProgDialog.setCancelable(false);
            syncProgDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                try {
                    if (!OfflineManager.isOfflineStoreOpen()) {
                        try {
                            OfflineManager.openOfflineStore(SyncSelectionActivity.this, SyncSelectionActivity.this);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    } else {
                        updatePendingReq();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (InterruptedException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private void updatePendingReq() {
        try {
            if (OfflineManager.offlineStore.getRequestQueueIsEmpty() && mIntPendingCollVal == 0) {
                closingProgDialog();
                UtilConstants.showAlert(getString(R.string.no_req_to_update_sap), SyncSelectionActivity.this);
                Constants.isSync = false;
            } else {
                if (mIntPendingCollVal > 0) {
                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                        if (Constants.iSAutoSync) {
                            closingProgDialog();
                            UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress), SyncSelectionActivity.this);
                            Constants.isSync = false;

                        } else {
                            startService(this);
                            startServiceMustSells(this); // MustSell
                            try {
                                new PostingDataValutData().execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        closingProgDialog();
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
                        Constants.isSync = false;
                    }
                } else if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                        if (Constants.iSAutoSync) {
                            closingProgDialog();
                            UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress), SyncSelectionActivity.this);
                            Constants.isSync = false;
                        } else {
                            startService(this);
                            startServiceMustSells(this);
                            try {
                                new PostingData().execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        closingProgDialog();
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
                        Constants.isSync = false;
                    }
                }
            }
        } catch (ODataException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * This method calls sync all collections for the selected "All" icon
     */
    private void onAllSync() {
        Constants.SyncTypeID = Constants.str_01;
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress), SyncSelectionActivity.this);
            } else {
                startService(this);
                startServiceMustSells(this); // MustSell
                try {
                    Constants.Entity_Set.clear();
                    Constants.AL_ERROR_MSG.clear();
                    dialogCancelled = false;
                    Constants.isDayStartSyncEnbled = true;
                    syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
                    syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
                    syncProgDialog.setCancelable(false);
                    syncProgDialog.setCanceledOnTouchOutside(false);
                    syncProgDialog.show();

                    syncProgDialog
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface Dialog) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(
                                            SyncSelectionActivity.this, R.style.MyTheme);
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

                    UpdatePendingRequest.getInstance(this).callScheduleFirstLoginSync();


                } catch (Exception e) {
                    e.printStackTrace();
                    closingProgressDialog();
                }
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
            builder.setMessage(R.string.no_network_conn).setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int id) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    /**
     * This method calls fresh sync for the selected "Fresh" icon
     */
    private void onFreshSync() {
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            Intent intent = new Intent(this, SyncSelectViewActivity.class);
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
            builder.setMessage(R.string.no_network_conn).setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int id) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    /*AsyncTask for refresh collection*/
    public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SyncSelectionActivity.this, R.style.MyTheme);
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

                Thread.sleep(1000);
                alAssignColl.clear();
                concatCollectionStr = "";
                alAssignColl.addAll(Constants.getDefinigReqList(SyncSelectionActivity.this));
                concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                try {
                    OfflineManager.refreshStoreSync(getApplicationContext(), SyncSelectionActivity.this, Constants.All, concatCollectionStr);

                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
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

    public class PostingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (syncProgDialog == null) {
                syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
            }
            syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
            syncProgDialog.setCancelable(false);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();
            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SyncSelectionActivity.this, R.style.MyTheme);
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
                Thread.sleep(1000);

                concatFlushCollStr = UtilConstants.getConcatinatinFlushCollectios(alFlushColl);
                try {
                    if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                        try {
                            LogManager.writeLogInfo(concatFlushCollStr + " posting started");
                            OfflineManager.flushQueuedRequests(SyncSelectionActivity.this, concatFlushCollStr);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (ODataException e) {
                    e.printStackTrace();
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

    boolean onlineStoreOpen = false;

    public class PostingDataValutData extends AsyncTask<Void, Boolean, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (syncProgDialog == null) {
                syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
                syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
                syncProgDialog.setCancelable(false);
                syncProgDialog.setCanceledOnTouchOutside(false);
                syncProgDialog.show();
            } else {
                syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
            }
            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SyncSelectionActivity.this, R.style.MyTheme);
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
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                try {
                    // get Application Connection ID
                    LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
                    endPointURL = lgCtx.getAppEndPointUrl();
                    appConnID = LogonCore.getInstance().getLogonContext()
                            .getConnId();
                } catch (LogonCoreException e) {
                    LogManager.writeLogError(getString(R.string.Device_registration_failed), e);
                }

                tokenFlag = false;

                Constants.x_csrf_token = "";
                Constants.ErrorCode = 0;
                Constants.ErrorNo = 0;
                Constants.ErrorName = "";
                Constants.IsOnlineStoreFailed = false;
                mBoolIsReqResAval = true;
                OnlineStoreListener.instance = null;
                LogManager.writeLogInfo("Posting data to backend started");
                try {
                    onlineStoreOpen = OnlineManager.openOnlineStore(SyncSelectionActivity.this);
                } catch (OnlineODataStoreException e) {
                    e.printStackTrace();
                    Constants.printLog("Get online store ended with error(1) " + e.getMessage());
                }
                if (onlineStoreOpen) {
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                    if (sharedPreferences.getString(Constants.isInvoiceCreateKey, "").equalsIgnoreCase(Constants.isInvoiceTcode)) {
                        onLoadToken();
                        if (tokenFlag) {
                            LogManager.writeLogInfo("Token received successfully");
                            if (Constants.x_csrf_token != null && !Constants.x_csrf_token.equalsIgnoreCase("")) {
                                readValuesFromDataVault();
                            } else {
                                return false;
                            }
                        } else {
                            return tokenFlag;
                        }
                    } else {
                        readValuesFromDataVault();
                    }
                } else {
                    return onlineStoreOpen;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return onlineStoreOpen;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {
                closingProgressDialog();
                syncProgDialog = null;

                if (!onlineStoreOpen) {
                    if (Constants.ErrorNo == Constants.Network_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), SyncSelectionActivity.this);

                    } else if (Constants.ErrorNo == Constants.UnAuthorized_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                        UtilConstants.showAlert(getString(R.string.auth_fail_plz_contact_admin, Constants.ErrorNo + ""), SyncSelectionActivity.this);
                    } else if (Constants.ErrorNo == Constants.Comm_Error_Code) {
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), SyncSelectionActivity.this);
                    } else {
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), SyncSelectionActivity.this);
                    }
                } else if (!tokenFlag) {
                    Constants.displayMsgINet(Constants.ErrorNo_Get_Token, SyncSelectionActivity.this);
                } else if (Constants.x_csrf_token == null || Constants.x_csrf_token.equalsIgnoreCase("")) {
                    UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, -2 + ""), SyncSelectionActivity.this);
                }
            }
        }
    }

    private void readValuesFromDataVault() {
        if (mIntPendingCollVal > 0) {
            for (int k = 0; k < invKeyValues.length; k++) {

                while (!mBoolIsReqResAval) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (mBoolIsNetWorkNotAval) {
                    break;
                }
                mBoolIsReqResAval = false;

                String store = null;
                try {
                    store = LogonCore.getInstance().getObjectFromStore(invKeyValues[k][0].toString());
                } catch (LogonCoreException e) {
                    e.printStackTrace();
                }

                if (store != null && !store.equalsIgnoreCase("")) {
                    //Fetch object from data vault
                    try {

                        JSONObject fetchJsonHeaderObject = new JSONObject(store);
                        dbHeadTable = new Hashtable();
                        arrtable = new ArrayList<>();

                        if (fetchJsonHeaderObject.getString(Constants.EntityType).equalsIgnoreCase(Constants.SSInvoice)) {

                            if (!alAssignColl.contains(Constants.SSINVOICES)) {
                                alAssignColl.add(Constants.SSInvoiceItemDetails);
                                alAssignColl.add(Constants.SSINVOICES);
                                alAssignColl.add(Constants.SPStockItemSNos);
                                alAssignColl.add(Constants.SPStockItems);
                            }

                            if (!alAssignColl.contains(Constants.CPStockItems)) {
                                alAssignColl.add(Constants.CPStockItems);
                                alAssignColl.add(Constants.CPStockItemSnos);
                            }

                            if (!alAssignColl.contains(Constants.OutstandingInvoices)) {
                                alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
                                alAssignColl.add(Constants.OutstandingInvoices);
                            }


                            dbHeadTable.put(Constants.InvoiceGUID, fetchJsonHeaderObject.getString(Constants.InvoiceGUID));
                            dbHeadTable.put(Constants.LoginID, fetchJsonHeaderObject.getString(Constants.LoginID));
                            dbHeadTable.put(Constants.InvoiceTypeID, fetchJsonHeaderObject.getString(Constants.InvoiceTypeID));
                            dbHeadTable.put(Constants.InvoiceDate, fetchJsonHeaderObject.getString(Constants.InvoiceDate));
                            dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
                            dbHeadTable.put(Constants.SoldToID, fetchJsonHeaderObject.getString(Constants.SoldToID));
                            dbHeadTable.put(Constants.ShipToID, fetchJsonHeaderObject.getString(Constants.SoldToID));
                            dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
                            dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
                            dbHeadTable.put(Constants.SPGuid, fetchJsonHeaderObject.getString(Constants.SPGuid));
                            dbHeadTable.put(Constants.SoldToCPGUID, fetchJsonHeaderObject.getString(Constants.SoldToCPGUID));
                            dbHeadTable.put(Constants.SoldToTypeID, fetchJsonHeaderObject.getString(Constants.SoldToTypeID));
                            dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));

                            dbHeadTable.put(Constants.NetAmount, fetchJsonHeaderObject.getString(Constants.NetAmount));
                            dbHeadTable.put(Constants.TestRun, fetchJsonHeaderObject.getString(Constants.TestRun));

                            dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));

                            String itemsString = fetchJsonHeaderObject.getString(Constants.strITEMS);

                            String itemsSnoString = fetchJsonHeaderObject.getString(Constants.ITEMSSerialNo);

                            arrtable = UtilConstants.convertToArrayListMap(itemsString);

                            hashTableItemSerialNos = Constants.convertToMapArryList(itemsSnoString);

                            String invGUID32 = fetchJsonHeaderObject.getString(Constants.InvoiceGUID).replace("-", "");

                            String invCreatedOn = fetchJsonHeaderObject.getString(Constants.CreatedOn);
                            String invCreatedAt = fetchJsonHeaderObject.getString(Constants.CreatedAt);

                            String mStrDateTime = UtilConstants.getReArrangeDateFormat(invCreatedOn) + "T" + UtilConstants.convertTimeOnly(invCreatedAt);

                            JSONObject invoiceHeader = Constants.prepareInvoiceJsonObject(dbHeadTable, arrtable, hashTableItemSerialNos);
                            performPushSSSubscription(SyncSelectionActivity.this, invoiceHeader.toString(), invGUID32.toUpperCase(), mStrDateTime, SyncSelectionActivity.this);
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Collection)) {

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
                            dbHeadTable = Constants.getCollHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);

                            arrtable = UtilConstants.convertToArrayListMap(itemsString);

                            try {
                                OnlineManager.createCollectionEntry(dbHeadTable, arrtable, SyncSelectionActivity.this);

                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }

                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SecondarySOCreate)) {
                            if (!alAssignColl.contains(Constants.SSINVOICES)) {
                                alAssignColl.add(Constants.SSInvoiceItemDetails);
                                alAssignColl.add(Constants.SSINVOICES);
                            }
                            if (!alAssignColl.contains(Constants.SSSOs)) {
                                alAssignColl.add(Constants.SSSoItemDetails);
                                alAssignColl.add(Constants.SSSOs);
                            }
                            dbHeadTable = Constants.getSOHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.createSOEntity(dbHeadTable, arrtable, SyncSelectionActivity.this);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Feedback)) {
                            // preparing entity pending
                            if (!alAssignColl.contains(Constants.Feedbacks)) {
                                alAssignColl.add(Constants.Feedbacks);
                                alAssignColl.add(Constants.FeedbackItemDetails);
                            }


                            dbHeadTable = Constants.getFeedbackHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.createFeedBack(dbHeadTable, arrtable, SyncSelectionActivity.this);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SampleDisbursement)) {
                            if (!alAssignColl.contains(Constants.SSINVOICES)) {
                                alAssignColl.add(Constants.SSInvoiceItemDetails);
                                alAssignColl.add(Constants.SSINVOICES);
                            }
                            dbHeadTable = Constants.getSSInvoiceHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.createSSInvoiceEntity(dbHeadTable, arrtable, SyncSelectionActivity.this);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.ReturnOrderCreate)) {
                            if (!alAssignColl.contains(Constants.SSROs)) {
                                alAssignColl.add(Constants.SSROItemDetails);
                                alAssignColl.add(Constants.SSROs);
                            }
                            dbHeadTable = Constants.getROHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.createROEntity(dbHeadTable, arrtable, SyncSelectionActivity.this);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Expenses)) {
                            if (!alAssignColl.contains(Constants.Expenses)) {
                                alAssignColl.add(Constants.ExpenseItemDetails);
                                alAssignColl.add(Constants.Expenses);
                            }
                            dbHeadTable = Constants.getExpenseHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.createDailyExpense(dbHeadTable, arrtable, SyncSelectionActivity.this);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.ChannelPartners)) {
                            // preparing entity pending
                            if (!alAssignColl.contains(Constants.ChannelPartners)) {
                                alAssignColl.add(Constants.ChannelPartners);
                                alAssignColl.add(Constants.CPDMSDivisions);
                            }


                            dbHeadTable = Constants.getCPHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.createCP(dbHeadTable, arrtable, SyncSelectionActivity.this);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SSInvoices)) {
                            if (!alAssignColl.contains(Constants.SSInvoices)) {
                                alAssignColl.add(Constants.SSInvoices);
                                alAssignColl.add(Constants.SSInvoiceItemDetails);
                                alAssignColl.add(Constants.SSSOs);
                                alAssignColl.add(Constants.SSSoItemDetails);
                                alAssignColl.add(Constants.CPStockItems);
                                alAssignColl.add(Constants.CPStockItemSnos);
                                alAssignColl.add(Constants.OutstandingInvoices);
                                alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
                            }
                            dbHeadTable = Constants.getSecondaryInvHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            try {
                                OnlineManager.createInvEntity(dbHeadTable, arrtable, SyncSelectionActivity.this);
                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }
                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.OutletSurveyCreate)) {

                            if (!alAssignColl.contains(Constants.CPMarketSet)) {
                                alAssignColl.add(Constants.CPMarketSet);
                                alAssignColl.add(Constants.CPBusinessSet);
                                alAssignColl.add(Constants.CompetitorSales);
                            }

                            dbHeadTable = Constants.getOutletSurveyHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                            String itemsString1 = fetchJsonHeaderObject.getString(Constants.ITEM_TXT1);

                            arrtable = UtilConstants.convertToArrayListMap(itemsString);
                            arrtable1 = UtilConstants.convertToArrayListMap(itemsString1);

                            try {
                                OnlineManager.createOutletSurvey(dbHeadTable, arrtable, arrtable1, SyncSelectionActivity.this);

                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }

                        } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.OutletSurveyUpdate)) {

                            if (!alAssignColl.contains(Constants.CPMarketSet)) {
                                alAssignColl.add(Constants.CPMarketSet);
                                alAssignColl.add(Constants.CPBusinessSet);
                                alAssignColl.add(Constants.CompetitorSales);
                            }

                            dbHeadTable = Constants.getOutletSurveyUpdateHeader(fetchJsonHeaderObject);
                            String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);

                            arrtable = UtilConstants.convertToArrayListMap(itemsString);

                            try {
                                OfflineManager.updateCPMarketBatch(dbHeadTable, arrtable, SyncSelectionActivity.this);

                            } catch (OnlineODataStoreException e) {
                                e.printStackTrace();
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mBoolIsReqResAval = true;
                }


            }
        }
    }

    public class PostingDataValutDataCollFeedBack extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (syncProgDialog == null) {
                syncProgDialog = new ProgressDialog(SyncSelectionActivity.this, R.style.ProgressDialogTheme);
                syncProgDialog.setMessage(getString(R.string.updating_data_plz_wait));
                syncProgDialog.setCancelable(false);
                syncProgDialog.setCanceledOnTouchOutside(false);
                syncProgDialog.show();
            }
            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SyncSelectionActivity.this, R.style.MyTheme);
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

                Thread.sleep(1000);


                tokenFlag = false;
                mBoolIsReqResAval = true;

                if (mIntPendingCollVal > 0) {

                    for (int k = 0; k < invKeyValues.length; k++) {

                        while (!mBoolIsReqResAval) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (mBoolIsNetWorkNotAval) {
                            break;
                        }
                        mBoolIsReqResAval = false;

                        String store = null;
                        try {
                            store = LogonCore.getInstance().getObjectFromStore(invKeyValues[k][0].toString());
                        } catch (LogonCoreException e) {
                            e.printStackTrace();
                        }

                        //Fetch object from data vault
                        try {

                            JSONObject fetchJsonHeaderObject = new JSONObject(store);
                            dbHeadTable = new Hashtable();
                            arrtable = new ArrayList<>();

                            if (fetchJsonHeaderObject.getString(Constants.EntityType).equalsIgnoreCase(Constants.SSInvoice)) {

                                if (!alAssignColl.contains(Constants.SSINVOICES)) {
                                    alAssignColl.add(Constants.SSInvoiceItemDetails);
                                    alAssignColl.add(Constants.SSINVOICES);
                                    alAssignColl.add(Constants.SPStockItemSNos);
                                    alAssignColl.add(Constants.SPStockItems);
                                }

                                if (!alAssignColl.contains(Constants.CPStockItems)) {
                                    alAssignColl.add(Constants.CPStockItemDetails);
                                    alAssignColl.add(Constants.CPStockItems);
                                    alAssignColl.add(Constants.CPStockItemSnos);
                                }


                                dbHeadTable.put(Constants.InvoiceGUID, fetchJsonHeaderObject.getString(Constants.InvoiceGUID));
                                dbHeadTable.put(Constants.LoginID, fetchJsonHeaderObject.getString(Constants.LoginID));
                                //Todo
                                dbHeadTable.put(Constants.InvoiceTypeID, fetchJsonHeaderObject.getString(Constants.InvoiceTypeID));
                                dbHeadTable.put(Constants.InvoiceDate, fetchJsonHeaderObject.getString(Constants.InvoiceDate));
                                dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
                                dbHeadTable.put(Constants.SoldToID, fetchJsonHeaderObject.getString(Constants.SoldToID));
                                dbHeadTable.put(Constants.ShipToID, fetchJsonHeaderObject.getString(Constants.ShipToID));
                                dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
                                dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
                                dbHeadTable.put(Constants.SPGuid, fetchJsonHeaderObject.getString(Constants.SPGuid));
                                dbHeadTable.put(Constants.SoldToCPGUID, fetchJsonHeaderObject.getString(Constants.SoldToCPGUID));
                                dbHeadTable.put(Constants.SoldToTypeID, fetchJsonHeaderObject.getString(Constants.SoldToTypeID));
                                dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));

                                dbHeadTable.put(Constants.NetAmount, fetchJsonHeaderObject.getString(Constants.NetAmount));
                                dbHeadTable.put(Constants.TestRun, fetchJsonHeaderObject.getString(Constants.TestRun));

                                dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));

                                String itemsString = fetchJsonHeaderObject.getString(Constants.strITEMS);

                                String itemsSnoString = fetchJsonHeaderObject.getString(Constants.ITEMSSerialNo);

                                arrtable = UtilConstants.convertToArrayListMap(itemsString);

                                hashTableItemSerialNos = Constants.convertToMapArryList(itemsSnoString);

                                String invGUID32 = fetchJsonHeaderObject.getString(Constants.InvoiceGUID).replace("-", "");

                                String invCreatedOn = fetchJsonHeaderObject.getString(Constants.CreatedOn);
                                String invCreatedAt = fetchJsonHeaderObject.getString(Constants.CreatedAt);

                                String mStrDateTime = UtilConstants.getReArrangeDateFormat(invCreatedOn) + "T" + UtilConstants.convertTimeOnly(invCreatedAt);

                                JSONObject invoiceHeader = Constants.prepareInvoiceJsonObject(dbHeadTable, arrtable, hashTableItemSerialNos);
                                performPushSSSubscription(SyncSelectionActivity.this, invoiceHeader.toString(), invGUID32.toUpperCase(), mStrDateTime, SyncSelectionActivity.this);
                            } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Collection)) {

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
                                dbHeadTable = Constants.getCollHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);

                                arrtable = UtilConstants.convertToArrayListMap(itemsString);

                                try {
                                    OnlineManager.createCollectionEntry(dbHeadTable, arrtable, SyncSelectionActivity.this);

                                } catch (OnlineODataStoreException e) {
                                    e.printStackTrace();
                                }

                            } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SecondarySOCreate)) {

                                if (!alAssignColl.contains(Constants.SSINVOICES)) {
                                    alAssignColl.add(Constants.SSInvoiceItemDetails);
                                    alAssignColl.add(Constants.SSINVOICES);
                                }
                                if (!alAssignColl.contains(Constants.SSSOs)) {
                                    alAssignColl.add(Constants.SSSOs);
                                    alAssignColl.add(Constants.SSSoItemDetails);
                                }
                                dbHeadTable = Constants.getSOHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                try {
                                    OnlineManager.createSOEntity(dbHeadTable, arrtable, SyncSelectionActivity.this);
                                } catch (OnlineODataStoreException e) {
                                    e.printStackTrace();
                                }
                            } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Feedback)) {
                                // preparing entity pending
                                if (!alAssignColl.contains(Constants.Feedbacks)) {
                                    alAssignColl.add(Constants.Feedbacks);
                                    alAssignColl.add(Constants.FeedbackItemDetails);
                                }

                                dbHeadTable = Constants.getFeedbackHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                try {
                                    OnlineManager.createFeedBack(dbHeadTable, arrtable, SyncSelectionActivity.this);
                                } catch (OnlineODataStoreException e) {
                                    e.printStackTrace();
                                }
                            } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.ReturnOrderCreate)) {
                                if (!alAssignColl.contains(Constants.SSROs)) {
                                    alAssignColl.add(Constants.SSROItemDetails);
                                    alAssignColl.add(Constants.SSROs);
                                }
                                dbHeadTable = Constants.getROHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                try {
                                    OnlineManager.createROEntity(dbHeadTable, arrtable, SyncSelectionActivity.this);
                                } catch (OnlineODataStoreException e) {
                                    e.printStackTrace();
                                }
                            } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SampleDisbursement)) {
                                if (!alAssignColl.contains(Constants.SSINVOICES)) {
                                    alAssignColl.add(Constants.SSInvoiceItemDetails);
                                    alAssignColl.add(Constants.SSINVOICES);
                                }
                                dbHeadTable = Constants.getSSInvoiceHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                try {
                                    OnlineManager.createSSInvoiceEntity(dbHeadTable, arrtable, SyncSelectionActivity.this);
                                } catch (OnlineODataStoreException e) {
                                    e.printStackTrace();
                                }
                            } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.ChannelPartners)) {
                                // preparing entity pending
                                if (!alAssignColl.contains(Constants.ChannelPartners)) {
                                    alAssignColl.add(Constants.ChannelPartners);
                                    alAssignColl.add(Constants.CPDMSDivisions);
                                }


                                dbHeadTable = Constants.getCPHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                try {
                                    OnlineManager.createCP(dbHeadTable, arrtable, SyncSelectionActivity.this);
                                } catch (OnlineODataStoreException e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

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

    @Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception, SyncSelectionActivity.this);
        try {
            if (errorBean.hasNoError()) {
                mError++;
                penReqCount++;
                mBoolIsReqResAval = true;
                if (((operation == Operation.Create.getValue()) || (operation == Operation.Update.getValue())) && (penReqCount == mIntPendingCollVal)) {
                    try {
                        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                            if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                                try {
                                    new PostingData().execute();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            } else {
                                if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                                    try {
                                        if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                                            alAssignColl.add(Constants.ConfigTypsetTypeValues);

                                        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

                                        OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);
                                    } catch (OfflineODataStoreException e) {
                                        TraceLog.e("Sync::onRequestSuccess", e);
                                    }
                                } else {
                                    stopService(SyncSelectionActivity.this);
                                    closingProgressDialog();
                                    UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
                                }
                            }
                        } else {
                            stopService(SyncSelectionActivity.this);
                            closingProgressDialog();
                            UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
                        }


                    } catch (ODataException e3) {
                        e3.printStackTrace();
                    }
                }
                if (operation == Operation.OfflineFlush.getValue()) {
                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                        try {
                            if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                                alAssignColl.add(Constants.ConfigTypsetTypeValues);
                            concatCollectionStr = Constants.getConcatinatinFlushCollectios(alAssignColl);
                            LogManager.writeLogInfo(concatCollectionStr + " refresh started");
                            OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);
                        } catch (OfflineODataStoreException e) {
                            TraceLog.e("Sync::onRequestSuccess", e);
                        }
                    } else {
                        stopService(SyncSelectionActivity.this);
                        closingProgressDialog();
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
                    }

                } else if (operation == Operation.OfflineRefresh.getValue()) {
                    LogManager.writeLogError("Error : " + exception.getMessage());
                    Constants.isSync = false;
                    String mErrorMsg = "";
                    if (Constants.AL_ERROR_MSG.size() > 0) {
                        mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
                    }
                    closingProgressDialog();
                    stopService(SyncSelectionActivity.this);
                    if (mErrorMsg.equalsIgnoreCase("")) {
                        UtilConstants.showAlert(errorBean.getErrorMsg(), SyncSelectionActivity.this);
                    } else {
                        Constants.customAlertDialogWithScroll(SyncSelectionActivity.this, mErrorMsg);
                    }

                } else if (operation == Operation.GetStoreOpen.getValue()) {
                    stopService(SyncSelectionActivity.this);
                    closingProgressDialog();
                    UtilConstants.showAlert(getString(R.string.msg_offline_store_failure),
                            SyncSelectionActivity.this);
                }
            } else {
                stopService(SyncSelectionActivity.this);
                mBoolIsReqResAval = true;
                mBoolIsNetWorkNotAval = true;
                Constants.isSync = false;
                if (errorBean.isStoreFailed()) {
                    OfflineManager.offlineStore = null;
                    OfflineManager.options = null;
                    try {
                        if (!OfflineManager.isOfflineStoreOpen()) {
                            closingProgressDialog();
                            try {
                                new OpenOfflineStore().execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            closingProgressDialog();
                            Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectionActivity.this);
                        }
                    } catch (Exception e) {
                        closingProgressDialog();
                        Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectionActivity.this);
                        e.printStackTrace();
                    }
                } else {
                    closingProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectionActivity.this);
                }


            }
        } catch (Exception e) {
            mBoolIsReqResAval = true;
            mBoolIsNetWorkNotAval = true;
            Constants.isSync = false;
            stopService(SyncSelectionActivity.this);
            closingProgressDialog();
            Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectionActivity.this);
        }

    }

    @Override
    public void onRequestSuccess(int operation, String key) {

        if (((operation == Operation.Create.getValue()) || (operation == Operation.Update.getValue())) && mIntPendingCollVal > 0) {
            mBoolIsReqResAval = true;
            if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CollList)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.CollList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOList)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SOList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.FeedbackList)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.FeedbackList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.InvList)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.InvList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.ROList)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.ROList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SampleDisbursement)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SampleDisbursement, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.Expenses)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.Expenses, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CPList)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.CPList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SSInvoices)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SSInvoices, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.outletSurvery)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.outletSurvery, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.OutletSurveyUpdate)) {
                Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.OutletSurveyUpdate, invKeyValues[penReqCount][0]);
            }

            UtilDataVault.storeInDataVault(invKeyValues[penReqCount][0], "");

            penReqCount++;
        }
        if (((operation == Operation.Create.getValue()) || (operation == Operation.Update.getValue())) && (penReqCount == mIntPendingCollVal)) {
            try {
                if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                        try {
                            new PostingData().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        stopService(SyncSelectionActivity.this);
                        closingProgressDialog();
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
                    }
                } else {
                    if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                        try {
                            if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                                alAssignColl.add(Constants.ConfigTypsetTypeValues);

                            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                            OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);
                        } catch (OfflineODataStoreException e) {
                            TraceLog.e(Constants.SyncOnRequestSuccess, e);
                        }
                    } else {
                        stopService(SyncSelectionActivity.this);
                        closingProgressDialog();
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
                    }
                }

            } catch (ODataException e) {
                e.printStackTrace();
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {
            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                try {
                    if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                        alAssignColl.add(Constants.ConfigTypsetTypeValues);
                    concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                    OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);
                } catch (OfflineODataStoreException e) {
                    TraceLog.e(Constants.SyncOnRequestSuccess, e);
                }
            } else {
                stopService(SyncSelectionActivity.this);
                closingProgressDialog();
                UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
            }


        } else if (operation == Operation.OfflineRefresh.getValue()) {

            if (alAssignColl.contains(Constants.UserProfileAuthSet)) {
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }

            if (alAssignColl.contains(Constants.RoutePlans) || alAssignColl.contains(Constants.ChannelPartners) || alAssignColl.contains(Constants.Visits)) {
                Constants.alTodayBeatRet.clear();
                Constants.TodayTargetRetailersCount = Constants.getVisitTargetForToday();
                Constants.TodayActualVisitRetailersCount = Constants.getVisitedRetailerCount(Constants.alTodayBeatRet);
            }
            if (alAssignColl.contains(Constants.Visits) || alAssignColl.contains(Constants.ChannelPartners)) {
                Constants.setBirthdayListToDataValut(SyncSelectionActivity.this);
                Constants.setBirthDayRecordsToDataValut(SyncSelectionActivity.this);
                Constants.setAppointmentNotification(SyncSelectionActivity.this);
            }
            if (alAssignColl.contains(Constants.SSSOs) || alAssignColl.contains(Constants.Targets)) {
                Constants.loadingTodayAchived(SyncSelectionActivity.this, Constants.alTodayBeatRet);
            }

            if (alAssignColl.contains(Constants.MerchReviews)) {
                Constants.deleteDeviceMerchansisingFromDataVault(SyncSelectionActivity.this);
            }
            if (alAssignColl.contains(Constants.Alerts)) {
                Constants.setAlertsRecordsToDataValut(SyncSelectionActivity.this);
            }
            // Staring MustSell Code Snippet
            try {
                new SyncMustSellAsyncTask(SyncSelectionActivity.this, new MessageWithBooleanCallBack() {
                    @Override
                    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
                        Log.d("clickedStatus Req", clickedStatus + "");
                        setUI();
                    }
                }, "").execute();
            } catch (Exception e) {
                setUI();
                e.printStackTrace();
            }
            // Ending MustSell Code Snippet

               /* stopService(SyncSelectionActivity.this);
                Constants.updateLastSyncTimeToTable(alAssignColl);
                closingProgressDialog();

                String mErrorMsg = "";
                if(Constants.AL_ERROR_MSG.size()>0){
                    mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
                }

                Constants.isSync = false;
                if (mError == 0) {
                    UtilConstants.dialogBoxWithCallBack(SyncSelectionActivity.this, "", getString(R.string.msg_sync_successfully_completed), getString(R.string.ok), "", false, new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean b) {
                            AppUpgradeConfig.getUpdateAvailability(OfflineManager.offlineStore,SyncSelectionActivity.this,"",false);
                        }
                    });
                } else {
                    if(mErrorMsg.equalsIgnoreCase("")){
                        UtilConstants.showAlert(getString(R.string.error_occured_during_post), SyncSelectionActivity.this);
                    }else{
                        Constants.customAlertDialogWithScroll(SyncSelectionActivity.this,mErrorMsg);
                    }
                }*/

        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
            stopService(SyncSelectionActivity.this);
//                Constants.ReIntilizeStore =false;
            Constants.isSync = false;
            new NotificationSetClass(getApplicationContext());
            try {
                OfflineManager.getAuthorizations(getApplicationContext());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(SyncSelectionActivity.this);

            // Staring MustSell Code Snippet
            try {
                new SyncMustSellAsyncTask(SyncSelectionActivity.this, new MessageWithBooleanCallBack() {
                    @Override
                    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
                        Log.d("clickedStatus Req", clickedStatus + "");
                        setStoreOpenUI();
                    }
                }, Constants.All).execute();
            } catch (Exception e) {
                setStoreOpenUI();
                e.printStackTrace();
            }
            // Ending MustSell Code Snippet


        }
    }

    private void setStoreOpenUI() {
        closingProgressDialog();
        UtilConstants.showAlert(getString(R.string.msg_offline_store_success),
                SyncSelectionActivity.this);
    }

    private void setUI() {
        stopService(SyncSelectionActivity.this);
        Constants.updateLastSyncTimeToTable(alAssignColl);
        closingProgressDialog();

        String mErrorMsg = "";
        if (Constants.AL_ERROR_MSG.size() > 0) {
            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
        }

        Constants.isSync = false;
        if (mError == 0) {
            UtilConstants.dialogBoxWithCallBack(SyncSelectionActivity.this, "", getString(R.string.msg_sync_successfully_completed), getString(R.string.ok), "", false, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                    AppUpgradeConfig.getUpdateAvailability(OfflineManager.offlineStore, SyncSelectionActivity.this, "", false);
                }
            });
        } else {
            if (mErrorMsg.equalsIgnoreCase("")) {
                UtilConstants.showAlert(getString(R.string.error_occured_during_post), SyncSelectionActivity.this);
            } else {
                Constants.customAlertDialogWithScroll(SyncSelectionActivity.this, mErrorMsg);
            }
        }
    }

    private void closingProgressDialog() {
        try {
            syncProgDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onSyncHist() {
        Intent intent = new Intent(this, SyncHistoryActivity.class);
        startActivity(intent);
    }

    /**
     * This method retrieves csrf token from server.
     */
    private void onLoadToken() {
        try {
            // get Application Connection ID
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
            endPointURL = lgCtx.getAppEndPointUrl();
            appConnID = LogonCore.getInstance().getLogonContext()
                    .getConnId();
        } catch (LogonCoreException e) {
            LogManager.writeLogError(getString(R.string.Device_registration_failed), e);
        }

        String PushUrl = endPointURL + "/?sap-language=en";
        IRequest req = new BaseRequest();
        req.setRequestMethod(IRequest.REQUEST_METHOD_GET);
        req.setPriority(1);
        req.setRequestUrl(PushUrl);
        Hashtable headers = new Hashtable();
        headers.put("X-SMP-APPCID", appConnID);
        ((BaseRequest) req).setHeaders(headers);
        req.setListener(new INetListener() {
            @Override
            public void onSuccess(IRequest iRequest, IResponse iResponse) {
                try {
                    tokenFlag = true;
                    isDataAvailable = true;
                    Constants.x_csrf_token = iResponse.getHeaders("x-csrf-token")[0].getValue();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(IRequest iRequst, IResponse iResponse, IRequestStateElement iReqStateElement) {
                Constants.ErrorNo_Get_Token = iReqStateElement.getErrorCode();
                try {
                    isDataAvailable = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (iReqStateElement.getErrorCode() == 4) {
                    LogManager.writeLogError(Constants.error_txt + getString(R.string.auth_fail_plz_contact_admin, iReqStateElement.getErrorCode() + ""));
                } else if (iReqStateElement.getErrorCode() == 3) {
                    LogManager.writeLogError(Constants.error_txt + getString(R.string.data_conn_lost_during_sync_error_code, iReqStateElement.getErrorCode() + ""));
                } else {
                    LogManager.writeLogError(Constants.error_txt + getString(R.string.data_conn_lost_during_sync_error_code, iReqStateElement.getErrorCode() + ""));
                }
            }
        });
        IRequestManager mRequestmanager = null;
        mRequestmanager = Constants.mApplication.getRequestManager();
        mRequestmanager.makeRequest(req);
        while (!isDataAvailable) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isDataAvailable = false;

    }

    public static void performPushSSSubscription(Context mContext, String entry, String invGUID32, String mStrDateTime, INetListener iNetListener) {
        mRequestmanager = Constants.mApplication.getRequestManager();

        String endPointURL = "";
        String appConnID = "";
        try {
            Constants.x_csrf_token = "";
            // get Application Connection ID
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
            endPointURL = lgCtx.getAppEndPointUrl();
            appConnID = LogonCore.getInstance().getLogonContext()
                    .getConnId();
        } catch (LogonCoreException e) {
            LogManager.writeLogError(mContext.getString(R.string.Device_registration_failed), e);
        }

        try {
            String PushUrl = endPointURL + "/" + Constants.SSINVOICES;
            String xmlData = entry;
            byte[] data = null;
            try {
                data = xmlData.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unsupported encoding!");
            }

            IRequest request = new BaseRequest();
            request.setListener(iNetListener);
            request.setRequestUrl(PushUrl);
            Hashtable<String, String> headers = new Hashtable<String, String>();
            headers.put("Content-Type", "application/json");
            headers.put("x-csrf-token", Constants.x_csrf_token);
            headers.put("X-SMP-APPCID", appConnID);
            headers.put("Cookie", Constants.cookies);
            headers.put("Accept", "application/xml");
            headers.put("RequestID", invGUID32);
            headers.put("RepeatabilityCreation", mStrDateTime);
            ((BaseRequest) request).setData(data);
            request.setHeaders(headers);
            ((BaseRequest) request).setData(data);
            request.setRequestMethod(IRequest.REQUEST_METHOD_POST);
            request.setPriority(IRequest.PRIORITY_HIGH);
            request.setRequestTAG(Constants.SSINVOICES);

            mRequestmanager.makeRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(IRequest arg0, IResponse aResponse,
                        IRequestStateElement iReqStateElement) {
        try {
            int errorcode = iReqStateElement.getErrorCode();
            Constants.ErrorNo = iReqStateElement.getErrorCode();
            if (IRequestStateElement.AUTHENTICATION_ERROR != errorcode && IRequestStateElement.NETWORK_ERROR != errorcode) {
                mError++;
                penReqCount++;
                mBoolIsReqResAval = true;
                Constants.parser = Constants.mApplication.getParser();
                IODataError errResponse = null;
                HttpResponse response = aResponse;
                if (aResponse != null) {
                    try {
                        HttpEntity responseEntity = aResponse.getEntity();
                        String responseString = EntityUtils.toString(responseEntity);
                        responseString = responseString.replace(getString(R.string.Bad_Request), "");

                        errResponse = Constants.parser.parseODataError(responseString);

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();

                    } catch (IllegalStateException e) {
                        e.printStackTrace();

                    } catch (ParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
                String errorMsg = errResponse.getMessage() != null ? errResponse.getMessage() : "";
                LogManager.writeLogError(getString(R.string.Error_in_Retailer_invoice) + errorMsg);
                Constants.Entity_Set.add("Invoice");
                Constants.AL_ERROR_MSG.add("Invoice : " + errorMsg);
            } else {
                if (iReqStateElement.getErrorCode() == 4) {
                    LogManager.writeLogError(Constants.Error + " :" + getString(R.string.auth_fail_plz_contact_admin, iReqStateElement.getErrorCode() + ""));
                } else if (iReqStateElement.getErrorCode() == 3) {
                    LogManager.writeLogError(Constants.Error + " :" + getString(R.string.data_conn_lost_during_sync_error_code, iReqStateElement.getErrorCode() + ""));
                } else {
                    LogManager.writeLogError(Constants.Error + " :" + getString(R.string.data_conn_lost_during_sync_error_code, iReqStateElement.getErrorCode() + ""));
                }
                mHandler.post(mUpdateError);
            }
        } catch (Exception e) {
            mHandler.post(mUpdateError);
        }


        if (penReqCount == mIntPendingCollVal) {
            mHandler.post(mUpdateResults);
        }
    }

    protected void updateErrorInUi() {
        mBoolIsReqResAval = true;
        mBoolIsNetWorkNotAval = true;
        Constants.isSync = false;
        closingProgressDialog();
        Constants.displayMsgINet(Constants.ErrorNo, SyncSelectionActivity.this);
    }

    @Override
    public void onSuccess(IRequest aRequest, IResponse response) {
        try {
            mBoolIsReqResAval = true;
            if (mIntPendingCollVal > 0) {

                if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CollList)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.CollList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOList)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SOList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.FeedbackList)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.FeedbackList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.InvList)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.InvList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SampleDisbursement)) {
                    Constants.removeDeviceDocNoFromSharedPref(SyncSelectionActivity.this, Constants.SampleDisbursement, invKeyValues[penReqCount][0]);
                }

                UtilDataVault.storeInDataVault(invKeyValues[penReqCount][0], "");

                penReqCount++;
            }

            //ignore this sections for hard codes (parsing xml request for invoice creation)
            String repData = EntityUtils.toString(response.getEntity());

            int repStInd = repData.toString().indexOf("<d:InvoiceNo>") + 13;
            int repEndInd = repData.toString().indexOf("</d:InvoiceNo>");
            String invNo = repData.substring(repStInd, repEndInd);

            String popUpText = "Retailer invoice # " + invNo + " created successfully.";

            LogManager.writeLogInfo(popUpText);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (penReqCount == mIntPendingCollVal) {
            mHandler.post(mUpdateResults);
        }

    }

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };
    final Runnable mUpdateError = new Runnable() {
        public void run() {
            updateErrorInUi();
        }
    };

    @SuppressWarnings("deprecation")
    protected void updateResultsInUi() {

        if (mError == 0) {

            if (!UtilConstants.isNetworkAvailable(SyncSelectionActivity.this)) {
                stopService(SyncSelectionActivity.this);
                closingProgressDialog();
                UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
            } else {

                getPendingCollList();
                penReqCount = 0;

                if (mIntPendingCollVal > 0) {

                } else {
                    mIntPendingCollVal = 0;
                    invKeyValues = null;
                    ArrayList<Object> objectArrayList = getPendingCollList(SyncSelectionActivity.this);
                    if (!objectArrayList.isEmpty()) {
                        mIntPendingCollVal = (int) objectArrayList.get(0);
                        invKeyValues = (String[][]) objectArrayList.get(1);
                    }

                }
                penReqCount = 0;


                if (mIntPendingCollVal > 0) {
                    mBoolIsNetWorkNotAval = false;
                    if (UtilConstants.isNetworkAvailable(SyncSelectionActivity.this)) {
                        try {
                            new PostingDataValutDataCollFeedBack().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        stopService(SyncSelectionActivity.this);
                        closingProgressDialog();
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
                    }
                } else {


                    try {
                        if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                            if (UtilConstants.isNetworkAvailable(SyncSelectionActivity.this)) {
                                try {
                                    new PostingData().execute();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                stopService(SyncSelectionActivity.this);
                                closingProgressDialog();
                                UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
                            }
                        } else {
                            if (UtilConstants.isNetworkAvailable(SyncSelectionActivity.this)) {
                                try {
                                    if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                                        alAssignColl.add(Constants.ConfigTypsetTypeValues);

                                    concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                                    OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);

                                } catch (OfflineODataStoreException e) {
                                    TraceLog.e(Constants.SyncOnRequestSuccess, e);
                                }
                            } else {
                                stopService(SyncSelectionActivity.this);
                                closingProgressDialog();
                                UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
                            }
                        }

                    } catch (ODataException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {

            try {
                if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                    if (UtilConstants.isNetworkAvailable(SyncSelectionActivity.this)) {
                        try {
                            new PostingData().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        stopService(SyncSelectionActivity.this);
                        closingProgressDialog();
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
                    }
                } else {
                    if (UtilConstants.isNetworkAvailable(SyncSelectionActivity.this)) {
                        try {
                            if (!alAssignColl.contains(Constants.ConfigTypsetTypeValues))
                                alAssignColl.add(Constants.ConfigTypsetTypeValues);

                            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                            OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, SyncSelectionActivity.this);
                        } catch (OfflineODataStoreException e) {
                            TraceLog.e(Constants.SyncOnRequestSuccess, e);
                        }
                    } else {
                        stopService(SyncSelectionActivity.this);
                        closingProgressDialog();
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), SyncSelectionActivity.this);
                    }
                }

            } catch (ODataException e) {
                e.printStackTrace();
            }

        }
    }


    public void showPopup(View v) {
        UtilConstants.showPopup(getApplicationContext(), v, SyncSelectionActivity.this,
                R.menu.menu_back);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back:
                onBackPressed();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    public static class ArrayComarator implements Comparator<String[]> {

        @Override
        public int compare(String s1[], String s2[]) {
            BigInteger i1 = null;
            BigInteger i2 = null;
            try {
                i1 = new BigInteger(s1[0]);
            } catch (NumberFormatException e) {
            }

            try {
                i2 = new BigInteger(s2[0]);
            } catch (NumberFormatException e) {
            }

            if (i1 != null && i2 != null) {
                return i1.compareTo(i2);
            } else {
                return s1[0].compareTo(s2[0]);
            }
        }

    }

    public static String getAllSyncValue(Context mContext, ArrayList<String> alAssignColl) {
        String concatCollectionStr = "";
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);

        //Ignore this sections for hardcode  because T codes we have to check (Verify)
        String sharedVal = sharedPreferences.getString("isStartCloseEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/MC_ATTND")) {
            alAssignColl.add(Constants.Attendances);
        }

        sharedVal = sharedPreferences.getString("isRetailerListEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_CP_GETLST")) {
            alAssignColl.add(Constants.ChannelPartners);
            alAssignColl.add(Constants.CPDMSDivisions);
        }

        sharedVal = sharedPreferences.getString("isVisitCreate", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_VST")) {
            alAssignColl.add(Constants.Visits);
        }

        sharedVal = sharedPreferences.getString("isRouteEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_ROUTPLAN")) {
            alAssignColl.add(Constants.RoutePlans);
            alAssignColl.add(Constants.RouteSchedulePlans);
            alAssignColl.add(Constants.RouteSchedules);
        }

        sharedVal = sharedPreferences.getString("isCollHistory", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_COLLHIS")) {
            alAssignColl.add(Constants.FinancialPostingItemDetails);
            alAssignColl.add(Constants.FinancialPostings);
        }

        sharedVal = sharedPreferences.getString("isInvHistory", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_INVHIS")) {
            alAssignColl.add(Constants.SSInvoiceItemDetails);
            alAssignColl.add(Constants.SSINVOICES);
            alAssignColl.add(Constants.OutstandingInvoices);
            alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
        }

        sharedVal = sharedPreferences.getString("isMyStock", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_MYSTK")) {
            alAssignColl.add(Constants.SPStockItemSNos);
            alAssignColl.add(Constants.SPStockItems);
        }


        sharedVal = sharedPreferences.getString("iFocusedProductEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_FOCPROD")) {
            alAssignColl.add(Constants.SegmentedMaterials);
        }

        sharedVal = sharedPreferences.getString("isHelpLine", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_HELPLINE")) {
            alAssignColl.add(Constants.TEXT_CATEGORY_SET);
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
        }

        sharedVal = sharedPreferences.getString("isCompInfoEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_COMPINFO")) {
            alAssignColl.add(Constants.CompetitorMasters);
            alAssignColl.add(Constants.CompetitorInfos);
        }

        sharedVal = sharedPreferences.getString("isMyTargetsEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_MYTRGTS")) {
            alAssignColl.add(Constants.Targets);
            alAssignColl.add(Constants.KPISet);
            alAssignColl.add(Constants.TargetItems);
            alAssignColl.add(Constants.KPIItems);
        }

        sharedVal = sharedPreferences.getString("isTrends", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_TRENDS")) {
            alAssignColl.add(Constants.Performances);
        }


        sharedVal = sharedPreferences.getString("isBehaviourEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_SPCP_EVAL")) {
            alAssignColl.add(Constants.SPChannelEvaluationList);
        }
        sharedVal = sharedPreferences.getString(Constants.isComplintsListKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isComplintsListTcode)) {
            alAssignColl.add(Constants.Complaints);
        }

        //MerchReviews
        sharedVal = sharedPreferences.getString(Constants.isMerchReviewKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isMerchReviewTcode)) {
            alAssignColl.add(Constants.MerchReviews);
            alAssignColl.add(Constants.MerchReviewImages);
        }

        //SalesOrder
        sharedVal = sharedPreferences.getString(Constants.isSOCreateKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isSOCreateTcode)) {
            alAssignColl.add(Constants.SSSOs);
            alAssignColl.add(Constants.SSSoItemDetails);
        }

        //Alerts
        alAssignColl.add(Constants.Alerts);

        //Return Orders
        sharedVal = sharedPreferences.getString(Constants.isReturnOrderCreateEnabled, "");
        if (sharedVal.equalsIgnoreCase(Constants.isReturnOrderTcode)) {
            alAssignColl.add(Constants.SSROs);
            alAssignColl.add(Constants.SSROItemDetails);
        }

        //VisitActivity
        alAssignColl.add(Constants.VisitActivities);

        //Authorization
        alAssignColl.add(Constants.UserProfileAuthSet);

        // ValueHelps
        alAssignColl.add(Constants.ValueHelps);
        alAssignColl.add(Constants.Brands);
        alAssignColl.add(Constants.MaterialCategories);
        alAssignColl.add(Constants.BrandsCategories);
        alAssignColl.add(Constants.OrderMaterialGroups);
        alAssignColl.add(Constants.ConfigTypsetTypeValues);
        alAssignColl.add(Constants.ConfigTypesetTypes);
        alAssignColl.add(Constants.PricingConditions);
        alAssignColl.add(Constants.SSInvoiceTypes);

        //FOS
        alAssignColl.add(Constants.SalesPersons);
        alAssignColl.add(Constants.CPSPRelations);

        //Visual Aid
        sharedVal = sharedPreferences.getString(Constants.isVisualAidKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isVisualAidTcode)) {
            alAssignColl.add(Constants.Documents);
        }

        // Expenses
        sharedVal = sharedPreferences.getString(Constants.isExpenseEntryKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isExpenseEntryTcode)) {
            alAssignColl.add(Constants.ExpenseConfigs);
            alAssignColl.add(Constants.Expenses);
            alAssignColl.add(Constants.ExpenseItemDetails);
            alAssignColl.add(Constants.ExpenseAllowances);
            alAssignColl.add(Constants.ExpenseDocuments);

        }


        // Schemes
        sharedVal = sharedPreferences.getString(Constants.isSchemeKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isSchemeTcode)) {
            alAssignColl.add(Constants.Schemes);
            alAssignColl.add(Constants.SchemeItemDetails);
            alAssignColl.add(Constants.SchemeGeographies);
            alAssignColl.add(Constants.SchemeCPs);
            alAssignColl.add(Constants.SchemeSalesAreas);
            alAssignColl.add(Constants.SchemeCostObjects);
            alAssignColl.add(Constants.CPGeoClassifications);
            alAssignColl.add(Constants.SchemeCPDocuments);
            alAssignColl.add(Constants.SchemeSlabs);
            alAssignColl.add(Constants.Claims);
            alAssignColl.add(Constants.ClaimItemDetails);
            alAssignColl.add(Constants.ClaimDocuments);
            alAssignColl.add(Constants.SchemeFreeMatGrpMaterials);
        }

        sharedVal = sharedPreferences.getString(Constants.isDBStockKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isDBStockTcode)) {
            alAssignColl.add(Constants.CPStockItems);
            alAssignColl.add(Constants.CPStockItemSnos);
        }
        concatCollectionStr = Constants.getConcatinatinFlushCollectios(alAssignColl);

        return concatCollectionStr;

    }

    @Override
    protected void onDestroy() {
        stopService(SyncSelectionActivity.this);
        super.onDestroy();
    }

    public static void startService(Context context) {
        Constants.onlineStoreSyncLog = null;
        OnlineSynLogListener.instance = null;
        Constants.IsOnlineStoreSyncLogFailed = false;
        Constants.isFirstTime = false;
        Constants.TimeDifference1 = Calendar.getInstance();
        try {
            String userName = "", mobileNumber = "";
            ArrayList<String> salesPersonArrayList = OfflineManager.getSalespersonDetails(Constants.SalesPersons);
            if (salesPersonArrayList.size() > 0) {
                userName = salesPersonArrayList.get(0);
                mobileNumber = salesPersonArrayList.get(1);
            }
            LogonCoreContext logonCoreContext = LogonCore.getInstance().getLogonContext();
            String userID = logonCoreContext.getBackendUser();
            Constants.USER_ID = userID;
            Constants.USER_NAME = userName;
            Constants.USER_MOBILE_NUMBER = mobileNumber;
            context.startService(new Intent(context, AllSyncBackgroundService.class).putExtra(Constants.TRIGGER_TIME, Constants.SyncStartFreqency));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void stopService(Context context) {
        try {
            Intent intent = new Intent(context, AllSyncBackgroundService.class);
            boolean alarmUp = (PendingIntent.getService(context, Constants.PENDING_INTENT_SYNC_ID, intent, PendingIntent.FLAG_NO_CREATE) != null);
            if (alarmUp) {
                PendingIntent pendingIntent = PendingIntent.getService(context, Constants.PENDING_INTENT_SYNC_ID, intent, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                context.stopService(intent);
                alarmManager.cancel(pendingIntent);
                Log.d("AllSyncBackground", "stopService: alarm up stop service");
            } else {
                Log.d("AllSyncBackground", "stopService: alarm is not up");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            stopServiceTechincalCache(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startServiceMustSells(Context context) {
        try {
                if(Constants.onlineStoreMustCell!=null)
                    Constants.onlineStoreMustCell.close();
            } catch (ODataContractViolationException e) {
                e.printStackTrace();
            }
            Constants.onlineStoreMustCell=null;
            OnlineStoreCacheListner.instance = null;
            Constants.IsOnlineStoreMustSellFailed = false;
            Log.d("AllSyncTechincalCache", "startServiceTechincalCache: alarm up start service");
            try {
                context.startService(new Intent(context,MustSellBackGroundService.class).putExtra(Constants.TRIGGER_TIME,Constants.SyncStartFreqency));
            } catch (Throwable e) {
                e.printStackTrace();
        }
    }

    public static void stopServiceTechincalCache(Context context) {
        try {
            context.stopService(new Intent(context, MustSellBackGroundService.class));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        Log.d("AllSyncTechincalCache", "stopServiceTechincalCache: alarm up stop service");
    }

}
