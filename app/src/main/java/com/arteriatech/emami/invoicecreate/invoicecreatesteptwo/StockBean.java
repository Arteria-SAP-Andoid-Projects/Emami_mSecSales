package com.arteriatech.emami.invoicecreate.invoicecreatesteptwo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class StockBean implements Parcelable {
    String MaterialNo = "";
    private Boolean isSelected = false;

    public StockBean(){

    }


    protected StockBean(Parcel in) {
        MaterialNo = in.readString();
        byte tmpIsSelected = in.readByte();
        isSelected = tmpIsSelected == 0 ? null : tmpIsSelected == 1;
        beanPosition = in.readInt();
        SelctedBatch = in.readParcelable(BatchBean.class.getClassLoader());
        EnterdQTY = in.readString();
        CashDisc = in.readString();
        UOM = in.readString();
        MaterialDESC = in.readString();
        StockGUID = in.readString();
        StockQTY = in.readString();
        MRP = in.readString();
        SelectedBatchNo = in.readString();
        SelectedStockGuid = in.readString();
        arrayListBatchItem = in.createTypedArrayList(BatchBean.CREATOR);
        alMaterals = in.createTypedArrayList(StockBean.CREATOR);
        ProductCategoryID = in.readString();
        ProductCategoryDesc = in.readString();
        OrderMaterialGroupID = in.readString();
        OrderMaterialGroupDesc = in.readString();
        SKUGroup = in.readString();
        SKUGroupDesc = in.readString();
        Banner = in.readString();
        BannerDesc = in.readString();
        Brand = in.readString();
        BrandDesc = in.readString();
        DMSDivision = in.readString();
        SSSoItemGUID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(MaterialNo);
        dest.writeByte((byte) (isSelected == null ? 0 : isSelected ? 1 : 2));
        dest.writeInt(beanPosition);
        dest.writeParcelable(SelctedBatch, flags);
        dest.writeString(EnterdQTY);
        dest.writeString(CashDisc);
        dest.writeString(UOM);
        dest.writeString(MaterialDESC);
        dest.writeString(StockGUID);
        dest.writeString(StockQTY);
        dest.writeString(MRP);
        dest.writeString(SelectedBatchNo);
        dest.writeString(SelectedStockGuid);
        dest.writeTypedList(arrayListBatchItem);
        dest.writeTypedList(alMaterals);
        dest.writeString(ProductCategoryID);
        dest.writeString(ProductCategoryDesc);
        dest.writeString(OrderMaterialGroupID);
        dest.writeString(OrderMaterialGroupDesc);
        dest.writeString(SKUGroup);
        dest.writeString(SKUGroupDesc);
        dest.writeString(Banner);
        dest.writeString(BannerDesc);
        dest.writeString(Brand);
        dest.writeString(BrandDesc);
        dest.writeString(DMSDivision);
        dest.writeString(SSSoItemGUID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StockBean> CREATOR = new Creator<StockBean>() {
        @Override
        public StockBean createFromParcel(Parcel in) {
            return new StockBean(in);
        }

        @Override
        public StockBean[] newArray(int size) {
            return new StockBean[size];
        }
    };

    public int getBeanPosition() {
        return beanPosition;
    }

    public void setBeanPosition(int beanPosition) {
        this.beanPosition = beanPosition;
    }

    int beanPosition = 0;

    public BatchBean getSelctedBatch() {
        return SelctedBatch;
    }

    public void setSelctedBatch(BatchBean selctedBatch) {
        SelctedBatch = selctedBatch;
    }

    private BatchBean SelctedBatch =null;

    public String getEnterdQTY() {
        return EnterdQTY;
    }

    public void setEnterdQTY(String enterdQTY) {
        EnterdQTY = enterdQTY;
    }

    String EnterdQTY = "";

    public String getCashDisc() {
        return CashDisc;
    }

    public void setCashDisc(String cashDisc) {
        CashDisc = cashDisc;
    }

    String CashDisc = "";

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    String UOM = "";

    public String getMaterialDESC() {
        return MaterialDESC;
    }

    public void setMaterialDESC(String materialDESC) {
        MaterialDESC = materialDESC;
    }

    String MaterialDESC = "";
    String StockGUID = "";
    String StockQTY = "";
    String MRP = "";
    String SelectedBatchNo = "";

    public String getSelectedBatchNo() {
        return SelectedBatchNo;
    }

    public void setSelectedBatchNo(String selectedBatchNo) {
        SelectedBatchNo = selectedBatchNo;
    }

    public String getSelectedStockGuid() {
        return SelectedStockGuid;
    }

    public void setSelectedStockGuid(String selectedStockGuid) {
        SelectedStockGuid = selectedStockGuid;
    }

    String SelectedStockGuid = "";

    public String getMaterialNo() {
        return MaterialNo;
    }

    public void setMaterialNo(String materialNo) {
        MaterialNo = materialNo;
    }

    public String getStockGUID() {
        return StockGUID;
    }

    public void setStockGUID(String stockGUID) {
        StockGUID = stockGUID;
    }

    public String getStockQTY() {
        return StockQTY;
    }

    public void setStockQTY(String stockQTY) {
        StockQTY = stockQTY;
    }

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    public ArrayList<BatchBean> getArrayListBatchItem() {
        return arrayListBatchItem;
    }

    public void setArrayListBatchItem(ArrayList<BatchBean> arrayListBatchItem) {
        this.arrayListBatchItem = arrayListBatchItem;
    }

    ArrayList<BatchBean> arrayListBatchItem = null;





    public ArrayList<StockBean> getAlMaterals() {
        return alMaterals;
    }

    public void setAlMaterals(ArrayList<StockBean> alMaterals) {
        this.alMaterals = alMaterals;
    }

    private ArrayList<StockBean> alMaterals =null;

    public String getProductCategoryID() {
        return ProductCategoryID;
    }

    public void setProductCategoryID(String productCategoryID) {
        ProductCategoryID = productCategoryID;
    }

    public String getProductCategoryDesc() {
        return ProductCategoryDesc;
    }

    public void setProductCategoryDesc(String productCategoryDesc) {
        ProductCategoryDesc = productCategoryDesc;
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

    public String getSKUGroup() {
        return SKUGroup;
    }

    public void setSKUGroup(String SKUGroup) {
        this.SKUGroup = SKUGroup;
    }

    public String getSKUGroupDesc() {
        return SKUGroupDesc;
    }

    public void setSKUGroupDesc(String SKUGroupDesc) {
        this.SKUGroupDesc = SKUGroupDesc;
    }

    public String getBanner() {
        return Banner;
    }

    public void setBanner(String banner) {
        Banner = banner;
    }

    public String getBannerDesc() {
        return BannerDesc;
    }

    public void setBannerDesc(String bannerDesc) {
        BannerDesc = bannerDesc;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getBrandDesc() {
        return BrandDesc;
    }

    public void setBrandDesc(String brandDesc) {
        BrandDesc = brandDesc;
    }

    public String getDMSDivision() {
        return DMSDivision;
    }

    public void setDMSDivision(String DMSDivision) {
        this.DMSDivision = DMSDivision;
    }

    private String ProductCategoryID = "";
    private String ProductCategoryDesc = "";
    private String OrderMaterialGroupID = "";
    private String OrderMaterialGroupDesc = "";
    private String SKUGroup = "";
    private String SKUGroupDesc = "";
    private String Banner = "";
    private String BannerDesc = "";
    private String Brand = "";
    private String BrandDesc = "";
    private String DMSDivision = "";

    public String getSSSoItemGUID() {
        return SSSoItemGUID;
    }

    public void setSSSoItemGUID(String SSSoItemGUID) {
        this.SSSoItemGUID = SSSoItemGUID;
    }

    private String SSSoItemGUID = "";
    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

}
