package com.arteriatech.emami.mbo;

/**
 * Created by e10762 on 23-12-2016.
 */

public class RoutePlanBean {

    public String getRschGuid() {
        return RschGuid;
    }

    public void setRschGuid(String rschGuid) {
        RschGuid = rschGuid;
    }

    public String getDOM() {
        return DOM;
    }

    public void setDOM(String DOM) {
        this.DOM = DOM;
    }

    public String getDOW() {
        return DOW;
    }

    public void setDOW(String DOW) {
        this.DOW = DOW;
    }

    private String RschGuid = "";
    private String DOM = "";
    private String DOW = "";

}
