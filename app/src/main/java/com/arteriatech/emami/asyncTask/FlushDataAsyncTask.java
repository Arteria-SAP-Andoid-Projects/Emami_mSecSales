package com.arteriatech.emami.asyncTask;

import android.os.AsyncTask;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

/**
 * Created by e10769 on 22-04-2017.
 */

public class FlushDataAsyncTask extends AsyncTask<Void, Void, Void> {
    private UIListener uiListener;
    private ArrayList<String> alFlushColl;
    private String concatFlushCollStr = "";

    public FlushDataAsyncTask(UIListener uiListener, ArrayList<String> flushColl) {
        this.uiListener = uiListener;
        this.alFlushColl = flushColl;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(1000);

            for (int incVal = 0; incVal < alFlushColl.size(); incVal++) {
                if (incVal == 0 && incVal == alFlushColl.size() - 1) {
                    concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal);
                } else if (incVal == 0) {
                    concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal) + ", ";
                } else if (incVal == alFlushColl.size() - 1) {
                    concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal);
                } else {
                    concatFlushCollStr = concatFlushCollStr + alFlushColl.get(incVal) + ", ";
                }
            }

            try {
                if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                    try {
                        OfflineManager.flushQueuedRequests(uiListener, concatFlushCollStr);
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
}
