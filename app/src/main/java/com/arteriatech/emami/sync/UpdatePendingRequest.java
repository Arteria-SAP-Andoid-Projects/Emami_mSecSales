package com.arteriatech.emami.sync;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.asyncTask.AllSyncAsyncTask;
import com.arteriatech.emami.asyncTask.FlushDataAsyncTask;
import com.arteriatech.emami.asyncTask.PostDataFromDataValt;
import com.arteriatech.emami.asyncTask.SyncMustSellAsyncTask;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.MSFAApplication;
import com.arteriatech.emami.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.emami.main.MainMenu;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.store.OnlineManager;
import com.arteriatech.emami.store.OnlineODataStoreException;
import com.arteriatech.emami.store.OnlineStoreListener;
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
import com.sap.smp.client.odata.exception.ODataException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by e10769 on 22-04-2017.
 */

public class UpdatePendingRequest extends TimerTask implements UIListener, INetListener {
    public static String TAG = "UpdatePendingRequest";
    public static UpdatePendingRequest instance = null;
    private Context mContext = MainMenu.context;
    private String endPointURL = "";
    private String appConnID = "";
    private int penReqCount = 0;
    private int mIntPendingCollVal = 0;
    private String[][] invKeyValues = null;
    private ArrayList<String> alAssignColl = new ArrayList<>();
    private ArrayList<String> alFlushColl = new ArrayList<>();
    private Handler mHandler = new Handler();
    private Handler mHandlerDifferentTrd = new Handler();
    private int mError = 0;
    public static boolean isDataAvailable = false;
    private boolean tokenFlag = false, onlineStoreOpen = false;

    static MessageWithBooleanCallBack dialogCallBack = null;
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };

    public static UpdatePendingRequest getInstance(MessageWithBooleanCallBack dialogCalls) {
        if (null == instance) {
            instance = new UpdatePendingRequest();
        }
        dialogCallBack = dialogCalls;
        return instance;
    }

    @Override
    public void run() {
        mHandlerDifferentTrd.post(new Runnable() {
            public void run() {
                try {
                    Log.d(TAG, "auto sync run: started");
                    Constants.mErrorCount = 0;
                    if (!Constants.isDayStartSyncEnbled)
                        LogManager.writeLogInfo(mContext.getString(R.string.auto_sync_trigger));
                    if (!Constants.isSync) {
                        Constants.isSync = false;
                        Constants.iSAutoSync = false;
                        if (UtilConstants.isNetworkAvailable(mContext)) {
                            if (!Constants.isDayStartSyncEnbled)
                                LogManager.writeLogInfo(mContext.getString(R.string.auto_sync_started));
                            Constants.mApplication = (MSFAApplication) mContext.getApplicationContext();
                            onUpdateSync(mContext, UpdatePendingRequest.this, UpdatePendingRequest.this);
                        } else {
                            LogManager.writeLogInfo(mContext.getString(R.string.auto_sync_not_perfrom_due_to_no_network));
                            Constants.iSAutoSync = false;
                            Constants.mErrorCount++;
                            setCallBackToUI(true, mContext.getString(R.string.no_network_conn), null);
                        }
                    } else {
                        Log.d(TAG, "run: stoped started");
                        if (!Constants.isDayStartSyncEnbled)
                            LogManager.writeLogInfo(mContext.getString(R.string.sync_prog_auto_sync_not_perfrom));
                        Constants.mErrorCount++;
                        setCallBackToUI(true, mContext.getString(R.string.alert_auto_sync_is_progress), null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogManager.writeLogInfo("Autto Sync error" + e.getMessage());
                    Constants.mErrorCount++;
                    setCallBackToUI(true, e.getMessage(), null);
                }
            }
        });



        /*    }
        }).start();
*/
    }

    private void onUpdateSync(final Context mContext, final UIListener uiListener, final INetListener iNetListener) {
        try {
            penReqCount = 0;
            mIntPendingCollVal = 0;
            ArrayList<Object> objectArrayLists = SyncSelectionActivity.getPendingInvList(mContext);
            if (!objectArrayLists.isEmpty()) {
                mIntPendingCollVal = (int) objectArrayLists.get(0);
                invKeyValues = (String[][]) objectArrayLists.get(1);
            }

            if (mIntPendingCollVal > 0) {

            } else {
                mIntPendingCollVal = 0;
                invKeyValues = null;
                ArrayList<Object> objectArrayList = SyncSelectionActivity.getPendingCollList(mContext);
                if (!objectArrayList.isEmpty()) {
                    mIntPendingCollVal = (int) objectArrayList.get(0);
                    invKeyValues = (String[][]) objectArrayList.get(1);
                }

            }
            penReqCount = 0;
//            LogManager.writeLogError("onUpdateSync(AllSync) : Check Store Failed Or not");
            if (!OfflineManager.isOfflineStoreOpen()) {
//                LogManager.writeLogError("onUpdateSync(AllSync) : Check Store Failed");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OfflineManager.openOfflineStore(mContext, new UIListener() {
                                @Override
                                public void onRequestError(int operation, Exception exception) {
                                    ErrorBean errorBean = Constants.getErrorCode(operation, exception, mContext);

                                    Constants.iSAutoSync = false;
                                    Constants.mErrorCount++;
                                    if (errorBean.getErrorCode() == Constants.Resource_not_found) {
                                        Constants.ReIntilizeStore = true;
                                    }
                                    setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false), null);
                                }


                                @Override
                                public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                    if (OfflineManager.isOfflineStoreOpen()) {
                                        try {
                                            OfflineManager.getAuthorizations(mContext);
                                        } catch (OfflineODataStoreException e) {
                                            e.printStackTrace();
                                        }
                                        Constants.mErrorCount = 0;
                                        setCallBackToUI(true, "", null);
                                    }
                                }
                            });
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                }).start();

            } else {
//                LogManager.writeLogError("onUpdateSync(AllSync) : Check Store Not Failed");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        postData(uiListener, iNetListener);
                    }
                }).start();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postData(UIListener uiListener, INetListener iNetListener) {
        try {
            if (OfflineManager.offlineStore.getRequestQueueIsEmpty() && mIntPendingCollVal == 0) {
                LogManager.writeLogInfo(mContext.getString(R.string.no_req_to_update_sap));
//                LogManager.writeLogInfo("postData(AllSync) : Before All sync");
                if (UtilConstants.isNetworkAvailable(mContext)) {
                    alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                    onAllSync(mContext);
                } else {
                    Constants.iSAutoSync = false;
                    Constants.mErrorCount++;
                    setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                }
            } else {
                alAssignColl.clear();
                alFlushColl.clear();
                ArrayList<String> allAssignColl = SyncSelectionActivity.getRefreshList();
                if (!allAssignColl.isEmpty()) {
                    alAssignColl.addAll(allAssignColl);
                    alFlushColl.addAll(allAssignColl);
                }
                if (mIntPendingCollVal > 0) {
                    if (UtilConstants.isNetworkAvailable(mContext)) {
                        onlineStoreOpen = false;
                        Constants.mBoolIsReqResAval = true;
                        Constants.mBoolIsNetWorkNotAval = false;
                        Constants.onlineStore = null;

                        tokenFlag = false;
                        Constants.x_csrf_token = "";
                        Constants.ErrorCode = 0;
                        Constants.ErrorNo = 0;
                        Constants.ErrorName = "";
                        Constants.ErrorNo_Get_Token = 0;
                        Constants.IsOnlineStoreFailed = false;
                        OnlineStoreListener.instance = null;
                        try {
                            onlineStoreOpen = OnlineManager.openOnlineStore(mContext);
                        } catch (OnlineODataStoreException e) {
                            e.printStackTrace();
                        }
                        if (onlineStoreOpen) {
                            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
                            if (sharedPreferences.getString(Constants.isInvoiceCreateKey, "").equalsIgnoreCase(Constants.isInvoiceTcode)) {
                                onLoadToken(mContext);
                                if (tokenFlag) {
                                    if (Constants.x_csrf_token != null && !Constants.x_csrf_token.equalsIgnoreCase("")) {
                                        try {
                                            new PostDataFromDataValt(mContext, uiListener, invKeyValues, iNetListener).execute();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Constants.iSAutoSync = false;
                                        Constants.mErrorCount++;
                                        setCallBackToUI(true, Constants.makeMsgReqError(-2, mContext, true), null);
                                    }
                                } else {
                                    Constants.iSAutoSync = false;
                                    Constants.mErrorCount++;
                                    setCallBackToUI(true, Constants.makeMsgReqError(Constants.ErrorNo_Get_Token, mContext, true), null);
                                }
                            } else {
                                try {
                                    new PostDataFromDataValt(mContext, uiListener, invKeyValues, iNetListener).execute();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Constants.iSAutoSync = false;
                            Constants.mErrorCount++;
                            setCallBackToUI(true, Constants.makeMsgReqError(Constants.ErrorNo, mContext, false), null);
                        }


                    } else {
                        Constants.iSAutoSync = false;
                        Constants.mErrorCount++;
                        setCallBackToUI(true, mContext.getString(R.string.no_network_conn), null);
                        LogManager.writeLogInfo(mContext.getString(R.string.no_network_conn));
                    }
                } else if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                    if (UtilConstants.isNetworkAvailable(mContext)) {
                        try {
                            new FlushDataAsyncTask(this, alFlushColl).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Constants.iSAutoSync = false;
                        Constants.mErrorCount++;
                        setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                        LogManager.writeLogInfo(mContext.getString(R.string.data_conn_lost_during_sync));
                    }
                } else {
                    if (!UtilConstants.isNetworkAvailable(mContext)) {
                        Constants.iSAutoSync = false;
                        Constants.mErrorCount++;
                        setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                        LogManager.writeLogInfo(mContext.getString(R.string.data_conn_lost_during_sync));
                    } else {
                        onAllSync(mContext);
                    }
                }
            }
        } catch (ODataException e) {
            e.printStackTrace();
            Constants.mErrorCount++;
            setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
        }
    }


    public void callSchedule(String duration) {
        try {
            UpdatePendingRequest as = new UpdatePendingRequest();
            TimerTask sync = as;
            Calendar date = Calendar.getInstance();
            date.set(Calendar.MINUTE, date.get(Calendar.MINUTE) + Integer.parseInt(duration));
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(sync, date.getTime(), 1000 * 60 * Integer.parseInt(duration));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callScheduleFirstLoginSync() throws Exception {
        UpdatePendingRequest as = new UpdatePendingRequest();
        System.out.println("callScheduleFirstLoginSync------------>" + UtilConstants.getSyncHistoryddmmyyyyTime());
        TimerTask syncTime = as;
        Timer timer = new Timer();
        timer.schedule(syncTime, 100);
    }

    @Override
    public void onRequestError(int operation, Exception exception) {
        LogManager.writeLogError("onRequestError(AllSync) : " + exception.getMessage());
        ErrorBean errorBean = Constants.getErrorCode(operation, exception, mContext);
        try {
            if (errorBean.hasNoError()) {
                mError++;
                penReqCount++;
                Constants.mBoolIsReqResAval = true;
                Constants.mErrorCount++;

                if ((operation == Operation.Create.getValue()) && (penReqCount == mIntPendingCollVal)) {
                    try {
                        if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                            if (UtilConstants.isNetworkAvailable(mContext)) {
                                try {
                                    new FlushDataAsyncTask(this, alFlushColl).execute();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            } else {
                                Constants.iSAutoSync = false;
                                Constants.mErrorCount++;
                                setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                            }
                        } else {
                            if (UtilConstants.isNetworkAvailable(mContext)) {
                                alAssignColl.clear();
                                alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                                onAllSync(mContext);
                            } else {
                                Constants.iSAutoSync = false;
                                Constants.mErrorCount++;
                                setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                            }
                        }
                    } catch (ODataException e3) {
                        e3.printStackTrace();
                    }
                }

                if (operation == Operation.OfflineFlush.getValue()) {
                    if (UtilConstants.isNetworkAvailable(mContext)) {
                        alAssignColl.clear();
                        alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                        onAllSync(mContext);
                    } else {
                        Constants.iSAutoSync = false;
                        Constants.mErrorCount++;
                        setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                    }
                } else if (operation == Operation.OfflineRefresh.getValue()) {
                    Constants.iSAutoSync = false;
                    Constants.mErrorCount++;
                    final String mErrorMsg = "";

                    try {
                        new SyncMustSellAsyncTask(mContext, new MessageWithBooleanCallBack() {
                            @Override
                            public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
                                Log.d("clickedStatus Req", clickedStatus + "");
                                setErrorUI(mErrorMsg, errorBean);
                            }
                        }, Constants.All).execute();
                    } catch (Exception e) {
                        setErrorUI(mErrorMsg, errorBean);
                        e.printStackTrace();
                    }


                }
            } else {
                Constants.mBoolIsNetWorkNotAval = true;
                Constants.mBoolIsReqResAval = true;
                if (Constants.iSAutoSync) {
                    Constants.iSAutoSync = false;
                }
                Constants.mErrorCount++;
//                LogManager.writeLogError("onRequestError(AllSync) : : Check Store Failed Or not");
                if (errorBean.isStoreFailed()) {
//                    LogManager.writeLogError("onRequestError(AllSync) : : Check Store Failed");
                    OfflineManager.offlineStore = null;
                    OfflineManager.options = null;
                    openStore(errorBean);
                } else {
//                    LogManager.writeLogError("onRequestError(AllSync) : : Check Store Not Failed");
                    setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false), errorBean);
                }

            }
        } catch (Exception e) {
            Constants.mBoolIsNetWorkNotAval = true;
            Constants.mBoolIsReqResAval = true;
            if (Constants.iSAutoSync) {
                Constants.iSAutoSync = false;
            }
            Constants.mErrorCount++;
            setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false), null);
        }
    }

    private void setErrorUI(String mErrorMsg, ErrorBean errorBean) {
        if (Constants.AL_ERROR_MSG.size() > 0) {
            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
        }
        if (mErrorMsg.equalsIgnoreCase("")) {
            setCallBackToUI(true, errorBean.getErrorMsg(), null);
        } else {
            setCallBackToUI(true, mErrorMsg, null);
        }
    }

    private void openStore(final ErrorBean errorBean) {
        if (!OfflineManager.isOfflineStoreOpen()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OfflineManager.openOfflineStore(mContext, new UIListener() {
                            @Override
                            public void onRequestError(int operation, Exception exception) {
                                ErrorBean errorBean = Constants.getErrorCode(operation, exception, mContext);

                                Constants.iSAutoSync = false;
                                Constants.mErrorCount++;
                                if (errorBean.getErrorCode() == Constants.Resource_not_found) {
                                    Constants.ReIntilizeStore = true;
                                }
                                setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false), null);
                            }


                            @Override
                            public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                if (OfflineManager.isOfflineStoreOpen()) {
                                    try {
                                        OfflineManager.getAuthorizations(mContext);
                                    } catch (OfflineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                    Constants.mErrorCount = 0;
                                    setCallBackToUI(true, "", null);
                                }
                            }
                        });
                    } catch (OfflineODataStoreException e) {
                        setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false), errorBean);
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                }
            }).start();

        } else {
            setCallBackToUI(true, Constants.makeMsgReqError(errorBean.getErrorCode(), mContext, false), errorBean);
        }
    }


    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        Log.d(TAG, "onRequestSuccess: ");
        if (operation == Operation.Create.getValue() && mIntPendingCollVal > 0) {
            Constants.mBoolIsReqResAval = true;
            if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CollList)) {
                Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.CollList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOList)) {
                Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.SOList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.FeedbackList)) {
                Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.FeedbackList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.InvList)) {
                Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.InvList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.ROList)) {
                Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.ROList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SampleDisbursement)) {
                Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.SampleDisbursement, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.Expenses)) {
                Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.Expenses, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CPList)) {
                Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.CPList, invKeyValues[penReqCount][0]);
            } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SSInvoices)) {
                Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.SSInvoices, invKeyValues[penReqCount][0]);
            }

            UtilDataVault.storeInDataVault(invKeyValues[penReqCount][0], "");
            penReqCount++;
        }
        if ((operation == Operation.Create.getValue()) && (penReqCount == mIntPendingCollVal)) {
            try {
                if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                    if (UtilConstants.isNetworkAvailable(mContext)) {
                        try {
                            new FlushDataAsyncTask(this, alFlushColl).execute();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    } else {
                        Constants.iSAutoSync = false;
                        setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                    }
                } else {
                    if (UtilConstants.isNetworkAvailable(mContext)) {
                        alAssignColl.clear();
                        alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                        onAllSync(mContext);
                    } else {
                        Constants.iSAutoSync = false;
                        setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                    }
                }

            } catch (ODataException e) {
                e.printStackTrace();
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {
            if (UtilConstants.isNetworkAvailable(mContext)) {
                alAssignColl.clear();
                alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                onAllSync(mContext);
            } else {
                Constants.iSAutoSync = false;
                setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {

            try {
                OfflineManager.getAuthorizations(mContext);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setBirthdayListToDataValut(mContext);
            Constants.updateLastSyncTimeToTable(alAssignColl);
            Constants.deleteDeviceMerchansisingFromDataVault(mContext);
            Constants.setAppointmentNotification(mContext);
            if (alAssignColl.contains(Constants.RoutePlans) || alAssignColl.contains(Constants.ChannelPartners) || alAssignColl.contains(Constants.Visits)) {
                Constants.alTodayBeatRet.clear();
                Constants.TodayTargetRetailersCount = Constants.getVisitTargetForToday();
                Constants.TodayActualVisitRetailersCount = Constants.getVisitedRetailerCount(Constants.alTodayBeatRet);
            }
            if (alAssignColl.contains(Constants.SSSOs) || alAssignColl.contains(Constants.Targets)) {
                Constants.loadingTodayAchived(mContext, Constants.alTodayBeatRet);
            }

            try {
                new SyncMustSellAsyncTask(mContext, new MessageWithBooleanCallBack() {
                    @Override
                    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
                        Log.d("clickedStatus Req", clickedStatus + "");
                        setUI();
                    }
                }, Constants.All).execute();
            } catch (Exception e) {
                setUI();
                e.printStackTrace();
            }
                /*Constants.iSAutoSync = false;

                String mErrorMsg = "";
                if (Constants.AL_ERROR_MSG.size() > 0) {
                    mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
                }

                if (mErrorMsg.equalsIgnoreCase("")) {
                    setCallBackToUI(true, mContext.getString(R.string.error_occured_during_post),null);
                } else {
                    setCallBackToUI(true, mErrorMsg,null);
                }*/

        }
    }

    private void setUI() {
        Constants.iSAutoSync = false;

        String mErrorMsg = "";
        if (Constants.AL_ERROR_MSG.size() > 0) {
            mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
        }

        if (mErrorMsg.equalsIgnoreCase("")) {
            setCallBackToUI(true, mContext.getString(R.string.error_occured_during_post), null);
        } else {
            setCallBackToUI(true, mErrorMsg, null);
        }
    }

    @Override
    public void onSuccess(IRequest iRequest, IResponse iResponse) {
        Log.d(TAG, "onSuccess: ");
        try {
            Constants.mBoolIsReqResAval = true;
            if (mIntPendingCollVal > 0) {

                if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.CollList)) {
                    Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.CollList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SOList)) {
                    Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.SOList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.FeedbackList)) {
                    Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.FeedbackList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.InvList)) {
                    Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.InvList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.ROList)) {
                    Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.ROList, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SampleDisbursement)) {
                    Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.SampleDisbursement, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.Expenses)) {
                    Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.Expenses, invKeyValues[penReqCount][0]);
                } else if (invKeyValues[penReqCount][1].equalsIgnoreCase(Constants.SSInvoices)) {
                    Constants.removeDeviceDocNoFromSharedPref(mContext, Constants.SSInvoices, invKeyValues[penReqCount][0]);
                }

                UtilDataVault.storeInDataVault(invKeyValues[penReqCount][0], "");

                penReqCount++;
            }

            //ignore this sections for hard codes (parsing xml request for invoice creation)
            String repData = EntityUtils.toString(iResponse.getEntity());

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

    @Override
    public void onError(IRequest iRequest, IResponse iResponse, IRequestStateElement iReqStateElement) {
        final int errorcode = iReqStateElement.getErrorCode();
        if (IRequestStateElement.AUTHENTICATION_ERROR != errorcode && IRequestStateElement.NETWORK_ERROR != errorcode) {
            mError++;
            penReqCount++;
            Constants.mBoolIsReqResAval = true;

            Constants.mErrorCount++;
            try {
                Constants.parser = Constants.mApplication.getParser();

                IODataError errResponse = null;
                HttpResponse response = iResponse;
                if (iResponse != null) {
                    try {
                        HttpEntity responseEntity = iResponse.getEntity();
                        String responseString = EntityUtils.toString(responseEntity);
                        responseString = responseString.replace(mContext.getString(R.string.Bad_Request), "");

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

                LogManager.writeLogError(mContext.getString(R.string.Error_in_Retailer_invoice) + errorMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (penReqCount == mIntPendingCollVal) {
                mHandler.post(mUpdateResults);
            }
        } else {
            if (iReqStateElement.getErrorCode() == 4) {
                LogManager.writeLogError(Constants.Error + " :" + mContext.getString(R.string.auth_fail_plz_contact_admin, iReqStateElement.getErrorCode() + ""));
            } else if (iReqStateElement.getErrorCode() == 3) {
                LogManager.writeLogError(Constants.Error + " :" + mContext.getString(R.string.data_conn_lost_during_sync_error_code, iReqStateElement.getErrorCode() + ""));
            } else {
                LogManager.writeLogError(Constants.Error + " :" + mContext.getString(R.string.data_conn_lost_during_sync_error_code, iReqStateElement.getErrorCode() + ""));
            }
            Constants.mBoolIsNetWorkNotAval = true;
            Constants.mBoolIsReqResAval = true;
            if (Constants.iSAutoSync) {
                Constants.iSAutoSync = false;
            }
            setCallBackToUI(true, Constants.makeMsgReqError(errorcode, mContext, true), null);

        }


    }

    protected void updateResultsInUi() {

        if (mError == 0) {

            if (!UtilConstants.isNetworkAvailable(mContext)) {

                LogManager.writeLogInfo(mContext.getString(R.string.data_conn_lost_during_sync));
                Constants.iSAutoSync = false;
                setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);

            } else {

                mIntPendingCollVal = 0;
                invKeyValues = null;
                ArrayList<Object> objectArrayList = SyncSelectionActivity.getPendingCollList(mContext);
                if (!objectArrayList.isEmpty()) {
                    mIntPendingCollVal = (int) objectArrayList.get(0);
                    invKeyValues = (String[][]) objectArrayList.get(1);
                }
                penReqCount = 0;
                Constants.mBoolIsReqResAval = true;
                Constants.mBoolIsNetWorkNotAval = false;
                if (mIntPendingCollVal > 0) {
                    if (UtilConstants.isNetworkAvailable(mContext)) {
                        try {
                            new PostDataFromDataValt(mContext, this, invKeyValues, this).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (Constants.iSAutoSync) {
                            Constants.iSAutoSync = false;
                        }
                        setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                    }
                } else {

                    try {
                        if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                            if (UtilConstants.isNetworkAvailable(mContext)) {
                                try {
                                    new FlushDataAsyncTask(this, alFlushColl).execute();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (Constants.iSAutoSync) {
                                    Constants.iSAutoSync = false;
                                }
                                setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                            }
                        } else {
                            if (UtilConstants.isNetworkAvailable(mContext)) {
                                alAssignColl.clear();
                                alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                                onAllSync(mContext);
                            } else {
                                if (Constants.iSAutoSync) {
                                    Constants.iSAutoSync = false;
                                }
                                setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
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
                    if (UtilConstants.isNetworkAvailable(mContext)) {
                        try {
                            new FlushDataAsyncTask(this, alFlushColl).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (Constants.iSAutoSync) {
                            Constants.iSAutoSync = false;
                        }
                        setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                    }
                } else {
                    if (UtilConstants.isNetworkAvailable(mContext)) {
                        alAssignColl.clear();
                        alAssignColl.addAll(Constants.getDefinigReqList(mContext));
                        onAllSync(mContext);
                    } else {
                        if (Constants.iSAutoSync) {
                            Constants.iSAutoSync = false;
                        }
                        setCallBackToUI(true, mContext.getString(R.string.data_conn_lost_during_sync), null);
                    }
                }

            } catch (ODataException e) {
                e.printStackTrace();
            }

        }
    }

    private void onAllSync(Context mContext) {
        new AllSyncAsyncTask(mContext, this, new ArrayList<String>()).execute();
    }

    private void setCallBackToUI(final boolean status, final String error_Msg, final ErrorBean errorBean) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialogCallBack != null) {
                    dialogCallBack.clickedStatus(status, error_Msg, errorBean);
                } else {
                    if (!Constants.isDayStartSyncEnbled)
                        LogManager.writeLogInfo(mContext.getString(R.string.auto_sync_end));
                }
            }
        });

    }

    public void onLoadToken(final Context context) {
        String endPointURL = "";
        String appConnID = "";
        try {
            // get Application Connection ID
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
            endPointURL = lgCtx.getAppEndPointUrl();
            appConnID = LogonCore.getInstance().getLogonContext()
                    .getConnId();
        } catch (LogonCoreException e) {
            e.printStackTrace();
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
                    Constants.x_csrf_token = iResponse.getHeaders("X-CSRF-Token")[0].getValue();
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
                    LogManager.writeLogError(Constants.Error + " :" + context.getString(R.string.auth_fail_plz_contact_admin, iReqStateElement.getErrorCode() + ""));
                } else if (iReqStateElement.getErrorCode() == 3) {
                    LogManager.writeLogError(Constants.Error + " :" + context.getString(R.string.data_conn_lost_during_sync_error_code, iReqStateElement.getErrorCode() + ""));
                } else {
                    LogManager.writeLogError(Constants.Error + " :" + context.getString(R.string.data_conn_lost_during_sync_error_code, iReqStateElement.getErrorCode() + ""));
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
}
