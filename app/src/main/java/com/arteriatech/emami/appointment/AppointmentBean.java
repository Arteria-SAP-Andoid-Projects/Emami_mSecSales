package com.arteriatech.emami.appointment;

/**
 * Created by e10604 on 9/7/2016.
 */
public class AppointmentBean {
    private String VisitActivityGUID = "";
    private String VisitGUID = "";
    private String ActivityType = "";
    private String ActivityTypeDesc = "";
    private String PlannedDate = "";
    private String PlannedStartTime= "";
    private String PlannedEndTime="";


    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    private String CPGUID = "";

    public String getVisitTypeDesc() {
        return VisitTypeDesc;
    }

    public void setVisitTypeDesc(String visitTypeDesc) {
        VisitTypeDesc = visitTypeDesc;
    }

    public String getPlannedEndTime() {
        return PlannedEndTime;
    }

    public void setPlannedEndTime(String plannedEndTime) {
        PlannedEndTime = plannedEndTime;
    }

    public String getPlannedStartTime() {
        return PlannedStartTime;
    }

    public void setPlannedStartTime(String plannedStartTime) {
        PlannedStartTime = plannedStartTime;
    }

    public String getPlannedDate() {
        return PlannedDate;
    }

    public void setPlannedDate(String plannedDate) {
        PlannedDate = plannedDate;
    }

    private String VisitTypeDesc="";


    public String getLoginID() {
        return LoginID;
    }

    public void setLoginID(String loginID) {
        LoginID = loginID;
    }

    private String LoginID = "";

    private String ETag = "";
    public String getETag() {
        return ETag;
    }

    public void setETag(String ETag) {
        this.ETag = ETag;
    }

    public String getActivityRefID() {
        return ActivityRefID;
    }

    public void setActivityRefID(String activityRefID) {
        ActivityRefID = activityRefID;
    }

    public String getVisitActivityGUID() {
        return VisitActivityGUID;
    }

    public void setVisitActivityGUID(String visitActivityGUID) {
        VisitActivityGUID = visitActivityGUID;
    }

    public String getVisitGUID() {
        return VisitGUID;
    }

    public void setVisitGUID(String visitGUID) {
        VisitGUID = visitGUID;
    }

    public String getActivityType() {
        return ActivityType;
    }

    public void setActivityType(String activityType) {
        ActivityType = activityType;
    }

    public String getActivityTypeDesc() {
        return ActivityTypeDesc;
    }

    public void setActivityTypeDesc(String activityTypeDesc) {
        ActivityTypeDesc = activityTypeDesc;
    }

    private String ActivityRefID = "";
}
