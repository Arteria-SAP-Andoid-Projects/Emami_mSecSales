/*
package com.arteriatech.ss.socreate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.ss.common.ActionBarView;
import com.arteriatech.ss.common.Constants;
import com.arteriatech.ss.common.ConstantsUtils;
import com.arteriatech.ss.mbo.MaterialBatchBean;
import com.arteriatech.ss.mbo.SKUGroupBean;
import com.arteriatech.ss.mbo.SchemeBean;
import com.arteriatech.ss.msecsales.R;
import com.arteriatech.ss.store.OfflineManager;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.xscript.core.GUID;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

*/
/**
 * Created by e10526 on 1/9/2017.
 *
 *//*


public class SalesOrderReviewActivity extends AppCompatActivity {
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "", mStrBundleCPGUID32 = "";
    private String mStrBundleRetailerUID = "";
    String mStrComingFrom = "";
    private HashMap<String, ArrayList<SKUGroupBean>> hashMapMaterials;
    TextView retName, retId, tv_so_total_order_val, tvTLSD;
    private ArrayList<SKUGroupBean> alReviewSOItems, alSOSubItems ;
    private ArrayList<SchemeBean> alRevTempItems;
    private Hashtable<String, String> headerTable = new Hashtable<>();
    private String[][] mArrayDistributors = null, mArrayCPDMSDivisoins = null,mArraySPValues=null;
    private Double mDobTotalOrderVal = 0.0;
    private Set<String> mStrCrsSku = new HashSet<>();
    private ProgressDialog pdLoadDialog;

    Map<String, Double> mapNetAmt = new HashMap<>();
    Map<String, BigDecimal> mapCRSSKUQTY = new HashMap<>();
    Map<String, Double> mapPriSchemePer = new HashMap<>();
    Map<String, Double> mapSecSchemePer = new HashMap<>();
    Map<String, Double> mapSecSchemeAmt = new HashMap<>();
    private Map<String, SKUGroupBean> mapSKUGRPVal = new HashMap<>();
    HashMap<String, SchemeBean> hashMapSchemeValByOrderMatGrp = new HashMap<>();
    HashMap<String, SchemeBean> hashMapSchemeValByMaterial = new HashMap<>();
    HashMap<String, String> hashMapOrderMatGrpValByMaterial = new HashMap<>();
    HashMap<String, SchemeBean> hashMapFreeMaterialByMaterial = new HashMap<>();
    HashMap<String, String> hashMapFreeMatByOrderMatGrp = new HashMap<>();
    Set<String> mSetOrderMatGrp = new HashSet<>();
    Set<String> mSetSchemeGuid = new HashSet<>();
    Map<String, Integer> mapCntMatByCRSKUGRP = new HashMap<>();

    private  SchemeBean mFreeMat =null;
    //New
    Map<String, String> mapUOM = new HashMap<>();

    Map<String, Double> mapMaterialWiseQty = new HashMap<>();
    Map<String, Double> mapMaterialWiseTempQty = new HashMap<>();
    Map<String, Double> mapBrandWiseQty = new HashMap<>();
    Map<String, Double> mapBrandWiseTempQty = new HashMap<>();
    Map<String, Double> mapBannerWiseQty = new HashMap<>();
    Map<String, Double> mapBannerWiseTempQty = new HashMap<>();

    Map<String, Double> mapSKUGrpWiseQty = new HashMap<>();
    Map<String, Double> mapSKUGrpWiseTempQty = new HashMap<>();

    Map<String, Double> mapCRSSKUGrpWiseQty = new HashMap<>();
    Map<String, Double> mapCRSSKUGrpWiseTempQty = new HashMap<>();


    Set<String> setMatList = new HashSet<>();

    Set<String> setSchemeList = new HashSet<>();

    Map<String, String> mapHeaderWiseSchemeQty = new HashMap<>();
    Map<String, String> mapSchemePerORAmtByOrderMatGrp = new HashMap<>();
    Map<String, String> mapSchemePerORAmtByMaterial = new HashMap<>();
    Map<String, SchemeBean> mapSchemeFreeMatByOrderMatGrp = new HashMap<>();
    Map<String, SchemeBean> mapSchemeFreeMatByMaterial = new HashMap<>();

    Map<String, Double> mapBasketBrandMinQty = new HashMap<>();
    Map<String, Double> mapBasketBannerMinQty = new HashMap<>();
    Map<String, Double> mapBasketSKUGRPMinQty = new HashMap<>();
    Map<String, Double> mapBasketCRSSKUGRPMinQty = new HashMap<>();
    Map<String, Double> mapBasketMaterialMinQty = new HashMap<>();
    String[] orderMatGrp = null;
    ArrayList<String> mStrAmtBasedSchemeAvl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.lbl_so_create));
        setContentView(R.layout.activity_so_review_scroll);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }
        getSalesPersonValues();
        getMatList();
        getSOSubItems();
        getDistributorValues();
        getCPDMSDivisions();
        initializeUI();
        setValueToUI();
        loadAsyncTask();
    }

    private void getMatList() {
        hashMapOrderMatGrpValByMaterial = OfflineManager.getSchemeCRSSKUGRPBYMaterial();
    }

    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
    }

    private void getCPDMSDivisions() {
        mArrayCPDMSDivisoins = Constants.getDMSDivisionByCPGUID(mStrBundleCPGUID);
    }

    */
/**
     * get salesPerson values
     *//*

    private void getSalesPersonValues() {
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(mStrBundleCPGUID);
    }

    */
/*initializes UI for screen*//*

    void initializeUI() {
        tv_so_total_order_val = (TextView) findViewById(R.id.tv_so_total_order_val);
        tvTLSD = (TextView) findViewById(R.id.tv_so_create_tlsd_amt);

        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retId = (TextView) findViewById(R.id.tv_reatiler_id);
    }

    private void setValueToUI() {
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetailerUID);
    }

    private void loadAsyncTask() {
        try {
            new GetReviewCal().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/*AsyncTask to get Retailers List*//*

    private class GetReviewCal extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(SalesOrderReviewActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            getSOItemValues();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            displayReviewPage();
            tv_so_total_order_val.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mDobTotalOrderVal + "") + " " + mArrayDistributors[10][0]);
            getTLSD();
        }
    }

    private void getTLSD() {
        tvTLSD.setText(mStrCrsSku.size() + "");
    }

    private void getSOSubItems() {
        hashMapMaterials = Constants.HashMapSubMaterials;
    }

    private void getSOItemValues() {
        Constants.hashMapCpStockItemGuidQtyValByMaterial.clear();
        Constants.hashMapMaterialValByOrdMatGrp.clear();
        alReviewSOItems = new ArrayList<>();
        if (!hashMapMaterials.isEmpty()) {
            Iterator mapSelctedValues = hashMapMaterials.keySet()
                    .iterator();
            while (mapSelctedValues.hasNext()) {
                String Key = (String) mapSelctedValues.next();
                alSOSubItems = hashMapMaterials.get(Key);
                if (alSOSubItems != null && alSOSubItems.size() > 0) {
                    for (int incsubVal = 0; incsubVal < alSOSubItems.size(); incsubVal++) {
                        if (Double.parseDouble(alSOSubItems.get(incsubVal).getORDQty().equalsIgnoreCase("")
                                ? "0" : alSOSubItems.get(incsubVal).getORDQty()) > 0) {
                            alReviewSOItems.add(alSOSubItems.get(incsubVal));
                            setMatList.add(alSOSubItems.get(incsubVal).getMaterialNo());

                            SchemeBean schemeBean = new SchemeBean();
                            schemeBean.setCPItemGUID(alSOSubItems.get(incsubVal).getCPStockItemGUID());
                            schemeBean.setOrderQty(alSOSubItems.get(incsubVal).getORDQty());
                            Constants.hashMapCpStockItemGuidQtyValByMaterial.put(alSOSubItems.get(incsubVal).getMaterialNo(),schemeBean);

                            if(!Constants.hashMapMaterialValByOrdMatGrp.containsKey(alSOSubItems.get(incsubVal).getSKUGroup())) {
                                Set set =Constants.hashMapMaterialValByOrdMatGrp.get(alSOSubItems.get(incsubVal).getSKUGroup());
                                if(set!=null){
                                    set.add(alSOSubItems.get(incsubVal).getMaterialNo());
                                }else {
                                    set=new HashSet();
                                    set.add(alSOSubItems.get(incsubVal).getMaterialNo());
                                }
                                Constants.hashMapMaterialValByOrdMatGrp.put(alSOSubItems.get(incsubVal).getSKUGroup(),set);
                            }else{
                                Set set=new HashSet();
                                set.add(alSOSubItems.get(incsubVal).getMaterialNo());
                                Constants.hashMapMaterialValByOrdMatGrp.put(alSOSubItems.get(incsubVal).getSKUGroup(),set);
                            }

                            if(!alSOSubItems.get(incsubVal).getBrand().equalsIgnoreCase("")) {
                                // get brand wise qty
                                if (mapBrandWiseQty.containsKey(alSOSubItems.get(incsubVal).getBrand())) {
                                    double mDouOrderQty = Double.parseDouble(alSOSubItems.get(incsubVal).getORDQty()) + mapBrandWiseQty.get(alSOSubItems.get(incsubVal).getBrand());
                                    mapBrandWiseQty.put(alSOSubItems.get(incsubVal).getBrand(), mDouOrderQty);
                                } else {
                                    double mDouOrderQty = Double.parseDouble(alSOSubItems.get(incsubVal).getORDQty());
                                    mapBrandWiseQty.put(alSOSubItems.get(incsubVal).getBrand(), mDouOrderQty);
                                }
                            }

                            if(!alSOSubItems.get(incsubVal).getBanner().equalsIgnoreCase("")) {
                                // get banner wise qty
                                if (mapBannerWiseQty.containsKey(alSOSubItems.get(incsubVal).getBanner())) {
                                    double mDouOrderQty = Double.parseDouble(alSOSubItems.get(incsubVal).getORDQty()) + mapBannerWiseQty.get(alSOSubItems.get(incsubVal).getBanner());
                                    mapBannerWiseQty.put(alSOSubItems.get(incsubVal).getBanner(), mDouOrderQty);
                                } else {
                                    double mDouOrderQty = Double.parseDouble(alSOSubItems.get(incsubVal).getORDQty());
                                    mapBannerWiseQty.put(alSOSubItems.get(incsubVal).getBanner(), mDouOrderQty);
                                }
                            }

                            if(!alSOSubItems.get(incsubVal).getSKUGroupID().equalsIgnoreCase("")) {
                                // get SKU GRP wise qty
                                if (mapSKUGrpWiseQty.containsKey(alSOSubItems.get(incsubVal).getSKUGroupID())) {
                                    double mDouOrderQty = Double.parseDouble(alSOSubItems.get(incsubVal).getORDQty()) + mapSKUGrpWiseQty.get(alSOSubItems.get(incsubVal).getSKUGroupID());
                                    mapSKUGrpWiseQty.put(alSOSubItems.get(incsubVal).getSKUGroupID(), mDouOrderQty);
                                } else {
                                    double mDouOrderQty = Double.parseDouble(alSOSubItems.get(incsubVal).getORDQty());
                                    mapSKUGrpWiseQty.put(alSOSubItems.get(incsubVal).getSKUGroupID(), mDouOrderQty);
                                }
                            }

                            if(!alSOSubItems.get(incsubVal).getSKUGroup().equalsIgnoreCase("")) {
                                // get CRS SKU GRP wise qty
                                if (mapCRSSKUGrpWiseQty.containsKey(alSOSubItems.get(incsubVal).getSKUGroup())) {
                                    double mDouOrderQty = Double.parseDouble(alSOSubItems.get(incsubVal).getORDQty()) + mapCRSSKUGrpWiseQty.get(alSOSubItems.get(incsubVal).getSKUGroup());
                                    mapCRSSKUGrpWiseQty.put(alSOSubItems.get(incsubVal).getSKUGroup(), mDouOrderQty);
                                } else {
                                    double mDouOrderQty = Double.parseDouble(alSOSubItems.get(incsubVal).getORDQty());
                                    mapCRSSKUGrpWiseQty.put(alSOSubItems.get(incsubVal).getSKUGroup(), mDouOrderQty);
                                }
                            }
                            mapMaterialWiseQty.put(alSOSubItems.get(incsubVal).getMaterialNo(), Double.parseDouble(alSOSubItems.get(incsubVal).getORDQty()));

                            mStrCrsSku.add(Key);
                        }
                    }
                }
            }
        }
        mapBrandWiseTempQty.putAll(mapBrandWiseQty);
        mapBannerWiseTempQty.putAll(mapBannerWiseQty);
        mapSKUGrpWiseTempQty.putAll( mapSKUGrpWiseQty);
        mapCRSSKUGrpWiseTempQty.putAll(mapCRSSKUGrpWiseQty);
        mapMaterialWiseTempQty.putAll(mapMaterialWiseQty);
        getOrderMatGrpSchemeCal();
        getMaterialSchemeCal();
        sumOfSkuGrpItems();

    }

    private void sumOfSkuGrpItems() {
        alRevTempItems = new ArrayList<>();
        if (alReviewSOItems != null && alReviewSOItems.size() > 0) {
            SchemeBean primaryDisTaxValBean = null;
            for (int i = 0; i < alReviewSOItems.size(); i++) {
                final SKUGroupBean skuGroupBean = alReviewSOItems.get(i);

                double calSecPer = 0.0;
                double mDoubSecPer = 0.0;
                SchemeBean schPerCalBean = null;
                boolean mBoolSchemeMatWise = false;
                if (Constants.MAPSCHGuidByMaterial.containsKey(skuGroupBean.getMaterialNo())) {
                    mBoolSchemeMatWise = true;
                    schPerCalBean = hashMapSchemeValByMaterial.get(skuGroupBean.getMaterialNo());

                    if(schPerCalBean==null){
                        mBoolSchemeMatWise = false;
                        if (mSetOrderMatGrp.size() == 0) {
                            mSetOrderMatGrp.add(skuGroupBean.getSKUGroup());
                            schPerCalBean = hashMapSchemeValByOrderMatGrp.get(skuGroupBean.getSKUGroup());
                        } else if (!mSetOrderMatGrp.contains(skuGroupBean.getSKUGroup())) {
                            mSetOrderMatGrp.add(skuGroupBean.getSKUGroup());
                            schPerCalBean = hashMapSchemeValByOrderMatGrp.get(skuGroupBean.getSKUGroup());
                        } else {
                            schPerCalBean = null;
                        }
                    }

                } else {
                    mBoolSchemeMatWise = false;
                    if (mSetOrderMatGrp.size() == 0) {
                        mSetOrderMatGrp.add(skuGroupBean.getSKUGroup());
                        schPerCalBean = hashMapSchemeValByOrderMatGrp.get(skuGroupBean.getSKUGroup());
                    } else if (!mSetOrderMatGrp.contains(skuGroupBean.getSKUGroup())) {
                        mSetOrderMatGrp.add(skuGroupBean.getSKUGroup());
                        schPerCalBean = hashMapSchemeValByOrderMatGrp.get(skuGroupBean.getSKUGroup());
                    } else {
                        schPerCalBean = null;
                    }
                }



                String secondarySchemeAmt = "0",schemeSlabRule="";
                Double mDouSumNetTaxSecAmt = 0.0, mDouSumPriDis = 0.0, mDouSumSecDiscount = 0.0,mDouPriDis = 0.0, mDouSecDiscount = 0.0,mDouSecAmt=0.0;
                String mStrSchemeGUID = "";
                mFreeMat = null;
                if (schPerCalBean != null) {
                    String mStrSlabRuleId = schPerCalBean.getSlabRuleID();
                    Double mDouSlabTypeCal = 0.0;

                    if(mBoolSchemeMatWise){
                        if(!schPerCalBean.getIsHeaderBasedSlab().equalsIgnoreCase(Constants.X)) {
                            mDouSlabTypeCal = getSecondaryDiscountOrAmtOrFreeQty(mBoolSchemeMatWise, schPerCalBean, mStrSlabRuleId,
                                    skuGroupBean.getORDQty(), skuGroupBean.getMaterialNo(), skuGroupBean.getSKUGroup(), skuGroupBean.getCPStockItemGUID(), "");
                            mStrSchemeGUID = schPerCalBean.getSchemeGuid();
                        }else{
                            mDouSlabTypeCal = Double.parseDouble(mapSchemePerORAmtByMaterial.get(skuGroupBean.getMaterialNo()));
                            mFreeMat = mapSchemeFreeMatByMaterial.get(skuGroupBean.getMaterialNo());
                            mStrSchemeGUID = schPerCalBean.getSchemeGuid();
                        }
                    }else{
                        if(!schPerCalBean.getIsHeaderBasedSlab().equalsIgnoreCase(Constants.X)){
                            mDouSlabTypeCal = getSecondaryDiscountOrAmtOrFreeQty(mBoolSchemeMatWise, schPerCalBean, mStrSlabRuleId,
                                    skuGroupBean.getORDQty(), skuGroupBean.getMaterialNo(), skuGroupBean.getSKUGroup(), skuGroupBean.getCPStockItemGUID(),"");
                            mStrSchemeGUID = schPerCalBean.getSchemeGuid();
                        }else {
                            mDouSlabTypeCal = Double.parseDouble(mapSchemePerORAmtByOrderMatGrp.get(skuGroupBean.getSKUGroup()));
                            mFreeMat = mapSchemeFreeMatByOrderMatGrp.get(skuGroupBean.getSKUGroup());
                            mStrSchemeGUID = Constants.MAPSCHGuidByCrsSkuGrp.get(skuGroupBean.getSKUGroup());
                        }
                    }

                    primaryDisTaxValBean = getPrimaryTaxValByMaterial(skuGroupBean.getCPStockItemGUID(), skuGroupBean.getMaterialNo(), skuGroupBean.getORDQty());
                    if (primaryDisTaxValBean != null) {
                        ArrayList<MaterialBatchBean> alMatBatchItemBean = primaryDisTaxValBean.getMaterialBatchBeanArrayList();
                        if (alMatBatchItemBean != null && alMatBatchItemBean.size() > 0) {
                            for (MaterialBatchBean matBatchItemBean : alMatBatchItemBean) {
                                String priPercentage = matBatchItemBean.getPrimaryPer();
                                if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
                                    boolean mBoolBasketSchemeapplicable=false;
                                    mDouCalNetAmt = 0.0;
                                    if(schPerCalBean.getSchemeTypeID().equalsIgnoreCase(Constants.SchemeTypeIDBasketScheme)){
                                        mBoolBasketSchemeapplicable = getBasketSchemePer(schPerCalBean,matBatchItemBean,skuGroupBean);
                                    }

                                    try {
                                        mDoubSecPer = Double.parseDouble(mDouSlabTypeCal + "");
                                    } catch (NumberFormatException e) {
                                        mDoubSecPer = 0.0;
                                    }



                                    if (schPerCalBean.getIsIncludingPrimary().equalsIgnoreCase(Constants.X)) {
                                        calSecPer = mDoubSecPer - Double.parseDouble(priPercentage);
                                        if (calSecPer < 0) {
                                            calSecPer = 0;
                                        }
                                    } else {
                                        calSecPer = mDoubSecPer;
                                    }
                                    matBatchItemBean.setSecPer(calSecPer + "");
                                    matBatchItemBean.setSecDiscountAmt(0+"");
                                    if(!mBoolBasketSchemeapplicable) {
                                        secondarySchemeAmt = Constants.calculatePrimaryDiscount(calSecPer + "", matBatchItemBean.getNetAmtAftPriDis());
                                    }else{
                                        secondarySchemeAmt = Constants.calculatePrimaryDiscount(calSecPer + "", mDouCalNetAmt+"");
                                    }
                                    schemeSlabRule = Constants.SchemeFreeDiscountPercentage;

                                    String mStrTaxAmt = getTaxAmount(matBatchItemBean.getNetAmtAftPriDis(),secondarySchemeAmt,matBatchItemBean.getoDataEntity(),matBatchItemBean.getQty());
                                    matBatchItemBean.setTax(mStrTaxAmt);

                                    matBatchItemBean.setTotalNetAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt)) + "");
                                    mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt));

                                    matBatchItemBean.setGrossAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt)+""));

                                } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                    String mStrBatchQty = matBatchItemBean.getQty();
                                    calSecPer =0.0;
                                    try {
                                        mDouSecAmt = Double.parseDouble(mDouSlabTypeCal+"");
                                    } catch (NumberFormatException e) {
                                        mDouSecAmt = 0.0;
                                    }
                                    secondarySchemeAmt = mDouSecAmt+"";
                                    matBatchItemBean.setSecPer(0 + "");
//                                    matBatchItemBean.setSecDiscountAmt(mDouSecAmt+"");
                                    matBatchItemBean.setSecDiscountAmt(0+"");
                                    matBatchItemBean.setSlabRuleAmt( Constants.SchemeFreeDiscountAmount);
                                    schemeSlabRule = Constants.SchemeFreeDiscountAmount;
                                    // If amount based scheme scheme amount adjust to singele line item
                                    if(!mStrAmtBasedSchemeAvl.contains(mStrSchemeGUID.toUpperCase())){
                                        mStrAmtBasedSchemeAvl.add(mStrSchemeGUID.toUpperCase());

                                        String mStrTaxAmt = getTaxAmount(matBatchItemBean.getNetAmtAftPriDis(),secondarySchemeAmt,matBatchItemBean.getoDataEntity(),matBatchItemBean.getQty());
                                        matBatchItemBean.setTax(mStrTaxAmt);

                                        matBatchItemBean.setTotalNetAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt)) + "");
                                        mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt));

                                        matBatchItemBean.setGrossAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt)+""));
                                    }else{
                                        secondarySchemeAmt = "0";
                                        String mStrTaxAmt = getTaxAmount(matBatchItemBean.getNetAmtAftPriDis(),secondarySchemeAmt,matBatchItemBean.getoDataEntity(),matBatchItemBean.getQty());
                                        matBatchItemBean.setTax(mStrTaxAmt);

                                        matBatchItemBean.setTotalNetAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt)) + "");
                                        mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt));

                                        matBatchItemBean.setGrossAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt)+""));
                                    }


                                } else {

                                    secondarySchemeAmt = "0";
                                    String mStrTaxAmt = getTaxAmount(matBatchItemBean.getNetAmtAftPriDis(),secondarySchemeAmt,matBatchItemBean.getoDataEntity(),matBatchItemBean.getQty());
                                    matBatchItemBean.setTax(mStrTaxAmt);

                                    matBatchItemBean.setTotalNetAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt)) + "");
                                    mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt));

                                    matBatchItemBean.setGrossAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt)+""));

                                    schemeSlabRule = "";
                                    secondarySchemeAmt = "0";
                                    mDouSecAmt = 0.0;
                                    matBatchItemBean.setSecPer(0 + "");
                                    matBatchItemBean.setSecDiscountAmt(0+"");
                                }




                                mDouPriDis = Double.parseDouble(matBatchItemBean.getPrimaryPer());


                                mDouSecDiscount = calSecPer;
                            }
                            primaryDisTaxValBean.setMaterialBatchBeanArrayList(alMatBatchItemBean);
                        }

                    } else {
                        mDouSumNetTaxSecAmt = 0.0;
                        mDouSumPriDis = 0.0;
                        mDouSumSecDiscount = 0.0;
                        mDouPriDis = 0.0;
                        mDouSecDiscount = 0.;

                        schemeSlabRule = "";
                        secondarySchemeAmt = "0";
                    }



                } else {
                    schemeSlabRule = "";

                    primaryDisTaxValBean = getPrimaryTaxValByMaterial(skuGroupBean.getCPStockItemGUID(), skuGroupBean.getMaterialNo(), skuGroupBean.getORDQty());
                    if (primaryDisTaxValBean != null) {
                        ArrayList<MaterialBatchBean> alMatBatchItemBean = primaryDisTaxValBean.getMaterialBatchBeanArrayList();
                        if (alMatBatchItemBean != null && alMatBatchItemBean.size() > 0) {
                            for (MaterialBatchBean matBatchItemBean : alMatBatchItemBean) {
                                String netAmount = matBatchItemBean.getNetAmount();
                                calSecPer = 0;
                                matBatchItemBean.setSecPer(calSecPer + "");
                                secondarySchemeAmt = Constants.calculatePrimaryDiscount(calSecPer + "", netAmount);

                                String mStrTaxAmt = getTaxAmount(matBatchItemBean.getNetAmtAftPriDis(),secondarySchemeAmt,matBatchItemBean.getoDataEntity(),matBatchItemBean.getQty());
                                matBatchItemBean.setTax(mStrTaxAmt);

                                matBatchItemBean.setTotalNetAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt)) + "");
                                mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt));

                                mDouPriDis = Double.parseDouble(matBatchItemBean.getPrimaryPer());

                                matBatchItemBean.setGrossAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt)+""));

                                mDouSecDiscount = calSecPer;
                            }
                            primaryDisTaxValBean.setMaterialBatchBeanArrayList(alMatBatchItemBean);
                        }

                    } else {
                        mDouSumNetTaxSecAmt = 0.0;
                        mDouSumPriDis = 0.0;
                        mDouSumSecDiscount = 0.0;

                        mDouPriDis = 0.0;

                        mDouSecDiscount = 0.;

                        schemeSlabRule = "";
                        secondarySchemeAmt = "0";
                    }

                    schemeSlabRule = "";

                }


                mDobTotalOrderVal = mDobTotalOrderVal + mDouSumNetTaxSecAmt;

                alReviewSOItems.get(i).setPRMScheme(mDouPriDis + "");

                alReviewSOItems.get(i).setNetAmount(mDouSumNetTaxSecAmt + "");

                alReviewSOItems.get(i).setMaterialBatchBean(primaryDisTaxValBean);

                if(!schemeSlabRule.equalsIgnoreCase("") && schemeSlabRule.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)){
                    alReviewSOItems.get(i).setSecScheme(0 + "");
                    alReviewSOItems.get(i).setSecSchemeAmt(mDouSecAmt + "");
//                    alReviewSOItems.get(i).setSecSchemeAmt(0 + "");
                    alReviewSOItems.get(i).setSchemeSlabRule(Constants.SchemeFreeDiscountAmount);
                }else if(!schemeSlabRule.equalsIgnoreCase("") && schemeSlabRule.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)){
                    alReviewSOItems.get(i).setSecScheme(mDouSecDiscount + "");
                    alReviewSOItems.get(i).setSecSchemeAmt(0 + "");
                    alReviewSOItems.get(i).setSchemeSlabRule(Constants.SchemeFreeDiscountPercentage);
                }else{
                    alReviewSOItems.get(i).setSecScheme(0+ "");
                    alReviewSOItems.get(i).setSecSchemeAmt(0 + "");
                    alReviewSOItems.get(i).setSchemeSlabRule("");
                }

                if(mFreeMat!=null) {
                    if (mSetSchemeGuid.size() == 0) {
                        mSetSchemeGuid.add(mStrSchemeGUID);
                        alRevTempItems.add(mFreeMat);
                    } else if (!mSetSchemeGuid.contains(mStrSchemeGUID)) {
                        mSetSchemeGuid.add(mStrSchemeGUID);
                        alRevTempItems.add(mFreeMat);
                    }
                }

                // get scheme numinator or deon
//                SchemeBean freeMatBean = getFreeMaterial(skuGroupBean.getCPStockItemGUID(), skuGroupBean.getMaterialNo(), skuGroupBean.getORDQty());

                try {
                    if (primaryDisTaxValBean != null) {
                        if(!primaryDisTaxValBean.getFreeMaterialNo().equalsIgnoreCase("")){
                            hashMapFreeMaterialByMaterial.put(skuGroupBean.getMaterialNo(),primaryDisTaxValBean);
                            hashMapFreeMatByOrderMatGrp.put(skuGroupBean.getMaterialNo(),skuGroupBean.getSKUGroup());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }

        mapSKUGRPVal = getALSKUGRP(alReviewSOItems);
    }

    private double getSecondaryDiscountOrAmtOrFreeQty(boolean mBoolMatWise, SchemeBean schPerCalBean, String mStrSlabRuleId,
                                                      String mOrderQty, String mMatNo, String mOrderMatGrp, String mCPItemGUID,String isHeaderBased) {
        double mDoubSecDisOrAmtOrQty = 0.0;
        if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeProduct)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
               mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                       mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }

            if (mBoolMatWise) {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getMaterialDesc(), schPerCalBean.getMaterialNo(), mCPItemGUID,mOrderMatGrp);
            } else {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getMaterialDesc(), schPerCalBean.getMaterialNo(), mCPItemGUID,mOrderMatGrp);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeSKUGroup)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }
            if (mBoolMatWise) {
                mFreeMat =  getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getSKUGroupDesc(), schPerCalBean.getSKUGroupID(), mCPItemGUID,mOrderMatGrp);
            } else {
                mFreeMat =getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getSKUGroupDesc(), schPerCalBean.getSKUGroupID(), mCPItemGUID,mOrderMatGrp);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeCRSSKUGroup)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }
            if (mBoolMatWise) {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getOrderMaterialGroupDesc(), schPerCalBean.getOrderMaterialGroupID(), mCPItemGUID,mOrderMatGrp);
            } else {
                mFreeMat =getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getOrderMaterialGroupDesc(), schPerCalBean.getOrderMaterialGroupID(), mCPItemGUID,mOrderMatGrp);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutPerc(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutPerc(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutAmount(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutAmount(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeScratchCard)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }
            if (mBoolMatWise) {
                mFreeMat =  getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getCardTitle(), schPerCalBean.getCardTitle(), mCPItemGUID,mOrderMatGrp);
            } else {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getCardTitle(), schPerCalBean.getCardTitle(), mCPItemGUID,mOrderMatGrp);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeFreeArticle)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }

            if (mBoolMatWise) {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getFreeArticle(), schPerCalBean.getFreeArticle(), mCPItemGUID,mOrderMatGrp);
            } else {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getFreeArticle(), schPerCalBean.getFreeArticle(), mCPItemGUID,mOrderMatGrp);
            }
        }

        return mDoubSecDisOrAmtOrQty;
    }

    private void getOrderMatGrpSchemeCal() {
        if (!Constants.MAPORDQtyByCrsSkuGrp.isEmpty()) {
            Iterator iterator = Constants.MAPORDQtyByCrsSkuGrp.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                if (!Constants.MAPSCHGuidByCrsSkuGrp.get(key).equalsIgnoreCase("")) {
                    // Todo check scheme is instant scheme or not
                    if (isSchemeInstantOrNot(Constants.MAPSCHGuidByCrsSkuGrp.get(key))) {

                        if(schemeIsAvaliable(Constants.MAPSCHGuidByCrsSkuGrp.get(key))){
                            String orderQty = Constants.MAPORDQtyByCrsSkuGrp.get(key);
                            if (Double.parseDouble(orderQty.equalsIgnoreCase("") ? "0" : orderQty) > 0) {

                                // Todo check scheme is Basket scheme or not
                                if (!Constants.isSchemeBasketOrNot(Constants.MAPSCHGuidByCrsSkuGrp.get(key))) {
                                    // Todo check scheme calculation  HDR SLAB BASED/ ITEM SLAB BASED
                                    if (!isSchemeHeaderBasedOrItemBased(Constants.MAPSCHGuidByCrsSkuGrp.get(key))) {
                                        SchemeBean schemeBean = getSecSchemeBeanByCrsSKUGrp((Constants.MAPSCHGuidByCrsSkuGrp.get(key)), key, Constants.MAPORDQtyByCrsSkuGrp.get(key), "",Constants.SchemeTypeNormal);
                                        if (schemeBean != null) {
                                            hashMapSchemeValByOrderMatGrp.put(key, schemeBean);
                                        }
                                    } else {
                                        SchemeBean schemeBean = getSecSchemeBeanByCrsSKUGrp((Constants.MAPSCHGuidByCrsSkuGrp.get(key)), key, Constants.MAPORDQtyByCrsSkuGrp.get(key), Constants.X,Constants.SchemeTypeNormal);
//                                    if (schemeBean != null) {
//                                        hashMapSchemeValByOrderMatGrp.put(key, schemeBean);
//                                    }
                                    }
                                }else{
                                    getBasketSchemeCal(Constants.MAPSCHGuidByCrsSkuGrp.get(key));
                                }

                            }
                        }

                    }
                }
            }
        }
    }


    private boolean schemeIsAvaliable(String mStrScheme){
        boolean schemeISAval =false;
        if(setSchemeList.size()==0){
            schemeISAval = true;
        }else if(!setSchemeList.contains(mStrScheme)){
            schemeISAval = true;
        }
        return schemeISAval;
    }
    private void getMaterialSchemeCal() {
        if (!Constants.MAPSCHGuidByMaterial.isEmpty()) {
            if (alReviewSOItems != null && alReviewSOItems.size() > 0) {
                for (int i = 0; i < alReviewSOItems.size(); i++) {
                    final SKUGroupBean skuGroupBean = alReviewSOItems.get(i);
                    if (Constants.MAPSCHGuidByMaterial.get(skuGroupBean.getMaterialNo()) != null && !Constants.MAPSCHGuidByMaterial.get(skuGroupBean.getMaterialNo()).equalsIgnoreCase("")) {
                        // Todo check scheme is instant scheme or not
                        if (isSchemeInstantOrNot(Constants.MAPSCHGuidByMaterial.get(skuGroupBean.getMaterialNo()))) {

                            if(schemeIsAvaliable(Constants.MAPSCHGuidByMaterial.get(skuGroupBean.getMaterialNo()))){
                                // Todo check scheme is Basket scheme or not
                                if (!Constants.isSchemeBasketOrNot(Constants.MAPSCHGuidByMaterial.get(skuGroupBean.getMaterialNo()))) {
                                    // Todo check scheme calculation  HDR SLAB BASED/ ITEM SLAB BASED
                                    if (!isSchemeHeaderBasedOrItemBased(Constants.MAPSCHGuidByMaterial.get(skuGroupBean.getMaterialNo()))) {
                                        SchemeBean schemeBean = getSecSchemeBeanByMaterial((Constants.MAPSCHGuidByMaterial.get(skuGroupBean.getMaterialNo())),
                                                skuGroupBean.getMaterialNo(), skuGroupBean.getORDQty(), "");
                                        if (schemeBean != null) {
                                            hashMapSchemeValByMaterial.put(skuGroupBean.getMaterialNo(), schemeBean);
                                        }
                                    } else {
                                        SchemeBean schemeBean = getSecSchemeBeanByMaterial((Constants.MAPSCHGuidByMaterial.get(skuGroupBean.getMaterialNo())),
                                                skuGroupBean.getMaterialNo(), skuGroupBean.getORDQty(), Constants.X);
                                        if (schemeBean != null) {
                                            hashMapSchemeValByMaterial.put(skuGroupBean.getMaterialNo(), schemeBean);
                                        }
                                    }
                                }else{
                                    getBasketSchemeCalByMaterial(Constants.MAPSCHGuidByMaterial.get(skuGroupBean.getMaterialNo()),skuGroupBean.getORDQty());
                                }
                            }



                        }
                    }
                }
            }
        }
    }

    private void getBasketSchemeCal(String schemeGuid) {
        String onSaleOnID="",onRefID="";

        SchemeBean headerScheme=null;
        ArrayList<SchemeBean> alItems = null;
        try {
            headerScheme = OfflineManager.getBasketSchemeHeader(Constants.SchemeItemDetails +"?$select=" + ConstantsUtils.OnSaleOfCatID + ","
                    +Constants.SchemeItemGUID +","+Constants.ItemMin+" &$filter="
                    + Constants.SchemeGUID + " eq guid'" + schemeGuid.toUpperCase() + "' and "+Constants.ItemCatID+" eq '"+Constants.BasketCatID+"' &$top=1 ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(headerScheme!=null){
            onSaleOnID = headerScheme.getOnSaleOfCatID();
            if(onSaleOnID.equalsIgnoreCase("000005")){  // Order Material Group
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails +"?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if(alItems!=null && alItems.size()>0){
                    for (SchemeBean schemeBeanVal : alItems) {
                        if(Double.parseDouble(schemeBeanVal.getItemMin())>0){
                            if(Constants.MAPORDQtyByCrsSkuGrp.containsKey(schemeBeanVal.getOrderMaterialGroupID())){
                                double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                if(calItemMinQty<=Double.parseDouble(Constants.MAPORDQtyByCrsSkuGrp.get(schemeBeanVal.getOrderMaterialGroupID()))){

                                }else{
                                    isBasketschemeAval =true;
                                    break;
                                }
                            }else{
                                isBasketschemeAval =true;
                                break;
                            }
                        }

                    }
                }

                if(!isBasketschemeAval && alItems.size()>0){
                    for (SchemeBean schemeBeanVal : alItems) {
                        double calItemMinQty=0.0;
                        if(Double.parseDouble(schemeBeanVal.getItemMin())>0){
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                        }else{
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                        }
                        // adding brand wise min qty into map table
                        mapBasketCRSSKUGRPMinQty.put(schemeBeanVal.getOrderMaterialGroupID(),calItemMinQty);
                        addBasketSchemeTohashmapByOrderMatGrp(schemeBeanVal.getOrderMaterialGroupID());
                    }
                }

            }else if(onSaleOnID.equalsIgnoreCase("000006")){  // Material
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails +"?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if(alItems!=null && alItems.size()>0){
                    for (SchemeBean schemeBeanVal : alItems) {
                        if(!isBasketschemeAval){
                            for(SKUGroupBean skuGroupBean : alReviewSOItems){
                                if(Double.parseDouble(schemeBeanVal.getItemMin())>0){
                                    if(skuGroupBean.getMaterialNo().equalsIgnoreCase(schemeBeanVal.getMaterialNo())){
                                        double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                        if(calItemMinQty<=Double.parseDouble(skuGroupBean.getORDQty())){
                                            schemeBeanVal.setOrderQty(skuGroupBean.getORDQty());
                                            break;
                                        }else{
                                            isBasketschemeAval =true;
                                            break;
                                        }
                                    }else{
                                        if(!setMatList.contains(skuGroupBean.getMaterialNo())){
                                            isBasketschemeAval =true;
                                            break;
                                        }
                                    }
                                }else{
                                    if(skuGroupBean.getMaterialNo().equalsIgnoreCase(schemeBeanVal.getMaterialNo())){
                                        schemeBeanVal.setOrderQty(skuGroupBean.getORDQty().equalsIgnoreCase("")?"0":skuGroupBean.getORDQty());
                                    }
                                }
                            }
                        }

                    }

                    if(!isBasketschemeAval && alItems.size()>0){
                        for (SchemeBean schemeBeanVal : alItems) {
                            double calItemMinQty=0.0;
                            if(Double.parseDouble(schemeBeanVal.getItemMin())>0){
                                calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                            }else{
                                calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                            }
                            // adding material wise min qty into map table
                            mapBasketMaterialMinQty.put(schemeBeanVal.getMaterialNo(),calItemMinQty);
                            addBasketSchemeTohashmapByMaterial(schemeBeanVal.getMaterialNo(),schemeBeanVal.getOrderQty());
                        }
                    }
                }

            }else if(onSaleOnID.equalsIgnoreCase("000003")){ // Product category

            }else if(onSaleOnID.equalsIgnoreCase("000002")){  // Brand

                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails +"?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if(alItems!=null && alItems.size()>0){
                    for (SchemeBean schemeBeanVal : alItems) {
                        if(Double.parseDouble(schemeBeanVal.getItemMin())>0){
                            if(mapBrandWiseQty.containsKey(schemeBeanVal.getBrandID())){
                                double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                if(calItemMinQty<=mapBrandWiseQty.get(schemeBeanVal.getBrandID())){

                                }else{
                                    isBasketschemeAval =true;
                                    break;
                                }
                            }else{
                                isBasketschemeAval =true;
                                break;
                            }
                        }

                    }
                }

                if(!isBasketschemeAval && alItems.size()>0){
                    for (SchemeBean schemeBeanVal : alItems) {
                        double calItemMinQty=0.0;
                        if(Double.parseDouble(schemeBeanVal.getItemMin())>0){
                             calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                        }else{
                             calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                        }
                        // adding brand wise min qty into map table
                        mapBasketBrandMinQty.put(schemeBeanVal.getBrandID(),calItemMinQty);
                        try {
                            ArrayList<String> mOrderGrp = OfflineManager.getOrderMatGrp(Constants.CPStockItems + "?$select=" + Constants.OrderMaterialGroupID + " &$filter = "
                                    + ConstantsUtils.Brand + " eq '" + schemeBeanVal.getBrandID() + "' and "+Constants.StockOwner + " eq '01' ");
                            for (String OrderMatGrp : mOrderGrp) {
                                addBasketSchemeTohashmapByOrderMatGrp(OrderMatGrp);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }else if(onSaleOnID.equalsIgnoreCase("000001")){  // Banner
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails +"?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if(alItems!=null && alItems.size()>0){
                    for (SchemeBean schemeBeanVal : alItems) {
                        if(Double.parseDouble(schemeBeanVal.getItemMin())>0){
                            if(mapBannerWiseQty.containsKey(schemeBeanVal.getBannerID())){
                                double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                if(calItemMinQty<=mapBannerWiseQty.get(schemeBeanVal.getBannerID())){

                                }else{
                                    isBasketschemeAval =true;
                                    break;
                                }
                            }else{
                                isBasketschemeAval =true;
                                break;
                            }
                        }

                    }
                }

                if(!isBasketschemeAval && alItems.size()>0){
                    for (SchemeBean schemeBeanVal : alItems) {
                        double calItemMinQty=0.0;
                        if(Double.parseDouble(schemeBeanVal.getItemMin())>0){
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                        }else{
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                        }
                        // adding banner wise min qty into map table
                        mapBasketBannerMinQty.put(schemeBeanVal.getBannerID(),calItemMinQty);

                        try {
                            ArrayList<String> mOrderGrp = OfflineManager.getOrderMatGrp(Constants.CPStockItems + "?$select=" + Constants.OrderMaterialGroupID + " &$filter = "
                                    + ConstantsUtils.Banner + " eq '" + schemeBeanVal.getBannerID() + "' and "+Constants.StockOwner + " eq '01' ");
                            for (String OrderMatGrp : mOrderGrp) {
                                addBasketSchemeTohashmapByOrderMatGrp(OrderMatGrp);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else if(onSaleOnID.equalsIgnoreCase("000004")){  // Scheme Material Group
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails +"?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if(alItems!=null && alItems.size()>0){
                    for (SchemeBean schemeBeanVal : alItems) {
                        if(Double.parseDouble(schemeBeanVal.getItemMin())>0){
                            if(mapSKUGrpWiseQty.containsKey(schemeBeanVal.getSKUGroupID())){
                                double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                if(calItemMinQty<=mapSKUGrpWiseQty.get(schemeBeanVal.getSKUGroupID())){

                                }else{
                                    isBasketschemeAval =true;
                                    break;
                                }
                            }else{
                                isBasketschemeAval =true;
                                break;
                            }
                        }

                    }
                }

                if(!isBasketschemeAval && alItems.size()>0){
                    for (SchemeBean schemeBeanVal : alItems) {
                        double calItemMinQty=0.0;
                        if(Double.parseDouble(schemeBeanVal.getItemMin())>0){
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                        }else{
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                        }
                        // adding banner wise min qty into map table
                        mapBasketSKUGRPMinQty.put(schemeBeanVal.getSKUGroupID(),calItemMinQty);

                        try {
                            ArrayList<String> mOrderGrp = OfflineManager.getOrderMatGrp(Constants.CPStockItems + "?$select=" + Constants.OrderMaterialGroupID + " &$filter = "
                                    + ConstantsUtils.SKUGroup + " eq '" + schemeBeanVal.getSKUGroupID() + "' and "+Constants.StockOwner + " eq '01' ");
                            for (String OrderMatGrp : mOrderGrp) {
                                addBasketSchemeTohashmapByOrderMatGrp(OrderMatGrp);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    private void getBasketSchemeCalByMaterial(String schemeGuid,String orderQty) {
        String onSaleOnID="",onRefID="";

        SchemeBean headerScheme=null;
        ArrayList<SchemeBean> alItems = null;
        try {
            headerScheme = OfflineManager.getBasketSchemeHeader(Constants.SchemeItemDetails +"?$select=" + ConstantsUtils.OnSaleOfCatID + ","
                    +Constants.SchemeItemGUID +","+Constants.ItemMin+" &$filter="
                    + Constants.SchemeGUID + " eq guid'" + schemeGuid.toUpperCase() + "' and "+Constants.ItemCatID+" eq '"+Constants.BasketCatID+"' &$top=1 ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(headerScheme!=null){
            onSaleOnID = headerScheme.getOnSaleOfCatID();
           if(onSaleOnID.equalsIgnoreCase("000006")){  // Material
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails +"?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if(alItems!=null && alItems.size()>0){
                    for (SchemeBean schemeBeanVal : alItems) {
                        if(!isBasketschemeAval){
                            for(SKUGroupBean skuGroupBean : alReviewSOItems){
                                if(Double.parseDouble(schemeBeanVal.getItemMin())>0){
                                    if(skuGroupBean.getMaterialNo().equalsIgnoreCase(schemeBeanVal.getMaterialNo())){
                                        double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                        if(calItemMinQty<=Double.parseDouble(skuGroupBean.getORDQty())){
                                            schemeBeanVal.setOrderQty(skuGroupBean.getORDQty());
                                            break;
                                        }else{
                                            isBasketschemeAval =true;
                                            break;
                                        }
                                    }else{
                                        if(!setMatList.contains(schemeBeanVal.getMaterialNo())){
                                            isBasketschemeAval =true;
                                            break;
                                        }
                                    }
                                }else{

                                    if(skuGroupBean.getMaterialNo().equalsIgnoreCase(schemeBeanVal.getMaterialNo())){
                                        schemeBeanVal.setOrderQty(skuGroupBean.getORDQty().equalsIgnoreCase("")?"0":skuGroupBean.getORDQty());
                                    }
                                }
                            }
                        }

                    }

                    if(!isBasketschemeAval && alItems.size()>0){
                        for (SchemeBean schemeBeanVal : alItems) {
                            addBasketSchemeTohashmapByMaterial(schemeBeanVal.getMaterialNo(),schemeBeanVal.getOrderQty());
                        }
                    }
                }

            }else if(onSaleOnID.equalsIgnoreCase("000003")){ // Product category

            }else if(onSaleOnID.equalsIgnoreCase("000002")){  // Brand

            }else if(onSaleOnID.equalsIgnoreCase("000001")){  // Banner

            }else if(onSaleOnID.equalsIgnoreCase("000004")){  // Scheme Material Group

            }
        }

    }
    private void addBasketSchemeTohashmapByOrderMatGrp(String orderMatgrp){
        // Todo check scheme calculation  HDR SLAB BASED/ ITEM SLAB BASED
        if (!isSchemeHeaderBasedOrItemBased(Constants.MAPSCHGuidByCrsSkuGrp.get(orderMatgrp))) {
            SchemeBean schemeBean = getSecSchemeBeanByCrsSKUGrp((Constants.MAPSCHGuidByCrsSkuGrp.get(orderMatgrp)), orderMatgrp, Constants.MAPORDQtyByCrsSkuGrp.get(orderMatgrp), "",Constants.SchemeTypeBasket);
            if (schemeBean != null) {
                hashMapSchemeValByOrderMatGrp.put(orderMatgrp, schemeBean);
            }
        } else {
            SchemeBean schemeBean = getSecSchemeBeanByCrsSKUGrp((Constants.MAPSCHGuidByCrsSkuGrp.get(orderMatgrp)), orderMatgrp, Constants.MAPORDQtyByCrsSkuGrp.get(orderMatgrp), Constants.X,Constants.SchemeTypeBasket);
            if (schemeBean != null) {
                hashMapSchemeValByOrderMatGrp.put(orderMatgrp, schemeBean);
            }
        }
    }

    private void addBasketSchemeTohashmapByMaterial(String matNo,String orderQty){
        // Todo check scheme calculation  HDR SLAB BASED/ ITEM SLAB BASED
        if (!isSchemeHeaderBasedOrItemBased(Constants.MAPSCHGuidByMaterial.get(matNo))) {
            SchemeBean schemeBean = getSecSchemeBeanByMaterial((Constants.MAPSCHGuidByMaterial.get(matNo)),
                    matNo, orderQty, "");
            if (schemeBean != null) {
                hashMapSchemeValByMaterial.put(matNo, schemeBean);
            }
        } else {
            SchemeBean schemeBean = getSecSchemeBeanByMaterial((Constants.MAPSCHGuidByMaterial.get(matNo)),
                    matNo, orderQty, Constants.X);
            if (schemeBean != null) {
                hashMapSchemeValByMaterial.put(matNo, schemeBean);
            }
        }
    }

    private boolean isSchemeHeaderBasedOrItemBased(String mStrSchemeGuid) {
        boolean mBoolHeadWiseScheme = false;
        String mStrSchemeQry = Constants.Schemes + "?$filter= " + Constants.SchemeGUID +
                " eq guid'" + mStrSchemeGuid + "' and  " + Constants.IsHeaderBasedSlab + " eq 'X' ";
        try {
            mBoolHeadWiseScheme = OfflineManager.getVisitStatusForCustomer(mStrSchemeQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mBoolHeadWiseScheme;
    }

    private boolean isSchemeInstantOrNot(String mStrSchemeGuid) {
        boolean mBoolHeadWiseScheme = false;
        String mStrSchemeQry = Constants.Schemes + "?$filter= " + Constants.SchemeGUID +
                " eq guid'" + mStrSchemeGuid + "' and  " + Constants.SchemeCatID + " eq '" + Constants.SchemeCatIDInstantScheme + "' ";
        try {
            mBoolHeadWiseScheme = OfflineManager.getVisitStatusForCustomer(mStrSchemeQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mBoolHeadWiseScheme;
    }



    private Map<String, SKUGroupBean> getALSKUGRP(ArrayList<SKUGroupBean> alSKUList) {
        Map<String, SKUGroupBean> mapSKUList = new HashMap<>();
        if (alSKUList != null && alSKUList.size() > 0) {
            for (SKUGroupBean bean : alSKUList)
                if (mapNetAmt.containsKey(bean.getSKUGroup())) {
                    BigDecimal mDoubCRSQty = null;
                    try {
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        decimalFormat.setParseBigDecimal(true);
                        mDoubCRSQty = (BigDecimal) decimalFormat.parse(bean.getORDQty());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (mDoubCRSQty != null) {
                        mDoubCRSQty = mDoubCRSQty.add(mapCRSSKUQTY.get(bean.getSKUGroup()));
                    }else {
                        mDoubCRSQty = mapCRSSKUQTY.get(bean.getSKUGroup());
                    }
                    double mDouNetPrice = Double.parseDouble(bean.getNetAmount()) + mapNetAmt.get(bean.getSKUGroup());
                    double mDouPriSchPer = Double.parseDouble(bean.getPRMScheme()) + mapPriSchemePer.get(bean.getSKUGroup());
                    double mDouSecSchPer = Double.parseDouble(bean.getSecScheme()) + mapSecSchemePer.get(bean.getSKUGroup());
                    double mDouSecSchAmt = Double.parseDouble(bean.getSecSchemeAmt()) + mapSecSchemeAmt.get(bean.getSKUGroup());


                    int matCountInc =mapCntMatByCRSKUGRP.get(bean.getSKUGroup());
                    mapCntMatByCRSKUGRP.put(bean.getSKUGroup(), matCountInc+1 );

                    mapSecSchemeAmt.put(bean.getSKUGroup(), mDouSecSchAmt);
                    mapSecSchemePer.put(bean.getSKUGroup(), mDouSecSchPer);
                    mapPriSchemePer.put(bean.getSKUGroup(), mDouPriSchPer);
                    mapNetAmt.put(bean.getSKUGroup(), mDouNetPrice);
                    mapCRSSKUQTY.put(bean.getSKUGroup(), mDoubCRSQty);
                    mapUOM.put(bean.getSKUGroup(), bean.getUOM());
                    mapSKUList.put(bean.getSKUGroup(), bean);
                } else {
                    double mDoubNetAmt = Double.parseDouble(bean.getNetAmount());
                    double mDouPriSchPer = Double.parseDouble(bean.getPRMScheme());
                    double mDouSecSchPer = Double.parseDouble(bean.getSecScheme());
                    double mDouSecSchAmt = Double.parseDouble(bean.getSecSchemeAmt());
                    BigDecimal mDoubOrderQty = null;
                    try {
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        decimalFormat.setParseBigDecimal(true);
                        mDoubOrderQty = (BigDecimal) decimalFormat.parse(bean.getORDQty());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mapSecSchemeAmt.put(bean.getSKUGroup(), mDouSecSchAmt);
                    mapCntMatByCRSKUGRP.put(bean.getSKUGroup(),1);
                    mapNetAmt.put(bean.getSKUGroup(), mDoubNetAmt);
                    mapCRSSKUQTY.put(bean.getSKUGroup(), mDoubOrderQty);
                    mapPriSchemePer.put(bean.getSKUGroup(), mDouPriSchPer);
                    mapSecSchemePer.put(bean.getSKUGroup(), mDouSecSchPer);
                    mapUOM.put(bean.getSKUGroup(), bean.getUOM());
                    mapSKUList.put(bean.getSKUGroup(), bean);
                }
        }

        return mapSKUList;
    }

    private void displayReviewPage() {
        TableLayout tlCRSList = (TableLayout) findViewById(R.id.crs_sku);
        TableLayout tlSOList = (TableLayout) findViewById(R.id.report_table);
        tlCRSList.removeAllViews();
        tlSOList.removeAllViews();

        TableLayout tlSKUGroupItem = (TableLayout) LayoutInflater.from(this)
                .inflate(R.layout.tl_so_review_item_heading, null, false);
        TableLayout tlCRSKUGroup = (TableLayout) LayoutInflater.from(this)
                .inflate(R.layout.tl_crsskugrp_heading, null, false);

        tlCRSList.addView(tlCRSKUGroup);
        tlSOList.addView(tlSKUGroupItem);

        LinearLayout llSKUGroupItem = null;
        LinearLayout llCRSKUGroup = null;

        if (!mapSKUGRPVal.isEmpty()) {

            Iterator iterator = mapSKUGRPVal.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
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
                tv_sku_grp_desc.setText(skuGroupBean.getSKUGroupDesc());
                Constants.setFontSizeByMaxText(tv_sku_grp_desc);
                if (!skuGroupBean.getUOM().equalsIgnoreCase(""))
                    tv_ord_qty.setText(OfflineManager.trimQtyDecimalPlace(mapCRSSKUQTY.get(key).toString()) + " " + skuGroupBean.getUOM());
                else
                    tv_ord_qty.setText(String.format("%.3f", mapCRSSKUQTY.get(key)));

                double avgPriDisVal=0.0;
                try {
                    avgPriDisVal = mapPriSchemePer.get(key)/mapCntMatByCRSKUGRP.get(key);
                } catch (Exception e) {
                    avgPriDisVal =0.0;
                }
                double avgSecDisVal=0.0;
                try {
                    avgSecDisVal = mapSecSchemePer.get(key)/mapCntMatByCRSKUGRP.get(key);
                } catch (Exception e) {
                    avgSecDisVal =0.0;
                }
                double avgSecDisAmtVal=0.0;
                try {
                    avgSecDisAmtVal = mapSecSchemeAmt.get(key)/mapCntMatByCRSKUGRP.get(key);
                } catch (Exception e) {
                    avgSecDisAmtVal =0.0;
                }
                tv_primary_scheme.setText(UtilConstants.removeLeadingZerowithTwoDecimal(avgPriDisVal+""));
                tv_sec_scheme.setText(UtilConstants.removeLeadingZerowithTwoDecimal(avgSecDisVal+""));
                tv_sec_scheme_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(avgSecDisAmtVal+""));
                tv_net_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mapNetAmt.get(key).toString()));

                tlSOList.addView(llSKUGroupItem);
                tlCRSList.addView(llCRSKUGroup);

                if(alRevTempItems.size()>0){
                    for(SchemeBean schemeBean : alRevTempItems){
                        if(key.equalsIgnoreCase(schemeBean.getOrderMaterialGroupID())){
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
                            tv_net_amt.setVisibility(View.INVISIBLE);
                            tv_ord_qty.setText(schemeBean.getFreeQty());
                            tv_sec_scheme.setVisibility(View.INVISIBLE);
                            tv_primary_scheme.setVisibility(View.INVISIBLE);
                            tlSOList.addView(llSKUGroupItem);
                            tlCRSList.addView(llCRSKUGroup);
                            break;
                        }
                    }
                }

                try {
                    if(hashMapFreeMatByOrderMatGrp.containsValue(key)){

                        Set<String> keys = Constants.getKeysByValue(hashMapFreeMatByOrderMatGrp,key);
                        if (keys != null && !keys.isEmpty()) {
                            Iterator itr = keys.iterator();
                            while (itr.hasNext()) {
                                SchemeBean freeMATBean = hashMapFreeMaterialByMaterial.get(itr.next().toString());

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
                                tv_sku_grp_desc.setText(freeMATBean.getFreeMaterialNo());
                                Constants.setFontSizeByMaxText(tv_sku_grp_desc);
                                tv_net_amt.setVisibility(View.INVISIBLE);
                                tv_ord_qty.setText(freeMATBean.getFreeQty());
                                tv_sec_scheme.setVisibility(View.INVISIBLE);
                                tv_primary_scheme.setVisibility(View.INVISIBLE);
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

            LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(SalesOrderReviewActivity.this)
                    .inflate(R.layout.so_review_empty_layout, null);

            tlSOList.addView(llEmptyLayout);
        }
    }


    private SchemeBean getPrimaryTaxValByMaterial(String cPStockItemGUID, String mStrMatNo, String mStrOrderQty) {

        SchemeBean mStrNetAmount = null;
        try {

            mStrNetAmount = OfflineManager.getNetAmount(Constants.CPStockItemSnos + "?$filter=" + Constants.MaterialNo + " eq '" + mStrMatNo + "' and "
                    + Constants.CPStockItemGUID + " eq guid'" + cPStockItemGUID + "' and "+Constants.StockTypeID+" ne '"+Constants.str_3+"'  &$orderby=" + Constants.ManufacturingDate + "%20asc ", mStrOrderQty, mStrMatNo);
//            and "+Constants.ManufacturingDate+" ne null
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mStrNetAmount;
    }

    private SchemeBean getFreeMaterial(String cPStockItemGUID, String mStrMatNo, String mStrOrderQty) {

        SchemeBean mStrNetAmount = null;
        try {

            mStrNetAmount = OfflineManager.getFreeMaterial(Constants.CPStockItemSnos + "?$filter=" + Constants.MaterialNo + " eq '" + mStrMatNo + "' and "
                    + Constants.CPStockItemGUID + " eq guid'" + cPStockItemGUID + "'  &$orderby=" + Constants.ManufacturingDate + "%20asc ", mStrOrderQty, mStrMatNo);
//            and "+Constants.ManufacturingDate+" ne null
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return mStrNetAmount;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                onSave();
                break;
        }
        return true;
    }

    private void saveAsyncTask() {
        try {
            new SaveValToDataVault().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/*AsyncTask to save vales into datavault*//*

    private class SaveValToDataVault extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(SalesOrderReviewActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.saving_data_plz_wait));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            onSaveValesToDataVault();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            navigateToVisit();
        }
    }


    private void onSaveValesToDataVault(){
        String doc_no = (System.currentTimeMillis() + "");

        GUID ssoHeaderGuid = GUID.newRandom();
        headerTable.put(Constants.SSSOGuid, ssoHeaderGuid.toString36().toUpperCase());
        headerTable.put(Constants.OrderNo, doc_no);
        String ordettype = "";
        try {
            ordettype = OfflineManager.getValueByColumnName(Constants.ValueHelps+ "?$select=" + Constants.ID + " &$filter="+Constants.EntityType+" eq 'SSSO' and  " +
                    ""+Constants.PropName+" eq 'OrderType' and  "+Constants.ParentID+" eq '000010' ",Constants.ID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        headerTable.put(Constants.OrderType, ordettype);
        headerTable.put(Constants.OrderTypeDesc, "");
        headerTable.put(Constants.OrderDate, UtilConstants.getNewDateTimeFormat());
        headerTable.put(Constants.DmsDivision, mArrayCPDMSDivisoins[0][0] != null ? mArrayCPDMSDivisoins[0][0] : "");
        headerTable.put(Constants.DmsDivisionDesc, mArrayCPDMSDivisoins[1][0] != null ? mArrayCPDMSDivisoins[1][0] : "");
        headerTable.put(Constants.PONo, "");
        headerTable.put(Constants.PODate, UtilConstants.getNewDateTimeFormat());
        headerTable.put(Constants.FromCPGUID, mArrayDistributors[4][0]);
        headerTable.put(Constants.FromCPNo, mArrayDistributors[4][0]);
        headerTable.put(Constants.FromCPName, mArrayDistributors[7][0]);
        headerTable.put(Constants.FromCPTypId, mArrayDistributors[5][0]);
        headerTable.put(Constants.FromCPTypDs, "");
        headerTable.put(Constants.CPGUID, mStrBundleCPGUID);
        headerTable.put(Constants.CPNo, mStrBundleRetID);
        headerTable.put(Constants.CPName, mStrBundleRetName);
        headerTable.put(Constants.CPType, Constants.str_02);
        headerTable.put(Constants.CPTypeDesc, mArrayDistributors[9][0]);
        headerTable.put(Constants.SoldToCPGUID, mStrBundleCPGUID);
        headerTable.put(Constants.SoldToId, mStrBundleRetID);
        headerTable.put(Constants.SoldToUID, mStrBundleRetailerUID);
        headerTable.put(Constants.SoldToDesc, mStrBundleRetName);
        headerTable.put(Constants.SoldToType, mArrayDistributors[8][0]);
        headerTable.put(Constants.SPGUID, mArraySPValues[4][0]!= null ? mArraySPValues[4][0] : "");
        headerTable.put(Constants.SPNo, mArraySPValues[6][0]!= null ? mArraySPValues[6][0] : "");
        headerTable.put(Constants.FirstName, mArrayDistributors[3][0]);
        headerTable.put(Constants.GrossAmt, "0");

        headerTable.put(Constants.Currency, mArrayDistributors[10][0]);
        headerTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
        headerTable.put(Constants.CreatedAt, UtilConstants.getOdataDuration().toString());
        headerTable.put(Constants.TLSD, mStrCrsSku.size() + "");

        ArrayList<HashMap<String, String>> soItems = new ArrayList<>();
        Double mDouTotNetAmt = 0.0, mDouNetAmt = 0.0,mDouGrossAmt=0.0,mDouTotalGrossAmt=0.0,mDouTotalOrderQty=0.0;
        int incItemCountVal = 0;
        for (SKUGroupBean skuGroupBeanItem : alReviewSOItems) {
            if (skuGroupBeanItem.getMaterialBatchBean() != null) {
                SchemeBean mGetMatBatchInfo = skuGroupBeanItem.getMaterialBatchBean();
                if (mGetMatBatchInfo != null) {
                    ArrayList<MaterialBatchBean> alMatBatchItemBean = mGetMatBatchInfo.getMaterialBatchBeanArrayList();
                    for (MaterialBatchBean matBatchItemBean : alMatBatchItemBean) {
                        HashMap<String, String> singleItem = new HashMap<>();
                        GUID ssoItemGuid = GUID.newRandom();
                        singleItem.put(Constants.SSSOItemGUID, ssoItemGuid.toString36().toUpperCase());
                        singleItem.put(Constants.SSSOGuid, ssoHeaderGuid.toString36().toUpperCase());
                        singleItem.put(Constants.ItemNo, (incItemCountVal + 1) + "");
                        singleItem.put(Constants.MaterialNo, skuGroupBeanItem.getMaterialNo());
                        singleItem.put(Constants.MaterialDesc, skuGroupBeanItem.getMaterialDesc());
                        singleItem.put(Constants.OrderMatGrp, skuGroupBeanItem.getSKUGroup());
                        singleItem.put(Constants.OrderMatGrpDesc, skuGroupBeanItem.getSKUGroupDesc());
                        singleItem.put(Constants.Currency, mArrayDistributors[10][0]);
                        singleItem.put(Constants.Uom, skuGroupBeanItem.getUOM());
                        singleItem.put(Constants.UnitPrice, matBatchItemBean.getLandingPrice());
                        try {
                            mDouNetAmt = Double.parseDouble(matBatchItemBean.getTotalNetAmt());
                            mDouTotNetAmt = mDouTotNetAmt + Double.parseDouble(matBatchItemBean.getTotalNetAmt());

                            mDouGrossAmt = Double.parseDouble(matBatchItemBean.getGrossAmt());
                            mDouTotalGrossAmt = mDouTotalGrossAmt + Double.parseDouble(matBatchItemBean.getGrossAmt());

                            mDouTotalOrderQty = mDouTotalOrderQty + Double.parseDouble(matBatchItemBean.getQty());
                        } catch (NumberFormatException e) {
                            mDouNetAmt = 0.0;
                            mDouTotNetAmt = 0.0;

                            mDouGrossAmt = 0.0;
                            mDouTotalGrossAmt = 0.0;
                        }
                        singleItem.put(Constants.Quantity, matBatchItemBean.getQty());
                        singleItem.put(Constants.NetPrice, mDouNetAmt + "");
                        singleItem.put(Constants.MRP, matBatchItemBean.getMRP().equalsIgnoreCase("")?"0":matBatchItemBean.getMRP());
                        singleItem.put(Constants.GrossAmt, mDouGrossAmt+"");
                        singleItem.put(Constants.TAX, matBatchItemBean.getTax().equalsIgnoreCase("")?"0":matBatchItemBean.getTax());

                        singleItem.put(Constants.SecDiscount, */
/*matBatchItemBean.getSecDiscountAmt().equalsIgnoreCase("")?"0":matBatchItemBean.getSecDiscountAmt()*//*
"0");
                        singleItem.put(Constants.PriDiscount, */
/*matBatchItemBean.getNetAmtAftPriDis().equalsIgnoreCase("")?"0":matBatchItemBean.getNetAmtAftPriDis()*//*
 "0");
                        singleItem.put(Constants.CashDiscount, "0");
                        singleItem.put(Constants.CashDiscountPerc, "0");
                        singleItem.put(Constants.SecondaryDiscountPerc, matBatchItemBean.getSecPer().equalsIgnoreCase("")?"0":matBatchItemBean.getSecPer());
                        singleItem.put(Constants.PrimaryDiscountPerc, matBatchItemBean.getPrimaryPer().equalsIgnoreCase("")?"0":matBatchItemBean.getPrimaryPer());

                        singleItem.put(Constants.Batch, matBatchItemBean.getBatchNo());
                        singleItem.put(Constants.MFD, ConstantsUtils.convertDateFromString(matBatchItemBean.getMFD()));
                        singleItem.put(Constants.IsfreeGoodsItem, "");
                        soItems.add(singleItem);
                        incItemCountVal++;
                    }
                }
            }
        }
        headerTable.put(Constants.NetPrice, mDouTotNetAmt + "");
        headerTable.put(Constants.Quantity, mDouTotalOrderQty + "");
        headerTable.put(Constants.GrossAmount, mDouTotalGrossAmt + "");
        headerTable.put(Constants.entityType, Constants.SecondarySOCreate);
        headerTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(soItems));
        headerTable.put(Constants.TestRun, Constants.TestRun_Text);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);


        Constants.saveDeviceDocNoToSharedPref(SalesOrderReviewActivity.this, Constants.SOList, doc_no);

        headerTable.put(Constants.LOGINID, sharedPreferences.getString(Constants.username, "").toUpperCase());

        JSONObject jsonHeaderObject = new JSONObject(headerTable);

        UtilDataVault.storeInDataVault(doc_no, jsonHeaderObject.toString());

        Constants.onVisitActivityUpdate(mStrBundleCPGUID32, sharedPreferences.getString(Constants.username, ""),
                ssoHeaderGuid.toString36().toUpperCase(), Constants.SOCreateID, Constants.SecondarySOCreate);
    }

    private void onSave() {
        if(Constants.isValidTime( UtilConstants.convertTimeOnly(Constants.mStartTimeDuration.toString()),
                UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString())) && Constants.isValidDate()) {
            if (mStrCrsSku.size() > 0) {
                if (!Constants.onGpsCheck(SalesOrderReviewActivity.this)) {
                    return;
                }
                if(!UtilConstants.getLocation(SalesOrderReviewActivity.this)){
                    return;
                }
                saveAsyncTask();
            } else {
                UtilConstants.showAlert(getString(R.string.alert_enter_atlest_one_material), SalesOrderReviewActivity.this);
            }
        }else{
            UtilConstants.showAlert(getString(R.string.validation_app_time_incorrect), SalesOrderReviewActivity.this);
        }


    }

    */
/*Navigate to day summary screen*//*

    public void navigateToVisit() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                SalesOrderReviewActivity.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.msg_secondary_so_created))
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    Constants.ComingFromCreateSenarios = Constants.X;
                                    onDaySummary();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
        builder.show();
    }

    */
/*Navigates to Day Summary*//*

    private void onDaySummary() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intDaySummary = new Intent(this,
                DaySummaryActivity.class);
        intDaySummary.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intDaySummary.putExtra(Constants.CPNo, mStrBundleRetID);
        intDaySummary.putExtra(Constants.RetailerName, mStrBundleRetName);
        intDaySummary.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intDaySummary.putExtra(Constants.comingFrom, mStrComingFrom);
        intDaySummary.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        if (!Constants.OtherRouteNameVal.equalsIgnoreCase("")) {
            intDaySummary.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intDaySummary.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }
        startActivity(intDaySummary);
    }

    private SchemeBean getSecSchemeBeanByCrsSKUGrp(String schemeGUID, String mStrCRSSKUGrp, String mStrOrderQty, String mStrHeaderOrItemType,String schemeType) {
        SchemeBean schemeBean = null;
        String mStrSchemeItemGuid = "";
        String getCondition="";


            String mStrMinOrderQty = "0";
            try {
                mStrMinOrderQty = OfflineManager.getQtyValueByColumnName(Constants.SchemeItemDetails + "?$filter="
                        + Constants.SchemeGUID + " eq guid'" + schemeGUID + "'   ", Constants.ItemMin);
//                &$top=1
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            boolean mBoolMinItemQtyAval = false;
            if (Double.parseDouble(mStrMinOrderQty) >= 0) {

                if(Double.parseDouble(mStrOrderQty.equalsIgnoreCase("")?"0":mStrOrderQty)>0) {
                    if (Double.parseDouble(mStrMinOrderQty) <= Double.parseDouble(mStrOrderQty)) {
                        mBoolMinItemQtyAval = true;
                    } else {
                        mBoolMinItemQtyAval = false;
                    }
                }
            } else {
                mBoolMinItemQtyAval = true;
            }

            if (mBoolMinItemQtyAval) {
                if(!mStrHeaderOrItemType.equalsIgnoreCase(Constants.X)){
                    String[] orderMatGrpArray = new String[1];
                    orderMatGrpArray[0] = mStrCRSSKUGrp;
                    try {
                        schemeBean = OfflineManager.getSecondarySchemeSlabPer(Constants.SchemeSlabs + "?$filter="
                                + Constants.SchemeItemGUID + " eq guid'" + schemeGUID.toUpperCase() + "' ",
                                mStrOrderQty, schemeGUID,Constants.SKUGroupID,orderMatGrpArray,mStrHeaderOrItemType);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }else{
                    boolean schemeISAval = false;
                    if(setSchemeList.size()==0){
                        schemeISAval = true;
                    }else if(!setSchemeList.contains(schemeGUID)){
                        schemeISAval = true;
                    }
                    if(schemeISAval){
                        Multimap<String, String> multiMap = HashMultimap.create();
                        for (Map.Entry<String, String> entry : Constants.MAPSCHGuidByCrsSkuGrp.entrySet()) {
                            if(!entry.getValue().equalsIgnoreCase("")) {
                                multiMap.put(entry.getValue(), entry.getKey());
                            }
                        }
                        String[] orderMatGrpArray = new String[multiMap.get(schemeGUID).size()];
                        Double mStrSumQtyBySKU = 0.0;
                        for (Map.Entry<String, Collection<String>> entry : multiMap.asMap().entrySet()) {
                            if(schemeGUID.equalsIgnoreCase(entry.getKey())){
                                Collection<String> values = entry.getValue();
                                int index=0;
                                for (String value : values) {
                                        if (Double.parseDouble(Constants.MAPORDQtyByCrsSkuGrp.get(value).equalsIgnoreCase("") ? "0" : Constants.MAPORDQtyByCrsSkuGrp.get(value)) > 0) {
                                            orderMatGrpArray[index] = value;
                                            mStrSumQtyBySKU = mStrSumQtyBySKU + Double.parseDouble(Constants.MAPORDQtyByCrsSkuGrp.get(value));
                                            index++;
                                        }
                                }
                            }
                        }

                        try {
                            schemeBean = OfflineManager.getSecondarySchemeSlabPer(Constants.SchemeSlabs + "?$filter="
                                    + Constants.SchemeItemGUID + " eq guid'" + schemeGUID.toUpperCase() + "' ", mStrSumQtyBySKU+"",
                                    schemeGUID,Constants.SKUGroupID,orderMatGrpArray,mStrHeaderOrItemType);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }

                        if(schemeBean!=null){
                            orderMatGrp = orderMatGrpArray;
                            mapHeaderWiseSchemeQty.put(schemeGUID.toUpperCase(),mStrSumQtyBySKU+"");
                            mFreeMat = null;
                            Double mDouSlabTypeCal=0.0;
                            if(schemeBean.getTargetBasedID().equalsIgnoreCase("02")){
                                mDouSlabTypeCal = getSecondaryDiscountOrAmtOrFreeQtyHeaderWise(schemeBean, schemeBean.getSlabRuleID(),
                                        mStrSumQtyBySKU+"", "", "", "",Constants.X);
                            }else{
                                String mStrFreeQty ="";
                                    if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)){
                                        mStrFreeQty = schemeBean.getPayoutPerc();
                                    }else if(schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)){
                                        mStrFreeQty = schemeBean.getPayoutAmount();
                                    }else{
                                        mStrFreeQty = schemeBean.getFreeQty();
                                    }
                                        mDouSlabTypeCal = getSchSlabTypeIDCalculation(schemeBean.getSlabTypeID(),
                                                mStrFreeQty, schemeBean.getToQty(), mStrSumQtyBySKU+"", schemeBean.getSlabRuleID(),
                                        schemeGUID.toUpperCase(),schemeBean.getFromQty(),Constants.X);
                            }



                            for(String mStrCRSSKUGRP:orderMatGrpArray) {
                                FreeProduct(schemeBean,schemeBean.getSlabRuleID(),mStrCRSSKUGRP,"",mDouSlabTypeCal+"");
                                hashMapSchemeValByOrderMatGrp.put(mStrCRSSKUGRP, schemeBean);
                                mapSchemePerORAmtByOrderMatGrp.put(mStrCRSSKUGRP,mDouSlabTypeCal+"");
                                mapSchemeFreeMatByOrderMatGrp.put(mStrCRSSKUGRP,mFreeMat);
                            }
                        }

                        setSchemeList.add(schemeGUID.toUpperCase());
                    }
                }






            } else {
                schemeBean = null;
            }
        return schemeBean;
    }

    private SchemeBean getSecSchemeBeanByMaterial(String schemeGUID, String mStrMatNo, String mStrOrderQty, String mStrHeaderOrItemType) {
        SchemeBean schemeBean = null;
            String mStrMinOrderQty = "0";
            try {
                mStrMinOrderQty = OfflineManager.getQtyValueByColumnName(Constants.SchemeItemDetails + "?$filter="
                        + Constants.SchemeGUID + " eq guid'" + schemeGUID + "' and " + Constants.MaterialNo + " eq '" + mStrMatNo + "'  ", Constants.ItemMin);
//                &$top=1
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            boolean mBoolMinItemQtyAval = false;
            if (Double.parseDouble(mStrMinOrderQty) >= 0) {
                if(Double.parseDouble(mStrOrderQty.equalsIgnoreCase("")?"0":mStrOrderQty)>0) {
                    if (Double.parseDouble(mStrMinOrderQty) <= Double.parseDouble(mStrOrderQty)) {
                        mBoolMinItemQtyAval = true;
                    } else {
                        mBoolMinItemQtyAval = false;
                    }
                }

            } else {
                mBoolMinItemQtyAval = true;
            }

            if (mBoolMinItemQtyAval) {
                if(!mStrHeaderOrItemType.equalsIgnoreCase(Constants.X)){
                    String[] orderMatArray = new String[1];
                    orderMatArray[0] = mStrMatNo;
                    try {
                        schemeBean = OfflineManager.getSecondarySchemeSlabPer(Constants.SchemeSlabs + "?$filter="
                                        + Constants.SchemeItemGUID + " eq guid'" + schemeGUID.toUpperCase() + "' ", mStrOrderQty,
                                schemeGUID,Constants.Material,orderMatArray,mStrHeaderOrItemType);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }else {
                    // ToDO here write logic  Constants.MAPSCHGuidByMaterial
                    boolean schemeISAval = false;
                    if(setSchemeList.size()==0){
                        schemeISAval = true;
                    }else if(!setSchemeList.contains(schemeGUID)){
                        schemeISAval = true;
                    }

                    if(schemeISAval) {
                        Multimap<String, String> multiMap = HashMultimap.create();
                        for (Map.Entry<String, String> entry : Constants.MAPSCHGuidByMaterial.entrySet()) {
                            if (!entry.getValue().equalsIgnoreCase("")) {
                                multiMap.put(entry.getValue(), entry.getKey());
                            }
                        }
                        String[] materialGrpArray = new String[multiMap.get(schemeGUID).size()];
                        Double mStrSumQtyByMat = 0.0;
                        for (Map.Entry<String, Collection<String>> entry : multiMap.asMap().entrySet()) {
                            if(schemeGUID.equalsIgnoreCase(entry.getKey())){
                                Collection<String> values = entry.getValue();
                                int index=0;
                                for (String value : values) {
                                    if(mapMaterialWiseQty.containsKey(value)) {
                                        if (mapMaterialWiseQty.get(value) > 0) {
                                            materialGrpArray[index] = value;
                                            mStrSumQtyByMat = mStrSumQtyByMat + mapMaterialWiseQty.get(value);
                                            index++;
                                        }
                                    }
                                }
                            }
                        }

                        try {
                            schemeBean = OfflineManager.getSecondarySchemeSlabPer(Constants.SchemeSlabs + "?$filter="
                                            + Constants.SchemeItemGUID + " eq guid'" + schemeGUID.toUpperCase() + "' ", mStrSumQtyByMat+"",
                                    schemeGUID,Constants.Material,materialGrpArray,mStrHeaderOrItemType);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }

                        if(schemeBean!=null){
                            mapHeaderWiseSchemeQty.put(schemeGUID.toUpperCase(),mStrSumQtyByMat+"");
                            mFreeMat = null;
                            Double mDouSlabTypeCal=0.0;
                            if(schemeBean.getTargetBasedID().equalsIgnoreCase("02")){
                                mDouSlabTypeCal = getSecondaryDiscountOrAmtOrFreeQtyHeaderWise(schemeBean, schemeBean.getSlabRuleID(),
                                        mStrSumQtyByMat+"", "", "", "",Constants.X);
                            }else{
                                String mStrFreeQty ="";
                                if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)){
                                    mStrFreeQty = schemeBean.getPayoutPerc();
                                }else if(schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)){
                                    mStrFreeQty = schemeBean.getPayoutAmount();
                                }else{
                                    mStrFreeQty = schemeBean.getFreeQty();
                                }
                                mDouSlabTypeCal = getSchSlabTypeIDCalculation(schemeBean.getSlabTypeID(),
                                        mStrFreeQty, schemeBean.getToQty(), mStrSumQtyByMat+"", schemeBean.getSlabRuleID(),
                                        schemeGUID.toUpperCase(),schemeBean.getFromQty(),Constants.X);
                            }



                            for(String mStrMaterial:materialGrpArray) {
                                FreeProduct(schemeBean,schemeBean.getSlabRuleID(),mStrMaterial,"",mDouSlabTypeCal+"");
                                hashMapSchemeValByMaterial.put(mStrMaterial, schemeBean);
                                mapSchemePerORAmtByMaterial.put(mStrMaterial,mDouSlabTypeCal+"");
                                mapSchemeFreeMatByMaterial.put(mStrMaterial,mFreeMat);
                            }
                        }

                        setSchemeList.add(schemeGUID.toUpperCase());

                    }
                }



            } else {
                schemeBean = null;
            }

        return schemeBean;
    }

    */
/**
     * @param SlabTypeID
     * @param freePerOrQty
     * @param slabTOQty
     * @param orderQty
     * @param mStrSchemeItemGuid
     * @return slabTypeCalValue
     *//*

    private double getSchSlabTypeIDCalculation(String SlabTypeID, String freePerOrQty, String slabTOQty,
                                               String orderQty, String mStrSlabRuleId,
                                               String mStrSchemeItemGuid,String mStrSlabFromQty,String isHeaderBased) {
        Double mDoubSlabCal = 0.0;
        if (SlabTypeID.equalsIgnoreCase("000001")) { // TODO  000001	Running
            Constants.DoubGetRunningSlabPer = 0.0;
            try {
                OfflineManager.getSecondarySchemeSlabPerRunning(Constants.SchemeSlabs + "?$filter="
                        + Constants.SchemeItemGUID + " eq guid'" + mStrSchemeItemGuid.toUpperCase() + "' ",
                        orderQty + "", mStrSchemeItemGuid,"",null,isHeaderBased);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            mDoubSlabCal = Constants.DoubGetRunningSlabPer;
        } else if (SlabTypeID.equalsIgnoreCase("000002")) { // TODO  000002	Fixed
            try {
                mDoubSlabCal = Double.parseDouble(freePerOrQty) * 1;
            } catch (NumberFormatException e) {
                mDoubSlabCal = 0.0;
            }
        } else if (SlabTypeID.equalsIgnoreCase("000003")) { // TODO  000003	Step
                BigInteger mDouCalStep;
                BigInteger mBigCalStep;
                BigInteger mBigResultValue = null;
                try {
                    mDouCalStep = new BigInteger(UtilConstants.removeLeadingZeroVal(orderQty + ""));
                    mBigCalStep = new BigInteger(UtilConstants.removeLeadingZeroVal(mStrSlabFromQty + ""));
                    mBigResultValue = mDouCalStep.divide(mBigCalStep);
                } catch (NumberFormatException e) {
                    mDouCalStep = new BigInteger("0");
                    mBigResultValue = new BigInteger("0");
                }

                try {
                    mDoubSlabCal = Double.parseDouble(freePerOrQty) * mBigResultValue.doubleValue();
                } catch (NumberFormatException e) {
                    mDoubSlabCal = 0.0;
                }

        } else if (SlabTypeID.equalsIgnoreCase("000004")) { // TODO  000004	Linear
            try {
                mDoubSlabCal = Double.parseDouble(orderQty) / Double.parseDouble(mStrSlabFromQty) * Double.parseDouble(freePerOrQty);
            } catch (NumberFormatException e) {
                mDoubSlabCal = 0.0;
            }
            if (mDoubSlabCal.isInfinite() || mDoubSlabCal.isNaN()) {
                mDoubSlabCal = 0.0;
            }

            // Changed code date 19-05-2017
            if(mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage) || mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)){
                mDoubSlabCal = Double.valueOf(mDoubSlabCal);
            }else{
                mDoubSlabCal = Double.valueOf(round(mDoubSlabCal));  // Round off only QTY
            }

        }
        return mDoubSlabCal;
    }

    */
/**
     * @param SlabTypeID
     * @param freePerOrQty
     * @param slabTOValue
     * @param orderQty
     * @param mStrSchemeItemGuid
     * @return slabTypeCalValue
     *//*

    private double getSchSlabTypeIDCalculationTargetByAmount(String SlabTypeID, String freePerOrQty, String slabTOValue, String orderQty,
                                                             String mStrSlabRuleId, String mStrSchemeItemGuid,
                                                             String mMatNo,String mStrTargetBasedID,String mStrSlabFromVal,String isHeaderBased) {
        Double mDoubSlabCal = 0.0;
        if (SlabTypeID.equalsIgnoreCase("000001")) { // TODO  000001	Running
            Constants.DoubGetRunningSlabPer = 0.0;
            String[] orderMatArray=null;
            if (!isHeaderBased.equalsIgnoreCase(Constants.X)){
                orderMatArray = new String[1];
                orderMatArray[0] = mMatNo;
            }else {
                orderMatArray = orderMatGrp;
            }
            try {
                OfflineManager.getSecondarySchemeSlabPerRunning(Constants.SchemeSlabs + "?$filter="
                        + Constants.SchemeItemGUID + " eq guid'" + mStrSchemeItemGuid.toUpperCase() + "' ", orderQty + "",
                        mStrSchemeItemGuid,mStrTargetBasedID,orderMatArray,isHeaderBased);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            mDoubSlabCal = Constants.DoubGetRunningSlabPer;
        } else if (SlabTypeID.equalsIgnoreCase("000002")) { // TODO  000002	Fixed
            try {
                mDoubSlabCal = Double.parseDouble(freePerOrQty) * 1;
            } catch (NumberFormatException e) {
                mDoubSlabCal = 0.0;
            }
        } else if (SlabTypeID.equalsIgnoreCase("000003")) { // TODO  000003	Step
            String mOrderVal="0";

            String[] orderMatArray = null;
//            orderMatArray[0] = mMatNo;
            if (!isHeaderBased.equalsIgnoreCase(Constants.X)){
                orderMatArray = new String[1];
                orderMatArray[0] = mMatNo;
                mOrderVal = OfflineManager.getMatWiseSchemeAmt(orderMatArray);
            }else {
                orderMatArray = orderMatGrp;
                mOrderVal = OfflineManager.getSKUGrpWiseSchemeAmt(orderMatArray);
            }



                BigInteger mDouCalStep;
                BigInteger mBigCalStep;
                BigInteger mBigResultValue = null;
                try {
                    mDouCalStep = new BigInteger(UtilConstants.removeLeadingZeroVal(mOrderVal + ""));
                    mBigCalStep = new BigInteger(UtilConstants.removeLeadingZeroVal(mStrSlabFromVal + ""));
                    mBigResultValue = mDouCalStep.divide(mBigCalStep);
                } catch (NumberFormatException e) {
                    mDouCalStep = new BigInteger("0");
                    mBigResultValue = new BigInteger("0");
                }

                try {
                    mDoubSlabCal = Double.parseDouble(freePerOrQty) * mBigResultValue.doubleValue();
                } catch (NumberFormatException e) {
                    mDoubSlabCal = 0.0;
                }

        } else if (SlabTypeID.equalsIgnoreCase("000004")) { // TODO  000004	Linear


            String mOrderVal="0";

            String[] orderMatArray = null;
            if (!isHeaderBased.equalsIgnoreCase(Constants.X)){
                orderMatArray = new String[1];
                orderMatArray[0] = mMatNo;
                mOrderVal = OfflineManager.getMatWiseSchemeAmt(orderMatArray);
            }else {
                orderMatArray = orderMatGrp;
                mOrderVal = OfflineManager.getSKUGrpWiseSchemeAmt(orderMatArray);
            }

            try {
                mDoubSlabCal = Double.parseDouble(mOrderVal) / Double.parseDouble(mStrSlabFromVal) * Double.parseDouble(freePerOrQty);
            } catch (NumberFormatException e) {
                mDoubSlabCal = 0.0;
            }
            if (mDoubSlabCal.isInfinite() || mDoubSlabCal.isNaN()) {
                mDoubSlabCal = 0.0;
            }

            // Changed code date 19-05-2017
            if(mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage) || mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)){
                mDoubSlabCal = Double.valueOf(mDoubSlabCal);
            }else{
                mDoubSlabCal = Double.valueOf(round(mDoubSlabCal));  // Round off only QTY
            }
        }
        return mDoubSlabCal;
    }

    private static SchemeBean getFreeMatTxt(String mStrFrreQty, String mStrFreeMatTxt, String freeMAt, String mCPITemGUID,String mCRSSKUGrp) {
        SchemeBean schemeBean = new SchemeBean();
        schemeBean.setFreeQty(mStrFrreQty);
        schemeBean.setFreeMatTxt(mStrFreeMatTxt);
        schemeBean.setFreeMAt(freeMAt);
        schemeBean.setOrderMaterialGroupID(mCRSSKUGrp);
        try {
            if(!mCPITemGUID.equalsIgnoreCase("")) {
                schemeBean.setBatch(OfflineManager.getValueByColumnName(Constants.CPStockItemSnos + "?$filter="
                        + Constants.CPStockItemGUID + " eq guid'" + mCPITemGUID.toUpperCase() + "' and " + Constants.MaterialNo + " eq '" + freeMAt + "' and "
                        + Constants.ManufacturingDate + " ne null and "+Constants.StockTypeID+" ne '"+Constants.str_3+"' &$orderby=" + Constants.ManufacturingDate + "%20asc  ", Constants.Batch));
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return schemeBean;
    }

    private int round(double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return d < 0 ? -i : i;
        } else {
            return d < 0 ? -(i + 1) : i + 1;
        }
    }

    private String getTaxAmount(String mStrAfterPriDisAmount, String mStrSecDisAmt,ODataEntity oDataEntity,String mStrOrderQty){
        String mStrAfterSecAmt =  (Double.parseDouble(mStrAfterPriDisAmount) - Double.parseDouble(mStrSecDisAmt)) + "";
        Double mStrNetAmtPerQty = Double.parseDouble(mStrAfterSecAmt)/Double.parseDouble(mStrOrderQty);
        String mStrTaxAmt = "0";
        try {
            mStrTaxAmt = OfflineManager.getPriceOnFieldByMatBatchAfterPrimarySecDiscount(oDataEntity,mStrNetAmtPerQty+"",mStrOrderQty);
        } catch (OfflineODataStoreException e) {
            mStrTaxAmt = "0";
        }

        return  mStrTaxAmt;
    }

    private double getSecondaryDiscountOrAmtOrFreeQtyHeaderWise(SchemeBean schPerCalBean, String mStrSlabRuleId,
                                                      String mOrderQty, String mMatNo, String mOrderMatGrp, String mCPItemGUID,String isHeaderBased) {
        double mDoubSecDisOrAmtOrQty = 0.0;
        if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeProduct)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }

                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getMaterialDesc(), schPerCalBean.getMaterialNo(), mCPItemGUID,mOrderMatGrp);
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeSKUGroup)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }
                mFreeMat =getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getSKUGroupDesc(), schPerCalBean.getSKUGroupID(), mCPItemGUID,mOrderMatGrp);
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeCRSSKUGroup)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }

                mFreeMat =getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getOrderMaterialGroupDesc(), schPerCalBean.getOrderMaterialGroupID(), mCPItemGUID,mOrderMatGrp);
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutPerc(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutPerc(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutAmount(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutAmount(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeScratchCard)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }

                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getCardTitle(), schPerCalBean.getCardTitle(), mCPItemGUID,mOrderMatGrp);
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeFreeArticle)) {
            if(schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")){
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo,schPerCalBean.getTargetBasedID(),schPerCalBean.getFromQty(),isHeaderBased);
            }else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),schPerCalBean.getFromQty(),isHeaderBased);
            }
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getFreeArticle(), schPerCalBean.getFreeArticle(), mCPItemGUID,mOrderMatGrp);
        }

        return mDoubSecDisOrAmtOrQty;
    }

    private void FreeProduct(SchemeBean schPerCalBean,String mStrSlabRuleId,String mOrderMatGrp,String mCPItemGUID,String mDoubSecDisOrAmtOrQty){

            if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeProduct)) {
                    mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getMaterialDesc(), schPerCalBean.getMaterialNo(), mCPItemGUID,mOrderMatGrp);
            } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeSKUGroup)) {

                    mFreeMat =getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getSKUGroupDesc(), schPerCalBean.getSKUGroupID(), mCPItemGUID,mOrderMatGrp);
            } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeCRSSKUGroup)) {
                    mFreeMat =getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getOrderMaterialGroupDesc(), schPerCalBean.getOrderMaterialGroupID(), mCPItemGUID,mOrderMatGrp);
            } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {

            } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {

            } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeScratchCard)) {

                    mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getCardTitle(), schPerCalBean.getCardTitle(), mCPItemGUID,mOrderMatGrp);
            } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeFreeArticle)) {

                    mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getFreeArticle(), schPerCalBean.getFreeArticle(), mCPItemGUID,mOrderMatGrp);
            }

    }

    private double mDouCalNetAmt = 0.0;
    private boolean getBasketSchemePer(SchemeBean schemeBean,MaterialBatchBean materialBatchBean,SKUGroupBean skuGroupBean){
        boolean mBoolSecDis = false;

                if(schemeBean.getOnSaleOfCatID().equalsIgnoreCase("000001")){ //Banner
                    if(!mapBannerWiseQty.isEmpty()) {
                        int mIntBannerReminder = (int) (mapBannerWiseTempQty.get(skuGroupBean.getBanner()) % mapBasketBannerMinQty.get(skuGroupBean.getBanner()));
                        if (mapBannerWiseQty.containsKey(skuGroupBean.getBanner())){
                            Double mDouTotalSelBannerQty =  mapBannerWiseQty.get(skuGroupBean.getBanner());
                            if(mIntBannerReminder>0){
                               */
/* Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                                Double mDouRemaingQty = mDouTotalSelBannerQty - mDouMatBatQty;
                                if(mDouRemaingQty==0){
                                    mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntBannerReminder ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                    mBoolSecDis =true;
                                    return mBoolSecDis;
                                }*//*


                                if(mDouTotalSelBannerQty>0){
                                    Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                                    Double mDouRemaingQty = mDouTotalSelBannerQty - mDouMatBatQty;
                                    // testing 04052017
                                    mapBannerWiseQty.put(skuGroupBean.getBanner(),mDouRemaingQty);
                                    // ending

                                    if(mDouRemaingQty==0){
                                        mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntBannerReminder ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                        mBoolSecDis =true;
                                        return mBoolSecDis;
                                    }else if(mDouRemaingQty<0){
                                        mDouCalNetAmt = (mDouTotalSelBannerQty ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                        mBoolSecDis =true;
                                        return mBoolSecDis;
                                    }
                                }else{
                                    mDouCalNetAmt = 0;
                                    mBoolSecDis =true;
                                    return mBoolSecDis;
                                }
                            }

                        }
                    }
                }else if(schemeBean.getOnSaleOfCatID().equalsIgnoreCase("000002")){  // Brand
                    if(!mapBrandWiseQty.isEmpty()) {
                       int mIntBrandReminder = (int) (mapBrandWiseTempQty.get(skuGroupBean.getBrand()) % mapBasketBrandMinQty.get(skuGroupBean.getBrand()));
                        if (mapBrandWiseQty.containsKey(skuGroupBean.getBrand())){
                            Double mDouTotalSelBrandQty =  mapBrandWiseQty.get(skuGroupBean.getBrand());
                            if(mIntBrandReminder>0){
                                */
/*Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                                Double mDouRemaingQty = mDouTotalSelBrandQty - mDouMatBatQty;
                                if(mDouRemaingQty==0){
                                    mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntBrandReminder ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                    mBoolSecDis =true;
                                    return mBoolSecDis;
                                }*//*

                                if(mDouTotalSelBrandQty>0){
                                    Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                                    Double mDouRemaingQty = mDouTotalSelBrandQty - mDouMatBatQty;
                                    // testing 04052017
                                    mapBrandWiseQty.put(skuGroupBean.getBrand(),mDouRemaingQty);
                                    // ending

                                    if(mDouRemaingQty==0){
                                        mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntBrandReminder ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                        mBoolSecDis =true;
                                        return mBoolSecDis;
                                    }else if(mDouRemaingQty<0){
                                        mDouCalNetAmt = (mDouTotalSelBrandQty ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                        mBoolSecDis =true;
                                        return mBoolSecDis;
                                    }
                                }else{
                                    mDouCalNetAmt = 0;
                                    mBoolSecDis =true;
                                    return mBoolSecDis;
                                }
                            }

                        }
                    }
                }else if(schemeBean.getOnSaleOfCatID().equalsIgnoreCase("000003")){  // ProductCat
                }else if(schemeBean.getOnSaleOfCatID().equalsIgnoreCase("000004")){  // SKUGroup
                    if(!mapSKUGrpWiseQty.isEmpty()) {
                        int mIntSKUGrpReminder = (int) (mapSKUGrpWiseTempQty.get(skuGroupBean.getSKUGroupID()) % mapBasketSKUGRPMinQty.get(skuGroupBean.getSKUGroupID()));
                        if (mapSKUGrpWiseQty.containsKey(skuGroupBean.getSKUGroupID())){
                            Double mDouTotalSelSKUGRPQty =  mapSKUGrpWiseQty.get(skuGroupBean.getSKUGroupID());
                            if(mIntSKUGrpReminder>0){
                                */
/*Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                                Double mDouRemaingQty = mDouTotalSelSKUGRPQty - mDouMatBatQty;
                                if(mDouRemaingQty==0){
                                    mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntSKUGrpReminder ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                    mBoolSecDis =true;
                                    return mBoolSecDis;
                                }*//*


                                if(mDouTotalSelSKUGRPQty>0){
                                    Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                                    Double mDouRemaingQty = mDouTotalSelSKUGRPQty - mDouMatBatQty;
                                    // testing 04052017
                                    mapSKUGrpWiseQty.put(skuGroupBean.getSKUGroupID(),mDouRemaingQty);
                                    // ending

                                    if(mDouRemaingQty==0){
                                        mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntSKUGrpReminder ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                        mBoolSecDis =true;
                                        return mBoolSecDis;
                                    }else if(mDouRemaingQty<0){
                                        mDouCalNetAmt = (mDouTotalSelSKUGRPQty ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                        mBoolSecDis =true;
                                        return mBoolSecDis;
                                    }
                                }else{
                                    mDouCalNetAmt = 0;
                                    mBoolSecDis =true;
                                    return mBoolSecDis;
                                }
                            }

                        }
                    }
                }else if(schemeBean.getOnSaleOfCatID().equalsIgnoreCase("000005")){  // OrderMaterialGroup
                    if(!mapCRSSKUGrpWiseQty.isEmpty()) {
                        int mIntCRSSKUGrpReminder = (int) (mapCRSSKUGrpWiseTempQty.get(skuGroupBean.getSKUGroup()) % mapBasketCRSSKUGRPMinQty.get(skuGroupBean.getSKUGroup()));
                        if (mapCRSSKUGrpWiseQty.containsKey(skuGroupBean.getSKUGroup())){
                            Double mDouTotalSelCRSSKUGRPQty =  mapCRSSKUGrpWiseQty.get(skuGroupBean.getSKUGroup());
                            if(mIntCRSSKUGrpReminder>0){
                               */
/* Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                                Double mDouRemaingQty = mDouTotalSelCRSSKUGRPQty - mDouMatBatQty;
                                if(mDouRemaingQty==0){
                                    mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntCRSSKUGrpReminder ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                    mBoolSecDis =true;
                                    return mBoolSecDis;
                                }*//*


                                if(mDouTotalSelCRSSKUGRPQty>0){
                                    Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                                    Double mDouRemaingQty = mDouTotalSelCRSSKUGRPQty - mDouMatBatQty;
                                    // testing 04052017
                                    mapCRSSKUGrpWiseQty.put(skuGroupBean.getSKUGroup(),mDouRemaingQty);
                                    // ending

                                    if(mDouRemaingQty==0){
                                        mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntCRSSKUGrpReminder ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                        mBoolSecDis =true;
                                        return mBoolSecDis;
                                    }else if(mDouRemaingQty<0){
                                        mDouCalNetAmt = (mDouTotalSelCRSSKUGRPQty ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                        mBoolSecDis =true;
                                        return mBoolSecDis;
                                    }
                                }else{
                                    mDouCalNetAmt = 0;
                                    mBoolSecDis =true;
                                    return mBoolSecDis;
                                }
                            }

                        }
                    }
                }else if(schemeBean.getOnSaleOfCatID().equalsIgnoreCase("000006")){  //Material
                    if(!mapMaterialWiseQty.isEmpty()) {
                        int mIntMaterialReminder = (int) (mapMaterialWiseTempQty.get(skuGroupBean.getMaterialNo()) % mapBasketMaterialMinQty.get(skuGroupBean.getMaterialNo()));
                        if (mapMaterialWiseQty.containsKey(skuGroupBean.getMaterialNo())){
                            Double mDouTotalSelMatQty =  mapMaterialWiseQty.get(skuGroupBean.getMaterialNo());
                            if(mIntMaterialReminder>0){

                                if(mDouTotalSelMatQty>0){
                                    Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                                    Double mDouRemaingQty = mDouTotalSelMatQty - mDouMatBatQty;
                                    // testing 04052017
                                    mapMaterialWiseQty.put(skuGroupBean.getMaterialNo(),mDouRemaingQty);
                                    // ending

                                    if(mDouRemaingQty==0){
                                        mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntMaterialReminder ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                        mBoolSecDis =true;
                                        return mBoolSecDis;
                                    }else if(mDouRemaingQty<0){
                                        mDouCalNetAmt = (mDouTotalSelMatQty ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                        mBoolSecDis =true;
                                        return mBoolSecDis;
                                    }
                                }else{
                                    mDouCalNetAmt = 0;
                                    mBoolSecDis =true;
                                    return mBoolSecDis;
                                }

                            }

                        }
                    }
                }
        return  mBoolSecDis;
    }

}
*/
