package com.arteriatech.emami.invoicecreate.invoicereview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.invoicecreate.invoicecreatesteptwo.StockBean;
import com.arteriatech.emami.mbo.InvoiceCreateBean;
import com.arteriatech.emami.mbo.MaterialBatchBean;
import com.arteriatech.emami.mbo.SKUGroupBean;
import com.arteriatech.emami.mbo.SchemeBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.socreate.SchemeCalcuBean;
import com.arteriatech.emami.store.OfflineManager;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.store.ODataRequestExecution;
import com.sap.xscript.core.GUID;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by e10526 on 21-04-2018.
 *
 */

public class InvoiceReviewPresenterImpl implements InvoiceReviewPresenter,OnlineODataInterface {
    private Context mContext;
    private Activity mActivity;
    private InvoiceReviewView soCreateView;
    private boolean isSessionRequired;
    private Hashtable<String, String> masterHeaderTable = new Hashtable<>();
    private ArrayList<HashMap<String, String>> itemTable = new ArrayList<>();
    private InvoiceCreateBean invCreateBean =null;
    private String stockOwner = "",mStrParentID="",mStrCPTypeID ="";
    private String mStrBMT = "", mStrTLSD = "";
    ArrayList<SKUGroupBean> alCRSSKUGrpList = new ArrayList<>();
    private String mTargetQry = "";
    private ArrayList<SKUGroupBean> alReviewSOItems;
    Set<String> setMatList = new HashSet<>();

    Map<String, Double> mapMaterialWiseQty = new HashMap<>();
    Map<String, Double> mapMaterialWiseTempQty = new HashMap<>();
    Map<String, Double> mapBrandWiseQty = new HashMap<>();
    Map<String, Double> mapBrandWiseTempQty = new HashMap<>();
    Map<String, Double> mapBannerWiseQty = new HashMap<>();
    Map<String, Double> mapBannerWiseTempQty = new HashMap<>();
    Map<String, Double> mapSKUGrpWiseQty = new HashMap<>();
    Map<String, Double> mapSKUGrpWiseTempQty = new HashMap<>();
    Map<String, Double> mapCRSSKUGrpWiseQty = new HashMap<>();
    Map<String, Double> mapCRSSKUGrpWiseTempQty = new HashMap<>();
    Map<String, String> mapHeaderWiseSchemeQty = new HashMap<>();

    Map<String, Double> mapBasketCRSSKUGRPMinQty = new HashMap<>();
    Map<String, Double> mapBasketMaterialMinQty = new HashMap<>();
    Map<String, Double> mapBasketBrandMinQty = new HashMap<>();
    Map<String, Double> mapBasketBannerMinQty = new HashMap<>();
    Map<String, Double> mapBasketSKUGRPMinQty = new HashMap<>();
    HashMap<String, ArrayList<SchemeBean>> hashMapSchemeValByMaterial = new HashMap<>();

    Map<String, String> mapSchemePerORAmtByOrderMatGrp = new HashMap<>();
    Map<String, String> mapSchemePerORAmtByMaterial = new HashMap<>();
    Map<String, SchemeBean> mapSchemeFreeMatByOrderMatGrp = new HashMap<>();
    Map<String, SchemeBean> mapSchemeFreeMatByMaterial = new HashMap<>();
    private ArrayList<SchemeBean> alSchFreeProd;

    HashMap<String, SchemeBean> hashMapFreeMaterialByMaterial = new HashMap<>();
    HashMap<String, String> hashMapFreeMatByOrderMatGrp = new HashMap<>();
    ArrayList<String> mStrAmtBasedSchemeAvl = new ArrayList<>();
    ArrayList<String> mStrCrsSKUList = new ArrayList<>();
    Map<String, Double> mapNetPriceByScheme = new HashMap<>();
    Map<String, Double> mapFreeQtyeByScheme = new HashMap<>();
    Set<String> mSetOrderMatGrp = new HashSet<>();
    private double mDouCalNetAmt = 0.0;

    HashMap<String, ArrayList<SchemeBean>> hashMapSchemeValByOrderMatGrp = new HashMap<>();
    private Set<String> mStrCrsSku = new HashSet<>();
    Set<String> setSchemeList = new HashSet<>();
    int schmeIndex = 0;
    String[] orderMatGrp = null;

    Map<String, Double> mapNetAmt = new HashMap<>();
    Map<String, Double> mapRatioSchDis = new HashMap<>();
    Map<String, Double> mapFreeDisAmt = new HashMap<>();
    Map<String, BigDecimal> mapCRSSKUQTY = new HashMap<>();
    Map<String, Double> mapPriSchemePer = new HashMap<>();
    Map<String, Double> mapSecSchemePer = new HashMap<>();
    Map<String, Double> mapSecSchemeAmt = new HashMap<>();

    Map<String, Integer> mapCntMatByCRSKUGRP = new HashMap<>();

    private SchemeBean mFreeMat = null;
    //New
    Map<String, String> mapUOM = new HashMap<>();
    private Double mDobTotalOrderVal = 0.0;
    private Map<String, SKUGroupBean> mapSKUGRPVal = new HashMap<>();
    private ArrayList<SKUGroupBean> skuGroupBeanArrayList = new ArrayList<>();

    public InvoiceReviewPresenterImpl(Context mContext, InvoiceReviewView soCreateView, boolean isSessionRequired, Activity mActivity, InvoiceCreateBean invCreateBean) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.soCreateView = soCreateView;
        this.isSessionRequired = isSessionRequired;
        this.invCreateBean = invCreateBean;
    }


    @Override
    public void onStart() {
        requestInvData();
    }

    @Override
    public void onDestroy() {

    }


    @Override
    public boolean validateFields(InvoiceCreateBean feedbackBean) {
        boolean isNotError = true;
        return isNotError;
    }

    @Override
    public void getProductRelInfo(String FeedbackId) {
        if (soCreateView != null) {
            soCreateView.showProgressDialog(mContext.getString(R.string.app_loading));
        }

        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '"
                + Constants.FeedbackSubType + "' and "+Constants.ParentID+" eq '"+FeedbackId+"' ";
        ConstantsUtils.onlineRequest(mContext, mStrConfigQry, isSessionRequired, 4, ConstantsUtils.SESSION_HEADER, this,false);
    }

    @Override
    public void onAsignData(String save, String strRejReason, String strRejReasonDesc, InvoiceCreateBean soCreateBean) {
        if(mStrCrsSKUList.size()>0){
            String mStrSKUGrp = UtilConstants.getConcatinatinFlushCollectios(mStrCrsSKUList);
            if (soCreateView != null) {
                soCreateView.hideProgressDialog();
                soCreateView.displayMessage(mContext.getString(R.string.alert_mat_batch_not_avalible,mStrSKUGrp));
            }
        }else{
            if (mStrCrsSku.size() > 0) {
                if (soCreateView != null) {
                    soCreateView.conformationDialog(mContext.getString(R.string.invoice_save_conformation_msg), 1);
                }
            } else {
                if (soCreateView != null) {
                    soCreateView.hideProgressDialog();
                    soCreateView.displayMessage(mContext.getString(R.string.alert_enter_atlest_one_mat));
                }

            }
        }


    }

    @Override
    public void approveData(String ids, String description, String approvalStatus) {

    }

    @Override
    public void onSaveData() {
        getLocation();
    }

    private void finalSaveCondition(){
        Bundle bundle = new Bundle();
            if (soCreateView != null) {
                soCreateView.showProgressDialog(mContext.getString(R.string.saving_data_wait));
            }
            bundle.putInt(Constants.BUNDLE_REQUEST_CODE, 1);
        onSaveValesToDataVault();
    }

    private void onSaveValesToDataVault() {
        String doc_no = (System.currentTimeMillis() + "");

        Hashtable<String, String> headerTable = new Hashtable<>();
        GUID ssoHeaderGuid = GUID.newRandom();
        headerTable.put(Constants.InvoiceGUID, ssoHeaderGuid.toString36().toUpperCase());
        headerTable.put(Constants.InvoiceNo, doc_no);
        String ordettype = "";
        try {
            ordettype = OfflineManager.getValueByColumnName(Constants.ValueHelps + "?$select=" + Constants.ID + " &$filter=" + Constants.EntityType + " eq 'SSInvoice' and  " +
                    "" + Constants.PropName + " eq 'InvoiceTypeID' ", Constants.ID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        headerTable.put(Constants.InvoiceTypeID, ordettype);
        headerTable.put(Constants.InvoiceTypeDesc, "Distributor Invoice");
        headerTable.put(Constants.InvoiceDate, UtilConstants.getNewDateTimeFormat());
        headerTable.put(Constants.DmsDivision, invCreateBean.getDmsDivision());
        headerTable.put(Constants.DmsDivisionDesc, invCreateBean.getDmsDivisionDesc());
        headerTable.put(Constants.PONo, "");
        headerTable.put(Constants.PODate, invCreateBean.getPODate());
        headerTable.put(Constants.DeliveryDate, invCreateBean.getDelDate());
        headerTable.put(Constants.DeliveryPerson, invCreateBean.getDeliveryPerson());
        headerTable.put(Constants.DriverName, invCreateBean.getDriverName());
        headerTable.put(Constants.DriverMobile, invCreateBean.getDriverMobile());
        headerTable.put(Constants.TransVhclId, invCreateBean.getVehicleNo());
        headerTable.put(Constants.CPGUID, invCreateBean.getParentID());
        headerTable.put(Constants.CPNo, invCreateBean.getParentID());
        headerTable.put(Constants.CPName, invCreateBean.getParentName());
        headerTable.put(Constants.CPTypeID, invCreateBean.getParentTypeID());
        headerTable.put(Constants.FromCPTypDs, "");
        headerTable.put(Constants.BillTo,  "");
        headerTable.put(Constants.ShipToTypeID,  "");
        headerTable.put(Constants.ShipToName,  "");
        headerTable.put(Constants.ShipToID,  "");
        headerTable.put(Constants.ShipToCPGUID,  "");
        headerTable.put(Constants.SoldToCPGUID,  invCreateBean.getCPGUID());
        headerTable.put(Constants.SoldToID, invCreateBean.getCPNo());
        headerTable.put(Constants.SoldToUID, invCreateBean.getCpUID());
        headerTable.put(Constants.SoldToName, invCreateBean.getCPName());
        headerTable.put(Constants.SoldToTypeID, invCreateBean.getCPTypeID());
        headerTable.put(Constants.SPGUID, invCreateBean.getSPGUID());
        headerTable.put(Constants.SPNo, invCreateBean.getSpNo());
        headerTable.put(Constants.SPName, invCreateBean.getSpFirstName());

        try {
            headerTable.put(Constants.RefDocGUID, invCreateBean.getSSSoGuid()!=null?invCreateBean.getSSSoGuid():"");
        } catch (Exception e) {
            headerTable.put(Constants.RefDocGUID, "");
            e.printStackTrace();
        }
        headerTable.put(Constants.BeatGUID, invCreateBean.getRouteSchGuid());

        headerTable.put(Constants.GrossAmount, "0");
        headerTable.put(Constants.PaymentModeID, invCreateBean.getPaymentModeID());
        headerTable.put(Constants.PaymentModeDesc, invCreateBean.getPaymentModeDesc());

        headerTable.put(Constants.Currency, invCreateBean.getCurrency());
        headerTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
        headerTable.put(Constants.CreatedAt, UtilConstants.getOdataDuration().toString());
        headerTable.put(Constants.TLSD, mStrCrsSku.size() + "");

        ArrayList<HashMap<String, String>> soItems = new ArrayList<>();
        Double mDouTotNetAmt = 0.0, mDouNetAmt = 0.0, mDouGrossAmt = 0.0, mDouTotalGrossAmt = 0.0, mDouTotalOrderQty = 0.0, mDouTotalFreeQty = 0.0;
        int incItemCountVal = 0;
        for (SKUGroupBean skuGroupBeanItem : alReviewSOItems) {
            if (skuGroupBeanItem.getMaterialBatchBean() != null) {
                SchemeBean mGetMatBatchInfo = skuGroupBeanItem.getMaterialBatchBean();
                if (mGetMatBatchInfo != null) {
                    ArrayList<MaterialBatchBean> alMatBatchItemBean = mGetMatBatchInfo.getMaterialBatchBeanArrayList();
                    for (MaterialBatchBean matBatchItemBean : alMatBatchItemBean) {
                        HashMap<String, String> singleItem = new HashMap<>();
                        GUID ssoItemGuid = GUID.newRandom();
                        singleItem.put(Constants.InvoiceItemGUID, ssoItemGuid.toString36().toUpperCase());
                        singleItem.put(Constants.InvoiceGUID, ssoHeaderGuid.toString36().toUpperCase());
                        singleItem.put(Constants.ItemNo, ConstantsUtils.addZeroBeforeValue(incItemCountVal + 1, ConstantsUtils.ITEM_MAX_LENGTH));
                        singleItem.put(Constants.MaterialNo, skuGroupBeanItem.getMaterialNo());
                        singleItem.put(Constants.MaterialDesc, skuGroupBeanItem.getMaterialDesc());
                        singleItem.put(Constants.OrderMaterialGrp, skuGroupBeanItem.getSKUGroup());
                        singleItem.put(Constants.OrderMaterialGrpDesc, skuGroupBeanItem.getSKUGroupDesc());
                        singleItem.put(Constants.Currency, invCreateBean.getCurrency());
                        try {
//                            singleItem.put(Constants.Uom, skuGroupBeanItem.getSelectedUOM().equalsIgnoreCase("")?skuGroupBeanItem.getUOM():skuGroupBeanItem.getSelectedUOM());
                            singleItem.put(Constants.UOM, skuGroupBeanItem.getUOM().equalsIgnoreCase("")?skuGroupBeanItem.getUOM():skuGroupBeanItem.getUOM());
                        } catch (Exception e) {
                            singleItem.put(Constants.UOM, skuGroupBeanItem.getUOM());
                            e.printStackTrace();
                        }
                        singleItem.put(Constants.UnitPrice, matBatchItemBean.getLandingPrice());
                        try {
                            mDouNetAmt = Double.parseDouble(matBatchItemBean.getTotalNetAmt());
                            mDouTotNetAmt = mDouTotNetAmt + Double.parseDouble(matBatchItemBean.getTotalNetAmt());

                            mDouGrossAmt = Double.parseDouble(matBatchItemBean.getGrossAmt());
                            mDouTotalGrossAmt = mDouTotalGrossAmt + Double.parseDouble(matBatchItemBean.getGrossAmt());

                            mDouTotalOrderQty = mDouTotalOrderQty + Double.parseDouble(matBatchItemBean.getQty());
                        } catch (NumberFormatException e) {
                            mDouNetAmt = 0.0;
                            mDouTotNetAmt = 0.0;
                            mDouGrossAmt = 0.0;
                            mDouTotalGrossAmt = 0.0;
                        }
//                        singleItem.put(Constants.Quantity, matBatchItemBean.getQty());
                        String alternativeUOMQty = matBatchItemBean.getQty();
                       /* if (!TextUtils.isEmpty(Constants.getPRICALBATCHType()) && Constants.getPRICALBATCHType().equalsIgnoreCase(Constants.X)) {
                            if (!skuGroupBeanItem.getSelectedUOM().equalsIgnoreCase(skuGroupBeanItem.getUOM()) && !skuGroupBeanItem.getSelectedUOM().equalsIgnoreCase("")) {
                                alternativeUOMQty = String.valueOf(Math.round(Double.parseDouble(matBatchItemBean.getActualEnteredQty()) * Double.parseDouble(skuGroupBeanItem.getAlternativeUOM2Num())));
                            }
                        }*/
                        singleItem.put(Constants.Quantity, alternativeUOMQty);
                        singleItem.put(Constants.NetAmount, mDouNetAmt + "");
                        singleItem.put(Constants.MRP, matBatchItemBean.getMRP().equalsIgnoreCase("") ? "0" : matBatchItemBean.getMRP());
                        singleItem.put(Constants.GrossAmount, mDouGrossAmt + "");
                        singleItem.put(Constants.TAX, matBatchItemBean.getTax().equalsIgnoreCase("") ? "0" : matBatchItemBean.getTax());

                        singleItem.put(Constants.SecDiscount, "0");
                        singleItem.put(Constants.PriDiscount,  "0");
                        singleItem.put(Constants.CashDiscount, "0");
                        singleItem.put(Constants.CashDiscountPerc, "0");
                        singleItem.put(Constants.SecondaryTradeDisc, matBatchItemBean.getSecPer().equalsIgnoreCase("") ? "0" : matBatchItemBean.getSecPer());
                        singleItem.put(Constants.PrimaryTradeDis, matBatchItemBean.getPrimaryPer().equalsIgnoreCase("") ? "0" : matBatchItemBean.getPrimaryPer());
                        singleItem.put(Constants.CashDiscountPer, matBatchItemBean.getInvCashDisc().equalsIgnoreCase("") ? "0" : matBatchItemBean.getInvCashDisc());

                        try {
                            singleItem.put(Constants.RefDocItmGUID, skuGroupBeanItem.getSSSOItemGuid()!=null?skuGroupBeanItem.getSSSOItemGuid():"");
                        } catch (Exception e) {
                            singleItem.put(Constants.RefDocItmGUID, "");
                            e.printStackTrace();
                        }
                        singleItem.put(Constants.Batch, matBatchItemBean.getBatchNo());

                        singleItem.put(Constants.TransRefTypeID, matBatchItemBean.getTransRefTypeID());
                        singleItem.put(Constants.TransRefNo, matBatchItemBean.getTransRefNo());
                        singleItem.put(Constants.TransRefItemNo, matBatchItemBean.getTransRefItemNo());
                        try {
                            singleItem.put(Constants.MFD, ConstantsUtils.convertDateFromString(matBatchItemBean.getMFD()));
                        } catch (Exception e) {
                            singleItem.put(Constants.MFD, "");
                        }
                        try {
                            singleItem.put(Constants.ExpiryDate, ConstantsUtils.convertDateFromString(matBatchItemBean.getExpiryDate()));
                        } catch (Exception e) {
                            singleItem.put(Constants.ExpiryDate, "");
                        }

                        singleItem.put(Constants.IsfreeGoodsItem, "");
                        singleItem.put(Constants.StockRefGUID, matBatchItemBean.getCPSnoGUID());

                        OfflineManager.updateCPStkSno(matBatchItemBean.getCPSnoGUID(),Double.parseDouble(alternativeUOMQty));

                        soItems.add(singleItem);
                        incItemCountVal++;
                    }
                }
            }
        }
        headerTable.put(Constants.Status, "000002");
        headerTable.put(Constants.NetAmount, mDouTotNetAmt + "");
        headerTable.put(Constants.Quantity, mDouTotalOrderQty + "");
        headerTable.put(Constants.FreeQuantity, mDouTotalFreeQty + "");
        headerTable.put(Constants.GrossAmount, mDouTotalGrossAmt + "");
        headerTable.put(Constants.entityType, Constants.SSInvoices);
        headerTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(soItems));
        headerTable.put(Constants.TestRun, Constants.TestRun_Text);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);


        Constants.saveDeviceDocNoToSharedPref(mContext, Constants.SSInvoices, doc_no);

        headerTable.put(Constants.LOGINID, sharedPreferences.getString(Constants.username, "").toUpperCase());

        JSONObject jsonHeaderObject = new JSONObject(headerTable);

        UtilDataVault.storeInDataVault(doc_no, jsonHeaderObject.toString());

        Constants.onVisitActivityUpdate(invCreateBean.getCPGUID32(), sharedPreferences.getString(Constants.username, ""),
                ssoHeaderGuid.toString36().toUpperCase(), Constants.Secondary_Invoice_Type, Constants.Secondary_Invoice);

        navigateToDetails();

    }


    private void navigateToDetails() {
        if (soCreateView != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    soCreateView.hideProgressDialog();
                    soCreateView.showMessage(mContext.getString(R.string.msg_inv_created), false);
                }
            });
        }
    }

    private void requestInvData() {
        if (soCreateView != null) {
            soCreateView.showProgressDialog(mContext.getString(R.string.app_loading));
        }

        getSOItemValues();
    }

    private void getSOItemValues() {

        Log.d("Time getSOItemValues st", UtilConstants.getSyncHistoryddmmyyyyTime());
        Constants.hashMapCpStockItemGuidQtyValByMaterial.clear();
        Constants.hashMapMaterialValByOrdMatGrp.clear();
        mStrCrsSKUList.clear();
        alReviewSOItems = new ArrayList<>();
        if (Constants.selectedStockItems != null && Constants.selectedStockItems.size() > 0) {

                    for (StockBean subItem : Constants.selectedStockItems) {
                        if (Double.parseDouble(subItem.getEnterdQTY().equalsIgnoreCase("")
                                ? "0" : subItem.getEnterdQTY()) > 0) {

                            if(Constants.MAPORDQtyByCrsSkuGrp.containsKey(subItem.getOrderMaterialGroupID())){
                                double douOrderQty = 0.0;
                                try {
                                    douOrderQty = Double.parseDouble(Constants.MAPORDQtyByCrsSkuGrp.get(subItem.getOrderMaterialGroupID()));
                                } catch (NumberFormatException e) {
                                    douOrderQty = 0.0;
                                    e.printStackTrace();
                                }
                                double doubSumOrderQty = 0.0;
                                try {
                                    doubSumOrderQty = douOrderQty + Double.parseDouble(subItem.getEnterdQTY());
                                } catch (NumberFormatException e) {
                                    doubSumOrderQty= 0.0;
                                    e.printStackTrace();
                                }

                                Constants.MAPORDQtyByCrsSkuGrp.put(subItem.getOrderMaterialGroupID(), doubSumOrderQty+"");
                            }else{
                                Constants.MAPORDQtyByCrsSkuGrp.put(subItem.getOrderMaterialGroupID(), subItem.getEnterdQTY());
                            }


                            SKUGroupBean skuGroupBean = new SKUGroupBean();
                            skuGroupBean.setEtQty(subItem.getEnterdQTY());
                            skuGroupBean.setUOM(subItem.getUOM());
                            skuGroupBean.setORDQty(subItem.getEnterdQTY());
                            skuGroupBean.setMaterialNo(subItem.getMaterialNo());
                            skuGroupBean.setMaterialDesc(subItem.getMaterialDESC());
                            skuGroupBean.setBatch(subItem.getSelectedBatchNo());
                            skuGroupBean.setInvCashDisc(subItem.getCashDisc());

                            skuGroupBean.setCPStockItemGUID(subItem.getStockGUID());
                            skuGroupBean.setSSSOItemGuid(subItem.getSSSoItemGUID());
                            skuGroupBean.setBanner(subItem.getBanner());
                            skuGroupBean.setBrand(subItem.getBanner());
                            skuGroupBean.setProductCategoryID(subItem.getProductCategoryID());
                            skuGroupBean.setSKUGroupID(subItem.getSKUGroup());
                            skuGroupBean.setSKUGroup(subItem.getOrderMaterialGroupID());
                            skuGroupBean.setSKUGroupDesc(subItem.getOrderMaterialGroupDesc());


                            alReviewSOItems.add(skuGroupBean);
                            setMatList.add(subItem.getMaterialNo());

                            SchemeBean schemeBean = new SchemeBean();
                            schemeBean.setCPItemGUID(subItem.getStockGUID());
                            schemeBean.setOrderQty(subItem.getEnterdQTY());
                            Constants.hashMapCpStockItemGuidQtyValByMaterial.put(subItem.getMaterialNo(), schemeBean);

                            if (!Constants.hashMapMaterialValByOrdMatGrp.containsKey(subItem.getSKUGroup())) {
                                Set set = Constants.hashMapMaterialValByOrdMatGrp.get(subItem.getSKUGroup());
                                if (set != null) {
                                    set.add(subItem.getMaterialNo());
                                } else {
                                    set = new HashSet();
                                    set.add(subItem.getMaterialNo());
                                }
                                Constants.hashMapMaterialValByOrdMatGrp.put(subItem.getSKUGroup(), set);
                            } else {
                                Set set = new HashSet();
                                set.add(subItem.getMaterialNo());
                                Constants.hashMapMaterialValByOrdMatGrp.put(subItem.getSKUGroup(), set);
                            }

                            if (subItem.getBrand()!=null && !subItem.getBrand().equalsIgnoreCase("")) {
                                // get brand wise qty
                                if (mapBrandWiseQty.containsKey(subItem.getBrand())) {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEnterdQTY()) + mapBrandWiseQty.get(subItem.getBrand());
                                    mapBrandWiseQty.put(subItem.getBrand(), mDouOrderQty);
                                } else {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEnterdQTY());
                                    mapBrandWiseQty.put(subItem.getBrand(), mDouOrderQty);
                                }
                            }

                            if (subItem.getBanner()!=null && !subItem.getBanner().equalsIgnoreCase("")) {
                                // get banner wise qty
                                if (mapBannerWiseQty.containsKey(subItem.getBanner())) {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEnterdQTY()) + mapBannerWiseQty.get(subItem.getBanner());
                                    mapBannerWiseQty.put(subItem.getBanner(), mDouOrderQty);
                                } else {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEnterdQTY());
                                    mapBannerWiseQty.put(subItem.getBanner(), mDouOrderQty);
                                }
                            }

                            if (subItem.getSKUGroup()!=null && !subItem.getSKUGroup().equalsIgnoreCase("")) {
                                // get SKU GRP wise qty
                                if (mapSKUGrpWiseQty.containsKey(subItem.getSKUGroup())) {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEnterdQTY()) + mapSKUGrpWiseQty.get(subItem.getSKUGroup());
                                    mapSKUGrpWiseQty.put(subItem.getSKUGroup(), mDouOrderQty);
                                } else {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEnterdQTY());
                                    mapSKUGrpWiseQty.put(subItem.getSKUGroup(), mDouOrderQty);
                                }
                            }

                            if (subItem.getOrderMaterialGroupID()!=null && !subItem.getOrderMaterialGroupID().equalsIgnoreCase("")) {
                                // get CRS SKU GRP wise qty
                                if (mapCRSSKUGrpWiseQty.containsKey(subItem.getSKUGroup())) {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEnterdQTY()) + mapCRSSKUGrpWiseQty.get(subItem.getSKUGroup());
                                    mapCRSSKUGrpWiseQty.put(subItem.getSKUGroup(), mDouOrderQty);
                                } else {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEnterdQTY());
                                    mapCRSSKUGrpWiseQty.put(subItem.getSKUGroup(), mDouOrderQty);
                                }
                            }
                            mapMaterialWiseQty.put(subItem.getMaterialNo(), Double.parseDouble(subItem.getEnterdQTY()));

                            mStrCrsSku.add(subItem.getSKUGroup());
                        }
                    }

        }
        Log.d("Time getSOItemValues En", UtilConstants.getSyncHistoryddmmyyyyTime());
        mapBrandWiseTempQty.putAll(mapBrandWiseQty);
        mapBannerWiseTempQty.putAll(mapBannerWiseQty);
        mapSKUGrpWiseTempQty.putAll(mapSKUGrpWiseQty);
        mapCRSSKUGrpWiseTempQty.putAll(mapCRSSKUGrpWiseQty);
        mapMaterialWiseTempQty.putAll(mapMaterialWiseQty);
        Constants.HashMapSchemeValuesBySchemeGuid.clear();


        getInstantSchemeAndBasketScheme();
        Log.d("Time getInsSchAndBasSch", UtilConstants.getSyncHistoryddmmyyyyTime());
        getOrderMatGrpSchemeCal();
        Log.d("Time getOrdMatGrpSchCal", UtilConstants.getSyncHistoryddmmyyyyTime());
        getMaterialSchemeCal();
        Log.d("Time getMatSchemeCal", UtilConstants.getSyncHistoryddmmyyyyTime());
        sumOfSkuGrpItems();
        Log.d("Time sumOfSkuGrpItems", UtilConstants.getSyncHistoryddmmyyyyTime());

        onDisplayReviewPage();

    }
    private void onDisplayReviewPage(){
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (soCreateView != null) {
                    soCreateView.hideProgressDialog();
                    soCreateView.displaySOReview(mapSKUGRPVal,mapCRSSKUQTY,
                            mapPriSchemePer,mapSecSchemePer,
                            mapSecSchemeAmt,mapCntMatByCRSKUGRP,mapNetAmt,
                            alSchFreeProd,hashMapFreeMatByOrderMatGrp,
                            hashMapFreeMaterialByMaterial,mStrCrsSku.size(),mDobTotalOrderVal,skuGroupBeanArrayList);
                }
            }
        });
    }


    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
        switch (type) {
            case 3:
//                alCRSSKUGrpList.clear();

               /* try {
                    alCRSSKUGrpList.addAll(OfflineManager.getCRSSKUGroup(list,dmsDivQryBean.getDMSDivisionQry(),
                            hashMapDBStk,hashMapRetailerStk,hashMapTargetByCrsskugrp,hashMapInvQtyByCrsskugrp,
                            alMapMaterialDBStkUOM,hashMapRetailerStkByMat,stockOwner,mStrParentID,hashMapMustSell,
                            orderMaterialGrpSchemeId,hashMapSchemeGuidMatByMaterial));
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }*/
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (soCreateView != null) {
                            soCreateView.hideProgressDialog();
//                            soCreateView.displaySO(alCRSSKUGrpList);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {

    }

    private void getInstantSchemeAndBasketScheme(){
        try {
            String schemeQry = Constants.Schemes + "?$filter= " + Constants.StatusID +
                    " eq '01' and ValidTo ge datetime'" + UtilConstants.getNewDate() + "' and ApprovalStatusID eq '03' and "+Constants.SchemeCatID+" eq '"+Constants.SchemeCatIDInstantScheme+"' ";
            Constants.HashMapSchemeValuesBySchemeGuid = OfflineManager.getInstantSchemesAndSchemeType(schemeQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getOrderMatGrpSchemeCal() {
        if (!Constants.MAPORDQtyByCrsSkuGrp.isEmpty()) {
            setSchemeList.clear();
            Iterator iterator = Constants.MAPORDQtyByCrsSkuGrp.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                if(Constants.MAPSCHGuidByCrsSkuGrp.size()>0){
                    if (!Constants.MAPSCHGuidByCrsSkuGrp.get(key).isEmpty()) {
//                    setSchemeList.clear();
                        for(String schemeGuid : Constants.MAPSCHGuidByCrsSkuGrp.get(key)) {
                            //  check scheme is instant scheme or not
                            if (Constants.HashMapSchemeValuesBySchemeGuid.containsKey(schemeGuid.toUpperCase())) {  // added 15/09/2017
//                        if (isSchemeInstantOrNot(schemeGuid)) {

                                if (schemeIsAvaliable(schemeGuid)) {
                                    String orderQty = Constants.MAPORDQtyByCrsSkuGrp.get(key);
                                    if (Double.parseDouble(orderQty.equalsIgnoreCase("") ? "0" : orderQty) > 0) {

                                        //  check scheme is Basket scheme or not
                                        if (!Constants.HashMapSchemeValuesBySchemeGuid.get(schemeGuid.toUpperCase()).getSchemeTypeID().equalsIgnoreCase(Constants.SchemeTypeIDBasketScheme)) {    // added 15/09/2017
//                                    if (!Constants.isSchemeBasketOrNot(schemeGuid)) {
                                            //  check scheme calculation  HDR SLAB BASED/ ITEM SLAB BASED
                                            if (!isSchemeHeaderBasedOrItemBased(schemeGuid)) {
                                                SchemeBean schemeBean = getSecSchemeBeanByCrsSKUGrp((schemeGuid), key, Constants.MAPORDQtyByCrsSkuGrp.get(key), "", Constants.SchemeTypeNormal, key);
                                                if (schemeBean != null) {
                                                }
                                            } else {
                                                SchemeBean schemeBean = getSecSchemeBeanByCrsSKUGrp((schemeGuid), key, Constants.MAPORDQtyByCrsSkuGrp.get(key), Constants.X, Constants.SchemeTypeNormal,key);
                                            }
                                        } else {
                                            getBasketSchemeCal(schemeGuid,key);
                                        }

                                    }
                                }

                            }
                        }
                    }
                }

            }
        }
    }

    private SchemeBean getSecSchemeBeanByCrsSKUGrp(String schemeGUID, String mStrCRSSKUGrp, String mStrOrderQty, String mStrHeaderOrItemType, String schemeType, String orderMatGrpid) {
        SchemeBean schemeBean = null;
        SchemeBean schemeItemBean = null;
        String mStrSchemeItemGuid = "";
        String getCondition = "";

        String mStrSaleOfCatId = "0";
        String mStrMinOrderQty = "0";

        boolean schemeISAval = false;
        if (setSchemeList.size() == 0) {
            schemeISAval = true;
        } else if (!setSchemeList.contains(schemeGUID)) {
            schemeISAval = true;
        }

        if (schemeISAval) {
            schmeIndex++;
            try {
                schemeItemBean = OfflineManager.getSchemeItemDetailsBySchemeGuid(Constants.SchemeItemDetails + "?$filter="
                        + Constants.SchemeGUID + " eq guid'" + schemeGUID + "' &$top=1");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            if (!schemeItemBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfMat)) {
                mStrMinOrderQty = schemeItemBean.getItemMin();
                boolean mBoolMinItemQtyAval = false;
                if (Double.parseDouble(mStrMinOrderQty) >= 0) {

                    if (Double.parseDouble(mStrOrderQty.equalsIgnoreCase("") ? "0" : mStrOrderQty) > 0) {
                        if (Double.parseDouble(mStrMinOrderQty) <= Double.parseDouble(mStrOrderQty)) {
                            mBoolMinItemQtyAval = true;
                        } else {
                            mBoolMinItemQtyAval = false;
                        }
                    }
                } else {
                    mBoolMinItemQtyAval = true;
                }

                if (mBoolMinItemQtyAval) {
                    if (!mStrHeaderOrItemType.equalsIgnoreCase(Constants.X)) {
                        String[] orderMatGrpArray = new String[1];
                        orderMatGrpArray[0] = mStrCRSSKUGrp;
                        try {
                            schemeBean = OfflineManager.getSecondarySchemeSlabPer(Constants.SchemeSlabs + "?$filter="
                                            + Constants.SchemeItemGUID + " eq guid'" + schemeGUID.toUpperCase() + "' ",
                                    mStrOrderQty, schemeGUID, Constants.SKUGroupID, orderMatGrpArray, mStrHeaderOrItemType,schemeItemBean);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    } else {


                        if (schemeISAval) {
                            Double mStrSumQtyBySKU = 0.0;
                            Multimap<String, String> multiMap = HashMultimap.create();
                            for (Map.Entry<String, ArrayList<String>> entry : Constants.MAPSCHGuidByCrsSkuGrp.entrySet()) {
                                if (!entry.getValue().isEmpty()) {
                                    for (String stValue : entry.getValue()) {
                                        multiMap.put(stValue, entry.getKey());
                                    }
                                }
                            }
                            String[] orderMatGrpArray = new String[multiMap.get(schemeGUID).size()];
                            for (Map.Entry<String, Collection<String>> entry : multiMap.asMap().entrySet()) {
                                if ((schemeGUID).equalsIgnoreCase(entry.getKey())) {
                                    Collection<String> values = entry.getValue();
                                    int index = 0;
                                    for (String value : values) {
                                        if (Constants.MAPORDQtyByCrsSkuGrp.containsKey(value)) {
                                            if (Double.parseDouble(Constants.MAPORDQtyByCrsSkuGrp.get(value).equalsIgnoreCase("") ? "0" : Constants.MAPORDQtyByCrsSkuGrp.get(value)) > 0) {
                                                orderMatGrpArray[index] = value;
                                                mStrSumQtyBySKU = mStrSumQtyBySKU + Double.parseDouble(Constants.MAPORDQtyByCrsSkuGrp.get(value));
                                                index++;
                                            }
                                        } else {
                                            orderMatGrpArray[index] = value;
                                            index++;
                                        }
                                    }
                                }
                            }
                            try {
                                schemeBean = OfflineManager.getSecondarySchemeSlabPer(Constants.SchemeSlabs + "?$filter="
                                                + Constants.SchemeItemGUID + " eq guid'" + schemeGUID.toUpperCase() + "' ", mStrSumQtyBySKU + "",
                                        schemeGUID, Constants.SKUGroupID, orderMatGrpArray, mStrHeaderOrItemType,schemeItemBean);
                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                            }


                            if (schemeBean != null) {
                                orderMatGrp = orderMatGrpArray;
                                mapHeaderWiseSchemeQty.put(schemeGUID.toUpperCase(), mStrSumQtyBySKU + "");
                                mFreeMat = null;
                                Double mDouSlabTypeCal = 0.0;
                                int incVal = 0;
                                if (schemeBean.getTargetBasedID().equalsIgnoreCase("02")) {
                                    mDouSlabTypeCal = getSecondaryDiscountOrAmtOrFreeQtyHeaderWise(schemeBean, schemeBean.getSlabRuleID(),
                                            schemeBean.getSaleUnitID().equalsIgnoreCase(Constants.SchemeSaleUnitIDCBB)?schemeBean.getCBBQty():mStrSumQtyBySKU + "", "", "", "", Constants.X);

                                    // added 06/09/2017
                                    if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                        try {

                                            for (String omGId : orderMatGrpArray) {
                                                String orderQty = Constants.MAPORDQtyByCrsSkuGrp.get(omGId);
                                                if (orderQty != null && !orderQty.equalsIgnoreCase("")) {
                                                    if (Double.parseDouble(orderQty) > 0) {
                                                        incVal++;
                                                    }
                                                }
                                            }
                                            if (incVal > 0) {
                                                schemeBean.setPayoutAmount((Double.parseDouble(schemeBean.getPayoutAmount()) / incVal) + "");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                } else {
                                    String mStrFreeQty = "";
                                    if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
                                        mStrFreeQty = schemeBean.getPayoutPerc();
                                    } else if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                        try {

                                            for (String omGId : orderMatGrpArray) {
                                                String orderQty = Constants.MAPORDQtyByCrsSkuGrp.get(omGId);
                                                if (orderQty != null && !orderQty.equalsIgnoreCase("")) {
                                                    if (Double.parseDouble(orderQty) > 0) {
                                                        incVal++;
                                                    }
                                                }
                                            }
                                            if (incVal > 0) {
                                                schemeBean.setPayoutAmount((Double.parseDouble(schemeBean.getPayoutAmount()) / incVal) + "");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        mStrFreeQty = schemeBean.getPayoutAmount();
                                    } else {
                                        mStrFreeQty = schemeBean.getFreeQty();
                                    }
                                    mDouSlabTypeCal = getSchSlabTypeIDCalculation(schemeBean.getSlabTypeID(),
                                            mStrFreeQty, schemeBean.getToQty(), schemeBean.getSaleUnitID().equalsIgnoreCase(Constants.SchemeSaleUnitIDCBB)?schemeBean.getCBBQty():mStrSumQtyBySKU + "", schemeBean.getSlabRuleID(),
                                            schemeGUID.toUpperCase(), schemeBean.getFromQty(), Constants.X);
                                }


                                for (String mStrCRSSKUGRP : orderMatGrpArray) {
                                    FreeProduct(schemeBean, schemeBean.getSlabRuleID(), mStrCRSSKUGRP, "", mDouSlabTypeCal + "");
                                    ArrayList<SchemeBean> schemeBeanArrayList = hashMapSchemeValByOrderMatGrp.get(mStrCRSSKUGRP);
                                    if (schemeBeanArrayList == null) {
                                        schemeBeanArrayList = new ArrayList<>();
                                        schemeBeanArrayList.add(schemeBean);
                                        hashMapSchemeValByOrderMatGrp.put(mStrCRSSKUGRP, schemeBeanArrayList);
                                    } else {
                                        if (!checkSchemeIsPresentInList(schemeBeanArrayList, schemeBean)) {
                                            schemeBeanArrayList.add(schemeBean);
                                            hashMapSchemeValByOrderMatGrp.put(mStrCRSSKUGRP, schemeBeanArrayList);
                                        }
                                    }
                                    try {
                                        // added 06/09/2017
                                        if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                            if (incVal > 0) {
                                                mapSchemePerORAmtByOrderMatGrp.put(mStrCRSSKUGRP + "_" + schemeGUID, (mDouSlabTypeCal/incVal) + "");
                                            }else{
                                                mapSchemePerORAmtByOrderMatGrp.put(mStrCRSSKUGRP + "_" + schemeGUID, mDouSlabTypeCal + "");
                                            }
                                        }else{
                                            mapSchemePerORAmtByOrderMatGrp.put(mStrCRSSKUGRP + "_" + schemeGUID, mDouSlabTypeCal + "");
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    mapSchemeFreeMatByOrderMatGrp.put(mStrCRSSKUGRP + "_" + schemeGUID, mFreeMat);
                                }
                            }

                            setSchemeList.add(schemeGUID.toUpperCase());
                        }
                    }


                } else {
                    schemeBean = null;
                }
            }
        }

        return schemeBean;
    }

    private void getBasketSchemeCal(String schemeGuid, String key) {
        String onSaleOnID = "", onRefID = "";

        SchemeBean headerScheme = null;
        ArrayList<SchemeBean> alItems = null;
        try {
            headerScheme = OfflineManager.getBasketSchemeHeader(Constants.SchemeItemDetails + "?$select=" + ConstantsUtils.OnSaleOfCatID + ","
                    + Constants.SchemeItemGUID + "," + Constants.ItemMin + " &$filter="
                    + Constants.SchemeGUID + " eq guid'" + schemeGuid.toUpperCase() + "' and " + Constants.ItemCatID + " eq '" + Constants.BasketCatID + "' &$top=1 ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (headerScheme != null) {
            onSaleOnID = headerScheme.getOnSaleOfCatID();
            if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfOrderMatGrp)) {  // Order Material Group
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails + "?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if (alItems != null && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            if (Constants.MAPORDQtyByCrsSkuGrp.containsKey(schemeBeanVal.getOrderMaterialGroupID())) {
                                double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                if (calItemMinQty <= Double.parseDouble(Constants.MAPORDQtyByCrsSkuGrp.get(schemeBeanVal.getOrderMaterialGroupID()))) {

                                } else {
                                    isBasketschemeAval = true;
                                    break;
                                }
                            } else {
                                isBasketschemeAval = true;
                                break;
                            }
                        }

                    }
                }

                if (!isBasketschemeAval && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        double calItemMinQty = 0.0;
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                        } else {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                        }
                        // adding brand wise min qty into map table
                        mapBasketCRSSKUGRPMinQty.put(schemeBeanVal.getOrderMaterialGroupID(), calItemMinQty);
                        addBasketSchemeTohashmapByOrderMatGrp(schemeBeanVal.getOrderMaterialGroupID(),schemeGuid,key);
                    }
                }

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfMat)) {  // Material
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails + "?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if (alItems != null && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        if (!isBasketschemeAval) {
                            for (SKUGroupBean skuGroupBean : alReviewSOItems) {
                                if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                                    if (skuGroupBean.getMaterialNo().equalsIgnoreCase(schemeBeanVal.getMaterialNo())) {
                                        double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                        if (calItemMinQty <= Double.parseDouble(skuGroupBean.getORDQty())) {
                                            schemeBeanVal.setOrderQty(skuGroupBean.getORDQty());
                                            break;
                                        } else {
                                            isBasketschemeAval = true;
                                            break;
                                        }
                                    } else {
                                        if (!setMatList.contains(skuGroupBean.getMaterialNo())) {
                                            isBasketschemeAval = true;
                                            break;
                                        }
                                    }
                                } else {
                                    if (skuGroupBean.getMaterialNo().equalsIgnoreCase(schemeBeanVal.getMaterialNo())) {
                                        schemeBeanVal.setOrderQty(skuGroupBean.getORDQty().equalsIgnoreCase("") ? "0" : skuGroupBean.getORDQty());
                                    }
                                }
                            }
                        }

                    }

                    if (!isBasketschemeAval && alItems.size() > 0) {
                        for (SchemeBean schemeBeanVal : alItems) {
                            double calItemMinQty = 0.0;
                            if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                                calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                            } else {
                                calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                            }
                            // adding material wise min qty into map table
                            mapBasketMaterialMinQty.put(schemeBeanVal.getMaterialNo(), calItemMinQty);
                            addBasketSchemeTohashmapByMaterial(schemeBeanVal.getMaterialNo(), schemeBeanVal.getOrderQty(),schemeGuid);
                        }
                    }
                }

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfProdCat)) { // Product category

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfBrand)) {  // Brand

                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails + "?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if (alItems != null && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            if (mapBrandWiseQty.containsKey(schemeBeanVal.getBrandID())) {
                                double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                if (calItemMinQty <= mapBrandWiseQty.get(schemeBeanVal.getBrandID())) {

                                } else {
                                    isBasketschemeAval = true;
                                    break;
                                }
                            } else {
                                isBasketschemeAval = true;
                                break;
                            }
                        }

                    }
                }

                if (!isBasketschemeAval && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        double calItemMinQty = 0.0;
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                        } else {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                        }
                        // adding brand wise min qty into map table
                        mapBasketBrandMinQty.put(schemeBeanVal.getBrandID(), calItemMinQty);
                        try {
                            ArrayList<String> mOrderGrp = OfflineManager.getOrderMatGrp(Constants.CPStockItems + "?$select=" + Constants.OrderMaterialGroupID + " &$filter = "
                                    + ConstantsUtils.Brand + " eq '" + schemeBeanVal.getBrandID() + "' and " + Constants.StockOwner + " eq '01' ");
                            for (String OrderMatGrp : mOrderGrp) {
                                addBasketSchemeTohashmapByOrderMatGrp(OrderMatGrp,schemeGuid,key);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfBanner)) {  // Banner
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails + "?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if (alItems != null && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            if (mapBannerWiseQty.containsKey(schemeBeanVal.getBannerID())) {
                                double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                if (calItemMinQty <= mapBannerWiseQty.get(schemeBeanVal.getBannerID())) {

                                } else {
                                    isBasketschemeAval = true;
                                    break;
                                }
                            } else {
                                isBasketschemeAval = true;
                                break;
                            }
                        }

                    }
                }

                if (!isBasketschemeAval && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        double calItemMinQty = 0.0;
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                        } else {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                        }
                        // adding banner wise min qty into map table
                        mapBasketBannerMinQty.put(schemeBeanVal.getBannerID(), calItemMinQty);

                        try {
                            ArrayList<String> mOrderGrp = OfflineManager.getOrderMatGrp(Constants.CPStockItems + "?$select=" + Constants.OrderMaterialGroupID + " &$filter = "
                                    + ConstantsUtils.Banner + " eq '" + schemeBeanVal.getBannerID() + "' and " + Constants.StockOwner + " eq '01' ");
                            for (String OrderMatGrp : mOrderGrp) {
                                addBasketSchemeTohashmapByOrderMatGrp(OrderMatGrp,schemeGuid,key);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfSchemeMatGrp)) {  // Scheme Material Group
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails + "?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if (alItems != null && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            if (mapSKUGrpWiseQty.containsKey(schemeBeanVal.getSKUGroupID())) {
                                double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                if (calItemMinQty <= mapSKUGrpWiseQty.get(schemeBeanVal.getSKUGroupID())) {

                                } else {
                                    isBasketschemeAval = true;
                                    break;
                                }
                            } else {
                                isBasketschemeAval = true;
                                break;
                            }
                        }

                    }
                }

                if (!isBasketschemeAval && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        double calItemMinQty = 0.0;
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                        } else {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                        }
                        // adding banner wise min qty into map table
                        mapBasketSKUGRPMinQty.put(schemeBeanVal.getSKUGroupID(), calItemMinQty);

                        try {
                            ArrayList<String> mOrderGrp = OfflineManager.getOrderMatGrp(Constants.CPStockItems + "?$select=" + Constants.OrderMaterialGroupID + " &$filter = "
                                    + ConstantsUtils.SKUGroup + " eq '" + schemeBeanVal.getSKUGroupID() + "' and " + Constants.StockOwner + " eq '01' ");
                            for (String OrderMatGrp : mOrderGrp) {
                                addBasketSchemeTohashmapByOrderMatGrp(OrderMatGrp,schemeGuid,key);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }
    private void addBasketSchemeTohashmapByOrderMatGrp(String orderMatgrp, String schemeGuid, String key) {
        // check scheme calculation  HDR SLAB BASED/ ITEM SLAB BASED
        if (!isSchemeHeaderBasedOrItemBased(schemeGuid)) {
            SchemeBean schemeBean = getSecSchemeBeanByCrsSKUGrp(schemeGuid, orderMatgrp, Constants.MAPORDQtyByCrsSkuGrp.get(orderMatgrp), "", Constants.SchemeTypeBasket, key);
            if (schemeBean != null) {
            }
        } else {
            SchemeBean schemeBean = getSecSchemeBeanByCrsSKUGrp(schemeGuid, orderMatgrp, Constants.MAPORDQtyByCrsSkuGrp.get(orderMatgrp), Constants.X, Constants.SchemeTypeBasket, key);
            if (schemeBean != null) {
            }
        }
    }

    private void addBasketSchemeTohashmapByMaterial(String matNo, String orderQty, String schemeGuid) {
        // check scheme calculation  HDR SLAB BASED/ ITEM SLAB BASED
        if (!isSchemeHeaderBasedOrItemBased(schemeGuid)) {
            SchemeBean schemeBean = getSecSchemeBeanByMaterial(schemeGuid,
                    matNo, orderQty, "");
            if (schemeBean != null) {
                ArrayList<SchemeBean> schemeBeanArrayList = hashMapSchemeValByMaterial.get(matNo);
                if (schemeBeanArrayList==null){
                    schemeBeanArrayList = new ArrayList<>();
                    schemeBeanArrayList.add(schemeBean);
                    hashMapSchemeValByMaterial.put(matNo, schemeBeanArrayList);
                }else {
                    if (!checkSchemeIsPresentInList(schemeBeanArrayList, schemeBean)) {
                        schemeBeanArrayList.add(schemeBean);
                        hashMapSchemeValByMaterial.put(matNo, schemeBeanArrayList);
                    }
                }
//                hashMapSchemeValByMaterial.put(matNo, schemeBean);
            }
        } else {
            SchemeBean schemeBean = getSecSchemeBeanByMaterial(schemeGuid,
                    matNo, orderQty, Constants.X);
            if (schemeBean != null) {
                ArrayList<SchemeBean> schemeBeanArrayList = hashMapSchemeValByMaterial.get(matNo);
                if (schemeBeanArrayList==null){
                    schemeBeanArrayList = new ArrayList<>();
                    schemeBeanArrayList.add(schemeBean);
                    hashMapSchemeValByMaterial.put(matNo, schemeBeanArrayList);
                }else {
                    if (!checkSchemeIsPresentInList(schemeBeanArrayList, schemeBean)) {
                        schemeBeanArrayList.add(schemeBean);
                        hashMapSchemeValByMaterial.put(matNo, schemeBeanArrayList);
                    }
                }
            }
        }
    }
    private void getBasketSchemeCalByMaterial(String schemeGuid, String orderQty) {
        String onSaleOnID = "", onRefID = "";

        SchemeBean headerScheme = null;
        ArrayList<SchemeBean> alItems = null;
        try {
            headerScheme = OfflineManager.getBasketSchemeHeader(Constants.SchemeItemDetails + "?$select=" + ConstantsUtils.OnSaleOfCatID + ","
                    + Constants.SchemeItemGUID + "," + Constants.ItemMin + " &$filter="
                    + Constants.SchemeGUID + " eq guid'" + schemeGuid.toUpperCase() + "' and " + Constants.ItemCatID + " eq '" + Constants.BasketCatID + "' &$top=1 ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (headerScheme != null) {
            onSaleOnID = headerScheme.getOnSaleOfCatID();
            if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfMat)) {  // Material
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails + "?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if (alItems != null && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        if (!isBasketschemeAval) {
                            for (SKUGroupBean skuGroupBean : alReviewSOItems) {
                                if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                                    if (skuGroupBean.getMaterialNo().equalsIgnoreCase(schemeBeanVal.getMaterialNo())) {
                                        double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                        if (calItemMinQty <= Double.parseDouble(skuGroupBean.getORDQty())) {
                                            schemeBeanVal.setOrderQty(skuGroupBean.getORDQty());
                                            break;
                                        } else {
                                            isBasketschemeAval = true;
                                            break;
                                        }
                                    } else {
                                        if (!setMatList.contains(schemeBeanVal.getMaterialNo())) {
                                            isBasketschemeAval = true;
                                            break;
                                        }
                                    }
                                } else {

                                    if (skuGroupBean.getMaterialNo().equalsIgnoreCase(schemeBeanVal.getMaterialNo())) {
                                        schemeBeanVal.setOrderQty(skuGroupBean.getORDQty().equalsIgnoreCase("") ? "0" : skuGroupBean.getORDQty());
                                    }
                                }
                            }
                        }

                    }

                    if (!isBasketschemeAval && alItems.size() > 0) {
                        for (SchemeBean schemeBeanVal : alItems) {
                            addBasketSchemeTohashmapByMaterial(schemeBeanVal.getMaterialNo(), schemeBeanVal.getOrderQty(), schemeGuid);
                        }
                    }
                }

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfProdCat)) { // Product category

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfBrand)) {  // Brand

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfBanner)) {  // Banner

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfSchemeMatGrp)) {  // Scheme Material Group

            }
        }

    }
    // TODO Below method logic will change
    private boolean isSchemeHeaderBasedOrItemBased(String mStrSchemeGuid) {
        boolean mBoolHeadWiseScheme = false;
        String mStrSchemeQry = Constants.Schemes + "?$filter= " + Constants.SchemeGUID +
                " eq guid'" + mStrSchemeGuid + "' and  " + Constants.IsHeaderBasedSlab + " eq 'X' ";
        try {
            mBoolHeadWiseScheme = OfflineManager.getVisitStatusForCustomer(mStrSchemeQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mBoolHeadWiseScheme;
    }

    private boolean schemeIsAvaliable(String mStrScheme) {
        boolean schemeISAval = false;
        if (setSchemeList.size() == 0) {
            schemeISAval = true;
        } else if (!setSchemeList.contains(mStrScheme)) {
            schemeISAval = true;
        }
        return schemeISAval;
    }

    private double getSecondaryDiscountOrAmtOrFreeQtyHeaderWise(SchemeBean schPerCalBean, String mStrSlabRuleId,
                                                                String mOrderQty, String mMatNo, String mOrderMatGrp, String mCPItemGUID, String isHeaderBased) {
        double mDoubSecDisOrAmtOrQty = 0.0;
        if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeProduct)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }

            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getMaterialDesc(), schPerCalBean.getMaterialNo(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeSKUGroup)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getSKUGroupDesc(), schPerCalBean.getSKUGroupID(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeCRSSKUGroup)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }

            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getOrderMaterialGroupDesc(), schPerCalBean.getOrderMaterialGroupID(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutPerc(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutPerc(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutAmount(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutAmount(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeScratchCard)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }

            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getCardTitle(), schPerCalBean.getCardTitle(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeFreeArticle)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getFreeArticle(), schPerCalBean.getFreeArticle(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        }

        return mDoubSecDisOrAmtOrQty;
    }

    private void FreeProduct(SchemeBean schPerCalBean, String mStrSlabRuleId, String mOrderMatGrp, String mCPItemGUID, String mDoubSecDisOrAmtOrQty) {

        if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeProduct)) {
            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getMaterialDesc(), schPerCalBean.getMaterialNo(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeSKUGroup)) {

            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getSKUGroupDesc(), schPerCalBean.getSKUGroupID(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeCRSSKUGroup)) {
            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getOrderMaterialGroupDesc(),
                    schPerCalBean.getOrderMaterialGroupID(), mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {

        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {

        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeScratchCard)) {

            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getCardTitle(), schPerCalBean.getCardTitle(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeFreeArticle)) {

            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getFreeArticle(), schPerCalBean.getFreeArticle(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        }

    }
    private double getSchSlabTypeIDCalculationTargetByAmount(String SlabTypeID, String freePerOrQty, String slabTOValue, String orderQty,
                                                             String mStrSlabRuleId, String mStrSchemeItemGuid,
                                                             String mMatNo, String mStrTargetBasedID,
                                                             String mStrSlabFromVal, String isHeaderBased, String mTargetAmount) {
        Double mDoubSlabCal = 0.0;
        if (SlabTypeID.equalsIgnoreCase("000001")) { //  000001	Running
            Constants.DoubGetRunningSlabPer = 0.0;
            String[] orderMatArray = null;
            if (!isHeaderBased.equalsIgnoreCase(Constants.X)) {
                orderMatArray = new String[1];
                orderMatArray[0] = mMatNo;
            } else {
                orderMatArray = orderMatGrp;
            }
            try {
                OfflineManager.getSecondarySchemeSlabPerRunning(Constants.SchemeSlabs + "?$filter="
                                + Constants.SchemeItemGUID + " eq guid'" + mStrSchemeItemGuid.toUpperCase() + "' ", orderQty + "",
                        mStrSchemeItemGuid, mStrTargetBasedID, orderMatArray, isHeaderBased,mTargetAmount);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            mDoubSlabCal = Constants.DoubGetRunningSlabPer;
        } else if (SlabTypeID.equalsIgnoreCase("000002")) { //   000002	Fixed
            try {
                mDoubSlabCal = Double.parseDouble(freePerOrQty) * 1;
            } catch (NumberFormatException e) {
                mDoubSlabCal = 0.0;
            }
        } else if (SlabTypeID.equalsIgnoreCase("000003")) { //   000003	Step
            String mOrderVal = "0";

            String[] orderMatArray = null;
            if (!isHeaderBased.equalsIgnoreCase(Constants.X)) {
                orderMatArray = new String[1];
                orderMatArray[0] = mMatNo;
                mOrderVal = mTargetAmount;
            } else {
                mOrderVal = mTargetAmount;
            }


            BigInteger mDouCalStep;
            BigInteger mBigCalStep;
            BigInteger mBigResultValue = null;
            try {
                mDouCalStep = new BigInteger(UtilConstants.removeLeadingZeroVal(mOrderVal + ""));
                mBigCalStep = new BigInteger(UtilConstants.removeLeadingZeroVal(mStrSlabFromVal + ""));
                mBigResultValue = mDouCalStep.divide(mBigCalStep);
            } catch (NumberFormatException e) {
                mDouCalStep = new BigInteger("0");
                mBigResultValue = new BigInteger("0");
            }

            try {
                mDoubSlabCal = Double.parseDouble(freePerOrQty) * mBigResultValue.doubleValue();
            } catch (NumberFormatException e) {
                mDoubSlabCal = 0.0;
            }

        } else if (SlabTypeID.equalsIgnoreCase("000004")) { //   000004	Linear


            String mOrderVal = "0";

            String[] orderMatArray = null;
            if (!isHeaderBased.equalsIgnoreCase(Constants.X)) {
                orderMatArray = new String[1];
                orderMatArray[0] = mMatNo;
                mOrderVal = mTargetAmount;
            } else {
                orderMatArray = orderMatGrp;
                mOrderVal = mTargetAmount;
            }

            try {
                mDoubSlabCal = Double.parseDouble(mOrderVal) / Double.parseDouble(mStrSlabFromVal) * Double.parseDouble(freePerOrQty);
            } catch (NumberFormatException e) {
                mDoubSlabCal = 0.0;
            }
            if (mDoubSlabCal.isInfinite() || mDoubSlabCal.isNaN()) {
                mDoubSlabCal = 0.0;
            }

            // Changed code date 19-05-2017
            if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage) || mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                mDoubSlabCal = Double.valueOf(mDoubSlabCal);
            } else {
                mDoubSlabCal = Double.valueOf(Constants.round(mDoubSlabCal));  // Round off only QTY
            }
        }
        return mDoubSlabCal;
    }
    private static SchemeBean getFreeMatTxt(String mStrFrreQty, String mStrFreeMatTxt,
                                            String mStrFreeMat, String mCPITemGUID,
                                            String mCRSSKUGrp, String freeQTYUOM, String mStrSlabRuleId, String mStrFreeMatCritria) {
        SchemeBean schemeBean = new SchemeBean();
        schemeBean.setFreeQty(mStrFrreQty);
        schemeBean.setFreeMatTxt(mStrFreeMatTxt);
        schemeBean.setFreeMAt(mStrFreeMat);
        schemeBean.setOrderMaterialGroupID(mCRSSKUGrp);
        schemeBean.setFreeQtyUOM(freeQTYUOM);

        SchemeBean freeMatQtyBean = getMaterialPrice(mStrSlabRuleId, mStrFreeMat, mStrFrreQty,mStrFreeMatCritria);

        schemeBean.setFreeMatPrice(freeMatQtyBean.getFreeMatPrice());
        schemeBean.setFreeMatTax(freeMatQtyBean.getFreeMatTax());
        try {
            if (!mCPITemGUID.equalsIgnoreCase("")) {
                schemeBean.setBatch(OfflineManager.getValueByColumnName(Constants.CPStockItemSnos + "?$filter="
                        + Constants.CPStockItemGUID + " eq guid'" + mCPITemGUID.toUpperCase() + "' and " + Constants.MaterialNo + " eq '" + mStrFreeMat + "' and "
                        + Constants.ManufacturingDate + " ne null and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "' &$orderby=" + Constants.ManufacturingDate + "%20asc  ", Constants.Batch));
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return schemeBean;
    }
    private static SchemeBean getMaterialPrice(String mStrSlabRuleID, String mStrMaterial, String mStrFreeQty, String mStrFreeMatCritria) {
        SchemeBean freeMatBean = new SchemeBean();
        ODataEntity entity = null;
        String mStrMatNo = "";
        String mStrMatUnitPrice = "0";
        Double mDouCalMatPrice = 0.0;

        if (mStrSlabRuleID.equalsIgnoreCase(Constants.SchemeFreeProduct)) {
            // material
            try {
                entity = OfflineManager.getDecimalValueByColumnName(Constants.CPStockItemSnos + "?$filter="
                        + Constants.MaterialNo + " eq '" + mStrMaterial + "' " +
                        " and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "' &$orderby=" + Constants.ManufacturingDate + "%20asc  ", Constants.IntermUnitPrice);

            } catch (OfflineODataStoreException e) {
                entity = null;
            }

        } else if (mStrSlabRuleID.equalsIgnoreCase(Constants.SchemeFreeSKUGroup)) {
            // sku group id

            ArrayList<String> alFreeMatList = OfflineManager.getFreeMaterialsFromSchFreeMatGrp(Constants.SchemeFreeMatGrpMaterials + "?$select=" + Constants.MaterialNo + " &$filter = "
                    + Constants.SchFreeMatGrpGUID + " eq guid'" + Constants.convertStrGUID32to36(mStrMaterial) + "' and " + Constants.StatusID + " eq '" + Constants.str_01 + "' &$orderby = ItemNo asc ", Constants.MaterialNo);

            if (mStrFreeMatCritria.equalsIgnoreCase(Constants.SchemeFreeProdSeq)) {

                if (alFreeMatList != null && alFreeMatList.size() > 0) {
                    for (String mStrFreeMatNo : alFreeMatList) {
                        try {
                            entity = OfflineManager.getDecimalValueByColumnName(Constants.CPStockItemSnos + "?$filter="
                                    + Constants.MaterialNo + " eq '" + mStrFreeMatNo + "' " +
                                    " and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "' &$orderby=" + Constants.ManufacturingDate + "%20asc  ", Constants.IntermUnitPrice);

                            if (entity != null) {
                                break;
                            }

                        } catch (OfflineODataStoreException e) {
                            entity = null;
                        }
                    }
                }


            } else if (mStrFreeMatCritria.equalsIgnoreCase(Constants.SchemeFreeProdLowMRP)) {
                if (alFreeMatList != null && alFreeMatList.size() > 0) {
                    String mStrMatListQry = Constants.makeCPQry(alFreeMatList, Constants.MaterialNo);
                    try {
                        entity = OfflineManager.getFreeProdLowestMrp(Constants.CPStockItemSnos + "?$filter= (" + mStrMatListQry + ") and "
                                + Constants.StockTypeID + " ne '" + Constants.str_3 + "' &$orderby=" + Constants.ManufacturingDate + "%20asc  ", Constants.IntermUnitPrice);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                entity = null;
            }

        } else if (mStrSlabRuleID.equalsIgnoreCase(Constants.SchemeFreeCRSSKUGroup)) {
            // order mat grp

            try {
                mStrMatNo = OfflineManager.getValueByColumnName(Constants.SchemeFreeMatGrpMaterials + "?$select=" + Constants.MaterialNo + " &$filter = "
                        + Constants.MaterialGrp + " eq '" + mStrMaterial + "' &$orderby = ItemNo asc &$top=1", Constants.MaterialNo);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            try {
                entity = OfflineManager.getDecimalValueByColumnName(Constants.CPStockItemSnos + "?$filter="
                        + Constants.MaterialNo + " eq '" + mStrMatNo + "' " +
                        " and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "' &$orderby=" + Constants.ManufacturingDate + "%20asc  ", Constants.IntermUnitPrice);

            } catch (OfflineODataStoreException e) {
                entity = null;
            }
        } else {
            // free article and free free scratch is pending

            mStrMatUnitPrice = "0";
            entity = null;
        }
        String mStrTaxAmt = "0";
        if (entity != null) {
            ODataProperty property;
            ODataPropMap properties;

            properties = entity.getProperties();
            property = properties.get(Constants.IntermUnitPrice);

            try {
                BigDecimal mBigDecVal = (BigDecimal) property.getValue();
                mStrMatUnitPrice = mBigDecVal.doubleValue() + "";
            } catch (Exception e) {
                mStrMatUnitPrice = "0";
            }

            try {
                mDouCalMatPrice = Double.parseDouble(mStrMatUnitPrice) * Double.parseDouble(mStrFreeQty);
            } catch (NumberFormatException e) {
                mDouCalMatPrice = 0.0;
            }
            if (mDouCalMatPrice.isNaN() || mDouCalMatPrice.isInfinite()) {
                mDouCalMatPrice = 0.0;
            }

            mStrTaxAmt = Constants.getTaxAmount(mDouCalMatPrice + "", "0", entity, mStrFreeQty);
        }


        freeMatBean.setFreeMatPrice(mDouCalMatPrice + "");

        freeMatBean.setFreeMatTax(mStrTaxAmt + "");

        return freeMatBean;
    }
    private double getSchSlabTypeIDCalculation(String SlabTypeID, String freePerOrQty, String slabTOQty,
                                               String orderQty, String mStrSlabRuleId,
                                               String mStrSchemeItemGuid, String mStrSlabFromQty, String isHeaderBased) {
        Double mDoubSlabCal = 0.0;
        if (SlabTypeID.equalsIgnoreCase("000001")) { //  000001	Running
            Constants.DoubGetRunningSlabPer = 0.0;
            try {
                OfflineManager.getSecondarySchemeSlabPerRunning(Constants.SchemeSlabs + "?$filter="
                                + Constants.SchemeItemGUID + " eq guid'" + mStrSchemeItemGuid.toUpperCase() + "' ",
                        orderQty + "", mStrSchemeItemGuid, "", null, isHeaderBased,"0");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            mDoubSlabCal = Constants.DoubGetRunningSlabPer;
        } else if (SlabTypeID.equalsIgnoreCase("000002")) { //  000002	Fixed
            try {
                mDoubSlabCal = Double.parseDouble(freePerOrQty) * 1;
            } catch (NumberFormatException e) {
                mDoubSlabCal = 0.0;
            }
        } else if (SlabTypeID.equalsIgnoreCase("000003")) { //  000003	Step
            BigInteger mDouCalStep;
            BigInteger mBigCalStep;
            BigInteger mBigResultValue = null;
            try {
                mDouCalStep = new BigInteger(UtilConstants.removeLeadingZeroVal(orderQty + ""));
                mBigCalStep = new BigInteger(UtilConstants.removeLeadingZeroVal(mStrSlabFromQty + ""));
                mBigResultValue = mDouCalStep.divide(mBigCalStep);
            } catch (NumberFormatException e) {
                mDouCalStep = new BigInteger("0");
                mBigResultValue = new BigInteger("0");
            }

            try {
                mDoubSlabCal = Double.parseDouble(freePerOrQty) * mBigResultValue.doubleValue();
            } catch (NumberFormatException e) {
                mDoubSlabCal = 0.0;
            }

        } else if (SlabTypeID.equalsIgnoreCase("000004")) { //   000004	Linear
            try {
                mDoubSlabCal = Double.parseDouble(orderQty) / Double.parseDouble(mStrSlabFromQty) * Double.parseDouble(freePerOrQty);
            } catch (NumberFormatException e) {
                mDoubSlabCal = 0.0;
            }
            if (mDoubSlabCal.isInfinite() || mDoubSlabCal.isNaN()) {
                mDoubSlabCal = 0.0;
            }

            // Changed code date 19-05-2017
            if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage) || mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                mDoubSlabCal = Double.valueOf(mDoubSlabCal);
            } else {
                mDoubSlabCal = Double.valueOf(Constants.round(mDoubSlabCal));  // Round off only QTY
            }

        }
        return mDoubSlabCal;
    }
    private SchemeBean getSecSchemeBeanByMaterial(String schemeGUID, String mStrMatNo, String mStrOrderQty, String mStrHeaderOrItemType) {
        SchemeBean schemeBean = null;
        String mStrMinOrderQty = "0";
        SchemeBean schemeItemBean =new SchemeBean();
        try {
            schemeItemBean = OfflineManager.getSchemeItemDetailsBySchemeGuid(Constants.SchemeItemDetails + "?$filter="
                    + Constants.SchemeGUID + " eq guid'" + schemeGUID + "'");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        boolean mBoolMinItemQtyAval = false;
        mStrMinOrderQty = schemeItemBean.getItemMin();
        if (Double.parseDouble(mStrMinOrderQty) >= 0) {
            if (Double.parseDouble(mStrOrderQty.equalsIgnoreCase("") ? "0" : mStrOrderQty) > 0) {
                if (Double.parseDouble(mStrMinOrderQty) <= Double.parseDouble(mStrOrderQty)) {
                    mBoolMinItemQtyAval = true;
                } else {
                    mBoolMinItemQtyAval = false;
                }
            }

        } else {
            mBoolMinItemQtyAval = true;
        }

        if (mBoolMinItemQtyAval) {
            if (!mStrHeaderOrItemType.equalsIgnoreCase(Constants.X)) {
                String[] orderMatArray = new String[1];
                orderMatArray[0] = mStrMatNo;
                try {
                    schemeBean = OfflineManager.getSecondarySchemeSlabPer(Constants.SchemeSlabs + "?$filter="
                                    + Constants.SchemeItemGUID + " eq guid'" + schemeGUID.toUpperCase() + "' ", mStrOrderQty,
                            schemeGUID, Constants.Material, orderMatArray, mStrHeaderOrItemType,schemeItemBean);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            } else {
                //  here write logic  Constants.MAPSCHGuidByMaterial
                boolean schemeISAval = false;
                if (setSchemeList.size() == 0) {
                    schemeISAval = true;
                } else if (!setSchemeList.contains(schemeGUID)) {
                    schemeISAval = true;
                }

                if (schemeISAval) {
                    Multimap<String, String> multiMap = HashMultimap.create();
                    for (Map.Entry<String, ArrayList<String>> entry : Constants.MAPSCHGuidByMaterial.entrySet()) {
                        if (!entry.getValue().isEmpty()) {
                            for (String stValue : entry.getValue()) {
                                multiMap.put(stValue, entry.getKey());
                            }
                        }
                    }
                    String[] materialGrpArray = new String[multiMap.get(schemeGUID).size()];
                    Double mStrSumQtyByMat = 0.0;
                    for (Map.Entry<String, Collection<String>> entry : multiMap.asMap().entrySet()) {
                        if (schemeGUID.equalsIgnoreCase(entry.getKey())) {
                            Collection<String> values = entry.getValue();
                            int index = 0;
                            for (String value : values) {
                                if (mapMaterialWiseQty.containsKey(value)) {
                                    if (mapMaterialWiseQty.get(value) > 0) {
                                        materialGrpArray[index] = value;
                                        mStrSumQtyByMat = mStrSumQtyByMat + mapMaterialWiseQty.get(value);
                                        index++;
                                    }
                                }
                            }
                        }
                    }

                    try {
                        schemeBean = OfflineManager.getSecondarySchemeSlabPer(Constants.SchemeSlabs + "?$filter="
                                        + Constants.SchemeItemGUID + " eq guid'" + schemeGUID.toUpperCase() + "' ", mStrSumQtyByMat + "",
                                schemeGUID, Constants.Material, materialGrpArray, mStrHeaderOrItemType,schemeItemBean);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }

                    if (schemeBean != null) {
                        mapHeaderWiseSchemeQty.put(schemeGUID.toUpperCase(), mStrSumQtyByMat + "");
                        mFreeMat = null;
                        int incVal = 0;
                        Double mDouSlabTypeCal = 0.0;
                        if (schemeBean.getTargetBasedID().equalsIgnoreCase("02")) {
                            mDouSlabTypeCal = getSecondaryDiscountOrAmtOrFreeQtyHeaderWise(schemeBean, schemeBean.getSlabRuleID(),
                                    schemeBean.getSaleUnitID().equalsIgnoreCase(Constants.SchemeSaleUnitIDCBB)?schemeBean.getCBBQty():mStrSumQtyByMat + "", "", "", "", Constants.X);
                            // added 06/09/2017
                            if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                try {

                                    for (String matGuid : materialGrpArray) {
                                        if (!TextUtils.isEmpty(matGuid)) {
                                            incVal++;
                                        }
                                    }

                                    if (incVal > 0) {
                                        schemeBean.setPayoutAmount((Double.parseDouble(schemeBean.getPayoutAmount()) / incVal) + "");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            String mStrFreeQty = "";
                            if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
                                mStrFreeQty = schemeBean.getPayoutPerc();
                            } else if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                try {

                                    for (String matGuid : materialGrpArray) {
                                        if (!TextUtils.isEmpty(matGuid)) {
                                            incVal++;
                                        }
                                    }

                                    if (incVal > 0) {
                                        schemeBean.setPayoutAmount((Double.parseDouble(schemeBean.getPayoutAmount()) / incVal) + "");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                mStrFreeQty = schemeBean.getPayoutAmount();
                            } else {
                                mStrFreeQty = schemeBean.getFreeQty();
                            }
                            mDouSlabTypeCal = getSchSlabTypeIDCalculation(schemeBean.getSlabTypeID(),
                                    mStrFreeQty, schemeBean.getToQty(), schemeBean.getSaleUnitID().equalsIgnoreCase(Constants.SchemeSaleUnitIDCBB)?schemeBean.getCBBQty():mStrSumQtyByMat + "", schemeBean.getSlabRuleID(),
                                    schemeGUID.toUpperCase(), schemeBean.getFromQty(), Constants.X);
                        }


                        for (String mStrMaterial : materialGrpArray) {
                            FreeProduct(schemeBean, schemeBean.getSlabRuleID(), mStrMaterial, "", mDouSlabTypeCal + "");

                            ArrayList<SchemeBean> schemeBeanArrayList = hashMapSchemeValByMaterial.get(mStrMaterial);
                            if (schemeBeanArrayList==null){
                                schemeBeanArrayList = new ArrayList<>();
                                schemeBeanArrayList.add(schemeBean);
                                hashMapSchemeValByMaterial.put(mStrMaterial, schemeBeanArrayList);
                            }else {
                                if (!checkSchemeIsPresentInList(schemeBeanArrayList, schemeBean)) {
                                    schemeBeanArrayList.add(schemeBean);
                                    hashMapSchemeValByMaterial.put(mStrMaterial, schemeBeanArrayList);
                                }
                            }
                            try {
                                // added in 06/09/2017
                                if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                    if (incVal > 0) {
                                        mapSchemePerORAmtByMaterial.put(mStrMaterial + "_" + schemeGUID, (mDouSlabTypeCal/incVal) + "");
                                    }else{
                                        mapSchemePerORAmtByMaterial.put(mStrMaterial + "_" + schemeGUID, mDouSlabTypeCal + "");
                                    }
                                }else{
                                    mapSchemePerORAmtByMaterial.put(mStrMaterial + "_" + schemeGUID, mDouSlabTypeCal + "");
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mapSchemeFreeMatByMaterial.put(mStrMaterial, mFreeMat);
                        }
                    }

                    setSchemeList.add(schemeGUID.toUpperCase());

                }
            }


        } else {
            schemeBean = null;
        }

        return schemeBean;
    }
    private boolean checkSchemeIsPresentInList(ArrayList<SchemeBean> schemeBeanArrayList, SchemeBean schemeBeans) {
        for (SchemeBean schemeBean: schemeBeanArrayList){
            if (schemeBean.getSchemeGuid().equalsIgnoreCase(schemeBeans.getSchemeGuid())){
                return true;
            }
        }
        return false;
    }
    private void getLocation() {
        if (soCreateView != null) {
            soCreateView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            LocationUtils.checkLocationPermission(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    if (soCreateView != null) {
                        soCreateView.hideProgressDialog();
                    }
                    if (status) {
                        locationPerGranted();
                    }
                }
            });
        }
    }
    private void locationPerGranted() {
        if (soCreateView != null) {
            soCreateView.showProgressDialog(mContext.getString(R.string.checking_pemission));
            Constants.getLocation(mActivity, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                    if (soCreateView != null) {
                        soCreateView.hideProgressDialog();
                    }
                    if (status) {
                        if (ConstantsUtils.isAutomaticTimeZone(mContext)) {
                            finalSaveCondition();
                        } else {
                            if (soCreateView != null)
                                ConstantsUtils.showAutoDateSetDialog(mActivity);
                        }
                    }
                }
            });
        }
    }
    private void getMaterialSchemeCal() {
        if (!Constants.MAPSCHGuidByMaterial.isEmpty()) {
            if (alReviewSOItems != null && alReviewSOItems.size() > 0) {
                for (int i = 0; i < alReviewSOItems.size(); i++) {
                    final SKUGroupBean skuGroupBean = alReviewSOItems.get(i);
                    ArrayList<String> materialSchemeArrayList = Constants.MAPSCHGuidByMaterial.get(skuGroupBean.getMaterialNo());
                    if (materialSchemeArrayList!=null && !materialSchemeArrayList.isEmpty()) {
                        for (String materialScheme : materialSchemeArrayList) {
                            // check scheme is instant scheme or not
                            if (Constants.HashMapSchemeValuesBySchemeGuid.containsKey(materialScheme.toUpperCase())) {  // added in 15/09/2017
                                if (schemeIsAvaliable(materialScheme)) {
                                    // check scheme is Basket scheme or not
                                    if (!Constants.HashMapSchemeValuesBySchemeGuid.get(materialScheme.toUpperCase()).getSchemeTypeID().equalsIgnoreCase(Constants.SchemeTypeIDBasketScheme)) {    // added 15/09/2017
                                        // check scheme calculation  HDR SLAB BASED/ ITEM SLAB BASED
                                        if (!isSchemeHeaderBasedOrItemBased(materialScheme)) {
                                            SchemeBean schemeBean = getSecSchemeBeanByMaterial(materialScheme,
                                                    skuGroupBean.getMaterialNo(), skuGroupBean.getORDQty(), "");
                                            if (schemeBean != null) {
                                                ArrayList<SchemeBean> schemeBeanArrayList = hashMapSchemeValByMaterial.get(skuGroupBean.getMaterialNo());
                                                if (schemeBeanArrayList==null){
                                                    schemeBeanArrayList = new ArrayList<>();
                                                    schemeBeanArrayList.add(schemeBean);
                                                    hashMapSchemeValByMaterial.put(skuGroupBean.getMaterialNo(), schemeBeanArrayList);
                                                }else {
                                                    if (!checkSchemeIsPresentInList(schemeBeanArrayList, schemeBean)) {
                                                        schemeBeanArrayList.add(schemeBean);
                                                        hashMapSchemeValByMaterial.put(skuGroupBean.getMaterialNo(), schemeBeanArrayList);
                                                    }
                                                }

                                            }
                                        } else {
                                            SchemeBean schemeBean = getSecSchemeBeanByMaterial(materialScheme,
                                                    skuGroupBean.getMaterialNo(), skuGroupBean.getORDQty(), Constants.X);
                                            if (schemeBean != null) {
                                                ArrayList<SchemeBean> schemeBeanArrayList = hashMapSchemeValByMaterial.get(skuGroupBean.getMaterialNo());
                                                if (schemeBeanArrayList==null){
                                                    schemeBeanArrayList = new ArrayList<>();
                                                    schemeBeanArrayList.add(schemeBean);
                                                    hashMapSchemeValByMaterial.put(skuGroupBean.getMaterialNo(), schemeBeanArrayList);
                                                }else {
                                                    if (!checkSchemeIsPresentInList(schemeBeanArrayList, schemeBean)) {
                                                        schemeBeanArrayList.add(schemeBean);
                                                        hashMapSchemeValByMaterial.put(skuGroupBean.getMaterialNo(), schemeBeanArrayList);
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        getBasketSchemeCalByMaterial(materialScheme, skuGroupBean.getORDQty());
                                    }
                                }


                            }
                        }
                    }
                }
            }
        }
    }

    private void sumOfSkuGrpItems() {
        alSchFreeProd = new ArrayList<>();
        if (alReviewSOItems != null && alReviewSOItems.size() > 0) {
            SchemeBean primaryDisTaxValBean = null;
            for (int i = 0; i < alReviewSOItems.size(); i++) {
                final SKUGroupBean skuGroupBean = alReviewSOItems.get(i);

                double calSecPer = 0.0;
                double mDoubSecPer = 0.0;
                SchemeBean appliedRatio=null;
                ArrayList<SchemeBean> schPerCalBeanList = null;
                ArrayList<SchemeBean> schPerOrderMatLevelList = null;
                if (Constants.MAPSCHGuidByMaterial.containsKey(skuGroupBean.getMaterialNo())) {
                    schPerCalBeanList = hashMapSchemeValByMaterial.get(skuGroupBean.getMaterialNo());
                    setMaterialLevelScheme(schPerCalBeanList);
                }
                if (mSetOrderMatGrp.size() == 0) {
                    mSetOrderMatGrp.add(skuGroupBean.getSKUGroup());
                    schPerOrderMatLevelList = hashMapSchemeValByOrderMatGrp.get(skuGroupBean.getSKUGroup());
                } else if (!mSetOrderMatGrp.contains(skuGroupBean.getSKUGroup())) {
                    mSetOrderMatGrp.add(skuGroupBean.getSKUGroup());
                    schPerOrderMatLevelList = hashMapSchemeValByOrderMatGrp.get(skuGroupBean.getSKUGroup());
                }
                if (schPerCalBeanList != null && schPerOrderMatLevelList != null) {
                    schPerCalBeanList.addAll(schPerOrderMatLevelList);
                } else if (schPerOrderMatLevelList != null) {
                    schPerCalBeanList = schPerOrderMatLevelList;
                }
                schPerCalBeanList = Constants.removeDuplicateScheme(schPerCalBeanList);

                String secondarySchemeAmt = "0", secondarySchemePerAmt = "0", schemeSlabRule = "";
                Double mDouSumNetTaxSecAmt = 0.0, mDouSumPriDis = 0.0, mDouSumSecDiscount = 0.0, mDouPriDis = 0.0, mDouSecDiscount = 0.0, mDouSecAmt = 0.0, mDouSumTax = 0.0;

                mFreeMat = null;
                ArrayList<SchemeCalcuBean> schemeCalcuBeanArrayList = new ArrayList<>();
                if (schPerCalBeanList!=null && !schPerCalBeanList.isEmpty()) {
                    int totalSchemeSize =schPerCalBeanList.size();
                    int currentSize = 0;
                    for (SchemeBean schPerCalBeans : schPerCalBeanList){
                        currentSize++;
                        SchemeCalcuBean schemeCalcuBean = new SchemeCalcuBean();

                        String mStrSchemeGUID = "";
                        String mStrSlabRuleId = schPerCalBeans.getSlabRuleID();
                        Double mDouSlabTypeCal = 0.0;

                        if (schPerCalBeans.isMaterialLevel()) {
                            if (!schPerCalBeans.getIsHeaderBasedSlab().equalsIgnoreCase(Constants.X)) {
                                mDouSlabTypeCal = getSecondaryDiscountOrAmtOrFreeQty(schPerCalBeans.isMaterialLevel(), schPerCalBeans, mStrSlabRuleId,
                                        schPerCalBeans.getSaleUnitID().equalsIgnoreCase(Constants.SchemeSaleUnitIDCBB)?schPerCalBeans.getCBBQty():skuGroupBean.getORDQty(), skuGroupBean.getMaterialNo(), skuGroupBean.getSKUGroup(), skuGroupBean.getCPStockItemGUID(), "");
                                mStrSchemeGUID = schPerCalBeans.getSchemeGuid();
                            } else {
                                mStrSchemeGUID = schPerCalBeans.getSchemeGuid();
                                mDouSlabTypeCal = Double.parseDouble(mapSchemePerORAmtByMaterial.get(skuGroupBean.getMaterialNo()+"_"+mStrSchemeGUID));
                                mFreeMat = mapSchemeFreeMatByMaterial.get(skuGroupBean.getMaterialNo());
                            }
                        } else {
                            if (!schPerCalBeans.getIsHeaderBasedSlab().equalsIgnoreCase(Constants.X)) {
                                mDouSlabTypeCal = getSecondaryDiscountOrAmtOrFreeQty(schPerCalBeans.isMaterialLevel(), schPerCalBeans, mStrSlabRuleId,
                                        schPerCalBeans.getSaleUnitID().equalsIgnoreCase(Constants.SchemeSaleUnitIDCBB)?schPerCalBeans.getCBBQty():skuGroupBean.getORDQty(), skuGroupBean.getMaterialNo(), skuGroupBean.getSKUGroup(), skuGroupBean.getCPStockItemGUID(), "");
                                mStrSchemeGUID = schPerCalBeans.getSchemeGuid();
                            } else {
                                mStrSchemeGUID = schPerCalBeans.getSchemeGuid();
                                mDouSlabTypeCal = Double.parseDouble(mapSchemePerORAmtByOrderMatGrp.get(skuGroupBean.getSKUGroup()+"_"+mStrSchemeGUID));
                                mFreeMat = mapSchemeFreeMatByOrderMatGrp.get(skuGroupBean.getSKUGroup()+"_"+mStrSchemeGUID);
                            }
                        }
                        calSecPer = 0.0;
                        mDoubSecPer = 0.0;
                        mDouSecAmt = 0.0;
                        boolean isBatchSecondTime = false;
                        secondarySchemeAmt = "0";
                        boolean mBoolBasketSchemeapplicable = false;
                        primaryDisTaxValBean = Constants.getPrimaryTaxValByMaterial(skuGroupBean.getCPStockItemGUID(), skuGroupBean.getMaterialNo(),
                                skuGroupBean.getORDQty(),false,skuGroupBean.getBatch());
                        if (primaryDisTaxValBean != null) {
                            ArrayList<MaterialBatchBean> alMatBatchItemBean = primaryDisTaxValBean.getMaterialBatchBeanArrayList();
                            if (alMatBatchItemBean != null && alMatBatchItemBean.size() > 0) {
                                for (MaterialBatchBean matBatchItemBean : alMatBatchItemBean) {
                                    String priPercentage = matBatchItemBean.getPrimaryPer();

                                    if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
                                        mBoolBasketSchemeapplicable = false;//TODO need to check this logic
                                        mDouCalNetAmt = 0.0;
                                        if (schPerCalBeans.getSchemeTypeID().equalsIgnoreCase(Constants.SchemeTypeIDBasketScheme)) {
                                            mBoolBasketSchemeapplicable = getBasketSchemePer(schPerCalBeans, matBatchItemBean, skuGroupBean);
                                        }

                                        try {
                                            mDoubSecPer = Double.parseDouble(mDouSlabTypeCal + "");
                                        } catch (NumberFormatException e) {
                                            mDoubSecPer = 0.0;
                                        }


                                        if (schPerCalBeans.getIsIncludingPrimary().equalsIgnoreCase(Constants.X)) {
                                            calSecPer = mDoubSecPer - Double.parseDouble(priPercentage);
                                            if (calSecPer < 0) {
                                                calSecPer = 0;
                                            }
                                        } else {
                                            calSecPer = mDoubSecPer;
                                        }
                                        matBatchItemBean.setSecPer(calSecPer + "");
                                        matBatchItemBean.setSecDiscountAmt(0 + "");

                                        schemeSlabRule = Constants.SchemeFreeDiscountPercentage;

                                        if (currentSize==totalSchemeSize) {
                                            if (!mBoolBasketSchemeapplicable) {
                                                secondarySchemeAmt = Constants.calculatePrimaryDiscount(Constants.getSecDiscAmtPer(calSecPer, schemeCalcuBeanArrayList)+"", matBatchItemBean.getNetAmtAftPriDis());
                                            } else {
                                                secondarySchemeAmt = Constants.calculatePrimaryDiscount(Constants.getSecDiscAmtPer(calSecPer, schemeCalcuBeanArrayList) + "", mDouCalNetAmt + "");
                                            }
                                            secondarySchemeAmt = (Double.parseDouble(secondarySchemeAmt) + Constants.getSecSchemeAmt(mDouSecAmt, schemeCalcuBeanArrayList))+"";

                                            // TODO invoice cash discount added 12-07-2018 Start
                                            String netAmount = matBatchItemBean.getNetAmtAftPriDis();

                                            String invCashDis =   Constants.calculatePrimaryDiscount(skuGroupBean.getInvCashDisc() + "", netAmount);

                                            String calSchmAndCashDisc = null;
                                            try {
                                                calSchmAndCashDisc = (Double.parseDouble(secondarySchemeAmt)+Double.parseDouble(invCashDis))+"";
                                            } catch (NumberFormatException e) {
                                                calSchmAndCashDisc = "0";
                                                e.printStackTrace();
                                            }

                                            // Todo 12-07-2018 end

                                            //apply ratio scheme
//                                            appliedRatio = OfflineManager.applyRatioScheme(primaryDisTaxValBean,skuGroupBean.getORDQty(),alMatBatchItemBean,secondarySchemeAmt,matBatchItemBean);
                                            appliedRatio = OfflineManager.applyRatioScheme(primaryDisTaxValBean,skuGroupBean.getORDQty(),alMatBatchItemBean,calSchmAndCashDisc,matBatchItemBean);

                                            String mStrTaxAmt = Constants.getTaxAmount(appliedRatio.getMatNetAmtAftPriDis(), "0", matBatchItemBean.getoDataEntity(), matBatchItemBean.getQty());
                                            matBatchItemBean.setTax(mStrTaxAmt);

                                            matBatchItemBean.setTotalNetAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()) + Double.parseDouble(mStrTaxAmt)) + "");
                                            mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()) + Double.parseDouble(mStrTaxAmt));


                                            matBatchItemBean.setGrossAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis())+ ""));
                                            matBatchItemBean.setInvCashDisc(skuGroupBean.getInvCashDisc());

                                        }

                                    } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                        String mStrBatchQty = matBatchItemBean.getQty();
                                        calSecPer = 0.0;
                                        try {
                                            mDouSecAmt = Double.parseDouble(mDouSlabTypeCal + "");
                                        } catch (NumberFormatException e) {
                                            mDouSecAmt = mDouSecAmt+ 0.0;
                                        }

                                        matBatchItemBean.setSecPer(0 + "");
                                        matBatchItemBean.setSecDiscountAmt(0 + "");
                                        matBatchItemBean.setSlabRuleAmt(Constants.SchemeFreeDiscountAmount);
                                        schemeSlabRule = Constants.SchemeFreeDiscountAmount;
                                        // If amount based scheme scheme amount adjust to singele line item
                                        if (!mStrAmtBasedSchemeAvl.contains(mStrSchemeGUID.toUpperCase())) {
                                            mStrAmtBasedSchemeAvl.add(mStrSchemeGUID.toUpperCase());
                                            if (currentSize==totalSchemeSize) {
                                                if (!mBoolBasketSchemeapplicable) {
                                                    secondarySchemeAmt = Constants.calculatePrimaryDiscount(Constants.getSecDiscAmtPer(calSecPer, schemeCalcuBeanArrayList)+"", matBatchItemBean.getNetAmtAftPriDis());// TODO change to globaly
                                                } else {
                                                    secondarySchemeAmt = Constants.calculatePrimaryDiscount(Constants.getSecDiscAmtPer(calSecPer, schemeCalcuBeanArrayList) + "", mDouCalNetAmt + ""); // TODO change to globaly
                                                }
                                                secondarySchemeAmt = (Double.parseDouble(secondarySchemeAmt) + Constants.getSecSchemeAmt(mDouSecAmt, schemeCalcuBeanArrayList))+"";

                                                // TODO invoice cash discount added 12-07-2018 Start
                                                String netAmount = matBatchItemBean.getNetAmtAftPriDis();

                                                String invCashDis =   Constants.calculatePrimaryDiscount(skuGroupBean.getInvCashDisc() + "", netAmount);

                                                String calSchmAndCashDisc = null;
                                                try {
                                                    calSchmAndCashDisc = (Double.parseDouble(secondarySchemeAmt)+Double.parseDouble(invCashDis))+"";
                                                } catch (NumberFormatException e) {
                                                    calSchmAndCashDisc = "0";
                                                    e.printStackTrace();
                                                }

                                                // Todo 12-07-2018 end

                                                //apply ratio scheme
//                                                appliedRatio = OfflineManager.applyRatioScheme(primaryDisTaxValBean,skuGroupBean.getORDQty(),alMatBatchItemBean,secondarySchemeAmt,matBatchItemBean);
                                                appliedRatio = OfflineManager.applyRatioScheme(primaryDisTaxValBean,skuGroupBean.getORDQty(),alMatBatchItemBean,calSchmAndCashDisc,matBatchItemBean);
                                                String mStrTaxAmt = Constants.getTaxAmount(appliedRatio.getMatNetAmtAftPriDis(), "0", matBatchItemBean.getoDataEntity(), matBatchItemBean.getQty());
                                                matBatchItemBean.setTax(mStrTaxAmt);

                                                matBatchItemBean.setTotalNetAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()) + Double.parseDouble(mStrTaxAmt)) + "");
                                                mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()) + Double.parseDouble(mStrTaxAmt));
                                                matBatchItemBean.setGrossAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis())+ ""));
                                                matBatchItemBean.setInvCashDisc(skuGroupBean.getInvCashDisc());
//                                            isTaxCalculated=true;
                                            }
                                        } else {
//                                        secondarySchemeAmt = "0";
                                            if (currentSize==totalSchemeSize) {
                                                if (!mBoolBasketSchemeapplicable) {
                                                    secondarySchemeAmt = Constants.calculatePrimaryDiscount(Constants.getSecDiscAmtPer(calSecPer, schemeCalcuBeanArrayList)+"", matBatchItemBean.getNetAmtAftPriDis());// TODO change to globaly
                                                } else {
                                                    secondarySchemeAmt = Constants.calculatePrimaryDiscount(Constants.getSecDiscAmtPer(calSecPer, schemeCalcuBeanArrayList) + "", mDouCalNetAmt + ""); // TODO change to globaly
                                                }
                                                secondarySchemeAmt = (Double.parseDouble(secondarySchemeAmt) + Constants.getSecSchemeAmt(mDouSecAmt, schemeCalcuBeanArrayList))+"";

                                                // TODO invoice cash discount added 12-07-2018 Start
                                                String netAmount = matBatchItemBean.getNetAmtAftPriDis();

                                                String invCashDis =   Constants.calculatePrimaryDiscount(skuGroupBean.getInvCashDisc() + "", netAmount);

                                                String calSchmAndCashDisc = null;
                                                try {
                                                    calSchmAndCashDisc = (Double.parseDouble(secondarySchemeAmt)+Double.parseDouble(invCashDis))+"";
                                                } catch (NumberFormatException e) {
                                                    calSchmAndCashDisc = "0";
                                                    e.printStackTrace();
                                                }

                                                //apply ratio scheme
//                                                appliedRatio = OfflineManager.applyRatioScheme(primaryDisTaxValBean,skuGroupBean.getORDQty(),alMatBatchItemBean,secondarySchemeAmt,matBatchItemBean);
                                                appliedRatio = OfflineManager.applyRatioScheme(primaryDisTaxValBean,skuGroupBean.getORDQty(),alMatBatchItemBean,calSchmAndCashDisc,matBatchItemBean);
                                                String mStrTaxAmt = Constants.getTaxAmount(appliedRatio.getMatNetAmtAftPriDis(), "0", matBatchItemBean.getoDataEntity(), matBatchItemBean.getQty());
                                                matBatchItemBean.setTax(mStrTaxAmt);

                                                matBatchItemBean.setTotalNetAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()) + Double.parseDouble(mStrTaxAmt)) + "");
                                                mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()) + Double.parseDouble(mStrTaxAmt));

                                                matBatchItemBean.setGrossAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis())+ ""));
                                                matBatchItemBean.setInvCashDisc(skuGroupBean.getInvCashDisc());
                                            }
                                        }


                                    } else {


                                        if (currentSize==totalSchemeSize) {
                                            secondarySchemeAmt = "0";

                                            // TODO invoice cash discount added 12-07-2018 Start
                                            String netAmount = matBatchItemBean.getNetAmtAftPriDis();

                                            String invCashDis =   Constants.calculatePrimaryDiscount(skuGroupBean.getInvCashDisc() + "", netAmount);

                                            String calSchmAndCashDisc = null;
                                            try {
                                                calSchmAndCashDisc = (Double.parseDouble(secondarySchemeAmt)+Double.parseDouble(invCashDis))+"";
                                            } catch (NumberFormatException e) {
                                                calSchmAndCashDisc = "0";
                                                e.printStackTrace();
                                            }

                                            // Todo 12-07-2018 end

                                            //apply ratio scheme
//                                            appliedRatio = OfflineManager.applyRatioScheme(primaryDisTaxValBean,skuGroupBean.getORDQty(),alMatBatchItemBean,secondarySchemeAmt,matBatchItemBean);
                                            appliedRatio = OfflineManager.applyRatioScheme(primaryDisTaxValBean,skuGroupBean.getORDQty(),alMatBatchItemBean,calSchmAndCashDisc,matBatchItemBean);

                                            matBatchItemBean.setTotalNetAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis())) + "");
                                            mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()));
                                            matBatchItemBean.setGrossAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis())+ ""));
                                            matBatchItemBean.setInvCashDisc(skuGroupBean.getInvCashDisc());
                                        }
                                        schemeSlabRule = "";
                                        secondarySchemeAmt = "0";
                                        mDouSecAmt = 0.0;
                                        matBatchItemBean.setSecPer(0 + "");
                                        matBatchItemBean.setSecDiscountAmt(0 + "");

                                        // Start Scheme calculation logic is added 14/07/2017
                                        if (mapNetPriceByScheme.containsKey(skuGroupBean.getSKUGroup()+"_"+mStrSchemeGUID)) {
                                            double mDouNetAmt = Double.parseDouble(matBatchItemBean.getNetAmount()) + mapNetPriceByScheme.get(skuGroupBean.getSKUGroup()+"_"+mStrSchemeGUID);
                                            mapNetPriceByScheme.put(skuGroupBean.getSKUGroup()+"_"+mStrSchemeGUID, mDouNetAmt);
                                        } else {
                                            double mDouNetAmt = Double.parseDouble(matBatchItemBean.getNetAmount());
                                            mapNetPriceByScheme.put(skuGroupBean.getSKUGroup()+"_"+mStrSchemeGUID, mDouNetAmt);
                                        }
                                        // End Scheme calculation logic is added 14/07/2017

                                    }


                                    mDouPriDis = Double.parseDouble(matBatchItemBean.getPrimaryPer());


                                    mDouSecDiscount = calSecPer;
                                }

                                // Start Scheme calculation logic is added 14/07/2017
                                if (!mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount) && !mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {

                                    if (mapFreeQtyeByScheme.containsKey(mStrSchemeGUID)) {
                                        double mDouQty = Double.parseDouble(skuGroupBean.getORDQty()) + mapFreeQtyeByScheme.get(mStrSchemeGUID);
                                        mapFreeQtyeByScheme.put(mStrSchemeGUID, mDouQty);
                                    } else {
                                        double mDouQty = Double.parseDouble(skuGroupBean.getORDQty());
                                        mapFreeQtyeByScheme.put(mStrSchemeGUID, mDouQty);
                                    }
                                }
                                // End Scheme calculation logic is added 14/07/2017

                                primaryDisTaxValBean.setMaterialBatchBeanArrayList(alMatBatchItemBean);
                            }else{
                                mStrCrsSKUList.add(skuGroupBean.getMaterialDesc());
                            }

                        } else {
                            mDouSumNetTaxSecAmt = 0.0;
                            mDouSumPriDis = 0.0;
                            mDouSumSecDiscount = 0.0;
                            mDouPriDis = 0.0;
                            mDouSecDiscount = 0.0;

                            schemeSlabRule = "";
                            secondarySchemeAmt = "0";
                            mStrCrsSKUList.add(skuGroupBean.getMaterialDesc());
                        }

                        // Start Scheme is free qty or not logic is added 10/08/2017
                        if (!mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)
                                && !mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
                            schemeCalcuBean.setSchemeFreeQty(true);
                        } else {
                            schemeCalcuBean.setSchemeFreeQty(false);
                        }
                        if (mFreeMat != null) {
                            schemeCalcuBean.setmFreeMat(mFreeMat);
                            alSchFreeProd.add(mFreeMat);
                        }
                        schemeCalcuBean.setSchemeGuidNo(mStrSchemeGUID);
                        schemeCalcuBean.setmDouSecDiscount(mDouSecDiscount);
                        schemeCalcuBean.setmDouSecAmt(mDouSecAmt);
                        schemeCalcuBean.setmDouSecDiscountAmt(Double.parseDouble(secondarySchemePerAmt));


                        schemeCalcuBeanArrayList.add(schemeCalcuBean);


                        if (!schemeSlabRule.equalsIgnoreCase("") && schemeSlabRule.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {

                            double finalDouSecAmt=0.0;
                            double secDiscount = Constants.getSecSchemeAmt(0.0, schemeCalcuBeanArrayList);
                            if (secDiscount>0){
                                finalDouSecAmt = secDiscount;
                            }else {
                                finalDouSecAmt=mDouSecDiscount;
                            }

                            alReviewSOItems.get(i).setSecSchemeAmt(finalDouSecAmt + "");
                            alReviewSOItems.get(i).setSchemeSlabRule(Constants.SchemeFreeDiscountAmount);
                        } else if (!schemeSlabRule.equalsIgnoreCase("") && schemeSlabRule.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
                            double finalSecDiscount=0.0;
                            double secDiscount = Constants.getSecDiscAmtPer(0.0, schemeCalcuBeanArrayList);
                            if (secDiscount>0){
                                finalSecDiscount = secDiscount;
                            }else {
                                finalSecDiscount=mDouSecDiscount;
                            }
                            alReviewSOItems.get(i).setSecScheme(finalSecDiscount + "");
                            alReviewSOItems.get(i).setSchemeSlabRule(Constants.SchemeFreeDiscountPercentage);
                        } else {
                            alReviewSOItems.get(i).setSchemeSlabRule("");
                        }
                    }

                    alReviewSOItems.get(i).setSchemeCalcuBeanArrayList(schemeCalcuBeanArrayList);

                    if (appliedRatio != null) {
                        try {
                            alReviewSOItems.get(i).setRatioSchDisAmt(appliedRatio.getRatioSchDisAmt() != null ? appliedRatio.getRatioSchDisAmt() : Constants.str_0);
                            alReviewSOItems.get(i).setRatioSchMatPrice(appliedRatio.getRatioSchMatPrice() != null ? appliedRatio.getRatioSchMatPrice() : Constants.str_0);
                        } catch (Exception e) {
                            alReviewSOItems.get(i).setRatioSchDisAmt(Constants.str_0);
                            alReviewSOItems.get(i).setRatioSchMatPrice(Constants.str_0);
                        }
                        try {
                            alReviewSOItems.get(i).setISFreeTypeID(appliedRatio.getISFreeTypeID()!=null?appliedRatio.getISFreeTypeID():Constants.str_0);
                        } catch (Exception e) {
                            alReviewSOItems.get(i).setISFreeTypeID(Constants.str_0);
                        }


                        if (!appliedRatio.getFreeMaterialNo().equalsIgnoreCase("")) {
                            appliedRatio.setRatioScheme(true);
                            alSchFreeProd.add(appliedRatio);
                            hashMapFreeMaterialByMaterial.put(skuGroupBean.getMaterialNo(), appliedRatio);
                            hashMapFreeMatByOrderMatGrp.put(skuGroupBean.getMaterialNo(), skuGroupBean.getSKUGroup());
                        }
                        alReviewSOItems.get(i).setMaterialBatchBean(appliedRatio);
                    }else {
                        alReviewSOItems.get(i).setMaterialBatchBean(primaryDisTaxValBean);
                    }

                } else {
                    schemeSlabRule = "";

                    primaryDisTaxValBean = Constants.getPrimaryTaxValByMaterial(skuGroupBean.getCPStockItemGUID(),
                            skuGroupBean.getMaterialNo(), skuGroupBean.getORDQty(),true,skuGroupBean.getBatch());
                    if (primaryDisTaxValBean != null) {
                        ArrayList<MaterialBatchBean> alMatBatchItemBean = primaryDisTaxValBean.getMaterialBatchBeanArrayList();
                        if (alMatBatchItemBean != null && alMatBatchItemBean.size() > 0) {
                            for (MaterialBatchBean matBatchItemBean : alMatBatchItemBean) {
                                String netAmount = matBatchItemBean.getNetAmount();
                                calSecPer = 0;
                                matBatchItemBean.setSecPer(calSecPer + "");
                                secondarySchemeAmt = Constants.calculatePrimaryDiscount(calSecPer + "", netAmount);

                                //12-07-2018 invoice cash discount added 12-07-2018
                                String invCashDis =   Constants.calculatePrimaryDiscount(skuGroupBean.getInvCashDisc() + "", netAmount);

                                String calSchmAndCashDisc = null;
                                try {
                                    calSchmAndCashDisc = (Double.parseDouble(secondarySchemeAmt)+Double.parseDouble(invCashDis))+"";
                                } catch (NumberFormatException e) {
                                    calSchmAndCashDisc = "0";
                                    e.printStackTrace();
                                }

                                String mStrTaxAmt = Constants.getTaxAmount(matBatchItemBean.getNetAmtAftPriDis(), calSchmAndCashDisc,
                                        matBatchItemBean.getoDataEntity(), matBatchItemBean.getQty());
                                matBatchItemBean.setTax(mStrTaxAmt);


                                matBatchItemBean.setTotalNetAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(calSchmAndCashDisc) + Double.parseDouble(mStrTaxAmt)) + "");
                                mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(calSchmAndCashDisc) + Double.parseDouble(mStrTaxAmt));

                                mDouPriDis = Double.parseDouble(matBatchItemBean.getPrimaryPer());

                                matBatchItemBean.setGrossAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(calSchmAndCashDisc) + ""));
                                matBatchItemBean.setInvCashDisc(skuGroupBean.getInvCashDisc());

                                mDouSecDiscount = calSecPer;
                            }
                            primaryDisTaxValBean.setMaterialBatchBeanArrayList(alMatBatchItemBean);
                        }else{
                            mStrCrsSKUList.add(skuGroupBean.getMaterialDesc());
                        }

                    } else {
                        mDouSumNetTaxSecAmt = 0.0;
                        mDouSumPriDis = 0.0;
                        mDouSumSecDiscount = 0.0;

                        mDouPriDis = 0.0;

                        mDouSecDiscount = 0.0;

                        schemeSlabRule = "";
                        secondarySchemeAmt = "0";
                        mStrCrsSKUList.add(skuGroupBean.getMaterialDesc());
                    }

                    schemeSlabRule = "";
                    try {//TODO need to check this logic
                        if (primaryDisTaxValBean != null) {

                            try {
                                alReviewSOItems.get(i).setRatioSchDisAmt(primaryDisTaxValBean.getRatioSchDisAmt()!=null?primaryDisTaxValBean.getRatioSchDisAmt():Constants.str_0);
                                alReviewSOItems.get(i).setRatioSchMatPrice(primaryDisTaxValBean.getRatioSchMatPrice()!=null?primaryDisTaxValBean.getRatioSchMatPrice():Constants.str_0);
                            } catch (Exception e) {
                                alReviewSOItems.get(i).setRatioSchDisAmt(Constants.str_0);
                                alReviewSOItems.get(i).setRatioSchMatPrice(Constants.str_0);
                            }
                            try {
                                alReviewSOItems.get(i).setISFreeTypeID(primaryDisTaxValBean.getISFreeTypeID()!=null?primaryDisTaxValBean.getISFreeTypeID():Constants.str_0);
                            } catch (Exception e) {
                                alReviewSOItems.get(i).setISFreeTypeID(Constants.str_0);
                            }


                            if (!primaryDisTaxValBean.getFreeMaterialNo().equalsIgnoreCase("")) {
                                hashMapFreeMaterialByMaterial.put(skuGroupBean.getMaterialNo(), primaryDisTaxValBean);
                                hashMapFreeMatByOrderMatGrp.put(skuGroupBean.getMaterialNo(), skuGroupBean.getSKUGroup());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    alReviewSOItems.get(i).setSecScheme("0.0");
                    alReviewSOItems.get(i).setMaterialBatchBean(primaryDisTaxValBean);
                }

                alReviewSOItems.get(i).setPRMScheme(mDouPriDis + "");

                alReviewSOItems.get(i).setNetAmount(mDouSumNetTaxSecAmt + "");

            }

        }

        adjustFreeQtyDiscountVal(alReviewSOItems);
        mapSKUGRPVal = getALSKUGRP(alReviewSOItems);
        skuGroupBeanArrayList.clear();
        Iterator iterator = mapSKUGRPVal.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            SKUGroupBean skuGroupBean = mapSKUGRPVal.get(key);
            skuGroupBeanArrayList.add(skuGroupBean);
        }
        Collections.sort(skuGroupBeanArrayList, new Comparator<SKUGroupBean>() {
            @Override
            public int compare(SKUGroupBean skuGroupBean, SKUGroupBean t1) {
                return skuGroupBean.getMaterialDesc().toLowerCase().compareTo(t1.getMaterialDesc().toLowerCase());
            }
        });
    }

    private Map<String, SKUGroupBean> getALSKUGRP(ArrayList<SKUGroupBean> alSKUList) {
        Map<String, SKUGroupBean> mapSKUList = new HashMap<>();
        if (alSKUList != null && alSKUList.size() > 0) {
            for (SKUGroupBean bean : alSKUList)
                if (mapNetAmt.containsKey(bean.getMaterialNo())) {
                    BigDecimal mDoubCRSQty = null;
                    try {
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        decimalFormat.setParseBigDecimal(true);
                        mDoubCRSQty = (BigDecimal) decimalFormat.parse(bean.getORDQty());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (mDoubCRSQty != null) {
                        mDoubCRSQty = mDoubCRSQty.add(mapCRSSKUQTY.get(bean.getMaterialNo()));
                    } else {
                        mDoubCRSQty = mapCRSSKUQTY.get(bean.getMaterialNo());
                    }
                    double mDouNetPrice = Double.parseDouble(bean.getNetAmount()) + mapNetAmt.get(bean.getMaterialNo());
                    double mDouPriSchPer = Double.parseDouble(bean.getPRMScheme()) + mapPriSchemePer.get(bean.getMaterialNo());
                    double mDouSecSchPer = Double.parseDouble(bean.getSecScheme()) + mapSecSchemePer.get(bean.getMaterialNo());
                    double mDouSecSchAmt = Double.parseDouble(bean.getSecSchemeAmt()) + mapSecSchemeAmt.get(bean.getMaterialNo());

                    double mDouRatioSchDisAmt = 0.0;
                    try {
                        mDouRatioSchDisAmt = Double.parseDouble(bean.getRatioSchDisAmt()) + mapRatioSchDis.get(bean.getMaterialNo());
                    } catch (NumberFormatException e) {
                        mDouRatioSchDisAmt = 0.0;
                    }

                    double mDouFreeSchDisAmt = 0.0;
                    try {
                        mDouFreeSchDisAmt = Double.parseDouble(bean.getFreeMatDisAmt().equalsIgnoreCase("")?"0":bean.getFreeMatDisAmt()) + mapRatioSchDis.get(bean.getMaterialNo());
                    } catch (NumberFormatException e) {
                        mDouFreeSchDisAmt = 0.0;
                    }


                    int matCountInc = mapCntMatByCRSKUGRP.get(bean.getMaterialNo());
                    mapCntMatByCRSKUGRP.put(bean.getMaterialNo(), matCountInc + 1);

                    mapSecSchemeAmt.put(bean.getMaterialNo(), mDouSecSchAmt);
                    mapSecSchemePer.put(bean.getMaterialNo(), mDouSecSchPer);
                    mapPriSchemePer.put(bean.getMaterialNo(), mDouPriSchPer);
                    mapNetAmt.put(bean.getMaterialNo(), mDouNetPrice);
                    mapRatioSchDis.put(bean.getMaterialNo(), mDouRatioSchDisAmt);
                    mapFreeDisAmt.put(bean.getMaterialNo(), mDouFreeSchDisAmt);
                    mapCRSSKUQTY.put(bean.getMaterialNo(), mDoubCRSQty);
                    mapUOM.put(bean.getMaterialNo(), bean.getUOM());
                    mapSKUList.put(bean.getMaterialNo(), bean);


                    mDobTotalOrderVal = mDobTotalOrderVal + Double.parseDouble(bean.getNetAmount());
                } else {
                    double mDoubNetAmt = Double.parseDouble(bean.getNetAmount());
                    double mDouPriSchPer = Double.parseDouble(bean.getPRMScheme());
                    double mDouSecSchPer = Double.parseDouble(bean.getSecScheme());
                    double mDouSecSchAmt = Double.parseDouble(bean.getSecSchemeAmt());
                    double mDouRatioSchDisAmt = 0.0;
                    try {
                        mDouRatioSchDisAmt= Double.parseDouble(bean.getRatioSchDisAmt());
                    } catch (NumberFormatException e) {
                        mDouRatioSchDisAmt = 0.0;
                    }
                    double mDouFreeSchDisAmt = 0.0;
                    try {
                        mDouFreeSchDisAmt= Double.parseDouble(bean.getFreeMatDisAmt());
                    } catch (NumberFormatException e) {
                        mDouFreeSchDisAmt = 0.0;
                    }
                    BigDecimal mDoubOrderQty = null;
                    try {
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        decimalFormat.setParseBigDecimal(true);
                        mDoubOrderQty = (BigDecimal) decimalFormat.parse(bean.getORDQty());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mapSecSchemeAmt.put(bean.getMaterialNo(), mDouSecSchAmt);
                    mapCntMatByCRSKUGRP.put(bean.getMaterialNo(), 1);
                    mapNetAmt.put(bean.getMaterialNo(), mDoubNetAmt);
                    mapCRSSKUQTY.put(bean.getMaterialNo(), mDoubOrderQty);
                    mapPriSchemePer.put(bean.getMaterialNo(), mDouPriSchPer);
                    mapSecSchemePer.put(bean.getMaterialNo(), mDouSecSchPer);

                    mapRatioSchDis.put(bean.getMaterialNo(), mDouRatioSchDisAmt);
                    mapFreeDisAmt.put(bean.getMaterialNo(), mDouFreeSchDisAmt);

                    mapUOM.put(bean.getMaterialNo(), bean.getUOM());
                    mapSKUList.put(bean.getMaterialNo(), bean);

                    mDobTotalOrderVal = mDobTotalOrderVal + Double.parseDouble(bean.getNetAmount());
                }
        }

        return mapSKUList;
    }

    private void setMaterialLevelScheme(ArrayList<SchemeBean> schPerCalBeanList) {
        if (schPerCalBeanList!=null) {
            for (SchemeBean schemeBean : schPerCalBeanList) {
                schemeBean.setMaterialLevel(true);
            }
        }
    }
    private double getSecondaryDiscountOrAmtOrFreeQty(boolean mBoolMatWise, SchemeBean schPerCalBean, String mStrSlabRuleId,
                                                      String mOrderQty, String mMatNo, String mOrderMatGrp, String mCPItemGUID, String isHeaderBased) {
        double mDoubSecDisOrAmtOrQty = 0.0;
        if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeProduct)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }

            if (mBoolMatWise) {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getMaterialDesc(), schPerCalBean.getMaterialNo(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            } else {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getMaterialDesc(), schPerCalBean.getMaterialNo(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeSKUGroup)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
            if (mBoolMatWise) {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getSKUGroupDesc(), schPerCalBean.getSKUGroupID(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            } else {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getSKUGroupDesc(), schPerCalBean.getSKUGroupID(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeCRSSKUGroup)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
            if (mBoolMatWise) {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getOrderMaterialGroupDesc(), schPerCalBean.getOrderMaterialGroupID(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            } else {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getOrderMaterialGroupDesc(), schPerCalBean.getOrderMaterialGroupID(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutPerc(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutPerc(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutAmount(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutAmount(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeScratchCard)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
            if (mBoolMatWise) {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getCardTitle(), schPerCalBean.getCardTitle(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            } else {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getCardTitle(), schPerCalBean.getCardTitle(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeFreeArticle)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }

            if (mBoolMatWise) {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getFreeArticle(), schPerCalBean.getFreeArticle(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            } else {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getFreeArticle(), schPerCalBean.getFreeArticle(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            }
        }

        return mDoubSecDisOrAmtOrQty;
    }
    private boolean getBasketSchemePer(SchemeBean schemeBean, MaterialBatchBean materialBatchBean, SKUGroupBean skuGroupBean) {
        boolean mBoolSecDis = false;

        if (schemeBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfBanner)) { //Banner
            if (!mapBannerWiseQty.isEmpty()) {
                int mIntBannerReminder = (int) (mapBannerWiseTempQty.get(skuGroupBean.getBanner()) % mapBasketBannerMinQty.get(skuGroupBean.getBanner()));
                if (mapBannerWiseQty.containsKey(skuGroupBean.getBanner())) {
                    Double mDouTotalSelBannerQty = mapBannerWiseQty.get(skuGroupBean.getBanner());
                    if (mIntBannerReminder > 0) {
                        if (mDouTotalSelBannerQty > 0) {
                            Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                            Double mDouRemaingQty = mDouTotalSelBannerQty - mDouMatBatQty;
                            // testing 04052017
                            mapBannerWiseQty.put(skuGroupBean.getBanner(), mDouRemaingQty);
                            // ending

                            if (mDouRemaingQty == 0) {
                                mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntBannerReminder) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            } else if (mDouRemaingQty < 0) {
                                mDouCalNetAmt = (mDouTotalSelBannerQty) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            }
                        } else {
                            mDouCalNetAmt = 0;
                            mBoolSecDis = true;
                            return mBoolSecDis;
                        }
                    }

                }
            }
        } else if (schemeBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfBrand)) {  // Brand
            if (!mapBrandWiseQty.isEmpty()) {
                int mIntBrandReminder = (int) (mapBrandWiseTempQty.get(skuGroupBean.getBrand()) % mapBasketBrandMinQty.get(skuGroupBean.getBrand()));
                if (mapBrandWiseQty.containsKey(skuGroupBean.getBrand())) {
                    Double mDouTotalSelBrandQty = mapBrandWiseQty.get(skuGroupBean.getBrand());
                    if (mIntBrandReminder > 0) {
                        if (mDouTotalSelBrandQty > 0) {
                            Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                            Double mDouRemaingQty = mDouTotalSelBrandQty - mDouMatBatQty;
                            // testing 04052017
                            mapBrandWiseQty.put(skuGroupBean.getBrand(), mDouRemaingQty);
                            // ending

                            if (mDouRemaingQty == 0) {
                                mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntBrandReminder) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            } else if (mDouRemaingQty < 0) {
                                mDouCalNetAmt = (mDouTotalSelBrandQty) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            }
                        } else {
                            mDouCalNetAmt = 0;
                            mBoolSecDis = true;
                            return mBoolSecDis;
                        }
                    }

                }
            }
        } else if (schemeBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfProdCat)) {  // ProductCat
        } else if (schemeBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfSchemeMatGrp)) {  // SKUGroup
            if (!mapSKUGrpWiseQty.isEmpty()) {
                int mIntSKUGrpReminder = (int) (mapSKUGrpWiseTempQty.get(skuGroupBean.getSKUGroupID()) % mapBasketSKUGRPMinQty.get(skuGroupBean.getSKUGroupID()));
                if (mapSKUGrpWiseQty.containsKey(skuGroupBean.getSKUGroupID())) {
                    Double mDouTotalSelSKUGRPQty = mapSKUGrpWiseQty.get(skuGroupBean.getSKUGroupID());
                    if (mIntSKUGrpReminder > 0) {

                        if (mDouTotalSelSKUGRPQty > 0) {
                            Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                            Double mDouRemaingQty = mDouTotalSelSKUGRPQty - mDouMatBatQty;
                            // testing 04052017
                            mapSKUGrpWiseQty.put(skuGroupBean.getSKUGroupID(), mDouRemaingQty);
                            // ending

                            if (mDouRemaingQty == 0) {
                                mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntSKUGrpReminder) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            } else if (mDouRemaingQty < 0) {
                                mDouCalNetAmt = (mDouTotalSelSKUGRPQty) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            }
                        } else {
                            mDouCalNetAmt = 0;
                            mBoolSecDis = true;
                            return mBoolSecDis;
                        }
                    }

                }
            }
        } else if (schemeBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfOrderMatGrp)) {  // OrderMaterialGroup
            if (!mapCRSSKUGrpWiseQty.isEmpty()) {
                int mIntCRSSKUGrpReminder = (int) (mapCRSSKUGrpWiseTempQty.get(skuGroupBean.getSKUGroup()) % mapBasketCRSSKUGRPMinQty.get(skuGroupBean.getSKUGroup()));
                if (mapCRSSKUGrpWiseQty.containsKey(skuGroupBean.getSKUGroup())) {
                    Double mDouTotalSelCRSSKUGRPQty = mapCRSSKUGrpWiseQty.get(skuGroupBean.getSKUGroup());
                    if (mIntCRSSKUGrpReminder > 0) {

                        if (mDouTotalSelCRSSKUGRPQty > 0) {
                            Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                            Double mDouRemaingQty = mDouTotalSelCRSSKUGRPQty - mDouMatBatQty;
                            // testing 04052017
                            mapCRSSKUGrpWiseQty.put(skuGroupBean.getSKUGroup(), mDouRemaingQty);
                            // ending

                            if (mDouRemaingQty == 0) {
                                mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntCRSSKUGrpReminder) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            } else if (mDouRemaingQty < 0) {
                                mDouCalNetAmt = (mDouTotalSelCRSSKUGRPQty) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            }
                        } else {
                            mDouCalNetAmt = 0;
                            mBoolSecDis = true;
                            return mBoolSecDis;
                        }
                    }

                }
            }
        } else if (schemeBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfMat)) {  //Material
            if (!mapMaterialWiseQty.isEmpty()) {
                int mIntMaterialReminder = (int) (mapMaterialWiseTempQty.get(skuGroupBean.getMaterialNo()) % mapBasketMaterialMinQty.get(skuGroupBean.getMaterialNo()));
                if (mapMaterialWiseQty.containsKey(skuGroupBean.getMaterialNo())) {
                    Double mDouTotalSelMatQty = mapMaterialWiseQty.get(skuGroupBean.getMaterialNo());
                    if (mIntMaterialReminder > 0) {

                        if (mDouTotalSelMatQty > 0) {
                            Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                            Double mDouRemaingQty = mDouTotalSelMatQty - mDouMatBatQty;
                            // testing 04052017
                            mapMaterialWiseQty.put(skuGroupBean.getMaterialNo(), mDouRemaingQty);
                            // ending

                            if (mDouRemaingQty == 0) {
                                mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntMaterialReminder) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            } else if (mDouRemaingQty < 0) {
                                mDouCalNetAmt = (mDouTotalSelMatQty) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            }
                        } else {
                            mDouCalNetAmt = 0;
                            mBoolSecDis = true;
                            return mBoolSecDis;
                        }

                    }

                }
            }
        }
        return mBoolSecDis;
    }
    // Adjust scheme free qty material price to  net amount
    private void adjustFreeQtyDiscountVal(ArrayList<SKUGroupBean> alSKUList) {
        if (alSKUList != null && alSKUList.size() > 0) {
            for (SKUGroupBean soBean : alSKUList) {
                Double mDouFreeMaterialDiscount = 0.0;
                Double mDouFreeMatTotalDisAmt = 0.0;
                Double mDouSumNetTaxSecAmt = 0.0;

                for(SchemeCalcuBean schemeCalcuBean :soBean.getSchemeCalcuBeanArrayList()) {
                    if (schemeCalcuBean.isSchemeFreeQty()) {
                        SchemeBean mFreeMatPriceBean = schemeCalcuBean.getmFreeMat();
                        SchemeBean mGetMatBatchInfo = soBean.getMaterialBatchBean();

                        if (mGetMatBatchInfo != null) {
                            ArrayList<MaterialBatchBean> alMatBatchItemBean = mGetMatBatchInfo.getMaterialBatchBeanArrayList();
                            for (MaterialBatchBean matBatchItemBean : alMatBatchItemBean) {

                                double mBatchQtyPrice = Double.parseDouble(matBatchItemBean.getNetAmount());

                                double mDouTotalPrice = 0;
                                try {
                                    mDouTotalPrice = mapNetPriceByScheme.get(soBean.getSKUGroup()+"_"+schemeCalcuBean.getSchemeGuidNo());
                                } catch (Exception e) {
                                    mDouTotalPrice = 0.0;
                                }


                                Double mDouMatPrice = 0.0;
                                try {
                                    mDouMatPrice = Double.parseDouble(mFreeMatPriceBean.getFreeMatPrice());
                                } catch (Exception e) {
                                    mDouMatPrice = 0.0;
                                }

                                try {
                                    mDouFreeMaterialDiscount = mDouMatPrice * mBatchQtyPrice / mDouTotalPrice;
                                } catch (NumberFormatException e) {
                                    mDouFreeMaterialDiscount = 0.0;
                                }

                                if (mDouFreeMaterialDiscount.isNaN() || mDouFreeMaterialDiscount.isInfinite()) {
                                    mDouFreeMaterialDiscount = 0.0;
                                }
                                mDouFreeMatTotalDisAmt = mDouFreeMatTotalDisAmt + mDouFreeMaterialDiscount;
                                Double mDouCalNet = 0.0;
                                try {
                                    mDouCalNet = Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - mDouFreeMaterialDiscount;
                                } catch (NumberFormatException e) {
                                    mDouCalNet = 0.0;
                                }

                                String mStrTaxAmt = Constants.getTaxAmount(mDouCalNet + "", "0", matBatchItemBean.getoDataEntity(), matBatchItemBean.getQty());
                                matBatchItemBean.setTax(mStrTaxAmt);

                                mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(matBatchItemBean.getGrossAmt()) - mDouFreeMaterialDiscount + Double.parseDouble(matBatchItemBean.getTax()));
                                matBatchItemBean.setTotalNetAmt((Double.parseDouble(matBatchItemBean.getGrossAmt()) - mDouFreeMaterialDiscount + Double.parseDouble(matBatchItemBean.getTax())) + "");
                                matBatchItemBean.setGrossAmt((Double.parseDouble(matBatchItemBean.getGrossAmt()) - mDouFreeMaterialDiscount) + "");

                            }
                        }
                        soBean.setFreeMatDisAmt(mDouFreeMatTotalDisAmt + "");
                        soBean.setNetAmount(mDouSumNetTaxSecAmt + "");
                    }
                }
            }
        }

    }
}
