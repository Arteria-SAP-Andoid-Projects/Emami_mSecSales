package com.arteriatech.emami.feedback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.xscript.core.GUID;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by e10526 on 05-05-2016.
 *
 */
@SuppressLint("NewApi")
public class FeedBackActivity extends AppCompatActivity implements UIListener {
    private EditText editRemraks, edit_bts_id, edit_other_input;
    private String[][] arrFeedBackType = null,arrFeedBackSubType=null;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrSelFeedBackType = "", mStrSelFeedBackTypeDesc = "",mStrParentID="",
            mStrSelFeedBackSubType = "", mStrSelFeedBackSubTypeDesc = "",
            mStrRemarks = "", mStrBtsId = "", mStrOtherType = "", popUpText = "";
    ArrayList<HashMap<String, String>> arrtable;
    Hashtable tableHdr;
    private String doc_no = "";
    private String[][] mArrayDistributors,mArraySPValues=null;

    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    String mStrComingFrom = "";
    LinearLayout ll_payment_related,ll_other_input;
    TextView tv_feed_back_rel,tv_other_feed_back_name;
    Spinner sp_payment_related;
    Spinner spinnerFeedBackType;
    private ProgressDialog pdLoadDialog=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.lbl_feed_back_create));

        setContentView(R.layout.activity_feed_back);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!Constants.restartApp(FeedBackActivity.this)) {
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

        TextView tvRetName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView tvUID = (TextView) findViewById(R.id.tv_reatiler_id);
        tv_feed_back_rel = (TextView) findViewById(R.id.tv_feed_back_rel);
        tv_other_feed_back_name = (TextView) findViewById(R.id.tv_other_feed_back_name);
        ll_payment_related = (LinearLayout)findViewById(R.id.ll_payment_related);
        ll_other_input = (LinearLayout)findViewById(R.id.ll_other_input);
        sp_payment_related = (Spinner)findViewById(R.id.sp_payment_related);
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetID);

        getSalesPersonValues();
        getFeedBackType();
        getDistributorValues();

        if (arrFeedBackType == null) {
            arrFeedBackType = new String[5][1];
            arrFeedBackType[0][0] = "";
            arrFeedBackType[1][0] = "";
            arrFeedBackType[2][0] = "";
            arrFeedBackType[3][0] = "";
            arrFeedBackType[4][0] = "";
        }else {
            arrFeedBackType = Constants.CheckForOtherInConfigValue(arrFeedBackType);
        }
        spinnerFeedBackType = (Spinner) findViewById(R.id.sp_feed_back_type);

        ArrayAdapter<String> arrayAdepterFeedBackTypeValues = new ArrayAdapter<>(this, R.layout.custom_textview, arrFeedBackType[1]);
        arrayAdepterFeedBackTypeValues.setDropDownViewResource(R.layout.spinnerinside);
        spinnerFeedBackType.setAdapter(arrayAdepterFeedBackTypeValues);
        spinnerFeedBackType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                mStrSelFeedBackType = arrFeedBackType[0][position];
                mStrSelFeedBackTypeDesc = arrFeedBackType[1][position];
                mStrParentID  = arrFeedBackType[4][position];
                spinnerFeedBackType.setBackgroundResource(R.drawable.spinner_bg);

                if(mStrSelFeedBackType.equalsIgnoreCase(Constants.str_05)){
                    tv_feed_back_rel.setText(mStrSelFeedBackTypeDesc);
                    ll_payment_related.setVisibility(View.VISIBLE);
                    edit_other_input.setText("");
                    ll_other_input.setVisibility(View.GONE);
                    getFeedBackSubType(mStrSelFeedBackType);
                    paymentRelatedDropDown();
                }else if(mStrSelFeedBackType.equalsIgnoreCase(Constants.str_06)){
                    ll_payment_related.setVisibility(View.GONE);
                    ll_other_input.setVisibility(View.VISIBLE);

                    tv_other_feed_back_name.setText(mStrSelFeedBackTypeDesc);


                }else {
                    edit_other_input.setText("");
                    ll_payment_related.setVisibility(View.GONE);
                    ll_other_input.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        editRemraks = (EditText) findViewById(R.id.edit_remarks);
        InputFilter[] FilterArray = new InputFilter[2];
        FilterArray[0] = new InputFilter.LengthFilter(250);
        FilterArray[1] = Constants.getNumberAlphabetOnly();
        editRemraks.setFilters(FilterArray);

        editRemraks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editRemraks.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        edit_bts_id = (EditText) findViewById(R.id.edit_bts_id);

        edit_other_input = (EditText) findViewById(R.id.edit_other_input);
        edit_other_input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});

        edit_other_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit_other_input.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void paymentRelatedDropDown(){
        if (arrFeedBackSubType == null) {
            arrFeedBackSubType = new String[5][1];
            arrFeedBackSubType[0][0] = "";
            arrFeedBackSubType[1][0] = "";
            arrFeedBackSubType[2][0] = "";
            arrFeedBackSubType[3][0] = "";
            arrFeedBackSubType[4][0] = "";
        } else {
            arrFeedBackSubType = Constants.CheckForOtherInConfigValue(arrFeedBackSubType);
        }

        ArrayAdapter<String> arrAdpFeedBackSubTypeVal = new ArrayAdapter<>(this, R.layout.custom_textview, arrFeedBackSubType[1]);
        arrAdpFeedBackSubTypeVal.setDropDownViewResource(R.layout.spinnerinside);
        sp_payment_related.setAdapter(arrAdpFeedBackSubTypeVal);
        sp_payment_related.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                sp_payment_related.setBackgroundResource(R.drawable.spinner_bg);
                mStrSelFeedBackSubType = arrFeedBackSubType[0][position];
                mStrSelFeedBackSubTypeDesc = arrFeedBackSubType[1][position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    /*Gets feedback Types from value helps*/
    private void getFeedBackType() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.FeedbackType + "'";
            arrFeedBackType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry,"");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }
    private void getFeedBackSubType(String mStrParentId) {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '"
                    + Constants.FeedbackSubType + "' and "+Constants.ParentID+" eq '"+mStrParentId+"' ";
            arrFeedBackSubType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry,"");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt  + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_feed_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_feedback_save:
                onSave();

                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedBackActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_create_feed_back).setCancelable(false)
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

    /*Validating Data*/
    private boolean validateData() {

        boolean hasError = false;


        if (mStrSelFeedBackType.equalsIgnoreCase("")) {
            // Full day attendance type = None
            if (mStrSelFeedBackType.equalsIgnoreCase("")) {
                // error
                spinnerFeedBackType.setBackgroundResource(R.drawable.error_spinner);
                hasError = true;
            }

            if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                // error
                editRemraks.setBackgroundResource(R.drawable.edittext_border);
                hasError = true;
            }

        } else  if (mStrSelFeedBackType.equalsIgnoreCase("05")) {

            if (mStrSelFeedBackSubType.equalsIgnoreCase("")) {
                // error
                hasError = true;
                sp_payment_related.setBackgroundResource(R.drawable.error_spinner);
            }

                if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                    // error
                    hasError = true;
                    editRemraks.setBackgroundResource(R.drawable.edittext_border);
                }
        }else if(mStrSelFeedBackType.equalsIgnoreCase(Constants.str_06)) {

            if (edit_other_input.getText() == null || edit_other_input.getText().toString().trim().equalsIgnoreCase("")) {
                // error
                hasError = true;
                edit_other_input.setBackgroundResource(R.drawable.edittext_border);
            }

            if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                // error
                hasError = true;
                editRemraks.setBackgroundResource(R.drawable.edittext_border);
            }
        } else{
            if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                // error
                hasError = true;
                editRemraks.setBackgroundResource(R.drawable.edittext_border);
            }
        }


        return hasError;
    }

    /*Saves feedback into data vault*/
    private void onSave() {

//        if(Constants.isValidTime( UtilConstants.convertTimeOnly(Constants.mStartTimeDuration.toString()),
//                UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString())) && Constants.isValidDate()){
            if (validateData()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
                builder.setMessage(R.string.validation_plz_enter_mandatory_flds)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                builder.show();
            } else {
               /* if (!Constants.onGpsCheck(FeedBackActivity.this)) {
                    return;
                }
                if(!UtilConstants.getLocation(FeedBackActivity.this)){
                    return;
                }*/

                pdLoadDialog = Constants.showProgressDialog(FeedBackActivity.this, "", getString(R.string.checking_pemission));
                LocationUtils.checkLocationPermission(FeedBackActivity.this, new LocationInterface() {
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
//            UtilConstants.showAlert(getString(R.string.validation_app_time_incorrect), FeedBackActivity.this);
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
        pdLoadDialog = Constants.showProgressDialog(FeedBackActivity.this,"",getString(R.string.gps_progress));
        Constants.getLocation(FeedBackActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closingProgressDialog();
                if(status){
                    onSaveFeedBack();
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
                    LocationUtils.checkLocationPermission(FeedBackActivity.this, new LocationInterface() {
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
    private void onSaveFeedBack(){
        doc_no = (System.currentTimeMillis() + "");
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);

        String loginIdVal = sharedPreferences.getString(Constants.username, "");


        GUID mStrGuide = GUID.newRandom();
        Constants.onVisitActivityUpdate(mStrBundleCPGUID32, sharedPreferences.getString(Constants.username, ""),
                mStrGuide.toString36().toUpperCase(),Constants.FeedbackID, Constants.Feedback);

        String tempInvNo = (System.currentTimeMillis() + "");
        mStrRemarks = editRemraks.getText().toString();
        mStrBtsId = edit_bts_id.getText().toString().trim().equalsIgnoreCase("") ? "" : edit_bts_id.getText().toString();
        mStrOtherType = edit_other_input.getText().toString().trim().equalsIgnoreCase("") ? "" : edit_other_input.getText().toString();

        tableHdr = new Hashtable();

        //noinspection unchecked
        tableHdr.put(Constants.FeebackGUID, mStrGuide.toString());
        //noinspection unchecked
        tableHdr.put(Constants.Remarks, mStrRemarks);
        //noinspection unchecked
        tableHdr.put(Constants.CPNo, UtilConstants.removeLeadingZeros(mStrBundleRetID));
        //noinspection unchecked
        tableHdr.put(Constants.CPGUID, mStrBundleCPGUID32.toUpperCase());

        //noinspection unchecked
        tableHdr.put(Constants.FeedbackType, mStrSelFeedBackType);



        if(mStrSelFeedBackType.equalsIgnoreCase(Constants.str_06)){
            tableHdr.put(Constants.FeedbackTypeDesc, mStrOtherType);
        }else{
            tableHdr.put(Constants.FeedbackTypeDesc, mStrSelFeedBackTypeDesc);
        }

        if(mStrSelFeedBackType.equalsIgnoreCase(Constants.str_05)) {
            //noinspection unchecked
            tableHdr.put(Constants.FeedbackSubTypeID, mStrSelFeedBackSubType);

            tableHdr.put(Constants.FeedbackSubTypeDesc, mStrSelFeedBackSubTypeDesc);
        }else{
            tableHdr.put(Constants.FeedbackSubTypeID, "");

            tableHdr.put(Constants.FeedbackSubTypeDesc, "");
        }
        //noinspection unchecked
//                tableHdr.put(Constants.Location1, mStrOtherType);

        tableHdr.put(Constants.Location1, "");

        tableHdr.put(Constants.BTSID, mStrBtsId);

        tableHdr.put(Constants.FeedbackNo, tempInvNo);
        tableHdr.put(Constants.CPTypeID,Constants.str_02);
        tableHdr.put(Constants.SPGUID, mArraySPValues[4][0].toUpperCase());
        tableHdr.put(Constants.SPNo, mArraySPValues[6][0]);
        tableHdr.put(Constants.ParentID, mArrayDistributors[4][0]);
        tableHdr.put(Constants.ParentName, mArrayDistributors[7][0]);
        tableHdr.put(Constants.ParentTypeID, mArrayDistributors[5][0]);
        tableHdr.put(Constants.ParentTypDesc, mArrayDistributors[6][0]);


        //noinspection unchecked
        tableHdr.put(Constants.LOGINID, loginIdVal);

        tableHdr.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());

        tableHdr.put(Constants.CreatedAt, UtilConstants.getOdataDuration());

        tableHdr.put(Constants.entityType, Constants.Feedback);

        arrtable = new ArrayList<HashMap<String, String>>();

        HashMap tableItm = new HashMap();

        try {
            //noinspection unchecked
            tableItm.put(Constants.FeebackGUID, mStrGuide.toString());
            mStrGuide = GUID.newRandom();
            //noinspection unchecked
            tableItm.put(Constants.FeebackItemGUID, mStrGuide.toString());
            //noinspection unchecked
            tableItm.put(Constants.FeedbackType, mStrSelFeedBackType);

            if(mStrSelFeedBackType.equalsIgnoreCase(Constants.str_06)){
                tableItm.put(Constants.FeedbackTypeDesc, mStrOtherType);
            }else{
                tableItm.put(Constants.FeedbackTypeDesc, mStrSelFeedBackTypeDesc);
            }
            //noinspection unchecked
            tableItm.put(Constants.Remarks, mStrRemarks);

            if(mStrSelFeedBackType.equalsIgnoreCase(Constants.str_05)) {
                //noinspection unchecked
                tableItm.put(Constants.FeedbackSubTypeID, mStrSelFeedBackSubType);

                tableItm.put(Constants.FeedbackSubTypeDesc, mStrSelFeedBackSubTypeDesc);
            }else{
                tableItm.put(Constants.FeedbackSubTypeID, "");

                tableItm.put(Constants.FeedbackSubTypeDesc, "");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        arrtable.add(tableItm);

        tableHdr.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(arrtable));


        Constants.saveDeviceDocNoToSharedPref(FeedBackActivity.this, Constants.FeedbackList,doc_no);

        tableHdr.put(Constants.LOGINID, sharedPreferences.getString(Constants.username, "").toUpperCase());

        JSONObject jsonHeaderObject = new JSONObject(tableHdr);

        UtilDataVault.storeInDataVault(doc_no,jsonHeaderObject.toString());

        backToVisit();
    }
    private void backToVisit() {


        popUpText = getString(R.string.lbl_feed_back_created);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                FeedBackActivity.this, R.style.MyTheme);
        builder.setMessage(popUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {

                                    Dialog.cancel();
                                    navigateToRetDetailsActivity();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        LogManager.writeLogError("Error in FeedBack : " + e.getMessage());


        AlertDialog.Builder builder = new AlertDialog.Builder(
                FeedBackActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.error_occured_during_post)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    navigateToRetDetailsActivity();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();

    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {

        if (operation == Operation.Create.getValue()) {

            Set<String> set = new HashSet<>();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            set = sharedPreferences.getStringSet(Constants.FeedbackList, null);
            HashSet<String> setTemp = new HashSet<>();
            if (set != null && !set.isEmpty()) {
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    setTemp.add(itr.next().toString());
                }
            }

            setTemp.remove(doc_no);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(Constants.FeedbackList, setTemp);
            editor.commit();

            String store = null;
            try {
                LogonCore.getInstance().addObjectToStore(doc_no, "");
            } catch (LogonCoreException e) {
                e.printStackTrace();
            }

            if (!UtilConstants.isNetworkAvailable(FeedBackActivity.this)) {
                onNoNetwork();
            } else {
                OfflineManager.flushQueuedRequests(FeedBackActivity.this);
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {
            OfflineManager.refreshRequests(getApplicationContext(), Constants.VisitActivities, FeedBackActivity.this);
        } else if (operation == Operation.OfflineRefresh.getValue()) {

            popUpText = getString(R.string.Feedback_created);

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    FeedBackActivity.this, R.style.MyTheme);
            builder.setMessage(popUpText)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface Dialog,
                                        int id) {
                                    try {

                                        Dialog.cancel();
                                        navigateToRetDetailsActivity();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            });
            builder.show();
        }

    }

    private void onNoNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                FeedBackActivity.this, R.style.MyTheme);
        builder.setMessage(
                getString(R.string.alert_sync_cannot_be_performed))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        navigateToRetDetailsActivity();
                    }
                });

        builder.show();
    }
    /**
     * get distributor value
     */
    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
    }
    private void navigateToRetDetailsActivity(){
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(FeedBackActivity.this,RetailersDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        if(!Constants.OtherRouteNameVal.equalsIgnoreCase("")){
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }
        startActivity(intentNavPrevScreen);
    }
}
