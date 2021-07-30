package com.arteriatech.emami.store;

import android.os.Build;
import android.system.ErrnoException;

import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.Constants;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.exception.ODataNetworkException;
import com.sap.smp.client.odata.online.OnlineODataStore;
import com.sap.smp.client.odata.online.OnlineODataStore.OpenListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class OnlineStoreListener implements OpenListener {
    public static OnlineStoreListener instance;

    private final CountDownLatch latch = new CountDownLatch(1);
    OnlineODataStore store;
    Exception error;

    private OnlineStoreListener() {
    }

    public static OnlineStoreListener getInstance() {
        if (instance == null) {
            instance = new OnlineStoreListener();
        }
        return instance;
    }

    public synchronized void closeStore() {
        if (store != null) {
            try {
                store.close();
                // clear technical cache for offline store
// so there's nothing in the cache for the next user
// prevents confusion if you're using the requestCacheResponse method
                //store.resetCache();
                store = null;
                Constants.onlineStore = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void storeOpenError(ODataException e) {
        try {
            e.printStackTrace();
            try {
                Constants.printLog(Constants.Error + " :[" + Constants.ErrorNo + "]" + e.getMessage());
                Constants.printLog("DBG: Cause " + e.getCause().getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            try {
                Constants.printLog("DBG: getLocalizedMessage " + e.getCause().getLocalizedMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            this.error = e;
            if (Constants.ErrorNo == 0) {
                try {
                    if (((ODataNetworkException.ErrorCode) e.errorCode).name().equalsIgnoreCase(Constants.NetworkError_Name)) {
                        Constants.ErrorName = ((ODataNetworkException.ErrorCode) e.errorCode).name();


                        Throwable throwables = (((ODataNetworkException) e).getCause()).getCause().getCause();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (throwables instanceof ErrnoException) {
                                Constants.ErrorNo = ((ErrnoException) throwables).errno;
                            } else {

                                if (e.getMessage().contains(Constants.Unothorized_Error_Name) || e.getMessage().contains(Constants.Max_restart_reached)) {
                                    Constants.ErrorNo = 401;
                                } else if (e.getMessage().contains(Constants.Comm_error_name)) {
                                    Constants.ErrorNo = 101;
                                } else {
                                    Constants.ErrorNo = -1;
                                }
                            }
                        } else {

                            if (e.getMessage().contains(Constants.Unothorized_Error_Name) || e.getMessage().contains(Constants.Max_restart_reached)) {
                                Constants.ErrorNo = 401;
                            } else if (e.getMessage().contains(Constants.Comm_error_name)) {
                                Constants.ErrorNo = 101;
                            } else {
                                Constants.ErrorNo = Constants.Network_Error_Code;
                            }


                        }
                    } else {
                        Constants.ErrorName = ((ODataNetworkException.ErrorCode) e.errorCode).name();
                        Throwable throwables = (((ODataNetworkException) e).getCause()).getCause().getCause();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (throwables instanceof ErrnoException) {
                                Constants.ErrorNo = ((ErrnoException) throwables).errno;
                            } else {
                                if (e.getMessage().contains(Constants.Unothorized_Error_Name) || e.getMessage().contains(Constants.Max_restart_reached)) {
                                    Constants.ErrorNo = 401;
                                } else if (e.getMessage().contains(Constants.Comm_error_name)) {
                                    Constants.ErrorNo = 101;
                                } else {
                                    Constants.ErrorNo = -1;
                                }
                            }

                        } else {

                            if (e.getMessage().contains(Constants.Unothorized_Error_Name) || e.getMessage().contains(Constants.Max_restart_reached)) {
                                Constants.ErrorNo = 401;
                            } else if (e.getMessage().contains(Constants.Comm_error_name)) {
                                Constants.ErrorNo = 101;
                            } else {
                                Constants.ErrorNo = Constants.Comm_Error_Code;
                            }
                        }
                    }
                } catch (Exception e1) {


                    if (e.getMessage().contains(Constants.Unothorized_Error_Name) || e.getMessage().contains(Constants.Max_restart_reached)) {
                        Constants.ErrorNo = 401;
                    } else if (e.getMessage().contains(Constants.Comm_error_name)) {
                        Constants.ErrorNo = 101;
                    } else {
                        Constants.ErrorNo = -1;
                    }
                }


                latch.countDown();
            }
        } catch (Exception e1) {
            LogManager.writeLogInfo("CatchBlock: storeOpenError is failed");
            Constants.ErrorNo = -1;
            LogManager.writeLogError(Constants.Error + " :[" + Constants.ErrorNo + "]" + e1.getMessage() != null ? e1.getMessage() : "");
            e1.printStackTrace();
        }
        Constants.IsOnlineStoreFailed = true;
    }

    @Override
    public void storeOpened(OnlineODataStore store) {
        this.store = store;
        latch.countDown();
        Constants.IsOnlineStoreFailed = true;
        Constants.onlineStore = store;       //latch.countDown();
        LogManager.writeLogInfo("Online store opened successfully");
    }

    public synchronized boolean finished() {
        return (store != null || error != null);
    }

    public synchronized Exception getError() {
        return error;
    }

    public synchronized OnlineODataStore getStore() {
        return store;
    }

    /**
     * Waits for the completion of the asynchronous process. In case this listener is not invoked within 30 seconds then it fails with an exception.
     */
    public void waitForCompletion() {
        try {
            if (latch.await(60, TimeUnit.SECONDS)) {

            }
            //throw new IllegalStateException("Open listener was not called within 30 seconds.");
            else if (!finished()) {

            }
            //throw new IllegalStateException("Open listener is not in finished state after having completed successfully");
        } catch (InterruptedException e) {
            throw new IllegalStateException("Open listener waiting for results was interrupted.", e);
        }
    }

}
