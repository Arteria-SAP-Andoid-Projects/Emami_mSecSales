package com.arteriatech.emami.store;

import android.os.Bundle;
import android.util.Log;

import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.mutils.store.OnlineRequestListeners;
import com.sap.smp.client.odata.ODataEntitySet;
import com.sap.smp.client.odata.ODataError;
import com.sap.smp.client.odata.ODataPayload;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.smp.client.odata.store.ODataRequestListener;
import com.sap.smp.client.odata.store.ODataResponseSingle;

import java.util.List;

/**
 * Created by e10526 on 22-03-2018.
 */

public class OnlineCacheListner implements ODataRequestListener {
    private OnlineODataInterface onlineODataInterface;
    private String TAG = OnlineRequestListeners.class.getSimpleName();
    private Bundle bundle = null;

    public OnlineCacheListner(OnlineODataInterface onlineODataInterface, Bundle bundle) {
        this.onlineODataInterface = onlineODataInterface;
        this.bundle = bundle;
    }

    public void requestStarted(ODataRequestExecution oDataRequestExecution) {
        Log.d(this.TAG, "requestStarted: ");
        TraceLog.scoped(this).d("requestStarted");
    }

    public void requestCacheResponse(ODataRequestExecution oDataRequestExecution) {
        Log.d(this.TAG, "requestCacheResponse: ");
        TraceLog.scoped(this).d("requestCacheResponse");
        if (this.onlineODataInterface != null && this.bundle != null && this.bundle.getBoolean("readFromTechnicalCacheBundle", false)) {
            List entities = null;

            try {
                ODataEntitySet payload = (ODataEntitySet) ((ODataResponseSingle) oDataRequestExecution.getResponse()).getPayload();
                entities = payload.getEntities();
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            this.onlineODataInterface.responseSuccess(oDataRequestExecution, entities, this.bundle);
        }

    }

    public void requestServerResponse(ODataRequestExecution oDataRequestExecution) {
        Log.d(this.TAG, "requestServerResponse: ");
        TraceLog.scoped(this).d("requestServerResponse");
        List entities = null;

        try {
            ODataEntitySet payload = (ODataEntitySet) ((ODataResponseSingle) oDataRequestExecution.getResponse()).getPayload();
            entities = payload.getEntities();
        } catch (Exception var4) {
            var4.printStackTrace();
        }

        boolean isFromTechnicalCache = false;
        if (this.bundle != null && this.bundle.getBoolean("readFromTechnicalCacheBundle", false)) {
            isFromTechnicalCache = true;
        }

        if (this.onlineODataInterface != null && !isFromTechnicalCache) {
            this.onlineODataInterface.responseSuccess(oDataRequestExecution, entities, this.bundle);
        }

    }

    public void requestFailed(ODataRequestExecution request, ODataException e) {
        Log.d(this.TAG, "requestFailed: ");
        TraceLog.scoped(this).d("requestFailed");
        String resourcePath = "";
        if (this.bundle != null) {
            resourcePath = this.bundle.getString("bundle", "");
        }

        LogManager.writeLogError("requestFailed :" + e.getMessage() + " " + resourcePath);
        String errorMsg = e.getMessage();
        if (request != null && request.getResponse() != null && !request.getResponse().isBatch()) {
            ODataPayload payload = ((ODataResponseSingle) request.getResponse()).getPayload();
            if (payload != null && payload instanceof ODataError) {
                ODataError oError = (ODataError) payload;
                TraceLog.d("requestFailed - status message " + oError.getMessage());
                LogManager.writeLogError("Error :" + oError.getMessage());
                errorMsg = oError.getMessage();
            }
        }

        if (this.onlineODataInterface != null) {
            this.onlineODataInterface.responseFailed(request, errorMsg, this.bundle);
        }

    }

    public void requestFinished(ODataRequestExecution oDataRequestExecution) {
        Log.d(this.TAG, "requestFinished: ");
        TraceLog.scoped(this).d("requestFinished");
    }
}
