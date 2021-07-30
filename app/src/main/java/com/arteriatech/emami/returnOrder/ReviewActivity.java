package com.arteriatech.emami.returnOrder;

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
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.xscript.core.GUID;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class ReviewActivity extends AppCompatActivity {

    ArrayList<ReturnOrderBean> filteredArrayList = null;
    private RecyclerView recyclerView;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "", mStrComingFrom = "";
    private String[][] mArrayDistributors = null;
    private String[][] mArrayCPDMSDivisoins = null;
    private Hashtable<String, String> headerTable = null;
    private String[][] mArraySPValues=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ActionBarView.initActionBarView(this, true, getString(R.string.title_return_order_create));
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            filteredArrayList = bundleExtras.getParcelableArrayList(Constants.EXTRA_ARRAY_LIST);
        }
        if (!Constants.restartApp(ReviewActivity.this)) {
            getSalesPersonValues();
            getDistributorValues();
            getCPDMSDivisions();
            initView();
        }
    }

    /**
     * get salesPerson values
     */
    private void getSalesPersonValues() {
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(mStrBundleCPGUID);
    }
    private void initView() {
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (filteredArrayList != null) {
            RoReviewAdapter roReviewAdapter = new RoReviewAdapter(ReviewActivity.this, filteredArrayList);
            recyclerView.setAdapter(roReviewAdapter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_appointment_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_appointment_save:
                onSave();
                break;
        }
        return false;
    }

    /*get distributor values*/
    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
    }

    /*get cpdms divisions value*/
    private void getCPDMSDivisions() {
        mArrayCPDMSDivisoins = Constants.getDMSDivisionByCPGUID(mStrBundleCPGUID);
    }

    private void onSave() {
//        if (Constants.isValidTime(UtilConstants.convertTimeOnly(Constants.mStartTimeDuration.toString()),
//                UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString())) && Constants.isValidDate()) {
           /* if (Constants.onGpsCheck(ReviewActivity.this)) {
                if(UtilConstants.getLocation(ReviewActivity.this)){
                getDistributorValues();
                getCPDMSDivisions();
                createDataVaultObjects();
            }
            }*/
//        }else{
//            UtilConstants.showAlert(getString(R.string.validation_app_time_incorrect), ReviewActivity.this);
//        }

        pdLoadDialog = Constants.showProgressDialog(ReviewActivity.this, "", getString(R.string.checking_pemission));
        LocationUtils.checkLocationPermission(ReviewActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                closingProgressDialog();
                if (status) {
                    locationPerGranted();
                }
            }
        });
    }

    private void onSaveRO(){
        getDistributorValues();
        getCPDMSDivisions();
        createDataVaultObjects();
    }
    private ProgressDialog pdLoadDialog=null;
    private void closingProgressDialog(){
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void locationPerGranted(){
        pdLoadDialog = Constants.showProgressDialog(ReviewActivity.this,"",getString(R.string.gps_progress));
        Constants.getLocation(ReviewActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closingProgressDialog();
                if(status){
                    onSaveRO();
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
                    LocationUtils.checkLocationPermission(ReviewActivity.this, new LocationInterface() {
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

    /*save data to data valt*/
    private void createDataVaultObjects() {
        headerTable = new Hashtable<>();
        GUID ssoHeaderGuid = GUID.newRandom();
        String doc_no = (System.currentTimeMillis() + "");
        headerTable.put(Constants.SSROGUID, ssoHeaderGuid.toString36().toUpperCase());
        String ordettype = "";
        try {
            ordettype = OfflineManager.getValueByColumnName(Constants.ValueHelps+ "?$select=" + Constants.ID + " &$filter="+Constants.EntityType+" eq 'SSRO' and  "+Constants.PropName+" eq 'OrderType' and  "+Constants.ParentID+" eq '000020' ",Constants.ID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        headerTable.put(Constants.OrderType, ordettype);
        headerTable.put(Constants.OrderTypeDesc, "");
        headerTable.put(Constants.OrderNo, doc_no);
        headerTable.put(Constants.OrderDate, UtilConstants.getNewDateTimeFormat());
        headerTable.put(Constants.DmsDivision, mArrayCPDMSDivisoins[0][0] != null ? mArrayCPDMSDivisoins[0][0] : "");
        headerTable.put(Constants.DmsDivisionDesc, mArrayCPDMSDivisoins[1][0] != null ? mArrayCPDMSDivisoins[1][0] : "");
        headerTable.put(Constants.FromCPGUID, mArrayDistributors[4][0]);
        headerTable.put(Constants.FromCPNo, mArrayDistributors[4][0]);
        headerTable.put(Constants.FromCPName, mArrayDistributors[7][0]);
        headerTable.put(Constants.FromCPTypId, mArrayDistributors[5][0]);
        headerTable.put(Constants.FromCPTypDs, "");
        headerTable.put(Constants.CPGUID, mStrBundleCPGUID);
        headerTable.put(Constants.CPNo, mStrBundleRetID);
        headerTable.put(Constants.CPName, mStrBundleRetName);
        headerTable.put(Constants.CPTypeID, Constants.str_02);
        headerTable.put(Constants.CPTypeDesc, mArrayDistributors[9][0]);
        headerTable.put(Constants.SoldToCPGUID, mStrBundleCPGUID);
        headerTable.put(Constants.SoldToId, mStrBundleRetID);
        headerTable.put(Constants.SoldToUID, mStrBundleCPGUID);
        headerTable.put(Constants.SoldToDesc, mStrBundleRetName);
        headerTable.put(Constants.SoldToTypeID, mArrayDistributors[8][0]);
        headerTable.put(Constants.SoldToTypDesc, mArrayDistributors[9][0]);
        headerTable.put(Constants.SPGUID, mArraySPValues[4][0]);
        headerTable.put(Constants.SPNo, mArraySPValues[6][0]);
        headerTable.put(Constants.FirstName, mArraySPValues[7][0]);
        headerTable.put(Constants.Currency, mArrayDistributors[10][0]);
        headerTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
        headerTable.put(Constants.CreatedAt, Constants.getOdataDuration().toString());
        headerTable.put(Constants.StatusID, "000001");
        headerTable.put(Constants.ApprovalStatusID, "000001");
        headerTable.put(Constants.TestRun, "M");
        ArrayList<HashMap<String, String>> soItems = new ArrayList<HashMap<String, String>>();
        for (int itemIncVal = 0; itemIncVal < filteredArrayList.size(); itemIncVal++) {
            HashMap<String, String> singleItem = new HashMap<String, String>();
            GUID ssoItemGuid = GUID.newRandom();
            singleItem.put(Constants.SSROItemGUID, ssoItemGuid.toString36().toUpperCase());
            singleItem.put(Constants.SSROGUID, ssoHeaderGuid.toString36().toUpperCase());
            singleItem.put(Constants.ItemNo, ConstantsUtils.addZeroBeforeValue(itemIncVal + 1, ConstantsUtils.ITEM_MAX_LENGTH));
            singleItem.put(Constants.MaterialNo, filteredArrayList.get(itemIncVal).getMaterialNo());
            singleItem.put(Constants.MaterialDesc, filteredArrayList.get(itemIncVal).getMaterialDesc());
            singleItem.put(Constants.OrderMatGrp, filteredArrayList.get(itemIncVal).getOrderMaterialGroupID());
            singleItem.put(Constants.OrderMatGrpDesc, filteredArrayList.get(itemIncVal).getOrderMaterialGroupDesc());
            singleItem.put(Constants.Quantity, filteredArrayList.get(itemIncVal).getReturnQty());
            singleItem.put(Constants.Currency, mArrayDistributors[10][0]);
            singleItem.put(Constants.Uom, filteredArrayList.get(itemIncVal).getUom());
            singleItem.put(Constants.Batch, filteredArrayList.get(itemIncVal).getReturnBatchNumber().toUpperCase());
            singleItem.put(Constants.RejectionReasonID, filteredArrayList.get(itemIncVal).getReturnReason());
            singleItem.put(Constants.RejectionReasonDesc, filteredArrayList.get(itemIncVal).getReturnDesc());
            singleItem.put(Constants.MRP, filteredArrayList.get(itemIncVal).getReturnMrp());

            soItems.add(singleItem);

        }
        headerTable.put(Constants.entityType, Constants.ReturnOrderCreate);
        headerTable.put(Constants.ITEM_TXT, Constants.convertArrListToGsonString(soItems));
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        Constants.onVisitActivityUpdate(mStrBundleCPGUID32, sharedPreferences.getString(Constants.username, ""),
                ssoHeaderGuid.toString36().toUpperCase(), Constants.ROCreateID, Constants.ReturnOrderCreate);

        Constants.saveDeviceDocNoToSharedPref(ReviewActivity.this, Constants.ROList, doc_no);

        headerTable.put(Constants.LOGINID, sharedPreferences.getString(Constants.username, "").toUpperCase());

        JSONObject jsonHeaderObject = new JSONObject(headerTable);

        UtilDataVault.storeInDataVault(doc_no, jsonHeaderObject.toString());

        navigateToVisit();

    }

    /*Navigate to visit screen*/
    public void navigateToVisit() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                ReviewActivity.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.msg_return_order_created))
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    Constants.ComingFromCreateSenarios = Constants.X;
                                    navigateToRetDetailsActivity();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
        builder.show();
    }

    /*navigation to retailers details activity*/
    private void navigateToRetDetailsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(ReviewActivity.this, RetailersDetailsActivity.class);
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
}
