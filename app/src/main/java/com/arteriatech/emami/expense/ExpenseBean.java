package com.arteriatech.emami.expense;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by e10769 on 06-03-2017.
 */

public class ExpenseBean implements Serializable {
    private String itemType = "";
    private String MaxAllowancePer = "";
    private boolean isDefault = false;
    private String ExpenseItemType = "";
    private String ExpenseItemTypeDesc = "";
    private String BeatGUID = "";
    private String BeatDesc = "";
    private String BeatId = "";
    private String BeatDistance = "";
    private String Location = "";
    private String LocationDesc = "";
    private String ConvenyanceMode = "";
    private String ConvenyanceModeDs = "";
    private String UOM = "";
    private String Amount = "";
    private String Currency = "";
    private String Remarks = "";
    private int itemPos = 0;
    private String AmountCategory = "";
    private String AmountCategoryDesc = "";
    private String ItemFieldSet = "";
    private String ItemFieldSetDesc = "";
    private String Allowance = "";
    private String IsSupportDocReq = "";
    private String IsRemarksReq = "";
    private String editAmount = "";
    private String ExpenseType="";
    private String ExpenseTypeDesc="";
    private String DefaultItemCat="";

    private ArrayList<ExpenseImageBean> expenseImageBeanArrayList = null;


    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public ArrayList<ExpenseImageBean> getExpenseImageBeanArrayList() {
        return expenseImageBeanArrayList;
    }

    public void setExpenseImageBeanArrayList(ArrayList<ExpenseImageBean> expenseImageBeanArrayList) {
        this.expenseImageBeanArrayList = expenseImageBeanArrayList;
    }

    public String getMaxAllowancePer() {
        return MaxAllowancePer;
    }

    public void setMaxAllowancePer(String maxAllowancePer) {
        MaxAllowancePer = maxAllowancePer;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getExpenseItemType() {
        return ExpenseItemType;
    }

    public void setExpenseItemType(String expenseItemType) {
        ExpenseItemType = expenseItemType;
    }

    public String getExpenseItemTypeDesc() {
        return ExpenseItemTypeDesc;
    }

    public void setExpenseItemTypeDesc(String expenseItemTypeDesc) {
        ExpenseItemTypeDesc = expenseItemTypeDesc;
    }

    public String getBeatGUID() {
        return BeatGUID;
    }

    public void setBeatGUID(String beatGUID) {
        BeatGUID = beatGUID;
    }

    public String getBeatDistance() {
        return BeatDistance;
    }

    public void setBeatDistance(String beatDistance) {
        BeatDistance = beatDistance;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getConvenyanceMode() {
        return ConvenyanceMode;
    }

    public void setConvenyanceMode(String convenyanceMode) {
        ConvenyanceMode = convenyanceMode;
    }

    public String getConvenyanceModeDs() {
        return ConvenyanceModeDs;
    }

    public void setConvenyanceModeDs(String convenyanceModeDs) {
        ConvenyanceModeDs = convenyanceModeDs;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getBeatDesc() {
        return BeatDesc;
    }

    public void setBeatDesc(String beatDesc) {
        BeatDesc = beatDesc;
    }

    public String getLocationDesc() {
        return LocationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        LocationDesc = locationDesc;
    }

    public int getItemPos() {
        return itemPos;
    }

    public void setItemPos(int itemPos) {
        this.itemPos = itemPos;
    }

    public String getAmountCategory() {
        return AmountCategory;
    }

    public void setAmountCategory(String amountCategory) {
        AmountCategory = amountCategory;
    }

    public String getAmountCategoryDesc() {
        return AmountCategoryDesc;
    }

    public void setAmountCategoryDesc(String amountCategoryDesc) {
        AmountCategoryDesc = amountCategoryDesc;
    }

    public String getItemFieldSet() {
        return ItemFieldSet;
    }

    public void setItemFieldSet(String itemFieldSet) {
        ItemFieldSet = itemFieldSet;
    }

    public String getItemFieldSetDesc() {
        return ItemFieldSetDesc;
    }

    public void setItemFieldSetDesc(String itemFieldSetDesc) {
        ItemFieldSetDesc = itemFieldSetDesc;
    }

    public String getAllowance() {
        return Allowance;
    }

    public void setAllowance(String allowance) {
        Allowance = allowance;
    }

    public String getBeatId() {
        return BeatId;
    }

    public void setBeatId(String beatId) {
        BeatId = beatId;
    }

    public String getIsSupportDocReq() {
        return IsSupportDocReq;
    }

    public void setIsSupportDocReq(String isSupportDocReq) {
        IsSupportDocReq = isSupportDocReq;
    }

    public String getIsRemarksReq() {
        return IsRemarksReq;
    }

    public void setIsRemarksReq(String isRemarksReq) {
        IsRemarksReq = isRemarksReq;
    }

    public String getEditAmount() {
        return editAmount;
    }

    public void setEditAmount(String editAmount) {
        this.editAmount = editAmount;
    }

    public String getExpenseType() {
        return ExpenseType;
    }

    public void setExpenseType(String expenseType) {
        ExpenseType = expenseType;
    }

    public String getExpenseTypeDesc() {
        return ExpenseTypeDesc;
    }

    public void setExpenseTypeDesc(String expenseTypeDesc) {
        ExpenseTypeDesc = expenseTypeDesc;
    }

    public String getDefaultItemCat() {
        return DefaultItemCat;
    }

    public void setDefaultItemCat(String defaultItemCat) {
        DefaultItemCat = defaultItemCat;
    }
}
