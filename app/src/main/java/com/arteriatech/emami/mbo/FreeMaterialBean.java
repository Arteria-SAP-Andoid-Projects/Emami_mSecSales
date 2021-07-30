package com.arteriatech.emami.mbo;

import com.sap.smp.client.odata.ODataEntity;

/**
 * Created by e10526 on 8/1/2017.
 */

public class FreeMaterialBean {
    private String IntermUnitPrice = "";
    private String UnitPrice = "";
    private String MRP = "";
    private String Quantity = "";
    private String MaterialNo = "";
    private ODataEntity oDataEntity = null;

    public ODataEntity getoDataEntity() {
        return oDataEntity;
    }

    public void setoDataEntity(ODataEntity oDataEntity) {
        this.oDataEntity = oDataEntity;
    }


    public String getIntermUnitPrice() {
        return IntermUnitPrice;
    }

    public void setIntermUnitPrice(String intermUnitPrice) {
        IntermUnitPrice = intermUnitPrice;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }


}
