package com.arteriatech.emami.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.finance.InvoiceBean;
import com.arteriatech.emami.store.OnlineManager;
import com.arteriatech.emami.store.OnlineODataStoreException;
import com.arteriatech.emami.sync.SyncSelectionActivity;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.mobile.lib.request.INetListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by e10769 on 22-04-2017.
 */

public class PostDataFromDataValt extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private UIListener uiListener;
    private String[][] invKeyValues;
    private INetListener iNetListener;
    private Hashtable dbHeadTable;
    private ArrayList<HashMap<String, String>> arrtable;
    private Hashtable<String, ArrayList<InvoiceBean>> hashTableItemSerialNos;
    private static String TAG = "UpdatePendingRequest";

    public PostDataFromDataValt(Context mContext, UIListener uiListener, String[][] invKeyValues, INetListener iNetListener) {
        this.mContext = mContext;
        this.uiListener = uiListener;
        this.invKeyValues = invKeyValues;
        this.iNetListener = iNetListener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(1000);

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
                        store = LogonCore.getInstance().getObjectFromStore(invKeyValues[k][0].toString());
                    } catch (LogonCoreException e) {
                        e.printStackTrace();
                    }

                    try {
                        if(store!=null && !store.equalsIgnoreCase("")){
                            //Fetch object from data vault
                            try {

                                JSONObject fetchJsonHeaderObject = new JSONObject(store);
                                dbHeadTable = new Hashtable();
                                arrtable = new ArrayList<>();

                                if (fetchJsonHeaderObject.getString(Constants.EntityType).equalsIgnoreCase(Constants.SSInvoice)) {



                                    dbHeadTable.put(Constants.InvoiceGUID, fetchJsonHeaderObject.getString(Constants.InvoiceGUID));
                                    dbHeadTable.put(Constants.LoginID, fetchJsonHeaderObject.getString(Constants.LoginID));
                                    dbHeadTable.put(Constants.InvoiceTypeID, fetchJsonHeaderObject.getString(Constants.InvoiceTypeID));
                                    dbHeadTable.put(Constants.InvoiceDate, fetchJsonHeaderObject.getString(Constants.InvoiceDate));
                                    dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
                                    dbHeadTable.put(Constants.SoldToID, fetchJsonHeaderObject.getString(Constants.SoldToID));
    //                            dbHeadTable.put(Constants.ShipToID, fetchJsonHeaderObject.getString(Constants.ShipToID));
                                    dbHeadTable.put(Constants.CPGUID, fetchJsonHeaderObject.getString(Constants.CPGUID));
                                    dbHeadTable.put(Constants.CPTypeID, fetchJsonHeaderObject.getString(Constants.CPTypeID));
                                    dbHeadTable.put(Constants.SPGuid, fetchJsonHeaderObject.getString(Constants.SPGuid));
                                    dbHeadTable.put(Constants.SoldToCPGUID, fetchJsonHeaderObject.getString(Constants.SoldToCPGUID));
                                    dbHeadTable.put(Constants.SoldToTypeID, fetchJsonHeaderObject.getString(Constants.SoldToTypeID));
                                    dbHeadTable.put(Constants.SPNo, fetchJsonHeaderObject.getString(Constants.SPNo));

                                    dbHeadTable.put(Constants.NetAmount, fetchJsonHeaderObject.getString(Constants.NetAmount));
                                    dbHeadTable.put(Constants.TestRun, fetchJsonHeaderObject.getString(Constants.TestRun));

                                    dbHeadTable.put(Constants.Currency, fetchJsonHeaderObject.getString(Constants.Currency));

                                    String itemsString = fetchJsonHeaderObject.getString(Constants.strITEMS);

                                    String itemsSnoString = fetchJsonHeaderObject.getString(Constants.ITEMSSerialNo);

                                    arrtable = UtilConstants.convertToArrayListMap(itemsString);

                                    hashTableItemSerialNos = Constants.convertToMapArryList(itemsSnoString);

                                    String invGUID32 = fetchJsonHeaderObject.getString(Constants.InvoiceGUID).replace("-", "");

                                    String invCreatedOn = fetchJsonHeaderObject.getString(Constants.CreatedOn);
                                    String invCreatedAt = fetchJsonHeaderObject.getString(Constants.CreatedAt);

                                    String mStrDateTime = UtilConstants.getReArrangeDateFormat(invCreatedOn) + "T" + UtilConstants.convertTimeOnly(invCreatedAt);

                                    JSONObject invoiceHeader = Constants.prepareInvoiceJsonObject(dbHeadTable, arrtable, hashTableItemSerialNos);
                                    SyncSelectionActivity.performPushSSSubscription(mContext,invoiceHeader.toString(), invGUID32.toUpperCase(), mStrDateTime,iNetListener);
                                } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Collection)) {

                                    dbHeadTable = Constants.getCollHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);

                                    arrtable = UtilConstants.convertToArrayListMap(itemsString);

                                    try {
                                        OnlineManager.createCollectionEntry(dbHeadTable, arrtable, uiListener);

                                    } catch (OnlineODataStoreException e) {
                                        e.printStackTrace();
                                    }

                                } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SecondarySOCreate)) {
                                    dbHeadTable = Constants.getSOHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                    arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                    try {
                                        OnlineManager.createSOEntity(dbHeadTable, arrtable, uiListener);
                                    } catch (OnlineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Feedback)) {


                                    dbHeadTable = Constants.getFeedbackHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                    arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                    try {
                                        OnlineManager.createFeedBack(dbHeadTable, arrtable, uiListener);
                                    } catch (OnlineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SampleDisbursement)) {
                                    dbHeadTable = Constants.getSSInvoiceHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                    arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                    try {
                                        OnlineManager.createSSInvoiceEntity(dbHeadTable, arrtable, uiListener);
                                    } catch (OnlineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                }else if(fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.ReturnOrderCreate)){
                                    dbHeadTable = Constants.getROHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                    arrtable= UtilConstants.convertToArrayListMap(itemsString);
                                    try {
                                        OnlineManager.createROEntity(dbHeadTable, arrtable, uiListener);
                                    } catch (OnlineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                }else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.Expenses)) {
                                    dbHeadTable = Constants.getExpenseHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                    arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                    try {
                                        OnlineManager.createDailyExpense(dbHeadTable, arrtable, uiListener);
                                    } catch (OnlineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                }else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.ChannelPartners)) {
                                    dbHeadTable = Constants.getCPHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                    arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                    try {
                                        OnlineManager.createCP(dbHeadTable, arrtable, uiListener);
                                    } catch (OnlineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                } else if (fetchJsonHeaderObject.getString(Constants.entityType).equalsIgnoreCase(Constants.SSInvoices)) {
                                    dbHeadTable = Constants.getSecondaryInvHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                    arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                    try {
                                        OnlineManager.createInvEntity(dbHeadTable, arrtable, uiListener);
                                    } catch (OnlineODataStoreException e) {
                                        e.printStackTrace();
                                    }
                                }




                            } catch (JSONException e) {
                                e.printStackTrace();
                                Constants.iSAutoSync = false;
                            }
                        }else{
                            Constants.mBoolIsReqResAval= true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Constants.iSAutoSync = false;
        }
        return null;
    }
}
