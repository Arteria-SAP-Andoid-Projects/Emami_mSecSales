package com.arteriatech.emami.mbo;

/**
 * Created by e10526 on 2/4/2017.
 */

public class VisitSummaryBean {

    private String RetailerName = "";
    private String TimeTaken = "";
    private String OrderValue = "";
    private String DayTarget = "";
    private String TodayTlsd = "";
    private String TlsdTilldate = "";
    private String MonthTarget = "";
    private String AchMTD = "";
    private String MTDPer = "";

    public String getMTDPer() {
        return MTDPer;
    }

    public void setMTDPer(String MTDPer) {
        this.MTDPer = MTDPer;
    }

    public String getRetailerName() {
        return RetailerName;
    }

    public void setRetailerName(String retailerName) {
        RetailerName = retailerName;
    }

    public String getTimeTaken() {
        return TimeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        TimeTaken = timeTaken;
    }

    public String getOrderValue() {
        return OrderValue;
    }

    public void setOrderValue(String orderValue) {
        OrderValue = orderValue;
    }

    public String getDayTarget() {
        return DayTarget;
    }

    public void setDayTarget(String dayTarget) {
        DayTarget = dayTarget;
    }

    public String getTodayTlsd() {
        return TodayTlsd;
    }

    public void setTodayTlsd(String todayTlsd) {
        TodayTlsd = todayTlsd;
    }

    public String getTlsdTilldate() {
        return TlsdTilldate;
    }

    public void setTlsdTilldate(String tlsdTilldate) {
        TlsdTilldate = tlsdTilldate;
    }

    public String getMonthTarget() {
        return MonthTarget;
    }

    public void setMonthTarget(String monthTarget) {
        MonthTarget = monthTarget;
    }

    public String getAchMTD() {
        return AchMTD;
    }

    public void setAchMTD(String achMTD) {
        AchMTD = achMTD;
    }


}
