package com.arteriatech.emami.expense;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.interfaces.DialogCallBack;
import com.arteriatech.emami.main.MainMenu;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.xscript.core.GUID;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseDailyFragment extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener, UIListener, KeyboardView.OnKeyboardActionListener {


    private static final String TAG = "ExpenseDailyFragment";
    public static int addExpenseReqCode = 14563;
    private ArrayList<ExpenseBean> expenseBeanList = new ArrayList<>();
    private ArrayList<ExpenseBean> tempExpenseBeanList = new ArrayList<>();
    private Spinner spExpenseType;
    private TextView tvExpDate;
    private int mYear = 0;
    private int mMonth = 0;
    private String mStrCurrentDate = "";
    private int mDay = 0;
    private DatePickerDialog datePickerDialog;
    private String[][] mArrayExpenseType = null;
    private String mStrSeleExpenseTypeId = "";
    private String mStrSeleExpenseTypeDesc = "";
    private String[][] mArrayDefaultExpenseType = null;
    private String[][] mArraySalesPerson = null;
    private int fiscalYear = 0;
    private ScrollView svExpenseList;
    private boolean mBooleanRemoveScrollViews = true;
    private String[][] arrayExpItemVal = null;
    private String[][] arrayRouteVal = null;
    private String[][] arrayExpLocationVal = null;
    private String[][] arrayExpModeVal = null;
    private Spinner[] spExpenseItemType = null;
    private Spinner[] spExpenseItemBeat = null;
    private Spinner[] spExpenseItemLocation = null;
    private Spinner[] spExpenseItemMode = null;
    private EditText[] edExpenseItemDistance = null;
    private ImageView[] ivDelete = null;
    private ImageView[] ivDetailsScreen = null;
    private String expenseFreq = "";
    private EditText[] edExpenseItemAmount = null;
    private TextView[] tvExpenseItemAmount = null;
    private TextView tvHeaderDistance;
    private TextView tvHeaderLocation;
    private TextView tvHeaderBeat;
    private String[][] mArrayDefaultExpenseAllowance = null;
    private Hashtable masterHashTable = new Hashtable();
    private ArrayList<HashMap<String, String>> arrItemTable = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> arrImageItemTable = new ArrayList<HashMap<String, String>>();
    private HashSet<String> headerDataUniq = new HashSet<>();
    private TextView tvHeaderMode;
    private ArrayAdapter<String> expenseTypeAdapter;
    private boolean isDialogNotOpened = true;
    private String secondTimeErrorMessage = "";
    private boolean isAmountValidationFailed = false;
    private AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int i, long id) {
            if (!mArrayExpenseType[0][i].equalsIgnoreCase("")) {
                if (!expenseItemValidation(expenseBeanList, mStrSeleExpenseTypeId, mArrayExpenseType[1][i])) {
                    spExpenseType.setOnItemSelectedListener(null);
                    int locationPoss = expenseTypeAdapter.getPosition(mStrSeleExpenseTypeDesc);
                    spExpenseType.setSelection(locationPoss);
                    spExpenseType.setOnItemSelectedListener(listener);
                    return;
                } else {
                    mStrSeleExpenseTypeId = mArrayExpenseType[0][i];
                    mStrSeleExpenseTypeDesc = mArrayExpenseType[1][i];
                    secondTimeErrorMessage = "Expense already submitted for " + mStrSeleExpenseTypeDesc;
                    if (!validateAlreadyDataSaved(mStrCurrentDate, mStrSeleExpenseTypeId)) {
                        expenseItemType(mStrSeleExpenseTypeId);
                        checkDefaultExpense();
                    } else {
                        Constants.dialogBoxWithButton(getContext(), "", secondTimeErrorMessage, "Ok", "", null);
                    }
                }
            } else {
                mStrSeleExpenseTypeId = mArrayExpenseType[0][i];
                mStrSeleExpenseTypeDesc = mArrayExpenseType[1][i];
                secondTimeErrorMessage = "Expense already submitted for " + mStrSeleExpenseTypeDesc;
                if (!validateAlreadyDataSaved(mStrCurrentDate, mStrSeleExpenseTypeId)) {
                    expenseItemType(mStrSeleExpenseTypeId);
                    checkDefaultExpense();
                } else {
                    Constants.dialogBoxWithButton(getContext(), "", secondTimeErrorMessage, "Ok", "", null);
                }
            }
            if (!mStrSeleExpenseTypeId.equals("")) {
                spExpenseType.setBackgroundResource(R.drawable.spinner_bg);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private boolean dialogBoxCreated = false;
    private Keyboard keyboard;
    public static KeyboardView keyboardView;
    private int lastSelectedEditText=0;
    private int lastSelectedAmountEditText=0;

    public ExpenseDailyFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_expense_daily, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        spExpenseType = (Spinner) view.findViewById(R.id.sp_expense_type);
        tvExpDate = (TextView) view.findViewById(R.id.tv_exp_date);
        svExpenseList = (ScrollView) view.findViewById(R.id.scroll_visit_summary_list);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        fiscalYear = mYear;
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        setHeaderUI(view);
        getDataFromOfflineDB();
        setDateIntoTextView(mMonth, mDay, mYear);
        setDataInUI();
        getSalesPersonValue();
        initializeKeyboardDependencies(view);
    }

    /*set header ui config*/
    private void setHeaderUI(View view) {
        tvHeaderDistance = (TextView) view.findViewById(R.id.iv_header_distance);
        tvHeaderLocation = (TextView) view.findViewById(R.id.iv_header_location);
        tvHeaderBeat = (TextView) view.findViewById(R.id.iv_header_beat);
        tvHeaderMode = (TextView) view.findViewById(R.id.iv_header_mode);
        if (expenseFreq.equalsIgnoreCase(Constants.ExpenseMonthly)) {
            tvHeaderDistance.setVisibility(View.GONE);
            tvHeaderLocation.setVisibility(View.GONE);
            tvHeaderBeat.setVisibility(View.GONE);
            tvHeaderMode.setVisibility(View.VISIBLE);
            secondTimeErrorMessage = getString(R.string.expense_secondtime_daily_error);
        } else {
            tvHeaderDistance.setVisibility(View.VISIBLE);
            tvHeaderLocation.setVisibility(View.VISIBLE);
            tvHeaderBeat.setVisibility(View.VISIBLE);
            tvHeaderMode.setVisibility(View.VISIBLE);
            secondTimeErrorMessage = getString(R.string.expense_secondtime_daily_error);
        }
    }

    /*get data from offline db and set into spinner*/
    private void setDataInUI() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(getContext(),AlertDialog.THEME_TRADITIONAL, null, year, month, day);
        tvExpDate.setOnClickListener(this);

        String mStrConfigQry = Constants.ExpenseConfigs + "?$filter=" + Constants.ExpenseFreq + " eq '" + expenseFreq + "'";
        try {
            mArrayExpenseType = OfflineManager.getConfigExpenseType(mStrConfigQry, "");
            if (mArrayExpenseType == null) {
                mArrayExpenseType = new String[2][1];
                mArrayExpenseType[0][0] = "";
                mArrayExpenseType[1][0] = "";
            }
            expenseTypeAdapter = new ArrayAdapter<String>(getContext(),
                    R.layout.custom_textview, mArrayExpenseType[1]);
            expenseTypeAdapter.setDropDownViewResource(R.layout.spinnerinside);
            spExpenseType.setAdapter(expenseTypeAdapter);
            spExpenseType.setOnItemSelectedListener(listener);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /*add default data into list*/
    private void checkDefaultExpense() {
        tempExpenseBeanList.clear();
        if (!expenseBeanList.isEmpty()) {
            for (ExpenseBean expenseBean : expenseBeanList) {
                if (expenseBean.getExpenseType().equalsIgnoreCase(mStrSeleExpenseTypeId)) {
                    tempExpenseBeanList.add(expenseBean);
                }
            }
            if (!tempExpenseBeanList.isEmpty()) {
                refreshRecyclerView(tempExpenseBeanList, false);
                return;

            }
        }
        if (!mStrSeleExpenseTypeId.equalsIgnoreCase("")) {
            String mStrConfigQry = Constants.ExpenseConfigs + "?$filter=" + Constants.ExpenseType + " eq '" + mStrSeleExpenseTypeId + "' and " + Constants.DefaultItemCat + " eq '000010'";
            try {
                mArrayDefaultExpenseType = OfflineManager.getConfigExpense(mStrConfigQry, "");
                if (mArrayDefaultExpenseType != null) {
                    int totalExpanse = mArrayDefaultExpenseType[3].length - 1;
                    if (totalExpanse > 0) {
                        for (int defaultExp = 1; defaultExp <= totalExpanse; defaultExp++) {
                            ExpenseBean expenseBean = new ExpenseBean();
                            expenseBean.setExpenseItemTypeDesc(mArrayDefaultExpenseType[3][defaultExp]);
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
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        refreshRecyclerView(expenseBeanList, true);
    }

    /*set value based on expense item type*/
    private void getSelectedConfig(String expenseItemTypeId, ExpenseBean expenseBean, ImageView imageView, TextView tvAmount, EditText etAmount) {
        if (!mStrSeleExpenseTypeId.equalsIgnoreCase("") && !expenseItemTypeId.equalsIgnoreCase("") && !expenseBean.isDefault()) {
            String mStrConfigQry = Constants.ExpenseConfigs + "?$top=1&$filter=" + Constants.ExpenseType + " eq '" + mStrSeleExpenseTypeId + "' and " + Constants.ExpenseItemType + " eq '" + expenseItemTypeId + "'";
            try {
                mArrayDefaultExpenseType = OfflineManager.getConfigExpense(mStrConfigQry, "");
                if (mArrayDefaultExpenseType != null) {
                    int totalExpanse = mArrayDefaultExpenseType[3].length - 1;
                    if (totalExpanse > 0) {
                        int defaultExp = 1;
                        expenseBean.setDefault(false);
                        expenseBean.setAllowance(mArrayDefaultExpenseType[16][defaultExp]);
                        expenseBean.setItemFieldSet(mArrayDefaultExpenseType[14][defaultExp]);
                        expenseBean.setItemFieldSetDesc(mArrayDefaultExpenseType[15][defaultExp]);
                        expenseBean.setAmountCategory(mArrayDefaultExpenseType[10][defaultExp]);
                        expenseBean.setAmountCategoryDesc(mArrayDefaultExpenseType[11][defaultExp]);
                        expenseBean.setMaxAllowancePer(mArrayDefaultExpenseType[12][defaultExp]);
                        expenseBean.setIsSupportDocReq(mArrayDefaultExpenseType[17][defaultExp]);
                        expenseBean.setIsRemarksReq(mArrayDefaultExpenseType[19][defaultExp]);
                        expenseBean.setCurrency(mArrayDefaultExpenseType[18][defaultExp]);
                        expenseBean.setUOM(mArrayDefaultExpenseType[13][defaultExp]);
                    } else {
                        clearDatafromObject(expenseBean);
                    }
                } else {
                    clearDatafromObject(expenseBean);
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
                clearDatafromObject(expenseBean);
            }
        } else if (!expenseBean.isDefault()) {
            clearDatafromObject(expenseBean);
        }
        if (expenseBean.getIsSupportDocReq().equalsIgnoreCase("") && expenseBean.getIsRemarksReq().equalsIgnoreCase("")) {
            imageView.setVisibility(View.INVISIBLE);
        } else {
            imageView.setVisibility(View.VISIBLE);
        }

        if (expenseBean.getAmountCategory().equalsIgnoreCase("000030")) {
            try {
                double maxPer = Double.parseDouble(expenseBean.getMaxAllowancePer());
                double tempAmount = Double.parseDouble(expenseBean.getAllowance());
                double finalAmount = (tempAmount / 100.0f) * maxPer;
                tvAmount.setText(finalAmount + "");
                expenseBean.setAmount(finalAmount + "");
            } catch (Exception e) {
                e.printStackTrace();
                tvAmount.setText("");
            }
        } else if (expenseBean.getAmountCategory().equalsIgnoreCase("000020")) {
            tvAmount.setVisibility(View.GONE);
            etAmount.setVisibility(View.VISIBLE);
        } else if (expenseBean.getAmountCategory().equalsIgnoreCase("000010")) {
            tvAmount.setVisibility(View.GONE);
            etAmount.setVisibility(View.VISIBLE);
        }
    }

    /*clear data from object*/
    private void clearDatafromObject(ExpenseBean expenseBean) {
        expenseBean.setDefault(false);
        expenseBean.setAmount("");
        expenseBean.setAllowance("");
        expenseBean.setItemFieldSet("");
        expenseBean.setItemFieldSetDesc("");
        expenseBean.setAmountCategory("");
        expenseBean.setAmountCategoryDesc("");
        expenseBean.setIsSupportDocReq("");
        expenseBean.setIsRemarksReq("");
        expenseBean.setCurrency("");
        expenseBean.setUOM("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_expense, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
            case R.id.item_add:
                secondTimeErrorMessage = "Expense already submited for " + mStrSeleExpenseTypeDesc;
                if (!validateAlreadyDataSaved(mStrCurrentDate, mStrSeleExpenseTypeId)) {
                    addNewField();
                } else {
                    Constants.dialogBoxWithButton(getContext(), "", secondTimeErrorMessage, "Ok", "", null);
                }

                break;
            case R.id.item_save:
                onSaveData(getContext());
                break;
        }
        return true;
    }

    /*get data from sales person*/
    private void getSalesPersonValue() {
        try {
            mArraySalesPerson = Constants.getDistributors();
        } catch (Exception e) {
        }
    }

    /*check daily/monthly already created*/
    private boolean validateAlreadyDataSaved(String date, String expenseType) {
        boolean isDataPresent = false;
        String query = "";
        if (!expenseFreq.equalsIgnoreCase(Constants.ExpenseMonthly)) {
            try {
                query = Constants.Expenses + "?$filter=ExpenseDate eq datetime'" + UtilConstants.getTimeformat2(date, "") + "' and ExpenseType eq '" + expenseType + "'";
                isDataPresent = OfflineManager.getVisitStatusForCustomer(query);
                Log.d(TAG, "validateAlreadyDataSaved: " + isDataPresent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!isDataPresent) {
                isDataPresent = OfflineManager.checkDatavaltDataisPresent(getContext(), date, expenseType);
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
                    isDataPresent = OfflineManager.checkDatavaltMonthCompare(getContext(), date, expenseType);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return isDataPresent;
    }

    /*create new field in a list*/
    private void addNewField() {
        if (!mStrSeleExpenseTypeId.equalsIgnoreCase("")) {
            expenseItemType(mStrSeleExpenseTypeId);
            ExpenseBean expenseBean = new ExpenseBean();
            expenseBean.setExpenseType(mStrSeleExpenseTypeId);
            expenseBean.setExpenseTypeDesc(mStrSeleExpenseTypeDesc);
            expenseBeanList.add(expenseBean);
            refreshRecyclerView(expenseBeanList, true);
        }
    }

    /*save data into data valt*/
    private void onSaveData(Context context) {
        if (onSaveValidate()) {
            secondTimeErrorMessage = "Expense already submited for " + mStrSeleExpenseTypeDesc;
            if (!validateAlreadyDataSaved(mStrCurrentDate, mStrSeleExpenseTypeId)) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0);
                String loginIdVal = sharedPreferences.getString(Constants.username, "");
                GUID guid = GUID.newRandom();
                String doc_no = (System.currentTimeMillis() + "").substring(3, 10);
                headerDataUniq.clear();
                ArrayList<ExpenseImageBean> expenseImageBeanArrayList = null;
                sortArrayList();
                int expenseItemNo = 0;
                int headerNo = 0;
                int arrayTotalSize = expenseBeanList.size();
                boolean storeToDataValt = false;
                for (ExpenseBean expenseBean : expenseBeanList) {

                    //header
                    if (!headerDataUniq.contains(expenseBean.getExpenseType())) {
                        masterHashTable.clear();
                        arrItemTable.clear();
                        headerNo = 0;
                        guid = GUID.newRandom();
                        doc_no = (System.currentTimeMillis() + "").substring(3, 10);
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
                        masterHashTable.put(Constants.ExpenseType, expenseBean.getExpenseType());
                        masterHashTable.put(Constants.ExpenseTypeDesc, expenseBean.getExpenseTypeDesc());
                        masterHashTable.put(Constants.ExpenseDate, mStrCurrentDate);
                        masterHashTable.put(Constants.Status, "");
                        masterHashTable.put(Constants.StatusDesc, "");
                        masterHashTable.put(Constants.Amount, "0.0");
                        masterHashTable.put(Constants.Currency, expenseBean.getCurrency());

                    }

                    //item start
                    HashMap<String, String> singleItem = new HashMap<String, String>();
                    GUID itemGuid = GUID.newRandom();
                    singleItem.put(Constants.ExpenseItemGUID, itemGuid.toString36().toUpperCase());
                    singleItem.put(Constants.ExpenseGUID, guid.toString36().toUpperCase());
                    singleItem.put(Constants.ExpeseItemNo, expenseItemNo + "");
                    singleItem.put(Constants.LoginID, loginIdVal);
                    singleItem.put(Constants.ExpenseItemType, expenseBean.getExpenseItemType());
                    singleItem.put(Constants.ExpenseItemTypeDesc, expenseBean.getExpenseItemTypeDesc());
                    singleItem.put(Constants.BeatGUID, expenseBean.getBeatGUID());
                    singleItem.put(Constants.Location, expenseBean.getLocation());
                    singleItem.put(Constants.ConvenyanceMode, expenseBean.getConvenyanceMode());
                    singleItem.put(Constants.ConvenyanceModeDs, expenseBean.getConvenyanceModeDs());
                    singleItem.put(Constants.BeatDistance, expenseBean.getBeatDistance());
                    singleItem.put(Constants.UOM, expenseBean.getUOM());
                    singleItem.put(Constants.Amount, expenseBean.getAmount());
                    singleItem.put(Constants.Currency, expenseBean.getCurrency());
                    singleItem.put(Constants.Remarks, expenseBean.getRemarks());
                    arrImageItemTable.clear();
                    expenseImageBeanArrayList = expenseBean.getExpenseImageBeanArrayList();
                    if (expenseImageBeanArrayList != null) {
                        for (ExpenseImageBean expenseImageBean : expenseImageBeanArrayList) {
                            HashMap<String, String> singleImageItem = new HashMap<String, String>();
                            GUID itemImageGuid = GUID.newRandom();
                            singleImageItem.put(Constants.ExpenseDocumentID, itemImageGuid.toString36().toUpperCase());
                            singleImageItem.put(Constants.ExpenseItemGUID, itemGuid.toString36().toUpperCase());
                            singleImageItem.put(Constants.DocumentStore, "");
                            singleImageItem.put(Constants.DocumentTypeID, "");
                            singleImageItem.put(Constants.DocumentTypeDesc, "");
                            singleImageItem.put(Constants.LoginID, loginIdVal);
                            singleImageItem.put(Constants.DocumentStatusID, "");
                            singleImageItem.put(Constants.DocumentStatusDesc, "");
                            singleImageItem.put(Constants.ValidFrom, UtilConstants.getNewDateTimeFormat());
                            singleImageItem.put(Constants.ValidTo, UtilConstants.getNewDateTimeFormat());
                            singleImageItem.put(Constants.DocumentLink, expenseImageBean.getImagePath());
                            singleImageItem.put(Constants.FileName, expenseImageBean.getFileName() + "." + expenseImageBean.getImageExtensions());
                            singleImageItem.put(Constants.DocumentMimeType, expenseImageBean.getDocumentMimeType());
                            singleImageItem.put(Constants.DocumentSize, expenseImageBean.getDocumentSize());
                            singleImageItem.put(Constants.Remarks, expenseBean.getRemarks());
                            arrImageItemTable.add(singleImageItem);
                        }
                    }
                    singleItem.put("item_no" + headerNo, UtilConstants.convertArrListToGsonString(arrImageItemTable));
                    arrItemTable.add(singleItem);
                    //item end

            /*store data*/
                    if (arrayTotalSize > expenseItemNo + 1) {
                        if (expenseBean.getExpenseType().equals(expenseBeanList.get(expenseItemNo + 1).getExpenseType())) {
                            storeToDataValt = false;
                        } else {
                            storeToDataValt = true;
                        }
                    } else {
                        storeToDataValt = true;
                    }

                    if (storeToDataValt) {
                        masterHashTable.put(Constants.entityType, Constants.Expenses);
                        masterHashTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(arrItemTable));
                        Constants.saveDeviceDocNoToSharedPref(getContext(), Constants.Expenses, doc_no);
                        JSONObject jsonHeaderObject = new JSONObject(masterHashTable);
                        Log.d(TAG, "jsonHeaderObject: " + jsonHeaderObject);
                        UtilDataVault.storeInDataVault(doc_no, jsonHeaderObject.toString());
                        saveDocumentEntityToTable();
                    }
                    headerDataUniq.add(expenseBean.getExpenseType());
                    headerNo++;
                    expenseItemNo++;
                }
                String message = "";
                if (expenseFreq.equalsIgnoreCase(Constants.ExpenseMonthly)) {
                    message = getString(R.string.expense_monthly_created_success);
                } else {
                    message = getString(R.string.expense_daily_created_success);
                }
                Constants.dialogBoxWithButton(getContext(), "", message, getString(R.string.ok), "", new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean clickedStatus) {
                        if (clickedStatus) {
                            Intent intBack = new Intent(getContext(), MainMenu.class);
                            intBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intBack);
                        }
                    }
                });
            } else {
                Constants.dialogBoxWithButton(getContext(), "", secondTimeErrorMessage, getString(R.string.ok), "", null);
            }
        }
    }

    /*sorting array list based on expense type*/
    private void sortArrayList() {
        Collections.sort(expenseBeanList, new Comparator<ExpenseBean>() {
            @Override
            public int compare(ExpenseBean expenseBean1, ExpenseBean expenseBean2) {

                return expenseBean2.getExpenseType().compareTo(expenseBean1.getExpenseType());
            }
        });
    }

    /*save image into offline db*/
    private void saveDocumentEntityToTable() {
        try {
            //noinspection unchecked
            int getNumb = 0;
            for (HashMap<String, String> arrItemTables : arrItemTable) {
                String imageStringArray = arrItemTables.get("item_no" + getNumb);
                ArrayList<HashMap<String, String>> convertedString = UtilConstants.convertToArrayListMap(imageStringArray);

                for (HashMap<String, String> hashItem : convertedString) {
                    OfflineManager.createExpensesingItem(arrItemTables, hashItem, this);
                }
                Log.d(TAG, "onSaveData: " + imageStringArray);
                Log.d(TAG, "convertedString: " + convertedString);
                getNumb++;
            }

        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    /*validation when you click save button*/
    private boolean onSaveValidate() {
        int itemPoss = 0;
        boolean isValidation = true;
        if (mStrSeleExpenseTypeId.equalsIgnoreCase("")) {
            spExpenseType.setBackgroundResource(R.drawable.error_spinner);
            isValidation = false;
        }
        if (expenseBeanList.isEmpty()) {
            isValidation = false;
        }
        isAmountValidationFailed = false;
        for (ExpenseBean expenseBean : expenseBeanList) {

            if (!validateIndividualItem(expenseBean, expenseBean.getItemPos(), true)) {
                isValidation = false;
            }
            itemPoss++;
        }
        if (!isValidation) {
            if (!isAmountValidationFailed) {
                Constants.dialogBoxWithButton(getContext(), "", getString(R.string.validation_plz_fill_mandatory_flds), getString(R.string.ok), "", null);
            }
        }
        return isValidation;
    }

    /*validation when you are switching one item to another item*/
    private boolean expenseItemValidation(ArrayList<ExpenseBean> expenseBeanList, String mStrSeleExpenseTypeId, final String newExpenseTypeDesc) {
        boolean isValidation = true;
        int itemPoss = 0;
        for (ExpenseBean expenseBean : expenseBeanList) {
            if (expenseBean.getExpenseType().equalsIgnoreCase(mStrSeleExpenseTypeId)) {
                if (!validateIndividualItem(expenseBean, expenseBean.getItemPos(), true)) {
                    isValidation = false;
                }
            }
            itemPoss++;
        }
        if (!isValidation) {
            if (isDialogNotOpened) {
                Constants.dialogBoxWithButton(getContext(), "", getString(R.string.expense_error_change_type), getString(R.string.yes), getString(R.string.no), new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean clickedStatus) {
                        if (clickedStatus) {
                            clearAllData();
                            int locationPoss = expenseTypeAdapter.getPosition(newExpenseTypeDesc);
                            spExpenseType.setSelection(locationPoss);
                        }
                        isDialogNotOpened = true;
                    }
                });
                isDialogNotOpened = false;
            }
        }
        return isValidation;
    }

    private void clearAllData() {
        expenseBeanList.clear();
        spExpenseItemType = null;
        spExpenseItemBeat = null;
        spExpenseItemLocation = null;
        spExpenseItemMode = null;
        edExpenseItemDistance = null;
        ivDelete = null;
        ivDetailsScreen = null;
        edExpenseItemAmount = null;
        tvExpenseItemAmount = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (addExpenseReqCode == requestCode) {
            if (data != null) {
                boolean inNewRecord = data.getBooleanExtra(AddExpenseActivity.EXTRA_EXPENSE_IS_ADD_NEW, false);
                ExpenseBean expenseBean = (ExpenseBean) data.getSerializableExtra(AddExpenseActivity.EXTRA_EXPENSE_BEAN);
                if (inNewRecord) {
                    expenseBeanList.add(expenseBean);
                } else {
                    int itemPos = 0;
                    int removeItemPoss = -1;
                    for (ExpenseBean expenseBeans : expenseBeanList) {
                        if (expenseBeans.getExpenseType().equalsIgnoreCase(mStrSeleExpenseTypeId) && expenseBeans.getItemPos() == expenseBean.getItemPos()) {
                            removeItemPoss = itemPos;
                        }
                        itemPos++;
                    }
                    if (removeItemPoss > -1) {
                        expenseBeanList.remove(removeItemPoss);
                        expenseBeanList.add(expenseBean);
                    }
                }
                refreshRecyclerView(expenseBeanList, true);
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    /*get expense item type*/
    private void expenseItemType(String typeId) {
        String query = Constants.ExpenseConfigs + "?$filter=" + Constants.ExpenseFreq + " eq '" + expenseFreq + "' and " + Constants.ExpenseType + " eq '" + typeId + "'";
        try {
            arrayExpItemVal = null;
            arrayExpItemVal = OfflineManager.getConfigExpense(query, "");
            Log.d(TAG, "expenseItemType: arrayExpItemVal " + arrayExpItemVal);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }
    }

    /*get dropdown data from offline db*/
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
    }

    /*list view create or refresh*/
    private void refreshRecyclerView(ArrayList<ExpenseBean> expenseBeanList, boolean checkCondition) {
        if (checkCondition) {
            tempExpenseBeanList.clear();
            if (!expenseBeanList.isEmpty()) {
                for (ExpenseBean expenseBean : expenseBeanList) {
                    if (expenseBean.getExpenseType().equalsIgnoreCase(mStrSeleExpenseTypeId)) {
                        tempExpenseBeanList.add(expenseBean);
                    }
                }
                displayTableView(tempExpenseBeanList, getContext());
                return;

            }
        }
        displayTableView(expenseBeanList, getContext());
    }

    /*display table view*/
    private void displayTableView(ArrayList<ExpenseBean> expenseBeanList, Context mContext) {
        if (!mBooleanRemoveScrollViews) {
            svExpenseList.removeAllViews();
        }

        mBooleanRemoveScrollViews = false;

        TableLayout tlRetTrends = (TableLayout) LayoutInflater.from(mContext).inflate(
                R.layout.item_table, null, false);
        LinearLayout llRetTrends;
        if (!expenseBeanList.isEmpty()) {
            int itemPoss = 0;
            int totalSizeofArray = expenseBeanList.size();
            spExpenseItemType = new Spinner[totalSizeofArray];
            spExpenseItemBeat = new Spinner[totalSizeofArray];
            spExpenseItemLocation = new Spinner[totalSizeofArray];
            spExpenseItemMode = new Spinner[totalSizeofArray];
            edExpenseItemDistance = new EditText[totalSizeofArray];
            ivDelete = new ImageView[totalSizeofArray];
            ivDetailsScreen = new ImageView[totalSizeofArray];
            edExpenseItemAmount = new EditText[totalSizeofArray];
            tvExpenseItemAmount = new TextView[totalSizeofArray];
            for (final ExpenseBean expenseBean : expenseBeanList) {
                llRetTrends = (LinearLayout) LayoutInflater.from(mContext)
                        .inflate(R.layout.expense_daily_item,
                                null, false);
                expenseBean.setItemPos(itemPoss);
                /*init all views*/
                initAllView(expenseBean, itemPoss, llRetTrends, mContext);
                itemPoss++;
                tlRetTrends.addView(llRetTrends);
            }

        } else {

            llRetTrends = (LinearLayout) LayoutInflater.from(mContext)
                    .inflate(R.layout.no_data_found_ll,
                            null, false);

            tlRetTrends.addView(llRetTrends);
        }


        svExpenseList.addView(tlRetTrends);
        svExpenseList.requestLayout();
    }

    /*init all view for custom list*/
    private void initAllView(final ExpenseBean expenseBean, final int itemPoss, LinearLayout llRetTrends, Context mContext) {
        spExpenseItemType[itemPoss] = (Spinner) llRetTrends.findViewById(R.id.sp_expense_item_type);
        spExpenseItemBeat[itemPoss] = (Spinner) llRetTrends.findViewById(R.id.sp_expense_item_beat);
        spExpenseItemLocation[itemPoss] = (Spinner) llRetTrends.findViewById(R.id.sp_expense_item_location);
        spExpenseItemMode[itemPoss] = (Spinner) llRetTrends.findViewById(R.id.sp_expense_item_mode);
        edExpenseItemDistance[itemPoss] = (EditText) llRetTrends.findViewById(R.id.ed_expense_item_distance);
        ivDelete[itemPoss] = (ImageView) llRetTrends.findViewById(R.id.iv_delete);
        ivDetailsScreen[itemPoss] = (ImageView) llRetTrends.findViewById(R.id.iv_open_other);
        edExpenseItemAmount[itemPoss] = (EditText) llRetTrends.findViewById(R.id.ed_expense_item_amount);
        tvExpenseItemAmount[itemPoss] = (TextView) llRetTrends.findViewById(R.id.tv_exp_item_amount);
        LinearLayout llDistance = (LinearLayout) llRetTrends.findViewById(R.id.ll_item_distance);
        LinearLayout llDistanceLine = (LinearLayout) llRetTrends.findViewById(R.id.ll_item_distance_line);
        LinearLayout llLocation = (LinearLayout) llRetTrends.findViewById(R.id.ll_item_location);
        LinearLayout llBeat = (LinearLayout) llRetTrends.findViewById(R.id.ll_item_beat);
        LinearLayout llBeatLine = (LinearLayout) llRetTrends.findViewById(R.id.ll_item_beat_line);
        LinearLayout llLocationLine = (LinearLayout) llRetTrends.findViewById(R.id.ll_item_location_line);
        LinearLayout llModeLine = (LinearLayout) llRetTrends.findViewById(R.id.ll_item_mode_line);
        LinearLayout llMode = (LinearLayout) llRetTrends.findViewById(R.id.ll_item_mode);
        UtilConstants.editTextDecimalFormat(edExpenseItemDistance[itemPoss], 13, 3);
        UtilConstants.editTextDecimalFormat(edExpenseItemAmount[itemPoss], 13, 3);
        edExpenseItemDistance[itemPoss].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastSelectedEditText=itemPoss;
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView,getActivity());
                return true;
            }
        });
        edExpenseItemDistance[itemPoss].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lastSelectedEditText=itemPoss;
                if(hasFocus){
                    Constants.showCustomKeyboard(v, keyboardView, getActivity());
                }else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        edExpenseItemDistance[itemPoss].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                expenseBean.setBeatDistance(s.toString());
                edExpenseItemDistance[itemPoss].setBackgroundResource(R.drawable.edittext);
                if (expenseBean.getAmountCategory().equalsIgnoreCase("000040"))
                    setAmountfromDB(expenseBean, tvExpenseItemAmount[itemPoss], edExpenseItemAmount[itemPoss], "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edExpenseItemAmount[itemPoss].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastSelectedAmountEditText=itemPoss;
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView,getActivity());
                return true;
            }
        });
        edExpenseItemAmount[itemPoss].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lastSelectedAmountEditText=itemPoss;
                if(hasFocus){
                    Constants.showCustomKeyboard(v, keyboardView, getActivity());
                }else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        edExpenseItemAmount[itemPoss].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                expenseBean.setEditAmount(s.toString());
                edExpenseItemAmount[itemPoss].setBackgroundResource(R.drawable.edittext);
                edExpenseItemAmount[itemPoss].setError(null);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edExpenseItemAmount[itemPoss].setText(expenseBean.getEditAmount());
        ivDelete[itemPoss].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItemFromList(itemPoss);
            }
        });
        ivDetailsScreen[itemPoss].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditItemActivity(v, expenseBean, itemPoss);
            }
        });
        if (expenseBean.getIsSupportDocReq().equalsIgnoreCase("") && expenseBean.getIsRemarksReq().equalsIgnoreCase("")) {
            ivDetailsScreen[itemPoss].setVisibility(View.INVISIBLE);
        } else {
            ivDetailsScreen[itemPoss].setVisibility(View.VISIBLE);
        }
        edExpenseItemDistance[itemPoss].setText(expenseBean.getBeatDistance());
        if (expenseBean.isDefault()) {
            ivDelete[itemPoss].setVisibility(View.INVISIBLE);
        } else {
            ivDelete[itemPoss].setVisibility(View.VISIBLE);
        }
        if (!expenseFreq.equalsIgnoreCase(Constants.ExpenseMonthly)) {
            llDistance.setVisibility(View.VISIBLE);
            llDistanceLine.setVisibility(View.VISIBLE);
            llLocation.setVisibility(View.VISIBLE);
            llBeat.setVisibility(View.VISIBLE);
            llBeatLine.setVisibility(View.VISIBLE);
            llLocationLine.setVisibility(View.VISIBLE);
            llModeLine.setVisibility(View.VISIBLE);
            llMode.setVisibility(View.VISIBLE);

            if (expenseBean.getItemFieldSet().equalsIgnoreCase("000040")) {
                spExpenseItemBeat[itemPoss].setVisibility(View.VISIBLE);
                spExpenseItemLocation[itemPoss].setVisibility(View.VISIBLE);
                setBeatDataIntoSpinner(mContext, expenseBean, spExpenseItemBeat[itemPoss], itemPoss);
                setExpanseLocation(mContext, expenseBean, spExpenseItemLocation[itemPoss], itemPoss, tvExpenseItemAmount[itemPoss], edExpenseItemAmount[itemPoss]);
            } else if (expenseBean.getItemFieldSet().equalsIgnoreCase("000030")) {
                spExpenseItemBeat[itemPoss].setVisibility(View.INVISIBLE);
                spExpenseItemLocation[itemPoss].setVisibility(View.VISIBLE);
                setExpanseLocation(mContext, expenseBean, spExpenseItemLocation[itemPoss], itemPoss, tvExpenseItemAmount[itemPoss], edExpenseItemAmount[itemPoss]);
            } else if (expenseBean.getItemFieldSet().equalsIgnoreCase("000020")) {
                spExpenseItemBeat[itemPoss].setVisibility(View.VISIBLE);
                spExpenseItemLocation[itemPoss].setVisibility(View.INVISIBLE);
                setBeatDataIntoSpinner(mContext, expenseBean, spExpenseItemBeat[itemPoss], itemPoss);
            } else if (expenseBean.getItemFieldSet().equalsIgnoreCase("000010")) {
                spExpenseItemBeat[itemPoss].setVisibility(View.INVISIBLE);
                spExpenseItemLocation[itemPoss].setVisibility(View.INVISIBLE);
            } else {
                spExpenseItemBeat[itemPoss].setVisibility(View.VISIBLE);
                spExpenseItemLocation[itemPoss].setVisibility(View.VISIBLE);
                setBeatDataIntoSpinner(mContext, expenseBean, spExpenseItemBeat[itemPoss], itemPoss);
                setExpanseLocation(mContext, expenseBean, spExpenseItemLocation[itemPoss], itemPoss, tvExpenseItemAmount[itemPoss], edExpenseItemAmount[itemPoss]);
            }
            setExpanseItemType(mContext, expenseBean, spExpenseItemType[itemPoss], itemPoss, ivDetailsScreen[itemPoss], tvExpenseItemAmount[itemPoss], edExpenseItemAmount[itemPoss], spExpenseItemMode[itemPoss], edExpenseItemDistance[itemPoss]);
            setExpanseMode(mContext, expenseBean, spExpenseItemMode[itemPoss], itemPoss, tvExpenseItemAmount[itemPoss], edExpenseItemAmount[itemPoss]);

        } else {
            llDistance.setVisibility(View.GONE);
            llDistanceLine.setVisibility(View.GONE);
            llLocation.setVisibility(View.GONE);
            llBeat.setVisibility(View.GONE);
            llBeatLine.setVisibility(View.GONE);
            llLocationLine.setVisibility(View.GONE);
            llModeLine.setVisibility(View.VISIBLE);
            llMode.setVisibility(View.INVISIBLE);
            setExpanseItemType(mContext, expenseBean, spExpenseItemType[itemPoss], itemPoss, ivDetailsScreen[itemPoss], tvExpenseItemAmount[itemPoss], edExpenseItemAmount[itemPoss], spExpenseItemMode[itemPoss], edExpenseItemDistance[itemPoss]);
        }

        if (expenseBean.getAmountCategory().equalsIgnoreCase("000040")) {
            tvExpenseItemAmount[itemPoss].setVisibility(View.VISIBLE);
            edExpenseItemAmount[itemPoss].setVisibility(View.GONE);
        } else if (expenseBean.getAmountCategory().equalsIgnoreCase("000030")) {
            tvExpenseItemAmount[itemPoss].setVisibility(View.VISIBLE);
            edExpenseItemAmount[itemPoss].setVisibility(View.GONE);
        } else if (expenseBean.getAmountCategory().equalsIgnoreCase("000020")) {
            tvExpenseItemAmount[itemPoss].setVisibility(View.GONE);
            edExpenseItemAmount[itemPoss].setVisibility(View.VISIBLE);
        } else if (expenseBean.getAmountCategory().equalsIgnoreCase("000010")) {
            tvExpenseItemAmount[itemPoss].setVisibility(View.GONE);
            edExpenseItemAmount[itemPoss].setVisibility(View.VISIBLE);
        }
    }

    /*get amount from expense table */
    private void setAmountfromDB(ExpenseBean expenseBean, TextView amountTextView, EditText etAmount, String qtyText) {

        try {
            String itemType = expenseBean.getExpenseItemType();
            String location = expenseBean.getLocation();
            String convenyanceMode = expenseBean.getConvenyanceMode();

            String query = Constants.ExpenseAllowances + "?$filter=" + Constants.ExpenseType + " eq '" + mStrSeleExpenseTypeId + "' and ExpenseItemType eq '" +
                    itemType + "' and Location eq '" + location + "' and ConveyanceMode eq '" + convenyanceMode + "'";
            mArrayDefaultExpenseAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
            if (mArrayDefaultExpenseAllowance != null) {
                try {
                    if (mArrayDefaultExpenseAllowance[0].length > 0) {
                        if (expenseBean.getAmountCategory().equalsIgnoreCase("000040")) {
                            double doubQty = Double.parseDouble(expenseBean.getBeatDistance());
                            double maxValue = Double.parseDouble(mArrayDefaultExpenseAllowance[0][0]);
                            double finalAmount = doubQty * maxValue;
                            amountTextView.setText(finalAmount + "");
                            expenseBean.setAmount(finalAmount + "");

                        }

                    } else {
                        amountTextView.setText("0.0");
                        expenseBean.setAmount("0.0");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    amountTextView.setText("0.0");
                    expenseBean.setAmount("0.0");
                }
            } else {
                double maxPer = Double.parseDouble(expenseBean.getMaxAllowancePer());
                double tempAmount = Double.parseDouble(mArrayDefaultExpenseAllowance[0][0]);
                double finalAmount = (tempAmount / 100.0f) * maxPer;
                amountTextView.setText(finalAmount + "");
                expenseBean.setAmount(finalAmount + "");
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*set expense item type into spinner*/
    private void setExpanseItemType(Context context, final ExpenseBean expenseBean, final Spinner spExpenseItemType, final int pos, final ImageView imageView, final TextView tvAmount, final EditText etAmount, final Spinner spMode, final EditText etDistance) {
        if (arrayExpItemVal == null) {
            arrayExpItemVal = new String[4][1];
            arrayExpItemVal[0][0] = "";
            arrayExpItemVal[1][0] = "";
            arrayExpItemVal[2][0] = "";
            arrayExpItemVal[3][0] = Constants.None;
        }
        ArrayAdapter<String> spBeatAdapter = new ArrayAdapter<>(context,
                R.layout.custom_textview, arrayExpItemVal[3]);
        spBeatAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spExpenseItemType.setAdapter(spBeatAdapter);
        if (expenseBean != null) {
            int itemPoss = spBeatAdapter.getPosition(expenseBean.getExpenseItemTypeDesc());
            spExpenseItemType.setSelection(itemPoss);
        }
        spExpenseItemType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                if (!checkItemAlreadySelected(arrayExpItemVal[2][position], pos)) {
                    expenseBean.setExpenseItemType(arrayExpItemVal[2][position]);
                    expenseBean.setExpenseItemTypeDesc(arrayExpItemVal[3][position]);
                    if (!expenseBean.getExpenseItemType().equalsIgnoreCase("")) {
                        spExpenseItemType.setBackgroundResource(R.drawable.spinner_bg);
                    }
                    getSelectedConfig(arrayExpItemVal[2][position], expenseBean, imageView, tvAmount, etAmount);
                    if (expenseBean.getAmountCategory().equalsIgnoreCase("000040")) {
                        setAmountfromDB(expenseBean, tvAmount, etAmount, "");
                        spMode.setVisibility(View.VISIBLE);
                        etDistance.setVisibility(View.VISIBLE);
                    } else {
                        spMode.setVisibility(View.INVISIBLE);
                        etDistance.setVisibility(View.INVISIBLE);
                    }
                } else {
                    spExpenseItemType.setSelection(0);
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        if (expenseBean.isDefault()) {
            spExpenseItemType.setEnabled(false);
        } else {
            spExpenseItemType.setEnabled(true);
        }
    }

    /*check item already present in the list*/
    private boolean checkItemAlreadySelected(String expItemId, int itemPos) {
        if (!expItemId.equals("")) {
            for (ExpenseBean expenseBean : expenseBeanList) {
                if (expenseBean.getExpenseItemType().equals(expItemId) && itemPos != expenseBean.getItemPos() && expenseBean.getExpenseType().equals(mStrSeleExpenseTypeId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*set beat data into spinner*/
    private void setBeatDataIntoSpinner(Context context, final ExpenseBean expenseBean, final Spinner spExpenseItemBeat, int pos) {

        if (arrayRouteVal == null) {
            arrayRouteVal = new String[3][1];
            arrayRouteVal[0][0] = "";
            arrayRouteVal[1][0] = Constants.None;
            arrayRouteVal[2][0] = "";
        }
        ArrayAdapter<String> spBeatAdapter = new ArrayAdapter<>(context,
                R.layout.custom_textview, arrayRouteVal[1]);
        spBeatAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spExpenseItemBeat.setAdapter(spBeatAdapter);
        if (expenseBean != null) {
            int beatPoss = spBeatAdapter.getPosition(expenseBean.getBeatDesc());
            spExpenseItemBeat.setSelection(beatPoss);
        }
        spExpenseItemBeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                expenseBean.setBeatId(arrayRouteVal[0][position]);
                expenseBean.setBeatDesc(arrayRouteVal[1][position]);
                expenseBean.setBeatGUID(arrayRouteVal[2][position]);
                if (!expenseBean.getBeatId().equalsIgnoreCase("")) {
                    spExpenseItemBeat.setBackgroundResource(R.drawable.spinner_bg);
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    /*set expense location into spinner*/
    private void setExpanseLocation(Context context, final ExpenseBean expenseBean, final Spinner spExpenseItemLocation, int pos, final TextView tvAmount, final EditText etAmount) {
        if (arrayExpLocationVal == null) {
            arrayExpLocationVal = new String[2][1];
            arrayExpLocationVal[0][0] = "";
            arrayExpLocationVal[1][0] = Constants.None;
        }
        ArrayAdapter<String> spBeatAdapter = new ArrayAdapter<>(context,
                R.layout.custom_textview, arrayExpLocationVal[1]);
        spBeatAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spExpenseItemLocation.setAdapter(spBeatAdapter);
        if (expenseBean != null) {
            int locationPoss = spBeatAdapter.getPosition(expenseBean.getLocationDesc());
            spExpenseItemLocation.setSelection(locationPoss);
        }
        spExpenseItemLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                expenseBean.setLocation(arrayExpLocationVal[0][position]);
                expenseBean.setLocationDesc(arrayExpLocationVal[1][position]);
                if (!expenseBean.getLocation().equalsIgnoreCase("")) {
                    spExpenseItemLocation.setBackgroundResource(R.drawable.spinner_bg);
                }
                if (expenseBean.getAmountCategory().equalsIgnoreCase("000040")) {
                    setAmountfromDB(expenseBean, tvAmount, etAmount, "");
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    /*set expanse mode into spinner*/
    private void setExpanseMode(Context context, final ExpenseBean expenseBean, final Spinner spExpenseItemMode, int pos, final TextView tvAmount, final EditText etAmount) {
        if (arrayExpModeVal == null) {
            arrayExpModeVal = new String[2][1];
            arrayExpModeVal[0][0] = "";
            arrayExpModeVal[1][0] = Constants.None;
        }else {
            arrayExpModeVal = Constants.CheckForOtherInConfigValue(arrayExpModeVal);
        }
        ArrayAdapter<String> spBeatAdapter = new ArrayAdapter<>(context,
                R.layout.custom_textview, arrayExpModeVal[1]);
        spBeatAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spExpenseItemMode.setAdapter(spBeatAdapter);
        if (expenseBean != null) {
            int modePoss = spBeatAdapter.getPosition(expenseBean.getConvenyanceModeDs());
            spExpenseItemMode.setSelection(modePoss);
        }
        spExpenseItemMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                expenseBean.setConvenyanceMode(arrayExpModeVal[0][position]);
                expenseBean.setConvenyanceModeDs(arrayExpModeVal[1][position]);
                if (!expenseBean.getConvenyanceMode().equalsIgnoreCase("")) {
                    spExpenseItemMode.setBackgroundResource(R.drawable.spinner_bg);
                }
                if (expenseBean.getAmountCategory().equalsIgnoreCase("000040")) {
                    setAmountfromDB(expenseBean, tvAmount, etAmount, "");
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    /*remove item from list*/
    private void removeItemFromList(final int itemPoss) {
        Constants.dialogBoxWithButton(getContext(), "", getString(R.string.expense_delete_conformation), getString(R.string.btn_confirm_confirm), getString(R.string.title_forgot_pass_cancel), new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                if (clickedStatus) {
                    expenseBeanList.remove(itemPoss);
                    refreshRecyclerView(expenseBeanList, true);
                }
            }
        });

    }

    /*validation for each bean based on config*/
    private boolean validateIndividualItem(ExpenseBean expenseBean, int itemPoss, boolean withImageRem) {
        boolean isValidation = true;
        if (expenseBean.getExpenseItemType().equalsIgnoreCase("")) {
            isValidation = false;
            spExpenseItemType[itemPoss].setBackgroundResource(R.drawable.error_spinner);

        }
        if (expenseBean.getAmountCategory().equalsIgnoreCase("000020") || expenseBean.getAmountCategory().equalsIgnoreCase("000010")) {

            if (expenseBean.getEditAmount().equalsIgnoreCase("")) {
                isValidation = false;
                edExpenseItemAmount[itemPoss].setBackgroundResource(R.drawable.edittext_border);
            } else if (expenseBean.getAmountCategory().equalsIgnoreCase("000020")) {
                try {
                    double doubQty = Double.parseDouble(expenseBean.getEditAmount());
                    double maxValuePer = Double.parseDouble(expenseBean.getMaxAllowancePer());
                    double finalAmount = (doubQty / 100.0f) * maxValuePer;
                    if (finalAmount > Double.parseDouble(expenseBean.getAllowance())) {
                        isValidation = false;
                        edExpenseItemAmount[itemPoss].setBackgroundResource(R.drawable.edittext_border);
                        edExpenseItemAmount[itemPoss].setError(getString(R.string.expense_error_amount));
                        isAmountValidationFailed = true;
                    } else {
                        expenseBean.setAmount(finalAmount + "");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    isValidation = false;
                    edExpenseItemAmount[itemPoss].setBackgroundResource(R.drawable.edittext_border);
                    edExpenseItemAmount[itemPoss].setError(getString(R.string.expense_error_enter_valid_amount));
                    isAmountValidationFailed = true;
                }
            } else if (expenseBean.getAmountCategory().equalsIgnoreCase("000010")) {
                expenseBean.setAmount(expenseBean.getEditAmount());
            }


        }
        if (expenseFreq.equalsIgnoreCase(Constants.ExpenseDaily)) {
            if (expenseBean.getAmountCategory().equalsIgnoreCase("000040")) {
                if (expenseBean.getConvenyanceMode().equalsIgnoreCase("")) {
                    isValidation = false;
                    spExpenseItemMode[itemPoss].setBackgroundResource(R.drawable.error_spinner);

                }
                if (expenseBean.getBeatDistance().equalsIgnoreCase("")) {
                    isValidation = false;
                    edExpenseItemDistance[itemPoss].setBackgroundResource(R.drawable.edittext_border);
                }else {
                    try{
                        String distance = edExpenseItemDistance[itemPoss].getText().toString();
                        int iDistance = Integer.parseInt(distance);
                        if(iDistance<=0){
                            isValidation = false;
                            edExpenseItemDistance[itemPoss].setBackgroundResource(R.drawable.edittext_border);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        isValidation = false;
                        edExpenseItemDistance[itemPoss].setBackgroundResource(R.drawable.edittext_border);
                    }
                }
            }
            if (withImageRem) {
                if (!expenseBean.getIsSupportDocReq().equalsIgnoreCase("")) {
                    if (expenseBean.getExpenseImageBeanArrayList() == null) {
                        isValidation = false;
                    }
                }
                if (!expenseBean.getIsRemarksReq().equalsIgnoreCase("")) {
                    if (expenseBean.getRemarks().equalsIgnoreCase("")) {
                        isValidation = false;
                    }
                }
            }
            if (expenseBean.isDefault()) {
                if (expenseBean.getItemFieldSet().equalsIgnoreCase("000040")) {
                    if (expenseBean.getBeatId().equalsIgnoreCase("")) {
                        isValidation = false;
                        spExpenseItemBeat[itemPoss].setBackgroundResource(R.drawable.error_spinner);

                    }
                    if (expenseBean.getLocation().equalsIgnoreCase("")) {
                        isValidation = false;
                        spExpenseItemLocation[itemPoss].setBackgroundResource(R.drawable.error_spinner);
                    }
                } else if (expenseBean.getItemFieldSet().equalsIgnoreCase("000030")) {
                    if (expenseBean.getLocation().equalsIgnoreCase("")) {
                        isValidation = false;
                        spExpenseItemLocation[itemPoss].setBackgroundResource(R.drawable.error_spinner);
                    }
                } else if (expenseBean.getItemFieldSet().equalsIgnoreCase("000020")) {
                    if (expenseBean.getLocation().equalsIgnoreCase("")) {
                        isValidation = false;
                        spExpenseItemBeat[itemPoss].setBackgroundResource(R.drawable.error_spinner);
                    }
                }
            } else {
                if (expenseBean.getBeatId().equalsIgnoreCase("")) {
                    isValidation = false;
                    spExpenseItemBeat[itemPoss].setBackgroundResource(R.drawable.error_spinner);

                }
                if (expenseBean.getLocation().equalsIgnoreCase("")) {
                    isValidation = false;
                    spExpenseItemLocation[itemPoss].setBackgroundResource(R.drawable.error_spinner);
                }
            }
        } else {
            if (withImageRem) {
                if (!expenseBean.getIsSupportDocReq().equalsIgnoreCase("")) {
                    if (expenseBean.getExpenseImageBeanArrayList() == null) {
                        isValidation = false;
                    }
                }
                if (!expenseBean.getIsRemarksReq().equalsIgnoreCase("")) {
                    if (expenseBean.getRemarks().equalsIgnoreCase("")) {
                        isValidation = false;
                    }
                }
            }
        }

        return isValidation;
    }

    /*open addexpenseactivity*/
    private void openEditItemActivity(View v, ExpenseBean expenseBean, int itemPoss) {
        isAmountValidationFailed = false;
        if (validateIndividualItem(expenseBean, itemPoss, false)) {
            Intent intent = new Intent(getContext(), AddExpenseActivity.class);
            intent.putExtra(AddExpenseActivity.EXTRA_EXPENSE_TYPE_ID, mStrSeleExpenseTypeId);
            intent.putExtra(AddExpenseActivity.EXTRA_EXPENSE_IS_ADD_NEW, false);
            intent.putExtra(AddExpenseActivity.EXTRA_EXPENSE_BEAN, expenseBean);
            startActivityForResult(intent, addExpenseReqCode);
        } else {
            if (!isAmountValidationFailed) {
                Constants.dialogBoxWithButton(getContext(), "", getString(R.string.validation_plz_enter_mandatory_flds), getString(R.string.ok), "", null);
            }
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
            tvExpDate.setText(new StringBuilder().append(mMonth + 1).append("/").append(mYear).append(" "));
        } else {
            tvExpDate.setText(new StringBuilder()
                    .append(mDay).append("/").append(mMonth + 1).append("/").append(mYear).append(" "));
        }

        secondTimeErrorMessage = "Expense already submited for " + mStrSeleExpenseTypeDesc;
        expenseItemType(mStrSeleExpenseTypeId);
        if (validateAlreadyDataSaved(mStrCurrentDate, mStrSeleExpenseTypeId)) {

            if (!dialogBoxCreated) {
                expenseBeanList.clear();
                tempExpenseBeanList.clear();
                refreshRecyclerView(expenseBeanList, false);
                Constants.dialogBoxWithButton(getContext(), "", secondTimeErrorMessage, "Ok", "", new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean clickedStatus) {
                        dialogBoxCreated = false;
                    }
                });

            }
            dialogBoxCreated = true;
        } else {
            expenseBeanList.clear();
            checkDefaultExpense();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_exp_date:
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
                cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
                cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
                Calendar calendar = Calendar.getInstance();
                if (expenseFreq.equalsIgnoreCase(Constants.ExpenseMonthly)) {
                    calendar.set(Calendar.DATE, 1);
                    calendar.add(Calendar.MONTH, -(getDayMonthConfig() - 1));

                    datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);

                } else {
                    calendar.add(Calendar.DAY_OF_MONTH, -getDayMonthConfig());
                }
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
    public void onRequestError(int i, Exception e) {
        Log.d(TAG, "onRequestError: " + e.toString());

    }

    @Override
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException {
        Log.d(TAG, "onRequestSuccess: ");
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
                //Plus
                if (edExpenseItemDistance[lastSelectedEditText].isFocused()) {
                    Constants.incrementTextValues(edExpenseItemDistance[lastSelectedEditText], Constants.Y);
                } else {
                    Constants.incrementTextValues(edExpenseItemAmount[lastSelectedAmountEditText], Constants.Y);
                }
                break;
            case 69:
                //Minus
                if (edExpenseItemDistance[lastSelectedEditText].isFocused()) {
                    Constants.decrementEditTextVal(edExpenseItemDistance[lastSelectedEditText], Constants.Y);
                } else {
                    Constants.decrementEditTextVal(edExpenseItemAmount[lastSelectedAmountEditText], Constants.Y);
                }
                break;
            case 1:
                if (edExpenseItemDistance[lastSelectedEditText].isFocused()) {
                    changeEditTextFocus(0);
                } else {
                    changeAmountEditTextFocus(0);
                }
                break;
            case 2:
                if (edExpenseItemDistance[lastSelectedEditText].isFocused()) {
                    changeEditTextFocus(1);
                } else {
                    changeAmountEditTextFocus(1);
                }
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
    public void changeEditTextFocus(int upDownStatus) {

        if (upDownStatus == 1) {
            int ListSize = expenseBeanList.size() - 1;
            if (lastSelectedEditText != ListSize) {
                if (edExpenseItemDistance[lastSelectedEditText] != null)
                    edExpenseItemDistance[lastSelectedEditText + 1].requestFocus();
            }

        } else {
            if (lastSelectedEditText != 0) {
                if (edExpenseItemDistance[lastSelectedEditText - 1] != null)
                    edExpenseItemDistance[lastSelectedEditText - 1].requestFocus();
            }
        }
    }
    public void changeAmountEditTextFocus(int upDownStatus) {

        if (upDownStatus == 1) {
            int ListSize = expenseBeanList.size() - 1;
            if (lastSelectedEditText != ListSize) {
                if (edExpenseItemAmount[lastSelectedEditText] != null)
                    edExpenseItemAmount[lastSelectedEditText + 1].requestFocus();
            }

        } else {
            if (lastSelectedEditText != 0) {
                if (edExpenseItemAmount[lastSelectedEditText - 1] != null)
                    edExpenseItemAmount[lastSelectedEditText - 1].requestFocus();
            }

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
        keyboard = new Keyboard(getContext(), R.xml.ll_plus_minuus_updown_keyboard);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
}
