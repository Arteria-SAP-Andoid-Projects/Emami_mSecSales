package com.arteriatech.emami.feedback;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.arteriatech.emami.adapter.FeedbackListAdapter;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.FeedbackBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.store.OnlineManager;
import com.arteriatech.emami.store.OnlineODataStoreException;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by e10742 on 13-01-2017.
 *
 */

public class DeviceFeedbackListFragment extends Fragment implements UIListener {

    private FeedbackListAdapter feedbackHisListAdapter = null;
    private ArrayList<FeedbackBean> alFeedBackBean;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "",mStrCPGUID="";
    ListView lvFeedbackList = null;
    TextView tvEmptyLay = null;

    Bundle bundleExtras = null;
    View myInflatedView = null;

    public String[] tempFeedbackDevList = null;
    int pendingCollVal = 0, penReqCount = 0;
    ArrayList<String> alAssignColl = new ArrayList<>();
    String concatCollectionStr = "";
    private ProgressDialog syncProgDialog;
    String mStrPopUpText = "";

    Hashtable dbHeadTable;
    ArrayList<HashMap<String, String>> arrtable;

    public void setArguments(Bundle bundle){
        bundleExtras =bundle;
        // Inflate the layout for this fragment
        mStrBundleRetID = bundle.getString(Constants.CPNo);
        mStrCPGUID = bundle.getString(Constants.CPGUID);
        mStrBundleRetName = bundle.getString(Constants.RetailerName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myInflatedView = inflater.inflate(R.layout.feedback_fragment, container, false);

        initUI();
        return myInflatedView;
    }

    void initUI(){
        lvFeedbackList = (ListView) myInflatedView.findViewById(R.id.lv_feedback);
        tvEmptyLay = (TextView) myInflatedView.findViewById(R.id.tv_empty_lay);

        getFeedbackList(mStrCPGUID);

        EditText edNameSearch = (EditText) myInflatedView.findViewById(R.id.ed_invoice_search);
        edNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if(feedbackHisListAdapter!=null) {
                    feedbackHisListAdapter.getFilter().filter(cs); //Filter from my adapter
                    feedbackHisListAdapter.notifyDataSetChanged(); //Update my view
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    /*Gets Feedback List*/
    private void getFeedbackList(String strCPGUID){
        try {
            alFeedBackBean = OfflineManager.getDeviceFeedBackList(getActivity(), strCPGUID);

            pendingCollVal = 0;
            if (tempFeedbackDevList != null) {
                tempFeedbackDevList = null;
                penReqCount = 0;
            }

            if (alFeedBackBean != null && alFeedBackBean.size() > 0) {
                tempFeedbackDevList = new String[alFeedBackBean.size()];
                for (int k = 0; k < alFeedBackBean.size(); k++) {
                    tempFeedbackDevList[k] = alFeedBackBean.get(k).getDeviceNo();
                    pendingCollVal++;
                }
            }

            feedbackHisListAdapter = new FeedbackListAdapter(getActivity(),
                    R.layout.activity_invoice_history_list, alFeedBackBean,tvEmptyLay, mStrBundleRetID, mStrBundleRetName);
            lvFeedbackList.setAdapter(feedbackHisListAdapter);
            feedbackHisListAdapter.notifyDataSetChanged();

            if (alFeedBackBean != null && alFeedBackBean.size() > 0)
                tvEmptyLay.setVisibility(View.GONE);
            else
                tvEmptyLay.setVisibility(View.VISIBLE);


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        penReqCount++;
        if ((operation == Operation.Create.getValue()) && (penReqCount == pendingCollVal)) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
            cloasingProgDialog();
            UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), getActivity());
        }
        if (operation == Operation.OfflineFlush.getValue()) {
            cloasingProgDialog();
            UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), getActivity());
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.isSync = false;
            syncProgDialog.dismiss();
            UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), getActivity());

        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) {
        if (operation == Operation.Create.getValue() && pendingCollVal > 0) {
            Constants.removeDeviceDocNoFromSharedPref(getContext(), Constants.FeedbackList,tempFeedbackDevList[penReqCount]);
            UtilDataVault.storeInDataVault(tempFeedbackDevList[penReqCount],"");
            penReqCount++;
        }
        if ((operation == Operation.Create.getValue()) && (penReqCount == pendingCollVal)) {
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            try {
                OfflineManager.refreshRequests(getActivity(), concatCollectionStr, DeviceFeedbackListFragment.this);
            } catch (OfflineODataStoreException e) {
                TraceLog.e(Constants.SyncOnRequestSuccess, e);
            }
        } else if (operation == Operation.OfflineFlush.getValue()) {
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            try {
                OfflineManager.refreshRequests(getActivity(), concatCollectionStr, DeviceFeedbackListFragment.this);
            } catch (OfflineODataStoreException e) {
                TraceLog.e(Constants.SyncOnRequestSuccess, e);
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(alAssignColl);
            cloasingProgDialog();
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    getActivity(), R.style.MyTheme);
            builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    FeedbackListActivity.updateListener.onUpdate();
                                }
                            });

            builder.show();

        } else {
            cloasingProgDialog();
        }
    }

    private void cloasingProgDialog(){
        try {
            syncProgDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*post device collections*/
    public void postDeviceCollections() {
        mStrPopUpText = Constants.SubmittingDeviceFeedbacksPleaseWait;
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
                Thread.sleep(2000);

                OnlineManager.openOnlineStore(getActivity());

                if (pendingCollVal > 0) {
                    for (int k = 0; k < tempFeedbackDevList.length; k++) {
                        String store = null;
                        try {
                            store = LogonCore.getInstance().getObjectFromStore(tempFeedbackDevList[k].toString());
                        } catch (LogonCoreException e) {
                            e.printStackTrace();
                        }

                        //Fetch object from data vault
                        try {

                            JSONObject fetchJsonHeaderObject = new JSONObject(store);
                            dbHeadTable = new Hashtable();
                            arrtable = new ArrayList<>();
                            if (fetchJsonHeaderObject.getString(Constants.EntityType).equalsIgnoreCase(Constants.Feedback)) {

                                if (!alAssignColl.contains(Constants.Feedbacks)) {
                                    alAssignColl.add(Constants.Feedbacks);
                                    alAssignColl.add(Constants.FeedbackItemDetails);
                                }

                                dbHeadTable = Constants.getFeedbackHeaderValuesFromJsonObject(fetchJsonHeaderObject);
                                String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                                arrtable= UtilConstants.convertToArrayListMap(itemsString);
                                try {
                                    OnlineManager.createFeedBack(dbHeadTable, arrtable, DeviceFeedbackListFragment.this);
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

}
