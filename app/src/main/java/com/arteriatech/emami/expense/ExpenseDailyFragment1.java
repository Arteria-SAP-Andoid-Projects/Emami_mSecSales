package com.arteriatech.emami.expense;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.interfaces.DialogCallBack;
import com.arteriatech.emami.main.MainMenu;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.xscript.core.GUID;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseDailyFragment1 extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener, KeyboardView.OnKeyboardActionListener {


    private static final String TAG = "ExpenseDailyFragment1";
    public static KeyboardView keyboardView = null;
    private Spinner spExpenseType;
    private TextView tvExpenseDate;
    private Spinner spBeatName;
    private Spinner spBeatWorkAt;
    private TextView tvDailyAllowance;
    private Spinner spModeConve;
    private EditText etDistance;
    private EditText et_other_conv;
    //    private EditText etTotalFair;
    private String[][] arrayExpLocationVal = null;
    private String[][] arrayRouteVal = null;
    private String[][] arrayExpModeVal = null;
    private String expenseFreq = "";
    private String[][] mArrayExpenseType = null;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private int fiscalYear;
    private String mStrCurrentDate = "";
    private DatePickerDialog datePickerDialog;
    private String secondTimeErrorMessage = "";
    private String mStrSeleExpenseTypeId = "";
    private String mStrSeleExpenseTypeDesc = "";
    private boolean dialogBoxCreated = false;
    private ArrayList<ExpenseBean> expenseBeanList = new ArrayList<>();
    private String[][] mArrayDefaultExpenseAllowance = null;
    private TextView tvFarTotal;
    private String stBeatId = "";
    private String stBeatDesc = "";
    private String stBeatGUID = "";
    private String stLocation = "";
    private String stLocationDesc = "";
    private String stMode = "";
    private String stModeDesc = "";
    private TextView tvConvUOM;
    private TableRow trBeatName;
    private TextView tvBeatWorkAt;
    private TextView tvBeatDistance;
    private TextView tvAllTotalValue;
    private TextView tv_others;
    private String stDistanceValue = "";
    private Hashtable masterHashTable = new Hashtable();
    private String[][] mArraySalesPerson = null;
    private String stCurrenctHeader;
    private ArrayList<HashMap<String, String>> arrItemTable = new ArrayList<HashMap<String, String>>();
    private Keyboard keyboard = null;
    private String stDailyAllowance = "";
    private String stFarTotal = "";
    private TableRow trNonBeatType;
    private TableRow tr_other_conv_layout;
    private Spinner spNonBeatType = null;
    private String[][] arrayExpNonBeatTypeVal = null;
    private String stNonBeatTypeDesc = "";
    private String stNonBeatType = "";
    private EditText etDailyAllowance;
    private String stDailyTypeAllowance = "";
    private String stAmountCatType = "";
    private String stMaxAllowancePer = "";
    private TextView tvDailyAllowanceMandatory;


    public ExpenseDailyFragment1() {
        // Required empty public constructor
    }

    /*check daily/monthly already created*/
    public static boolean validateAlreadyDataSaved(Context mContext, String expenseFreq, String date, String expenseType) {
        boolean isDataPresent = false;
        String query = "";
        if (!expenseFreq.equalsIgnoreCase(Constants.ExpenseMonthly)) {
            try {
                query = Constants.Expenses + "?$filter=ExpenseDate eq datetime'" + UtilConstants.getTimeformat2(date, "") + "' and ExpenseType eq '" + expenseType + "'";
                isDataPresent = OfflineManager.getVisitStatusForCustomer(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!isDataPresent) {
                isDataPresent = OfflineManager.checkDatavaltDataisPresent(mContext, date, expenseType);
            }
        } else {
            int pastMonth = 0;
            try {
                String[] splitDate = date.split("-");
                pastMonth = Integer.parseInt(splitDate[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            query = Constants.Expenses + "?$filter= month(ExpenseDate) eq " + pastMonth + " and ExpenseType eq '" + expenseType + "'";
            try {
                isDataPresent = OfflineManager.getVisitStatusForCustomer(query);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            if (!isDataPresent) {
                try {
                    isDataPresent = OfflineManager.checkDatavaltMonthCompare(mContext, date, expenseType);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return isDataPresent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            expenseFreq = bundle.getString(Constants.ExpenseFreq);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_expense1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        spExpenseType = (Spinner) view.findViewById(R.id.sp_expense_type);
        tvExpenseDate = (TextView) view.findViewById(R.id.tv_expense_date);
        spBeatName = (Spinner) view.findViewById(R.id.sp_beat_name);
        spNonBeatType = (Spinner) view.findViewById(R.id.sp_non_beat_type);
        spBeatWorkAt = (Spinner) view.findViewById(R.id.sp_beat_work);
        tvDailyAllowance = (TextView) view.findViewById(R.id.tv_daily_allowance);
        spModeConve = (Spinner) view.findViewById(R.id.sp_modeof_con);
        etDistance = (EditText) view.findViewById(R.id.et_mode_distance);
        et_other_conv = (EditText) view.findViewById(R.id.et_other_conv);
        tvFarTotal = (TextView) view.findViewById(R.id.tv_fare_total);
        tvConvUOM = (TextView) view.findViewById(R.id.conv_uom);
        tvBeatWorkAt = (TextView) view.findViewById(R.id.tv_beat_work_id);
        tvBeatDistance = (TextView) view.findViewById(R.id.tv_beat_distance);
        tvAllTotalValue = (TextView) view.findViewById(R.id.tv_total_daily_expenses);
        tv_others = (TextView) view.findViewById(R.id.tv_others);
        tvDailyAllowanceMandatory = (TextView) view.findViewById(R.id.tv_daily_allowance_mandatory);
//        etTotalFair = (EditText) view.findViewById(R.id.et_fare_total);
        etDailyAllowance = (EditText) view.findViewById(R.id.et_daily_allowance);
        trBeatName = (TableRow) view.findViewById(R.id.tr_beat_name);
        trNonBeatType = (TableRow) view.findViewById(R.id.tr_non_beat_type);
        tr_other_conv_layout = (TableRow) view.findViewById(R.id.tr_other_conv_layout);
        et_other_conv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        trNonBeatType.setVisibility(View.GONE);

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        fiscalYear = mYear;
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
//        datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_TRADITIONAL, null, mYear, mMonth, mDay);
        datePickerDialog = new DatePickerDialog(getContext(), this, mYear, mMonth, mDay);
        setDateIntoTextView(mMonth, mDay, mYear);
        tvExpenseDate.setOnClickListener(this);


        getDataFromOfflineDB();
        setTextToUI();
        initializeKeyboardDependencies(view);
    }

    private void setTextToUI() {
        if (mArrayExpenseType == null) {
            mArrayExpenseType = new String[2][1];
            mArrayExpenseType[0][0] = "";
            mArrayExpenseType[1][0] = Constants.None;
        }
        ArrayAdapter<String> expenseTypeAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.custom_textview, mArrayExpenseType[1]);
        expenseTypeAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spExpenseType.setAdapter(expenseTypeAdapter);
        spExpenseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mStrSeleExpenseTypeId = mArrayExpenseType[0][position];
                mStrSeleExpenseTypeDesc = mArrayExpenseType[1][position];
                spExpenseType.setBackgroundResource(R.drawable.spinner_bg);
                if (!checkValidationAndShowDialogs()) {
                    setUIBasedOnType(mStrSeleExpenseTypeId);
                    getConfigurations(mStrSeleExpenseTypeId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (arrayRouteVal == null) {
            arrayRouteVal = new String[3][1];
            arrayRouteVal[0][0] = "";
            arrayRouteVal[1][0] = Constants.None;
            arrayRouteVal[2][0] = "";
        }
        ArrayAdapter<String> beatAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.custom_textview, arrayRouteVal[1]);
        beatAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spBeatName.setAdapter(beatAdapter);
        spBeatName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stBeatId = arrayRouteVal[0][position];
                stBeatDesc = arrayRouteVal[1][position];
                stBeatGUID = arrayRouteVal[2][position];
                if (!stBeatId.equalsIgnoreCase("")) {
                    spBeatName.setBackgroundResource(R.drawable.spinner_bg);
                }
                displayAndSetValue(expenseBeanList, 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (arrayExpLocationVal == null) {
            arrayExpLocationVal = new String[2][1];
            arrayExpLocationVal[0][0] = "";
            arrayExpLocationVal[1][0] = Constants.None;
        }
        ArrayAdapter<String> beatAtWorkAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.custom_textview, arrayExpLocationVal[1]);
        beatAtWorkAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spBeatWorkAt.setAdapter(beatAtWorkAdapter);
        spBeatWorkAt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stLocation = arrayExpLocationVal[0][position];
                stLocationDesc = arrayExpLocationVal[1][position];
                if (!stLocation.equalsIgnoreCase("")) {
                    spBeatWorkAt.setBackgroundResource(R.drawable.spinner_bg);
                }
                displayAndSetValue(expenseBeanList, 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (arrayExpNonBeatTypeVal == null) {
            arrayExpNonBeatTypeVal = new String[2][3];
            arrayExpNonBeatTypeVal[0][0] = "";
            arrayExpNonBeatTypeVal[1][0] = Constants.None;
            arrayExpNonBeatTypeVal[0][1] = "03";
            arrayExpNonBeatTypeVal[1][1] = ConstantsUtils.Meeting;
            arrayExpNonBeatTypeVal[0][2] = "02";
            arrayExpNonBeatTypeVal[1][2] = ConstantsUtils.Training;
        }
        ArrayAdapter<String> nonBeatTypeAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.custom_textview, arrayExpNonBeatTypeVal[1]);
        nonBeatTypeAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spNonBeatType.setAdapter(nonBeatTypeAdapter);
        spNonBeatType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stNonBeatType = arrayExpNonBeatTypeVal[0][position];
                stNonBeatTypeDesc = arrayExpNonBeatTypeVal[1][position];
                if (!stLocation.equalsIgnoreCase("")) {
                    spNonBeatType.setBackgroundResource(R.drawable.spinner_bg);
                }
                displayAndSetValue(expenseBeanList, 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (arrayExpModeVal == null) {
            arrayExpModeVal = new String[2][1];
            arrayExpModeVal[0][0] = "";
            arrayExpModeVal[1][0] = Constants.None;
        } else {
            arrayExpModeVal = Constants.CheckForOtherInConfigValue(arrayExpModeVal);
        }
        ArrayAdapter<String> modeWorkAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.custom_textview, arrayExpModeVal[1]);
        modeWorkAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spModeConve.setAdapter(modeWorkAdapter);
        spModeConve.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stMode = arrayExpModeVal[0][position];
                stModeDesc = arrayExpModeVal[1][position];
                if(stMode.equalsIgnoreCase(Constants.Conv_Mode_Type_Other)){
                    tv_others.setText(stModeDesc);

                    tr_other_conv_layout.setVisibility(View.VISIBLE);
                }else{
                    et_other_conv.setText("");
                    tr_other_conv_layout.setVisibility(View.GONE);
                }


                if (!stMode.equalsIgnoreCase("")) {
                    spModeConve.setBackgroundResource(R.drawable.spinner_bg);
                }
                displayAndSetValue(expenseBeanList, 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        UtilConstants.editTextDecimalFormat(etDistance, 10, 3);
        etDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etDistance.setBackgroundResource(R.drawable.edittext);
                stDistanceValue = s + "";
                displayAndSetValue(expenseBeanList, 1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etDistance.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, getActivity());
                Constants.setCursorPostion(etDistance,v,event);
                return true;
            }
        });
        etDistance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Constants.showCustomKeyboard(v, keyboardView, getActivity());
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        UtilConstants.editTextDecimalFormat(etDailyAllowance, 10, 3);
        etDailyAllowance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etDailyAllowance.setBackgroundResource(R.drawable.edittext);
                stDailyTypeAllowance = s + "";
                displayAndSetValue(expenseBeanList, 1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etDailyAllowance.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, getActivity());
                return true;
            }
        });
        etDailyAllowance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Constants.showCustomKeyboard(v, keyboardView, getActivity());
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });

        et_other_conv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                et_other_conv.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void setUIBasedOnType(String mStrSeleExpenseTypeId) {
        try {
            if (mStrSeleExpenseTypeId.equalsIgnoreCase("000010")) {
                spBeatName.setSelection(0);
                spBeatWorkAt.setSelection(0);
                spModeConve.setSelection(0);
                spNonBeatType.setSelection(0);
                etDistance.setText("");
                trBeatName.setVisibility(View.VISIBLE);
                trNonBeatType.setVisibility(View.GONE);
                tvBeatWorkAt.setText(getText(R.string.lbl_beat_work_at));
                tvBeatDistance.setText(getText(R.string.lbl_beat_distance));
            } else if (mStrSeleExpenseTypeId.equalsIgnoreCase("000020")) {
                etDistance.setText("");
                spModeConve.setSelection(0);
                spBeatWorkAt.setSelection(0);
                spNonBeatType.setSelection(0);
                trBeatName.setVisibility(View.GONE);
                tvBeatWorkAt.setText(getText(R.string.lbl_non_beat_work_at));
                tvBeatDistance.setText(getText(R.string.lbl_non_beat_distance));
                trNonBeatType.setVisibility(View.VISIBLE);
            } else {
                etDistance.setText("");
                spModeConve.setSelection(0);
                spBeatWorkAt.setSelection(0);
                spNonBeatType.setSelection(0);
                trBeatName.setVisibility(View.GONE);
                tvBeatWorkAt.setText(getText(R.string.lbl_non_beat_work_at));
                tvBeatDistance.setText(getText(R.string.lbl_non_beat_distance));
                trNonBeatType.setVisibility(View.GONE);
            }
            tvFarTotal.setText("");
            tvDailyAllowance.setText("");
            tvConvUOM.setText("");
            tvAllTotalValue.setText("");
            etDailyAllowance.setText("");
            stDailyAllowance = "";
            stFarTotal = "";
            stBeatGUID = "";
            stLocation = "";
            stLocationDesc = "";
            stMode = "";
            stModeDesc = "";
            stNonBeatType = "";
            stNonBeatTypeDesc = "";
            stDistanceValue = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getConfigurations(String mStrSeleExpenseTypeId) {
        String mStrConfigQry = Constants.ExpenseConfigs + "?$filter=" + Constants.ExpenseType + " eq '" + mStrSeleExpenseTypeId + "' and " + Constants.DefaultItemCat +
                " eq '000010' and (ExpenseItemType eq '0000000002' or ExpenseItemType eq '0000000003') &$orderby = ExpenseItemType asc &$top=2";
        try {
            expenseBeanList.clear();
            String[][] mArrayDefaultExpenseType = OfflineManager.getConfigExpense(mStrConfigQry, "");
            if (mArrayDefaultExpenseType != null) {
                int totalExpanse = mArrayDefaultExpenseType[3].length - 1;
                if (totalExpanse > 0) {
                    for (int defaultExp = 1; defaultExp <= totalExpanse; defaultExp++) {
                        ExpenseBean expenseBean = new ExpenseBean();
                        expenseBean.setExpenseItemTypeDesc(mArrayDefaultExpenseType[3][defaultExp]);
                        expenseBean.setExpenseItemType(mArrayDefaultExpenseType[2][defaultExp]);
                        expenseBean.setDefault(true);
                        expenseBean.setAllowance(mArrayDefaultExpenseType[16][defaultExp]);
                        expenseBean.setItemFieldSet(mArrayDefaultExpenseType[14][defaultExp]);
                        expenseBean.setItemFieldSetDesc(mArrayDefaultExpenseType[15][defaultExp]);
                        expenseBean.setAmountCategory(mArrayDefaultExpenseType[10][defaultExp]);
                        expenseBean.setAmountCategoryDesc(mArrayDefaultExpenseType[11][defaultExp]);
                        expenseBean.setMaxAllowancePer(mArrayDefaultExpenseType[12][defaultExp]);
                        expenseBean.setExpenseType(mStrSeleExpenseTypeId);
                        expenseBean.setExpenseTypeDesc(mStrSeleExpenseTypeDesc);
                        expenseBean.setIsSupportDocReq(mArrayDefaultExpenseType[17][defaultExp]);
                        expenseBean.setIsRemarksReq(mArrayDefaultExpenseType[19][defaultExp]);
                        expenseBean.setCurrency(mArrayDefaultExpenseType[18][defaultExp]);
                        expenseBean.setUOM(mArrayDefaultExpenseType[13][defaultExp]);
                        expenseBeanList.add(expenseBean);
                    }
                }
            }
            displayAndSetValue(expenseBeanList, 1);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void displayAndSetValue(ArrayList<ExpenseBean> expenseBeanList, int type) {
        if (!expenseBeanList.isEmpty()) {
            if (expenseBeanList.size() > 1) {
                ExpenseBean expenseCBean = expenseBeanList.get(0);
                ExpenseBean expenseDBean = expenseBeanList.get(1);
//                if(type==1)
                displayConveyanceUI(expenseCBean);
//                else
                displayDailyAllow(expenseDBean);
                setTotalValues();

            }
        }
    }

    private void setTotalValues() {
        try {
            double intDailyAll = Double.parseDouble(stDailyAllowance);
            double intFareTotal = Double.parseDouble(stFarTotal);
            double totalFinalValue = intDailyAll + intFareTotal;
            tvAllTotalValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(totalFinalValue + "") + " " + mArraySalesPerson[10][0]);
        } catch (Exception e) {
            e.printStackTrace();
            tvAllTotalValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal("") + " " + mArraySalesPerson[10][0]);
        }
    }

    private void displayDailyAllow(ExpenseBean expenseBean) {
        stAmountCatType = expenseBean.getAmountCategory();
        stMaxAllowancePer = expenseBean.getMaxAllowancePer();
        String query = Constants.ExpenseAllowances + "?$filter=" + Constants.ExpenseType + " eq '" + mStrSeleExpenseTypeId + "' and ExpenseItemType eq '0000000003' " +
                "and Location eq '" + stLocation + "' &$top=1";
        if (expenseBean.getAmountCategory().equalsIgnoreCase("000030")) {
            try {

                String mStrConfigQry = Constants.ExpenseConfigs + "?$filter=" + Constants.ExpenseType + " eq '" + mStrSeleExpenseTypeId + "' and " + Constants.DefaultItemCat +
                        " eq '000010' and ExpenseItemType eq '0000000003' &$top=1";
                String[][] mArrayDefaultExpenseType = OfflineManager.getConfigExpense(mStrConfigQry, "");
                if (mArrayDefaultExpenseType != null) {
                    int totalExpanse = mArrayDefaultExpenseType[3].length - 1;
                    if (totalExpanse > 0) {
                        BigDecimal maxPerBigDecimal = null;
                        try {
                            DecimalFormat decimalFormat = new DecimalFormat("0.00");
                            decimalFormat.setParseBigDecimal(true);
                            maxPerBigDecimal = (BigDecimal) decimalFormat.parse(mArrayDefaultExpenseType[12][1]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        mArrayDefaultExpenseAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
                        if (mArrayDefaultExpenseAllowance[0].length > 0) {
                            BigDecimal amountBigDec = null;
                            try {
                                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                                decimalFormat.setParseBigDecimal(true);
                                amountBigDec = (BigDecimal) decimalFormat.parse(mArrayDefaultExpenseAllowance[0][0]);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (amountBigDec != null && maxPerBigDecimal != null) {
                                BigDecimal finalAmount = amountBigDec.divide(new BigDecimal(100.0f)).multiply(maxPerBigDecimal);
                                stDailyAllowance = finalAmount + "";
                                tvDailyAllowance.setText(UtilConstants.removeLeadingZerowithTwoDecimal(finalAmount + "") + " " + mArraySalesPerson[10][0]);
                                expenseBean.setAmount(finalAmount + "");
                            } else {
                                stDailyAllowance = "";
                                tvDailyAllowance.setText(UtilConstants.removeLeadingZerowithTwoDecimal("") + " " + mArraySalesPerson[10][0]);
                                expenseBean.setAmount("0.0");
                            }
                        } else {
                            stDailyAllowance = "";
                            tvDailyAllowance.setText(UtilConstants.removeLeadingZerowithTwoDecimal("") + " " + mArraySalesPerson[10][0]);
                            expenseBean.setAmount("0.0");
                        }
                    } else {
                        stDailyAllowance = "";
                        tvDailyAllowance.setText(UtilConstants.removeLeadingZerowithTwoDecimal("") + " " + mArraySalesPerson[10][0]);
                        expenseBean.setAmount("0.0");
                    }
                } else {
                    stDailyAllowance = "";
                    tvDailyAllowance.setText(UtilConstants.removeLeadingZerowithTwoDecimal("") + " " + mArraySalesPerson[10][0]);
                    expenseBean.setAmount("0.0");
                }


            } catch (Exception e) {
                e.printStackTrace();
                stDailyAllowance = "";
                tvDailyAllowance.setText(UtilConstants.removeLeadingZerowithTwoDecimal(""));
                expenseBean.setAmount("0.0");
            }
            etDailyAllowance.setVisibility(View.GONE);
            tvDailyAllowanceMandatory.setVisibility(View.GONE);
            tvDailyAllowance.setVisibility(View.VISIBLE);
        } else if (expenseBean.getAmountCategory().equalsIgnoreCase("000020")) {
            etDailyAllowance.setVisibility(View.VISIBLE);
            tvDailyAllowanceMandatory.setVisibility(View.VISIBLE);
            tvDailyAllowance.setVisibility(View.GONE);
            try {
                mArrayDefaultExpenseAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
                if (mArrayDefaultExpenseAllowance[0].length > 0) {
                    double enteredValue = Double.parseDouble(stDailyTypeAllowance);
                    double maxValuePer = Double.parseDouble(stMaxAllowancePer);
                    double finalAmount = (enteredValue / 100.0f) * maxValuePer;
                    if (finalAmount > Double.parseDouble(mArrayDefaultExpenseAllowance[0][0])) {
                        stDailyAllowance = "";
                    } else {
                        stDailyAllowance = finalAmount + "";
                    }
                } else {
                    stDailyAllowance = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
                stDailyAllowance = "";
            }
            expenseBean.setAmount(stDailyTypeAllowance);
        } else if (expenseBean.getAmountCategory().equalsIgnoreCase("000010")) {
            etDailyAllowance.setVisibility(View.VISIBLE);
            tvDailyAllowanceMandatory.setVisibility(View.VISIBLE);
            tvDailyAllowance.setVisibility(View.GONE);
            stDailyAllowance = stDailyTypeAllowance;
            expenseBean.setAmount(stDailyTypeAllowance);
        }
    }

    private void displayConveyanceUI(ExpenseBean expenseConveyance) {


        tvConvUOM.setText(expenseConveyance.getUOM());
        String itemType = expenseConveyance.getExpenseItemType();
        String location = stLocation;
        String convenyanceMode = stMode;
        String stDistance = stDistanceValue;
        expenseConveyance.setBeatDistance(stDistanceValue);
        expenseConveyance.setConvenyanceMode(stMode);
        expenseConveyance.setConvenyanceModeDs(stModeDesc);
        expenseConveyance.setLocation(stLocation);
        expenseConveyance.setLocationDesc(stLocationDesc);
        String query = Constants.ExpenseAllowances + "?$filter=" + Constants.ExpenseType + " eq '" + mStrSeleExpenseTypeId + "' and ExpenseItemType eq '" +
                itemType + "' and Location eq '" + location + "' and ConveyanceMode eq '" + convenyanceMode + "'";
        try {
            mArrayDefaultExpenseAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
            if (mArrayDefaultExpenseAllowance[0].length > 0) {
                try {
                    double doubQty = Double.parseDouble(stDistance);
                    double maxValue = Double.parseDouble(mArrayDefaultExpenseAllowance[0][0]);
                    double finalAmount = doubQty * maxValue;
                    stFarTotal = finalAmount + "";
                    tvFarTotal.setText(UtilConstants.removeLeadingZerowithTwoDecimal(finalAmount + "") + " " + mArraySalesPerson[10][0]);
                    expenseConveyance.setAmount(finalAmount + "");
                } catch (Exception e) {
                    e.printStackTrace();
                    stFarTotal = "0.0";
                    tvFarTotal.setText(UtilConstants.removeLeadingZerowithTwoDecimal("") + " " + mArraySalesPerson[10][0]);
                    expenseConveyance.setAmount("0.0");
                }
            } else {
                stFarTotal = "0.0";
                tvFarTotal.setText(UtilConstants.removeLeadingZerowithTwoDecimal("") + " " + mArraySalesPerson[10][0]);
                expenseConveyance.setAmount("0.0");
            }

        } catch (Exception e) {
            e.printStackTrace();
            stFarTotal = "0.0";
            tvFarTotal.setText("0.00");
            expenseConveyance.setAmount("0.0");
        }

    }

    private void getDataFromOfflineDB() {
        try {
            arrayRouteVal = OfflineManager.getBeatsArray(Constants.RouteSchedules + "?$filter=" + Constants.StatusID + " eq '01'");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }
        String query = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.Location + "'";
        try {
            arrayExpLocationVal = OfflineManager.getConfigListWithDefaultValAndNone(query, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }
        query = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.ConvenyanceMode + "'";
        try {
            arrayExpModeVal = OfflineManager.getConfigListWithDefaultValAndNone(query, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }
        query = Constants.ExpenseConfigs + "?$filter=" + Constants.ExpenseFreq + " eq '" + expenseFreq + "'";
        try {
            mArrayExpenseType = OfflineManager.getConfigExpenseType(query, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }
        try {
            mArraySalesPerson = Constants.getDistributors();
        } catch (Exception e) {
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        setDateIntoTextView(mMonth, mDay, mYear);

    }

    /*set date into textview*/
    private void setDateIntoTextView(int mMonth, int mDay, int mYear) {
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
        mStrCurrentDate = mYear + "-" + mon + "-" + day;
        if (expenseFreq.equalsIgnoreCase(Constants.ExpenseMonthly)) {
            tvExpenseDate.setText(new StringBuilder().append(mMonth + 1).append("/").append(mYear).append(" "));
        } else {
            tvExpenseDate.setText(new StringBuilder()
                    .append(mDay).append("/").append(mMonth + 1).append("/").append(mYear).append(" "));
        }

        if (!dialogBoxCreated) {
            if (!checkValidationAndShowDialogs()) {
                if (!TextUtils.isEmpty(mStrSeleExpenseTypeId)) {
                    setUIBasedOnType(mStrSeleExpenseTypeId);
                    getConfigurations(mStrSeleExpenseTypeId);
                }
            }
        }

    }

    private boolean checkValidationAndShowDialogs() {
        secondTimeErrorMessage = "Expense already submited for " + mStrSeleExpenseTypeDesc;
        boolean finalStatus = false;
        if (validateAlreadyDataSaved(getContext(), expenseFreq, mStrCurrentDate, mStrSeleExpenseTypeId)) {

            Constants.dialogBoxWithButton(getContext(), "", secondTimeErrorMessage, "Ok", "", new DialogCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus) {
                    dialogBoxCreated = false;
                }
            });
            dialogBoxCreated = true;
            finalStatus = true;
        }
        hiddenUI(finalStatus);
        return finalStatus;
    }

    private void hiddenUI(boolean isDisable) {

        if (isDisable) {

            spBeatName.setEnabled(false);
            spBeatName.setClickable(false);
            spNonBeatType.setEnabled(false);
            spNonBeatType.setClickable(false);
            spBeatWorkAt.setEnabled(false);
            spBeatWorkAt.setClickable(false);
            spModeConve.setEnabled(false);
            spModeConve.setClickable(false);
            etDistance.setEnabled(false);
            etDistance.setFocusable(false);
            etDailyAllowance.setEnabled(false);
            etDailyAllowance.setFocusable(false);
        } else {
            spBeatName.setEnabled(true);
            spBeatName.setClickable(true);
            spNonBeatType.setEnabled(true);
            spNonBeatType.setClickable(true);
            spBeatWorkAt.setEnabled(true);
            spBeatWorkAt.setClickable(true);
            spModeConve.setEnabled(true);
            spModeConve.setClickable(true);
            etDistance.setEnabled(true);
            etDistance.setFocusable(true);
            etDistance.setFocusableInTouchMode(true);
            etDailyAllowance.setEnabled(true);
            etDailyAllowance.setFocusable(true);
            etDailyAllowance.setFocusableInTouchMode(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_expense_date:
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -getDayMonthConfig());
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                datePickerDialog.show();
                break;
        }
    }

    /*get day/month config*/
    private int getDayMonthConfig() {
        String qryStr = "";
        int maxDays = 1;
        if (expenseFreq.equalsIgnoreCase(Constants.ExpenseMonthly)) {
            qryStr = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SF + "' and " + Constants.Types + " eq '" + Constants.MAXEXPALWM + "' ";
        } else {
            qryStr = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SF + "' and " + Constants.Types + " eq '" + Constants.MAXEXPALWD + "' ";
        }
        try {
            String mStrDays = OfflineManager.getConfigValue(qryStr);
            maxDays = Integer.parseInt(mStrDays);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxDays;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_window_display, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
            case R.id.menu_window_save:
                onSaveData(getContext());
                break;
        }
        return true;
    }

    private void onSaveData(Context context) {
        String errorMsg = "";
        if (!checkValidationAndShowDialogs()) {
            int validationCode = checkValidations();
            if (validationCode == 0) {
                saveDataToDataValt(context);
            } else if (validationCode == 1) {
                errorMsg = getString(R.string.validation_plz_fill_mandatory_flds);
            } else if (validationCode == 2) {
                errorMsg = getString(R.string.validation_plz_fill_mandatory_flds);
            } else if (validationCode == 3) {
                errorMsg = getString(R.string.expense_error_enter_valid_amount);
            } else if (validationCode == 4) {
                errorMsg = getString(R.string.expense_error_amount);
            }
            if (validationCode > 0) {
                Constants.dialogBoxWithButton(getContext(), "", errorMsg, getString(R.string.ok), "", null);
            }
        }

    }

    private void saveDataToDataValt(Context context) {
        GUID guid = GUID.newRandom();
        String doc_no = (System.currentTimeMillis() + "").substring(3, 10);
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");
        masterHashTable.clear();
        masterHashTable.put(Constants.ExpenseGUID, guid.toString36().toUpperCase());
        masterHashTable.put(Constants.ExpenseNo, doc_no);
        masterHashTable.put(Constants.FiscalYear, fiscalYear + "");
        masterHashTable.put(Constants.LoginID, loginIdVal);
        masterHashTable.put(Constants.CPGUID, "");
        masterHashTable.put(Constants.CPNo, "");
        masterHashTable.put(Constants.CPName, "");
        masterHashTable.put(Constants.CPType, "");
        masterHashTable.put(Constants.CPTypeDesc, "");
        masterHashTable.put(Constants.SPGUID, mArraySalesPerson[4][0]);
        masterHashTable.put(Constants.SPNo, mArraySalesPerson[6][0]);
        masterHashTable.put(Constants.SPName, mArraySalesPerson[7][0]);
        masterHashTable.put(Constants.ExpenseType, mStrSeleExpenseTypeId);
        masterHashTable.put(Constants.ExpenseTypeDesc, mStrSeleExpenseTypeDesc);
        masterHashTable.put(Constants.ExpenseDate, mStrCurrentDate);
        masterHashTable.put(Constants.Status, "");
        masterHashTable.put(Constants.StatusDesc, "");
        masterHashTable.put(Constants.Amount, "0.0");
        masterHashTable.put(Constants.Currency, mArraySalesPerson[10][0]);
        int itemIncVal = 0;
        arrItemTable.clear();
        for (ExpenseBean expenseBean : expenseBeanList) {
            HashMap<String, String> singleItem = new HashMap<String, String>();
            GUID itemGuid = GUID.newRandom();
            singleItem.put(Constants.ExpenseItemGUID, itemGuid.toString36().toUpperCase());
            singleItem.put(Constants.ExpenseGUID, guid.toString36().toUpperCase());
            singleItem.put(Constants.ExpeseItemNo, ConstantsUtils.addZeroBeforeValue(itemIncVal + 1, ConstantsUtils.ITEM_MAX_LENGTH));
            singleItem.put(Constants.LoginID, loginIdVal);
            singleItem.put(Constants.ExpenseItemType, expenseBean.getExpenseItemType());
            singleItem.put(Constants.ExpenseItemTypeDesc, expenseBean.getExpenseItemTypeDesc());
            singleItem.put(Constants.BeatGUID, stBeatGUID);
            singleItem.put(Constants.Location, expenseBean.getLocation());
            singleItem.put(Constants.ConvenyanceMode, expenseBean.getConvenyanceMode());
            if(expenseBean.getConvenyanceMode().equalsIgnoreCase(Constants.Conv_Mode_Type_Other)){
                singleItem.put(Constants.ConvenyanceModeDs, et_other_conv.getText().toString());
            }else{
                singleItem.put(Constants.ConvenyanceModeDs, expenseBean.getConvenyanceModeDs());
            }

            singleItem.put(Constants.BeatDistance, expenseBean.getBeatDistance());
            singleItem.put(Constants.UOM, expenseBean.getUOM());
            singleItem.put(Constants.Amount, expenseBean.getAmount());
            singleItem.put(Constants.Currency, expenseBean.getCurrency());
            singleItem.put(Constants.Remarks, expenseBean.getRemarks());
            arrItemTable.add(singleItem);
            itemIncVal++;
        }
        masterHashTable.put(Constants.entityType, Constants.Expenses);
        masterHashTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(arrItemTable));
        Constants.saveDeviceDocNoToSharedPref(getContext(), Constants.Expenses, doc_no);
        JSONObject jsonHeaderObject = new JSONObject(masterHashTable);
        Log.d(TAG, "jsonHeaderObject: " + jsonHeaderObject);
        UtilDataVault.storeInDataVault(doc_no, jsonHeaderObject.toString());
        displayCompletedDialogBox(getContext());
    }

    private void displayCompletedDialogBox(final Context mContext) {
        Constants.dialogBoxWithButton(mContext, "", getString(R.string.expense_daily_created_success, getString(R.string.expense_beat_non_beat_work)) + ""/*String.format(getString(R.string.expense_daily_created_success),mStrSeleExpenseTypeDesc)*/, getString(R.string.ok), "", new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                if (clickedStatus) {
                    Intent intBack = new Intent(mContext, MainMenu.class);
                    intBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intBack);
                }
            }
        });
    }

    private int checkValidations() {
        int validationCode = 0;
        if (TextUtils.isEmpty(mStrSeleExpenseTypeId)) {
            validationCode = 1;
            spExpenseType.setBackgroundResource(R.drawable.error_spinner);
        }
        if (mStrSeleExpenseTypeId.equalsIgnoreCase("000010")) {
            if (TextUtils.isEmpty(stBeatId)) {
                validationCode = 1;
                spBeatName.setBackgroundResource(R.drawable.error_spinner);
            }
        } else {
            if (TextUtils.isEmpty(stNonBeatType)) {
                validationCode = 1;
                spNonBeatType.setBackgroundResource(R.drawable.error_spinner);
            }

        }
        if (TextUtils.isEmpty(stLocation)) {
            validationCode = 1;
            spBeatWorkAt.setBackgroundResource(R.drawable.error_spinner);
        }
        if (TextUtils.isEmpty(stMode)) {
            validationCode = 1;
            spModeConve.setBackgroundResource(R.drawable.error_spinner);
        }
        if (!TextUtils.isEmpty(stMode)) {
            if(stMode.equalsIgnoreCase(Constants.Conv_Mode_Type_Other)){
                if (et_other_conv.getText() == null || et_other_conv.getText().toString().trim().equalsIgnoreCase("")) {
                    validationCode = 1;
                    et_other_conv.setBackgroundResource(R.drawable.edittext_border);
                }
            }
        }


        if (TextUtils.isEmpty(stDistanceValue)) {
            validationCode = 1;
            etDistance.setBackgroundResource(R.drawable.edittext_border);
        }else if(stDistanceValue.equals(".") || stDistanceValue.equals("0") ) {
            validationCode = 1;
            etDistance.setBackgroundResource(R.drawable.edittext_border);
        }else {

            try {
                BigDecimal bDistance = null;
                try {
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    decimalFormat.setParseBigDecimal(true);
                    bDistance = (BigDecimal) decimalFormat.parse(stDistanceValue);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (bDistance == null || bDistance.compareTo(new BigDecimal("0.0")) != 1) {
                    validationCode = 1;
                    etDistance.setBackgroundResource(R.drawable.edittext_border);
                }
            } catch (Exception e) {
                e.printStackTrace();
                validationCode = 1;
                etDistance.setBackgroundResource(R.drawable.edittext_border);
            }
        }
        if (stAmountCatType.equalsIgnoreCase("000020")) {
            if (TextUtils.isEmpty(stDailyTypeAllowance)) {
                validationCode = 1;
                etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
            } else {
                try {
                    double doubQty = Double.parseDouble(stDailyTypeAllowance);
                    double maxValuePer = Double.parseDouble(stMaxAllowancePer);
                    double finalAmount = (doubQty / 100.0f) * maxValuePer;
                    if (finalAmount > Double.parseDouble(mArrayDefaultExpenseAllowance[0][0])) {
                        validationCode = 4;
                        etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
                    } else {
                        expenseBeanList.get(1).setAmount(finalAmount + "");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    validationCode = 3;
                    expenseBeanList.get(1).setAmount("0.0");
                    etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
                }
            }

        } else if (stAmountCatType.equalsIgnoreCase("000010")) {
            if (TextUtils.isEmpty(stDailyTypeAllowance)) {
                validationCode = 1;
                etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
            }
        }

        return validationCode;
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
                Constants.incrementTextValues(etDistance, Constants.Y);
                break;
            case 69:
                //Minus
                Constants.decrementEditTextVal(etDistance, Constants.Y);
                break;
            case 1:

                break;
            case 2:

                break;
            case 56:
                KeyEvent event2 = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                getActivity().dispatchKeyEvent(event2);
                break;

            default:
                //default numbers
                KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                getActivity().dispatchKeyEvent(event);
                break;
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

    public void initializeKeyboardDependencies(View view) {
        keyboardView = (KeyboardView) view.findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(getContext(), R.xml.ll_plus_minus_without_updown_keybord);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
}
