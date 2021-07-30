package com.arteriatech.emami.attendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
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
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.emami.alerts.AlertsActivity;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.xscript.core.GUID;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

/**
 * Created by e10526 on 26-10-2016.
 *
 */
public class CreateAttendanceActivity extends AppCompatActivity implements UIListener, View.OnClickListener {
    private EditText editRemraks;
    private String[][] arrWorkType = Constants.arrWorkType;
    private String[][] arrAttendanceType = null;
    private String mStrSelFieldWorkTypeType = "",  mStrSelFullday = "",
            mStrFullDayRemarksFlag ="", mStrFirstHalfRemarksFlag ="", mStrSecondHalfRemarksFlag ="",
            mStrSelFirstHalf = "", mStrSelSecondHalf = "";
    private String mStrPopUpText;
    private ProgressDialog pdLoadDialog;
    TextView tv_lbl_fullday, tv_lbl_first_half, tv_lbl_second_half, tv_remarks_val;
    LinearLayout ll_full_day;
    LinearLayout ll_first_half, ll_second_half;
    private String mStrDefultFullDay = Constants.str_01;
    Spinner spAttendanceType;
    Spinner sp_full_day;
    Spinner sp_first_half;
    Spinner sp_second_half;

    ArrayList<String> alAssignColl=new ArrayList<>();
    ArrayList<String> alFlushColl=new ArrayList<>();
    String concatCollectionStr="";
    String concatFlushCollStr="";
    private long mLastClickTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_create);

        //Initialize action bar with back button(true/false)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_attendance));


        ll_full_day = (LinearLayout) findViewById(R.id.ll_full_day);
        ll_first_half = (LinearLayout) findViewById(R.id.ll_first_half);
        ll_second_half = (LinearLayout) findViewById(R.id.ll_second_half);
        tv_lbl_fullday = (TextView) findViewById(R.id.tv_lbl_fullday);
        tv_lbl_first_half = (TextView) findViewById(R.id.tv_lbl_first_half);
        tv_lbl_second_half = (TextView) findViewById(R.id.tv_lbl_second_half);
        tv_remarks_val = (TextView) findViewById(R.id.tv_remarks_val);
        tv_remarks_val.setText(getString(R.string.lbl_star));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!Constants.restartApp(CreateAttendanceActivity.this)) {
            initUI();
        }
    }

    /*Initializes user interface*/
    void initUI(){
        getAttendanceType();

        spAttendanceType = (Spinner) findViewById(R.id.sp_field_work_type);

        sp_full_day = (Spinner) findViewById(R.id.sp_full_day);
        sp_first_half = (Spinner) findViewById(R.id.sp_first_half);
        sp_second_half = (Spinner) findViewById(R.id.sp_second_half);


        ArrayAdapter<String> attendanceTypeAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrWorkType[1]);
        attendanceTypeAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spAttendanceType.setAdapter(attendanceTypeAdapter);
        spAttendanceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                spAttendanceType.setBackgroundResource(R.drawable.spinner_bg);
                mStrSelFieldWorkTypeType = arrWorkType[0][position];
                if (mStrSelFieldWorkTypeType.equalsIgnoreCase("")) {
                    ll_full_day.setVisibility(View.GONE);
                    tv_lbl_fullday.setVisibility(View.GONE);
                    ll_first_half.setVisibility(View.GONE);
                    ll_second_half.setVisibility(View.GONE);
                    tv_lbl_first_half.setVisibility(View.GONE);
                    tv_lbl_second_half.setVisibility(View.GONE);

                } else if (mStrSelFieldWorkTypeType.equalsIgnoreCase("01")) {

                    for (int i = 0; i < arrAttendanceType[1].length; i++) {
                        if (mStrDefultFullDay.equalsIgnoreCase(arrAttendanceType[0][i])) {
                            sp_full_day.setSelection(i);
                            break;
                        }
                    }

                    if (!mStrFullDayRemarksFlag.equalsIgnoreCase(Constants.X)) {
                        editRemraks.setBackgroundResource(R.drawable.edittext);
                        tv_remarks_val.setText("");
                    } else {
                        tv_remarks_val.setText(getString(R.string.lbl_star));
                    }
                    sp_full_day.setBackgroundResource(R.drawable.spinner_bg);
                    ll_full_day.setVisibility(View.VISIBLE);
                    tv_lbl_fullday.setVisibility(View.VISIBLE);
                    tv_lbl_fullday.setText(Constants.full_Day);
                    ll_first_half.setVisibility(View.GONE);
                    ll_second_half.setVisibility(View.GONE);
                    tv_lbl_first_half.setVisibility(View.GONE);
                    tv_lbl_second_half.setVisibility(View.GONE);

                } else {
                    tv_remarks_val.setText(getString(R.string.lbl_star));
                    editRemraks.setBackgroundResource(R.drawable.edittext);
                    sp_first_half.setSelection(0);
                    sp_second_half.setSelection(0);
                    sp_first_half.setBackgroundResource(R.drawable.spinner_bg);
                    sp_second_half.setBackgroundResource(R.drawable.spinner_bg);

                    ll_full_day.setVisibility(View.GONE);
                    tv_lbl_fullday.setVisibility(View.GONE);
                    ll_first_half.setVisibility(View.VISIBLE);
                    ll_second_half.setVisibility(View.VISIBLE);
                    tv_lbl_first_half.setVisibility(View.VISIBLE);
                    tv_lbl_second_half.setVisibility(View.VISIBLE);

                    tv_lbl_first_half.setText(Constants.first_half);
                    tv_lbl_second_half.setText(Constants.second_half);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        ArrayAdapter<String> fullDayAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrAttendanceType[1]);
        fullDayAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_full_day.setAdapter(fullDayAdapter);

        for (int i = 0; i < arrAttendanceType[1].length; i++) {
            if (mStrDefultFullDay.equalsIgnoreCase(arrAttendanceType[0][i])) {
                sp_full_day.setSelection(i);
                break;
            }
        }

        sp_full_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                sp_full_day.setBackgroundResource(R.drawable.spinner_bg);
                mStrSelFullday = arrAttendanceType[0][position];
                mStrFullDayRemarksFlag = arrAttendanceType[2][position];

                if (!mStrFullDayRemarksFlag.equalsIgnoreCase(Constants.X)) {
                    editRemraks.setBackgroundResource(R.drawable.edittext);
                    tv_remarks_val.setText("");
                } else {
                    tv_remarks_val.setText(getString(R.string.lbl_star));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        ArrayAdapter<String> firstHalfAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrAttendanceType[1]);
        firstHalfAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_first_half.setAdapter(firstHalfAdapter);
        String mStrNoneSplit = Constants.str_00;
        for (int i = 0; i < arrAttendanceType[1].length; i++) {
            if (mStrNoneSplit.equalsIgnoreCase(arrAttendanceType[0][i])) {
                sp_first_half.setSelection(i);
                break;
            }
        }
        sp_first_half.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                sp_first_half.setBackgroundResource(R.drawable.spinner_bg);
                mStrSelFirstHalf = arrAttendanceType[0][position];
                mStrFirstHalfRemarksFlag = arrAttendanceType[2][position];

                if (!mStrSelFieldWorkTypeType.equalsIgnoreCase("01")) {
                    if (mStrFirstHalfRemarksFlag.equalsIgnoreCase(Constants.X) || mStrSecondHalfRemarksFlag.equalsIgnoreCase(Constants.X)) {
                        tv_remarks_val.setText(getString(R.string.lbl_star));
                    } else {
                        editRemraks.setBackgroundResource(R.drawable.edittext);
                        tv_remarks_val.setText("");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        ArrayAdapter<String> secondHalfAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrAttendanceType[1]);
        secondHalfAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_second_half.setAdapter(secondHalfAdapter);
        for (int i = 0; i < arrAttendanceType[1].length; i++) {
            if (mStrNoneSplit.equalsIgnoreCase(arrAttendanceType[0][i])) {
                sp_second_half.setSelection(i);
                break;
            }
        }
        sp_second_half.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                sp_second_half.setBackgroundResource(R.drawable.spinner_bg);
                mStrSelSecondHalf = arrAttendanceType[0][position];
                mStrSecondHalfRemarksFlag = arrAttendanceType[2][position];

                if (!mStrSelFieldWorkTypeType.equalsIgnoreCase("01")) {
                    if (mStrFirstHalfRemarksFlag.equalsIgnoreCase(Constants.X) || mStrSecondHalfRemarksFlag.equalsIgnoreCase(Constants.X)) {
                        tv_remarks_val.setText(getString(R.string.lbl_star));
                    } else {
                        editRemraks.setBackgroundResource(R.drawable.edittext);
                        tv_remarks_val.setText("");
                    }
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


        if (!mStrFullDayRemarksFlag.equalsIgnoreCase(Constants.X)) {
            editRemraks.setBackgroundResource(R.drawable.edittext);
            tv_remarks_val.setText("");
        } else {
            tv_remarks_val.setText(getString(R.string.lbl_star));
        }
    }

    //Gets attendances type from offline manager
    private void getAttendanceType() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.EntityType
                    + " eq 'Attendance' &$orderby=" + Constants.DESCRIPTION + " asc";
            arrAttendanceType = OfflineManager.getConfigListAttendance(mStrConfigQry);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
//        arrAttendanceType = new String[3][1];
//        arrAttendanceType[0][0] = "01";
//        arrAttendanceType[1][0] = "FieldWork";
//        arrAttendanceType[2][0] = "";
        if (arrAttendanceType == null) {
            arrAttendanceType = new String[3][1];
            arrAttendanceType[0][0] = "";
            arrAttendanceType[1][0] = "";
            arrAttendanceType[2][0] = "";
        }else {
            arrAttendanceType = Constants.CheckForOtherInConfigValue(arrAttendanceType);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_attendance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_attendance_save:
                onSave(CreateAttendanceActivity.this);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    /*Starts Attendance*/
    private void onLoadDialog() {

            try {
                new LoadingData().execute();
            } catch (Exception e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
    }

    /*Validating Data*/
    private boolean validateData() {

        boolean hasError = false;

        // Full Day
        if (mStrSelFieldWorkTypeType.equalsIgnoreCase("01")) {
            // Full day attendance type = None
            if (mStrSelFullday.equalsIgnoreCase("00") && mStrSelFullday.equalsIgnoreCase("")) {
                // error
                sp_full_day.setBackgroundResource(R.drawable.error_spinner);
                hasError = true;

                if(mStrFullDayRemarksFlag.equalsIgnoreCase(Constants.X)) {
                    if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                        // error
                        editRemraks.setBackgroundResource(R.drawable.edittext_border);
                        hasError = true;
                    }
                }
            } else if (!mStrSelFullday.equalsIgnoreCase("01")) {

                if(mStrFullDayRemarksFlag.equalsIgnoreCase(Constants.X)) {
                    if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                        // error
                        editRemraks.setBackgroundResource(R.drawable.edittext_border);
                        hasError = true;
                    }
                }
            }
        } else {
            if (mStrSelFirstHalf.equalsIgnoreCase("00")) {
                // error
                hasError = true;
                sp_first_half.setBackgroundResource(R.drawable.error_spinner);
            }
            if (mStrSelSecondHalf.equalsIgnoreCase("00")) {
                // error
                hasError = true;
                sp_second_half.setBackgroundResource(R.drawable.error_spinner);
            }
            if(mStrFirstHalfRemarksFlag.equalsIgnoreCase(Constants.X) || mStrSecondHalfRemarksFlag.equalsIgnoreCase(Constants.X)) {
                if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                    // error
                    hasError = true;
                    editRemraks.setBackgroundResource(R.drawable.edittext_border);
                }
            }
        }


        return hasError;
    }

    /*Saves start attendance data into store*/
    private void onSave(final Context mContext) {
       /* if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();*/
        mStrPopUpText = getString(R.string.msg_marking_attendance);
        pdLoadDialog = Constants.showProgressDialog(mContext,"",mStrPopUpText);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Constants.HashMapEntityVal.clear();
                try {
                    OfflineManager.getMaxStartDateTime(Constants.Attendances  +" ?$orderby=" + Constants.StartDate + "%20desc &$top=1");
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }

                if(!Constants.HashMapEntityVal.isEmpty()) {
                    if (Constants.isEndateAndEndTimeValid(UtilConstants.getConvertCalToStirngFormat((Calendar)
                            Constants.HashMapEntityVal.get(Constants.StartDate)), Constants.HashMapEntityVal.get(Constants.StartTime) + "")) {
                        onValidDateTime();
                    }else{
                        // display error pop up
                        onCloseProgressDialog();
                        UtilConstants.showAlert(getString(R.string.msg_start_date_should_not_be_past_date), CreateAttendanceActivity.this);
                    }
                }else{
                    onValidDateTime();
                }
            }
        }, 100);
    }

    private void onValidDateTime(){
        if (!validateData()) {
           /* if (Constants.onGpsCheck(CreateAttendanceActivity.this)) {
                if (UtilConstants.getLocation(CreateAttendanceActivity.this)) {
                    onLoadDialog();
                }else {
                    onCloseProgressDialog();
                }
            }else {
                onCloseProgressDialog();
            }*/
            onCloseProgressDialog();
            pdLoadDialog = Constants.showProgressDialog(CreateAttendanceActivity.this, "", getString(R.string.checking_pemission));
            LocationUtils.checkLocationPermission(CreateAttendanceActivity.this, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    Log.d("location fun","1");
                    onCloseProgressDialog();
                    if (status) {
                        locationPerGranted();
                    }
                }
            });
        }else{
            onCloseProgressDialog();
            Constants.customAlertMessage(this, getString(R.string.validation_plz_enter_mandatory_flds));
        }
    }
    private void locationPerGranted(){
        pdLoadDialog = Constants.showProgressDialog(CreateAttendanceActivity.this,"",getString(R.string.gps_progress));
        Constants.getLocation(CreateAttendanceActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                onCloseProgressDialog();
                if(status){
                    onLoadDialog();
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
                    LocationUtils.checkLocationPermission(CreateAttendanceActivity.this, new LocationInterface() {
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
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LocationUtils.REQUEST_CHECK_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                locationPerGranted();
            }
        }
    }

    /*displays alert with message*/
    public void displayPopUpMsg() {
        AlertDialog.Builder alertDialogAlerts = new AlertDialog.Builder(CreateAttendanceActivity.this, R.style.MyTheme);
        alertDialogAlerts.setMessage(mStrPopUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    onAlerts();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        alertDialogAlerts.show();
    }


    private void onAlerts() {
        Intent intentMainmenu = new Intent(CreateAttendanceActivity.this,
                    AlertsActivity.class);
            intentMainmenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentMainmenu);
    }


    @Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception,CreateAttendanceActivity.this);
        if (errorBean.hasNoError()) {
            try {
                mStrPopUpText = getString(R.string.err_msg_concat,getString(R.string.lbl_attence_start),exception.getMessage());
            } catch (Exception e) {
                mStrPopUpText = getString(R.string.msg_start_upd_sync_error);
            }
            if (operation == Operation.Create.getValue()) {
                Constants.isSync = false;
                onCloseProgressDialog();
                displayPopUpMsg();
            }else if(operation == Operation.OfflineFlush.getValue() ){
                Constants.isSync = false;
                onCloseProgressDialog();
                displayPopUpMsg();
            }else if(operation == Operation.OfflineRefresh.getValue() ){
                Constants.isSync = false;
                onCloseProgressDialog();
                displayPopUpMsg();
            }else {
                Constants.isSync = false;
                onCloseProgressDialog();
                displayPopUpMsg();
            }
        }else{
            Constants.isSync = false;

            if(errorBean.isStoreFailed()){
                if(!OfflineManager.isOfflineStoreOpen()) {
                    mStrPopUpText = getString(R.string.app_loading);
                    try {
                        new LoadingData().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    onCloseProgressDialog();
                    mStrPopUpText = Constants.makeMsgReqError(errorBean.getErrorCode(),CreateAttendanceActivity.this,false);
                    displayPopUpMsg();
                }
            }else{
                onCloseProgressDialog();
                mStrPopUpText = Constants.makeMsgReqError(errorBean.getErrorCode(),CreateAttendanceActivity.this,false);
                displayPopUpMsg();
            }
        }


    }

    private void onCloseProgressDialog(){
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {

        if (operation == Operation.Create.getValue()) {
            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                if (Constants.iSAutoSync) {
                    onCloseProgressDialog();
                    mStrPopUpText = getString(R.string.alert_auto_sync_is_progress);
                    displayPopUpMsg();
                } else {
                    Constants.isSync = true;

                    Constants.Entity_Set.clear();
                    Constants.AL_ERROR_MSG.clear();

                    alFlushColl = Constants.getPendingList();
                    alAssignColl = Constants.getRefreshList();
                    concatFlushCollStr = UtilConstants.getConcatinatinFlushCollectios(alFlushColl);
                    try {
                        OfflineManager.flushQueuedRequests(CreateAttendanceActivity.this, concatFlushCollStr);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                onCloseProgressDialog();
                mStrPopUpText = getString(R.string.no_network_conn);
                displayPopUpMsg();
            }

        }else if(operation == Operation.OfflineFlush.getValue() ){
            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                if(!Constants.isSpecificCollTodaySyncOrNot(Constants.getLastSyncDate(Constants.SYNC_TABLE, Constants.Collections,
                        Constants.CPStockItems, Constants.TimeStamp,CreateAttendanceActivity.this))){
                    addCPStockItemToArrayList();
                }
                try {
                    concatCollectionStr = Constants.getConcatinatinFlushCollectios(alAssignColl);
                    OfflineManager.refreshRequests(getApplicationContext(), concatCollectionStr, CreateAttendanceActivity.this);
                } catch (OfflineODataStoreException e) {
                    TraceLog.e(Constants.SyncOnRequestSuccess, e);
                }
            }else{
                onCloseProgressDialog();
                mStrPopUpText = getString(R.string.data_conn_lost_during_sync);
                displayPopUpMsg();
            }


        } else if (operation == Operation.OfflineRefresh.getValue()){
            Constants.updateLastSyncTimeToTable(alAssignColl);
            onCloseProgressDialog();
            mStrPopUpText =getString(R.string.dialog_day_started);
            Constants.iSAutoSync = false;
            Constants.isSync = false;
            UtilConstants.dialogBoxWithCallBack(CreateAttendanceActivity.this, "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                    if (!AppUpgradeConfig.getUpdateAvailability(OfflineManager.offlineStore,CreateAttendanceActivity.this,"",true)) {
                        onAlerts();
                    }
                }
            });
//            displayPopUpMsg();
        }else if (operation == Operation.GetStoreOpen.getValue() &&   OfflineManager.isOfflineStoreOpen()) {
            try {
                OfflineManager.getAuthorizations(getApplicationContext());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(CreateAttendanceActivity.this);
            onCloseProgressDialog();
            UtilConstants.dialogBoxWithCallBack(CreateAttendanceActivity.this, "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                    if (!AppUpgradeConfig.getUpdateAvailability(OfflineManager.offlineStore,CreateAttendanceActivity.this,"",true)) {
                        onAlerts();
                    }
                }
            });
        }

    }

    private void addCPStockItemToArrayList(){
        if(!alAssignColl.contains(Constants.CPStockItems)){
            alAssignColl.add(Constants.CPStockItems);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                onBackPressed();
                break;
            case R.id.tv_submit:
                onSave(CreateAttendanceActivity.this);
                break;
        }
    }

    boolean closeScreen = false;

    @Override
    public void onBackPressed() {

        if (closeScreen) {
            super.onBackPressed();
        } else {
            mStrPopUpText = getString(R.string.msg_exit_attendance);
            /*
             * ToDo disply alert dialog for exit current activity
             */
            AlertDialog.Builder alertDailogExitAttendance = new AlertDialog.Builder(CreateAttendanceActivity.this, R.style.MyTheme);
            alertDailogExitAttendance.setMessage(mStrPopUpText)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface Dialog,
                                        int id) {
                                    try {
                                        Dialog.cancel();
                                        closeScreen = true;
                                        onBackPressed();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            }).setNegativeButton(R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface Dialog,
                                int id) {
                            try {
                                Dialog.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    });
            alertDailogExitAttendance.show();
        }
    }

    /*AsyncTask to store attendance data into offline store*/
    private class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(CreateAttendanceActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                if(!OfflineManager.isOfflineStoreOpen()) {
                    try {
                        OfflineManager.openOfflineStore(CreateAttendanceActivity.this, CreateAttendanceActivity.this);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                }else {
                    onSaveDayStartData();
                }
            } catch (InterruptedException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /*Save Day start data on offline store*/
    private void onSaveDayStartData() {
        try {


            Constants.MapEntityVal.clear();
            GUID guid = GUID.newRandom();
            Hashtable hashTableAttendanceValues = new Hashtable();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginIdVal = sharedPreferences.getString(Constants.username, "");

            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.AttendanceGUID, guid.toString());
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartDate, UtilConstants.getNewDateTimeFormat());

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

            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartTime, oDataDuration);
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartLat, BigDecimal.valueOf(UtilConstants.latitude));
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.StartLong, BigDecimal.valueOf(UtilConstants.longitude));
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndLat, "");
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndLong, "");
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndDate, "");
            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.EndTime, "");

            //noinspection unchecked
            hashTableAttendanceValues.put(Constants.Remarks, editRemraks.getText().toString() != null ? editRemraks.getText().toString() : "");

            if (mStrSelFieldWorkTypeType.equalsIgnoreCase("01")) {
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.AttendanceTypeH1, mStrSelFullday);
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.AttendanceTypeH2, mStrSelFullday);
            } else {
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.AttendanceTypeH1, mStrSelFirstHalf);
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.AttendanceTypeH2, mStrSelSecondHalf);
            }


            hashTableAttendanceValues.put(Constants.SPGUID, Constants.getSPGUID(Constants.SalesPersons, Constants.SPGUID));

            hashTableAttendanceValues.put(Constants.SetResourcePath, "guid'" + guid.toString() + "'");

            /*SharedPreferences sharedPreferencesVal = getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferencesVal.edit();
            editor.putInt(Constants.VisitSeqId, 0);
            editor.commit();*/
            try {
                //noinspection unchecked
                OfflineManager.createAttendance(hashTableAttendanceValues, CreateAttendanceActivity.this);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }

        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }
}
