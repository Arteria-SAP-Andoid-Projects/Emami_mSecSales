package com.arteriatech.emami.finance;

import java.io.Serializable;

/**
 * Created by e10526 on 26-04-2016.
 */
public class CollectionBean implements Serializable{
    String CPNo="";
    String BankID = "";
    String BankName = "";
    String InstrumentNo="";
    String Amount ="";
    String Remarks="";
    String FIPDocType="";
    String PaymentModeID="";
    String FIPDate="";
    String FIPGUID="";
    String BranchName = "";
    String URTNo = "";

    public String getCPTypeID() {
        return CPTypeID;
    }

    public void setCPTypeID(String CPTypeID) {
        this.CPTypeID = CPTypeID;
    }

    String CPTypeID = "";

    public String getInstrumentDate() {
        return InstrumentDate;
    }

    public void setInstrumentDate(String instrumentDate) {
        InstrumentDate = instrumentDate;
    }

    String InstrumentDate = "";

    public String getBankName() {
        return BankName;
    }

    public void setBankName(String bankName) {
        BankName = bankName;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    String Currency = "";
    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String branchName) {
        BranchName = branchName;
    }

    public String getURTNo() {
        return URTNo;
    }

    public void setURTNo(String URTNo) {
        this.URTNo = URTNo;
    }



    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String parentID) {
        ParentID = parentID;
    }

    public String getSpNo() {
        return SpNo;
    }

    public void setSpNo(String spNo) {
        SpNo = spNo;
    }

    public String getSpFirstName() {
        return SpFirstName;
    }

    public void setSpFirstName(String spFirstName) {
        SpFirstName = spFirstName;
    }

    String ParentID="";
    String SpNo="";
    String SpFirstName="";



    public String getPaymentModeDesc() {
        return PaymentModeDesc;
    }

    public void setPaymentModeDesc(String paymentModeDesc) {
        PaymentModeDesc = paymentModeDesc;
    }

    String PaymentModeDesc="";
    public String getSPGUID() {
        return SPGUID;
    }

    public void setSPGUID(String SPGUID) {
        this.SPGUID = SPGUID;
    }

    String SPGUID="";

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    String CPGUID="";


    public String getFIPGUID() {
        return FIPGUID;
    }

    public void setFIPGUID(String FIPGUID) {
        this.FIPGUID = FIPGUID;
    }

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }

    public String getBankID() {
        return BankID;
    }

    public void setBankID(String bankID) {
        BankID = bankID;
    }

    public String getInstrumentNo() {
        return InstrumentNo;
    }

    public void setInstrumentNo(String instrumentNo) {
        InstrumentNo = instrumentNo;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getFIPDocType() {
        return FIPDocType;
    }

    public void setFIPDocType(String FIPDocType) {
        this.FIPDocType = FIPDocType;
    }

    public String getPaymentModeID() {
        return PaymentModeID;
    }

    public void setPaymentModeID(String paymentModeID) {
        PaymentModeID = paymentModeID;
    }

    public String getFIPDate() {
        return FIPDate;
    }

    public void setFIPDate(String FIPDate) {
        this.FIPDate = FIPDate;
    }


}
