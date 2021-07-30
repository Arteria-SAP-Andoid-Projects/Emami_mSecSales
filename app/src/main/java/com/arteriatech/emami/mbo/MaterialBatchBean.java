package com.arteriatech.emami.mbo;

import com.sap.smp.client.odata.ODataEntity;

/**
 * Created by e10526 on 4/5/2017.
 */

public class MaterialBatchBean {
    String Currency = "";
    private String MaterialNo = "";
    private String LandingPrice = "";
    private String BatchNo = "";
    private String Qty = "";
    private String PrimaryPer = "";
    private String secondarySchemeAmt = "";
    private String TransRefTypeID = "";
    private String TransRefNo = "";
    private String TransRefItemNo = "";
    private String PrimaryPerAmt = "";
    private String IntermUnitPrice = "";
    private String GrossAmt = "";
    private String MRP = "";
    private String Tax = "";
    private String SlabRuleAmt = "";
    private String SecDiscountAmt = "";
    private String SecPer = "";
    private String NetAmount = "";
    private ODataEntity oDataEntity;
    private String NetAmtAftPriDis = "";
    private String MFD = "";

    public String getInvCashDisc() {
        return InvCashDisc;
    }

    public void setInvCashDisc(String invCashDisc) {
        InvCashDisc = invCashDisc;
    }

    private String InvCashDisc = "";

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public String getCPSnoGUID() {
        return CPSnoGUID;
    }

    public void setCPSnoGUID(String CPSnoGUID) {
        this.CPSnoGUID = CPSnoGUID;
    }

    private String CPSnoGUID = "";
    private String ExpiryDate = "";
    private String TaxAmount = "";
    private String TotalNetAmt = "";

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getPrimaryPerAmt() {
        return PrimaryPerAmt;
    }

    public void setPrimaryPerAmt(String primaryPerAmt) {
        PrimaryPerAmt = primaryPerAmt;
    }

    public String getIntermUnitPrice() {
        return IntermUnitPrice;
    }

    public void setIntermUnitPrice(String intermUnitPrice) {
        IntermUnitPrice = intermUnitPrice;
    }

    public String getGrossAmt() {
        return GrossAmt;
    }

    public void setGrossAmt(String grossAmt) {
        GrossAmt = grossAmt;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getTax() {
        return Tax;
    }

    public void setTax(String tax) {
        Tax = tax;
    }

    public String getSlabRuleAmt() {
        return SlabRuleAmt;
    }

    public void setSlabRuleAmt(String slabRuleAmt) {
        SlabRuleAmt = slabRuleAmt;
    }

    public String getSecDiscountAmt() {
        return SecDiscountAmt;
    }

    public void setSecDiscountAmt(String secDiscountAmt) {
        SecDiscountAmt = secDiscountAmt;
    }

    public String getSecPer() {
        return SecPer;
    }

    public void setSecPer(String secPer) {
        SecPer = secPer;
    }

    public ODataEntity getoDataEntity() {
        return oDataEntity;
    }

    public void setoDataEntity(ODataEntity oDataEntity) {
        this.oDataEntity = oDataEntity;
    }

    public String getNetAmtAftPriDis() {
        return NetAmtAftPriDis;
    }

    public void setNetAmtAftPriDis(String netAmtAftPriDis) {
        NetAmtAftPriDis = netAmtAftPriDis;
    }

    public String getMFD() {
        return MFD;
    }

    public void setMFD(String MFD) {
        this.MFD = MFD;
    }

    public String getTaxAmount() {
        return TaxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        TaxAmount = taxAmount;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getLandingPrice() {
        return LandingPrice;
    }

    public void setLandingPrice(String landingPrice) {
        LandingPrice = landingPrice;
    }

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getPrimaryPer() {
        return PrimaryPer;
    }

    public void setPrimaryPer(String primaryPer) {
        PrimaryPer = primaryPer;
    }

    public String getNetAmount() {
        return NetAmount;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    public String getTotalNetAmt() {
        return TotalNetAmt;
    }

    public void setTotalNetAmt(String totalNetAmt) {
        TotalNetAmt = totalNetAmt;
    }

    public String getSecondarySchemeAmt() {
        return secondarySchemeAmt;
    }

    public void setSecondarySchemeAmt(String secondarySchemeAmt) {
        this.secondarySchemeAmt = secondarySchemeAmt;
    }

    public String getTransRefTypeID() {
        return TransRefTypeID;
    }

    public void setTransRefTypeID(String transRefTypeID) {
        TransRefTypeID = transRefTypeID;
    }

    public String getTransRefNo() {
        return TransRefNo;
    }

    public void setTransRefNo(String transRefNo) {
        TransRefNo = transRefNo;
    }

    public String getTransRefItemNo() {
        return TransRefItemNo;
    }

    public void setTransRefItemNo(String transRefItemNo) {
        TransRefItemNo = transRefItemNo;
    }
}
