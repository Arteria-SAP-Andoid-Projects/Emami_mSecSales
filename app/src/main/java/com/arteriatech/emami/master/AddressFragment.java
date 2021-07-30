package com.arteriatech.emami.master;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.appointment.AppointmentCreate;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataParserException;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;

@SuppressLint("NewApi")
public class AddressFragment extends Fragment implements View.OnClickListener {
    String mStrCPGUID = "", mStrRetID = "", mStrRetName = "";

    ODataPropMap oDataProperties;
    ODataProperty oDataProperty;
    ImageView iv_mail, iv_sms, iv_call, iv_appointment, iv_lat_long_icon;
    ImageView iv_whatsApp, iv_dob_sel, iv_annversary_sel;

    private String mStrEmailID = "", mStrAddressTwo = "", mStrAddressThree = "",
            mStrAddressFour = "", mStrLandmark = "", mStrCityDesc = "", mStrDistrictDesc = "", selCPTypeDesc = "",
            mStrOwnerName = "", mStrPostalCode = "";
    String mDistributorName = "", mContactNum = "", mRetCategory = "", mClassification = "",
            mWeeklyOff = "", mDOB = "", mAnniversary = "", mStrCPTypeId = "", mStrFirstAddress = "",
            mStrGSTIN = "", mStrGSTREGStatus = "", partnerGUID = "";

    ODataGuid mCpGuid = null;
    private LinearLayout ll_lat_long_layout;
    private ProgressDialog pdLoadDialog;
    private View myInflatedView = null;
    TextView tvFirstAddress, tvSecondAddress, tv_third_address,
            tv_fourth_address, tv_land_mark, tv_postalCode, tv_OwnerName,
            tvDistributorName, tvContactNum, tvRetailerCategory, tvClassification, tvWeeklyOff,
            tvDOB, tvAnniversary, tvEmilView, tv_gst_in, tv_gst_reg_status, tv_long_value, tv_lat_value;

    public static AddressFragment newInstance(String mStrRetId, String mStrRetName, String mStrCpGuid) {

        AddressFragment addressFragment = new AddressFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.CPGUID, mStrCpGuid);
        bundle.putString(Constants.RetName, mStrRetName);
        bundle.putString(Constants.CPNo, mStrRetId);
        addressFragment.setArguments(bundle);
        return addressFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mStrCPGUID = getArguments().getString(Constants.CPGUID);
        mStrRetID = getArguments().getString(Constants.CPNo);
        mStrRetName = getArguments().getString(Constants.RetName);
        myInflatedView = inflater.inflate(R.layout.fragment_address_lay, container, false);


        onInitUI();
        onRetailerDetails();
        setValuesToUI();
        return myInflatedView;
    }

    /*
     * TODO This method initialize UI
     */
    private void onInitUI() {
        iv_call = (ImageView) myInflatedView.findViewById(R.id.call);
        iv_call.setOnClickListener(this);

        iv_sms = (ImageView) myInflatedView.findViewById(R.id.sms);
        iv_sms.setOnClickListener(this);

        iv_mail = (ImageView) myInflatedView.findViewById(R.id.mail);
        iv_mail.setOnClickListener(this);

        iv_whatsApp = (ImageView) myInflatedView.findViewById(R.id.whats_app);
        iv_whatsApp.setOnClickListener(this);

        iv_appointment = (ImageView) myInflatedView.findViewById(R.id.appointment);
        iv_appointment.setOnClickListener(this);

        iv_dob_sel = (ImageView) myInflatedView.findViewById(R.id.iv_dob_sel);
        try {
            String typeValues = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SSCP + "' and " + Constants.Types + " eq '" + Constants.CPDOBUPD + "' &$top=1", Constants.TypeValue);
            if (!typeValues.equalsIgnoreCase(Constants.X)) {
                iv_dob_sel.setVisibility(View.GONE);
            } else {
                iv_dob_sel.setVisibility(View.VISIBLE);
            }
        } catch (OfflineODataStoreException e) {
            iv_dob_sel.setVisibility(View.GONE);
        } catch (Exception e) {
            iv_dob_sel.setVisibility(View.GONE);
        }
        iv_dob_sel.setOnClickListener(this);

        iv_annversary_sel = (ImageView) myInflatedView.findViewById(R.id.iv_annversary_sel);
        iv_annversary_sel.setOnClickListener(this);

        try {
            String typeValues = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SSCP + "' and " + Constants.Types + " eq '" + Constants.CPANNVUPD + "' &$top=1", Constants.TypeValue);
            if (!typeValues.equalsIgnoreCase(Constants.X)) {
                iv_annversary_sel.setVisibility(View.GONE);
            } else {
                iv_annversary_sel.setVisibility(View.VISIBLE);
            }
        } catch (OfflineODataStoreException e) {
            iv_annversary_sel.setVisibility(View.GONE);
        } catch (Exception e) {
            iv_annversary_sel.setVisibility(View.GONE);
        }

        tvFirstAddress = (TextView) myInflatedView.findViewById(R.id.tv_add1);
        tvSecondAddress = (TextView) myInflatedView.findViewById(R.id.tv_second_address);
        tv_third_address = (TextView) myInflatedView.findViewById(R.id.tv_third_address);
        tv_fourth_address = (TextView) myInflatedView.findViewById(R.id.tv_fourth_address);
        tv_land_mark = (TextView) myInflatedView.findViewById(R.id.tv_land_mark);
        tv_postalCode = (TextView) myInflatedView.findViewById(R.id.tv_postal_code);
        tv_OwnerName = (TextView) myInflatedView.findViewById(R.id.tv_ownerName);
        tvDistributorName = (TextView) myInflatedView.findViewById(R.id.tv_distributor_name);
        tvContactNum = (TextView) myInflatedView.findViewById(R.id.tv_contact_num);
        tvRetailerCategory = (TextView) myInflatedView.findViewById(R.id.tv_ret_category);
        tvClassification = (TextView) myInflatedView.findViewById(R.id.tv_classification);
        tvWeeklyOff = (TextView) myInflatedView.findViewById(R.id.tv_weekly_off);
        tvDOB = (TextView) myInflatedView.findViewById(R.id.tv_date_of_birth);
        tvAnniversary = (TextView) myInflatedView.findViewById(R.id.tv_anniversary);
        tvEmilView = (TextView) myInflatedView.findViewById(R.id.tv_email_id);
        tv_lat_value = (TextView) myInflatedView.findViewById(R.id.tv_lat_value);
        tv_long_value = (TextView) myInflatedView.findViewById(R.id.tv_long_value);

        iv_lat_long_icon = (ImageView) myInflatedView.findViewById(R.id.iv_lat_long_icon);

        try {
            String typeValues = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SSCP + "' and " + Constants.Types + " eq '" + Constants.GEOLOCUPD + "' &$top=1", Constants.TypeValue);
            if (!typeValues.equalsIgnoreCase(Constants.X)) {
                iv_lat_long_icon.setVisibility(View.GONE);
            } else {
                iv_lat_long_icon.setVisibility(View.VISIBLE);
            }
        } catch (OfflineODataStoreException e) {
            iv_lat_long_icon.setVisibility(View.GONE);
        } catch (Exception e) {
            iv_lat_long_icon.setVisibility(View.GONE);
        }

        ll_lat_long_layout = (LinearLayout) myInflatedView.findViewById(R.id.ll_lat_long_layout);


        try {
            String typeValues = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SSROUT + "' and " + Constants.Types + " eq '" + Constants.DISPGEO + "' &$top=1", Constants.TypeValue);
            if (!typeValues.equalsIgnoreCase(Constants.X)) {
                ll_lat_long_layout.setVisibility(View.GONE);
            } else {
                ll_lat_long_layout.setVisibility(View.VISIBLE);
            }
        } catch (OfflineODataStoreException e) {
            ll_lat_long_layout.setVisibility(View.GONE);
        } catch (Exception e) {
            ll_lat_long_layout.setVisibility(View.GONE);
        }
        tv_gst_in = (TextView) myInflatedView.findViewById(R.id.tv_gst_in);
        tv_gst_reg_status = (TextView) myInflatedView.findViewById(R.id.tv_gst_reg_status);
    }

    private double mDouLatVal = 0.0, mDouLongVal = 0.0;

    /*
     * TODO This method set values to UI
     */
    private void setValuesToUI() {
        tvRetailerCategory.setText(selCPTypeDesc);
        tvDistributorName.setText(mDistributorName);
        tvContactNum.setText(mContactNum);
        tvClassification.setText(mClassification);
        iv_lat_long_icon.setOnClickListener(this);
        if (mDouLatVal == 0.0 || mDouLongVal == 0.0) {

            iv_lat_long_icon.setImageResource(R.drawable.ic_loca_red_mark_small);

            tv_lat_value.setText(Constants.addZerosAfterDecimal(mDouLatVal + "", 12));
            tv_long_value.setText(Constants.addZerosAfterDecimal(mDouLongVal + "", 12));

        } else {
//            tv_lat_value.setText(mDouLatVal+"");
//            tv_long_value.setText(mDouLongVal+"");

            tv_lat_value.setText(Constants.addZerosAfterDecimal(mDouLatVal + "", 12));
            tv_long_value.setText(Constants.addZerosAfterDecimal(mDouLongVal + "", 12));

            iv_lat_long_icon.setImageResource(R.drawable.ic_loca_green_mark_small);
        }


        setDOB();
        setAnniverssaryDate();
        tvWeeklyOff.setText(mWeeklyOff);

        if (mStrFirstAddress != null && !mStrFirstAddress.equalsIgnoreCase("")) {
            tvFirstAddress.setText(mStrFirstAddress);
        } else {
            tvFirstAddress.setVisibility(View.GONE);
        }

        if (mStrAddressTwo != null && mStrAddressTwo.equalsIgnoreCase("")) {
            tvSecondAddress.setText(mStrAddressTwo);
        } else {
            tvSecondAddress.setVisibility(View.GONE);
        }

        if (mStrAddressThree != null && !mStrAddressThree.equalsIgnoreCase("")) {
            tv_third_address.setText(mStrAddressThree);
        } else {
            tv_third_address.setVisibility(View.GONE);
        }

        if (mStrAddressFour != null && !mStrAddressFour.equalsIgnoreCase("")) {
            tv_fourth_address.setText(mStrAddressFour);
        } else {
            tv_fourth_address.setVisibility(View.GONE);
        }

        if (!mStrPostalCode.equalsIgnoreCase("") && !mStrDistrictDesc.equalsIgnoreCase("")) {
            tv_postalCode.setText(mStrDistrictDesc + " " + mStrPostalCode);
        } else if (!mStrPostalCode.equalsIgnoreCase("") && mStrDistrictDesc.equalsIgnoreCase("")) {
            tv_postalCode.setText(mStrPostalCode);
        } else if (mStrPostalCode.equalsIgnoreCase("") && !mStrDistrictDesc.equalsIgnoreCase("")) {
            tv_postalCode.setText(mStrDistrictDesc);
        } else {
            tv_postalCode.setVisibility(View.GONE);
        }


        if (!mStrLandmark.equalsIgnoreCase("") && !mStrCityDesc.equalsIgnoreCase("")) {
            tv_land_mark.setText(mStrLandmark + "," + mStrCityDesc);
        } else if (!mStrLandmark.equalsIgnoreCase("") && mStrCityDesc.equalsIgnoreCase("")) {
            tv_land_mark.setText(mStrLandmark);
        } else if (mStrLandmark.equalsIgnoreCase("") && !mStrDistrictDesc.equalsIgnoreCase("")) {
            tv_land_mark.setText(mStrCityDesc);
        } else {
            tv_land_mark.setVisibility(View.GONE);
        }

        tv_OwnerName.setText(mStrOwnerName);
        tvEmilView.setText(mStrEmailID);

        tv_gst_in.setText(mStrGSTIN);
        tv_gst_reg_status.setText(mStrGSTREGStatus);

    }

    private int mYear = 0, mMonth = 0, mDay = 0, mnt = 0;
    private int mYearAnniver = 0, mMonthAnniver = 0, mDayAnniver = 0, mntAnniver = 0;
    private String mon = "", day = "", dateOfBirth = "";
    private String monAnniver = "", dayAnniver = "", dateOfAnniver = "";
    String mStrDobDay = "", mStrDobYear = "";
    String mStrAnniverDay = "", mStrAnniverYear = "";
    int mIntDobMonth = 0;
    int mIntAnniverMonth = 0;

    private void setDOB() {
        final Calendar calDob = Calendar.getInstance();
        if (mDOB.equalsIgnoreCase("")) {
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

            tvDOB.setText("");


        } else {
            mStrDobDay = UtilConstants.getNextVisitDay(mDOB);
            mIntDobMonth = UtilConstants.getNextVisitMonth(mDOB) - 1;
            mStrDobYear = UtilConstants.getNextVisitear(mDOB);

            calDob.set(Calendar.DAY_OF_MONTH,
                    Integer.parseInt(mStrDobDay));
            calDob.set(Calendar.MONTH, mIntDobMonth);
            calDob.set(Calendar.YEAR, Integer.parseInt(mStrDobYear));


            mDay = calDob.get(Calendar.DAY_OF_MONTH);
            mMonth = calDob.get(Calendar.MONTH);
            mYear = calDob.get(Calendar.YEAR);

            dateOfBirth = mYear + "-" + (mMonth + 1 < 10 ? (getString(R.string.Zero_0) + (mMonth + 1)) : mMonth + 1) + "-" + (mDay < 10 ? ("0" + (mDay)) : mDay);

            tvDOB.setText(new StringBuilder().append(mDay)
                    .append("/").append((mMonth + 1 < 10 ? (getString(R.string.Zero_0) + (mMonth + 1)) : mMonth + 1))
                    .append("/").append("").append(mYear));
        }

    }

    private void setAnniverssaryDate() {
        final Calendar calAnniver = Calendar.getInstance();
        if (mAnniversary.equalsIgnoreCase("")) {
            mYearAnniver = calAnniver.get(Calendar.YEAR);
            mMonthAnniver = calAnniver.get(Calendar.MONTH);
            mDayAnniver = calAnniver.get(Calendar.DAY_OF_MONTH);
            mntAnniver = mMonthAnniver + 1;
            if (mntAnniver < 10)
                monAnniver = getString(R.string.Zero_0) + mnt;
            else
                monAnniver = "" + mntAnniver;
            dayAnniver = "" + mDayAnniver;
            if (mDayAnniver < 10)
                dayAnniver = getString(R.string.Zero_0) + mDayAnniver;

            tvAnniversary.setText("");


        } else {
            mStrAnniverDay = UtilConstants.getNextVisitDay(mAnniversary);
            mIntAnniverMonth = UtilConstants.getNextVisitMonth(mAnniversary) - 1;
            mStrAnniverYear = UtilConstants.getNextVisitear(mAnniversary);

            calAnniver.set(Calendar.DAY_OF_MONTH,
                    Integer.parseInt(mStrAnniverDay));
            calAnniver.set(Calendar.MONTH, mIntAnniverMonth);
            calAnniver.set(Calendar.YEAR, Integer.parseInt(mStrAnniverYear));


            mDayAnniver = calAnniver.get(Calendar.DAY_OF_MONTH);
            mMonthAnniver = calAnniver.get(Calendar.MONTH);
            mYearAnniver = calAnniver.get(Calendar.YEAR);

            dateOfAnniver = mYearAnniver + "-" + (mMonthAnniver + 1 < 10 ?
                    (getString(R.string.Zero_0) + (mMonthAnniver + 1)) : mMonthAnniver + 1) + "-" +
                    (mDayAnniver < 10 ? ("0" + (mDayAnniver)) : mDayAnniver);

            tvAnniversary.setText(new StringBuilder().append(mDayAnniver)
                    .append("/").append((mMonthAnniver + 1 < 10 ? (getString(R.string.Zero_0) + (mMonthAnniver + 1)) : mMonthAnniver + 1))
                    .append("/").append("").append(mYearAnniver));
        }

    }

    static final int DATE_DIALOG_ID = 0;
    static final int ANNIVERSARY_DATE_DIALOG_ID = 1;

    @SuppressLint("NewApi")
    protected Dialog onDatePickerDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), mDateSetListener,
                        mYear, mMonth, mDay);
                Calendar c = Calendar.getInstance();
                Date newDate = c.getTime();
                datePicker.getDatePicker().setMaxDate(newDate.getTime());
                return datePicker;

            case ANNIVERSARY_DATE_DIALOG_ID:
                DatePickerDialog datePickerAnniver = new DatePickerDialog(getActivity(), mDateSetListenerAnniver,
                        mYearAnniver, mMonthAnniver, mDayAnniver);
                Calendar calAnniver = Calendar.getInstance();
                Date anniverDate = calAnniver.getTime();
                datePickerAnniver.getDatePicker().setMaxDate(anniverDate.getTime());
                return datePickerAnniver;
        }
        return null;
    }

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
            tvDOB.setText(new StringBuilder().append(mDay)
                    .append("/").append(mon)
                    .append("/").append("").append(mYear));
            updateDOBToChannelPartner();
        }
    };

    private void updateDOBToChannelPartner() {
        Hashtable table = new Hashtable();
        try {
            String retDetgry = Constants.ChannelPartners + "(guid'" + mStrCPGUID.toUpperCase() + "')";
            ODataEntity retilerEntity = OfflineManager.getRetDetails(retDetgry);
            oDataProperties = retilerEntity.getProperties();
            oDataProperty = oDataProperties.get(Constants.CPGUID);
            table.put(Constants.CPGUID, mStrCPGUID.toUpperCase());
            oDataProperty = oDataProperties.get(Constants.MobileNo);
            table.put(Constants.MobileNo, (String) oDataProperty.getValue());
            //noinspection unchecked
            table.put(Constants.DOB, dateOfBirth + Constants.Time_Value);

            oDataProperty = oDataProperties.get(Constants.Anniversary);
            try {
                //noinspection unchecked
                table.put(Constants.Anniversary, oDataProperty.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }


            table.put(Constants.OutletName, (String) oDataProperties.get(Constants.OutletName).getValue() != null ? (String) oDataProperties.get(Constants.OutletName).getValue() : "");
            table.put(Constants.OwnerName, (String) oDataProperties.get(Constants.OwnerName).getValue() != null ? (String) oDataProperties.get(Constants.OwnerName).getValue() : "");
            table.put(Constants.RetailerProfile, (String) oDataProperties.get(Constants.RetailerProfile).getValue() != null ? (String) oDataProperties.get(Constants.RetailerProfile).getValue() : "");
            table.put(Constants.Group2, (String) oDataProperties.get(Constants.Group2).getValue() != null ? (String) oDataProperties.get(Constants.Group2).getValue() : "");
            table.put(Constants.PAN, (String) oDataProperties.get(Constants.PAN).getValue() != null ? (String) oDataProperties.get(Constants.PAN).getValue() : "");
            table.put(Constants.VATNo, (String) oDataProperties.get(Constants.VATNo).getValue() != null ? (String) oDataProperties.get(Constants.VATNo).getValue() : "");
            table.put(Constants.EmailID, (String) oDataProperties.get(Constants.EmailID).getValue() != null ? (String) oDataProperties.get(Constants.EmailID).getValue() : "");
            table.put(Constants.MobileNo, (String) oDataProperties.get(Constants.MobileNo).getValue() != null ? (String) oDataProperties.get(Constants.MobileNo).getValue() : "");
            table.put(Constants.PostalCode, (String) oDataProperties.get(Constants.PostalCode).getValue() != null ? (String) oDataProperties.get(Constants.PostalCode).getValue() : "");
            table.put(Constants.Landmark, (String) oDataProperties.get(Constants.Landmark).getValue() != null ? (String) oDataProperties.get(Constants.Landmark).getValue() : "");
            table.put(Constants.Address1, (String) oDataProperties.get(Constants.Address1).getValue() != null ? (String) oDataProperties.get(Constants.Address1).getValue() : "");
            table.put(Constants.CPTypeID, (String) oDataProperties.get(Constants.CPTypeID).getValue() != null ? (String) oDataProperties.get(Constants.CPTypeID).getValue() : "");
            table.put(Constants.Latitude, oDataProperties.get(Constants.Latitude).getValue() != null ? oDataProperties.get(Constants.Latitude).getValue() : 0.0);
            table.put(Constants.Longitude, oDataProperties.get(Constants.Longitude).getValue() != null ? oDataProperties.get(Constants.Longitude).getValue() : 0.0);
            table.put(Constants.ParentID, (String) oDataProperties.get(Constants.ParentID).getValue() != null ? (String) oDataProperties.get(Constants.ParentID).getValue() : "");
            table.put(Constants.ParentTypeID, (String) oDataProperties.get(Constants.ParentTypeID).getValue() != null ? (String) oDataProperties.get(Constants.ParentTypeID).getValue() : "");
            table.put(Constants.ParentName, (String) oDataProperties.get(Constants.ParentName).getValue() != null ? (String) oDataProperties.get(Constants.ParentName).getValue() : "");
            table.put(Constants.StateID, (String) oDataProperties.get(Constants.StateID).getValue() != null ? (String) oDataProperties.get(Constants.StateID).getValue() : "");
            table.put(Constants.PartnerMgrGUID, (ODataGuid) oDataProperties.get(Constants.PartnerMgrGUID).getValue() != null ? (ODataGuid) oDataProperties.get(Constants.PartnerMgrGUID).getValue() : "");
            table.put(Constants.CityDesc, (String) oDataProperties.get(Constants.CityDesc).getValue() != null ? (String) oDataProperties.get(Constants.CityDesc).getValue() : "");
            table.put(Constants.CityID, (String) oDataProperties.get(Constants.CityID).getValue() != null ? (String) oDataProperties.get(Constants.CityID).getValue() : "");
            table.put(Constants.DistrictDesc, (String) oDataProperties.get(Constants.DistrictDesc).getValue() != null ? (String) oDataProperties.get(Constants.DistrictDesc).getValue() : "");
            table.put(Constants.DistrictID, (String) oDataProperties.get(Constants.DistrictID).getValue() != null ? (String) oDataProperties.get(Constants.DistrictID).getValue() : "");
            table.put(Constants.CPNo, (String) oDataProperties.get(Constants.CPNo).getValue() != null ? (String) oDataProperties.get(Constants.CPNo).getValue() : "");
            table.put(Constants.CPUID, (String) oDataProperties.get(Constants.CPUID).getValue() != null ? (String) oDataProperties.get(Constants.CPUID).getValue() : "");
            table.put(Constants.StatusID, (String) oDataProperties.get(Constants.StatusID).getValue() != null ? (String) oDataProperties.get(Constants.StatusID).getValue() : "");
            table.put(Constants.ApprvlStatusID, (String) oDataProperties.get(Constants.ApprvlStatusID).getValue() != null ? (String) oDataProperties.get(Constants.ApprvlStatusID).getValue() : "");

            table.put(Constants.WeeklyOff, (String) oDataProperties.get(Constants.WeeklyOff).getValue() != null ? (String) oDataProperties.get(Constants.WeeklyOff).getValue() : "");
            table.put(Constants.Tax1, (String) oDataProperties.get(Constants.Tax1).getValue() != null ? (String) oDataProperties.get(Constants.Tax1).getValue() : "");
            table.put(Constants.TaxRegStatus, (String) oDataProperties.get(Constants.TaxRegStatus).getValue() != null ? (String) oDataProperties.get(Constants.TaxRegStatus).getValue() : "");
            table.put(Constants.IsLatLongUpdate, Constants.X);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            table.put(Constants.LOGINID, loginIdVal);

            table.put(Constants.SetResourcePath, Constants.ChannelPartners + "(guid'" + mStrCPGUID.toUpperCase() + "')");
            if (retilerEntity.getEtag() != null) {
                table.put(Constants.Etag, retilerEntity.getEtag());
            }
            table.put(Constants.comingFrom, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        try {
            OfflineManager.updateRetilerBatchReq(table, Constants.DOB);
        } catch (ODataParserException e) {
            e.printStackTrace();
        }
        UtilConstants.showAlert(getString(R.string.str_dob_update), getActivity());
    }

    private void updateAnniverssiryToChannelPartner() {
        Hashtable hashtable = new Hashtable();
        try {
            String retDetgry = Constants.ChannelPartners + "(guid'" + mStrCPGUID.toUpperCase() + "')";
            ODataEntity retilerEntity = OfflineManager.getRetDetails(retDetgry);
            oDataProperties = retilerEntity.getProperties();
            oDataProperty = oDataProperties.get(Constants.CPGUID);
            hashtable.put(Constants.CPGUID, mStrCPGUID.toUpperCase());
            //noinspection unchecked
            hashtable.put(Constants.Anniversary, dateOfAnniver + Constants.Time_Value);

            oDataProperty = oDataProperties.get(Constants.DOB);
            //noinspection unchecked
            try {
                hashtable.put(Constants.DOB, oDataProperty.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }

            oDataProperty = oDataProperties.get(Constants.MobileNo);
            hashtable.put(Constants.MobileNo, (String) oDataProperty.getValue());

            hashtable.put(Constants.OutletName, (String) oDataProperties.get(Constants.OutletName).getValue() != null ? (String) oDataProperties.get(Constants.OutletName).getValue() : "");
            hashtable.put(Constants.OwnerName, (String) oDataProperties.get(Constants.OwnerName).getValue() != null ? (String) oDataProperties.get(Constants.OwnerName).getValue() : "");
            hashtable.put(Constants.RetailerProfile, (String) oDataProperties.get(Constants.RetailerProfile).getValue() != null ? (String) oDataProperties.get(Constants.RetailerProfile).getValue() : "");
            hashtable.put(Constants.Group2, (String) oDataProperties.get(Constants.Group2).getValue() != null ? (String) oDataProperties.get(Constants.Group2).getValue() : "");
            hashtable.put(Constants.PAN, (String) oDataProperties.get(Constants.PAN).getValue() != null ? (String) oDataProperties.get(Constants.PAN).getValue() : "");
            hashtable.put(Constants.VATNo, (String) oDataProperties.get(Constants.VATNo).getValue() != null ? (String) oDataProperties.get(Constants.VATNo).getValue() : "");
            hashtable.put(Constants.EmailID, (String) oDataProperties.get(Constants.EmailID).getValue() != null ? (String) oDataProperties.get(Constants.EmailID).getValue() : "");
            hashtable.put(Constants.MobileNo, (String) oDataProperties.get(Constants.MobileNo).getValue() != null ? (String) oDataProperties.get(Constants.MobileNo).getValue() : "");
            hashtable.put(Constants.PostalCode, (String) oDataProperties.get(Constants.PostalCode).getValue() != null ? (String) oDataProperties.get(Constants.PostalCode).getValue() : "");
            hashtable.put(Constants.Landmark, (String) oDataProperties.get(Constants.Landmark).getValue() != null ? (String) oDataProperties.get(Constants.Landmark).getValue() : "");
            hashtable.put(Constants.Address1, (String) oDataProperties.get(Constants.Address1).getValue() != null ? (String) oDataProperties.get(Constants.Address1).getValue() : "");
            hashtable.put(Constants.CPTypeID, (String) oDataProperties.get(Constants.CPTypeID).getValue() != null ? (String) oDataProperties.get(Constants.CPTypeID).getValue() : "");
            hashtable.put(Constants.Latitude, oDataProperties.get(Constants.Latitude).getValue() != null ? oDataProperties.get(Constants.Latitude).getValue() : 0.0);
            hashtable.put(Constants.Longitude, oDataProperties.get(Constants.Longitude).getValue() != null ? oDataProperties.get(Constants.Longitude).getValue() : 0.0);
            hashtable.put(Constants.ParentID, (String) oDataProperties.get(Constants.ParentID).getValue() != null ? (String) oDataProperties.get(Constants.ParentID).getValue() : "");
            hashtable.put(Constants.ParentTypeID, (String) oDataProperties.get(Constants.ParentTypeID).getValue() != null ? (String) oDataProperties.get(Constants.ParentTypeID).getValue() : "");
            hashtable.put(Constants.ParentName, (String) oDataProperties.get(Constants.ParentName).getValue() != null ? (String) oDataProperties.get(Constants.ParentName).getValue() : "");
            hashtable.put(Constants.StateID, (String) oDataProperties.get(Constants.StateID).getValue() != null ? (String) oDataProperties.get(Constants.StateID).getValue() : "");

            hashtable.put(Constants.PartnerMgrGUID, (ODataGuid) oDataProperties.get(Constants.PartnerMgrGUID).getValue() != null ? (ODataGuid) oDataProperties.get(Constants.PartnerMgrGUID).getValue() : "");
            hashtable.put(Constants.CityDesc, (String) oDataProperties.get(Constants.CityDesc).getValue() != null ? (String) oDataProperties.get(Constants.CityDesc).getValue() : "");
            hashtable.put(Constants.CityID, (String) oDataProperties.get(Constants.CityID).getValue() != null ? (String) oDataProperties.get(Constants.CityID).getValue() : "");
            hashtable.put(Constants.DistrictDesc, (String) oDataProperties.get(Constants.DistrictDesc).getValue() != null ? (String) oDataProperties.get(Constants.DistrictDesc).getValue() : "");
            hashtable.put(Constants.DistrictID, (String) oDataProperties.get(Constants.DistrictID).getValue() != null ? (String) oDataProperties.get(Constants.DistrictID).getValue() : "");
            hashtable.put(Constants.CPNo, (String) oDataProperties.get(Constants.CPNo).getValue() != null ? (String) oDataProperties.get(Constants.CPNo).getValue() : "");
            hashtable.put(Constants.CPUID, (String) oDataProperties.get(Constants.CPUID).getValue() != null ? (String) oDataProperties.get(Constants.CPUID).getValue() : "");
            hashtable.put(Constants.StatusID, (String) oDataProperties.get(Constants.StatusID).getValue() != null ? (String) oDataProperties.get(Constants.StatusID).getValue() : "");
            hashtable.put(Constants.ApprvlStatusID, (String) oDataProperties.get(Constants.ApprvlStatusID).getValue() != null ? (String) oDataProperties.get(Constants.ApprvlStatusID).getValue() : "");
            hashtable.put(Constants.WeeklyOff, (String) oDataProperties.get(Constants.WeeklyOff).getValue() != null ? (String) oDataProperties.get(Constants.WeeklyOff).getValue() : "");
            hashtable.put(Constants.Tax1, (String) oDataProperties.get(Constants.Tax1).getValue() != null ? (String) oDataProperties.get(Constants.Tax1).getValue() : "");
            hashtable.put(Constants.TaxRegStatus, (String) oDataProperties.get(Constants.TaxRegStatus).getValue() != null ? (String) oDataProperties.get(Constants.TaxRegStatus).getValue() : "");
            hashtable.put(Constants.IsLatLongUpdate, Constants.X);
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            hashtable.put(Constants.LOGINID, loginIdVal);

            hashtable.put(Constants.SetResourcePath, Constants.ChannelPartners + "(guid'" + mStrCPGUID.toUpperCase() + "')");
            if (retilerEntity.getEtag() != null) {
                hashtable.put(Constants.Etag, retilerEntity.getEtag());
            }
            hashtable.put(Constants.comingFrom, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        try {
            OfflineManager.updateRetilerBatchReq(hashtable, Constants.Anniversary);
        } catch (ODataParserException e) {
            e.printStackTrace();
        }

        UtilConstants.showAlert(getString(R.string.str_Anniversary_update), getActivity());

    }

    private DatePickerDialog.OnDateSetListener mDateSetListenerAnniver = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker v, int year, int monthOfYear,
                              int dayOfMonth) {
            mYearAnniver = year;
            mMonthAnniver = monthOfYear;
            mDayAnniver = dayOfMonth;
            String mon = "";
            String day = "";
            int mnt = 0;
            mnt = mMonthAnniver + 1;
            if (mnt < 10)
                mon = getString(R.string.Zero_0) + mnt;
            else
                mon = "" + mnt;
            day = "" + mDayAnniver;
            if (mDayAnniver < 10)
                day = getString(R.string.Zero_0) + mDayAnniver;
            dateOfAnniver = mYearAnniver + "-" + mon + "-" + day;
            tvAnniversary.setText(new StringBuilder().append(mDayAnniver)
                    .append("/").append(mon)
                    .append("/").append("").append(mYearAnniver));
            updateAnniverssiryToChannelPartner();
        }
    };

    /*
     * TODO This method get retailer address details.
     */
    private void onRetailerDetails() {
        try {
            String retDetgry = Constants.ChannelPartners + "(guid'" + mStrCPGUID.toUpperCase() + "')";
            ODataEntity retilerEntity = OfflineManager.getRetDetails(retDetgry);
            oDataProperties = retilerEntity.getProperties();
            oDataProperty = oDataProperties.get(Constants.EmailID);
            mStrEmailID = (String) oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.ParentName);
            mDistributorName = (String) oDataProperty.getValue();

            oDataProperty = oDataProperties.get(Constants.PartnerMgrGUID);
            try {
                ODataGuid mParGUID = (ODataGuid) oDataProperty.getValue();
                partnerGUID = mParGUID.guidAsString36();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (mDistributorName != null && !mDistributorName.equalsIgnoreCase("")) {

                } else {
                    mDistributorName = Constants.getDistNameFromCPDMSDIV(mStrCPGUID.toUpperCase(), partnerGUID.toUpperCase());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            oDataProperty = oDataProperties.get(Constants.MobileNo);
            mContactNum = (String) oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.CPTypeID);
            mRetCategory = (String) oDataProperty.getValue();

            oDataProperty = oDataProperties.get(Constants.WeeklyOffDesc);
            mWeeklyOff = (String) oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.DOB);
//            mDOB = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) oDataProperty.getValue());
            mDOB = UtilConstants.convertGregorianCalendarToYYYYMMDDFormat((GregorianCalendar) oDataProperty.getValue());
            oDataProperty = oDataProperties.get(Constants.Anniversary);
//            mAnniversary = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) oDataProperty.getValue());
            mAnniversary = UtilConstants.convertGregorianCalendarToYYYYMMDDFormat((GregorianCalendar) oDataProperty.getValue());
            oDataProperty = oDataProperties.get(Constants.CPTypeID);
            mStrCPTypeId = (String) oDataProperty.getValue();

            oDataProperty = oDataProperties.get(Constants.CPGUID);
            mCpGuid = (ODataGuid) oDataProperty.getValue();

            selCPTypeDesc = OfflineManager.getValueByColumnName(Constants.CPDMSDivisions + "?$select=" + Constants.Group3Desc + " &$filter="
                    + Constants.CPGUID + " eq guid'" + mCpGuid.guidAsString36().toUpperCase() + "'", Constants.Group3Desc);
            mClassification = OfflineManager.getValueByColumnName(Constants.CPDMSDivisions + "?$select=" + Constants.Group4Desc + " &$filter="
                    + Constants.CPGUID + " eq guid'" + mCpGuid.guidAsString36().toUpperCase() + "'", Constants.Group4Desc);

            oDataProperty = oDataProperties.get(Constants.Address1);
            mStrFirstAddress = (String) oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.Address2);
            mStrAddressTwo = (String) oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.Address3);
            mStrAddressThree = (String) oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.Address4);
            mStrAddressFour = (String) oDataProperty.getValue();
            oDataProperty = oDataProperties.get(Constants.Landmark);
            mStrLandmark = oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";
            oDataProperty = oDataProperties.get(Constants.PostalCode);
            mStrPostalCode = oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";
            oDataProperty = oDataProperties.get(Constants.DistrictDesc);
            mStrDistrictDesc = oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";
            oDataProperty = oDataProperties.get(Constants.CityDesc);
            mStrCityDesc = oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";
            oDataProperty = oDataProperties.get(Constants.OwnerName);
            mStrOwnerName = oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.Latitude);
            BigDecimal mDecimalLatitude = (BigDecimal) oDataProperty.getValue();  //---------> Decimal property

            try {
                if (mDecimalLatitude != null) {
                    mDouLatVal = mDecimalLatitude.doubleValue();
                } else {
                    mDouLatVal = 0.0;
                }
            } catch (Exception e) {
                mDouLatVal = 0.0;
            }

            oDataProperty = oDataProperties.get(Constants.Longitude);
            BigDecimal mDecimalLongitude = (BigDecimal) oDataProperty.getValue();  //---------> Decimal property

            try {
                if (mDecimalLongitude != null) {
                    mDouLongVal = mDecimalLongitude.doubleValue();
                } else {
                    mDouLongVal = 0.0;
                }
            } catch (Exception e) {
                mDouLongVal = 0.0;
            }

            try {
                oDataProperty = oDataProperties.get(Constants.Tax1);
                mStrGSTIN = oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

                oDataProperty = oDataProperties.get(Constants.TaxRegStatusDesc);
                mStrGSTREGStatus = oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";
            } catch (Exception e) {
                mStrGSTIN = "";
                mStrGSTREGStatus = "";
            }

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call:
                onCall();
                break;
            case R.id.sms:
                onSMS();
                break;
            case R.id.mail:
                onMail();
                break;
            case R.id.tv_email_id:
                onMail();
                break;
            case R.id.whats_app:
                whatsAppCall();
                break;
            case R.id.appointment:
                appointment();
                break;
            case R.id.iv_lat_long_icon:
                setLatLongVal();
                break;
            case R.id.iv_dob_sel:
                selDOB();
                break;
            case R.id.iv_annversary_sel:
                selAnniversary();
                break;

        }
    }

    private void selDOB() {
        onDatePickerDialog(DATE_DIALOG_ID).show();
    }

    private void selAnniversary() {
        onDatePickerDialog(ANNIVERSARY_DATE_DIALOG_ID).show();
    }

    private void setLatLongVal() {
        String mStrAlertMsg = getString(R.string.update_lat_long_retailer);
        alertMSGLatAndLong(mStrAlertMsg);
    }

    private void onPermissionLatLong() {
        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.checking_pemission));

        LocationUtils.checkLocationPermission(getActivity(), new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                closeProgressDialog();
                if (status) {
                    locationPerGranted();
                }
            }
        });
    }

    private void alertMSGLatAndLong(String mAlertMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
        builder.setMessage(mAlertMsg).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onPermissionLatLong();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    private void setLatLonValues() {
        tv_lat_value.setText(Constants.addZerosAfterDecimal(UtilConstants.latitude + "", 12));
        tv_long_value.setText(Constants.addZerosAfterDecimal(UtilConstants.longitude + "", 12));
        iv_lat_long_icon.setImageResource(R.drawable.ic_loca_green_mark_small);
    }

    private void locationPerGranted() {
        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
        Constants.getLocation(getActivity(), new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closeProgressDialog();
                if (status) {
                    setLatLonValues();
                    updateLatAndLonToChannelPartner();
                }
            }
        });
    }

    private void closeProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void updateLatAndLonToChannelPartner() {
        Hashtable table = new Hashtable();
        try {
            String retDetgry = Constants.ChannelPartners + "(guid'" + mStrCPGUID.toUpperCase() + "')";
            ODataEntity retilerEntity = OfflineManager.getRetDetails(retDetgry);
            oDataProperties = retilerEntity.getProperties();
            oDataProperty = oDataProperties.get(Constants.CPGUID);
            table.put(Constants.CPGUID, mStrCPGUID.toUpperCase());
            //noinspection unchecked
            try {
                oDataProperty = oDataProperties.get(Constants.Anniversary);
                //noinspection unchecked
                table.put(Constants.Anniversary, oDataProperty.getValue());

                oDataProperty = oDataProperties.get(Constants.DOB);
                //noinspection unchecked
                table.put(Constants.DOB, oDataProperty.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
            oDataProperty = oDataProperties.get(Constants.MobileNo);
            table.put(Constants.MobileNo, (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "");
            table.put(Constants.OutletName, (String) oDataProperties.get(Constants.OutletName).getValue() != null ? (String) oDataProperties.get(Constants.OutletName).getValue() : "");
            table.put(Constants.OwnerName, (String) oDataProperties.get(Constants.OwnerName).getValue() != null ? (String) oDataProperties.get(Constants.OwnerName).getValue() : "");
            table.put(Constants.RetailerProfile, (String) oDataProperties.get(Constants.RetailerProfile).getValue() != null ? (String) oDataProperties.get(Constants.RetailerProfile).getValue() : "");
            table.put(Constants.Group2, (String) oDataProperties.get(Constants.Group2).getValue() != null ? (String) oDataProperties.get(Constants.Group2).getValue() : "");
            table.put(Constants.PAN, (String) oDataProperties.get(Constants.PAN).getValue() != null ? (String) oDataProperties.get(Constants.PAN).getValue() : "");
            table.put(Constants.VATNo, (String) oDataProperties.get(Constants.VATNo).getValue() != null ? (String) oDataProperties.get(Constants.VATNo).getValue() : "");
            table.put(Constants.EmailID, (String) oDataProperties.get(Constants.EmailID).getValue() != null ? (String) oDataProperties.get(Constants.EmailID).getValue() : "");
            table.put(Constants.MobileNo, (String) oDataProperties.get(Constants.MobileNo).getValue() != null ? (String) oDataProperties.get(Constants.MobileNo).getValue() : "");
            table.put(Constants.PostalCode, (String) oDataProperties.get(Constants.PostalCode).getValue() != null ? (String) oDataProperties.get(Constants.PostalCode).getValue() : "");
            table.put(Constants.Landmark, (String) oDataProperties.get(Constants.Landmark).getValue() != null ? (String) oDataProperties.get(Constants.Landmark).getValue() : "");
            table.put(Constants.Address1, (String) oDataProperties.get(Constants.Address1).getValue() != null ? (String) oDataProperties.get(Constants.Address1).getValue() : "");
            table.put(Constants.CPTypeID, (String) oDataProperties.get(Constants.CPTypeID).getValue() != null ? (String) oDataProperties.get(Constants.CPTypeID).getValue() : "");
            table.put(Constants.Latitude, oDataProperties.get(Constants.Latitude).getValue() != null ? oDataProperties.get(Constants.Latitude).getValue() : 0.0);
            table.put(Constants.Longitude, oDataProperties.get(Constants.Longitude).getValue() != null ? oDataProperties.get(Constants.Longitude).getValue() : 0.0);
            table.put(Constants.ParentID, (String) oDataProperties.get(Constants.ParentID).getValue() != null ? (String) oDataProperties.get(Constants.ParentID).getValue() : "");
            table.put(Constants.ParentTypeID, (String) oDataProperties.get(Constants.ParentTypeID).getValue() != null ? (String) oDataProperties.get(Constants.ParentTypeID).getValue() : "");
            table.put(Constants.ParentName, (String) oDataProperties.get(Constants.ParentName).getValue() != null ? (String) oDataProperties.get(Constants.ParentName).getValue() : "");
            table.put(Constants.StateID, (String) oDataProperties.get(Constants.StateID).getValue() != null ? (String) oDataProperties.get(Constants.StateID).getValue() : "");

            table.put(Constants.PartnerMgrGUID, (ODataGuid) oDataProperties.get(Constants.PartnerMgrGUID).getValue() != null ? (ODataGuid) oDataProperties.get(Constants.PartnerMgrGUID).getValue() : "");
            table.put(Constants.CityDesc, (String) oDataProperties.get(Constants.CityDesc).getValue() != null ? (String) oDataProperties.get(Constants.CityDesc).getValue() : "");
            table.put(Constants.CityID, (String) oDataProperties.get(Constants.CityID).getValue() != null ? (String) oDataProperties.get(Constants.CityID).getValue() : "");
            table.put(Constants.DistrictDesc, (String) oDataProperties.get(Constants.DistrictDesc).getValue() != null ? (String) oDataProperties.get(Constants.DistrictDesc).getValue() : "");
            table.put(Constants.DistrictID, (String) oDataProperties.get(Constants.DistrictID).getValue() != null ? (String) oDataProperties.get(Constants.DistrictID).getValue() : "");
            table.put(Constants.CPNo, (String) oDataProperties.get(Constants.CPNo).getValue() != null ? (String) oDataProperties.get(Constants.CPNo).getValue() : "");
            table.put(Constants.CPUID, (String) oDataProperties.get(Constants.CPUID).getValue() != null ? (String) oDataProperties.get(Constants.CPUID).getValue() : "");
            table.put(Constants.StatusID, (String) oDataProperties.get(Constants.StatusID).getValue() != null ? (String) oDataProperties.get(Constants.StatusID).getValue() : "");
            table.put(Constants.ApprvlStatusID, (String) oDataProperties.get(Constants.ApprvlStatusID).getValue() != null ? (String) oDataProperties.get(Constants.ApprvlStatusID).getValue() : "");
            table.put(Constants.WeeklyOff, (String) oDataProperties.get(Constants.WeeklyOff).getValue() != null ? (String) oDataProperties.get(Constants.WeeklyOff).getValue() : "");
            table.put(Constants.Tax1, (String) oDataProperties.get(Constants.Tax1).getValue() != null ? (String) oDataProperties.get(Constants.Tax1).getValue() : "");
            table.put(Constants.TaxRegStatus, (String) oDataProperties.get(Constants.TaxRegStatus).getValue() != null ? (String) oDataProperties.get(Constants.TaxRegStatus).getValue() : "");
            table.put(Constants.Latitude, BigDecimal.valueOf(UtilConstants.latitude));
            table.put(Constants.Longitude, BigDecimal.valueOf(UtilConstants.longitude));
            table.put(Constants.Source, "MOBILE");

            try {
                table.put(Constants.CreatedAt, oDataProperties.get(Constants.CreatedAt).getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                table.put(Constants.CreatedOn, oDataProperties.get(Constants.CreatedOn).getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                table.put(Constants.CreatedBy, (String) oDataProperties.get(Constants.CreatedBy).getValue() != null ? (String) oDataProperties.get(Constants.CreatedBy).getValue() : "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                table.put(Constants.RouteID, (String) oDataProperties.get(Constants.RouteID).getValue() != null ? (String) oDataProperties.get(Constants.RouteID).getValue() : "");
            } catch (Exception e) {
                e.printStackTrace();
            }


            table.put(Constants.IsLatLongUpdate, Constants.X);


            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            table.put(Constants.LOGINID, loginIdVal);

            table.put(Constants.SetResourcePath, Constants.ChannelPartners + "(guid'" + mStrCPGUID.toUpperCase() + "')");
            if (retilerEntity.getEtag() != null) {
                table.put(Constants.Etag, retilerEntity.getEtag());
            }
            table.put(Constants.comingFrom, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }


        try {
            OfflineManager.updateRetilerBatchReq(table, Constants.Latitude);
        } catch (ODataParserException e) {
            e.printStackTrace();
        }

        UtilConstants.showAlert(getString(R.string.str_lat_long_update), getActivity());

    }

    private void appointment() {
        Intent intent = new Intent(getActivity(), AppointmentCreate.class);
        intent.putExtra(Constants.RetailerName, mStrRetName);
        intent.putExtra(Constants.CPUID, mStrRetID);
        intent.putExtra(Constants.CPGUID, mStrCPGUID);
        startActivity(intent);
    }

    /*
     * TODO This method make a whats up call.
     */
    private void whatsAppCall() {
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName(Constants.whatsapp_packagename, Constants.whatsapp_conv_packagename));
            sendIntent.putExtra(Constants.jid, PhoneNumberUtils.stripSeparators(mContactNum) + Constants.whatsapp_domainname);
            startActivity(sendIntent);

        } catch (Exception e) {
            Constants.customAlertMessage(getActivity(), getString(R.string.alert_whatsapp_not_installed));
        }
    }

    /*
     * TODO This method make a sms.
     */
    private void onSMS() {
        if (!mContactNum.equalsIgnoreCase("")) {
            Uri smsUri = Uri.parse(Constants.sms_txt + mContactNum);
            Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
            startActivity(intent);
        } else {
            Constants.customAlertMessage(getActivity(), getString(R.string.alert_mobile_no_maintend));

        }
    }

    /*
     * TODO This method make a email.
     */
    private void onMail() {
        try {
            if (!mStrEmailID.equalsIgnoreCase("")) {
                Intent email = new Intent(Intent.ACTION_SEND);
                String[] emailList = {mStrEmailID};
                email.putExtra(Intent.EXTRA_EMAIL, emailList);
                email.setType(Constants.plain_text);
                startActivity(Intent
                        .createChooser(email, Constants.send_email));
            } else {
                Constants.customAlertMessage(getActivity(), getString(R.string.alert_mail_id_not_maintend));
            }

        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    /*
     * TODO This method make a call.
     */
    private void onCall() {
        try {
            if (!mContactNum.equalsIgnoreCase("")) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (mContactNum)));
                startActivity(dialIntent);
            } else {
                Constants.customAlertMessage(getActivity(), getString(R.string.alert_mobile_no_maintend));
            }
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case UtilConstants.Location_PERMISSION_CONSTANT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationUtils.checkLocationPermission(getActivity(), new LocationInterface() {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LocationUtils.REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationPerGranted();
            }
        }
    }
}
