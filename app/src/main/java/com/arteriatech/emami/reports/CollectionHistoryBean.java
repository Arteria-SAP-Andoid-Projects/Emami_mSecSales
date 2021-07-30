package com.arteriatech.emami.reports;

/**
 * Created by e10526 on 28-04-2016.
 */
public class CollectionHistoryBean {
    private  String FIPDocNo="";
    private  String FIPDate ="";
    private  String Amount="";
    private  String CPNo="";
    private  String PaymentModeDesc="";
    private  String PaymentModeID="";
    private  String FIPItemNo="";
    private  String InvoiceDate="";
    private  String InvoiceAmount="";
    private  String InvoiceClearedAmount="";

    public String getReferenceTypeDesc() {
        return ReferenceTypeDesc;
    }

    public void setReferenceTypeDesc(String referenceTypeDesc) {
        ReferenceTypeDesc = referenceTypeDesc;
    }

    private  String ReferenceTypeDesc="";

    public String getTotalPaidAmount() {
        return TotalPaidAmount;
    }

    public void setTotalPaidAmount(String totalPaidAmount) {
        TotalPaidAmount = totalPaidAmount;
    }

    private String TotalPaidAmount = "";

    public Boolean getIsDetailEnabled() {
        return isDetailEnabled;
    }

    public void setIsDetailEnabled(Boolean isDetailEnabled) {
        this.isDetailEnabled = isDetailEnabled;
    }

    private Boolean isDetailEnabled;

    public String getCpMobileNo() {
        return CpMobileNo;
    }

    public void setCpMobileNo(String cpMobileNo) {
        CpMobileNo = cpMobileNo;
    }

    private  String CpMobileNo="";

    public String getCpName() {
        return CpName;
    }

    public void setCpName(String cpName) {
        CpName = cpName;
    }

    private  String CpName="";
    public String getCollTime() {
        return CollTime;
    }

    public void setCollTime(String collTime) {
        CollTime = collTime;
    }

    private  String CollTime="";

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

    public String getInvoiceNo() {
        return InvoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        InvoiceNo = invoiceNo;
    }

    private  String InvoiceNo="";

    public String getFIPGUID() {
        return FIPGUID;
    }

    public void setFIPGUID(String FIPGUID) {
        this.FIPGUID = FIPGUID;
    }

    private  String FIPGUID="";

    public String getInstrumentNo() {
        return InstrumentNo;
    }

    public void setInstrumentNo(String instrumentNo) {
        InstrumentNo = instrumentNo;
    }

    private String InstrumentNo="";


    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }


    private String Currency="";

    public String getInvoiceBalanceAmount() {
        return InvoiceBalanceAmount;
    }

    public void setInvoiceBalanceAmount(String invoiceBalanceAmount) {
        InvoiceBalanceAmount = invoiceBalanceAmount;
    }

    public String getFIPDocNo() {
        return FIPDocNo;
    }

    public void setFIPDocNo(String FIPDocNo) {
        this.FIPDocNo = FIPDocNo;
    }

    public String getFIPDate() {
        return FIPDate;
    }

    public void setFIPDate(String FIPDate) {
        this.FIPDate = FIPDate;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }

    public String getPaymentModeDesc() {
        return PaymentModeDesc;
    }

    public void setPaymentModeDesc(String paymentModeDesc) {
        PaymentModeDesc = paymentModeDesc;
    }

    public String getPaymentModeID() {
        return PaymentModeID;
    }

    public void setPaymentModeID(String paymentModeID) {
        PaymentModeID = paymentModeID;
    }

    public String getFIPItemNo() {
        return FIPItemNo;
    }

    public void setFIPItemNo(String FIPItemNo) {
        this.FIPItemNo = FIPItemNo;
    }

    public String getInvoiceDate() {
        return InvoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        InvoiceDate = invoiceDate;
    }

    public String getInvoiceAmount() {
        return InvoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        InvoiceAmount = invoiceAmount;
    }

    public String getInvoiceClearedAmount() {
        return InvoiceClearedAmount;
    }

    public void setInvoiceClearedAmount(String invoiceClearedAmount) {
        InvoiceClearedAmount = invoiceClearedAmount;
    }

    private  String InvoiceBalanceAmount="";
}
