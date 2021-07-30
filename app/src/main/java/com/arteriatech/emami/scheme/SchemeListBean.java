package com.arteriatech.emami.scheme;

import java.util.ArrayList;

/**
 * Created by e10769 on 27-03-2017.
 */

public class SchemeListBean {
    private String schemeNameTitle = "";
    private String schemeTypeName = "";
    private String SchemeTypeID = "";
    private String SchemeTypeDesc = "";
    private String schemeGuid = "";
    private String schemeName = "";
    private String schemeId = "";
    private String validDate = "";
    private String SchemeDesc = "";
    private String SchemeApplicableTitle = "";
    private int screenType = 0;
    private ArrayList<SchemeSalesAreaBean> salesAreaBeanArrayList = null;
    private ArrayList<SchemeItemListBean> itemListBeanArrayList = null;
    private String SlabRuleDesc = "";
    private String SlabRuleID = "";
    private String OnSaleOfCatDesc = "";
    private ArrayList<SchemeSlabBean> schemeSlabBeanArrayList = null;
    private String SlabRuleType = "";
    private String SlabTitle = "";
    private String ValidFrom = "";

    public String getSlabTypeDesc() {
        return SlabTypeDesc;
    }

    public void setSlabTypeDesc(String slabTypeDesc) {
        SlabTypeDesc = slabTypeDesc;
    }

    private String SlabTypeDesc = "";


    public String getSchemeGuid() {
        return schemeGuid;
    }

    public void setSchemeGuid(String schemeGuid) {
        this.schemeGuid = schemeGuid;
    }

    public String getValidDate() {
        return validDate;
    }

    public void setValidDate(String validDate) {
        this.validDate = validDate;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getSchemeDesc() {
        return SchemeDesc;
    }

    public void setSchemeDesc(String schemeDesc) {
        SchemeDesc = schemeDesc;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public String getSchemeNameTitle() {
        return schemeNameTitle;
    }

    public void setSchemeNameTitle(String schemeNameTitle) {
        this.schemeNameTitle = schemeNameTitle;
    }

    public String getSchemeTypeName() {
        return schemeTypeName;
    }

    public void setSchemeTypeName(String schemeTypeName) {
        this.schemeTypeName = schemeTypeName;
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

    public String getSchemeApplicableTitle() {
        return SchemeApplicableTitle;
    }

    public void setSchemeApplicableTitle(String schemeApplicableTitle) {
        SchemeApplicableTitle = schemeApplicableTitle;
    }

    public ArrayList<SchemeSalesAreaBean> getSalesAreaBeanArrayList() {
        return salesAreaBeanArrayList;
    }

    public void setSalesAreaBeanArrayList(ArrayList<SchemeSalesAreaBean> salesAreaBeanArrayList) {
        this.salesAreaBeanArrayList = salesAreaBeanArrayList;
    }

    public ArrayList<SchemeItemListBean> getItemListBeanArrayList() {
        return itemListBeanArrayList;
    }

    public void setItemListBeanArrayList(ArrayList<SchemeItemListBean> itemListBeanArrayList) {
        this.itemListBeanArrayList = itemListBeanArrayList;
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

    public String getOnSaleOfCatDesc() {
        return OnSaleOfCatDesc;
    }

    public void setOnSaleOfCatDesc(String onSaleOfCatDesc) {
        OnSaleOfCatDesc = onSaleOfCatDesc;
    }

    public ArrayList<SchemeSlabBean> getSchemeSlabBeanArrayList() {
        return schemeSlabBeanArrayList;
    }

    public void setSchemeSlabBeanArrayList(ArrayList<SchemeSlabBean> schemeSlabBeanArrayList) {
        this.schemeSlabBeanArrayList = schemeSlabBeanArrayList;
    }

    public String getSlabRuleType() {
        return SlabRuleType;
    }

    public void setSlabRuleType(String slabRuleType) {
        SlabRuleType = slabRuleType;
    }

    public String getSlabTitle() {
        return SlabTitle;
    }

    public void setSlabTitle(String slabTitle) {
        SlabTitle = slabTitle;
    }

    public String getValidFrom() {
        return ValidFrom;
    }

    public void setValidFrom(String validFrom) {
        ValidFrom = validFrom;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }
}
