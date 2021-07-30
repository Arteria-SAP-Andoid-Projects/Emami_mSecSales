/*
package com.arteriatech.ss.socreate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.common.ActionBarView;
import com.arteriatech.ss.common.Constants;
import com.arteriatech.ss.master.RetailersDetailsActivity;
import com.arteriatech.ss.mbo.MyTargetsBean;
import com.arteriatech.ss.mbo.SKUGroupBean;
import com.arteriatech.ss.mbo.SKUGroupItemBean;
import com.arteriatech.ss.msecsales.R;
import com.arteriatech.ss.scheme.SchemeListActivity;
import com.arteriatech.ss.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

*/
/**
 * Created by e10526 on 12/21/2016.
 *
 *//*


public class SalesOrderCreateActivity extends AppCompatActivity implements KeyboardView.OnKeyboardActionListener, UIListener {
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "", mStrBundleCPGUID32 = "";
    private String mStrBundleRetailerUID = "";
    String mStrComingFrom = "";
    EditText etSoCreateSearch;
    TextView tvBMT, tvTLSD,tv_brand_label,tv_cat_label;
    TextView retName, retId;
    Spinner spnrSKUType = null;
    Spinner spnrCat = null;
    Spinner spnrBrand = null;
    int mIntBalVisitRet = 0;
    private ArrayList<SKUGroupBean> skuGroupBeanAL;
    private ArrayList<SKUGroupBean> alCRSSKUGrpList, CRSSKUGrpList,
            filteredArraylist, alCRSSKUListTemp, alCRSSKUMatList, alSKULevelList,
            alLastPurQtyMatList;

    private ArrayList<SKUGroupBean> alMustSellMatList;

    HorizontalScrollView svHeader = null, svItem = null;

    // Below hard code values
    String[][] skuType = {{"00", "01"}, {"All", "Must Sell"}};
    String[][] brandArrvalues, catArrValues;
    private String mStrSelCatType = "", mStrSelType = "";
    private String mStrSelBrand = "";
    private EditText[] soQtyEdit = null;
    KeyboardView keyboardView;
    Keyboard keyboard;
    private static int lastSelectedEditText = 0;
    private static int lastCursorPosVal =0;
    private String mStrBMT = "", mStrTLSD = "";
    private String mStrInvListQry = "";
    private String mStrInvCurrentMntQry = "";
    private String mStrSSOListQry = "";

    private ArrayList<SKUGroupBean> selectedSOItems;
    private HashMap<String, ArrayList<SKUGroupBean>> hashMapMaterials = new HashMap<>();
    private String[][] mArrayDistributors = null;
    private String mStrLatestMatNo = "";
    private Set<String> mStrCrsSkuCount = new HashSet<>();
    MyTargetsBean salesKpi = null;
    private boolean textNotTypeFromSubItem = false;
    private boolean textTypeFromSubItem = false;
    private boolean textTypeFromMasterText = false;
    TableLayout tlCRSList;
    TableLayout tlSOList;
    static EditText mEditTextSelected = null;
    static EditText mEditTextSelectedSubItems = null;
    private ProgressDialog pdLoadDialog;
    private String mStrCPDMSDIV="";
    private boolean mBoolFirstTime = false;
    private boolean mBoolSubItemSel = false,mBoolBackBtnPressed=false;
    View viewEditText;
    MotionEvent motionEventET;
    HashMap<String,String> mapMustSellMatQty = new HashMap<>();
    boolean mBoolMustSellMatQtyValid=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.lbl_so_create));
        setContentView(R.layout.layout_so_scroll);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }
        initializeUI();
        loadAsyncTask();
    }

    private void getDMSDivision(){
        mStrCPDMSDIV = Constants.getDMSDIV();
    }

    public void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(SalesOrderCreateActivity.this, R.xml.ll_with_out_dot_inc_dec_up_down);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    */
/*initializes UI for screen*//*

    void initializeUI() {
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        Constants.selectedSOItems.clear();
        Constants.HashMapSubMaterials.clear();
        Constants.MAPSCHGuidByMaterial.clear();
        Constants.MAPQPSSCHGuidByMaterial.clear();
        tv_cat_label= (TextView) findViewById(R.id.tv_cat_label);
        tv_brand_label= (TextView) findViewById(R.id.tv_brand_label);
        tvBMT = (TextView) findViewById(R.id.tv_so_create_bmt_amt);
        tvTLSD = (TextView) findViewById(R.id.tv_so_create_tlsd_amt);
        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retId = (TextView) findViewById(R.id.tv_reatiler_id);
        spnrSKUType = (Spinner) findViewById(R.id.spnr_so_create_sku_group_type);
        spnrCat = (Spinner) findViewById(R.id.spnr_so_create_category);
        spnrBrand = (Spinner) findViewById(R.id.spnr_so_create_brand);
        etSoCreateSearch = (EditText) findViewById(R.id.et_so_create_search);

        etSoCreateSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                filteredArraylist = new ArrayList<>();

                if(skuGroupBeanAL!=null && skuGroupBeanAL.size()>0) {
                    for (int i = 0; i < skuGroupBeanAL.size(); i++) {
                        SKUGroupBean item = skuGroupBeanAL.get(i);
                        if (item.getSKUGroupDesc().toLowerCase()
                                .contains(cs.toString().toLowerCase().trim())) {
                            filteredArraylist.add(item);

                        }
                    }
                    alCRSSKUListTemp = filteredArraylist;
                    displaySKUGroups(filteredArraylist);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }


        });
        initializeKeyboardDependencies();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initScroll();
    }

    void initScroll(){
        svHeader = (HorizontalScrollView) findViewById(R.id.sv_header);
        svItem = (HorizontalScrollView) findViewById(R.id.sv_item);

        svHeader.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    svHeader.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                        @Override
                        public void onScrollChange(View view, int scrollX, int scrollY, int i2, int i3) {
                            svItem.scrollTo(scrollX,scrollY);
                        }
                    });
                }
                return false;
            }
        });

        svItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("Tag1",String.valueOf(getWindow().getCurrentFocus()));
                svItem.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        Log.e("Tag2",String.valueOf(getWindow().getCurrentFocus()));
                        int scrollY = svItem.getScrollY(); // For ScrollView
                        int scrollX = svItem.getScrollX(); // For HorizontalScrollView
                        // DO SOMETHING WITH THE SCROLL COORDINATES
                        svHeader.scrollTo(scrollX,scrollY);
                        Log.e("Tag3",String.valueOf(getWindow().getCurrentFocus()));
                    }
                });
                return false;
            }
        });
    }

    private void loadAsyncTask(){
        try {
            new GetSOData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/*AsyncTask to get Retailers List*//*

    private class GetSOData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(SalesOrderCreateActivity.this,R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            loadingSO();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(!mBoolFirstTime) {
                setTempArrayList();
                displaySKUGroups(alCRSSKUGrpList);
            }
        }
    }
    private void setTempArrayList(){
        skuGroupBeanAL = new ArrayList<>();
        if (alCRSSKUGrpList != null && alCRSSKUGrpList.size() > 0) {
            skuGroupBeanAL.addAll(alCRSSKUGrpList);
        }

        alCRSSKUListTemp = skuGroupBeanAL;
    }


    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
    }

    private void loadingSO(){
        selectedSOItems = new ArrayList<>();
        Log.d("Time getDMSDivision",UtilConstants.getSyncHistoryddmmyyyyTime());
        getDMSDivision();
        Log.d("Time getSystemKPI",UtilConstants.getSyncHistoryddmmyyyyTime());
        getSystemKPI(UtilConstants.getCurrentMonth(), UtilConstants.getCurrentYear());
        Log.d("Time getMustSellMaterials",UtilConstants.getSyncHistoryddmmyyyyTime());
        getMustSellMaterials();
        Log.d("Time getSSSOQry",UtilConstants.getSyncHistoryddmmyyyyTime());
        getSSSOQry();
        Log.d("Time getInvQry",UtilConstants.getSyncHistoryddmmyyyyTime());
        getInvQry();
        Log.d("Time monthTarget",UtilConstants.getSyncHistoryddmmyyyyTime());
        monthTarget();
        Log.d("Time getDistributorValues",UtilConstants.getSyncHistoryddmmyyyyTime());
        getDistributorValues();
        Log.d("Time getInvHisQry",UtilConstants.getSyncHistoryddmmyyyyTime());
        getInvHisQry();
        Log.d("Time getBalVisit",UtilConstants.getSyncHistoryddmmyyyyTime());
        getBalVisit();
        Log.d("Time spinnerSKUValues",UtilConstants.getSyncHistoryddmmyyyyTime());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinnerSKUValues();
                getMatCat();
            }
        });
        Log.d("Time getCPStockList",UtilConstants.getSyncHistoryddmmyyyyTime());
        getCPStockList();
        Log.d("Time getting getCRSSKUGroup",UtilConstants.getSyncHistoryddmmyyyyTime());
    }

    private void setValueToUI() {
        if(!mBoolFirstTime){
            mBoolFirstTime = true;
        }
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetailerUID);
        tvBMT.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mStrBMT) + " " + mArrayDistributors[10][0]);
        tvTLSD.setText(UtilConstants.removeDecimalPoints(mStrTLSD));
    }

    private void getMustSellMaterials() {
        try {
            alMustSellMatList = OfflineManager.getMustSellMat(Constants.TargetItems + "?$filter= " + Constants.PartnerGUID + " eq '" + mStrBundleCPGUID32 + "' ", mStrBundleCPGUID32);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    */
/*Gets kpiList for selected month and year*//*

    private void getSystemKPI(String month, String mStrCurrentYear) {
        try {
            salesKpi = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter = " + Constants.Month + " eq '" + month + "' " +
                    "and " + Constants.Year + " eq '" + mStrCurrentYear + "' " +
                    " and " + Constants.Periodicity + " eq '00' and " + Constants.KPICategory + " eq '06' and " + Constants.CalculationBase + " eq '02' ",mStrCPDMSDIV);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

    }

    private void monthTarget() {
        Double mDoubleDayTarget = 0.0, mDoubleDayAchived = 0.0, mDoubleBMT = 0.0;
        String mTodayOrderQty="0",mMonthInvQty="0";
        if (salesKpi != null) {
            ArrayList<MyTargetsBean> alMyTargets = null;
            try {
                alMyTargets = OfflineManager.getMyTargetsByKPI(salesKpi,
                        mStrBundleCPGUID32.toUpperCase());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            Map<String, MyTargetsBean> mapSalesKPIVal = OfflineManager.getALMyTargetList(alMyTargets);

            mDoubleDayTarget = Double.parseDouble(mapSalesKPIVal.get(salesKpi.getKPICode()).getMonthTarget());

            mTodayOrderQty = Constants.getOrderQtyByRetiler(mStrBundleRetailerUID,UtilConstants.getNewDate(),SalesOrderCreateActivity.this,mStrSSOListQry);
            mMonthInvQty = Constants.getInvQtyByInvQry(mStrInvCurrentMntQry);


            try {
                mDoubleBMT = mDoubleDayTarget - (Double.parseDouble(mMonthInvQty)+Double.parseDouble(mTodayOrderQty));
            } catch (Exception e) {
                mDoubleBMT = 0.0;
            }
            mStrBMT = (mDoubleBMT>0?mDoubleBMT:0) + "";
        } else {
            mStrBMT = "0.0";
        }
    }

    private void getInvHisQry() {
        try {
            mStrInvListQry = OfflineManager.makeInvoiceQry(Constants.SSINVOICES + "?$select=" + Constants.InvoiceGUID + " " +
                    "&$filter=" + Constants.SoldToID + " eq '" + mStrBundleRetailerUID + "' " +
                    "and " + Constants.InvoiceDate + " ge datetime'" + UtilConstants.getLastThreeMonthDate() + "' ");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getSSSOQry() {
        try {
            mStrSSOListQry = OfflineManager.makeSSSOQry(Constants.SSSOs + "?$select=" + Constants.SSSOGuid + " " +
                    "&$filter=" + Constants.SoldToId + " eq '" + mStrBundleRetailerUID + "' " +
                    "and " + Constants.OrderDate + " eq datetime'" + UtilConstants.getNewDate() + "' ",Constants.SSSOGuid);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getInvQry() {
        try {
            mStrInvCurrentMntQry = OfflineManager.makeSSSOQry(Constants.SSINVOICES + "?$select=" + Constants.InvoiceGUID + " " +
                    "&$filter=" + Constants.SoldToID + " eq '" + mStrBundleRetailerUID + "' " +
                    "and " + Constants.InvoiceDate + " ge datetime'" + Constants.getFirstDateOfCurrentMonth() + "' and "+Constants.DmsDivision+" eq '"+mStrCPDMSDIV+"' ", Constants.InvoiceGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }



    private void getBrands(final Context mContext) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String mStrBrandqry = "";
                    if (mStrSelCatType.equalsIgnoreCase(Constants.str_00) || mStrSelCatType.equalsIgnoreCase("")) {
                        mStrBrandqry = Constants.Brands+ "?$orderby="+Constants.BrandDesc+" &$filter=startswith("+Constants.DMSDivision+",'"+mStrCPDMSDIV+"')";
                        brandArrvalues = OfflineManager.getBrands(mStrBrandqry);
                    } else {
                        mStrBrandqry = Constants.BrandsCategories + "?$orderby="+Constants.BrandDesc+" &$filter=" + Constants.MaterialCategoryID + " eq '" + mStrSelCatType + "' and startswith("+Constants.DMSDivision+",'"+mStrCPDMSDIV+"') ";
                        brandArrvalues = OfflineManager.getCatgeriesBrandsLink(mStrBrandqry, Constants.BrandID, Constants.BrandDesc);
                    }
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        brandValuesToSpinner();
                    }
                });
            }
        }).start();



    }

    private void getMatCat() {
        try {
            String mStrMatCatQry = "";
            if (mStrSelBrand.equalsIgnoreCase(Constants.str_00) || mStrSelBrand.equalsIgnoreCase("")) {
                mStrMatCatQry = Constants.MaterialCategories+ "?$orderby="+Constants.MaterialCategoryDesc+" &$filter=startswith("+Constants.DMSDivision+",'"+mStrCPDMSDIV+"')  ";
                catArrValues = OfflineManager.getMaterialCategries(mStrMatCatQry);
            } else {
                mStrMatCatQry = Constants.BrandsCategories + "?$orderby="+Constants.MaterialCategoryDesc+" &$filter=" + Constants.BrandID + " eq '" + mStrSelBrand + "' and startswith("+Constants.DMSDivision+",'"+mStrCPDMSDIV+"')  ";
                catArrValues = OfflineManager.getCatgeriesBrandsLink(mStrMatCatQry, Constants.MaterialCategoryID, Constants.MaterialCategoryDesc);
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        categoryValuesToSpinner();
    }

    private void getCPStockList() {
        try {
                alCRSSKUGrpList = OfflineManager.getCRSSKUGroup(Constants.CPStockItems + "?$filter= " + Constants.StockOwner + " eq '01' and "
                                + Constants.OrderMaterialGroupID + " ne '' and "+Constants.StockTypeID+" ne '"+Constants.str_3+"' and "+Constants.DMSDivision+" eq '"+mStrCPDMSDIV+"' ", mStrInvListQry, mIntBalVisitRet, mStrBundleCPGUID32,
                        alMustSellMatList, mArrayDistributors[4][0],mArrayDistributors[5][0],mArrayDistributors[8][0],mStrCPDMSDIV);
            CRSSKUGrpList = new ArrayList<>();
            CRSSKUGrpList.addAll(alCRSSKUGrpList);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getBalVisit() {

        String mStrBalVisitQry = Constants.RouteSchedulePlans + "?$filter = " + Constants.VisitCPGUID + " eq '"
                + mStrBundleCPGUID32.toUpperCase() + "' ";
        try {
            mIntBalVisitRet = OfflineManager.getBalanceRetVisitRoute(mStrBalVisitQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getCRSKSUList() {
        Set<String> mSetOrderMatGrp =new HashSet<>();
        if (CRSSKUGrpList != null && CRSSKUGrpList.size() > 0) {
            alCRSSKUGrpList.clear();
            switch (mStrSelType) {
                case Constants.str_00: {
                    if (!mStrSelCatType.equalsIgnoreCase(Constants.str_00) && !mStrSelBrand.equalsIgnoreCase(Constants.str_00)) {
                        mSetOrderMatGrp = getOrderMatGrpByBrandAndCategory(mStrSelCatType, mStrSelBrand,mStrCPDMSDIV);
                    } else if (!mStrSelCatType.equalsIgnoreCase(Constants.str_00) && mStrSelBrand.equalsIgnoreCase(Constants.str_00)) {
                        mSetOrderMatGrp = getOrderMatGrpByBrandAndCategory(mStrSelCatType, "",mStrCPDMSDIV);
                    } else if (mStrSelCatType.equalsIgnoreCase(Constants.str_00) && !mStrSelBrand.equalsIgnoreCase(Constants.str_00)) {
                        mSetOrderMatGrp = getOrderMatGrpByBrandAndCategory("", mStrSelBrand,mStrCPDMSDIV);
                    } else if (mStrSelCatType.equalsIgnoreCase(Constants.str_00) && mStrSelBrand.equalsIgnoreCase(Constants.str_00)) {
                        mSetOrderMatGrp =new HashSet<>();
                    }

                    for (SKUGroupBean item : CRSSKUGrpList) {
                        if (mSetOrderMatGrp.size()>0) {
                            if (mSetOrderMatGrp.contains(item.getSKUGroup()))
                                alCRSSKUGrpList.add(item);
                        } else {
                            if (mStrSelCatType.equalsIgnoreCase(Constants.str_00) && mStrSelBrand.equalsIgnoreCase(Constants.str_00))
                                alCRSSKUGrpList.add(item);
                        }
                    }
                }
                break;
                case Constants.str_01: {
                    for (SKUGroupBean item : CRSSKUGrpList) {
                        if (!item.getMatTypeVal().equalsIgnoreCase(""))
                            alCRSSKUGrpList.add(item);
                    }
                }
                break;
            }
        }

        skuGroupBeanAL = new ArrayList<>();
        if (alCRSSKUGrpList != null && alCRSSKUGrpList.size() > 0) {
            skuGroupBeanAL.addAll(alCRSSKUGrpList);
        }

        alCRSSKUListTemp = skuGroupBeanAL;
        displaySKUGroups(skuGroupBeanAL);
    }

    private Set<String> getOrderMatGrpByBrandAndCategory(String mStrCatID, String mStrBrandID,String mStrDMSDivision) {
        Set<String> mSetOrderMatGrp =new HashSet<>();
        try {

            if (!mStrCatID.equalsIgnoreCase("") && mStrBrandID.equalsIgnoreCase("")) {
                mSetOrderMatGrp = OfflineManager.getValueByColumnNameCRSSKU(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupID +
                        " &$filter = " + Constants.MaterialCategoryID + " eq '" + mStrCatID + "' and startswith("+Constants.DMSDivision+",'"+mStrCPDMSDIV+"')  ", Constants.OrderMaterialGroupID);
            } else if (mStrCatID.equalsIgnoreCase("") && !mStrBrandID.equalsIgnoreCase("")) {
                mSetOrderMatGrp = OfflineManager.getValueByColumnNameCRSSKU(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupID +
                        " &$filter = " + Constants.BrandID + " eq '" + mStrBrandID + "' and startswith("+Constants.DMSDivision+",'"+mStrCPDMSDIV+"')  ", Constants.OrderMaterialGroupID);
            } else {
                mSetOrderMatGrp = OfflineManager.getValueByColumnNameCRSSKU(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupID +
                        " &$filter = " + Constants.BrandID + " eq '" + mStrBrandID + "' and " + Constants.MaterialCategoryID + " eq '" + mStrCatID + "' and startswith("+Constants.DMSDivision+",'"+mStrCPDMSDIV+"')  ", Constants.OrderMaterialGroupID);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mSetOrderMatGrp;
    }

    void displaySKUGroups(ArrayList<SKUGroupBean> filteredArraylist) {
        tlCRSList = (TableLayout) findViewById(R.id.crs_sku);
        tlSOList = (TableLayout) findViewById(R.id.report_table);
        int incVal =0 ;

        tlCRSList.removeAllViews();
        tlSOList.removeAllViews();
        LinearLayout llSKUGroupItem;
        LinearLayout llCRSKUGroup;
        if (filteredArraylist != null && filteredArraylist.size() > 0) {

            soQtyEdit = new EditText[filteredArraylist.size()];

                for(final SKUGroupBean skuGroupBean:filteredArraylist){
                final int selValue = incVal;
                llSKUGroupItem = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.subitem_so_create_crslineitem, null, false);
                llCRSKUGroup = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.subitem_so_create_skugroup, null, false);

                LinearLayout ll_so_create_line_item = (LinearLayout) llSKUGroupItem.findViewById(R.id.ll_so_create_line_item);
                LinearLayout ll_so_create_sku = (LinearLayout) llCRSKUGroup.findViewById(R.id.ll_so_create_sku);
                TextView tvSKUGroupName = (TextView) llCRSKUGroup.findViewById(R.id.tv_item_so_create_sku_grp);
                ImageView ivSKuGrpScheme = (ImageView) llCRSKUGroup.findViewById(R.id.iv_sku_grp_scheme);
                ImageView ivMatScheme = (ImageView) llCRSKUGroup.findViewById(R.id.iv_mat_scheme);
                TextView tvMRP = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_create_mrp);
                TextView tvDBSTK = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_create_db_stk);
                TextView tvRETSTK = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_create_ret_stk);
                TextView tvSOQ = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_create_soq);
                soQtyEdit[incVal] = (EditText) llSKUGroupItem.findViewById(R.id.et_item_so_create_ord_qty);
                final ImageView iv_expand_icon = (ImageView) llCRSKUGroup.findViewById(R.id.iv_expand_icon);
                final ScrollView sv_exapndble_list = (ScrollView) llSKUGroupItem.findViewById(R.id.sv_exapndble_list);
                final ScrollView exapndble_list = (ScrollView) llCRSKUGroup.findViewById(R.id.exapndble_list);

                if(skuGroupBean.getIsMaterialActive().equalsIgnoreCase(Constants.X)){
                    ivMatScheme.setVisibility(View.VISIBLE);
                }else {
                    ivMatScheme.setVisibility(View.GONE);
                }
                if(skuGroupBean.getIsSchemeActive().equalsIgnoreCase(Constants.X) || skuGroupBean.getSchemeQPSActive().equalsIgnoreCase(Constants.X)){
                    ivSKuGrpScheme.setVisibility(View.VISIBLE);
                }else {
                    ivSKuGrpScheme.setVisibility(View.GONE);
                }
                ivSKuGrpScheme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!skuGroupBean.getSchemeGuid().equalsIgnoreCase("") && !skuGroupBean.getQPSSchemeGuid().equalsIgnoreCase("")){
                            openSchemeActivity(skuGroupBean.getSchemeGuid()+","+skuGroupBean.getQPSSchemeGuid());
                        }else if(!skuGroupBean.getSchemeGuid().equalsIgnoreCase("") && skuGroupBean.getQPSSchemeGuid().equalsIgnoreCase("")){
                            openSchemeActivity(skuGroupBean.getSchemeGuid());
                        }else if(skuGroupBean.getSchemeGuid().equalsIgnoreCase("") && !skuGroupBean.getQPSSchemeGuid().equalsIgnoreCase("")){
                            openSchemeActivity(skuGroupBean.getQPSSchemeGuid());
                        }

                    }
                });
                ivMatScheme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!skuGroupBean.getSchemeGuid().equalsIgnoreCase("") && !skuGroupBean.getQPSSchemeGuid().equalsIgnoreCase("")){
                            openSchemeActivity(skuGroupBean.getSchemeGuid()+","+skuGroupBean.getQPSSchemeGuid());
                        }else if(!skuGroupBean.getSchemeGuid().equalsIgnoreCase("") && skuGroupBean.getQPSSchemeGuid().equalsIgnoreCase("")){
                            openSchemeActivity(skuGroupBean.getSchemeGuid());
                        }else if(skuGroupBean.getSchemeGuid().equalsIgnoreCase("") && !skuGroupBean.getQPSSchemeGuid().equalsIgnoreCase("")){
                            openSchemeActivity(skuGroupBean.getQPSSchemeGuid());
                        }
                    }
                });


                if (skuGroupBean.isQtyEntered()) {
                    soQtyEdit[selValue].setText(skuGroupBean.getORDQty());
                    skuGroupBean.setQtyEntered(true);
                }
                try {
                    Constants.MAPSCHGuidByCrsSkuGrp.put(skuGroupBean.getSKUGroup(),skuGroupBean.getSchemeGuid().toUpperCase());
                } catch (Exception e) {
                    Constants.MAPSCHGuidByCrsSkuGrp.put(skuGroupBean.getSKUGroup(),"");
                }

                soQtyEdit[incVal].setCursorVisible(true);
                soQtyEdit[incVal].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        textNotTypeFromSubItem = hasFocus;
                        textTypeFromSubItem = false;
                        if (hasFocus) {
                            mEditTextSelected = soQtyEdit[selValue];
                            soQtyEdit[selValue].setHint("");
                            lastSelectedEditText = selValue;
                            Constants.showCustomKeyboard(v, keyboardView, SalesOrderCreateActivity.this);
                        } else {
                            soQtyEdit[selValue].setHint(getString(R.string.qty));
                            lastSelectedEditText = selValue;
                            hideCustomKeyboard();
                        }

                    }
                });
                soQtyEdit[incVal].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        soQtyEdit[selValue].setFocusableInTouchMode(true);
                        mBoolSubItemSel =false;
                        mEditTextSelected = soQtyEdit[selValue];
                        textTypeFromSubItem = false;

                        lastSelectedEditText = selValue;
                        viewEditText = v;
                        motionEventET = event;
                        lastCursorPosVal = Constants.getCursorPostion(soQtyEdit[selValue],v,event);
                        v.requestFocus();
                        Constants.showCustomKeyboard(v, keyboardView, SalesOrderCreateActivity.this);
                        Constants.setCursorPostion(soQtyEdit[selValue],v,event);
                        return true;
                    }
                });
                    if (skuGroupBean.getMatTypeVal().equalsIgnoreCase(Constants.str_01)
                            || skuGroupBean.getMatTypeVal().equalsIgnoreCase(Constants.str_02)
                            || skuGroupBean.getMatTypeVal().equalsIgnoreCase(Constants.str_03) ) {
                        mapMustSellMatQty.put(skuGroupBean.getSKUGroup(),"");
                        UtilConstants.editTextDecimalFormatZeroAllow(soQtyEdit[incVal], 13, 3);
                    }else{
                        UtilConstants.editTextDecimalFormat(soQtyEdit[incVal], 13, 3);
                    }

                soQtyEdit[incVal].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        getLastInvoiceNoandMaterialNo(skuGroupBean.getSKUGroup());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {


                        String s1 = s.toString();
                        if (!s1.equalsIgnoreCase(""))
                            soQtyEdit[selValue].setBackgroundResource(R.drawable.edittext);
                        if (selectedSOItems.contains(skuGroupBean)) {
                            if (!s1.equalsIgnoreCase("")) {
                                skuGroupBean.setQtyEntered(true);
                            } else {
                                skuGroupBean.setQtyEntered(false);
                            }
                            skuGroupBean.setORDQty(s1.toString());
                        } else {
                            if (!s1.equalsIgnoreCase("")) {
                                skuGroupBean.setQtyEntered(true);
                            } else {
                                skuGroupBean.setQtyEntered(false);
                            }
                            skuGroupBean.setORDQty(s1.toString());
                            selectedSOItems.add(skuGroupBean);
                        }
                        Constants.MAPORDQtyByCrsSkuGrp.put(skuGroupBean.getSKUGroup(),s1.toString());

                        if(!skuGroupBean.getMatTypeVal().equalsIgnoreCase("")){
                            mapMustSellMatQty.put(skuGroupBean.getSKUGroup(),s1.toString());
                        }

                        if (sv_exapndble_list.getVisibility() == View.GONE && exapndble_list.getVisibility() == View.GONE) {
                            setMatQty(skuGroupBean.getSKUGroup(), s1.toString());
                        }

                        if (!s1.equalsIgnoreCase("")) {
                            getTLSD(s1, skuGroupBean.getSKUGroup());
                        } else {
                            getTLSDRemoveSKU(s1, skuGroupBean.getSKUGroup());
                        }
                        if (textNotTypeFromSubItem && !textTypeFromSubItem && !textTypeFromMasterText) {
                            alCRSSKUMatList = hashMapMaterials.get(skuGroupBean.getSKUGroup());
                            setQtyVal();


                            soSubItems(alCRSSKUMatList, sv_exapndble_list, skuGroupBean.getSKUGroup(),
                                    skuGroupBean.getORDQty(), false, mStrLatestMatNo, soQtyEdit[selValue],
                                    viewEditText,motionEventET,skuGroupBean.getMatTypeVal());
                        } else {
                            if (textNotTypeFromSubItem && !textTypeFromSubItem && textTypeFromMasterText) {
                                textTypeFromMasterText = false;
                            }
                        }
                    }
                });

                // Must sell and focused products are orange color
                if (skuGroupBean.getMatTypeVal().equalsIgnoreCase(Constants.str_01) || skuGroupBean.getMatTypeVal().equalsIgnoreCase(Constants.str_02)) {
                    ll_so_create_line_item.setBackgroundColor(getResources().getColor(R.color.ORANGE));
                    ll_so_create_sku.setBackgroundColor(getResources().getColor(R.color.ORANGE));

                }
                //  new launched products are blue color
                else if (skuGroupBean.getMatTypeVal().equalsIgnoreCase(Constants.str_03)) {
                    ll_so_create_line_item.setBackgroundColor(getResources().getColor(R.color.light_blue_color));
                    ll_so_create_sku.setBackgroundColor(getResources().getColor(R.color.light_blue_color));
                } else {
                    ll_so_create_line_item.setBackgroundColor(getResources().getColor(R.color.WHITE));
                    ll_so_create_sku.setBackgroundColor(getResources().getColor(R.color.WHITE));
                }

                if (skuGroupBean.getUnBilledStatus().equalsIgnoreCase("")) {
                    tvSKUGroupName.setTextColor(getResources().getColor(R.color.RED));
                }


                tvSKUGroupName.setText(skuGroupBean.getSKUGroupDesc());
                    Constants.setFontSizeByMaxText(tvSKUGroupName);
                tvMRP.setText(UtilConstants.removeLeadingZerowithTwoDecimal(skuGroupBean.getMRP()));
                if(!skuGroupBean.getUOM().equalsIgnoreCase("")) {
                    tvDBSTK.setText(skuGroupBean.getDBSTK()+" "+ skuGroupBean.getUOM());
                    tvRETSTK.setText(skuGroupBean.getRETSTK()+" "+ skuGroupBean.getUOM());
                    tvSOQ.setText(skuGroupBean.getSOQ()+" "+ skuGroupBean.getUOM());
                }
                else{
                    tvDBSTK.setText(skuGroupBean.getDBSTK());
                    tvRETSTK.setText(skuGroupBean.getRETSTK());
                    tvSOQ.setText(skuGroupBean.getSOQ());
                }
                soQtyEdit[incVal].setText(skuGroupBean.getORDQty());

                    soQtyEdit[incVal].setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                            if(keyCode == KeyEvent.KEYCODE_DEL) {
                                mBoolBackBtnPressed =true;
                            }else {
                                mBoolBackBtnPressed =false;
                            }
                            return false;
                        }
                    });

                iv_expand_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (sv_exapndble_list.getVisibility() == View.VISIBLE) {
                            sv_exapndble_list.setVisibility(View.GONE);
                            exapndble_list.setVisibility(View.GONE);
                            iv_expand_icon.setImageResource(R.drawable.down);
                        } else {
                            if (!selectedSOItems.contains(skuGroupBean)) {
                                selectedSOItems.add(skuGroupBean);
                            }
                            sv_exapndble_list.setVisibility(View.VISIBLE);
                            exapndble_list.setVisibility(View.VISIBLE);
                            iv_expand_icon.setImageResource(R.drawable.up);
                            boolean isCrsSkuGrpExpanded = false;
                            if (hashMapMaterials.get(skuGroupBean.getSKUGroup()) != null && hashMapMaterials.get(skuGroupBean.getSKUGroup()).size() > 0) {
                                alCRSSKUMatList = hashMapMaterials.get(skuGroupBean.getSKUGroup());
                                getLastInvoiceNoandMaterialNo(skuGroupBean.getSKUGroup());
                                isCrsSkuGrpExpanded = true;
                            } else {
                                getLastInvoiceNoandMaterialNo(skuGroupBean.getSKUGroup());


                                try {
                                    alCRSSKUMatList = OfflineManager.getMaterialsByCRSSKUGroup(Constants.CPStockItems + "?$filter=" + Constants.OrderMaterialGroupID
                                            + " eq '" + skuGroupBean.getSKUGroup() + "' and " + Constants.MaterialNo + " ne '' " +
                                            "and "+Constants.StockTypeID+" ne '"+Constants.str_3+"' and "+Constants.DMSDivision+" eq '"+mStrCPDMSDIV+"' ", mStrLatestMatNo);
                                } catch (OfflineODataStoreException e) {
                                    e.printStackTrace();
                                }
                                isCrsSkuGrpExpanded = false;
                                hashMapMaterials.put(skuGroupBean.getSKUGroup(), alCRSSKUMatList);
                            }
                            soMaterialItems(alCRSSKUMatList, exapndble_list, skuGroupBean.getSKUGroup(),
                                    skuGroupBean.getORDQty(), false, mStrLatestMatNo, soQtyEdit[selValue],skuGroupBean.getSkuGroupItemBean());
                            soSubItems(alCRSSKUMatList, sv_exapndble_list, skuGroupBean.getSKUGroup(),
                                    skuGroupBean.getORDQty(), isCrsSkuGrpExpanded, mStrLatestMatNo,
                                    soQtyEdit[selValue],viewEditText,motionEventET,
                                    skuGroupBean.getMatTypeVal());
                        }


                    }
                });

                tlSOList.addView(llSKUGroupItem);
                tlCRSList.addView(llCRSKUGroup);

                    incVal++;
            }
        } else {
            filteredArraylist =new ArrayList<>();
            tlCRSList.removeAllViews();
            tlSOList.removeAllViews();
            tlSOList = (TableLayout) findViewById(R.id.report_table);

            LinearLayout llEmptyLayout = (LinearLayout) LayoutInflater.from(SalesOrderCreateActivity.this)
                    .inflate(R.layout.ll_so_create_empty_layout, null);

            tlSOList.addView(llEmptyLayout);
        }

        if(incVal==filteredArraylist.size()) {
            try {
                if (pdLoadDialog.isShowing()) {
                    setValueToUI();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openSchemeActivity(String schemeGuid) {
        Intent intent = new Intent(SalesOrderCreateActivity.this, SchemeListActivity.class);
        intent.putExtra(Constants.EXTRA_SCHEME_GUID,schemeGuid);
        startActivity(intent);
    }

    private void soMaterialItems(ArrayList<SKUGroupBean> alCRSSKUMatList, ScrollView exapndble_list,
                                 String skuGroup, String ordQty, boolean b, String mStrLatestMatNo,
                                 EditText editText, ArrayList<SKUGroupItemBean> skuGroupItemBean) {


        exapndble_list.removeAllViews();

        TableLayout tlSOList = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);


        LinearLayout llCRSGroupItem = null;

        if (alCRSSKUMatList != null && alCRSSKUMatList.size() > 0) {
            int i = 0;
            for (final SKUGroupBean skuGroupBean : alCRSSKUMatList) {
                final SKUGroupBean matBean = skuGroupBean;
                llCRSGroupItem = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.material_list_batch_item, null, false);

                final TextView tvSKUGroupName = (TextView) llCRSGroupItem.findViewById(R.id.tv_item_so_create_sku_grp_sub);
                ImageView ivMatItem = (ImageView) llCRSGroupItem.findViewById(R.id.iv_mat_scheme_item);
                View view_line_color = (View) llCRSGroupItem.findViewById(R.id.view_line_color);
                tvSKUGroupName.setText(skuGroupBean.getMaterialDesc() + " - " + skuGroupBean.getMaterialNo());
                if(skuGroupItemBean!=null){
                    if(skuGroupItemBean.isEmpty()){
                        ivMatItem.setVisibility(View.GONE);
                    }
                    for (SKUGroupItemBean skuGroupItemBean1 : skuGroupItemBean){
                        if(skuGroupItemBean1.getMaterialId().equalsIgnoreCase(skuGroupBean.getMaterialNo())){
                            if(skuGroupItemBean1.isImageDisplay()){
                                ivMatItem.setVisibility(View.VISIBLE);
                                break;
                            }else {
                                ivMatItem.setVisibility(View.GONE);
                            }
                        }else {
                            ivMatItem.setVisibility(View.GONE);
                        }
                    }

                }else {
                    ivMatItem.setVisibility(View.GONE);
                }
                ivMatItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mStrMatScheme="";
                        try {
                            mStrMatScheme = Constants.MAPSCHGuidByMaterial.get(matBean.getMaterialNo());
                        } catch (Exception e) {
                            mStrMatScheme = "";
                        }
                        String mStrMatQPSScheme="";
                        try {
                            mStrMatQPSScheme = Constants.MAPQPSSCHGuidByMaterial.get(matBean.getMaterialNo());
                        } catch (Exception e) {
                            mStrMatQPSScheme = "";
                        }
                        if(mStrMatQPSScheme==null){
                            mStrMatQPSScheme ="";
                        }
                        if(mStrMatScheme==null){
                            mStrMatScheme ="";
                        }
                        if(!mStrMatScheme.equalsIgnoreCase("") && !mStrMatQPSScheme.equalsIgnoreCase("")){
                            openSchemeActivity(mStrMatScheme+","+mStrMatQPSScheme);
                        }else if(!mStrMatScheme.equalsIgnoreCase("") && mStrMatQPSScheme.equalsIgnoreCase("")){
                            openSchemeActivity(mStrMatScheme);
                        }else if(mStrMatScheme.equalsIgnoreCase("") && !mStrMatQPSScheme.equalsIgnoreCase("")){
                            openSchemeActivity(mStrMatQPSScheme);
                        }
                    }
                });


                Constants.setFontSizeByMaxText(tvSKUGroupName);

                if (i == alCRSSKUMatList.size() - 1) {
                    view_line_color.setBackgroundResource(R.color.BLACK);
                }

                i++;
                tlSOList.addView(llCRSGroupItem);
            }
        }

        exapndble_list.addView(tlSOList);
        exapndble_list.requestLayout();


    }

    private void setMatQty(String crsSkuGrp, String orderQty) {
        getLastInvoiceNoandMaterialNo(crsSkuGrp);
        if (hashMapMaterials.get(crsSkuGrp) != null && hashMapMaterials.get(crsSkuGrp).size() > 0) {
            alSKULevelList = hashMapMaterials.get(crsSkuGrp);
            setQtySKUVal(orderQty);
        } else {
            try {
                alSKULevelList = OfflineManager.getMaterialsByCRSSKUGroup(Constants.CPStockItems + "?$filter=" + Constants.OrderMaterialGroupID
                        + " eq '" + crsSkuGrp + "' and " + Constants.MaterialNo + " ne ''", mStrLatestMatNo);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            setQtySKUVal(orderQty);

        }
        hashMapMaterials.put(crsSkuGrp, alSKULevelList);
    }

    private void setQtySKUVal(String orderQty) {
        int incVal = 0;
        if (alSKULevelList != null && alSKULevelList.size() > 0) {
            for (SKUGroupBean skuGroupBean : alSKULevelList) {
                if (mStrLatestMatNo.equalsIgnoreCase(skuGroupBean.getMaterialNo())) {
                    skuGroupBean.setORDQty(orderQty);
                } else {
                    skuGroupBean.setORDQty("");
                }
                alSKULevelList.set(incVal, skuGroupBean);
                incVal++;
            }
        }
    }

    private void setQtyVal() {
        int incVal = 0;
        if (alCRSSKUMatList != null && alCRSSKUMatList.size() > 0) {
            for (SKUGroupBean skuGroupBean : alCRSSKUMatList) {
                skuGroupBean.setORDQty("");
                alCRSSKUMatList.set(incVal, skuGroupBean);
                incVal++;
            }
        }
    }


    private void getTLSD(String s, String mStrSkuGrp) {
        try {
            if (!s.toString().equalsIgnoreCase("")) {
                mStrCrsSkuCount.add(mStrSkuGrp);
            }
            tvTLSD.setText(UtilConstants.removeDecimalPoints(mStrCrsSkuCount.size() + ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTLSDRemoveSKU(String s, String mStrSkuGrp) {
        try {
            mStrCrsSkuCount.remove(mStrSkuGrp);
            tvTLSD.setText(UtilConstants.removeDecimalPoints(mStrCrsSkuCount.size() + ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adjustQtyAtCRSSKULevel(String mStrCRSGrp, HashMap<String, ArrayList<SKUGroupBean>> hashMapMaterials,
                                        EditText editText,View v,MotionEvent mv,String mStrMatType) {
        ArrayList<SKUGroupBean> alCRSSKUMat = hashMapMaterials.get(mStrCRSGrp);
        Double mDouSumQty = 0.0, mDouOrdQty = 0.0;
        if (alCRSSKUMat != null && alCRSSKUMat.size() > 0) {
            for (SKUGroupBean skuGroupBean : alCRSSKUMat) {
                try {
                    mDouOrdQty = Double.parseDouble(skuGroupBean.getORDQty());
                } catch (NumberFormatException e) {
                    mDouOrdQty = 0.0;
                }
                if (mDouOrdQty.isInfinite() || mDouOrdQty.isNaN()) {
                    mDouOrdQty = 0.0;
                }
                mDouSumQty = mDouSumQty + mDouOrdQty;
            }
        }
        if (textNotTypeFromSubItem)
            textTypeFromMasterText = true;
        if (mDouSumQty > 0) {
            Constants.MAPORDQtyByCrsSkuGrp.put(mStrCRSGrp,mDouSumQty+"");

            editText.setText(UtilConstants.removeLeadingZeroVal(mDouSumQty.toString()));
            if (editText.isFocused()) {
                try {
                    if(!mBoolBackBtnPressed) {
                        editText.setSelection(lastCursorPosVal + 1);
                        lastCursorPosVal = lastCursorPosVal + 1;
                    }else{
                        editText.setSelection(lastCursorPosVal - 1);
                        lastCursorPosVal = lastCursorPosVal - 1;
                    }
                } catch (Exception e) {
                    try {
                        editText.setSelection(lastCursorPosVal);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } else {
            if(mStrMatType.equalsIgnoreCase("")) {
                Constants.MAPORDQtyByCrsSkuGrp.put(mStrCRSGrp, "");
                editText.setText("");
            }else {
                if(!mBoolBackBtnPressed){


                    Constants.MAPORDQtyByCrsSkuGrp.put(mStrCRSGrp,mDouSumQty+"");
                    editText.setText(UtilConstants.removeLeadingZeroVal(mDouSumQty.toString()));
                    try {
                        if (editText.isFocused()) {
                            editText.setSelection(lastCursorPosVal + 1);
                            lastCursorPosVal =  1;
                        }
                    } catch (Exception e) {
                        editText.setSelection(0);
                    }

                }else{
                    lastCursorPosVal = 0;
                    Constants.MAPORDQtyByCrsSkuGrp.put(mStrCRSGrp, "");
                    editText.setText("");
                }

            }
        }

    }

    private void soSubItems(final ArrayList<SKUGroupBean> alCRSSKUBatchList, ScrollView sv_exapndble_list,
                            final String crsSKUGrp, String orderQty,
                            boolean isCrsSkuGrpExpanded, String mStrLatestMatNo,
                            final EditText editTextForCRSSKU,final View view,
                            final MotionEvent motionEvent,final  String mStrMatType) {

        boolean mBoolLastMatAvalible = false;
        sv_exapndble_list.removeAllViews();

        TableLayout tlSOList = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);

        LinearLayout llSKUGroupItem = null;

        if (alCRSSKUBatchList != null && alCRSSKUBatchList.size() > 0) {
            int i = 0;
            for (final SKUGroupBean skuGroupBean : alCRSSKUBatchList) {
                final int selSubVal = i;
                llSKUGroupItem = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.so_list_batch_item, null, false);
                TextView tvMRP = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_create_mrp_sub);
                TextView tvDBSTK = (TextView) llSKUGroupItem.findViewById(R.id.tv_item_so_create_db_stk_sub);
                View view_line_color = (View) llSKUGroupItem.findViewById(R.id.view_line_color);
                final EditText soMatQtyEdit = (EditText) llSKUGroupItem.findViewById(R.id.et_item_so_create_ord_qty_sub);
                soMatQtyEdit.setCursorVisible(true);
                soMatQtyEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        textTypeFromSubItem = hasFocus;
                        textNotTypeFromSubItem = false;
                        if (hasFocus) {
                            mEditTextSelectedSubItems = soMatQtyEdit;
                            soMatQtyEdit.setHint("");
                            showCustomKeyboard(v);
                        } else {
                            soMatQtyEdit.setHint(getString(R.string.qty));
                            hideCustomKeyboard();
                        }

                    }
                });
                soMatQtyEdit.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        mBoolSubItemSel = true;
                        mEditTextSelectedSubItems = soMatQtyEdit;
                        soMatQtyEdit.setHint("");
                        textNotTypeFromSubItem = false;
                        v.requestFocus();
                        showCustomKeyboard(v);
                        Constants.setCursorPostion(soMatQtyEdit, v, event);
                        return true;
                    }
                });

                    if (mStrMatType.equalsIgnoreCase(Constants.str_01)
                            || mStrMatType.equalsIgnoreCase(Constants.str_02)
                            || mStrMatType.equalsIgnoreCase(Constants.str_03) ) {
                        UtilConstants.editTextDecimalFormatZeroAllow(soMatQtyEdit, 13, 3);
                    }else{
                        UtilConstants.editTextDecimalFormat(soMatQtyEdit, 13, 3);
                    }


                if (isCrsSkuGrpExpanded) {
                    soMatQtyEdit.setText(skuGroupBean.getORDQty());
                } else {
                    if (mStrLatestMatNo.equalsIgnoreCase("") && !mBoolLastMatAvalible) {
                        mBoolLastMatAvalible = true;
                        soMatQtyEdit.setText(orderQty);
                        skuGroupBean.setORDQty(orderQty);
                    } else {
                        if (!mBoolLastMatAvalible && mStrLatestMatNo.equalsIgnoreCase(skuGroupBean.getMaterialNo())) {
                            mBoolLastMatAvalible = true;
                            soMatQtyEdit.setText(orderQty);
                            skuGroupBean.setORDQty(orderQty);
                        }
                    }

                    hashMapMaterials.put(crsSKUGrp, alCRSSKUBatchList);
                    if (selSubVal == 0) {
                        adjustQtyAtCRSSKULevel(skuGroupBean.getSKUGroup(), hashMapMaterials, editTextForCRSSKU,
                                view, motionEvent,mStrMatType);
                    }
                }

                soMatQtyEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        String s1 = s.toString();
                        if (!s1.equalsIgnoreCase("")) {
                            if (!s1.equalsIgnoreCase("")) {
                                skuGroupBean.setQtyEntered(true);
                            } else {
                                skuGroupBean.setQtyEntered(false);
                            }
                            skuGroupBean.setORDQty(s1);
                            getTLSD(s1, skuGroupBean.getSKUGroup());
                        } else {
                            skuGroupBean.setQtyEntered(false);
                            skuGroupBean.setORDQty(s1);
                            getTLSDRemoveSKU(s1, skuGroupBean.getSKUGroup());
                        }
                        hashMapMaterials.put(crsSKUGrp, alCRSSKUBatchList);

                        adjustQtyAtCRSSKULevel(skuGroupBean.getSKUGroup(), hashMapMaterials, editTextForCRSSKU, view, motionEvent,mStrMatType);
                    }
                });
                tvMRP.setText(UtilConstants.removeLeadingZerowithTwoDecimal(skuGroupBean.getMRP()));
                if (!skuGroupBean.getUOM().equalsIgnoreCase(""))
                    tvDBSTK.setText(skuGroupBean.getDBSTK() + " " + skuGroupBean.getUOM());
                else
                    tvDBSTK.setText(skuGroupBean.getDBSTK());

                if (i == alCRSSKUBatchList.size() - 1) {
                    view_line_color.setBackgroundResource(R.color.BLACK);
                }

                i++;
                tlSOList.addView(llSKUGroupItem);
            }
        }

        sv_exapndble_list.addView(tlSOList);
        sv_exapndble_list.requestLayout();

    }

    private void spinnerSKUValues() {
        ArrayAdapter<String> mustSellAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, skuType[1]);
        mustSellAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spnrSKUType.setAdapter(mustSellAdapter);

        spnrSKUType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                mStrSelType = skuType[0][position];

                clearEditTextSearchBox();
                if (mStrSelType.equalsIgnoreCase("00")) {
                    spnrCat.setSelection(0);
                    spnrBrand.setSelection(0);
                    spnrCat.setVisibility(View.VISIBLE);
                    spnrBrand.setVisibility(View.VISIBLE);
                    tv_cat_label.setVisibility(View.VISIBLE);
                    tv_brand_label.setVisibility(View.VISIBLE);

                } else {
//                  hide categrry brand
                    spnrCat.setVisibility(View.INVISIBLE);
                    spnrBrand.setVisibility(View.INVISIBLE);
                    tv_cat_label.setVisibility(View.INVISIBLE);
                    tv_brand_label.setVisibility(View.INVISIBLE);

                }

                if(mBoolFirstTime && !pdLoadDialog.isShowing()) {
                    getCRSKSUList();
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    private void categoryValuesToSpinner() {
        if (catArrValues == null) {
            catArrValues = new String[2][1];
            catArrValues[0][0] = "";
            catArrValues[1][0] = "";
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, catArrValues[1]);
        categoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spnrCat.setAdapter(categoryAdapter);

        spnrCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                try {
                    mStrSelCatType = catArrValues[0][position];
                } catch (Exception e) {
                    mStrSelCatType = "";
                }
                clearEditTextSearchBox();
                getBrands(SalesOrderCreateActivity.this);

            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void brandValuesToSpinner() {
        if (brandArrvalues == null) {
            brandArrvalues = new String[2][1];
            brandArrvalues[0][0] = "";
            brandArrvalues[1][0] = "";
        }
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, brandArrvalues[1]);
        brandAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spnrBrand.setAdapter(brandAdapter);

        spnrBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                try {
                    mStrSelBrand = brandArrvalues[0][position];
                } catch (Exception e) {
                    mStrSelBrand = "";
                }
                clearEditTextSearchBox();

                if(mBoolFirstTime && !pdLoadDialog.isShowing()) {
                    getCRSKSUList();
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void clearEditTextSearchBox() {
        if (etSoCreateSearch != null && etSoCreateSearch.getText().toString().length() > 0)
            etSoCreateSearch.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sec_so, menu);
        menu.removeItem(R.id.menu_save);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_review:
                onReviewPage();
                break;
        }
        return true;
    }

    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    private void getLastInvoiceNoandMaterialNo(String mStrCRSSKUGRP) {
        mStrLatestMatNo = "";
        String itmQry = Constants.CPStockItems + "?$filter= " + Constants.OrderMaterialGroupID + " eq '" + mStrCRSSKUGRP + "' " +
                " and " + Constants.Material_No + " ne '' &$orderby=" + Constants.ManufacturingDate + "%20desc";

        try {
            alLastPurQtyMatList = OfflineManager.getLastInvNoAndMaterialNo(itmQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (alLastPurQtyMatList != null && alLastPurQtyMatList.size() > 0) {
            mStrLatestMatNo = alLastPurQtyMatList.get(0).getLastMaterialNo();
        } else {
            mStrLatestMatNo = "";
        }
    }

    @Override
    public void onBackPressed() {
        if (isCustomKeyboardVisible()) {
            hideCustomKeyboard();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(SalesOrderCreateActivity.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_create_so).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            onNavigateToRetDetilsActivity();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }

                    });
            builder.show();

        }


    }

    private void onNavigateToRetDetilsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(SalesOrderCreateActivity.this, RetailersDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        if (!Constants.OtherRouteNameVal.equalsIgnoreCase("")) {
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }
        startActivity(intentNavPrevScreen);
    }


    public void changeEditTextFocus(int upDownStatus) {

        if (upDownStatus == 1) {
            int ListSize = alCRSSKUListTemp.size() - 1;
            if (lastSelectedEditText != ListSize) {
                if (soQtyEdit[lastSelectedEditText] != null)
                    soQtyEdit[lastSelectedEditText + 1].setFocusableInTouchMode(true);
                    soQtyEdit[lastSelectedEditText + 1].requestFocus();
            }

        } else {
            if (lastSelectedEditText != 0) {
                if (soQtyEdit[lastSelectedEditText - 1] != null)
                    soQtyEdit[lastSelectedEditText - 1].setFocusableInTouchMode(true);
                    soQtyEdit[lastSelectedEditText - 1].requestFocus();
            }

        }

    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        return super.onKeyLongPress(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            //Handle what you want in long press.
            super.onKeyLongPress(keyCode, event);
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onRelease(int primaryCode) {

    }

    public void onKey(int primaryCode, int[] keyCodes) {
        switch (primaryCode) {

            case 81:
                //Plus
                if(!mBoolSubItemSel) {
                    Constants.incrementTextValues(mEditTextSelected, Constants.N);
                }else{
                    Constants.incrementTextValues(mEditTextSelectedSubItems, Constants.N);
                }
                break;
            case 69:
                //Minus
                if(!mBoolSubItemSel) {
                    Constants.decrementEditTextVal(mEditTextSelected, Constants.N);
                }else{
                    Constants.incrementTextValues(mEditTextSelectedSubItems, Constants.N);
                }
                break;
            case 1:
                changeEditTextFocus(0);
                break;
            case 2:
                changeEditTextFocus(1);
                break;
            case 56:
                KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event);

                break;

            default:
                KeyEvent event2 = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event2);
                break;
        }


    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }


    public void hideCustomKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }

    public void showCustomKeyboard(View v) {

        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
        if (v != null) {
            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }



    private void onReviewPage() {
        if (ValidateSOItems() && validateMustSell()) {
            Intent intentSOCreate = new Intent(SalesOrderCreateActivity.this,
                    SalesOrderReviewActivity.class);
            Constants.selectedSOItems = selectedSOItems;
            Constants.HashMapSubMaterials = hashMapMaterials;
            intentSOCreate.putExtra(Constants.CPNo, mStrBundleRetID);
            intentSOCreate.putExtra(Constants.CPUID, mStrBundleRetailerUID);
            intentSOCreate.putExtra(Constants.RetailerName, mStrBundleRetName);
            intentSOCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
            intentSOCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
            intentSOCreate.putExtra(Constants.comingFrom, mStrComingFrom);
            startActivity(intentSOCreate);
        } else {
            if(!mBoolMustSellMatQtyValid){
                UtilConstants.showAlert(getString(R.string.all_must_sell_qty_should_be_entered), SalesOrderCreateActivity.this);
            }else{
                UtilConstants.showAlert(getString(R.string.alert_enter_atlest_one_material), SalesOrderCreateActivity.this);
            }

        }
    }

    @Override
    public void onRequestError(int i, Exception e) {

    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {

    }

    private boolean validateMustSell(){
        mBoolMustSellMatQtyValid = true;
        if(!mapMustSellMatQty.isEmpty()){
            Iterator mapSelctedValues = mapMustSellMatQty.keySet()
                    .iterator();
            while (mapSelctedValues.hasNext()) {
                String Key = (String) mapSelctedValues.next();
                if(mapMustSellMatQty.get(Key).equalsIgnoreCase("")){
                    mBoolMustSellMatQtyValid = false;
                    break;
                }
            }
        }else{
            mBoolMustSellMatQtyValid =true;
        }

        return mBoolMustSellMatQtyValid;
    }
    private boolean ValidateSOItems() {
        boolean mBoolSOQTYNothingEntered = false;
        for(SKUGroupBean skuGroupBean:selectedSOItems){
            if (Double.parseDouble(skuGroupBean.getORDQty().equalsIgnoreCase("")
                    ? "0" : skuGroupBean.getORDQty()) > 0 && skuGroupBean.getMatTypeVal().equalsIgnoreCase("")) {
                mBoolSOQTYNothingEntered = true;
                break;
            }else if(!skuGroupBean.getMatTypeVal().equalsIgnoreCase("") && Double.parseDouble(skuGroupBean.getORDQty().equalsIgnoreCase("")
                    ? "0" : skuGroupBean.getORDQty()) >= 0){
                mBoolSOQTYNothingEntered = true;
                break;
            }
        }

       */
/* for (int i = 0; i < selectedSOItems.size(); i++) {
            if (Double.parseDouble(selectedSOItems.get(i).getORDQty().equalsIgnoreCase("")
                    ? "0" : selectedSOItems.get(i).getORDQty()) > 0 && selectedSOItems.get(i).getMatTypeVal().equalsIgnoreCase("")) {
                mBoolSOQTYNothingEntered = true;
                break;
            }
        }*//*

        return mBoolSOQTYNothingEntered;
    }

    private boolean ValidateSOSubItems() {
        boolean mBoolSOSubItemQTYNothingEntered = false;

        if (!hashMapMaterials.isEmpty()) {
            Iterator mapSelctedValues = hashMapMaterials.keySet()
                    .iterator();
            while (mapSelctedValues.hasNext()) {
                String Key = (String) mapSelctedValues.next();
                ArrayList<SKUGroupBean> skuGroupList = hashMapMaterials.get(Key);
                if (skuGroupList != null && skuGroupList.size() > 0) {
                    for (int incVal = 0; incVal < skuGroupList.size(); incVal++) {
                        if (Double.parseDouble(skuGroupList.get(incVal).getORDQty().equalsIgnoreCase("")
                                ? "0" : skuGroupList.get(incVal).getORDQty()) > 0) {
                            mBoolSOSubItemQTYNothingEntered = true;
                        }
                    }
                }
            }
        } else {
            mBoolSOSubItemQTYNothingEntered = false;
        }


        return mBoolSOSubItemQTYNothingEntered;
    }

}
*/
