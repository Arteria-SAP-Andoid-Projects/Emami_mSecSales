package com.arteriatech.emami.sampleDisbursement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.dbstock.DMSDivionBean;
import com.arteriatech.emami.interfaces.DialogCallBack;
import com.arteriatech.emami.interfaces.FocusOnTextChangeInterface;
import com.arteriatech.emami.interfaces.OnClickInterface;
import com.arteriatech.emami.interfaces.TextWatcherInterface;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.retailerStock.RetailerStockBean;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.ui.CustomDividerItemDecoration;
import com.arteriatech.emami.visit.VisitFragment;
import com.sap.xscript.core.GUID;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class SampleDisbursementActivity extends AppCompatActivity implements TextWatcherInterface, OnClickInterface, KeyboardView.OnKeyboardActionListener, FocusOnTextChangeInterface {

    //TODO
    KeyboardView keyboardView;
    Keyboard keyboard;
    private RecyclerView recyclerView;
    private List<RetailerStockBean> retailerStockBeanArrayList = new ArrayList<>();
    private List<RetailerStockBean> retailerStockBeanTotalArrayList = new ArrayList<>();
    private ArrayList<RetailerStockBean> retailerStockBeanPopupArrayList = new ArrayList<>();
    private Hashtable<String, String> headerTable = new Hashtable<>();
    private SampleDisbursementAdapter sampleDisbursementAdapter;
    private TextView tvNoRecordFound;
    private String mStrBundleCPGUID32 = "";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleCPGUID = "";
    private String[][] mArrayDistributors = null,mArraySPValues=null,mArrayCPDMSDivisoins=null;
    private String[][] mArrayInvoiceTypeId = null;
    private EditText searchMaterialDesc;
    private ProgressDialog pdLoadDialog = null;
    private MenuItem menuItem = null;
    private EditText focusEditText;
    private int itemTextChangePoss = 0;
    private String typevalue="";
    TextView tv_sku_desc;
    private ArrayList<DMSDivionBean> distListDms=null;
    private String mStrStockOwner ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_collection);
        ActionBarView.initActionBarReturnView(this, true, getString(R.string.sample_disbursement_title));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvNoRecordFound = (TextView) findViewById(R.id.no_record_found);
        Bundle bundleExtras = getIntent().getExtras();
        typevalue=Constants.getTypesetValueForSkugrp(SampleDisbursementActivity.this);
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
        }
        if (!Constants.restartApp(SampleDisbursementActivity.this)) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            initUI();
            initializeKeyboardDependencies();
        }


    }
    private void initUI(){
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();

        TextView tvRetName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView tvUID = (TextView) findViewById(R.id.tv_reatiler_id);
        searchMaterialDesc = (EditText) findViewById(R.id.et_material_desc_search);
        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetID);
        tv_sku_desc= (TextView) findViewById(R.id.tv_sku_desc);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.recycler_divider);
        recyclerView.addItemDecoration(new CustomDividerItemDecoration(dividerDrawable));
        sampleDisbursementAdapter = new SampleDisbursementAdapter(SampleDisbursementActivity.this, retailerStockBeanArrayList);
        sampleDisbursementAdapter.onClickDeleteItemListener(this);
        sampleDisbursementAdapter.textWatcher(this);
        sampleDisbursementAdapter.onFocusOn(this);
        recyclerView.setAdapter(sampleDisbursementAdapter);
        new GetSampleDisbursementList().execute();
        searchMaterialDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sampleDisbursementAdapter.filter(s + "", tvNoRecordFound, recyclerView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(typevalue.equalsIgnoreCase(Constants.SKUGROUP)){
            tv_sku_desc.setText(Constants.SKUGROUP);

        }else{
            tv_sku_desc.setText(Constants.CRSSKUGROUP);

        }
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
    private void getDistributorDMS()
    {
        String spGuid = Constants.getSPGUID();
        try {
            String mStrDistQry= Constants.CPSPRelations+" ?$filter="+ Constants.SPGUID+" eq '"+spGuid.replace("-","")+"' ";
//            String mStrDistQry= Constants.CPSPRelations;
            distListDms = OfflineManager.getDistributorsDms(mStrDistQry);
         /*   if(distList==null){
                distList =new String[5][1];
                distList[0][0] = "";
                distList[1][0] = "";
                distList[2][0] = "";
                distList[3][0] = "";
                distList[4][0] = "";
            }*/


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }
    /**
     * get data from db
     */
    private void getDataFromOfflineDB() {
        try {
            try {
                mStrStockOwner = distListDms.get(0).getStockOwner();
            } catch (Exception e) {
                mStrStockOwner = "";
            }
            retailerStockBeanTotalArrayList.clear();
            retailerStockBeanArrayList.clear();
            retailerStockBeanTotalArrayList = OfflineManager.getSampleCollectionList(Constants.CPStockItems + "?$orderby="+Constants.Material_Desc+" &$filter=StockOwner eq '"+mStrStockOwner+"'", retailerStockBeanTotalArrayList);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * refresh recyclerview
     */
    private void refreshRecyclerView() {
        sampleDisbursementAdapter.filter("", tvNoRecordFound, recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_retailer_stock, menu);
        menuItem = menu.findItem(R.id.menu_add);
        if (!retailerStockBeanTotalArrayList.isEmpty()) {
            menuItem.setVisible(true);
        } else {
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_save:
                //save data
                onSave();
                break;
            case R.id.menu_add:
                //add data
                onAdd(SampleDisbursementActivity.this);
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (Constants.isCustomKeyboardVisible(keyboardView)) {
            Constants.hideCustomKeyboard(keyboardView);
        } else {
            Constants.dialogBoxWithButton(this, "", getString(R.string.sample_disbursement_back_pressed), getString(R.string.btn_confirm_confirm), getString(R.string.btn_confirm_cancel), new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
                    if (clickedStatus) {
                        if (VisitFragment.visitUpdateListener != null) {
                            VisitFragment.visitUpdateListener.onUpdate();
                        }
                        finish();
                    }
                }
            });
        }

    }

    /**
     * add data to list open popup
     */
    private void onAdd(final Context mContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                retailerStockBeanPopupArrayList.clear();
                for (RetailerStockBean retailerStockBean : retailerStockBeanTotalArrayList) {
                    if (!retailerStockBean.getSelected()) {
                        retailerStockBeanPopupArrayList.add(retailerStockBean);
                    }
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!retailerStockBeanPopupArrayList.isEmpty()) {
                            //display popup
                            Intent intent = new Intent(mContext, AddSampleDisbursementActivity.class);
                            intent.putExtra(ConstantsUtils.EXTRA_ARRAY_LIST, retailerStockBeanPopupArrayList);
                            startActivityForResult(intent, AddSampleDisbursementActivity.SD_RESULT_ID);
                        } else {
                            Constants.dialogBoxWithButton(mContext, "", getString(R.string.sample_disbursement_error_no_data), getString(R.string.ok), "", null);
                        }
                    }
                });
            }
        }).start();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddSampleDisbursementActivity.SD_RESULT_ID) {
            if (data != null) {
                ArrayList<RetailerStockBean> retailerStockBeen = (ArrayList<RetailerStockBean>) data.getSerializableExtra(ConstantsUtils.EXTRA_ARRAY_LIST);
                if (!retailerStockBeen.isEmpty()) {
                    for (RetailerStockBean retailerStockBean : retailerStockBeen) {
                        if (retailerStockBean.getSelected()) {
                            retailerStockBeanArrayList.add(retailerStockBean);
                            RetailerStockBean retailerStockBeenMain = retailerStockBeanTotalArrayList.get(retailerStockBean.getRetailerPos());
                            retailerStockBeenMain.setSelected(true);
                        }
                    }
                    refreshRecyclerView();
                }
            }

        }
    }

    /**
     * save data to datavalt
     */
    private void onSave() {
//        if(Constants.isValidTime( UtilConstants.convertTimeOnly(Constants.mStartTimeDuration.toString()),
//                UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString())) && Constants.isValidDate()){
            boolean validateionStatus = checkValidation();
            if (validateionStatus) {
              /*  if (Constants.onGpsCheck(SampleDisbursementActivity.this)) {
                    if(UtilConstants.getLocation(SampleDisbursementActivity.this)) {
                        onSaveDataIntoDataValt();
                    }
                }*/

                pdLoadDialog = Constants.showProgressDialog(SampleDisbursementActivity.this, "", getString(R.string.checking_pemission));
                LocationUtils.checkLocationPermission(SampleDisbursementActivity.this, new LocationInterface() {
                    @Override
                    public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                        closingProgressDialog();
                        if (status) {
                            locationPerGranted();
                        }
                    }
                });

            }
//        }else{
//            UtilConstants.showAlert(getString(R.string.validation_app_time_incorrect), SampleDisbursementActivity.this);
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
        pdLoadDialog = Constants.showProgressDialog(SampleDisbursementActivity.this,"",getString(R.string.gps_progress));
        Constants.getLocation(SampleDisbursementActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closingProgressDialog();
                if(status){
                    onSaveDataIntoDataValt();
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
                    LocationUtils.checkLocationPermission(SampleDisbursementActivity.this, new LocationInterface() {
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

    /**
     * validation
     *
     * @return
     */
    private boolean checkValidation() {
        if (retailerStockBeanArrayList.isEmpty()) {
            Constants.dialogBoxWithButton(this, "", getString(R.string.sample_disbursement_error_one_item), getString(R.string.ok), "", null);
            return false;
        } else {
            int childCount = retailerStockBeanArrayList.size();
            int initialCount = 0;
            boolean allFieldEntered = false;
            boolean allValidFieldEntered = false;
            boolean enteredQty = false;
            boolean isRemarks = false;
            for (int i = 0; i < childCount; i++) {
                RetailerStockBean retailerStockBean = retailerStockBeanArrayList.get(i);

//                if (recyclerView.findViewHolderForAdapterPosition(i) instanceof SampleDisbursementViewHolder) {
                    SampleDisbursementViewHolder childHolder = (SampleDisbursementViewHolder) recyclerView.findViewHolderForLayoutPosition(i);

                if(childHolder!=null){
                    String matQty = childHolder.edMaterialQty.getText().toString();
                    String remarks = childHolder.etRemarks.getText().toString();
                    if (TextUtils.isEmpty(remarks)) {
                        allValidFieldEntered = true;
                        isRemarks=true;
                        childHolder.etRemarks.setBackgroundResource(R.drawable.edittext_border);
                    }else if(remarks.trim().length()==0){
                        allValidFieldEntered = true;
                        isRemarks=true;
                        childHolder.etRemarks.setBackgroundResource(R.drawable.edittext_border);
                    }
                    if (matQty.isEmpty()) {
                        allValidFieldEntered = true;
                        childHolder.edMaterialQty.setBackgroundResource(R.drawable.edittext_border);
                    } else {
                        allFieldEntered = true;
                        try {
                            double dbStock = Double.parseDouble(retailerStockBean.getUnrestrictedQty());
                            double enteredStock = Double.parseDouble(matQty);
                            if (dbStock >= enteredStock) {
                                initialCount++;
                            } else {
                                enteredQty = true;
                                childHolder.edMaterialQty.setBackgroundResource(R.drawable.edittext_border);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }else{
                    String matQty = retailerStockBean.getQAQty();
                    String remarks = retailerStockBean.getRemarks();
                    if (TextUtils.isEmpty(remarks)) {
                        allValidFieldEntered = true;
                        isRemarks=true;
//                        childHolder.etRemarks.setBackgroundResource(R.drawable.edittext_border);
                    }else if(remarks.trim().length()==0){
                        allValidFieldEntered = true;
                        isRemarks=true;
//                        childHolder.etRemarks.setBackgroundResource(R.drawable.edittext_border);
                    }
                    if (matQty.isEmpty()) {
                        allValidFieldEntered = true;
//                        childHolder.edMaterialQty.setBackgroundResource(R.drawable.edittext_border);
                    } else {
                        allFieldEntered = true;
                        try {
                            double dbStock = Double.parseDouble(retailerStockBean.getUnrestrictedQty());
                            double enteredStock = Double.parseDouble(matQty);
                            if (dbStock >= enteredStock) {
                                initialCount++;
                            } else {
                                enteredQty = true;
//                                childHolder.edMaterialQty.setBackgroundResource(R.drawable.edittext_border);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }


//                }
            }
            if (initialCount == retailerStockBeanArrayList.size() && !isRemarks) {
                return true;
            }
            if (allFieldEntered && !allValidFieldEntered) {
                if (enteredQty) {
                    Constants.dialogBoxWithButton(this, "", getString(R.string.sample_disbursement_val_error), getString(R.string.ok), "", null);
                } else {
                    Constants.dialogBoxWithButton(this, "", getString(R.string.validation_plz_enter_mandatory_flds), getString(R.string.ok), "", null);
                }
            } else {
                Constants.dialogBoxWithButton(this, "", getString(R.string.validation_plz_enter_mandatory_flds), getString(R.string.ok), "", null);
            }
            return false;
        }
    }

    /**
     * get distributor value
     */
    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
        mArrayInvoiceTypeId = OfflineManager.getInVoidTypeId();
    }

    /**
     * save data
     */
    private void onSaveDataIntoDataValt() {
        try {
            GUID sdGUID = GUID.newRandom();
            String mRouteSchGuid = Constants.getRouteSchGUID(Constants.RouteSchedulePlans,Constants.RouteSchGUID,Constants.VisitCPGUID,mStrBundleCPGUID32,mArrayDistributors[5][0]);
//            String doc_no = (System.currentTimeMillis() + "").substring(3, 10);
            String doc_no = (System.currentTimeMillis() + "");
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            String userName = sharedPreferences.getString(Constants.username, "");
            headerTable.clear();
            headerTable.put(Constants.InvoiceGUID, sdGUID.toString36().toUpperCase());
            headerTable.put(Constants.LoginID, userName.toUpperCase());
            headerTable.put(Constants.CPGUID, mArrayDistributors[4][0]);
            headerTable.put(Constants.CPNo, mStrBundleRetID);
            headerTable.put(Constants.CPName, mArrayDistributors[7][0]);
            headerTable.put(Constants.CPTypeDesc, mArrayDistributors[9][0]);
            headerTable.put(Constants.CPTypeID, mArrayDistributors[5][0]);
            headerTable.put(Constants.Currency, mArrayDistributors[10][0]);
            headerTable.put(Constants.SPNo, mArraySPValues[6][0] != null ? mArraySPValues[6][0] : "");
            headerTable.put(Constants.SPName, mArraySPValues[7][0] != null ? mArraySPValues[7][0] : "");
            headerTable.put(Constants.DmsDivision, mArrayCPDMSDivisoins[0][0] != null ? mArrayCPDMSDivisoins[0][0] : "");
            headerTable.put(Constants.DmsDivisionDesc, mArrayCPDMSDivisoins[1][0] != null ? mArrayCPDMSDivisoins[1][0] : "");

            String mInvNo = doc_no.substring(3, 10);
            headerTable.put(Constants.InvoiceNo, mInvNo);
            headerTable.put(Constants.InvoiceTypeID, mArrayInvoiceTypeId[0][0]);
            headerTable.put(Constants.InvoiceTypeDesc, "");
            headerTable.put(Constants.InvoiceDate, UtilConstants.getNewDateTimeFormat());

            headerTable.put(Constants.PONo, "");
            headerTable.put(Constants.PODate, UtilConstants.getNewDateTimeFormat());

            headerTable.put(Constants.SoldToCPGUID, mStrBundleCPGUID);
            headerTable.put(Constants.BeatGUID, mRouteSchGuid);
            headerTable.put(Constants.SoldToID, mStrBundleRetID);
            headerTable.put(Constants.SoldToName, mStrBundleRetName);
            headerTable.put(Constants.SoldToTypeID, Constants.str_02);
            headerTable.put(Constants.SoldToTypeDesc, mArrayDistributors[9][0]);

            headerTable.put(Constants.SPGUID, mArraySPValues[4][0].toUpperCase());


            ArrayList<HashMap<String, String>> sdItems = new ArrayList<HashMap<String, String>>();
            for (int itemIncVal = 0; itemIncVal < retailerStockBeanArrayList.size(); itemIncVal++) {
                RetailerStockBean retailerStockBean = retailerStockBeanArrayList.get(itemIncVal);
                HashMap<String, String> singleItem = new HashMap<String, String>();
                GUID ssoItemGuid = GUID.newRandom();
                singleItem.put(Constants.InvoiceItemGUID, ssoItemGuid.toString36().toUpperCase());
                singleItem.put(Constants.InvoiceGUID, sdGUID.toString36().toUpperCase());
                singleItem.put(Constants.ItemNo, ConstantsUtils.addZeroBeforeValue(itemIncVal + 1, ConstantsUtils.ITEM_MAX_LENGTH));
                singleItem.put(Constants.InvoiceNo, mInvNo);
                singleItem.put(Constants.Quantity, retailerStockBean.getQAQty());
                singleItem.put(Constants.MaterialNo, retailerStockBean.getMaterialNo());
                singleItem.put(Constants.MaterialDesc, retailerStockBean.getMaterialDesc());
                singleItem.put(Constants.StockGuid, retailerStockBean.getCPStockItemGUID());
                singleItem.put(Constants.UOM, retailerStockBean.getUom());
                singleItem.put(Constants.Currency, mArrayDistributors[10][0]);
                singleItem.put(Constants.UnitPrice, "0.0");
                singleItem.put(Constants.GrossAmount, "0.0");
                singleItem.put(Constants.Tax, "0.0");
                singleItem.put(Constants.Discount, "0.0");
                singleItem.put(Constants.Freight, "0.0");
                singleItem.put(Constants.NetAmount, "0.0");
                singleItem.put(Constants.DiscountPer, "0.0");
                singleItem.put(Constants.MRP, "0.0");
                singleItem.put(Constants.Remarks, retailerStockBean.getRemarks());
                singleItem.put(Constants.DeletionInd, "");
                singleItem.put(Constants.Batch, "");
                singleItem.put(Constants.Division, "");
                singleItem.put(Constants.InvoiceDate, UtilConstants.getNewDateTimeFormat());
                singleItem.put(Constants.BeatGUID, mRouteSchGuid);
                sdItems.add(singleItem);
            }
            Constants.onVisitActivityUpdate(mStrBundleCPGUID32, userName,
                    sdGUID.toString36().toUpperCase(), Constants.SampleDisbursementID, Constants.SampleDisbursementDesc);
            headerTable.put(Constants.entityType, Constants.SampleDisbursement);
            headerTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(sdItems));
            Constants.saveDeviceDocNoToSharedPref(this, Constants.SampleDisbursement, doc_no);
            JSONObject jsonHeaderObject = new JSONObject(headerTable);

            UtilDataVault.storeInDataVault(doc_no, jsonHeaderObject.toString());
            navigateToVisit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(View view, final int position) {
        final RetailerStockBean retailerStockBean = retailerStockBeanArrayList.get(position);
        Constants.dialogBoxWithButton(this, "", getString(R.string.do_want_to_delete_retailer_stock, retailerStockBean.getMaterialDesc()), getString(R.string.btn_confirm_confirm), getString(R.string.btn_confirm_cancel), new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                if (clickedStatus) {

                    retailerStockBean.setSelected(false);
                    retailerStockBeanArrayList.remove(position);
                    RetailerStockBean retailerStockBean1 = retailerStockBeanTotalArrayList.get(retailerStockBean.getRetailerPos());
                    retailerStockBean1.setSelected(false);
                    SampleDisbursementViewHolder childHolder = (SampleDisbursementViewHolder) recyclerView.findViewHolderForLayoutPosition(position);
                    childHolder.edMaterialQty.setBackgroundResource(R.drawable.edittext);
                    childHolder.etRemarks.setBackgroundResource(R.drawable.edittext);
                    refreshRecyclerView();
                }
            }
        });

    }

    /*Navigate to prev screen*/
    public void navigateToVisit() {
        Constants.dialogBoxWithButton(SampleDisbursementActivity.this, "", getString(R.string.msg_sample_disbursement_created), getString(R.string.ok), "", new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                SampleDisbursementActivity.this.finish();
            }
        });

    }

    @Override
    public void textChane(String charSequence, int position) {

    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        switch (primaryCode) {

            case 81:
                //Plus
                Constants.incrementTextValues(focusEditText, Constants.N);
                break;
            case 69:
                //Minus
                Constants.decrementEditTextVal(focusEditText, Constants.N);
                break;
            case 1:
                changeCursor(0, focusEditText);
                break;
            case 2:
                changeCursor(1, focusEditText);
                break;
            case 56:
                KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event);
                break;

            default:
                //default numbers
                KeyEvent events = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(events);
                break;
        }
    }

    private void changeCursor(int upDownStatus, EditText focusEditText) {
        if (upDownStatus == 1) {
            int ListSize = retailerStockBeanArrayList.size() - 1;
            if (itemTextChangePoss != ListSize) {
                int newItemPoss = itemTextChangePoss + 1;
                if (recyclerView.findViewHolderForLayoutPosition(newItemPoss) instanceof SampleDisbursementViewHolder) {
                    SampleDisbursementViewHolder childHolder = (SampleDisbursementViewHolder) recyclerView.findViewHolderForLayoutPosition(newItemPoss);
                    childHolder.edMaterialQty.requestFocus();
                }
            }

        } else {
            if (itemTextChangePoss != 0) {
                try {
                    int newItemPoss = itemTextChangePoss - 1;
                    if (recyclerView.findViewHolderForLayoutPosition(newItemPoss) instanceof SampleDisbursementViewHolder) {
                        SampleDisbursementViewHolder childHolder = (SampleDisbursementViewHolder) recyclerView.findViewHolderForLayoutPosition(newItemPoss);
                        childHolder.edMaterialQty.requestFocus();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

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

    @Override
    public void onTextChange(View v, boolean hasFocus, int poss, EditText editText) {
        if (hasFocus) {
            itemTextChangePoss = poss;
            focusEditText = editText;
            Constants.setCursorPosition(editText);
            Constants.showCustomKeyboard(v, keyboardView, SampleDisbursementActivity.this);
        } else {
            Constants.hideCustomKeyboard(keyboardView);
        }

    }

    @Override
    public void setOnTouch(View v, int poss) {
        itemTextChangePoss = poss;
        v.requestFocus();
        Constants.showCustomKeyboard(v, keyboardView, SampleDisbursementActivity.this);
    }

    public void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_samp_dis_sel);
        keyboard = new Keyboard(SampleDisbursementActivity.this, R.xml.ll_with_out_dot_inc_dec_up_down);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private class GetSampleDisbursementList extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(SampleDisbursementActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            getDistributorDMS();
            getCPDMSDivisions();
            getSalesPersonValues();
            getDistributorValues();
            getDataFromOfflineDB();
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
            refreshRecyclerView();
            if (menuItem != null) {
                if (!retailerStockBeanTotalArrayList.isEmpty()) {
                    menuItem.setVisible(true);
                } else {
                    menuItem.setVisible(false);
                }
            }
        }
    }

}
