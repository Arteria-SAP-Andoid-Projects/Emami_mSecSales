//package com.arteriatech.ss.asyncTask;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//
//import com.arteriatech.mutils.common.UtilConstants;
//import com.arteriatech.mutils.store.OnlineODataInterface;
//import com.arteriatech.ss.common.Constants;
//import com.arteriatech.ss.common.ConstantsUtils;
//import com.arteriatech.ss.interfaces.MessageWithBooleanCallBack;
//import com.arteriatech.ss.mbo.MustSellBean;
//import com.arteriatech.ss.store.OfflineManager;
//import com.sap.smp.client.odata.ODataEntity;
//import com.sap.smp.client.odata.store.ODataRequestExecution;
//
//import java.util.HashMap;
//import java.util.List;
//
///**
// * Created by e10526 on 26-03-2018.
// */
//
//public class TechincalCacheMustSellAsyncTask extends AsyncTask<String, Boolean, Boolean> {
//    private Context mContext;
//    private MessageWithBooleanCallBack dialogCallBack = null;
//    boolean isTechincalCacheDone = false;
//    private String mSyncType="";
//    public TechincalCacheMustSellAsyncTask(Context context, MessageWithBooleanCallBack dialogCallBack,String mSyncType) {
//        this.mContext = context;
//        this.dialogCallBack = dialogCallBack;
//        this.mSyncType = mSyncType;
//    }
//
//    @Override
//    protected Boolean doInBackground(String... params) {
//        isTechincalCacheDone = false;
//        try {
//            Log.d("BeforeCallMustSell Req", UtilConstants.getSyncHistoryddmmyyyyTime());
//            try {
//                String qry = Constants.MustSells;
//                Constants.onlineRequest(mContext, qry, false, 1,
//                        ConstantsUtils.SESSION_QRY, new OnlineODataInterface() {
//                            @Override
//                            public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
//                                HashMap<String,MustSellBean> hashMapMustSellTemp =  OfflineManager.getMustSellMatList(list);
//                                setCallBackToUI(true,"");
//                            }
//
//                            @Override
//                            public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {
//                                setCallBackToUI(true,"");
//                            }
//                        }, true, true);
//
//            } catch (Exception e) {
//                isTechincalCacheDone =true;
//                e.printStackTrace();
//            }
//            Log.d("AfterCallMustSell Req", UtilConstants.getSyncHistoryddmmyyyyTime());
//        } catch (Exception e) {
//            isTechincalCacheDone =true;
//            e.printStackTrace();
//        }
//        return isTechincalCacheDone;
//    }
//
//    @Override
//    protected void onPostExecute(Boolean aBoolean) {
//        super.onPostExecute(aBoolean);
//        if(aBoolean) {
//            setCallBackToUI(aBoolean,Constants.makeMsgReqError(Constants.ErrorNo,mContext,false));
//        }
//
//    }
//
//    private void setCallBackToUI(boolean status,String error_Msg){
//        if (dialogCallBack!=null){
//            dialogCallBack.clickedStatus(status,error_Msg,null);
//        }
//    }
//
//}
