package com.arteriatech.emami.scheme;

import android.text.TextUtils;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.common.UtilOfflineManager;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.SKUGroupBean;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.windowdisplay.ValidationQueryLogic;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by e10769 on 24-03-2017.
 */

public class GetSalesOrderListBasedOnScheme {

    public static ArrayList<SchemeIDBean> getOrderMaterialId(String cpGUID, String parentId, String parentTypeId,
                                                             String cpTypeId, String mSPGUID,
                                                             String cpDmsDivQry, String cpDmsDivSchSalAreaQry, ArrayList<SKUGroupBean> alCPStkList) throws OfflineODataStoreException {

        ArrayList<CPDMSDivisionBean> cpDMSDivisionList = new ArrayList<>();
        if (!cpGUID.equalsIgnoreCase("")) {
            cpDMSDivisionList = OfflineManager.getCPDMSDivisionList(Constants.CPDMSDivisions + "?$filter=" + Constants.PartnerMgrGUID + " eq guid'" + mSPGUID.toUpperCase() + "' and " + Constants.CPGUID + " eq guid'" + Constants.convertStrGUID32to36(cpGUID) + "' and " + cpDmsDivQry + " ");
        } else {
            cpDMSDivisionList = OfflineManager.getCPDMSDivisionList(Constants.CPDMSDivisions + "?$filter=" + cpDmsDivQry + "");//+"?$select=DMSDivision,Group1,Group2,Group3,Group4"
        }


        ArrayList<SchemeIDBean> skuGroupBeanArrayList = new ArrayList<>();
        if (OfflineManager.offlineStore != null) {
            SchemeIDBean schemeIDBean = null;
            ODataProperty property;
            ODataPropMap properties;
            HashSet<String> hashSet = new HashSet<>();
            hashSet.add("000001");
            hashSet.add("000002");
            List<ODataEntity> entities = getValidScheme(hashSet);
            if (entities != null && entities.size() > 0) {
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    schemeIDBean = new SchemeIDBean();
                    property = properties.get(Constants.SchemeGUID);
                    ODataGuid mSchemeGuid = null;
                    String schemeGuid = "";
                    try {
                        mSchemeGuid = (ODataGuid) property.getValue();
                        schemeGuid = mSchemeGuid.guidAsString36().toUpperCase();
                        schemeIDBean.setSchemeGuid(schemeGuid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    property = properties.get(Constants.SchemeCatID);
                    String schemeCatID = "";
                    schemeCatID = (String) property.getValue();
                    schemeIDBean.setSchemeCatID(schemeCatID);
                    String schemeTypeId = "";
                    property = properties.get(Constants.SchemeTypeID);
                    if (property != null) {
                        schemeTypeId = (String) property.getValue();
                    }
                    boolean statusForArea = false;
                    Constants.HashMapSchemeIsInstantOrQPS.put(schemeGuid, schemeCatID);


//                    Log.d("Time start checkThreeCondition", UtilConstants.getSyncHistoryddmmyyyyTime());
                    statusForArea = checkThreeCondition(schemeGuid, cpGUID, parentId, parentTypeId, cpTypeId, mSPGUID, cpDmsDivSchSalAreaQry, cpDMSDivisionList);
//                    Log.d("Time end checkThreeCondition", UtilConstants.getSyncHistoryddmmyyyyTime());
                    if (statusForArea) {

                        // Make Scheme QRY
                        if (Constants.SchemeQRY.length() == 0)
                            Constants.SchemeQRY += "guid'" + schemeGuid + "'";
                        else
                            Constants.SchemeQRY += " or " + Constants.SchemeGUID + " eq guid'" + schemeGuid + "'";

                        boolean isBasketScheme = false;
                        if (!TextUtils.isEmpty(schemeTypeId) && schemeTypeId.equals(Constants.SchemeTypeIDBasketScheme)) {
                            isBasketScheme = true;
                        }
//                        Log.d("Time start getAllValidStock", UtilConstants.getSyncHistoryddmmyyyyTime());
                        schemeIDBean.setOrderMaterialId(ValidationQueryLogic.getAllValidStock(schemeGuid, isBasketScheme, true, alCPStkList));
//                        Log.d("Time end getAllValidStock", UtilConstants.getSyncHistoryddmmyyyyTime());
                    }
                    skuGroupBeanArrayList.add(schemeIDBean);
                }
            }
        }

        return skuGroupBeanArrayList;
    }

    public static boolean checkConditionOne(String query, String type, String cpGUID, String mSPGUID, ArrayList<CPDMSDivisionBean> cpDMSDivisionList) throws OfflineODataStoreException {
        boolean statusForArea = false;
        if (OfflineManager.offlineStore != null) {
            ODataProperty property;
            ODataPropMap properties;
            String[][] strArray = null;

            List<ODataEntity> entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, query);
            strArray = new String[6][entities.size()];
            if (entities != null && entities.size() > 0) {
                int posCount = 0;
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    property = properties.get(Constants.SalesAreaID);
                    strArray[0][posCount] = (String) property.getValue();

                    property = properties.get(Constants.DMSDivisionID);
                    strArray[1][posCount] = (String) property.getValue();
                    property = properties.get(Constants.CPGroup1ID);
                    strArray[2][posCount] = (String) property.getValue();
                    property = properties.get(Constants.CPGroup2ID);
                    strArray[3][posCount] = (String) property.getValue();
                    property = properties.get(Constants.CPGroup3ID);
                    strArray[4][posCount] = (String) property.getValue();
                    property = properties.get(Constants.CPGroup4ID);
                    strArray[5][posCount] = (String) property.getValue();
                    String queryCondition = "";
                    if (!TextUtils.isEmpty(strArray[1][posCount])) {
                        queryCondition = queryCondition + " and DMSDivision eq '" + strArray[1][posCount] + "'";
                    }
                    if (!TextUtils.isEmpty(strArray[2][posCount])) {
                        queryCondition = queryCondition + " and Group1 eq '" + strArray[2][posCount] + "'";
                    }
                    if (!TextUtils.isEmpty(strArray[3][posCount])) {
                        queryCondition = queryCondition + " and Group2 eq '" + strArray[3][posCount] + "'";
                    }
                    if (!TextUtils.isEmpty(strArray[4][posCount])) {
                        queryCondition = queryCondition + " and Group3 eq '" + strArray[4][posCount] + "'";
                    }
                    if (!TextUtils.isEmpty(strArray[5][posCount])) {
                        queryCondition = queryCondition + " and Group4 eq '" + strArray[5][posCount] + "'";
                    }
//                    if(cpDMSDivisionList==null){
                    if (!cpGUID.equalsIgnoreCase("")) {
                        statusForArea = OfflineManager.checkVisitActivitiesForRetailer(Constants.CPDMSDivisions + "?$top=1 &$select=" + Constants.CPNo + " &$filter=" + Constants.PartnerMgrGUID + " eq guid'" + mSPGUID.toUpperCase() + "' and "
                                + Constants.CPGUID + " eq guid'" + Constants.convertStrGUID32to36(cpGUID) + "'" + queryCondition);
                    } else {
//                        statusForArea = OfflineManager.checkVisitActivitiesForRetailer(Constants.CPDMSDivisions + "?$filter="+Constants.PartnerMgrGUID+" eq guid'"+mSPGUID.toUpperCase()+"' " + queryCondition);
                        if (!queryCondition.equalsIgnoreCase("")) {
                            statusForArea = OfflineManager.checkVisitActivitiesForRetailer(Constants.CPDMSDivisions + "?$top=1 &$select=" + Constants.CPNo + " &$filter=" + queryCondition.substring(4));
                        }


                    }
//                    }else{
//                        statusForArea = checkDivisionStatus(strArray[1][posCount],strArray[2][posCount],strArray[3][posCount],strArray[4][posCount],strArray[5][posCount],cpDMSDivisionList);
//                    }


                    if (statusForArea) {
                        return statusForArea;
                    }

                    posCount++;
                }
            }
        }

        return statusForArea;
    }

    private static boolean checkDivisionStatus(String divisionId, String g1, String g2, String g3, String g4, ArrayList<CPDMSDivisionBean> cpDMSDivisionList) {
        for (CPDMSDivisionBean cpdmsDivisionBean : cpDMSDivisionList) {
            boolean isSuccess = false;

            if (TextUtils.isEmpty(divisionId) || TextUtils.isEmpty(g1) || TextUtils.isEmpty(g2) || TextUtils.isEmpty(g3) || TextUtils.isEmpty(g4)) {
                isSuccess = true;
            } else {
                if (!TextUtils.isEmpty(divisionId)) {
                    if (!cpdmsDivisionBean.getdMSDivision().equals(divisionId)) {
                        continue;
                    } else {
                        isSuccess = true;
                    }
                }
                if (!TextUtils.isEmpty(g1)) {
                    if (!cpdmsDivisionBean.getGroup1().equals(g1)) {
                        continue;
                    } else {
                        isSuccess = true;
                    }
                }
                if (!TextUtils.isEmpty(g2)) {
                    if (!cpdmsDivisionBean.getGroup2().equals(g2)) {
                        continue;
                    } else {
                        isSuccess = true;
                    }
                }
                if (!TextUtils.isEmpty(g3)) {
                    if (!cpdmsDivisionBean.getGroup3().equals(g3)) {
                        continue;
                    } else {
                        isSuccess = true;
                    }
                }
                if (!TextUtils.isEmpty(g4)) {
                    if (!cpdmsDivisionBean.getGroup4().equals(g4)) {
                        continue;
                    } else {
                        isSuccess = true;
                    }
                }
            }


            if (isSuccess) {
                return true;
            }

        }


        return false;
    }

    public static boolean checkGeoGraph(String query, String cpGUID) throws OfflineODataStoreException {
        if (OfflineManager.offlineStore != null) {
            ODataProperty property;
            ODataPropMap properties;
            String[][] strArray = null;
            boolean statusForArea = false;
            List<ODataEntity> entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, query);
            strArray = new String[3][entities.size()];
            if (entities != null && entities.size() > 0) {
                int posCount = 0;
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    property = properties.get(Constants.GeographyScopeID);
                    strArray[0][posCount] = (String) property.getValue();
                    property = properties.get(Constants.GeographyLevelID);
                    strArray[1][posCount] = (String) property.getValue();
                    property = properties.get(Constants.GeographyTypeID);
                    strArray[2][posCount] = (String) property.getValue();
                    String geographyLevelID = OfflineManager.getGuidValueByColumnName(Constants.CPGeoClassifications + "?$filter = " + Constants.GeographyScopeID + " eq '" + strArray[0][posCount] + "' and " + Constants.GeographyLevelID + " eq " +
                            "'" + strArray[1][posCount] + "' and " + Constants.GeographyTypeID + " eq '" + strArray[2][posCount] + "'", Constants.GeographyMapping);
                    if (!TextUtils.isEmpty(geographyLevelID)) {

                        statusForArea = OfflineManager.checkVisitActivitiesForRetailer(Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + Constants.convertStrGUID32to36(cpGUID) + "' and " + geographyLevelID + " ne ''");
                        if (statusForArea) {
                            return true;
                        }
                    }
                    posCount++;
                }
            }
        }

        return false;
    }

    private static boolean checkSchemeCps(String query, String cpGUID) throws OfflineODataStoreException {
        if (OfflineManager.offlineStore != null) {
            ODataProperty property;
            ODataPropMap properties;
            String[][] strArray = null;
            boolean statusForArea = false;
            List<ODataEntity> entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, query);
            strArray = new String[3][entities.size()];
            if (entities != null && entities.size() > 0) {
                int posCount = 0;
                for (ODataEntity entity : entities) {
                    properties = entity.getProperties();
                    property = properties.get(Constants.GeographyScopeID);
                    strArray[0][posCount] = (String) property.getValue();
                    property = properties.get(Constants.GeographyLevelID);
                    strArray[1][posCount] = (String) property.getValue();
                    property = properties.get(Constants.GeographyTypeID);
                    strArray[2][posCount] = (String) property.getValue();
                    String geographyLevelID = OfflineManager.getGuidValueByColumnName(Constants.CPGeoClassifications + "?$filter = " + Constants.GeographyScopeID + " eq '" + strArray[0][posCount] + "' and " + Constants.GeographyLevelID + " eq " +
                            "'" + strArray[1][posCount] + "' and " + Constants.GeographyTypeID + " eq '" + strArray[2][posCount] + "'", Constants.GeographyMapping);
                    if (!TextUtils.isEmpty(geographyLevelID)) {

                        statusForArea = OfflineManager.checkVisitActivitiesForRetailer(Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + cpGUID + "' and " + geographyLevelID + " ne ''");
                        if (statusForArea) {
                            return true;
                        }
                    }
                    posCount++;
                }
            }
        }

        return false;
    }

    public static HashSet<String> getOrderMaterialGrpId(String schemeGuid) {
        HashSet<String> orderMaterialGrpId = new HashSet<>();
        HashSet<String> brandOrderMaterialGrpId = new HashSet<>();
        HashSet<String> bannerOrderMaterialGrpId = new HashSet<>();
        try {
            String[][] schemeItemDetailsList = ValidationQueryLogic.getSchemeItemDetails(Constants.SchemeItemDetails + "?$select = OrderMaterialGroupID,BrandID,BannerID &$filter = SchemeGUID eq guid'" + schemeGuid + "'");
            if (schemeItemDetailsList != null) {
                int schemeCount = 0;
                String orderMaterialGRPId = "";
                String[][] orderMaterialGRPArrId = null;
                for (String[] schemeItemDetails : schemeItemDetailsList) {
                    if (schemeCount == 0) {
                        if (schemeItemDetails.length > 0) {
                            for (String orderMaterialId : schemeItemDetails)
                                orderMaterialGrpId.add(orderMaterialId);
                        }
                        if (!orderMaterialGrpId.isEmpty())
                            break;
                    } else if (schemeCount == 1) {
                        if (schemeItemDetails.length > 0) {
                            for (String brandId : schemeItemDetails) {
                                orderMaterialGRPArrId = null;
                                orderMaterialGRPArrId = ValidationQueryLogic.getOrderMaterialGroupId(Constants.CPStockItems + "?$select = OrderMaterialGroupID &$filter = Brand eq '" + brandId + "' and OrderMaterialGroupID ne '' &$top=1");
                                if (orderMaterialGRPArrId != null) {
                                    for (String bannorderMaterialGRPId : orderMaterialGRPArrId[0]) {
                                        if (!bannorderMaterialGRPId.isEmpty()) {
                                            brandOrderMaterialGrpId.add(orderMaterialGRPId);
                                        }
                                    }
                                }
                            }
                        }
                        if (!brandOrderMaterialGrpId.isEmpty())
                            break;

                    } else if (schemeCount == 2) {
                        ValidationQueryLogic.getBannerOrderMaterialGroup(bannerOrderMaterialGrpId, schemeItemDetails, "and OrderMaterialGroupID ne '' &$top=1");
                        if (!bannerOrderMaterialGrpId.isEmpty())
                            break;

                    }
                    schemeCount++;
                }
            }
            if (!orderMaterialGrpId.isEmpty()) {

                return orderMaterialGrpId;
            } else if (!brandOrderMaterialGrpId.isEmpty()) {
                return brandOrderMaterialGrpId;
            } else if (!bannerOrderMaterialGrpId.isEmpty()) {
                return bannerOrderMaterialGrpId;
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        return orderMaterialGrpId;
    }
    /*check condition for window display and icon display*/
   /* public static boolean checkThreeCondition(String schemeGuid, String mStrCPGUID32, String parentId,
                                              String parentTypeId, String cpTypeId,String mSPGUID,String cpDmsDivQry,ArrayList<CPDMSDivisionBean> cpDMSDivisionList) {
        boolean overAllConditionStatus = false;
        String schemeType = "";
        try {
            if (!TextUtils.isEmpty(schemeGuid)) {
//                Log.d("Time Start checkConditionOne", UtilConstants.getSyncHistoryddmmyyyyTime());
                overAllConditionStatus = GetSalesOrderListBasedOnScheme.checkConditionOne(Constants.SchemeSalesAreas + "?$filter=" + Constants.SchemeGUID + " eq guid'" + schemeGuid + "' and "+cpDmsDivQry+" ", schemeType, mStrCPGUID32,mSPGUID,cpDMSDivisionList);
//                Log.d("Time End checkConditionOne", UtilConstants.getSyncHistoryddmmyyyyTime());
                if (overAllConditionStatus) {
                    overAllConditionStatus = OfflineManager.checkVisitActivitiesForRetailer(Constants.SchemeCPs + "?$top=1 &$filter=" + Constants.SchemeGUID + " eq guid'" + schemeGuid + "'");
                    if(overAllConditionStatus) {
                        overAllConditionStatus = OfflineManager.checkVisitActivitiesForRetailer(Constants.SchemeCPs + "?$top=1 &$filter=" + Constants.SchemeGUID + " eq guid'" + schemeGuid + "' and CPTypeID eq '" + parentTypeId + "' and CPGUID eq '" + parentId + "'");
                        if (overAllConditionStatus) {
                            overAllConditionStatus = OfflineManager.checkVisitActivitiesForRetailer(Constants.SchemeCPs + "?$top=1 &$filter=" + Constants.SchemeGUID + " eq guid'" + schemeGuid + "' and CPTypeID eq '" + parentTypeId + "' and CPGUID eq '" + parentId +
                                    "' and IsExcluded ne 'X'");
                            if (!overAllConditionStatus) {
                                overAllConditionStatus = OfflineManager.checkVisitActivitiesForRetailer(Constants.SchemeCPs + "?$top=1 &$filter=" + Constants.SchemeGUID + " eq guid'" + schemeGuid + "' and CPTypeID eq '" + cpTypeId + "' and CPGUID eq '" + mStrCPGUID32 +
                                        "' and IsExcluded ne 'X'");
                            }
                            return overAllConditionStatus;

                        } else {
//                            overAllConditionStatus = OfflineManager.checkVisitActivitiesForRetailer(Constants.SchemeCPs + "?$filter=" + Constants.SchemeGUID + " eq guid'" + schemeGuid + "' and CPTypeID eq '" + cpTypeId + "' and CPGUID eq '" + mStrCPGUID32 +
//                                    "' and IsExcluded ne 'X'");
                            return true;
                        }
                    }else {
                        return true;
                    }
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return overAllConditionStatus;
    }*/

    public static boolean checkThreeCondition(String schemeGuid, String mStrCPGUID32, String parentId,
                                              String parentTypeId, String cpTypeId, String mSPGUID, String cpDmsDivQry, ArrayList<CPDMSDivisionBean> cpDMSDivisionList) {
        boolean overAllConditionStatus = false;
        String schemeType = "";
        try {
            if (!TextUtils.isEmpty(schemeGuid)) {
//                Log.d("Time Start checkConditionOne", UtilConstants.getSyncHistoryddmmyyyyTime());
                overAllConditionStatus = GetSalesOrderListBasedOnScheme.checkConditionOne(Constants.SchemeSalesAreas + "?$filter=" + Constants.SchemeGUID + " eq guid'" + schemeGuid + "' and " + cpDmsDivQry + " ", schemeType, mStrCPGUID32, mSPGUID, cpDMSDivisionList);
//                Log.d("Time End checkConditionOne", UtilConstants.getSyncHistoryddmmyyyyTime());
                if (overAllConditionStatus) {
                    overAllConditionStatus = OfflineManager.checkVisitActivitiesForRetailer(Constants.SchemeCPs + "?$top=1 &$filter=" + Constants.SchemeGUID + " eq guid'" + schemeGuid + "'");
                    if (overAllConditionStatus) {
                        overAllConditionStatus = OfflineManager.checkVisitActivitiesForRetailer(Constants.SchemeCPs + "?$top=1 &$filter=" + Constants.SchemeGUID + " eq guid'" + schemeGuid + "' and " +
                                "CPTypeID eq '" + parentTypeId + "' and CPGUID eq '" + Constants.appendPrecedingZeros(parentId, 10) + "'");
                        if (overAllConditionStatus) {
                            overAllConditionStatus = OfflineManager.checkVisitActivitiesForRetailer(Constants.SchemeCPs + "?$top=1 &$filter=" + Constants.SchemeGUID + " eq guid'" + schemeGuid + "' and " +
                                    "CPTypeID eq '" + parentTypeId + "' and CPGUID eq '" + Constants.appendPrecedingZeros(parentId, 10) +
                                    "' and IsExcluded ne 'X'");
                            if (!overAllConditionStatus) {
                                overAllConditionStatus = OfflineManager.checkVisitActivitiesForRetailer(Constants.SchemeCPs + "?$top=1 &$filter=" + Constants.SchemeGUID + " eq guid'" + schemeGuid + "'" +
                                        " and CPTypeID eq '" + cpTypeId + "' and CPGUID eq '" + mStrCPGUID32 +
                                        "' and IsExcluded ne 'X'");
                            }
                            return overAllConditionStatus;

                        } else {
//                            overAllConditionStatus = OfflineManager.checkVisitActivitiesForRetailer(Constants.SchemeCPs + "?$filter=" + Constants.SchemeGUID + " eq guid'" + schemeGuid + "' and CPTypeID eq '" + cpTypeId + "' and CPGUID eq '" + mStrCPGUID32 +
//                                    "' and IsExcluded ne 'X'");
                            return false;
                        }
                    } else {
                        return true;
                    }
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return overAllConditionStatus;
    }

    public static List<ODataEntity> getValidScheme(HashSet<String> schemeCatID) {
        List<ODataEntity> entities = null;
        if (OfflineManager.offlineStore != null) {
            String schemeQry = "";
            schemeQry = Constants.Schemes + "?$filter= " + Constants.StatusID +
                    " eq '01' and ValidTo ge datetime'" + UtilConstants.getNewDate() + "' and ApprovalStatusID eq '03'";
            if (schemeCatID != null) {
                if (!schemeCatID.isEmpty()) {
                    String mStrSchemeCatId = "";
                    int totalSize = schemeCatID.size();
                    int i = 0;
                    for (String schemeId : schemeCatID) {
                        if (i == 0 && i == totalSize - 1) {
                            mStrSchemeCatId = mStrSchemeCatId
                                    + "(" + Constants.SchemeCatID + " eq '"
                                    + schemeId + "')";

                        } else if (i == 0) {
                            mStrSchemeCatId = mStrSchemeCatId
                                    + "(" + Constants.SchemeCatID + " eq '"
                                    + schemeId + "'";

                        } else if (i == totalSize - 1) {
                            mStrSchemeCatId = mStrSchemeCatId
                                    + " or " + Constants.SchemeCatID + " eq '"
                                    + schemeId + "')";
                        } else {
                            mStrSchemeCatId = mStrSchemeCatId
                                    + " or " + Constants.SchemeCatID + " eq '"
                                    + schemeId + "'";
                        }
                        i++;
                    }

                    schemeQry = schemeQry + " and " + mStrSchemeCatId;
                }
            }
            try {
                entities = UtilOfflineManager.getEntities(OfflineManager.offlineStore, schemeQry);
                if (entities != null && entities.size() > 0) {
                    return entities;
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

        }
        return entities;
    }
}
