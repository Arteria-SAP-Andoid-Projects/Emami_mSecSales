package com.arteriatech.emami.socreate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.mbo.CPPartnerFunctionsBean;
import com.arteriatech.emami.mbo.MaterialBatchBean;
import com.arteriatech.emami.mbo.SKUGroupBean;
import com.arteriatech.emami.mbo.SchemeBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
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

import static com.arteriatech.emami.store.OfflineManager.getValueByColumnName;

/**
 * Created by e10526 on 1/9/2017.
 *
 */

public class SalesOrderReviewActivity1 extends AppCompatActivity {
    private String mStrBundleRetID = "";
    private String mStrBillTo = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "", mStrBundleCPGUID32 = "";
    private String mStrBundleRetailerUID = "";
    String mStrComingFrom = "";
    private HashMap<String, ArrayList<SKUGroupBean>> hashMapMaterials;
    TextView retName, retId, tv_so_total_order_val, tvTLSD;
    LinearLayout ll_ship_to;
    Spinner spinner_ship_to;
    private ArrayList<SKUGroupBean> alReviewSOItems, alSOSubItems;
    private ArrayList<SchemeBean> alSchFreeProd;
    private Hashtable<String, String> headerTable = new Hashtable<>();
    private String[][] mArrayDistributors = null, mArrayCPDMSDivisoins = null, mArraySPValues = null;
    private Double mDobTotalOrderVal = 0.0;
    private Set<String> mStrCrsSku = new HashSet<>();
    private ProgressDialog pdLoadDialog;

    Map<String, Double> mapNetAmt = new HashMap<>();
    Map<String, Double> mapRatioSchDis = new HashMap<>();
    Map<String, Double> mapFreeDisAmt = new HashMap<>();
    Map<String, BigDecimal> mapCRSSKUQTY = new HashMap<>();
    Map<String, Double> mapPriSchemePer = new HashMap<>();
    Map<String, Double> mapSecSchemePer = new HashMap<>();
    Map<String, Double> mapSecSchemeAmt = new HashMap<>();
    private Map<String, SKUGroupBean> mapSKUGRPVal = new HashMap<>();
    HashMap<String, ArrayList<SchemeBean>> hashMapSchemeValByOrderMatGrp = new HashMap<>();
    HashMap<String, ArrayList<SchemeBean>> hashMapSchemeValByMaterial = new HashMap<>();
    HashMap<String, String> hashMapOrderMatGrpValByMaterial = new HashMap<>();
    HashMap<String, SchemeBean> hashMapFreeMaterialByMaterial = new HashMap<>();
    HashMap<String, String> hashMapFreeMatByOrderMatGrp = new HashMap<>();
//    HashMap<String, SchemeBean> hashMapFreeQtyInfoBySchemeGuid = new HashMap<>();
    Set<String> mSetOrderMatGrp = new HashSet<>();
    Set<String> mSetSchemeGuid = new HashSet<>();
    Map<String, Integer> mapCntMatByCRSKUGRP = new HashMap<>();

    private SchemeBean mFreeMat = null;
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


    Map<String, Double> mapNetPriceByScheme = new HashMap<>();
    Map<String, Double> mapFreeQtyeByScheme = new HashMap<>();


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
    ArrayList<String> mStrCrsSKUList = new ArrayList<>();
    ArrayList<CPPartnerFunctionsBean> mStrCpPartnerFunVal = new ArrayList<>();
    private String stockOwner = "";
    private String typevalue="";
    private String mStrFunVal="";
    TextView tv_crs_sku_label;
    CPPartnerFunctionsBean partnerFunctionsBean;
    MenuItem menuSave ;


    LatLng latlng_timeofsave;
    LatLng latlng_retailer;

    private double mDouLatVal = 0.0, mDouLongVal = 0.0;

    double distance=0.00d;
    double distanceconfigtype=0.00d;


    ODataPropMap oDataProperties;
    ODataProperty oDataProperty;

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
        if (!Constants.restartApp(SalesOrderReviewActivity1.this)) {


            //my change 12/03/2020
            getConfigTypeDistanceValeumm();
              getRetailerLat_Long();
            initializeUI();
            loadAsyncTask();

        }
    }

    private void getConfigTypeDistanceValeumm() {

        String query="ConfigTypsetTypeValues?$filter=Typeset eq 'DISTNC'";
        try {
      String distancefromConfig  = OfflineManager.getConfigValueDistancemm(query);

      if(!distancefromConfig.equals("")) {

          distanceconfigtype = Double.parseDouble(distancefromConfig);

          System.out.println(distancefromConfig);
      }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }


    }

    private void getRetailerLat_Long() {
        String retDetgry = Constants.ChannelPartners + "(guid'" + mStrBundleCPGUID.toUpperCase() + "')";
        try {
            ODataEntity retilerEntity = OfflineManager.getRetDetails(retDetgry);
            oDataProperties = retilerEntity.getProperties();
            oDataProperty = oDataProperties.get(Constants.Latitude);
            BigDecimal mDecimalLatitude = (BigDecimal) oDataProperty.getValue();

            try {
                if (mDecimalLatitude != null) {
                    mDouLatVal = mDecimalLatitude.doubleValue();
                } else {
                    mDouLatVal = 0.0;
                }
            } catch (Exception e) {
                mDouLatVal = 0.0;
            }

            oDataProperty = oDataProperties.get(Constants.Longitude);
            BigDecimal mDecimalLongitude = (BigDecimal) oDataProperty.getValue();  //---------> Decimal property

            try {
                if (mDecimalLongitude != null) {
                    mDouLongVal = mDecimalLongitude.doubleValue();
                } else {
                    mDouLongVal = 0.0;
                }
            } catch (Exception e) {
                mDouLongVal = 0.0;
            }

             latlng_retailer = new LatLng(mDouLatVal,mDouLongVal);
             System.out.println(latlng_retailer);


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }


    }

    private void Distance_retailer_and_save()
    {
        try {
            Location locationA = new Location("Retailer Location");
            locationA.setLatitude(latlng_retailer.latitude);
            locationA.setLongitude(latlng_retailer.longitude);
            Location locationB = new Location("Save Location");
            locationB.setLatitude(latlng_timeofsave.latitude);
            locationB.setLongitude(latlng_timeofsave.longitude);
            distance = locationA.distanceTo(locationB);
            System.out.println(distance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTypeValue() {


        tv_crs_sku_label= (TextView) findViewById(R.id.tv_crsname);

        typevalue=Constants.getTypesetValueForSkugrp(SalesOrderReviewActivity1.this);
        if(typevalue.equalsIgnoreCase(Constants.SKUGROUP)){
            tv_crs_sku_label.setText(Constants.SKUGROUP);
            // etSKUDescSearch.setHint(R.string.lbl_Search_by_skugroup);
        }else{
            tv_crs_sku_label.setText(Constants.CRSSKUGROUP);
            //  etSKUDescSearch.setHint(R.string.lbl_Search_by_crsskugroup);
        }
    }
    private void getMatList() {
            hashMapOrderMatGrpValByMaterial = OfflineManager.getSchemeCRSSKUGRPBYMaterial();
    }

    private  void getCpPartnerFunVal() throws OfflineODataStoreException {
        try {
            String query = Constants.CPPartnerFunctions + "?$filter=CPGUID eq guid'" + mStrBundleCPGUID + "' and PartnerFunction eq '02'";
            mStrCpPartnerFunVal = OfflineManager.getCpPartnerVal(query);
            if (mStrCpPartnerFunVal!=null &&  mStrCpPartnerFunVal.size()>0) {
                ll_ship_to.setVisibility(View.VISIBLE);

                try {
                    ArrayAdapter<CPPartnerFunctionsBean> adapter = new ArrayAdapter<CPPartnerFunctionsBean>(this,android.R.layout.simple_spinner_item,mStrCpPartnerFunVal);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_ship_to.setAdapter(adapter);

                    spinner_ship_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            partnerFunctionsBean = (CPPartnerFunctionsBean) parent.getItemAtPosition(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                ll_ship_to.setVisibility(View.GONE);
            }
        } catch (OfflineODataStoreException e) {
            ll_ship_to.setVisibility(View.GONE);
            e.printStackTrace();
        }

    }

    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
        if (mArrayDistributors != null) {
            try {
                stockOwner = mArrayDistributors[5][0];
            } catch (Exception e) {
                stockOwner = "";
            }
        }

    }

    private void getCPDMSDivisions() {
      //  mArrayCPDMSDivisoins = Constants.getDMSDivisionByCPGUID(mStrBundleCPGUID);
        String spGuid="";
        try {
            spGuid = OfflineManager.getGuidValueByColumnName(Constants.SalesPersons + "?$select=" + Constants.SPGUID+" ", Constants.SPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        String dealerCode  = "";
        String qryStr = Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrBundleCPGUID.toUpperCase() + "' and "+Constants.PartnerMgrGUID+" eq guid'"+spGuid.toUpperCase()+"' ";
        try {
            dealerCode = OfflineManager.getDealerCodeByCPGUID(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        String qryStr1 = Constants.CPSPRelations + "?$filter=" + Constants.SPGUID + " eq '" + spGuid.toUpperCase().replaceAll("-","") + "' and "+Constants.CPGUID+" eq '"+dealerCode+ "' and "+Constants.StatusID+" eq '01'";
        try {
            mArrayCPDMSDivisoins = OfflineManager.getDMSDivisionFromCPS(qryStr1);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayCPDMSDivisoins == null) {
            mArrayCPDMSDivisoins = new String[2][1];
            mArrayCPDMSDivisoins[0][0] = "";
            mArrayCPDMSDivisoins[1][0] = "";
        }


    }

    /**
     * get salesPerson values
     */
    private void getSalesPersonValues() {
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(mStrBundleCPGUID);
    }

    /*initializes UI for screen*/
    void initializeUI() {
        tv_so_total_order_val = (TextView) findViewById(R.id.tv_so_total_order_val);
        tvTLSD = (TextView) findViewById(R.id.tv_so_create_tlsd_amt);

        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retId = (TextView) findViewById(R.id.tv_reatiler_id);

        ll_ship_to = findViewById(R.id.ll_ship_to);
        spinner_ship_to = findViewById(R.id.spinner_ship_to);

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

    /*AsyncTask to get Retailers List*/
    private class GetReviewCal extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoadDialog = new ProgressDialog(SalesOrderReviewActivity1.this, R.style.ProgressDialogTheme);
                    pdLoadDialog.setMessage(getString(R.string.app_loading));
                    pdLoadDialog.setCancelable(false);
                    pdLoadDialog.show();



        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                getCpPartnerFunVal();
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            getSalesPersonValues();
            getMatList();
            getSOSubItems();
            getDistributorValues();
            getCPDMSDivisions();

            getSOItemValues();

            return null;
        }

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Time pdLoadDialog.dismiss()", UtilConstants.getSyncHistoryddmmyyyyTime());


            setValueToUI();
            getTypeValue();

            checkMatBatchItemAvalibleOrnot();

        }
    }

    private void checkMatBatchItemAvalibleOrnot(){
        if(mStrCrsSKUList.size()>0){
            String mStrSKUGrp = UtilConstants.getConcatinatinFlushCollectios(mStrCrsSKUList);
            UtilConstants.showAlert(getString(R.string.alert_mat_batch_not_avalible,mStrSKUGrp), SalesOrderReviewActivity1.this);
        }else{
            displayReviewPage();
            Log.d("Time displayReviewPage", UtilConstants.getSyncHistoryddmmyyyyTime());
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

    @SuppressLint("LongLogTag")
    private void getSOItemValues() {
        Log.d("Time getSOItemValues start", UtilConstants.getSyncHistoryddmmyyyyTime());
        Constants.hashMapCpStockItemGuidQtyValByMaterial.clear();
        Constants.hashMapMaterialValByOrdMatGrp.clear();
        mStrCrsSKUList.clear();
        alReviewSOItems = new ArrayList<>();
        if (Constants.selectedSOItems != null && Constants.selectedSOItems.size() > 0) {
            for (SKUGroupBean skuGroupBean : Constants.selectedSOItems) {

                if (skuGroupBean.getSkuSubGroupBeanArrayList() != null && skuGroupBean.getSkuSubGroupBeanArrayList().size() > 0) {
                    for (SKUGroupBean subItem : skuGroupBean.getSkuSubGroupBeanArrayList()) {
                        if (Double.parseDouble(subItem.getEtQty().equalsIgnoreCase("")
                                ? "0" : subItem.getEtQty()) > 0) {
                            subItem.setORDQty(subItem.getEtQty());
                            alReviewSOItems.add(subItem);
                            setMatList.add(subItem.getMaterialNo());

                            SchemeBean schemeBean = new SchemeBean();
                            schemeBean.setCPItemGUID(subItem.getCPStockItemGUID());
                            schemeBean.setOrderQty(subItem.getEtQty());
                            Constants.hashMapCpStockItemGuidQtyValByMaterial.put(subItem.getMaterialNo(), schemeBean);

                            if (!Constants.hashMapMaterialValByOrdMatGrp.containsKey(subItem.getSKUGroup())) {
                                Set set = Constants.hashMapMaterialValByOrdMatGrp.get(subItem.getSKUGroup());
                                if (set != null) {
                                    set.add(subItem.getMaterialNo());
                                } else {
                                    set = new HashSet();
                                    set.add(subItem.getMaterialNo());
                                }
                                Constants.hashMapMaterialValByOrdMatGrp.put(subItem.getSKUGroup(), set);
                            } else {
                                Set set = new HashSet();
                                set.add(subItem.getMaterialNo());
                                Constants.hashMapMaterialValByOrdMatGrp.put(subItem.getSKUGroup(), set);
                            }

                            if (subItem.getBrand()!=null && !subItem.getBrand().equalsIgnoreCase("")) {
                                // get brand wise qty
                                if (mapBrandWiseQty.containsKey(subItem.getBrand())) {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEtQty()) + mapBrandWiseQty.get(subItem.getBrand());
                                    mapBrandWiseQty.put(subItem.getBrand(), mDouOrderQty);
                                } else {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEtQty());
                                    mapBrandWiseQty.put(subItem.getBrand(), mDouOrderQty);
                                }
                            }

                            if (subItem.getBanner()!=null && !subItem.getBanner().equalsIgnoreCase("")) {
                                // get banner wise qty
                                if (mapBannerWiseQty.containsKey(subItem.getBanner())) {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEtQty()) + mapBannerWiseQty.get(subItem.getBanner());
                                    mapBannerWiseQty.put(subItem.getBanner(), mDouOrderQty);
                                } else {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEtQty());
                                    mapBannerWiseQty.put(subItem.getBanner(), mDouOrderQty);
                                }
                            }

                            if (subItem.getSKUGroupID()!=null && !subItem.getSKUGroupID().equalsIgnoreCase("")) {
                                // get SKU GRP wise qty
                                if (mapSKUGrpWiseQty.containsKey(subItem.getSKUGroupID())) {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEtQty()) + mapSKUGrpWiseQty.get(subItem.getSKUGroupID());
                                    mapSKUGrpWiseQty.put(subItem.getSKUGroupID(), mDouOrderQty);
                                } else {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEtQty());
                                    mapSKUGrpWiseQty.put(subItem.getSKUGroupID(), mDouOrderQty);
                                }
                            }

                            if (subItem.getSKUGroup()!=null && !subItem.getSKUGroup().equalsIgnoreCase("")) {
                                // get CRS SKU GRP wise qty
                                if (mapCRSSKUGrpWiseQty.containsKey(subItem.getSKUGroup())) {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEtQty()) + mapCRSSKUGrpWiseQty.get(subItem.getSKUGroup());
                                    mapCRSSKUGrpWiseQty.put(subItem.getSKUGroup(), mDouOrderQty);
                                } else {
                                    double mDouOrderQty = Double.parseDouble(subItem.getEtQty());
                                    mapCRSSKUGrpWiseQty.put(subItem.getSKUGroup(), mDouOrderQty);
                                }
                            }
                            mapMaterialWiseQty.put(subItem.getMaterialNo(), Double.parseDouble(subItem.getEtQty()));

                            mStrCrsSku.add(subItem.getSKUGroup());
                        }
                    }
                }

            }
        }
        Log.d("Time getSOItemValues End", UtilConstants.getSyncHistoryddmmyyyyTime());
        mapBrandWiseTempQty.putAll(mapBrandWiseQty);
        mapBannerWiseTempQty.putAll(mapBannerWiseQty);
        mapSKUGrpWiseTempQty.putAll(mapSKUGrpWiseQty);
        mapCRSSKUGrpWiseTempQty.putAll(mapCRSSKUGrpWiseQty);
        mapMaterialWiseTempQty.putAll(mapMaterialWiseQty);
        Constants.HashMapSchemeValuesBySchemeGuid.clear();
        getInstantSchemeAndBasketScheme();
        Log.d("Time getInstantSchemeAndBasketScheme", UtilConstants.getSyncHistoryddmmyyyyTime());
        getOrderMatGrpSchemeCal();
        Log.d("Time getOrderMatGrpSchemeCal", UtilConstants.getSyncHistoryddmmyyyyTime());
        getMaterialSchemeCal();
        Log.d("Time getMaterialSchemeCal", UtilConstants.getSyncHistoryddmmyyyyTime());
        sumOfSkuGrpItems();
        Log.d("Time sumOfSkuGrpItems", UtilConstants.getSyncHistoryddmmyyyyTime());

    }


    private void sumOfSkuGrpItems() {
        alSchFreeProd = new ArrayList<>();
        if (alReviewSOItems != null && alReviewSOItems.size() > 0) {
            SchemeBean primaryDisTaxValBean = null;
            for (int i = 0; i < alReviewSOItems.size(); i++) {
                final SKUGroupBean skuGroupBean = alReviewSOItems.get(i);

                double calSecPer = 0.0;
                double mDoubSecPer = 0.0;
                SchemeBean appliedRatio=null;
                ArrayList<SchemeBean> schPerCalBeanList = null;
                ArrayList<SchemeBean> schPerOrderMatLevelList = null;
                if (Constants.MAPSCHGuidByMaterial.containsKey(skuGroupBean.getMaterialNo())) {
                    schPerCalBeanList = hashMapSchemeValByMaterial.get(skuGroupBean.getMaterialNo());
                    setMaterialLevelScheme(schPerCalBeanList);
                }
                if (mSetOrderMatGrp.size() == 0) {
                    mSetOrderMatGrp.add(skuGroupBean.getSKUGroup());
                    schPerOrderMatLevelList = hashMapSchemeValByOrderMatGrp.get(skuGroupBean.getSKUGroup());
                } else if (!mSetOrderMatGrp.contains(skuGroupBean.getSKUGroup())) {
                    mSetOrderMatGrp.add(skuGroupBean.getSKUGroup());
                    schPerOrderMatLevelList = hashMapSchemeValByOrderMatGrp.get(skuGroupBean.getSKUGroup());
                }
                if (schPerCalBeanList != null && schPerOrderMatLevelList != null) {
                    schPerCalBeanList.addAll(schPerOrderMatLevelList);
                } else if (schPerOrderMatLevelList != null) {
                    schPerCalBeanList = schPerOrderMatLevelList;
                }
                schPerCalBeanList = removeDuplicateScheme(schPerCalBeanList);

                String secondarySchemeAmt = "0", secondarySchemePerAmt = "0", schemeSlabRule = "";
                Double mDouSumNetTaxSecAmt = 0.0, mDouSumPriDis = 0.0, mDouSumSecDiscount = 0.0, mDouPriDis = 0.0, mDouSecDiscount = 0.0, mDouSecAmt = 0.0, mDouSumTax = 0.0;

                mFreeMat = null;
                ArrayList<SchemeCalcuBean> schemeCalcuBeanArrayList = new ArrayList<>();
                if (schPerCalBeanList!=null && !schPerCalBeanList.isEmpty()) {
                    int totalSchemeSize =schPerCalBeanList.size();
                    int currentSize = 0;
                    for (SchemeBean schPerCalBeans : schPerCalBeanList){
                        currentSize++;
                        SchemeCalcuBean schemeCalcuBean = new SchemeCalcuBean();

                        String mStrSchemeGUID = "";
                        String mStrSlabRuleId = schPerCalBeans.getSlabRuleID();
                        Double mDouSlabTypeCal = 0.0;

                    if (schPerCalBeans.isMaterialLevel()) {
                        if (!schPerCalBeans.getIsHeaderBasedSlab().equalsIgnoreCase(Constants.X)) {
                            mDouSlabTypeCal = getSecondaryDiscountOrAmtOrFreeQty(schPerCalBeans.isMaterialLevel(), schPerCalBeans, mStrSlabRuleId,
                                    schPerCalBeans.getSaleUnitID().equalsIgnoreCase(Constants.SchemeSaleUnitIDCBB)?schPerCalBeans.getCBBQty():skuGroupBean.getORDQty(), skuGroupBean.getMaterialNo(), skuGroupBean.getSKUGroup(), skuGroupBean.getCPStockItemGUID(), "");
                            mStrSchemeGUID = schPerCalBeans.getSchemeGuid();
                        } else {
                            mStrSchemeGUID = schPerCalBeans.getSchemeGuid();
                            mDouSlabTypeCal = Double.parseDouble(mapSchemePerORAmtByMaterial.get(skuGroupBean.getMaterialNo()+"_"+mStrSchemeGUID));
                            mFreeMat = mapSchemeFreeMatByMaterial.get(skuGroupBean.getMaterialNo());
                        }
                    } else {
                        if (!schPerCalBeans.getIsHeaderBasedSlab().equalsIgnoreCase(Constants.X)) {
                            mDouSlabTypeCal = getSecondaryDiscountOrAmtOrFreeQty(schPerCalBeans.isMaterialLevel(), schPerCalBeans, mStrSlabRuleId,
                                    schPerCalBeans.getSaleUnitID().equalsIgnoreCase(Constants.SchemeSaleUnitIDCBB)?schPerCalBeans.getCBBQty():skuGroupBean.getORDQty(), skuGroupBean.getMaterialNo(), skuGroupBean.getSKUGroup(), skuGroupBean.getCPStockItemGUID(), "");
                            mStrSchemeGUID = schPerCalBeans.getSchemeGuid();
                        } else {
                            mStrSchemeGUID = schPerCalBeans.getSchemeGuid();
                            mDouSlabTypeCal = Double.parseDouble(mapSchemePerORAmtByOrderMatGrp.get(skuGroupBean.getSKUGroup()+"_"+mStrSchemeGUID));
                            mFreeMat = mapSchemeFreeMatByOrderMatGrp.get(skuGroupBean.getSKUGroup()+"_"+mStrSchemeGUID);
                        }
                    }
                        calSecPer = 0.0;
                        mDoubSecPer = 0.0;
                        mDouSecAmt = 0.0;
                        boolean isBatchSecondTime = false;
                        secondarySchemeAmt = "0";
                        boolean mBoolBasketSchemeapplicable = false;
                    primaryDisTaxValBean = getPrimaryTaxValByMaterial(skuGroupBean.getCPStockItemGUID(), skuGroupBean.getMaterialNo(), skuGroupBean.getORDQty(),false);
                    if (primaryDisTaxValBean != null) {
                        ArrayList<MaterialBatchBean> alMatBatchItemBean = primaryDisTaxValBean.getMaterialBatchBeanArrayList();
                        if (alMatBatchItemBean != null && alMatBatchItemBean.size() > 0) {
                            for (MaterialBatchBean matBatchItemBean : alMatBatchItemBean) {
                                String priPercentage = matBatchItemBean.getPrimaryPer();

                                if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
                                    mBoolBasketSchemeapplicable = false;//TODO need to check this logic
                                    mDouCalNetAmt = 0.0;
                                    if (schPerCalBeans.getSchemeTypeID().equalsIgnoreCase(Constants.SchemeTypeIDBasketScheme)) {
                                        mBoolBasketSchemeapplicable = getBasketSchemePer(schPerCalBeans, matBatchItemBean, skuGroupBean);
                                    }

                                    try {
                                        mDoubSecPer = Double.parseDouble(mDouSlabTypeCal + "");
                                    } catch (NumberFormatException e) {
                                        mDoubSecPer = 0.0;
                                    }


                                    if (schPerCalBeans.getIsIncludingPrimary().equalsIgnoreCase(Constants.X)) {
                                        calSecPer = mDoubSecPer - Double.parseDouble(priPercentage);
                                        if (calSecPer < 0) {
                                            calSecPer = 0;
                                        }
                                    } else {
                                        calSecPer = mDoubSecPer;
                                    }
                                    matBatchItemBean.setSecPer(calSecPer + "");
                                    matBatchItemBean.setSecDiscountAmt(0 + "");

                                    schemeSlabRule = Constants.SchemeFreeDiscountPercentage;

                                    if (currentSize==totalSchemeSize) {
                                        if (!mBoolBasketSchemeapplicable) {
                                            secondarySchemeAmt = Constants.calculatePrimaryDiscount(getSecDiscAmtPer(calSecPer, schemeCalcuBeanArrayList)+"", matBatchItemBean.getNetAmtAftPriDis());
                                        } else {
                                            secondarySchemeAmt = Constants.calculatePrimaryDiscount(getSecDiscAmtPer(calSecPer, schemeCalcuBeanArrayList) + "", mDouCalNetAmt + "");
                                        }
                                        secondarySchemeAmt = (Double.parseDouble(secondarySchemeAmt) + getSecSchemeAmt(mDouSecAmt, schemeCalcuBeanArrayList))+"";
                                        //apply ratio scheme
                                        appliedRatio = OfflineManager.applyRatioScheme(primaryDisTaxValBean,skuGroupBean.getORDQty(),alMatBatchItemBean,secondarySchemeAmt,matBatchItemBean);

                                        String mStrTaxAmt = getTaxAmount(appliedRatio.getMatNetAmtAftPriDis(), "0", matBatchItemBean.getoDataEntity(), matBatchItemBean.getQty());
                                        matBatchItemBean.setTax(mStrTaxAmt);

                                        matBatchItemBean.setTotalNetAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()) + Double.parseDouble(mStrTaxAmt)) + "");
                                        mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()) + Double.parseDouble(mStrTaxAmt));


                                        matBatchItemBean.setGrossAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis())+ ""));

                                    }

                                } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                    String mStrBatchQty = matBatchItemBean.getQty();
                                    calSecPer = 0.0;
                                    try {
                                        mDouSecAmt = Double.parseDouble(mDouSlabTypeCal + "");
                                    } catch (NumberFormatException e) {
                                        mDouSecAmt = mDouSecAmt+ 0.0;
                                    }

                                    matBatchItemBean.setSecPer(0 + "");
                                    matBatchItemBean.setSecDiscountAmt(0 + "");
                                    matBatchItemBean.setSlabRuleAmt(Constants.SchemeFreeDiscountAmount);
                                    schemeSlabRule = Constants.SchemeFreeDiscountAmount;
                                    // If amount based scheme scheme amount adjust to singele line item
                                    if (!mStrAmtBasedSchemeAvl.contains(mStrSchemeGUID.toUpperCase())) {
                                        mStrAmtBasedSchemeAvl.add(mStrSchemeGUID.toUpperCase());
                                        if (currentSize==totalSchemeSize) {
                                            if (!mBoolBasketSchemeapplicable) {
                                                secondarySchemeAmt = Constants.calculatePrimaryDiscount(getSecDiscAmtPer(calSecPer, schemeCalcuBeanArrayList)+"", matBatchItemBean.getNetAmtAftPriDis());// TODO change to globaly
                                            } else {
                                                secondarySchemeAmt = Constants.calculatePrimaryDiscount(getSecDiscAmtPer(calSecPer, schemeCalcuBeanArrayList) + "", mDouCalNetAmt + ""); // TODO change to globaly
                                            }
                                            secondarySchemeAmt = (Double.parseDouble(secondarySchemeAmt) + getSecSchemeAmt(mDouSecAmt, schemeCalcuBeanArrayList))+"";
                                            //apply ratio scheme
                                            appliedRatio = OfflineManager.applyRatioScheme(primaryDisTaxValBean,skuGroupBean.getORDQty(),alMatBatchItemBean,secondarySchemeAmt,matBatchItemBean);
                                            String mStrTaxAmt = getTaxAmount(appliedRatio.getMatNetAmtAftPriDis(), "0", matBatchItemBean.getoDataEntity(), matBatchItemBean.getQty());
                                            matBatchItemBean.setTax(mStrTaxAmt);

                                            matBatchItemBean.setTotalNetAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()) + Double.parseDouble(mStrTaxAmt)) + "");
                                            mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()) + Double.parseDouble(mStrTaxAmt));
                                            matBatchItemBean.setGrossAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis())+ ""));
//                                            isTaxCalculated=true;
                                        }
                                    } else {
//                                        secondarySchemeAmt = "0";
                                        if (currentSize==totalSchemeSize) {
                                            if (!mBoolBasketSchemeapplicable) {
                                                secondarySchemeAmt = Constants.calculatePrimaryDiscount(getSecDiscAmtPer(calSecPer, schemeCalcuBeanArrayList)+"", matBatchItemBean.getNetAmtAftPriDis());// TODO change to globaly
                                            } else {
                                                secondarySchemeAmt = Constants.calculatePrimaryDiscount(getSecDiscAmtPer(calSecPer, schemeCalcuBeanArrayList) + "", mDouCalNetAmt + ""); // TODO change to globaly
                                            }
                                            secondarySchemeAmt = (Double.parseDouble(secondarySchemeAmt) + getSecSchemeAmt(mDouSecAmt, schemeCalcuBeanArrayList))+"";

                                            //apply ratio scheme
                                            appliedRatio = OfflineManager.applyRatioScheme(primaryDisTaxValBean,skuGroupBean.getORDQty(),alMatBatchItemBean,secondarySchemeAmt,matBatchItemBean);
                                            String mStrTaxAmt = getTaxAmount(appliedRatio.getMatNetAmtAftPriDis(), "0", matBatchItemBean.getoDataEntity(), matBatchItemBean.getQty());
                                            matBatchItemBean.setTax(mStrTaxAmt);

                                            matBatchItemBean.setTotalNetAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()) + Double.parseDouble(mStrTaxAmt)) + "");
                                            mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()) + Double.parseDouble(mStrTaxAmt));

                                            matBatchItemBean.setGrossAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis())+ ""));
                                        }
                                    }


                                } else {


                                    if (currentSize==totalSchemeSize) {
                                        secondarySchemeAmt = "0";
                                        //apply ratio scheme
                                        appliedRatio = OfflineManager.applyRatioScheme(primaryDisTaxValBean,skuGroupBean.getORDQty(),alMatBatchItemBean,secondarySchemeAmt,matBatchItemBean);

                                        matBatchItemBean.setTotalNetAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis())) + "");
                                        mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis()));
                                        matBatchItemBean.setGrossAmt((Double.parseDouble(appliedRatio.getMatNetAmtAftPriDis())+ ""));
                                    }
                                    schemeSlabRule = "";
                                    secondarySchemeAmt = "0";
                                    mDouSecAmt = 0.0;
                                    matBatchItemBean.setSecPer(0 + "");
                                    matBatchItemBean.setSecDiscountAmt(0 + "");

                                    // Start Scheme calculation logic is added 14/07/2017
                                    if (mapNetPriceByScheme.containsKey(skuGroupBean.getSKUGroup()+"_"+mStrSchemeGUID)) {
                                        double mDouNetAmt = Double.parseDouble(matBatchItemBean.getNetAmount()) + mapNetPriceByScheme.get(skuGroupBean.getSKUGroup()+"_"+mStrSchemeGUID);
                                        mapNetPriceByScheme.put(skuGroupBean.getSKUGroup()+"_"+mStrSchemeGUID, mDouNetAmt);
                                    } else {
                                        double mDouNetAmt = Double.parseDouble(matBatchItemBean.getNetAmount());
                                        mapNetPriceByScheme.put(skuGroupBean.getSKUGroup()+"_"+mStrSchemeGUID, mDouNetAmt);
                                    }
                                    // End Scheme calculation logic is added 14/07/2017

                                }


                                mDouPriDis = Double.parseDouble(matBatchItemBean.getPrimaryPer());


                                mDouSecDiscount = calSecPer;
                            }

                            // Start Scheme calculation logic is added 14/07/2017
                            if (!mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount) && !mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {

                                if (mapFreeQtyeByScheme.containsKey(mStrSchemeGUID)) {
                                    double mDouQty = Double.parseDouble(skuGroupBean.getORDQty()) + mapFreeQtyeByScheme.get(mStrSchemeGUID);
                                    mapFreeQtyeByScheme.put(mStrSchemeGUID, mDouQty);
                                } else {
                                    double mDouQty = Double.parseDouble(skuGroupBean.getORDQty());
                                    mapFreeQtyeByScheme.put(mStrSchemeGUID, mDouQty);
                                }
                            }
                            // End Scheme calculation logic is added 14/07/2017

                            primaryDisTaxValBean.setMaterialBatchBeanArrayList(alMatBatchItemBean);
                        }else{
                            mStrCrsSKUList.add(skuGroupBean.getSKUGroupDesc());
                        }

                    } else {
                        mDouSumNetTaxSecAmt = 0.0;
                        mDouSumPriDis = 0.0;
                        mDouSumSecDiscount = 0.0;
                        mDouPriDis = 0.0;
                        mDouSecDiscount = 0.0;

                        schemeSlabRule = "";
                        secondarySchemeAmt = "0";
                        mStrCrsSKUList.add(skuGroupBean.getSKUGroupDesc());
                    }

                        // Start Scheme is free qty or not logic is added 10/08/2017
                        if (!mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)
                                && !mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
                            schemeCalcuBean.setSchemeFreeQty(true);
                        } else {
                            schemeCalcuBean.setSchemeFreeQty(false);
                        }
                        if (mFreeMat != null) {
                            schemeCalcuBean.setmFreeMat(mFreeMat);
                            alSchFreeProd.add(mFreeMat);
                        }
                        schemeCalcuBean.setSchemeGuidNo(mStrSchemeGUID);
                        schemeCalcuBean.setmDouSecDiscount(mDouSecDiscount);
                        schemeCalcuBean.setmDouSecAmt(mDouSecAmt);
                        schemeCalcuBean.setmDouSecDiscountAmt(Double.parseDouble(secondarySchemePerAmt));


                        schemeCalcuBeanArrayList.add(schemeCalcuBean);


                        if (!schemeSlabRule.equalsIgnoreCase("") && schemeSlabRule.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {

                            double finalDouSecAmt=0.0;
                            double secDiscount = getSecSchemeAmt(0.0, schemeCalcuBeanArrayList);
                            if (secDiscount>0){
                                finalDouSecAmt = secDiscount;
                            }else {
                                finalDouSecAmt=mDouSecDiscount;
                            }

                            alReviewSOItems.get(i).setSecSchemeAmt(finalDouSecAmt + "");
                            alReviewSOItems.get(i).setSchemeSlabRule(Constants.SchemeFreeDiscountAmount);
                        } else if (!schemeSlabRule.equalsIgnoreCase("") && schemeSlabRule.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
                            double finalSecDiscount=0.0;
                            double secDiscount = getSecDiscAmtPer(0.0, schemeCalcuBeanArrayList);
                            if (secDiscount>0){
                                finalSecDiscount = secDiscount;
                            }else {
                                finalSecDiscount=mDouSecDiscount;
                            }
                            alReviewSOItems.get(i).setSecScheme(finalSecDiscount + "");
                            alReviewSOItems.get(i).setSchemeSlabRule(Constants.SchemeFreeDiscountPercentage);
                        } else {
                            alReviewSOItems.get(i).setSchemeSlabRule("");
                        }
                }

                    alReviewSOItems.get(i).setSchemeCalcuBeanArrayList(schemeCalcuBeanArrayList);

                    if (appliedRatio != null) {
                        try {
                            alReviewSOItems.get(i).setRatioSchDisAmt(appliedRatio.getRatioSchDisAmt() != null ? appliedRatio.getRatioSchDisAmt() : Constants.str_0);
                            alReviewSOItems.get(i).setRatioSchMatPrice(appliedRatio.getRatioSchMatPrice() != null ? appliedRatio.getRatioSchMatPrice() : Constants.str_0);
                        } catch (Exception e) {
                            alReviewSOItems.get(i).setRatioSchDisAmt(Constants.str_0);
                            alReviewSOItems.get(i).setRatioSchMatPrice(Constants.str_0);
                        }
                        try {
                            alReviewSOItems.get(i).setISFreeTypeID(appliedRatio.getISFreeTypeID()!=null?appliedRatio.getISFreeTypeID():Constants.str_0);
                        } catch (Exception e) {
                            alReviewSOItems.get(i).setISFreeTypeID(Constants.str_0);
                        }


                        if (!appliedRatio.getFreeMaterialNo().equalsIgnoreCase("")) {
                            appliedRatio.setRatioScheme(true);
                            alSchFreeProd.add(appliedRatio);
                            hashMapFreeMaterialByMaterial.put(skuGroupBean.getMaterialNo(), appliedRatio);
                            hashMapFreeMatByOrderMatGrp.put(skuGroupBean.getMaterialNo(), skuGroupBean.getSKUGroup());
                        }
                        alReviewSOItems.get(i).setMaterialBatchBean(appliedRatio);
                    }else {
                        alReviewSOItems.get(i).setMaterialBatchBean(primaryDisTaxValBean);
                    }

                } else {
                    schemeSlabRule = "";

                    primaryDisTaxValBean = getPrimaryTaxValByMaterial(skuGroupBean.getCPStockItemGUID(), skuGroupBean.getMaterialNo(), skuGroupBean.getORDQty(),true);
                    if (primaryDisTaxValBean != null) {
                        ArrayList<MaterialBatchBean> alMatBatchItemBean = primaryDisTaxValBean.getMaterialBatchBeanArrayList();
                        if (alMatBatchItemBean != null && alMatBatchItemBean.size() > 0) {
                            for (MaterialBatchBean matBatchItemBean : alMatBatchItemBean) {
                                String netAmount = matBatchItemBean.getNetAmount();
                                calSecPer = 0;
                                matBatchItemBean.setSecPer(calSecPer + "");
                                secondarySchemeAmt = Constants.calculatePrimaryDiscount(calSecPer + "", netAmount);

                                String mStrTaxAmt = getTaxAmount(matBatchItemBean.getNetAmtAftPriDis(), secondarySchemeAmt, matBatchItemBean.getoDataEntity(), matBatchItemBean.getQty());
                                matBatchItemBean.setTax(mStrTaxAmt);


                                matBatchItemBean.setTotalNetAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt)) + "");
                                mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt));

                                mDouPriDis = Double.parseDouble(matBatchItemBean.getPrimaryPer());

                                matBatchItemBean.setGrossAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + ""));

                                mDouSecDiscount = calSecPer;
                            }
                            primaryDisTaxValBean.setMaterialBatchBeanArrayList(alMatBatchItemBean);
                        }else{
                            mStrCrsSKUList.add(skuGroupBean.getSKUGroupDesc());
                        }

                    } else {
                        mDouSumNetTaxSecAmt = 0.0;
                        mDouSumPriDis = 0.0;
                        mDouSumSecDiscount = 0.0;

                        mDouPriDis = 0.0;

                        mDouSecDiscount = 0.0;

                        schemeSlabRule = "";
                        secondarySchemeAmt = "0";
                        mStrCrsSKUList.add(skuGroupBean.getSKUGroupDesc());
                    }

                    schemeSlabRule = "";
                    try {//TODO need to check this logic
                        if (primaryDisTaxValBean != null) {

                            try {
                                alReviewSOItems.get(i).setRatioSchDisAmt(primaryDisTaxValBean.getRatioSchDisAmt()!=null?primaryDisTaxValBean.getRatioSchDisAmt():Constants.str_0);
                                alReviewSOItems.get(i).setRatioSchMatPrice(primaryDisTaxValBean.getRatioSchMatPrice()!=null?primaryDisTaxValBean.getRatioSchMatPrice():Constants.str_0);
                            } catch (Exception e) {
                                alReviewSOItems.get(i).setRatioSchDisAmt(Constants.str_0);
                                alReviewSOItems.get(i).setRatioSchMatPrice(Constants.str_0);
                            }
                            try {
                                alReviewSOItems.get(i).setISFreeTypeID(primaryDisTaxValBean.getISFreeTypeID()!=null?primaryDisTaxValBean.getISFreeTypeID():Constants.str_0);
                            } catch (Exception e) {
                                alReviewSOItems.get(i).setISFreeTypeID(Constants.str_0);
                            }


                            if (!primaryDisTaxValBean.getFreeMaterialNo().equalsIgnoreCase("")) {
                                hashMapFreeMaterialByMaterial.put(skuGroupBean.getMaterialNo(), primaryDisTaxValBean);
                                hashMapFreeMatByOrderMatGrp.put(skuGroupBean.getMaterialNo(), skuGroupBean.getSKUGroup());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    alReviewSOItems.get(i).setSecScheme("0.0");
                    alReviewSOItems.get(i).setMaterialBatchBean(primaryDisTaxValBean);
                }

                alReviewSOItems.get(i).setPRMScheme(mDouPriDis + "");

                alReviewSOItems.get(i).setNetAmount(mDouSumNetTaxSecAmt + "");

            }

        }

        adjustFreeQtyDiscountVal(alReviewSOItems);
        mapSKUGRPVal = getALSKUGRP(alReviewSOItems);
    }

    private ArrayList<SchemeBean> removeDuplicateScheme(ArrayList<SchemeBean> schPerCalBeanList) {
        ArrayList<SchemeBean> schPerCalBeanListFinal = new ArrayList<>();
        ArrayList<String> schemeIdList = new ArrayList<>();
        if(schPerCalBeanList!=null) {
            for (SchemeBean schemeBean : schPerCalBeanList) {
                if (!schemeIdList.contains(schemeBean.getSchemeGuid())) {
                    schPerCalBeanListFinal.add(schemeBean);
                    schemeIdList.add(schemeBean.getSchemeGuid());
                }
            }
        }
        return schPerCalBeanListFinal;
    }

    private void setMaterialLevelScheme(ArrayList<SchemeBean> schPerCalBeanList) {
        if (schPerCalBeanList!=null) {
            for (SchemeBean schemeBean : schPerCalBeanList) {
                schemeBean.setMaterialLevel(true);
            }
        }
    }

    private double getSecSchemeAmt(double mDouSecAmt, ArrayList<SchemeCalcuBean> schemeCalcuBeanArrayList) {
        double values = 0.0;
        for (SchemeCalcuBean schemeCalcuBean: schemeCalcuBeanArrayList){
            values = values + schemeCalcuBean.getmDouSecAmt();
        }
        values = mDouSecAmt+values;
        return values;
    }

    private double getSecDiscAmtPer(double calSecPer, ArrayList<SchemeCalcuBean> schemeCalcuBeanArrayList){
        double values=0.0;
        for (SchemeCalcuBean schemeCalcuBean: schemeCalcuBeanArrayList){
            values = values + schemeCalcuBean.getmDouSecDiscount();
        }
        values = values+calSecPer;
        return values;
    }
    private double getSecDiscAmt(double calSecPer, ArrayList<SchemeCalcuBean> schemeCalcuBeanArrayList){
        double values=0.0;
        for (SchemeCalcuBean schemeCalcuBean: schemeCalcuBeanArrayList){
            values = values + schemeCalcuBean.getmDouSecDiscountAmt();
        }
        values = values+calSecPer;
        return values;
    }

    // Adjust scheme free qty material price to  net amount
    private void adjustFreeQtyDiscountVal(ArrayList<SKUGroupBean> alSKUList) {
        if (alSKUList != null && alSKUList.size() > 0) {
            for (SKUGroupBean soBean : alSKUList) {
                Double mDouFreeMaterialDiscount = 0.0;
                Double mDouFreeMatTotalDisAmt = 0.0;
                Double mDouSumNetTaxSecAmt = 0.0;

                for(SchemeCalcuBean schemeCalcuBean :soBean.getSchemeCalcuBeanArrayList()) {
                    if (schemeCalcuBean.isSchemeFreeQty()) {
                        SchemeBean mFreeMatPriceBean = schemeCalcuBean.getmFreeMat();
                        SchemeBean mGetMatBatchInfo = soBean.getMaterialBatchBean();

                        if (mGetMatBatchInfo != null) {
                            ArrayList<MaterialBatchBean> alMatBatchItemBean = mGetMatBatchInfo.getMaterialBatchBeanArrayList();
                            for (MaterialBatchBean matBatchItemBean : alMatBatchItemBean) {

                                double mBatchQtyPrice = Double.parseDouble(matBatchItemBean.getNetAmount());

                                double mDouTotalPrice = 0;
                                try {
                                    mDouTotalPrice = mapNetPriceByScheme.get(soBean.getSKUGroup()+"_"+schemeCalcuBean.getSchemeGuidNo());
                                } catch (Exception e) {
                                    mDouTotalPrice = 0.0;
                                }


                                Double mDouMatPrice = 0.0;
                                try {
                                    mDouMatPrice = Double.parseDouble(mFreeMatPriceBean.getFreeMatPrice());
                                } catch (Exception e) {
                                    mDouMatPrice = 0.0;
                                }

                                try {
                                    mDouFreeMaterialDiscount = mDouMatPrice * mBatchQtyPrice / mDouTotalPrice;
                                } catch (NumberFormatException e) {
                                    mDouFreeMaterialDiscount = 0.0;
                                }

                                if (mDouFreeMaterialDiscount.isNaN() || mDouFreeMaterialDiscount.isInfinite()) {
                                    mDouFreeMaterialDiscount = 0.0;
                                }
                                mDouFreeMatTotalDisAmt = mDouFreeMatTotalDisAmt + mDouFreeMaterialDiscount;
                                Double mDouCalNet = 0.0;
                                try {
                                    mDouCalNet = Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - mDouFreeMaterialDiscount;
                                } catch (NumberFormatException e) {
                                    mDouCalNet = 0.0;
                                }

                                String mStrTaxAmt = getTaxAmount(mDouCalNet + "", "0", matBatchItemBean.getoDataEntity(), matBatchItemBean.getQty());
                                matBatchItemBean.setTax(mStrTaxAmt);

                                mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(matBatchItemBean.getGrossAmt()) - mDouFreeMaterialDiscount + Double.parseDouble(matBatchItemBean.getTax()));
                                matBatchItemBean.setTotalNetAmt((Double.parseDouble(matBatchItemBean.getGrossAmt()) - mDouFreeMaterialDiscount + Double.parseDouble(matBatchItemBean.getTax())) + "");
                                matBatchItemBean.setGrossAmt((Double.parseDouble(matBatchItemBean.getGrossAmt()) - mDouFreeMaterialDiscount) + "");

                            }
                        }
                        soBean.setFreeMatDisAmt(mDouFreeMatTotalDisAmt + "");
                        soBean.setNetAmount(mDouSumNetTaxSecAmt + "");
                    }
                }
            }
        }

    }

    private double getSecondaryDiscountOrAmtOrFreeQty(boolean mBoolMatWise, SchemeBean schPerCalBean, String mStrSlabRuleId,
                                                      String mOrderQty, String mMatNo, String mOrderMatGrp, String mCPItemGUID, String isHeaderBased) {
        double mDoubSecDisOrAmtOrQty = 0.0;
        if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeProduct)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }

            if (mBoolMatWise) {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getMaterialDesc(), schPerCalBean.getMaterialNo(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            } else {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getMaterialDesc(), schPerCalBean.getMaterialNo(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeSKUGroup)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
            if (mBoolMatWise) {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getSKUGroupDesc(), schPerCalBean.getSKUGroupID(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            } else {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getSKUGroupDesc(), schPerCalBean.getSKUGroupID(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeCRSSKUGroup)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
            if (mBoolMatWise) {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getOrderMaterialGroupDesc(), schPerCalBean.getOrderMaterialGroupID(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            } else {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getOrderMaterialGroupDesc(), schPerCalBean.getOrderMaterialGroupID(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutPerc(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutPerc(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutAmount(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutAmount(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeScratchCard)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
            if (mBoolMatWise) {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getCardTitle(), schPerCalBean.getCardTitle(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            } else {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getCardTitle(), schPerCalBean.getCardTitle(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeFreeArticle)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }

            if (mBoolMatWise) {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getFreeArticle(), schPerCalBean.getFreeArticle(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            } else {
                mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getFreeArticle(), schPerCalBean.getFreeArticle(),
                        mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
            }
        }

        return mDoubSecDisOrAmtOrQty;
    }

    private void getInstantSchemeAndBasketScheme(){
        try {
            String schemeQry = Constants.Schemes + "?$filter= " + Constants.StatusID +
                    " eq '01' and ValidTo ge datetime'" + UtilConstants.getNewDate() + "' and ApprovalStatusID eq '03' and "+Constants.SchemeCatID+" eq '"+Constants.SchemeCatIDInstantScheme+"' ";
            Constants.HashMapSchemeValuesBySchemeGuid = OfflineManager.getInstantSchemesAndSchemeType(schemeQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }
    private void getOrderMatGrpSchemeCal() {
        if (!Constants.MAPORDQtyByCrsSkuGrp.isEmpty()) {
            setSchemeList.clear();
            Iterator iterator = Constants.MAPORDQtyByCrsSkuGrp.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                if (!Constants.MAPSCHGuidByCrsSkuGrp.get(key).isEmpty()) {
//                    setSchemeList.clear();
                    for(String schemeGuid : Constants.MAPSCHGuidByCrsSkuGrp.get(key)) {
                        //  check scheme is instant scheme or not
                        if (Constants.HashMapSchemeValuesBySchemeGuid.containsKey(schemeGuid.toUpperCase())) {  // added 15/09/2017
//                        if (isSchemeInstantOrNot(schemeGuid)) {

                            if (schemeIsAvaliable(schemeGuid)) {
                                String orderQty = Constants.MAPORDQtyByCrsSkuGrp.get(key);
                                if (Double.parseDouble(orderQty.equalsIgnoreCase("") ? "0" : orderQty) > 0) {

                                    //  check scheme is Basket scheme or not
                                    if (!Constants.HashMapSchemeValuesBySchemeGuid.get(schemeGuid.toUpperCase()).getSchemeTypeID().equalsIgnoreCase(Constants.SchemeTypeIDBasketScheme)) {    // added 15/09/2017
//                                    if (!Constants.isSchemeBasketOrNot(schemeGuid)) {
                                        //  check scheme calculation  HDR SLAB BASED/ ITEM SLAB BASED
                                        if (!isSchemeHeaderBasedOrItemBased(schemeGuid)) {
                                            SchemeBean schemeBean = getSecSchemeBeanByCrsSKUGrp((schemeGuid), key, Constants.MAPORDQtyByCrsSkuGrp.get(key), "", Constants.SchemeTypeNormal, key);
                                            if (schemeBean != null) {
                                            }
                                        } else {
                                            SchemeBean schemeBean = getSecSchemeBeanByCrsSKUGrp((schemeGuid), key, Constants.MAPORDQtyByCrsSkuGrp.get(key), Constants.X, Constants.SchemeTypeNormal,key);
                                        }
                                    } else {
                                        getBasketSchemeCal(schemeGuid,key);
                                    }

                                }
                            }

                        }
                    }
                }
            }
        }
    }


    private boolean schemeIsAvaliable(String mStrScheme) {
        boolean schemeISAval = false;
        if (setSchemeList.size() == 0) {
            schemeISAval = true;
        } else if (!setSchemeList.contains(mStrScheme)) {
            schemeISAval = true;
        }
        return schemeISAval;
    }

    private void getMaterialSchemeCal() {
        if (!Constants.MAPSCHGuidByMaterial.isEmpty()) {
            if (alReviewSOItems != null && alReviewSOItems.size() > 0) {
                for (int i = 0; i < alReviewSOItems.size(); i++) {
                    final SKUGroupBean skuGroupBean = alReviewSOItems.get(i);
                    ArrayList<String> materialSchemeArrayList = Constants.MAPSCHGuidByMaterial.get(skuGroupBean.getMaterialNo());
                    if (materialSchemeArrayList!=null && !materialSchemeArrayList.isEmpty()) {
                        for (String materialScheme : materialSchemeArrayList) {
                            // check scheme is instant scheme or not
                            if (Constants.HashMapSchemeValuesBySchemeGuid.containsKey(materialScheme.toUpperCase())) {  // added in 15/09/2017
//                                if (isSchemeInstantOrNot(materialScheme)) {

                                if (schemeIsAvaliable(materialScheme)) {
                                    // check scheme is Basket scheme or not
                                    if (!Constants.HashMapSchemeValuesBySchemeGuid.get(materialScheme.toUpperCase()).getSchemeTypeID().equalsIgnoreCase(Constants.SchemeTypeIDBasketScheme)) {    // added 15/09/2017
//                                    if (!Constants.isSchemeBasketOrNot(materialScheme)) {
                                        // check scheme calculation  HDR SLAB BASED/ ITEM SLAB BASED
                                        if (!isSchemeHeaderBasedOrItemBased(materialScheme)) {
                                            SchemeBean schemeBean = getSecSchemeBeanByMaterial(materialScheme,
                                                    skuGroupBean.getMaterialNo(), skuGroupBean.getORDQty(), "");
                                            if (schemeBean != null) {
                                                ArrayList<SchemeBean> schemeBeanArrayList = hashMapSchemeValByMaterial.get(skuGroupBean.getMaterialNo());
                                                if (schemeBeanArrayList==null){
                                                    schemeBeanArrayList = new ArrayList<>();
                                                    schemeBeanArrayList.add(schemeBean);
                                                    hashMapSchemeValByMaterial.put(skuGroupBean.getMaterialNo(), schemeBeanArrayList);
                                                }else {
                                                    if (!checkSchemeIsPresentInList(schemeBeanArrayList, schemeBean)) {
                                                        schemeBeanArrayList.add(schemeBean);
                                                        hashMapSchemeValByMaterial.put(skuGroupBean.getMaterialNo(), schemeBeanArrayList);
                                                    }
                                                }

//                                                hashMapSchemeValByMaterial.put(skuGroupBean.getMaterialNo(), schemeBean);
                                            }
                                        } else {
                                            SchemeBean schemeBean = getSecSchemeBeanByMaterial(materialScheme,
                                                    skuGroupBean.getMaterialNo(), skuGroupBean.getORDQty(), Constants.X);
                                            if (schemeBean != null) {
                                                ArrayList<SchemeBean> schemeBeanArrayList = hashMapSchemeValByMaterial.get(skuGroupBean.getMaterialNo());
                                                if (schemeBeanArrayList==null){
                                                    schemeBeanArrayList = new ArrayList<>();
                                                    schemeBeanArrayList.add(schemeBean);
                                                    hashMapSchemeValByMaterial.put(skuGroupBean.getMaterialNo(), schemeBeanArrayList);
                                                }else {
                                                    if (!checkSchemeIsPresentInList(schemeBeanArrayList, schemeBean)) {
                                                        schemeBeanArrayList.add(schemeBean);
                                                        hashMapSchemeValByMaterial.put(skuGroupBean.getMaterialNo(), schemeBeanArrayList);
                                                    }
                                                }
//                                                hashMapSchemeValByMaterial.put(skuGroupBean.getMaterialNo(), schemeBean);
                                            }
                                        }
                                    } else {
                                        getBasketSchemeCalByMaterial(materialScheme, skuGroupBean.getORDQty());
                                    }
                                }


                            }
                        }
                    }
                }
            }
        }
    }

    private void getBasketSchemeCal(String schemeGuid, String key) {
        String onSaleOnID = "", onRefID = "";

        SchemeBean headerScheme = null;
        ArrayList<SchemeBean> alItems = null;
        try {
            headerScheme = OfflineManager.getBasketSchemeHeader(Constants.SchemeItemDetails + "?$select=" + ConstantsUtils.OnSaleOfCatID + ","
                    + Constants.SchemeItemGUID + "," + Constants.ItemMin + " &$filter="
                    + Constants.SchemeGUID + " eq guid'" + schemeGuid.toUpperCase() + "' and " + Constants.ItemCatID + " eq '" + Constants.BasketCatID + "' &$top=1 ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (headerScheme != null) {
            onSaleOnID = headerScheme.getOnSaleOfCatID();
            if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfOrderMatGrp)) {  // Order Material Group
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails + "?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if (alItems != null && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            if (Constants.MAPORDQtyByCrsSkuGrp.containsKey(schemeBeanVal.getOrderMaterialGroupID())) {
                                double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                if (calItemMinQty <= Double.parseDouble(Constants.MAPORDQtyByCrsSkuGrp.get(schemeBeanVal.getOrderMaterialGroupID()))) {

                                } else {
                                    isBasketschemeAval = true;
                                    break;
                                }
                            } else {
                                isBasketschemeAval = true;
                                break;
                            }
                        }

                    }
                }

                if (!isBasketschemeAval && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        double calItemMinQty = 0.0;
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                        } else {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                        }
                        // adding brand wise min qty into map table
                        mapBasketCRSSKUGRPMinQty.put(schemeBeanVal.getOrderMaterialGroupID(), calItemMinQty);
                        addBasketSchemeTohashmapByOrderMatGrp(schemeBeanVal.getOrderMaterialGroupID(),schemeGuid,key);
                    }
                }

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfMat)) {  // Material
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails + "?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if (alItems != null && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        if (!isBasketschemeAval) {
                            for (SKUGroupBean skuGroupBean : alReviewSOItems) {
                                if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                                    if (skuGroupBean.getMaterialNo().equalsIgnoreCase(schemeBeanVal.getMaterialNo())) {
                                        double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                        if (calItemMinQty <= Double.parseDouble(skuGroupBean.getORDQty())) {
                                            schemeBeanVal.setOrderQty(skuGroupBean.getORDQty());
                                            break;
                                        } else {
                                            isBasketschemeAval = true;
                                            break;
                                        }
                                    } else {
                                        if (!setMatList.contains(skuGroupBean.getMaterialNo())) {
                                            isBasketschemeAval = true;
                                            break;
                                        }
                                    }
                                } else {
                                    if (skuGroupBean.getMaterialNo().equalsIgnoreCase(schemeBeanVal.getMaterialNo())) {
                                        schemeBeanVal.setOrderQty(skuGroupBean.getORDQty().equalsIgnoreCase("") ? "0" : skuGroupBean.getORDQty());
                                    }
                                }
                            }
                        }

                    }

                    if (!isBasketschemeAval && alItems.size() > 0) {
                        for (SchemeBean schemeBeanVal : alItems) {
                            double calItemMinQty = 0.0;
                            if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                                calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                            } else {
                                calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                            }
                            // adding material wise min qty into map table
                            mapBasketMaterialMinQty.put(schemeBeanVal.getMaterialNo(), calItemMinQty);
                            addBasketSchemeTohashmapByMaterial(schemeBeanVal.getMaterialNo(), schemeBeanVal.getOrderQty(),schemeGuid);
                        }
                    }
                }

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfProdCat)) { // Product category

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfBrand)) {  // Brand

                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails + "?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if (alItems != null && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            if (mapBrandWiseQty.containsKey(schemeBeanVal.getBrandID())) {
                                double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                if (calItemMinQty <= mapBrandWiseQty.get(schemeBeanVal.getBrandID())) {

                                } else {
                                    isBasketschemeAval = true;
                                    break;
                                }
                            } else {
                                isBasketschemeAval = true;
                                break;
                            }
                        }

                    }
                }

                if (!isBasketschemeAval && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        double calItemMinQty = 0.0;
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                        } else {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                        }
                        // adding brand wise min qty into map table
                        mapBasketBrandMinQty.put(schemeBeanVal.getBrandID(), calItemMinQty);
                        try {
                            ArrayList<String> mOrderGrp = OfflineManager.getOrderMatGrp(Constants.CPStockItems + "?$select=" + Constants.OrderMaterialGroupID + " &$filter = "
                                    + ConstantsUtils.Brand + " eq '" + schemeBeanVal.getBrandID() + "' and " + Constants.StockOwner + " eq '01' ");
                            for (String OrderMatGrp : mOrderGrp) {
                                addBasketSchemeTohashmapByOrderMatGrp(OrderMatGrp,schemeGuid,key);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfBanner)) {  // Banner
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails + "?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if (alItems != null && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            if (mapBannerWiseQty.containsKey(schemeBeanVal.getBannerID())) {
                                double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                if (calItemMinQty <= mapBannerWiseQty.get(schemeBeanVal.getBannerID())) {

                                } else {
                                    isBasketschemeAval = true;
                                    break;
                                }
                            } else {
                                isBasketschemeAval = true;
                                break;
                            }
                        }

                    }
                }

                if (!isBasketschemeAval && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        double calItemMinQty = 0.0;
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                        } else {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                        }
                        // adding banner wise min qty into map table
                        mapBasketBannerMinQty.put(schemeBeanVal.getBannerID(), calItemMinQty);

                        try {
                            ArrayList<String> mOrderGrp = OfflineManager.getOrderMatGrp(Constants.CPStockItems + "?$select=" + Constants.OrderMaterialGroupID + " &$filter = "
                                    + ConstantsUtils.Banner + " eq '" + schemeBeanVal.getBannerID() + "' and " + Constants.StockOwner + " eq '01' ");
                            for (String OrderMatGrp : mOrderGrp) {
                                addBasketSchemeTohashmapByOrderMatGrp(OrderMatGrp,schemeGuid,key);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfSchemeMatGrp)) {  // Scheme Material Group
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails + "?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if (alItems != null && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            if (mapSKUGrpWiseQty.containsKey(schemeBeanVal.getSKUGroupID())) {
                                double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                if (calItemMinQty <= mapSKUGrpWiseQty.get(schemeBeanVal.getSKUGroupID())) {

                                } else {
                                    isBasketschemeAval = true;
                                    break;
                                }
                            } else {
                                isBasketschemeAval = true;
                                break;
                            }
                        }

                    }
                }

                if (!isBasketschemeAval && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        double calItemMinQty = 0.0;
                        if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                        } else {
                            calItemMinQty = Double.parseDouble(headerScheme.getItemMin());
                        }
                        // adding banner wise min qty into map table
                        mapBasketSKUGRPMinQty.put(schemeBeanVal.getSKUGroupID(), calItemMinQty);

                        try {
                            ArrayList<String> mOrderGrp = OfflineManager.getOrderMatGrp(Constants.CPStockItems + "?$select=" + Constants.OrderMaterialGroupID + " &$filter = "
                                    + ConstantsUtils.SKUGroup + " eq '" + schemeBeanVal.getSKUGroupID() + "' and " + Constants.StockOwner + " eq '01' ");
                            for (String OrderMatGrp : mOrderGrp) {
                                addBasketSchemeTohashmapByOrderMatGrp(OrderMatGrp,schemeGuid,key);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    private void getBasketSchemeCalByMaterial(String schemeGuid, String orderQty) {
        String onSaleOnID = "", onRefID = "";

        SchemeBean headerScheme = null;
        ArrayList<SchemeBean> alItems = null;
        try {
            headerScheme = OfflineManager.getBasketSchemeHeader(Constants.SchemeItemDetails + "?$select=" + ConstantsUtils.OnSaleOfCatID + ","
                    + Constants.SchemeItemGUID + "," + Constants.ItemMin + " &$filter="
                    + Constants.SchemeGUID + " eq guid'" + schemeGuid.toUpperCase() + "' and " + Constants.ItemCatID + " eq '" + Constants.BasketCatID + "' &$top=1 ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (headerScheme != null) {
            onSaleOnID = headerScheme.getOnSaleOfCatID();
            if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfMat)) {  // Material
                alItems = OfflineManager.getBasketSchemeItems(Constants.SchemeItemDetails + "?$filter="
                        + ConstantsUtils.HierarchicalRefGUID + " eq guid'" + headerScheme.getHierarchicalRefGUID().toUpperCase() + "' ");
                boolean isBasketschemeAval = false;
                if (alItems != null && alItems.size() > 0) {
                    for (SchemeBean schemeBeanVal : alItems) {
                        if (!isBasketschemeAval) {
                            for (SKUGroupBean skuGroupBean : alReviewSOItems) {
                                if (Double.parseDouble(schemeBeanVal.getItemMin()) > 0) {
                                    if (skuGroupBean.getMaterialNo().equalsIgnoreCase(schemeBeanVal.getMaterialNo())) {
                                        double calItemMinQty = Double.parseDouble(headerScheme.getItemMin()) * Double.parseDouble(schemeBeanVal.getItemMin());
                                        if (calItemMinQty <= Double.parseDouble(skuGroupBean.getORDQty())) {
                                            schemeBeanVal.setOrderQty(skuGroupBean.getORDQty());
                                            break;
                                        } else {
                                            isBasketschemeAval = true;
                                            break;
                                        }
                                    } else {
                                        if (!setMatList.contains(schemeBeanVal.getMaterialNo())) {
                                            isBasketschemeAval = true;
                                            break;
                                        }
                                    }
                                } else {

                                    if (skuGroupBean.getMaterialNo().equalsIgnoreCase(schemeBeanVal.getMaterialNo())) {
                                        schemeBeanVal.setOrderQty(skuGroupBean.getORDQty().equalsIgnoreCase("") ? "0" : skuGroupBean.getORDQty());
                                    }
                                }
                            }
                        }

                    }

                    if (!isBasketschemeAval && alItems.size() > 0) {
                        for (SchemeBean schemeBeanVal : alItems) {
                            addBasketSchemeTohashmapByMaterial(schemeBeanVal.getMaterialNo(), schemeBeanVal.getOrderQty(), schemeGuid);
                        }
                    }
                }

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfProdCat)) { // Product category

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfBrand)) {  // Brand

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfBanner)) {  // Banner

            } else if (onSaleOnID.equalsIgnoreCase(Constants.OnSaleOfSchemeMatGrp)) {  // Scheme Material Group

            }
        }

    }

    private void addBasketSchemeTohashmapByOrderMatGrp(String orderMatgrp, String schemeGuid, String key) {
        // check scheme calculation  HDR SLAB BASED/ ITEM SLAB BASED
        if (!isSchemeHeaderBasedOrItemBased(schemeGuid)) {
            SchemeBean schemeBean = getSecSchemeBeanByCrsSKUGrp(schemeGuid, orderMatgrp, Constants.MAPORDQtyByCrsSkuGrp.get(orderMatgrp), "", Constants.SchemeTypeBasket, key);
            if (schemeBean != null) {
               /* HashSet<SchemeBean> schemeBeanArrayList = hashMapSchemeValByOrderMatGrp.get(orderMatgrp);
                if (schemeBeanArrayList==null){
                    schemeBeanArrayList = new HashSet<>();
                }
                schemeBeanArrayList.add(schemeBean);
                hashMapSchemeValByOrderMatGrp.put(orderMatgrp, schemeBeanArrayList);*/
            }
        } else {
            SchemeBean schemeBean = getSecSchemeBeanByCrsSKUGrp(schemeGuid, orderMatgrp, Constants.MAPORDQtyByCrsSkuGrp.get(orderMatgrp), Constants.X, Constants.SchemeTypeBasket, key);
            if (schemeBean != null) {
               /* HashSet<SchemeBean> schemeBeanArrayList = hashMapSchemeValByOrderMatGrp.get(orderMatgrp);
                if (schemeBeanArrayList==null){
                    schemeBeanArrayList = new HashSet<>();
                }
                schemeBeanArrayList.add(schemeBean);
                hashMapSchemeValByOrderMatGrp.put(orderMatgrp, schemeBeanArrayList);*/
            }
        }
    }

    private void addBasketSchemeTohashmapByMaterial(String matNo, String orderQty, String schemeGuid) {
        // check scheme calculation  HDR SLAB BASED/ ITEM SLAB BASED
        if (!isSchemeHeaderBasedOrItemBased(schemeGuid)) {
            SchemeBean schemeBean = getSecSchemeBeanByMaterial(schemeGuid,
                    matNo, orderQty, "");
            if (schemeBean != null) {
                ArrayList<SchemeBean> schemeBeanArrayList = hashMapSchemeValByMaterial.get(matNo);
                if (schemeBeanArrayList==null){
                    schemeBeanArrayList = new ArrayList<>();
                    schemeBeanArrayList.add(schemeBean);
                    hashMapSchemeValByMaterial.put(matNo, schemeBeanArrayList);
                }else {
                    if (!checkSchemeIsPresentInList(schemeBeanArrayList, schemeBean)) {
                        schemeBeanArrayList.add(schemeBean);
                        hashMapSchemeValByMaterial.put(matNo, schemeBeanArrayList);
                    }
                }
//                hashMapSchemeValByMaterial.put(matNo, schemeBean);
            }
        } else {
            SchemeBean schemeBean = getSecSchemeBeanByMaterial(schemeGuid,
                    matNo, orderQty, Constants.X);
            if (schemeBean != null) {
                ArrayList<SchemeBean> schemeBeanArrayList = hashMapSchemeValByMaterial.get(matNo);
                if (schemeBeanArrayList==null){
                    schemeBeanArrayList = new ArrayList<>();
                    schemeBeanArrayList.add(schemeBean);
                    hashMapSchemeValByMaterial.put(matNo, schemeBeanArrayList);
                }else {
                    if (!checkSchemeIsPresentInList(schemeBeanArrayList, schemeBean)) {
                        schemeBeanArrayList.add(schemeBean);
                        hashMapSchemeValByMaterial.put(matNo, schemeBeanArrayList);
                    }
                }
//                hashMapSchemeValByMaterial.put(matNo, schemeBean);
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
                    } else {
                        mDoubCRSQty = mapCRSSKUQTY.get(bean.getSKUGroup());
                    }
                    double mDouNetPrice = Double.parseDouble(bean.getNetAmount()) + mapNetAmt.get(bean.getSKUGroup());
                    double mDouPriSchPer = Double.parseDouble(bean.getPRMScheme()) + mapPriSchemePer.get(bean.getSKUGroup());
                    double mDouSecSchPer = Double.parseDouble(bean.getSecScheme()) + mapSecSchemePer.get(bean.getSKUGroup());
                    double mDouSecSchAmt = Double.parseDouble(bean.getSecSchemeAmt()) + mapSecSchemeAmt.get(bean.getSKUGroup());

                    double mDouRatioSchDisAmt = 0.0;
                    try {
                        mDouRatioSchDisAmt = Double.parseDouble(bean.getRatioSchDisAmt()) + mapRatioSchDis.get(bean.getSKUGroup());
                    } catch (NumberFormatException e) {
                        mDouRatioSchDisAmt = 0.0;
                    }

                    double mDouFreeSchDisAmt = 0.0;
                    try {
                        mDouFreeSchDisAmt = Double.parseDouble(bean.getFreeMatDisAmt().equalsIgnoreCase("")?"0":bean.getFreeMatDisAmt()) + mapRatioSchDis.get(bean.getSKUGroup());
                    } catch (NumberFormatException e) {
                        mDouFreeSchDisAmt = 0.0;
                    }


                    int matCountInc = mapCntMatByCRSKUGRP.get(bean.getSKUGroup());
                    mapCntMatByCRSKUGRP.put(bean.getSKUGroup(), matCountInc + 1);

                    mapSecSchemeAmt.put(bean.getSKUGroup(), mDouSecSchAmt);
                    mapSecSchemePer.put(bean.getSKUGroup(), mDouSecSchPer);
                    mapPriSchemePer.put(bean.getSKUGroup(), mDouPriSchPer);
                    mapNetAmt.put(bean.getSKUGroup(), mDouNetPrice);
                    mapRatioSchDis.put(bean.getSKUGroup(), mDouRatioSchDisAmt);
                    mapFreeDisAmt.put(bean.getSKUGroup(), mDouFreeSchDisAmt);
                    mapCRSSKUQTY.put(bean.getSKUGroup(), mDoubCRSQty);
                    mapUOM.put(bean.getSKUGroup(), bean.getUOM());
                    mapSKUList.put(bean.getSKUGroup(), bean);


                    mDobTotalOrderVal = mDobTotalOrderVal + Double.parseDouble(bean.getNetAmount());
                } else {
                    double mDoubNetAmt = Double.parseDouble(bean.getNetAmount());
                    double mDouPriSchPer = Double.parseDouble(bean.getPRMScheme());
                    double mDouSecSchPer = Double.parseDouble(bean.getSecScheme());
                    double mDouSecSchAmt = Double.parseDouble(bean.getSecSchemeAmt());
                    double mDouRatioSchDisAmt = 0.0;
                    try {
                        mDouRatioSchDisAmt= Double.parseDouble(bean.getRatioSchDisAmt());
                    } catch (NumberFormatException e) {
                        mDouRatioSchDisAmt = 0.0;
                    }
                    double mDouFreeSchDisAmt = 0.0;
                    try {
                        mDouFreeSchDisAmt= Double.parseDouble(bean.getFreeMatDisAmt());
                    } catch (NumberFormatException e) {
                        mDouFreeSchDisAmt = 0.0;
                    }
                    BigDecimal mDoubOrderQty = null;
                    try {
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        decimalFormat.setParseBigDecimal(true);
                        mDoubOrderQty = (BigDecimal) decimalFormat.parse(bean.getORDQty());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    mapSecSchemeAmt.put(bean.getSKUGroup(), mDouSecSchAmt);
                    mapCntMatByCRSKUGRP.put(bean.getSKUGroup(), 1);
                    mapNetAmt.put(bean.getSKUGroup(), mDoubNetAmt);
                    mapCRSSKUQTY.put(bean.getSKUGroup(), mDoubOrderQty);
                    mapPriSchemePer.put(bean.getSKUGroup(), mDouPriSchPer);
                    mapSecSchemePer.put(bean.getSKUGroup(), mDouSecSchPer);

                    mapRatioSchDis.put(bean.getSKUGroup(), mDouRatioSchDisAmt);
                    mapFreeDisAmt.put(bean.getSKUGroup(), mDouFreeSchDisAmt);

                    mapUOM.put(bean.getSKUGroup(), bean.getUOM());
                    mapSKUList.put(bean.getSKUGroup(), bean);

                    mDobTotalOrderVal = mDobTotalOrderVal + Double.parseDouble(bean.getNetAmount());
                }
        }

        return mapSKUList;
    }

    Set<String> mFreeMatScheme = new HashSet<>();
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

                double avgPriDisVal = 0.0;
                try {
                    avgPriDisVal = 0.0;
                   // avgPriDisVal = mapPriSchemePer.get(key) / mapCntMatByCRSKUGRP.get(key);
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
                            mAfterTaxCal = Double.parseDouble(schemeCalcuBean.getmFreeMat()!=null?schemeCalcuBean.getmFreeMat().getFreeMatTax():"0.00");
                            mFreeMatScheme.add(schemeCalcuBean.getSchemeGuidNo());
                        } catch (Exception ex) {
                            mAfterTaxCal = 0.0;
                        }
                        try {

                            mStrFreeQtyDisAmt = schemeCalcuBean.getmFreeMat()!=null?schemeCalcuBean.getmFreeMat().getFreeMatTax():"0.00";//hashMapFreeQtyInfoBySchemeGuid.get(skuGroupBean.getSchemeGuidNo()).getFreeMatPrice();
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
                if(skuGroupBean.getISFreeTypeID().equalsIgnoreCase(Constants.str_2)){

                    tv_ratio_discount_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(skuGroupBean.getRatioSchDisAmt()));
                    try{
                        mDobTotalOrderVal = mDobTotalOrderVal + Double.parseDouble(skuGroupBean.getRatioSchMatPrice());
                    }catch (Exception ex){
                        mDobTotalOrderVal = mDobTotalOrderVal + 0;
                    }
                }
                //  display end Ratio scheme Price discount 24072017

                tlSOList.addView(llSKUGroupItem);
                tlCRSList.addView(llCRSKUGroup);

                if (alSchFreeProd.size() > 0) {
                    for (SchemeBean schemeBean : alSchFreeProd) {
                        if (key.equalsIgnoreCase(schemeBean.getOrderMaterialGroupID()) && !schemeBean.isRatioScheme()) {
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

                            try{
                                mAfterTaxCal = Double.parseDouble(schemeBean.getFreeMatPrice()) + Double.parseDouble(schemeBean.getFreeMatTax());
                            }catch (Exception ex){
                                mAfterTaxCal = 0.0;
                            }

                            tv_net_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mAfterTaxCal+""));

                            if(mAfterTaxCal<=0){
                                tv_sku_grp_desc.setTextColor(getResources().getColor(R.color.BLUE));
                            }

                            String stFreeQty = "";
                            try {
                                if (OfflineManager.checkNoUOMZero(schemeBean.getFreeQtyUOM()))
                                    stFreeQty = OfflineManager.trimQtyDecimalPlace(schemeBean.getFreeQty());
                                else
                                    stFreeQty = schemeBean.getFreeQty();
                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                            }
                            tv_ord_qty.setText(stFreeQty + " " + schemeBean.getFreeQtyUOM());
                            tv_sec_scheme.setVisibility(View.INVISIBLE);
                            tv_primary_scheme.setVisibility(View.INVISIBLE);
                            tlSOList.addView(llSKUGroupItem);
                            tlCRSList.addView(llCRSKUGroup);
                            break;
                        }
                    }
                }
                try {
                    if (hashMapFreeMatByOrderMatGrp.containsValue(key)) {

                        Set<String> keys = Constants.getKeysByValue(hashMapFreeMatByOrderMatGrp, key);
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
                                        stFreeQty = OfflineManager.trimQtyDecimalPlace(ratioSchemeMatBean.getFreeQty());
                                    else
                                        stFreeQty = ratioSchemeMatBean.getFreeQty();
                                } catch (OfflineODataStoreException e) {
                                    e.printStackTrace();
                                }
                                tv_ord_qty.setText(stFreeQty + " " + ratioSchemeMatBean.getUOM());

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

            LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(SalesOrderReviewActivity1.this)
                    .inflate(R.layout.so_review_empty_layout, null);

            tlSOList.addView(llEmptyLayout);
        }
    }


    private SchemeBean getPrimaryTaxValByMaterial(String cPStockItemGUID, String mStrMatNo, String mStrOrderQty, boolean ratioSchemeCal) {

        SchemeBean mStrNetAmount = null;
        try {

            mStrNetAmount = OfflineManager.getNetAmountSalesReview(Constants.CPStockItemSnos + "?$filter=" + Constants.MaterialNo + " eq '" + mStrMatNo + "' and "
                    + Constants.CPStockItemGUID + " eq guid'" + cPStockItemGUID + "' and " + Constants.StockTypeID + " eq '" + Constants.str_1 + "'  &$orderby=" + Constants.ManufacturingDate + "%20asc ", mStrOrderQty, mStrMatNo,ratioSchemeCal);
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
        menuSave = menu.findItem(R.id.menu_save);
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
                menuSave.setEnabled(false);
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

    /*AsyncTask to save vales into datavault*/
    private class SaveValToDataVault extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(SalesOrderReviewActivity1.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.saving_data_plz_wait));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            onSaveValesToDataVault(UtilConstants.latitude,UtilConstants.longitude,distance);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                pdLoadDialog.dismiss();
                if(distance>distanceconfigtype)
                {
                    Toast.makeText(SalesOrderReviewActivity1.this,"Distance is greater than "+String.valueOf(distanceconfigtype),Toast.LENGTH_LONG).show();
                }
                else {

                    Toast.makeText(SalesOrderReviewActivity1.this,"Distance is within"+String.valueOf(distanceconfigtype),Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            navigateToVisit();
        }
    }


    private void onSaveValesToDataVault(double latitude,double longitude,double distance) {
        String doc_no = (System.currentTimeMillis() + "");

        GUID ssoHeaderGuid = GUID.newRandom();
        headerTable.put(Constants.SSSOGuid, ssoHeaderGuid.toString36().toUpperCase());
        headerTable.put(Constants.OrderNo, doc_no);
        String ordettype = "";
        try {
            ordettype = getValueByColumnName(Constants.ValueHelps + "?$select=" + Constants.ID + " &$filter=" + Constants.EntityType + " eq 'SSSO' and  " +
                    "" + Constants.PropName + " eq 'OrderType' and  " + Constants.ParentID + " eq '000010' ", Constants.ID);
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
        headerTable.put(Constants.SPGUID, mArraySPValues[4][0] != null ? mArraySPValues[4][0] : "");
        headerTable.put(Constants.SPNo, mArraySPValues[6][0] != null ? mArraySPValues[6][0] : "");
        headerTable.put(Constants.FirstName, mArrayDistributors[3][0]);
        headerTable.put(Constants.GrossAmt, "0");

        headerTable.put(Constants.Currency, mArrayDistributors[10][0]);
        headerTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
        headerTable.put(Constants.CreatedAt, UtilConstants.getOdataDuration().toString());
        headerTable.put(Constants.TLSD, mStrCrsSku.size() + "");
        //geofencing changes...
         headerTable.put(Constants.Distance,String.valueOf(distance));
         headerTable.put(Constants.Longitude, String.valueOf(BigDecimal.valueOf(longitude)));
         headerTable.put(Constants.Latitude,String.valueOf(BigDecimal.valueOf(latitude)));
        if(distance>distanceconfigtype)
        {headerTable.put(Constants.Remarks,"Distance is greater than "+String.valueOf(distanceconfigtype));}
        else
        { headerTable.put(Constants.Remarks,"Distance is within"+String.valueOf(distanceconfigtype)); }
        //.............//............//...............


        try {
            if (mStrCpPartnerFunVal!=null &&  mStrCpPartnerFunVal.size()>0) {
                headerTable.put(Constants.BillToCPGUID, partnerFunctionsBean.getPartnarCPGUID());
            }else {
                headerTable.put(Constants.BillToCPGUID, mStrBundleCPGUID);
            }
        } catch (Exception e) {
            headerTable.put(Constants.BillToCPGUID, mStrBundleCPGUID);
            e.printStackTrace();
        }


        ArrayList<HashMap<String, String>> soItems = new ArrayList<>();
        Double mDouTotNetAmt = 0.0, mDouNetAmt = 0.0, mDouGrossAmt = 0.0, mDouTotalGrossAmt = 0.0, mDouTotalOrderQty = 0.0, mDouTotalFreeQty = 0.0;
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
                        singleItem.put(Constants.MRP, matBatchItemBean.getMRP().equalsIgnoreCase("") ? "0" : matBatchItemBean.getMRP());
                        singleItem.put(Constants.GrossAmt, mDouGrossAmt + "");
                        singleItem.put(Constants.TAX, matBatchItemBean.getTax().equalsIgnoreCase("") ? "0" : matBatchItemBean.getTax());

                        singleItem.put(Constants.SecDiscount, /*matBatchItemBean.getSecDiscountAmt().equalsIgnoreCase("")?"0":matBatchItemBean.getSecDiscountAmt()*/"0");
                        singleItem.put(Constants.PriDiscount, /*matBatchItemBean.getNetAmtAftPriDis().equalsIgnoreCase("")?"0":matBatchItemBean.getNetAmtAftPriDis()*/ "0");
                        singleItem.put(Constants.CashDiscount, "0");
                        singleItem.put(Constants.CashDiscountPerc, "0");
                        singleItem.put(Constants.SecondaryDiscountPerc, matBatchItemBean.getSecPer().equalsIgnoreCase("") ? "0" : matBatchItemBean.getSecPer());
                        singleItem.put(Constants.PrimaryDiscountPerc, matBatchItemBean.getPrimaryPer().equalsIgnoreCase("") ? "0" : matBatchItemBean.getPrimaryPer());

                        singleItem.put(Constants.Batch, matBatchItemBean.getBatchNo());
                        singleItem.put(Constants.TransRefTypeID, matBatchItemBean.getTransRefTypeID());
                        singleItem.put(Constants.TransRefNo, matBatchItemBean.getTransRefNo());
                        singleItem.put(Constants.TransRefItemNo, matBatchItemBean.getTransRefItemNo());
                        try {
                            singleItem.put(Constants.MFD, ConstantsUtils.convertDateFromString(matBatchItemBean.getMFD()));
                        } catch (Exception e) {
                            singleItem.put(Constants.MFD, "");
                        }
                        singleItem.put(Constants.IsfreeGoodsItem, "");
                        soItems.add(singleItem);
                        incItemCountVal++;
                    }
                }
            }
        }

//        for (SKUGroupBean skuGroupBeanItem : alReviewSOItems) {
//            if (skuGroupBeanItem.getMaterialBatchBean() != null) {
//                SchemeBean mGetMatBatchInfo = skuGroupBeanItem.getMaterialBatchBean();
//                if (mGetMatBatchInfo != null) {
//                    ArrayList<MaterialBatchBean> alMatBatchItemBean = mGetMatBatchInfo.getMaterialBatchBeanArrayList();
        for (SchemeBean schemeBean : alSchFreeProd) {
            if (!schemeBean.isRatioScheme()) {
                HashMap<String, String> singleItem = new HashMap<>();
                GUID ssoItemGuid = GUID.newRandom();
                singleItem.put(Constants.SSSOItemGUID, ssoItemGuid.toString36().toUpperCase());
                singleItem.put(Constants.SSSOGuid, ssoHeaderGuid.toString36().toUpperCase());
                singleItem.put(Constants.ItemNo, (incItemCountVal + 1) + "");
                singleItem.put(Constants.MaterialNo, schemeBean.getFreeMAt());
                singleItem.put(Constants.MaterialDesc, schemeBean.getFreeMatTxt());
                singleItem.put(Constants.OrderMatGrp, schemeBean.getOrderMaterialGroupID());
                singleItem.put(Constants.OrderMatGrpDesc, schemeBean.getOrderMaterialGroupDesc());
                singleItem.put(Constants.Currency, mArrayDistributors[10][0]);
                singleItem.put(Constants.Uom, schemeBean.getFreeQtyUOM());
                singleItem.put(Constants.UnitPrice, schemeBean.getFreeMatPrice());
                try {
                    mDouNetAmt = Double.parseDouble(schemeBean.getFreeMatPrice()) + Double.parseDouble(schemeBean.getFreeMatTax());
                    mDouTotNetAmt = mDouTotNetAmt + Double.parseDouble(schemeBean.getFreeMatPrice()) + Double.parseDouble(schemeBean.getFreeMatTax());

//                            mDouGrossAmt = Double.parseDouble(matBatchItemBean.getGrossAmt());
//                            mDouTotalGrossAmt = mDouTotalGrossAmt + Double.parseDouble(matBatchItemBean.getGrossAmt());

                    mDouTotalFreeQty = mDouTotalFreeQty + Double.parseDouble(schemeBean.getFreeQty());
                } catch (NumberFormatException e) {
                    mDouNetAmt = 0.0;
                    mDouTotalFreeQty = 0.0;
                }
                singleItem.put(Constants.Quantity, schemeBean.getFreeQty());
                singleItem.put(Constants.NetPrice, mDouNetAmt + "");
                singleItem.put(Constants.MRP, "");
                singleItem.put(Constants.GrossAmt, "0.0");
                singleItem.put(Constants.TAX, schemeBean.getFreeMatTax());

                singleItem.put(Constants.SecDiscount, /*matBatchItemBean.getSecDiscountAmt().equalsIgnoreCase("")?"0":matBatchItemBean.getSecDiscountAmt()*/"0");
                singleItem.put(Constants.PriDiscount, /*matBatchItemBean.getNetAmtAftPriDis().equalsIgnoreCase("")?"0":matBatchItemBean.getNetAmtAftPriDis()*/ "0");
                singleItem.put(Constants.CashDiscount, "0");
                singleItem.put(Constants.CashDiscountPerc, "0");
                singleItem.put(Constants.SecondaryDiscountPerc, "0");
                singleItem.put(Constants.PrimaryDiscountPerc, "0");

                singleItem.put(Constants.Batch, schemeBean.getBatch());
                singleItem.put(Constants.MFD, "");
                singleItem.put(Constants.IsfreeGoodsItem, "X");
                soItems.add(singleItem);
                incItemCountVal++;
            }
        }

            for (SchemeBean schemeBean : alSchFreeProd) {
                if (schemeBean.isRatioScheme()) {
                    HashMap<String, String> singleItem = new HashMap<>();
                    GUID ssoItemGuid = GUID.newRandom();
                    singleItem.put(Constants.SSSOItemGUID, ssoItemGuid.toString36().toUpperCase());
                    singleItem.put(Constants.SSSOGuid, ssoHeaderGuid.toString36().toUpperCase());
                    singleItem.put(Constants.ItemNo, (incItemCountVal + 1) + "");
                    singleItem.put(Constants.MaterialNo, schemeBean.getFreeMaterialNo());
                    singleItem.put(Constants.MaterialDesc, schemeBean.getFreeMaterialNo());
                    singleItem.put(Constants.OrderMatGrp, "");
                    singleItem.put(Constants.OrderMatGrpDesc, "");
                    singleItem.put(Constants.Currency, mArrayDistributors[10][0]);
                    singleItem.put(Constants.Uom, schemeBean.getUOM());
                    singleItem.put(Constants.UnitPrice, schemeBean.getRatioSchMatPrice());
                    try {
                        mDouTotNetAmt = mDouTotNetAmt + Double.parseDouble(schemeBean.getRatioSchMatPrice());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    singleItem.put(Constants.Quantity, schemeBean.getFreeQty());
                    singleItem.put(Constants.NetPrice, schemeBean.getRatioSchMatPrice() + "");
                    singleItem.put(Constants.MRP, "");
                    singleItem.put(Constants.GrossAmt, "0.0");
                    singleItem.put(Constants.TAX, ""/*schemeBean.getFreeMatTax()*/);

                    singleItem.put(Constants.SecDiscount, /*matBatchItemBean.getSecDiscountAmt().equalsIgnoreCase("")?"0":matBatchItemBean.getSecDiscountAmt()*/"0");
                    singleItem.put(Constants.PriDiscount, /*matBatchItemBean.getNetAmtAftPriDis().equalsIgnoreCase("")?"0":matBatchItemBean.getNetAmtAftPriDis()*/ "0");
                    singleItem.put(Constants.CashDiscount, "0");
                    singleItem.put(Constants.CashDiscountPerc, "0");
                    singleItem.put(Constants.SecondaryDiscountPerc, "0");
                    singleItem.put(Constants.PrimaryDiscountPerc, "0");

                    singleItem.put(Constants.Batch, "");
                    singleItem.put(Constants.MFD, "");
                    singleItem.put(Constants.IsfreeGoodsItem, "X");
                    soItems.add(singleItem);
                    incItemCountVal++;
                }
            }
//            if(mDouTotNetAmt>0){
        try {
            String mRouteSchGuid = Constants.getRouteSchGUID(Constants.RouteSchedulePlans,Constants.RouteSchGUID,Constants.VisitCPGUID,mStrBundleCPGUID32,mArrayDistributors[5][0]);
            if(!mRouteSchGuid.equalsIgnoreCase("") ){
                headerTable.put(Constants.BeatGuid, mRouteSchGuid);
            }else{
                headerTable.put(Constants.BeatGuid, "");
            }
        } catch (Exception e) {
            headerTable.put(Constants.BeatGuid, "");
            e.printStackTrace();
        }
                headerTable.put(Constants.NetPrice, mDouTotNetAmt + "");
                headerTable.put(Constants.Quantity, mDouTotalOrderQty + "");
                headerTable.put(Constants.FreeQuantity, mDouTotalFreeQty + "");
                headerTable.put(Constants.GrossAmount, mDouTotalGrossAmt + "");
                headerTable.put(Constants.entityType, Constants.SecondarySOCreate);
                headerTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(soItems));
                headerTable.put(Constants.TestRun, Constants.TestRun_Text);
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);


                Constants.saveDeviceDocNoToSharedPref(SalesOrderReviewActivity1.this, Constants.SOList, doc_no);

                headerTable.put(Constants.LOGINID, sharedPreferences.getString(Constants.username, "").toUpperCase());

                JSONObject jsonHeaderObject = new JSONObject(headerTable);

                UtilDataVault.storeInDataVault(doc_no, jsonHeaderObject.toString());

                Constants.onVisitActivityUpdate(mStrBundleCPGUID32, sharedPreferences.getString(Constants.username, ""),
                        ssoHeaderGuid.toString36().toUpperCase(), Constants.SOCreateID, Constants.SecondarySOCreate);
//            }else{
//                unitPriceNotMaintained();
//            }



    }
    private void unitPriceNotMaintained(){
        closingProgressDialog();
        UtilConstants.showAlert(getString(R.string.alert_unit_price_cannot_zero), SalesOrderReviewActivity1.this);
    }

    private void onSave() {
//        if (Constants.isValidTime(UtilConstants.convertTimeOnly(Constants.mStartTimeDuration.toString()),
//                UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString())) && Constants.isValidDate()) {

        if(mStrCrsSKUList.size()>0){
            String mStrSKUGrp = UtilConstants.getConcatinatinFlushCollectios(mStrCrsSKUList);
            UtilConstants.showAlert(getString(R.string.alert_mat_batch_not_avalible,mStrSKUGrp), SalesOrderReviewActivity1.this);
            menuSave.setEnabled(true);
        }else{
            if (mStrCrsSku.size() > 0) {
                pdLoadDialog = Constants.showProgressDialog(SalesOrderReviewActivity1.this, "", getString(R.string.checking_pemission));
                LocationUtils.checkLocationPermission(SalesOrderReviewActivity1.this, new LocationInterface() {
                    @Override
                    public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                        closingProgressDialog();
                        if (status) {
                            locationPerGranted();

                        }else{
                            menuSave.setEnabled(true);
                        }
                    }
                });
            } else {
                UtilConstants.showAlert(getString(R.string.alert_enter_atlest_one_material), SalesOrderReviewActivity1.this);
                menuSave.setEnabled(true);
            }
        }

//        } else {
//            UtilConstants.showAlert(getString(R.string.validation_app_time_incorrect), SalesOrderReviewActivity1.this);
//        }


    }
    private void closingProgressDialog(){
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void locationPerGranted(){
        pdLoadDialog = Constants.showProgressDialog(SalesOrderReviewActivity1.this,"",getString(R.string.gps_progress));
        Constants.getLocation(SalesOrderReviewActivity1.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closingProgressDialog();
                if(status){
                 latlng_timeofsave =  new LatLng(UtilConstants.latitude,UtilConstants.longitude);
                   System.out.println(latlng_timeofsave);
                   Distance_retailer_and_save();
                    saveAsyncTask();
                }else{
                    menuSave.setEnabled(true);
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case UtilConstants.Location_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationUtils.checkLocationPermission(SalesOrderReviewActivity1.this, new LocationInterface() {
                        @Override
                        public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                            if(status){
                                locationPerGranted();
                            }
                        }
                    });
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


        }
        // other 'case' lines to check for other
        // permissions this app might request
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if(requestCode==LocationUtils.REQUEST_CHECK_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                locationPerGranted();
            }
        }
    }
    /*Navigate to day summary screen*/
    public void navigateToVisit() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                SalesOrderReviewActivity1.this, R.style.MyTheme);
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

    /*Navigates to Day Summary*/
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
        menuSave.setEnabled(true);
    }

    int schmeIndex = 0;
    private SchemeBean getSecSchemeBeanByCrsSKUGrp(String schemeGUID, String mStrCRSSKUGrp, String mStrOrderQty, String mStrHeaderOrItemType, String schemeType, String orderMatGrpid) {
        SchemeBean schemeBean = null;
        SchemeBean schemeItemBean = null;
        String mStrSchemeItemGuid = "";
        String getCondition = "";

        String mStrSaleOfCatId = "0";
        String mStrMinOrderQty = "0";

        boolean schemeISAval = false;
        if (setSchemeList.size() == 0) {
            schemeISAval = true;
        } else if (!setSchemeList.contains(schemeGUID)) {
            schemeISAval = true;
        }

        if (schemeISAval) {
            schmeIndex++;
//            try {
//                mStrSaleOfCatId = OfflineManager.getValueByColumnName(Constants.SchemeItemDetails + "?$filter="
//                        + Constants.SchemeGUID + " eq guid'" + schemeGUID + "' &$top=1", Constants.OnSaleOfCatID);
//            } catch (OfflineODataStoreException e) {
//                e.printStackTrace();
//            }
            try {
                schemeItemBean = OfflineManager.getSchemeItemDetailsBySchemeGuid(Constants.SchemeItemDetails + "?$filter="
                        + Constants.SchemeGUID + " eq guid'" + schemeGUID + "' &$top=1");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

//            if (!mStrSaleOfCatId.equalsIgnoreCase("000006")) {
            if (!schemeItemBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfMat)) {
                mStrMinOrderQty = schemeItemBean.getItemMin();
                /*try {
                    mStrMinOrderQty = OfflineManager.getQtyValueByColumnName(Constants.SchemeItemDetails + "?$filter="
                            + Constants.SchemeGUID + " eq guid'" + schemeGUID + "'   ", Constants.ItemMin);
//                &$top=1
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }*/
                boolean mBoolMinItemQtyAval = false;
                if (Double.parseDouble(mStrMinOrderQty) >= 0) {

                    if (Double.parseDouble(mStrOrderQty.equalsIgnoreCase("") ? "0" : mStrOrderQty) > 0) {
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
                    if (!mStrHeaderOrItemType.equalsIgnoreCase(Constants.X)) {
                        String[] orderMatGrpArray = new String[1];
                        orderMatGrpArray[0] = mStrCRSSKUGrp;
                        try {
                            schemeBean = OfflineManager.getSecondarySchemeSlabPer(Constants.SchemeSlabs + "?$filter="
                                            + Constants.SchemeItemGUID + " eq guid'" + schemeGUID.toUpperCase() + "' ",
                                    mStrOrderQty, schemeGUID, Constants.SKUGroupID, orderMatGrpArray, mStrHeaderOrItemType,schemeItemBean);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                    } else {


                        if (schemeISAval) {
                            Double mStrSumQtyBySKU = 0.0;
                            Multimap<String, String> multiMap = HashMultimap.create();
                            for (Map.Entry<String, ArrayList<String>> entry : Constants.MAPSCHGuidByCrsSkuGrp.entrySet()) {
                                if (!entry.getValue().isEmpty()) {
                                    for (String stValue : entry.getValue()) {
                                        multiMap.put(stValue, entry.getKey());
                                    }
                                }
                            }
                            String[] orderMatGrpArray = new String[multiMap.get(schemeGUID).size()];
                            for (Map.Entry<String, Collection<String>> entry : multiMap.asMap().entrySet()) {
                                if ((schemeGUID).equalsIgnoreCase(entry.getKey())) {
                                    Collection<String> values = entry.getValue();
                                    int index = 0;
                                    for (String value : values) {
                                        if (Constants.MAPORDQtyByCrsSkuGrp.containsKey(value)) {
                                            if (Double.parseDouble(Constants.MAPORDQtyByCrsSkuGrp.get(value).equalsIgnoreCase("") ? "0" : Constants.MAPORDQtyByCrsSkuGrp.get(value)) > 0) {
                                                orderMatGrpArray[index] = value;
                                                mStrSumQtyBySKU = mStrSumQtyBySKU + Double.parseDouble(Constants.MAPORDQtyByCrsSkuGrp.get(value));
                                                index++;
                                            }
                                        } else {
                                            orderMatGrpArray[index] = value;
                                            index++;
                                        }
                                    }
                                }
                            }
                            try {
                                schemeBean = OfflineManager.getSecondarySchemeSlabPer(Constants.SchemeSlabs + "?$filter="
                                                + Constants.SchemeItemGUID + " eq guid'" + schemeGUID.toUpperCase() + "' ", mStrSumQtyBySKU + "",
                                        schemeGUID, Constants.SKUGroupID, orderMatGrpArray, mStrHeaderOrItemType,schemeItemBean);
                            } catch (OfflineODataStoreException e) {
                                e.printStackTrace();
                            }


                            if (schemeBean != null) {
                                orderMatGrp = orderMatGrpArray;
                                mapHeaderWiseSchemeQty.put(schemeGUID.toUpperCase(), mStrSumQtyBySKU + "");
                                mFreeMat = null;
                                Double mDouSlabTypeCal = 0.0;
                                int incVal = 0;
                                if (schemeBean.getTargetBasedID().equalsIgnoreCase("02")) {
                                    mDouSlabTypeCal = getSecondaryDiscountOrAmtOrFreeQtyHeaderWise(schemeBean, schemeBean.getSlabRuleID(),
                                            schemeBean.getSaleUnitID().equalsIgnoreCase(Constants.SchemeSaleUnitIDCBB)?schemeBean.getCBBQty():mStrSumQtyBySKU + "", "", "", "", Constants.X);

                                    // added 06/09/2017
                                    if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                        try {

                                            for (String omGId : orderMatGrpArray) {
                                                String orderQty = Constants.MAPORDQtyByCrsSkuGrp.get(omGId);
                                                if (orderQty != null && !orderQty.equalsIgnoreCase("")) {
                                                    if (Double.parseDouble(orderQty) > 0) {
                                                        incVal++;
                                                    }
                                                }
                                            }
                                            if (incVal > 0) {
                                                schemeBean.setPayoutAmount((Double.parseDouble(schemeBean.getPayoutAmount()) / incVal) + "");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                } else {
                                    String mStrFreeQty = "";
                                    if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
                                        mStrFreeQty = schemeBean.getPayoutPerc();
                                    } else if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                        try {

                                            for (String omGId : orderMatGrpArray) {
                                                String orderQty = Constants.MAPORDQtyByCrsSkuGrp.get(omGId);
                                                if (orderQty != null && !orderQty.equalsIgnoreCase("")) {
                                                    if (Double.parseDouble(orderQty) > 0) {
                                                        incVal++;
                                                    }
                                                }
                                            }
                                            if (incVal > 0) {
                                                schemeBean.setPayoutAmount((Double.parseDouble(schemeBean.getPayoutAmount()) / incVal) + "");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        mStrFreeQty = schemeBean.getPayoutAmount();
                                    } else {
                                        mStrFreeQty = schemeBean.getFreeQty();
                                    }
                                    mDouSlabTypeCal = getSchSlabTypeIDCalculation(schemeBean.getSlabTypeID(),
                                            mStrFreeQty, schemeBean.getToQty(), schemeBean.getSaleUnitID().equalsIgnoreCase(Constants.SchemeSaleUnitIDCBB)?schemeBean.getCBBQty():mStrSumQtyBySKU + "", schemeBean.getSlabRuleID(),
                                            schemeGUID.toUpperCase(), schemeBean.getFromQty(), Constants.X);
                                }


                                for (String mStrCRSSKUGRP : orderMatGrpArray) {
                                    FreeProduct(schemeBean, schemeBean.getSlabRuleID(), mStrCRSSKUGRP, "", mDouSlabTypeCal + "");
                                    ArrayList<SchemeBean> schemeBeanArrayList = hashMapSchemeValByOrderMatGrp.get(mStrCRSSKUGRP);
                                    if (schemeBeanArrayList == null) {
                                        schemeBeanArrayList = new ArrayList<>();
                                        schemeBeanArrayList.add(schemeBean);
                                        hashMapSchemeValByOrderMatGrp.put(mStrCRSSKUGRP, schemeBeanArrayList);
                                    } else {
                                        if (!checkSchemeIsPresentInList(schemeBeanArrayList, schemeBean)) {
                                            schemeBeanArrayList.add(schemeBean);
                                            hashMapSchemeValByOrderMatGrp.put(mStrCRSSKUGRP, schemeBeanArrayList);
                                        }
                                    }
                                    try {
                                        // added 06/09/2017
                                        if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                            if (incVal > 0) {
                                                mapSchemePerORAmtByOrderMatGrp.put(mStrCRSSKUGRP + "_" + schemeGUID, (mDouSlabTypeCal/incVal) + "");
                                            }else{
                                                mapSchemePerORAmtByOrderMatGrp.put(mStrCRSSKUGRP + "_" + schemeGUID, mDouSlabTypeCal + "");
                                            }
                                        }else{
                                            mapSchemePerORAmtByOrderMatGrp.put(mStrCRSSKUGRP + "_" + schemeGUID, mDouSlabTypeCal + "");
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    mapSchemeFreeMatByOrderMatGrp.put(mStrCRSSKUGRP + "_" + schemeGUID, mFreeMat);
                                }
                            }

                            setSchemeList.add(schemeGUID.toUpperCase());
                        }
                    }


                } else {
                    schemeBean = null;
                }
            }
        }

        return schemeBean;
    }

    private boolean checkSchemeIsPresentInList(ArrayList<SchemeBean> schemeBeanArrayList,SchemeBean schemeBeans) {
        for (SchemeBean schemeBean: schemeBeanArrayList){
            if (schemeBean.getSchemeGuid().equalsIgnoreCase(schemeBeans.getSchemeGuid())){
                return true;
            }
        }
        return false;
    }

    private SchemeBean getSecSchemeBeanByMaterial(String schemeGUID, String mStrMatNo, String mStrOrderQty, String mStrHeaderOrItemType) {
        SchemeBean schemeBean = null;
        String mStrMinOrderQty = "0";
        SchemeBean schemeItemBean =new SchemeBean();
//        try {
//            mStrMinOrderQty = OfflineManager.getQtyValueByColumnName(Constants.SchemeItemDetails + "?$filter="
//                    + Constants.SchemeGUID + " eq guid'" + schemeGUID + "' and " + Constants.MaterialNo + " eq '" + mStrMatNo + "'  ", Constants.ItemMin);
////                &$top=1
//        } catch (OfflineODataStoreException e) {
//            e.printStackTrace();
//        }

        try {
            schemeItemBean = OfflineManager.getSchemeItemDetailsBySchemeGuid(Constants.SchemeItemDetails + "?$filter="
                    + Constants.SchemeGUID + " eq guid'" + schemeGUID + "'");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        boolean mBoolMinItemQtyAval = false;
        mStrMinOrderQty = schemeItemBean.getItemMin();
        if (Double.parseDouble(mStrMinOrderQty) >= 0) {
            if (Double.parseDouble(mStrOrderQty.equalsIgnoreCase("") ? "0" : mStrOrderQty) > 0) {
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
            if (!mStrHeaderOrItemType.equalsIgnoreCase(Constants.X)) {
                String[] orderMatArray = new String[1];
                orderMatArray[0] = mStrMatNo;
                try {
                    schemeBean = OfflineManager.getSecondarySchemeSlabPer(Constants.SchemeSlabs + "?$filter="
                                    + Constants.SchemeItemGUID + " eq guid'" + schemeGUID.toUpperCase() + "' ", mStrOrderQty,
                            schemeGUID, Constants.Material, orderMatArray, mStrHeaderOrItemType,schemeItemBean);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            } else {
                //  here write logic  Constants.MAPSCHGuidByMaterial
                boolean schemeISAval = false;
                if (setSchemeList.size() == 0) {
                    schemeISAval = true;
                } else if (!setSchemeList.contains(schemeGUID)) {
                    schemeISAval = true;
                }

                if (schemeISAval) {
                    Multimap<String, String> multiMap = HashMultimap.create();
                    for (Map.Entry<String, ArrayList<String>> entry : Constants.MAPSCHGuidByMaterial.entrySet()) {
                        if (!entry.getValue().isEmpty()) {
                            for (String stValue : entry.getValue()) {
                                multiMap.put(stValue, entry.getKey());
                            }
                        }
                    }
                    String[] materialGrpArray = new String[multiMap.get(schemeGUID).size()];
                    Double mStrSumQtyByMat = 0.0;
                    for (Map.Entry<String, Collection<String>> entry : multiMap.asMap().entrySet()) {
                        if (schemeGUID.equalsIgnoreCase(entry.getKey())) {
                            Collection<String> values = entry.getValue();
                            int index = 0;
                            for (String value : values) {
                                if (mapMaterialWiseQty.containsKey(value)) {
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
                                        + Constants.SchemeItemGUID + " eq guid'" + schemeGUID.toUpperCase() + "' ", mStrSumQtyByMat + "",
                                schemeGUID, Constants.Material, materialGrpArray, mStrHeaderOrItemType,schemeItemBean);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }

                    if (schemeBean != null) {
                        mapHeaderWiseSchemeQty.put(schemeGUID.toUpperCase(), mStrSumQtyByMat + "");
                        mFreeMat = null;
                        int incVal = 0;
                        Double mDouSlabTypeCal = 0.0;
                        if (schemeBean.getTargetBasedID().equalsIgnoreCase("02")) {
                            mDouSlabTypeCal = getSecondaryDiscountOrAmtOrFreeQtyHeaderWise(schemeBean, schemeBean.getSlabRuleID(),
                                    schemeBean.getSaleUnitID().equalsIgnoreCase(Constants.SchemeSaleUnitIDCBB)?schemeBean.getCBBQty():mStrSumQtyByMat + "", "", "", "", Constants.X);
                            // added 06/09/2017
                            if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                try {

                                    for (String matGuid : materialGrpArray) {
                                        if (!TextUtils.isEmpty(matGuid)) {
                                            incVal++;
                                        }
                                    }

                                    if (incVal > 0) {
                                        schemeBean.setPayoutAmount((Double.parseDouble(schemeBean.getPayoutAmount()) / incVal) + "");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            String mStrFreeQty = "";
                            if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
                                mStrFreeQty = schemeBean.getPayoutPerc();
                            } else if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                try {

                                    for (String matGuid : materialGrpArray) {
                                        if (!TextUtils.isEmpty(matGuid)) {
                                            incVal++;
                                        }
                                    }

                                    if (incVal > 0) {
                                        schemeBean.setPayoutAmount((Double.parseDouble(schemeBean.getPayoutAmount()) / incVal) + "");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                mStrFreeQty = schemeBean.getPayoutAmount();
                            } else {
                                mStrFreeQty = schemeBean.getFreeQty();
                            }
                            mDouSlabTypeCal = getSchSlabTypeIDCalculation(schemeBean.getSlabTypeID(),
                                    mStrFreeQty, schemeBean.getToQty(), schemeBean.getSaleUnitID().equalsIgnoreCase(Constants.SchemeSaleUnitIDCBB)?schemeBean.getCBBQty():mStrSumQtyByMat + "", schemeBean.getSlabRuleID(),
                                    schemeGUID.toUpperCase(), schemeBean.getFromQty(), Constants.X);
                        }


                        for (String mStrMaterial : materialGrpArray) {
                            FreeProduct(schemeBean, schemeBean.getSlabRuleID(), mStrMaterial, "", mDouSlabTypeCal + "");

                            ArrayList<SchemeBean> schemeBeanArrayList = hashMapSchemeValByMaterial.get(mStrMaterial);
                            if (schemeBeanArrayList==null){
                                schemeBeanArrayList = new ArrayList<>();
                                schemeBeanArrayList.add(schemeBean);
                                hashMapSchemeValByMaterial.put(mStrMaterial, schemeBeanArrayList);
                            }else {
                                if (!checkSchemeIsPresentInList(schemeBeanArrayList, schemeBean)) {
                                    schemeBeanArrayList.add(schemeBean);
                                    hashMapSchemeValByMaterial.put(mStrMaterial, schemeBeanArrayList);
                                }
                            }
//                            hashMapSchemeValByMaterial.put(mStrMaterial, schemeBean);
                            try {
                                // added in 06/09/2017
                                if (schemeBean.getSlabRuleID().equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                                    if (incVal > 0) {
                                        mapSchemePerORAmtByMaterial.put(mStrMaterial + "_" + schemeGUID, (mDouSlabTypeCal/incVal) + "");
                                    }else{
                                        mapSchemePerORAmtByMaterial.put(mStrMaterial + "_" + schemeGUID, mDouSlabTypeCal + "");
                                    }
                                }else{
                                    mapSchemePerORAmtByMaterial.put(mStrMaterial + "_" + schemeGUID, mDouSlabTypeCal + "");
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mapSchemeFreeMatByMaterial.put(mStrMaterial, mFreeMat);
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

    /**
     * @param SlabTypeID
     * @param freePerOrQty
     * @param slabTOQty
     * @param orderQty
     * @param mStrSchemeItemGuid
     * @return slabTypeCalValue
     */
    private double getSchSlabTypeIDCalculation(String SlabTypeID, String freePerOrQty, String slabTOQty,
                                               String orderQty, String mStrSlabRuleId,
                                               String mStrSchemeItemGuid, String mStrSlabFromQty, String isHeaderBased) {
        Double mDoubSlabCal = 0.0;
        if (SlabTypeID.equalsIgnoreCase("000001")) { //  000001	Running
            Constants.DoubGetRunningSlabPer = 0.0;
            try {
                OfflineManager.getSecondarySchemeSlabPerRunning(Constants.SchemeSlabs + "?$filter="
                                + Constants.SchemeItemGUID + " eq guid'" + mStrSchemeItemGuid.toUpperCase() + "' ",
                        orderQty + "", mStrSchemeItemGuid, "", null, isHeaderBased,"0");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            mDoubSlabCal = Constants.DoubGetRunningSlabPer;
        } else if (SlabTypeID.equalsIgnoreCase("000002")) { //  000002	Fixed
            try {
                mDoubSlabCal = Double.parseDouble(freePerOrQty) * 1;
            } catch (NumberFormatException e) {
                mDoubSlabCal = 0.0;
            }
        } else if (SlabTypeID.equalsIgnoreCase("000003")) { //  000003	Step
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

        } else if (SlabTypeID.equalsIgnoreCase("000004")) { //   000004	Linear
            try {
                mDoubSlabCal = Double.parseDouble(orderQty) / Double.parseDouble(mStrSlabFromQty) * Double.parseDouble(freePerOrQty);
            } catch (NumberFormatException e) {
                mDoubSlabCal = 0.0;
            }
            if (mDoubSlabCal.isInfinite() || mDoubSlabCal.isNaN()) {
                mDoubSlabCal = 0.0;
            }

            // Changed code date 19-05-2017
            if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage) || mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                mDoubSlabCal = Double.valueOf(mDoubSlabCal);
            } else {
                mDoubSlabCal = Double.valueOf(round(mDoubSlabCal));  // Round off only QTY
            }

        }
        return mDoubSlabCal;
    }

    /**
     * @param SlabTypeID
     * @param freePerOrQty
     * @param slabTOValue
     * @param orderQty
     * @param mStrSchemeItemGuid
     * @return slabTypeCalValue
     */
    private double getSchSlabTypeIDCalculationTargetByAmount(String SlabTypeID, String freePerOrQty, String slabTOValue, String orderQty,
                                                             String mStrSlabRuleId, String mStrSchemeItemGuid,
                                                             String mMatNo, String mStrTargetBasedID,
                                                             String mStrSlabFromVal, String isHeaderBased,String mTargetAmount) {
        Double mDoubSlabCal = 0.0;
        if (SlabTypeID.equalsIgnoreCase("000001")) { //  000001	Running
            Constants.DoubGetRunningSlabPer = 0.0;
            String[] orderMatArray = null;
            if (!isHeaderBased.equalsIgnoreCase(Constants.X)) {
                orderMatArray = new String[1];
                orderMatArray[0] = mMatNo;
            } else {
                orderMatArray = orderMatGrp;
            }
            try {
                OfflineManager.getSecondarySchemeSlabPerRunning(Constants.SchemeSlabs + "?$filter="
                                + Constants.SchemeItemGUID + " eq guid'" + mStrSchemeItemGuid.toUpperCase() + "' ", orderQty + "",
                        mStrSchemeItemGuid, mStrTargetBasedID, orderMatArray, isHeaderBased,mTargetAmount);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            mDoubSlabCal = Constants.DoubGetRunningSlabPer;
        } else if (SlabTypeID.equalsIgnoreCase("000002")) { //   000002	Fixed
            try {
                mDoubSlabCal = Double.parseDouble(freePerOrQty) * 1;
            } catch (NumberFormatException e) {
                mDoubSlabCal = 0.0;
            }
        } else if (SlabTypeID.equalsIgnoreCase("000003")) { //   000003	Step
            String mOrderVal = "0";

            String[] orderMatArray = null;
//            orderMatArray[0] = mMatNo;
            if (!isHeaderBased.equalsIgnoreCase(Constants.X)) {
                orderMatArray = new String[1];
                orderMatArray[0] = mMatNo;
//                mOrderVal = OfflineManager.getMatWiseSchemeAmt(orderMatArray);
                mOrderVal = mTargetAmount;
            } else {
                orderMatArray = orderMatGrp;
//                mOrderVal = OfflineManager.getSKUGrpWiseSchemeAmt(orderMatArray);
                mOrderVal = mTargetAmount;
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

        } else if (SlabTypeID.equalsIgnoreCase("000004")) { //   000004	Linear


            String mOrderVal = "0";

            String[] orderMatArray = null;
            if (!isHeaderBased.equalsIgnoreCase(Constants.X)) {
                orderMatArray = new String[1];
                orderMatArray[0] = mMatNo;
//                mOrderVal = OfflineManager.getMatWiseSchemeAmt(orderMatArray);
                mOrderVal = mTargetAmount;
            } else {
                orderMatArray = orderMatGrp;
//                mOrderVal = OfflineManager.getSKUGrpWiseSchemeAmt(orderMatArray);
                mOrderVal = mTargetAmount;
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
            if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage) || mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
                mDoubSlabCal = Double.valueOf(mDoubSlabCal);
            } else {
                mDoubSlabCal = Double.valueOf(round(mDoubSlabCal));  // Round off only QTY
            }
        }
        return mDoubSlabCal;
    }

    // Getting scheme free qty material price
    private static SchemeBean getFreeMatTxt(String mStrFrreQty, String mStrFreeMatTxt,
                                            String mStrFreeMat, String mCPITemGUID,
                                            String mCRSSKUGrp, String freeQTYUOM, String mStrSlabRuleId,String mStrFreeMatCritria) {
        SchemeBean schemeBean = new SchemeBean();
        schemeBean.setFreeQty(mStrFrreQty);
        schemeBean.setFreeMatTxt(mStrFreeMatTxt);
        schemeBean.setFreeMAt(mStrFreeMat);
        schemeBean.setOrderMaterialGroupID(mCRSSKUGrp);
        schemeBean.setFreeQtyUOM(freeQTYUOM);

        SchemeBean freeMatQtyBean = getMaterialPrice(mStrSlabRuleId, mStrFreeMat, mStrFrreQty,mStrFreeMatCritria);

        schemeBean.setFreeMatPrice(freeMatQtyBean.getFreeMatPrice());
        schemeBean.setFreeMatTax(freeMatQtyBean.getFreeMatTax());
        try {
            if (!mCPITemGUID.equalsIgnoreCase("")) {
                schemeBean.setBatch(getValueByColumnName(Constants.CPStockItemSnos + "?$filter="
                        + Constants.CPStockItemGUID + " eq guid'" + mCPITemGUID.toUpperCase() + "' and " + Constants.MaterialNo + " eq '" + mStrFreeMat + "' and "
                        + Constants.ManufacturingDate + " ne null and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "' &$orderby=" + Constants.ManufacturingDate + "%20asc  ", Constants.Batch));
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return schemeBean;
    }

    private static SchemeBean getMaterialPrice(String mStrSlabRuleID, String mStrMaterial, String mStrFreeQty,String mStrFreeMatCritria) {
        SchemeBean freeMatBean = new SchemeBean();
        ODataEntity entity = null;
        String mStrMatNo = "";
        String mStrMatUnitPrice = "0";
        Double mDouCalMatPrice = 0.0;

        if (mStrSlabRuleID.equalsIgnoreCase(Constants.SchemeFreeProduct)) {
            // material
            try {
                entity = OfflineManager.getDecimalValueByColumnName(Constants.CPStockItemSnos + "?$filter="
                        + Constants.MaterialNo + " eq '" + mStrMaterial + "' " +
                        " and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "' &$orderby=" + Constants.ManufacturingDate + "%20asc  ", Constants.IntermUnitPrice);

            } catch (OfflineODataStoreException e) {
                entity = null;
            }

        } else if (mStrSlabRuleID.equalsIgnoreCase(Constants.SchemeFreeSKUGroup)) {
            // sku group id

            ArrayList<String> alFreeMatList = OfflineManager.getFreeMaterialsFromSchFreeMatGrp(Constants.SchemeFreeMatGrpMaterials + "?$select=" + Constants.MaterialNo + " &$filter = "
                    + Constants.SchFreeMatGrpGUID + " eq guid'" + Constants.convertStrGUID32to36(mStrMaterial) + "' and " + Constants.StatusID + " eq '" + Constants.str_01 + "' &$orderby = ItemNo asc ", Constants.MaterialNo);

            if (mStrFreeMatCritria.equalsIgnoreCase(Constants.SchemeFreeProdSeq)) {

                if (alFreeMatList != null && alFreeMatList.size() > 0) {
                    for (String mStrFreeMatNo : alFreeMatList) {
                        try {
                            entity = OfflineManager.getDecimalValueByColumnName(Constants.CPStockItemSnos + "?$filter="
                                    + Constants.MaterialNo + " eq '" + mStrFreeMatNo + "' " +
                                    " and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "' &$orderby=" + Constants.ManufacturingDate + "%20asc  ", Constants.IntermUnitPrice);

                            if (entity != null) {
                                break;
                            }

                        } catch (OfflineODataStoreException e) {
                            entity = null;
                        }
                    }
                }


            } else if (mStrFreeMatCritria.equalsIgnoreCase(Constants.SchemeFreeProdLowMRP)) {
                if (alFreeMatList != null && alFreeMatList.size() > 0) {
                    String mStrMatListQry = Constants.makeCPQry(alFreeMatList, Constants.MaterialNo);
                    try {
                        entity = OfflineManager.getFreeProdLowestMrp(Constants.CPStockItemSnos + "?$filter= (" + mStrMatListQry + ") and "
                                + Constants.StockTypeID + " ne '" + Constants.str_3 + "' &$orderby=" + Constants.ManufacturingDate + "%20asc  ", Constants.IntermUnitPrice);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                entity = null;
            }

        } else if (mStrSlabRuleID.equalsIgnoreCase(Constants.SchemeFreeCRSSKUGroup)) {
            // order mat grp

            try {
                mStrMatNo = getValueByColumnName(Constants.SchemeFreeMatGrpMaterials + "?$select=" + Constants.MaterialNo + " &$filter = "
                        + Constants.MaterialGrp + " eq '" + mStrMaterial + "' &$orderby = ItemNo asc &$top=1", Constants.MaterialNo);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            try {
                entity = OfflineManager.getDecimalValueByColumnName(Constants.CPStockItemSnos + "?$filter="
                        + Constants.MaterialNo + " eq '" + mStrMatNo + "' " +
                        " and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "' &$orderby=" + Constants.ManufacturingDate + "%20asc  ", Constants.IntermUnitPrice);

            } catch (OfflineODataStoreException e) {
                entity = null;
            }
        } else {
            // free article and free free scratch is pending

            mStrMatUnitPrice = "0";
            entity = null;
        }
        String mStrTaxAmt = "0";
        if (entity != null) {
            ODataProperty property;
            ODataPropMap properties;

            properties = entity.getProperties();
            property = properties.get(Constants.IntermUnitPrice);

            try {
                BigDecimal mBigDecVal = (BigDecimal) property.getValue();
                mStrMatUnitPrice = mBigDecVal.doubleValue() + "";
            } catch (Exception e) {
                mStrMatUnitPrice = "0";
            }

            try {
                mDouCalMatPrice = Double.parseDouble(mStrMatUnitPrice) * Double.parseDouble(mStrFreeQty);
            } catch (NumberFormatException e) {
                mDouCalMatPrice = 0.0;
            }
            if (mDouCalMatPrice.isNaN() || mDouCalMatPrice.isInfinite()) {
                mDouCalMatPrice = 0.0;
            }

            mStrTaxAmt = getTaxAmount(mDouCalMatPrice + "", "0", entity, mStrFreeQty);
        }


        freeMatBean.setFreeMatPrice(mDouCalMatPrice + "");

        freeMatBean.setFreeMatTax(mStrTaxAmt + "");

        return freeMatBean;
    }

    public static int round(double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return d < 0 ? -i : i;
        } else {
            return d < 0 ? -(i + 1) : i + 1;
        }
    }

    public static String getTaxAmount(String mStrAfterPriDisAmount, String mStrSecDisAmt, ODataEntity oDataEntity, String mStrOrderQty) {
        String mStrAfterSecAmt = (Double.parseDouble(mStrAfterPriDisAmount) - Double.parseDouble(mStrSecDisAmt)) + "";
        Double mStrNetAmtPerQty = Double.parseDouble(mStrAfterSecAmt) / Double.parseDouble(mStrOrderQty);
        String mStrTaxAmt = "0";
        try {
            mStrTaxAmt = OfflineManager.getPriceOnFieldByMatBatchAfterPrimarySecDiscount(oDataEntity, mStrNetAmtPerQty + "", mStrOrderQty);
        } catch (OfflineODataStoreException e) {
            mStrTaxAmt = "0";
        }

        return mStrTaxAmt;
    }

    private double getSecondaryDiscountOrAmtOrFreeQtyHeaderWise(SchemeBean schPerCalBean, String mStrSlabRuleId,
                                                                String mOrderQty, String mMatNo, String mOrderMatGrp, String mCPItemGUID, String isHeaderBased) {
        double mDoubSecDisOrAmtOrQty = 0.0;
        if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeProduct)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }

            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getMaterialDesc(), schPerCalBean.getMaterialNo(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeSKUGroup)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getSKUGroupDesc(), schPerCalBean.getSKUGroupID(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeCRSSKUGroup)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }

            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getOrderMaterialGroupDesc(), schPerCalBean.getOrderMaterialGroupID(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutPerc(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutPerc(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutAmount(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getPayoutAmount(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeScratchCard)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }

            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getCardTitle(), schPerCalBean.getCardTitle(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeFreeArticle)) {
            if (schPerCalBean.getTargetBasedID().equalsIgnoreCase("02")) {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculationTargetByAmount(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(),
                        mMatNo, schPerCalBean.getTargetBasedID(), schPerCalBean.getFromQty(), isHeaderBased,schPerCalBean.getTargetAmount());
            } else {
                mDoubSecDisOrAmtOrQty = getSchSlabTypeIDCalculation(schPerCalBean.getSlabTypeID(),
                        schPerCalBean.getFreeQty(), schPerCalBean.getToQty(), mOrderQty, mStrSlabRuleId, schPerCalBean.getSchemeGuid(), schPerCalBean.getFromQty(), isHeaderBased);
            }
            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getFreeArticle(), schPerCalBean.getFreeArticle(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        }

        return mDoubSecDisOrAmtOrQty;
    }

    private void FreeProduct(SchemeBean schPerCalBean, String mStrSlabRuleId, String mOrderMatGrp, String mCPItemGUID, String mDoubSecDisOrAmtOrQty) {

        if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeProduct)) {
            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getMaterialDesc(), schPerCalBean.getMaterialNo(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeSKUGroup)) {

            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getSKUGroupDesc(), schPerCalBean.getSKUGroupID(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeCRSSKUGroup)) {
            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getOrderMaterialGroupDesc(),
                    schPerCalBean.getOrderMaterialGroupID(), mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountPercentage)) {

        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeDiscountAmount)) {

        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeScratchCard)) {

            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getCardTitle(), schPerCalBean.getCardTitle(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        } else if (mStrSlabRuleId.equalsIgnoreCase(Constants.SchemeFreeFreeArticle)) {

            mFreeMat = getFreeMatTxt(mDoubSecDisOrAmtOrQty + "", schPerCalBean.getFreeArticle(), schPerCalBean.getFreeArticle(),
                    mCPItemGUID, mOrderMatGrp, schPerCalBean.getFreeQtyUOM(), mStrSlabRuleId,schPerCalBean.getFreeMatCritria());
        }

    }

    private double mDouCalNetAmt = 0.0;

    private boolean getBasketSchemePer(SchemeBean schemeBean, MaterialBatchBean materialBatchBean, SKUGroupBean skuGroupBean) {
        boolean mBoolSecDis = false;

        if (schemeBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfBanner)) { //Banner
            if (!mapBannerWiseQty.isEmpty()) {
                int mIntBannerReminder = (int) (mapBannerWiseTempQty.get(skuGroupBean.getBanner()) % mapBasketBannerMinQty.get(skuGroupBean.getBanner()));
                if (mapBannerWiseQty.containsKey(skuGroupBean.getBanner())) {
                    Double mDouTotalSelBannerQty = mapBannerWiseQty.get(skuGroupBean.getBanner());
                    if (mIntBannerReminder > 0) {
                               /* Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                                Double mDouRemaingQty = mDouTotalSelBannerQty - mDouMatBatQty;
                                if(mDouRemaingQty==0){
                                    mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntBannerReminder ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                    mBoolSecDis =true;
                                    return mBoolSecDis;
                                }*/

                        if (mDouTotalSelBannerQty > 0) {
                            Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                            Double mDouRemaingQty = mDouTotalSelBannerQty - mDouMatBatQty;
                            // testing 04052017
                            mapBannerWiseQty.put(skuGroupBean.getBanner(), mDouRemaingQty);
                            // ending

                            if (mDouRemaingQty == 0) {
                                mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntBannerReminder) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            } else if (mDouRemaingQty < 0) {
                                mDouCalNetAmt = (mDouTotalSelBannerQty) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            }
                        } else {
                            mDouCalNetAmt = 0;
                            mBoolSecDis = true;
                            return mBoolSecDis;
                        }
                    }

                }
            }
        } else if (schemeBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfBrand)) {  // Brand
            if (!mapBrandWiseQty.isEmpty()) {
                int mIntBrandReminder = (int) (mapBrandWiseTempQty.get(skuGroupBean.getBrand()) % mapBasketBrandMinQty.get(skuGroupBean.getBrand()));
                if (mapBrandWiseQty.containsKey(skuGroupBean.getBrand())) {
                    Double mDouTotalSelBrandQty = mapBrandWiseQty.get(skuGroupBean.getBrand());
                    if (mIntBrandReminder > 0) {
                                /*Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                                Double mDouRemaingQty = mDouTotalSelBrandQty - mDouMatBatQty;
                                if(mDouRemaingQty==0){
                                    mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntBrandReminder ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                    mBoolSecDis =true;
                                    return mBoolSecDis;
                                }*/
                        if (mDouTotalSelBrandQty > 0) {
                            Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                            Double mDouRemaingQty = mDouTotalSelBrandQty - mDouMatBatQty;
                            // testing 04052017
                            mapBrandWiseQty.put(skuGroupBean.getBrand(), mDouRemaingQty);
                            // ending

                            if (mDouRemaingQty == 0) {
                                mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntBrandReminder) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            } else if (mDouRemaingQty < 0) {
                                mDouCalNetAmt = (mDouTotalSelBrandQty) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            }
                        } else {
                            mDouCalNetAmt = 0;
                            mBoolSecDis = true;
                            return mBoolSecDis;
                        }
                    }

                }
            }
        } else if (schemeBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfProdCat)) {  // ProductCat
        } else if (schemeBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfSchemeMatGrp)) {  // SKUGroup
            if (!mapSKUGrpWiseQty.isEmpty()) {
                int mIntSKUGrpReminder = (int) (mapSKUGrpWiseTempQty.get(skuGroupBean.getSKUGroupID()) % mapBasketSKUGRPMinQty.get(skuGroupBean.getSKUGroupID()));
                if (mapSKUGrpWiseQty.containsKey(skuGroupBean.getSKUGroupID())) {
                    Double mDouTotalSelSKUGRPQty = mapSKUGrpWiseQty.get(skuGroupBean.getSKUGroupID());
                    if (mIntSKUGrpReminder > 0) {
                                /*Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                                Double mDouRemaingQty = mDouTotalSelSKUGRPQty - mDouMatBatQty;
                                if(mDouRemaingQty==0){
                                    mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntSKUGrpReminder ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                    mBoolSecDis =true;
                                    return mBoolSecDis;
                                }*/

                        if (mDouTotalSelSKUGRPQty > 0) {
                            Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                            Double mDouRemaingQty = mDouTotalSelSKUGRPQty - mDouMatBatQty;
                            // testing 04052017
                            mapSKUGrpWiseQty.put(skuGroupBean.getSKUGroupID(), mDouRemaingQty);
                            // ending

                            if (mDouRemaingQty == 0) {
                                mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntSKUGrpReminder) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            } else if (mDouRemaingQty < 0) {
                                mDouCalNetAmt = (mDouTotalSelSKUGRPQty) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            }
                        } else {
                            mDouCalNetAmt = 0;
                            mBoolSecDis = true;
                            return mBoolSecDis;
                        }
                    }

                }
            }
        } else if (schemeBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfOrderMatGrp)) {  // OrderMaterialGroup
            if (!mapCRSSKUGrpWiseQty.isEmpty()) {
                int mIntCRSSKUGrpReminder = (int) (mapCRSSKUGrpWiseTempQty.get(skuGroupBean.getSKUGroup()) % mapBasketCRSSKUGRPMinQty.get(skuGroupBean.getSKUGroup()));
                if (mapCRSSKUGrpWiseQty.containsKey(skuGroupBean.getSKUGroup())) {
                    Double mDouTotalSelCRSSKUGRPQty = mapCRSSKUGrpWiseQty.get(skuGroupBean.getSKUGroup());
                    if (mIntCRSSKUGrpReminder > 0) {
                               /* Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                                Double mDouRemaingQty = mDouTotalSelCRSSKUGRPQty - mDouMatBatQty;
                                if(mDouRemaingQty==0){
                                    mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntCRSSKUGrpReminder ) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                    mBoolSecDis =true;
                                    return mBoolSecDis;
                                }*/

                        if (mDouTotalSelCRSSKUGRPQty > 0) {
                            Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                            Double mDouRemaingQty = mDouTotalSelCRSSKUGRPQty - mDouMatBatQty;
                            // testing 04052017
                            mapCRSSKUGrpWiseQty.put(skuGroupBean.getSKUGroup(), mDouRemaingQty);
                            // ending

                            if (mDouRemaingQty == 0) {
                                mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntCRSSKUGrpReminder) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            } else if (mDouRemaingQty < 0) {
                                mDouCalNetAmt = (mDouTotalSelCRSSKUGRPQty) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            }
                        } else {
                            mDouCalNetAmt = 0;
                            mBoolSecDis = true;
                            return mBoolSecDis;
                        }
                    }

                }
            }
        } else if (schemeBean.getOnSaleOfCatID().equalsIgnoreCase(Constants.OnSaleOfMat)) {  //Material
            if (!mapMaterialWiseQty.isEmpty()) {
                int mIntMaterialReminder = (int) (mapMaterialWiseTempQty.get(skuGroupBean.getMaterialNo()) % mapBasketMaterialMinQty.get(skuGroupBean.getMaterialNo()));
                if (mapMaterialWiseQty.containsKey(skuGroupBean.getMaterialNo())) {
                    Double mDouTotalSelMatQty = mapMaterialWiseQty.get(skuGroupBean.getMaterialNo());
                    if (mIntMaterialReminder > 0) {

                        if (mDouTotalSelMatQty > 0) {
                            Double mDouMatBatQty = Double.parseDouble(materialBatchBean.getQty());
                            Double mDouRemaingQty = mDouTotalSelMatQty - mDouMatBatQty;
                            // testing 04052017
                            mapMaterialWiseQty.put(skuGroupBean.getMaterialNo(), mDouRemaingQty);
                            // ending

                            if (mDouRemaingQty == 0) {
                                mDouCalNetAmt = (Double.parseDouble(materialBatchBean.getQty()) - mIntMaterialReminder) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            } else if (mDouRemaingQty < 0) {
                                mDouCalNetAmt = (mDouTotalSelMatQty) * Double.parseDouble(materialBatchBean.getLandingPrice());
                                mBoolSecDis = true;
                                return mBoolSecDis;
                            }
                        } else {
                            mDouCalNetAmt = 0;
                            mBoolSecDis = true;
                            return mBoolSecDis;
                        }

                    }

                }
            }
        }
        return mBoolSecDis;
    }

}
