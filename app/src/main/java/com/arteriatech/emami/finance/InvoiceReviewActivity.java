package com.arteriatech.emami.finance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.InvoiceReviewAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.mbo.MaterialBatchBean;
import com.arteriatech.emami.mbo.SchemeBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.google.gson.Gson;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.xscript.core.GUID;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by e10742 on 7/28/2017.
 */

public class InvoiceReviewActivity extends AppCompatActivity {
    private ArrayList<InvoiceBean> alMatList = new ArrayList<>();
    ArrayList<HashMap<String, String>> arrtable = null;
    Hashtable dbHeaderTable = null;
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "", mStrMobileNo = "", mStrCpGuid36 = "", mStrRetailerCpType = "", mStrInvListQry = "";
    private ODataGuid mCpGuid;
    String mStrComingFrom = "";
    TextView tv_total_order_value = null, tvNetAmt = null, tvTaxAmt = null;
    double totalNetAmount = 0.0, totalTaxAmount = 0.0;

    Hashtable<String, ArrayList<InvoiceBean>> hashTableItemSerialNos;
    int mError = 0;
    String invGUID32 = "";
    String mStrDateTime = "";
    Hashtable visitActivityTable = null;
    private String popUpText = "", matGrpType = "";
    private String doc_no = null;
    private String mStrSpStockItemGuid = "";
    private String[][] mArrayDistributors, mArraySPValues = null;

    RecyclerView rvInvProductsList = null;
    TextView tvEmptyListLay = null;

    private ODataPropMap oDataProperties;
    private ODataProperty oDataProperty;

    String strCurrency = "";
    String strParentCpType = "";
    private ProgressDialog pdLoadDialog=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.title_invoice_review));

        setContentView(R.layout.activity_invoice_review);

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
            arrtable = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra(Constants.InvoiceItemList);
        }
        if (!Constants.restartApp(InvoiceReviewActivity.this)) {
            getSalesPersonValues();
            getRetailerDetails();
            initUI();
        }
    }

    private void getRetailerDetails() {
        String retDetgry = Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq guid'" + mStrBundleCPGUID.toUpperCase() + "' ";
        try {
            ODataEntity retilerEntity = OfflineManager.getRetDetails(retDetgry);

            oDataProperties = retilerEntity.getProperties();

            oDataProperty = oDataProperties.get(Constants.CPGUID);

            mCpGuid = (ODataGuid) oDataProperty.getValue();

            oDataProperty = oDataProperties.get(Constants.CPTypeID);

            mStrRetailerCpType = (String) oDataProperty.getValue();

            oDataProperty = oDataProperties.get(Constants.ParentTypeID);

            strParentCpType = (String) oDataProperty.getValue();


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


        tv_total_order_value = (TextView) findViewById(R.id.tv_total_order_value);
        tvNetAmt = (TextView) findViewById(R.id.tv_net_amt);
        tvTaxAmt = (TextView) findViewById(R.id.tv_tax_amt);

        tv_total_order_value.setText(Constants.removeLeadingZero("")+" "+ strCurrency);
        rvInvProductsList = (RecyclerView) findViewById(R.id.rv_inv_review);
        rvInvProductsList.setLayoutManager(new LinearLayoutManager(this));
        tvEmptyListLay = (TextView) findViewById(R.id.tv_empty_lay);

        getInvoiceDetails();

    }

    private void getInvoiceDetails() {
        if(strCurrency.equalsIgnoreCase(""))
            strCurrency = Constants.getCurrency();
        totalNetAmount = 0;
        totalTaxAmount = 0;
        for (HashMap<String, String> materialItem : arrtable) {
            if(materialItem.get(Constants.Currency)==null)
                materialItem.put(Constants.Currency, Constants.getCurrency());
            SchemeBean primaryDisTaxValBean = null;
            primaryDisTaxValBean = getPrimaryTaxValByMaterial(materialItem.get(Constants.StockGuid),
                    materialItem.get(Constants.MatCode), materialItem.get(Constants.Qty));
            if (primaryDisTaxValBean != null) {
                ArrayList<MaterialBatchBean> alMatBatchItemBean = primaryDisTaxValBean.getMaterialBatchBeanArrayList();
                if (alMatBatchItemBean != null && alMatBatchItemBean.size() > 0) {
                    for (MaterialBatchBean matBatchItemBean : alMatBatchItemBean) {
                        double calSecPer = 0.0;
                        String netAmount = matBatchItemBean.getNetAmount();
                        matBatchItemBean.setSecPer(calSecPer + "");
                        String secondarySchemeAmt = Constants.calculatePrimaryDiscount(calSecPer + "", netAmount);

                        String mStrTaxAmt = getTaxAmount(matBatchItemBean.getNetAmtAftPriDis(), secondarySchemeAmt, matBatchItemBean.getoDataEntity(), matBatchItemBean.getQty());
                        matBatchItemBean.setTax(mStrTaxAmt);
                        totalTaxAmount = totalTaxAmount+Double.parseDouble(mStrTaxAmt);
                        totalNetAmount = totalNetAmount+Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis());


                        matBatchItemBean.setTotalNetAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt)) + "");
//                        mDouSumNetTaxSecAmt = mDouSumNetTaxSecAmt + (Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + Double.parseDouble(mStrTaxAmt));

//                        mDouPriDis = Double.parseDouble(matBatchItemBean.getPrimaryPer());
//
//                        matBatchItemBean.setGrossAmt((Double.parseDouble(matBatchItemBean.getNetAmtAftPriDis()) - Double.parseDouble(secondarySchemeAmt) + ""));
//
//                        mDouSecDiscount = calSecPer;
                    }
                    primaryDisTaxValBean.setMaterialBatchBeanArrayList(alMatBatchItemBean);
                }
            }
        }
        displayInvoiceData();
    }

    void displayInvoiceData(){
        tvNetAmt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(String.valueOf(totalNetAmount))+" "+ strCurrency);
        tvTaxAmt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(String.valueOf(totalTaxAmount))+" "+ strCurrency);
        tv_total_order_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(String.valueOf(totalNetAmount+totalTaxAmount))+" "+ strCurrency);
        rvInvProductsList.setAdapter(new InvoiceReviewAdapter(arrtable, InvoiceReviewActivity.this, tvEmptyListLay));
    }

    private SchemeBean getPrimaryTaxValByMaterial(String cPStockItemGUID, String mStrMatNo, String mStrOrderQty) {

        SchemeBean InvoiceItemPriceBean = null;
        try {

            InvoiceItemPriceBean = OfflineManager.getNetAmount(Constants.CPStockItemSnos + "?$filter=" + Constants.MaterialNo + " eq '" + mStrMatNo + "' and "
                    + Constants.CPStockItemGUID + " eq guid'" + cPStockItemGUID + "' and " + Constants.StockTypeID + " ne '" + Constants.str_3 + "'  &$orderby=" + Constants.ManufacturingDate + "%20asc ", mStrOrderQty, mStrMatNo,true);
//            and "+Constants.ManufacturingDate+" ne null
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return InvoiceItemPriceBean;
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

    /*Saves Invoice in offline store*/
    private void onSave() {
        ArrayList<InvoiceCreateBean> alSelectedInvoice = new ArrayList<>();
        if (Constants.isValidTime(UtilConstants.convertTimeOnly(Constants.mStartTimeDuration.toString()),
                UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString())) && Constants.isValidDate()) {


           /* if (!Constants.onGpsCheck(InvoiceReviewActivity.this)) {
                return;
            }
            if(!UtilConstants.getLocation(InvoiceReviewActivity.this)){
                return;
            }*/

            pdLoadDialog = Constants.showProgressDialog(InvoiceReviewActivity.this, "", getString(R.string.checking_pemission));
            LocationUtils.checkLocationPermission(InvoiceReviewActivity.this, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    closingProgressDialog();
                    if (status) {
                        locationPerGranted();
                    }
                }
            });

        } else {
            UtilConstants.showAlert(getString(R.string.validation_app_time_incorrect), InvoiceReviewActivity.this);
        }

    }

    private void closingProgressDialog(){
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void locationPerGranted(){
        pdLoadDialog = Constants.showProgressDialog(InvoiceReviewActivity.this,"",getString(R.string.gps_progress));
        Constants.getLocation(InvoiceReviewActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closingProgressDialog();
                if(status){
                    onSaveInv();
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
                    LocationUtils.checkLocationPermission(InvoiceReviewActivity.this, new LocationInterface() {
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
        if(requestCode== LocationUtils.REQUEST_CHECK_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                locationPerGranted();
            }
        }
    }

    private void onSaveInv(){
        mError = 0;
        Constants.InvoiceNumber = "";
        Constants.InvoiceTotalAmount = 0.0;
        doc_no = (System.currentTimeMillis() + "");

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


            //========>Start VisitActivity
            GUID guid = GUID.newRandom();

            Constants.onVisitActivityUpdate(mStrBundleCPGUID32, loginIdVal,
                    guid.toString36().toUpperCase(), Constants.Secondary_Invoice_Type, Constants.Secondary_Invoice);


            Set<String> set = new HashSet<>();

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
            dbHeaderTable.put(Constants.Currency, Constants.getCurrency());


            dbHeaderTable.put(Constants.SoldToID, mStrBundleRetID);
            dbHeaderTable.put(Constants.NetAmount, (totalNetAmount+totalTaxAmount) + "");
            Constants.InvoiceTotalAmount = totalNetAmount+totalTaxAmount;
//                dbHeaderTable.put(Constants.CPTypeID, Constants.str_02);
//                    dbHeaderTable.put(Constants.CPTypeID, mArraySPValues[9][0]);
            dbHeaderTable.put(Constants.CPTypeID, strParentCpType);
            dbHeaderTable.put(Constants.SPGuid, mArraySPValues[4][0].toUpperCase());

            dbHeaderTable.put(Constants.SoldToCPGUID, mStrCpGuid36.toUpperCase());
            dbHeaderTable.put(Constants.SoldToTypeID, mStrRetailerCpType);
            dbHeaderTable.put(Constants.SPNo, mArraySPValues[6][0]);

            dbHeaderTable.put(Constants.TestRun, "");
            dbHeaderTable.put(Constants.EntityType, Constants.SSInvoice);
            try {
                dbHeaderTable.put(Constants.TLSD, arrtable.size()+"");
            } catch (Exception e) {
                dbHeaderTable.put(Constants.TLSD, "0");
            }

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

            Gson gson = new Gson();

            try {
                String jsonFromMap = gson.toJson(arrtable);

                dbHeaderTable.put(Constants.strITEMS, jsonFromMap);

                jsonFromMap = gson.toJson(hashTableItemSerialNos);

                dbHeaderTable.put(Constants.ITEMSSerialNo, jsonFromMap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            set = sharedPreferences.getStringSet(Constants.InvList, null);

            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }
            setTemp.add(doc_no);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.InvList, setTemp);
            editor.commit();

            JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);


            LogonCore.getInstance().addObjectToStore(doc_no, jsonHeaderObject.toString());

            try {
                OfflineManager.createVisitActivity(visitActivityTable);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            navigateToVisitTemp();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Displays error message*/
    public void displayError(String errorMessage) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(
                InvoiceReviewActivity.this, R.style.MyTheme);
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

    public void navigateToVisitTemp() {
//        try {
//            pdLoadDialog.dismiss();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        popUpText = "Invoice " + doc_no + " created successfully." +
                "Do you want collection transaction.";

        AlertDialog.Builder builder = new AlertDialog.Builder(
                InvoiceReviewActivity.this, R.style.MyTheme);
        builder.setMessage(popUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {

                                    Dialog.cancel();
                                    Intent intentCollCreate = new Intent(InvoiceReviewActivity.this, CollectionCreateActivity.class);
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
    /*Navigates to Retailer Details*/
    private void onNavigateToRetDetilsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(InvoiceReviewActivity.this, RetailersDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        startActivity(intentNavPrevScreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_invoice_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_invoice_save:
                onSave();
                break;
        }
        return true;
    }
}
