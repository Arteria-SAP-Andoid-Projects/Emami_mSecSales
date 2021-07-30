package com.arteriatech.emami.retailerStock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.sampleDisbursement.SampleDisbursementDialogAdapter;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.xscript.core.GUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;


public class RetailerStockEntry extends AppCompatActivity implements UIListener, KeyboardView.OnKeyboardActionListener {
    private static int lastSelectedEditText = 0;
    TextView tvAsOnDateView;
    ArrayList<RetailerStockBean> retailerCrsList;
    ArrayList<RetailerStockBean> filteredCrsList;
    int incrementVal = 0;
    //TODO
    KeyboardView keyboardView;
    Keyboard keyboard;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "", mStrBundleCPGUID32 = "";
    private String mStrBundleRetailerUID = "", mStrComingFrom = "";
    private LinearLayout llDelStockLayout;
    private EditText[] edEnterQty;
    private EditText etRetailerSkuSearch;
    private Boolean flag = true, mBoolFirstTime = false;
    private ArrayList<String> deletedItems;
    private int dataAddedCount = 0;
    private ArrayList<RetailerStockBean> distStockList = new ArrayList<>();
    private ArrayList<RetailerStockBean> retailerStockBeanPopupArrayList = new ArrayList<>();
    private ArrayList<RetailerStockBean> retailerHeaderList = new ArrayList<>();
    private PopupWindow popwind;
    private HashMap<String, RetailerStockBean> mapRetStock = new HashMap<>();
    private ProgressDialog pdLoadDialog;
    private MenuItem menuItem = null;
    private String mStrAsOnDate = "";
    private RelativeLayout llforPopUp;
    private TextView dialogEmptyTextView;
    private RecyclerView dialogRecyclerView;
    private SampleDisbursementDialogAdapter sampleDisbursementDialogAdapter;
    private RelativeLayout viewGroupDialogSet;
    private TextView tvActionBarTitle;
    private EditText edSearch;
    private String[][] mArraySPValues=null;
    private String mStrVisitActRefID = "",LoginIDVal = "";
    private Button btn_ok,btn_cancel;
    boolean mBoolBtnOKSel =false;
    private String[][] mArrayCPDMSDivisoins=null;
    private String[][] mArrayDistributors = null;
    private String stockOwner = "";
    private String typevalue="";
    TextView tv_crs_sku_heading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_stock_entry);
        if (!Constants.restartApp(RetailerStockEntry.this)) {
            tvActionBarTitle = ActionBarView.initActionBarReturnView(this, true, getString(R.string.lbl_retailer_stock_entry));
            viewGroupDialogSet = (RelativeLayout) findViewById(R.id.relative_layout_spinner);
            Bundle bundleExtras = getIntent().getExtras();
            this.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            if (bundleExtras != null) {
                mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
                mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
                mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
                mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
                mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
                mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            }
            getLastSyncDate();
            initializeKeyboardDependencies();
            typevalue=Constants.getTypesetValueForSkugrp(RetailerStockEntry.this);
            initUI();


            new GetRetailerList().execute();

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
            createCustomLayoutView();
        }
    }

    //Filter data values as per the Sku search
    private void filterDataValues(CharSequence prefix) {

        if (prefix == null || prefix.length() == 0) {
            filteredCrsList = retailerCrsList;
        } else {
            {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<RetailerStockBean> filteredItems = new ArrayList<>();
                int count = retailerCrsList.size();

                for (int i = 0; i < count; i++) {
                    RetailerStockBean item = retailerCrsList.get(i);
                    String mStrRetName = item.getOrderMaterialGroupDesc().toLowerCase();
                    if (mStrRetName.contains(prefixString)) {
                        filteredItems.add(item);
                    }
                }
                filteredCrsList = filteredItems;
            }
        }
        displayCRSStockValues();
    }

    public void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(RetailerStockEntry.this, R.xml.ll_with_out_dot_inc_dec_up_down);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /*get cpdms divisions value*/
    private void getCPDMSDivisions() {
        mArrayCPDMSDivisoins = Constants.getDMSDivisionByCPGUID(mStrBundleCPGUID);
    }
    /**
     * get salesPerson values
     */
    private void getSalesPersonValues() {
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(mStrBundleCPGUID);
    }

    //display the list values in the view
    private void displayCRSStockValues() {
        if (!flag) {
            llDelStockLayout.removeAllViews();
        }
        flag = false;

        final TableLayout tableHeading = (TableLayout) LayoutInflater.from(this)
                .inflate(R.layout.retailer_stock_table_view, null);
        int cursorLength = filteredCrsList.size();
        TextView[] tvMaterialDesc = new TextView[cursorLength];
        TextView[] tvMaterialStockQty = new TextView[cursorLength];
        ImageButton[] ibDeleteItem = new ImageButton[cursorLength];
        edEnterQty = new EditText[cursorLength];
        if (cursorLength > 0) {

            for (int i = 0; i < cursorLength; i++) {
                final int selvalue = i;
                LinearLayout rowRelativeLayout = (LinearLayout) LayoutInflater
                        .from(this).inflate(R.layout.dealer_stock_list, null);

                incrementVal = i;
                tableHeading.setTag(i);
                tvMaterialDesc[i] = (TextView) rowRelativeLayout.findViewById(R.id.tvQuantityHeading);
                tvMaterialDesc[i].setText(filteredCrsList.get(i).getOrderMaterialGroupDesc());

                tvMaterialStockQty[i] = (TextView) rowRelativeLayout.findViewById(R.id.tvProdQuntyView);


                if (!mBoolFirstTime) {
                    mapRetStock.put(filteredCrsList.get(i).getOrderMaterialGroupID(), filteredCrsList.get(i));
                }

                if(!filteredCrsList.get(i).getUom().equalsIgnoreCase(""))
                    tvMaterialStockQty[i].setText(filteredCrsList.get(i).getQAQty()+" "+filteredCrsList.get(i).getUom());
                else
                    tvMaterialStockQty[i].setText(filteredCrsList.get(i).getQAQty());


                edEnterQty[i] = (EditText) rowRelativeLayout.findViewById(R.id.editTextQuantityView);
                edEnterQty[i].setTag(i);
                edEnterQty[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                edEnterQty[i].setCursorVisible(true);
                edEnterQty[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if (hasFocus) {
                            Constants.setCursorPosition(edEnterQty[selvalue]);
                            lastSelectedEditText = selvalue;
                            Constants.showCustomKeyboard(v, keyboardView, RetailerStockEntry.this);
                        } else {
                            lastSelectedEditText = selvalue;
                            Constants.hideCustomKeyboard(keyboardView);
                        }

                    }
                });
                edEnterQty[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        lastSelectedEditText = selvalue;
                        Constants.setCursorPosition(edEnterQty[selvalue]);
                        v.requestFocus();
                        Constants.showCustomKeyboard(v, keyboardView, RetailerStockEntry.this);
                        Constants.setCursorPostion(edEnterQty[selvalue],v,event);
                        return true;
                    }
                });


                // TOdo next time will useful
//                UtilConstants.editTextDecimalFormat(edEnterQty[i],10,3);
                if (filteredCrsList.get(i).getNewStockValue() != null)
                    edEnterQty[i].setText(filteredCrsList.get(i).getNewStockValue());

                final RetailerStockBean retailerStockBean = filteredCrsList.get(incrementVal);

                edEnterQty[i].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        retailerStockBean.setNewStockValue(s.toString());


                    }
                });


                ibDeleteItem[i] = (ImageButton) rowRelativeLayout.findViewById(R.id.ib_delete_item);
                ibDeleteItem[i].setTag(i);


                ibDeleteItem[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteItem(v.getTag() + "",retailerStockBean);

                    }
                });


                rowRelativeLayout.setId(i);
                if (!deletedItems.contains(filteredCrsList.get(incrementVal).getMaterialNo()))
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

    private void addStockToEntry(ArrayList<RetailerStockBean> retailerHeaderList) {

            filteredCrsList.addAll(retailerHeaderList);
        displayCRSStockValues();

    }

    //Delete the item from the list
    private void deleteItem(String itemId, final RetailerStockBean retailerStockBean) {
        final int selectedID = Integer.parseInt(itemId);
        final String selectedStockName = filteredCrsList.get(selectedID).getOrderMaterialGroupDesc();
        final String selectedStockID = filteredCrsList.get(selectedID).getOrderMaterialGroupID();
        AlertDialog.Builder builder = new AlertDialog.Builder(RetailerStockEntry.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.do_want_to_delete_retailer_stock, selectedStockName)).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        try {
                            mapRetStock.remove(selectedStockID);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        filteredCrsList.remove(selectedID);
                        retailerStockBean.setSelected(false);
                        retailerStockBean.setNewStockValue("");
                        if(!retailerStockBean.getStockType().equals("Dist") && !distStockList.contains(retailerStockBean)){
                            distStockList.add(retailerStockBean);
                        }
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

    private void initUI() {
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        retailerCrsList = new ArrayList<>();
        filteredCrsList = new ArrayList<>();
        deletedItems = new ArrayList<>();
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);

        tv_crs_sku_heading=(TextView) findViewById(R.id.tv_crs_sku_heading);
        tvAsOnDateView = (TextView) findViewById(R.id.tvAsOnDateView);
        etRetailerSkuSearch = (EditText) findViewById(R.id.et_retiler_sku_search);
        llDelStockLayout = (LinearLayout) findViewById(R.id.llDealerStockCreate);
        if (mStrAsOnDate.equalsIgnoreCase("")) {
            tvAsOnDateView.setText(getString(R.string.msg_as_on));
        } else {
            tvAsOnDateView.setText(getString(R.string.msg_as_on) + " " + UtilConstants.convertDateIntoDeviceFormat(this, mStrAsOnDate));
        }
        getSalesPersonValues();
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);

        if(typevalue.equalsIgnoreCase(Constants.SKUGROUP)){
            tv_crs_sku_heading.setText("SKU Group Description");
            etRetailerSkuSearch.setHint(R.string.lbl_Search_by_skugroupdesc);
        }else{
            tv_crs_sku_heading.setText("CRS SKU Group Description");
            etRetailerSkuSearch.setHint(R.string.lbl_Search_by_crsskugroupdesc);
        }

    }

    //get The Crs stock items from the CPStockItems
    private void getCRSStockItems() {
        String mStrMyStockQry = Constants.CPStockItems + "?$orderby="+Constants.OrderMaterialGroupDesc+" &$filter=" + Constants.CPGUID + " eq '" + mStrBundleCPGUID32 + "'" +
                " and " + Constants.StockOwner + " eq '02' ";

        try {
            retailerCrsList = OfflineManager.getRetailerStockList(mStrMyStockQry);
            filteredCrsList = retailerCrsList;
        } catch (OfflineODataStoreException e) {
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

    private void getDistributorStock() {
        try {
            distStockList.clear();
            distStockList = OfflineManager.getDBStockMaterials(Constants.CPStockItems +
                    "?$orderby="+Constants.OrderMaterialGroupDesc+" &$filter=" + Constants.StockOwner + " eq '"+stockOwner+"' and " + Constants.OrderMaterialGroupID + " ne '' ",distStockList,retailerCrsList);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }

    private void getLastSyncDate() {
        try {
            mStrAsOnDate = OfflineManager.getAsOnDate(Constants.CPStockItems +
                    "?$filter=" + Constants.StockOwner + " eq '02' and " + Constants.CPGUID + " eq '" + mStrBundleCPGUID32 + "' and " + Constants.AsOnDate + " ne null &$orderby=" + Constants.AsOnDate + " asc",Constants.AsOnDate);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_retailer_stock, menu);

        menuItem = menu.findItem(R.id.menu_add);

        if (!distStockList.isEmpty()) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                onValid();
                break;
            case R.id.menu_add:
                onAddItems(RetailerStockEntry.this);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void onValid() {
//        if (Constants.isValidTime(UtilConstants.convertTimeOnly(Constants.mStartTimeDuration.toString()),
//                UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString())) && Constants.isValidDate()) {
            if (filteredCrsList.size() > 0) {
                onSave();
            } else {
                UtilConstants.showAlert(getString(R.string.alert_pls_select_atlest_one_stock), RetailerStockEntry.this);

            }
//        }else{
//            UtilConstants.showAlert(getString(R.string.validation_app_time_incorrect), RetailerStockEntry.this);
//        }
    }


    //Add new items to the array list
    private void onAddItems(final Context mContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                retailerStockBeanPopupArrayList.clear();
                for (RetailerStockBean retailerStockBean : distStockList) {
                    if (!retailerStockBean.getSelected()) {
                        retailerStockBeanPopupArrayList.add(retailerStockBean);
                    }
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!retailerStockBeanPopupArrayList.isEmpty()) {
                            //display popup
                            displayDistributorStock(llforPopUp);
                        } else {
                            Constants.dialogBoxWithButton(mContext, "", getString(R.string.sample_disbursement_error_no_data), getString(R.string.ok), "", null);
                        }
                    }
                });
            }
        }).start();

    }

    private void onSave() {
        /*if (Constants.onGpsCheck(RetailerStockEntry.this)) {
            if(UtilConstants.getLocation(RetailerStockEntry.this)) {
                new onCreateRetailerStockAsyncTask().execute();
            }
        }*/
        pdLoadDialog = Constants.showProgressDialog(RetailerStockEntry.this, "", getString(R.string.checking_pemission));
        LocationUtils.checkLocationPermission(RetailerStockEntry.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                closingProgressDialog();
                if (status) {
                    locationPerGranted();
                }
            }
        });
    }

    private void locationPerGranted(){
        pdLoadDialog = Constants.showProgressDialog(RetailerStockEntry.this,"",getString(R.string.gps_progress));
        Constants.getLocation(RetailerStockEntry.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closingProgressDialog();
                if(status){
                    new onCreateRetailerStockAsyncTask().execute();
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
                    LocationUtils.checkLocationPermission(RetailerStockEntry.this, new LocationInterface() {
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

    //Save All stock items in Offline
    private void saveAllStockItems() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");

        boolean mBoolOneTimeSavedVisitAct = false;
        for (int i = 0; i < filteredCrsList.size(); i++) {
            Hashtable<String, String> singleItem = new Hashtable<>();

            singleItem.put(Constants.LOGINID, loginIdVal.toUpperCase());
            singleItem.put(Constants.CPTypeID, Constants.str_02);
            singleItem.put(Constants.CPNo, Constants.getName(Constants.ChannelPartners, Constants.CPNo, Constants.CPUID, mStrBundleRetID));
            singleItem.put(Constants.CPName, mStrBundleRetName);
            singleItem.put(Constants.CPTypeDesc, Constants.getName(Constants.ChannelPartners, Constants.CPTypeDesc, Constants.CPUID, mStrBundleRetID));
            singleItem.put(Constants.OrderMaterialGroupID, filteredCrsList.get(i).getOrderMaterialGroupID());
            singleItem.put(Constants.OrderMaterialGroupDesc, filteredCrsList.get(i).getOrderMaterialGroupDesc());
            singleItem.put(Constants.UOM, filteredCrsList.get(i).getUom());
            singleItem.put(Constants.MRP, !filteredCrsList.get(i).getMRP().equalsIgnoreCase("") ? filteredCrsList.get(i).getMRP() : "0.00");
            singleItem.put(Constants.StockValue, !filteredCrsList.get(i).getStockValue().equalsIgnoreCase("") ? filteredCrsList.get(i).getStockValue() : "0.00");
            singleItem.put(Constants.LandingPrice, !filteredCrsList.get(i).getLandingPrice().equalsIgnoreCase("") ? filteredCrsList.get(i).getLandingPrice() : "0.00");
            singleItem.put(Constants.StockOwner, "02");
            singleItem.put(Constants.AsOnDate, UtilConstants.getNewDateTimeFormat());

            singleItem.put(Constants.DMSDivision, mArrayCPDMSDivisoins[0][0] != null ? mArrayCPDMSDivisoins[0][0] : "");
            singleItem.put(Constants.DmsDivisionDesc, mArrayCPDMSDivisoins[1][0] != null ? mArrayCPDMSDivisoins[1][0] : "");

            if (edEnterQty[i].getText().toString().equals(""))
                singleItem.put(Constants.UnrestrictedQty, "0.0");//!filteredCrsList.get(i).getQAQty().equalsIgnoreCase("") ? filteredCrsList.get(i).getQAQty() : "0.000"
            else
                singleItem.put(Constants.UnrestrictedQty, !edEnterQty[i].getText().toString().equalsIgnoreCase("") ? edEnterQty[i].getText().toString() : "0.000");


            singleItem.put(Constants.Currency, filteredCrsList.get(i).getCurrency());

            singleItem.put(Constants.Etag, filteredCrsList.get(i).getEtag());


            if (filteredCrsList.get(i).getStockType().equalsIgnoreCase("Dist")) {


                GUID guid = GUID.newRandom();


                singleItem.put(Constants.CPGUID, mStrBundleCPGUID32);
                singleItem.put(Constants.CPStockItemGUID, guid.toString36().toUpperCase());
                try {

                    OfflineManager.createCPStockItems(singleItem, this);

                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                if (!mBoolOneTimeSavedVisitAct) {
                    mStrVisitActRefID = guid.toString36().toUpperCase();
                    LoginIDVal = sharedPreferences.getString(Constants.username, "");
                    mBoolOneTimeSavedVisitAct = true;
                }
            } else {



                singleItem.put(Constants.CPStockItemGUID, filteredCrsList.get(i).getCPStockItemGUID());
                singleItem.put(Constants.CPGUID, mStrBundleCPGUID32);
                try {

                    OfflineManager.updateCPStockItems(singleItem, this);

                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                if (!mBoolOneTimeSavedVisitAct) {
                    mStrVisitActRefID = filteredCrsList.get(i).getCPStockItemGUID().toUpperCase();
                    LoginIDVal = sharedPreferences.getString(Constants.username, "");
                    mBoolOneTimeSavedVisitAct = true;
                }
            }


        }

    }


    @Override
    public void onRequestError(int i, Exception e) {

        Constants.customAlertMessage(this, e.getMessage());
        closingProgressDialog();
    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue()) {
            if (++dataAddedCount == filteredCrsList.size()) {
                Constants.onVisitActivityUpdate(mStrBundleCPGUID32, LoginIDVal,
                        mStrVisitActRefID, Constants.RetailerStockID, Constants.RetailerStock);
                backToPrevScreenDialog();
            }
        } else if (operation == Operation.Update.getValue()) {
            if (++dataAddedCount == filteredCrsList.size()) {
                Constants.onVisitActivityUpdate(mStrBundleCPGUID32, LoginIDVal,
                        mStrVisitActRefID, Constants.RetailerStockID, Constants.RetailerStock);
                backToPrevScreenDialog();

            }
        }
    }

    private void closingProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Navigate to previous screen dialog*/
    private void backToPrevScreenDialog() {
        closingProgressDialog();

        AlertDialog.Builder builder = new AlertDialog.Builder(
                RetailerStockEntry.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.msg_ret_stock_created))
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    onNavigateToRetDetilsActivity();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();
    }

    @SuppressWarnings("deprecation")
    private void displayDistributorStock(RelativeLayout layout) {

        tvActionBarTitle.setText(getString(R.string.lbl_select_order_mat_grp));
        ActionBarView.initActionBarReturnView(this, false, getString(R.string.lbl_select_order_mat_grp));
        popwind = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        popwind.setBackgroundDrawable(new BitmapDrawable());
        popwind.setTouchable(true);

        popwind.setOutsideTouchable(false);
        popwind.setHeight(ActionBar.LayoutParams.MATCH_PARENT);
        popwind.setContentView(layout);

        mBoolBtnOKSel =false;
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBoolBtnOKSel = true;
                boolean mboolAtLeastSel = false;
                retailerHeaderList.clear();
                for (RetailerStockBean retailerStockBean : retailerStockBeanPopupArrayList) {
                    if (retailerStockBean.getSelected()) {

                        retailerHeaderList.add(retailerStockBean);
                        if (!mboolAtLeastSel) {
                            mboolAtLeastSel = true;
                        }
                    }
                }
                if (mboolAtLeastSel) {
                    ActionBarView.initActionBarView(RetailerStockEntry.this, true, getString(R.string.lbl_retailer_stock_entry));
                    popwind.dismiss();
                    addStockToEntry(retailerHeaderList);
                }
//                tvActionBarTitle.setText(getString(R.string.lbl_retailer_stock_entry));
                ActionBarView.initActionBarReturnView(RetailerStockEntry.this, false, getString(R.string.lbl_retailer_stock_entry));


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (RetailerStockBean retailerStockBean : retailerStockBeanPopupArrayList) {
                    if (retailerStockBean.getSelected()) {
                        retailerStockBean.setSelected(false);
                    }
                }
                popwind.dismiss();
                ActionBarView.initActionBarReturnView(RetailerStockEntry.this, false, getString(R.string.lbl_retailer_stock_entry));
            }
        });

        popwind.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(!mBoolBtnOKSel) {
                    for (RetailerStockBean retailerStockBean : retailerStockBeanPopupArrayList) {
                        if (retailerStockBean.getSelected()) {
                            retailerStockBean.setSelected(false);
                        }
                    }
                    popwind.dismiss();
                    ActionBarView.initActionBarReturnView(RetailerStockEntry.this, false, getString(R.string.lbl_retailer_stock_entry));
                }


            }
        });
        edSearch.setText("");
        popwind.showAsDropDown(viewGroupDialogSet);
        sampleDisbursementDialogAdapter.filter("", dialogEmptyTextView, dialogRecyclerView);

    }


    @Override
    public void onBackPressed() {

        if (Constants.isCustomKeyboardVisible(keyboardView)) {
            Constants.hideCustomKeyboard(keyboardView);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RetailerStockEntry.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_create_retailer_stock).setCancelable(false)
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

    private Boolean checkAlreadyDotIsThere() {
        ArrayList<EditText> myEditTextList = new ArrayList<EditText>();

        for (int i = 0; i < llDelStockLayout.getChildCount(); i++)
            if (llDelStockLayout.getChildAt(i) instanceof EditText) {
                myEditTextList.add((EditText) llDelStockLayout.getChildAt(i));
                if (myEditTextList.get(i).hasFocus()) {
                    String textValue = myEditTextList.get(i).getText().toString();
                    if (textValue.contains(".")) {
                        return true;
                    } else
                        return false;
                }
            }
        return false;
    }

    private void onNavigateToRetDetilsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(RetailerStockEntry.this, RetailersDetailsActivity.class);
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
                Constants.incrementTextValues(edEnterQty[lastSelectedEditText], Constants.N);
                break;
            case 69:
                //Minus
                Constants.decrementEditTextVal(edEnterQty[lastSelectedEditText], Constants.N);
                break;
            case 1:
                changeEditTextFocus(0);
                break;
            case 2:
                changeEditTextFocus(1);
                break;
            case 56:
                if (!checkAlreadyDotIsThere()) {
                    KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                    dispatchKeyEvent(event);
                }

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
            int ListSize = filteredCrsList.size() - 1;
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

    /*AsyncTask to create retailer*/
    public class onCreateRetailerStockAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(RetailerStockEntry.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.pop_up_msg_retailer_stock));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                saveAllStockItems();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
    /*creating custom layout view*/
    private void createCustomLayoutView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        llforPopUp = (RelativeLayout) inflater.inflate(R.layout.pop_up_window_sample_disb_sel, (ViewGroup) findViewById(R.id.PopUpView));

         /*dialog box view*/
        btn_ok = (Button) llforPopUp.findViewById(R.id.btn_ok);
        btn_cancel = (Button) llforPopUp.findViewById(R.id.btn_cancel);

        dialogEmptyTextView = (TextView) llforPopUp.findViewById(R.id.no_record_found);
        dialogRecyclerView = (RecyclerView) llforPopUp.findViewById(R.id.dialog_recycler_view);
        dialogRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        dialogRecyclerView.setLayoutManager(linearLayoutManager);
        sampleDisbursementDialogAdapter = new SampleDisbursementDialogAdapter(RetailerStockEntry.this, retailerStockBeanPopupArrayList,2);
        dialogRecyclerView.setAdapter(sampleDisbursementDialogAdapter);
        edSearch = (EditText) llforPopUp.findViewById(R.id.et_dbstk_search);
        if(typevalue.equalsIgnoreCase(Constants.SKUGROUP)){
            edSearch.setHint(R.string.lbl_Search_by_skugroupdesc);
        }else{
            edSearch.setHint(R.string.lbl_Search_by_crsskugroupdesc);
        }

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchData = s + "";
                sampleDisbursementDialogAdapter.filter(searchData, dialogEmptyTextView, dialogRecyclerView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
    private class GetRetailerList extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(RetailerStockEntry.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            getDistributorValues();
            getCRSStockItems();
            getCPDMSDivisions();
            getDistributorStock();

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
            displayCRSStockValues();
            if (menuItem != null) {
                if (!distStockList.isEmpty()) {
                    menuItem.setVisible(true);
                } else {
                    menuItem.setVisible(false);
                }
            }
        }
    }
}
