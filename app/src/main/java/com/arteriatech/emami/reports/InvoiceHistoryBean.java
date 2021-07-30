package com.arteriatech.emami.reports;

import java.io.Serializable;

/**
 *
 * Created by ${e10604} on ${27/4/2016}.
 *
 */
public class InvoiceHistoryBean implements Serializable{

    String invoiceNo;
    String invoiceDate;
    String invoiceAmount;
    String invoiceStatus;
    String matDesc;
    String matCode;
    String collectionAmount;
    private String orderMaterialGroup="";

    public String getDevCollAmount() {
        return devCollAmount;
    }

    public void setDevCollAmount(String devCollAmount) {
        this.devCollAmount = devCollAmount;
    }

    String devCollAmount = "";

    public Boolean getIsDetailEnabled() {
        return isDetailEnabled;
    }

    public void setIsDetailEnabled(Boolean isDetailEnabled) {
        this.isDetailEnabled = isDetailEnabled;
    }

    Boolean isDetailEnabled;
    public String getCollectionAmount() {
        return collectionAmount;
    }

    public void setCollectionAmount(String collectionAmount) {
        this.collectionAmount = collectionAmount;
    }

    public String getCpGuid() {
        return cpGuid;
    }

    public void setCpGuid(String cpGuid) {
        this.cpGuid = cpGuid;
    }

    String cpGuid ="";

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    String deviceNo = "";

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    String deviceStatus = "";

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    String itemNo = "";

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    String uom="";

    public String getInvoiceGuid() {
        return invoiceGuid;
    }

    public void setInvoiceGuid(String invoiceGuid) {
        this.invoiceGuid = invoiceGuid;
    }

    String invoiceGuid;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    String currency;


    public String getMatDesc() {
        return matDesc;
    }

    public void setMatDesc(String matDesc) {
        this.matDesc = matDesc;
    }

    public String getInvQty() {
        return invQty;
    }

    public void setInvQty(String invQty) {
        this.invQty = invQty;
    }



    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }

    String invQty;

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }


    public String getOrderMaterialGroup() {
        return orderMaterialGroup;
    }

    public void setOrderMaterialGroup(String orderMaterialGroup) {
        this.orderMaterialGroup = orderMaterialGroup;
    }
}
