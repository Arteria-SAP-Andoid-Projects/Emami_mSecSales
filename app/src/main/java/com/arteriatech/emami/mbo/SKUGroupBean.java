package com.arteriatech.emami.mbo;

import com.arteriatech.emami.socreate.SchemeCalcuBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 12/22/2016.
 */

public class SKUGroupBean {
    String LastInvoiceNo = "";
    String LastMaterialNo = "";
    String SKUGroup = "";
    String MRP = "";
    String DBSTK = "";
    String RETSTK = "";
    String SOQ = "";
    String ORDQty = "";
    String NetAmount = "";
    String PRMScheme = "";
    String SecScheme = "0";
    String Brand = "";
    String Category = "";
    String MatTypeVal = "";
    String MatTypeDesc = "";
    String Currency = "";
    String MaterialDesc = "";
    String MaterialNo = "";
    String MonthThree = "";
    String MonthOne = "";
    String MonthTwo = "";
    String AvgThreeMnts = "";
    String AvgPerThreeMnts = "";

    public String getSSSOItemGuid() {
        return SSSOItemGuid;
    }

    public void setSSSOItemGuid(String SSSOItemGuid) {
        this.SSSOItemGuid = SSSOItemGuid;
    }

    String SSSOItemGuid = "";

    public Double getUnResQty() {
        return UnResQty;
    }

    public void setUnResQty(Double unResQty) {
        UnResQty = unResQty;
    }

    Double UnResQty = 0.0;


    private ArrayList<String> SchemeGuid = new ArrayList<>();
    public String ISFreeTypeID = "";
    private String etQty = "";
    private boolean isFocusHeaderText = false;
    private boolean isHeader = false;
    private boolean isViewOpened = false;
    private boolean isItemTyping = false;
    private int setCursorPos = -1;
    private boolean matLevelImageDisplay = false;
    private ArrayList<SchemeCalcuBean> schemeCalcuBeanArrayList = new ArrayList<>();

    public String getRatioSchMatPrice() {
        return RatioSchMatPrice;
    }

    public void setRatioSchMatPrice(String ratioSchMatPrice) {
        RatioSchMatPrice = ratioSchMatPrice;
    }

    public String RatioSchMatPrice = "";

    public String getFreeMatDisAmt() {
        return FreeMatDisAmt;
    }

    public void setFreeMatDisAmt(String freeMatDisAmt) {
        FreeMatDisAmt = freeMatDisAmt;
    }

    public String FreeMatDisAmt = "";


    public String getISFreeTypeID() {
        return ISFreeTypeID;
    }


    public void setISFreeTypeID(String ISFreeTypeID) {
        this.ISFreeTypeID = ISFreeTypeID;
    }

    public String getRatioSchDisAmt() {
        return RatioSchDisAmt;
    }

    public void setRatioSchDisAmt(String ratioSchDisAmt) {
        RatioSchDisAmt = ratioSchDisAmt;
    }

    public String RatioSchDisAmt = "";
    private ArrayList<SKUGroupBean> skuSubGroupBeanArrayList = new ArrayList<>();

    public String getSchemeQPSActive() {
        return SchemeQPSActive;
    }

    public void setSchemeQPSActive(String schemeQPSActive) {
        SchemeQPSActive = schemeQPSActive;
    }

    String SchemeQPSActive = "";
    private String OrgScopeID = "";
    private String isMaterialActive = "";
    private ArrayList<SKUGroupItemBean> skuGroupItemBean = null;

    public ArrayList<String> getSchemeGuid() {
        return SchemeGuid;
    }

    public void setSchemeGuid(ArrayList<String> schemeGuid) {
        SchemeGuid = schemeGuid;
    }

    public String SlabTypeID = "";
    public String SlabTypeDesc = "";
    public String IsIncludingPrimary = "";
    public String SlabRuleID = "";
    public String SlabRuleDesc = "";
    public String TaxAmount = "";


    public String getTaxAmount() {
        return TaxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        TaxAmount = taxAmount;
    }


    public String getQPSSchemeGuid() {
        return QPSSchemeGuid;
    }

    public void setQPSSchemeGuid(String QPSSchemeGuid) {
        this.QPSSchemeGuid = QPSSchemeGuid;
    }

    private String QPSSchemeGuid = "";

    public String getSlabRuleDesc() {
        return SlabRuleDesc;
    }

    public void setSlabRuleDesc(String slabRuleDesc) {
        SlabRuleDesc = slabRuleDesc;
    }

    public String getSlabRuleID() {
        return SlabRuleID;
    }

    public void setSlabRuleID(String slabRuleID) {
        SlabRuleID = slabRuleID;
    }

    public String getIsIncludingPrimary() {
        return IsIncludingPrimary;
    }

    public void setIsIncludingPrimary(String isIncludingPrimary) {
        IsIncludingPrimary = isIncludingPrimary;
    }

    public String getSlabTypeID() {
        return SlabTypeID;
    }

    public void setSlabTypeID(String slabTypeID) {
        SlabTypeID = slabTypeID;
    }

    public String getSlabTypeDesc() {
        return SlabTypeDesc;
    }

    public void setSlabTypeDesc(String slabTypeDesc) {
        SlabTypeDesc = slabTypeDesc;
    }

    /*
   following Strings for Scheme Sales Area
    */
    String SalesOrgID = "";
    String DistributionChannelID = "";
    String DMSDivisionID = "";
    String DivisionID = "";
    String CPGroup1ID = "";
    String CPGroup2ID = "";
    String CPGroup3ID = "";
    String CPGroup4ID = "";

    /*
    following String for SchemeGeographies
     */
    String GeographyScopeID = "";
    String GeographyLevelID = "";
    String GeographyTypeID = "";
    String GeographyValueID = "";

    /*
    following Strings for SchemeCPs
     */

    String CPTypeID = "";
    String CPGUID = "";
    String CPNo = "";

    public String getCPTypeID() {
        return CPTypeID;
    }

    public void setCPTypeID(String CPTypeID) {
        this.CPTypeID = CPTypeID;
    }

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }


    public String getGeographyScopeID() {
        return GeographyScopeID;
    }

    public void setGeographyScopeID(String geographyScopeID) {
        GeographyScopeID = geographyScopeID;
    }

    public String getGeographyLevelID() {
        return GeographyLevelID;
    }

    public void setGeographyLevelID(String geographyLevelID) {
        GeographyLevelID = geographyLevelID;
    }

    public String getGeographyTypeID() {
        return GeographyTypeID;
    }

    public void setGeographyTypeID(String geographyTypeID) {
        GeographyTypeID = geographyTypeID;
    }

    public String getGeographyValueID() {
        return GeographyValueID;
    }

    public void setGeographyValueID(String geographyValueID) {
        GeographyValueID = geographyValueID;
    }


    public String getCPGroup1ID() {
        return CPGroup1ID;
    }

    public void setCPGroup1ID(String CPGroup1ID) {
        this.CPGroup1ID = CPGroup1ID;
    }

    public String getCPGroup2ID() {
        return CPGroup2ID;
    }

    public void setCPGroup2ID(String CPGroup2ID) {
        this.CPGroup2ID = CPGroup2ID;
    }

    public String getCPGroup3ID() {
        return CPGroup3ID;
    }

    public void setCPGroup3ID(String CPGroup3ID) {
        this.CPGroup3ID = CPGroup3ID;
    }

    public String getCPGroup4ID() {
        return CPGroup4ID;
    }

    public void setCPGroup4ID(String CPGroup4ID) {
        this.CPGroup4ID = CPGroup4ID;
    }


    public String getDivisionID() {
        return DivisionID;
    }

    public void setDivisionID(String divisionID) {
        DivisionID = divisionID;
    }


    public String getDMSDivisionID() {
        return DMSDivisionID;
    }

    public void setDMSDivisionID(String DMSDivisionID) {
        this.DMSDivisionID = DMSDivisionID;
    }

    public String getSalesOrgID() {
        return SalesOrgID;
    }

    public void setSalesOrgID(String salesOrgID) {
        SalesOrgID = salesOrgID;
    }

    public String getDistributionChannelID() {
        return DistributionChannelID;
    }

    public void setDistributionChannelID(String distributionChannelID) {
        DistributionChannelID = distributionChannelID;
    }


    public String getIsSchemeActive() {
        return IsSchemeActive;
    }

    public void setIsSchemeActive(String isSchemeActive) {
        IsSchemeActive = isSchemeActive;
    }

    String IsSchemeActive = "";


    public String getSSSOGuid() {
        return SSSOGuid;
    }

    public void setSSSOGuid(String SSSOGuid) {
        this.SSSOGuid = SSSOGuid;
    }

    String SSSOGuid = "";

    public String getAvgPerThreeMnts() {
        return AvgPerThreeMnts;
    }

    public void setAvgPerThreeMnts(String avgPerThreeMnts) {
        AvgPerThreeMnts = avgPerThreeMnts;
    }


    public String getAvgThreeMnts() {
        return AvgThreeMnts;
    }

    public void setAvgThreeMnts(String avgThreeMnts) {
        AvgThreeMnts = avgThreeMnts;
    }

    public String getMonthThree() {
        return MonthThree;
    }

    public void setMonthThree(String monthThree) {
        MonthThree = monthThree;
    }

    public String getMonthTwo() {
        return MonthTwo;
    }

    public void setMonthTwo(String monthTwo) {
        MonthTwo = monthTwo;
    }

    public String getMonthOne() {
        return MonthOne;
    }

    public void setMonthOne(String monthOne) {
        MonthOne = monthOne;
    }


    public String getCPStockItemGUID() {
        return CPStockItemGUID;
    }

    public void setCPStockItemGUID(String CPStockItemGUID) {
        this.CPStockItemGUID = CPStockItemGUID;
    }

    String CPStockItemGUID = "";

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }


    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    String LastPurchasedMaterial = "";

    public String getChildItemTag() {
        return ChildItemTag;
    }

    public void setChildItemTag(String childItemTag) {
        ChildItemTag = childItemTag;
    }

    String ChildItemTag = "";

    public String getLastPurchasedMaterial() {
        return LastPurchasedMaterial;
    }

    public void setLastPurchasedMaterial(String lastPurchasedMaterial) {
        LastPurchasedMaterial = lastPurchasedMaterial;
    }


    public String getLastMaterialNo() {
        return LastMaterialNo;
    }

    public void setLastMaterialNo(String lastMaterialNo) {
        LastMaterialNo = lastMaterialNo;
    }

    public String getLastInvoiceNo() {
        return LastInvoiceNo;
    }

    public void setLastInvoiceNo(String lastInvoiceNo) {
        LastInvoiceNo = lastInvoiceNo;
    }


    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    String UOM = "";

    public String getUnBilledStatus() {
        return UnBilledStatus;
    }

    public void setUnBilledStatus(String unBilledStatus) {
        UnBilledStatus = unBilledStatus;
    }

    String UnBilledStatus = "";

    public boolean isQtyEntered() {
        return IsQtyEntered;
    }

    public void setQtyEntered(boolean qtyEntered) {
        IsQtyEntered = qtyEntered;
    }

    boolean IsQtyEntered = false;

    public String getMatTypeVal() {
        return MatTypeVal;
    }

    public void setMatTypeVal(String matTypeVal) {
        MatTypeVal = matTypeVal;
    }

    public String getMatTypeDesc() {
        return MatTypeDesc;
    }

    public void setMatTypeDesc(String matTypeDesc) {
        MatTypeDesc = matTypeDesc;
    }


    public String getSKUGroupDesc() {
        return SKUGroupDesc;
    }

    public void setSKUGroupDesc(String SKUGroupDesc) {
        this.SKUGroupDesc = SKUGroupDesc;
    }

    String SKUGroupDesc = "";

    public boolean isMustSell() {
        return MustSell;
    }

    public void setMustSell(boolean mustSell) {
        MustSell = mustSell;
    }

    public String getSKUGroup() {
        return SKUGroup;
    }

    public void setSKUGroup(String SKUGroup) {
        this.SKUGroup = SKUGroup;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getDBSTK() {
        return DBSTK;
    }

    public void setDBSTK(String DBSTK) {
        this.DBSTK = DBSTK;
    }

    public String getRETSTK() {
        return RETSTK;
    }

    public void setRETSTK(String RETSTK) {
        this.RETSTK = RETSTK;
    }

    public String getSOQ() {
        return SOQ;
    }

    public void setSOQ(String SOQ) {
        this.SOQ = SOQ;
    }

    public String getORDQty() {
        return ORDQty;
    }

    public void setORDQty(String ORDQty) {
        this.ORDQty = ORDQty;
    }

    public String getNetAmount() {
        return NetAmount;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    public String getPRMScheme() {
        return PRMScheme;
    }

    public void setPRMScheme(String PRMScheme) {
        this.PRMScheme = PRMScheme;
    }

    public String getSecScheme() {
        return SecScheme;
    }

    public void setSecScheme(String secScheme) {
        SecScheme = secScheme;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    boolean MustSell = false;

    public String getOrgScopeID() {
        return OrgScopeID;
    }

    public void setOrgScopeID(String orgScopeID) {
        OrgScopeID = orgScopeID;
    }

    public String getIsMaterialActive() {
        return isMaterialActive;
    }

    public void setIsMaterialActive(String isMaterialActive) {
        this.isMaterialActive = isMaterialActive;
    }

    public ArrayList<SKUGroupItemBean> getSkuGroupItemBean() {
        return skuGroupItemBean;
    }

    public void setSkuGroupItemBean(ArrayList<SKUGroupItemBean> skuGroupItemBean) {
        this.skuGroupItemBean = skuGroupItemBean;
    }

    private String NoOfCards = "";
    private String CardTitle = "";
    private String FreeArticle = "";
    private String FreeQty = "";
    private String OrderMaterialGroupID = "";
    private String OrderMaterialGroupDesc = "";

    public String getOrderMaterialGroupID() {
        return OrderMaterialGroupID;
    }

    public void setOrderMaterialGroupID(String orderMaterialGroupID) {
        OrderMaterialGroupID = orderMaterialGroupID;
    }

    public String getOrderMaterialGroupDesc() {
        return OrderMaterialGroupDesc;
    }

    public void setOrderMaterialGroupDesc(String orderMaterialGroupDesc) {
        OrderMaterialGroupDesc = orderMaterialGroupDesc;
    }

    public String getCardTitle() {
        return CardTitle;
    }

    public void setCardTitle(String cardTitle) {
        CardTitle = cardTitle;
    }

    public String getFreeArticle() {
        return FreeArticle;
    }

    public void setFreeArticle(String freeArticle) {
        FreeArticle = freeArticle;
    }

    public String getFreeQty() {
        return FreeQty;
    }

    public void setFreeQty(String freeQty) {
        FreeQty = freeQty;
    }

    public String getNoOfCards() {
        return NoOfCards;
    }

    public void setNoOfCards(String noOfCards) {
        NoOfCards = noOfCards;
    }

    public SchemeBean getMaterialBatchBean() {
        return MaterialBatchBean;
    }

    public void setMaterialBatchBean(SchemeBean MaterialBatchBean) {
        this.MaterialBatchBean = MaterialBatchBean;
    }

    private SchemeBean MaterialBatchBean = null;

    private String Banner = "";
    private String SKUGroupID = "";
    private String ProductCategoryID = "";
    private String SKU = "";

    public String getBanner() {
        return Banner;
    }

    public void setBanner(String banner) {
        Banner = banner;
    }

    public String getSKUGroupID() {
        return SKUGroupID;
    }

    public void setSKUGroupID(String SKUGroupID) {
        this.SKUGroupID = SKUGroupID;
    }

    public String getProductCategoryID() {
        return ProductCategoryID;
    }

    public void setProductCategoryID(String productCategoryID) {
        ProductCategoryID = productCategoryID;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getISFreeMat() {
        return ISFreeMat;
    }

    public void setISFreeMat(String ISFreeMat) {
        this.ISFreeMat = ISFreeMat;
    }

    private String ISFreeMat = "";

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String batch) {
        Batch = batch;
    }

    public String getInvCashDisc() {
        return InvCashDisc;
    }

    public void setInvCashDisc(String invCashDisc) {
        InvCashDisc = invCashDisc;
    }

    private String InvCashDisc = "";
    private String Batch = "";
    public String SchemeSlabRule = "";

    public String getSchemeSlabRule() {
        return SchemeSlabRule;
    }

    public void setSchemeSlabRule(String schemeSlabRule) {
        SchemeSlabRule = schemeSlabRule;
    }

    private String SecSchemeAmt = "0";

    public String getSecSchemeAmt() {
        return SecSchemeAmt;
    }

    public void setSecSchemeAmt(String secSchemeAmt) {
        SecSchemeAmt = secSchemeAmt;
    }

    public String getEtQty() {
        return etQty;
    }

    public void setEtQty(String etQty) {
        this.etQty = etQty;
    }

    public ArrayList<SKUGroupBean> getSkuSubGroupBeanArrayList() {
        return skuSubGroupBeanArrayList;
    }

    public void setSkuSubGroupBeanArrayList(ArrayList<SKUGroupBean> skuSubGroupBeanArrayList) {
        this.skuSubGroupBeanArrayList = skuSubGroupBeanArrayList;
    }

    public boolean isFocusHeaderText() {
        return isFocusHeaderText;
    }

    public void setFocusHeaderText(boolean focusHeaderText) {
        isFocusHeaderText = focusHeaderText;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public boolean isViewOpened() {
        return isViewOpened;
    }

    public void setViewOpened(boolean viewOpened) {
        isViewOpened = viewOpened;
    }

    public boolean isItemTyping() {
        return isItemTyping;
    }

    public void setItemTyping(boolean itemTyping) {
        isItemTyping = itemTyping;
    }

    public int getSetCursorPos() {
        return setCursorPos;
    }

    public void setSetCursorPos(int setCursorPos) {
        this.setCursorPos = setCursorPos;
    }

    public boolean isMatLevelImageDisplay() {
        return matLevelImageDisplay;
    }

    public void setMatLevelImageDisplay(boolean matLevelImageDisplay) {
        this.matLevelImageDisplay = matLevelImageDisplay;
    }


    public ArrayList<SchemeCalcuBean> getSchemeCalcuBeanArrayList() {
        return schemeCalcuBeanArrayList;
    }

    public void setSchemeCalcuBeanArrayList(ArrayList<SchemeCalcuBean> schemeCalcuBeanArrayList) {
        this.schemeCalcuBeanArrayList = schemeCalcuBeanArrayList;
    }


}
