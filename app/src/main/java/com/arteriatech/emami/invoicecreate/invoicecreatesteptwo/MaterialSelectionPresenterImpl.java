package com.arteriatech.emami.invoicecreate.invoicecreatesteptwo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.finance.InvoiceBean;
import com.arteriatech.emami.invoicecreate.CollectionCreatePresenter;
import com.arteriatech.emami.mbo.InvoiceCreateBean;
import com.arteriatech.emami.mbo.ValueHelpBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.reports.OutstandingBean;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by e10526 on 21-04-2018.
 */

public class MaterialSelectionPresenterImpl implements CollectionCreatePresenter, OnlineODataInterface {
    ArrayList<HashMap<String, String>> arrtable;
    double mDoubleTotalInvSum = 0.0;
    private Context mContext;
    private Activity mActivity;
    private MaterialSelView collCreateView;
    private boolean isSessionRequired;
    private ArrayList<ValueHelpBean> alCollRefType = new ArrayList<>();
    private ArrayList<ValueHelpBean> alCollPaymentMode = new ArrayList<>();
    private ArrayList<ValueHelpBean> alCollBankNames = new ArrayList<>();
    private Hashtable<String, String> masterHeaderTable = new Hashtable<>();
    private ODataDuration mStartTimeDuration;
    private String[][] mArrayDistributors, mArraySPValues = null;
    private InvoiceCreateBean invCreateBean = null;
    private int totalRequest = 0;
    private int currentRequest = 0;
    private ArrayList<OutstandingBean> alOutstandingsBean;
    private ArrayList<InvoiceBean> alInvoiceList = new ArrayList<>();
    private BigDecimal totalOutVal = new BigDecimal(0.0);

    public MaterialSelectionPresenterImpl(Context mContext, MaterialSelView collCreateView, boolean isSessionRequired, Activity mActivity, InvoiceCreateBean invCreateBean) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.collCreateView = collCreateView;
        this.isSessionRequired = isSessionRequired;
        this.mStartTimeDuration = UtilConstants.getOdataDuration();
        this.invCreateBean = invCreateBean;
    }


    @Override
    public void onStart() {
        requestCollType();
    }

    @Override
    public void getInvoices() {
        if (collCreateView != null) {
            collCreateView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        currentRequest = 0;
        totalRequest = 1;

//        String mStrInvQry = Constants.SSOutstandingInvoices + "?$orderby=" + Constants.InvoiceNo + " asc&$filter=SoldToID eq '" + invCreateBean.getCPNo() + "' and StatusID eq '03'";
//        ConstantsUtils.onlineRequest(mContext, mStrInvQry, isSessionRequired, 4, ConstantsUtils.SESSION_HEADER, this, false);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public boolean validateFields(InvoiceCreateBean collectionBean, String syncType) {
        boolean isNotError = true;
        if (TextUtils.isEmpty(collectionBean.getPaymentModeID())) {
            collCreateView.errorPaymentMode("Select Payment Mode");
            isNotError = false;
        }
        /*if (TextUtils.isEmpty(collectionBean.getPaymentModeID())) {
            collCreateView.errorPaymentMode("Select Payment Mode");
            isNotError = false;
        }

        if (!TextUtils.isEmpty(collectionBean.getPaymentModeID()) && !collectionBean.getPaymentModeID().equalsIgnoreCase(Constants.str_04)) {
            if (TextUtils.isEmpty(collectionBean.getBankID())) {
                collCreateView.errorBankName(mContext.getString(R.string.coll_val_bank));
                isNotError = false;
            }
            if (TextUtils.isEmpty(collectionBean.getChequeDate())) {
                collCreateView.errorChequeDate(mContext.getString(R.string.coll_val_date));
                isNotError = false;
            }
            if (collectionBean.getPaymentModeID().equalsIgnoreCase("03")) {
                if (TextUtils.isEmpty(collectionBean.getUTRNo())) {
                    collCreateView.errorUTRNoOrChequeDD(mContext.getString(R.string.coll_val_utr));
                    isNotError = false;
                } else if (collectionBean.getUTRNo().trim().length() < 8 || collectionBean.getUTRNo().trim().length() > 22) {
                    collCreateView.errorUTRNoOrChequeDD(mContext.getString(R.string.coll_val_utr_valid));
                    isNotError = false;
                }

            } else if (collectionBean.getPaymentModeID().equalsIgnoreCase("02")) {
                if (TextUtils.isEmpty(collectionBean.getUTRNo())) {
                    collCreateView.errorUTRNoOrChequeDD(mContext.getString(R.string.coll_val_card));
                    isNotError = false;
                } else if (collectionBean.getUTRNo().trim().length() < 12 || collectionBean.getUTRNo().trim().length() > 19) {
                    collCreateView.errorUTRNoOrChequeDD(mContext.getString(R.string.coll_val_card_valid));
                    isNotError = false;
                }
            } else if (collectionBean.getPaymentModeID().equalsIgnoreCase("01")) {
                if (TextUtils.isEmpty(collectionBean.getUTRNo())) {
                    collCreateView.errorUTRNoOrChequeDD(mContext.getString(R.string.coll_val_cheque));
                    isNotError = false;
                } else if (collectionBean.getUTRNo().trim().length() != 6) {
                    collCreateView.errorUTRNoOrChequeDD(mContext.getString(R.string.coll_val_cheque_valid));
                    isNotError = false;
                }
            }
        }*/


       /* if (TextUtils.isEmpty(collectionBean.getAmount())) {
            collCreateView.errorAmount("Enter Amount");
            isNotError = false;
        } else {
            if (!TextUtils.isEmpty(collectionBean.getRefTypeID()) && collectionBean.getRefTypeID().equalsIgnoreCase("01")) {
                if (collectionBean.getAmount().equalsIgnoreCase("") || collectionBean.getAmount().equalsIgnoreCase(".")) {
                    isNotError = false;
                    collCreateView.errorCollScreen(mContext.getString(R.string.alert_enter_valid_amount));
                } else if (Double.parseDouble(collectionBean.getAmount()) <= 0) {
                    isNotError = false;
                    collCreateView.errorCollScreen(mContext.getString(R.string.alert_enter_valid_amount));
                } else if (Double.parseDouble(collectionBean.getOutstandingAmount()) <= 0) {
                    isNotError = false;
                    collCreateView.errorCollScreen(mContext.getString(R.string.alert_enter_outstnding_amount_not_there));
                } else if (Double.parseDouble(collectionBean.getOutstandingAmount()) < Double.parseDouble(collectionBean.getAmount())) {
                    isNotError = false;
                    collCreateView.errorCollScreen(mContext.getString(R.string.alert_amt_greater_than_out_amt));
                }
            } else {
                if (collectionBean.getAmount().equalsIgnoreCase("") || collectionBean.getAmount().equalsIgnoreCase(".")) {
                    isNotError = false;
                    collCreateView.errorCollScreen(mContext.getString(R.string.alert_enter_valid_amount));
                } else if (Double.parseDouble(collectionBean.getAmount()) <= 0) {
                    isNotError = false;
                    collCreateView.errorCollScreen(mContext.getString(R.string.alert_enter_valid_amount));
                }
            }
        }*/
        return isNotError;
    }




    @Override
    public void onAsignData(String save, String strRefType, InvoiceCreateBean collectionBean, String comingFrom) {
        if (comingFrom.equalsIgnoreCase(Constants.str_01)) {
            assignDataToHashTable("", strRefType, collectionBean);
        } else {
            assignDataVar("", strRefType, collectionBean);
        }

    }


    @Override
    public void onSaveData() {
        getLocation();
    }

    private void assignDataToHashTable(String save, String strRefType, InvoiceCreateBean collectionBean) {
//        String doc_no = (System.currentTimeMillis() + "");
//
//        arrtable = new ArrayList<HashMap<String, String>>();
//
//        HashMap<String, String> singleItem = new HashMap<String, String>();
//
//        GUID guidItem = GUID.newRandom();
//        GUID guid = GUID.newRandom();
//
//        singleItem.put(Constants.FIPItemGUID, guidItem.toString());
//        singleItem.put(Constants.FIPGUID, guid.toString());
//
//        singleItem.put(Constants.ReferenceID, "");
//        singleItem.put(Constants.FIPAmount, String.valueOf(ConstantsUtils.decimalRoundOff(new BigDecimal(collectionBean.getAmount()),2)));
//        singleItem.put(Constants.ReferenceTypeID, collectionBean.getRefTypeID());
//        singleItem.put(Constants.ReferenceTypeDesc, collectionBean.getRefTypeDesc());
//        singleItem.put(Constants.DebitCredit, Constants.H);
//        singleItem.put(Constants.Currency, collectionBean.getCurrency());
//        singleItem.put(Constants.InstrumentNo, collectionBean.getInstrumentNo());
//        if (!collectionBean.getRefTypeID().equalsIgnoreCase(Constants.str_05)) {
//            singleItem.put(Constants.PaymentModeID, collectionBean.getPaymentModeID());
//            singleItem.put(Constants.PaymetModeDesc, collectionBean.getPaymentModeDesc());
//        } else {
//            singleItem.put(Constants.PaymentModeID, "");
//            singleItem.put(Constants.PaymetModeDesc, "");
//        }
//        singleItem.put(Constants.FIPDate, collectionBean.getCollDate());
//
//        singleItem.put(Constants.BeatGUID, collectionBean.getBeatGuid());
//        if (!collectionBean.getPaymentModeID().equalsIgnoreCase(Constants.str_04)) {
//            singleItem.put(Constants.InstrumentDate, collectionBean.getInstrumentDate());
//        } else {
//            singleItem.put(Constants.InstrumentDate, "");
//        }
//        singleItem.put(Constants.CashDiscountPercentage, "");
//        singleItem.put(Constants.CashDiscount, "");
//        arrtable.add(singleItem);
//
//        masterHeaderTable = new Hashtable();
//
//        masterHeaderTable.put(Constants.FIPDocNo, doc_no);
//
//        masterHeaderTable.put(Constants.CPNo, collectionBean.getCPNo());
//        masterHeaderTable.put(Constants.BankID, collectionBean.getBankID());
//        masterHeaderTable.put(Constants.BankName, collectionBean.getBankName());
//        masterHeaderTable.put(Constants.InstrumentNo, collectionBean.getUTRNo());
//        masterHeaderTable.put(Constants.Amount, Double.parseDouble(collectionBean.getAmount()) + "");
//        masterHeaderTable.put(Constants.Remarks, collectionBean.getRemarks());
//
//        masterHeaderTable.put(Constants.FIPDocType, Constants.str_05);
//
//        if (!collectionBean.getRefTypeID().equalsIgnoreCase(Constants.str_05)) {
//            masterHeaderTable.put(Constants.PaymentModeID, collectionBean.getPaymentModeID());
//            masterHeaderTable.put(Constants.PaymentModeDesc, collectionBean.getPaymentModeDesc());
//        } else {
//            masterHeaderTable.put(Constants.PaymentModeID, "");
//            masterHeaderTable.put(Constants.PaymentModeDesc, "");
//        }
//        masterHeaderTable.put(Constants.SPGuid, collectionBean.getSPGUID());
//        masterHeaderTable.put(Constants.FIPDate, collectionBean.getCollDate());
//
//        if (!collectionBean.getPaymentModeID().equalsIgnoreCase(Constants.str_04)) {
//            masterHeaderTable.put(Constants.InstrumentDate, collectionBean.getChequeDate());
//        } else {
//            masterHeaderTable.put(Constants.InstrumentDate, "");
//        }
//        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
//        String loginIdVal = sharedPreferences.getString(Constants.username, "");
//
//        masterHeaderTable.put(Constants.LOGINID, "");
//        masterHeaderTable.put(Constants.BranchName, collectionBean.getBranchName());
//
//        masterHeaderTable.put(Constants.Source, Constants.Source_SFA);
//
//        masterHeaderTable.put(Constants.CPName, collectionBean.getCPName());
//        masterHeaderTable.put(Constants.ParentNo, collectionBean.getParentID());
//        masterHeaderTable.put(Constants.SPNo, collectionBean.getSpNo());
//        masterHeaderTable.put(Constants.SPFirstName, collectionBean.getSpFirstName());
//        masterHeaderTable.put(Constants.Currency, collectionBean.getCurrency());
//        masterHeaderTable.put(Constants.CPTypeID, Constants.str_02);
//        masterHeaderTable.put(Constants.ReferenceTypeDesc, collectionBean.getRefTypeDesc());
//        masterHeaderTable.put(Constants.ReferenceTypeID, collectionBean.getRefTypeID());
//
//        masterHeaderTable.put(Constants.FIPGUID, guid.toString());
//        masterHeaderTable.put(Constants.CPGUID, collectionBean.getCPGUID());
//
//
//        if (!collectionBean.getBeatGuid().equalsIgnoreCase("")) {
//            masterHeaderTable.put(Constants.BeatGUID, collectionBean.getBeatGuid());
//        } else {
//            masterHeaderTable.put(Constants.BeatGUID, "");
//        }
//
//        masterHeaderTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
//
//        masterHeaderTable.put(Constants.CreatedAt, UtilConstants.getOdataDuration().toString());
//
//        masterHeaderTable.put(Constants.EntityType, Constants.FinancialPostings);
//
//        masterHeaderTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(arrtable));
//
//        if (collCreateView != null) {
//            collCreateView.conformationDialog(mContext.getString(R.string.collection_save_conformation_msg), 1);
//        }

    }

    private void assignDataVar(String save, String strRefType, InvoiceCreateBean collectionBean) {

//        String doc_no = (System.currentTimeMillis() + "");
//
//
//        masterHeaderTable.put(Constants.CPNo, collectionBean.getCPNo());
//        masterHeaderTable.put(Constants.BankID, collectionBean.getBankID());
//        masterHeaderTable.put(Constants.BankName, collectionBean.getBankName());
//        masterHeaderTable.put(Constants.InstrumentNo, collectionBean.getUTRNo());
//        masterHeaderTable.put(Constants.Amount, collectionBean.getAmount());
//        masterHeaderTable.put(Constants.Remarks, collectionBean.getRemarks());
//        masterHeaderTable.put(Constants.ReferenceTypeDesc, collectionBean.getRefTypeDesc());
//
//        masterHeaderTable.put(Constants.FIPDocType, Constants.str_03);
//        masterHeaderTable.put(Constants.FIPDocType1, Constants.str_01);
//        masterHeaderTable.put(Constants.ReferenceTypeID, collectionBean.getRefTypeID());
//        masterHeaderTable.put(Constants.PaymentModeID, collectionBean.getPaymentModeID());
//        masterHeaderTable.put(Constants.PaymentModeDesc, collectionBean.getPaymentModeDesc());
//        masterHeaderTable.put(Constants.FIPDocNo, doc_no);
//
//        masterHeaderTable.put(Constants.FIPDate, collectionBean.getCollDate());
//        masterHeaderTable.put(Constants.CPGUID, collectionBean.getCPGUID());
//        masterHeaderTable.put(Constants.SPGuid, collectionBean.getSPGUID());
//        masterHeaderTable.put(Constants.InstrumentDate, collectionBean.getChequeDate());
//
//        masterHeaderTable.put(Constants.CPTypeID, Constants.str_02);
//        masterHeaderTable.put(Constants.CPName, collectionBean.getCPName());
//        masterHeaderTable.put(Constants.ParentNo, collectionBean.getParentID());
//        masterHeaderTable.put(Constants.SPNo, collectionBean.getSpNo());
//        masterHeaderTable.put(Constants.SPFirstName, collectionBean.getSpFirstName());
//        masterHeaderTable.put(Constants.Currency, collectionBean.getCurrency());
//        masterHeaderTable.put(Constants.BranchName, collectionBean.getBranchName());
//
//
//        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
//        String loginIdVal = sharedPreferences.getString(Constants.username, "");
//
//        masterHeaderTable.put(Constants.LOGINID, loginIdVal);
//
//        masterHeaderTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
//
//        masterHeaderTable.put(Constants.CreatedAt, UtilConstants.getOdataDuration().toString());
//
//        masterHeaderTable.put(Constants.FIPGUID, collectionBean.getFIPGUID());
//        masterHeaderTable.put(Constants.EntityType, Constants.FinancialPostings);
//
//        masterHeaderTable.put(Constants.FIPDocNo, doc_no);
//
//        masterHeaderTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(arrtable));
//
//        if (!collectionBean.getBeatGuid().equalsIgnoreCase("")) {
//            masterHeaderTable.put(Constants.BeatGUID, collectionBean.getBeatGuid());
//        } else {
//            masterHeaderTable.put(Constants.BeatGUID, "");
//        }
//
//        if (collectionBean.getRefTypeID().equalsIgnoreCase(Constants.str_05)) {
//            masterHeaderTable.put(Constants.Amount, mDoubleTotalInvSum + "");
//        } else {
//            masterHeaderTable.put(Constants.Amount, collectionBean.getBundleTotalInvAmt() + "");
//        }
//
//        masterHeaderTable.put(Constants.Source, Constants.Source_SFA);
//
//        if (collCreateView != null) {
//            collCreateView.conformationDialog(mContext.getString(R.string.collection_save_conformation_msg), 1);
//        }
    }

    private void finalSaveCondition() {
        Bundle bundle = new Bundle();
        if (collCreateView != null) {
            collCreateView.showProgressDialog(mContext.getString(R.string.saving_data_wait));
        }
        bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 1);
        onSave();
    }

    private void onSave() {

        Constants.saveDeviceDocNoToSharedPref(mContext, Constants.FinancialPostings, masterHeaderTable.get(Constants.FIPDocNo));

        JSONObject jsonHeaderObject = new JSONObject(masterHeaderTable);

        UtilDataVault.storeInDataVault(masterHeaderTable.get(Constants.FIPDocNo), jsonHeaderObject.toString());

//        Constants.onVisitActivityUpdate(mContext, invCreateBean.getCPGUID32(),
//                masterHeaderTable.get(Constants.FIPGUID),
//                Constants.CollCreateID, Constants.FinancialPostings, mStartTimeDuration);

        navigateToDetails();
    }

    private void navigateToDetails() {
        if (collCreateView != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    collCreateView.hideProgressDialog();
                    collCreateView.showMessage(mContext.getString(R.string.msg_coll_created), false);
                }
            });
        }
    }

    private void requestCollType() {
        if (collCreateView != null) {
            collCreateView.showProgressDialog(mContext.getString(R.string.app_loading));
        }
        currentRequest = 0;
        totalRequest = 1;

        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.InvoicePaymentModeID + "' and " + Constants.EntityType + " eq '"+ Constants.SSInvoice+"' ";
        ConstantsUtils.onlineRequest(mContext, mStrConfigQry, isSessionRequired, 2, ConstantsUtils.SESSION_HEADER, this, false);

    }


    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 1:

                break;
            case 2:
                currentRequest++;
                alCollPaymentMode.clear();
                try {
                    alCollPaymentMode.addAll(OfflineManager.getConfigListFromValueHelp(list, Constants.InvoicePaymentModeID));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                break;
        }
        if (totalRequest == currentRequest) {

            if (type == 4) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (collCreateView != null) {
                            collCreateView.hideProgressDialog();
                            collCreateView.displayInvoiceData(alInvoiceList);
                        }
                    }
                });
            } else {
                mArrayDistributors = Constants.getDistributorsByCPGUID(Constants.convertStrGUID32to36(invCreateBean.getCPGUID32()));
                mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(Constants.convertStrGUID32to36(invCreateBean.getCPGUID32()));
                String mRouteSchGuid = Constants.getRouteSchGUID(Constants.RouteSchedulePlans, Constants.RouteSchGUID,
                        Constants.VisitCPGUID, invCreateBean.getCPGUID32(), mArrayDistributors[5][0]);
                invCreateBean.setCurrency(mArrayDistributors[10][0]);
                invCreateBean.setRouteSchGuid(mRouteSchGuid);
                invCreateBean.setSPGUID(mArrayDistributors[0][0]);
                invCreateBean.setSpNo(mArrayDistributors[2][0]);
                invCreateBean.setCPTypeID(mArrayDistributors[8][0]);
                invCreateBean.setParentID(mArrayDistributors[4][0]);
                invCreateBean.setParentTypeID(mArrayDistributors[5][0]);
                invCreateBean.setCurrency(mArrayDistributors[10][0]);
                invCreateBean.setParentName(mArrayDistributors[7][0]);
                invCreateBean.setSpFirstName(mArrayDistributors[3][0]);
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (collCreateView != null) {
                            collCreateView.hideProgressDialog();
                            collCreateView.displayByCollectionData( alCollPaymentMode);
                        }
                    }
                });
            }

        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, final String s, Bundle bundle) {
        currentRequest++;
        if (totalRequest == currentRequest) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (collCreateView != null) {
                        collCreateView.hideProgressDialog();
                        collCreateView.displayMessage(s);
                    }
                }
            });
        }
    }


    private void getLocation() {
        if (collCreateView != null) {
            collCreateView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            LocationUtils.checkLocationPermission(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    if (collCreateView != null) {
                        collCreateView.hideProgressDialog();
                    }
                    if (status) {
                        locationPerGranted();
                    }
                }
            });
        }
    }

    private void locationPerGranted() {
        if (collCreateView != null) {
            collCreateView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            Constants.getLocation(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                    if (collCreateView != null) {
                        collCreateView.hideProgressDialog();
                    }
                    if (status) {
                        if (ConstantsUtils.isAutomaticTimeZone(mContext)) {
                            finalSaveCondition();
                        } else {
                            if (collCreateView != null)
                                ConstantsUtils.showAutoDateSetDialog(mActivity);
                        }
                    }
                }
            });
        }
    }

}
