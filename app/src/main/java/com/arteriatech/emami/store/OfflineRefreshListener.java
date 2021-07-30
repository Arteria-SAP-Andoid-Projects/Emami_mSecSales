package com.arteriatech.emami.store;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.sap.smp.client.odata.exception.ODataContractViolationException;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.offline.ODataOfflineException;
import com.sap.smp.client.odata.offline.ODataOfflineStoreRefreshListener;

/**
 * Created by e10526 on 14-03-2016.
 */
public class OfflineRefreshListener implements ODataOfflineStoreRefreshListener {
    private UIListener uiListener;
    private int operation;
    private String syncType = "", defineReq = "", autosync = "";

    private final int SUCCESS = 0;
    private final int ERROR = -1;
    private Context contxt;
    private boolean isRequestSuccess = false;
    private boolean isRequestFailed = false;
    Exception e;
    String errorMsg = "";

    private Handler uiHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == SUCCESS) {
                // Notify the Activity the is complete
                String key = (String) msg.obj;
                try {

                    uiListener.onRequestSuccess(operation, key);


                } catch (ODataException e) {
                    e.printStackTrace();
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            } else if (msg.what == ERROR) {
                e = (Exception) msg.obj;


                uiListener.onRequestError(operation, e);


            }
        }
    };

    public OfflineRefreshListener(Context contxt, String syncType, String defineReq, UIListener uiListener) {
        super();
        this.operation = Operation.OfflineRefresh.getValue();
        this.uiListener = uiListener;
        this.syncType = syncType;
        this.defineReq = defineReq;
        this.contxt = contxt;
    }

    public OfflineRefreshListener(Context contxt, String defineReq, UIListener uiListener) {
        super();
        this.operation = Operation.OfflineRefresh.getValue();
        this.uiListener = uiListener;
        this.defineReq = defineReq;
        this.contxt = contxt;
    }

    public OfflineRefreshListener(Context contxt, String defineReq, String autosync) {
        super();
        this.operation = Operation.OfflineRefresh.getValue();
        this.defineReq = defineReq;
        this.autosync = autosync;
        this.contxt = contxt;
    }

    /*****************
     * Utils Methods
     *****************/


    /**
     * Notify the OnlineUIListener that the request was successful.
     */
    protected void notifySuccessToListener(String key) {
        Message msg = uiHandler.obtainMessage();
        msg.what = SUCCESS;
        msg.obj = key;
        uiHandler.sendMessage(msg);
    }

    /**
     * Notify the OnlineUIListener that the request has an error.
     *
     * @param exception an Exception that denotes the error that occurred.
     */
    protected void notifyErrorToListener(Exception exception) {
        Message msg = uiHandler.obtainMessage();
        msg.what = ERROR;
        msg.obj = exception;
        uiHandler.sendMessage(msg);
        errorMsg = exception.getMessage();
        try {
            errorMsg = ((ODataOfflineException) ((ODataContractViolationException) exception).getCause()).getMessage();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }


    @Override
    public void offlineStoreRefreshStarted(com.sap.smp.client.odata.offline.ODataOfflineStore oDataOfflineStore) {

        TraceLog.scoped(this).d(Constants.OfflineStoreRefreshStarted);
    }

    @Override
    public void offlineStoreRefreshFinished(com.sap.smp.client.odata.offline.ODataOfflineStore oDataOfflineStore) {

        try {
            if (isRequestSuccess) {
                String successMsg = "";
                if (this.syncType.equalsIgnoreCase(Constants.ALL)) {
                    try {
                        if (!this.defineReq.equalsIgnoreCase("")) {
                            successMsg = this.contxt.getString(R.string.msg_success_sync, this.defineReq);
                        } else {
                            successMsg = this.contxt.getString(R.string.msg_success_sync, this.syncType);
                        }
                    } catch (Exception e1) {
                        successMsg = this.contxt.getString(R.string.msg_success_sync, this.syncType);
                    }
                } else {
                    successMsg = this.contxt.getString(R.string.msg_success_sync, this.defineReq);
                }

                LogManager.writeLogInfo(successMsg);
            } else if (isRequestFailed) {
                String errMsg = "";
                if (this.syncType.equalsIgnoreCase(Constants.ALL)) {
                    try {
                        if (!this.defineReq.equalsIgnoreCase("")) {
                            errMsg = this.contxt.getString(R.string.msg_error_sync, this.defineReq);
                        } else {
                            errMsg = this.contxt.getString(R.string.msg_error_sync, this.syncType);
                        }
                    } catch (Exception e1) {
                        errMsg = this.contxt.getString(R.string.msg_error_sync, this.syncType);
                    }
                } else {
                    errMsg = this.contxt.getString(R.string.msg_error_sync, this.defineReq);
                }


                LogManager.writeLogDebug(errMsg + " " + errorMsg);
                Constants.AL_ERROR_MSG.add(errMsg + " : " + errorMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void offlineStoreRefreshSucceeded(com.sap.smp.client.odata.offline.ODataOfflineStore oDataOfflineStore) {
        TraceLog.scoped(this).d(Constants.OfflineStoreRefreshSucceeded);
        isRequestSuccess = true;
        notifySuccessToListener(null);
    }

    @Override
    public void offlineStoreRefreshFailed(com.sap.smp.client.odata.offline.ODataOfflineStore oDataOfflineStore, ODataException e) {
        TraceLog.scoped(this).d(Constants.OfflineStoreRefreshFailed);
        isRequestFailed = true;

        notifyErrorToListener(e);
    }
}
