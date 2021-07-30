package com.arteriatech.emami.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.emami.store.OnlineManager;
import com.arteriatech.emami.store.OnlineODataStoreException;
import com.arteriatech.emami.store.OnlineStoreListener;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by e10769 on 04-03-2017.
 *
 */

public class SyncFromDataValtAsyncTask extends AsyncTask<String, Boolean, Boolean> {
    private Context mContext;
    private UIListener uiListener;
    private Hashtable dbHeadTable;
    private ArrayList<HashMap<String, String>> arrtable;
    private String[] invKeyValues = null;
    private MessageWithBooleanCallBack dialogCallBack = null;

    public SyncFromDataValtAsyncTask(Context context, String[] invKeyValues, UIListener uiListener, MessageWithBooleanCallBack dialogCallBack) {
        this.mContext = context;
        this.uiListener = uiListener;
        this.invKeyValues = invKeyValues;
        this.dialogCallBack = dialogCallBack;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean onlineStoreOpen = false;
        try {
            Thread.sleep(1000);
            Constants.IsOnlineStoreFailed = false;
            Constants.onlineStore = null;
            OnlineStoreListener.instance =null;
            Constants.AL_ERROR_MSG.clear();

            Constants.ErrorCode = 0;
            Constants.ErrorNo = 0;
            Constants.ErrorName = "";

            onlineStoreOpen = OnlineManager.openOnlineStore(mContext);

            if(onlineStoreOpen){
                if (invKeyValues != null) {
                    for (int k = 0; k < invKeyValues.length; k++) {

                        while (!Constants.mBoolIsReqResAval) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if(Constants.mBoolIsNetWorkNotAval){
                            break;
                        }
                        Constants.mBoolIsReqResAval= false;


                        String store = null;
                        try {
                            store = LogonCore.getInstance().getObjectFromStore(invKeyValues[k].toString());
                        } catch (LogonCoreException e) {
                            e.printStackTrace();
                        }
                        if(store!=null && !store.equalsIgnoreCase("")){
                            //Fetch object from data vault
                            try {

                                JSONObject fetchJsonHeaderObject = new JSONObject(store);
                                dbHeadTable = new Hashtable();
                                arrtable = new ArrayList<>();
                                if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.ReturnOrderCreate)) {
                                    dbHeadTable = Constants.getROHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                    arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                    try {
                                        OnlineManager.createROEntity(dbHeadTable, arrtable, uiListener);
                                    } catch (OnlineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                }else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SecondarySOCreate)) {
                                    dbHeadTable = Constants.getSOHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                    arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                    try {
                                        OnlineManager.createSOEntity(dbHeadTable, arrtable, uiListener);
                                    } catch (OnlineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                }else if (fetchJsonHeaderObject.getString(Constants.EntityType).equalsIgnoreCase(Constants.Collection)) {
                                    dbHeadTable = Constants.getCollHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                    String itemsString = fetchJsonHeaderObject.getString(Constants.ItemsText);

                                    arrtable = UtilConstants.convertToArrayListMap(itemsString);

                                    try {
                                        OnlineManager.createCollectionEntry(dbHeadTable, arrtable, uiListener);

                                    } catch (com.arteriatech.emami.store.OnlineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                }else if (fetchJsonHeaderObject.getString(Constants.EntityType).equalsIgnoreCase(Constants.SSInvoices)) {
                                    dbHeadTable = Constants.getSSInvoiceHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                    String itemsString = fetchJsonHeaderObject.getString(Constants.ItemsText);

                                    arrtable = UtilConstants.convertToArrayListMap(itemsString);

                                    try {
                                        OnlineManager.createInvEntity(dbHeadTable, arrtable, uiListener);

                                    } catch (com.arteriatech.emami.store.OnlineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            Constants.mBoolIsReqResAval= true;
                        }


                    }
                    onlineStoreOpen = true;
                }
            }else{
                return onlineStoreOpen;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return onlineStoreOpen;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(!aBoolean) {
//            if (dialogCallBack != null) {
//                dialogCallBack.clickedStatus(aBoolean,"");
//            }

            setCallBackToUI(aBoolean,Constants.makeMsgReqError(Constants.ErrorNo,mContext,false));
        }

    }




    private void setCallBackToUI(boolean status,String error_Msg){
        if (dialogCallBack!=null){
            dialogCallBack.clickedStatus(status,error_Msg,null);
        }
    }

//    private void closingproDialog(){
//        try {
//            syncProgDialog.dismiss();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
