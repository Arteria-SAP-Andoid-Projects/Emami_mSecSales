package com.arteriatech.emami.store;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.emami.common.Constants;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataEntitySet;
import com.sap.smp.client.odata.ODataError;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataNavigationProperty;
import com.sap.smp.client.odata.ODataPayload;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.smp.client.odata.store.ODataRequestListener;
import com.sap.smp.client.odata.store.ODataRequestParamSingle;
import com.sap.smp.client.odata.store.ODataResponse;
import com.sap.smp.client.odata.store.ODataResponse.Headers;
import com.sap.smp.client.odata.store.ODataResponseBatch;
import com.sap.smp.client.odata.store.ODataResponseBatchItem;
import com.sap.smp.client.odata.store.ODataResponseSingle;
import com.sap.smp.client.odata.store.impl.ODataRequestParamSingleDefaultImpl;
import com.sap.smp.client.odata.store.impl.ODataResponseChangeSetDefaultImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.arteriatech.emami.common.Constants.getCollectionSuccessMsg;

public class OnlineRequestListener implements ODataRequestListener {

    private UIListener uiListener;
    private String autoSync;
    private int operation;

    private final int SUCCESS = 0;
    private final int ERROR = -1;

    private Handler uiHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == SUCCESS) {
                // Notify the Activity the is complete
                String key = (String) msg.obj;
                TraceLog.d(Constants.Requestsuccess_status_message_key + key);
                try {

                    uiListener.onRequestSuccess(operation, key);

                } catch (ODataException e) {
                    e.printStackTrace();
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            } else if (msg.what == ERROR) {
                Exception e = (Exception) msg.obj;

                uiListener.onRequestError(operation, e);

            }
        }
    };

    public OnlineRequestListener(int operation, UIListener uiListener) {
        super();
        this.operation = operation;
        this.uiListener = uiListener;
    }

    public OnlineRequestListener(int operation, String autoSync) {
        super();
        this.operation = operation;
        this.autoSync = autoSync;
    }

    /*****************
     * Methods that implements ODataRequestListener interface
     *****************/

    @Override
    public void requestCacheResponse(ODataRequestExecution request) {
        TraceLog.scoped(this).d(Constants.RequestCacheResponse);

        ODataProperty property;
        ODataPropMap properties;
        //Verify request’s response is not null. Request is always not null
        if (request.getResponse() != null) {
            //Parse the response
            ODataResponseSingle response = (ODataResponseSingle) request.getResponse();
            if (response != null) {
                //Get the response payload
                ODataEntitySet feed = (ODataEntitySet) response.getPayload();
                if (feed != null) {
                    //Get the list of ODataEntity
                    List<ODataEntity> entities = feed.getEntities();
                    //Loop to retrieve the information from the response
                    for (ODataEntity entity : entities) {
                        //Obtain the properties you want to display in the screen
                        properties = entity.getProperties();
                        property = properties.get("");
                    }
                    //TODO - Send content to the screen
                }
            }
        }

    }


    @Override
    public void requestFailed(ODataRequestExecution request, ODataException e) {
        TraceLog.scoped(this).d(Constants.RequestFailed);
        if (request != null && request.getResponse() != null) {
            ODataPayload payload = ((ODataResponseSingle) request.getResponse()).getPayload();
            if (payload != null && payload instanceof ODataError) {
                ODataError oError = (ODataError) payload;
                TraceLog.d(Constants.RequestFailed_status_message + oError.getMessage());
                try {
                    ODataRequestParamSingle oDataResponseSingle = (ODataRequestParamSingleDefaultImpl) request.getRequest();
                    ODataEntity oDataEntity = (ODataEntity) oDataResponseSingle.getPayload();
                    Constants.Entity_Set.add(oDataEntity.getResourcePath());
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
                LogManager.writeLogError(Constants.Error + " :" + oError.getMessage());
                Constants.AL_ERROR_MSG.add(oError.getMessage());
                notifyErrorToListener(new OnlineODataStoreException(oError.getMessage()));
                return;
            }
        }
        notifyErrorToListener(e);
    }

    @Override
    public void requestFinished(ODataRequestExecution request) {
        TraceLog.scoped(this).d(Constants.RequestFinished);
    }

    @Override
    public void requestServerResponse(ODataRequestExecution request) {
        TraceLog.scoped(this).d(Constants.RequestServerResponse);
        if (request != null && request.getResponse() != null) {
            if (request.getResponse().isBatch()) {
                try {
                    ODataResponse oDataResponse = request.getResponse();
                    if (oDataResponse != null) {

                        ODataResponseBatch batchResponse = (ODataResponseBatch) oDataResponse;

                        List<ODataResponseBatchItem> responses = batchResponse.getResponses();

                        for (ODataResponseBatchItem response : responses) {

                            if (response instanceof ODataResponseChangeSetDefaultImpl) {

                                ODataResponseChangeSetDefaultImpl changesetResponse = (ODataResponseChangeSetDefaultImpl) response;

                                List<ODataResponseSingle> singles = changesetResponse.getResponses();

                                for (ODataResponseSingle singleResponse : singles) {
                                    // Get individual response

                                    ODataPayload payload = singleResponse.getPayload();
                                    if (payload != null) {

                                        if (payload instanceof ODataError) {

                                            ODataError oError = (ODataError) payload;

                                            notifyErrorToListener(new OnlineODataStoreException(oError.getMessage()));
                                            return;
                                        } else {
                                            TraceLog.d("requestsuccess - status message before success");
                                            notifySuccessToListener(null);
                                            return;
                                        }
                                    } else {
                                        TraceLog.d("requestsuccess - status message before success");
                                        notifySuccessToListener(null);
                                        return;
                                    }
                                }
                            }

                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ODataResponseSingle response = (ODataResponseSingle) request.getResponse();
                Map<Headers, String> headerMap = response.getHeaders();
                String code = headerMap.get(Headers.Code);
                TraceLog.d(Constants.RequestServerResponseStatusCode + code);
                String eTag = headerMap.get(Headers.ETag);
                if (!TextUtils.isEmpty(eTag)) {
                    notifySuccessToListener(eTag);
                    return;
                } else {


                    ODataPayload payload = ((ODataResponseSingle) request.getResponse()).getPayload();
                    if (payload != null && payload instanceof ODataEntity) {
                        ODataEntity oEntity = (ODataEntity) payload;
                        ODataPropMap properties = oEntity.getProperties();
                        ODataProperty property = null;
                        if (oEntity.getEntityType().endsWith(Constants.InvoiceEntity)) {

                            ODataNavigationProperty navProp = oEntity.getNavigationProperty(Constants.SSInvoiceItemDetails);
                            properties = oEntity.getProperties();
                            property = properties.get(Constants.InvoiceNo);
                            Constants.InvoiceNumber = property.getValue().toString();

                            property = properties.get(Constants.NetAmount);

                            if (property != null) {
                                BigDecimal mUnitPrice = (BigDecimal) property.getValue();
                                Constants.InvoiceTotalAmount = mUnitPrice.doubleValue();
                            }

                            property = properties.get(Constants.UnitPrice);

                            if (property != null) {
                                BigDecimal mUnitPrice = (BigDecimal) property.getValue();
                                Constants.MaterialUnitPrice = mUnitPrice.doubleValue();
                            }


                            if (navProp.getNavigationType().toString().equalsIgnoreCase(Constants.EntitySet)) {
                                try {
                                    ODataEntitySet feed = (ODataEntitySet) navProp.getNavigationContent();
                                    List<ODataEntity> entities = feed.getEntities();
                                    for (ODataEntity entity : entities) {
                                        properties = entity.getProperties();
                                        property = properties.get(Constants.UnitPrice);

                                        if (property != null) {
                                            BigDecimal mUnitPrice = (BigDecimal) property.getValue();
                                            Constants.MaterialUnitPrice = mUnitPrice.doubleValue();
                                        }

                                        property = properties.get(Constants.NetAmount);

                                        if (property != null) {
                                            BigDecimal mGrossAmt = (BigDecimal) property.getValue();
                                            Constants.MaterialNetAmount = mGrossAmt.doubleValue();
                                        }


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } else if (oEntity.getEntityType().endsWith(Constants.FinancialPostingsEntity)) {

                            Constants.VisitActivityRefID = null;
                            properties = oEntity.getProperties();
                            property = properties.get(Constants.FIPDocNo);
                            Constants.FIPDocumentNumber = property.getValue().toString();
                            property = properties.get(Constants.FIPGUID);
                            Constants.VisitActivityRefID = (ODataGuid) property.getValue();

                            String popUpText = getCollectionSuccessMsg(Constants.FIPDocumentNumber);


                            LogManager.writeLogInfo(popUpText);

                        } else if (oEntity.getEntityType().endsWith(Constants.FeedbackEntity)) {
                            property = properties.get(Constants.FeebackGUID);

                            String popUpText = Constants.FeedbackCreated;

                            LogManager.writeLogInfo(popUpText);
                        } else if (oEntity.getEntityType().endsWith(Constants.SalesOrderEntity)) {
                            property = properties.get(Constants.OrderNo);

                            String popUpText = Constants.getSOSuccessMsg(property.getValue().toString());

                            LogManager.writeLogInfo(popUpText);
                        } else if (oEntity.getEntityType().endsWith(Constants.ReturnOrderEntity)) {
                            property = properties.get(Constants.OrderNo);

                            String popUpText = Constants.getROSuccessMsg(property.getValue().toString());

                            LogManager.writeLogInfo(popUpText);
                        }

                        notifySuccessToListener("");
                        return;
                    }
                }
            }

        }
        TraceLog.d(Constants.RequestsuccessStatusMessageBeforeSuccess);
        notifySuccessToListener(null);
    }

    @Override
    public void requestStarted(ODataRequestExecution request) {
        TraceLog.scoped(this).d(Constants.RequestStarted);
    }


    /*****************
     * Utils Methods
     *****************/


    /**
     * Notify the OnlineUIListener that the request was successful.
     */
    protected void notifySuccessToListener(String key) {
        Message msg = uiHandler.obtainMessage();
        msg.what = SUCCESS;
        msg.obj = key;
        uiHandler.sendMessage(msg);
    }

    /**
     * Notify the OnlineUIListener that the request has an error.
     *
     * @param exception an Exception that denotes the error that occurred.
     */
    protected void notifyErrorToListener(Exception exception) {
        Message msg = uiHandler.obtainMessage();
        msg.what = ERROR;
        msg.obj = exception;
        uiHandler.sendMessage(msg);
        TraceLog.e(Constants.OnlineRequestListenerNotifyError, exception);
    }

}
