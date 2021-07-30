package com.arteriatech.emami.mbo;

import java.util.ArrayList;

/**
 * Created by e10526 on 3/13/2017.
 */

public class SchemeBean {
    private String SchemeGUID = "";
    private String MaterialNo = "";
    private String MaterialDesc = "";
    private String PrimaryScheme = "";
    private String PrimaryPer = "";
    private String SecondaryPer = "";
    private String ConsumerScheme = "";
    private String ItemNo = "";
    private String SchemeDesc = "";
    private String Flag = "";
    private String SchemeGuid = "";
    private String SchemeTypeID = "";
    private String SchemeTypeDesc = "";
    private String SchemeID = "";
    private String SchemeGroupID = "";
    private String SKUGroupDesc = "";
    private String SKUGroupID = "";
    private String IsHeaderBasedSlab = "";
    public String OrgScopeID = "";
    public String OrgScopeDesc = "";
    private String CPTypeID = "";
    private String IsExclude = "";
    private boolean isSecondTime = false;
    public String SlabTypeID = "";
    public String SlabTypeDesc = "";
    public String Batch = "";
    public String IsIncludingPrimary = "";
    public String FreeMAt = "";
    public String ISFreeTypeID = "";
    public String RatioSchNum = "";
    public String RatioSchDen = "";
    public String FreeMatCritria = "";
    public String FreeMatCritriaTxt = "";
    private boolean isMaterialLevel = false;
    private boolean isRatioSchemeApplicable = false;
    private boolean mBoolBatchQtyNotEmpty = false;
    private boolean mRatioSchemeAval = false;
    private boolean mRatioSchemeAvalTemp = false;
    private String mStrFreeMaterialNoTemp = "";
    private double doubleRatioSchNumTemp = 0.0;
    private double mDobRatioSchDenTemp = 0.0;
    private String matNetAmtAftPriDis = "0.0";
    private String matSecPer = "0.0";
    private String matNetAmount = "0.0";

    public String getTargetAmount() {
        return TargetAmount;
    }

    public void setTargetAmount(String targetAmount) {
        TargetAmount = targetAmount;
    }

    private String TargetAmount = "0.0";

    public String getCBBQty() {
        return CBBQty;
    }

    public void setCBBQty(String CBBQty) {
        this.CBBQty = CBBQty;
    }

    private String CBBQty = "0";

    public String getSaleUnitID() {
        return SaleUnitID;
    }

    public void setSaleUnitID(String saleUnitID) {
        SaleUnitID = saleUnitID;
    }

    private String SaleUnitID = "";
    private boolean isRatioScheme = false;

    public boolean isRatioSchemeApplied() {
        return isRatioSchemeApplied;
    }

    public void setRatioSchemeApplied(boolean ratioSchemeApplied) {
        isRatioSchemeApplied = ratioSchemeApplied;
    }

    private boolean isRatioSchemeApplied = false;


    public String getFreeMatCritria() {
        return FreeMatCritria;
    }

    public void setFreeMatCritria(String freeMatCritria) {
        FreeMatCritria = freeMatCritria;
    }

    public String getFreeMatCritriaTxt() {
        return FreeMatCritriaTxt;
    }

    public void setFreeMatCritriaTxt(String freeMatCritriaTxt) {
        FreeMatCritriaTxt = freeMatCritriaTxt;
    }


    public String getRatioSchDisAmt() {
        return RatioSchDisAmt;
    }

    public void setRatioSchDisAmt(String ratioSchDisAmt) {
        RatioSchDisAmt = ratioSchDisAmt;
    }

    public String RatioSchDisAmt = "";
    public String FreeMaterialNo = "";

    public String getFreeMatTax() {
        return FreeMatTax;
    }

    public void setFreeMatTax(String freeMatTax) {
        FreeMatTax = freeMatTax;
    }

    public String FreeMatTax = "";

    public String getFreeMatPrice() {
        return FreeMatPrice;
    }

    public void setFreeMatPrice(String freeMatPrice) {
        FreeMatPrice = freeMatPrice;
    }

    public String FreeMatPrice = "";
    public String RatioSchemeIndexVal = "-1";

    public String getRatioSchMatPrice() {
        return RatioSchMatPrice;
    }

    public void setRatioSchMatPrice(String ratioSchMatPrice) {
        RatioSchMatPrice = ratioSchMatPrice;
    }

    public String RatioSchMatPrice = "";

    public String getRatioSchemeIndexVal() {
        return RatioSchemeIndexVal;
    }

    public void setRatioSchemeIndexVal(String ratioSchemeIndexVal) {
        RatioSchemeIndexVal = ratioSchemeIndexVal;
    }


    public String getISFreeTypeID() {
        return ISFreeTypeID;
    }

    public void setISFreeTypeID(String ISFreeTypeID) {
        this.ISFreeTypeID = ISFreeTypeID;
    }

    public String getFreeQtyUOM() {
        return FreeQtyUOM;
    }

    public void setFreeQtyUOM(String freeQtyUOM) {
        FreeQtyUOM = freeQtyUOM;
    }

    public String FreeQtyUOM = "";

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String UOM = "";

    public String getRatioSchNum() {
        return RatioSchNum;
    }

    public void setRatioSchNum(String ratioSchNum) {
        RatioSchNum = ratioSchNum;
    }

    public String getRatioSchDen() {
        return RatioSchDen;
    }

    public void setRatioSchDen(String ratioSchDen) {
        RatioSchDen = ratioSchDen;
    }

    public String getFreeMaterialNo() {
        return FreeMaterialNo;
    }

    public void setFreeMaterialNo(String freeMaterialNo) {
        FreeMaterialNo = freeMaterialNo;
    }


    public String getSchemeCatID() {
        return SchemeCatID;
    }

    public void setSchemeCatID(String schemeCatID) {
        SchemeCatID = schemeCatID;
    }

    public String SchemeCatID = "";

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String MRP = "";

    public String getTargetBasedID() {
        return TargetBasedID;
    }

    public void setTargetBasedID(String targetBasedID) {
        TargetBasedID = targetBasedID;
    }

    public String TargetBasedID = "";

    public String getCPItemGUID() {
        return CPItemGUID;
    }

    public void setCPItemGUID(String CPItemGUID) {
        this.CPItemGUID = CPItemGUID;
    }

    public String CPItemGUID = "";

    public String getTax() {
        return Tax;
    }

    public void setTax(String tax) {
        Tax = tax;
    }

    public String Tax = "";

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String batch) {
        Batch = batch;
    }


    public String getFreeMAt() {
        return FreeMAt;
    }

    public void setFreeMAt(String freeMAt) {
        FreeMAt = freeMAt;
    }


    public String getMinimumOrderQty() {
        return MinimumOrderQty;
    }

    public void setMinimumOrderQty(String minimumOrderQty) {
        MinimumOrderQty = minimumOrderQty;
    }

    public String MinimumOrderQty = "";

    public String getFreeMatTxt() {
        return FreeMatTxt;
    }

    public void setFreeMatTxt(String freeMatTxt) {
        FreeMatTxt = freeMatTxt;
    }

    public String FreeMatTxt = "";

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


    public String getIsExclude() {
        return IsExclude;
    }

    public void setIsExclude(String isExclude) {
        IsExclude = isExclude;
    }


    public String getCPTypeID() {
        return CPTypeID;
    }

    public void setCPTypeID(String CPTypeID) {
        this.CPTypeID = CPTypeID;
    }


    public String getOrgScopeID() {
        return OrgScopeID;
    }

    public void setOrgScopeID(String orgScopeID) {
        OrgScopeID = orgScopeID;
    }

    public String getOrgScopeDesc() {
        return OrgScopeDesc;
    }

    public void setOrgScopeDesc(String orgScopeDesc) {
        OrgScopeDesc = orgScopeDesc;
    }


    public String getIsHeaderBasedSlab() {
        return IsHeaderBasedSlab;
    }

    public void setIsHeaderBasedSlab(String isHeaderBasedSlab) {
        IsHeaderBasedSlab = isHeaderBasedSlab;
    }


    public String getSKUGroupDesc() {
        return SKUGroupDesc;
    }

    public void setSKUGroupDesc(String SKUGroupDesc) {
        this.SKUGroupDesc = SKUGroupDesc;
    }

    public String getSKUGroupID() {
        return SKUGroupID;
    }

    public void setSKUGroupID(String SKUGroupID) {
        this.SKUGroupID = SKUGroupID;
    }


    public String getSchemeGroupDesc() {
        return SchemeGroupDesc;
    }

    public void setSchemeGroupDesc(String schemeGroupDesc) {
        SchemeGroupDesc = schemeGroupDesc;
    }

    public String getSchemeTypeID() {
        return SchemeTypeID;
    }

    public void setSchemeTypeID(String schemeTypeID) {
        SchemeTypeID = schemeTypeID;
    }

    public String getSchemeTypeDesc() {
        return SchemeTypeDesc;
    }

    public void setSchemeTypeDesc(String schemeTypeDesc) {
        SchemeTypeDesc = schemeTypeDesc;
    }

    public String getSchemeID() {
        return SchemeID;
    }

    public void setSchemeID(String schemeID) {
        SchemeID = schemeID;
    }

    public String getSchemeGroupID() {
        return SchemeGroupID;
    }

    public void setSchemeGroupID(String schemeGroupID) {
        SchemeGroupID = schemeGroupID;
    }

    private String SchemeGroupDesc = "";


    public String getFlag() {
        return Flag;
    }

    public void setFlag(String flag) {
        Flag = flag;
    }

    public String getSchemeGuid() {
        return SchemeGuid;
    }

    public void setSchemeGuid(String schemeGuid) {
        SchemeGuid = schemeGuid;
    }

    public String getSchemeDesc() {
        return SchemeDesc;
    }

    public void setSchemeDesc(String schemeDesc) {
        SchemeDesc = schemeDesc;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getSchemeGUID() {
        return SchemeGUID;
    }

    public void setSchemeGUID(String schemeGUID) {
        SchemeGUID = schemeGUID;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getPrimaryScheme() {
        return PrimaryScheme;
    }

    public void setPrimaryScheme(String primaryScheme) {
        PrimaryScheme = primaryScheme;
    }

    public String getPrimaryPer() {
        return PrimaryPer;
    }

    public void setPrimaryPer(String primaryPer) {
        PrimaryPer = primaryPer;
    }

    public String getSecondaryPer() {
        return SecondaryPer;
    }

    public void setSecondaryPer(String secondaryPer) {
        SecondaryPer = secondaryPer;
    }

    public String getConsumerScheme() {
        return ConsumerScheme;
    }

    public void setConsumerScheme(String consumerScheme) {
        ConsumerScheme = consumerScheme;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    private String Location = "";
    public String DMSDivisionID = "";
    public String CPGroup2Desc = "";
    public String DMSDivisionDesc = "";
    public String SalesAreaGUID = "";
    public String CPGroup1ID = "";
    public String CPGroup1Desc = "";
    public String CPGroup3Desc = "";
    public String DivisionID = "";
    public String CPGroup4Desc = "";
    public String DistributionChannelID = "";
    public String CPGroup3ID = "";
    public String SalesOrgDesc = "";
    public String DistributionChannelDesc = "";
    public String DivisionDesc = "";
    public String CPGroup4ID = "";
    public String SalesOrgID = "";
    public String CPGroup2ID = "";
    public String SchemeLevelID = "";
    public String GeographyGUID = "";
    public String SchemeValueDesc = "";
    public String SchemeScopeDesc = "";
    public String SchemeLevelDesc = "";
    public String SchemeValueID = "";
    public String SchemeScopeID = "";
    public String GeographyScopeID = "";
    public String GeographyScopeDesc = "";
    public String GeographyLevelID = "";
    public String GeographyLevelDesc = "";
    public String GeographyTypeID = "";
    public String GeographyTypeDesc = "";
    public String GeographyValueID = "";
    public String GeographyValueDesc = "";
    public String FromQty = "";
    public String ToQty = "";
    public String FromValue = "";
    public String ToValue = "";
    public String PayoutPerc = "";
    public String PayoutAmount = "";
    public String SchemeItemGUID = "";
    public String SlabRuleID = "";
    public String SlabRuleDesc = "";
    private String SchemeOffer = "";

    public String SchemeCPGUID = "";
    public String SchemeCPName = "";
    private String SchemeCPUID = "";

    public String Zone = "";
    public String Region = "";
    private String Area = "";
    private String HeadQuarter = "";
    private String Depot = "";
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


    public String getDepot() {
        return Depot;
    }

    public void setDepot(String depot) {
        Depot = depot;
    }

    public String getZone() {
        return Zone;
    }

    public void setZone(String zone) {
        Zone = zone;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getHeadQuarter() {
        return HeadQuarter;
    }

    public void setHeadQuarter(String headQuarter) {
        HeadQuarter = headQuarter;
    }


    public String getSchemeCPUID() {
        return SchemeCPUID;
    }

    public void setSchemeCPUID(String schemeCPUID) {
        SchemeCPUID = schemeCPUID;
    }

    public String getSchemeCPGUID() {
        return SchemeCPGUID;
    }

    public void setSchemeCPGUID(String schemeCPGUID) {
        SchemeCPGUID = schemeCPGUID;
    }

    public String getSchemeCPName() {
        return SchemeCPName;
    }

    public void setSchemeCPName(String schemeCPName) {
        SchemeCPName = schemeCPName;
    }


    public String getGeographyMapping() {
        return GeographyMapping;
    }

    public void setGeographyMapping(String geographyMapping) {
        GeographyMapping = geographyMapping;
    }

    private String GeographyMapping = "";

    public String getSchemeOffer() {
        return SchemeOffer;
    }

    public void setSchemeOffer(String schemeOffer) {
        SchemeOffer = schemeOffer;
    }


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


    public String getSchemeItemGUID() {
        return SchemeItemGUID;
    }

    public void setSchemeItemGUID(String schemeItemGUID) {
        SchemeItemGUID = schemeItemGUID;
    }

    public String getFromQty() {
        return FromQty;
    }

    public void setFromQty(String fromQty) {
        FromQty = fromQty;
    }

    public String getToQty() {
        return ToQty;
    }

    public void setToQty(String toQty) {
        ToQty = toQty;
    }

    public String getFromValue() {
        return FromValue;
    }

    public void setFromValue(String fromValue) {
        FromValue = fromValue;
    }

    public String getToValue() {
        return ToValue;
    }

    public void setToValue(String toValue) {
        ToValue = toValue;
    }

    public String getPayoutPerc() {
        return PayoutPerc;
    }

    public void setPayoutPerc(String payoutPerc) {
        PayoutPerc = payoutPerc;
    }

    public String getPayoutAmount() {
        return PayoutAmount;
    }

    public void setPayoutAmount(String payoutAmount) {
        PayoutAmount = payoutAmount;
    }

    public String getDMSDivisionID() {
        return DMSDivisionID;
    }

    public void setDMSDivisionID(String DMSDivisionID) {
        this.DMSDivisionID = DMSDivisionID;
    }

    public String getCPGroup2Desc() {
        return CPGroup2Desc;
    }

    public void setCPGroup2Desc(String CPGroup2Desc) {
        this.CPGroup2Desc = CPGroup2Desc;
    }

    public String getDMSDivisionDesc() {
        return DMSDivisionDesc;
    }

    public void setDMSDivisionDesc(String DMSDivisionDesc) {
        this.DMSDivisionDesc = DMSDivisionDesc;
    }

    public String getSalesAreaGUID() {
        return SalesAreaGUID;
    }

    public void setSalesAreaGUID(String salesAreaGUID) {
        SalesAreaGUID = salesAreaGUID;
    }

    public String getCPGroup1ID() {
        return CPGroup1ID;
    }

    public void setCPGroup1ID(String CPGroup1ID) {
        this.CPGroup1ID = CPGroup1ID;
    }

    public String getCPGroup1Desc() {
        return CPGroup1Desc;
    }

    public void setCPGroup1Desc(String CPGroup1Desc) {
        this.CPGroup1Desc = CPGroup1Desc;
    }

    public String getCPGroup3Desc() {
        return CPGroup3Desc;
    }

    public void setCPGroup3Desc(String CPGroup3Desc) {
        this.CPGroup3Desc = CPGroup3Desc;
    }

    public String getDivisionID() {
        return DivisionID;
    }

    public void setDivisionID(String divisionID) {
        DivisionID = divisionID;
    }

    public String getCPGroup4Desc() {
        return CPGroup4Desc;
    }

    public void setCPGroup4Desc(String CPGroup4Desc) {
        this.CPGroup4Desc = CPGroup4Desc;
    }

    public String getDistributionChannelID() {
        return DistributionChannelID;
    }

    public void setDistributionChannelID(String distributionChannelID) {
        DistributionChannelID = distributionChannelID;
    }

    public String getCPGroup3ID() {
        return CPGroup3ID;
    }

    public void setCPGroup3ID(String CPGroup3ID) {
        this.CPGroup3ID = CPGroup3ID;
    }

    public String getSalesOrgDesc() {
        return SalesOrgDesc;
    }

    public void setSalesOrgDesc(String salesOrgDesc) {
        SalesOrgDesc = salesOrgDesc;
    }

    public String getDistributionChannelDesc() {
        return DistributionChannelDesc;
    }

    public void setDistributionChannelDesc(String distributionChannelDesc) {
        DistributionChannelDesc = distributionChannelDesc;
    }

    public String getDivisionDesc() {
        return DivisionDesc;
    }

    public void setDivisionDesc(String divisionDesc) {
        DivisionDesc = divisionDesc;
    }

    public String getCPGroup4ID() {
        return CPGroup4ID;
    }

    public void setCPGroup4ID(String CPGroup4ID) {
        this.CPGroup4ID = CPGroup4ID;
    }

    public String getSalesOrgID() {
        return SalesOrgID;
    }

    public void setSalesOrgID(String salesOrgID) {
        SalesOrgID = salesOrgID;
    }

    public String getCPGroup2ID() {
        return CPGroup2ID;
    }

    public void setCPGroup2ID(String CPGroup2ID) {
        this.CPGroup2ID = CPGroup2ID;
    }


    public String getSchemeScopeID() {
        return SchemeScopeID;
    }

    public void setSchemeScopeID(String schemeScopeID) {
        SchemeScopeID = schemeScopeID;
    }

    public String getSchemeLevelID() {
        return SchemeLevelID;
    }

    public void setSchemeLevelID(String schemeLevelID) {
        SchemeLevelID = schemeLevelID;
    }

    public String getGeographyGUID() {
        return GeographyGUID;
    }

    public void setGeographyGUID(String geographyGUID) {
        GeographyGUID = geographyGUID;
    }

    public String getSchemeValueDesc() {
        return SchemeValueDesc;
    }

    public void setSchemeValueDesc(String schemeValueDesc) {
        SchemeValueDesc = schemeValueDesc;
    }

    public String getSchemeScopeDesc() {
        return SchemeScopeDesc;
    }

    public void setSchemeScopeDesc(String schemeScopeDesc) {
        SchemeScopeDesc = schemeScopeDesc;
    }

    public String getSchemeLevelDesc() {
        return SchemeLevelDesc;
    }

    public void setSchemeLevelDesc(String schemeLevelDesc) {
        SchemeLevelDesc = schemeLevelDesc;
    }

    public String getSchemeValueID() {
        return SchemeValueID;
    }

    public void setSchemeValueID(String schemeValueID) {
        SchemeValueID = schemeValueID;
    }


    public String getGeographyValueDesc() {
        return GeographyValueDesc;
    }

    public void setGeographyValueDesc(String geographyValueDesc) {
        GeographyValueDesc = geographyValueDesc;
    }

    public String getGeographyScopeID() {
        return GeographyScopeID;
    }

    public void setGeographyScopeID(String geographyScopeID) {
        GeographyScopeID = geographyScopeID;
    }

    public String getGeographyScopeDesc() {
        return GeographyScopeDesc;
    }

    public void setGeographyScopeDesc(String geographyScopeDesc) {
        GeographyScopeDesc = geographyScopeDesc;
    }

    public String getGeographyLevelID() {
        return GeographyLevelID;
    }

    public void setGeographyLevelID(String geographyLevelID) {
        GeographyLevelID = geographyLevelID;
    }

    public String getGeographyLevelDesc() {
        return GeographyLevelDesc;
    }

    public void setGeographyLevelDesc(String geographyLevelDesc) {
        GeographyLevelDesc = geographyLevelDesc;
    }

    public String getGeographyTypeID() {
        return GeographyTypeID;
    }

    public void setGeographyTypeID(String geographyTypeID) {
        GeographyTypeID = geographyTypeID;
    }

    public String getGeographyTypeDesc() {
        return GeographyTypeDesc;
    }

    public void setGeographyTypeDesc(String geographyTypeDesc) {
        GeographyTypeDesc = geographyTypeDesc;
    }

    public String getGeographyValueID() {
        return GeographyValueID;
    }

    public void setGeographyValueID(String geographyValueID) {
        GeographyValueID = geographyValueID;
    }

    public boolean isSecondTime() {
        return isSecondTime;
    }

    public void setSecondTime(boolean secondTime) {
        isSecondTime = secondTime;
    }

    public ArrayList<MaterialBatchBean> getMaterialBatchBeanArrayList() {
        return materialBatchBeanArrayList;
    }

    public void setMaterialBatchBeanArrayList(ArrayList<MaterialBatchBean> materialBatchBeanArrayList) {
        this.materialBatchBeanArrayList = materialBatchBeanArrayList;
    }

    private ArrayList<MaterialBatchBean> materialBatchBeanArrayList;
    private String NetAmount = "";

    public String getNetAmount() {
        return NetAmount;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    private String OnSaleOfCatID = "";
    private String HierarchicalRefGUID = "";
    private String ItemMin = "";

    public String getOnSaleOfCatID() {
        return OnSaleOfCatID;
    }

    public void setOnSaleOfCatID(String onSaleOfCatID) {
        OnSaleOfCatID = onSaleOfCatID;
    }

    public String getHierarchicalRefGUID() {
        return HierarchicalRefGUID;
    }

    public void setHierarchicalRefGUID(String hierarchicalRefGUID) {
        HierarchicalRefGUID = hierarchicalRefGUID;
    }

    public String getItemMin() {
        return ItemMin;
    }

    public void setItemMin(String itemMin) {
        ItemMin = itemMin;
    }

    private String BrandID = "";

    public String getBrandID() {
        return BrandID;
    }

    public void setBrandID(String brandID) {
        BrandID = brandID;
    }

    public String getBannerID() {
        return BannerID;
    }

    public void setBannerID(String bannerID) {
        BannerID = bannerID;
    }

    private String BannerID = "";

    public String getOrderQty() {
        return OrderQty;
    }

    public void setOrderQty(String orderQty) {
        OrderQty = orderQty;
    }

    private String OrderQty = "";

    public boolean isMaterialLevel() {
        return isMaterialLevel;
    }

    public void setMaterialLevel(boolean materialLevel) {
        isMaterialLevel = materialLevel;
    }

    public boolean isRatioSchemeApplicable() {
        return isRatioSchemeApplicable;
    }

    public void setRatioSchemeApplicable(boolean ratioSchemeApplicable) {
        isRatioSchemeApplicable = ratioSchemeApplicable;
    }

    public boolean ismBoolBatchQtyNotEmpty() {
        return mBoolBatchQtyNotEmpty;
    }

    public void setmBoolBatchQtyNotEmpty(boolean mBoolBatchQtyNotEmpty) {
        this.mBoolBatchQtyNotEmpty = mBoolBatchQtyNotEmpty;
    }

    public boolean ismRatioSchemeAval() {
        return mRatioSchemeAval;
    }

    public void setmRatioSchemeAval(boolean mRatioSchemeAval) {
        this.mRatioSchemeAval = mRatioSchemeAval;
    }

    public boolean ismRatioSchemeAvalTemp() {
        return mRatioSchemeAvalTemp;
    }

    public void setmRatioSchemeAvalTemp(boolean mRatioSchemeAvalTemp) {
        this.mRatioSchemeAvalTemp = mRatioSchemeAvalTemp;
    }

    public String getmStrFreeMaterialNoTemp() {
        return mStrFreeMaterialNoTemp;
    }

    public void setmStrFreeMaterialNoTemp(String mStrFreeMaterialNoTemp) {
        this.mStrFreeMaterialNoTemp = mStrFreeMaterialNoTemp;
    }

    public double getDoubleRatioSchNumTemp() {
        return doubleRatioSchNumTemp;
    }

    public void setDoubleRatioSchNumTemp(double doubleRatioSchNumTemp) {
        this.doubleRatioSchNumTemp = doubleRatioSchNumTemp;
    }

    public double getmDobRatioSchDenTemp() {
        return mDobRatioSchDenTemp;
    }

    public void setmDobRatioSchDenTemp(double mDobRatioSchDenTemp) {
        this.mDobRatioSchDenTemp = mDobRatioSchDenTemp;
    }

    public String getMatNetAmtAftPriDis() {
        return matNetAmtAftPriDis;
    }

    public void setMatNetAmtAftPriDis(String matNetAmtAftPriDis) {
        this.matNetAmtAftPriDis = matNetAmtAftPriDis;
    }

    public String getMatSecPer() {
        return matSecPer;
    }

    public void setMatSecPer(String matSecPer) {
        this.matSecPer = matSecPer;
    }

    public String getMatNetAmount() {
        return matNetAmount;
    }

    public void setMatNetAmount(String matNetAmount) {
        this.matNetAmount = matNetAmount;
    }

    public boolean isRatioScheme() {
        return isRatioScheme;
    }

    public void setRatioScheme(boolean ratioScheme) {
        isRatioScheme = ratioScheme;
    }
}
