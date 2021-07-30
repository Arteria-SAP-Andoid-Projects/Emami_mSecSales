package com.arteriatech.emami.appointment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.CustomSpinnerAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.BirthdaysBean;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.notification.NotificationSetClass;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataGuidDefaultImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class AppointmentCreate extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, UIListener, TimePickerDialog.OnTimeSetListener {

    TimePickerDialog mTimePicker;
    Map<String, String> startParameterMap;
    ArrayList<BirthdaysBean> alDataValutBirthDayList = null;
    ArrayList<BirthdaysBean> alRetBirthDayList = null;
    String[][] oneWeekDay;
    String splitDayMonth[] = null;
    BirthdaysBean alertsBean;
    ImageView iv_customer_edit;
    ODataGuid mCPGUID;
    private TextView tvRetailerName, tvBeatName, tvAppointmentdate, tvAppointmentStartTime, tvAppointmentEndTime, tvRemarksMandatory;
    private String mStrRetailerName = "", mStrRetailerId = "", mStrCPGUID = "";
    private Spinner spActivityType;
    private int timePicked = 0;/* if timePicked is 0 start time is selected else end Time is selecte */
    private String startTime = "";
    private String endTime = "";
    private String selectedDate = "",selDate="", activityTypeSelDesc = "", activityTypeMandatory = "", selectedDate2 = "";
    private EditText editRemarks;
    private String activityTypeSel = "";
    private String[][] arrAppointmentType = null;
    private ArrayList<CustomerBean> arrayRetList = null;
    private PopupWindow popwind;
    private boolean mBoolFirstTimeVisit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_create);
        ActionBarView.initActionBarView(this, true, getString(R.string.lbl_appointment_create));
        if (!Constants.restartApp(AppointmentCreate.this)) {
            initUI();
            getRetailerList();
            getRetailerBeatName();
            getAppointementTypes();
            setAppointmentValuesToSpinner();
            setDatePickerForAppointmentDate();
            setTimePickerForAppointmentTime();
        }
    }

    private void setTimePickerForAppointmentTime() {

        final Calendar c = Calendar.getInstance();
        tvAppointmentStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicked = 0;
                c.add(Calendar.MINUTE, 30);
                mTimePicker = new TimePickerDialog(AppointmentCreate.this, AppointmentCreate.this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
                mTimePicker.setTitle(getString(R.string.lbl_select_start_time));
                mTimePicker.show();
            }
        });
        tvAppointmentEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTime.equals("")) {
                    Constants.customAlertMessage(AppointmentCreate.this, getString(R.string.alert_please_start_time));
                    tvAppointmentStartTime.requestFocus();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
                    Date date = null;
                    try {
                        date = sdf.parse(startTime.replace("-", ":"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(date);
                    c2.add(Calendar.HOUR, 1);

                    mTimePicker = new TimePickerDialog(AppointmentCreate.this, AppointmentCreate.this, c2.get(Calendar.HOUR_OF_DAY), c2.get(Calendar.MINUTE), false);
                    timePicked = 1;
                    mTimePicker.setTitle(getString(R.string.lbl_select_end_time));
                    mTimePicker.show();
                }


            }
        });
    }

    private void updateTime(String time) {
        if (timePicked == 0) {
            if (!time.equalsIgnoreCase("")) {
                tvAppointmentStartTime.setBackgroundResource(R.drawable.textview_transprent);
                tvAppointmentEndTime.setBackgroundResource(R.drawable.textview_transprent);
            }

            tvAppointmentStartTime.setText(time);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.US);
            Date date = null;
            try {
                date = sdf.parse(startTime.replace("-", ":"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar c2 = Calendar.getInstance();
            c2.setTime(date);
            c2.add(Calendar.HOUR, 1);
            int selectedHour = c2.get(Calendar.HOUR_OF_DAY);
            int selectedMinute = c2.get(Calendar.MINUTE);
            endTime = selectedHour + "-" + selectedMinute;
            boolean isPM = (selectedHour >= 12);
            String endTempTime = String.format("%02d:%02d %s", (selectedHour == 12 || selectedHour == 0) ? 12 : selectedHour % 12, selectedMinute, isPM ? "PM" : "AM");
            if (!endTempTime.equalsIgnoreCase("")) {
                tvAppointmentEndTime.setBackgroundResource(R.drawable.textview_transprent);
            }
            tvAppointmentEndTime.setText(endTempTime);
        } else
            tvAppointmentEndTime.setText(time);
    }

    private Boolean isValidTime() {
        String time1 = startTime + "-00";
        String time2 = endTime + "-00";

        SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss");

        Date date1 = null;
        Date date2 = null;
        try {
            date1 = format.parse(time1);
            date2 = format.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long difference = 0;
        if (date2 != null && date1 != null) {
            difference = date2.getTime() - date1.getTime();
        }

        if (difference < 0)
            return false;
        else
            return true;
    }



    private void setDatePickerForAppointmentDate() {

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        final DatePickerDialog dialog = new DatePickerDialog(AppointmentCreate.this, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        tvAppointmentdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));

                dialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                dialog.show();
            }
        });


    }

    private void getRetailerList() {
        try {
            arrayRetList = OfflineManager.getRetailerListArray(Constants.ChannelPartners + "?$filter=(" + Constants.CPNo + " ne '' and " + Constants.CPNo + " ne null)" +
                    " and " + Constants.StatusID + " eq '01' and " + Constants.ApprvlStatusID + " eq '03'" +
                    " &$orderby=" + Constants.RetailerName + "%20asc");

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }


    }


    // TODO get Appointment type values from value help table
    private void getAppointementTypes() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'ActivityType'";
            arrAppointmentType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, Constants.PROP_ACTTYP);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (arrAppointmentType == null) {
            arrAppointmentType = new String[4][1];
            arrAppointmentType[0][0] = "";
            arrAppointmentType[1][0] = Constants.None;
            arrAppointmentType[2][0] = Constants.X;
            arrAppointmentType[3][0] = Constants.str_false;
        }else {
            arrAppointmentType = Constants.CheckForOtherInConfigValue(arrAppointmentType);
        }
    }

    private void setAppointmentValuesToSpinner() {


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrAppointmentType[1]);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spActivityType.setAdapter(dataAdapter);
        spActivityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                activityTypeSel = arrAppointmentType[0][position];
                activityTypeSelDesc = arrAppointmentType[1][position];
                activityTypeMandatory = arrAppointmentType[2][position];

                if (!activityTypeSel.equalsIgnoreCase("")) {
                    spActivityType.setBackgroundResource(R.drawable.spinner_bg);
                }
                if (activityTypeMandatory.equalsIgnoreCase(Constants.X)) {
                    tvRemarksMandatory.setVisibility(View.VISIBLE);

                } else {
                    tvRemarksMandatory.setVisibility(View.INVISIBLE);
                    editRemarks.setBackgroundResource(R.drawable.edittext);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /* Get Beat Name for the particular retailer */
    private void getRetailerBeatName() {
        String query = Constants.RouteSchedulePlans + "?$filter=" + Constants.VisitCPGUID + " eq '" + mStrCPGUID.replace("-", "").toUpperCase() + "'";

        try {
            String beatName = OfflineManager.getBeatNameForRetailer(query);
            tvBeatName.setText(beatName);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }

    private void initUI() {

        iv_customer_edit = (ImageView) findViewById(R.id.iv_customer_edit);
        tvRetailerName = (TextView) findViewById(R.id.tv_retailer_name);

        tvAppointmentStartTime = (TextView) findViewById(R.id.tv_appointment_starttime);
        tvAppointmentEndTime = (TextView) findViewById(R.id.tv_appointment_endtime);
        tvRetailerName = (TextView) findViewById(R.id.tv_retailer_name);
        tvBeatName = (TextView) findViewById(R.id.tv_beat_name);
        tvAppointmentdate = (TextView) findViewById(R.id.tv_appointment_date);
        tvRemarksMandatory = (TextView) findViewById(R.id.tv_remarks_mandatory);
        spActivityType = (Spinner) findViewById(R.id.sp_activity_type);
        editRemarks = (EditText) findViewById(R.id.edit_remarks);
        editRemarks.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});

        editRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editRemarks.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        startParameterMap = new HashMap<>();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mStrRetailerName = extras.getString(Constants.RetailerName);
            mStrRetailerId = extras.getString(Constants.CPNo);
            mStrCPGUID = extras.getString(Constants.CPGUID);
        }
        tvRetailerName.setText(mStrRetailerName);


        iv_customer_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayRetList != null) {
                    if (arrayRetList.size() > 0) {
                        getCustomSpinner();

                    }
                }
            }
        });


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

                if (isVaildationSuccess()) {
                    if(Constants.isDateToday(selectedDate2)){
                        // check time is past time
                        if(Constants.isTimePast(startTime,false)){
                            if(Constants.isTimePast(startTime,true)) {
                                if(Constants.checkTimeDuration(startTime,endTime)){
                                    saveAppointment();
                                }else{
                                    UtilConstants.showAlert(getString(R.string.msg_end_time_duration_20_min_from_start_time),AppointmentCreate.this);
                                }
                            }else{
                                UtilConstants.showAlert(getString(R.string.msg_time_should_be_grater_now),AppointmentCreate.this);
                            }
                        }else{
                            UtilConstants.showAlert(getString(R.string.pls_select_valid_time),AppointmentCreate.this);
                        }
                    }else{

                        if(Constants.checkTimeDuration(startTime,endTime)) {
                            saveAppointment();
                        }else{
                            UtilConstants.showAlert(getString(R.string.msg_end_time_duration_20_min_from_start_time),AppointmentCreate.this);
                        }
                    }

                }
                break;

            case android.R.id.home:
                onBackPressed();
                break;

        }
        return true;
    }

    public Boolean isVaildationSuccess() {
        if (tvAppointmentStartTime.getText().toString().equals("")
                || tvAppointmentEndTime.getText().toString().equals("") || activityTypeSel.equalsIgnoreCase("") ||
                tvAppointmentdate.getText().toString().equals("") ) {

            if (activityTypeSel.equalsIgnoreCase("")) {
                spActivityType.setBackgroundResource(R.drawable.error_spinner);
            }

            if (editRemarks.getText().toString().equals("")) {
                if (activityTypeMandatory.equalsIgnoreCase(Constants.X)) {
                    editRemarks.setBackgroundResource(R.drawable.edittext_border);

                }
            }
            if (tvAppointmentdate.getText().equals(""))
                tvAppointmentdate.setBackgroundResource(R.drawable.textview_border);
            if (tvAppointmentStartTime.getText().equals(""))
                tvAppointmentStartTime.setBackgroundResource(R.drawable.textview_border);
            if (tvAppointmentEndTime.getText().equals(""))
                tvAppointmentEndTime.setBackgroundResource(R.drawable.textview_border);

            Constants.customAlertMessage(AppointmentCreate.this, getString(R.string.validation_plz_enter_mandatory_flds));
            return false;
        } else if (editRemarks.getText().toString().equals("")) {
            if (activityTypeMandatory.equalsIgnoreCase(Constants.X)) {
                editRemarks.setBackgroundResource(R.drawable.edittext_border);
                Constants.customAlertMessage(AppointmentCreate.this, getString(R.string.validation_plz_enter_mandatory_flds));
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }


    }


    public String getNewDateTimeFormat() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy", Locale.US);
        String currentDateTimeString = null;
        try {
            currentDateTimeString = (String) DateFormat.format("yyyy-MM-dd\'T\'HH:mm:ss", formatter.parse(selectedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentDateTimeString;
    }


    private boolean checkSelectedDateIsTodayDateOrNot() {
        boolean isTodayDate = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date date1 = null;
            try {
                date1 = sdf.parse(selectedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date date2 = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            cal2.set(Calendar.HOUR_OF_DAY, 0);
            cal2.set(Calendar.MINUTE, 0);
            cal2.set(Calendar.SECOND, 0);
            cal2.set(Calendar.MILLISECOND, 0);

            if (cal.get(Calendar.YEAR)==cal2.get(Calendar.YEAR) &&
                    cal.get(Calendar.MONTH)==cal2.get(Calendar.MONTH) &&
                    cal.get(Calendar.DAY_OF_MONTH)==cal2.get(Calendar.DAY_OF_MONTH)) {
                isTodayDate = true;
            } else {
                isTodayDate = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTodayDate;
    }

    private void saveAppointment() {

        startParameterMap.put(Constants.CPNo, "");
        startParameterMap.put(Constants.VisitSeq, "");
        startParameterMap.put(Constants.CPName, "");
        startParameterMap.put(Constants.CPTypeID,  Constants.str_02);
        startParameterMap.put(Constants.VisitCatID,  Constants.str_03);
        startParameterMap.put(Constants.StatusID,  Constants.str_00);
        startParameterMap.put(Constants.PlannedDate, selectedDate2);

        startParameterMap.put(Constants.PlannedStartTime, startTime);
        startParameterMap.put(Constants.PlannedEndTime, endTime);
        startParameterMap.put(Constants.VisitTypeID, activityTypeSel);
        startParameterMap.put(Constants.VisitTypeDesc, activityTypeSelDesc);
        startParameterMap.put(Constants.Remarks, editRemarks.getText().toString());
        startParameterMap.put(Constants.VisitDate, UtilConstants.getNewDateTimeFormat());
        try {
            String routeId = Constants.getNameByCPGUID(Constants.CPDMSDivisions, Constants.RouteID, Constants.CPGUID, mStrCPGUID);
            if (!TextUtils.isEmpty(routeId)) {
                String qryForTodaysBeat = Constants.RouteSchedulePlans + "?$filter=" + Constants.RouteID + " eq '" + routeId + "' &$orderby=" + Constants.SequenceNo + "";
                ArrayList<CustomerBean> alRetailerList = OfflineManager.getRetailerListForOtherBeats(qryForTodaysBeat);
                startParameterMap.put(Constants.NoOfOutlet, String.valueOf(alRetailerList.size()));
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        mCPGUID = ODataGuidDefaultImpl.initWithString32(mStrCPGUID);

        Boolean isAlertExists = checkAlertExsistsForSameDateTime();

        if (!isAlertExists) {
            if (checkSelectedDateIsTodayDateOrNot()) {
                getTodayAlertsFromDataVault();
                alertsBean = new BirthdaysBean();
                alertsBean.setAppointMentDate(getNewDateTimeFormat());
                ODataDuration startDuration = UtilConstants.getTimeAsODataDuration(startTime);
                alertsBean.setAppointmentTime(startDuration.toString());
                ODataDuration endDuration = UtilConstants.getTimeAsODataDuration(endTime);
                alertsBean.setAppointmentEndTime(endDuration.toString());
                alertsBean.setAppointmentType(activityTypeSelDesc);
                alertsBean.setCPUID(mStrCPGUID.toUpperCase());

                try {

                    ODataProperty retProperty;
                    ODataPropMap retProperties;
                    String cpGuidQry = Constants.ChannelPartners + "(guid'" + mStrCPGUID.toUpperCase() + "') ";
                    ODataEntity retilerEntity = OfflineManager.getRetDetails(cpGuidQry);

                    retProperties = retilerEntity.getProperties();
                    retProperty = retProperties.get(Constants.RetailerName);
                    alertsBean.setRetailerName((String) retProperty.getValue());

                    retProperty = retProperties.get(Constants.OwnerName);
                    alertsBean.setOwnerName((String) retProperty.getValue());

                    retProperty = retProperties.get(Constants.MobileNo);
                    alertsBean.setMobileNo((String) retProperty.getValue());


                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                alertsBean.setAppointmentAlert(true);
                alertsBean.setAppointmentStatus("");

                todayAppointmentAssignToDataVault(alertsBean);

            }

            Constants.createVisit(startParameterMap, mCPGUID, AppointmentCreate.this, this);
        } else {
            Constants.customAlertMessage(this, getResources().getString(R.string.appointment_exists));
        }


    }

    private Boolean checkAlertExsistsForSameDateTime()
    {
        String query = Constants.Visits+"?$filter="+ Constants.StatusID+" eq '00' and "+ Constants.PlannedDate+
                " ge datetime'"+selectedDate2+"' ";
        ArrayList<AppointmentBean> appointmentList = null;
        try {
            appointmentList = OfflineManager.getAppointmentList(query);


        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (appointmentList != null) {
            int count = 0;
            for (int i = 0; i < appointmentList.size(); i++) {
                if (checkTimeExistsBetween(appointmentList.get(i).getPlannedStartTime(), appointmentList.get(i).getPlannedEndTime())) {
                    count++;
                }

            }
            if (count > 0)
                return true;
            else
                return false;
        } else
            return false;
    }
    //* Check Time Exists Between Start and End time

    private String convertDateToHourFormat(String timeInString) {
        String convertedDate = "";
        String stHour = "00";
        String stMin = "00";
        String mStrConvertTime = UtilConstants.convertTimeOnly(timeInString);
        String[] splitTime = mStrConvertTime.split(":");
        try {
            stHour = splitTime[0];
            stMin = splitTime[1];
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        convertedDate = stHour + ":" + stMin;
        return convertedDate;
    }

    private Boolean checkTimeExistsBetween(String startTimeOld, String endTimeOld) {

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

        Date startDate = null;
        Date endDate = null;
        Date selectedTime = null;
        try {
            startDate = sdf.parse(convertDateToHourFormat(startTimeOld));
            endDate = sdf.parse(convertDateToHourFormat(endTimeOld));
            selectedTime = sdf.parse(startTime.replace("-", ":"));

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(startDate);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(endDate);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(selectedTime);

            if (selectedTime.after(calendar1.getTime()) && selectedTime.before(calendar2.getTime())) {
                //checkes whether the current time is between 14:49:00 and 20:11:13.
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        selectedDate = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
        if ((monthOfYear + 1) > 9)
            selectedDate2 = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + "T00:00:00";
        else
            selectedDate2 = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth + "T00:00:00";


        if (!selectedDate.equalsIgnoreCase(""))
            tvAppointmentdate.setBackgroundResource(R.drawable.textview_transprent);


        tvAppointmentdate.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(dayOfMonth).append("/").append(monthOfYear + 1).append("/").append(year).append(" "));
    }


    @Override
    public void onRequestError(int i, Exception e) {
        Constants.customAlertMessage(this, getString(R.string.msg_appointment_creation_failed));

    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);

        builder.setMessage(R.string.msg_appointement_created_successfully)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @SuppressLint("NewApi")
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                setAppointmentNotification();
                                onBackPressed();
                            }
                        });


        builder.show();
    }

    private void setAppointmentNotification() {
        new NotificationSetClass(this);

    }

    @Override
    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
        boolean isPM = (selectedHour >= 12);
        if (timePicked == 0) {
            startTime = selectedHour + "-" + selectedMinute;


        } else {
            endTime = selectedHour + "-" + selectedMinute;
            if (!isValidTime()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);

                builder.setMessage(R.string.msg_invalid_time_selection)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    @SuppressLint("NewApi")
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        tvAppointmentEndTime.setText("");
                                        endTime = "";
                                    }
                                });


                builder.show();
            }


        }

        updateTime(String.format("%02d:%02d %s", (selectedHour == 12 || selectedHour == 0) ? 12 : selectedHour % 12, selectedMinute, isPM ? "PM" : "AM"));
    }

    private void getTodayAlertsFromDataVault() {

        oneWeekDay = UtilConstants.getOneweekValues(1);
        splitDayMonth = oneWeekDay[0][0].split("-");

        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME,
                0);
        String mStrBirthdayDate = settings.getString(Constants.BirthDayAlertsDate, "");

        if (mStrBirthdayDate.equalsIgnoreCase(UtilConstants.getDate1())) {
            // ToDO check birthday records available  in data vault
            String mStrDataAval = null;
            try {
                mStrDataAval = LogonCore.getInstance().getObjectFromStore(Constants.BirthDayAlertsKey);
            } catch (LogonCoreException e) {
                e.printStackTrace();
                mStrDataAval = "";
            }
            if (!mStrDataAval.equalsIgnoreCase("")) {
                // ToDO data vault data convert into json object
                try {
                    JSONObject fetchJsonHeaderObject = new JSONObject(mStrDataAval);
                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                    alDataValutBirthDayList = new ArrayList<>();
                    alDataValutBirthDayList = Constants.convertToBirthDayArryList(itemsString);
                    alRetBirthDayList = new ArrayList<>();
                    if (alDataValutBirthDayList != null && alDataValutBirthDayList.size() > 0) {
                        for (int k = 0; k < alDataValutBirthDayList.size(); k++) {
//                            if(alDataValutBirthDayList.get(k).getAlertGUID().equalsIgnoreCase("")){
                                if (!alDataValutBirthDayList.get(k).getAppointmentAlert()) {
                                    if ((alDataValutBirthDayList.get(k).getDOBStatus().equalsIgnoreCase("")
                                            && alDataValutBirthDayList.get(k).getDOB().contains(splitDayMonth[1] + "/" + splitDayMonth[0]))
                                            || (alDataValutBirthDayList.get(k).getAnniversaryStatus().equalsIgnoreCase("")
                                            && alDataValutBirthDayList.get(k).getAnniversary().contains(splitDayMonth[1] + "/" + splitDayMonth[0]))) {
                                        alRetBirthDayList.add(alDataValutBirthDayList.get(k));
                                    }
                                } else {
                                    alRetBirthDayList.add(alDataValutBirthDayList.get(k));
                                }
//                            }else{
//                                alRetBirthDayList.add(alDataValutBirthDayList.get(k));
//                            }


                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void todayAppointmentAssignToDataVault(BirthdaysBean alertsBean) {
        if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
            alRetBirthDayList.add(alRetBirthDayList.size(), alertsBean);
            Constants.assignValuesIntoDataVault(alRetBirthDayList);
        } else {
            alRetBirthDayList = new ArrayList<>();
            alRetBirthDayList.add(alertsBean);
            Constants.assignValuesIntoDataVault(alRetBirthDayList);
        }
    }

    @SuppressWarnings("deprecation")
    private void getCustomSpinner() {
        LayoutInflater inflater = (LayoutInflater) AppointmentCreate.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.pop_up_window, (ViewGroup) findViewById(R.id.PopUpView));

        RelativeLayout layout1 = (RelativeLayout) findViewById(R.id.relative_layout_spinner);
        popwind = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        popwind.setBackgroundDrawable(new BitmapDrawable());
        popwind.setTouchable(true);

        popwind.setOutsideTouchable(true);
        popwind.setHeight(ActionBar.LayoutParams.WRAP_CONTENT);

        popwind.setTouchInterceptor(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popwind.dismiss();
                    return true;
                }

                return false;
            }
        });

        popwind.setContentView(layout);

        popwind.showAsDropDown(layout1);

        ListView list_drop_down = (ListView) layout.findViewById(R.id.list_drop_down);

        ListAdapter custom_adapter = new CustomSpinnerAdapter(this, arrayRetList, popwind);

        for (int i = 0; i < arrayRetList.size() && !mBoolFirstTimeVisit; i++) {
            if (mStrCPGUID.equalsIgnoreCase(arrayRetList.get(i).getCPGUID())) {
                list_drop_down.setSelection(i);
                CustomerBean.SELECTED__SPINNER_INDEX = i;
                mBoolFirstTimeVisit = true;
                break;
            }
        }

        list_drop_down.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                // TODO Auto-generated method stub

                CustomerBean.SELECTED__SPINNER_INDEX = arg2;
                CustomerBean retSelected = arrayRetList.get(CustomerBean.SELECTED__SPINNER_INDEX);
                tvRetailerName.setText(retSelected.getRetailerName());

                mStrRetailerName = retSelected.getRetailerName();
                mStrRetailerId = retSelected.getCPNo();
                mStrCPGUID = retSelected.getCPGUID();

                getRetailerBeatName();

                popwind.dismiss();
            }
        });
        list_drop_down.setAdapter(custom_adapter);
    }


}
