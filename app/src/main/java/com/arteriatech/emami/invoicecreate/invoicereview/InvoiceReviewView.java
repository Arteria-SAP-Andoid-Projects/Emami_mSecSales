package com.arteriatech.emami.invoicecreate.invoicereview;

import com.arteriatech.emami.mbo.SKUGroupBean;
import com.arteriatech.emami.mbo.SchemeBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by e10526 on 21-04-2018.
 */

public interface InvoiceReviewView {
    void showProgressDialog(String message);

    void hideProgressDialog();

    void displayMessage(String message);
    void conformationDialog(String message, int from);
    void showMessage(String message, boolean isSimpleDialog);
    void displaySOReview(Map<String, SKUGroupBean> mapSKUGRPVal, Map<String, BigDecimal> mapCRSSKUQTY,
                         Map<String, Double> mapPriSchemePer, Map<String, Double> mapSecSchemePer,
                         Map<String, Double> mapSecSchemeAmt, Map<String, Integer> mapCntMatByCRSKUGRP,
                         Map<String, Double> mapNetAmt, ArrayList<SchemeBean> alSchFreeProd,
                         HashMap<String, String> hashMapFreeMatByOrderMatGrp,
                         HashMap<String, SchemeBean> hashMapFreeMaterialByMaterial, int tlsdCount, double mDobTotalOrderVal, ArrayList<SKUGroupBean> skuGroupBeanArrayList);
}
