package com.arteriatech.emami.store;

import android.content.Context;

import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.finance.InvoiceBean;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreContext;
import com.sap.maf.tools.logon.logonui.api.LogonUIFacade;
import com.sap.smp.client.httpc.HttpConversationManager;
import com.sap.smp.client.httpc.IManagerConfigurator;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataNavigationProperty;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataContractViolationException;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.exception.ODataParserException;
import com.sap.smp.client.odata.impl.ODataEntityDefaultImpl;
import com.sap.smp.client.odata.impl.ODataEntitySetDefaultImpl;
import com.sap.smp.client.odata.impl.ODataGuidDefaultImpl;
import com.sap.smp.client.odata.impl.ODataPropertyDefaultImpl;
import com.sap.smp.client.odata.online.OnlineODataStore;
import com.sap.smp.client.odata.store.ODataDownloadMediaExecution;
import com.sap.smp.client.odata.store.ODataDownloadMediaListener;
import com.sap.smp.client.odata.store.ODataDownloadMediaResult;
import com.sap.smp.client.odata.store.ODataRequestChangeSet;
import com.sap.smp.client.odata.store.ODataRequestParamBatch;
import com.sap.smp.client.odata.store.ODataRequestParamSingle;
import com.sap.smp.client.odata.store.ODataStore;
import com.sap.smp.client.odata.store.ODataStore.PropMode;
import com.sap.smp.client.odata.store.impl.ODataDownloadMediaResultDefaultImpl;
import com.sap.smp.client.odata.store.impl.ODataRequestChangeSetDefaultImpl;
import com.sap.smp.client.odata.store.impl.ODataRequestParamBatchDefaultImpl;
import com.sap.smp.client.odata.store.impl.ODataRequestParamSingleDefaultImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class OnlineManager {
    public static final String TAG = OnlineManager.class.getSimpleName();

    /**
     * Initialize an online OData store for online access
     *
     * @param context used only to access the application context
     * @return true if the online is open and false otherwise
     * @throws OnlineODataStoreException
     */
    public static boolean openOnlineStore(Context context) throws OnlineODataStoreException {
        //OnlineOpenListener implements OpenListener interface
        //Listener to be invoked when the opening process of an OnlineODataStore object finishes
        try {
            Constants.printLogInfo("Get online store instance");
            OnlineStoreListener openListener = OnlineStoreListener.getInstance();
            Constants.printLogInfo("online store instance assigned");
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
            Constants.printLogInfo("logon core context instance assigned :" + lgCtx);

            //The logon configurator uses the information obtained in the registration
            IManagerConfigurator configurator = LogonUIFacade.getInstance().getLogonConfigurator(context);
            HttpConversationManager manager = new HttpConversationManager(context);
            configurator.configure(manager);

//            OnlineODataStore.OnlineStoreOptions onlineOptions =  new OnlineODataStore.OnlineStoreOptions(OnlineODataStore.PayloadFormatEnum.XML);
//            onlineOptions.useCache = true;//if true technical cache is enabled
//            onlineOptions.cacheEncryptionKey = Constants.EncryptKey;

            //XCSRFTokenRequestFilter implements IRequestFilter
            //Request filter that is allowed to preprocess the request before sending
            XCSRFTokenRequestFilter requestFilter = XCSRFTokenRequestFilter.getInstance(lgCtx);
            XCSRFTokenResponseFilter responseFilter = XCSRFTokenResponseFilter.getInstance(context,
                    requestFilter);
            manager.addFilter(requestFilter);
            manager.addFilter(responseFilter);

            try {
                String endPointURL = lgCtx.getAppEndPointUrl();
                Constants.printLogInfo("end point url " + endPointURL);
                Constants.printLogInfo("appid " + lgCtx.getAppId());
                Constants.printLogInfo("connection id " + lgCtx.getConnId());
                Constants.printLogInfo("resource path " + lgCtx.getResourcePath());
                Constants.printLogInfo("backend user " + lgCtx.getBackendUser());
                Constants.printLogInfo("host name " + lgCtx.getHost());


                URL url = new URL(endPointURL);
                //Method to open a new online store asynchronously
                Constants.printLogInfo("request for open online store");
                OnlineODataStore.open(context, url, manager, openListener, null);

                Constants.printLogInfo("request for open online store completed");
                //            openListener.waitForCompletion();

                if (openListener.getError() != null) {
                    Constants.printLog("open online store ended with error");
                    throw openListener.getError();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Constants.printLog("open online store ended with exception " + e.getMessage());
                throw new OnlineODataStoreException(e);
            }
            //Check if OnlineODataStore opened successfully

            while (!Constants.IsOnlineStoreFailed) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Constants.IsOnlineStoreFailed = false;


            if (Constants.onlineStore != null) {
                return true;
            } else {
                return false;
            }
        } catch (OnlineODataStoreException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Create Entity for collection creation and Schedule in Online Manager
     *
     * @throws OnlineODataStoreException
     */
    public static void createCollectionEntry(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity collectionCreateEntity = createCollectionEntryEntity(table, itemtable, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                String fipGUID32 = table.get(Constants.FIPGUID).replace("-", "");

                String collCreatedOn = table.get(Constants.CreatedOn);
                String collCreatedAt = table.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(collCreatedOn) + Constants.T + UtilConstants.convertTimeOnly(collCreatedAt);

                Map<String, String> createHeaders = new HashMap<String, String>();
                createHeaders.put(Constants.RequestID, fipGUID32);
                createHeaders.put(Constants.RepeatabilityCreation, mStrDateTime);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(collectionCreateEntity.getResourcePath());
                collectionReq.setPayload(collectionCreateEntity);
                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
        //END

    }

    /**
     * Create Entity for collection creation
     *
     * @throws ODataParserException
     */
    private static ODataEntity createCollectionEntryEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.FinancialPostingsEntity);

                newHeaderEntity.setResourcePath(Constants.FinancialPostings, Constants.FinancialPostings);

                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                newHeaderEntity.getProperties().put(Constants.FIPGUID,
                        new ODataPropertyDefaultImpl(Constants.FIPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.FIPGUID))));

                try {
                    newHeaderEntity.getProperties().put(Constants.ExtRefID,
                            new ODataPropertyDefaultImpl(Constants.ExtRefID, hashtable.get(Constants.FIPGUID).replace("-", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CPGUID))));

                try {
                    newHeaderEntity.getProperties().put(Constants.SPGUID,
                            new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGuid))));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                newHeaderEntity.getProperties().put(Constants.CPNo,
                        new ODataPropertyDefaultImpl(Constants.CPNo, hashtable.get(Constants.CPNo)));
                if (!hashtable.get(Constants.BankID).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.BankID,
                            new ODataPropertyDefaultImpl(Constants.BankID, hashtable.get(Constants.BankID)));
                }
                if (!hashtable.get(Constants.InstrumentNo).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.InstrumentNo,
                            new ODataPropertyDefaultImpl(Constants.InstrumentNo, hashtable.get(Constants.InstrumentNo)));
                }

                if (!hashtable.get(Constants.InstrumentDate).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.InstrumentDate,
                            new ODataPropertyDefaultImpl(Constants.InstrumentDate, UtilConstants.convertDateFormat(hashtable.get(Constants.InstrumentDate))));
                }
                newHeaderEntity.getProperties().put(Constants.Amount,
                        new ODataPropertyDefaultImpl(Constants.Amount, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.Amount)))));

                if (!hashtable.get(Constants.Remarks).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.Remarks,
                            new ODataPropertyDefaultImpl(Constants.Remarks, hashtable.get(Constants.Remarks)));
                }
                newHeaderEntity.getProperties().put(Constants.FIPDocType,
                        new ODataPropertyDefaultImpl(Constants.FIPDocType, hashtable.get(Constants.FIPDocType)));

                try {
                    newHeaderEntity.getProperties().put(Constants.Source,
                            new ODataPropertyDefaultImpl(Constants.Source, hashtable.get(Constants.Source)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                newHeaderEntity.getProperties().put(Constants.PaymentModeID,
                        new ODataPropertyDefaultImpl(Constants.PaymentModeID, hashtable.get(Constants.PaymentModeID)));

                newHeaderEntity.getProperties().put(Constants.FIPDate,
                        new ODataPropertyDefaultImpl(Constants.FIPDate, UtilConstants.convertDateFormat(hashtable.get(Constants.FIPDate))));

          /*      newHeaderEntity.getProperties().put(Constants.LOGINID,
                        new ODataPropertyDefaultImpl(Constants.LOGINID, hashtable.get(Constants.LOGINID)));*/
                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));
                newHeaderEntity.getProperties().put(Constants.BranchName,
                        new ODataPropertyDefaultImpl(Constants.BranchName, hashtable.get(Constants.BranchName)));
                newHeaderEntity.getProperties().put(Constants.CPName,
                        new ODataPropertyDefaultImpl(Constants.CPName, hashtable.get(Constants.CPName)));
                newHeaderEntity.getProperties().put(Constants.ParentNo,
                        new ODataPropertyDefaultImpl(Constants.ParentNo, hashtable.get(Constants.ParentNo)));
                newHeaderEntity.getProperties().put(Constants.SPNo,
                        new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));
                newHeaderEntity.getProperties().put(Constants.SPFirstName,
                        new ODataPropertyDefaultImpl(Constants.SPFirstName, hashtable.get(Constants.SPFirstName)));

                newHeaderEntity.getProperties().put(Constants.CPTypeID,
                        new ODataPropertyDefaultImpl(Constants.CPTypeID, hashtable.get(Constants.CPTypeID)));

                try {
                    if (!hashtable.get(Constants.BeatGUID).equalsIgnoreCase("")) {
                        newHeaderEntity.getProperties().put(Constants.BeatGUID,
                                new ODataPropertyDefaultImpl(Constants.BeatGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.BeatGUID))));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                int incremntVal = 0;
                for (int i = 0; i < itemhashtable.size(); i++) {

                    HashMap<String, String> singleRow = itemhashtable.get(i);

                    incremntVal = i + 1;

                    newItemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.FinancialPostingsItemEntity);

                    newItemEntity.setResourcePath(Constants.FinancialPostingItemDetails + "(" + incremntVal + ")", Constants.FinancialPostingItemDetails + "(" + incremntVal + ")");
                    try {
                        store.allocateProperties(newItemEntity, PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }


                    newItemEntity.getProperties().put(Constants.FIPItemGUID,
                            new ODataPropertyDefaultImpl(Constants.FIPItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.FIPItemGUID))));

                    newItemEntity.getProperties().put(Constants.FIPGUID,
                            new ODataPropertyDefaultImpl(Constants.FIPGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.FIPGUID))));

                    newItemEntity.getProperties().put(Constants.FIPItemNo,
                            new ODataPropertyDefaultImpl(Constants.FIPItemNo, incremntVal + ""));


                    newItemEntity.getProperties().put(Constants.ReferenceTypeID,
                            new ODataPropertyDefaultImpl(Constants.ReferenceTypeID, singleRow.get(Constants.ReferenceTypeID)));

/*
                    newItemEntity.getProperties().put(Constants.LOGINID,
                            new ODataPropertyDefaultImpl(Constants.LOGINID, hashtable.get(Constants.LOGINID)));*/


                    if (!singleRow.get(Constants.ReferenceID).equalsIgnoreCase("")) {
                        newItemEntity.getProperties().put(Constants.FIPAmount,
                                new ODataPropertyDefaultImpl(Constants.FIPAmount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.FIPAmount)))));
                        newItemEntity.getProperties().put(Constants.ReferenceID,
                                new ODataPropertyDefaultImpl(Constants.ReferenceID, singleRow.get(Constants.ReferenceID).toUpperCase()));
                        newItemEntity.getProperties().put(Constants.Amount,
                                new ODataPropertyDefaultImpl(Constants.Amount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Amount)))));
                    } else {
                        newItemEntity.getProperties().put(Constants.FIPAmount,
                                new ODataPropertyDefaultImpl(Constants.FIPAmount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.FIPAmount)))));
                    }

                    if (!singleRow.get(Constants.InstrumentDate).equalsIgnoreCase("")) {
                        newItemEntity.getProperties().put(Constants.InstrumentDate,
                                new ODataPropertyDefaultImpl(Constants.InstrumentDate, UtilConstants.convertDateFormat(singleRow.get(Constants.InstrumentDate))));
                    }

                    newItemEntity.getProperties().put(Constants.Currency,
                            new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));

                    newItemEntity.getProperties().put(Constants.InstrumentNo,
                            new ODataPropertyDefaultImpl(Constants.InstrumentNo, singleRow.get(Constants.InstrumentNo)));

                    newItemEntity.getProperties().put(Constants.PaymentMode,
                            new ODataPropertyDefaultImpl(Constants.PaymentMode, singleRow.get(Constants.PaymentModeID)));

                    newItemEntity.getProperties().put(Constants.PaymetModeDesc,
                            new ODataPropertyDefaultImpl(Constants.PaymetModeDesc, singleRow.get(Constants.PaymetModeDesc)));

                   /* try {
                        if (!singleRow.get(Constants.BeatGUID).equalsIgnoreCase("")) {
                            newItemEntity.getProperties().put(Constants.BeatGUID,
                                    new ODataPropertyDefaultImpl(Constants.BeatGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.BeatGUID))));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/


                    tempArray.add(i, newItemEntity);

                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.FinancialPostingItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.FinancialPostingItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.FinancialPostingItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }

    /**
     * Create Entity for collection creation and Schedule in Online Manager
     *
     * @throws OnlineODataStoreException
     */
    public static void createROEntity(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity soCreateEntity = createROCreateEntity(table, itemtable, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                String ssoGUID32 = table.get(Constants.SSROGUID).replace("-", "");

                String soCreatedOn = table.get(Constants.CreatedOn);
                String soCreatedAt = table.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(soCreatedOn) + Constants.T + UtilConstants.convertTimeOnly(soCreatedAt);

                Map<String, String> createHeaders = new HashMap<String, String>();
                createHeaders.put(Constants.RequestID, ssoGUID32);
                createHeaders.put(Constants.RepeatabilityCreation, mStrDateTime);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);
                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
        //END

    }

    /**
     * Create Entity for collection creation
     *
     * @throws ODataParserException
     */
    private static ODataEntity createROCreateEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.ReturnOrderEntity);

                newHeaderEntity.setResourcePath(Constants.SSROs, Constants.SSROs);

                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                newHeaderEntity.getProperties().put(Constants.SSROGUID,
                        new ODataPropertyDefaultImpl(Constants.SSROGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SSROGUID))));
//                newHeaderEntity.getProperties().put(Constants.OrderNo,
//                        new ODataPropertyDefaultImpl(Constants.OrderNo, hashtable.get(Constants.OrderNo)));
                newHeaderEntity.getProperties().put(Constants.OrderType,
                        new ODataPropertyDefaultImpl(Constants.OrderType, hashtable.get(Constants.OrderType)));
                newHeaderEntity.getProperties().put(Constants.OrderTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.OrderTypeDesc, hashtable.get(Constants.OrderTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.OrderDate,
                        new ODataPropertyDefaultImpl(Constants.OrderDate, UtilConstants.convertDateFormat(hashtable.get(Constants.OrderDate))));
                newHeaderEntity.getProperties().put(Constants.DmsDivision,
                        new ODataPropertyDefaultImpl(Constants.DmsDivision, hashtable.get(Constants.DmsDivision)));
                newHeaderEntity.getProperties().put(Constants.DmsDivisionDesc,
                        new ODataPropertyDefaultImpl(Constants.DmsDivisionDesc, hashtable.get(Constants.DmsDivisionDesc)));

                newHeaderEntity.getProperties().put(Constants.FromCPGUID,
                        new ODataPropertyDefaultImpl(Constants.FromCPGUID, hashtable.get(Constants.FromCPGUID).replace("-", "")));
//                newHeaderEntity.getProperties().put(Constants.FromCPNo,
//                        new ODataPropertyDefaultImpl(Constants.FromCPNo, hashtable.get(Constants.FromCPNo)));
                newHeaderEntity.getProperties().put(Constants.FromCPName,
                        new ODataPropertyDefaultImpl(Constants.FromCPName, hashtable.get(Constants.FromCPName)));
//                newHeaderEntity.getProperties().put("FromCPTypID",
//                        new ODataPropertyDefaultImpl("FromCPTypID", hashtable.get(Constants.FromCPTypId)));
                newHeaderEntity.getProperties().put(Constants.FromCPTypDs,
                        new ODataPropertyDefaultImpl(Constants.FromCPTypDs, hashtable.get(Constants.FromCPTypId)));


                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CPGUID))));
                newHeaderEntity.getProperties().put(Constants.CPNo,
                        new ODataPropertyDefaultImpl(Constants.CPNo, hashtable.get(Constants.CPNo)));
                newHeaderEntity.getProperties().put(Constants.CPName,
                        new ODataPropertyDefaultImpl(Constants.CPName, hashtable.get(Constants.CPName)));
                newHeaderEntity.getProperties().put(Constants.CPTypeID,
                        new ODataPropertyDefaultImpl(Constants.CPTypeID, hashtable.get(Constants.CPTypeID)));
                newHeaderEntity.getProperties().put(Constants.CPTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.CPTypeDesc, hashtable.get(Constants.CPTypeDesc)));


                newHeaderEntity.getProperties().put(Constants.SoldToCPGUID,
                        new ODataPropertyDefaultImpl(Constants.SoldToCPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SoldToCPGUID))));
                newHeaderEntity.getProperties().put(Constants.SoldToID,
                        new ODataPropertyDefaultImpl(Constants.SoldToID, hashtable.get(Constants.SoldToID)));
      /*          newHeaderEntity.getProperties().put(Constants.SoldToUID,
                        new ODataPropertyDefaultImpl(Constants.SoldToUID, hashtable.get(Constants.SoldToUID)));*/
                newHeaderEntity.getProperties().put(Constants.SoldToDesc,
                        new ODataPropertyDefaultImpl(Constants.SoldToDesc, hashtable.get(Constants.SoldToDesc)));

                newHeaderEntity.getProperties().put(Constants.SoldToTypeID,
                        new ODataPropertyDefaultImpl(Constants.SoldToTypeID, hashtable.get(Constants.SoldToTypeID)));
                newHeaderEntity.getProperties().put(Constants.SoldToTypDs,
                        new ODataPropertyDefaultImpl(Constants.SoldToTypDs, hashtable.get(Constants.SoldToTypDesc)));


                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));


                newHeaderEntity.getProperties().put(Constants.SPGUID,
                        new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID))));
                newHeaderEntity.getProperties().put(Constants.SPNo,
                        new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));
                newHeaderEntity.getProperties().put(Constants.FirstName,
                        new ODataPropertyDefaultImpl(Constants.FirstName, hashtable.get(Constants.FirstName)));
             /*   newHeaderEntity.getProperties().put(Constants.LOGINID,
                        new ODataPropertyDefaultImpl(Constants.LOGINID, hashtable.get(Constants.LOGINID)));*/
                newHeaderEntity.getProperties().put(Constants.StatusID,
                        new ODataPropertyDefaultImpl(Constants.StatusID, hashtable.get(Constants.StatusID)));
                newHeaderEntity.getProperties().put(Constants.ApprovalStatusID,
                        new ODataPropertyDefaultImpl(Constants.ApprovalStatusID, hashtable.get(Constants.ApprovalStatusID)));

                newHeaderEntity.getProperties().put(Constants.TestRun,
                        new ODataPropertyDefaultImpl(Constants.TestRun, hashtable.get(Constants.TestRun)));


                int incremntVal = 0;
                for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {

                    HashMap<String, String> singleRow = itemhashtable.get(incrementVal);

                    incremntVal = incrementVal + 1;

                    newItemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.ReturnOrderItemEntity);

                    newItemEntity.setResourcePath(Constants.SSROItemDetails + "(" + incremntVal + ")", Constants.SSROItemDetails + "(" + incremntVal + ")");
                    try {
                        store.allocateProperties(newItemEntity, PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }


                    newItemEntity.getProperties().put(Constants.SSROItemGUID,
                            new ODataPropertyDefaultImpl(Constants.SSROItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.SSROItemGUID))));

                    newItemEntity.getProperties().put(Constants.SSROGUID,
                            new ODataPropertyDefaultImpl(Constants.SSROGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.SSROGUID))));

                    newItemEntity.getProperties().put(Constants.ItemNo,
                            new ODataPropertyDefaultImpl(Constants.ItemNo, singleRow.get(Constants.ItemNo)));


                    newItemEntity.getProperties().put(Constants.MaterialNo,
                            new ODataPropertyDefaultImpl(Constants.MaterialNo, singleRow.get(Constants.MaterialNo)));

                    newItemEntity.getProperties().put(Constants.MaterialDesc,
                            new ODataPropertyDefaultImpl(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc)));

                    newItemEntity.getProperties().put(Constants.OrdMatGrp,
                            new ODataPropertyDefaultImpl(Constants.OrdMatGrp, singleRow.get(Constants.OrdMatGrp)));
//
                    newItemEntity.getProperties().put(Constants.OrdMatGrpDesc,
                            new ODataPropertyDefaultImpl(Constants.OrdMatGrpDesc, singleRow.get(Constants.OrdMatGrpDesc)));
//
                    newItemEntity.getProperties().put(Constants.Quantity,
                            new ODataPropertyDefaultImpl(Constants.Quantity, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Quantity)))));

                    newItemEntity.getProperties().put(Constants.MRP,
                            new ODataPropertyDefaultImpl(Constants.MRP, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.MRP)))));


                    newItemEntity.getProperties().put(Constants.Currency,
                            new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));

                    newItemEntity.getProperties().put(Constants.Batch,
                            new ODataPropertyDefaultImpl(Constants.Batch, singleRow.get(Constants.Batch)));
                    newItemEntity.getProperties().put(Constants.RejectionReasonID,
                            new ODataPropertyDefaultImpl(Constants.RejectionReasonID, singleRow.get(Constants.RejectionReasonID)));
                    newItemEntity.getProperties().put(Constants.RejectionReasonDesc,
                            new ODataPropertyDefaultImpl(Constants.RejectionReasonDesc, singleRow.get(Constants.RejectionReasonDesc)));


                    tempArray.add(incrementVal, newItemEntity);

                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.SSROItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.SSROItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.SSROItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }


    /**
     * Create Entity for collection creation and Schedule in Online Manager
     *
     * @throws OnlineODataStoreException
     */
    public static void createSOEntity(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity soCreateEntity = createSOCreateEntity(table, itemtable, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                String ssoGUID32 = table.get(Constants.SSSOGuid).replace("-", "");

                String soCreatedOn = table.get(Constants.CreatedOn);
                String soCreatedAt = table.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(soCreatedOn) + Constants.T + UtilConstants.convertTimeOnly(soCreatedAt);

                Map<String, String> createHeaders = new HashMap<String, String>();
                createHeaders.put(Constants.RequestID, ssoGUID32);
                createHeaders.put(Constants.RepeatabilityCreation, mStrDateTime);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);
                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
        //END

    }

    /**
     * Create Entity for collection creation
     *
     * @throws ODataParserException
     */
    private static ODataEntity createSOCreateEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.SalesOrderEntity);

                newHeaderEntity.setResourcePath(Constants.SSSOs, Constants.SSSOs);

                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);
                try {
                    newHeaderEntity.getProperties().put(Constants.BeatGuid,
                            new ODataPropertyDefaultImpl(Constants.BeatGuid, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.BeatGuid))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                newHeaderEntity.getProperties().put(Constants.SSSOGuid,
                        new ODataPropertyDefaultImpl(Constants.SSSOGuid, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SSSOGuid))));
                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CPGUID))));

                newHeaderEntity.getProperties().put(Constants.CPNo,
                        new ODataPropertyDefaultImpl(Constants.CPNo, hashtable.get(Constants.CPNo)));
                newHeaderEntity.getProperties().put(Constants.CPName,
                        new ODataPropertyDefaultImpl(Constants.CPName, hashtable.get(Constants.CPName)));
                newHeaderEntity.getProperties().put(Constants.CPType,
                        new ODataPropertyDefaultImpl(Constants.CPType, hashtable.get(Constants.CPType)));
                newHeaderEntity.getProperties().put(Constants.CPTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.CPTypeDesc, hashtable.get(Constants.CPTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.TestRun,
                        new ODataPropertyDefaultImpl(Constants.TestRun, hashtable.get(Constants.TestRun)));
                newHeaderEntity.getProperties().put(Constants.SoldToCPGUID,
                        new ODataPropertyDefaultImpl(Constants.SoldToCPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SoldToCPGUID))));
                newHeaderEntity.getProperties().put(Constants.SoldToId,
                        new ODataPropertyDefaultImpl(Constants.SoldToId, hashtable.get(Constants.SoldToId)));
                newHeaderEntity.getProperties().put(Constants.SoldToUID,
                        new ODataPropertyDefaultImpl(Constants.SoldToUID, hashtable.get(Constants.SoldToUID)));
               /* newHeaderEntity.getProperties().put(Constants.SoldToDesc,
                        new ODataPropertyDefaultImpl(Constants.SoldToDesc, hashtable.get(Constants.SoldToDesc)));*/
                newHeaderEntity.getProperties().put(Constants.SoldToType,
                        new ODataPropertyDefaultImpl(Constants.SoldToType, hashtable.get(Constants.SoldToType)));
                newHeaderEntity.getProperties().put(Constants.DmsDivision,
                        new ODataPropertyDefaultImpl(Constants.DmsDivision, hashtable.get(Constants.DmsDivision)));
                newHeaderEntity.getProperties().put(Constants.DmsDivisionDesc,
                        new ODataPropertyDefaultImpl(Constants.DmsDivisionDesc, hashtable.get(Constants.DmsDivisionDesc)));

                newHeaderEntity.getProperties().put(Constants.OrderType,
                        new ODataPropertyDefaultImpl(Constants.OrderType, hashtable.get(Constants.OrderType)));
                newHeaderEntity.getProperties().put(Constants.OrderTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.OrderTypeDesc, hashtable.get(Constants.OrderTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.OrderDate,
                        new ODataPropertyDefaultImpl(Constants.OrderDate, UtilConstants.convertDateFormat(hashtable.get(Constants.OrderDate))));

                newHeaderEntity.getProperties().put(Constants.GrossAmt,
                        new ODataPropertyDefaultImpl(Constants.GrossAmt, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.GrossAmt)))));
                newHeaderEntity.getProperties().put(Constants.NetPrice,
                        new ODataPropertyDefaultImpl(Constants.NetPrice, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.NetPrice)))));

               /* newHeaderEntity.getProperties().put(Constants.LOGINID,
                        new ODataPropertyDefaultImpl(Constants.LOGINID, hashtable.get(Constants.LOGINID)));*/

                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));

                newHeaderEntity.getProperties().put(Constants.FromCPGUID,
                        new ODataPropertyDefaultImpl(Constants.FromCPGUID, hashtable.get(Constants.FromCPGUID).replace("-", "")));
//                newHeaderEntity.getProperties().put(Constants.FromCPNo,
//                        new ODataPropertyDefaultImpl(Constants.FromCPNo, hashtable.get(Constants.FromCPNo)));
                newHeaderEntity.getProperties().put(Constants.FromCPName,
                        new ODataPropertyDefaultImpl(Constants.FromCPName, hashtable.get(Constants.FromCPName)));
                newHeaderEntity.getProperties().put(Constants.FromCPTypId,
                        new ODataPropertyDefaultImpl(Constants.FromCPTypId, hashtable.get(Constants.FromCPTypId)));

                if (!hashtable.get(Constants.SPGUID).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.SPGUID,
                            new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID)))
                    );
                    newHeaderEntity.getProperties().put(Constants.SPNo,
                            new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));
                }

                newHeaderEntity.getProperties().put(Constants.FirstName, new ODataPropertyDefaultImpl(Constants.FirstName, hashtable.get(Constants.FirstName)));


                newHeaderEntity.getProperties().put(Constants.Distance, new ODataPropertyDefaultImpl(Constants.Distance, hashtable.get(Constants.Distance)));
                newHeaderEntity.getProperties().put(Constants.Longitude, new ODataPropertyDefaultImpl(Constants.Longitude, hashtable.get(Constants.Longitude)));
                newHeaderEntity.getProperties().put(Constants.Latitude, new ODataPropertyDefaultImpl(Constants.Latitude, hashtable.get(Constants.Latitude)));
               // newHeaderEntity.getProperties().put(Constants.Remarks, new ODataPropertyDefaultImpl(Constants.Remarks, hashtable.get(Constants.Remarks)));

                /*try {
                    if(newHeaderEntity.getProperties().containsKey(Constants.BillToCPGUID)){
                        newHeaderEntity.getProperties().put(Constants.BillToCPGUID,
                                new ODataPropertyDefaultImpl(Constants.BillToCPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.BillToCPGUID))));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                newHeaderEntity.getProperties().put(Constants.OrderDate,
                        new ODataPropertyDefaultImpl(Constants.OrderDate, UtilConstants.convertDateFormat(hashtable.get(Constants.OrderDate))));


                int incremntVal = 0;
                for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {

                    HashMap<String, String> singleRow = itemhashtable.get(incrementVal);
                    if (!singleRow.get(Constants.IsfreeGoodsItem).equalsIgnoreCase("X")) {
                        incremntVal = incrementVal + 1;

                        newItemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.SalesOrderItemEntity);

                        newItemEntity.setResourcePath(Constants.SSSoItemDetails + "(" + incremntVal + ")", Constants.SSSoItemDetails + "(" + incremntVal + ")");
                        try {
                            store.allocateProperties(newItemEntity, PropMode.Keys);
                        } catch (ODataException e) {
                            e.printStackTrace();
                        }


                        newItemEntity.getProperties().put(Constants.SSSOItemGUID,
                                new ODataPropertyDefaultImpl(Constants.SSSOItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.SSSOItemGUID))));

                        newItemEntity.getProperties().put(Constants.SSSOGuid,
                                new ODataPropertyDefaultImpl(Constants.SSSOGuid, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.SSSOGuid))));

                        newItemEntity.getProperties().put(Constants.ItemNo,
                                new ODataPropertyDefaultImpl(Constants.ItemNo, singleRow.get(Constants.ItemNo)));


                        newItemEntity.getProperties().put(Constants.MaterialNo,
                                new ODataPropertyDefaultImpl(Constants.MaterialNo, singleRow.get(Constants.MaterialNo)));

                        newItemEntity.getProperties().put(Constants.MaterialDesc,
                                new ODataPropertyDefaultImpl(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc)));

                        newItemEntity.getProperties().put(Constants.OrderMatGrp,
                                new ODataPropertyDefaultImpl(Constants.OrderMatGrp, singleRow.get(Constants.OrderMatGrp)));

                        newItemEntity.getProperties().put(Constants.OrderMatGrpDesc,
                                new ODataPropertyDefaultImpl(Constants.OrderMatGrpDesc, singleRow.get(Constants.OrderMatGrpDesc)));

             /*           newItemEntity.getProperties().put(Constants.LoginId,
                                new ODataPropertyDefaultImpl(Constants.LoginId, hashtable.get(Constants.LOGINID)));*/


                        newItemEntity.getProperties().put(Constants.Currency,
                                new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));

                        newItemEntity.getProperties().put(Constants.Uom,
                                new ODataPropertyDefaultImpl(Constants.Uom, singleRow.get(Constants.Uom)));

                        newItemEntity.getProperties().put(Constants.NetPrice,
                                new ODataPropertyDefaultImpl(Constants.NetPrice, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.NetPrice)))));

                        newItemEntity.getProperties().put(Constants.UnitPrice,
                                new ODataPropertyDefaultImpl(Constants.UnitPrice, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.UnitPrice)))));
                        newItemEntity.getProperties().put(Constants.MRP,
                                new ODataPropertyDefaultImpl(Constants.MRP, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.MRP)))));
                        newItemEntity.getProperties().put(Constants.Quantity,
                                new ODataPropertyDefaultImpl(Constants.Quantity, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Quantity)))));

                        newItemEntity.getProperties().put(Constants.PriDiscount,
                                new ODataPropertyDefaultImpl(Constants.PriDiscount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.PriDiscount)))));

                        newItemEntity.getProperties().put(Constants.SecDiscount,
                                new ODataPropertyDefaultImpl(Constants.SecDiscount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.SecDiscount)))));

                        newItemEntity.getProperties().put(Constants.CashDiscount,
                                new ODataPropertyDefaultImpl(Constants.CashDiscount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.CashDiscount)))));

                        newItemEntity.getProperties().put(Constants.PrimaryDiscountPerc,
                                new ODataPropertyDefaultImpl(Constants.PrimaryDiscountPerc, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.PrimaryDiscountPerc)))));

                        newItemEntity.getProperties().put(Constants.SecondaryDiscountPerc,
                                new ODataPropertyDefaultImpl(Constants.SecondaryDiscountPerc, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.SecondaryDiscountPerc)))));

                        newItemEntity.getProperties().put(Constants.CashDiscountPerc,
                                new ODataPropertyDefaultImpl(Constants.CashDiscountPerc, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.CashDiscountPerc)))));

                        newItemEntity.getProperties().put(Constants.TAX,
                                new ODataPropertyDefaultImpl(Constants.TAX, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.TAX)))));

                        if (!singleRow.get(Constants.MFD).equalsIgnoreCase("")) {
                            newItemEntity.getProperties().put(Constants.MFD,
                                    new ODataPropertyDefaultImpl(Constants.MFD, UtilConstants.convertDateFormat(singleRow.get(Constants.MFD))));
                        }

                        newItemEntity.getProperties().put(Constants.IsfreeGoodsItem,
                                new ODataPropertyDefaultImpl(Constants.IsfreeGoodsItem, singleRow.get(Constants.IsfreeGoodsItem)));

                        newItemEntity.getProperties().put(Constants.Batch,
                                new ODataPropertyDefaultImpl(Constants.Batch, singleRow.get(Constants.Batch)));

                        newItemEntity.getProperties().put(Constants.TransRefTypeID,
                                new ODataPropertyDefaultImpl(Constants.TransRefTypeID, singleRow.get(Constants.TransRefTypeID)));

                        newItemEntity.getProperties().put(Constants.TransRefNo,
                                new ODataPropertyDefaultImpl(Constants.TransRefNo, singleRow.get(Constants.TransRefNo)));

                        newItemEntity.getProperties().put(Constants.TransRefItemNo,
                                new ODataPropertyDefaultImpl(Constants.TransRefItemNo, singleRow.get(Constants.TransRefItemNo)));

                        tempArray.add(incrementVal, newItemEntity);

                    }
                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.SSSoItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.SSSoItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.SSSoItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }

    public static byte[] getMerchindisingImage(String merImgQuery) throws OnlineODataStoreException {
        //BEGIN

        final boolean[] isDataAvailable = {false};
        //Get the open online store
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        final InputStream[] inputStream = {null};
        final byte[][] bytes = {null};

        if (store != null) {
            try {
                //Executor method for reading an Entity set synchronously


                URL urlPath = new URL(merImgQuery);
                ODataDownloadMediaListener oDataDownloadMediaListener = new ODataDownloadMediaListener() {
                    @Override
                    public void mediaDownloadStarted(ODataDownloadMediaExecution oDataDownloadMediaExecution) {

                    }

                    @Override
                    public void mediaDownloadCacheResponse(ODataDownloadMediaExecution oDataDownloadMediaExecution, ODataDownloadMediaResult oDataDownloadMediaResult) {

                    }

                    @Override
                    public void mediaDownloadServerResponse(ODataDownloadMediaExecution oDataDownloadMediaExecution, ODataDownloadMediaResult oDataDownloadMediaResult) {

                        ODataDownloadMediaResultDefaultImpl oDataDownloadMediaResultDefault = (ODataDownloadMediaResultDefaultImpl) oDataDownloadMediaResult;

                        InputStream is = oDataDownloadMediaResultDefault.getInputStream();
                        byte[] buf = null;
                        try {
                            int len;
                            int size = 100 * 1024;


                            if (is instanceof ByteArrayInputStream) {
                                size = is.available();
                                buf = new byte[size];
                                len = is.read(buf, 0, size);
                            } else {
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                buf = new byte[size];
                                while ((len = is.read(buf, 0, size)) != -1)
                                    bos.write(buf, 0, len);
                                buf = bos.toByteArray();


                            }
                        } catch (IOException e) {

                        }

                        bytes[0] = buf;

                        isDataAvailable[0] = true;
                    }

                    @Override
                    public void mediaDownloadFailed(ODataDownloadMediaExecution oDataDownloadMediaExecution, ODataException e) {
                        bytes[0] = null;
                        isDataAvailable[0] = true;

                    }

                    @Override
                    public void mediaDownloadFinished(ODataDownloadMediaExecution oDataDownloadMediaExecution) {

                    }
                };
                store.scheduleMediaDownload(urlPath, oDataDownloadMediaListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }

        while (!isDataAvailable[0]) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isDataAvailable[0] = false;

        return bytes[0];
        //END
    }

    public static void getUnitPrice(Hashtable<String, String> headerTable, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        //BEGIN
        //Get the open online store
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) ;
        try {

            ODataEntity newEntity = createInvoiceSimulation(store, headerTable, itemtable);

            OnlineRequestListener invoiceListener = new OnlineRequestListener(Operation.GetRequest.getValue(), uiListener);

            store.scheduleCreateEntity(newEntity, Constants.SSINVOICES, invoiceListener, null);

        } catch (Exception e) {
            throw new OnlineODataStoreException(e);
        }
        //END
    }

    private static ODataEntity createInvoiceSimulation(OnlineODataStore store,
                                                       Hashtable<String, String> headerhashtable,
                                                       ArrayList<HashMap<String, String>> itemhashtable) throws ODataParserException {
        //BEGIN
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ODataProperty property;
        ODataPropMap properties;

        ArrayList<ODataEntity> tempArray = new ArrayList();
        ArrayList<ODataEntity> tempSerialArray;

        try {


            if (headerhashtable != null) {
                //Use default implementation to create a new travel agency entity type
                newHeaderEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.InvoiceEntity);

                String resourcePath = UtilConstants.getEditResourcePath("SSInvoices", "SSInvoices");
                newHeaderEntity.setResourcePath("SSInvoices", "SSInvoices");

                //If available, it will populates those properties of an OData Entity
                //which are defined by the allocation mode
                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                //Set the corresponding properties
                newHeaderEntity.getProperties().put("InvoiceGUID",
                        new ODataPropertyDefaultImpl("InvoiceGUID", ODataGuidDefaultImpl.initWithString32(headerhashtable.get("InvoiceGUID"))));

                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, headerhashtable.get(Constants.CPGUID)));

         /*       newHeaderEntity.getProperties().put("LoginID",
                        new ODataPropertyDefaultImpl("LoginID", headerhashtable.get("LoginID")));*/
                newHeaderEntity.getProperties().put("InvoiceTypeID",
                        new ODataPropertyDefaultImpl("InvoiceTypeID", headerhashtable.get("InvoiceTypeID")));
                newHeaderEntity.getProperties().put("InvoiceDate",
                        new ODataPropertyDefaultImpl("InvoiceDate", UtilConstants.convertDateFormat(headerhashtable.get("InvoiceDate"))));

                newHeaderEntity.getProperties().put(Constants.CPTypeID,
                        new ODataPropertyDefaultImpl(Constants.CPTypeID, headerhashtable.get(Constants.CPTypeID)));
                newHeaderEntity.getProperties().put("CPNo",
                        new ODataPropertyDefaultImpl("CPNo", headerhashtable.get("CPNo")));
                newHeaderEntity.getProperties().put(Constants.SPGUID,
                        new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(headerhashtable.get(Constants.SPGUID))));
                newHeaderEntity.getProperties().put("SoldToID",
                        new ODataPropertyDefaultImpl("SoldToID", headerhashtable.get("SoldToID")));
                newHeaderEntity.getProperties().put("ShipToCPGUID",
                        new ODataPropertyDefaultImpl("ShipToCPGUID", ODataGuidDefaultImpl.initWithString32(headerhashtable.get("ShipToCPGUID"))));
                newHeaderEntity.getProperties().put("SoldToCPGUID",
                        new ODataPropertyDefaultImpl("SoldToCPGUID", ODataGuidDefaultImpl.initWithString32(headerhashtable.get("SoldToCPGUID"))));

                newHeaderEntity.getProperties().put("ShipToID",
                        new ODataPropertyDefaultImpl("ShipToID", headerhashtable.get("ShipToID")));

                newHeaderEntity.getProperties().put("TestRun",
                        new ODataPropertyDefaultImpl("TestRun", headerhashtable.get("TestRun")));

                for (int i = 0; i < itemhashtable.size(); i++) {

                    HashMap<String, String> singleRow = itemhashtable.get(i);
                    newItemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.InvoiceItemEntity);

                    newItemEntity.setResourcePath("SSInvoiceItemDetails(" + ((i + 1) * 10) + ")", "SSInvoiceItemDetails(" + ((i + 1) * 10) + ")");

                    try {
                        store.allocateProperties(newItemEntity, PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }

                    newItemEntity.getProperties().put("InvoiceItemGUID",
                            new ODataPropertyDefaultImpl("InvoiceItemGUID", ODataGuidDefaultImpl.initWithString32(singleRow.get("InvoiceItemGUID"))));

                    newItemEntity.getProperties().put("InvoiceGUID",
                            new ODataPropertyDefaultImpl("InvoiceGUID", ODataGuidDefaultImpl.initWithString32(headerhashtable.get("InvoiceGUID"))));

                    newItemEntity.getProperties().put("MaterialNo",
                            new ODataPropertyDefaultImpl("MaterialNo", singleRow.get("MatCode")));

                    newItemEntity.getProperties().put("MaterialDesc",
                            new ODataPropertyDefaultImpl("MaterialDesc", singleRow.get("MatDesc")));

                    newItemEntity.getProperties().put("Quantity",
                            new ODataPropertyDefaultImpl("Quantity", BigDecimal.valueOf(Double.parseDouble(singleRow.get("Qty")))));

                    newItemEntity.getProperties().put(Constants.StockGuid,
                            new ODataPropertyDefaultImpl(Constants.StockGuid, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.StockGuid))));

                    newItemEntity.getProperties().put(Constants.UOM,
                            new ODataPropertyDefaultImpl(Constants.UOM, singleRow.get(Constants.UOM)));

                    newItemEntity.getProperties().put(Constants.ItemNo,
                            new ODataPropertyDefaultImpl(Constants.ItemNo, singleRow.get(Constants.ItemNo)));


                    try {
                        store.allocateNavigationProperties(newItemEntity);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }

                    tempArray.add(i, newItemEntity);
                }


                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }

                itemEntity.setResourcePath("SSInvoiceItemDetails");
                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty("SSInvoiceItemDetails");
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty("SSInvoiceItemDetails", navProp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;

    }

    private static ODataEntity createInvoiceHeaderEntity(OnlineODataStore store,
                                                         Hashtable<String, String> headerhashtable,
                                                         ArrayList<HashMap<String, String>> itemhashtable,
                                                         Hashtable<String, ArrayList<InvoiceBean>> hashTableItemSerialNos) throws ODataParserException {
        //BEGIN
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ODataEntity newSerialNoItemEntity = null;
        ODataProperty property;
        ODataPropMap properties;

        ArrayList<ODataEntity> tempArray = new ArrayList();
        ArrayList<ODataEntity> tempSerialArray;

        try {


            if (headerhashtable != null) {
                //Use default implementation to create a new travel agency entity type
                newHeaderEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.InvoiceEntity);

                String resourcePath = UtilConstants.getEditResourcePath("SSInvoices", "SSInvoices");
                newHeaderEntity.setResourcePath("SSInvoices", "SSInvoices");

                //If available, it will populates those properties of an OData Entity
                //which are defined by the allocation mode
                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                //Set the corresponding properties
                newHeaderEntity.getProperties().put("InvoiceGUID",
                        new ODataPropertyDefaultImpl("InvoiceGUID", ODataGuidDefaultImpl.initWithString32(headerhashtable.get("InvoiceGUID"))));

                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, headerhashtable.get(Constants.CPGUID)));

         /*       newHeaderEntity.getProperties().put("LoginID",
                        new ODataPropertyDefaultImpl("LoginID", headerhashtable.get("LoginID")));*/
                newHeaderEntity.getProperties().put("InvoiceTypeID",
                        new ODataPropertyDefaultImpl("InvoiceTypeID", headerhashtable.get("InvoiceTypeID")));
                newHeaderEntity.getProperties().put("InvoiceDate",
                        new ODataPropertyDefaultImpl("InvoiceDate", UtilConstants.convertDateFormat(headerhashtable.get("InvoiceDate"))));
                newHeaderEntity.getProperties().put("CPNo",
                        new ODataPropertyDefaultImpl("CPNo", headerhashtable.get("CPNo")));
                newHeaderEntity.getProperties().put("SoldToCPGUID",
                        new ODataPropertyDefaultImpl("SoldToCPGUID", ODataGuidDefaultImpl.initWithString32(headerhashtable.get("SoldToCPGUID"))));
                newHeaderEntity.getProperties().put("SoldToID",
                        new ODataPropertyDefaultImpl("SoldToID", headerhashtable.get("SoldToID")));
                newHeaderEntity.getProperties().put("ShipToCPGUID",
                        new ODataPropertyDefaultImpl("ShipToCPGUID", ODataGuidDefaultImpl.initWithString32(headerhashtable.get("ShipToCPGUID"))));
                newHeaderEntity.getProperties().put("ShipToID",
                        new ODataPropertyDefaultImpl("ShipToID", headerhashtable.get("ShipToID")));

                for (int i = 0; i < itemhashtable.size(); i++) {

                    HashMap<String, String> singleRow = itemhashtable.get(i);
                    newItemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.InvoiceItemEntity);

                    newItemEntity.setResourcePath("SSInvoiceItemDetails(" + ((i + 1) * 10) + ")", "SSInvoiceItemDetails(" + ((i + 1) * 10) + ")");

                    try {
                        store.allocateProperties(newItemEntity, PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }

                    ArrayList<InvoiceBean> alItemSerialNo = hashTableItemSerialNos.get(singleRow.get("InvoiceItemGUID"));

                    tempSerialArray = new ArrayList();
                    int incementsize = 0;
                    for (int j = 0; j < alItemSerialNo.size(); j++) {
                        InvoiceBean serialNoInvoiceBean = alItemSerialNo.get(j);
                        if (!serialNoInvoiceBean.getStatus().equalsIgnoreCase("04")) {

                            newSerialNoItemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.InvoiceSerialNoEntity);

                            newSerialNoItemEntity.setResourcePath("SSInvoiceItemSerialNos(" + ((j + 1) * 10) + ")", "SSInvoiceItemSerialNos(" + ((j + 1) * 10) + ")");


                            try {
                                store.allocateProperties(newSerialNoItemEntity, PropMode.Keys);
                            } catch (ODataException e) {
                                e.printStackTrace();
                            }

                            newSerialNoItemEntity.getProperties().put("InvoiceItemSNoGUID",
                                    new ODataPropertyDefaultImpl("InvoiceItemSNoGUID", ODataGuidDefaultImpl.initWithString32(serialNoInvoiceBean.getSPSNoGUID())));

                            newSerialNoItemEntity.getProperties().put("InvoiceItemGUID",
                                    new ODataPropertyDefaultImpl("InvoiceItemGUID", ODataGuidDefaultImpl.initWithString32(singleRow.get("InvoiceItemGUID"))));

                            if (serialNoInvoiceBean.getSerialNoTo().equalsIgnoreCase("")) {
                                newSerialNoItemEntity.getProperties().put("SerialNoFrom",
                                        new ODataPropertyDefaultImpl("SerialNoFrom", serialNoInvoiceBean.getSerialNoFrom()));
                            } else {
                                newSerialNoItemEntity.getProperties().put("SerialNoFrom",
                                        new ODataPropertyDefaultImpl("SerialNoFrom", serialNoInvoiceBean.getSerialNoFrom()));

                                newSerialNoItemEntity.getProperties().put("SerialNoTo",
                                        new ODataPropertyDefaultImpl("SerialNoTo", serialNoInvoiceBean.getSerialNoTo()));


                            }

                            if (!serialNoInvoiceBean.getStatus().equalsIgnoreCase("")) {
                                newSerialNoItemEntity.getProperties().put(Constants.InvoiceStatus,
                                        new ODataPropertyDefaultImpl(Constants.InvoiceStatus, serialNoInvoiceBean.getStatus()));
                            }


                            try {
                                store.allocateNavigationProperties(newSerialNoItemEntity);
                            } catch (ODataException e) {
                                e.printStackTrace();
                            }

                            tempSerialArray.add(incementsize, newSerialNoItemEntity);
                            incementsize++;

                        }
                    }

                    newItemEntity.getProperties().put("InvoiceItemGUID",
                            new ODataPropertyDefaultImpl("InvoiceItemGUID", ODataGuidDefaultImpl.initWithString32(singleRow.get("InvoiceItemGUID"))));

                    newItemEntity.getProperties().put("InvoiceGUID",
                            new ODataPropertyDefaultImpl("InvoiceGUID", ODataGuidDefaultImpl.initWithString32(headerhashtable.get("InvoiceGUID"))));

                    newItemEntity.getProperties().put("MaterialNo",
                            new ODataPropertyDefaultImpl("MaterialNo", singleRow.get("MatCode")));

                    newItemEntity.getProperties().put("MaterialDesc",
                            new ODataPropertyDefaultImpl("MaterialDesc", singleRow.get("MatDesc")));

                    newItemEntity.getProperties().put("Quantity",
                            new ODataPropertyDefaultImpl("Quantity", BigDecimal.valueOf(Double.parseDouble(singleRow.get("Qty")))));

                    try {
                        store.allocateNavigationProperties(newItemEntity);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }

                    tempArray.add(i, newItemEntity);


                    ODataEntitySetDefaultImpl itemSerialEntity = new ODataEntitySetDefaultImpl(tempSerialArray.size(), null, null);
                    for (ODataEntity serial : tempSerialArray) {
                        itemSerialEntity.getEntities().add(serial);
                    }
                    itemSerialEntity.setResourcePath("SSInvoiceItemSerialNos");

                    ODataNavigationProperty navSerialProp = newItemEntity.getNavigationProperty("SSInvoiceItemSerialNos");
                    navSerialProp.setNavigationContent(itemSerialEntity);
                    newItemEntity.setNavigationProperty("SSInvoiceItemSerialNos", navSerialProp);

                }


                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }

                itemEntity.setResourcePath("SSInvoiceItemDetails");
                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty("SSInvoiceItemDetails");
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty("SSInvoiceItemDetails", navProp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;

    }

    public static void createFeedBack(Hashtable<String, String> tableHdr, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
//            ODataEntity newEntity = createFeedBackEntity(tableHdr, itemtable, store);
//
//            OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(),uiListener);

//            store.scheduleCreateEntity(newEntity,Constants.Feedbacks,collectionListener,null);

                ODataEntity feedBackEntity = createFeedBackEntity(tableHdr, itemtable, store);

                OnlineRequestListener feedbackListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);


                String feedbackGUID32 = tableHdr.get(Constants.FeebackGUID).replace("-", "");

                String feedbackCreatedOn = tableHdr.get(Constants.CreatedOn);
                String feedbackCreatedAt = tableHdr.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(feedbackCreatedOn) + "T" + UtilConstants.convertTimeOnly(feedbackCreatedAt);


                Map<String, String> collHeaders = new HashMap<String, String>();
                collHeaders.put("RequestID", feedbackGUID32);
                collHeaders.put("RepeatabilityCreation", mStrDateTime);

                ODataRequestParamSingle feedbackReq = new ODataRequestParamSingleDefaultImpl();
                feedbackReq.setMode(ODataRequestParamSingle.Mode.Create);
                feedbackReq.setResourcePath(feedBackEntity.getResourcePath());
                feedbackReq.setPayload(feedBackEntity);
                feedbackReq.getCustomHeaders().putAll(collHeaders);

                store.scheduleRequest(feedbackReq, feedbackListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
        //END

    }

    private static ODataEntity createFeedBackEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity headerEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                // CreateOperation the parent Entity
                headerEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.FeedbackEntity);

                headerEntity.setResourcePath(Constants.Feedbacks, Constants.Feedbacks);


                try {
                    store.allocateProperties(headerEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }

                store.allocateNavigationProperties(headerEntity);

                headerEntity.getProperties().put(Constants.FeebackGUID,
                        new ODataPropertyDefaultImpl(Constants.FeebackGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.FeebackGUID))));
                headerEntity.getProperties().put(Constants.Remarks,
                        new ODataPropertyDefaultImpl(Constants.Remarks, hashtable.get(Constants.Remarks)));
                headerEntity.getProperties().put(Constants.CPNo,
                        new ODataPropertyDefaultImpl(Constants.CPNo, hashtable.get(Constants.CPNo)));
//                headerEntity.getProperties().put(Constants.CPGUID,
//                        new ODataPropertyDefaultImpl(Constants.CPGUID, hashtable.get(Constants.CPGUID)));
                headerEntity.getProperties().put(Constants.FromCPGUID,
                        new ODataPropertyDefaultImpl(Constants.FromCPGUID, hashtable.get(Constants.CPGUID)));
//                headerEntity.getProperties().put(Constants.Location1,
//                        new ODataPropertyDefaultImpl(Constants.Location1, hashtable.get(Constants.Location1)));
//                headerEntity.getProperties().put(Constants.BTSID,
//                        new ODataPropertyDefaultImpl(Constants.BTSID, hashtable.get(Constants.BTSID)));

                headerEntity.getProperties().put(Constants.FeedbackType,
                        new ODataPropertyDefaultImpl(Constants.FeedbackType, hashtable.get(Constants.FeedbackType)));

                headerEntity.getProperties().put(Constants.FeedbackTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.FeedbackTypeDesc, hashtable.get(Constants.FeedbackTypeDesc)));

             /*   headerEntity.getProperties().put(Constants.LOGINID,
                        new ODataPropertyDefaultImpl(Constants.LOGINID, hashtable.get(Constants.LOGINID)));*/

//                headerEntity.getProperties().put(Constants.CPTypeID,
//                        new ODataPropertyDefaultImpl(Constants.CPTypeID, hashtable.get(Constants.CPTypeID)));
                headerEntity.getProperties().put(Constants.FromCPTypeID,
                        new ODataPropertyDefaultImpl(Constants.FromCPTypeID, hashtable.get(Constants.CPTypeID)));

                headerEntity.getProperties().put(Constants.ParentID,
                        new ODataPropertyDefaultImpl(Constants.ParentID, hashtable.get(Constants.ParentID)));

                headerEntity.getProperties().put(Constants.ParentName,
                        new ODataPropertyDefaultImpl(Constants.ParentName, hashtable.get(Constants.ParentName)));

                headerEntity.getProperties().put(Constants.ParentTypeID,
                        new ODataPropertyDefaultImpl(Constants.ParentTypeID, hashtable.get(Constants.ParentTypeID)));

                headerEntity.getProperties().put(Constants.ParentTypDesc,
                        new ODataPropertyDefaultImpl(Constants.ParentTypDesc, hashtable.get(Constants.ParentTypDesc)));

                try {
                    headerEntity.getProperties().put(Constants.SPGUID,
                            new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                headerEntity.getProperties().put(Constants.SPNo,
                        new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));

                // CreateOperation the item Entity

                ODataEntity itemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.FeedbackItemDetailEntity);
//

                try {
                    store.allocateProperties(itemEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < itemhashtable.size(); i++) {
                    HashMap<String, String> singleRow = itemhashtable.get(i);
                    try {
                        store.allocateProperties(itemEntity, PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }


                    itemEntity.getProperties().put(Constants.Remarks, new ODataPropertyDefaultImpl(Constants.Remarks, hashtable.get(Constants.Remarks)));
                    //  itemEntity.getProperties().put(Constants.LOGINID, new ODataPropertyDefaultImpl(Constants.LOGINID, hashtable.get(Constants.LOGINID)));
                    itemEntity.getProperties().put(Constants.FeebackGUID, new ODataPropertyDefaultImpl(Constants.FeebackGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.FeebackGUID))));
                    itemEntity.getProperties().put(Constants.FeebackItemGUID, new ODataPropertyDefaultImpl(Constants.FeebackItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.FeebackItemGUID))));
                    itemEntity.getProperties().put(Constants.FeedbackType, new ODataPropertyDefaultImpl(Constants.FeedbackType, singleRow.get(Constants.FeedbackType)));
                    itemEntity.getProperties().put(Constants.FeedbackTypeDesc, new ODataPropertyDefaultImpl(Constants.FeedbackTypeDesc, singleRow.get(Constants.FeedbackTypeDesc)));
                    itemEntity.getProperties().put(Constants.ParentID,
                            new ODataPropertyDefaultImpl(Constants.ParentID, hashtable.get(Constants.ParentID)));

                    itemEntity.getProperties().put(Constants.ParentName,
                            new ODataPropertyDefaultImpl(Constants.ParentName, hashtable.get(Constants.ParentName)));

                    itemEntity.getProperties().put(Constants.ParentTypeID,
                            new ODataPropertyDefaultImpl(Constants.ParentTypeID, hashtable.get(Constants.ParentTypeID)));

                    itemEntity.getProperties().put(Constants.ParentTypDesc,
                            new ODataPropertyDefaultImpl(Constants.ParentTypDesc, hashtable.get(Constants.ParentTypDesc)));
                    tempArray.add(i, itemEntity);
                }

                ODataEntitySetDefaultImpl itmEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itmEntity.getEntities().add(entity);
                }
                itmEntity.setResourcePath(Constants.FeedbackItemDetails);


                ODataNavigationProperty navProp = headerEntity.getNavigationProperty(Constants.FeedbackItemDetails);
                navProp.setNavigationContent(itmEntity);
                headerEntity.setNavigationProperty(Constants.FeedbackItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headerEntity;

    }

    /**
     * Create Entity for SS invoice creation and Schedule in Online Manager
     *
     * @throws OnlineODataStoreException
     */
    public static void createSSInvoiceEntity(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity soCreateEntity = createSSInvoiceCreateEntity(table, itemtable, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
        //END

    }

    /**
     * Create Entity for collection creation
     *
     * @throws ODataParserException
     */
    private static ODataEntity createSSInvoiceCreateEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.InvoiceEntity);

                newHeaderEntity.setResourcePath(Constants.SSINVOICES, Constants.SSINVOICES);

                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                newHeaderEntity.getProperties().put(Constants.InvoiceGUID,
                        new ODataPropertyDefaultImpl(Constants.InvoiceGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.InvoiceGUID))));

                newHeaderEntity.getProperties().put(Constants.SPGUID,
                        new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID))));
          /*      newHeaderEntity.getProperties().put(Constants.LoginID,
                        new ODataPropertyDefaultImpl(Constants.LoginID, hashtable.get(Constants.LoginID)));*/
                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, hashtable.get(Constants.CPGUID)));
                newHeaderEntity.getProperties().put(Constants.CPNo,
                        new ODataPropertyDefaultImpl(Constants.CPNo, hashtable.get(Constants.CPNo)));
                newHeaderEntity.getProperties().put(Constants.CPName,
                        new ODataPropertyDefaultImpl(Constants.CPName, hashtable.get(Constants.CPName)));

                newHeaderEntity.getProperties().put(Constants.DmsDivision,
                        new ODataPropertyDefaultImpl(Constants.DmsDivision, hashtable.get(Constants.DmsDivision)));
                newHeaderEntity.getProperties().put(Constants.DmsDivisionDesc,
                        new ODataPropertyDefaultImpl(Constants.DmsDivisionDesc, hashtable.get(Constants.DmsDivisionDesc)));

                newHeaderEntity.getProperties().put(Constants.CPTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.CPTypeDesc, hashtable.get(Constants.CPTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.CPTypeID,
                        new ODataPropertyDefaultImpl(Constants.CPTypeID, hashtable.get(Constants.CPTypeID)));
//
                newHeaderEntity.getProperties().put(Constants.SPNo,
                        new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));
                newHeaderEntity.getProperties().put(Constants.SPName,
                        new ODataPropertyDefaultImpl(Constants.SPName, hashtable.get(Constants.SPName)));
                newHeaderEntity.getProperties().put(Constants.InvoiceNo,
                        new ODataPropertyDefaultImpl(Constants.InvoiceNo, hashtable.get(Constants.InvoiceNo)));
                newHeaderEntity.getProperties().put(Constants.InvoiceTypeID,
                        new ODataPropertyDefaultImpl(Constants.InvoiceTypeID, hashtable.get(Constants.InvoiceTypeID)));
                newHeaderEntity.getProperties().put(Constants.InvoiceTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.InvoiceTypeDesc, hashtable.get(Constants.InvoiceTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.InvoiceDate,
                        new ODataPropertyDefaultImpl(Constants.InvoiceDate, UtilConstants.convertDateFormat(hashtable.get(Constants.InvoiceDate))));

                newHeaderEntity.getProperties().put(Constants.PONo,
                        new ODataPropertyDefaultImpl(Constants.PONo, hashtable.get(Constants.PONo)));
                newHeaderEntity.getProperties().put(Constants.PODate,
                        new ODataPropertyDefaultImpl(Constants.PODate, UtilConstants.convertDateFormat(hashtable.get(Constants.PODate))));


                newHeaderEntity.getProperties().put(Constants.SoldToCPGUID,
                        new ODataPropertyDefaultImpl(Constants.SoldToCPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SoldToCPGUID))));


                newHeaderEntity.getProperties().put(Constants.SoldToID,
                        new ODataPropertyDefaultImpl(Constants.SoldToID, hashtable.get(Constants.SoldToID)));
                newHeaderEntity.getProperties().put(Constants.SoldToName,
                        new ODataPropertyDefaultImpl(Constants.SoldToName, hashtable.get(Constants.SoldToName)));
                newHeaderEntity.getProperties().put(Constants.SoldToTypeID,
                        new ODataPropertyDefaultImpl(Constants.SoldToTypeID, hashtable.get(Constants.SoldToTypeID)));
                newHeaderEntity.getProperties().put(Constants.SoldToTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.SoldToTypeDesc, hashtable.get(Constants.SoldToTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));

                try {
                    if (!hashtable.get(Constants.BeatGUID).equalsIgnoreCase("")) {
                        newHeaderEntity.getProperties().put(Constants.BeatGUID,
                                new ODataPropertyDefaultImpl(Constants.BeatGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.BeatGUID))));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                int incremntVal = 0;
                for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {

                    HashMap<String, String> singleRow = itemhashtable.get(incrementVal);

                    incremntVal = incrementVal + 1;

                    newItemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.InvoiceItemEntity);

                    newItemEntity.setResourcePath(Constants.SSInvoiceItemDetails + "(" + incremntVal + ")", Constants.SSInvoiceItemDetails + "(" + incremntVal + ")");
                    try {
                        store.allocateProperties(newItemEntity, PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }


                    newItemEntity.getProperties().put(Constants.InvoiceItemGUID,
                            new ODataPropertyDefaultImpl(Constants.InvoiceItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.InvoiceItemGUID))));

                    newItemEntity.getProperties().put(Constants.InvoiceGUID,
                            new ODataPropertyDefaultImpl(Constants.InvoiceGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.InvoiceGUID))));

                    newItemEntity.getProperties().put(Constants.StockGuid,
                            new ODataPropertyDefaultImpl(Constants.StockGuid, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.StockGuid))));

                    newItemEntity.getProperties().put(Constants.ItemNo,
                            new ODataPropertyDefaultImpl(Constants.ItemNo, singleRow.get(Constants.ItemNo)));

                    newItemEntity.getProperties().put(Constants.InvoiceNo,
                            new ODataPropertyDefaultImpl(Constants.InvoiceNo, singleRow.get(Constants.InvoiceNo)));

                    newItemEntity.getProperties().put(Constants.Remarks,
                            new ODataPropertyDefaultImpl(Constants.Remarks, singleRow.get(Constants.Remarks)));

                    newItemEntity.getProperties().put(Constants.Quantity,
                            new ODataPropertyDefaultImpl(Constants.Quantity, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Quantity)))));


                    newItemEntity.getProperties().put(Constants.MaterialNo,
                            new ODataPropertyDefaultImpl(Constants.MaterialNo, singleRow.get(Constants.MaterialNo)));

                    newItemEntity.getProperties().put(Constants.MaterialDesc,
                            new ODataPropertyDefaultImpl(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc)));

                    newItemEntity.getProperties().put(Constants.UOM,
                            new ODataPropertyDefaultImpl(Constants.UOM, singleRow.get(Constants.UOM)));

                    newItemEntity.getProperties().put(Constants.Currency,
                            new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));

                    newItemEntity.getProperties().put(Constants.InvoiceDate,
                            new ODataPropertyDefaultImpl(Constants.InvoiceDate, UtilConstants.convertDateFormat(singleRow.get(Constants.InvoiceDate))));

                    try {
                        if (!singleRow.get(Constants.BeatGUID).equalsIgnoreCase("")) {
                            newItemEntity.getProperties().put(Constants.BeatGUID,
                                    new ODataPropertyDefaultImpl(Constants.BeatGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.BeatGUID))));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    tempArray.add(incrementVal, newItemEntity);

                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.SSInvoiceItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.SSInvoiceItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.SSInvoiceItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }

    /*create daily expense*/
    public static void createDailyExpense(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                ODataEntity soCreateEntity = createDailyExpenseCreateEntity(table, itemtable, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);
//                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }

    }

    /*entity for expense */
    public static ODataEntity createDailyExpenseCreateEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ODataEntity newItemImageEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        ArrayList<ODataEntity> docmentArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.ExpenseEntity);

                newHeaderEntity.setResourcePath(Constants.Expenses, Constants.Expenses);

                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                newHeaderEntity.getProperties().put(Constants.ExpenseGUID,
                        new ODataPropertyDefaultImpl(Constants.ExpenseGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.ExpenseGUID))));
//                newHeaderEntity.getProperties().put(Constants.OrderNo,
//                        new ODataPropertyDefaultImpl(Constants.OrderNo, hashtable.get(Constants.OrderNo)));
                newHeaderEntity.getProperties().put(Constants.ExpenseNo,
                        new ODataPropertyDefaultImpl(Constants.ExpenseNo, hashtable.get(Constants.ExpenseNo)));
                newHeaderEntity.getProperties().put(Constants.FiscalYear,
                        new ODataPropertyDefaultImpl(Constants.FiscalYear, hashtable.get(Constants.FiscalYear)));
             /*   newHeaderEntity.getProperties().put(Constants.LoginID,
                        new ODataPropertyDefaultImpl(Constants.LoginID, hashtable.get(Constants.LoginID)));*/
               /* newHeaderEntity.getProperties().put(Constants.OrderDate,
                        new ODataPropertyDefaultImpl(Constants.OrderDate, UtilConstants.convertDateFormat(hashtable.get(Constants.OrderDate))));*/

                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, hashtable.get(Constants.CPGUID)));
                newHeaderEntity.getProperties().put(Constants.CPNo,
                        new ODataPropertyDefaultImpl(Constants.CPNo, hashtable.get(Constants.CPNo)));
                newHeaderEntity.getProperties().put(Constants.CPName,
                        new ODataPropertyDefaultImpl(Constants.CPName, hashtable.get(Constants.CPName)));
                newHeaderEntity.getProperties().put(Constants.CPType,
                        new ODataPropertyDefaultImpl(Constants.CPType, hashtable.get(Constants.CPType)));
                newHeaderEntity.getProperties().put(Constants.CPTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.CPTypeDesc, hashtable.get(Constants.CPTypeDesc)));


                newHeaderEntity.getProperties().put(Constants.ExpenseType,
                        new ODataPropertyDefaultImpl(Constants.ExpenseType, hashtable.get(Constants.ExpenseType)));
                newHeaderEntity.getProperties().put(Constants.ExpenseTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.ExpenseTypeDesc, hashtable.get(Constants.ExpenseTypeDesc)));
                try {
                    newHeaderEntity.getProperties().put(Constants.ExpenseDate,
                            new ODataPropertyDefaultImpl(Constants.ExpenseDate, UtilConstants.convertDateFormat(hashtable.get(Constants.ExpenseDate))));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                newHeaderEntity.getProperties().put(Constants.Status,
                        new ODataPropertyDefaultImpl(Constants.Status, hashtable.get(Constants.Status)));
                newHeaderEntity.getProperties().put(Constants.StatusDesc,
                        new ODataPropertyDefaultImpl(Constants.StatusDesc, hashtable.get(Constants.StatusDesc)));

                newHeaderEntity.getProperties().put(Constants.Amount,
                        new ODataPropertyDefaultImpl(Constants.Amount, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.Amount)))));
               /* newHeaderEntity.getProperties().put(Constants.CreatedOn,
                        new ODataPropertyDefaultImpl(Constants.CreatedOn, hashtable.get(Constants.CreatedOn)));*/
//                newHeaderEntity.getProperties().put(Constants.CreatedBy,
//                        new ODataPropertyDefaultImpl(Constants.CreatedBy, hashtable.get(Constants.CreatedBy)));


                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));


                newHeaderEntity.getProperties().put(Constants.SPGUID,
                        new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID))));
                newHeaderEntity.getProperties().put(Constants.SPNo,
                        new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));
                newHeaderEntity.getProperties().put(Constants.SPName,
                        new ODataPropertyDefaultImpl(Constants.SPName, hashtable.get(Constants.SPName)));


                int incremntVal = 0;
                for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {

                    HashMap<String, String> singleRow = itemhashtable.get(incrementVal);

                    incremntVal = incrementVal + 1;

                    newItemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.ExpenseItemEntity);

                    newItemEntity.setResourcePath(Constants.ExpenseItemDetails + "(" + incremntVal + ")", Constants.ExpenseItemDetails + "(" + incremntVal + ")");
                    try {
                        store.allocateProperties(newItemEntity, PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }
                    /*try {
                        store.allocateProperties(newItemEntity, PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }*/
                    //If available, it populates the navigation properties of an OData Entity
                    store.allocateNavigationProperties(newItemEntity);

                    newItemEntity.getProperties().put(Constants.ExpenseItemGUID,
                            new ODataPropertyDefaultImpl(Constants.ExpenseItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.ExpenseItemGUID))));

                    newItemEntity.getProperties().put(Constants.ExpenseGUID,
                            new ODataPropertyDefaultImpl(Constants.ExpenseGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.ExpenseGUID))));

                    newItemEntity.getProperties().put(Constants.ExpeseItemNo,
                            new ODataPropertyDefaultImpl(Constants.ExpeseItemNo, singleRow.get(Constants.ExpeseItemNo)));


                    /*newItemEntity.getProperties().put(Constants.LoginID,
                            new ODataPropertyDefaultImpl(Constants.LoginID, singleRow.get(Constants.LoginID)));*/

//                    newItemEntity.getProperties().put(Constants.MaterialDesc,
//                            new ODataPropertyDefaultImpl(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc)));

                    newItemEntity.getProperties().put(Constants.ExpenseItemType,
                            new ODataPropertyDefaultImpl(Constants.ExpenseItemType, singleRow.get(Constants.ExpenseItemType)));
//
                    newItemEntity.getProperties().put(Constants.ExpenseItemTypeDesc,
                            new ODataPropertyDefaultImpl(Constants.ExpenseItemTypeDesc, singleRow.get(Constants.ExpenseItemTypeDesc)));
                    if (!singleRow.get(Constants.BeatGUID).equalsIgnoreCase("")) {
                        newItemEntity.getProperties().put(Constants.BeatGUID,
                                new ODataPropertyDefaultImpl(Constants.BeatGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.BeatGUID))));
                    }
                    newItemEntity.getProperties().put(Constants.Location,
                            new ODataPropertyDefaultImpl(Constants.Location, singleRow.get(Constants.Location)));
                    if (!singleRow.get(Constants.ConvenyanceMode).equals("")) {
                        newItemEntity.getProperties().put(Constants.ConvenyanceMode,
                                new ODataPropertyDefaultImpl(Constants.ConvenyanceMode, singleRow.get(Constants.ConvenyanceMode)));
                        newItemEntity.getProperties().put(Constants.ConvenyanceModeDs,
                                new ODataPropertyDefaultImpl(Constants.ConvenyanceModeDs, singleRow.get(Constants.ConvenyanceModeDs)));
                    }
                    if (!singleRow.get(Constants.BeatDistance).equals("")) {
                        newItemEntity.getProperties().put(Constants.BeatDistance, new ODataPropertyDefaultImpl(Constants.BeatDistance, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.BeatDistance)))));
                    }
                    if (!singleRow.get(Constants.Amount).equalsIgnoreCase("")) {
                        newItemEntity.getProperties().put(Constants.Amount,
                                new ODataPropertyDefaultImpl(Constants.Amount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Amount)))));
                    }
                    newItemEntity.getProperties().put(Constants.UOM,
                            new ODataPropertyDefaultImpl(Constants.UOM, singleRow.get(Constants.UOM)));
                    newItemEntity.getProperties().put(Constants.Currency,
                            new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));
                    newItemEntity.getProperties().put(Constants.Remarks,
                            new ODataPropertyDefaultImpl(Constants.Remarks, singleRow.get(Constants.Remarks)));



                    /*image document*/
                    /*String imageStringArray = singleRow.get("item_no" + incrementVal);
                    ArrayList<HashMap<String, String>> convertedImage = UtilConstants.convertToArrayListMap(imageStringArray);
                    int incremntImgVal = 0;
                    for (int incrementImgVal = 0; incrementImgVal < convertedImage.size(); incrementImgVal++) {
                        HashMap<String, String> singleImgRow = convertedImage.get(incrementImgVal);
                        incremntImgVal = incrementImgVal + 1;

                        newItemImageEntity = new ODataEntityDefaultImpl(Constants.ExpenseItemDocumentEntity);

                        newItemImageEntity.setResourcePath(Constants.ExpenseDocuments + "(" + incremntImgVal + ")", Constants.ExpenseDocuments + "(" + incremntImgVal + ")");
                        try {
                            store.allocateProperties(newItemImageEntity, PropMode.Keys);
                        } catch (ODataException e) {
                            e.printStackTrace();
                        }
                        newItemImageEntity.getProperties().put(Constants.ExpenseImgGUID,
                                new ODataPropertyDefaultImpl(Constants.ExpenseImgGUID, singleImgRow.get(Constants.ExpenseImgGUID)));

                        newItemImageEntity.getProperties().put(Constants.ExpenseItemGUID,
                                new ODataPropertyDefaultImpl(Constants.ExpenseItemGUID, ODataGuidDefaultImpl.initWithString32(singleImgRow.get(Constants.ExpenseItemGUID))));

                        newItemImageEntity.getProperties().put(Constants.LoginID,
                                new ODataPropertyDefaultImpl(Constants.LoginID, singleImgRow.get(Constants.LoginID)));

                        newItemImageEntity.getProperties().put(Constants.DocumentTypeID,
                                new ODataPropertyDefaultImpl(Constants.DocumentTypeID, singleImgRow.get(Constants.DocumentTypeID)));

                        newItemImageEntity.getProperties().put(Constants.DocumentTypeDesc,
                                new ODataPropertyDefaultImpl(Constants.DocumentTypeDesc, singleImgRow.get(Constants.DocumentTypeDesc)));

                        newItemImageEntity.getProperties().put(Constants.DocumentStatusID,
                                new ODataPropertyDefaultImpl(Constants.DocumentStatusID, singleImgRow.get(Constants.DocumentStatusID)));

                        newItemImageEntity.getProperties().put(Constants.DocumentStatusDesc,
                                new ODataPropertyDefaultImpl(Constants.DocumentStatusDesc, singleImgRow.get(Constants.DocumentStatusDesc)));

                        newItemImageEntity.getProperties().put(Constants.ValidFrom,
                                new ODataPropertyDefaultImpl(Constants.ValidFrom, UtilConstants.convertDateFormat(singleImgRow.get(Constants.ValidFrom))));

                        newItemImageEntity.getProperties().put(Constants.ValidTo,
                                new ODataPropertyDefaultImpl(Constants.ValidTo, UtilConstants.convertDateFormat(singleImgRow.get(Constants.ValidTo))));

                        newItemImageEntity.getProperties().put(Constants.DocumentLink,
                                new ODataPropertyDefaultImpl(Constants.DocumentLink, singleImgRow.get(Constants.DocumentLink)));

                        newItemImageEntity.getProperties().put(Constants.FileName,
                                new ODataPropertyDefaultImpl(Constants.FileName, singleImgRow.get(Constants.FileName)));

                        newItemImageEntity.getProperties().put(Constants.DocumentMimeType,
                                new ODataPropertyDefaultImpl(Constants.DocumentMimeType, singleImgRow.get(Constants.DocumentMimeType)));

                        newItemImageEntity.getProperties().put(Constants.DocumentSize,
                                new ODataPropertyDefaultImpl(Constants.DocumentSize, singleImgRow.get(Constants.DocumentSize)));

                        docmentArray.add(incrementImgVal,newItemImageEntity);
                    }

                    ODataEntitySetDefaultImpl itemImageEntity = new ODataEntitySetDefaultImpl(docmentArray.size(), null, null);
                    for (ODataEntity entity : docmentArray) {
                        itemImageEntity.getEntities().add(entity);
                    }
                    itemImageEntity.setResourcePath(Constants.ExpenseDocuments);

                    ODataNavigationProperty navProp = newItemEntity.getNavigationProperty(Constants.ExpenseDocuments);
                    navProp.setNavigationContent(itemImageEntity);
                    newItemEntity.setNavigationProperty(Constants.ExpenseDocuments, navProp);*/

                    tempArray.add(incrementVal, newItemEntity);


                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.ExpenseItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.ExpenseItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.ExpenseItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }

    public static boolean openOnlineStoreForForgetPassword(Context context, String mStrOldPwd) throws OnlineODataStoreException {
        try {
            //Listener to be invoked when the opening process of an OnlineODataStore object finishes
            OnlineStoreListener openListener = OnlineStoreListener.getInstance();
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();

            //The logon configurator uses the information obtained in the registration
            // (i.e endpoint URL, login, etc ) to configure the conversation manager
            IManagerConfigurator configurator = LogonUIFacade.getInstance().getLogonConfigurator(context);
            HttpConversationManager manager = new HttpConversationManager(context);
            configurator.configure(manager);
            //XCSRFTokenRequestFilter implements IRequestFilter
            //Request filter that is allowed to preprocess the request before sending
            XCSRFTokenRequestFilter requestFilter = XCSRFTokenRequestFilter.getInstance(lgCtx);
            XCSRFTokenResponseFilter responseFilter = XCSRFTokenResponseFilter.getInstance(context,
                    requestFilter);
            manager.addFilter(requestFilter);
            manager.addFilter(responseFilter);
            String relayUrlSuffix = lgCtx.getResourcePath();
            try {


                URL url = null;
                String protocol = lgCtx.isHttps() ? "https" : "http";
                if (relayUrlSuffix.equalsIgnoreCase(""))
                    url = new URL("" + protocol + "://" + lgCtx.getHost() + ":" + lgCtx.getPort() + "/" + com.arteriatech.emami.registration.Configuration.ForgotPasswordConnectionName);
                else {
                    String farmId = lgCtx.getFarmId();
                    url = new URL("" + protocol + "://" + lgCtx.getHost() + ":" + lgCtx.getPort() + "/" + relayUrlSuffix + "/" + farmId + "/" + com.arteriatech.emami.registration.Configuration.ForgotPasswordConnectionName);

                }
                //Method to open a new online store asynchronously

                OnlineODataStore.open(context, url, manager, openListener, null);
                if (openListener.getError() != null) {
                    throw openListener.getError();
                }
            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
            while (!Constants.IsOnlineStoreFailed) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Constants.IsOnlineStoreFailed = false;


            if (Constants.onlineStore != null) {
                return true;
            } else {
                return false;
            }
        } catch (OnlineODataStoreException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean getOTP(String resourcePath, UIListener uiListener) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        boolean mBooleanOTPGenerate = false;
        if (store != null) {
            try {

                ODataRequestChangeSet changeSetItem = new ODataRequestChangeSetDefaultImpl();


                ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();

                ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();

                batchItem.setResourcePath(resourcePath);

                batchItem.setMode(ODataRequestParamSingle.Mode.Create);

//                ODataEntity parentEntity = new ODataEntityDefaultImpl(Constants.PasswordEntity);
                ODataEntity parentEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.PasswordEntity);
                parentEntity.setResourcePath(Constants.Passwords, Constants.Passwords);

                store.allocateProperties(parentEntity, ODataStore.PropMode.Keys);
                store.allocateNavigationProperties(parentEntity);

                parentEntity.getProperties().put(Constants.ApplicationID,
                        new ODataPropertyDefaultImpl(Constants.ApplicationID, ""));


                batchItem.setPayload(parentEntity);

                changeSetItem.add(batchItem);

                requestParamBatch.add(changeSetItem);

                OnlineRequestListener batchListener = new OnlineRequestListener(Operation.GetRequest.getValue(), uiListener);

                try {
                    store.scheduleRequest(requestParamBatch, batchListener);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
        return mBooleanOTPGenerate;
    }


    public static boolean sendResetPassword(String resourcePath, String guidValue, String mStrOTP, UIListener uiListener) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        boolean mBooleanOTPGenerate = false;
        if (store != null) {
            try {

                ODataRequestChangeSet changeSetItem = new ODataRequestChangeSetDefaultImpl();

                ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();

                ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();

                batchItem.setResourcePath(resourcePath);

                batchItem.setMode(ODataRequestParamSingle.Mode.Update);

//                ODataEntity resetPwdEntity = new ODataEntityDefaultImpl(Constants.PasswordEntity);
                ODataEntity resetPwdEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.PasswordEntity);
                resetPwdEntity.setResourcePath(Constants.Passwords, Constants.Passwords);

                store.allocateProperties(resetPwdEntity, ODataStore.PropMode.Keys);
                store.allocateNavigationProperties(resetPwdEntity);

                resetPwdEntity.getProperties().put(Constants.ApplicationID,
                        new ODataPropertyDefaultImpl(Constants.ApplicationID, ""));

                resetPwdEntity.getProperties().put(Constants.OTP,
                        new ODataPropertyDefaultImpl(Constants.OTP, mStrOTP));
                resetPwdEntity.getProperties().put(Constants.PasswordGUID,
                        new ODataPropertyDefaultImpl(Constants.PasswordGUID, ODataGuidDefaultImpl.initWithString32(guidValue.toUpperCase())));


                batchItem.setPayload(resetPwdEntity);

                changeSetItem.add(batchItem);

                requestParamBatch.add(changeSetItem);

                OnlineRequestListener batchListener = new OnlineRequestListener(Operation.GetRequest.getValue(), uiListener);

                try {
                    store.scheduleRequest(requestParamBatch, batchListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
        return mBooleanOTPGenerate;
    }

    public static boolean changePassword(String resourcePath, String mStrOldPwd, String mStrNewPwd, String mStrOtp, String mStrGuid, UIListener uiListener) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        boolean mBooleanOTPGenerate = false;
        if (store != null) {
            try {

                ODataRequestChangeSet changeSetItem = new ODataRequestChangeSetDefaultImpl();

                ODataRequestParamBatch requestParamBatch = new ODataRequestParamBatchDefaultImpl();

                ODataRequestParamSingle batchItem = new ODataRequestParamSingleDefaultImpl();

                batchItem.setResourcePath(resourcePath);

                batchItem.setMode(ODataRequestParamSingle.Mode.Update);

//                ODataEntity changePwdEntity = new ODataEntityDefaultImpl(Constants.PasswordEntity);
                ODataEntity changePwdEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.PasswordEntity);
                changePwdEntity.setResourcePath(Constants.Passwords, Constants.Passwords);

                store.allocateProperties(changePwdEntity, ODataStore.PropMode.Keys);
                store.allocateNavigationProperties(changePwdEntity);
                changePwdEntity.getProperties().put(Constants.OTP,
                        new ODataPropertyDefaultImpl(Constants.OTP, mStrOtp));
                changePwdEntity.getProperties().put(Constants.PasswordGUID,
                        new ODataPropertyDefaultImpl(Constants.PasswordGUID, ODataGuidDefaultImpl.initWithString32(mStrGuid.toUpperCase())));

                changePwdEntity.getProperties().put(Constants.NewPassword,
                        new ODataPropertyDefaultImpl(Constants.NewPassword, Constants.encodedPwd(mStrNewPwd)));
                changePwdEntity.getProperties().put(Constants.IsChange,
                        new ODataPropertyDefaultImpl(Constants.IsChange, true));
                changePwdEntity.getProperties().put(Constants.OldPassword,
                        new ODataPropertyDefaultImpl(Constants.OldPassword, Constants.encodedPwd(mStrOldPwd)));
                batchItem.setPayload(changePwdEntity);


                // Add headers

                Map<String, String> createHeaders = new HashMap<String, String>();

                createHeaders.put("Content-Type", "application/http");

                createHeaders.put("Content-Transfer-Encoding", "binary");

                batchItem.setOptions(createHeaders);

                changeSetItem.add(batchItem);

                requestParamBatch.add(changeSetItem);

                OnlineRequestListener batchListener = new OnlineRequestListener(Operation.GetRequest.getValue(), uiListener);

                try {
                    store.scheduleRequest(requestParamBatch, batchListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
        return mBooleanOTPGenerate;
    }

    public static void createCP(Hashtable<String, String> tableHdr, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity cpEntity = createCPEntity(tableHdr, itemtable, store);

                OnlineRequestListener CPListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);


                String cpGUID32 = tableHdr.get(Constants.CPGUID).replace("-", "");

                String mStrCreatedOn = tableHdr.get(Constants.CreatedOn);
                String mStrCreatedAt = tableHdr.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(mStrCreatedOn) + "T" + UtilConstants.convertTimeOnly(mStrCreatedAt);


                Map<String, String> collHeaders = new HashMap<String, String>();
                collHeaders.put(Constants.RequestID, cpGUID32);
                collHeaders.put(Constants.RepeatabilityCreation, mStrDateTime);

                ODataRequestParamSingle cpReq = new ODataRequestParamSingleDefaultImpl();
                cpReq.setMode(ODataRequestParamSingle.Mode.Create);
                cpReq.setResourcePath(cpEntity.getResourcePath());
                cpReq.setPayload(cpEntity);
                cpReq.getCustomHeaders().putAll(collHeaders);

                store.scheduleRequest(cpReq, CPListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
        //END

    }

    private static ODataEntity createCPEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity headerEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                // CreateOperation the parent Entity
                headerEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.ChannelPartnerEntity);
                headerEntity.setResourcePath(Constants.ChannelPartners, Constants.ChannelPartners);
                try {
                    store.allocateProperties(headerEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                store.allocateNavigationProperties(headerEntity);

                headerEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CPGUID).toUpperCase())));
                headerEntity.getProperties().put(Constants.Address1,
                        new ODataPropertyDefaultImpl(Constants.Address1, hashtable.get(Constants.Address1)));
                headerEntity.getProperties().put(Constants.Country,
                        new ODataPropertyDefaultImpl(Constants.Country, hashtable.get(Constants.Country)));
                headerEntity.getProperties().put(Constants.DistrictDesc,
                        new ODataPropertyDefaultImpl(Constants.DistrictDesc, hashtable.get(Constants.DistrictDesc)));
                headerEntity.getProperties().put(Constants.DistrictID,
                        new ODataPropertyDefaultImpl(Constants.DistrictID, hashtable.get(Constants.DistrictID)));
                headerEntity.getProperties().put(Constants.StateID,
                        new ODataPropertyDefaultImpl(Constants.StateID, hashtable.get(Constants.StateID)));
                headerEntity.getProperties().put(Constants.StateDesc,
                        new ODataPropertyDefaultImpl(Constants.StateDesc, hashtable.get(Constants.StateDesc)));
                headerEntity.getProperties().put(Constants.CityID,
                        new ODataPropertyDefaultImpl(Constants.CityID, hashtable.get(Constants.CityID)));
                headerEntity.getProperties().put(Constants.CityDesc,
                        new ODataPropertyDefaultImpl(Constants.CityDesc, hashtable.get(Constants.CityDesc)));
                headerEntity.getProperties().put(Constants.Landmark,
                        new ODataPropertyDefaultImpl(Constants.Landmark, hashtable.get(Constants.Landmark)));
                headerEntity.getProperties().put(Constants.PostalCode,
                        new ODataPropertyDefaultImpl(Constants.PostalCode, hashtable.get(Constants.PostalCode)));
                headerEntity.getProperties().put(Constants.MobileNo,
                        new ODataPropertyDefaultImpl(Constants.MobileNo, hashtable.get(Constants.MobileNo)));
                headerEntity.getProperties().put(Constants.EmailID,
                        new ODataPropertyDefaultImpl(Constants.EmailID, hashtable.get(Constants.EmailID)));
                headerEntity.getProperties().put(Constants.PAN,
                        new ODataPropertyDefaultImpl(Constants.PAN, hashtable.get(Constants.PAN)));
                headerEntity.getProperties().put(Constants.VATNo,
                        new ODataPropertyDefaultImpl(Constants.VATNo, hashtable.get(Constants.VATNo)));
                headerEntity.getProperties().put(Constants.OutletName,
                        new ODataPropertyDefaultImpl(Constants.OutletName, hashtable.get(Constants.OutletName)));
                headerEntity.getProperties().put(Constants.OwnerName,
                        new ODataPropertyDefaultImpl(Constants.OwnerName, hashtable.get(Constants.OwnerName)));
                headerEntity.getProperties().put(Constants.RetailerProfile,
                        new ODataPropertyDefaultImpl(Constants.RetailerProfile, hashtable.get(Constants.RetailerProfile)));
                if (!hashtable.get(Constants.DOB).equalsIgnoreCase("")) {
                    headerEntity.getProperties().put(Constants.DOB,
                            new ODataPropertyDefaultImpl(Constants.DOB, UtilConstants.convertDateFormat(hashtable.get(Constants.DOB))));
                }
                headerEntity.getProperties().put(Constants.Latitude,
                        new ODataPropertyDefaultImpl(Constants.Latitude, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.Latitude)))));
                headerEntity.getProperties().put(Constants.Longitude,
                        new ODataPropertyDefaultImpl(Constants.Longitude, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.Longitude)))));
                headerEntity.getProperties().put(Constants.ParentID,
                        new ODataPropertyDefaultImpl(Constants.ParentID, hashtable.get(Constants.ParentID)));
                /*headerEntity.getProperties().put(Constants.LOGINID,
                        new ODataPropertyDefaultImpl(Constants.LOGINID, hashtable.get(Constants.LOGINID)));*/
                headerEntity.getProperties().put(Constants.ParentTypeID,
                        new ODataPropertyDefaultImpl(Constants.ParentTypeID, hashtable.get(Constants.ParentTypeID)));
                headerEntity.getProperties().put(Constants.ParentName,
                        new ODataPropertyDefaultImpl(Constants.ParentName, hashtable.get(Constants.ParentName)));
                /*headerEntity.getProperties().put(Constants.Group2,
                        new ODataPropertyDefaultImpl(Constants.Group2, hashtable.get(Constants.Group2)));*/
                if (!hashtable.get(Constants.Anniversary).equalsIgnoreCase("")) {
                    headerEntity.getProperties().put(Constants.Anniversary,
                            new ODataPropertyDefaultImpl(Constants.Anniversary, UtilConstants.convertDateFormat(hashtable.get(Constants.Anniversary))));
                }
                try {
                    headerEntity.getProperties().put(Constants.PartnerMgrGUID,
                            new ODataPropertyDefaultImpl(Constants.PartnerMgrGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID).toUpperCase())));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                headerEntity.getProperties().put(Constants.CPTypeID,
                        new ODataPropertyDefaultImpl(Constants.CPTypeID, hashtable.get(Constants.CPTypeID)));
                headerEntity.getProperties().put(Constants.CPTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.CPTypeDesc, hashtable.get(Constants.CPTypeDesc)));

                headerEntity.getProperties().put(Constants.WeeklyOff,
                        new ODataPropertyDefaultImpl(Constants.WeeklyOff, hashtable.get(Constants.WeeklyOff)));
                headerEntity.getProperties().put(Constants.Tax1,
                        new ODataPropertyDefaultImpl(Constants.Tax1, hashtable.get(Constants.Tax1)));
                headerEntity.getProperties().put(Constants.CPUID,
                        new ODataPropertyDefaultImpl(Constants.CPUID, hashtable.get(Constants.CPUID)));
                headerEntity.getProperties().put(Constants.TaxRegStatus,
                        new ODataPropertyDefaultImpl(Constants.TaxRegStatus, hashtable.get(Constants.TaxRegStatus)));

                // CreateOperation the item Entity

                ODataEntity itemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.CPDMSDivisionEntity);
                try {
                    store.allocateProperties(itemEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < itemhashtable.size(); i++) {
                    HashMap<String, String> singleRow = itemhashtable.get(i);
                    try {
                        store.allocateProperties(itemEntity, PropMode.All);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }
                    itemEntity.getProperties().put(Constants.CPGUID,
                            new ODataPropertyDefaultImpl(Constants.CPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CPGUID).toUpperCase())));
                    itemEntity.getProperties().put(Constants.CP1GUID,
                            new ODataPropertyDefaultImpl(Constants.CP1GUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.CP1GUID).toString().toUpperCase())));
                    itemEntity.getProperties().put(Constants.ParentID,
                            new ODataPropertyDefaultImpl(Constants.ParentID, hashtable.get(Constants.ParentID)));
                    itemEntity.getProperties().put(Constants.ParentName,
                            new ODataPropertyDefaultImpl(Constants.ParentName, hashtable.get(Constants.ParentName)));
                    itemEntity.getProperties().put(Constants.ParentTypeID,
                            new ODataPropertyDefaultImpl(Constants.ParentTypeID, hashtable.get(Constants.ParentTypeID)));
                    itemEntity.getProperties().put(Constants.DMSDivision,
                            new ODataPropertyDefaultImpl(Constants.DMSDivision, singleRow.get(Constants.DMSDivision)));
                    itemEntity.getProperties().put(Constants.SalesPersonMobileNo,
                            new ODataPropertyDefaultImpl(Constants.SalesPersonMobileNo, hashtable.get(Constants.MobileNo)));
                    itemEntity.getProperties().put(Constants.CPTypeID,
                            new ODataPropertyDefaultImpl(Constants.CPTypeID, hashtable.get(Constants.CPTypeID)));
                    itemEntity.getProperties().put(Constants.CPTypeDesc,
                            new ODataPropertyDefaultImpl(Constants.CPTypeDesc, hashtable.get(Constants.CPTypeDesc)));
                    try {
                        itemEntity.getProperties().put(Constants.PartnerMgrGUID,
                                new ODataPropertyDefaultImpl(Constants.PartnerMgrGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID).toUpperCase())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    itemEntity.getProperties().put(Constants.Group1,
                            new ODataPropertyDefaultImpl(Constants.Group1, hashtable.get(Constants.Group1)));
                    itemEntity.getProperties().put(Constants.Group4,
                            new ODataPropertyDefaultImpl(Constants.Group4, hashtable.get(Constants.Group4)));

                    tempArray.add(i, itemEntity);
                }

                ODataEntitySetDefaultImpl itmEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itmEntity.getEntities().add(entity);
                }
                itmEntity.setResourcePath(Constants.CPDMSDivisions);


                ODataNavigationProperty navProp = headerEntity.getNavigationProperty(Constants.CPDMSDivisions);
                navProp.setNavigationContent(itmEntity);
                headerEntity.setNavigationProperty(Constants.CPDMSDivisions, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headerEntity;

    }

    public static void scheduledReqEntity(String resourcePath, OnlineODataStore store) throws ODataContractViolationException {
        OnlineSynLogListener openListener = OnlineSynLogListener.getInstance();
        store = openListener.getStore();
        if (store != null) {
            ODataRequestParamSingleDefaultImpl requestParam = new ODataRequestParamSingleDefaultImpl();
            requestParam.setMode(ODataRequestParamSingle.Mode.Create);
            requestParam.setResourcePath(resourcePath);


            Map<String, String> createHeaders = new HashMap<String, String>();
            createHeaders.put("Content-Type", "application/atom+xml");
            requestParam.getCustomHeaders().putAll(createHeaders);

            try {
                OnlineRequestListener odataReqListener = new OnlineRequestListener(Operation.Create.getValue(), "");
                store.scheduleRequest(requestParam, odataReqListener);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    public static boolean openOnlineStoreForSyncLog(Context context) throws OnlineODataStoreException {
        try {
            //Listener to be invoked when the opening process of an OnlineODataStore object finishes
            OnlineSynLogListener openListener = OnlineSynLogListener.getInstance();
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();

            //The logon configurator uses the information obtained in the registration
            // (i.e endpoint URL, login, etc ) to configure the conversation manager
            IManagerConfigurator configurator = LogonUIFacade.getInstance().getLogonConfigurator(context);
            HttpConversationManager manager = new HttpConversationManager(context);
            configurator.configure(manager);
            //XCSRFTokenRequestFilter implements IRequestFilter
            //Request filter that is allowed to preprocess the request before sending
            XCSRFTokenRequestFilter requestFilter = XCSRFTokenRequestFilter.getInstance(lgCtx);
            XCSRFTokenResponseFilter responseFilter = XCSRFTokenResponseFilter.getInstance(context,
                    requestFilter);
            manager.addFilter(requestFilter);
            manager.addFilter(responseFilter);
            String relayUrlSuffix = lgCtx.getResourcePath();
            try {


                URL url = null;
                String protocol = lgCtx.isHttps() ? "https" : "http";
                if (relayUrlSuffix.equalsIgnoreCase(""))
                    url = new URL("" + protocol + "://" + lgCtx.getHost() + ":" + lgCtx.getPort() + "/" + com.arteriatech.emami.registration.Configuration.SyncLog);
                else {
                    String farmId = lgCtx.getFarmId();
                    url = new URL("" + protocol + "://" + lgCtx.getHost() + ":" + lgCtx.getPort() + "/" + relayUrlSuffix + "/" + farmId + "/" + com.arteriatech.emami.registration.Configuration.SyncLog);

                }
                //Method to open a new online store asynchronously

                OnlineODataStore.open(context, url, manager, openListener, null);
                if (openListener.getError() != null) {
                    throw openListener.getError();
                }
            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
            while (!Constants.IsOnlineStoreSyncLogFailed) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Constants.IsOnlineStoreSyncLogFailed = false;


            if (Constants.onlineStoreSyncLog != null) {
                return true;
            } else {
                return false;
            }
        } catch (OnlineODataStoreException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void createSyncLog(Hashtable<String, String> tableHdr, UIListener uiListener) throws OnlineODataStoreException {
        OnlineSynLogListener openListener = OnlineSynLogListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity cpEntity = createSyncLogEntity(tableHdr, store);
                OnlineRequestListener CPListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);
                ODataRequestParamSingle cpReq = new ODataRequestParamSingleDefaultImpl();
                cpReq.setMode(ODataRequestParamSingle.Mode.Create);
                cpReq.setResourcePath(cpEntity.getResourcePath());
                cpReq.setPayload(cpEntity);
                store.scheduleRequest(cpReq, CPListener);
            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
        //END
    }

    private static ODataEntity createSyncLogEntity(Hashtable<String, String> hashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity headerEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                // CreateOperation the parent Entity
                headerEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.SyncLogInsertEntity);
                headerEntity.setResourcePath(Constants.SyncLogInserts, Constants.SyncLogInserts);
                try {
                    store.allocateProperties(headerEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                store.allocateNavigationProperties(headerEntity);

                headerEntity.getProperties().put(Constants.Guid,
                        new ODataPropertyDefaultImpl(Constants.Guid, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.Guid).toUpperCase())));
                headerEntity.getProperties().put(Constants.Userid,
                        new ODataPropertyDefaultImpl(Constants.Userid, hashtable.get(Constants.Userid)));
                headerEntity.getProperties().put(Constants.Name,
                        new ODataPropertyDefaultImpl(Constants.Name, hashtable.get(Constants.Name)));
                headerEntity.getProperties().put(Constants.SalesPersonMobileNo,
                        new ODataPropertyDefaultImpl(Constants.SalesPersonMobileNo, hashtable.get(Constants.SalesPersonMobileNo)));

                if (!hashtable.get(Constants.StartDate).equalsIgnoreCase("")) {
                    headerEntity.getProperties().put(Constants.StartDate,
                            new ODataPropertyDefaultImpl(Constants.StartDate, Constants.convertDateTimeFormat(hashtable.get(Constants.StartDate))));
                }
                headerEntity.getProperties().put(Constants.STARTTIME,
                        new ODataPropertyDefaultImpl(Constants.STARTTIME, hashtable.get(Constants.STARTTIME)));
                headerEntity.getProperties().put(Constants.SyncType,
                        new ODataPropertyDefaultImpl(Constants.SyncType, hashtable.get(Constants.SyncType)));

                headerEntity.getProperties().put(Constants.AlertAt,
                        new ODataPropertyDefaultImpl(Constants.AlertAt, hashtable.get(Constants.AlertAt)));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return headerEntity;

    }

    public static boolean openOnlineStoreForTechincalCache(Context context) throws OnlineODataStoreException {
        try {
            //Listener to be invoked when the opening process of an OnlineODataStore object finishes
            OnlineStoreCacheListner openListener = OnlineStoreCacheListner.getInstance();
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();

            //The logon configurator uses the information obtained in the registration
            // (i.e endpoint URL, login, etc ) to configure the conversation manager
            IManagerConfigurator configurator = LogonUIFacade.getInstance().getLogonConfigurator(context);
            HttpConversationManager manager = new HttpConversationManager(context);
            configurator.configure(manager);

            OnlineODataStore.OnlineStoreOptions onlineOptions = new OnlineODataStore.OnlineStoreOptions();


            onlineOptions.useCache = true;//if true technical cache is enabled
            onlineOptions.cacheEncryptionKey = Constants.EncryptKey;

            //XCSRFTokenRequestFilter implements IRequestFilter
            //Request filter that is allowed to preprocess the request before sending
            XCSRFTokenRequestFilter requestFilter = XCSRFTokenRequestFilter.getInstance(lgCtx);
            XCSRFTokenResponseFilter responseFilter = XCSRFTokenResponseFilter.getInstance(context,
                    requestFilter);
            manager.addFilter(requestFilter);
            manager.addFilter(responseFilter);
            String relayUrlSuffix = lgCtx.getResourcePath();
            try {


                URL url = null;
                String protocol = lgCtx.isHttps() ? "https" : "http";
                if (relayUrlSuffix.equalsIgnoreCase(""))
                    url = new URL("" + protocol + "://" + lgCtx.getHost() + ":" + lgCtx.getPort() + "/" + com.arteriatech.emami.registration.Configuration.MustSell);
                else {
                    String farmId = lgCtx.getFarmId();
                    url = new URL("" + protocol + "://" + lgCtx.getHost() + ":" + lgCtx.getPort() + "/" + relayUrlSuffix + "/" + farmId + "/" + com.arteriatech.emami.registration.Configuration.MustSell);

                }
                //Method to open a new online store asynchronously

                OnlineODataStore.open(context, url, manager, openListener, onlineOptions);
                if (openListener.getError() != null) {
                    throw openListener.getError();
                }
            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
            while (!Constants.IsOnlineStoreMustSellFailed) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Constants.IsOnlineStoreMustSellFailed = false;


            if (Constants.onlineStoreMustCell != null) {
                return true;
            } else {
                return false;
            }
        } catch (OnlineODataStoreException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void createInvEntity(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, UIListener uiListener) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity soCreateEntity = createInvCreateEntity(table, itemtable, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                String ssoGUID32 = table.get(Constants.InvoiceGUID).replace("-", "");

                String soCreatedOn = table.get(Constants.CreatedOn);
                String soCreatedAt = table.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(soCreatedOn) + Constants.T + UtilConstants.convertTimeOnly(soCreatedAt);

                Map<String, String> createHeaders = new HashMap<String, String>();
                createHeaders.put(Constants.RequestID, ssoGUID32);
                createHeaders.put(Constants.RepeatabilityCreation, mStrDateTime);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(soCreateEntity.getResourcePath());
                collectionReq.setPayload(soCreateEntity);
                collectionReq.getCustomHeaders().putAll(createHeaders);

                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
        //END

    }

    /**
     * Create Entity for collection creation
     *
     * @throws ODataParserException
     */
    private static ODataEntity createInvCreateEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.InvoiceEntity);

                newHeaderEntity.setResourcePath(Constants.SSINVOICES, Constants.SSINVOICES);

                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                newHeaderEntity.getProperties().put(Constants.InvoiceGUID,
                        new ODataPropertyDefaultImpl(Constants.InvoiceGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.InvoiceGUID))));

                try {
                    newHeaderEntity.getProperties().put(Constants.RefDocGUID,
                            new ODataPropertyDefaultImpl(Constants.RefDocGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.RefDocGUID))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    newHeaderEntity.getProperties().put(Constants.BeatGUID,
                            new ODataPropertyDefaultImpl(Constants.BeatGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.BeatGUID))));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                newHeaderEntity.getProperties().put(Constants.PaymentModeID,
                        new ODataPropertyDefaultImpl(Constants.PaymentModeID, hashtable.get(Constants.PaymentModeID)));
                newHeaderEntity.getProperties().put(Constants.PaymentModeDesc,
                        new ODataPropertyDefaultImpl(Constants.PaymentModeDesc, hashtable.get(Constants.PaymentModeDesc)));
                newHeaderEntity.getProperties().put(Constants.TestRun,
                        new ODataPropertyDefaultImpl(Constants.TestRun, hashtable.get(Constants.TestRun)));
                try {
                    newHeaderEntity.getProperties().put(Constants.SoldToCPGUID,
                            new ODataPropertyDefaultImpl(Constants.SoldToCPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SoldToCPGUID))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                newHeaderEntity.getProperties().put(Constants.SoldToID,
                        new ODataPropertyDefaultImpl(Constants.SoldToID, hashtable.get(Constants.SoldToID)));
                newHeaderEntity.getProperties().put(Constants.SoldToTypeID,
                        new ODataPropertyDefaultImpl(Constants.SoldToTypeID, hashtable.get(Constants.SoldToTypeID)));
                newHeaderEntity.getProperties().put(Constants.DmsDivision,
                        new ODataPropertyDefaultImpl(Constants.DmsDivision, hashtable.get(Constants.DmsDivision)));
                newHeaderEntity.getProperties().put(Constants.DmsDivisionDesc,
                        new ODataPropertyDefaultImpl(Constants.DmsDivisionDesc, hashtable.get(Constants.DmsDivisionDesc)));

                newHeaderEntity.getProperties().put(Constants.InvoiceTypeID,
                        new ODataPropertyDefaultImpl(Constants.InvoiceTypeID, hashtable.get(Constants.InvoiceTypeID)));
                newHeaderEntity.getProperties().put(Constants.InvoiceTypeDesc,
                        new ODataPropertyDefaultImpl(Constants.InvoiceTypeDesc, hashtable.get(Constants.InvoiceTypeDesc)));
                newHeaderEntity.getProperties().put(Constants.InvoiceDate,
                        new ODataPropertyDefaultImpl(Constants.InvoiceDate, UtilConstants.convertDateFormat(hashtable.get(Constants.InvoiceDate))));

                newHeaderEntity.getProperties().put(Constants.GrossAmount,
                        new ODataPropertyDefaultImpl(Constants.GrossAmount, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.GrossAmount)))));
                newHeaderEntity.getProperties().put(Constants.NetAmount,
                        new ODataPropertyDefaultImpl(Constants.NetAmount, BigDecimal.valueOf(Double.parseDouble(hashtable.get(Constants.NetAmount)))));

                newHeaderEntity.getProperties().put(Constants.Currency,
                        new ODataPropertyDefaultImpl(Constants.Currency, hashtable.get(Constants.Currency)));
                newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, hashtable.get(Constants.CPGUID).replace("-", "")));
                newHeaderEntity.getProperties().put(Constants.CPName,
                        new ODataPropertyDefaultImpl(Constants.CPName, hashtable.get(Constants.CPName)));
                newHeaderEntity.getProperties().put(Constants.CPTypeID,
                        new ODataPropertyDefaultImpl(Constants.CPTypeID, hashtable.get(Constants.CPTypeID)));
                if (!hashtable.get(Constants.SPGUID).equalsIgnoreCase("")) {
                    newHeaderEntity.getProperties().put(Constants.SPGUID,
                            new ODataPropertyDefaultImpl(Constants.SPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.SPGUID)))
                    );
                    newHeaderEntity.getProperties().put(Constants.SPNo,
                            new ODataPropertyDefaultImpl(Constants.SPNo, hashtable.get(Constants.SPNo)));
                }

                newHeaderEntity.getProperties().put(Constants.SPName,
                        new ODataPropertyDefaultImpl(Constants.SPName, hashtable.get(Constants.SPName)));

                newHeaderEntity.getProperties().put(Constants.PONo,
                        new ODataPropertyDefaultImpl(Constants.PONo, hashtable.get(Constants.PONo)));

                try {
                    newHeaderEntity.getProperties().put(Constants.PODate,
                            new ODataPropertyDefaultImpl(Constants.PODate, UtilConstants.convertDateFormat(hashtable.get(Constants.PODate))));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    newHeaderEntity.getProperties().put(Constants.DeliveryDate,
                            new ODataPropertyDefaultImpl(Constants.DeliveryDate, UtilConstants.convertDateFormat(hashtable.get(Constants.DeliveryDate))));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    newHeaderEntity.getProperties().put(Constants.DeliveryPerson,
                            new ODataPropertyDefaultImpl(Constants.DeliveryPerson, hashtable.get(Constants.DeliveryPerson)));

                    newHeaderEntity.getProperties().put(Constants.DriverName,
                            new ODataPropertyDefaultImpl(Constants.DriverName, hashtable.get(Constants.DriverName)));

                    newHeaderEntity.getProperties().put(Constants.DriverMobile,
                            new ODataPropertyDefaultImpl(Constants.DriverMobile, hashtable.get(Constants.DriverMobile)));
                    newHeaderEntity.getProperties().put(Constants.TransVhclId,
                            new ODataPropertyDefaultImpl(Constants.TransVhclId, hashtable.get(Constants.TransVhclId)));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                int incremntVal = 0;
                for (int incrementVal = 0; incrementVal < itemhashtable.size(); incrementVal++) {

                    HashMap<String, String> singleRow = itemhashtable.get(incrementVal);
                    if (!singleRow.get(Constants.IsfreeGoodsItem).equalsIgnoreCase("X")) {
                        incremntVal = incrementVal + 1;

                        newItemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.InvoiceItemEntity);

                        newItemEntity.setResourcePath(Constants.SSInvoiceItemDetails + "(" + incremntVal + ")", Constants.SSInvoiceItemDetails + "(" + incremntVal + ")");
                        try {
                            store.allocateProperties(newItemEntity, PropMode.Keys);
                        } catch (ODataException e) {
                            e.printStackTrace();
                        }


                        newItemEntity.getProperties().put(Constants.InvoiceItemGUID,
                                new ODataPropertyDefaultImpl(Constants.InvoiceItemGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.InvoiceItemGUID))));

                        newItemEntity.getProperties().put(Constants.InvoiceGUID,
                                new ODataPropertyDefaultImpl(Constants.InvoiceGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.InvoiceGUID))));

                        newItemEntity.getProperties().put(Constants.ItemNo,
                                new ODataPropertyDefaultImpl(Constants.ItemNo, singleRow.get(Constants.ItemNo)));


                        newItemEntity.getProperties().put(Constants.MaterialNo,
                                new ODataPropertyDefaultImpl(Constants.MaterialNo, singleRow.get(Constants.MaterialNo)));

                        newItemEntity.getProperties().put(Constants.MaterialDesc,
                                new ODataPropertyDefaultImpl(Constants.MaterialDesc, singleRow.get(Constants.MaterialDesc)));

                        newItemEntity.getProperties().put(Constants.OrderMaterialGrp,
                                new ODataPropertyDefaultImpl(Constants.OrderMaterialGrp, singleRow.get(Constants.OrderMaterialGrp)));

                        /*newItemEntity.getProperties().put(Constants.OrderMaterialGrpDesc,
                                new ODataPropertyDefaultImpl(Constants.OrderMaterialGrpDesc, singleRow.get(Constants.OrderMaterialGrpDesc)));*/


                        newItemEntity.getProperties().put(Constants.Currency,
                                new ODataPropertyDefaultImpl(Constants.Currency, singleRow.get(Constants.Currency)));

                        newItemEntity.getProperties().put(Constants.UOM,
                                new ODataPropertyDefaultImpl(Constants.UOM, singleRow.get(Constants.UOM)));

                        /*newItemEntity.getProperties().put(Constants.NetPrice,
                                new ODataPropertyDefaultImpl(Constants.NetPrice, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.NetPrice)))));*/

                        try {
                            newItemEntity.getProperties().put(Constants.UnitPrice,
                                    new ODataPropertyDefaultImpl(Constants.UnitPrice, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.UnitPrice)))));
                            newItemEntity.getProperties().put(Constants.MRP,
                                    new ODataPropertyDefaultImpl(Constants.MRP, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.MRP)))));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        newItemEntity.getProperties().put(Constants.Quantity,
                                new ODataPropertyDefaultImpl(Constants.Quantity, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.Quantity)))));

                       /* newItemEntity.getProperties().put(Constants.PriDiscount,
                                new ODataPropertyDefaultImpl(Constants.PriDiscount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.PriDiscount)))));

                        newItemEntity.getProperties().put(Constants.SecDiscount,
                                new ODataPropertyDefaultImpl(Constants.SecDiscount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.SecDiscount)))));

                        newItemEntity.getProperties().put(Constants.CashDiscount,
                                new ODataPropertyDefaultImpl(Constants.CashDiscount, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.CashDiscount)))));*/

                        newItemEntity.getProperties().put(Constants.PrimaryTradeDis,
                                new ODataPropertyDefaultImpl(Constants.PrimaryTradeDis, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.PrimaryTradeDis)))));

                        newItemEntity.getProperties().put(Constants.SecondaryTradeDisc,
                                new ODataPropertyDefaultImpl(Constants.SecondaryTradeDisc, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.SecondaryTradeDisc)))));

                        try {
                            newItemEntity.getProperties().put(Constants.CashDiscountPer,
                                    new ODataPropertyDefaultImpl(Constants.CashDiscountPer, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.CashDiscountPer)))));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                       /* newItemEntity.getProperties().put(Constants.CashDiscountPerc,
                                new ODataPropertyDefaultImpl(Constants.CashDiscountPerc, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.CashDiscountPerc)))));

                        newItemEntity.getProperties().put(Constants.TAX,
                                new ODataPropertyDefaultImpl(Constants.TAX, BigDecimal.valueOf(Double.parseDouble(singleRow.get(Constants.TAX)))));*/

                        if (!singleRow.get(Constants.MFD).equalsIgnoreCase("")) {
                            newItemEntity.getProperties().put(Constants.MFD,
                                    new ODataPropertyDefaultImpl(Constants.MFD, UtilConstants.convertDateFormat(singleRow.get(Constants.MFD))));
                        }

                        try {
                            if (!singleRow.get(Constants.ExpiryDate).equalsIgnoreCase("")) {
                                newItemEntity.getProperties().put(Constants.ExpiryDate,
                                        new ODataPropertyDefaultImpl(Constants.ExpiryDate, UtilConstants.convertDateFormat(singleRow.get(Constants.ExpiryDate))));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        newItemEntity.getProperties().put(Constants.ISFreeGoodsItem,
                                new ODataPropertyDefaultImpl(Constants.ISFreeGoodsItem, singleRow.get(Constants.IsfreeGoodsItem)));

                        newItemEntity.getProperties().put(Constants.Batch,
                                new ODataPropertyDefaultImpl(Constants.Batch, singleRow.get(Constants.Batch)));

                       /* newItemEntity.getProperties().put(Constants.TransRefTypeID,
                                new ODataPropertyDefaultImpl(Constants.TransRefTypeID, singleRow.get(Constants.TransRefTypeID)));

                        newItemEntity.getProperties().put(Constants.TransRefNo,
                                new ODataPropertyDefaultImpl(Constants.TransRefNo, singleRow.get(Constants.TransRefNo)));

                        newItemEntity.getProperties().put(Constants.TransRefItemNo,
                                new ODataPropertyDefaultImpl(Constants.TransRefItemNo, singleRow.get(Constants.TransRefItemNo)));*/

                        try {
                            newItemEntity.getProperties().put(Constants.RefDocItmGUID,
                                    new ODataPropertyDefaultImpl(Constants.RefDocItmGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.RefDocItmGUID))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            newItemEntity.getProperties().put(Constants.StockRefGUID,
                                    new ODataPropertyDefaultImpl(Constants.StockRefGUID, ODataGuidDefaultImpl.initWithString32(singleRow.get(Constants.StockRefGUID))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        tempArray.add(incrementVal, newItemEntity);

                    }
                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.SSInvoiceItemDetails);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.SSInvoiceItemDetails);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.SSInvoiceItemDetails, navProp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }

    public static void createOutletSurvey(Hashtable<String, String> table, ArrayList<HashMap<String, String>> itemtable, ArrayList<HashMap<String, String>> itemtable1, UIListener uiListener) throws OnlineODataStoreException {
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();

        if (store != null) {
            try {
                //Creates the entity payload
                ODataEntity outletSurveyCreateEntity = createOutletSurveyEntity(table, itemtable, itemtable1, store);

                OnlineRequestListener collectionListener = new OnlineRequestListener(Operation.Create.getValue(), uiListener);

                String fipGUID32 = table.get(Constants.CPMKTGUID).replace("-", "");

                String collCreatedOn = table.get(Constants.CreatedOn);
                String collCreatedAt = table.get(Constants.CreatedAt);

                String mStrDateTime = UtilConstants.getReArrangeDateFormat(collCreatedOn) + Constants.T + UtilConstants.convertTimeOnly(collCreatedAt);

                Map<String, String> createHeaders = new HashMap<String, String>();
                createHeaders.put(Constants.RequestID, fipGUID32);
                createHeaders.put(Constants.RepeatabilityCreation, mStrDateTime);

                ODataRequestParamSingle collectionReq = new ODataRequestParamSingleDefaultImpl();
                collectionReq.setMode(ODataRequestParamSingle.Mode.Create);
                collectionReq.setResourcePath(outletSurveyCreateEntity.getResourcePath());
                collectionReq.setPayload(outletSurveyCreateEntity);
                collectionReq.getCustomHeaders().putAll(createHeaders);
                store.scheduleRequest(collectionReq, collectionListener);

            } catch (Exception e) {
                throw new OnlineODataStoreException(e);
            }
        }
        //END

    }

    private static ODataEntity createOutletSurveyEntity(Hashtable<String, String> hashtable, ArrayList<HashMap<String, String>> itemhashtable, ArrayList<HashMap<String, String>> itemhashtable1, OnlineODataStore store) throws ODataParserException {
        ODataEntity newHeaderEntity = null;
        ODataEntity newItemEntity = null;
        ODataEntity newItemEntity1 = null;
        ArrayList<ODataEntity> tempArray = new ArrayList();
        ArrayList<ODataEntity> tempArray1 = new ArrayList();
        try {
            if (hashtable != null) {
                newHeaderEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.CPMarketEntity);

                newHeaderEntity.setResourcePath(Constants.CPMarketSet, Constants.CPMarketSet);

                try {
                    store.allocateProperties(newHeaderEntity, PropMode.All);
                } catch (ODataException e) {
                    e.printStackTrace();
                }
                //If available, it populates the navigation properties of an OData Entity
                store.allocateNavigationProperties(newHeaderEntity);

                newHeaderEntity.getProperties().put(Constants.CPMKTGUID,
                        new ODataPropertyDefaultImpl(Constants.CPMKTGUID, ODataGuidDefaultImpl.initWithString36(hashtable.get(Constants.CPMKTGUID))));

                try {
                    newHeaderEntity.getProperties().put(Constants.CPGUID,
                            new ODataPropertyDefaultImpl(Constants.CPGUID, hashtable.get(Constants.CPGUID)));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                /*newHeaderEntity.getProperties().put(Constants.CPGUID,
                        new ODataPropertyDefaultImpl(Constants.CPGUID, ODataGuidDefaultImpl.initWithString32(hashtable.get(Constants.CPGUID))));*/


                int incremntVal = 0;
                for (int i = 0; i < itemhashtable.size(); i++) {

                    HashMap<String, String> singleRow = itemhashtable.get(i);

                    incremntVal = i + 1;

                    newItemEntity = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.CPBusinessItemEntity);

                    newItemEntity.setResourcePath(Constants.CPBusinessSet + "(" + incremntVal + ")", Constants.CPBusinessSet + "(" + incremntVal + ")");
                    try {
                        store.allocateProperties(newItemEntity, PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }


                    newItemEntity.getProperties().put(Constants.CPBUSGUID,
                            new ODataPropertyDefaultImpl(Constants.CPBUSGUID, ODataGuidDefaultImpl.initWithString36(singleRow.get(Constants.CPBUSGUID))));

                    newItemEntity.getProperties().put(Constants.CPGUID,
                            new ODataPropertyDefaultImpl(Constants.CPGUID, singleRow.get(Constants.CPGUID)));
                    newItemEntity.getProperties().put(Constants.AllBusinessDesc,
                            new ODataPropertyDefaultImpl(Constants.AllBusinessDesc, singleRow.get(Constants.AllBusinessDesc)));

                    newItemEntity.getProperties().put(Constants.AllBusinessID,
                            new ODataPropertyDefaultImpl(Constants.AllBusinessID, singleRow.get(Constants.AllBusinessID)));

                    newItemEntity.getProperties().put(Constants.CPMKTGUID,
                            new ODataPropertyDefaultImpl(Constants.CPMKTGUID, ODataGuidDefaultImpl.initWithString36(singleRow.get(Constants.CPMKTGUID))));

                    newItemEntity.getProperties().put(Constants.AnnualTurnover,
                            new ODataPropertyDefaultImpl(Constants.AnnualTurnover, BigDecimal.valueOf(Double.parseDouble("1"))));

                    tempArray.add(i, newItemEntity);

                }

                for (int i = 0; i < itemhashtable1.size(); i++) {

                    HashMap<String, String> singleRow = itemhashtable1.get(i);

                    incremntVal = i + 1;

                    newItemEntity1 = new ODataEntityDefaultImpl(Constants.getNameSpaceOnline(store) + "" + Constants.CompetitorSaleEntity);

                    newItemEntity1.setResourcePath(Constants.CompetitorSales + "(" + incremntVal + ")", Constants.CompetitorSales + "(" + incremntVal + ")");
                    try {
                        store.allocateProperties(newItemEntity1, PropMode.Keys);
                    } catch (ODataException e) {
                        e.printStackTrace();
                    }


                    newItemEntity1.getProperties().put(Constants.CompSalesGUID,
                            new ODataPropertyDefaultImpl(Constants.CompSalesGUID, ODataGuidDefaultImpl.initWithString36(singleRow.get(Constants.CompSalesGUID))));

                    newItemEntity1.getProperties().put(Constants.CPGUID,
                            new ODataPropertyDefaultImpl(Constants.CPGUID, singleRow.get(Constants.CPGUID)));

                    newItemEntity1.getProperties().put(Constants.CPMKTGUID,
                            new ODataPropertyDefaultImpl(Constants.CPMKTGUID, ODataGuidDefaultImpl.initWithString36(singleRow.get(Constants.CPMKTGUID))));

                    tempArray1.add(i, newItemEntity1);

                }

                ODataEntitySetDefaultImpl itemEntity = new ODataEntitySetDefaultImpl(tempArray.size(), null, null);
                for (ODataEntity entity : tempArray) {
                    itemEntity.getEntities().add(entity);
                }
                itemEntity.setResourcePath(Constants.CPBusinessSet);
                ODataEntitySetDefaultImpl itemEntity1 = new ODataEntitySetDefaultImpl(tempArray1.size(), null, null);
                for (ODataEntity entity : tempArray1) {
                    itemEntity1.getEntities().add(entity);
                }
                itemEntity1.setResourcePath(Constants.CompetitorSales);

                ODataNavigationProperty navProp = newHeaderEntity.getNavigationProperty(Constants.CPBusinessSet);
                navProp.setNavigationContent(itemEntity);
                newHeaderEntity.setNavigationProperty(Constants.CPBusinessSet, navProp);

                ODataNavigationProperty navProp1 = newHeaderEntity.getNavigationProperty(Constants.CompetitorSales);
                navProp1.setNavigationContent(itemEntity1);
                newHeaderEntity.setNavigationProperty(Constants.CompetitorSales, navProp1);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newHeaderEntity;
    }
}
