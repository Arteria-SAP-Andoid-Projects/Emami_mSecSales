package com.arteriatech.emami.visit;

/**
 * Created by e10604 on 9/7/2016.
 */
public class VisitActivityBean {
    private String VisitActivityGUID = "";
    private String VisitGUID = "";
    private String ActivityType = "";
    private String ActivityTypeDesc = "";

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
