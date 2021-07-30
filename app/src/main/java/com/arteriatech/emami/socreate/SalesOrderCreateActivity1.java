package com.arteriatech.emami.socreate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.store.OnlineODataInterface;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.interfaces.OnScrollViewInterface;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.mbo.DmsDivQryBean;
import com.arteriatech.emami.mbo.MustSellBean;
import com.arteriatech.emami.mbo.MyTargetsBean;
import com.arteriatech.emami.mbo.SKUGroupBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.orginal.OriginalCellViewGroup;
import com.arteriatech.emami.orginal.OriginalTableFixHeader;
import com.arteriatech.emami.orginal.TableFixHeaderAdapter;
import com.arteriatech.emami.scroll.BaseTableAdapter;
import com.arteriatech.emami.scroll.TableFixHeaders;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by e10526 on 12/21/2016.
 */

public class SalesOrderCreateActivity1 extends AppCompatActivity implements KeyboardView.OnKeyboardActionListener, UIListener,
        TableFixHeaderAdapter.ClickListener, TableFixHeaderAdapter.TextTypeListener, OnScrollViewInterface, OnlineODataInterface {
    static EditText mEditTextSelected = null;
    String mStrComingFrom = "";
    EditText etSoCreateSearch;
    TextView tvBMT, tvTLSD, tv_brand_label, tv_cat_label;
    TextView retName, retId;
    Spinner spnrSKUType = null;
    Spinner spnrCat = null;
    Spinner spnrBrand = null;
    int mIntBalVisitRet = 0;
    HorizontalScrollView svHeader = null, svItem = null;
    // Below hard code values
    String[][] skuType = {{"01", "00"}, {"Must Sell", "All"}};
    String[][] brandArrvalues, catArrValues;
    KeyboardView keyboardView;
    Keyboard keyboard;
    MyTargetsBean salesKpi = null;
    TableLayout tlCRSList;
    TableLayout tlSOList;
    View viewEditText;
    MotionEvent motionEventET;
    HashMap<String, String> mapMustSellMatQty = new HashMap<>();
    boolean mBoolMustSellMatQtyValid = true;
    ArrayList<HashMap<String, String>> alMapDBStkUOM = new ArrayList<>();
    ArrayList<HashMap<String, String>> alMapMaterialDBStkUOM = new ArrayList<>();
    HashMap<String, MustSellBean> hashMapMustSell = new HashMap<>();
    HashMap<String, String> hashMapDBStk = new HashMap<>();
    HashMap<String, String> hashMapUOM = new HashMap<>();
    HashMap<String, String> hashMapSegmentedMat = new HashMap<>();
    HashMap<String, String> hashMapTargetByCrsskugrp = new HashMap<>();
    HashMap<String, String> hashMapInvQtyByCrsskugrp = new HashMap<>();
    HashMap<String, String> hashMapRetailerStk = new HashMap<>();
    HashMap<String, String> hashMapRetailerStkByMat = new HashMap<>();
    Map<String, String> hashMapMustSellMatAvgContribution = new HashMap<>();
    HashSet<String> mSetCpStockItemGuid = new HashSet<>();
    private int lastSelectedEditTextRow = 0;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "", mStrBundleCPGUID32 = "";
    private String mStrBundleRetailerUID = "";
    private ArrayList<SKUGroupBean> skuGroupBeanAL;
    private ArrayList<SKUGroupBean> alCRSSKUGrpList = new ArrayList<>(), CRSSKUGrpList,
            filteredArraylist, alCRSSKUListTemp, alCRSSKUMatList, alSKULevelList,
            alLastPurQtyMatList;
    private ArrayList<SKUGroupBean> skuGroupBeanArrayListAllData = new ArrayList<>();
    private ArrayList<Integer> expandPossList = new ArrayList<>();
    private ArrayList<SKUGroupBean> alMustSellMatList;
    private String mStrSelCatType = "", mStrSelType = "";
    private String mStrSelBrand = "";
    private EditText[] soQtyEdit = null;
    private String mStrBMT = "", mStrTLSD = "";
    private String mStrInvListQry = "";
    private String mStrInvCurrentMntQry = "";
    private String mStrSSOListQry = "";
    private ArrayList<SKUGroupBean> selectedSOItems;
    private HashMap<String, ArrayList<SKUGroupBean>> hashMapMaterials = new HashMap<>();
    private String[][] mArrayDistributors = null;
    private String mStrLatestMatNo = "";
    private Set<String> mStrCrsSkuCount = new HashSet<>();
    private boolean textNotTypeFromSubItem = false;
    private boolean textTypeFromSubItem = false;
    private boolean textTypeFromMasterText = false;
    private String mStrSchemeQry = "";
    private ProgressDialog pdLoadDialog;
    private String mStrCPDMSDI = "";
    private boolean mBoolFirstTime = false;
    private boolean mBoolSubItemSel = false, mBoolBackBtnPressed = false;
    private String mBackEndUserName = "";
    private boolean isTyping = false;
    private TableFixHeaders tableFixHeaders;
    private BaseTableAdapter tableFixHeadersAdapterFactory;
    private int oldCurPos = -1;
    private String mTargetQry = "";
    private BaseTableAdapter salesOrderAdapter = null;
    boolean isEditTextClicked = false;
    private String stockOwner = "", mStrParentID = "", mStrCPTypeID = "";
    private DmsDivQryBean dmsDivQryBean = new DmsDivQryBean();
    private String[][] mArraySPValues = null;
    private String typeValues = "";
    String skugroupValue = "";
    String mustselltypeset = "";
    private MenuItem reviewMenu = null;
    private ArrayList<SKUGroupBean> mustSellList = new ArrayList<>();

    public static void setCursorPostion(EditText editText, View view, MotionEvent motionEvent, SKUGroupBean item) {
        EditText edText = (EditText) view;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int touchPosition = editText.getOffsetForPosition(x, y);
        if (touchPosition >= 0) {
            editText.setSelection(touchPosition);
            item.setSetCursorPos(touchPosition);

        }
    }

    public static void setCursorPosition(EditText editText, SKUGroupBean item) {
        int position = editText.getText().toString().length();
        editText.setSelection(position);
        item.setSetCursorPos(position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.lbl_so_create));
        setContentView(R.layout.layout_so_scroll1);
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
        skugroupValue = Constants.getTypesetValueForSkugrp(SalesOrderCreateActivity1.this);
     //   System.out.println("skugroupValue " + skugroupValue);

        if (!Constants.restartApp(SalesOrderCreateActivity1.this)) {
         //   System.out.println("Triggred1");
            initializeUI();
            loadAsyncTask();
        }
    }

    private void getDMSDivision() {
        dmsDivQryBean = Constants.getDMSDIV(mStrParentID);
    }

    /**
     * get salesPerson values
     */
    private void getSalesPersonValues() {
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(mStrBundleCPGUID);
        try {
            mStrParentID = mArraySPValues[1][0];
        } catch (Exception e) {
            mStrParentID = "";
            e.printStackTrace();
        }
    }

    public void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(SalesOrderCreateActivity1.this, R.xml.ll_with_out_dot_inc_dec_up_down);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /*initializes UI for screen*/
    void initializeUI() {
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        Constants.selectedSOItems.clear();
        Constants.HashMapSubMaterials.clear();
        Constants.MAPSCHGuidByMaterial.clear();
        Constants.MAPQPSSCHGuidByMaterial.clear();
        initializeKeyboardDependencies();
        tableFixHeaders = (TableFixHeaders) findViewById(R.id.tablefixheaders);
        createTable();

        tv_cat_label = (TextView) findViewById(R.id.tv_cat_label);
        tv_brand_label = (TextView) findViewById(R.id.tv_brand_label);
        tvBMT = (TextView) findViewById(R.id.tv_so_create_bmt_amt);
        tvTLSD = (TextView) findViewById(R.id.tv_so_create_tlsd_amt);
        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retId = (TextView) findViewById(R.id.tv_reatiler_id);
        spnrSKUType = (Spinner) findViewById(R.id.spnr_so_create_sku_group_type);
        spnrCat = (Spinner) findViewById(R.id.spnr_so_create_category);
        spnrBrand = (Spinner) findViewById(R.id.spnr_so_create_brand);
        etSoCreateSearch = (EditText) findViewById(R.id.et_so_create_search);
        if (skugroupValue.equalsIgnoreCase(Constants.SKUGROUP)) {
            etSoCreateSearch.setHint(R.string.lbl_Search_by_skugroup);
        } else {
            etSoCreateSearch.setHint(R.string.lbl_Search_by_crsskugroup);
        }
        etSoCreateSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {
                createFilterFunc(cs + "");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }


        });
        etSoCreateSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideCustomKeyboard();
                return false;
            }
        });


    }

    private void createFilterFunc(String srchText) {
        checkAndCloseAllExpandedItem();
        alCRSSKUGrpList.clear();
        if (TextUtils.isEmpty(srchText)) {
            alCRSSKUGrpList.addAll(skuGroupBeanArrayListAllData);
        } else {
            for (SKUGroupBean skuGroupBean : skuGroupBeanArrayListAllData) {
                if (skuGroupBean.getSKUGroupDesc().toLowerCase().contains(srchText.toLowerCase())) {
                    alCRSSKUGrpList.add(skuGroupBean);
                }
            }
        }
        createTable();
    }

    private void checkAndCloseAllExpandedItem() {
        if (!expandPossList.isEmpty()) {
            Collections.reverse(expandPossList);
            for (int pos : expandPossList) {
                SKUGroupBean lastOpenedBean = alCRSSKUGrpList.get(pos);
                int imageDisplay = openCloseItem(lastOpenedBean, pos);
                if (imageDisplay == 1) {
                    lastOpenedBean.setViewOpened(true);
                } else if (imageDisplay == 2) {
                    lastOpenedBean.setViewOpened(false);
                }
            }
            expandPossList.clear();
        }
    }


    private void createTable() {
        tableFixHeadersAdapterFactory = new OriginalTableFixHeader(SalesOrderCreateActivity1.this, alCRSSKUGrpList, this, this, skugroupValue).getInstance();
        tableFixHeaders.setAdapter(tableFixHeadersAdapterFactory, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (reviewMenu != null)
            reviewMenu.setEnabled(true);
    }

    private void loadAsyncTask() {
        try {
         //   System.out.println("Triggred2");

            new GetSOData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClickItem(Object o, Object o2, int row, int column, BaseTableAdapter baseTableAdapter, ImageView ivExpandIcon) {
        SKUGroupBean skuGroupBean = (SKUGroupBean) o;
        hideCustomKeyboard();
        if (skuGroupBean.isHeader()) {
            int imageDisplay = openCloseItem(skuGroupBean, row);
            if (imageDisplay == 1) {
                ivExpandIcon.setImageResource(R.drawable.up);
                expandPossList.add(row);
            } else if (imageDisplay == 2) {
                ivExpandIcon.setImageResource(R.drawable.down);
                expandPossList.remove((Object) row);
            }
            baseTableAdapter.notifyDataSetChanged();
        }
        Log.d("onClickItem", "onClickItem: row :" + row + " column :" + column);
    }

    private int openCloseItem(SKUGroupBean skuGroupBean, int row) {
        row = alCRSSKUGrpList.indexOf(skuGroupBean);
        if (!skuGroupBean.isViewOpened()) {
            ArrayList<SKUGroupBean> skuGroupBeanArrayList = skuGroupBean.getSkuSubGroupBeanArrayList();

            if (!skuGroupBeanArrayList.isEmpty()) {
                alCRSSKUGrpList.addAll(row + 1, skuGroupBeanArrayList);
                skuGroupBean.getSkuSubGroupBeanArrayList().clear();
                skuGroupBean.setViewOpened(true);
                return 1;
            }

        } else {
            skuGroupBean.setSkuSubGroupBeanArrayList(getSkuSubItemGroup(row));
            skuGroupBean.setViewOpened(false);
            return 2;

        }
        return 0;
    }

    private ArrayList<SKUGroupBean> getSkuSubItemGroup(int startPos) {
        ArrayList<SKUGroupBean> skuGroupBeanArrayList = new ArrayList<>();
        for (int i = startPos + 1; i < alCRSSKUGrpList.size(); i++) {
            SKUGroupBean skuGroupBean = alCRSSKUGrpList.get(i);
            if (!skuGroupBean.isHeader()) {
                skuGroupBeanArrayList.add(skuGroupBean);
            } else {
                break;
            }
        }
        if (!skuGroupBeanArrayList.isEmpty()) {
            alCRSSKUGrpList.removeAll(skuGroupBeanArrayList);
        }
        return skuGroupBeanArrayList;
    }

    private int pFirstRow = 0;

    @Override
    public void onTextChangeItem(final SKUGroupBean item, final int row, final int column, final BaseTableAdapter tableFixHeaderAdapter, final EditText hEditText, final OriginalCellViewGroup viewGroup) {
        isTyping = false;
        salesOrderAdapter = tableFixHeaderAdapter;
        // checking Must sell validation removed 11042018
       /* if(typeValues.equalsIgnoreCase(Constants.X)) {
            if (!item.getMatTypeVal().equalsIgnoreCase(Constants.str_01)
                    || item.getMatTypeVal().equalsIgnoreCase(Constants.str_02)
                    || item.getMatTypeVal().equalsIgnoreCase(Constants.str_03)) {
                UtilConstants.editTextDecimalFormatZeroAllow(hEditText, 13, 3);
            } else {
                UtilConstants.editTextDecimalFormat(hEditText, 13, 3);
            }
        }else{
            UtilConstants.editTextDecimalFormat(hEditText, 13, 3);
        }*/

        if (!item.getMatTypeVal().equalsIgnoreCase("")) {
            // UtilConstants.editTextDecimalFormatZeroAllow(hEditText, 4, 1);
            UtilConstants.editTextDecimalFormatZeroAllow(hEditText, 5, 1);
        } else {
            //UtilConstants.editTextDecimalFormat(hEditText, 4, 1);
            UtilConstants.editTextDecimalFormat(hEditText, 5, 1);
        }

        hEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item.setEtQty(s + "");

                if (isTyping) {
                    setSalesOrderItemData(s + "", item, alCRSSKUGrpList, row);
                    item.setItemTyping(true);
                    tableFixHeaderAdapter.notifyDataSetChanged();
                } else {

                }
                isTyping = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    if (item.isHeader()) {
                        String s1 = s.toString();
                        if (!s1.equalsIgnoreCase("")) {
                            getTLSD(s1.toString(), item.getSKUGroup());
                        } else {
                            getTLSDRemoveSKU(s1, item.getSKUGroup());
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        hEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
              //  Log.d("onFocusChange", "onFocusChange: " + hasFocus + " pos :" + row);
                if (hasFocus) {
                    hEditText.setHint("");
                    mEditTextSelected = hEditText;
                    lastSelectedEditTextRow = row;
                    item.setFocusHeaderText(true);
                    Constants.showCustomKeyboard(v, keyboardView, SalesOrderCreateActivity1.this);
                } else {
                    hEditText.setHint(getString(R.string.qty));
               //     Log.d("onFocusChange", "isFocusHeaderText: " + item.isFocusHeaderText() + " isItemTyping :" + item.isItemTyping() + " pos :" + row);
                    item.setFocusHeaderText(false);
                    if (!item.isFocusHeaderText() && !item.isItemTyping()) {
                        hideCustomKeyboard();
                    }
                }
            }
        });

        hEditText.setText(item.getEtQty());
        if (item.isFocusHeaderText() || item.isItemTyping()) {
            hEditText.setFocusable(true);
            hEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(hEditText, InputMethodManager.SHOW_IMPLICIT);
            int pos = hEditText.getText().length();

            int itemTypePos = item.getSetCursorPos();
            if (itemTypePos >= 0) {
                if (pos > oldCurPos) {
                    item.setSetCursorPos(itemTypePos + 1);
                } else if (pos < oldCurPos) {
                    item.setSetCursorPos(itemTypePos - 1);
                }
                itemTypePos = item.getSetCursorPos();
                oldCurPos = pos;
                if (pos > itemTypePos && itemTypePos > 0) {
                    pos = itemTypePos;
                }
            } else {
                oldCurPos = pos;
            }
            if (pos == 0) {
                item.setSetCursorPos(-1);
            }
            hEditText.setSelection(pos);

            mEditTextSelected = hEditText;
            lastSelectedEditTextRow = row;
            Log.d("onFocusChange", "onTextChangeItem: isShown" + " pos :" + row);
//            hEditText.setHint("");
        } else {
            item.setSetCursorPos(-1);
            Log.d("onFocusChange", "onTextChangeItem: is not Shown" + " pos :" + row);
//            hEditText.setFocusable(false);
//            hEditText.setHint(getString(R.string.qty));
        }

        hEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.requestFocus();
                hEditText.setHint("");
                lastSelectedEditTextRow = row;
                mEditTextSelected = hEditText;
                getFirstClickedPos = tableFixHeaders.getFirstRow();
                Constants.showCustomKeyboard(v, keyboardView, SalesOrderCreateActivity1.this);
                item.setFocusHeaderText(true);
                setCursorPostion(hEditText, v, event, item);
                isEditTextClicked = true;
                return true;
            }
        });

        /*int firstRow = tableFixHeaders.getFirstRow();
        if (pFirstRow!=firstRow){
            hideCustomKeyboard();
            pFirstRow=firstRow;
        }*/
       /* hEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setFocusable(true);
                    v.requestFocus();
                    hEditText.setHint("");
                    mEditTextSelected = hEditText;
//                    lastSelectedEditTextRow=row;
//                    lastSelectedEditTextClmn=column;
//                    onTextChangeviewGroup=viewGroup;
                    Constants.showCustomKeyboard(v, keyboardView, SalesOrderCreateActivity1.this);
                    item.setFocusHeaderText(true);
                    setCursorPosition(hEditText, item);
                } else {
                    hEditText.setHint(getString(R.string.qty));
                    hideCustomKeyboard();
                }
            }
        });*/

        item.setItemTyping(false);


    }

   /* private void setSalesOrderItemData(String s, SKUGroupBean item, ArrayList<SKUGroupBean> skuGroupBeanArrayList, int row) {
        if (item.isHeader()) {
            if (!item.getSkuSubGroupBeanArrayList().isEmpty()) {

                int i = 0;
                for (SKUGroupBean subItem : item.getSkuSubGroupBeanArrayList()) {
                    if (i == 0) {
                        // set header qty
                        if (Constants.Map_Must_Sell_Mat.containsKey(item.getSKUGroup()) *//*&& !item.getMatTypeVal().equalsIgnoreCase("")*//*) {  // commented on 19072017
                            Constants.Map_Must_Sell_Mat.put(item.getSKUGroup(), s + "");
                        }
                        Constants.MAPORDQtyByCrsSkuGrp.put(item.getSKUGroup(), s.toString());
                        subItem.setEtQty(s);
                    } else {
                        subItem.setEtQty("");
                    }
                    i++;
                }
               *//* }else {

                }*//*
            } else if (item.isViewOpened()) {
                ArrayList<SKUGroupBean> skuGroupBeanArrayListFinal = new ArrayList<>();
                boolean isStored = false;
                for (int i = row + 1; i < skuGroupBeanArrayList.size(); i++) {
                    SKUGroupBean skuGroupBean = skuGroupBeanArrayList.get(i);
                    if (!skuGroupBean.isHeader()) {
                        if (!isStored) {
                            // set header qty
                            if (Constants.Map_Must_Sell_Mat.containsKey(skuGroupBean.getSKUGroup()) *//*&& !skuGroupBean.getMatTypeVal().equalsIgnoreCase("")*//*) { // commented on 19072017
                                Constants.Map_Must_Sell_Mat.put(skuGroupBean.getSKUGroup(), s + "");
                            }
                            Constants.MAPORDQtyByCrsSkuGrp.put(skuGroupBean.getSKUGroup(), s.toString());
                            skuGroupBean.setEtQty(s + "");
                            skuGroupBeanArrayListFinal.add(skuGroupBean);
                            isStored = true;
                        } else {
                            // set header qty
                            if (Constants.Map_Must_Sell_Mat.containsKey(skuGroupBean.getSKUGroup()) && !skuGroupBean.getMatTypeVal().equalsIgnoreCase("")) {
                                Constants.Map_Must_Sell_Mat.put(skuGroupBean.getSKUGroup(), "");
                            }
                            Constants.MAPORDQtyByCrsSkuGrp.put(skuGroupBean.getSKUGroup(), "");
                            skuGroupBean.setEtQty("");
                            skuGroupBeanArrayListFinal.add(skuGroupBean);
                        }
                    } else {
                        break;
                    }
                }
                if (!skuGroupBeanArrayListFinal.isEmpty()) {
                    skuGroupBeanArrayList.removeAll(skuGroupBeanArrayListFinal);
                    skuGroupBeanArrayList.addAll(row + 1, skuGroupBeanArrayListFinal);
                }
            }
        } else {
            setHeaderTotalValues(item, skuGroupBeanArrayList, row);
        }
    }*/

    private void setSalesOrderItemData(String s, SKUGroupBean item, ArrayList<SKUGroupBean> skuGroupBeanArrayList, int row) {
        if (item.isHeader()) {
            if (!item.getSkuSubGroupBeanArrayList().isEmpty()) {
                int i = 0;
                for (SKUGroupBean subItem : item.getSkuSubGroupBeanArrayList()) {
                    if (i == 0) {
                        // set header qty
                        if (Constants.Map_Must_Sell_Mat.containsKey(item.getSKUGroup()) /*&& !item.getMatTypeVal().equalsIgnoreCase("")*/) {  // commented on 19072017
                            Constants.Map_Must_Sell_Mat.put(item.getSKUGroup(), s + "");
                        }
                        Constants.MAPORDQtyByCrsSkuGrp.put(item.getSKUGroup(), s.toString());
//                        subItem.setEtQty(s);  // TODO Commented in 13092018 regarding spliting order Qty
                        subItem.setEtQty("");
                    } else {
                        subItem.setEtQty("");
                    }
                    i++;
                }

                // TODO 13092018 Code added regarding spliting order Qty start
                try {
                    if (!item.getSkuSubGroupBeanArrayList().isEmpty()) {
                        if (item.getSkuSubGroupBeanArrayList().size() > 1) {
                            if (!s.equalsIgnoreCase("")) {
                                int lastIndexVal = 0, balanceIndexVal = 0;
                                Double mDobOrderQty = Double.parseDouble(s);
                                Double mDobTempOrderQty = Double.parseDouble(s);
                                Double mDobDBStkQty = 0.0, mDoubleTempQty = 0.0, mDouTempQty = Double.parseDouble(s);
                                boolean mBoolBatchQtyNotEmpty = false;
                                for (SKUGroupBean skuGroupBean : item.getSkuSubGroupBeanArrayList()) {
                                    try {
                                        mDobDBStkQty = Double.parseDouble(skuGroupBean.getDBSTK().equalsIgnoreCase("") ? "0" : skuGroupBean.getDBSTK());
                                    } catch (NumberFormatException e) {
                                        mDobDBStkQty = 0.0;
                                        e.printStackTrace();
                                    }
                                    lastIndexVal++;
                                    if (mDobDBStkQty > 0) {

                                        if (!mBoolBatchQtyNotEmpty) {
                                            mBoolBatchQtyNotEmpty = true;
                                        }
                                        if (mDobTempOrderQty >= mDobDBStkQty && mDoubleTempQty != mDobOrderQty && mDouTempQty > 0) {

                                            mDobTempOrderQty = mDobTempOrderQty - mDobDBStkQty;

                                            mDouTempQty = mDouTempQty - mDobDBStkQty;

                                            mDoubleTempQty = mDoubleTempQty + mDobDBStkQty;

                                            skuGroupBean.setEtQty(UtilConstants.removeLeadingZeroVal(mDobDBStkQty + ""));

                                            balanceIndexVal = lastIndexVal;

                                        } else if (mDobTempOrderQty <= mDobDBStkQty && mDoubleTempQty != mDobOrderQty && mDouTempQty > 0) {

                                            mDouTempQty = mDobTempOrderQty;

                                            mDoubleTempQty = mDoubleTempQty + mDobTempOrderQty;

                                            skuGroupBean.setEtQty(UtilConstants.removeLeadingZeroVal(mDouTempQty + ""));

                                            break;
                                        } else {
                                            break;
                                        }
                                    }
                                }

                                if (mDobTempOrderQty < mDobOrderQty) {
                                    double mdouRemaingQty = 0.00;
                                    try {
                                        mdouRemaingQty = mDobOrderQty - mDoubleTempQty;
                                    } catch (Exception e) {
                                        mdouRemaingQty = 0.00;
                                        e.printStackTrace();
                                    }
                                    if (mdouRemaingQty > 0) {
                                        if (item.getSkuSubGroupBeanArrayList() != null && item.getSkuSubGroupBeanArrayList().size() > 0) {
                                            SKUGroupBean lastBatchItem = item.getSkuSubGroupBeanArrayList().get(balanceIndexVal - 1);
                                            Double mDouLastBatchQty = 0.00;
                                            try {
                                                mDouLastBatchQty = Double.parseDouble(lastBatchItem.getEtQty());
                                            } catch (NumberFormatException e) {
                                                mDouLastBatchQty = 0.00;
                                                e.printStackTrace();
                                            }
                                            Double mDoubSumOfQtyVal = 0.00;
                                            try {
                                                mDoubSumOfQtyVal = mDouLastBatchQty + mDobTempOrderQty;
                                            } catch (Exception e) {
                                                mDoubSumOfQtyVal = 0.00;
                                                e.printStackTrace();
                                            }
                                            lastBatchItem.setEtQty(UtilConstants.removeLeadingZeroVal(mDoubSumOfQtyVal + ""));
                                            item.getSkuSubGroupBeanArrayList().set(balanceIndexVal - 1, lastBatchItem);
                                        }
                                    }
                                } else {
                                    if (!mBoolBatchQtyNotEmpty) {
                                        SKUGroupBean lastBatchItem = item.getSkuSubGroupBeanArrayList().get(0);
                                        lastBatchItem.setEtQty(s + "");
                                        item.getSkuSubGroupBeanArrayList().set(0, lastBatchItem);
                                    }
                                }
                            }
                        } else {
                            item.getSkuSubGroupBeanArrayList().get(0).setEtQty("" + s);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TODO 13092018 Code added End
            } else if (item.isViewOpened()) {
                ArrayList<SKUGroupBean> skuGroupBeanArrayListFinal = new ArrayList<>();
                boolean isStored = false;
                for (int i = row + 1; i < skuGroupBeanArrayList.size(); i++) {
                    SKUGroupBean skuGroupBean = skuGroupBeanArrayList.get(i);
                    if (!skuGroupBean.isHeader()) {
                        if (!isStored) {
                            // set header qty
                            if (Constants.Map_Must_Sell_Mat.containsKey(skuGroupBean.getSKUGroup()) /*&& !skuGroupBean.getMatTypeVal().equalsIgnoreCase("")*/) { // commented on 19072017
                                Constants.Map_Must_Sell_Mat.put(skuGroupBean.getSKUGroup(), s + "");
                            }
                            Constants.MAPORDQtyByCrsSkuGrp.put(skuGroupBean.getSKUGroup(), s.toString());
//                            skuGroupBean.setEtQty(s + "");  // TODO Commented in 13092018 regarding spliting order Qty
                            skuGroupBean.setEtQty("");
                            skuGroupBeanArrayListFinal.add(skuGroupBean);
                            isStored = true;
                        } else {
                            // set header qty
                            if (Constants.Map_Must_Sell_Mat.containsKey(skuGroupBean.getSKUGroup()) && !skuGroupBean.getMatTypeVal().equalsIgnoreCase("")) {
                                Constants.Map_Must_Sell_Mat.put(skuGroupBean.getSKUGroup(), "");
                            }
//                            Constants.MAPORDQtyByCrsSkuGrp.put(skuGroupBean.getSKUGroup(), "");
                            skuGroupBean.setEtQty("");
                            skuGroupBeanArrayListFinal.add(skuGroupBean);
                        }
                    } else {
                        break;
                    }
                }
                // TODO 13092018 Code added regarding spliting order Qty start
                try {
                    if (!skuGroupBeanArrayListFinal.isEmpty()) {
                        if (skuGroupBeanArrayListFinal.size() > 1) {
                            if (!s.equalsIgnoreCase("")) {
                                int lastIndexVal = 0, balanceIndexVal = 0;
                                Double mDobOrderQty = Double.parseDouble(s);
                                Double mDobTempOrderQty = Double.parseDouble(s);
                                Double mDobBatchQty = 0.0, mDoubleTempQty = 0.0, mDouTempQty = Double.parseDouble(s);
                                boolean mBoolBatchQtyNotEmpty = false;
                                for (SKUGroupBean skuGroupBean : skuGroupBeanArrayListFinal) {
                                    try {
                                        mDobBatchQty = Double.parseDouble(skuGroupBean.getDBSTK().equalsIgnoreCase("") ? "0" : skuGroupBean.getDBSTK());
                                    } catch (NumberFormatException e) {
                                        mDobBatchQty = 0.0;
                                        e.printStackTrace();
                                    }
                                    lastIndexVal++;
                                    if (mDobBatchQty > 0) {
                                        if (!mBoolBatchQtyNotEmpty) {
                                            mBoolBatchQtyNotEmpty = true;
                                        }

                                        if (mDobTempOrderQty >= mDobBatchQty && mDoubleTempQty != mDobOrderQty && mDouTempQty > 0) {

                                            mDobTempOrderQty = mDobTempOrderQty - mDobBatchQty;

                                            mDouTempQty = mDouTempQty - mDobBatchQty;

                                            mDoubleTempQty = mDoubleTempQty + mDobBatchQty;

                                            skuGroupBean.setEtQty(UtilConstants.removeLeadingZeroVal(mDobBatchQty + ""));

                                            balanceIndexVal = lastIndexVal;

                                        } else if (mDobTempOrderQty <= mDobBatchQty && mDoubleTempQty != mDobOrderQty && mDouTempQty > 0) {

                                            mDouTempQty = mDobTempOrderQty;

                                            mDoubleTempQty = mDoubleTempQty + mDobTempOrderQty;

                                            skuGroupBean.setEtQty(UtilConstants.removeLeadingZeroVal(mDouTempQty + ""));

                                            break;

                                        } else {

                                            break;
                                        }
                                    }
                                }

                                if (mDobTempOrderQty < mDobOrderQty) {
                                    double mdouRemaingQty = 0;
                                    try {
                                        mdouRemaingQty = mDobOrderQty - mDoubleTempQty;
                                    } catch (Exception e) {
                                        mdouRemaingQty = 0.00;
                                        e.printStackTrace();
                                    }
                                    if (mdouRemaingQty > 0) {
                                        if (skuGroupBeanArrayListFinal != null && skuGroupBeanArrayListFinal.size() > 0) {
                                            SKUGroupBean lastBatchItem = skuGroupBeanArrayListFinal.get(balanceIndexVal - 1);
                                            Double mDouLastBatchQty = 0.00;
                                            try {
                                                mDouLastBatchQty = Double.parseDouble(lastBatchItem.getEtQty());
                                            } catch (NumberFormatException e) {
                                                mDouLastBatchQty = 0.00;
                                                e.printStackTrace();
                                            }
                                            Double mDoubSumOfQtyVal = 0.00;
                                            try {
                                                mDoubSumOfQtyVal = mDouLastBatchQty + mDobTempOrderQty;
                                            } catch (Exception e) {
                                                mDoubSumOfQtyVal = 0.00;
                                                e.printStackTrace();
                                            }
                                            lastBatchItem.setEtQty(UtilConstants.removeLeadingZeroVal(mDoubSumOfQtyVal + ""));
                                            skuGroupBeanArrayListFinal.set(balanceIndexVal - 1, lastBatchItem);
                                        }
                                    }
                                } else {
                                    if (!mBoolBatchQtyNotEmpty) {
                                        SKUGroupBean lastBatchItem = skuGroupBeanArrayListFinal.get(0);
                                        lastBatchItem.setEtQty(s + "");
                                        skuGroupBeanArrayListFinal.set(0, lastBatchItem);
                                    }
                                }
                            }
                        } else {
                            skuGroupBeanArrayListFinal.get(0).setEtQty("" + s);
                        }
                        skuGroupBeanArrayList.removeAll(skuGroupBeanArrayListFinal);
                        skuGroupBeanArrayList.addAll(row + 1, skuGroupBeanArrayListFinal);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TODO 13092018 Code added End
            }
        } else {
            setHeaderTotalValues(item, skuGroupBeanArrayList, row);
        }
    }

    private void setHeaderTotalValues(SKUGroupBean item, ArrayList<SKUGroupBean> skuGroupBeanArrayList, int row) {
        int totalListCount = skuGroupBeanArrayList.size();
        try {
            if (row == totalListCount - 1) {
                double totalValues = 0;
                for (int i = totalListCount - 1; i >= 0; i--) {
                    SKUGroupBean skuGroupBean = skuGroupBeanArrayList.get(i);
                    if (!skuGroupBean.isHeader()) {
                        if (!TextUtils.isEmpty(skuGroupBean.getEtQty()))
                            totalValues = totalValues + Double.parseDouble(skuGroupBean.getEtQty());
                    } else {
                        String totalString = totalValues + "";
                        // set header qty
                        if (Constants.Map_Must_Sell_Mat.containsKey(skuGroupBean.getSKUGroup()) /*&& !skuGroupBean.getMatTypeVal().equalsIgnoreCase("")*/) {   // commented on 19072017
                            Constants.Map_Must_Sell_Mat.put(skuGroupBean.getSKUGroup(), totalString.split("\\.")[0]);
                        }
                        Constants.MAPORDQtyByCrsSkuGrp.put(skuGroupBean.getSKUGroup(), totalString.split("\\.")[0]);
                        skuGroupBean.setEtQty(totalString.split("\\.")[0]);
                        break;
                    }
                }
            } else {
                double totalValues = 0;
                for (int i = row + 1; i < totalListCount; i++) {
                    SKUGroupBean skuGroupBean = skuGroupBeanArrayList.get(i);
                    if (!skuGroupBean.isHeader()) {
                        if (!TextUtils.isEmpty(skuGroupBean.getEtQty()))
                            totalValues = totalValues + Double.parseDouble(skuGroupBean.getEtQty());
                    } else {
                        break;
                    }
                }
                for (int i = row; i >= 0; i--) {
                    SKUGroupBean skuGroupBean = skuGroupBeanArrayList.get(i);
                    if (!skuGroupBean.isHeader()) {
                        if (!TextUtils.isEmpty(skuGroupBean.getEtQty()))
                            totalValues = totalValues + Double.parseDouble(skuGroupBean.getEtQty());
                    } else {
                        String totalString = totalValues + "";
                        // set header qty
                        if (Constants.Map_Must_Sell_Mat.containsKey(skuGroupBean.getSKUGroup()) /*&& !skuGroupBean.getMatTypeVal().equalsIgnoreCase("")*/) {  // commented on 19072017
                            Constants.Map_Must_Sell_Mat.put(skuGroupBean.getSKUGroup(), totalString.split("\\.")[0]);
                        }
                        Constants.MAPORDQtyByCrsSkuGrp.put(skuGroupBean.getSKUGroup(), totalString.split("\\.")[0]);
                        skuGroupBean.setEtQty(totalString.split("\\.")[0]);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTempArrayList() {
        skuGroupBeanAL = new ArrayList<>();
        if (alCRSSKUGrpList != null && alCRSSKUGrpList.size() > 0) {
            skuGroupBeanAL.addAll(alCRSSKUGrpList);
        }

        alCRSSKUListTemp = skuGroupBeanAL;
    }

    private void getSchemeQry() {
        mStrSchemeQry = Constants.getActiveSchemeQry();
    }

    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
        if (mArrayDistributors != null) {
            try {
                stockOwner = mArrayDistributors[5][0];
                mStrCPTypeID = mArrayDistributors[8][0];
            } catch (Exception e) {
                stockOwner = "";
                mStrCPTypeID = "";
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("LongLogTag")
    private void loadingSO() {
        selectedSOItems = new ArrayList<>();

        try {
            String mStrConfigTypeQry = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" + Constants.SSSO + "'";
            if (OfflineManager.getVisitStatusForCustomer(mStrConfigTypeQry)) {
                typeValues = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                        Constants.SSSO + "' and " + Constants.Types + " eq '" + Constants.MSTSELREQ + "' &$top=1", Constants.TypeValue);
            } else {
                typeValues = Constants.X;
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        getTypesetMustsell();


        Log.d("Time getSalesPersonValues", UtilConstants.getSyncHistoryddmmyyyyTime());
        getSalesPersonValues();
        Log.d("Time getDMSDivision", UtilConstants.getSyncHistoryddmmyyyyTime());
        getDMSDivision();
        Log.d("Time getInvHisQry", UtilConstants.getSyncHistoryddmmyyyyTime());   // TODO if must sell is done disable code
        getInvHisQry();  // TODO if must sell is done disable code
        Log.d("Time getSystemKPI", UtilConstants.getSyncHistoryddmmyyyyTime());
        getSystemKPI(UtilConstants.getCurrentMonth(), UtilConstants.getCurrentYear());
        Log.d("Time getMustSellMaterialsByInvQry", UtilConstants.getSyncHistoryddmmyyyyTime());  // TODO if must sell is done disable code
        getMustSellMaterials();  // TODO if must sell is done disable code
        Log.d("Time getSSSOQry", UtilConstants.getSyncHistoryddmmyyyyTime());
        getSSSOQry();
        Log.d("Time getInvQry", UtilConstants.getSyncHistoryddmmyyyyTime());
        getInvQry();
        Log.d("Time getDistributorValues", UtilConstants.getSyncHistoryddmmyyyyTime());
        getDistributorValues();
        Log.d("Time getBalVisit", UtilConstants.getSyncHistoryddmmyyyyTime());
        getBalVisit();
        Log.d("Time getMustSell", UtilConstants.getSyncHistoryddmmyyyyTime()); // TODO if must sell is done enable code
        getMustSell();  // TODO if must sell is done enable code
        Log.d("Time getMustSellTemp", UtilConstants.getSyncHistoryddmmyyyyTime());
      //  getMustSellTemp();
        Log.d("Time getDBSTOCK", UtilConstants.getSyncHistoryddmmyyyyTime());
        getDBStock();
        Log.d("Time getMaterialDBStock", UtilConstants.getSyncHistoryddmmyyyyTime());
        getMaterialDBStock();
        Log.d("Time getSegmentedMat", UtilConstants.getSyncHistoryddmmyyyyTime());
        getSegmentedMat();
        if (hashMapDBStk.size() > 0) {
            Log.d("Time getCrsskuGrpQry", UtilConstants.getSyncHistoryddmmyyyyTime());
            getCrsskuGrpQry();
            Log.d("Time getTargets", UtilConstants.getSyncHistoryddmmyyyyTime());
            getTargets();
            Log.d("Time getInvQtyByCrsSkuGrp", UtilConstants.getSyncHistoryddmmyyyyTime());
            getInvQtyByCrsSkuGrp();
        }
        Log.d("Time monthTarget", UtilConstants.getSyncHistoryddmmyyyyTime());
        monthTarget();
        Log.d("Time getRetailerStock", UtilConstants.getSyncHistoryddmmyyyyTime());
        getRetailerStock();
        Log.d("Time getMatRetailerStock", UtilConstants.getSyncHistoryddmmyyyyTime());
        getMatRetailerStock();
        Log.d("Time spinnerSKUValues", UtilConstants.getSyncHistoryddmmyyyyTime());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
        Log.d("Time getCPStockList", UtilConstants.getSyncHistoryddmmyyyyTime());
        getCPStockList();
        Log.d("Time get getCRSSKUGroup", UtilConstants.getSyncHistoryddmmyyyyTime());


    }


    private void getTypesetMustsell() {

        String query="ConfigTypsetTypeValues?$filter=Typeset eq 'NEWPRD'";
        try {
             mustselltypeset  = OfflineManager.getconfigValuemustsell(query);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }


    }


    private void closeProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setValueToUI() {
        if (!mBoolFirstTime) {
            mBoolFirstTime = true;
        }
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetailerUID);
//        tvBMT.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mStrBMT) + " " + mArrayDistributors[10][0]);
        tvBMT.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mStrBMT));
        tvTLSD.setText(UtilConstants.removeDecimalPoints(mStrTLSD));
    }

    private void getDBStock() {
        try {
            alMapDBStkUOM = OfflineManager.getDBStockList(Constants.CPStockItems
                    + "?$filter=" + Constants.MaterialNo + " ne '' and "
                    + Constants.StockOwner + " eq '" + stockOwner + "' and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "' and " + Constants.CPGUID + " eq '" + mStrParentID + "' and " + dmsDivQryBean.getDMSDivisionQry() + " &$orderby=" + Constants.OrderMaterialGroupID + " ", mSetCpStockItemGuid);

            if (alMapDBStkUOM.size() > 0) {
                hashMapDBStk = alMapDBStkUOM.get(0);
                hashMapUOM = alMapDBStkUOM.get(1);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getMustSell() {
        try {
            hashMapMustSell = OfflineManager.getMustSellMaterialsList(Constants.MustSells);
                   /* + "?$filter=" + Constants.OrderMatGrp + " ne '' and "
                    + Constants.ParentType + " eq '"+stockOwner+"'  and "+Constants.CPNo+" eq '"+mStrBundleRetID+"' " +
                    "and "+Constants.CPType+" eq '"+mStrCPTypeID+"' " +
                    "and "+Constants.ParentNo+" eq '"+mStrParentID+"' and "+Constants.ValidTo+" ge datetime'" + UtilConstants.getNewDate() + "' and "+dmsDivQryBean.getDMSDivisionSSInvQry()+" ");
*/
         //   System.out.println("Tiggred7 " + hashMapMustSell.toString());


//            if (alMapDBStkUOM.size() > 0) {
//                hashMapDBStk = alMapDBStkUOM.get(0);
//                hashMapUOM = alMapDBStkUOM.get(1);
//            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getMustSellTemp() {
        try {
            hashMapMustSell = OfflineManager.getMustSellMatListTemp(Constants.MustSells
                    + "?$filter=" + Constants.OrderMatGrp + " ne '' and "
                    + Constants.ParentType + " eq '" + stockOwner + "'  and " + Constants.CPNo + " eq '" + mStrBundleRetID + "' " +
                    "and " + Constants.CPType + " eq '" + mStrCPTypeID + "' " +
                    "and " + Constants.ParentNo + " eq '" + mStrParentID + "' and " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + dmsDivQryBean.getDMSDivisionSSInvQry() + " ");

//            if (alMapDBStkUOM.size() > 0) {
//                hashMapDBStk = alMapDBStkUOM.get(0);
//                hashMapUOM = alMapDBStkUOM.get(1);
//            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getMaterialDBStock() {
        try {
            alMapMaterialDBStkUOM = OfflineManager.getDBStockListMaterial(Constants.CPStockItems
                    + "?$filter=" + Constants.MaterialNo + " ne '' and "
                    + Constants.StockOwner + " eq '" + stockOwner + "' and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "' and " + Constants.CPGUID + " eq '" + mStrParentID + "' and " + dmsDivQryBean.getDMSDivisionQry() + " &$orderby=" + Constants.MaterialNo + " ");

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getSegmentedMat() {
        try {
            hashMapSegmentedMat = OfflineManager.getSegmentedMaterialsList(Constants.SegmentedMaterials);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getRetailerStock() {
        try {
            hashMapRetailerStk = OfflineManager.getRetStockList(Constants.CPStockItems + "?$filter=" + Constants.CPGUID + " eq '" + mStrBundleCPGUID32 + "'" +
                    " and " + Constants.StockOwner + " eq '02' &$orderby=" + Constants.OrderMaterialGroupID + " ");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getMatRetailerStock() {
        try {
            hashMapRetailerStkByMat = OfflineManager.getMaterialRetStockList(Constants.CPStockItems + "?$filter=" + Constants.CPGUID + " eq '" + mStrBundleCPGUID32 + "'" +
                    " and " + Constants.StockOwner + " eq '02' &$orderby=" + Constants.MaterialNo + " ");
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }


    private void getCrsskuGrpQry() {
        mTargetQry = Constants.convertHashMapToString(hashMapDBStk, Constants.OrderMaterialGroupID);
    }

    private void getTargets() {
        try {
            hashMapTargetByCrsskugrp = OfflineManager.getTargetByOrderMatGrp(hashMapDBStk, mStrBundleCPGUID32, mTargetQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getInvQtyByCrsSkuGrp() {
        try {
            hashMapInvQtyByCrsskugrp = OfflineManager.getSSInvItmQtyByOrderMatGrp(hashMapDBStk, mStrBundleCPGUID32, mTargetQry.replaceAll(Constants.OrderMaterialGroupID, Constants.OrderMaterialGroup));
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }


    private void getMustSellMaterials() {
        if (!mStrInvListQry.equalsIgnoreCase("")) {
            try {
                hashMapMustSellMatAvgContribution = OfflineManager.getMustSellMatFromInvItms(Constants.SSInvoiceItemDetails + "?$filter= " + Constants.OrderMaterialGroup + " ne '' and " + mStrInvListQry + " &$orderby=" + Constants.OrderMaterialGroup + " ");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
    }

    /*Gets kpiList for selected month and year*/
    private void getSystemKPI(String month, String mStrCurrentYear) {
        try {
           /* salesKpi = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter = " + Constants.Month + " eq '" + month + "' " +
                    "and " + Constants.Year + " eq '" + mStrCurrentYear + "' " +
                    " and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '06' and " + Constants.CalculationBase + " eq '02' ", mStrCPDMSDIV);*/

            salesKpi = OfflineManager.getSpecificKpi(Constants.KPISet + "?$filter = " + Constants.ValidTo + " ge datetime'" + UtilConstants.getNewDate() + "' and " + Constants.Periodicity + " eq '02' and " + Constants.KPICategory + " eq '06' and " + Constants.CalculationBase + " eq '02' ", dmsDivQryBean.getCVGValueQry());

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

    }

    private void monthTarget() {
        Double mDoubleDayTarget = 0.0, mDoubleBMT = 0.0;
        String mTodayOrderQty = "0", mMonthInvQty = "0";
        if (salesKpi != null) {
            try {
                ArrayList<MyTargetsBean> alMyTargets = null;
                try {
                    alMyTargets = OfflineManager.getMyTargetsByKPI(salesKpi,
                            mStrBundleCPGUID32.toUpperCase());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                Map<String, MyTargetsBean> mapSalesKPIVal = OfflineManager.getALMyTargetList(alMyTargets);

                mDoubleDayTarget = Double.parseDouble(mapSalesKPIVal.get(salesKpi.getKPICode()).getMonthTarget());

                if (mDoubleDayTarget > 0) {

                    mTodayOrderQty = Constants.getOrderQtyByRetiler(mStrBundleRetailerUID, UtilConstants.getNewDate(), SalesOrderCreateActivity1.this, mStrSSOListQry);

                    mMonthInvQty = Constants.getInvQtyByInvQry(mStrInvCurrentMntQry);

                    try {
                        mDoubleBMT = mDoubleDayTarget - Double.parseDouble(mMonthInvQty) - Double.parseDouble(mTodayOrderQty);
                    } catch (Exception e) {
                        mDoubleBMT = 0.0;
                    }
                } else {
                    mDoubleBMT = 0.0;
                }
                mStrBMT = (mDoubleBMT > 0 ? mDoubleBMT : 0) + "";
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
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
                    "and " + Constants.OrderDate + " eq datetime'" + UtilConstants.getNewDate() + "' ", Constants.SSSOGuid);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void getInvQry() {
        try {
            mStrInvCurrentMntQry = OfflineManager.makeSSSOQry(Constants.SSINVOICES + "?$select=" + Constants.InvoiceGUID + " " +
                    "&$filter=" + Constants.SoldToID + " eq '" + mStrBundleRetailerUID + "' " +
                    "and " + Constants.InvoiceDate + " ge datetime'" + Constants.getFirstDateOfCurrentMonth() + "' and " + dmsDivQryBean.getDMSDivisionSSInvQry() + " ", Constants.InvoiceGUID);
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
                        mStrBrandqry = Constants.Brands + "?$orderby=" + Constants.BrandDesc + " &$filter=" + dmsDivQryBean.getDMSDivisionQry() + "";

                    //    System.out.println("Brand qry" + mStrBrandqry);

                        brandArrvalues = OfflineManager.getBrands(mStrBrandqry);
                    } else {
                        mStrBrandqry = Constants.BrandsCategories + "?$orderby=" + Constants.BrandDesc + " &$filter=" + Constants.MaterialCategoryID + " eq '" + mStrSelCatType + "' and " + dmsDivQryBean.getDMSDivisionQry() + " ";
                        brandArrvalues = OfflineManager.getCatgeriesBrandsLink(mStrBrandqry, Constants.BrandID, Constants.BrandDesc);

                     //   System.out.println("BrandsCategories qry" + mStrBrandqry);


                    }
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
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
                mStrMatCatQry = Constants.MaterialCategories + "?$orderby=" + Constants.MaterialCategoryDesc + " &$filter=" + dmsDivQryBean.getDMSDivisionQry() + " ";
                catArrValues = OfflineManager.getMaterialCategries(mStrMatCatQry);
              //  System.out.println("MaterialCategories qry" + mStrMatCatQry);

            } else {
                mStrMatCatQry = Constants.BrandsCategories + "?$orderby=" + Constants.MaterialCategoryDesc + " &$filter=" + Constants.BrandID + " eq '" + mStrSelBrand + "' and " + dmsDivQryBean.getDMSDivisionQry() + " ";
                catArrValues = OfflineManager.getCatgeriesBrandsLink(mStrMatCatQry, Constants.MaterialCategoryID, Constants.MaterialCategoryDesc);
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        categoryValuesToSpinner();
    }


    private void getCPStockList() {
        try {
            Constants.Map_Must_Sell_Mat.clear();
            alCRSSKUGrpList.clear();
            skuGroupBeanArrayListAllData.clear();
            Constants.HashMapSchemeIsInstantOrQPS.clear();
            Constants.HashMapSchemeListByMaterial.clear();
            Constants.SchemeQRY = "";
//            LogManager.writeLogDebug("start CRSSKU grp");
            skuGroupBeanArrayListAllData = OfflineManager.getCRSSKUGroup(Constants.CPStockItems + "?$select=" + Constants.OrderMaterialGroupID + ","
                            + Constants.OrderMaterialGroupDesc + "," + Constants.MaterialNo + "," + Constants.MaterialDesc + ","
                            + Constants.CPStockItemGUID + "," + Constants.UOM + "," + Constants.Currency + "," +
                            ConstantsUtils.Brand + "," + ConstantsUtils.SKUGroup + "," + ConstantsUtils.Banner + "," + ConstantsUtils.ProductCategoryID + " &$filter= " + Constants.StockOwner + " eq '" + stockOwner + "' and "
                            + Constants.OrderMaterialGroupID + " ne '' and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "' and " + Constants.CPGUID + " eq '" + mStrParentID + "' and " + dmsDivQryBean.getDMSDivisionQry() + " ", mStrInvListQry,
                    mIntBalVisitRet, mStrBundleCPGUID32, alMustSellMatList, mArrayDistributors[4][0], mArrayDistributors[5][0], mArrayDistributors[8][0], dmsDivQryBean.getDMSDivisionQry(), dmsDivQryBean.getDMSDivisionIDQry(),
                    hashMapDBStk, hashMapRetailerStk, hashMapTargetByCrsskugrp, hashMapInvQtyByCrsskugrp, hashMapMustSellMatAvgContribution,
                    hashMapUOM, hashMapSegmentedMat, alMapMaterialDBStkUOM, hashMapRetailerStkByMat, stockOwner, mStrParentID, hashMapMustSell);

         //   System.out.println("hashMapMustSell"+hashMapMustSell.toString()+"mStrParentID"+mStrParentID.toString()+"stockOwner"+stockOwner.toString()+"hashMapRetailerStkByMat"+hashMapRetailerStkByMat.toString());
//            LogManager.writeLogDebug("end CRSSKU grp");

            CRSSKUGrpList = new ArrayList<>();
            alCRSSKUGrpList.addAll(skuGroupBeanArrayListAllData);
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
        hideCustomKeyboard();
        checkAndCloseAllExpandedItem();
        Set<String> mSetOrderMatGrp = new HashSet<>();
        if (skuGroupBeanArrayListAllData.size() > 0) {
            alCRSSKUGrpList.clear();
            switch (mStrSelType) {
                case Constants.str_00: {
                    if (!mStrSelCatType.equalsIgnoreCase(Constants.str_00) && !mStrSelBrand.equalsIgnoreCase(Constants.str_00)) {
                        mSetOrderMatGrp = getOrderMatGrpByBrandAndCategory(mStrSelCatType, mStrSelBrand, dmsDivQryBean.getDMSDivisionQry());
                    } else if (!mStrSelCatType.equalsIgnoreCase(Constants.str_00) && mStrSelBrand.equalsIgnoreCase(Constants.str_00)) {
                        mSetOrderMatGrp = getOrderMatGrpByBrandAndCategory(mStrSelCatType, "", dmsDivQryBean.getDMSDivisionQry());
                    } else if (mStrSelCatType.equalsIgnoreCase(Constants.str_00) && !mStrSelBrand.equalsIgnoreCase(Constants.str_00)) {
                        mSetOrderMatGrp = getOrderMatGrpByBrandAndCategory("", mStrSelBrand, dmsDivQryBean.getDMSDivisionQry());
                    } else if (mStrSelCatType.equalsIgnoreCase(Constants.str_00) && mStrSelBrand.equalsIgnoreCase(Constants.str_00)) {
                        mSetOrderMatGrp = new HashSet<>();
                    }

                    for (SKUGroupBean item : skuGroupBeanArrayListAllData) {
                        if (mSetOrderMatGrp.size() > 0) {
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

                    mustSellList.clear();

                    for (SKUGroupBean item : skuGroupBeanArrayListAllData) {

                        if (!item.getMatTypeVal().equalsIgnoreCase("")) {
                            //  alCRSSKUGrpList.add(item);
                            if(!mustselltypeset.equalsIgnoreCase("")) {

                                if (item.getSKUGroupDesc().contains(mustselltypeset)) {
                                    alCRSSKUGrpList.add(item);
                                } else {
                                    mustSellList.add(item);
                                }

                            }else {

                                alCRSSKUGrpList.add(item);
                            }
                        }

                    }
                }
                break;
            }
        }


        alCRSSKUGrpList.addAll(mustSellList);

        createTable();
    }

    private Set<String> getOrderMatGrpByBrandAndCategory(String mStrCatID, String mStrBrandID, String mStrDMSDivisionQry) {
        Set<String> mSetOrderMatGrp = new HashSet<>();
        try {

            if (!mStrCatID.equalsIgnoreCase("") && mStrBrandID.equalsIgnoreCase("")) {
                mSetOrderMatGrp = OfflineManager.getValueByColumnNameCRSSKU(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupID +
                        " &$filter = " + Constants.MaterialCategoryID + " eq '" + mStrCatID + "' and " + mStrDMSDivisionQry + "  ", Constants.OrderMaterialGroupID);
            } else if (mStrCatID.equalsIgnoreCase("") && !mStrBrandID.equalsIgnoreCase("")) {
                mSetOrderMatGrp = OfflineManager.getValueByColumnNameCRSSKU(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupID +
                        " &$filter = " + Constants.BrandID + " eq '" + mStrBrandID + "' and " + mStrDMSDivisionQry + " ", Constants.OrderMaterialGroupID);
            } else {
                mSetOrderMatGrp = OfflineManager.getValueByColumnNameCRSSKU(Constants.OrderMaterialGroups + "?$select=" + Constants.OrderMaterialGroupID +
                        " &$filter = " + Constants.BrandID + " eq '" + mStrBrandID + "' and " + Constants.MaterialCategoryID + " eq '" + mStrCatID + "' and " + mStrDMSDivisionQry + " ", Constants.OrderMaterialGroupID);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mSetOrderMatGrp;
    }

    private void spinnerSKUValues() {
        ArrayAdapter<String> mustSellAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, skuType[1]);
        mustSellAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spnrSKUType.setAdapter(mustSellAdapter);
      //  System.out.println("Triggred4" + skuType[1].toString());


        spnrSKUType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                mStrSelType = skuType[0][position];

          //      System.out.println("Triggred 9 " + skuType[0][position]);


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

                if (mBoolFirstTime && !pdLoadDialog.isShowing()) {
                    getCRSKSUList();
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
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
                getBrands(SalesOrderCreateActivity1.this);

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

                if (mBoolFirstTime && !pdLoadDialog.isShowing()) {
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
        reviewMenu = menu.findItem(R.id.menu_review);
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
                reviewMenu.setEnabled(false);
                Log.e("reviewMenu", "Clicked");
                onReviewPage();
                break;
        }
        return true;
    }

    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onBackPressed() {
        if (isCustomKeyboardVisible()) {
            hideCustomKeyboard();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(SalesOrderCreateActivity1.this, R.style.MyTheme);
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
        Intent intentNavPrevScreen = new Intent(SalesOrderCreateActivity1.this, RetailersDetailsActivity.class);
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
            int ListSize = alCRSSKUGrpList.size() - 1;
            if (lastSelectedEditTextRow != ListSize) {
                int firstRow = tableFixHeaders.getFirstRow();
                int visibleHeight = tableFixHeaders.getRowHeights() + firstRow;
                int newItemPoss = lastSelectedEditTextRow + 1;
                if (newItemPoss < visibleHeight) {
                    SKUGroupBean skuGroupBean = alCRSSKUGrpList.get(newItemPoss);
                    skuGroupBean.setFocusHeaderText(true);
                    skuGroupBean.setSetCursorPos(skuGroupBean.getEtQty().length());
                    if (salesOrderAdapter != null) {
                        salesOrderAdapter.notifyDataSetChanged();
                    }
                    Constants.showCustomKeyboard(null, keyboardView, SalesOrderCreateActivity1.this);
                }
            }
        } else {
            if (lastSelectedEditTextRow != 0) {
                int firstRow = tableFixHeaders.getFirstRow();
                if (firstRow <= lastSelectedEditTextRow - 1) {
                    int newItemPoss = lastSelectedEditTextRow - 1;
                    SKUGroupBean skuGroupBean = alCRSSKUGrpList.get(newItemPoss);
                    skuGroupBean.setFocusHeaderText(true);
                    skuGroupBean.setSetCursorPos(skuGroupBean.getEtQty().length());
                    if (salesOrderAdapter != null) {
                        salesOrderAdapter.notifyDataSetChanged();
                    }
                    Constants.showCustomKeyboard(null, keyboardView, SalesOrderCreateActivity1.this);
                }
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
//                if(!mBoolSubItemSel) {
                if (mEditTextSelected != null)
                    Constants.incrementTextValues(mEditTextSelected, Constants.N);
//                }else{
//                    Constants.incrementTextValues(mEditTextSelectedSubItems, Constants.N);
//                }
                break;
            case 69:
                //Minus
                if (mEditTextSelected != null)
                    Constants.decrementEditTextVal(mEditTextSelected, Constants.N);
//                }else{
//                    Constants.incrementTextValues(mEditTextSelectedSubItems, Constants.N);
//                }
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

        hideCustomKeyboard();
        checkAndCloseAllExpandedItem();
        createTable();

        if (ValidateSOItems() /*&& validateMustSell()*/) {

            Intent intentSOCreate = new Intent(SalesOrderCreateActivity1.this,
                    SalesOrderReviewActivity1.class);
            Constants.selectedSOItems = skuGroupBeanArrayListAllData;
            intentSOCreate.putExtra(Constants.CPNo, mStrBundleRetID);
            intentSOCreate.putExtra(Constants.CPUID, mStrBundleRetailerUID);
            intentSOCreate.putExtra(Constants.RetailerName, mStrBundleRetName);
            intentSOCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
            intentSOCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
            intentSOCreate.putExtra(Constants.comingFrom, mStrComingFrom);
            startActivity(intentSOCreate);
            // reviewMenu.setEnabled(true);
        } else {
//            if (!mBoolMustSellMatQtyValid) {
            UtilConstants.showAlert(getString(R.string.all_must_sell_qty_should_be_entered), SalesOrderCreateActivity1.this);
            reviewMenu.setEnabled(true);
//            } else {
//                UtilConstants.showAlert(getString(R.string.alert_enter_atlest_one_material), SalesOrderCreateActivity1.this);
//            }

        }
    }


    @Override
    public void onRequestError(int i, Exception e) {

    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {

    }

    private boolean validateMustSell() {
        mBoolMustSellMatQtyValid = true;

        if (!Constants.Map_Must_Sell_Mat.isEmpty()) {
            Iterator mapSelctedValues = Constants.Map_Must_Sell_Mat.keySet()
                    .iterator();
            while (mapSelctedValues.hasNext()) {
                String Key = (String) mapSelctedValues.next();
                if (Constants.Map_Must_Sell_Mat.get(Key).equalsIgnoreCase("")) {
                    mBoolMustSellMatQtyValid = false;
                    break;
                }
            }
        } else {
            mBoolMustSellMatQtyValid = true;
        }

        return mBoolMustSellMatQtyValid;
    }

    private boolean ValidateSOItems() {
        boolean mBoolMustSellMatQtyValid = false;

//        if(typeValues.equalsIgnoreCase(Constants.X)) {
//            for (SKUGroupBean skuGroupBean : skuGroupBeanArrayListAllData) {
//                if(skuGroupBean.isHeader()){
//                    if(!skuGroupBean.getMatTypeVal().equalsIgnoreCase("")){
//                        if (skuGroupBean.getEtQty().equalsIgnoreCase("")) {
//                            mBoolMustSellMatQtyValid = false;
//                            break;
//                        }
//                    }else{
//                        mBoolMustSellMatQtyValid = true;
//                    }
//                }else{
//                    mBoolMustSellMatQtyValid = true;
//                }
//
//            }
//        }else{
        mBoolMustSellMatQtyValid = true;
//        }


        return mBoolMustSellMatQtyValid;
    }

    private int oldPercentage = 0;
    private int getFirstClickedPos = 0;

    @Override
    public void onScrolled(int verticalScroll) {
//        Log.d("percentageOfView", " maxHeight:"+verticalScroll);
        if (!isEditTextClicked) {
            if (keyboardView != null && verticalScroll > 0 && (verticalScroll > oldPercentage + 5 || verticalScroll < oldPercentage - 5)) {
                keyboardView.setVisibility(View.GONE);
                keyboardView.setEnabled(false);
                oldPercentage = verticalScroll;
            }
        } else {
            int getFirstRow = tableFixHeaders.getFirstRow();
           // Log.d("percentageOfView", " getFirstRow:" + getFirstRow + " getFirstClickedPos :" + getFirstClickedPos);
            if ((getFirstRow > getFirstClickedPos + 1 || getFirstRow < getFirstClickedPos - 1)) {
                isEditTextClicked = false;
            }
        }
    }

    @Override
    public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
        int type = bundle != null ? bundle.getInt(Constants.BUNDLE_REQUEST_CODE) : 0;
      //  System.out.println("Triggred8 " + type);

        switch (type) {

            case 1:
                //      hashMapMustSellTemp =  OfflineManager.getMustSellMatList(list);
                break;
        }

    }

    @Override
    public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {

    }


    /*AsyncTask to get Retailers List*/
    private class GetSOData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

          //  System.out.println("Triggred3");

            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(SalesOrderCreateActivity1.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Constants.CPGUIDVAL = mStrBundleCPGUID32;
            loadingSO();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            closeProgressDialog();
            spinnerSKUValues();
            getMatCat();
            if (!mBoolFirstTime) {

             //   System.out.println("Triggred5");

                setTempArrayList();
                createTable();
                setValueToUI();
            }
           /* try {
         //      String qry = Constants.MustSells+"?$filter="+Constants.ValidTo+" ge datetime'" + UtilConstants.getNewDate() + "'";
                String qry = Constants.MustSells;
                Constants.onlineRequest(SalesOrderCreateActivity1.this, qry, false, 1,
                        ConstantsUtils.SESSION_QRY, new OnlineODataInterface() {
                            @Override
                            public void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> list, Bundle bundle) {
                                Log.d("Time getMustSellMatList", UtilConstants.getSyncHistoryddmmyyyyTime());
                                hashMapMustSell =  OfflineManager.getMustSellMatList(list,stockOwner,mStrBundleRetID,mStrCPTypeID,
                                        mStrParentID,dmsDivQryBean.getDMSDivisionSSInvQry(),UtilConstants.getNewDate());

                                System.out.println("Triggred6"+hashMapMustSell.toString());

                                setUI();
                            }

                            @Override
                            public void responseFailed(ODataRequestExecution oDataRequestExecution, String s, Bundle bundle) {

                                System.out.println("Triggred7"+s);

                                setUI();
                            }
                        }, true, true);

            } catch (Exception e) {
                setUI();
                e.printStackTrace();
            }*/
        }
    }

    private void setUI() {
      //  Log.d("Time getCPStockList", UtilConstants.getSyncHistoryddmmyyyyTime());
        getCPStockList();
      //  Log.d("Time get getCRSSKUGroup", UtilConstants.getSyncHistoryddmmyyyyTime());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeProgressDialog();
                if (!mBoolFirstTime) {
                    setTempArrayList();
                    createTable();
                    setValueToUI();
                }
            }
        });


    }
}

