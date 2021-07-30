package com.arteriatech.emami.mbo;

/**
 * Created by e10526 on 9/16/2017.
 */

public class CPStockSNoBean {
    private String IntermUnitPrice = "";
    private String Quantity = "";
    private String Batch = "";
    private String ManufacturingDate = "";
    private String PriDiscountPer = "";

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    private String MaterialNo = "";

    public String getIntermUnitPrice() {
        return IntermUnitPrice;
    }

    public void setIntermUnitPrice(String intermUnitPrice) {
        IntermUnitPrice = intermUnitPrice;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String batch) {
        Batch = batch;
    }

    public String getManufacturingDate() {
        return ManufacturingDate;
    }

    public void setManufacturingDate(String manufacturingDate) {
        ManufacturingDate = manufacturingDate;
    }

    public String getPriDiscountPer() {
        return PriDiscountPer;
    }

    public void setPriDiscountPer(String priDiscountPer) {
        PriDiscountPer = priDiscountPer;
    }


}
