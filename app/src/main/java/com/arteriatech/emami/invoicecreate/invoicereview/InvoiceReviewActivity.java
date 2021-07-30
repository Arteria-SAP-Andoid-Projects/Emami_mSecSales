package com.arteriatech.emami.invoicecreate.invoicereview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.mbo.InvoiceCreateBean;
import com.arteriatech.emami.mbo.SKUGroupBean;
import com.arteriatech.emami.mbo.SchemeBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.returnOrder.ReturnOrderListDetailsActivity;
import com.arteriatech.emami.returnOrder.ReturnOrderTabActivity;
import com.arteriatech.emami.socreate.SchemeCalcuBean;
import com.arteriatech.emami.store.OfflineManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by e10526 on 21-04-2018.
 *
 */

public class InvoiceReviewActivity extends AppCompatActivity implements InvoiceReviewView {

    private Toolbar toolbar;
    private Context mContext;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    String mStrComingFrom = "";
    InvoiceReviewPresenterImpl presenter;
    ProgressDialog progressDialog = null;
    private ArrayList<SKUGroupBean> alCRSSKUGrpList = new ArrayList<>(), CRSSKUGrpList;
    private InvoiceCreateBean invCreateBean = null;
    TextView tv_so_total_order_val, tvTLSD,retName, retId;

    HashMap<String, String> hashMapFreeMatByOrderMatGrp = new HashMap<>();
    Map<String, Double> mapNetAmt = new HashMap<>();
    Map<String, Double> mapRatioSchDis = new HashMap<>();
    Map<String, Double> mapFreeDisAmt = new HashMap<>();
    Map<String, BigDecimal> mapCRSSKUQTY = new HashMap<>();
    Map<String, Double> mapPriSchemePer = new HashMap<>();
    Map<String, Double> mapSecSchemePer = new HashMap<>();
    Map<String, Double> mapSecSchemeAmt = new HashMap<>();
    private Map<String, SKUGroupBean> mapSKUGRPVal = new HashMap<>();
    private ArrayList<SchemeBean> alSchFreeProd;
    private Double mDobTotalOrderVal = 0.0;
    private Double mDobOrderVal = 0.0;
    HashMap<String, SchemeBean> hashMapFreeMaterialByMaterial = new HashMap<>();
    Map<String, Integer> mapCntMatByCRSKUGRP = new HashMap<>();
    Set<String> mFreeMatScheme = new HashSet<>();
    private int tlsdCount= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_so_review_scroll);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            invCreateBean = (InvoiceCreateBean) bundleExtras.getSerializable(Constants.EXTRA_ARRAY_LIST);
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = InvoiceReviewActivity.this;
        ActionBarView.initActionBarView(this, true,getString(R.string.title_invoice_review));
        initUI();
        setValueToUI();
        if (invCreateBean == null) {
            invCreateBean = new InvoiceCreateBean();
            invCreateBean.setCPGUID32(mStrBundleCPGUID32);
            invCreateBean.setCPGUID(mStrBundleCPGUID);
            invCreateBean.setCPNo(mStrBundleRetID);
        }
        presenter = new InvoiceReviewPresenterImpl(InvoiceReviewActivity.this, this, true, InvoiceReviewActivity.this, invCreateBean);

        if (!Constants.restartApp(InvoiceReviewActivity.this)) {
            presenter.onStart();
        }
    }
    private void setValueToUI() {
        retName.setText(invCreateBean.getCPName());
        retId.setText(invCreateBean.getCPNo());
    }
    private void initUI() {
        tv_so_total_order_val = (TextView) findViewById(R.id.tv_so_total_order_val);
        tvTLSD = (TextView) findViewById(R.id.tv_so_create_tlsd_amt);

        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retId = (TextView) findViewById(R.id.tv_reatiler_id);
    }
    private void setUI(){
        try{
            mDobTotalOrderVal = mDobOrderVal + mDobTotalOrderVal;
        }catch (Exception e){
            e.printStackTrace();
        }
        tv_so_total_order_val.setText(Constants.getCurrencyFormat(invCreateBean.getCurrency(), String.valueOf(mDobTotalOrderVal)));
        tvTLSD.setText(tlsdCount + "");
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(InvoiceReviewActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void displayMessage(String message) {
        UtilConstants.showAlert(message, InvoiceReviewActivity.this);
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(InvoiceReviewActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
                    redirectActivity();
                }
            }
        });
    }


    private void redirectActivity() {
        /*Intent intentNavPrevScreen = new Intent(this, RetailersDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        intentNavPrevScreen.putExtra(Constants.CPGUID32, mStrBundleCPGUID32);
       *//* if(!Constants.OtherRouteNameVal.equalsIgnoreCase("")){
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }*//*
        startActivity(intentNavPrevScreen);*/

        Intent intentNavPrevScreen = null;

        if(mStrComingFrom.equalsIgnoreCase("SOList")){
            intentNavPrevScreen = new Intent(this, ReturnOrderListDetailsActivity.class);
            ReturnOrderTabActivity.isRefresh =true;
            intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentNavPrevScreen.putExtra(Constants.CPGUID, invCreateBean.getCPGUID());
            intentNavPrevScreen.putExtra(Constants.CPNo, invCreateBean.getCPNo());
            intentNavPrevScreen.putExtra(Constants.RetailerName, invCreateBean.getCPNo());
            intentNavPrevScreen.putExtra(Constants.CPUID, Constants.SOBundleValue.getRetUID());
            intentNavPrevScreen.putExtra(Constants.comingFrom, Constants.SOBundleValue.getRetcomingFrom());
            intentNavPrevScreen.putExtra(Constants.DeviceNo, Constants.SOBundleValue.getRetSODeviceNo());
            intentNavPrevScreen.putExtra(Constants.EXTRA_TAB_POS, Constants.SOBundleValue.getRetTabPos());
            intentNavPrevScreen.putExtra(Constants.EXTRA_SSRO_GUID, Constants.SOBundleValue.getRetSSROGUID());
            intentNavPrevScreen.putExtra(Constants.EXTRA_ORDER_DATE, Constants.SOBundleValue.getRetSODate());
            intentNavPrevScreen.putExtra(Constants.EXTRA_ORDER_IDS, Constants.SOBundleValue.getRetSSSONo());
            intentNavPrevScreen.putExtra(Constants.EXTRA_ORDER_AMOUNT, Constants.SOBundleValue.getRetSOAmount());
//            intentNavPrevScreen.putExtra(Constants.EXTRA_ORDER_SATUS, Constants.SOBundleValue.getRetSOStatus());
            intentNavPrevScreen.putExtra(Constants.EXTRA_ORDER_SATUS, "000002");
            intentNavPrevScreen.putExtra(Constants.EXTRA_TEMP_STATUS, Constants.X);
            intentNavPrevScreen.putExtra(Constants.EXTRA_ORDER_CURRENCY, Constants.SOBundleValue.getRetSOCurrency());

        }else{
            intentNavPrevScreen = new Intent(this, RetailersDetailsActivity.class);
            intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentNavPrevScreen.putExtra(Constants.CPNo, invCreateBean.getCPNo());
            intentNavPrevScreen.putExtra(Constants.RetailerName, invCreateBean.getCPName());
            intentNavPrevScreen.putExtra(Constants.CPUID, invCreateBean.getCpUID());
            intentNavPrevScreen.putExtra(Constants.comingFrom, invCreateBean.getComingFrom());
            intentNavPrevScreen.putExtra(Constants.CPGUID, invCreateBean.getCPGUID());
            if(!Constants.OtherRouteNameVal.equalsIgnoreCase("")){
                intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
                intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
            }
        }
        startActivity(intentNavPrevScreen);
    }


    @Override
    public void conformationDialog(String message, int from) {
        UtilConstants.dialogBoxWithCallBack(InvoiceReviewActivity.this, "", message, getString(R.string.ok), getString(R.string.cancel), false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (b) {
                    presenter.onSaveData();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sec_so, menu);
        menu.removeItem(R.id.menu_review);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_save:
                    presenter.onAsignData("", "", "", invCreateBean);
                break;
        }
        return true;
    }


    @Override
    public void displaySOReview(Map<String, SKUGroupBean> mapSKUGRPVal, Map<String, BigDecimal> mapCRSSKUQTY,
                                Map<String, Double> mapPriSchemePer, Map<String, Double> mapSecSchemePer,
                                Map<String, Double> mapSecSchemeAmt, Map<String, Integer> mapCntMatByCRSKUGRP,
                                Map<String, Double> mapNetAmt, ArrayList<SchemeBean> alSchFreeProd,
                                HashMap<String, String> hashMapFreeMatByOrderMatGrp,
                                HashMap<String, SchemeBean> hashMapFreeMaterialByMaterial, int tlsdCount, double mDobOrderVal, ArrayList<SKUGroupBean> skuGroupBeanArrayList) {
        this.mapSKUGRPVal =mapSKUGRPVal;
        this.mapCRSSKUQTY =mapCRSSKUQTY;
        this.mapPriSchemePer =mapPriSchemePer;
        this.mapSecSchemePer =mapSecSchemePer;
        this.mapSecSchemeAmt =mapSecSchemeAmt;
        this.mapCntMatByCRSKUGRP =mapCntMatByCRSKUGRP;
        this.mapNetAmt =mapNetAmt;
        this.alSchFreeProd =alSchFreeProd;
        this.hashMapFreeMatByOrderMatGrp =hashMapFreeMatByOrderMatGrp;
        this.hashMapFreeMaterialByMaterial =hashMapFreeMaterialByMaterial;
        this.tlsdCount =tlsdCount;
        this.mDobOrderVal = mDobOrderVal;
        displayReviewPage(skuGroupBeanArrayList);

    }

    private void displayReviewPage(ArrayList<SKUGroupBean> skuGroupBeanArrayList) {
        TableLayout tlCRSList = (TableLayout) findViewById(R.id.crs_sku);
        TableLayout tlSOList = (TableLayout) findViewById(R.id.report_table);
        tlCRSList.removeAllViews();
        tlSOList.removeAllViews();

        TableLayout tlSKUGroupItem = (TableLayout) LayoutInflater.from(this)
                .inflate(R.layout.tl_so_review_item_heading, null, false);
        TableLayout tlCRSKUGroup = (TableLayout) LayoutInflater.from(this)
                .inflate(R.layout.tl_crsskugrp_heading, null, false);
        TextView tv_sku_grp_title = (TextView) tlCRSKUGroup.findViewById(R.id.tv_crsname);
        tv_sku_grp_title.setText(getString(R.string.lbl_sku_group));
        tlCRSList.addView(tlCRSKUGroup);
        tlSOList.addView(tlSKUGroupItem);

        LinearLayout llSKUGroupItem = null;
        LinearLayout llCRSKUGroup = null;

        if (!mapSKUGRPVal.isEmpty()) {

//            Iterator iterator = mapSKUGRPVal.keySet().iterator();
            for (SKUGroupBean skuGrpBean : skuGroupBeanArrayList) {
                String key = skuGrpBean.getMaterialNo();
                String orderMatGrpKey = skuGrpBean.getOrderMaterialGroupID();
                llSKUGroupItem = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.ll_so_review_item, null, false);
                llCRSKUGroup = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.subitem_so_create_skugroup, null, false);

                ImageView iv_expand_icon = (ImageView) llCRSKUGroup.findViewById(R.id.iv_expand_icon);
                ImageView iv_sku_grp_scheme = (ImageView) llCRSKUGroup.findViewById(R.id.iv_sku_grp_scheme);
                ImageView iv_mat_scheme = (ImageView) llCRSKUGroup.findViewById(R.id.iv_mat_scheme);
                iv_sku_grp_scheme.setVisibility(View.GONE);
                iv_mat_scheme.setVisibility(View.GONE);
                iv_expand_icon.setVisibility(View.INVISIBLE);
                TextView tv_sku_grp_desc = (TextView) llCRSKUGroup.findViewById(R.id.tv_item_so_create_sku_grp);
                TextView tv_ord_qty = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_ord_qty);
                TextView tv_primary_scheme = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_primary_scheme);
                TextView tv_sec_scheme = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_sec_scheme);
                TextView tv_sec_scheme_amt = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_sec_scheme_amt);

                TextView tv_net_amt = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_net_amt);
                final SKUGroupBean skuGroupBean = mapSKUGRPVal.get(key);
                tv_sku_grp_desc.setText(skuGroupBean.getMaterialDesc());
                Constants.setFontSizeByMaxText(tv_sku_grp_desc);
                if (!skuGroupBean.getUOM().equalsIgnoreCase("")) {
//                    tv_ord_qty.setText(Constants.trimQtyDecimalPlace(mapCRSSKUQTY.get(key).toString()) + " " + (skuGroupBean.getSelectedUOM().equalsIgnoreCase("")?skuGroupBean.getUOM():skuGroupBean.getSelectedUOM()));
                    tv_ord_qty.setText(Constants.trimQtyDecimalPlace(mapCRSSKUQTY.get(key).toString()) + " " +  skuGroupBean.getUOM() );
                }else {
                    tv_ord_qty.setText(String.format("%.3f", mapCRSSKUQTY.get(key)));
                }

                double avgPriDisVal = 0.0;
                try {
                    avgPriDisVal = mapPriSchemePer.get(key) / mapCntMatByCRSKUGRP.get(key);
                } catch (Exception e) {
                    avgPriDisVal = 0.0;
                }
                double avgSecDisVal = 0.0;
                try {
                    avgSecDisVal = mapSecSchemePer.get(key) / mapCntMatByCRSKUGRP.get(key);
                } catch (Exception e) {
                    avgSecDisVal = 0.0;
                }
                double avgSecDisAmtVal = 0.0;
                try {
                    avgSecDisAmtVal = mapSecSchemeAmt.get(key) / mapCntMatByCRSKUGRP.get(key);
                } catch (Exception e) {
                    avgSecDisAmtVal = 0.0;
                }
                tv_primary_scheme.setText(UtilConstants.removeLeadingZerowithTwoDecimal(avgPriDisVal + ""));
                tv_sec_scheme.setText(UtilConstants.removeLeadingZerowithTwoDecimal(avgSecDisVal + ""));
                tv_sec_scheme_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(avgSecDisAmtVal + ""));
                tv_net_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mapNetAmt.get(key).toString()));

                TextView tv_item_free_qty_dis_amt = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_free_qty_dis_amt);
                tv_item_free_qty_dis_amt.setVisibility(View.VISIBLE);

                TextView tv_ratio_discount_amt = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_ratio_dis_amt);
                tv_ratio_discount_amt.setVisibility(View.VISIBLE);

                String mStrFreeQtyDisAmt = "0";


                // display start Scheme Free Qty Price discount 24072017
                Double mAfterTaxCal = 0.0;
                for (SchemeCalcuBean schemeCalcuBean : skuGroupBean.getSchemeCalcuBeanArrayList()) {
                    if (!mFreeMatScheme.contains(schemeCalcuBean.getSchemeGuidNo())) {
                        try {
                            mAfterTaxCal = Double.parseDouble(schemeCalcuBean.getmFreeMat() != null ? schemeCalcuBean.getmFreeMat().getFreeMatTax() : "0.00");
                            mFreeMatScheme.add(schemeCalcuBean.getSchemeGuidNo());
                        } catch (Exception ex) {
                            mAfterTaxCal = 0.0;
                        }
                        try {

                            mStrFreeQtyDisAmt = schemeCalcuBean.getmFreeMat() != null ? schemeCalcuBean.getmFreeMat().getFreeMatTax() : "0.00";//hashMapFreeQtyInfoBySchemeGuid.get(skuGroupBean.getSchemeGuidNo()).getFreeMatPrice();
                        } catch (Exception e) {
                            mStrFreeQtyDisAmt = "0";
                        }
                        try {
                            mDobTotalOrderVal = mDobTotalOrderVal + (Double.parseDouble(mStrFreeQtyDisAmt) + mAfterTaxCal);
                        } catch (Exception ex) {
                            mDobTotalOrderVal = mDobTotalOrderVal + 0;
                        }
                    }
                }
                tv_item_free_qty_dis_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(skuGroupBean.getFreeMatDisAmt()));
                //  display End Scheme Free Qty Price discount 24072017

                //  display start Ratio scheme Price discount 24072017
                if (skuGroupBean.getISFreeTypeID().equalsIgnoreCase(Constants.str_2)) {

                    tv_ratio_discount_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(skuGroupBean.getRatioSchDisAmt()));
                    try {
                        mDobTotalOrderVal = mDobTotalOrderVal + Double.parseDouble(skuGroupBean.getRatioSchMatPrice());
                    } catch (Exception ex) {
                        mDobTotalOrderVal = mDobTotalOrderVal + 0;
                    }
                }
                //  display end Ratio scheme Price discount 24072017

                tlSOList.addView(llSKUGroupItem);
                tlCRSList.addView(llCRSKUGroup);

                if (alSchFreeProd.size() > 0) {
                    for (SchemeBean schemeBean : alSchFreeProd) {
                        if (orderMatGrpKey.equalsIgnoreCase(schemeBean.getOrderMaterialGroupID()) && !schemeBean.isRatioScheme()) {
                            llSKUGroupItem = (LinearLayout) LayoutInflater.from(this)
                                    .inflate(R.layout.ll_so_review_item, null, false);
                            llCRSKUGroup = (LinearLayout) LayoutInflater.from(this)
                                    .inflate(R.layout.subitem_so_create_skugroup, null, false);
                            iv_expand_icon = (ImageView) llCRSKUGroup.findViewById(R.id.iv_expand_icon);
                            iv_expand_icon.setVisibility(View.INVISIBLE);
                            iv_mat_scheme = (ImageView) llCRSKUGroup.findViewById(R.id.iv_mat_scheme);
                            iv_mat_scheme.setVisibility(View.GONE);
                            tv_net_amt = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_net_amt);
                            tv_sku_grp_desc = (TextView) llCRSKUGroup.findViewById(R.id.tv_item_so_create_sku_grp);
                            tv_ord_qty = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_ord_qty);
                            tv_primary_scheme = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_primary_scheme);
                            tv_sec_scheme = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_sec_scheme);
                            tv_sku_grp_desc.setText(schemeBean.getFreeMatTxt());


                            Constants.setFontSizeByMaxText(tv_sku_grp_desc);

                            try {
                                mAfterTaxCal = Double.parseDouble(schemeBean.getFreeMatPrice()) + Double.parseDouble(schemeBean.getFreeMatTax());
                            } catch (Exception ex) {
                                mAfterTaxCal = 0.0;
                            }

                            tv_net_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mAfterTaxCal + ""));

                            if (mAfterTaxCal <= 0) {
                                tv_sku_grp_desc.setTextColor(getResources().getColor(R.color.BLUE));
                            }

                            String stFreeQty = "";
                            try {
                                if (OfflineManager.checkNoUOMZero(schemeBean.getFreeQtyUOM()))
                                    stFreeQty = UtilConstants.trimQtyDecimalPlace(schemeBean.getFreeQty());
                                else
                                    stFreeQty = schemeBean.getFreeQty();
                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                            }
                            tv_ord_qty.setText(stFreeQty + " " + schemeBean.getFreeQtyUOM());
                            tv_sec_scheme.setVisibility(View.INVISIBLE);
                            tv_primary_scheme.setVisibility(View.GONE);
                            tlSOList.addView(llSKUGroupItem);
                            tlCRSList.addView(llCRSKUGroup);
                            break;
                        }
                    }
                }
                try {
                    if (hashMapFreeMatByOrderMatGrp.containsValue(orderMatGrpKey)) {

                        Set<String> keys = Constants.getKeysByValue(hashMapFreeMatByOrderMatGrp, orderMatGrpKey);
                        if (keys != null && !keys.isEmpty()) {
                            Iterator itr = keys.iterator();
                            while (itr.hasNext()) {
                                SchemeBean ratioSchemeMatBean = hashMapFreeMaterialByMaterial.get(itr.next().toString());

                                llSKUGroupItem = (LinearLayout) LayoutInflater.from(this)
                                        .inflate(R.layout.ll_so_review_item, null, false);
                                llCRSKUGroup = (LinearLayout) LayoutInflater.from(this)
                                        .inflate(R.layout.subitem_so_create_skugroup, null, false);
                                iv_expand_icon = (ImageView) llCRSKUGroup.findViewById(R.id.iv_expand_icon);
                                iv_expand_icon.setVisibility(View.INVISIBLE);
                                iv_mat_scheme = (ImageView) llCRSKUGroup.findViewById(R.id.iv_mat_scheme);
                                iv_mat_scheme.setVisibility(View.GONE);

                                iv_sku_grp_scheme = (ImageView) llCRSKUGroup.findViewById(R.id.iv_sku_grp_scheme);
                                iv_sku_grp_scheme.setImageResource(R.drawable.ic_free_mat);

                                tv_net_amt = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_net_amt);
                                tv_sku_grp_desc = (TextView) llCRSKUGroup.findViewById(R.id.tv_item_so_create_sku_grp);
                                tv_ord_qty = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_ord_qty);
                                tv_primary_scheme = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_primary_scheme);
                                tv_sec_scheme = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_review_sec_scheme);
                                tv_sku_grp_desc.setText(ratioSchemeMatBean.getFreeMaterialNo());
                                Constants.setFontSizeByMaxText(tv_sku_grp_desc);


                                if (ratioSchemeMatBean.getISFreeTypeID().equalsIgnoreCase(Constants.str_2)) {
                                    tv_net_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(ratioSchemeMatBean.getRatioSchMatPrice()));
                                } else {
                                    tv_net_amt.setVisibility(View.INVISIBLE);
                                }


                                String stFreeQty = "";
                                try {
                                    if (OfflineManager.checkNoUOMZero(ratioSchemeMatBean.getUOM()))
                                        stFreeQty = UtilConstants.trimQtyDecimalPlace(ratioSchemeMatBean.getFreeQty());
                                    else
                                        stFreeQty = ratioSchemeMatBean.getFreeQty();
                                } catch (OfflineODataStoreException e) {
                                    e.printStackTrace();
                                }
                                tv_ord_qty.setText(stFreeQty + " " + ratioSchemeMatBean.getUOM());

                                tv_sec_scheme.setVisibility(View.INVISIBLE);
                                tv_primary_scheme.setVisibility(View.GONE);
                                tlSOList.addView(llSKUGroupItem);
                                tlCRSList.addView(llCRSKUGroup);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {

            tlCRSList.removeAllViews();
            tlSOList.removeAllViews();
            tlSOList = (TableLayout) findViewById(R.id.report_table);

            LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(InvoiceReviewActivity.this)
                    .inflate(R.layout.so_review_empty_layout, null);

            TextView tv_crsname = (TextView) llEmptyLayout.findViewById(R.id.tv_crsname);
            tv_crsname.setText(getString(R.string.lbl_sku_group));


            tlSOList.addView(llEmptyLayout);
        }
        setUI();
    }


}
