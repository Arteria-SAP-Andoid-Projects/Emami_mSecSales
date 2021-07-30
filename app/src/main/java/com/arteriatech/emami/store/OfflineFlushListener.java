package com.arteriatech.emami.store;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.emami.common.Constants;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.offline.ODataOfflineStoreFlushListener;

/**
 * Created by e10526 on 14-03-2016.
 */
public class OfflineFlushListener implements ODataOfflineStoreFlushListener {
    private UIListener uiListener;
    private String autoSync, collName;
    private int operation;

    private final int SUCCESS = 0;
    private final int ERROR = -1;

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
                Exception e = (Exception) msg.obj;

                uiListener.onRequestError(operation, e);
            }
        }
    };

    public OfflineFlushListener(UIListener uiListener, String collName) {
        super();
        this.operation = Operation.OfflineFlush.getValue();
        this.uiListener = uiListener;
        this.collName = collName;
    }

    public OfflineFlushListener(UIListener uiListener) {
        super();
        this.operation = Operation.OfflineFlush.getValue();
        this.uiListener = uiListener;
    }

    public OfflineFlushListener(String autoSync) {
        super();
        this.operation = Operation.OfflineFlush.getValue();
        this.autoSync = autoSync;
    }


    /*****************
     * Utils Methods
     *****************/


    /**
     * Notify the OfflineUIListener that the request was successful.
     */
    protected void notifySuccessToListener(String key) {
        Message msg = uiHandler.obtainMessage();
        msg.what = SUCCESS;
        msg.obj = key;
        uiHandler.sendMessage(msg);
        if (this.collName != null) {
            LogManager.writeLogInfo(this.collName + " " + Constants.PostedSuccessfully);
        } else {
            LogManager.writeLogInfo(Constants.SynchronizationCompletedSuccessfully);
        }


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
        TraceLog.e(Constants.FlushListenerNotifyError, exception);

    }

    /*****************
     * Methods that implements ODataOfflineStoreFlushListener interface
     *****************/

    @Override
    public void offlineStoreFlushStarted(com.sap.smp.client.odata.offline.ODataOfflineStore oDataOfflineStore) {
        TraceLog.scoped(this).d(Constants.OfflineStoreFlushStarted);

    }

    @Override
    public void offlineStoreFlushFinished(com.sap.smp.client.odata.offline.ODataOfflineStore oDataOfflineStore) {
        TraceLog.scoped(this).d(Constants.OfflineStoreFlushFinished);


    }

    @Override
    public void offlineStoreFlushSucceeded(com.sap.smp.client.odata.offline.ODataOfflineStore oDataOfflineStore) {
        TraceLog.scoped(this).d(Constants.OfflineStoreFlushSucceeded);
        notifySuccessToListener(null);
    }

    @Override
    public void offlineStoreFlushFailed(com.sap.smp.client.odata.offline.ODataOfflineStore oDataOfflineStore, ODataException e) {
        TraceLog.scoped(this).d(Constants.OfflineStoreFlushFailed);

        try {
            OfflineManager.getErrorArchive();
        } catch (OfflineODataStoreException e2) {
            e2.printStackTrace();
        }

        notifyErrorToListener(e);
    }

}
