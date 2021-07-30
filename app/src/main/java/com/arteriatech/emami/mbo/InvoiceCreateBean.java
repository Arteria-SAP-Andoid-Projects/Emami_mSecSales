package com.arteriatech.emami.mbo;

import java.io.Serializable;

/**
 * Created by e10526 on 06-07-2018.
 */

public class InvoiceCreateBean implements Serializable {
    String CPNo = "";
    String PaymentModeID = "";
    String CPGUID32 = "";
    String ParentTypeID = "";
    String ParentName = "";
    String comingFrom = "";
    String ParentID = "";
    String SpNo = "";
    String SpFirstName = "";
    String RouteSchGuid = "";
    String PODate = "";

    public String getDeliveryPerson() {
        return DeliveryPerson;
    }

    public void setDeliveryPerson(String deliveryPerson) {
        DeliveryPerson = deliveryPerson;
    }

    String DeliveryPerson = "";

    public String getVehicleNo() {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        VehicleNo = vehicleNo;
    }

    String VehicleNo = "";

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getDriverMobile() {
        return DriverMobile;
    }

    public void setDriverMobile(String driverMobile) {
        DriverMobile = driverMobile;
    }

    String DriverName = "";
    String DriverMobile = "";

    public String getPODate() {
        return PODate;
    }

    public void setPODate(String PODate) {
        this.PODate = PODate;
    }

    public String getPONo() {
        return PONo;
    }

    public void setPONo(String PONo) {
        this.PONo = PONo;
    }

    String PONo = "";

    public String getSSSoGuid() {
        return SSSoGuid;
    }

    public void setSSSoGuid(String SSSoGuid) {
        this.SSSoGuid = SSSoGuid;
    }

    String SSSoGuid = "";
    String DeviceNo = "";
    String cpUID = "";

    public DmsDivQryBean getDmsDivQryBean() {
        return dmsDivQryBean;
    }

    public void setDmsDivQryBean(DmsDivQryBean dmsDivQryBean) {
        this.dmsDivQryBean = dmsDivQryBean;
    }

    DmsDivQryBean dmsDivQryBean = null;

    public String getDmsDivision() {
        return DmsDivision;
    }

    public void setDmsDivision(String dmsDivision) {
        DmsDivision = dmsDivision;
    }

    public String getDmsDivisionDesc() {
        return DmsDivisionDesc;
    }

    public void setDmsDivisionDesc(String dmsDivisionDesc) {
        DmsDivisionDesc = dmsDivisionDesc;
    }

    String DmsDivision = "";
    String DmsDivisionDesc = "";

    public String getMatQry() {
        return matQry;
    }

    public void setMatQry(String matQry) {
        this.matQry = matQry;
    }

    String matQry = "";

    public String getDelDate() {
        return DelDate;
    }

    public void setDelDate(String delDate) {
        DelDate = delDate;
    }

    String DelDate = "";

    public String getComingFrom() {
        return comingFrom;
    }

    public void setComingFrom(String comingFrom) {
        this.comingFrom = comingFrom;
    }

    public String getCpUID() {
        return cpUID;
    }

    public void setCpUID(String cpUID) {
        this.cpUID = cpUID;
    }

    public String getDeviceNo() {
        return DeviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        DeviceNo = deviceNo;
    }


    public String getParentName() {
        return ParentName;
    }

    public void setParentName(String parentName) {
        ParentName = parentName;
    }

    public String getParentTypeID() {
        return ParentTypeID;
    }

    public void setParentTypeID(String parentTypeID) {
        ParentTypeID = parentTypeID;
    }

    public String getRouteSchGuid() {
        return RouteSchGuid;
    }

    public void setRouteSchGuid(String routeSchGuid) {
        RouteSchGuid = routeSchGuid;
    }

    public String getCPGUID32() {
        return CPGUID32;
    }

    public void setCPGUID32(String CPGUID32) {
        this.CPGUID32 = CPGUID32;
    }


    public String getBeatGuid() {
        return BeatGuid;
    }

    public void setBeatGuid(String beatGuid) {
        BeatGuid = beatGuid;
    }

    String BeatGuid = "";

    public String getCPName() {
        return CPName;
    }

    public void setCPName(String CPName) {
        this.CPName = CPName;
    }

    String CPName = "";


    public String getCPTypeID() {
        return CPTypeID;
    }

    public void setCPTypeID(String CPTypeID) {
        this.CPTypeID = CPTypeID;
    }

    String CPTypeID = "";


    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    String Currency = "";


    public String getParentID() {
        return ParentID;
    }

    public void setParentID(String parentID) {
        ParentID = parentID;
    }

    public String getSpNo() {
        return SpNo;
    }

    public void setSpNo(String spNo) {
        SpNo = spNo;
    }

    public String getSpFirstName() {
        return SpFirstName;
    }

    public void setSpFirstName(String spFirstName) {
        SpFirstName = spFirstName;
    }


    public String getPaymentModeDesc() {
        return PaymentModeDesc;
    }

    public void setPaymentModeDesc(String paymentModeDesc) {
        PaymentModeDesc = paymentModeDesc;
    }

    String PaymentModeDesc = "";

    public String getSPGUID() {
        return SPGUID;
    }

    public void setSPGUID(String SPGUID) {
        this.SPGUID = SPGUID;
    }

    String SPGUID = "";

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    String CPGUID = "";

    public String getCPNo() {
        return CPNo;
    }

    public void setCPNo(String CPNo) {
        this.CPNo = CPNo;
    }


    public String getPaymentModeID() {
        return PaymentModeID;
    }

    public void setPaymentModeID(String paymentModeID) {
        PaymentModeID = paymentModeID;
    }


}
