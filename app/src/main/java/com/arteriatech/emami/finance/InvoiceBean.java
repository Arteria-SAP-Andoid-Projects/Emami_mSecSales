package com.arteriatech.emami.finance;

/**
 * Created by e10604 on 16/4/2016.
 */
public class InvoiceBean {

    String matCode;
    String matDesc;
    String invoiceNo = "";
    String invoiceAmount = "";
    String CPStockItemGUID = "";
    String SPSNoGUID = "";
    String SerialNoFrom = "";
    String SerialNoTo = "";
    String SelectedSerialNoFrom = "";
    String SelectedSerialNoTo = "";
    String OldSPSNoGUID = "";
    String invoiceGUID = "";
    String Zzindicator = "";

    String UnitPrice = "0";

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public String getTaxAmt() {
        return TaxAmt;
    }

    public void setTaxAmt(String taxAmt) {
        TaxAmt = taxAmt;
    }

    public String getNetAmt() {
        return NetAmt;
    }

    public void setNetAmt(String netAmt) {
        NetAmt = netAmt;
    }

    String TaxAmt = "0";
    String NetAmt = "0";

    public String getCollectionAmount() {
        return CollectionAmount;
    }

    public void setCollectionAmount(String collectionAmount) {
        CollectionAmount = collectionAmount;
    }

    String CollectionAmount = "";

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    String Currency = "";

    public String getStockTypeID() {
        return StockTypeID;
    }

    public void setStockTypeID(String stockTypeID) {
        StockTypeID = stockTypeID;
    }

    String StockTypeID = "";

    public String getUnrestrictedQty() {
        return UnrestrictedQty;
    }

    public void setUnrestrictedQty(String unrestrictedQty) {
        UnrestrictedQty = unrestrictedQty;
    }

    String UnrestrictedQty = "";

    public String getInvQty() {
        return invQty;
    }

    public void setInvQty(String invQty) {
        this.invQty = invQty;
    }

    String invQty = "";

    public String getStockValue() {
        return StockValue;
    }

    public void setStockValue(String stockValue) {
        StockValue = stockValue;
    }

    public String getZzindicator() {
        return Zzindicator;
    }

    public void setZzindicator(String zzindicator) {
        Zzindicator = zzindicator;
    }

    String StockValue = "";


    public String getDeviceInvStatus() {
        return DeviceInvStatus;
    }

    public void setDeviceInvStatus(String deviceInvStatus) {
        DeviceInvStatus = deviceInvStatus;
    }

    String DeviceInvStatus = "";

    public String getInvoiceOutstanding() {
        return invoiceOutstanding;
    }

    public void setInvoiceOutstanding(String invoiceOutstanding) {
        this.invoiceOutstanding = invoiceOutstanding;
    }

    String invoiceOutstanding = "";

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    String uom = "";

    public String getInvoiceGUID() {
        return invoiceGUID;
    }

    public void setInvoiceGUID(String invoiceGUID) {
        this.invoiceGUID = invoiceGUID;
    }


    public String getSequence() {
        return Sequence;
    }

    public void setSequence(String sequence) {
        Sequence = sequence;
    }

    String Sequence = "";

    public String getTempSpSnoGuid() {
        return TempSpSnoGuid;
    }

    public void setTempSpSnoGuid(String tempSpSnoGuid) {
        TempSpSnoGuid = tempSpSnoGuid;
    }

    String TempSpSnoGuid = "";

    public String getEtag() {
        return Etag;
    }

    public void setEtag(String etag) {
        Etag = etag;
    }

    String Etag = "";

    public String getMatGrp() {
        return MatGrp;
    }

    public void setMatGrp(String matGrp) {
        MatGrp = matGrp;
    }

    String MatGrp = "";

    public String getPrefixLength() {
        return PrefixLength;
    }

    public void setPrefixLength(String prefixLength) {
        PrefixLength = prefixLength;
    }

    String PrefixLength = "";

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    String Status = "";

    public String getOldSPSNoGUID() {
        return OldSPSNoGUID;
    }

    public void setOldSPSNoGUID(String oldSPSNoGUID) {
        OldSPSNoGUID = oldSPSNoGUID;
    }


    public String getInputInvAmount() {
        return InputInvAmount;
    }

    public void setInputInvAmount(String inputInvAmount) {
        InputInvAmount = inputInvAmount;
    }

    String InputInvAmount = "";

    public boolean isItemSelected() {
        return ItemSelected;
    }

    public void setItemSelected(boolean itemSelected) {
        ItemSelected = itemSelected;
    }

    boolean ItemSelected = false;

    public String getSelectedSerialNoTo() {
        return SelectedSerialNoTo;
    }

    public void setSelectedSerialNoTo(String selectedSerialNoTo) {
        SelectedSerialNoTo = selectedSerialNoTo;
    }

    public String getSelectedSerialNoFrom() {
        return SelectedSerialNoFrom;
    }

    public void setSelectedSerialNoFrom(String selectedSerialNoFrom) {
        SelectedSerialNoFrom = selectedSerialNoFrom;
    }


    public String getOption() {
        return Option;
    }

    public void setOption(String option) {
        Option = option;
    }

    String Option = "";

    public String getSerialNoTo() {
        return SerialNoTo;
    }

    public void setSerialNoTo(String serialNoTo) {
        SerialNoTo = serialNoTo;
    }

    public String getSPSNoGUID() {
        return SPSNoGUID;
    }

    public void setSPSNoGUID(String SPSNoGUID) {
        this.SPSNoGUID = SPSNoGUID;
    }

    public String getSerialNoFrom() {
        return SerialNoFrom;
    }

    public void setSerialNoFrom(String serialNoFrom) {
        SerialNoFrom = serialNoFrom;
    }


    public String getCPStockItemGUID() {
        return CPStockItemGUID;
    }

    public void setCPStockItemGUID(String CPStockItemGUID) {
        this.CPStockItemGUID = CPStockItemGUID;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    int position;

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(String invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }


    public String getMatGroupCode() {
        return matGroupCode;
    }

    public void setMatGroupCode(String matGroupCode) {
        this.matGroupCode = matGroupCode;
    }

    public String getMatGroupDesc() {
        return matGroupDesc;
    }

    public void setMatGroupDesc(String matGroupDesc) {
        this.matGroupDesc = matGroupDesc;
    }

    String matGroupCode;
    String matGroupDesc;


    public String getMatDesc() {
        return matDesc;
    }

    public void setMatDesc(String matDesc) {
        this.matDesc = matDesc;
    }

    public String getMatCode() {
        return matCode;
    }

    public void setMatCode(String matCode) {
        this.matCode = matCode;
    }


}
