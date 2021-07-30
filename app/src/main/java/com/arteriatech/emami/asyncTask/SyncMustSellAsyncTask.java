package com.arteriatech.emami.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;


/**
 * Created by e10526 on 23-03-2018.
 */

public class SyncMustSellAsyncTask extends AsyncTask<String, Boolean, Boolean> {
    private Context mContext;
    private MessageWithBooleanCallBack dialogCallBack = null;
    boolean onlineStoreOpen = false;
    private String mSyncType="";
    public SyncMustSellAsyncTask(Context context, MessageWithBooleanCallBack dialogCallBack,String mSyncType) {
        this.mContext = context;
        this.dialogCallBack = dialogCallBack;
        this.mSyncType = mSyncType;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        onlineStoreOpen = false;
        try {
                    Log.d("BeforeCallMustSell Req", UtilConstants.getSyncHistoryddmmyyyyTime());
                    try {
                        if(!OfflineManager.isOfflineStoreOpenMustSell()) {
                            try {
                                OfflineManager.openOfflineStoreMustSell(mContext, new UIListener() {
                                    @Override
                                    public void onRequestError(int i, Exception e) {
                                        Log.d("opOffStoreMS onReqError", UtilConstants.getSyncHistoryddmmyyyyTime());
                                        setCallBackToUI(true,"");
                                    }

                                    @Override
                                    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                        Log.d("opOffStoreMS onReqSuc", UtilConstants.getSyncHistoryddmmyyyyTime());
                                        setCallBackToUI(true,"");
                                    }
                                });
                            } catch (OfflineODataStoreException e) {
                                onlineStoreOpen =true;
                                LogManager.writeLogError(Constants.error_txt + e.getMessage());
                            }

                        }else{
                        if(mSyncType.equalsIgnoreCase(Constants.Fresh) || mSyncType.equalsIgnoreCase(Constants.All)){
                            try {
                                if (UtilConstants.isNetworkAvailable(mContext)) {
                                    OfflineManager.refreshRequestsMustSell(mContext, Constants.MustSells, new UIListener() {
                                        @Override
                                        public void onRequestError(int operation, Exception exception) {
                                            ErrorBean errorBean = Constants.getErrorCodeMustSell(operation, exception,mContext);
                                            try {
                                                if (!errorBean.hasNoError()) {
                                                    if (errorBean.getErrorCode() == Constants.Resource_not_found) {
                                                        UtilConstants.closeStore(mContext,
                                                                OfflineManager.optionsMustSell, errorBean.getErrorMsg(),
                                                                OfflineManager.offlineStoreMustSell, Constants.PREFS_NAME,"");
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            Log.d("refReqMust onReqError", UtilConstants.getSyncHistoryddmmyyyyTime());
                                            setCallBackToUI(true,"");
                                        }

                                        @Override
                                        public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                            Log.d("refReqMust onReqError", UtilConstants.getSyncHistoryddmmyyyyTime());
                                            setCallBackToUI(true,"");
                                        }
                                    });
                                }else{
                                    onlineStoreOpen =true;
                                }
                            } catch (OfflineODataStoreException e) {
                                onlineStoreOpen =true;
                                TraceLog.e("Sync::onRequestSuccess", e);
                            }
                        }else{
                            onlineStoreOpen =true;
                        }
                        }
                    } catch (Exception e) {
                        onlineStoreOpen =true;
                        e.printStackTrace();
                    }
                    Log.d("AfterCallMustSell Req", UtilConstants.getSyncHistoryddmmyyyyTime());
        } catch (Exception e) {
            onlineStoreOpen =true;
            e.printStackTrace();
        }
        return onlineStoreOpen;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean) {
            setCallBackToUI(aBoolean,Constants.makeMsgReqError(Constants.ErrorNo,mContext,false));
        }

    }

    private void setCallBackToUI(boolean status,String error_Msg){
        if (dialogCallBack!=null){
            dialogCallBack.clickedStatus(status,error_Msg,null);
        }
    }

}
