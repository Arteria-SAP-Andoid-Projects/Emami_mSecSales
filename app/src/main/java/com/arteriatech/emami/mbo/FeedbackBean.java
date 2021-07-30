package com.arteriatech.emami.mbo;

/**
 * Created by ${e10526} on ${11-07-2016}.
 */
public class FeedbackBean {
    private String FeedbackNo = "";
    private String Location1 = "";
    private String FeedbackTypeDesc = "";
    private String FeedbackType = "";
    private String Remarks = "";
    private String BTSID = "";
    private String FeebackGUID = "";
    private String DeviceStatus = "";
    private String DeviceNo = "";

    public String getDeviceStatus() {
        return DeviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        DeviceStatus = deviceStatus;
    }

    public String getDeviceNo() {
        return DeviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        DeviceNo = deviceNo;
    }


    public String getFeebackGUID() {
        return FeebackGUID;
    }

    public void setFeebackGUID(String feebackGUID) {
        FeebackGUID = feebackGUID;
    }


    public String getBTSID() {
        return BTSID;
    }

    public void setBTSID(String BTSID) {
        this.BTSID = BTSID;
    }

    public String getFeedbackNo() {
        return FeedbackNo;
    }

    public void setFeedbackNo(String feedbackNo) {
        FeedbackNo = feedbackNo;
    }

    public String getLocation1() {
        return Location1;
    }

    public void setLocation1(String location1) {
        Location1 = location1;
    }

    public String getFeedbackTypeDesc() {
        return FeedbackTypeDesc;
    }

    public void setFeedbackTypeDesc(String feedbackTypeDesc) {
        FeedbackTypeDesc = feedbackTypeDesc;
    }

    public String getFeedbackType() {
        return FeedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        FeedbackType = feedbackType;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }


}
