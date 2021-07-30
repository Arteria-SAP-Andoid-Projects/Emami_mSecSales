package com.arteriatech.emami.returnOrder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by e10762 on 16-02-2017.
 */

public class ReturnOrderBean implements Parcelable
{
    private  String CPStockItemGUID="";
    private String MaterialDesc="";
    private String MaterialNo="";
    private String QAQty="";
    private String BlockedQty="";
    private String StockValue="";
    private String LandingPrice="";
    private String OrderMaterialGroupID="";
    private String OrderMaterialGroupDesc="";
    private String OrderNo;
    private String OrderDate;
    private String SSROGUID;
    private String NetAmount;
    private String StatusID;
    private Boolean IsDetailEnabled=false;
    private String sItemNo;
    private String deviceNo;
    private String Brand="";
    private String ProductCategoryID="";
    private String Distance="";

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getTempStatus() {
        return TempStatus;
    }

    public void setTempStatus(String tempStatus) {
        TempStatus = tempStatus;
    }

    private String TempStatus="";

    public String getSSSOItemGUID() {
        return SSSOItemGUID;
    }

    public void setSSSOItemGUID(String SSSOItemGUID) {
        this.SSSOItemGUID = SSSOItemGUID;
    }

    private String SSSOItemGUID="";




    private Boolean isSelected = false;
    private Boolean isDisplayed  = false;
    private int itemNo = 0;
    private String CPGUID="";
    private String  SerialNoFrom="";
    private String Currency="";
    private String Batch = "";
    private String returnQty = "";
    private String returnDesc = "";

    public String getReturnQty() {
        return returnQty;
    }

    public void setReturnQty(String returnQty) {
        this.returnQty = returnQty;
    }

    public String getReturnMrp() {
        return returnMrp;
    }

    public void setReturnMrp(String returnMrp) {
        this.returnMrp = returnMrp;
    }

    public String getReturnBatchNumber() {
        return returnBatchNumber;
    }

    public void setReturnBatchNumber(String returnBatchNumber) {
        this.returnBatchNumber = returnBatchNumber;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public Boolean getItemToReturn() {
        return isItemToReturn;
    }

    public void setItemToReturn(Boolean itemToReturn) {
        isItemToReturn = itemToReturn;
    }

    private String returnMrp = "";
    private String returnBatchNumber = "";
    private String returnReason = "";
    private Boolean isItemToReturn = false;

    public String getLandingPrice() {
        return LandingPrice;
    }

    public void setLandingPrice(String landingPrice) {
        LandingPrice = landingPrice;
    }

    public String getOrderMaterialGroupID() {
        return OrderMaterialGroupID;
    }

    public void setOrderMaterialGroupID(String orderMaterialGroupID) {
        OrderMaterialGroupID = orderMaterialGroupID;
    }

    public String getOrderMaterialGroupDesc() {
        return OrderMaterialGroupDesc;
    }

    public void setOrderMaterialGroupDesc(String orderMaterialGroupDesc) {
        OrderMaterialGroupDesc = orderMaterialGroupDesc;
    }



    public String getMFD() {
        return MFD;
    }

    public void setMFD(String MFD) {
        this.MFD = MFD;
    }

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String batch) {
        Batch = batch;
    }

    private String MFD = "";




    private String CrsSksGroup;

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public String getRLPrice() {
        return RLPrice;
    }

    public void setRLPrice(String RLPrice) {
        this.RLPrice = RLPrice;
    }

    String MRP="";
    String RLPrice="";

    public String getUnrestrictedQty() {
        return UnrestrictedQty;
    }

    public void setUnrestrictedQty(String unrestrictedQty) {
        UnrestrictedQty = unrestrictedQty;
    }

    String UnrestrictedQty="";

    public String getUom() {
        return Uom;
    }

    public void setUom(String uom) {
        Uom = uom;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    String Uom = "";



    public String getSerialNoTo() {
        return SerialNoTo;
    }

    public void setSerialNoTo(String serialNoTo) {
        SerialNoTo = serialNoTo;
    }

    public String getCPStockItemGUID() {
        return CPStockItemGUID;
    }

    public void setCPStockItemGUID(String CPStockItemGUID) {
        this.CPStockItemGUID = CPStockItemGUID;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getQAQty() {
        return QAQty;
    }

    public void setQAQty(String QAQty) {
        this.QAQty = QAQty;
    }

    public String getBlockedQty() {
        return BlockedQty;
    }

    public void setBlockedQty(String blockedQty) {
        BlockedQty = blockedQty;
    }

    public String getStockValue() {
        return StockValue;
    }

    public void setStockValue(String stockValue) {
        StockValue = stockValue;
    }

    public String getCPGUID() {
        return CPGUID;
    }

    public void setCPGUID(String CPGUID) {
        this.CPGUID = CPGUID;
    }

    public String getSerialNoFrom() {
        return SerialNoFrom;
    }

    public void setSerialNoFrom(String serialNoFrom) {
        SerialNoFrom = serialNoFrom;
    }

    String  SerialNoTo="";

    public String getCrsSksGroup() {
        return CrsSksGroup;
    }

    public void setCrsSksGroup(String crsSksGroup) {
        CrsSksGroup = crsSksGroup;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i)
    {
        dest.writeString(MaterialNo);
        dest.writeString(MaterialDesc);
        dest.writeString(OrderMaterialGroupID);
        dest.writeString(Uom);
        dest.writeString(returnQty);
        dest.writeString(returnBatchNumber);
        dest.writeString(returnMrp);
        dest.writeString(returnReason);
        dest.writeString(returnDesc);


    }
    public ReturnOrderBean()
    {

    }
    public ReturnOrderBean(Parcel in)
    {
        this.MaterialNo = in.readString();
        this.MaterialDesc = in.readString();
        this.OrderMaterialGroupID = in.readString();
        this.Uom = in.readString();
        this.returnQty = in.readString();
        this.returnBatchNumber = in.readString();
        this.returnMrp = in.readString();
        this.returnReason = in.readString();
        this.returnDesc = in.readString();

    }
    public static final Parcelable.Creator<ReturnOrderBean> CREATOR = new Parcelable.Creator<ReturnOrderBean>() {

        @Override
        public ReturnOrderBean createFromParcel(Parcel source) {
            return new ReturnOrderBean(source);
        }

        @Override
        public ReturnOrderBean[] newArray(int size) {
            return new ReturnOrderBean[size];
        }
    };

    public String getReturnDesc() {
        return returnDesc;
    }

    public void setReturnDesc(String returnDesc) {
        this.returnDesc = returnDesc;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Boolean getDisplayed() {
        return isDisplayed;
    }

    public void setDisplayed(Boolean displayed) {
        isDisplayed = displayed;
    }

    public int getItemNo() {
        return itemNo;
    }

    public void setItemNo(int itemNo) {
        this.itemNo = itemNo;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getSSROGUID() {
        return SSROGUID;
    }

    public void setSSROGUID(String SSROGUID) {
        this.SSROGUID = SSROGUID;
    }

    public String getNetAmount() {
        return NetAmount;
    }

    public void setNetAmount(String netAmount) {
        NetAmount = netAmount;
    }

    public String getStatusID() {
        return StatusID;
    }

    public void setStatusID(String statusID) {
        StatusID = statusID;
    }

    public Boolean getDetailEnabled() {
        return IsDetailEnabled;
    }

    public void setDetailEnabled(Boolean detailEnabled) {
        IsDetailEnabled = detailEnabled;
    }
    public String getsItemNo() {
        return sItemNo;
    }

    public void setsItemNo(String sItemNo) {
        this.sItemNo = sItemNo;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getProductCategoryID() {
        return ProductCategoryID;
    }

    public void setProductCategoryID(String productCategoryID) {
        ProductCategoryID = productCategoryID;
    }

}
