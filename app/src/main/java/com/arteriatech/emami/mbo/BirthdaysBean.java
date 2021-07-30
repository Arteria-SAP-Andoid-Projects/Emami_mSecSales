package com.arteriatech.emami.mbo;

/**
 * Created by e10526 on 11-12-2016.
 */

public class BirthdaysBean {
    private String OwnerName = "";
    private String CPUID = "";
    private String MobileNo = "";
    private String DOB = "";
    private String Anniversary = "";
    private String DOBStatus = "";
    private String AnniversaryStatus = "";
    private String AppointmentStatus = "";

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    private String Status = "";

    public String getAlertStatus() {
        return AlertStatus;
    }

    public void setAlertStatus(String alertStatus) {
        AlertStatus = alertStatus;
    }

    private String AlertStatus = "";
    private String AlertGUID = "";
    private String Application = "";
    private String PartnerID = "";
    private String LoginID = "";
    private String PartnerType = "";
    private String AlertTypeDesc = "";
    private String AlertType = "";
    private String AlertText = "";


    private String ObjectType = "";
    private String ObjectID = "";
    private String ObjectSubID = "";

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getObjectSubID() {
        return ObjectSubID;
    }

    public void setObjectSubID(String objectSubID) {
        ObjectSubID = objectSubID;
    }

    public String getObjectID() {
        return ObjectID;
    }

    public void setObjectID(String objectID) {
        ObjectID = objectID;
    }

    public String getObjectType() {
        return ObjectType;
    }

    public void setObjectType(String objectType) {
        ObjectType = objectType;
    }

    private String CreatedBy = "";

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


    public String getAppointmentStatus() {
        return AppointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        AppointmentStatus = appointmentStatus;
    }


    public Boolean getAppointmentAlert() {
        return isAppointmentAlert;
    }

    public void setAppointmentAlert(Boolean appointmentAlert) {
        isAppointmentAlert = appointmentAlert;
    }

    private Boolean isAppointmentAlert = false;

    public String getAppointMentDate() {
        return appointMentDate;
    }

    public void setAppointMentDate(String appointMentDate) {
        this.appointMentDate = appointMentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }


    private String appointMentDate = "";
    private String appointmentTime = "";
    private String appointmentType = "";
    private String appointmentEndTime = "";

    public String getAppointmentEndTime() {
        return appointmentEndTime;
    }

    public void setAppointmentEndTime(String appointmentEndTime) {
        this.appointmentEndTime = appointmentEndTime;
    }


    public String getRetailerName() {
        return RetailerName;
    }

    public void setRetailerName(String retailerName) {
        RetailerName = retailerName;
    }

    private String RetailerName = "";

    public String getAnniversaryStatus() {
        return AnniversaryStatus;
    }

    public void setAnniversaryStatus(String anniversaryStatus) {
        AnniversaryStatus = anniversaryStatus;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getCPUID() {
        return CPUID;
    }

    public void setCPUID(String CPUID) {
        this.CPUID = CPUID;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getAnniversary() {
        return Anniversary;
    }

    public void setAnniversary(String anniversary) {
        Anniversary = anniversary;
    }

    public String getDOBStatus() {
        return DOBStatus;
    }

    public void setDOBStatus(String DOBStatus) {
        this.DOBStatus = DOBStatus;
    }

    public String getDeleteStatus() {
        return DeleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        DeleteStatus = deleteStatus;
    }

    private String DeleteStatus = "";


}
