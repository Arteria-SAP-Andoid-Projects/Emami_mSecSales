package com.arteriatech.emami.retailerStock;

import java.io.Serializable;

/**
 * Created by e10762 on 23-01-2017.
 */

public class RetailerStockBean implements Serializable {
    private String CPStockItemGUID = "";
    private String MaterialDesc = "";
    private String MaterialNo = "";
    private String QAQty = "";
    private String BlockedQty = "";
    private String StockValue = "";
    private String LandingPrice = "";
    private String OrderMaterialGroupID = "";
    private String OrderMaterialGroupDesc = "";
    private String NewStockValue = "";
    private Boolean NewStockItem = false;
    private String CPGUID = "";
    private String SerialNoFrom = "";
    private String Currency = "";
    private String Batch = "";
    private String MFD = "";
    private String CrsSksGroup;
    private String MRP = "";
    private String RLPrice = "";
    private String Uom = "";
    private String SerialNoTo = "";
    private Boolean isSelected = false;
    private String StockType = "";
    private String Etag = "";
    private String StockOwner = "";
    private String UnrestrictedQty = "";
    private String ExpiryDate = "";
    private String Brand = "";
    private String Remarks = "";
    private int retailerPos=0;

    public String getEtag() {
        return Etag;
    }

    public void setEtag(String etag) {
        Etag = etag;
    }

    public String getStockOwner() {
        return StockOwner;
    }

    public void setStockOwner(String stockOwner) {
        StockOwner = stockOwner;
    }

    public String getStockType() {
        return StockType;
    }

    public void setStockType(String stockType) {
        StockType = stockType;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public Boolean getNewStockItem() {
        return NewStockItem;
    }

    public void setNewStockItem(Boolean newStockItem) {
        NewStockItem = newStockItem;
    }


    public String getNewStockValue() {
        return NewStockValue;
    }

    public void setNewStockValue(String newStockValue) {
        NewStockValue = newStockValue;
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


    public String getUnrestrictedQty() {
        return UnrestrictedQty;
    }

    public void setUnrestrictedQty(String unrestrictedQty) {
        UnrestrictedQty = unrestrictedQty;
    }


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


    public String getCrsSksGroup() {
        return CrsSksGroup;
    }

    public void setCrsSksGroup(String crsSksGroup) {
        CrsSksGroup = crsSksGroup;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public int getRetailerPos() {
        return retailerPos;
    }

    public void setRetailerPos(int retailerPos) {
        this.retailerPos = retailerPos;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }
}
