package com.arteriatech.emami.mbo;

import java.io.Serializable;

/**
 * Created by e10526 on 11-07-2018.
 */

public class SSOItemBean implements Serializable {
    private String MaterialNo = "";
    private String BatchNo = "";
    private String Qty = "";
    private String SOItemGuid = "";

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
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

    public String getSOItemGuid() {
        return SOItemGuid;
    }

    public void setSOItemGuid(String SOItemGuid) {
        this.SOItemGuid = SOItemGuid;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    private String MRP = "";
}
