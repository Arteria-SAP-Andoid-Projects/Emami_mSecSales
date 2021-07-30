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
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import com.arteriatech.emami.dbstock.DMSDivionBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.exception.ODataParserException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;

/**
 * Created by ${e10526} on ${30-04-2016}.
 */
public class UpdateRetailerActivity extends AppCompatActivity implements UIListener {
    private TextView tv_retailer_code, tv_parent_name;
    private TextView btn_date_of_annverisary, btn_date_of_birth;
    private int mYear = 0, mMonth = 0, mDay = 0, mnt = 0;
    private int mYearAnnv = 0, mMonthAnnv = 0, mDayAnnv = 0, mntAnnv = 0;
    private String mon = "", day = "", dateOfBirth = "", mStrAnnvDate = "", monAnnv = "", dayAnnv = "";
    static final int DATE_DIALOG_ID = 0;
    static final int DATE_DIALOG_ID_ANNVERSARY = 1;
    Calendar thatDay = Calendar.getInstance();
    private Spinner sp_retailer_type, sp_state, sp_retailer_profile/*,sp_city,sp_district*/, sp_classfication, sp_week_off, sp_tax_reg_status;
    private EditText et_outlet_name, et_owner_name,/*et_retailer_profile,*/
            et_address,
            et_district, et_city, et_land_mark, et_pin_code, et_mobile_number,
            et_email_id, et_pan_no, et_vat_no, et_tin_no, et_latitude, et_longitude, et_tax_one;

    private String[][] arrayRetTypeVal, arrayRetProfileVal, arrayRetClassficationVal, arrayWeekOffVal, arrayTaxRegStatus;
    private String[][] arrayStateVal, arrayCountryVal;
    private String selRetTypeCode = "", selRetProfileCode = "", selRetTypeDesc = "",
            selState = "", selStateCode = "", selStateDesc = "", selDistributorCode = "",
            selDistributorDesc = "", selCountryID = "IN", partnetGUID = "",
            partnetTypeId = "", partnetName = "", cpGUID = "", selStatusID = "", selApprvalStatusID = "";
    private String popUpText = "";
    private ProgressDialog pdLoadDialog;
    String selAddress = "", selDistrict = "", selCity = "", selLandMark = "", selPinCode = "", selMobileNo = "",
            selEmailID = "", selPan = "", selVatNo = "", selTinNo = "", selOutletName = "", selOwnerName = "",
            selRetailerProfile = "", selCityCode = "", selCityDesc = "", selDistrictCode = "", selDistrictDesc = "", selRetailerType = "", selRetClassficationCode = "", selRetClassficationDesc = "", selRetWeekOffCode = "", selRetWeekOffDesc = "", selTaxRegStaCode = "", selTaxRegStaDesc = "", selRetClassfication = "", selTaxRegSta = "", selRetWeekOff = "", selTaxOne = "", selCPUID = "";
    double selLatitude = 0.0, selLongitude = 0.0;
    private ImageView btn_lat_long;

    private ODataPropMap oDataProperties;
    private ODataProperty oDataProperty;

    private String mStrCustomerName = "";
    private String mStrCustomerId = "", mStrBundleCpGuid = "";
    private ODataGuid mCpGuid;
    public boolean isEtopchecked;

    String mStrDobDay = "", mStrDobYear = "";
    int mIntDobMonth = 0;
    String mStrDobDayAnnv = "", mStrDobYearAnnv = "";
    int mIntDobMonthAnnv = 0;
    ODataEntity retailerEntity;

    BigDecimal mDecimalLatitude, mDecimalLongitude;
    double mDoubleLatitude = 0.0, mDoubleLongitude = 0.0;
    LinearLayout ll_cb_number;
    CheckBox cb_etop_allowed;
    TableRow tr_etop_up_number;
    EditText et_pretup_mobile_number;
    private boolean mBooleanPretopUpenabled = false;
    String retDetgry = "";
    TableRow tr_vat_tin = null;
    DMSDivionBean distName = new DMSDivionBean();
    MenuItem menu_save = null;
    private ArrayList<DMSDivionBean> distListDms = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.title_update_retailer));

        setContentView(R.layout.activity_update_retailer);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mStrCustomerName = extras.getString(Constants.RetailerName);
            mStrCustomerId = extras.getString(Constants.CPNo);
            mStrBundleCpGuid = extras.getString(Constants.CPGUID);
        }
        if (!Constants.restartApp(UpdateRetailerActivity.this)) {
            initUI();
        }

    }

    void initUI() {
        retDetgry = Constants.ChannelPartners + "(guid'" + mStrBundleCpGuid.toUpperCase() + "') ";

        try {
            retailerEntity = OfflineManager.getRetDetails(retDetgry);


            oDataProperties = retailerEntity.getProperties();

            oDataProperty = oDataProperties.get(Constants.ParentID);
            selDistributorCode = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.ParentTypeID);
            partnetTypeId = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.ParentName);
            partnetName = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";


            oDataProperty = oDataProperties.get(Constants.CPTypeID);
            selRetTypeCode = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.OutletName);
            selOutletName = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.OwnerName);
            selOwnerName = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            /*oDataProperty = oDataProperties.get(Constants.RetailerProfile);
            selRetailerProfile = (String) oDataProperty.getValue()!=null?(String) oDataProperty.getValue():"";*/

            selRetailerProfile = OfflineManager.getValueByColumnName(Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" +
                    mStrBundleCpGuid.toUpperCase() + "' &$top=1", Constants.Group1);

//            oDataProperty = oDataProperties.get(Constants.Group4);
//            selRetClassfication = (String) oDataProperty.getValue()!=null?(String) oDataProperty.getValue():"";

            selRetClassfication = OfflineManager.getValueByColumnName(Constants.CPDMSDivisions + "?$filter=" + Constants.CPGUID + " eq guid'" +
                    mStrBundleCpGuid.toUpperCase() + "' &$top=1", Constants.Group4);

            oDataProperty = oDataProperties.get(Constants.WeeklyOff);
            selRetWeekOff = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.Tax1);
            selTaxOne = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.TaxRegStatus);
            selTaxRegSta = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.CPUID);
            selCPUID = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.Group2);
            selRetailerType = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.Address1);
            selAddress = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

//            oDataProperty = oDataProperties.get(Constants.DistrictID);
//            selDistrict = (String) oDataProperty.getValue()!=null?(String) oDataProperty.getValue():"";

            oDataProperty = oDataProperties.get(Constants.DistrictDesc);
            selDistrict = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.StateID);
            selState = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";


            oDataProperty = oDataProperties.get(Constants.PartnerMgrGUID);
            try {
                ODataGuid mInvoiceGUID = (ODataGuid) oDataProperty.getValue();
                partnetGUID = mInvoiceGUID.guidAsString36();
            } catch (Exception e) {
                e.printStackTrace();
            }


            oDataProperty = oDataProperties.get(Constants.CPGUID);
            try {
                ODataGuid mcpGUID = (ODataGuid) oDataProperty.getValue();
                cpGUID = mcpGUID.guidAsString32();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // WE may need this
//            oDataProperty = oDataProperties.get(Constants.CityID);
//            selCity = (String) oDataProperty.getValue()!=null?(String) oDataProperty.getValue():"";

            oDataProperty = oDataProperties.get(Constants.CityDesc);
            selCity = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.Landmark);
            selLandMark = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.PostalCode);
            selPinCode = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.MobileNo);
            selMobileNo = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.EmailID);
            selEmailID = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.PAN);
            selPan = ((String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "").toUpperCase();

            oDataProperty = oDataProperties.get(Constants.VATNo);
            selVatNo = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.TIN);
            selTinNo = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.Latitude);
            mDecimalLatitude = (BigDecimal) oDataProperty.getValue();  //---------> Decimal property

            if (mDecimalLatitude != null) {
                mDoubleLatitude = mDecimalLatitude.doubleValue();
            } else {
                mDoubleLatitude = 0.0;
            }

            oDataProperty = oDataProperties.get(Constants.Longitude);
            mDecimalLongitude = (BigDecimal) oDataProperty.getValue();  //---------> Decimal property

            if (mDecimalLongitude != null) {
                mDoubleLongitude = mDecimalLongitude.doubleValue();
            } else {
                mDoubleLongitude = 0.0;
            }

            oDataProperty = oDataProperties.get(Constants.DOB);
            dateOfBirth = UtilConstants.convertGregorianCalendarToYYYYMMDDFormat((GregorianCalendar) oDataProperty.getValue());

            oDataProperty = oDataProperties.get(Constants.Anniversary);
            mStrAnnvDate = UtilConstants.convertGregorianCalendarToYYYYMMDDFormat((GregorianCalendar) oDataProperty.getValue());


            oDataProperty = oDataProperties.get(Constants.CPGUID);
            mCpGuid = (ODataGuid) oDataProperty.getValue(); // ----->Guid Property

            oDataProperty = oDataProperties.get(Constants.StatusID);
            selStatusID = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.ApprvlStatusID);
            selApprvalStatusID = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }

        ll_cb_number = (LinearLayout) findViewById(R.id.ll_cb_number);
        tr_vat_tin = (TableRow) findViewById(R.id.tr_vat_tin);
        et_pretup_mobile_number = (EditText) findViewById(R.id.et_pretup_mobile_number);
        tr_etop_up_number = (TableRow) findViewById(R.id.tr_etop_up_number);
        cb_etop_allowed = (CheckBox) findViewById(R.id.cb_etop_allowed);
        btn_date_of_annverisary = (TextView) findViewById(R.id.btn_date_of_annverisary);
        btn_date_of_birth = (TextView) findViewById(R.id.btn_date_of_birth);
        sp_retailer_type = (Spinner) findViewById(R.id.sp_retailer_type);
        sp_retailer_profile = (Spinner) findViewById(R.id.sp_retailer_profile);

        sp_classfication = (Spinner) findViewById(R.id.sp_classfication);
        sp_week_off = (Spinner) findViewById(R.id.sp_week_off);
        sp_tax_reg_status = (Spinner) findViewById(R.id.sp_tax_reg_status);

        tv_retailer_code = (TextView) findViewById(R.id.tv_retailer_code);
        tv_parent_name = (TextView) findViewById(R.id.tv_parent_name);
        sp_state = (Spinner) findViewById(R.id.sp_state);
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
        et_email_id.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        et_pan_no = (EditText) findViewById(R.id.et_pan_no);
        et_pan_no.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        et_pan_no.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        et_vat_no = (EditText) findViewById(R.id.et_vat_no);
        et_vat_no.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        tr_vat_tin.setVisibility(View.GONE);
        et_pretup_mobile_number.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_pretup_mobile_number.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        btn_lat_long = (ImageView) findViewById(R.id.btn_lat_long_update);
        et_latitude = (EditText) findViewById(R.id.et_latitude_update);
        et_longitude = (EditText) findViewById(R.id.et_longitude_update);

        et_tax_one = (EditText) findViewById(R.id.et_tax_one);
        et_tax_one.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});

        et_latitude.setText(mDoubleLatitude + "");
        et_longitude.setText(mDoubleLongitude + "");

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
        getRetailerType();

        getCountry();
        try {
            selCountryID = arrayCountryVal[0][0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        getStates(selCountryID);

        tv_retailer_code.setText(mStrCustomerId);
        try {
            if (!partnetName.equalsIgnoreCase("")) {
                tv_parent_name.setText(partnetName);
            } else {
                tv_parent_name.setText(Constants.getDistNameFromCPDMSDIV(mStrBundleCpGuid.toUpperCase(), partnetGUID.toUpperCase()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (arrayRetTypeVal == null) {
            arrayRetTypeVal = new String[2][1];
            arrayRetTypeVal[0][0] = "";
            arrayRetTypeVal[1][0] = "";
        }

        ArrayAdapter<String> retailerTypeAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, arrayRetTypeVal[1]);
        retailerTypeAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_retailer_type.setAdapter(retailerTypeAdapter);

        for (int i = 0; i < arrayRetTypeVal[1].length; i++) {
            if (selRetTypeCode.equalsIgnoreCase(arrayRetTypeVal[0][i])) {
                sp_retailer_type.setSelection(i);
            }
        }
        sp_retailer_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selRetTypeCode = arrayRetTypeVal[0][position];
                selRetTypeDesc = arrayRetTypeVal[1][position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        getRetailerProfile();
        if (arrayRetProfileVal == null) {
            arrayRetProfileVal = new String[2][1];
            arrayRetProfileVal[0][0] = "";
            arrayRetProfileVal[1][0] = "";
        }


        ArrayAdapter<String> retailerProfileAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrayRetProfileVal[1]);
        retailerProfileAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_retailer_profile.setAdapter(retailerProfileAdapter);


        for (int i = 0; i < arrayRetProfileVal[1].length; i++) {
            if (selRetailerProfile.equalsIgnoreCase(arrayRetProfileVal[0][i])) {
                sp_retailer_profile.setSelection(i);
            }
        }
        sp_retailer_profile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                selRetProfileCode = arrayRetProfileVal[0][position];


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        getRetailerWeekOff();
        getRetailerTaxRexSatus();
        getRetailerClassfication();
        displayRetCalssfication();
        displayRetWeekOff();
        displayTaxTaxStatus();

        ll_cb_number.setVisibility(View.GONE);


        et_outlet_name.setText(selOutletName);
        et_owner_name.setText(selOwnerName);
        et_address.setText(selAddress);
        et_district.setText(selDistrict);
        et_city.setText(selCity);
        et_land_mark.setText(selLandMark);
        et_pin_code.setText(selPinCode);
        et_mobile_number.setText(selMobileNo);
        et_email_id.setText(selEmailID);
        et_pan_no.setText(selPan);
        et_vat_no.setText(selVatNo);
        et_tax_one.setText(selTaxOne);


        final Calendar calDob = Calendar.getInstance();
        if (dateOfBirth.equalsIgnoreCase("")) {
            mYear = calDob.get(Calendar.YEAR);
            mMonth = calDob.get(Calendar.MONTH);
            mDay = calDob.get(Calendar.DAY_OF_MONTH);
            mnt = mMonth + 1;
            if (mnt < 10)
                mon = getString(R.string.Zero_0) + mnt;
            else
                mon = "" + mnt;
            day = "" + mDay;
            if (mDay < 10)
                day = getString(R.string.Zero_0) + mDay;


        } else {
            mStrDobDay = UtilConstants.getNextVisitDay(dateOfBirth);
            mIntDobMonth = UtilConstants.getNextVisitMonth(dateOfBirth) - 1;
            mStrDobYear = UtilConstants.getNextVisitear(dateOfBirth);

            calDob.set(Calendar.DAY_OF_MONTH,
                    Integer.parseInt(mStrDobDay));
            calDob.set(Calendar.MONTH, mIntDobMonth);
            calDob.set(Calendar.YEAR, Integer.parseInt(mStrDobYear));


            mDay = calDob.get(Calendar.DAY_OF_MONTH);
            mMonth = calDob.get(Calendar.MONTH);
            mYear = calDob.get(Calendar.YEAR);

            dateOfBirth = mYear + "-" + (mMonth + 1 < 10 ? (getString(R.string.Zero_0) + (mMonth + 1)) : mMonth + 1) + "-" + (mDay < 10 ? ("0" + (mDay)) : mDay);

            btn_date_of_birth.setText(new StringBuilder().append(mDay)
                    .append("/").append((mMonth + 1 < 10 ? (getString(R.string.Zero_0) + (mMonth + 1)) : mMonth + 1))
                    .append("/").append("").append(mYear));
        }

        btn_date_of_birth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onDatePickerDialog(DATE_DIALOG_ID).show();
            }
        });
        selectAnnversary();
        btn_lat_long.setBackgroundResource(R.drawable.ic_loca_red_mark_small);
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

    private void selectAnnversary() {
        final Calendar calAnnver = Calendar.getInstance();
        if (mStrAnnvDate.equalsIgnoreCase("")) {
            mYearAnnv = calAnnver.get(Calendar.YEAR);
            mMonthAnnv = calAnnver.get(Calendar.MONTH);
            mDayAnnv = calAnnver.get(Calendar.DAY_OF_MONTH);
            mntAnnv = mMonthAnnv + 1;
            if (mntAnnv < 10)
                monAnnv = getString(R.string.Zero_0) + mntAnnv;
            else
                monAnnv = "" + mntAnnv;
            dayAnnv = "" + mDayAnnv;
            if (mDayAnnv < 10)
                dayAnnv = getString(R.string.Zero_0) + mDayAnnv;


        } else {
            mStrDobDayAnnv = UtilConstants.getNextVisitDay(mStrAnnvDate);
            mIntDobMonthAnnv = UtilConstants.getNextVisitMonth(mStrAnnvDate) - 1;
            mStrDobYearAnnv = UtilConstants.getNextVisitear(mStrAnnvDate);

            calAnnver.set(Calendar.DAY_OF_MONTH,
                    Integer.parseInt(mStrDobDayAnnv));
            calAnnver.set(Calendar.MONTH, mIntDobMonthAnnv);
            calAnnver.set(Calendar.YEAR, Integer.parseInt(mStrDobYearAnnv));


            mDayAnnv = calAnnver.get(Calendar.DAY_OF_MONTH);
            mMonthAnnv = calAnnver.get(Calendar.MONTH);
            mYearAnnv = calAnnver.get(Calendar.YEAR);

            mStrAnnvDate = mYearAnnv + "-" + (mMonthAnnv + 1 < 10 ? (getString(R.string.Zero_0) + (mMonthAnnv + 1)) : mMonthAnnv + 1) + "-" + (mDayAnnv < 10 ? ("0" + (mDayAnnv)) : mDayAnnv);

            btn_date_of_annverisary.setText(new StringBuilder().append(mDayAnnv)
                    .append("/").append((mMonthAnnv + 1 < 10 ? (getString(R.string.Zero_0) + (mMonthAnnv + 1)) : mMonthAnnv + 1))
                    .append("/").append("").append(mYearAnnv));
        }

        btn_date_of_annverisary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onDatePickerDialog(DATE_DIALOG_ID_ANNVERSARY).show();
            }
        });
    }

    private void displayRetCalssfication() {


        ArrayAdapter<String> retClassAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrayRetClassficationVal[1]);
        retClassAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_classfication.setAdapter(retClassAdapter);
        for (int i = 0; i < arrayRetClassficationVal[1].length; i++) {
            if (selRetClassfication.equalsIgnoreCase(arrayRetClassficationVal[0][i])) {
                sp_classfication.setSelection(i);
                break;
            }
        }
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


        ArrayAdapter<String> retWeekOffAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrayWeekOffVal[1]);
        retWeekOffAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_week_off.setAdapter(retWeekOffAdapter);
        for (int i = 0; i < arrayWeekOffVal[1].length; i++) {
            if (selRetWeekOff.equalsIgnoreCase(arrayWeekOffVal[0][i])) {
                sp_week_off.setSelection(i);
                break;
            }
        }
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

        ArrayAdapter<String> retTaxAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrayTaxRegStatus[1]);
        retTaxAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_tax_reg_status.setAdapter(retTaxAdapter);

        for (int i = 0; i < arrayTaxRegStatus[1].length; i++) {
            if (selTaxRegSta.equalsIgnoreCase(arrayTaxRegStatus[0][i])) {
                sp_tax_reg_status.setSelection(i);
                break;
            }
        }
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

        LogManager.writeLogError(getString(R.string.Error_in_update_retailer) + e.getMessage());
        Toast.makeText(UpdateRetailerActivity.this, getString(R.string.err_odata_unexpected, e.getMessage()),
                Toast.LENGTH_LONG).show();
        closeProgDialog();
        Intent intBack = new Intent(UpdateRetailerActivity.this, UpdateRetailerListActivity.class);
        intBack.setFlags(intBack.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intBack);
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue()) {

            closeProgDialog();
            popUpText = getString(R.string.Retailer_updated);
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    UpdateRetailerActivity.this, R.style.MyTheme);
            builder.setMessage(popUpText)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface Dialog,
                                        int id) {
                                    try {

                                        Dialog.cancel();

                                        Intent intBack = new Intent(UpdateRetailerActivity.this, UpdateRetailerListActivity.class);
                                        intBack.setFlags(intBack.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intBack);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            });
            builder.show();
        } else if (operation == Operation.Update.getValue()) {
            closeProgDialog();
            alertDialogRetailerUpdate();
        } else if (operation == Operation.OfflineFlush.getValue()) {
            OfflineManager.refreshRequests(getApplicationContext(), Constants.ChannelPartners, UpdateRetailerActivity.this);


        } else if (operation == Operation.OfflineRefresh.getValue()) {
            closeProgDialog();
            popUpText = getString(R.string.Retailer_updated_successfully);
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    UpdateRetailerActivity.this, R.style.MyTheme);
            builder.setMessage(popUpText)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface Dialog,
                                        int id) {
                                    try {

                                        Dialog.cancel();

                                        Intent intBack = new Intent(UpdateRetailerActivity.this, UpdateRetailerListActivity.class);
                                        intBack.setFlags(intBack.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intBack);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            });
            builder.show();
        }
    }

    private void alertDialogRetailerUpdate() {
        popUpText = getString(R.string.Retailer_updated);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                UpdateRetailerActivity.this, R.style.MyTheme);
        builder.setMessage(popUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    Intent intBack = new Intent(UpdateRetailerActivity.this, UpdateRetailerListActivity.class);
                                    intBack.setFlags(intBack.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intBack);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();
    }

    @SuppressLint("NewApi")
    protected Dialog onDatePickerDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog datePicker = new DatePickerDialog(this, mDateSetListener,
                        mYear, mMonth, mDay);
                Calendar c = Calendar.getInstance();
                Date newDate = c.getTime();
                datePicker.getDatePicker().setMaxDate(newDate.getTime());
                return datePicker;
            case DATE_DIALOG_ID_ANNVERSARY:
                DatePickerDialog datePickerAnnv = new DatePickerDialog(this, mDateSetListenerAnnv,
                        mYearAnnv, mMonthAnnv, mDayAnnv);
                Calendar cal = Calendar.getInstance();
                Date newDateAnv = cal.getTime();
                datePickerAnnv.getDatePicker().setMaxDate(newDateAnv.getTime());
                return datePickerAnnv;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListenerAnnv = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker v, int year, int monthOfYear,
                              int dayOfMonth) {
            mYearAnnv = year;
            mMonthAnnv = monthOfYear;
            mDayAnnv = dayOfMonth;
            String mon = "";
            String day = "";
            int mnt = 0;
            mnt = mMonthAnnv + 1;
            if (mnt < 10)
                mon = getString(R.string.Zero_0) + mnt;
            else
                mon = "" + mnt;
            day = "" + mDayAnnv;
            if (mDayAnnv < 10)
                day = getString(R.string.Zero_0) + mDayAnnv;
            mStrAnnvDate = mYearAnnv + "-" + mon + "-" + day;
            btn_date_of_annverisary.setText(new StringBuilder().append(mDayAnnv)
                    .append("/").append(mon)
                    .append("/").append("").append(mYearAnnv));
        }
    };
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
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
                mon = getString(R.string.Zero_0) + mnt;
            else
                mon = "" + mnt;
            day = "" + mDay;
            if (mDay < 10)
                day = getString(R.string.Zero_0) + mDay;
            dateOfBirth = mYear + "-" + mon + "-" + day;
            btn_date_of_birth.setText(new StringBuilder().append(mDay)
                    .append("/").append(mon)
                    .append("/").append("").append(mYear));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back_save, menu);
        menu_save = menu.findItem(R.id.menu_save);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
       /* if(selRetailerType.equalsIgnoreCase("02")){
            menu_save.setVisible(false);
        }
        else*/
        menu_save.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    public void showPopup(View v) {
        Context wrapper = new ContextThemeWrapper(getApplicationContext(), R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_back_save, popup.getMenu());

        if (selRetailerType.equalsIgnoreCase("02")) {
            popup.getMenu().removeItem(R.id.menu_save);
        } else {
            menu_save.setVisible(true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                onOptionsItemSelected(item);
                return true;
            }
        });

        popup.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_save:
                onSave();
                break;
        }
        return true;
    }

    /*gets list of country */
    private void getCountry() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'Country' ";
            arrayCountryVal = OfflineManager.getConfigList(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }

        if (arrayCountryVal == null) {
            arrayCountryVal = new String[2][1];
            arrayCountryVal[0][0] = "";
            arrayCountryVal[1][0] = "";
        }
    }

    /*Gets types of retailer*/
    private void getRetailerType() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'CPTypeID'";
            arrayRetTypeVal = OfflineManager.getConfigList(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }
    }

    /*gets types of profile for retailer*/
    private void getRetailerProfile() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'RetailerProfile'";
            arrayRetProfileVal = OfflineManager.getConfigListWithNone(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }
    }

    /*gets list of states*/
    private void getStates(String parentID) {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'StateID' &$orderby=" + Constants.Description + "%20asc";
            arrayStateVal = OfflineManager.getConfigList(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }


        if (arrayStateVal == null) {
            arrayStateVal = new String[2][1];
            arrayStateVal[0][0] = "";
            arrayStateVal[1][0] = "";
        }

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, arrayStateVal[1]);
        stateAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_state.setAdapter(stateAdapter);
        for (int i = 0; i < arrayStateVal[1].length; i++) {
            if (selState.equalsIgnoreCase(arrayStateVal[0][i])) {
                sp_state.setSelection(i);
            }
        }
        sp_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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


    /*getting values from input field*/
    private void loadingValues() {
        selAddress = et_address.getText().toString();
        selDistrict = et_district.getText().toString();
        selTaxOne = et_district.getText() != null ? et_district.getText().toString() : "";
        selCity = et_city.getText().toString();
        selLandMark = et_land_mark.getText().toString().equalsIgnoreCase("") ? "" : et_land_mark.getText().toString();
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

    /*alert for updating route plan retailer*/
    private void alertRoutePlanDialog(String mStrTextMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
        builder.setMessage(mStrTextMsg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @SuppressLint("NewApi")
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                loadingValues();
                            }
                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.show();
    }

    /*Save new data for retailer*/
    private void onSave() {
        if (validateData()) {
            loadingValues();

        }
    }

    /*Validating data for input fields*/
    public boolean validateData() {
        if (selRetTypeCode.toString().equalsIgnoreCase("")) {
            showAlert(getString(R.string.val_plz_select_retailer_type));
            return false;
        } else if (et_outlet_name.getText() == null) {
            showAlert(getString(R.string.val_plz_enter_outlet_name));
            return false;
        } else if (et_outlet_name.getText().toString().trim()
                .equalsIgnoreCase("")) {
            showAlert(getString(R.string.val_plz_enter_outlet_name));
            return false;
        } else if (et_owner_name.getText() == null) {
            showAlert(getString(R.string.val_plz_enter_owner_name));
            return false;
        } else if (et_owner_name.getText().toString().trim()
                .equalsIgnoreCase("")) {
            showAlert(getString(R.string.val_plz_enter_owner_name));
            return false;
        } else if (et_address.getText() == null) {
            showAlert(getString(R.string.val_plz_enter_address));
            return false;
        } else if (et_address.getText().toString().trim()
                .equalsIgnoreCase("")) {
            showAlert(getString(R.string.val_plz_enter_address));
            return false;
        } else if (selStateCode.equalsIgnoreCase("")) {
            showAlert(getString(R.string.val_plz_select_state));
            return false;
        } else if (et_district.getText() == null) {
            showAlert(getString(R.string.val_plz_enter_district));
            return false;
        } else if (et_district.getText().toString().trim()
                .equalsIgnoreCase("")) {
            showAlert(getString(R.string.val_plz_enter_district));
            return false;
        } else if (et_city.getText() == null) {
            showAlert(getString(R.string.val_plz_enter_city));
            return false;
        } else if (et_city.getText().toString().trim()
                .equalsIgnoreCase("")) {
            showAlert(getString(R.string.val_plz_enter_city));
            return false;
        } else if (et_pin_code.getText() == null) {
            showAlert(getString(R.string.val_plz_enter_pin_code));
            return false;
        } else if (et_pin_code.getText().toString().trim()
                .equalsIgnoreCase("")) {
            showAlert(getString(R.string.val_plz_enter_valid_pin_code));
            return false;
        } else if (et_pin_code.getText().toString().trim().length() < 6) {
            showAlert(getString(R.string.val_plz_enter_valid_pin_code));
            return false;
        } else if (et_mobile_number.getText() == null) {
            showAlert(getString(R.string.val_plz_enter_mobile));
            return false;
        } else if (et_mobile_number.getText().toString().trim()
                .equalsIgnoreCase("")) {
            showAlert(getString(R.string.val_plz_enter_valid_mobile));
            return false;
        } else if (et_mobile_number.getText().toString().trim().length() < 10) {
            showAlert(getString(R.string.val_plz_enter_valid_mobile));
            return false;
        } else if (!et_email_id.getText().toString().trim()
                .equalsIgnoreCase("") && !UtilConstants.isValidEmailAddress(et_email_id.getText().toString())) {
            showAlert(getString(R.string.val_plz_enter_valid_email_id));
            return false;
        } else if (cb_etop_allowed.isChecked() && et_pretup_mobile_number == null) {
            showAlert(getString(R.string.val_plz_enter_etop));
            return false;
        } else if (cb_etop_allowed.isChecked() && et_pretup_mobile_number.getText().toString().trim()
                .equalsIgnoreCase("")) {
            showAlert(getString(R.string.val_plz_enter_valid_etop));
            return false;
        } else if (cb_etop_allowed.isChecked() && et_pretup_mobile_number.getText().toString().trim().length() < 10) {
            showAlert(getString(R.string.val_plz_enter_valid_etop));
            return false;
        } else
            return true;
    }

    public void showAlert(String message) {
        UtilConstants.showAlert(message, UpdateRetailerActivity.this);
    }

    private void onLoadProgressDialog() {
        popUpText = getString(R.string.pop_up_msg_update_retailer);
        try {
            new onUpdateRetailerAsyncTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*AsyncTask to Update Retailer */
    public class onUpdateRetailerAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(UpdateRetailerActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(popUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                Hashtable table = new Hashtable();

                table.put(Constants.OutletName, selOutletName);
                table.put(Constants.OwnerName, selOwnerName);
                table.put(Constants.RetailerProfile, selRetProfileCode);
                table.put(Constants.Group2, "");
                table.put(Constants.PAN, selPan);
                table.put(Constants.VATNo, selVatNo);
                table.put(Constants.DOB, dateOfBirth);
                table.put(Constants.Anniversary, mStrAnnvDate + Constants.Time_Value);
                table.put(Constants.EmailID, selEmailID);
                table.put(Constants.MobileNo, selMobileNo);
                table.put(Constants.PostalCode, selPinCode);
                table.put(Constants.Landmark, selLandMark);
                table.put(Constants.Address1, selAddress);
                table.put(Constants.CPTypeID, selRetTypeCode);
                table.put(Constants.Latitude, BigDecimal.valueOf(selLatitude));
                table.put(Constants.Longitude, BigDecimal.valueOf(selLongitude));
                table.put(Constants.ParentID, selDistributorCode);
                table.put(Constants.ParentTypeID, partnetTypeId);
                table.put(Constants.ParentName, partnetName);
                table.put(Constants.StateID, selStateCode);
                table.put(Constants.PartnerMgrGUID, partnetGUID);

                table.put(Constants.StatusID, selStatusID);
                table.put(Constants.ApprvlStatusID, selApprvalStatusID);
                // We may need this part of code

//                table.put(Constants.CityDesc, selCityDesc);
//                table.put(Constants.CityID, selCityCode);
//                table.put(Constants.DistrictDesc, selDistrictDesc);
//                table.put(Constants.DistrictID, selDistrictCode);

                table.put(Constants.CityDesc, selCity);
                table.put(Constants.CityID, "9999999999");
                table.put(Constants.DistrictDesc, selDistrict);
                table.put(Constants.DistrictID, "9999");
                if (!mStrCustomerId.equalsIgnoreCase("")) {
                    table.put(Constants.CPNo, mStrCustomerId);
                } else {
                    table.put(Constants.CPNo, "");
                }

                table.put(Constants.Group4, selRetClassficationCode);
                table.put(Constants.Tax1, selTaxOne);
                table.put(Constants.CPUID, selMobileNo);
                table.put(Constants.TaxRegStatus, selTaxRegStaCode);
                table.put(Constants.WeeklyOff, selRetWeekOffCode);
                table.put(Constants.comingFrom, Constants.RetailerChange);
                table.put(Constants.IsLatLongUpdate, "");
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                String loginIdVal = sharedPreferences.getString(Constants.username, "");

                table.put(Constants.LOGINID, loginIdVal);
                table.put(Constants.CPGUID, mCpGuid.guidAsString36().toUpperCase());
                table.put("Query", retDetgry);
                table.put(Constants.SetResourcePath, Constants.ChannelPartners + "(guid'" + mStrBundleCpGuid + "')");
                if (retailerEntity.getEtag() != null) {
                    table.put(Constants.Etag, retailerEntity.getEtag());
                } else {
                    table.put(Constants.Etag, "");
                }


              /*  try {
                    OfflineManager.updateRetailerMaster(table, UpdateRetailerActivity.this);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }*/

                try {
                    OfflineManager.updateRetilerBatchReq(table, "");
                } catch (ODataParserException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            closeProgDialog();

            alertDialogRetailerUpdate();
        }
    }

    private void closeProgDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*Checks for the activation of GPS*/
    private boolean isGPSCheck() {
        if (!UtilConstants.getLocation(UpdateRetailerActivity.this)) {
            AlertDialog.Builder gpsEnableDlg = new AlertDialog.Builder(
                    UpdateRetailerActivity.this, R.style.MyTheme);
            gpsEnableDlg.setTitle(getString(R.string.GPS_settings));
            gpsEnableDlg
                    .setMessage(getString(R.string.alert_gps_not_enabled));
            gpsEnableDlg.setPositiveButton(getString(R.string.Settings),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            UpdateRetailerActivity.this.startActivity(intent);
                        }
                    });
            // on pressing cancel button
            gpsEnableDlg.setNegativeButton(getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            gpsEnableDlg.show();
        }
        return UtilConstants.getLocation(UpdateRetailerActivity.this);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateRetailerActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_update_retailer).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intBack = new Intent(UpdateRetailerActivity.this, UpdateRetailerListActivity.class);
                        intBack.setFlags(intBack.FLAG_ACTIVITY_CLEAR_TOP);
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

    private void updateLocation() {
        pdLoadDialog = Constants.showProgressDialog(UpdateRetailerActivity.this, "", getString(R.string.checking_pemission));
        LocationUtils.checkLocationPermission(UpdateRetailerActivity.this, new LocationInterface() {
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
        pdLoadDialog = Constants.showProgressDialog(UpdateRetailerActivity.this, "", getString(R.string.gps_progress));
        Constants.getLocation(UpdateRetailerActivity.this, new LocationInterface() {
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
                    LocationUtils.checkLocationPermission(UpdateRetailerActivity.this, new LocationInterface() {
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

}