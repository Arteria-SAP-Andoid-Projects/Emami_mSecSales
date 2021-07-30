package com.arteriatech.emami.reports;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.emami.adapter.InvoiceHisListAdapter;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.finance.InvoiceBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.store.OnlineManager;
import com.arteriatech.emami.store.OnlineODataStoreException;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreContext;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.mobile.lib.parser.IODataError;
import com.sap.mobile.lib.parser.ParserException;
import com.sap.mobile.lib.request.BaseRequest;
import com.sap.mobile.lib.request.INetListener;
import com.sap.mobile.lib.request.IRequest;
import com.sap.mobile.lib.request.IRequestManager;
import com.sap.mobile.lib.request.IRequestStateElement;
import com.sap.mobile.lib.request.IResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by e10742 on 10-01-2017.
 */

public class DeviceInvoiceFragment extends Fragment implements UIListener, INetListener {

    private InvoiceHisListAdapter invoiceHisListAdapter = null;
    private ArrayList<InvoiceHistoryBean> alInvoiceBean;
    private String mStrBundleRetID = "", mStrBundleCPGUID = "";
    private String mStrBundleRetName = "";
    Spinner spinvHisStatus;

    ListView lvInvHistList = null;
    TextView tvEmptyListLay = null;

    Bundle bundleExtras = null;
    View myInflatedView = null;

    private ProgressDialog syncProgDialog;
    String mStrPopUpText = "";
    int pendingInvoicesVal = 0, penReqCount = 0;
    ArrayList<String> alAssignColl = new ArrayList<>();
    String concatCollectionStr = "";
    public String[] tempInvDevList = null;

    Hashtable dbHeadTable;
    ArrayList<HashMap<String, String>> arrtable;
    Hashtable<String,ArrayList<InvoiceBean>> hashTableItemSerialNos;

    public static boolean isDataAvailable = false;
    private boolean tokenFlag=false;
    public static IRequestManager mRequestmanager = null;

    String endPointURL="";
    String appConnID="";
    int mError =0;

    public DeviceInvoiceFragment() {
    }

    public void setArguments(Bundle bundle){
        bundleExtras =bundle;
        // Inflate the layout for this fragment
        mStrBundleRetID = bundle.getString(Constants.CPNo);
        mStrBundleCPGUID = bundle.getString(Constants.CPGUID);
        mStrBundleRetName = bundle.getString(Constants.RetailerName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myInflatedView = inflater.inflate(R.layout.activity_invoice_history_list, container, false);
        initUI();

        return myInflatedView;
    }

    /*Initialize UI*/
    void initUI() {
        lvInvHistList = (ListView) myInflatedView.findViewById(R.id.lv_inv_hist_list);
        tvEmptyListLay = (TextView) myInflatedView.findViewById(R.id.tv_empty_lay);

        spinvHisStatus = (Spinner) myInflatedView.findViewById(R.id.spin_invoice_his_status_id);
        spinvHisStatus.setVisibility(View.GONE);

        EditText edNameSearch = (EditText) myInflatedView.findViewById(R.id.ed_invoice_search);
        edNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                invoiceHisListAdapter.getFilter().filter(cs); //Filter from my adapter
                invoiceHisListAdapter.notifyDataSetChanged(); //Update my view
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
        getDeviceInvoiceList();
    }

    /*Gets invoice list from device*/
    private void getDeviceInvoiceList() {
        try {

            alInvoiceBean = OfflineManager.getDevInvoiceHistoryList(getActivity(), mStrBundleRetID);
            pendingInvoicesVal = 0;
            if (tempInvDevList != null) {
                tempInvDevList = null;
                penReqCount = 0;
            }

            if (alInvoiceBean != null && alInvoiceBean.size() > 0) {
                tempInvDevList = new String[alInvoiceBean.size()];
                for (int k = 0; k < alInvoiceBean.size(); k++) {
                    tempInvDevList[k] = alInvoiceBean.get(k).getDeviceNo();
                    pendingInvoicesVal++;
                }
            }

            invoiceHisListAdapter = new InvoiceHisListAdapter(getActivity(), R.layout.activity_invoice_history_list, alInvoiceBean, bundleExtras);
            lvInvHistList.setAdapter(invoiceHisListAdapter);
            lvInvHistList.setEmptyView(myInflatedView.findViewById(R.id.tv_empty_lay) );
            invoiceHisListAdapter.notifyDataSetChanged();

            if(alInvoiceBean!=null && alInvoiceBean.size()>0){
                tvEmptyListLay.setVisibility(View.GONE);
            } else
                tvEmptyListLay.setVisibility(View.VISIBLE);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        penReqCount++;
        if ((operation == Operation.Create.getValue()) && (penReqCount == pendingInvoicesVal)) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }

        if (operation == Operation.OfflineFlush.getValue()) {
            try {
                OfflineManager.refreshRequests(getActivity(), Constants.Visits, DeviceInvoiceFragment.this);
            } catch (OfflineODataStoreException e1) {
                e1.printStackTrace();
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
            Constants.isSync = false;
            syncProgDialog.dismiss();
            UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), getActivity());

        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) {
        if (operation == Operation.Create.getValue() && pendingInvoicesVal > 0) {

            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
//            set = sharedPreferences.getStringSet(Constants.InvList, null);
            set = sharedPreferences.getStringSet(Constants.SSInvoices, null);

            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }

            setTemp.remove(tempInvDevList[penReqCount]);

            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putStringSet(Constants.InvList, setTemp);
            editor.putStringSet(Constants.SSInvoices, setTemp);
            editor.commit();

            try {
                LogonCore.getInstance().addObjectToStore(tempInvDevList[penReqCount], "");
            } catch (LogonCoreException e) {
                e.printStackTrace();
            }

            penReqCount++;
        }
        if ((operation == Operation.Create.getValue()) && (penReqCount == pendingInvoicesVal)) {

            try {
                for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
                    if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                    } else if (incVal == 0) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                    } else if (incVal == alAssignColl.size() - 1) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                    } else {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                    }
                }
                OfflineManager.refreshRequests(getActivity(), concatCollectionStr, DeviceInvoiceFragment.this);
            } catch (OfflineODataStoreException e) {
                TraceLog.e(Constants.SyncOnRequestSuccess, e);
            }


        } else if (operation == Operation.OfflineFlush.getValue()) {

            try {
                for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
                    if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                    } else if (incVal == 0) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                    } else if (incVal == alAssignColl.size() - 1) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                    } else {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                    }
                }
                OfflineManager.refreshRequests(getActivity(), concatCollectionStr, DeviceInvoiceFragment.this);

            } catch (OfflineODataStoreException e) {
                TraceLog.e(Constants.SyncOnRequestSuccess, e);
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            try {
                String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
                    String colName = alAssignColl.get(incReq);
                    if (colName.contains("?$")) {
                        String splitCollName[] = colName.split("\\?");
                        colName = splitCollName[0];
                    }

                    if(colName.contains("(")){
                        String splitCollName[] = colName.split("\\(");
                        colName = splitCollName[0];
                    }

                    Constants.events.updateStatus(Constants.SYNC_TABLE,
                            colName, Constants.TimeStamp, syncTime
                    );
                }
            } catch (Exception exce) {
                LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
            }


            try {
                syncProgDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }



            AlertDialog.Builder builder = new AlertDialog.Builder(
                    getActivity(), R.style.MyTheme);
            builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    InvoiceHistoryActivity.updateListener.onUpdate();
                                }
                            });

            builder.show();

        } else {
            try {
                syncProgDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*post device collections*/
    public void postDeviceInvoices() {
        mStrPopUpText = Constants.SubmittingDeviceInvoicesPleaseWait;
        try {
            new PostingDataVaultData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*AsyncTask to post device collections*/
    public class PostingDataVaultData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(mStrPopUpText);
            syncProgDialog.setCancelable(false);
            syncProgDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (pendingInvoicesVal > 0) {
                    tokenFlag = false;
                    onLoadToken();
                    for (int k = 0; k < tempInvDevList.length; k++) {
                        String store = null;
                        try {
                            store = LogonCore.getInstance().getObjectFromStore(tempInvDevList[k].toString());
                        } catch (LogonCoreException e) {
                            e.printStackTrace();
                        }

                        //Fetch object from data vault
                        try {

                            JSONObject fetchJsonHeaderObject = new JSONObject(store);
                            dbHeadTable = new Hashtable();
                            arrtable = new ArrayList<>();
                            if (fetchJsonHeaderObject.getString(Constants.EntityType).equalsIgnoreCase(Constants.SSInvoice)) {

                                if (!alAssignColl.contains(Constants.SSINVOICES)) {
                                    alAssignColl.add(Constants.SSInvoiceItemDetails);
                                    alAssignColl.add(Constants.SSINVOICES);
                                    alAssignColl.add(Constants.SPStockItemSNos);
                                    alAssignColl.add(Constants.SPStockItems);
                                }

                                if (!alAssignColl.contains(Constants.CPStockItems)) {
                                    alAssignColl.add(Constants.CPStockItemDetails);
                                    alAssignColl.add(Constants.CPStockItems);
                                    alAssignColl.add(Constants.CPStockItemSnos);
                                }

                                if(!alAssignColl.contains(Constants.OutstandingInvoices)){
                                    alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
                                    alAssignColl.add(Constants.OutstandingInvoices);
                                }


                                dbHeadTable.put(Constants.InvoiceGUID, fetchJsonHeaderObject.getString(Constants.InvoiceGUID));
                                dbHeadTable.put(Constants.LoginID, fetchJsonHeaderObject.getString(Constants.LoginID));
                                dbHeadTable.put(Constants.InvoiceTypeID, fetchJsonHeaderObject.getString(Constants.InvoiceTypeID));
                                dbHeadTable.put(Constants.InvoiceDate, fetchJsonHeaderObject.getString(Constants.InvoiceDate));
                                dbHeadTable.put(Constants.CPNo, fetchJsonHeaderObject.getString(Constants.CPNo));
                                dbHeadTable.put(Constants.SoldToID, fetchJsonHeaderObject.getString(Constants.SoldToID));
                                dbHeadTable.put(Constants.ShipToID, fetchJsonHeaderObject.getString(Constants.ShipToID));
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
                                performPushSSSubscription(invoiceHeader.toString(), invGUID32.toUpperCase(), mStrDateTime);
                            }else  if (fetchJsonHeaderObject.getString(Constants.EntityType).equalsIgnoreCase(Constants.SSInvoices)) {
                                if (!alAssignColl.contains(Constants.SSINVOICES)) {
                                    alAssignColl.add(Constants.SSInvoiceItemDetails);
                                    alAssignColl.add(Constants.SSINVOICES);
                                    alAssignColl.add(Constants.SPStockItemSNos);
                                    alAssignColl.add(Constants.SPStockItems);
                                }

                                if (!alAssignColl.contains(Constants.CPStockItems)) {
                                    alAssignColl.add(Constants.CPStockItemDetails);
                                    alAssignColl.add(Constants.CPStockItems);
                                    alAssignColl.add(Constants.CPStockItemSnos);
                                }

                                if(!alAssignColl.contains(Constants.OutstandingInvoices)){
                                    alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
                                    alAssignColl.add(Constants.OutstandingInvoices);
                                }



                                dbHeadTable = Constants.getSSInvoiceHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                arrtable = UtilConstants.convertToArrayListMap(itemsString);
                                try {
                                    OnlineManager.createSSInvoiceEntity(dbHeadTable, arrtable, DeviceInvoiceFragment.this);
                                } catch (OnlineODataStoreException e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private void performPushSSSubscription(String entry,String invGUID32,String mStrDateTime) {
        mRequestmanager = Constants.mApplication.getRequestManager();
        try {
            String PushUrl = endPointURL+ "/"+ Constants.SSINVOICES;
            String xmlData = entry;
            byte[] data = null;
            try {
                data = xmlData.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("Unsupported encoding!");
            }

            IRequest request = new BaseRequest();
            request.setListener(this);
            request.setRequestUrl(PushUrl);
            Hashtable<String, String> headers = new Hashtable<String, String>();
            headers.put("Content-Type", "application/json");
            headers.put("x-csrf-token", Constants.x_csrf_token);
            headers.put("X-SMP-APPCID", appConnID);
            headers.put("Cookie", Constants.cookies);
            headers.put("Accept", "application/xml");
            headers.put("RequestID", invGUID32);
            headers.put("RepeatabilityCreation", mStrDateTime);
            ((BaseRequest) request).setData(data);
            request.setHeaders(headers);
            ((BaseRequest) request).setData(data);
            request.setRequestMethod(IRequest.REQUEST_METHOD_POST);
            request.setPriority(IRequest.PRIORITY_HIGH);
            request.setRequestTAG(Constants.SSINVOICES);

            mRequestmanager.makeRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(IRequest arg0, IResponse aResponse,
                        IRequestStateElement arg2) {
        mError++;
        penReqCount++;
        try{
            Constants.parser = Constants.mApplication.getParser();

            IODataError errResponse = null;
            HttpResponse response = aResponse;
            if (aResponse != null) {
                try {
                    HttpEntity responseEntity = aResponse.getEntity();
                    String responseString = EntityUtils.toString(responseEntity);
                    responseString = responseString.replace(getString(R.string.Bad_Request), "");

                    errResponse = Constants.parser.parseODataError(responseString);

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();

                } catch (IllegalStateException e) {
                    e.printStackTrace();

                } catch (ParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            String errorMsg = errResponse.getMessage()!=null?errResponse.getMessage():"";

            LogManager.writeLogError(getString(R.string.Error_in_Retailer_invoice) + errorMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }



        if( penReqCount ==pendingInvoicesVal){

            try {

                for(int incVal=0;incVal<alAssignColl.size();incVal++){
                    if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                    } else if (incVal == 0) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal)+", ";
                    }else if(incVal == alAssignColl.size() - 1){
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                    }else{
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal)+", ";
                    }
                }
                OfflineManager.refreshRequests(getActivity(), concatCollectionStr, DeviceInvoiceFragment.this);
            } catch (OfflineODataStoreException e) {
                TraceLog.e(Constants.SyncOnRequestSuccess, e);
            }
        }
    }

    @Override
    public void onSuccess(IRequest aRequest, IResponse response) {
        try {

            if(pendingInvoicesVal>0){

                Set<String> set = new HashSet<>();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                set = sharedPreferences.getStringSet(Constants.InvList, null);

                HashSet<String> setTemp=new HashSet<>();
                if(set!=null && !set.isEmpty()){
                    Iterator itr = set.iterator();
                    while(itr.hasNext())
                    {
                        setTemp.add(itr.next().toString());
                    }
                }

                setTemp.remove(tempInvDevList[penReqCount]);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet(Constants.InvList, setTemp);
                editor.commit();

                try {
                    LogonCore.getInstance().addObjectToStore(tempInvDevList[penReqCount], "");
                } catch (LogonCoreException e) {
                    e.printStackTrace();
                }

                penReqCount ++;
            }

            //ignore this sections for hard codes (parsing xml request for invoice creation)
            String repData = EntityUtils.toString(response.getEntity());

            int repStInd = repData.toString().indexOf("<d:InvoiceNo>") + 13;
            int repEndInd = repData.toString().indexOf("</d:InvoiceNo>");
            String invNo = repData.substring(repStInd, repEndInd);

            String popUpText = "Retailer invoice # "+invNo+" created successfully." ;

            LogManager.writeLogInfo(popUpText);

        }catch (Exception e){
            e.printStackTrace();
        }

        if( penReqCount == pendingInvoicesVal){
            try {
                for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
                    if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                    } else if (incVal == 0) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                    } else if (incVal == alAssignColl.size() - 1) {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                    } else {
                        concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                    }
                }
                OfflineManager.refreshRequests(getActivity(), concatCollectionStr, DeviceInvoiceFragment.this);
            } catch (OfflineODataStoreException e) {
                TraceLog.e(Constants.SyncOnRequestSuccess, e);
            }

        }

    }

    /**
     * This method retrieves csrf token from server.
     */
    private void onLoadToken() {
        try {
            // get Application Connection ID
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
            endPointURL = lgCtx.getAppEndPointUrl();
            appConnID = LogonCore.getInstance().getLogonContext()
                    .getConnId();
        } catch (LogonCoreException e) {
            LogManager.writeLogError(getString(R.string.Device_registration_failed), e);
        }

        String PushUrl = endPointURL+ "/?sap-language=en";
        IRequest req = new BaseRequest();
        req.setRequestMethod(IRequest.REQUEST_METHOD_GET);
        req.setPriority(1);
        req.setRequestUrl(PushUrl);
        Hashtable headers = new Hashtable();
        headers.put("x-csrf-token", "fetch");
        headers.put("X-SMP-APPCID", appConnID);
        ((BaseRequest) req).setHeaders(headers);
        req.setListener(new INetListener() {
            @Override
            public void onSuccess(IRequest iRequest, IResponse iResponse) {
                try {
                    tokenFlag = true;
                    isDataAvailable = true;
                    Constants.x_csrf_token = iResponse.getHeaders("x-csrf-token")[0].getValue();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(IRequest iRequst, IResponse iResponse, IRequestStateElement iReqStateElement) {
                try {
                    isDataAvailable = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        IRequestManager mRequestmanager = null;
        mRequestmanager = Constants.mApplication.getRequestManager();
        mRequestmanager.makeRequest(req);
        while (!isDataAvailable) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isDataAvailable =false;
    }

}
