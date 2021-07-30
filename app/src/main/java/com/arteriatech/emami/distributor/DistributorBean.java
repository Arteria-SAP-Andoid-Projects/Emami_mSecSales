package com.arteriatech.emami.distributor;

public class DistributorBean {
    private String cPGUID = "";
    private String cPName = "";
    private String cPTypeID = "";
    private String sPGUID = "";

    public String getcPNo() {
        return cPNo;
    }

    public void setcPNo(String cPNo) {
        this.cPNo = cPNo;
    }

    private String cPNo = "";

    public String getcPGUID() {
        return cPGUID;
    }

    public void setcPGUID(String cPGUID) {
        this.cPGUID = cPGUID;
    }

    public String getcPName() {
        return cPName;
    }

    public void setcPName(String cPName) {
        this.cPName = cPName;
    }

    public String getcPTypeID() {
        return cPTypeID;
    }

    public void setcPTypeID(String cPTypeID) {
        this.cPTypeID = cPTypeID;
    }

    public String getsPGUID() {
        return sPGUID;
    }

    public void setsPGUID(String sPGUID) {
        this.sPGUID = sPGUID;
    }

    @Override
    public String toString() {
        return cPName + " - " + cPNo;
    }
}
