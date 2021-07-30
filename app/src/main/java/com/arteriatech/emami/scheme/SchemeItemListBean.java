package com.arteriatech.emami.scheme;

/**
 * Created by e10769 on 28-03-2017.
 */

public class SchemeItemListBean {
    private String OnSalesDesc = "";
    private String OnSaleOfCatID = "";
    private String ItemMin = "";
    private String UOM = "";


    public String getOnSaleOfCatID() {
        return OnSaleOfCatID;
    }

    public void setOnSaleOfCatID(String onSaleOfCatID) {
        OnSaleOfCatID = onSaleOfCatID;
    }

    public String getOnSalesDesc() {
        return OnSalesDesc;
    }

    public void setOnSalesDesc(String onSalesDesc) {
        OnSalesDesc = onSalesDesc;
    }

    public String getItemMin() {
        return ItemMin;
    }

    public void setItemMin(String itemMin) {
        ItemMin = itemMin;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }
}
