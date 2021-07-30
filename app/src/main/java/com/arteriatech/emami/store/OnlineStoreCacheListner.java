package com.arteriatech.emami.store;

import android.os.Build;
import android.system.ErrnoException;

import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.Constants;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.exception.ODataNetworkException;
import com.sap.smp.client.odata.online.OnlineODataStore;

/**
 * Created by e10526 on 22-03-2018.
 */

public class OnlineStoreCacheListner implements OnlineODataStore.OpenListener {
    public static OnlineStoreCacheListner instance;

    public static OnlineODataStore store;
    Exception error;

    private OnlineStoreCacheListner() {
    }

    public static OnlineStoreCacheListner getInstance() {
        if (instance == null) {
            instance = new OnlineStoreCacheListner();
        }
        return instance;
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
            if (Constants.ErrorNoTechincalCache == 0) {
                try {
                    if (((ODataNetworkException.ErrorCode) e.errorCode).name().equalsIgnoreCase(Constants.NetworkError_Name)) {
                        Constants.ErrorName = ((ODataNetworkException.ErrorCode) e.errorCode).name();


                        Throwable throwables = (((ODataNetworkException) e).getCause()).getCause().getCause();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (throwables instanceof ErrnoException) {
                                Constants.ErrorNoTechincalCache = ((ErrnoException) throwables).errno;
                            } else {

                                if (e.getMessage().contains(Constants.Unothorized_Error_Name) || e.getMessage().contains(Constants.Max_restart_reached)) {
                                    Constants.ErrorNoTechincalCache = 401;
                                } else if (e.getMessage().contains(Constants.Comm_error_name)) {
                                    Constants.ErrorNoTechincalCache = 101;
                                } else {
                                    Constants.ErrorNoTechincalCache = -1;
                                }
                            }
                        } else {

                            if (e.getMessage().contains(Constants.Unothorized_Error_Name) || e.getMessage().contains(Constants.Max_restart_reached)) {
                                Constants.ErrorNoTechincalCache = 401;
                            } else if (e.getMessage().contains(Constants.Comm_error_name)) {
                                Constants.ErrorNoTechincalCache = 101;
                            } else {
                                Constants.ErrorNoTechincalCache = Constants.Network_Error_Code;
                            }


                        }
                    } else {
                        Constants.ErrorName = ((ODataNetworkException.ErrorCode) e.errorCode).name();
                        Throwable throwables = (((ODataNetworkException) e).getCause()).getCause().getCause();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (throwables instanceof ErrnoException) {
                                Constants.ErrorNoTechincalCache = ((ErrnoException) throwables).errno;
                            } else {
                                if (e.getMessage().contains(Constants.Unothorized_Error_Name) || e.getMessage().contains(Constants.Max_restart_reached)) {
                                    Constants.ErrorNoTechincalCache = 401;
                                } else if (e.getMessage().contains(Constants.Comm_error_name)) {
                                    Constants.ErrorNoTechincalCache = 101;
                                } else {
                                    Constants.ErrorNoTechincalCache = -1;
                                }
                            }

                        } else {

                            if (e.getMessage().contains(Constants.Unothorized_Error_Name) || e.getMessage().contains(Constants.Max_restart_reached)) {
                                Constants.ErrorNoTechincalCache = 401;
                            } else if (e.getMessage().contains(Constants.Comm_error_name)) {
                                Constants.ErrorNoTechincalCache = 101;
                            } else {
                                Constants.ErrorNoTechincalCache = Constants.Comm_Error_Code;
                            }
                        }
                    }
                } catch (Exception e1) {


                    if (e.getMessage().contains(Constants.Unothorized_Error_Name) || e.getMessage().contains(Constants.Max_restart_reached)) {
                        Constants.ErrorNoTechincalCache = 401;
                    } else if (e.getMessage().contains(Constants.Comm_error_name)) {
                        Constants.ErrorNoTechincalCache = 101;
                    } else {
                        Constants.ErrorNoTechincalCache = -1;
                    }
                }


            }
        } catch (Exception e1) {
            LogManager.writeLogInfo("CatchBlock: storeOpenError is failed");
            Constants.ErrorNoTechincalCache = -1;
            LogManager.writeLogError(Constants.Error + " :[" + Constants.ErrorNoTechincalCache + "]" + e1.getMessage() != null ? e1.getMessage() : "");
            e1.printStackTrace();
        }
        Constants.IsOnlineStoreMustSellFailed = true;
    }

    @Override
    public void storeOpened(OnlineODataStore store) {
        this.store = store;
        Constants.IsOnlineStoreMustSellFailed = true;
        Constants.onlineStoreMustCell = store;       //latch.countDown();
        LogManager.writeLogInfo("MustSell : Online store opened successfully");
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


}
