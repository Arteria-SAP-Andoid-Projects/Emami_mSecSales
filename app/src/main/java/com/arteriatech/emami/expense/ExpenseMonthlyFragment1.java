package com.arteriatech.emami.expense;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.interfaces.DialogCallBack;
import com.arteriatech.emami.main.MainMenu;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.windowdisplay.SelfDisplay;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.xscript.core.GUID;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ExpenseMonthlyFragment1 extends Fragment implements UIListener, KeyboardView.OnKeyboardActionListener, View.OnClickListener {


    public static KeyboardView keyboardView = null;
    private String expenseFreq = "";
    private TextView tvMobileBill;
    private EditText etOtherExpenses;
    private EditText etRemarks;
    private ArrayList<ExpenseBean> expenseBeanList = new ArrayList<>();
    private String stOtherExpense = "";
    private String stRemarks = "";
    private boolean secondItemImage = true;
    private String[][] mArraySalesPerson = null;
    private Hashtable masterHashTable = new Hashtable();
    private int fiscalYear = 0;
    private String mStrCurrentDate = "";
    private int mDay = 0;
    private int mMonth = 0;
    private ArrayList<HashMap<String, String>> arrItemTable = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> arrImageItemTable = new ArrayList<HashMap<String, String>>();
    private String[][] mArrayExpenseType = null;
    private double finalPhoneAmount = 0;
    private ArrayList<ExpenseImageBean> finalImageBeanList = new ArrayList<>();
    private String TAG = "ExpenseMonthlyFragment1";
    private Keyboard keyboard;
    private String secondTimeErrorMessage = "";
    private TextView tvExpenseDate;
    private int monthConfigs = 0;
    private Calendar mCalendar;
    private LinearLayout llPhotoEdit;
    private EditText etDailyAllowance;
    private String stBillAllowance = "";
    private TextView tvDailyAllowanceMandatory;
    private String[][] mArrayDefaultExpenseAllowance = null;
    private String stAmountCatType = "";
    private String stMaxAllowancePer = "";
    private TextView tvOtherExpense;
    private double finalOtherExpenseAmount = 0.0;
    private TableRow trRemarks;
    private TextView tvOtherExpenseMandatory;
    private TextView tvRemarksMandatory;
    private String isOtherExpenseMandatory = "";
    private String[][] mArrayDefaultOtherExAllowance = null;
    private double finalOtherAmount = 0.0;
    private String stOtherExpenseAmountCategory = "";
    private String stOtherMaxPer = "";
    private LinearLayout llOtherExpense;
    private TextView tvOtherExpenseUOM;

    public ExpenseMonthlyFragment1() {
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
        return inflater.inflate(R.layout.fragment_monthly_exp, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        tvMobileBill = (TextView) view.findViewById(R.id.tv_mobile_bill);
        tvExpenseDate = (TextView) view.findViewById(R.id.tv_expense_date);
        etOtherExpenses = (EditText) view.findViewById(R.id.et_other_expenses);
        etRemarks = (EditText) view.findViewById(R.id.et_remarks);
        llPhotoEdit = (LinearLayout) view.findViewById(R.id.ll_photo_edit);
        etDailyAllowance = (EditText) view.findViewById(R.id.et_daily_allowance);
        tvDailyAllowanceMandatory = (TextView) view.findViewById(R.id.tv_daily_allowance_mandatory);
        tvOtherExpense = (TextView) view.findViewById(R.id.tv_other_expense);
        tvOtherExpenseMandatory = (TextView) view.findViewById(R.id.tv_other_expense_mandatory);
        tvRemarksMandatory = (TextView) view.findViewById(R.id.tv_remarks_mandatory);
        tvOtherExpenseUOM = (TextView) view.findViewById(R.id.tv_other_expense_uom);
        trRemarks = (TableRow) view.findViewById(R.id.tr_remarks);
        llOtherExpense = (LinearLayout) view.findViewById(R.id.ll_other_expense);

        UtilConstants.editTextDecimalFormat(etOtherExpenses, 10, 3);
        final Calendar c = Calendar.getInstance();
        fiscalYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        getSalesPersonValue();
        monthConfigs = getDayMonthConfig();
        initializeKeyboardDependencies(view);
        tvExpenseDate.setOnClickListener(this);
        setDateIntoTextView(mMonth, mDay, fiscalYear);

    }


    private void getSalesPersonValue() {
        try {
            mArraySalesPerson = Constants.getDistributors();
        } catch (Exception e) {
        }
        String query = Constants.ExpenseConfigs + "?$filter=" + Constants.ExpenseFreq + " eq '" + expenseFreq + "' &$top=1";
        try {
            mArrayExpenseType = OfflineManager.getConfigExpenseType(query, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }
    }

    private void openImageFragment() {
        Fragment fragment = new SelfDisplay();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantsUtils.EXTRA_FROM, 1);
        bundle.putBoolean(Constants.EXTRA_SCHEME_IS_SECONDTIME, true);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_item_container, fragment);
        fragmentTransaction.commit();

    }


    private void getExpenseConfigs() {
        String mStrConfigQry = Constants.ExpenseConfigs + "?$filter=" + Constants.ExpenseFreq + " eq '" + expenseFreq + "' and " + Constants.DefaultItemCat +
                " eq '000010' and (ExpenseItemType eq '0000000004' or ExpenseItemType eq '0000000001') &$orderby = ExpenseItemType asc &$top=2";
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
                        expenseBean.setIsSupportDocReq(mArrayDefaultExpenseType[17][defaultExp]);
                        expenseBean.setIsRemarksReq(mArrayDefaultExpenseType[19][defaultExp]);
                        expenseBean.setCurrency(mArrayDefaultExpenseType[18][defaultExp]);
                        expenseBean.setUOM(mArrayDefaultExpenseType[13][defaultExp]);
                        expenseBean.setDefaultItemCat(mArrayDefaultExpenseType[8][defaultExp]);
                        expenseBeanList.add(expenseBean);
                    }
                }
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        displayAndSetValue(expenseBeanList);
    }

    private void displayAndSetValue(ArrayList<ExpenseBean> expenseBeanList) {
        if (!expenseBeanList.isEmpty()) {
            if (expenseBeanList.size() > 1) {
                ExpenseBean otherExpenseBean = expenseBeanList.get(0);
                ExpenseBean mobileExpenseBean = expenseBeanList.get(1);
                displayOthersUI(otherExpenseBean);
                displayMobileUI(mobileExpenseBean);
            }
        }
    }

    private void displayMobileUI(ExpenseBean mobileExpenseBean) {
        stAmountCatType = mobileExpenseBean.getAmountCategory();
        stMaxAllowancePer = mobileExpenseBean.getMaxAllowancePer();
        String query = Constants.ExpenseAllowances + "?$filter= ExpenseItemType eq '0000000004'";
        if (mobileExpenseBean.getAmountCategory().equalsIgnoreCase("000030")) {
            try {

                String[][] mArrayDefaultExpenseAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
                if (mArrayDefaultExpenseAllowance[0].length > 0) {
                    double maxPer = Double.parseDouble(mobileExpenseBean.getMaxAllowancePer());
                    double tempAmount = Double.parseDouble(mArrayDefaultExpenseAllowance[0][0]);
                    double finalAmount = (tempAmount / 100.0f) * maxPer;
                    tvMobileBill.setText(UtilConstants.removeLeadingZerowithTwoDecimal(finalAmount + "") + " " + mArraySalesPerson[10][0]);
                    mobileExpenseBean.setAmount(finalAmount + "");
                    finalPhoneAmount = finalAmount;
                } else {
                    tvMobileBill.setText("");
                    mobileExpenseBean.setAmount("0.0");
                    finalPhoneAmount = 0.0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                tvMobileBill.setText("");
                mobileExpenseBean.setAmount("0.0");
                finalPhoneAmount = 0.0;
            }
            etDailyAllowance.setVisibility(View.GONE);
            tvDailyAllowanceMandatory.setVisibility(View.GONE);
            tvMobileBill.setVisibility(View.VISIBLE);
        } else if (mobileExpenseBean.getAmountCategory().equalsIgnoreCase("000020")) {
            etDailyAllowance.setVisibility(View.VISIBLE);
            tvDailyAllowanceMandatory.setVisibility(View.VISIBLE);
            tvMobileBill.setVisibility(View.GONE);
            try {
                mArrayDefaultExpenseAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
                if (mArrayDefaultExpenseAllowance[0].length > 0) {
                    double enteredValue = Double.parseDouble(stBillAllowance);
                    double maxValuePer = Double.parseDouble(mobileExpenseBean.getMaxAllowancePer());
                    double finalAmount = (enteredValue / 100.0f) * maxValuePer;
                    if (finalAmount > Double.parseDouble(mArrayDefaultExpenseAllowance[0][0])) {
                        finalPhoneAmount = 0.0;
                    } else {
                        finalPhoneAmount = finalAmount;
                    }
                } else {
                    finalPhoneAmount = 0.0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                finalPhoneAmount = 0.0;
            }
            mobileExpenseBean.setAmount(stBillAllowance);

        } else if (mobileExpenseBean.getAmountCategory().equalsIgnoreCase("000010")) {
            etDailyAllowance.setVisibility(View.VISIBLE);
            tvDailyAllowanceMandatory.setVisibility(View.VISIBLE);
            tvMobileBill.setVisibility(View.GONE);
            try {
                finalPhoneAmount = Double.parseDouble(stBillAllowance);
            } catch (Exception e) {
                e.printStackTrace();
                finalPhoneAmount = 0.0;
            }
            mobileExpenseBean.setAmount(stBillAllowance);
        }
    }

    private void displayOthersUI(final ExpenseBean otherExpenseBean) {
        String query = Constants.ExpenseAllowances + "?$filter= ExpenseItemType eq '0000000001' &$top =1";
        isOtherExpenseMandatory = otherExpenseBean.getDefaultItemCat();
        if (isOtherExpenseMandatory.equalsIgnoreCase("000010")) {
            tvOtherExpenseMandatory.setVisibility(View.VISIBLE);
            tvRemarksMandatory.setVisibility(View.VISIBLE);
        } else {
            tvOtherExpenseMandatory.setVisibility(View.GONE);
            tvRemarksMandatory.setVisibility(View.GONE);
        }
        stOtherExpenseAmountCategory = otherExpenseBean.getAmountCategory();
        stOtherMaxPer = otherExpenseBean.getMaxAllowancePer();
        if (otherExpenseBean.getAmountCategory().equalsIgnoreCase("000030")) {
            try {

                String[][] mArrayDefaultExpenseAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
                if (mArrayDefaultExpenseAllowance[0].length > 0) {
                    double maxPer = Double.parseDouble(otherExpenseBean.getMaxAllowancePer());
                    double tempAmount = Double.parseDouble(mArrayDefaultExpenseAllowance[0][0]);
                    double finalAmount = (tempAmount / 100.0f) * maxPer;
                    tvOtherExpense.setText(UtilConstants.removeLeadingZerowithTwoDecimal(finalAmount + "") + " " + mArraySalesPerson[10][0]);
                    otherExpenseBean.setAmount(finalAmount + "");
                    finalOtherExpenseAmount = finalAmount;
                } else {
                    tvOtherExpense.setText("");
                    otherExpenseBean.setAmount("0.0");
                    finalOtherExpenseAmount = 0.0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                tvOtherExpense.setText("");
                otherExpenseBean.setAmount("0.0");
                finalOtherExpenseAmount = 0.0;
            }
            etOtherExpenses.setVisibility(View.GONE);
            llOtherExpense.setVisibility(View.GONE);
            tvOtherExpense.setVisibility(View.VISIBLE);
        } else if (otherExpenseBean.getAmountCategory().equalsIgnoreCase("000020")) {
            llOtherExpense.setVisibility(View.VISIBLE);
            etOtherExpenses.setVisibility(View.VISIBLE);
            tvOtherExpense.setVisibility(View.GONE);
            try {
                mArrayDefaultOtherExAllowance = OfflineManager.getConfigExpenseAllwance(query, "");
                if (mArrayDefaultOtherExAllowance[0].length > 0) {
                    double enteredValue = Double.parseDouble(stOtherExpense);
                    double maxValuePer = Double.parseDouble(otherExpenseBean.getMaxAllowancePer());
                    double finalAmount = (enteredValue / 100.0f) * maxValuePer;
                    if (finalAmount > Double.parseDouble(mArrayDefaultOtherExAllowance[0][0])) {
                        finalOtherAmount = 0.0;
                    } else {
                        finalOtherAmount = finalAmount;
                    }
                } else {
                    finalOtherAmount = 0.0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                finalOtherAmount = 0.0;
            }
            tvOtherExpenseUOM.setText(mArraySalesPerson[10][0]);
            otherExpenseBean.setAmount(stOtherExpense);

        } else if (otherExpenseBean.getAmountCategory().equalsIgnoreCase("000010")) {

            etOtherExpenses.setVisibility(View.VISIBLE);
            llOtherExpense.setVisibility(View.VISIBLE);
            tvOtherExpense.setVisibility(View.GONE);
            try {
                finalOtherAmount = Double.parseDouble(stOtherExpense);
            } catch (Exception e) {
                e.printStackTrace();
                finalOtherAmount = 0.0;
            }
            tvOtherExpenseUOM.setText( mArraySalesPerson[10][0]);
            otherExpenseBean.setAmount(stOtherExpense);
        }
        if (!otherExpenseBean.getIsSupportDocReq().equalsIgnoreCase("")) {
            llPhotoEdit.setVisibility(View.VISIBLE);
        } else {
            llPhotoEdit.setVisibility(View.GONE);
        }
        if (!otherExpenseBean.getIsRemarksReq().equalsIgnoreCase("")) {
            otherExpenseBean.setRemarks(stRemarks);
            trRemarks.setVisibility(View.VISIBLE);
        } else {
            trRemarks.setVisibility(View.GONE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        if (!checkValidationAndShowDialogs()) {
            boolean validation = checkValidations();
            if (validation) {
                saveDataToDataValt(context);
            }
        }
    }

    private boolean checkValidationAndShowDialogs() {
        boolean finalStatus = false;
        secondTimeErrorMessage = "Expense already submited for " + Constants.ORG_MONTHS[mMonth];
        if (ExpenseDailyFragment1.validateAlreadyDataSaved(getContext(), expenseFreq, mStrCurrentDate, mArrayExpenseType[0][1])) {
            Constants.dialogBoxWithButton(getContext(), "", secondTimeErrorMessage, "Ok", "", null);
            finalStatus = true;
        }
        hiddenUI(finalStatus);
        return finalStatus;
    }

    private void hiddenUI(boolean finalStatus) {
        if (finalStatus) {
            etOtherExpenses.setEnabled(false);
            etOtherExpenses.setFocusable(false);
            etDailyAllowance.setEnabled(false);
            etDailyAllowance.setFocusable(false);
            etRemarks.setEnabled(false);
            etRemarks.setFocusable(false);

            llPhotoEdit.setVisibility(View.GONE);

        } else {
            etRemarks.setEnabled(true);
            etRemarks.setFocusable(true);
            etRemarks.setFocusableInTouchMode(true);
            etOtherExpenses.setEnabled(true);
            etOtherExpenses.setFocusable(true);
            etOtherExpenses.setFocusableInTouchMode(true);
            etDailyAllowance.setEnabled(true);
            etDailyAllowance.setFocusable(true);
            etDailyAllowance.setFocusableInTouchMode(true);
            llPhotoEdit.setVisibility(View.VISIBLE);
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
        masterHashTable.put(Constants.ExpenseType, mArrayExpenseType[0][1]);
        masterHashTable.put(Constants.ExpenseTypeDesc, mArrayExpenseType[1][1]);
        masterHashTable.put(Constants.ExpenseDate, mStrCurrentDate);
        masterHashTable.put(Constants.Status, "");
        masterHashTable.put(Constants.StatusDesc, "");
        masterHashTable.put(Constants.Amount, "0.0");
        masterHashTable.put(Constants.Currency, mArraySalesPerson[10][0]);
        int itemIncVal = 0;
        arrItemTable.clear();
        for (ExpenseBean expenseBean : expenseBeanList) {
//            if(!secondItemImage && itemIncVal==0) {

            if (secondItemImage || itemIncVal > 0) {
                HashMap<String, String> singleItem = new HashMap<String, String>();
                GUID itemGuid = GUID.newRandom();
                singleItem.put(Constants.ExpenseItemGUID, itemGuid.toString36().toUpperCase());
                singleItem.put(Constants.ExpenseGUID, guid.toString36().toUpperCase());
                singleItem.put(Constants.ExpeseItemNo, ConstantsUtils.addZeroBeforeValue(itemIncVal + 1, ConstantsUtils.ITEM_MAX_LENGTH));
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
                singleItem.put(Constants.Currency, mArraySalesPerson[10][0]);
                singleItem.put(Constants.Remarks, expenseBean.getRemarks());

//                expenseImageBeanArrayList = expenseBean.getExpenseImageBeanArrayList();
                if (itemIncVal == 0) {
                    if (finalImageBeanList.size() > 0) {
                        for (ExpenseImageBean expenseImageBean : finalImageBeanList) {
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
                        singleItem.put("item_no" + itemIncVal, UtilConstants.convertArrListToGsonString(arrImageItemTable));
                    }

                }
                arrItemTable.add(singleItem);
            }
            itemIncVal++;
        }
        masterHashTable.put(Constants.entityType, Constants.Expenses);
        masterHashTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(arrItemTable));
        Constants.saveDeviceDocNoToSharedPref(getContext(), Constants.Expenses, doc_no);
        JSONObject jsonHeaderObject = new JSONObject(masterHashTable);
        UtilDataVault.storeInDataVault(doc_no, jsonHeaderObject.toString());
        saveDocumentEntityToTable();
        displayCompletedDialogBox(getContext());
    }

    private void saveDocumentEntityToTable() {
        try {
            //noinspection unchecked
            int getNumb = 0;
            for (HashMap<String, String> arrItemTables : arrItemTable) {
                String imageStringArray = arrItemTables.get("item_no" + getNumb);
                if (imageStringArray != null) {
                    ArrayList<HashMap<String, String>> convertedString = UtilConstants.convertToArrayListMap(imageStringArray);

                    for (HashMap<String, String> hashItem : convertedString) {
                        OfflineManager.createExpensesingItem(arrItemTables, hashItem, this);
                    }
                    Log.d(TAG, "onSaveData: " + imageStringArray);
                    Log.d(TAG, "convertedString: " + convertedString);
                }
                getNumb++;
            }

        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void displayCompletedDialogBox(final Context mContext) {
        Constants.dialogBoxWithButton(mContext, "", getString(R.string.expense_monthly_created_success), getString(R.string.ok), "", new DialogCallBack() {
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

    private boolean checkValidations() {
        int isMandetory = 0;
        finalImageBeanList.clear();
        secondItemImage = false;
        if (stAmountCatType.equalsIgnoreCase("000020")) {
            if (TextUtils.isEmpty(stBillAllowance)) {
                isMandetory = 1;
                etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
            } else {
                try {
                    double doubQty = Double.parseDouble(stBillAllowance);
                    double maxValuePer = Double.parseDouble(stMaxAllowancePer);
                    double finalAmount = (doubQty / 100.0f) * maxValuePer;
                    if (finalAmount > Double.parseDouble(mArrayDefaultExpenseAllowance[0][0])) {
                        isMandetory = 4;
                        etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
                    } else {
                        expenseBeanList.get(1).setAmount(finalAmount + "");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    isMandetory = 3;
                    expenseBeanList.get(1).setAmount("0.0");
                    etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
                }
            }

        } else if (stAmountCatType.equalsIgnoreCase("000010")) {
            if (TextUtils.isEmpty(stBillAllowance)) {
                isMandetory = 1;
                etDailyAllowance.setBackgroundResource(R.drawable.edittext_border);
            }
        }
        if (isOtherExpenseMandatory.equalsIgnoreCase("000010")) {
            secondItemImage = true;
            if (stOtherExpenseAmountCategory.equalsIgnoreCase("000020")) {
                if (TextUtils.isEmpty(stOtherExpense)) {
                    isMandetory = 1;
                    etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                } else if(stOtherExpense.equals(".") || stOtherExpense.equals("0")){
                    isMandetory = 1;
                    etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                }else {
                    try {
                        double doubQty = Double.parseDouble(stOtherExpense);
                        double maxValuePer = Double.parseDouble(stOtherMaxPer);
                        double finalAmount = (doubQty / 100.0f) * maxValuePer;
                        if (finalAmount > Double.parseDouble(mArrayDefaultOtherExAllowance[0][0])) {
                            isMandetory = 4;
                            etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                        } else {
                            expenseBeanList.get(0).setAmount(finalAmount + "");
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        isMandetory = 3;
                        expenseBeanList.get(0).setAmount("0.0");
                        etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                    }
                }

            } else if (stOtherExpenseAmountCategory.equalsIgnoreCase("000010")) {
                if (TextUtils.isEmpty(stOtherExpense)) {
                    isMandetory = 1;
                    etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                }
            }
            if (llPhotoEdit.getVisibility() == View.VISIBLE) {
                if (SelfDisplay.imageBeanList.size() < 2) {
                    isMandetory = 5;
                } else {
                    for (ExpenseImageBean expenseImageBean : SelfDisplay.imageBeanList) {
                        if (!expenseImageBean.getImagePath().equals("") && !expenseImageBean.getFileName().equals("") && expenseImageBean.isNewImage())
                            finalImageBeanList.add(expenseImageBean);
                    }
                }
            }
            if (trRemarks.getVisibility() == View.VISIBLE) {
                if (TextUtils.isEmpty(stRemarks)) {
                    isMandetory = 1;
                    etRemarks.setBackgroundResource(R.drawable.edittext_border);
                }
            }
        } else {
            secondItemImage = false;
            if (stOtherExpenseAmountCategory.equalsIgnoreCase("000020")) {
                if (TextUtils.isEmpty(stOtherExpense)) {
                } else {
                    try {
                        double doubQty = Double.parseDouble(stOtherExpense);
                        double maxValuePer = Double.parseDouble(stOtherMaxPer);
                        double finalAmount = (doubQty / 100.0f) * maxValuePer;
                        if (finalAmount > Double.parseDouble(mArrayDefaultOtherExAllowance[0][0])) {
                            isMandetory = 4;
                            etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                        } else {
                            expenseBeanList.get(0).setAmount(finalAmount + "");
                            secondItemImage = true;
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        isMandetory = 3;
                        expenseBeanList.get(0).setAmount("0.0");
                        etOtherExpenses.setBackgroundResource(R.drawable.edittext_border);
                    }
                }

            } else if (stOtherExpenseAmountCategory.equalsIgnoreCase("000010")) {
                if (TextUtils.isEmpty(stOtherExpense)) {
                } else {
                    secondItemImage = true;
                }
            }
            if (llPhotoEdit.getVisibility() == View.VISIBLE) {
                if (SelfDisplay.imageBeanList.size() < 2) {
                } else {
                    for (ExpenseImageBean expenseImageBean : SelfDisplay.imageBeanList) {
                        if (!expenseImageBean.getImagePath().equals("") && !expenseImageBean.getFileName().equals("") && expenseImageBean.isNewImage())
                            finalImageBeanList.add(expenseImageBean);
                    }
                    secondItemImage = true;
                }
            }
            if (trRemarks.getVisibility() == View.VISIBLE) {
                if (TextUtils.isEmpty(stRemarks)) {
                } else {
                    secondItemImage = true;
                }
            }
        }
        String messages = "";
        if (isMandetory == 0) {
            return true;
        } else if (isMandetory == 1) {
            messages = getString(R.string.validation_plz_fill_mandatory_flds);
        } else if (isMandetory == 3) {
            messages = getString(R.string.expense_error_enter_valid_amount);
        } else if (isMandetory == 4) {
            messages = getString(R.string.expense_error_amount);
        } else if (isMandetory == 5) {
            messages = "Please upload atleast one image";
        }
        Constants.dialogBoxWithButton(getContext(), "", messages, getString(R.string.ok), "", null);
        return false;
    }

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
        tvExpenseDate.setText(new StringBuilder().append(mMonth + 1).append("/").append(mYear).append(" "));
        if (!checkValidationAndShowDialogs()) {
            initUI();
            getExpenseConfigs();
            openImageFragment();
        }
    }

    private void initUI() {
        etOtherExpenses.setText("");
        etRemarks.setText("");
        tvMobileBill.setText("");
        UtilConstants.editTextDecimalFormat(etDailyAllowance, 10, 3);
        etDailyAllowance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etDailyAllowance.setBackgroundResource(R.drawable.edittext);
                stBillAllowance = s + "";
                displayAndSetValue(expenseBeanList);
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
        etOtherExpenses.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                stOtherExpense = s + "";
                etOtherExpenses.setBackgroundResource(R.drawable.edittext);
                displayAndSetValue(expenseBeanList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etOtherExpenses.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, getActivity());
                Constants.setCursorPostion(etOtherExpenses,v,event);
                return true;
            }
        });
        etOtherExpenses.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Constants.showCustomKeyboard(v, keyboardView, getActivity());
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        etRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                stRemarks = s + "";
                etRemarks.setBackgroundResource(R.drawable.edittext);
                displayAndSetValue(expenseBeanList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onRequestError(int i, Exception e) {
        Log.d(TAG, "onRequestError: ");
    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
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
                Constants.incrementTextValues(etOtherExpenses, Constants.Y);
                break;
            case 69:
                //Minus
                Constants.decrementEditTextVal(etOtherExpenses, Constants.Y);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_expense_date:
                datePickerDialog();
                break;

        }

    }

    private void datePickerDialog() {
        mCalendar = Calendar.getInstance();
        final View dialogView = View.inflate(getContext(), R.layout.dialog_date_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();

        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);

        datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), null);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.MONTH, -(monthConfigs - 1));
        datePicker.setMinDate(calendar.getTimeInMillis());
        datePicker.setMaxDate(cal.getTimeInMillis());

        LinearLayout ll = (LinearLayout) datePicker.getChildAt(0);
        LinearLayout ll2 = (LinearLayout) ll.getChildAt(0);
        ll2.getChildAt(0).setVisibility(View.GONE);
        alertDialog.setView(dialogView);
        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);

                mCalendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth());
                mMonth = datePicker.getMonth();
                setDateIntoTextView(datePicker.getMonth(), datePicker.getDayOfMonth(), datePicker.getYear());

                alertDialog.dismiss();
            }
        });
        alertDialog.show();

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
}
