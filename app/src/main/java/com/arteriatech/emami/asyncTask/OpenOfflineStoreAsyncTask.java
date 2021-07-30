package com.arteriatech.emami.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.offline.ODataOfflineStoreOptions;

/**
 * Created by e10526 on 10/12/2017.
 */

public class OpenOfflineStoreAsyncTask extends AsyncTask<String, Boolean, Boolean> {
    private Context mContext;
    private UIListener uiListener;

    public OpenOfflineStoreAsyncTask(Context context,  UIListener uiListener) {
        this.mContext = context;
        this.uiListener = uiListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            Thread.sleep(1000);
            closeStore(mContext,OfflineManager.options);
           /* try {
                closeStoreMustSell(mContext,OfflineManager.optionsMustSell);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            try {
                OfflineManager.openOfflineStore(mContext, uiListener);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

    }

    public static void closeStore(Context mContext, ODataOfflineStoreOptions options){
        try {
            OfflineManager.closeOfflineStore(mContext,options);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_during_offline_close + e.getMessage());
        }
    }
    public static void closeStoreMustSell(Context mContext, ODataOfflineStoreOptions options){
        try {
            OfflineManager.closeOfflineStoreMustSell(mContext,options);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_during_offline_close + e.getMessage());
        }
    }
}
