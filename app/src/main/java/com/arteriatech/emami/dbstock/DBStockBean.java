package com.arteriatech.emami.dbstock;

/**
 * Created by e10742 on 14-11-2016.
 */
public class DBStockBean {

    private String CPStockItemGUID="";
    private String MaterialDesc="";
    private String MaterialNo="";
    private String QAQty="";
    private String BlockedQty="";
    private String StockValue="";
    String PrimDiscPer = "";

    public String getPrimDiscPer() {
        return PrimDiscPer;
    }

    public void setPrimDiscPer(String primDiscPer) {
        PrimDiscPer = primDiscPer;
    }

    public String getLandingPrice() {
        return LandingPrice;
    }

    public void setLandingPrice(String landingPrice) {
        LandingPrice = landingPrice;
    }

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

    private String LandingPrice="";
    private String OrderMaterialGroupID="";
    private String OrderMaterialGroupDesc="";


    String CPGUID="";
    String  SerialNoFrom="";
    String Currency="";
    private String Batch = "";

    public String getMFD() {
        return MFD;
    }

    public void setMFD(String MFD) {
        this.MFD = MFD;
    }

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String batch) {
        Batch = batch;
    }

    private String MFD = "";




    private String CrsSksGroup;

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getRLPrice() {
        return RLPrice;
    }

    public void setRLPrice(String RLPrice) {
        this.RLPrice = RLPrice;
    }

    String MRP="";
    String RLPrice="";

    public String getUnrestrictedQty() {
        return UnrestrictedQty;
    }

    public void setUnrestrictedQty(String unrestrictedQty) {
        UnrestrictedQty = unrestrictedQty;
    }

    String UnrestrictedQty="";

    public String getUom() {
        return Uom;
    }

    public void setUom(String uom) {
        Uom = uom;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    String Uom = "";



    public String getSerialNoTo() {
        return SerialNoTo;
    }

    public void setSerialNoTo(String serialNoTo) {
        SerialNoTo = serialNoTo;
    }

    public String getCPStockItemGUID() {
        return CPStockItemGUID;
    }

    public void setCPStockItemGUID(String CPStockItemGUID) {
        this.CPStockItemGUID = CPStockItemGUID;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getQAQty() {
        return QAQty;
    }

    public void setQAQty(String QAQty) {
        this.QAQty = QAQty;
    }

    public String getBlockedQty() {
        return BlockedQty;
    }

    public void setBlockedQty(String blockedQty) {
        BlockedQty = blockedQty;
    }

    public String getStockValue() {
        return StockValue;
    }

    public void setStockValue(String stockValue) {
        StockValue = stockValue;
    }

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    public String getSerialNoFrom() {
        return SerialNoFrom;
    }

    public void setSerialNoFrom(String serialNoFrom) {
        SerialNoFrom = serialNoFrom;
    }

    String  SerialNoTo="";

    public String getCrsSksGroup() {
        return CrsSksGroup;
    }

    public void setCrsSksGroup(String crsSksGroup) {
        CrsSksGroup = crsSksGroup;
    }

    public String getFirstMrpQty() {
        return FirstMrpQty;
    }

    public void setFirstMrpQty(String firstMrpQty) {
        FirstMrpQty = firstMrpQty;
    }

    public String getFirstMrpLandingPrice() {
        return FirstMrpLandingPrice;
    }

    public void setFirstMrpLandingPrice(String firstMrpLandingPrice) {
        FirstMrpLandingPrice = firstMrpLandingPrice;
    }

    private String FirstMrpQty="";
    private String FirstMrpLandingPrice="";

}
