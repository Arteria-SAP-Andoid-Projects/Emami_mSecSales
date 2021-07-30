package com.arteriatech.emami.reports;

/**
 * Created by e10526 on 09-05-2016.
 */
public class MyStockBean {
    private  String SPStockItemGUID="";
    private String MaterialDesc="";
    private String MaterialNo="";
    private String QAQty="";
    private String BlockedQty="";

    private String StockValue="";
    String SPSNoGUID="";
    String  SerialNoFrom="";
    String  SerialNoTo="";

    public String getPrefixLen() {
        return prefixLen;
    }

    public void setPrefixLen(String prefixLen) {
        this.prefixLen = prefixLen;
    }

    String prefixLen = "";

    public String getUnrestrictedQty() {
        return UnrestrictedQty;
    }

    public void setUnrestrictedQty(String unrestrictedQty) {
        UnrestrictedQty = unrestrictedQty;
    }

    String UnrestrictedQty ="";


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

    String  UOM="";
    String  Currency="";

    public String getSPSNoGUID() {
        return SPSNoGUID;
    }

    public void setSPSNoGUID(String SPSNoGUID) {
        this.SPSNoGUID = SPSNoGUID;
    }

    public String getSerialNoFrom() {
        return SerialNoFrom;
    }

    public void setSerialNoFrom(String serialNoFrom) {
        SerialNoFrom = serialNoFrom;
    }

    public String getSerialNoTo() {
        return SerialNoTo;
    }

    public void setSerialNoTo(String serialNoTo) {
        SerialNoTo = serialNoTo;
    }



    public String getStockValue() {
        return StockValue;
    }

    public void setStockValue(String stockValue) {
        StockValue = stockValue;
    }

    public String getSPStockItemGUID() {
        return SPStockItemGUID;
    }

    public void setSPStockItemGUID(String SPStockItemGUID) {
        this.SPStockItemGUID = SPStockItemGUID;
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



}
