package com.arteriatech.emami.mbo;

import java.io.Serializable;

/**
 * Created by e10526 on 11/22/2017.
 */

public class DmsDivQryBean implements Serializable {
    private String DMSDivisionQry = "";
    private String CVGValueQry = "";
    private String DMSDivisionSSInvQry = "";

    public String getDMSDivisionQry() {
        return DMSDivisionQry;
    }

    public void setDMSDivisionQry(String DMSDivisionQry) {
        this.DMSDivisionQry = DMSDivisionQry;
    }

    public String getCVGValueQry() {
        return CVGValueQry;
    }

    public void setCVGValueQry(String CVGValueQry) {
        this.CVGValueQry = CVGValueQry;
    }

    public String getDMSDivisionSSInvQry() {
        return DMSDivisionSSInvQry;
    }

    public void setDMSDivisionSSInvQry(String DMSDivisionSSInvQry) {
        this.DMSDivisionSSInvQry = DMSDivisionSSInvQry;
    }

    public String getDMSDivisionIDQry() {
        return DMSDivisionIDQry;
    }

    public void setDMSDivisionIDQry(String DMSDivisionIDQry) {
        this.DMSDivisionIDQry = DMSDivisionIDQry;
    }

    private String DMSDivisionIDQry = "";

}
