package com.arteriatech.emami.windowdisplay;

import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.common.UtilOfflineManager;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.mbo.SKUGroupBean;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

/**
 * Created by e10769 on 21-03-2017.
 */

public class ValidationQueryLogic {

    public static String[][] getSchemeItemDetails(String query) throws OfflineODataStoreException {
        String[][] arrList = null;
        if (OfflineManager.offlineStore != null) {
            ODataProperty property;
            ODataPropMap properties;
            List<ODataEntity> entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, query);
            if (entities != null && entities.size() > 0) {
                arrList = new String[3][entities.size()];
                int incVal = 0;
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    property = properties.get(Constants.OrderMaterialGroupID);
                    arrList[0][incVal] = (String) property.getValue();
                    property = properties.get(Constants.BrandID);
                    arrList[1][incVal] = (String) property.getValue();
                    property = properties.get(Constants.BannerID);
                    arrList[2][incVal] = (String) property.getValue();
                    incVal++;
                }
            }

        }
        return arrList;
    }
    public static HashSet<String> getSchemeItemDetailsWithSKU(String query,boolean comingFromSOList,ArrayList<SKUGroupBean> alCPStkList) throws OfflineODataStoreException {
        HashSet<String> orderMaterialGrpId = new HashSet<>();
        if (OfflineManager.offlineStore != null) {
            ODataProperty property;
            ODataPropMap properties;
            List<ODataEntity> entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, query);
            if (entities != null && entities.size() > 0) {
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    property = properties.get(Constants.OnSaleOfCatID);
                    String onSaleOfCatId = (String) property.getValue();
                    String catDesc = "";
                    String conditions="";
                    if(onSaleOfCatId.equalsIgnoreCase(Constants.OnSaleOfBanner)){  // Banner
                        property = properties.get(Constants.BannerID);
                        String banner = (String) property.getValue();
                        if(!TextUtils.isEmpty(banner)) {
                            conditions = conditions + " and Banner eq '" + banner + "'";
                            catDesc = banner;
                        }
                    }else if(onSaleOfCatId.equalsIgnoreCase(Constants.OnSaleOfBrand)){  // Brand
                        property = properties.get(Constants.BrandID);
                        String brand = (String) property.getValue();
                        if(!TextUtils.isEmpty(brand)) {
                            conditions = conditions + " and Brand eq '" + brand + "'";
                            catDesc = brand;
                        }
                    }else if(onSaleOfCatId.equalsIgnoreCase(Constants.OnSaleOfProdCat)){   // Product Category
                        property = properties.get(ConstantsUtils.ProductCatID);
                        String prdtCatId = (String) property.getValue();
                        if(!TextUtils.isEmpty(prdtCatId)) {
                            conditions = conditions + " and ProductCategoryID eq '" + prdtCatId + "'";
                            catDesc = prdtCatId;
                        }
                    }else if(onSaleOfCatId.equalsIgnoreCase(Constants.OnSaleOfSchemeMatGrp)){  // Scheme Mat Grp
                        property = properties.get(Constants.SKUGroupID);
                        String skuGrp = (String) property.getValue();
                        if(!TextUtils.isEmpty(skuGrp)) {
                            conditions = conditions + " and SKUGroup eq '" + skuGrp + "'";
                            catDesc = skuGrp;
                        }
                    }else if(onSaleOfCatId.equalsIgnoreCase(Constants.OnSaleOfOrderMatGrp)){   // Order Mat Grp
                        property = properties.get(Constants.OrderMaterialGroupID);
                        String orderMatGrpId = (String) property.getValue();
                        if(!TextUtils.isEmpty(orderMatGrpId)) {
                            conditions = conditions + " and OrderMaterialGroupID eq '" + orderMatGrpId + "'";
                            catDesc = orderMatGrpId;
                        }

                    }else if(onSaleOfCatId.equalsIgnoreCase(Constants.OnSaleOfMat)){   // Order SKU
                        property = properties.get(Constants.MaterialNo);
                        String matNo = (String) property.getValue();
                        if(!TextUtils.isEmpty(matNo)) {
                            conditions = conditions + " and MaterialNo eq '" + matNo + "'";
                            catDesc = matNo;
                        }
                    }
//                    if(comingFromSOList) {
//                        getOrderMaterialGrpFromCPSTKList(onSaleOfCatId,catDesc,orderMaterialGrpId,alCPStkList);
//                    }else{
                        getOrderMaterialgroup(Constants.CPStockItems + "?$select = OrderMaterialGroupID &$filter = OrderMaterialGroupID ne '' " + conditions, orderMaterialGrpId);
//                    }

                }
            }

        }
        return orderMaterialGrpId;
    }

    public static String[][] getOrderMaterialGroupId(String query) throws OfflineODataStoreException {
        String[][] arrList = null;
        if (OfflineManager.offlineStore != null) {
            ODataProperty property;
            ODataPropMap properties;
            List<ODataEntity> entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, query);
            if (entities != null && entities.size() > 0) {
                arrList = new String[1][entities.size() + 1];
                int incVal = 0;
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    property = properties.get(Constants.OrderMaterialGroupID);
                    arrList[0][incVal] = (String) property.getValue();

                    incVal++;
                }
            }

        }
        return arrList;
    }

    private static String[][] getInvoiceGuId(String query,boolean needDate) throws OfflineODataStoreException {
        String[][] arrList = null;
        if (OfflineManager.offlineStore != null) {
            ODataProperty property;
            ODataPropMap properties;
            List<ODataEntity> entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, query);
            if (entities != null && entities.size() > 0) {
                arrList = new String[2][entities.size()];
                int incVal = 0;
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    property = properties.get(Constants.InvoiceGUID);
                    try {
                        ODataGuid mInvoiceGUID = (ODataGuid) property.getValue();
                        arrList[0][incVal] = mInvoiceGUID.guidAsString32().toUpperCase();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(needDate) {
                        property = properties.get(Constants.InvoiceDate);
                        try {
                            String convertDateFormat = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) property.getValue());
                            arrList[1][incVal] = convertDateFormat;
                        } catch (Exception e) {
                            e.printStackTrace();
                            arrList[1][incVal] = "";
                        }
                    }else {
                        arrList[1][incVal] = "";
                    }
                    incVal++;
                }
            }

        }
        return arrList;
    }

    public static String validateStocks(String schemeGuid, int prevDays, String mStrCPGUID) {
        try {
            HashSet<String> orderMaterialGrpId = getAllValidStock(schemeGuid,false,false,null);
            if(orderMaterialGrpId!=null) {
                if (!orderMaterialGrpId.isEmpty()) {
                    return checkInvoice(prevDays, convertArrayToString(orderMaterialGrpId), mStrCPGUID);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static HashSet<String> getAllValidStock(String schemeGuid, boolean isCheckBasket, boolean comingFromSOList,ArrayList<SKUGroupBean> alCPStkList) {
        HashSet<String> orderMaterialGrpId=null;
        try {
            if (!comingFromSOList){
                isCheckBasket = Constants.isSchemeBasketOrNot(schemeGuid);
            }
            // Todo check scheme is Basket scheme or not
            if (!isCheckBasket) {
//            if (!Constants.isSchemeBasketOrNot(schemeGuid)) {
                orderMaterialGrpId = getSchemeItemDetailsWithSKU(Constants.SchemeItemDetails + "?$filter = SchemeGUID eq guid'" + schemeGuid + "'  "
                        ,comingFromSOList,alCPStkList);
            }else{
                orderMaterialGrpId = getSchemeItemDetailsWithSKU(Constants.SchemeItemDetails + "?$filter = SchemeGUID eq guid'" + schemeGuid + "'  " +
                        "and ("+ConstantsUtils.HierarchicalRefGUID+" ne guid'00000000-0000-0000-0000-000000000000' or "+ConstantsUtils.HierarchicalRefGUID+" eq null) ",comingFromSOList,alCPStkList);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        return orderMaterialGrpId;
    }

    public static void getBannerOrderMaterialGroup(HashSet<String> bannerOrderMaterialGrpId, String[] schemeItemDetails, String queryAttach) throws OfflineODataStoreException {
        String[][] orderMaterialGRPArrId = null;
        for (String banner : schemeItemDetails) {
            if (!TextUtils.isEmpty(banner)) {
                orderMaterialGRPArrId = null;
                orderMaterialGRPArrId = getOrderMaterialGroupId(Constants.CPStockItems + "?$select = OrderMaterialGroupID &$filter = Banner eq '" + banner + "'" + queryAttach);
                if (orderMaterialGRPArrId != null) {
                    for (String orderMaterialGRPId : orderMaterialGRPArrId[0]) {
                        if (orderMaterialGRPId!=null && !orderMaterialGRPId.isEmpty()) {
                            bannerOrderMaterialGrpId.add(orderMaterialGRPId);
                        }
                    }
                }
            }
        }

    }
    private static void getOrderMaterialgroup(String query,HashSet<String> bannerOrderMaterialGrpId) throws OfflineODataStoreException {
//        String[][] arrList = null;
        if (OfflineManager.offlineStore != null) {
            ODataProperty property;
            ODataPropMap properties;
            List<ODataEntity> entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, query);
            if (entities != null && entities.size() > 0) {
//                arrList = new String[1][entities.size() + 1];
                int incVal = 0;
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    property = properties.get(Constants.OrderMaterialGroupID);
                    String ordMatGrp = (String) property.getValue();
                    bannerOrderMaterialGrpId.add(ordMatGrp);

                    incVal++;
                }
            }

        }
    }
    private static void getOrderMaterialGrpFromCPSTKList(String OnSaleOfCatID,String  onSaleCatDesc,HashSet<String> bannerOrderMaterialGrpId, ArrayList<SKUGroupBean> alCPStock)  {
        if (OfflineManager.offlineStore != null) {
            if (alCPStock != null && alCPStock.size() > 0) {
                for (SKUGroupBean skuGroupBean : alCPStock) {
                    if(!bannerOrderMaterialGrpId.contains(skuGroupBean.getOrderMaterialGroupID())){
                        switch (OnSaleOfCatID){
                            case "000001":  // banner
                                if(skuGroupBean.getBanner().equalsIgnoreCase(onSaleCatDesc)){
                                    bannerOrderMaterialGrpId.add(skuGroupBean.getOrderMaterialGroupID());
                                }
                                break;
                            case "000002":  // brand
                                if(skuGroupBean.getBrand().equalsIgnoreCase(onSaleCatDesc)){
                                    bannerOrderMaterialGrpId.add(skuGroupBean.getOrderMaterialGroupID());
                                }
                                break;
                            case "000003":  // ProductCategoryID
                                if(skuGroupBean.getProductCategoryID().equalsIgnoreCase(onSaleCatDesc)){
                                    bannerOrderMaterialGrpId.add(skuGroupBean.getOrderMaterialGroupID());
                                }
                                break;
                            case "000004":  // SKUGrp ID
                                if(skuGroupBean.getSKUGroupID().equalsIgnoreCase(onSaleCatDesc)){
                                    bannerOrderMaterialGrpId.add(skuGroupBean.getOrderMaterialGroupID());
                                }
                                break;
                            case "000005":  // OrderMaterialGroupID
                                if(skuGroupBean.getOrderMaterialGroupID().equalsIgnoreCase(onSaleCatDesc)){
                                    bannerOrderMaterialGrpId.add(skuGroupBean.getOrderMaterialGroupID());
                                }
                                break;
                            case "000006":  // Material
                                if(skuGroupBean.getMaterialNo().equalsIgnoreCase(onSaleCatDesc)){
                                    bannerOrderMaterialGrpId.add(skuGroupBean.getOrderMaterialGroupID());
                                }
                                break;
                        }
                    }

                }
            }

        }
    }
    public static void getSKUOrderMaterialGroup(HashSet<String> bannerOrderMaterialGrpId, String[] schemeItemDetails, String queryAttach) throws OfflineODataStoreException {
        String[][] orderMaterialGRPArrId = null;
        for (String skuGroup : schemeItemDetails) {
            if (!TextUtils.isEmpty(skuGroup)) {
                orderMaterialGRPArrId = null;
                orderMaterialGRPArrId = getOrderMaterialGroupId(Constants.CPStockItems + "?$select = OrderMaterialGroupID &$filter = SKUGroup eq '" + skuGroup + "'" + queryAttach);
                if (orderMaterialGRPArrId != null) {
                    for (String orderMaterialGRPId : orderMaterialGRPArrId[0]) {
                        if (orderMaterialGRPId!=null && !orderMaterialGRPId.isEmpty()) {
                            bannerOrderMaterialGrpId.add(orderMaterialGRPId);
                        }
                    }
                }
            }
        }

    }

    private static String checkInvoice(int days, String orderMaterialId, String mStrCPGUID) {
        String querys = Constants.SSInvoices + "?$select = InvoiceGUID, InvoiceDate &$filter=" + Constants.InvoiceDate + " ge datetime'" + Constants.getNoOfDaysBefore(days) + "' and SoldToCPGUID eq guid'"+mStrCPGUID+"' &$orderby = InvoiceDate asc";
        try {//
            String[][] inVoiceGuid = getInvoiceGuId(querys, true);
            if (inVoiceGuid != null) {
                String mStrInvoiceItemDetailsQry = "";
                for (int i = 0; i < inVoiceGuid[0].length; i++) {
                    if (i == 0 && i == inVoiceGuid[0].length - 1) {
                        mStrInvoiceItemDetailsQry = mStrInvoiceItemDetailsQry
                                + "(" + Constants.InvoiceGUID + " eq guid'"
                                + Constants.convertStrGUID32to36(inVoiceGuid[0][i]).toUpperCase() + "')";

                    } else if (i == 0) {
                        mStrInvoiceItemDetailsQry = mStrInvoiceItemDetailsQry
                                + "(" + Constants.InvoiceGUID + " eq guid'"
                                + Constants.convertStrGUID32to36(inVoiceGuid[0][i]).toUpperCase() + "'";

                    } else if (i == inVoiceGuid[0].length - 1) {
                        mStrInvoiceItemDetailsQry = mStrInvoiceItemDetailsQry
                                + " or " + Constants.InvoiceGUID + " eq guid'"
                                + Constants.convertStrGUID32to36(inVoiceGuid[0][i]).toUpperCase() + "')";
                    } else {
                        mStrInvoiceItemDetailsQry = mStrInvoiceItemDetailsQry
                                + " or " + Constants.InvoiceGUID + " eq guid'"
                                + Constants.convertStrGUID32to36(inVoiceGuid[0][i]).toUpperCase() + "'";
                    }
                }
                if (!mStrInvoiceItemDetailsQry.isEmpty()) {
                    querys = Constants.SSInvoiceItemDetails + "?$select = InvoiceGUID &$filter=" + mStrInvoiceItemDetailsQry + " and " + orderMaterialId + "";
                    String[][] inVoiceItemDetailsGuid = getInvoiceGuId(querys,false);
                    if (inVoiceItemDetailsGuid != null) {
                        if (inVoiceItemDetailsGuid[0].length > 0) {
                            return inVoiceGuid[1][0];
                        }
                    }
                }

            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String convertArrayToString(HashSet<String> orderMaterialGrpId) {
        String query = "";
        int i = 0;
        int totalSize = orderMaterialGrpId.size();
        for (String orderMatId : orderMaterialGrpId) {
            if (i == 0 && i == totalSize - 1) {
                query = query + "(" + Constants.OrderMaterialGroup + " eq '" + orderMatId + "')";
            } else if (i == 0) {
                query = query + "(" + Constants.OrderMaterialGroup + " eq '" + orderMatId + "'";

            } else if (i == totalSize - 1) {
                query = query + " or " + Constants.OrderMaterialGroup + " eq '" + orderMatId + "')";
            } else {
                query = query + " or " + Constants.OrderMaterialGroup + " eq '" + orderMatId + "'";
            }
            i++;
        }

        return query;
    }


    public static String getValueBySchemeGuid(String query) throws OfflineODataStoreException {
        String conditions="";
        if (OfflineManager.offlineStore != null) {
            ODataProperty property;
            ODataPropMap properties;
            List<ODataEntity> entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, query);
            if (entities != null && entities.size() > 0) {
                int incVal = 0;
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();

                    property = properties.get(Constants.OnSaleOfCatID);
                    String onSaleOfCatId = (String) property.getValue();

                    if(onSaleOfCatId.equalsIgnoreCase("000001")){
                        property = properties.get(Constants.BannerID);
                        String banner = (String) property.getValue();
                        if(!TextUtils.isEmpty(banner))
                            conditions= conditions+" and "+Constants.BannerID+" eq '" + banner + "'";
                    }else if(onSaleOfCatId.equalsIgnoreCase("000002")){
                        property = properties.get(Constants.BrandID);
                        String brand = (String) property.getValue();
                        if(!TextUtils.isEmpty(brand))
                            conditions= conditions+" and "+Constants.BrandID+" eq '" + brand + "'";
                    }else if(onSaleOfCatId.equalsIgnoreCase("000003")){
                        property = properties.get(ConstantsUtils.ProductCatID);
                        String prdtCatId = (String) property.getValue();
                        if(!TextUtils.isEmpty(prdtCatId))
                            conditions= conditions+" and "+ConstantsUtils.ProductCatID+" eq '" + prdtCatId + "'";
                    }else if(onSaleOfCatId.equalsIgnoreCase("000004")){
                        property = properties.get(Constants.SKUGroupID);
                        String skuGrp = (String) property.getValue();
                        if(!TextUtils.isEmpty(skuGrp))
                            conditions= conditions+" and "+Constants.SKUGroupID+" eq '" + skuGrp + "'";
                    }else if(onSaleOfCatId.equalsIgnoreCase("000005")){
                        property = properties.get(Constants.OrderMaterialGroupID);
                        String orderMatGrpId = (String) property.getValue();
                        if(!TextUtils.isEmpty(orderMatGrpId))
                            conditions= conditions+" and "+Constants.OrderMaterialGroupID+" eq '" + orderMatGrpId + "'";

                    }else if(onSaleOfCatId.equalsIgnoreCase("000006")){
                        property = properties.get(Constants.MaterialNo);
                        String matNo = (String) property.getValue();
                        if(!TextUtils.isEmpty(matNo))
                            conditions= conditions+" and "+Constants.MaterialNo+" eq '" + matNo + "'";
                    }
                    incVal++;
                }
            }

        }
        return conditions;
    }

}
