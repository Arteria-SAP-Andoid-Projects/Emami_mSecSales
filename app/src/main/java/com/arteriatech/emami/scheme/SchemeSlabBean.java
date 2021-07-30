package com.arteriatech.emami.scheme;

/**
 * Created by e10769 on 28-03-2017.
 */

public class SchemeSlabBean {
    private String FromQty = "";
    private String ToQty = "";
    private String PayoutAmount = "";
    private String PayoutPerc = "";
    private String FreeQty = "";
    private String MaterialDesc = "";

    public String getFromQty() {
        return FromQty;
    }

    public void setFromQty(String fromQty) {
        FromQty = fromQty;
    }

    public String getToQty() {
        return ToQty;
    }

    public void setToQty(String toQty) {
        ToQty = toQty;
    }

    public String getPayoutAmount() {
        return PayoutAmount;
    }

    public void setPayoutAmount(String payoutAmount) {
        PayoutAmount = payoutAmount;
    }

    public String getPayoutPerc() {
        return PayoutPerc;
    }

    public void setPayoutPerc(String payoutPerc) {
        PayoutPerc = payoutPerc;
    }

    public String getFreeQty() {
        return FreeQty;
    }

    public void setFreeQty(String freeQty) {
        FreeQty = freeQty;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }
}
