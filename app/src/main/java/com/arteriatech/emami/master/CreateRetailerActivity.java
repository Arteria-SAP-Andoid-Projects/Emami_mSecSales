package com.arteriatech.emami.master;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.arteriatech.emami.dbstock.DMSDivionBean;
import com.arteriatech.emami.main.MainMenu;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.xscript.core.GUID;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by ${e10526} on ${19-03-2016}.
 */
public class CreateRetailerActivity extends AppCompatActivity implements UIListener {
    private TextView btnDateOfBirth;
    private TextView btnAnnversary;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private int mYearAnnversary = 0;
    private int mMonthAnnversary = 0;
    private int mDayAnnversary = 0;
    private String dateOfBirth = "", dateOfAnnversary = "";
    private static final int DATE_DIALOG_ID = 0;
    private static final int DATE_DIALOG_ID_ANNVERSARY = 1;
    private Spinner spnrRetailerType, spnrRetailerProfile, sp_classfication, sp_week_off, sp_tax_reg_status;
    private Spinner spnrState;
    private EditText et_outlet_name, et_owner_name, et_address,
            et_district, et_city, et_land_mark, et_pin_code, et_mobile_number,
            et_email_id, et_pan_no, et_vat_no, et_latitude, et_longitude, et_tax_one;
    private ArrayList<DMSDivionBean> distListDms = null;
    private String[][] arrayRetProfileVal, arrayRetClassficationVal, arrayWeekOffVal, arrayTaxRegStatus;
    private String[][] arrayRetTypeVal;
    private String[][] arrayStateVal, arrayCountryVal;
    private String[][] mArrayDistributors;
    private String SPGUID = "";
    private String CPGUID = "";
    private String selRetTypeCode = "", selRetTypeDesc = "", selRetProfileCode = "", selRetProfileDesc = "", selStateCode = "", selStateDesc = "",
            selDistributorCode = "", selDistributorDesc = "", selCityCode = "", selCityDesc = "", selDistrictCode = "", selDistrictDesc = "", ParentName = "", ParentType = "", selRetClassficationCode = "", selRetClassficationDesc = "", selRetWeekOffCode = "", selRetWeekOffDesc = "", selTaxRegStaCode = "", selTaxRegStaDesc = "";
    private String popUpText = "";
    private ProgressDialog pdLoadDialog;
    private String selAddress = "";
    private String selDistrict = "";
    private String selCity = "";
    private String selLandMark = "";
    private String selPinCode = "";
    private String selMobileNo = "";
    private String selEmailID = "";
    private String selPan = "";
    private String selVatNo = "", selTaxOne = "";
    private String selOutletName = "";
    private String selOwnerName = "";
    private String selRetailerProfile = "", selCountryID = "", mStrCpGuid = "";
    private double selLatitude = 0.0;
    private double selLongitude = 0.0;
    private Context context;
    private TableRow tr_vat_tin;
    private Spinner sp_distributor;
    DMSDivionBean distName = new DMSDivionBean();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar without back button(false)
        ActionBarView.initActionBarView(this, true, getString(R.string.title_create_retailer));

        setContentView(R.layout.activity_create_retailer);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        context = getApplicationContext();
        if (!Constants.restartApp(CreateRetailerActivity.this)) {
            initUI();
        }
    }

    /*Initializes UI*/
    void initUI() {
        btnDateOfBirth = (TextView) findViewById(R.id.btn_date_of_birth);
        btnAnnversary = (TextView) findViewById(R.id.btn_date_of_annverisary);
        spnrRetailerType = (Spinner) findViewById(R.id.sp_retailer_type);
        spnrRetailerProfile = (Spinner) findViewById(R.id.sp_retailer_profile);
        sp_classfication = (Spinner) findViewById(R.id.sp_classfication);
        sp_week_off = (Spinner) findViewById(R.id.sp_week_off);
        sp_tax_reg_status = (Spinner) findViewById(R.id.sp_tax_reg_status);
        tr_vat_tin = (TableRow) findViewById(R.id.tr_vat_tin);

        TextView tv_retailer_code = (TextView) findViewById(R.id.tv_retailer_code);
        spnrState = (Spinner) findViewById(R.id.sp_state);
        sp_distributor = (Spinner) findViewById(R.id.sp_dist_name);
        et_outlet_name = (EditText) findViewById(R.id.et_outlet_name);
        et_outlet_name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        et_owner_name = (EditText) findViewById(R.id.et_owner_name);
        et_owner_name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        et_address = (EditText) findViewById(R.id.et_address);
        et_address.setFilters(new InputFilter[]{new InputFilter.LengthFilter(35)});
        et_district = (EditText) findViewById(R.id.et_district);
        et_district.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        et_city = (EditText) findViewById(R.id.et_city);
        et_city.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        et_tax_one = (EditText) findViewById(R.id.et_tax_one);
        et_tax_one.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        et_land_mark = (EditText) findViewById(R.id.et_land_mark);
        et_land_mark.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        et_pin_code = (EditText) findViewById(R.id.et_pin_code);
        et_pin_code.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_pin_code.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        et_mobile_number = (EditText) findViewById(R.id.et_mobile_number);
        et_mobile_number.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_mobile_number.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        et_email_id = (EditText) findViewById(R.id.et_email_id);
        et_email_id.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        et_email_id.setFilters(new InputFilter[]{new InputFilter.LengthFilter(45)});
        et_pan_no = (EditText) findViewById(R.id.et_pan_no);
        et_pan_no.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        et_pan_no.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        et_vat_no = (EditText) findViewById(R.id.et_vat_no);
        et_vat_no.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        tr_vat_tin.setVisibility(View.GONE);

        ImageView btn_lat_long = (ImageView) findViewById(R.id.btn_lat_long);
        et_latitude = (EditText) findViewById(R.id.et_latitude);
        et_longitude = (EditText) findViewById(R.id.et_longitude);

        et_latitude.setText(String.valueOf(Constants.latitude));
        et_longitude.setText(String.valueOf(Constants.longitude));

        et_latitude.setFocusable(false);
        et_latitude.setClickable(false);
        et_longitude.setFocusable(false);
        et_longitude.setClickable(false);

        btn_lat_long.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation();
            }
        });
        getDistributorDMS();
        getDistributors();
        displayDistributorVal();
        getRetailerType();
        getRetailerProfile();
        getRetailerClassfication();
        getRetailerWeekOff();
        getRetailerTaxRexSatus();
        getCountry();


        if (mArrayDistributors == null) {
            mArrayDistributors = new String[6][1];
            mArrayDistributors[0][0] = "";
            mArrayDistributors[1][0] = "";
            mArrayDistributors[2][0] = "";
            mArrayDistributors[3][0] = "";
            mArrayDistributors[4][0] = "";
            mArrayDistributors[5][0] = "";
        } else {
            if (mArrayDistributors[0].length > 0) {
                selDistributorCode = mArrayDistributors[0][0];
                selDistributorDesc = mArrayDistributors[1][0];
                ParentType = mArrayDistributors[3][0];
                SPGUID = mArrayDistributors[4][0];
                CPGUID = mArrayDistributors[5][0];
            }
        }


        tv_retailer_code.setText(selDistributorDesc);

        try {
            selCountryID = arrayCountryVal[0][0];
        } catch (Exception e) {
            e.printStackTrace();
        }


        getStates(selCountryID);

        if (arrayRetTypeVal == null) {
            arrayRetTypeVal = new String[2][1];
            arrayRetTypeVal[0][0] = "";
            arrayRetTypeVal[1][0] = "";
        }

        ArrayAdapter<String> retailerTypeAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrayRetTypeVal[1]);
        retailerTypeAdapter.setDropDownViewResource(R.layout.spinnerinside);

//        final CustomSpinnerAdapter customSpinnerAdapter=new CustomSpinnerAdapter(this,arrayRetTypeVal[1]);
        spnrRetailerType.setAdapter(retailerTypeAdapter);
        spnrRetailerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
//                customSpinnerAdapter.setSelection(position);
                selRetTypeCode = arrayRetTypeVal[0][position];
                selRetTypeDesc = arrayRetTypeVal[1][position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        if (arrayRetProfileVal == null) {
            arrayRetProfileVal = new String[2][1];
            arrayRetProfileVal[0][0] = "";
            arrayRetProfileVal[1][0] = "";
        }

        ArrayAdapter<String> retailerProfileAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrayRetProfileVal[1]);
        retailerProfileAdapter.setDropDownViewResource(R.layout.spinnerinside);
//        final CustomSpinnerAdapter retailerProfileAdapter=new CustomSpinnerAdapter(this,arrayRetProfileVal[1]);
        spnrRetailerProfile.setAdapter(retailerProfileAdapter);
        spnrRetailerProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
//                retailerProfileAdapter.setSelection(position);
                selRetProfileCode = arrayRetProfileVal[0][position];
                selRetProfileDesc = arrayRetProfileVal[1][position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        displayRetCalssfication();
        displayRetWeekOff();
        displayTaxTaxStatus();
        final Calendar calDob = Calendar.getInstance();
        mYear = calDob.get(Calendar.YEAR);
        mMonth = calDob.get(Calendar.MONTH);
        mDay = calDob.get(Calendar.DAY_OF_MONTH);

        final Calendar calAnnversary = Calendar.getInstance();
        mYearAnnversary = calAnnversary.get(Calendar.YEAR);
        mMonthAnnversary = calAnnversary.get(Calendar.MONTH);
        mDayAnnversary = calAnnversary.get(Calendar.DAY_OF_MONTH);

//        btnDateOfBirth.setText(R.string.lbl_select_dob);

        btnDateOfBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //noinspection ConstantConditions
                onDatePickerDialog(DATE_DIALOG_ID).show();
            }
        });
        btnAnnversary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //noinspection ConstantConditions
                onDatePickerDialog(DATE_DIALOG_ID_ANNVERSARY).show();
            }
        });
    }

    private void displayRetCalssfication() {
        ArrayAdapter<String> retailerProfileAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrayRetClassficationVal[1]);
        retailerProfileAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_classfication.setAdapter(retailerProfileAdapter);
        sp_classfication.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selRetClassficationCode = arrayRetClassficationVal[0][position];
                selRetClassficationDesc = arrayRetClassficationVal[1][position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void displayRetWeekOff() {
        ArrayAdapter<String> retailerProfileAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrayWeekOffVal[1]);
        retailerProfileAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_week_off.setAdapter(retailerProfileAdapter);
        sp_week_off.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selRetWeekOffCode = arrayWeekOffVal[0][position];
                selRetWeekOffDesc = arrayWeekOffVal[1][position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void displayTaxTaxStatus() {
        ArrayAdapter<String> retailerProfileAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrayTaxRegStatus[1]);
        retailerProfileAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_tax_reg_status.setAdapter(retailerProfileAdapter);
        sp_tax_reg_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selTaxRegStaCode = arrayTaxRegStatus[0][position];
                selTaxRegStaDesc = arrayTaxRegStatus[1][position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateLocation() {
        pdLoadDialog = Constants.showProgressDialog(CreateRetailerActivity.this, "", getString(R.string.checking_pemission));
        LocationUtils.checkLocationPermission(CreateRetailerActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                closeProgressDialog();
                if (status) {
                    locationPerGranted();
                }
            }
        });
    }

    private void locationPerGranted() {
        pdLoadDialog = Constants.showProgressDialog(CreateRetailerActivity.this, "", getString(R.string.gps_progress));
        Constants.getLocation(CreateRetailerActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closeProgressDialog();
                if (status) {
                    et_latitude.setText(String.valueOf(UtilConstants.latitude));
                    et_longitude.setText(String.valueOf(UtilConstants.longitude));
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
                    LocationUtils.checkLocationPermission(CreateRetailerActivity.this, new LocationInterface() {
                        @Override
                        public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                            if (status) {
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
        if (requestCode == LocationUtils.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationPerGranted();
            }
        }
    }

    private void closeProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        LogManager.writeLogError(getString(R.string.Error_in_create_retailer) + e.getMessage());
        Toast.makeText(CreateRetailerActivity.this, getString(R.string.err_odata_unexpected, e.getMessage()),
                Toast.LENGTH_LONG).show();
        onCloseProgDialog();
        Intent intBack = new Intent(CreateRetailerActivity.this, MainMenu.class);
        intBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intBack);
    }

    private void onCloseProgDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        String message = "";
        if (operation == Operation.Create.getValue()) {
            onCloseProgDialog();
            popUpDisplayed();
        } else if (operation == Operation.Update.getValue()) {
            OfflineManager.flushQueuedRequests(CreateRetailerActivity.this);
        } else if (operation == Operation.OfflineFlush.getValue()) {
            OfflineManager.refreshRequests(getApplicationContext(), Constants.ChannelPartners, CreateRetailerActivity.this);
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            pdLoadDialog.dismiss();
            String mStrCpNo = "";
            String qryStr = Constants.ChannelPartners + "?$filter=" + Constants.CPGUID + " eq 'guid'" + mStrCpGuid + "' ";
            try {
                mStrCpNo = OfflineManager.getRetailerNo(qryStr);

            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            popUpText = "Retailer " + mStrCpNo + " # created";


            AlertDialog.Builder builder = new AlertDialog.Builder(
                    CreateRetailerActivity.this, R.style.MyTheme);
            builder.setMessage(popUpText)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface Dialog,
                                        int id) {
                                    try {

                                        Dialog.cancel();

                                        Intent intBack = new Intent(CreateRetailerActivity.this, MainMenu.class);
                                        intBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intBack);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            });
            builder.show();


        }
    }

    private void popUpDisplayed() {
        popUpText = getString(R.string.Retailer_created);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                CreateRetailerActivity.this, R.style.MyTheme);
        builder.setMessage(popUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {

                                    Dialog.cancel();

                                    Intent intBack = new Intent(CreateRetailerActivity.this, MainMenu.class);
                                    intBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intBack);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();
    }

    @SuppressLint("NewApi")
    private Dialog onDatePickerDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog datePicker = new DatePickerDialog(this, mDateSetListener,
                        mYear, mMonth, mDay);
                Calendar c = Calendar.getInstance();
                Date newDate = c.getTime();
                datePicker.getDatePicker().setMaxDate(newDate.getTime());
                return datePicker;
            case DATE_DIALOG_ID_ANNVERSARY:
                DatePickerDialog datePickerAnnversary = new DatePickerDialog(this, mDateSetAnnversaryListener,
                        mYearAnnversary, mMonthAnnversary, mDayAnnversary);
                Calendar cal = Calendar.getInstance();
                Date newDateAnnv = cal.getTime();
                datePickerAnnversary.getDatePicker().setMaxDate(newDateAnnv.getTime());
                return datePickerAnnversary;
        }
        return null;
    }

    private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker v, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String mon = "";
            String day = "";
            int mnt = 0;
            mnt = mMonth + 1;
            if (mnt < 10)
                mon = "0" + mnt;
            else
                mon = "" + mnt;
            day = "" + mDay;
            if (mDay < 10)
                day = "0" + mDay;
            dateOfBirth = mYear + "-" + mon + "-" + day;
            btnDateOfBirth.setText(new StringBuilder().append(mDay)
                    .append("/").append(mon)
                    .append("/").append("").append(mYear));
        }
    };

    private final DatePickerDialog.OnDateSetListener mDateSetAnnversaryListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker v, int year, int monthOfYear,
                              int dayOfMonth) {
            mYearAnnversary = year;
            mMonthAnnversary = monthOfYear;
            mDayAnnversary = dayOfMonth;
            String mon = "";
            String day = "";
            int mnt = 0;
            mnt = mMonthAnnversary + 1;
            if (mnt < 10)
                mon = "0" + mnt;
            else
                mon = "" + mnt;
            day = "" + mDayAnnversary;
            if (mDayAnnversary < 10)
                day = "0" + mDayAnnversary;
            dateOfAnnversary = mYearAnnversary + "-" + mon + "-" + day;
            btnAnnversary.setText(new StringBuilder().append(mDayAnnversary)
                    .append("/").append(mon)
                    .append("/").append("").append(mYearAnnversary));
        }
    };


    public void showPopup(View v) {
        UtilConstants.showPopup(getApplicationContext(), v, CreateRetailerActivity.this, R.menu.menu_create_retailer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_create_retailer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_device_retailer:
                onDeviceRetailer();
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_save:
                onSave();
                break;

        }
        return true;
    }

    /*Returns sales persons list*/
    private void getDistributors() {

        String qryStr = Constants.SalesPersons;
        try {
            mArrayDistributors = OfflineManager.getDistributorList(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }

    /*Returns Retailer type from value help table to create retailer*/
    private void getRetailerType() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'CPTypeID'";
            arrayRetTypeVal = OfflineManager.getConfigList(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    /*Returns profile type for retailer*/
    private void getRetailerProfile() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'RetailerProfile'";
            arrayRetProfileVal = OfflineManager.getConfigListWithNone(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    private void getRetailerClassfication() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.CPGroup4 + "' and " + Constants.EntityType + " eq '" + Constants.SchemeSalesArea + "' ";
            arrayRetClassficationVal = OfflineManager.getConfigListWithNone(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    private void getRetailerWeekOff() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.WeeklyOff + "' and " + Constants.EntityType + " eq '" + Constants.ChannelPartner + "' ";
            arrayWeekOffVal = OfflineManager.getConfigListWithNone(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    private void getRetailerTaxRexSatus() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.TaxRegStatus + "' and " + Constants.EntityType + " eq '" + Constants.ChannelPartner + "' ";
            arrayTaxRegStatus = OfflineManager.getConfigListWithNone(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    /*Returns Countries */
    private void getCountry() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'Country' ";
            arrayCountryVal = OfflineManager.getConfigList(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

        if (arrayCountryVal == null) {
            arrayCountryVal = new String[2][1];
            arrayCountryVal[0][0] = "";
            arrayCountryVal[1][0] = "";
        }
    }

    /*Returns States*/
    private void getStates(String parentID) {
        try {

            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'StateID' &$orderby=" + Constants.Description + "%20asc ";
            arrayStateVal = OfflineManager.getConfigList(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

        if (arrayStateVal == null) {
            arrayStateVal = new String[2][1];
            arrayStateVal[0][0] = "";
            arrayStateVal[1][0] = "";
        }

        final ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrayStateVal[1]);
        stateAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spnrState.setAdapter(stateAdapter);

        spnrState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selStateCode = arrayStateVal[0][position];
                selStateDesc = arrayStateVal[1][position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /*on Device Retailers*/
    private void onDeviceRetailer() {
        Intent intentRetailerDetails = new Intent(this, DeviceRetailersActivity.class);
        startActivity(intentRetailerDetails);
    }

    /*Saves newly created retailer into Offline DB*/
    private void onSave() {
        selLatitude = Double.parseDouble(et_latitude.getText().toString().equalsIgnoreCase("") ? "0.0" : et_latitude.getText().toString());
        selLongitude = Double.parseDouble(et_longitude.getText().toString().equalsIgnoreCase("") ? "0.0" : et_longitude.getText().toString());
        if (validateData()) {
            selAddress = et_address.getText().toString();
            selDistrict = et_district.getText().toString();
            selCity = et_city.getText().toString();
            selLandMark = et_land_mark.getText().toString().equalsIgnoreCase("") ? "" : et_land_mark.getText().toString();
            selTaxOne = et_tax_one.getText() != null ? et_tax_one.getText().toString() : "";
            selPinCode = et_pin_code.getText().toString();
            selMobileNo = et_mobile_number.getText().toString();
            selEmailID = et_email_id.getText().toString().equalsIgnoreCase("") ? "" : et_email_id.getText().toString();
            selPan = et_pan_no.getText().toString().equalsIgnoreCase("") ? "" : et_pan_no.getText().toString();
            selVatNo = et_vat_no.getText().toString().equalsIgnoreCase("") ? "" : et_vat_no.getText().toString();
            selOutletName = et_outlet_name.getText().toString();
            selOwnerName = et_owner_name.getText().toString();
            selLatitude = Double.parseDouble(et_latitude.getText().toString().equalsIgnoreCase("") ? "0.0" : et_latitude.getText().toString());
            selLongitude = Double.parseDouble(et_longitude.getText().toString().equalsIgnoreCase("") ? "0.0" : et_longitude.getText().toString());
            onLoadProgressDialog();
        }
    }

    /*Validates Data*/
    private boolean validateData() {
        if (selRetTypeCode.equalsIgnoreCase("")) {
            UtilConstants.showAlert(getString(R.string.val_plz_select_retailer_type), CreateRetailerActivity.this);
            return false;
        } else if (distName.getDistributorName().equalsIgnoreCase("")) {
            UtilConstants.showAlert(getString(R.string.val_plz_select_dist), CreateRetailerActivity.this);
            return false;
        } else if (et_outlet_name.getText() == null) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_outlet_name), CreateRetailerActivity.this);
            return false;
        } else if (et_outlet_name.getText().toString().trim()
                .equalsIgnoreCase("")) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_outlet_name), CreateRetailerActivity.this);
            return false;
        } else if (et_owner_name.getText() == null) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_owner_name), CreateRetailerActivity.this);
            return false;
        } else if (et_owner_name.getText().toString().trim()
                .equalsIgnoreCase("")) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_owner_name), CreateRetailerActivity.this);
            return false;
        } else if (et_address.getText() == null) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_address), CreateRetailerActivity.this);
            return false;
        } else if (et_address.getText().toString().trim()
                .equalsIgnoreCase("")) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_address), CreateRetailerActivity.this);
            return false;
        } else if (selStateCode.equalsIgnoreCase("")) {
            UtilConstants.showAlert(getString(R.string.val_plz_select_state), CreateRetailerActivity.this);
            return false;
        } else if (et_district.getText() == null) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_district), CreateRetailerActivity.this);
            return false;
        } else if (et_district.getText().toString().trim()
                .equalsIgnoreCase("")) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_district), CreateRetailerActivity.this);
            return false;
        } else if (et_city.getText() == null) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_city), CreateRetailerActivity.this);
            return false;
        } else if (et_city.getText().toString().trim()
                .equalsIgnoreCase("")) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_city), CreateRetailerActivity.this);
            return false;
        } else if (et_pin_code.getText() == null) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_pin_code), CreateRetailerActivity.this);
            return false;
        } else if (et_pin_code.getText().toString().trim()
                .equalsIgnoreCase("")) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_valid_pin_code), CreateRetailerActivity.this);
            return false;
        } else if (et_pin_code.getText().toString().trim().length() < 6) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_valid_pin_code), CreateRetailerActivity.this);
            return false;
        } else if (selLatitude == 0.0 && selLongitude == 0.0) {
            UtilConstants.showAlert(getString(R.string.val_plz_select_lat_long), CreateRetailerActivity.this);
            return false;
        } else if (et_mobile_number.getText() == null) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_mobile), CreateRetailerActivity.this);
            return false;
        } else if (et_mobile_number.getText().toString().trim()
                .equalsIgnoreCase("")) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_valid_mobile), CreateRetailerActivity.this);
            return false;
        } else if (et_mobile_number.getText().toString().trim().length() < 10) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_valid_mobile), CreateRetailerActivity.this);
            return false;
        } else if (!et_email_id.getText().toString().trim()
                .equalsIgnoreCase("") && !UtilConstants.isValidEmailAddress(et_email_id.getText().toString())) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_valid_email_id), CreateRetailerActivity.this);
            return false;
        } else if (!et_pan_no.getText().toString().trim()
                .equalsIgnoreCase("") && !UtilConstants.isValidPanNumber(et_pan_no.getText().toString())) {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_valid_pan), CreateRetailerActivity.this);
            return false;
        } else
            return true;
    }

    /*Displays Progress Dialog*/
    private void onLoadProgressDialog() {
        popUpText = getString(R.string.pop_up_msg_create_retailer);
        try {
            new onCreateRetailerAsyncTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*AsyncTask to create retailer*/
    private class onCreateRetailerAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(CreateRetailerActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(popUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                String doc_no = (System.currentTimeMillis() + "");
                Hashtable table = new Hashtable();
                Hashtable cpDMSDIVTable = new Hashtable();
                table.put(Constants.OutletName, selOutletName);
                table.put(Constants.OwnerName, selOwnerName);
                table.put(Constants.RetailerProfile, selRetProfileCode);
                table.put(Constants.PAN, selPan);
                table.put(Constants.VATNo, selVatNo);
                table.put(Constants.DOB, dateOfBirth);
                table.put(Constants.Anniversary, dateOfAnnversary);
                table.put(Constants.EmailID, selEmailID);
                table.put(Constants.MobileNo, selMobileNo);
                table.put(Constants.PostalCode, selPinCode);
                table.put(Constants.Landmark, selLandMark);
                table.put(Constants.StateID, selStateCode);
                table.put(Constants.StateDesc, selStateDesc);
                //We may need this part of code

//                table.put(Constants.CityDesc, selCityDesc);
//                table.put(Constants.CityID, selCityCode);
//                table.put(Constants.DistrictDesc, selDistrictDesc);
//                table.put(Constants.DistrictID, selDistrictCode);

                table.put(Constants.CityDesc, selCity);
                table.put(Constants.CityID, "9999999999");
                table.put(Constants.DistrictDesc, selDistrict);
                table.put(Constants.DistrictID, "9999");
                table.put(Constants.Address1, selAddress);
                table.put(Constants.CPTypeID, selRetTypeCode);
                table.put(Constants.CPTypeDesc, selRetTypeDesc);

                table.put(Constants.Latitude, BigDecimal.valueOf(selLatitude));
                table.put(Constants.Longitude, BigDecimal.valueOf(selLongitude));


                table.put(Constants.SPGUID, SPGUID.toUpperCase());

                table.put(Constants.Group2, "01");
                table.put(Constants.Group4, selRetClassficationCode);
                table.put(Constants.Country, selCountryID);
                table.put(Constants.Tax1, selTaxOne);
                table.put(Constants.CPUID, selMobileNo);
                table.put(Constants.TaxRegStatus, selTaxRegStaCode);
                table.put(Constants.WeeklyOff, selRetWeekOffCode);

                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                String loginIdVal = sharedPreferences.getString(Constants.username, "");

                table.put(Constants.LOGINID, loginIdVal);
                GUID guid = GUID.newRandom();


                table.put(Constants.CPGUID, guid.toString().toUpperCase());
               /* table.put(Constants.ParentID, selDistributorCode);
                table.put(Constants.ParentTypeID, ParentType);
                table.put(Constants.ParentName, selDistributorDesc);*/

                mStrCpGuid = guid.toString().toUpperCase();
                ArrayList<Hashtable> alCPDMS = new ArrayList<>();
                ArrayList<String> getDMSDivList = distName.getDmsDIVList();
                ArrayList<HashMap<String, String>> soItems = new ArrayList<>();
                if (getDMSDivList != null && getDMSDivList.size() > 0) {

                    table.put(Constants.ParentID, distName.getDistributorGuid());
                    table.put(Constants.ParentTypeID, distName.getStockOwner());
                    table.put(Constants.ParentName, distName.getDistributorName());


                    for (String dmsDiv : getDMSDivList) {
                        HashMap<String, String> singleItem = new HashMap<>();
                        String mStrCp1Guid = GUID.newRandom().toString().toUpperCase();
                        cpDMSDIVTable.put(Constants.CP1GUID, mStrCp1Guid);
                        cpDMSDIVTable.put(Constants.DMSDivision, dmsDiv);
                        alCPDMS.add(cpDMSDIVTable);

                        singleItem.put(Constants.CP1GUID, mStrCp1Guid);
                        singleItem.put(Constants.DMSDivision, dmsDiv);
                        soItems.add(singleItem);
                       /* try {
                            OfflineManager.createCPDMS(table);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }*/
                    }
                } else {
                    table.put(Constants.ParentID, selDistributorCode);
                    table.put(Constants.ParentTypeID, ParentType);
                    table.put(Constants.ParentName, selDistributorDesc);
                }
                table.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(soItems));

                table.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
                table.put(Constants.CreatedAt, UtilConstants.getOdataDuration().toString());
                table.put(Constants.entityType, Constants.ChannelPartners);
                Constants.saveDeviceDocNoToSharedPref(CreateRetailerActivity.this, Constants.CPList, doc_no);


                JSONObject jsonHeaderObject = new JSONObject(table);

                UtilDataVault.storeInDataVault(doc_no, jsonHeaderObject.toString());

               /* try {
                    OfflineManager.createRetailerMaster(table, alCPDMS,CreateRetailerActivity.this);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }*/
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
            popUpDisplayed();
        }
    }

    /*Check for the gps activation*/
    private boolean isGPSCheck() {
        if (!UtilConstants.getLocation(CreateRetailerActivity.this)) {
            AlertDialog.Builder gpsEnableDlg = new AlertDialog.Builder(
                    CreateRetailerActivity.this, R.style.MyTheme);
            gpsEnableDlg.setTitle(R.string.title_gps_setting);
            gpsEnableDlg
                    .setMessage(R.string.alert_gps_not_enabled);
            gpsEnableDlg.setPositiveButton(R.string.settings,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            CreateRetailerActivity.this.startActivity(intent);
                        }
                    });
            // on pressing cancel button
            gpsEnableDlg.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            gpsEnableDlg.show();
        }
        return UtilConstants.getLocation(CreateRetailerActivity.this);
    }

    private void onNavigateToPrevScreen() {
        Intent intBack = new Intent(CreateRetailerActivity.this, MainMenu.class);
        intBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intBack);
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateRetailerActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_create_retailer).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intBack = new Intent(CreateRetailerActivity.this, MainMenu.class);
                        intBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intBack);
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
                CreateRetailerActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_sync_cannot_be_performed)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intBack = new Intent(CreateRetailerActivity.this, MainMenu.class);
                        intBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intBack);
                    }
                });

        builder.show();
    }

    private void getDistributorDMS() {
        String spGuid = Constants.getSPGUID();
        try {
            String mStrDistQry = Constants.CPSPRelations + " ?$filter=" + Constants.SPGUID + " eq '" + spGuid.replace("-", "") + "' ";
            distListDms = OfflineManager.getDistributorsDms(mStrDistQry);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void displayDistributorVal() {

        ArrayAdapter<DMSDivionBean> typeAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, distListDms);
        typeAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_distributor.setAdapter(typeAdapter);

        sp_distributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                distName = distListDms.get(position);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        /*if(distListDms.size()==1)
        {
            mStrSelDistGuid =distListDms.get(0).getDistributorGuid();
            mStrSelDMSDIVID =   distListDms.get(0).getDMSDivisionQuery();
            mStrStkOwner =  distListDms.get(0).getStockOwner();
            ll_dist_layout.setVisibility(View.GONE);
            sp_distributor.setVisibility(View.GONE);
        }*/
    }

}
