package com.arteriatech.emami.scheme;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.emami.mbo.SchemeBean;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10526 on 3/14/2017.
 */

public class SchemeModel {
    private static SchemeModel instance = null;

    public static SchemeModel getInstance() {
        if (instance == null) {
            instance = new SchemeModel();
        }
        return instance;
    }

    public ArrayList<SchemeBean> getSchemeItemDetails(String mStrQry) {
        ArrayList<SchemeBean> schemeBeanArrayList = null;
        try {
            schemeBeanArrayList = OfflineManager.getSchemeItemDetails(mStrQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return schemeBeanArrayList;
    }

    public ArrayList<SchemeBean> getSchemeSalesArea(String mStrQry) {
        ArrayList<SchemeBean> schemeBeanArrayList = null;
        try {
            schemeBeanArrayList = OfflineManager.getSchemeSalesArea(mStrQry, false);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return schemeBeanArrayList;
    }

    public ArrayList<SchemeBean> getSchemeGeoArea(String mStrQry) {
        ArrayList<SchemeBean> schemeBeanArrayList = null;
        try {
            schemeBeanArrayList = OfflineManager.getSchemeGeoArea(mStrQry, false);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return schemeBeanArrayList;
    }

    public ArrayList<SchemeBean> getSchemeSlab(String mStrQry) {
        ArrayList<SchemeBean> schemeBeanArrayList = null;
        try {
            schemeBeanArrayList = OfflineManager.getSchemeSlabs(mStrQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return schemeBeanArrayList;
    }

    public ArrayList<SchemeBean> getSchemeCPs(String mStrQry) {
        ArrayList<SchemeBean> schemeBeanArrayList = null;
        try {
            schemeBeanArrayList = OfflineManager.getSchemeCPs(mStrQry, false);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return schemeBeanArrayList;
    }


}
