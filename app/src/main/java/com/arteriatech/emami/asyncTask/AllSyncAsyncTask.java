package com.arteriatech.emami.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.sync.SyncSelectionActivity;

import java.util.ArrayList;

/**
 * Created by e10769 on 22-04-2017.
 */

public class AllSyncAsyncTask extends AsyncTask<Void,Void,Void> {
    private UIListener uiListener;
    private ArrayList<String> allCollection;
    private Context mContext;
    private String concatCollectionStr="";

    public AllSyncAsyncTask(Context mContext, UIListener uiListener, ArrayList<String> allCollection) {
        this.uiListener = uiListener;
        this.allCollection = allCollection;
        this.mContext=mContext;
    }
    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        concatCollectionStr= SyncSelectionActivity.getAllSyncValue(mContext,allCollection);
        try {
            OfflineManager.refreshStoreSync(mContext, uiListener, Constants.All, concatCollectionStr);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return null;
    }
}
