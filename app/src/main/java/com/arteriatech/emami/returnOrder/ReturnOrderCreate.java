package com.arteriatech.emami.returnOrder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

public class ReturnOrderCreate extends AppCompatActivity implements KeyboardView.OnKeyboardActionListener {

    private static int lastSelectedEditText = 0;
    private static int lastSelectedMrpEditText = 0;
    EditText[] edEnterQty;
    EditText[] edEnterMrp;
    EditText[] edEnterBatch;
    LinearLayout ltNoRecords = null;
    ArrayList<ReturnOrderBean> filteredArrayList = new ArrayList<>();
    int incrementVal = 0;
    //TODO
    KeyboardView keyboardView;
    Keyboard keyboard;

    KeyboardView keyboardViewWithDot;
    Keyboard keyboardWithDot;

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "", mStrComingFrom = "";
    private ArrayList<ReturnOrderBean> masterAlStockList = new ArrayList<>();
    private ArrayList<ReturnOrderBean> distStockList = new ArrayList<>();
    private String[][] mArrayReasons;
    private LinearLayout llDelStockLayout;
    private Boolean flag = true, mBoolFirstTime = false;

    private EditText etRetailerSkuSearch;
    private Spinner[] spReason;
    private ProgressDialog pdLoadDialog;
    private MenuItem menuItem = null;
    private RelativeLayout viewGroupDialogSet;
    private String[][] mArrayDistributors = null;
    private String stockOwner = "";
    private String typevalue="";
    TextView tv_sku_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_order_create);
        ActionBarView.initActionBarView(this, true, getString(R.string.title_return_order_create));
        viewGroupDialogSet = (RelativeLayout) findViewById(R.id.relative_layout_spinner);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }
        filteredArrayList.clear();
        if (!Constants.restartApp(ReturnOrderCreate.this)) {
            typevalue=Constants.getTypesetValueForSkugrp(ReturnOrderCreate.this);
            initUI();
            initializeKeyboardDependencies();
            new GetRetailerList(false).execute();
            getReturnOrderList();
            displayCRSStockValues();
        }

    }



    /*get distributor stock*/
    private void getDistributorStock(String querys) {
        try {
            distStockList.clear();
            distStockList = OfflineManager.getReturnOrderList(querys, distStockList);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_return_order_create, menu);
        menuItem = menu.findItem(R.id.menu_return_add);
        if (distStockList != null && distStockList.size() > 0) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_return_add:
                Intent intent = new Intent(ReturnOrderCreate.this, AddReturnOrderActivity.class);
                intent.putExtra(Constants.StockOwner, stockOwner);
                startActivityForResult(intent, AddReturnOrderActivity.RETURN_ORDER_RESULT_ID);
                break;
            case R.id.menu_return_review:
                onReviewScreen();
                break;
        }
        return false;
    }

    /*keyboard initilization*/
    public void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(ReturnOrderCreate.this, R.xml.ll_with_out_dot_inc_dec_up_down);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);

        keyboardViewWithDot = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboardWithDot = new Keyboard(ReturnOrderCreate.this, R.xml.ll_plus_minuus_updown_keyboard);
        keyboardViewWithDot.setKeyboard(keyboardWithDot);
        keyboardViewWithDot.setPreviewEnabled(false);
        keyboardViewWithDot.setOnKeyboardActionListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void onReviewScreen() {
//        if (Constants.onGpsCheck(ReturnOrderCreate.this)) {
//            if(UtilConstants.getLocation(ReturnOrderCreate.this)) {
                if (validateListValues()) {
                    Intent intent = new Intent(ReturnOrderCreate.this, ReviewActivity.class);
                    intent.putExtra(Constants.CPNo, mStrBundleRetID);
                    intent.putExtra(Constants.CPUID, mStrBundleRetailerUID);
                    intent.putExtra(Constants.RetailerName, mStrBundleRetName);
                    intent.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
                    intent.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
                    intent.putExtra(Constants.comingFrom, mStrComingFrom);
                    intent.putParcelableArrayListExtra(Constants.EXTRA_ARRAY_LIST, filteredArrayList);
                    startActivity(intent);
                }
//            }
//        }
    }

    /*on save*/

    /*navigation to retailers details activity*/
    private void navigateToRetDetailsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(ReturnOrderCreate.this, RetailersDetailsActivity.class);
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

    /*validate the list*/
    private boolean validateListValues() {
        boolean isValid = true;
        if (filteredArrayList.isEmpty()) {
            Constants.dialogBoxWithButton(this, "", getString(R.string.sample_disbursement_error_one_item), getString(R.string.ok), "", null);
            return false;
        }
        String[] mArrayMaterialList = new String[filteredArrayList.size()];
        int[] duplicateValues = new int[filteredArrayList.size()];
        for (int i = 0; i < filteredArrayList.size(); i++) {
            ReturnOrderBean returnOrderBean = filteredArrayList.get(i);
            String stMrp = edEnterMrp[i].getText().toString()!=null?edEnterMrp[i].getText().toString():"";
            String stQty = edEnterQty[i].getText().toString();
            String stBatch = edEnterBatch[i].getText().toString();
            String stReturnDes = returnOrderBean.getReturnDesc();
            if (stMrp.isEmpty()) {
                edEnterMrp[i].setBackgroundResource(R.drawable.edittext_border);
            }
            if (stMrp.equals(".")) {
                edEnterMrp[i].setBackgroundResource(R.drawable.edittext_border);
            }
            if (stQty.isEmpty()) {
                edEnterQty[i].setBackgroundResource(R.drawable.edittext_border);
            }
            if (stBatch.isEmpty()) {
                edEnterBatch[i].setBackgroundResource(R.drawable.edittext_border);
            }
            if (stReturnDes.isEmpty()) {
                spReason[i].setBackgroundResource(R.drawable.error_spinner);
            } else if (stReturnDes.equalsIgnoreCase("None")) {
                spReason[i].setBackgroundResource(R.drawable.error_spinner);
            }
            mArrayMaterialList[i] = filteredArrayList.get(i).getMaterialNo() + filteredArrayList.get(i).getReturnBatchNumber();
        }
        for (int i = 0; i < mArrayMaterialList.length; i++) {
            for (int j = i + 1; j < mArrayMaterialList.length; j++) {
                if (mArrayMaterialList[i].equals(mArrayMaterialList[j])) {
                    duplicateValues[i] = 1;
                } else
                    duplicateValues[i] = 0;
            }
        }
        for (int k = 0; k < filteredArrayList.size(); k++) {
            String batchCh = filteredArrayList.get(k).getReturnBatchNumber();
            String qtyCh = filteredArrayList.get(k).getReturnQty();
            String mrpCh = filteredArrayList.get(k).getReturnMrp();
            String reasonCh = filteredArrayList.get(k).getReturnReason();
            if (batchCh.equals("") || qtyCh.equals("") || mrpCh.equals("") || reasonCh.equals(Constants.None) || mrpCh.equals(".") || mrpCh.equals("0")) {
                isValid = false;

            }
        }
        if (isValid) {
            for (int duplicateValue : duplicateValues) {
                if (duplicateValue == 1) {
                    isValid = false;
                }
            }
            if (!isValid)
                Constants.customAlertMessage(this, getString(R.string.msg_material_batch_already_exists));
        } else
            Constants.customAlertMessage(this, getString(R.string.validation_plz_enter_mandatory_flds));


        return isValid;

    }

    /*init the UI*/
    private void initUI() {
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        masterAlStockList = new ArrayList<>();
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        etRetailerSkuSearch = (EditText) findViewById(R.id.et_retiler_sku_search);
        tv_sku_desc= (TextView) findViewById(R.id.tv_sku_desc);
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);
        ltNoRecords = (LinearLayout) findViewById(R.id.lay_no_records);
        llDelStockLayout = (LinearLayout) findViewById(R.id.llDealerStockCreate);
        searchText();
        if(typevalue.equalsIgnoreCase(Constants.SKUGROUP)){
            tv_sku_desc.setText("SKU Description");
            etRetailerSkuSearch.setHint(R.string.lbl_Search_by_skugroupdesc);
        }else{
            tv_sku_desc.setText("CRS SKU Description");
            etRetailerSkuSearch.setHint(R.string.lbl_Search_by_skugroupdesc);
        }
    }

    /*search the text*/
    private void searchText() {
        etRetailerSkuSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDataValues(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //Filter data values as per the Sku search
    private void filterDataValues(CharSequence prefix) {

        if (prefix == null || prefix.length() == 0) {
            filteredArrayList = masterAlStockList;
        } else {
            {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<ReturnOrderBean> filteredItems = new ArrayList<>();
                int count = masterAlStockList.size();

                for (int i = 0; i < count; i++) {
                    ReturnOrderBean item = masterAlStockList.get(i);
                    String mStrRetName = item.getMaterialDesc().toLowerCase();
                    if (mStrRetName.contains(prefixString)) {
                        filteredItems.add(item);
                    }
                }
                filteredArrayList = filteredItems;
            }
        }
        displayCRSStockValues();
    }

    /*get return order list*/
    private void getReturnOrderList() {
        try {
            masterAlStockList.clear();
            String mStrMyStockQry = Constants.CPStockItems + "?$orderby="+Constants.Material_Desc+" &$filter=" + Constants.CPGUID + " eq '" + mStrBundleCPGUID.replace("-", "") + "' and " + Constants.MaterialNo + " ne '' ";
            masterAlStockList = OfflineManager.getReturnOrderList(mStrMyStockQry, masterAlStockList);
            filteredArrayList = masterAlStockList;
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }


    }

    /*display main list view*/
    private void displayCRSStockValues() {
        getSelectOrderReasons();
        if (!flag) {
            llDelStockLayout.removeAllViews();
        }
        flag = false;

        final TableLayout tableHeading = (TableLayout) LayoutInflater.from(this)
                .inflate(R.layout.retailer_stock_table_view, null);
        int cursorLength = filteredArrayList.size();
        edEnterQty = new EditText[cursorLength];
        edEnterMrp = new EditText[cursorLength];
        edEnterBatch = new EditText[cursorLength];
        TextView[] tvMaterialDesc = new TextView[cursorLength];
        spReason = new Spinner[cursorLength];
        ImageButton[] ibDeleteItem = new ImageButton[cursorLength];

        if (cursorLength > 0) {

            for (int i = 0; i < cursorLength; i++) {
                final int selvalue = i;
                final LinearLayout rowRelativeLayout = (LinearLayout) LayoutInflater
                        .from(this).inflate(R.layout.item_return_order, null);

                incrementVal = i;
                tableHeading.setTag(i);
                rowRelativeLayout.setTag(i);
                edEnterQty[i] = (EditText) rowRelativeLayout.findViewById(R.id.edit_quantity);


                edEnterBatch[i] = (EditText) rowRelativeLayout.findViewById(R.id.edit_batch_number);
                edEnterMrp[i] = (EditText) rowRelativeLayout.findViewById(R.id.edit_mrp);
                tvMaterialDesc[i] = (TextView) rowRelativeLayout.findViewById(R.id.item_dbstk_sku_desc);
                spReason[i] = (Spinner) rowRelativeLayout.findViewById(R.id.sp_select_reason);


                ArrayAdapter<String> spOrderReasonAdapter = new ArrayAdapter<>(ReturnOrderCreate.this, R.layout.custom_textview, mArrayReasons[1]);
                spOrderReasonAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spReason[i].setAdapter(spOrderReasonAdapter);
                spReason[i].setTag(i);

                edEnterQty[i].setTag(i);
                edEnterBatch[i].setTag(i);
                edEnterMrp[i].setTag(i);

                edEnterQty[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                edEnterQty[i].setCursorVisible(true);

                edEnterQty[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        lastSelectedEditText = selvalue;
                        Constants.setCursorPosition(edEnterQty[selvalue]);
                        v.requestFocus();
                        Constants.showCustomKeyboard(v, keyboardViewWithDot, ReturnOrderCreate.this);
                        Constants.setCursorPostion(edEnterQty[selvalue],v,event);
                        return true;
                    }
                });
                edEnterQty[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus) {
                            int positionTable = Integer.parseInt(view.getTag().toString());
                            Constants.setCursorPosition(edEnterQty[selvalue]);
                            lastSelectedEditText = selvalue;
                            Constants.showCustomKeyboard(view, keyboardViewWithDot, ReturnOrderCreate.this);
                            edEnterQty[positionTable].setHint("");
                        } else {
                            lastSelectedEditText = selvalue;
                            Constants.hideCustomKeyboard(keyboardViewWithDot);
                            int positionTable = Integer.parseInt(view.getTag().toString());
                            edEnterQty[positionTable].setHint(getString(R.string.qty));
                        }


                    }
                });
                UtilConstants.editTextDecimalFormat(edEnterMrp[i], 10, 2);
                edEnterMrp[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        lastSelectedMrpEditText = selvalue;
                        Constants.setCursorPosition(edEnterMrp[selvalue]);
                        v.requestFocus();
                        Constants.showCustomKeyboard(v, keyboardViewWithDot, ReturnOrderCreate.this);
                        Constants.setCursorPostion(edEnterMrp[selvalue],v,event);
                        return true;
                    }
                });
                edEnterMrp[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus) {
                            int positionTable = Integer.parseInt(view.getTag().toString());
                            Constants.setCursorPosition(edEnterMrp[selvalue]);
                            lastSelectedMrpEditText = selvalue;
                            Constants.showCustomKeyboard(view, keyboardViewWithDot, ReturnOrderCreate.this);
                            edEnterMrp[positionTable].setHint("");
                        } else {
                            lastSelectedMrpEditText = selvalue;
                            Constants.hideCustomKeyboard(keyboardViewWithDot);
                            int positionTable = Integer.parseInt(view.getTag().toString());
                            edEnterMrp[positionTable].setHint(getString(R.string.lbl_MRP));
                        }


                    }
                });

                edEnterBatch[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                edEnterBatch[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus) {
                            Constants.hideCustomKeyboard(keyboardView);
                            Constants.hideCustomKeyboard(keyboardViewWithDot);
                            int positionTable = Integer.parseInt(view.getTag().toString());
                            edEnterBatch[positionTable].setHint("");
                        } else {
                            int positionTable = Integer.parseInt(view.getTag().toString());
                            edEnterBatch[positionTable].setHint(getString(R.string.lbl_batch_number));
                        }


                    }
                });

                if (filteredArrayList.get(incrementVal).getReturnDesc() != null) {
                    int spPosition = spOrderReasonAdapter.getPosition(filteredArrayList.get(incrementVal).getReturnDesc());
                    spReason[i].setSelection(spPosition);
                }
                if (filteredArrayList.get(incrementVal).getReturnMrp() != null)
                    edEnterMrp[i].setText(filteredArrayList.get(incrementVal).getReturnMrp());
                if (filteredArrayList.get(incrementVal).getReturnQty() != null)
                    edEnterQty[i].setText(filteredArrayList.get(incrementVal).getReturnQty());
                if (filteredArrayList.get(incrementVal).getReturnBatchNumber() != null)
                    edEnterBatch[i].setText(filteredArrayList.get(incrementVal).getReturnBatchNumber());
                spReason[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View spView, int i, long l) {
                        int positionTable = Integer.parseInt(adapterView.getTag().toString());
                        filteredArrayList.get(positionTable).setReturnReason(mArrayReasons[0][i]);
                        filteredArrayList.get(positionTable).setReturnDesc(mArrayReasons[1][i]);
                        spReason[positionTable].setBackgroundResource(R.drawable.spinner_bg);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                edEnterMrp[i].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        View viewFocus = ReturnOrderCreate.this.getCurrentFocus();

                        int positionTable = Integer.parseInt(viewFocus.getTag().toString());
                        filteredArrayList.get(positionTable).setReturnMrp(editable.toString());
                        if (!editable.toString().isEmpty()) {
                            edEnterMrp[positionTable].setBackgroundResource(R.drawable.edittext);
                        }
                    }
                });
                edEnterQty[i].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        View viewFocus = ReturnOrderCreate.this.getCurrentFocus();

                        int positionTable = Integer.parseInt(viewFocus.getTag().toString());
                        filteredArrayList.get(positionTable).setReturnQty(editable.toString());
                        if (!editable.toString().isEmpty()) {
                            edEnterQty[positionTable].setBackgroundResource(R.drawable.edittext);
                        }
                    }
                });
                edEnterBatch[i].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        View viewFocus = ReturnOrderCreate.this.getCurrentFocus();

                        int positionTable = Integer.parseInt(viewFocus.getTag().toString());

                        filteredArrayList.get(positionTable).setReturnBatchNumber(editable.toString());
                        if (!editable.toString().isEmpty()) {
                            edEnterBatch[positionTable].setBackgroundResource(R.drawable.edittext);
                        }
                    }
                });
                tvMaterialDesc[i].setText(filteredArrayList.get(i).getMaterialDesc());

                ibDeleteItem[i] = (ImageButton) rowRelativeLayout.findViewById(R.id.ib_delete_item);
                ibDeleteItem[i].setTag(i);
                ibDeleteItem[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteItem(v.getTag() + "");

                    }
                });

                rowRelativeLayout.setId(i);
                filteredArrayList.get(i).setDisplayed(true);
                tableHeading.addView(rowRelativeLayout);
            }

            mBoolFirstTime = true;

            llDelStockLayout.addView(tableHeading);

        } else {
            View llEmptyLayout = (View) LayoutInflater.from(this)
                    .inflate(R.layout.empty_layout, null);
            TextView tvNoRecord = (TextView)llEmptyLayout.findViewById(R.id.tv_empty_lay);
            tvNoRecord.setText(getString(R.string.add_product_hint));
            llDelStockLayout.addView(llEmptyLayout);
        }
    }

    //Delete the item from the list
    private void deleteItem(String itemId) {
        final int selectedID = Integer.parseInt(itemId);
        final String selectedStockName = filteredArrayList.get(selectedID).getMaterialDesc();
        final String selectedStockID = filteredArrayList.get(selectedID).getMaterialNo();
        AlertDialog.Builder builder = new AlertDialog.Builder(ReturnOrderCreate.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.do_want_to_delete_return_order, selectedStockName)).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        filteredArrayList.remove(selectedID);
                        displayCRSStockValues();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }

                });
        builder.show();

    }


    /*add stock to entity*/
    private void addStockToEntry() {
        displayCRSStockValues();
    }

    /*get select order reasons*/
    private void getSelectOrderReasons() {
        String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.EntityType
                + " eq 'SSSOItemDetail'&$orderby=" + Constants.DESCRIPTION + " asc";
        try {
            mArrayReasons = OfflineManager.getOrderReasonValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (mArrayReasons == null) {
            mArrayReasons = new String[2][1];
            mArrayReasons[0][0] = Constants.None;
            mArrayReasons[1][0] = Constants.None;

        }

    }

    @Override
    public void onBackPressed() {
        if (Constants.isCustomKeyboardVisible(keyboardView)) {
            Constants.hideCustomKeyboard(keyboardView);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReturnOrderCreate.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_create_return).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            navigateToRetDetailsActivity();
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

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

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


    /*custom keyboard*/
    public void onKey(int primaryCode, int[] keyCodes) {
        switch (primaryCode) {

            case 81:
                //Plus
                if (edEnterQty[lastSelectedEditText].isFocused()) {
                    Constants.incrementTextValues(edEnterQty[lastSelectedEditText], Constants.N);
                } else {
                    Constants.incrementTextValues(edEnterMrp[lastSelectedMrpEditText], Constants.Y);
                }
                break;
            case 69:
                //Minus
                if (edEnterQty[lastSelectedEditText].isFocused()) {
                    Constants.decrementEditTextVal(edEnterQty[lastSelectedEditText], Constants.N);
                } else {
                    Constants.decrementEditTextVal(edEnterMrp[lastSelectedMrpEditText], Constants.Y);
                }
                break;
            case 1:
                if (edEnterQty[lastSelectedEditText].isFocused()) {
                    changeEditTextFocus(0);
                } else {
                    changeMRPEditTextFocus(0);
                }
                break;
            case 2:
                if (edEnterQty[lastSelectedEditText].isFocused()) {
                    changeEditTextFocus(1);
                } else {
                    changeMRPEditTextFocus(1);
                }
                break;
            case 56:
                KeyEvent event2 = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event2);
                break;

            default:
                //default numbers
                KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event);
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

    public void changeEditTextFocus(int upDownStatus) {

        if (upDownStatus == 1) {
            int ListSize = filteredArrayList.size() - 1;
            if (lastSelectedEditText != ListSize) {
                if (edEnterQty[lastSelectedEditText] != null)
                    edEnterQty[lastSelectedEditText + 1].requestFocus();
            }

        } else {
            if (lastSelectedEditText != 0) {
                if (edEnterQty[lastSelectedEditText - 1] != null)
                    edEnterQty[lastSelectedEditText - 1].requestFocus();
            }

        }

    }

    public void changeMRPEditTextFocus(int upDownStatus) {

        if (upDownStatus == 1) {
            int ListSize = filteredArrayList.size() - 1;
            if (lastSelectedMrpEditText != ListSize) {
                if (edEnterMrp[lastSelectedMrpEditText] != null)
                    edEnterMrp[lastSelectedMrpEditText + 1].requestFocus();
            }

        } else {
            if (lastSelectedMrpEditText != 0) {
                if (edEnterMrp[lastSelectedMrpEditText - 1] != null)
                    edEnterMrp[lastSelectedMrpEditText - 1].requestFocus();
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddReturnOrderActivity.RETURN_ORDER_RESULT_ID) {
            if (data != null) {
                ArrayList<ReturnOrderBean> returnOrderBeanArrayList = data.getParcelableArrayListExtra(ConstantsUtils.EXTRA_ARRAY_LIST);
                if (!returnOrderBeanArrayList.isEmpty()) {
                    filteredArrayList.addAll(returnOrderBeanArrayList);
                    addStockToEntry();
                }
            }
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
    private class GetRetailerList extends AsyncTask<String, Void, Void> {
        private boolean reloadView = false;

        private GetRetailerList(boolean reloadView) {
            this.reloadView = reloadView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(ReturnOrderCreate.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            getDistributorValues();
            String query = Constants.CPStockItems + "?$orderby="+Constants.Material_Desc+" &$filter=" + Constants.StockOwner + " eq '"+stockOwner+"' and " + Constants.MaterialNo + " ne '' ";
            getDistributorStock(query);
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
            if (menuItem != null) {
                if (distStockList != null && distStockList.size() > 0) {
                    menuItem.setVisible(true);
                } else {
                    menuItem.setVisible(false);
                }
            }
        }
    }

}