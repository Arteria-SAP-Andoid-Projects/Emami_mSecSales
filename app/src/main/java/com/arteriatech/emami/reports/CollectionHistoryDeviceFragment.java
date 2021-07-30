package com.arteriatech.emami.reports;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.emami.adapter.CollectionHisDeviceListAdapter;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.store.OnlineManager;
import com.arteriatech.emami.store.OnlineStoreListener;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionHistoryDeviceFragment extends Fragment implements  UIListener {
    private ArrayList<CollectionHistoryBean> alCollectionBean;
    private String mStrBundleRetID = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    TextView tvEmptyLayDevice = null;
    ListView lv_coll_his_list = null;
    public static String[] tempCollDevList = null;
    private CollectionHisDeviceListAdapter collectionHisListAdapter = null;
    int pendingCollVal = 0, penReqCount = 0;
    ArrayList<String> alAssignColl = new ArrayList<>();
    String concatCollectionStr = "";
    public  ProgressDialog syncProgDialog;
    String mStrPopUpText = "";

    Hashtable dbHeadTable;
    ArrayList<HashMap<String, String>> arrtable;
    private MessageWithBooleanCallBack dialogCallBack = null;
    private Context context=null;
    View myInflatedView = null;
    private Bundle bundle;
    private boolean mBoolIsReqResAval = false;
    private boolean mBoolIsNetWorkNotAval = false;

    public CollectionHistoryDeviceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bundle =savedInstanceState;
        // Inflate the layout for this fragment
        mStrBundleRetID = getArguments().getString(Constants.CPNo);
        mStrBundleCPGUID = getArguments().getString(Constants.CPGUID);
        mStrBundleRetUID = getArguments().getString(Constants.CPUID);
        mStrBundleRetName = getArguments().getString(Constants.RetailerName);
        myInflatedView = inflater.inflate(R.layout.fragment_collection_history_device, container, false);

        initUI();
        // Inflate the layout for this fragment
        return myInflatedView;
    }

    void initUI() {
        lv_coll_his_list = (ListView) myInflatedView.findViewById(R.id.lv_coll_list);
        tvEmptyLayDevice = (TextView) myInflatedView.findViewById(R.id.tv_empty_layone);

        getCollectionList();

        EditText edNameSearch = (EditText) myInflatedView.findViewById(R.id.ed_collection_search);
        edNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                collectionHisListAdapter.getFilter().filter(cs); //Filter from my adapter
                collectionHisListAdapter.notifyDataSetChanged(); //Update my view
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    /*Get CollectionList from device(DataVault)*/
    private void getCollectionList() {

        try {
            alCollectionBean = OfflineManager.getDevCollHisList(getActivity(), mStrBundleCPGUID);
            pendingCollVal = 0;
            if (tempCollDevList != null) {
                tempCollDevList = null;
                penReqCount = 0;
            }

            if (alCollectionBean != null && alCollectionBean.size() > 0) {
                tempCollDevList = new String[alCollectionBean.size()];
                for (int k = 0; k < alCollectionBean.size(); k++) {
                    tempCollDevList[k] = alCollectionBean.get(k).getDeviceNo();
                    pendingCollVal++;
                }
            }else{
                tvEmptyLayDevice.setVisibility(View.VISIBLE);
            }

            this.collectionHisListAdapter = new CollectionHisDeviceListAdapter(getActivity(), alCollectionBean,mStrBundleCPGUID,mStrBundleRetID,mStrBundleRetName, tvEmptyLayDevice);
            lv_coll_his_list.setEmptyView(getActivity().findViewById(R.id.tv_empty_lay) );
            lv_coll_his_list.setAdapter(this.collectionHisListAdapter);
            this.collectionHisListAdapter.notifyDataSetChanged();

            if(alCollectionBean.size()>0){
                tvEmptyLayDevice.setVisibility(View.GONE);
            } else
                tvEmptyLayDevice.setVisibility(View.VISIBLE);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception,getActivity());
        try {
            if (errorBean.hasNoError()) {
                penReqCount++;
                mBoolIsReqResAval = true;
                if ((operation == Operation.Create.getValue()) && (penReqCount == pendingCollVal)) {

                    Constants.isSync = false;
                    String mErrorMsg = "";
                    if (Constants.AL_ERROR_MSG.size() > 0) {
                        mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
                    }
                    closingproDialog();
                    if (mErrorMsg.equalsIgnoreCase("")) {
                        UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), getActivity());
                    } else {
                        Constants.customAlertDialogWithScroll(getActivity(), mErrorMsg);
                    }

                    CollectionHistoryActivity.updateListener.onUpdate();
                }

                if (operation == Operation.OfflineFlush.getValue()) {
                    if (UtilConstants.isNetworkAvailable(getActivity())) {
                        try {
                            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                            OfflineManager.refreshRequests(getActivity(), concatCollectionStr, CollectionHistoryDeviceFragment.this);
                        } catch (OfflineODataStoreException e) {
                            TraceLog.e(Constants.SyncOnRequestSuccess, e);
                        }
                    }else{
                        closingproDialog();
                        UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), getActivity());
                        CollectionHistoryActivity.updateListener.onUpdate();
                    }
                } else if (operation == Operation.OfflineRefresh.getValue()) {
                    LogManager.writeLogError(Constants.Error + " : " + exception.getMessage());

                    Constants.isSync = false;
                    String mErrorMsg = "";
                    if (Constants.AL_ERROR_MSG.size() > 0) {
                        mErrorMsg = Constants.convertALBussinessMsgToString(Constants.AL_ERROR_MSG);
                    }
                    closingproDialog();
                    if (mErrorMsg.equalsIgnoreCase("")) {
                        UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), getActivity());
                    } else {
                        Constants.customAlertDialogWithScroll(getActivity(), mErrorMsg);
                    }

                }
            }else{
                mBoolIsReqResAval = true;
                mBoolIsNetWorkNotAval = true;
                Constants.isSync = false;
                closingproDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), getActivity());
                CollectionHistoryActivity.updateListener.onUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closingproDialog(){
        try {
            syncProgDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) {
        if (operation == Operation.Create.getValue() && pendingCollVal > 0) {
            mBoolIsReqResAval =true;
            Constants.removeDeviceDocNoFromSharedPref(getActivity(), Constants.CollList, tempCollDevList[penReqCount]);
            UtilDataVault.storeInDataVault(tempCollDevList[penReqCount], "");
            penReqCount++;
        }
        if ((operation == Operation.Create.getValue()) && (penReqCount == pendingCollVal)) {
            if (UtilConstants.isNetworkAvailable(getActivity())) {
                try {
                    concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                    OfflineManager.refreshRequests(getActivity(), concatCollectionStr, CollectionHistoryDeviceFragment.this);
                } catch (OfflineODataStoreException e) {
                    TraceLog.e(Constants.SyncOnRequestSuccess, e);
                }
            }else{
                closingproDialog();
                UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), getActivity());
                CollectionHistoryActivity.updateListener.onUpdate();
            }
        } else if (operation == Operation.OfflineFlush.getValue()) {

            if (UtilConstants.isNetworkAvailable(getActivity())) {
                try {
                    concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
                    OfflineManager.refreshRequests(getActivity(), concatCollectionStr, CollectionHistoryDeviceFragment.this);

                } catch (OfflineODataStoreException e) {
                    TraceLog.e(Constants.SyncOnRequestSuccess, e);
                }
            }else{
                closingproDialog();
                UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync), getActivity());
                CollectionHistoryActivity.updateListener.onUpdate();
            }

        } else if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(alAssignColl);
           closingproDialog();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    getActivity(), R.style.MyTheme);
            builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    CollectionHistoryActivity.updateListener.onUpdate();
                                }
                            });

            builder.show();

        } else {
           closingproDialog();
        }
    }

    /*post device collections*/
    public void postDeviceCollections(MessageWithBooleanCallBack dialogCallBack, Context context) {
        this.dialogCallBack = dialogCallBack;
        this.context = context;

        mStrPopUpText = Constants.SubmittingDeviceCollectionsPleaseWait;
        try {
            new PostingDataVaultData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*AsyncTask to post device collections*/
    class PostingDataVaultData extends AsyncTask<Void, Boolean, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                syncProgDialog = new ProgressDialog(context, R.style.ProgressDialogTheme);
                syncProgDialog.setMessage(mStrPopUpText);
                syncProgDialog.setCancelable(false);
                syncProgDialog.show();


        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean onlineStoreOpen = false;
            try {
                Thread.sleep(1000);
                Constants.onlineStore = null;
                Constants.AL_ERROR_MSG.clear();
                Constants.IsOnlineStoreFailed = false;
                OnlineStoreListener.instance = null;

                Constants.ErrorCode = 0;
                Constants.ErrorNo = 0;
                Constants.ErrorName = "";
                mBoolIsReqResAval = true;
                mBoolIsNetWorkNotAval = false;

                onlineStoreOpen = OnlineManager.openOnlineStore(context);
                if(onlineStoreOpen){
                    if (pendingCollVal > 0) {
                        for (int k = 0; k < tempCollDevList.length; k++) {

                            while (!mBoolIsReqResAval) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            if(mBoolIsNetWorkNotAval){
                                break;
                            }
                            mBoolIsReqResAval= false;

                            String store = null;
                            try {
                                store = LogonCore.getInstance().getObjectFromStore(tempCollDevList[k].toString());
                            } catch (LogonCoreException e) {
                                e.printStackTrace();
                            }

                            //Fetch object from data vault
                            try {

                                JSONObject fetchJsonHeaderObject = new JSONObject(store);
                                dbHeadTable = new Hashtable();
                                arrtable = new ArrayList<>();
                                if (fetchJsonHeaderObject.getString(Constants.EntityType).equalsIgnoreCase(Constants.Collection)) {
                                    if(!alAssignColl.contains(Constants.SSINVOICES)){
                                        alAssignColl.add(Constants.SSInvoiceItemDetails);
                                        alAssignColl.add(Constants.SSINVOICES);
                                    }
                                    if (!alAssignColl.contains(Constants.FinancialPostings)) {
                                        alAssignColl.add(Constants.FinancialPostings);
                                        alAssignColl.add(Constants.FinancialPostingItemDetails);
                                    }

                                    if(!alAssignColl.contains(Constants.OutstandingInvoices)){
                                        alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
                                        alAssignColl.add(Constants.OutstandingInvoices);
                                    }
                                    dbHeadTable = Constants.getCollHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                    String itemsString = fetchJsonHeaderObject.getString(Constants.ItemsText);

                                    arrtable = UtilConstants.convertToArrayListMap(itemsString);

                                    try {
                                        OnlineManager.createCollectionEntry(dbHeadTable, arrtable, CollectionHistoryDeviceFragment.this);

                                    } catch (com.arteriatech.emami.store.OnlineODataStoreException e) {
                                        e.printStackTrace();
                                    }

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }



            } catch (Exception e) {
                e.printStackTrace();
            }

            return onlineStoreOpen;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if(!result) {
                /*try {
                    if (syncProgDialog != null) {
                        Constants.hideProgressDialog(syncProgDialog);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }*/

                closingproDialog();


               /* if (dialogCallBack != null) {
                    dialogCallBack.clickedStatus(result);
                }*/

                setCallBackToUI(result,Constants.makeMsgReqError(Constants.ErrorNo,context,false));

                CollectionHistoryActivity.updateListener.onUpdate();
            }
        }
    }

    private void setCallBackToUI(boolean status,String error_Msg){
        if (dialogCallBack!=null){
            dialogCallBack.clickedStatus(status,error_Msg,null);
        }
    }
}
