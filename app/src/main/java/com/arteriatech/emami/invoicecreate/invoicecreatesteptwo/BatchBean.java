package com.arteriatech.emami.invoicecreate.invoicecreatesteptwo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BatchBean implements Parcelable {
    String MaterialNo = "";
    String StockGUID = "";

    public BatchBean(){

    }

    protected BatchBean(Parcel in) {
        MaterialNo = in.readString();
        StockGUID = in.readString();
        UOM = in.readString();
        StockSnoGUID = in.readString();
        MRP = in.readString();
        UnResQty = in.readString();
        displayData = in.readString();
        BatchNo = in.readString();
        BatchNoDesc = in.readString();
        arrayListBatchItem = in.createTypedArrayList(BatchBean.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(MaterialNo);
        dest.writeString(StockGUID);
        dest.writeString(UOM);
        dest.writeString(StockSnoGUID);
        dest.writeString(MRP);
        dest.writeString(UnResQty);
        dest.writeString(displayData);
        dest.writeString(BatchNo);
        dest.writeString(BatchNoDesc);
        dest.writeTypedList(arrayListBatchItem);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BatchBean> CREATOR = new Creator<BatchBean>() {
        @Override
        public BatchBean createFromParcel(Parcel in) {
            return new BatchBean(in);
        }

        @Override
        public BatchBean[] newArray(int size) {
            return new BatchBean[size];
        }
    };

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    String UOM = "";

    public String getStockSnoGUID() {
        return StockSnoGUID;
    }

    public void setStockSnoGUID(String stockSnoGUID) {
        StockSnoGUID = stockSnoGUID;
    }

    String StockSnoGUID = "";

    public String getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP = MRP;
    }

    String MRP = "";

    public String getUnResQty() {
        return UnResQty;
    }

    public void setUnResQty(String unResQty) {
        UnResQty = unResQty;
    }

    String UnResQty = "";

    public String getDisplayData() {
        return displayData;
    }

    public void setDisplayData(String displayData) {
        this.displayData = displayData;
    }

    private String displayData="";

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

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    String BatchNo = "";

    public String getBatchNoDesc() {
        return BatchNoDesc;
    }

    public void setBatchNoDesc(String batchNoDesc) {
        BatchNoDesc = batchNoDesc;
    }

    String BatchNoDesc = "";
    @Override
    public String toString() {
        return displayData.toString();
    }

    public ArrayList<BatchBean> getArrayListBatchItem() {
        return arrayListBatchItem;
    }

    public void setArrayListBatchItem(ArrayList<BatchBean> arrayListBatchItem) {
        this.arrayListBatchItem = arrayListBatchItem;
    }

    ArrayList<BatchBean> arrayListBatchItem = null;
}
