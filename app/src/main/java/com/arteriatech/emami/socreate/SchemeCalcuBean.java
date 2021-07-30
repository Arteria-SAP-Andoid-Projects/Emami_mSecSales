package com.arteriatech.emami.socreate;

import com.arteriatech.emami.mbo.SchemeBean;

/**
 * Created by e10769 on 10-08-2017.
 */

public class SchemeCalcuBean {
    private boolean IsSchemeFreeQty = false;
    private SchemeBean mFreeMat =null;
    private String schemeGuidNo = "";
    private Double mDouSecDiscount =0.0;
    private Double mDouSecDiscountAmt =0.0;
    private Double mDouSecAmt =0.0;
    private String secondarySchemeAmt ="";

    public boolean isSchemeFreeQty() {
        return IsSchemeFreeQty;
    }

    public void setSchemeFreeQty(boolean schemeFreeQty) {
        IsSchemeFreeQty = schemeFreeQty;
    }

    public SchemeBean getmFreeMat() {
        return mFreeMat;
    }

    public void setmFreeMat(SchemeBean mFreeMat) {
        this.mFreeMat = mFreeMat;
    }

    public String getSchemeGuidNo() {
        return schemeGuidNo;
    }

    public void setSchemeGuidNo(String schemeGuidNo) {
        this.schemeGuidNo = schemeGuidNo;
    }

    public Double getmDouSecDiscount() {
        return mDouSecDiscount;
    }

    public void setmDouSecDiscount(Double mDouSecDiscount) {
        this.mDouSecDiscount = mDouSecDiscount;
    }

    public Double getmDouSecAmt() {
        return mDouSecAmt;
    }

    public void setmDouSecAmt(Double mDouSecAmt) {
        this.mDouSecAmt = mDouSecAmt;
    }

    public String getSecondarySchemeAmt() {
        return secondarySchemeAmt;
    }

    public void setSecondarySchemeAmt(String secondarySchemeAmt) {
        this.secondarySchemeAmt = secondarySchemeAmt;
    }

    public Double getmDouSecDiscountAmt() {
        return mDouSecDiscountAmt;
    }

    public void setmDouSecDiscountAmt(Double mDouSecDiscountAmt) {
        this.mDouSecDiscountAmt = mDouSecDiscountAmt;
    }
}
