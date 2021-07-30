package com.arteriatech.emami.dbstock;

import java.util.ArrayList;

/**
 * Created by e10854 on 06-11-2017.
 */

public class DMSDivionBean {
    String DistributorId="";
    String DistributorGuid="";
    String DistributorName="";
    String DMSDivisionQuery="";
    String StockOwner="";

    public String getDistributorId() {
        return DistributorId;
    }

    public void setDistributorId(String distributorId) {
        DistributorId = distributorId;
    }

    public String getDistributorName() {
        return DistributorName;
    }

    public void setDistributorName(String distributorName) {
        DistributorName = distributorName;
    }

    public String getDMSDivisionQuery() {
        return DMSDivisionQuery;
    }

    public void setDMSDivisionQuery(String DMSDivisionQuery) {
        this.DMSDivisionQuery =  DMSDivisionQuery;
    }

    public String getStockOwner() {
        return StockOwner;
    }

    public void setStockOwner(String stockOwner) {
        StockOwner = stockOwner;
    }

    public String getDistributorGuid() {
        return DistributorGuid;
    }

    public void setDistributorGuid(String distributorGuid) {
        DistributorGuid = distributorGuid;
    }

    public ArrayList<String> getDmsDIVList() {
        return dmsDIVList;
    }

    public void setDmsDIVList(ArrayList<String> dmsDIVList) {
        this.dmsDIVList = dmsDIVList;
    }

    private ArrayList<String> dmsDIVList = new ArrayList<>();


    @Override
    public String toString() {
        return DistributorName;
    }
}
