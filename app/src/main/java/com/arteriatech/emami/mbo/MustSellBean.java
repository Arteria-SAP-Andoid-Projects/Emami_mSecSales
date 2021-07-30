package com.arteriatech.emami.mbo;

/**
 * Created by e10526 on 27-02-2018.
 */

public class MustSellBean {

    private String LoginID = "";
    private String MSLGUID = "";
    private String ParentNo = "";
    private String ParentType = "";
    private String CPNo = "";
    private String CPType = "";
    private String DmsDivision = "";
    private String OrderMatGrp = "";
    private String SOQ = "";
    private String UOM = "";
    private String ValidFrom = "";
    private String ValidTo = "";
    private String MSLInd = "";
    private String FocussedInd = "";
    private String CrossSell = "";

    public String getSellIndicator() {
        return SellIndicator;
    }

    public void setSellIndicator(String sellIndicator) {
        SellIndicator = sellIndicator;
    }

    private String SellIndicator = "";

    public String getLoginID() {
        return LoginID;
    }

    public void setLoginID(String loginID) {
        LoginID = loginID;
    }

    public String getMSLGUID() {
        return MSLGUID;
    }

    public void setMSLGUID(String MSLGUID) {
        this.MSLGUID = MSLGUID;
    }

    public String getParentNo() {
        return ParentNo;
    }

    public void setParentNo(String parentNo) {
        ParentNo = parentNo;
    }

    public String getParentType() {
        return ParentType;
    }

    public void setParentType(String parentType) {
        ParentType = parentType;
    }

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }

    public String getCPType() {
        return CPType;
    }

    public void setCPType(String CPType) {
        this.CPType = CPType;
    }

    public String getDmsDivision() {
        return DmsDivision;
    }

    public void setDmsDivision(String dmsDivision) {
        DmsDivision = dmsDivision;
    }

    public String getOrderMatGrp() {
        return OrderMatGrp;
    }

    public void setOrderMatGrp(String orderMatGrp) {
        OrderMatGrp = orderMatGrp;
    }

    public String getSOQ() {
        return SOQ;
    }

    public void setSOQ(String SOQ) {
        this.SOQ = SOQ;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getValidFrom() {
        return ValidFrom;
    }

    public void setValidFrom(String validFrom) {
        ValidFrom = validFrom;
    }

    public String getValidTo() {
        return ValidTo;
    }

    public void setValidTo(String validTo) {
        ValidTo = validTo;
    }

    public String getMSLInd() {
        return MSLInd;
    }

    public void setMSLInd(String MSLInd) {
        this.MSLInd = MSLInd;
    }

    public String getFocussedInd() {
        return FocussedInd;
    }

    public void setFocussedInd(String focussedInd) {
        FocussedInd = focussedInd;
    }

    public String getCrossSell() {
        return CrossSell;
    }

    public void setCrossSell(String crossSell) {
        CrossSell = crossSell;
    }

    public String getUPSell() {
        return UPSell;
    }

    public void setUPSell(String UPSell) {
        this.UPSell = UPSell;
    }

    private String UPSell = "";

}
