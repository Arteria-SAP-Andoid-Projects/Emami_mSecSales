package com.arteriatech.emami.mbo;

import java.io.Serializable;

public class CPPartnerFunctionsBean implements Serializable {
    private String CPGUID = "";
    private String PFGUID = "";
    private String PartnerFunction = "";
    private String PartnerFunctionDesc = "";
    private String CPNo = "";
    private String CPName = "";
    private String CPMobileNo = "";
    private String PartnerCPNo = "";
    private String PartnarName = "";
    private String PartnerMobileNo = "";

    public String getPartnarCPGUID() {
        return PartnarCPGUID;
    }

    public void setPartnarCPGUID(String partnarCPGUID) {
        PartnarCPGUID = partnarCPGUID;
    }

    private String PartnarCPGUID = "";

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    public String getPFGUID() {
        return PFGUID;
    }

    public void setPFGUID(String PFGUID) {
        this.PFGUID = PFGUID;
    }

    public String getPartnerFunction() {
        return PartnerFunction;
    }

    public void setPartnerFunction(String partnerFunction) {
        PartnerFunction = partnerFunction;
    }

    public String getPartnerFunctionDesc() {
        return PartnerFunctionDesc;
    }

    public void setPartnerFunctionDesc(String partnerFunctionDesc) {
        PartnerFunctionDesc = partnerFunctionDesc;
    }

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }

    public String getCPName() {
        return CPName;
    }

    public void setCPName(String CPName) {
        this.CPName = CPName;
    }

    public String getCPMobileNo() {
        return CPMobileNo;
    }

    public void setCPMobileNo(String CPMobileNo) {
        this.CPMobileNo = CPMobileNo;
    }

    public String getPartnerCPNo() {
        return PartnerCPNo;
    }

    public void setPartnerCPNo(String partnerCPNo) {
        PartnerCPNo = partnerCPNo;
    }

    public String getPartnarName() {
        return PartnarName;
    }

    public void setPartnarName(String partnarName) {
        PartnarName = partnarName;
    }

    public String getPartnerMobileNo() {
        return PartnerMobileNo;
    }

    public void setPartnerMobileNo(String partnerMobileNo) {
        PartnerMobileNo = partnerMobileNo;
    }

    @Override
    public String toString() {
        return PartnarName;
    }
}
