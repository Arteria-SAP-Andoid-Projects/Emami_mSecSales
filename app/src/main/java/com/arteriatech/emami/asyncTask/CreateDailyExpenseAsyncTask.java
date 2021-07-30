/*
package com.arteriatech.ss.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.ss.interfaces.DialogCallBack;
import com.arteriatech.ss.store.OnlineManager;
import com.arteriatech.ss.store.OnlineODataStoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

*/
/**
 * Created by e10769 on 10-03-2017.
 *//*


public class CreateDailyExpenseAsyncTask extends AsyncTask<String, Boolean, Boolean> {
    private Context mContext;
    private UIListener uiListener;
    private Hashtable dbHeadTable;
    private ArrayList<HashMap<String, String>> arrtable;
    private DialogCallBack dialogCallBack = null;

    public CreateDailyExpenseAsyncTask(Context context, Hashtable dbHeadTable, ArrayList<HashMap<String, String>> arrtable, UIListener uiListener, DialogCallBack dialogCallBack) {
        this.mContext = context;
        this.uiListener = uiListener;
        this.dbHeadTable = dbHeadTable;
        this.arrtable = arrtable;
        this.dialogCallBack = dialogCallBack;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean storeOpen = false;
        try {
            storeOpen = OnlineManager.openOnlineStore(mContext);
        } catch (OnlineODataStoreException e) {
            e.printStackTrace();
        }
        try {
            OnlineManager.createDailyExpense(dbHeadTable, arrtable, uiListener);
        } catch (OnlineODataStoreException e) {
            e.printStackTrace();
        }
        return storeOpen;
    }

    @Override
    protected void onPostExecute(Boolean storeOpen) {
        super.onPostExecute(storeOpen);
        if (dialogCallBack != null) {
            dialogCallBack.clickedStatus(storeOpen);
        }
    }
}
*/
