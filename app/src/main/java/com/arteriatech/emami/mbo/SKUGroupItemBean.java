package com.arteriatech.emami.mbo;

/**
 * Created by e10769 on 27-03-2017.
 */

public class SKUGroupItemBean {
    private boolean isImageDisplay = false;
    private String materialId = "";
    private String schemeGuid = "";

    public boolean isImageDisplay() {
        return isImageDisplay;
    }

    public void setImageDisplay(boolean imageDisplay) {
        isImageDisplay = imageDisplay;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getSchemeGuid() {
        return schemeGuid;
    }

    public void setSchemeGuid(String schemeGuid) {
        this.schemeGuid = schemeGuid;
    }

    private String qpsSchemeGuid = "";

    public String getQpsSchemeGuid() {
        return qpsSchemeGuid;
    }

    public void setQpsSchemeGuid(String qpsSchemeGuid) {
        this.qpsSchemeGuid = qpsSchemeGuid;
    }

    public String getSchemeCatID() {
        return schemeCatID;
    }

    public void setSchemeCatID(String schemeCatID) {
        this.schemeCatID = schemeCatID;
    }

    private String schemeCatID = "";
}
