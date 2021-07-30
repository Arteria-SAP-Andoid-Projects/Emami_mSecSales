package com.arteriatech.emami.sync;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.store.OnlineManager;
import com.arteriatech.emami.store.OnlineStoreCacheListner;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.Hashtable;
import java.util.List;

/**
 * Created by e10526 on 22-03-2018.
 */

public class MustSellBackGroundService extends Service implements UIListener {
    private final IBinder mBinder = new MustSellBackGroundService.LocalBinder();
    String time = "time";
    private String TAG = MustSellBackGroundService.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
        //Log.e(TAG, "Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            new MustSellBackGroundService.SyncLogAsyncTask(null).execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        //Log.v(TAG, "Service killed");
        super.onDestroy();
    }

    /**
     * Mandatory method implement for service class.
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRequestError(int i, Exception e) {
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
    }

    public class LocalBinder extends Binder {
        MustSellBackGroundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MustSellBackGroundService.this;
        }

    }

    /**
     * This method will call when ever we force close an application. So that this can
     * cancel the service(Alarm)
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    /**
     * Sync log AsyncTask will perform the task to sending logs to server.
     */
    public class SyncLogAsyncTask extends AsyncTask<Void, Void, Void> {

        Hashtable<String, String> hashtable;

        public SyncLogAsyncTask(Hashtable<String, String> hashtable) {
            this.hashtable = hashtable;
        }

        @Override
        protected Void doInBackground(Void... params) {
            sendSyncLogToServer(hashtable);
            return null;
        }
    }

    private void sendSyncLogToServer(Hashtable<String, String> hashtable) {
        try {
            boolean onlineStoreOpen = false;

            Constants.onlineStoreMustCell = null;
            OnlineStoreCacheListner.instance = null;
            Constants.IsOnlineStoreMustSellFailed = false;
            onlineStoreOpen = OnlineManager.openOnlineStoreForTechincalCache(MustSellBackGroundService.this);
            if (onlineStoreOpen) {
                Bundle bundle = new Bundle();
                //MustSell/
                bundle.putString(Constants.BUNDLE_RESOURCE_PATH, Constants.MustSells);
                bundle.putBoolean(Constants.BUNDLE_SESSION_REQUIRED, false);
                bundle.putInt(Constants.BUNDLE_SESSION_TYPE, ConstantsUtils.SESSION_HEADER);
                bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 1);
                bundle.putBoolean(UtilConstants.BUNDLE_READ_FROM_TECHNICAL_CACHE, false);
                Constants.requestQuery(new OnlineODataInterface() {
                    @Override
                    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {

                       /* Log.d(TAG, "responseSuccess: " + list.size());
                        Log.d(TAG, "responseSuccess: " + list.get(0).getProperties().toString());
                        Log.d(TAG, "responseSuccess: " + list.get(0).getNavigationPropertyNames().toString());*/



                    }

                    @Override
                    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {
                        //Log.d(TAG, "responseFailed: " + s);
                    }
                }, bundle, this);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
