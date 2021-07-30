package com.arteriatech.emami.mbo;

/**
 * Created by e10763 on 3/8/2017.
 */

public class AlertsBean {

    private String AlertGUID = "";
    private String Application = "";
    private String PartnerID = "";

    private String LoginID = "";
    private String PartnerType = "";
    private String AlertTypeDesc = "";
    private String AlertType = "";
    private String AlertText = "";

    public String getAlertGUID() {
        return AlertGUID;
    }

    public void setAlertGUID(String alertGUID) {
        AlertGUID = alertGUID;
    }

    public String getApplication() {
        return Application;
    }

    public void setApplication(String application) {
        Application = application;
    }

    public String getPartnerID() {
        return PartnerID;
    }

    public void setPartnerID(String partnerID) {
        PartnerID = partnerID;
    }

    public String getLoginID() {
        return LoginID;
    }

    public void setLoginID(String loginID) {
        LoginID = loginID;
    }

    public String getPartnerType() {
        return PartnerType;
    }

    public void setPartnerType(String partnerType) {
        PartnerType = partnerType;
    }

    public String getAlertTypeDesc() {
        return AlertTypeDesc;
    }

    public void setAlertTypeDesc(String alertTypeDesc) {
        AlertTypeDesc = alertTypeDesc;
    }

    public String getAlertType() {
        return AlertType;
    }

    public void setAlertType(String alertType) {
        AlertType = alertType;
    }

    public String getAlertText() {
        return AlertText;
    }

    public void setAlertText(String alertText) {
        AlertText = alertText;
    }


}
