package com.arteriatech.emami.scheme;

import java.util.HashSet;

/**
 * Created by e10769 on 28-03-2017.
 */

public class SchemeIDBean {
    private String schemeGuid = "";
    private HashSet<String> orderMaterialId = null;

    public HashSet<String> getOrderMaterialId() {
        return orderMaterialId;
    }

    public void setOrderMaterialId(HashSet<String> orderMaterialId) {
        this.orderMaterialId = orderMaterialId;
    }

    public String getSchemeGuid() {
        return schemeGuid;
    }


    public void setSchemeGuid(String schemeGuid) {
        this.schemeGuid = schemeGuid;
    }

    public String getSchemeCatID() {
        return schemeCatID;
    }

    public void setSchemeCatID(String schemeCatID) {
        this.schemeCatID = schemeCatID;
    }

    private String schemeCatID = "";
}
