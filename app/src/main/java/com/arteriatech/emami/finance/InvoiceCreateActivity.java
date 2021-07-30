package com.arteriatech.emami.finance;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.reports.MyStockBean;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.mobile.lib.parser.IODataError;
import com.sap.mobile.lib.parser.ParserException;
import com.sap.mobile.lib.request.INetListener;
import com.sap.mobile.lib.request.IRequest;
import com.sap.mobile.lib.request.IRequestStateElement;
import com.sap.mobile.lib.request.IResponse;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.xscript.core.GUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

@SuppressLint("NewApi")
public class InvoiceCreateActivity extends AppCompatActivity implements UIListener, INetListener {

    public HashMap<String, String> mapCheckedStateHashMap = new HashMap<String, String>();
    public HashMap<String, String> mapUnitPriceHashMap = new HashMap<String, String>();
    public HashMap<String, String> mapEnteredTextsHashMap = new HashMap<String, String>();
    public HashMap<String, String> mapMatCodeHashMap = new HashMap<String, String>();
    public HashMap<String, String> mapMatDescHashMap = new HashMap<String, String>();
    public HashMap<String, String> mapMatUomHashMap = new HashMap<String, String>();
    public HashMap<String, String> mapStockQty = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> arrtable;
    Hashtable<String, ArrayList<InvoiceBean>> hashTableItemSerialNos;
    TextView tv_total_order_value;
    double unitPrice = 0.0, totalNetAmout = 0.0;
    int mError = 0;
    Hashtable dbHeaderTable;
    String invGUID32 = "";
    String mStrDateTime = "";
    Hashtable visitActivityTable;
    ArrayList<MyStockBean> alDevStock;
    Spinner filterType;
    String selectedType = "";
    EditText ed_invoice_search;
    String mStrComingFrom = "";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "", mStrMobileNo = "", mStrCpGuid36 = "", mStrRetailerCpType = "", mStrInvListQry = "";
    private ODataGuid mCpGuid;
    private ArrayList<InvoiceBean> alMatList = null, alMatTempList = null, filteredArraylist = null, alTempList;
    private ArrayList<InvoiceBean> alFocusedList = null;
    private ArrayList<InvoiceBean> alNewList = null;
    private ArrayList<InvoiceBean> alMustList = null;
    private ArrayList<InvoiceBean> selectedInvoice = null;
    private ProgressDialog pdLoadDialog;
    private String popUpText = "", matGrpType = "";
    private String doc_no;
    private String mStrSpStockItemGuid = "";
    private Handler mHandler = null;
    private String[][] mArrayDistributors, mArraySPValues = null;
    private ODataPropMap oDataProperties;
    private ODataProperty oDataProperty;
    private boolean isFirstTimeLoadMat = false;
    private String statusStr[] = {Constants.Desc, Constants.Code};
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateResultsInUi();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.title_invoiceCreate));

        setContentView(R.layout.activity_invoice_create);

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
        if (!Constants.restartApp(InvoiceCreateActivity.this)) {
            initUI();
        }
    }

    /**
     * get salesPerson values
     */
    private void getSalesPersonValues() {
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(mStrBundleCPGUID);
    }

    /*Initializes UI*/
    void initUI() {
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);

        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetailerUID);

        mHandler = new Handler();

        filterType = (Spinner) findViewById(R.id.sp_search_type);
        ed_invoice_search = (EditText) findViewById(R.id.ed_invoice_search);
        tv_total_order_value = (TextView) findViewById(R.id.tv_total_order_value);

        tv_total_order_value.setText(getString(R.string.str_rupee_symbol) + " " + Constants.removeLeadingZero(""));
        getSalesPersonValues();
        getDistributorValues();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, statusStr);
        adapter.setDropDownViewResource(R.layout.spinnerinside);
        filterType.setAdapter(adapter);

        filterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                selectedType = statusStr[position];
                ed_invoice_search.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        String retDetgry = Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrBundleCPGUID.toUpperCase() + "' ";
        try {
            ODataEntity retilerEntity = OfflineManager.getRetDetails(retDetgry);

            oDataProperties = retilerEntity.getProperties();

            oDataProperty = oDataProperties.get(Constants.CPGUID);

            mCpGuid = (ODataGuid) oDataProperty.getValue();

            oDataProperty = oDataProperties.get(Constants.CPTypeID);

            mStrRetailerCpType = (String) oDataProperty.getValue();


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        } catch (Exception e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

        try {
            mStrCpGuid36 = mCpGuid.guidAsString36().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getInvListQry();

        selectedInvoice = new ArrayList<InvoiceBean>();
        try {
            alMatTempList = new ArrayList<>();

            alDevStock = Constants.getDevStock(InvoiceCreateActivity.this, "");

//            String mStrMustSellQry = Constants.SegmentedMaterials + "?$filter=" + Constants.SegmentId + " eq '01' ";
//
//            String mStrFocusedPrdQry = Constants.SegmentedMaterials + "?$filter=" + Constants.SegmentId + " eq '02' ";
//
//            String mStrNewProductQry = Constants.SegmentedMaterials + "?$filter=" + Constants.SegmentId + " eq '03'";
//
//            alMustList = OfflineManager.getMustSellMatList(mStrMustSellQry, "01");
//
//            alFocusedList = OfflineManager.getSegMatList(mStrFocusedPrdQry, "02", mStrMustSellQry);
//
//            String mStrMustSellOrConQry = Constants.SegmentedMaterials + "?$filter=(" + Constants.SegmentId + " eq '01' or " + Constants.SegmentId + " eq '02' )";
//
//            alNewList = OfflineManager.getSegMatList(mStrNewProductQry, "03", mStrMustSellOrConQry);
//
//            String mStrAdditionalQry = Constants.SegmentedMaterials + "?$filter=(" + Constants.SegmentId + " eq '01' or " + Constants.SegmentId + " eq '02' or " + Constants.SegmentId + " eq '03')";
//
//            alMatList = OfflineManager.getOtherMaterialsList(mStrAdditionalQry, "04");
            alMatList = OfflineManager.getMaterialList(Constants.CPStockItems + "?$filter=StockOwner eq '01'", "04");

            if (alMustList != null && alMustList.size() > 0) {
                alMatTempList.addAll(alMustList);
            }

            if (alFocusedList != null && alFocusedList.size() > 0) {
                if (alMatTempList != null && alMatTempList.size() > 0) {
                    alMatTempList.addAll(alMatTempList.size(), alFocusedList);
                } else {
                    alMatTempList.addAll(alFocusedList);
                }

            }

            if (alNewList != null && alNewList.size() > 0) {
                if (alMatTempList != null && alMatTempList.size() > 0) {
                    alMatTempList.addAll(alMatTempList.size(), alNewList);
                } else {
                    alMatTempList.addAll(alNewList);
                }
            }

            if (alMatList != null && alMatList.size() > 0) {
                if (alMatTempList != null && alMatTempList.size() > 0) {
                    alMatTempList.addAll(alMatTempList.size(), alMatList);
                } else {
                    alMatTempList.addAll(alMatList);
                }
            }


            displayMaterialValues(alMatTempList);


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

        ed_invoice_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                filteredArraylist = new ArrayList<>();
                for (int i = 0; i < alMatTempList.size(); i++) {
                    InvoiceBean item = alMatTempList.get(i);
                    if (selectedType.equalsIgnoreCase(Constants.Desc)) {
                        if (item.getMatDesc().toLowerCase()
                                .contains(cs.toString().toLowerCase().trim())) {
                            filteredArraylist.add(item);

                        }
                    } else {
                        if (item.getMatCode().toLowerCase()
                                .contains(cs.toString().toLowerCase().trim())) {
                            filteredArraylist.add(item);

                        }
                    }
                }
                Collections.sort(filteredArraylist, new Comparator<InvoiceBean>() {
                    @Override
                    public int compare(InvoiceBean myBean1, InvoiceBean myBean2) {
                        return myBean1.getMatGrp().compareTo(myBean2.getMatGrp());
                    }
                });

                displayMaterialValues(filteredArraylist);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
    }

    /*Gets query for invoice list*/
    private void getInvListQry() {


        String qryStr = Constants.SSINVOICES + "?$filter=" + Constants.SoldToCPGUID + " eq guid'" + mStrCpGuid36 + "' ";
        try {
            mStrInvListQry = OfflineManager.getInvListQryByCpGUID(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * get distributor value
     */
    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
    }

    /*displays Values of materials*/
    @SuppressLint("InflateParams")
    private void displayMaterialValues(ArrayList<InvoiceBean> filteredArraylist) {
        matGrpType = "";
        LinearLayout ll_Mat_List = (LinearLayout) findViewById(R.id.ll_mat_list);
        ll_Mat_List.removeAllViews();

        @SuppressLint("InflateParams")
        TableLayout tlMatList = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);

        LinearLayout llMAtList;
        LinearLayout llMAtHead;

        if (filteredArraylist != null) {
            if (!filteredArraylist.isEmpty()
                    && filteredArraylist.size() > 0) {

                final CheckBox[] addInvoiceCheck;
                final EditText[] addtionalInvoiceEdit;
                final EditText[] rateInvoiceEdit;
                final ImageView[] addtionalSerialNoSel;
                TextView[] tv_invoice_last_qty;
                TextView[] tv_mat_rate;

                addInvoiceCheck = new CheckBox[filteredArraylist.size()];
                addtionalInvoiceEdit = new EditText[filteredArraylist.size()];
                rateInvoiceEdit = new EditText[filteredArraylist.size()];
                addtionalSerialNoSel = new ImageView[filteredArraylist.size()];
                tv_invoice_last_qty = new TextView[filteredArraylist.size()];
                tv_mat_rate = new TextView[filteredArraylist.size()];


                for (int i = 0; i < filteredArraylist.size(); i++) {
                    final int selvalue = i;
                    final InvoiceBean newbean = filteredArraylist.get(i);


                    if (matGrpType.equalsIgnoreCase("") || !matGrpType.equalsIgnoreCase(newbean.getMatGrp())) {
                        matGrpType = newbean.getMatGrp();

                        llMAtHead = (LinearLayout) LayoutInflater.from(this)
                                .inflate(R.layout.invoice_list_header,
                                        null, false);

                        if (matGrpType.equalsIgnoreCase("04")) {
                            ((TextView) llMAtHead.findViewById(R.id.tv_invoice_header_value))
                                    .setText(getString(R.string.lbl_addtional_material));
                        } else if (matGrpType.equalsIgnoreCase("03")) {
                            ((TextView) llMAtHead.findViewById(R.id.tv_invoice_header_value))
                                    .setText(getString(R.string.lbl_new_product));
                        } else if (matGrpType.equalsIgnoreCase("02")) {
                            ((TextView) llMAtHead.findViewById(R.id.tv_invoice_header_value))
                                    .setText(getString(R.string.lbl_focused_product));
                        } else if (matGrpType.equalsIgnoreCase("01")) {
                            ((TextView) llMAtHead.findViewById(R.id.tv_invoice_header_value))
                                    .setText(getString(R.string.lbl_must_sell));
                        }

                        tlMatList.addView(llMAtHead);
                    }

                    llMAtList = (LinearLayout) LayoutInflater.from(this)
                            .inflate(R.layout.invoice_list_item,
                                    null, false);
                    addInvoiceCheck[i] = (CheckBox) llMAtList
                            .findViewById(R.id.ch_invoice_create);
                    addtionalSerialNoSel[i] = (ImageView) llMAtList
                            .findViewById(R.id.iv_invoice_serial_no_sel);

                    ((TextView) llMAtList.findViewById(R.id.tv_invoice_matdesc))
                            .setText(filteredArraylist.get(i).getMatDesc());

                    ((TextView) llMAtList
                            .findViewById(R.id.tv_invoice_matcode))
                            .setText(UtilConstants.removeLeadingZeros(filteredArraylist.get(i).getMatCode()));

                    addtionalInvoiceEdit[i] = (EditText) llMAtList.findViewById(R.id.ed_invoice_inv_qty);

                    rateInvoiceEdit[i] = (EditText) llMAtList.findViewById(R.id.ed_mat_rate);

                    tv_invoice_last_qty[i] = (TextView) llMAtList.findViewById(R.id.tv_invoice_last_qty);

                    tv_mat_rate[i] = (TextView) llMAtList.findViewById(R.id.tv_mat_rate);

                    if (newbean.getZzindicator().equalsIgnoreCase("S")) {
                        tv_mat_rate[i].setVisibility(View.GONE);
                        rateInvoiceEdit[i].setEnabled(true);
                    } else {

                        rateInvoiceEdit[i].setVisibility(View.GONE);
                        rateInvoiceEdit[i].setEnabled(false);
                    }

//                    mapUnitPriceHashMap.put(newbean.getCPStockItemGUID(), newbean.getStockValue());
                    mapUnitPriceHashMap.put(newbean.getCPStockItemGUID(), newbean.getUnitPrice());
                    tv_mat_rate[i].setText(UtilConstants.removeLeadingZerowithTwoDecimal(newbean.getUnitPrice())+ " "+ newbean.getCurrency());
//                    tv_mat_rate[i].setText(newbean.getStockValue());
//                    rateInvoiceEdit[i].setText(newbean.getStockValue().equalsIgnoreCase("0") ? "" : newbean.getStockValue());
                    rateInvoiceEdit[i].setText(newbean.getUnitPrice().equalsIgnoreCase("0") ? "" : newbean.getUnitPrice());

                    String lastPurQty = "0";
                    try {
                        lastPurQty = OfflineManager.getLastPurchasesQty(mStrInvListQry, mStrCpGuid36.toUpperCase(), filteredArraylist.get(i).getMatCode(), InvoiceCreateActivity.this);

                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }

//                    tv_invoice_last_qty[i].setText(UtilConstants.removeLeadingZero(Double.parseDouble(lastPurQty)));
                    tv_invoice_last_qty[i].setText(UtilConstants.removeLeadingZero(Double.parseDouble(filteredArraylist.get(i).getUnrestrictedQty()))+" "+ filteredArraylist.get(i).getUom());
//                    if(checkSerialNo(newbean.getCPStockItemGUID()))
//                        addtionalSerialNoSel[selvalue].setVisibility(View.VISIBLE);
//                    else
//                        addtionalSerialNoSel[selvalue].setVisibility(View.GONE);
                    if (mapCheckedStateHashMap
                            .containsKey(newbean.getCPStockItemGUID().toString())) {
                        addInvoiceCheck[i].setChecked(true);
                        addtionalInvoiceEdit[selvalue].setEnabled(true);
                    }

                    if (Constants.HashTableSerialNoSelection.get(newbean.getCPStockItemGUID()) != null && Constants.HashTableSerialNoSelection.get(newbean.getCPStockItemGUID()).size() > 0) {
                        addtionalSerialNoSel[i].setImageResource(R.drawable.green_right_arrow);
                    } else {
                        addtionalSerialNoSel[i].setImageResource(R.drawable.navigateto);
                    }

                    if (checkSerialNo(newbean.getCPStockItemGUID()))
                        addtionalSerialNoSel[selvalue].setVisibility(View.VISIBLE);
                    else
                        addtionalSerialNoSel[selvalue].setVisibility(View.GONE);

                    if (newbean.getMatGrp().equalsIgnoreCase("01") && !isFirstTimeLoadMat) {
                        addtionalInvoiceEdit[i].setEnabled(true);
                        addInvoiceCheck[selvalue].setChecked(true);

                        if (!newbean.getCPStockItemGUID().equalsIgnoreCase("")) {
                            mapCheckedStateHashMap.put(newbean.getCPStockItemGUID(), "");

                            mapEnteredTextsHashMap.put(newbean.getCPStockItemGUID(), "");

                            mapMatCodeHashMap.put(newbean.getCPStockItemGUID(), newbean.getMatCode());

                            mapMatDescHashMap.put(newbean.getCPStockItemGUID(), newbean.getMatDesc());

                            mapMatUomHashMap.put(newbean.getCPStockItemGUID(), newbean.getUom());

                            mapStockQty.put(newbean.getCPStockItemGUID(), newbean.getUnrestrictedQty());
                        }

                    }

                    if (mapEnteredTextsHashMap
                            .containsKey(newbean.getCPStockItemGUID().toString())) {
                        addtionalInvoiceEdit[selvalue].setText(mapEnteredTextsHashMap.get(newbean.getCPStockItemGUID().toString()) != null ? mapEnteredTextsHashMap.get(newbean.getCPStockItemGUID().toString()) : "");
                    }

                    if (mapUnitPriceHashMap
                            .containsKey(newbean.getCPStockItemGUID().toString())) {
                        rateInvoiceEdit[selvalue].setText(mapUnitPriceHashMap.get(newbean.getCPStockItemGUID().toString()) != null ? mapUnitPriceHashMap.get(newbean.getCPStockItemGUID().toString()) : "");
                    }

                    addInvoiceCheck[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (isChecked) {
                                if (checkSerialNo(newbean.getCPStockItemGUID()))
                                    addtionalSerialNoSel[selvalue].setVisibility(View.VISIBLE);
                                else
                                    addtionalSerialNoSel[selvalue].setVisibility(View.GONE);

                                mapCheckedStateHashMap.put(newbean.getCPStockItemGUID(), "");

                                mapEnteredTextsHashMap.put(newbean.getCPStockItemGUID(), "");

                                if (!mapUnitPriceHashMap.containsKey(newbean.getCPStockItemGUID())) {
                                    mapUnitPriceHashMap.put(newbean.getCPStockItemGUID(), "");
                                }


                                mapMatCodeHashMap.put(newbean.getCPStockItemGUID(), newbean.getMatCode());

                                mapMatDescHashMap.put(newbean.getCPStockItemGUID(), newbean.getMatDesc());
                                mapMatUomHashMap.put(newbean.getCPStockItemGUID(), newbean.getUom());
                                mapStockQty.put(newbean.getCPStockItemGUID(), newbean.getUnrestrictedQty());
                            } else {

                                if (mapUnitPriceHashMap.containsKey(newbean.getCPStockItemGUID()) && newbean.getZzindicator().equalsIgnoreCase("S")) {
                                    mapUnitPriceHashMap.remove(newbean.getCPStockItemGUID());
                                }

                                if (mapCheckedStateHashMap.containsKey(newbean.getCPStockItemGUID())) {
                                    mapCheckedStateHashMap.remove(newbean.getCPStockItemGUID());
                                }

                                if (mapEnteredTextsHashMap.containsKey(newbean.getCPStockItemGUID())) {
                                    mapEnteredTextsHashMap.remove(newbean.getCPStockItemGUID());

                                }

                                if (mapMatCodeHashMap.containsKey(newbean.getCPStockItemGUID())) {
                                    mapMatCodeHashMap.remove(newbean.getCPStockItemGUID());
                                }

                                if (mapMatDescHashMap.containsKey(newbean.getCPStockItemGUID())) {
                                    mapMatDescHashMap.remove(newbean.getCPStockItemGUID());
                                    mapMatUomHashMap.remove(newbean.getCPStockItemGUID());
                                    mapStockQty.remove(newbean.getCPStockItemGUID());
                                }


                                if (Constants.HashTableSerialNoSelection.containsKey(newbean.getCPStockItemGUID())) {
                                    Constants.HashTableSerialNoSelection.remove(newbean.getCPStockItemGUID());

                                }

                                if (Constants.HashTableSerialNoAllocatedQty.containsKey(newbean.getCPStockItemGUID())) {
                                    Constants.HashTableSerialNoAllocatedQty.remove(newbean.getCPStockItemGUID());
                                }

                                rateInvoiceEdit[selvalue].setText("");
                                addtionalInvoiceEdit[selvalue].setText("");
                                addtionalInvoiceEdit[selvalue].setClickable(false);
                                addtionalSerialNoSel[selvalue].setImageResource(R.drawable.navigateto);
                                if (checkSerialNo(newbean.getCPStockItemGUID()))
                                    addtionalSerialNoSel[selvalue].setVisibility(View.VISIBLE);
                                else
                                    addtionalSerialNoSel[selvalue].setVisibility(View.GONE);

                            }
                        }
                    });

                    addtionalSerialNoSel[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mapEnteredTextsHashMap.containsKey(newbean.getCPStockItemGUID())) {
                                // ToDo quantity validation
                                if (Double.parseDouble(!mapEnteredTextsHashMap.get(newbean.getCPStockItemGUID()).equalsIgnoreCase("") ?
                                        mapEnteredTextsHashMap.get(newbean.getCPStockItemGUID()) : "0") > 0) {
                                    // ToDo unit price validation
                                    try {
//                                        if (Double.parseDouble(!newbean.getStockValue().equalsIgnoreCase("") ?
//                                                newbean.getStockValue() : "0") > 0) {
                                        if (Double.parseDouble(!newbean.getUnitPrice().equalsIgnoreCase("") ?
                                                newbean.getUnitPrice() : "0") > 0) {
//                                            String mStrVisitStartedQry = Constants.SPStockItemSNos
//                                                    + "?$filter=" + Constants.CPStockItemGUID + " eq guid'"
//                                                    + newbean.getCPStockItemGUID().toUpperCase() + "'";
                                            String mStrVisitStartedQry = Constants.CPStockItemSnos
                                                    + "?$filter=" + Constants.CPStockItemGUID + " eq guid'"
                                                    + newbean.getCPStockItemGUID().toUpperCase() + "'";
                                            try {
                                                // ToDo serial numbers available or not validation
                                                if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)) {


                                                    double mdoutotQty = 0.0, mDouUnResQty = 0.0;
                                                    if (alDevStock != null && alDevStock.size() > 0) {
                                                        for (int i = 0; i < alDevStock.size(); i++) {
                                                            if (newbean.getMatCode().equalsIgnoreCase(alDevStock.get(i).getMaterialNo())) {
                                                                mDouUnResQty = 0.0;
                                                                try {
                                                                    mDouUnResQty = Double.parseDouble(alDevStock.get(i).getUnrestrictedQty());
                                                                } catch (NumberFormatException e) {
                                                                    mDouUnResQty = 0.0;
                                                                    e.printStackTrace();
                                                                }
                                                                mdoutotQty = mdoutotQty + mDouUnResQty;
                                                            }
                                                        }
                                                    }

                                                    double unresQty = 0;
                                                    try {
                                                        unresQty = Double.parseDouble(newbean.getUnrestrictedQty()) - Double.parseDouble(mdoutotQty + "");
                                                    } catch (NumberFormatException e) {
                                                        e.printStackTrace();
                                                    }

                                                    if (Double.parseDouble(mapEnteredTextsHashMap.get(newbean.getCPStockItemGUID())) > unresQty) {
                                                        displayError(getString(R.string.alert_stock_entered_qty_more_than_avalible_qty));

                                                    } else {

                                                        Intent intentInvSelectionActivity = new Intent(InvoiceCreateActivity.this,
                                                                SerialNoSelectionActivity.class);
                                                        intentInvSelectionActivity.putExtra(Constants.SPStockItemGUID, newbean.getCPStockItemGUID());
                                                        intentInvSelectionActivity.putExtra(Constants.InvoiceQty, mapEnteredTextsHashMap.get(newbean.getCPStockItemGUID()) != null ?
                                                                mapEnteredTextsHashMap.get(newbean.getCPStockItemGUID()) : "0");
//                                                        intentInvSelectionActivity.putExtra(Constants.UnitPrice, newbean.getStockValue());
                                                        intentInvSelectionActivity.putExtra(Constants.UnitPrice, newbean.getUnitPrice());
                                                        intentInvSelectionActivity.putExtra(Constants.MatCode, newbean.getMatCode());
                                                        intentInvSelectionActivity.putExtra(Constants.MatDesc, newbean.getMatDesc());
                                                        intentInvSelectionActivity.putExtra(Constants.PassedFrom, 100);
                                                        startActivityForResult(intentInvSelectionActivity, 100);

                                                    }

                                                } else {
                                                    displayError(getString(R.string.alert_stock_is_not_available));
                                                }

                                            } catch (OfflineODataStoreException e) {
                                                e.printStackTrace();
                                            }
                                        } else {

                                            if (newbean.getZzindicator().equalsIgnoreCase("S")) {
                                                displayError(getString(R.string.alert_enter_valid_rate));
                                            } else {
                                                displayError(getString(R.string.alert_unit_price_not_maintained));
                                            }

                                        }
                                    } catch (NumberFormatException e) {
                                        displayError(getString(R.string.alert_enter_valid_rate));
                                    }


                                } else {
                                    displayError(getString(R.string.alert_enter_valid_qty));
                                }
                            } else {
                                displayError(getString(R.string.alert_enter_valid_qty));
                            }

                        }
                    });

                    addtionalInvoiceEdit[selvalue].addTextChangedListener(new TextWatcher() {

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
                                addInvoiceCheck[selvalue].setChecked(true);
                            } else {
                                addInvoiceCheck[selvalue].setChecked(false);
                            }


                            if (mapCheckedStateHashMap.containsKey(newbean.getCPStockItemGUID())) {
                                mapEnteredTextsHashMap.put(newbean.getCPStockItemGUID(), s1);
                            }


                            getTotalInvAmount();

                        }
                    });

                    rateInvoiceEdit[selvalue].addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                            rateInvoiceEdit[selvalue].setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            Constants.setAmountPattern(rateInvoiceEdit[selvalue], 13, 2);
                            String s1 = s.toString();

                            if (mapUnitPriceHashMap.containsKey(newbean.getCPStockItemGUID()) && newbean.getZzindicator().equalsIgnoreCase("S")) {
                                mapUnitPriceHashMap.put(newbean.getCPStockItemGUID(), s1);
                                newbean.setStockValue(s1);
                            }

                            getTotalInvAmount();
                        }
                    });

                    tlMatList.addView(llMAtList);


                }
            } else {

                llMAtList = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.no_data_found_ll,
                                null, false);

                ll_Mat_List.addView(llMAtList);
            }

            ll_Mat_List.addView(tlMatList);
            ll_Mat_List.requestLayout();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == 100) {
            // Make sure the request was successful
            if (resultCode == 100) {

                getTotalInvAmount();

                isFirstTimeLoadMat = true;
                displayMaterialValues(alMatTempList);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_next:
                onSave();
                break;
        }
        return true;
    }

    /*Saves Invoice in offline store*/
    private void onSave() {
        ArrayList<InvoiceCreateBean> alSelectedInvoice = new ArrayList<>();
        if (Constants.isValidTime(UtilConstants.convertTimeOnly(Constants.mStartTimeDuration.toString()),
                UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString())) && Constants.isValidDate()) {


           /* if (!Constants.onGpsCheck(InvoiceCreateActivity.this)) {
                return;
            }
            if(!UtilConstants.getLocation(InvoiceCreateActivity.this)){
                return;
            }*/

            mError = 0;
            Constants.InvoiceNumber = "";
            Constants.InvoiceTotalAmount = 0.0;
            boolean errorFlag = false;
            doc_no = (System.currentTimeMillis() + "");
            unitPrice = 0.0;
            totalNetAmout = 0.0;
            int incrementVal = 0;
            if (!mapEnteredTextsHashMap.isEmpty()) {
                arrtable = new ArrayList<HashMap<String, String>>();
                Iterator mapSelctedValues = mapEnteredTextsHashMap.keySet()
                        .iterator();
                while (mapSelctedValues.hasNext()) {
                    String Key = (String) mapSelctedValues.next();
                    String value = mapEnteredTextsHashMap.get(Key);

//                String mStrVisitStartedQry = Constants.SPStockItemSNos + "?$filter=" + Constants.CPStockItemGUID + " eq guid'" + Key + "'";
                    String mStrVisitStartedQry = Constants.CPStockItemSnos + "?$filter=" + Constants.CPStockItemGUID + " eq guid'" + Key + "'";
                    try {

                        if (value != null && !value.trim().toString().equalsIgnoreCase("")) {
                            if (checkSerialNo(Key)) {

                                if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)) {
                                    if (Constants.HashTableSerialNoSelection.get(Key) != null && Constants.HashTableSerialNoSelection.get(Key).size() > 0) {
                                        try {
                                            if (Double.parseDouble(value) > 0
                                                    && Double.parseDouble(mapUnitPriceHashMap.get(Key).equalsIgnoreCase("") ? "0" : mapUnitPriceHashMap.get(Key)) > 0) {

                                                if (Double.parseDouble(value) ==
                                                        Double.parseDouble(Constants.HashTableSerialNoAllocatedQty.get(Key).equalsIgnoreCase("") ? "0"
                                                                : Constants.HashTableSerialNoAllocatedQty.get(Key))) {
                                                    GUID invItmGUID = GUID.newRandom();
                                                    HashMap<String, String> singleItem = new HashMap<String, String>();

                                                    singleItem.put(Constants.ItemNo, (incrementVal + 1) + "");
                                                    singleItem.put(Constants.MatDesc, mapMatDescHashMap.get(Key));
                                                    singleItem.put(Constants.MatCode, mapMatCodeHashMap.get(Key));
                                                    singleItem.put(Constants.Qty, value);
                                                    singleItem.put(Constants.InvoiceItemGUID, invItmGUID.toString());
                                                    singleItem.put(Constants.StockGuid, Key);
                                                    singleItem.put(Constants.UOM, mapMatUomHashMap.get(Key));
                                                    singleItem.put(Constants.UnrestrictedQty, mapStockQty.get(Key));

                                                    singleItem.put(Constants.UnitPrice, mapUnitPriceHashMap.get(Key));

                                                    double mDouQty = Double.parseDouble(mapEnteredTextsHashMap.get(Key).equalsIgnoreCase("") ? "0" : mapEnteredTextsHashMap.get(Key));
                                                    double mdoubleUnitPrice = Double.parseDouble(mapUnitPriceHashMap.get(Key).equalsIgnoreCase("") ? "0" : mapUnitPriceHashMap.get(Key));
                                                    totalNetAmout = totalNetAmout + (mDouQty * mdoubleUnitPrice);

                                                    singleItem.put(Constants.NetAmount, (mDouQty * mdoubleUnitPrice) + "");
                                                    InvoiceCreateBean invoiceItem = new InvoiceCreateBean();

                                                    arrtable.add(singleItem);
                                                    incrementVal++;
                                                } else {
                                                    displayError(getString(R.string.alert_allocated_qty_equal_to_invoice_qty));
                                                    errorFlag = true;
                                                    break;
                                                }


                                            } else {
                                                displayError(getString(R.string.alert_enter_valid_qty));
                                                errorFlag = true;
                                                break;
                                            }
                                        } catch (NumberFormatException e) {
                                            displayError(getString(R.string.alert_enter_valid_rate));
                                            errorFlag = true;
                                            break;
                                        }


                                    } else {
                                        displayError(getString(R.string.alert_select_serial_number));
                                        errorFlag = true;
                                        break;
                                    }
                                } else {
                                    displayError(getString(R.string.alert_stock_is_not_available));
                                    errorFlag = true;
                                    break;
                                }
                            } else {
                                try {
                                    if (Double.parseDouble(value) > 0
                                            && Double.parseDouble(mapUnitPriceHashMap.get(Key).equalsIgnoreCase("") ? "0" : mapUnitPriceHashMap.get(Key)) > 0) {

//                                    if (Double.parseDouble(value) ==
//                                            Double.parseDouble(Constants.HashTableSerialNoAllocatedQty.get(Key).equalsIgnoreCase("") ? "0"
//                                                    : Constants.HashTableSerialNoAllocatedQty.get(Key))) {
                                        GUID invItmGUID = GUID.newRandom();
                                        HashMap<String, String> singleItem = new HashMap<String, String>();

                                        singleItem.put(Constants.ItemNo, (incrementVal + 1) + "");
                                        singleItem.put(Constants.MatDesc, mapMatDescHashMap.get(Key));
                                        singleItem.put(Constants.MatCode, mapMatCodeHashMap.get(Key));
                                        singleItem.put(Constants.Qty, value);
                                        singleItem.put(Constants.InvoiceItemGUID, invItmGUID.toString());
                                        singleItem.put(Constants.StockGuid, Key);
                                        singleItem.put(Constants.UOM, mapMatUomHashMap.get(Key));
                                        singleItem.put(Constants.UnrestrictedQty, mapStockQty.get(Key));

                                        singleItem.put(Constants.UnitPrice, mapUnitPriceHashMap.get(Key));

                                        double mDouQty = Double.parseDouble(mapEnteredTextsHashMap.get(Key).equalsIgnoreCase("") ? "0" : mapEnteredTextsHashMap.get(Key));
                                        double mdoubleUnitPrice = Double.parseDouble(mapUnitPriceHashMap.get(Key).equalsIgnoreCase("") ? "0" : mapUnitPriceHashMap.get(Key));
                                        totalNetAmout = totalNetAmout + (mDouQty * mdoubleUnitPrice);

                                        singleItem.put(Constants.NetAmount, (mDouQty * mdoubleUnitPrice) + "");

                                        arrtable.add(singleItem);
                                        incrementVal++;
//                                    } else {
//                                        displayError(getString(R.string.alert_allocated_qty_equal_to_invoice_qty));
//                                        errorFlag = true;
//                                        break;
//                                    }


                                    } else {
                                        displayError(getString(R.string.alert_enter_valid_qty));
                                        errorFlag = true;
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    displayError(getString(R.string.alert_enter_valid_rate));
                                    errorFlag = true;
                                    break;
                                }
                            }

                        } else {

                            displayError(getString(R.string.alert_enter_valid_qty));
                            errorFlag = true;
                            break;
                        }

                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }

                }

            } else {
                displayError(getString(R.string.alert_select_atleast_one_material));
                errorFlag = true;
            }

            if (!errorFlag) {
                hashTableItemSerialNos = Constants.HashTableSerialNoSelection;

                try {


                    if (!hashTableItemSerialNos.isEmpty() && hashTableItemSerialNos.size() > 0) {
                        Iterator mapSelctedValues = hashTableItemSerialNos.keySet()
                                .iterator();
                        while (mapSelctedValues.hasNext()) {
                            String Key = (String) mapSelctedValues.next();
                            ArrayList<InvoiceBean> alItemSerialNo = hashTableItemSerialNos.get(Key);
                            if (alItemSerialNo != null && alItemSerialNo.size() > 0) {
                                for (int j = 0; j < alItemSerialNo.size(); j++) {
                                    InvoiceBean serialNoInvoiceBean = alItemSerialNo.get(j);
                                    if (!serialNoInvoiceBean.getStatus().equalsIgnoreCase("04")) {
                                        try {
                                            //noinspection unchecked
                                            if (serialNoInvoiceBean.getStatus().equalsIgnoreCase("01")) {
                                                OfflineManager.deleteSpStockSNos(serialNoInvoiceBean, Key);
                                            } else if (serialNoInvoiceBean.getStatus().equalsIgnoreCase("02")) {
                                                OfflineManager.createSpStockSNos(serialNoInvoiceBean, Key);
                                            }

                                        } catch (OfflineODataStoreException e) {
                                            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
                                        }
                                    }

                                }
                            }
                        }
                    }

                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);

                    String loginIdVal = sharedPreferences.getString(Constants.username, "");


//                    //========>Start VisitActivity
                    GUID guid = GUID.newRandom();
//
//                    Constants.onVisitActivityUpdate(mStrBundleCPGUID32, loginIdVal,
//                            guid.toString36().toUpperCase(), Constants.Secondary_Invoice_Type, Constants.Secondary_Invoice);


//                    Set<String> set = new HashSet<>();

                    ArrayList<HashMap<String, String>> al1Objects = new ArrayList<HashMap<String, String>>();

                    dbHeaderTable = new Hashtable();
                    Hashtable dbItemTable = new Hashtable();

                    dbHeaderTable.put(Constants.InvoiceNo, doc_no);
                    dbHeaderTable.put(Constants.InvoiceGUID, guid.toString());
                    dbHeaderTable.put(Constants.LoginID, loginIdVal);
//                dbHeaderTable.put(Constants.InvoiceTypeID, "02");
                    dbHeaderTable.put(Constants.InvoiceTypeID, "01");
                    dbHeaderTable.put(Constants.InvoiceDate, UtilConstants.getNewDateTimeFormat());
//                dbHeaderTable.put(Constants.CPNo, mStrBundleRetID);
//                dbHeaderTable.put(Constants.CPGUID, mStrBundleCPGUID32.toUpperCase());
                    dbHeaderTable.put(Constants.CPNo, mArraySPValues[1][0]);
//                dbHeaderTable.put(Constants.CPGUID, mStrBundleCPGUID32.toUpperCase());
                    dbHeaderTable.put(Constants.CPGUID, mArraySPValues[1][0]);
                    dbHeaderTable.put(Constants.Currency, getString(R.string.lbl_inr));


                    dbHeaderTable.put(Constants.SoldToID, mStrBundleRetID);
                    dbHeaderTable.put(Constants.NetAmount, totalNetAmout + "");
                    Constants.InvoiceTotalAmount = totalNetAmout;
//                dbHeaderTable.put(Constants.CPTypeID, Constants.str_02);
                    dbHeaderTable.put(Constants.CPTypeID, mArraySPValues[9][0]);
                    dbHeaderTable.put(Constants.SPGuid, mArraySPValues[4][0].toUpperCase());

                    dbHeaderTable.put(Constants.SoldToCPGUID, mStrCpGuid36.toUpperCase());
                    dbHeaderTable.put(Constants.SoldToTypeID, mStrRetailerCpType);
                    dbHeaderTable.put(Constants.SPNo, mArraySPValues[6][0]);

                    dbHeaderTable.put(Constants.TestRun, "");
                    dbHeaderTable.put(Constants.EntityType, Constants.SSInvoice);

                    dbHeaderTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
                    final Calendar calCurrentTime = Calendar.getInstance();
                    int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                    int minute = calCurrentTime.get(Calendar.MINUTE);
                    int second = calCurrentTime.get(Calendar.SECOND);
                    ODataDuration oDataDuration = null;
                    try {
                        oDataDuration = new ODataDurationDefaultImpl();
                        oDataDuration.setHours(hourOfDay);
                        oDataDuration.setMinutes(minute);
                        oDataDuration.setSeconds(BigDecimal.valueOf(second));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dbHeaderTable.put(Constants.CreatedAt, oDataDuration.toString());

                    invGUID32 = guid.toString().replace("-", "");

                    String invCreatedOn = UtilConstants.getNewDateTimeFormat();
                    String invCreatedAt = oDataDuration.toString();

                    mStrDateTime = UtilConstants.getReArrangeDateFormat(invCreatedOn) + "T" + UtilConstants.convertTimeOnly(invCreatedAt);


                    dbHeaderTable.put(Constants.Status, "");

//                    Gson gson = new Gson();

//                    try {
//                        String jsonFromMap = gson.toJson(arrtable);
//
//                        dbHeaderTable.put(Constants.strITEMS, jsonFromMap);
//
//                        jsonFromMap = gson.toJson(hashTableItemSerialNos);
//
//                        dbHeaderTable.put(Constants.ITEMSSerialNo, jsonFromMap);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

//                    set = sharedPreferences.getStringSet(Constants.InvList, null);
//
//                    HashSet<String> setTemp = new HashSet<>();
//                    if (set != null && !set.isEmpty()) {
//                        Iterator itr = set.iterator();
//                        while (itr.hasNext()) {
//                            setTemp.add(itr.next().toString());
//                        }
//                    }
//                    setTemp.add(doc_no);
//
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putStringSet(Constants.InvList, setTemp);
//                    editor.commit();
//
//                    JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);
//
//
//                    LogonCore.getInstance().addObjectToStore(doc_no, jsonHeaderObject.toString());
//
//                    try {
//                        OfflineManager.createVisitActivity(visitActivityTable);
//                    } catch (OfflineODataStoreException e) {
//                        e.printStackTrace();
//                    }
//
//                    navigateToVisitTemp();
                    onReview();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else {
            UtilConstants.showAlert(getString(R.string.validation_app_time_incorrect), InvoiceCreateActivity.this);
        }

    }

    private void onReview() {
        Intent intentInvReview = new Intent(InvoiceCreateActivity.this, InvoiceReviewActivity.class);
        intentInvReview.putExtra(Constants.CPNo, mStrBundleRetID);
        intentInvReview.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentInvReview.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentInvReview.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
        intentInvReview.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
        intentInvReview.putExtra(Constants.comingFrom, mStrComingFrom);
        intentInvReview.putExtra(Constants.InvoiceItemList, arrtable);
        startActivity(intentInvReview);
    }

    /*Displays error message*/
    public void displayError(String errorMessage) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(
                InvoiceCreateActivity.this, R.style.MyTheme);
        dialog.setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        dialog.show();
    }

    @Override
    public void onRequestError(int operation, Exception e) {

        if (operation == Operation.GetRequest.getValue()) {

            try {
                pdLoadDialog.dismiss();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            displayError(e.getMessage() + "");
        } else if (operation == Operation.OfflineFlush.getValue()) {

            try {
//                OfflineManager.refreshRequests(getApplicationContext(), Constants.SPStockItemDetails + "," + Constants.SPStockItemSNos + ","
//                        + Constants.SSINVOICES + "," + Constants.SSInvoiceItemDetails + "," + Constants.SPStockItems + "," + Constants.VisitActivities, InvoiceCreateActivity.this);
                OfflineManager.refreshRequests(getApplicationContext(), Constants.SPStockItemDetails + "," + Constants.CPStockItemSnos + ","
                        + Constants.SSINVOICES + "," + Constants.SSInvoiceItemDetails + "," + Constants.CPStockItems + "," + Constants.VisitActivities, InvoiceCreateActivity.this);
            } catch (OfflineODataStoreException e1) {
                e1.printStackTrace();
            }

        } else if (operation == Operation.OfflineRefresh.getValue()) {
            navigateToPrevScreen();
        }


    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {

        if (operation == Operation.Create.getValue()) {

            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet(Constants.InvList, null);
            if (set != null) {
                if (!set.isEmpty()) {
                    set.remove(doc_no);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putStringSet(Constants.InvList, set);
                    editor.commit();
                }
            }
            String store = null;
            try {
                LogonCore.getInstance().addObjectToStore(doc_no, "");
            } catch (LogonCoreException e) {
                e.printStackTrace();
            }


        } else if (operation == Operation.GetRequest.getValue()) {
            pdLoadDialog.dismiss();

            boolean matPriceError = false;
            if (Constants.MaterialNetAmount > 0) {
                matPriceError = true;
            } else {
                matPriceError = false;
            }

            if (matPriceError) {
                Intent intentInvSelectionActivity = new Intent(InvoiceCreateActivity.this,
                        SerialNoSelectionActivity.class);
                intentInvSelectionActivity.putExtra(Constants.SPStockItemGUID, mStrSpStockItemGuid);
                intentInvSelectionActivity.putExtra(Constants.InvoiceQty, mapEnteredTextsHashMap.get(mStrSpStockItemGuid) != null ?
                        mapEnteredTextsHashMap.get(mStrSpStockItemGuid) : "0");
                intentInvSelectionActivity.putExtra(Constants.PassedFrom, 100);
                startActivityForResult(intentInvSelectionActivity, 100);
            } else {
                displayError(getString(R.string.alert_unit_price_not_maintained_for_sel_material));
            }


        } else if (operation == Operation.OfflineRefresh.getValue()) {
            navigateToPrevScreen();
        } else if (operation == Operation.OfflineFlush.getValue()) {

            if (!UtilConstants.isNetworkAvailable(InvoiceCreateActivity.this)) {
                navigateToPrevScreen();
            } else {
//                OfflineManager.refreshRequests(getApplicationContext(), Constants.SPStockItemDetails + "," + Constants.SPStockItemSNos + ","
//                        + Constants.SSINVOICES + "," + Constants.SSInvoiceItemDetails + "," + Constants.SPStockItems + "," + Constants.VisitActivities + "," + Constants.RetailerSummarySet, InvoiceCreateActivity.this);
                OfflineManager.refreshRequests(getApplicationContext(), Constants.SPStockItemDetails + "," + Constants.CPStockItemSnos + ","
                        + Constants.SSINVOICES + "," + Constants.SSInvoiceItemDetails + "," + Constants.CPStockItems + "," + Constants.VisitActivities + "," + Constants.RetailerSummarySet, InvoiceCreateActivity.this);
            }
        }

    }

    /*Navigate to Retailer detail*/
    public void navigateToPrevScreen() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        popUpText = "Retailer invoice # " + Constants.InvoiceNumber + " created." +
                "Do you want collection transaction.";

        AlertDialog.Builder builder = new AlertDialog.Builder(
                InvoiceCreateActivity.this, R.style.MyTheme);
        builder.setMessage(popUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {

                                    Dialog.cancel();

                                    Intent intentCollCreate = new Intent(InvoiceCreateActivity.this, CollectionCreateActivity.class);
                                    intentCollCreate.putExtra(Constants.CPNo, mStrBundleRetID);
                                    intentCollCreate.putExtra(Constants.CPUID, mStrBundleRetailerUID);
                                    intentCollCreate.putExtra(Constants.RetailerName, mStrBundleRetName);
                                    intentCollCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                                    intentCollCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
                                    intentCollCreate.putExtra(Constants.comingFrom, mStrComingFrom);
                                    intentCollCreate.putExtra(Constants.CurrentInvoice, Constants.InvoiceTotalAmount);
                                    startActivity(intentCollCreate);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        })

                .setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        onNavigateToRetDetilsActivity();
                    }

                });
        builder.show();
    }

    public void navigateToVisitTemp() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        popUpText = "Invoice " + doc_no + " created successfully." +
                "Do you want collection transaction.";

        AlertDialog.Builder builder = new AlertDialog.Builder(
                InvoiceCreateActivity.this, R.style.MyTheme);
        builder.setMessage(popUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {

                                    Dialog.cancel();
                                    Intent intentCollCreate = new Intent(InvoiceCreateActivity.this, CollectionCreateActivity.class);
                                    intentCollCreate.putExtra(Constants.CPNo, mStrBundleRetID);
                                    intentCollCreate.putExtra(Constants.CPUID, mStrBundleRetailerUID);
                                    intentCollCreate.putExtra(Constants.RetailerName, mStrBundleRetName);
                                    intentCollCreate.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                                    intentCollCreate.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
                                    intentCollCreate.putExtra(Constants.comingFrom, mStrComingFrom);
                                    intentCollCreate.putExtra("CurrentInvoice", Constants.InvoiceTotalAmount);
                                    startActivity(intentCollCreate);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })

                .setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        onNavigateToRetDetilsActivity();
                    }

                });
        builder.show();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_create_invoice).setCancelable(false)
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

    private void onNoNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                InvoiceCreateActivity.this, R.style.MyTheme);
        builder.setMessage(
                R.string.alert_sync_cannot_be_performed)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intentInvSelectionActivity = new Intent(InvoiceCreateActivity.this,
                                SerialNoSelectionActivity.class);
                        intentInvSelectionActivity.putExtra(Constants.SPStockItemGUID, mStrSpStockItemGuid);
                        intentInvSelectionActivity.putExtra(Constants.InvoiceQty, mapEnteredTextsHashMap.get(mStrSpStockItemGuid) != null ?
                                mapEnteredTextsHashMap.get(mStrSpStockItemGuid) : "0");
                        intentInvSelectionActivity.putExtra(Constants.PassedFrom, 100);
                        startActivityForResult(intentInvSelectionActivity, 100);
                    }
                });

        builder.show();
    }

    @Override
    public void onError(IRequest arg0, IResponse aResponse,
                        IRequestStateElement arg2) {
        mError++;
        try {


            Constants.parser = Constants.mApplication.getParser();

            IODataError errResponse = null;
            HttpResponse response = aResponse;
            if (aResponse != null) {
                try {
                    HttpEntity responseEntity = aResponse.getEntity();
                    String responseString = EntityUtils.toString(responseEntity);
                    responseString = responseString.replace(getString(R.string.Bad_Request), "");

                    errResponse = Constants.parser.parseODataError(responseString);

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();

                } catch (IllegalStateException e) {
                    e.printStackTrace();

                } catch (ParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            String errorMsg = errResponse.getMessage() != null ? errResponse.getMessage() : "";

            LogManager.writeLogError(getString(R.string.Error_in_Retailer_invoice) + " : " + errorMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mHandler.post(mUpdateResults);
    }

    @Override
    public void onSuccess(IRequest aRequest, IResponse response) {

        try {

            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet(Constants.InvList, null);
            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }

            setTemp.remove(doc_no);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.InvList, setTemp);
            editor.commit();

            try {
                LogonCore.getInstance().addObjectToStore(doc_no, "");
            } catch (LogonCoreException e) {
                e.printStackTrace();
            }
//            }

            String repData = EntityUtils.toString(response.getEntity());

            int repStInd = repData.toString().indexOf("<d:InvoiceNo>") + 13;
            int repEndInd = repData.toString().indexOf("</d:InvoiceNo>");
            Constants.InvoiceNumber = repData.substring(repStInd, repEndInd);

        } catch (Exception e) {
            e.printStackTrace();
        }

        popUpText = "Retailer invoice # " + Constants.InvoiceNumber + " created";

        LogManager.writeLogInfo(popUpText);

        mHandler.post(mUpdateResults);
    }

    @SuppressWarnings("deprecation")
    protected void updateResultsInUi() {

        if (mError == 0) {

            if (!UtilConstants.isNetworkAvailable(InvoiceCreateActivity.this)) {
                pdLoadDialog.dismiss();
                onNoNetwork();
            } else {
                try {
                    OfflineManager.flushQueuedRequests(InvoiceCreateActivity.this);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        } else {

            try {
                pdLoadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

            AlertDialog alertDialog = new AlertDialog.Builder(
                    InvoiceCreateActivity.this).create();
            alertDialog.setMessage(!Constants.ErrorMsg.equalsIgnoreCase("") ? Constants.ErrorMsg
                    : getString(R.string.error_occured_during_post));
            alertDialog.setButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    onNavigateToRetDetilsActivity();
                }
            });
            alertDialog.show();


        }
    }

    /*gets Total invoice amount*/
    private void getTotalInvAmount() {
        double mDouInvPrice = 0.0, mdoubleUnitPrice = 0.0, mDouQty = 0.0;
        if (!mapEnteredTextsHashMap.isEmpty()) {
            Iterator mapSelctedValues = mapEnteredTextsHashMap.keySet()
                    .iterator();
            while (mapSelctedValues.hasNext()) {
                String Key = (String) mapSelctedValues.next();
                try {
                    mDouQty = Double.parseDouble(mapEnteredTextsHashMap.get(Key).equalsIgnoreCase("") ? "0" : mapEnteredTextsHashMap.get(Key));
                    if (mapUnitPriceHashMap.containsKey(Key)) {
                        mdoubleUnitPrice = Double.parseDouble(mapUnitPriceHashMap.get(Key).equalsIgnoreCase("") ? "0" : mapUnitPriceHashMap.get(Key));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                mDouInvPrice = mDouInvPrice + (mDouQty * mdoubleUnitPrice);
            }
        }

        tv_total_order_value.setText(getString(R.string.str_rupee_symbol) + " " + Constants.removeLeadingZero(mDouInvPrice + ""));
    }

    /*Navigates to Retailer Details*/
    private void onNavigateToRetDetilsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(InvoiceCreateActivity.this, RetailersDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        startActivity(intentNavPrevScreen);
    }

    public boolean checkSerialNo(String spStockItemGuid) {
        String mStrVisitStartedQry = Constants.CPStockItems
                + "?$filter=" + Constants.CPStockItemGUID + " eq guid'"
                + spStockItemGuid.toUpperCase() + "' and " + Constants.BatchOrSerial + " eq 'X'";
        try {
            // ToDo serial numbers available or not validation
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)) {
                return true;
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return false;
    }
}
