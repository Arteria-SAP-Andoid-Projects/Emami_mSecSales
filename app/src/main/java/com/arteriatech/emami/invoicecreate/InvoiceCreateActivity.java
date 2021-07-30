package com.arteriatech.emami.invoicecreate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.finance.InvoiceBean;
import com.arteriatech.emami.invoicecreate.invoicecreatesteptwo.MaterialSelectionActivity;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.mbo.InvoiceCreateBean;
import com.arteriatech.emami.mbo.ValueHelpBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.returnOrder.ReturnOrderListDetailsActivity;
import com.arteriatech.emami.ui.MaterialDesignSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by e10526 on 21-04-2018.
 *
 */

public class InvoiceCreateActivity extends AppCompatActivity implements InvoiceCreateView,
        View.OnClickListener {

    private Toolbar toolbar;
    private Context mContext;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "", mStrBundleOrderNo = "", mStrBundleSSSOGuid = "", mStrBundleMatQry = "";
    String mStrComingFrom = "";
    MaterialDesignSpinner spPaymentMode;
    EditText etPONumber, etPODate, etDelDate, etDelPerson, etVehicleNo, etDriverMobNo, etDriverName;
    TextInputLayout tiRemarks, tiPONumber, tiPODate, tiDelDate, tiDelPerson, tiVehicleNo, tiDriverName, tiDriverMobNo;
    CollectionCreatePresenterImpl presenter;
    InvoiceCreateBean invCreateBean = new InvoiceCreateBean();
    ProgressDialog progressDialog = null;
    MenuItem menu_save, menu_next;
    private boolean mBooleanCollectionWithReference = false;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    TextView tv_RetailerName, tv_RetailerID;
    KeyboardView keyboardView;
    Keyboard keyboard;
    private InputFilter[] uTRFilter;
    private InputFilter[] cardNumbFilter;
    private InputFilter[] chequeFilter;

    private static final int DATE_DIALOG_ID = 0;
    private static final int DATE_DIALOG_ID_DEL_DATE = 1;
    private int mYearDely = 0;
    private int mMonthDely = 0;
    private int mDayDely = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inv_create);
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
            mStrBundleOrderNo = bundleExtras.getString(Constants.OrderNo);
            mStrBundleSSSOGuid = bundleExtras.getString(Constants.SSSOGuid);
            mStrBundleMatQry = bundleExtras.getString(Constants.MateralQry);
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mContext = InvoiceCreateActivity.this;
        ActionBarView.initActionBarView(this, true, getString(R.string.title_invoice_Create));
        initUI();
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        invCreateBean = new InvoiceCreateBean();
        invCreateBean.setCPGUID(mStrBundleCPGUID);
        invCreateBean.setCPGUID32(mStrBundleCPGUID32);
        invCreateBean.setCPNo(mStrBundleRetID);
        invCreateBean.setCPName(mStrBundleRetName);
        invCreateBean.setCpUID(mStrBundleRetailerUID);
        invCreateBean.setComingFrom(mStrComingFrom);
        invCreateBean.setMatQry(mStrBundleMatQry);
        invCreateBean.setSSSoGuid(mStrBundleSSSOGuid);
        presenter = new CollectionCreatePresenterImpl(InvoiceCreateActivity.this, this, true, InvoiceCreateActivity.this, invCreateBean);
        if (!Constants.restartApp(InvoiceCreateActivity.this)) {
            presenter.onStart();
        }
    }

    private void initUI() {
        Constants.MAPORDQtyByCrsSkuGrp.clear();
        Constants.MAPSCHGuidByCrsSkuGrp.clear();
        Constants.selectedStockItems.clear();
        Constants.HashMapSubMaterials.clear();
        Constants.MAPSCHGuidByMaterial.clear();
        Constants.MAPQPSSCHGuidByMaterial.clear();

        tv_RetailerName = (TextView) findViewById(R.id.tv_RetailerName);
        tv_RetailerID = (TextView) findViewById(R.id.tv_RetailerID);

        spPaymentMode = (MaterialDesignSpinner) findViewById(R.id.spPaymentMode);

        etPONumber = (EditText) findViewById(R.id.etPONumber);
        etPODate = (EditText) findViewById(R.id.etPODate);
        etDelDate = (EditText) findViewById(R.id.etDelDate);
        etDelPerson = (EditText) findViewById(R.id.etDelPerson);
        etVehicleNo = (EditText) findViewById(R.id.etVehicleNo);
        etDriverName = (EditText) findViewById(R.id.etDriverName);
        etDriverMobNo = (EditText) findViewById(R.id.etDriverMobNo);

        tiPONumber = (TextInputLayout) findViewById(R.id.tiPONumber);
        tiPODate = (TextInputLayout) findViewById(R.id.tiPODate);
        tiDelDate = (TextInputLayout) findViewById(R.id.tiDelDate);
        tiDelPerson = (TextInputLayout) findViewById(R.id.tiDelPerson);
        tiVehicleNo = (TextInputLayout) findViewById(R.id.tiVehicleNo);
        tiDriverName = (TextInputLayout) findViewById(R.id.tiDriverName);
        tiDriverMobNo = (TextInputLayout) findViewById(R.id.tiDriverMobNo);


        tv_RetailerID.setText(mStrBundleRetID);
        tv_RetailerName.setText(mStrBundleRetName);
        final Calendar calDob = Calendar.getInstance();
        mYear = calDob.get(Calendar.YEAR);
        mMonth = calDob.get(Calendar.MONTH);
        mDay = calDob.get(Calendar.DAY_OF_MONTH);

        final Calendar calAnnversary = Calendar.getInstance();
        mYearDely = calAnnversary.get(Calendar.YEAR);
        mMonthDely = calAnnversary.get(Calendar.MONTH);
        mDayDely = calAnnversary.get(Calendar.DAY_OF_MONTH);

        etDelDate.setOnClickListener(this);
        tiDelDate.setOnClickListener(this);

        etPODate.setOnClickListener(this);
        tiPODate.setOnClickListener(this);

//        setCollDate();

       /* InputFilter[] remarksFilter = new InputFilter[2];
        remarksFilter[0] = new InputFilter.LengthFilter(250);
        remarksFilter[1] = Constants.getNumberAlphabetOnly();
        etRemarks.setFilters(remarksFilter);

        uTRFilter = new InputFilter[2];
        uTRFilter[0] = new InputFilter.LengthFilter(16);//22
        uTRFilter[1] = Constants.getNumberAlphabet();

        cardNumbFilter = new InputFilter[2];
        cardNumbFilter[0] = new InputFilter.LengthFilter(16);//19
        cardNumbFilter[1] = Constants.getNumberOnly();

        chequeFilter = new InputFilter[2];
        chequeFilter[0] = new InputFilter.LengthFilter(6);
        chequeFilter[1] = Constants.getNumberOnly();*/

        etPONumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiPONumber.setErrorEnabled(false);
                invCreateBean.setPONo(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etDelPerson.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiDelPerson.setErrorEnabled(false);
                invCreateBean.setDeliveryPerson(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        etVehicleNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiVehicleNo.setErrorEnabled(false);
                invCreateBean.setVehicleNo(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etDriverName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiDriverName.setErrorEnabled(false);
                invCreateBean.setDriverName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etDriverMobNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiDriverMobNo.setErrorEnabled(false);
                invCreateBean.setDriverMobile(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog = ConstantsUtils.showProgressDialog(InvoiceCreateActivity.this, message);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void displayMessage(String message) {
        ConstantsUtils.displayLongToast(InvoiceCreateActivity.this, message);
    }

    @Override
    public void showMessage(String message, final boolean isSimpleDialog) {
        UtilConstants.dialogBoxWithCallBack(InvoiceCreateActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (!isSimpleDialog) {
                    redirectActivity();
                }
            }
        });
    }


    @Override
    public void displayByCollectionData(final ArrayList<ValueHelpBean> alPaymentMode) {
        ArrayAdapter<ValueHelpBean> adapterCollType = new ArrayAdapter<ValueHelpBean>(mContext, R.layout.custom_text_view_mvp,
                R.id.tvItemValue, alPaymentMode) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ConstantsUtils.selectedView(v, spPaymentMode, position, getContext());
                return v;
            }
        };
        adapterCollType.setDropDownViewResource(R.layout.spinner_innside_mvc);
        spPaymentMode.setAdapter(adapterCollType);
        spPaymentMode.showFloatingLabel();
        spPaymentMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                invCreateBean.setPaymentModeID(alPaymentMode.get(position).getID());
                invCreateBean.setPaymentModeDesc(alPaymentMode.get(position).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

//        setCollDate();
    }

    @Override
    public void displayInvoiceData(ArrayList<InvoiceBean> alInvList) {

    }


    @Override
    public void errorPaymentMode(String message) {
        if (spPaymentMode.getVisibility() == View.VISIBLE)
            spPaymentMode.setError(message);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_collection_create, menu);
        menu_save = menu.findItem(R.id.menu_collection_save);
        menu_next = menu.findItem(R.id.menu_collection_next);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mBooleanCollectionWithReference) {
            menu_save.setVisible(true);
            menu_next.setVisible(false);
        } else {
            menu_save.setVisible(false);
            menu_next.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_collection_save:
                onSave();
                break;
            case R.id.menu_collection_next:
                onNext();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceCreateActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_create_invoice).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        redirectActivity();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();

    }

    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    private void onNavigateToRetDetilsActivity() {
        Intent intentNavPrevScreen = new Intent(InvoiceCreateActivity.this, RetailersDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
//        if (!Constants.OtherRouteNameVal.equalsIgnoreCase("")) {
//            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
//            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
//        }
        startActivity(intentNavPrevScreen);
    }

    private void redirectActivity() {
        Intent intentNavPrevScreen = null;

        if (mStrComingFrom.equalsIgnoreCase("SOList")) {
            intentNavPrevScreen = new Intent(this, ReturnOrderListDetailsActivity.class);
            intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
            intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
            intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
            intentNavPrevScreen.putExtra(Constants.CPUID, Constants.SOBundleValue.getRetUID());
            intentNavPrevScreen.putExtra(Constants.comingFrom, Constants.SOBundleValue.getRetcomingFrom());
            intentNavPrevScreen.putExtra(Constants.DeviceNo, Constants.SOBundleValue.getRetSODeviceNo());
            intentNavPrevScreen.putExtra(Constants.EXTRA_TAB_POS, Constants.SOBundleValue.getRetTabPos());
            intentNavPrevScreen.putExtra(Constants.EXTRA_SSRO_GUID, Constants.SOBundleValue.getRetSSROGUID());
            intentNavPrevScreen.putExtra(Constants.EXTRA_ORDER_DATE, Constants.SOBundleValue.getRetSODate());
            intentNavPrevScreen.putExtra(Constants.EXTRA_ORDER_IDS, Constants.SOBundleValue.getRetSSSONo());
            intentNavPrevScreen.putExtra(Constants.EXTRA_ORDER_AMOUNT, Constants.SOBundleValue.getRetSOAmount());
            intentNavPrevScreen.putExtra(Constants.EXTRA_ORDER_SATUS, Constants.SOBundleValue.getRetSOStatus());
            intentNavPrevScreen.putExtra(Constants.EXTRA_ORDER_CURRENCY, Constants.SOBundleValue.getRetSOCurrency());

        } else {
            intentNavPrevScreen = new Intent(this, RetailersDetailsActivity.class);
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
        }
        startActivity(intentNavPrevScreen);

    }

    private void onNext() {
        //next step
        if (ConstantsUtils.isAutomaticTimeZone(InvoiceCreateActivity.this)) {
            if (presenter.validateFields(invCreateBean, "")) {
                Intent intent = new Intent(mContext, MaterialSelectionActivity.class);
                intent.putExtra(Constants.comingFrom, mStrComingFrom);
                intent.putExtra(Constants.EXTRA_SO_DETAIL, invCreateBean);
                startActivity(intent);
            }
        } else {
            ConstantsUtils.showAutoDateSetDialog(InvoiceCreateActivity.this);
        }
    }

    private void onSave() {
        Intent intent = new Intent(mContext, MaterialSelectionActivity.class);
        intent.putExtra(Constants.comingFrom, mStrComingFrom);
        intent.putExtra(Constants.EXTRA_SO_DETAIL, invCreateBean);
        startActivity(intent);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.etDelDate:
                openCallender();
                break;
            case R.id.tiDelDate:
                openCallender();
                break;
            case R.id.etPODate:
                openCallenderPO();
                break;
            case R.id.tiPODate:
                openCallenderPO();
                break;
        }

    }

    private void openCallenderPO() {
       /* Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        //Get last three months date
        Calendar calendarPast = Calendar.getInstance();
        calendarPast.add(Calendar.MONTH, -3);
        //Get Next three months date
        Calendar calendarFuture = Calendar.getInstance();
        calendarFuture.add(Calendar.MONTH, 3);
        // Cheque Date allow past and future three months only
        dialog.getDatePicker().setMinDate(calendarPast.getTimeInMillis());
        dialog.getDatePicker().setMaxDate(calendarFuture.getTimeInMillis());

        dialog.show();*/
        onDatePickerDialog(DATE_DIALOG_ID).show();
    }

    private void openCallender() {
        onDatePickerDialog(DATE_DIALOG_ID_DEL_DATE).show();
    }



    private void setDateToView(int mYear, int mMonth, int mDay) {
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
        invCreateBean.setDelDate(mYear + "-" + mon + "-" + day);
        tiDelDate.setErrorEnabled(false);
        String convertDateFormat = ConstantsUtils.convertDateIntoDisplayFormat(mContext, String.valueOf(new StringBuilder().append(mDay).append("/")
                .append(UtilConstants.MONTHS_NUMBER[mMonth]).append("/").append("").append(mYear)));
        etDelDate.setText(convertDateFormat);
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
            case DATE_DIALOG_ID_DEL_DATE:
                DatePickerDialog datePickerAnnversary = new DatePickerDialog(this, mDateSetDeliveryListe,
                        mYearDely, mMonthDely, mDayDely);
                Calendar cal = Calendar.getInstance();
                Date newDateAnnv = cal.getTime();
                datePickerAnnversary.getDatePicker().setMinDate(newDateAnnv.getTime());
                return datePickerAnnversary;
        }
        return null;
    }

    private final DatePickerDialog.OnDateSetListener mDateSetDeliveryListe = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker v, int year, int monthOfYear,
                              int dayOfMonth) {
            mYearDely = year;
            mMonthDely = monthOfYear;
            mDayDely = dayOfMonth;
            String mon = "";
            String day = "";
            int mnt = 0;
            mnt = mMonthDely + 1;
            if (mnt < 10)
                mon = "0" + mnt;
            else
                mon = "" + mnt;
            day = "" + mDayDely;
            if (mDayDely < 10)
                day = "0" + mDayDely;
            invCreateBean.setDelDate(mYearDely + "-" + mon + "-" + day);
            tiDelDate.setErrorEnabled(false);
            String convertDateFormat = ConstantsUtils.convertDateIntoDisplayFormat(mContext, String.valueOf(new StringBuilder().append(mDayDely).append("/")
                    .append(mon).append("/").append("").append(mYearDely)));
            etDelDate.setText(convertDateFormat);
        }
    };

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

            invCreateBean.setPODate(mYear + "-" + mon + "-" + day);
            tiPODate.setErrorEnabled(false);
            String convertDateFormat = ConstantsUtils.convertDateIntoDisplayFormat(mContext, String.valueOf(new StringBuilder().append(mDay).append("/")
                    .append(mon).append("/").append("").append(mYear)));
            etPODate.setText(convertDateFormat);
        }
    };
}
