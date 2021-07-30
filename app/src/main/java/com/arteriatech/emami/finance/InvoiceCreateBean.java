package com.arteriatech.emami.finance;

/**
 * Created by e10742 on 7/28/2017.
 */

public class InvoiceCreateBean {
    String CPStockItemGuid = "";
    String MaterialDesc = "";
    String MaterialNo = "";
    String MaterialGroup = "";
    String MaterialGroupDesc = "";
    String StockQty = "";
    String OrderQty = "";
    String UnitPrice = "";
    String MRP = "";
    String NetAmount = "";
    String DiscountAmount = "";
    String TaxAmount = "";
    String UOM = "";
    String Currency = "";

    public String getCPStockItemGuid() {
        return CPStockItemGuid;
    }

    public void setCPStockItemGuid(String CPStockItemGuid) {
        this.CPStockItemGuid = CPStockItemGuid;
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

    public String getMaterialGroup() {
        return MaterialGroup;
    }

    public void setMaterialGroup(String materialGroup) {
        MaterialGroup = materialGroup;
    }

    public String getMaterialGroupDesc() {
        return MaterialGroupDesc;
    }

    public void setMaterialGroupDesc(String materialGroupDesc) {
        MaterialGroupDesc = materialGroupDesc;
    }

    public String getStockQty() {
        return StockQty;
    }

    public void setStockQty(String stockQty) {
        StockQty = stockQty;
    }

    public String getOrderQty() {
        return OrderQty;
    }

    public void setOrderQty(String orderQty) {
        OrderQty = orderQty;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getNetAmount() {
        return NetAmount;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    public String getDiscountAmount() {
        return DiscountAmount;
    }

    public void setDiscountAmount(String discountAmount) {
        DiscountAmount = discountAmount;
    }

    public String getTaxAmount() {
        return TaxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        TaxAmount = taxAmount;
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
}
