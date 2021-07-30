package com.arteriatech.emami.windowdisplay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.ViewPagerTabAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.expense.ExpenseImageBean;
import com.arteriatech.emami.interfaces.DialogCallBack;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.xscript.core.GUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by e10526 on 3/2/2017.
 *
 */

public class WindowDisplayActivity extends AppCompatActivity implements UIListener, View.OnClickListener, KeyboardView.OnKeyboardActionListener {

    private static final int TAKE_PICTURE = 1;
    private static final int TAKE_CONTRACT_FORM = 2;
    private static final String TAG = "WindowDisplayActivity";
    String mStrComingFrom = "";
    Spinner sp_win_display_type;
    // TODO below unused variables next time will use ful
    private EditText editRemraks;
    private String[][] arrWinDispType = null;
    private String[][] arrSchemeCps = null;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrSelWinDispType = "", mStrSelWinDispTypeDesc = "", popUpText = "";
    private String[][] mArrayDistributors;
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    private String defaultCameraPackage = "";
    private String mStrSchemeGUID = "";
    private boolean mBoolHeaderPosted = false;
    private ArrayList<HashMap<String, String>> arrImageItemTable = new ArrayList<HashMap<String, String>>();

    private Button btTakePic;
    private ImageView ivTakePicture;
    private Button btContractForm;
    private TextView tvRemarks;
    private TextView tvPromoCode;
    private String mStrSchemeName = "";
    private boolean mBlIsSecondTime = false;
    private TextView tvRemarksMandatory,tv_other_mandatory;
    private TextView tvWindowSizeMandatory;
    private ArrayList<ExpenseImageBean> contractImageList = new ArrayList<>();
    private ArrayList<ExpenseImageBean> selfImageBeanList = new ArrayList<>();
    private ArrayList<ExpenseImageBean> finalImageBeanList = new ArrayList<>();
    private String[][] arrWinDocType = null;
    private Spinner spSizeType;
    private LinearLayout ll_other_input;
    private String[][] arrWinSizeType = null;
    private String mStrSelWinSizeType = "";
    private String mStrSelWinSizeTypeDesc = "";
    private TextView tvWindowType;
    private Hashtable<String, String> headerTable = new Hashtable<>();
    private String mStrSchemeTypeId = "";
    private EditText edit_window_size_l;
    private EditText edit_window_size_b;
    private EditText edit_window_size_h;
    private EditText edit_other_input;
    private TextView tvWindowSizeL;
    private TextView tvWindowSizeB;
    private TextView tvWindowSizeH;
    private TextView tvWindowSizeType;
    private TextView tv_other_window_display_type,tv_other_input;
    private String mStrInvoiceDate = "";
    private String mStrSchemeId = "";
    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private EditText focusEditText;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerTabAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.title_window_display));

        setContentView(R.layout.activity_window_display);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            mStrSchemeGUID = bundleExtras.getString(Constants.EXTRA_SCHEME_GUID);
            mStrSchemeName = bundleExtras.getString(Constants.EXTRA_SCHEME_NAME);
            mStrSchemeTypeId = bundleExtras.getString(Constants.EXTRA_SCHEME_TYPE_ID);
            mStrInvoiceDate = bundleExtras.getString(Constants.EXTRA_INVOICE_DATE);
            mStrSchemeId = bundleExtras.getString(Constants.EXTRA_SCHEME_ID);
            mBlIsSecondTime = bundleExtras.getBoolean(Constants.EXTRA_SCHEME_IS_SECONDTIME, false);
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!Constants.restartApp(WindowDisplayActivity.this)) {
            initUI();
            initializeKeyboardDependencies();
            tabInitialize();
        }
    }

    /*Initializes UI*/
    void initUI() {
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        TextView tvRetName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView tvUID = (TextView) findViewById(R.id.tv_reatiler_id);

        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetID);

        editRemraks = (EditText) findViewById(R.id.edit_remarks);
        edit_window_size_l = (EditText) findViewById(R.id.edit_window_size_l);
        edit_window_size_b = (EditText) findViewById(R.id.edit_window_size_b);
        edit_window_size_h = (EditText) findViewById(R.id.edit_window_size_h);
        edit_window_size_h = (EditText) findViewById(R.id.edit_window_size_h);
        btTakePic = (Button) findViewById(R.id.btn_take_pic);
        btContractForm = (Button) findViewById(R.id.btn_contract_form);
        ivTakePicture = (ImageView) findViewById(R.id.ivThumbnailPhoto);
        tvPromoCode = (TextView) findViewById(R.id.tv_promo_code);
        tvWindowSizeL = (TextView) findViewById(R.id.tv_window_size_l);
        tvWindowSizeB = (TextView) findViewById(R.id.tv_window_size_b);
        tvWindowSizeH = (TextView) findViewById(R.id.tv_window_size_h);
        tvWindowSizeType = (TextView) findViewById(R.id.tv_window_size_type);
        tvRemarks = (TextView) findViewById(R.id.tv_remarks);
        tvRemarksMandatory = (TextView) findViewById(R.id.tv_remarks_mandatory);
        tv_other_mandatory = (TextView) findViewById(R.id.tv_other_mandatory);
        tvWindowSizeMandatory = (TextView) findViewById(R.id.tv_window_size_mandatory);
        tvWindowType = (TextView) findViewById(R.id.tv_window_type);
        tv_other_window_display_type = (TextView) findViewById(R.id.tv_other_window_display_type);
        tv_other_input = (TextView) findViewById(R.id.tv_other_input);
        edit_other_input = (EditText) findViewById(R.id.edit_other_input);
        edit_other_input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});

        sp_win_display_type = (Spinner) findViewById(R.id.sp_win_display_type);
        spSizeType = (Spinner) findViewById(R.id.sp_win_size_type);
        ll_other_input = (LinearLayout) findViewById(R.id.ll_other_input);

        tvPromoCode.setText(mStrSchemeName);
        if (mBlIsSecondTime) {
            editRemraks.setVisibility(View.GONE);
            ivTakePicture.setVisibility(View.GONE);
            btTakePic.setVisibility(View.GONE);
            edit_window_size_l.setVisibility(View.GONE);
            edit_window_size_b.setVisibility(View.GONE);
            edit_window_size_h.setVisibility(View.GONE);
            tvWindowSizeL.setVisibility(View.VISIBLE);
            tvWindowSizeB.setVisibility(View.VISIBLE);
            tvWindowSizeH.setVisibility(View.VISIBLE);
            tvWindowSizeType.setVisibility(View.VISIBLE);
            tvRemarks.setVisibility(View.VISIBLE);
            tvRemarksMandatory.setVisibility(View.GONE);
            tvWindowSizeMandatory.setVisibility(View.GONE);
            spSizeType.setVisibility(View.GONE);
            sp_win_display_type.setVisibility(View.GONE);
            tvWindowType.setVisibility(View.VISIBLE);
            getRegistrationData();
            getImageFromDb();
            try {
                tvRemarks.setText(arrSchemeCps[5][1]);
                tvWindowSizeType.setText(arrSchemeCps[4][1]);
                tvWindowSizeL.setText(arrSchemeCps[1][1]);
                tvWindowSizeB.setText(arrSchemeCps[2][1]);
                tvWindowSizeH.setText(arrSchemeCps[3][1]);
                tvWindowType.setText(arrSchemeCps[6][1]);

               /* if(arrSchemeCps[6][1].equalsIgnoreCase(Constants.Win_Display_Reg_Type_Other)){
                    ll_other_input.setVisibility(View.VISIBLE);
                    tv_other_input.setVisibility(View.VISIBLE);

                    edit_other_input.setVisibility(View.GONE);
                    tv_other_mandatory.setVisibility(View.GONE);
                }else{
                    ll_other_input.setVisibility(View.GONE);
                }*/

                ll_other_input.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            tv_other_input.setVisibility(View.GONE);

            editRemraks.setVisibility(View.VISIBLE);
            ivTakePicture.setVisibility(View.VISIBLE);
            btTakePic.setVisibility(View.VISIBLE);
            tvRemarks.setVisibility(View.GONE);
            tvRemarksMandatory.setVisibility(View.VISIBLE);
            tvWindowSizeMandatory.setVisibility(View.VISIBLE);
            spSizeType.setVisibility(View.VISIBLE);
            sp_win_display_type.setVisibility(View.VISIBLE);
            tvWindowType.setVisibility(View.GONE);

            edit_window_size_l.setVisibility(View.VISIBLE);
            edit_window_size_b.setVisibility(View.VISIBLE);
            edit_window_size_h.setVisibility(View.VISIBLE);
            tvWindowSizeL.setVisibility(View.GONE);
            tvWindowSizeB.setVisibility(View.GONE);
            tvWindowSizeH.setVisibility(View.GONE);
            tvWindowSizeType.setVisibility(View.GONE);
        }
        btTakePic.setOnClickListener(this);
        btContractForm.setOnClickListener(this);
        defaultCameraPackage = Constants.getCameraPackage(WindowDisplayActivity.this);


        edit_window_size_l.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusEditText = edit_window_size_l;
                    Constants.showCustomKeyboard(v, keyboardView, WindowDisplayActivity.this);
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        edit_window_size_l.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, WindowDisplayActivity.this);
                Constants.setCursorPostion(edit_window_size_l,v,event);
                return true;
            }
        });
        edit_window_size_b.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusEditText = edit_window_size_b;
                    Constants.showCustomKeyboard(v, keyboardView, WindowDisplayActivity.this);
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        edit_window_size_b.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, WindowDisplayActivity.this);
                Constants.setCursorPostion(edit_window_size_b,v,event);
                return true;
            }
        });
        edit_window_size_h.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    focusEditText = edit_window_size_h;
                    Constants.showCustomKeyboard(v, keyboardView, WindowDisplayActivity.this);
                } else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        edit_window_size_h.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, WindowDisplayActivity.this);
                Constants.setCursorPostion(edit_window_size_h,v,event);
                return true;
            }
        });

        getWindowDispType();
        getDistributorValues();
        textChangeListener();
        setValuesToUI();
    }


    /*set empty image path*/
    private ExpenseImageBean getEmptyImage() {
        ExpenseImageBean expenseImageBean = new ExpenseImageBean();
        expenseImageBean.setImagePath("");
        return expenseImageBean;
    }


    private void textChangeListener() {
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

        UtilConstants.editTextDecimalFormat(edit_window_size_l, 3, 0);
        UtilConstants.editTextDecimalFormat(edit_window_size_b, 3, 0);
        UtilConstants.editTextDecimalFormat(edit_window_size_h, 3, 0);
        edit_window_size_l.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit_window_size_l.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_window_size_b.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit_window_size_b.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edit_window_size_h.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edit_window_size_h.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

    private void setValuesToUI() {

        if (arrWinDispType == null) {
            arrWinDispType = new String[5][1];
            arrWinDispType[0][0] = "";
            arrWinDispType[1][0] = Constants.None;
            arrWinDispType[2][0] = "";
            arrWinDispType[3][0] = "";
            arrWinDispType[4][0] = "";
        } else {
            arrWinDispType = Constants.CheckForOtherInConfigValue(arrWinDispType);
        }

        ArrayAdapter<String> arrAdpWinDisTypeVal = new ArrayAdapter<>(this, R.layout.custom_textview, arrWinDispType[1]);
        arrAdpWinDisTypeVal.setDropDownViewResource(R.layout.spinnerinside);
        sp_win_display_type.setAdapter(arrAdpWinDisTypeVal);
        sp_win_display_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                mStrSelWinDispType = arrWinDispType[0][position];
                mStrSelWinDispTypeDesc = arrWinDispType[1][position];
                sp_win_display_type.setBackgroundResource(R.drawable.spinner_bg);

                if(mStrSelWinDispType.equalsIgnoreCase(Constants.Win_Display_Reg_Type_Other)){
                    edit_other_input.setText("");
                    tv_other_window_display_type.setText(mStrSelWinDispTypeDesc);
                    ll_other_input.setVisibility(View.VISIBLE);
                }else{
                    ll_other_input.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        if (arrWinSizeType == null) {
            arrWinSizeType = new String[5][1];
            arrWinSizeType[0][0] = "";
            arrWinSizeType[1][0] = Constants.None;
            arrWinSizeType[2][0] = "";
            arrWinSizeType[3][0] = "";
            arrWinSizeType[4][0] = "";
        }

        ArrayAdapter<String> arrAdpWinSizeTypeVal = new ArrayAdapter<>(this, R.layout.custom_textview, arrWinSizeType[1]);
        arrAdpWinDisTypeVal.setDropDownViewResource(R.layout.spinnerinside);
        spSizeType.setAdapter(arrAdpWinSizeTypeVal);
        spSizeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                mStrSelWinSizeTypeDesc = arrWinSizeType[1][position];
                mStrSelWinSizeType = arrWinSizeType[0][position];
                spSizeType.setBackgroundResource(R.drawable.spinner_bg);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


    }


    /*Gets feedback Types from value helps*/
    private void getWindowDispType() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.SchemeCPDocType + "'";
            arrWinDocType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.WindowSizeUOM + "'";
            arrWinSizeType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + ConstantsUtils.RegistrationType + "' and " + Constants.ParentID + " eq '" + mStrSchemeTypeId + "' &$orderby = Description asc";
            arrWinDispType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void getRegistrationData() {
        try {
            String mStrConfigQry = Constants.SchemeCPs + "?$filter= SchemeGUID eq guid'" + mStrSchemeGUID + "' and CPGUID eq '" + mStrBundleCPGUID32.toUpperCase() + "' &$top=1";
            arrSchemeCps = OfflineManager.getArraySchemeCPs(mStrConfigQry, mStrSchemeGUID, mStrBundleCPGUID32);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void getImageFromDb() {
        try {
            contractImageList.clear();
            String mStrConfigQry = Constants.SchemeCPDocuments + "?$filter= SchemeCPGUID eq guid'" + arrSchemeCps[0][1] + "'";//Constants.isLocalFilterQry+ ;
            contractImageList = OfflineManager.getSchemeCPDocuments(mStrConfigQry, contractImageList);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            selfImageBeanList.clear();
            if (!TextUtils.isEmpty(arrSchemeCps[7][1])) {
                String mStrConfigQry = Constants.ClaimDocuments + "?$filter=ClaimGUID eq guid'" + arrSchemeCps[7][1] + "'";//Constants.isLocalFilterQry+ ;
                selfImageBeanList = OfflineManager.getSchemeCPDocuments(mStrConfigQry, selfImageBeanList);
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_window_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_window_save:
                onSave();

                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_take_pic:
                Intent intentResult = new Intent(Constants.CameraPackage);
                intentResult.setPackage(defaultCameraPackage);
                startActivityForResult(intentResult, TAKE_PICTURE);
                break;

            case R.id.btn_contract_form:
                Intent intentResultForm = new Intent(Constants.CameraPackage);
                intentResultForm.setPackage(defaultCameraPackage);
                startActivityForResult(intentResultForm, TAKE_CONTRACT_FORM);
                break;
            default:
                break;
        }
    }



    @Override
    public void onBackPressed() {
        if (Constants.isCustomKeyboardVisible(keyboardView)) {
            Constants.hideCustomKeyboard(keyboardView);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(WindowDisplayActivity.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_window_display).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }

                    });
            builder.show();
        }

    }

    /*Validating Data*/
    private boolean validateData() {

        boolean hasError = true;

        if (!mBlIsSecondTime) {
            if (edit_window_size_l.getText() == null || edit_window_size_l.getText().toString().trim().equalsIgnoreCase("") || edit_window_size_l.getText().toString().trim().equalsIgnoreCase(".")) {
                edit_window_size_l.setBackgroundResource(R.drawable.edittext_border);
                hasError = false;
            }
            if (edit_window_size_b.getText() == null || edit_window_size_b.getText().toString().trim().equalsIgnoreCase("") || edit_window_size_b.getText().toString().trim().equalsIgnoreCase(".")) {
                edit_window_size_b.setBackgroundResource(R.drawable.edittext_border);
                hasError = false;
            }
            if (edit_window_size_h.getText() == null || edit_window_size_h.getText().toString().trim().equalsIgnoreCase("") || edit_window_size_h.getText().toString().trim().equalsIgnoreCase(".")) {
                edit_window_size_h.setBackgroundResource(R.drawable.edittext_border);
                hasError = false;
            }

            if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                // error
                editRemraks.setBackgroundResource(R.drawable.edittext_border);
                hasError = false;
            }
            if (mStrSelWinSizeType.equalsIgnoreCase("") || mStrSelWinSizeType.equalsIgnoreCase(Constants.None)) {
                // error
                spSizeType.setBackgroundResource(R.drawable.error_spinner);
                hasError = false;
            }
            if (mStrSelWinDispType.equalsIgnoreCase("") || mStrSelWinDispType.equalsIgnoreCase(Constants.None)) {
                // error
                sp_win_display_type.setBackgroundResource(R.drawable.error_spinner);
                hasError = false;
            }

            if(!mStrSelWinDispType.equalsIgnoreCase("")){
                if(mStrSelWinDispType.equalsIgnoreCase(Constants.Win_Display_Reg_Type_Other)){
                    if (edit_other_input.getText() == null || edit_other_input.getText().toString().trim().equalsIgnoreCase("")) {
                        // error
                        edit_other_input.setBackgroundResource(R.drawable.edittext_border);
                        hasError = false;
                    }
                }
            }

            if (ContractFragment.imageBeanList.size() < 2) {
                hasError = false;
            } else {
                finalImageBeanList.clear();
                for (ExpenseImageBean expenseImageBean : ContractFragment.imageBeanList) {
                    if (!expenseImageBean.getImagePath().equals("") && !expenseImageBean.getFileName().equals("") && expenseImageBean.isNewImage())
                        finalImageBeanList.add(expenseImageBean);
                }
            }

        } else {
            if (SelfDisplay.imageBeanList.size() < 2) {
                hasError = false;
            } else {
                finalImageBeanList.clear();
                for (ExpenseImageBean expenseImageBean : SelfDisplay.imageBeanList) {
                    if (!expenseImageBean.getImagePath().equals("") && !expenseImageBean.getFileName().equals("") && expenseImageBean.isNewImage())
                        finalImageBeanList.add(expenseImageBean);
                }
            }
        }

        if (finalImageBeanList.size() < 1) {
            hasError = false;
        }
        return hasError;
    }

    /*Saves window display values into database*/
    private void onSave() {

//        if (Constants.isValidTime(UtilConstants.convertTimeOnly(Constants.mStartTimeDuration.toString()),
//                UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString())) && Constants.isValidDate()) {

        if (validateData()) {
            /*if (!Constants.onGpsCheck(WindowDisplayActivity.this)) {
                return;
            }

            if(!UtilConstants.getLocation(WindowDisplayActivity.this)){
                return;
            }*/
            pdLoadDialog = Constants.showProgressDialog(WindowDisplayActivity.this, "", getString(R.string.checking_pemission));
            LocationUtils.checkLocationPermission(WindowDisplayActivity.this, new LocationInterface() {
                @Override
                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                    closingProgressDialog();
                    if (status) {
                        locationPerGranted();
                    }
                }
            });

        } else {
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
        }

//        } else {
//            UtilConstants.showAlert(getString(R.string.validation_app_time_incorrect), WindowDisplayActivity.this);
//        }

    }

    private ProgressDialog pdLoadDialog=null;
    private void closingProgressDialog(){
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void locationPerGranted(){
        pdLoadDialog = Constants.showProgressDialog(WindowDisplayActivity.this,"",getString(R.string.gps_progress));
        Constants.getLocation(WindowDisplayActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closingProgressDialog();
                if(status){
                    onSaveWinDisplay();
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
                    LocationUtils.checkLocationPermission(WindowDisplayActivity.this, new LocationInterface() {
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
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if(requestCode==LocationUtils.REQUEST_CHECK_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                locationPerGranted();
            }
        }
    }*/
    private void onSaveWinDisplay(){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");
        if (!mBlIsSecondTime) {
            GUID guid = GUID.newRandom();
            String windowL = edit_window_size_l.getText().toString();
            String windowB = edit_window_size_b.getText().toString();
            String windowH = edit_window_size_h.getText().toString();
            String remarks = editRemraks.getText().toString();

            Hashtable hashTableWindowDisplay = new Hashtable();

            hashTableWindowDisplay.put(Constants.SchemeCPGUID, guid.toString());
            hashTableWindowDisplay.put(Constants.SchemeGUID, mStrSchemeGUID);
            hashTableWindowDisplay.put(Constants.CPTypeID, Constants.str_02);
            hashTableWindowDisplay.put(Constants.CPTypeDesc, mArrayDistributors[9][0]);
            hashTableWindowDisplay.put(Constants.CPGUID, mStrBundleCPGUID32.toUpperCase());
            hashTableWindowDisplay.put(Constants.CPNo, UtilConstants.removeLeadingZeros(mStrBundleRetID));
            hashTableWindowDisplay.put(Constants.CPName, "");
            hashTableWindowDisplay.put(Constants.IsExcluded, "");
            hashTableWindowDisplay.put(Constants.WindowLength, windowL);
            hashTableWindowDisplay.put(Constants.WindowBreadth, windowB);
            hashTableWindowDisplay.put(Constants.WindowHeight, windowH);
            hashTableWindowDisplay.put(Constants.WindowSizeUOM, mStrSelWinSizeType);
            hashTableWindowDisplay.put(Constants.Remarks, remarks);
            hashTableWindowDisplay.put(Constants.IsExcluded, "");
            hashTableWindowDisplay.put(ConstantsUtils.RegistrationTypeID, mStrSelWinDispType);
            hashTableWindowDisplay.put(ConstantsUtils.RegistrationTypeDesc, mStrSelWinDispTypeDesc);
            hashTableWindowDisplay.put(Constants.SetResourcePath, "guid'" + guid.toString() + "'");
            hashTableWindowDisplay.put(ConstantsUtils.RegistrationDate, UtilConstants.getNewDateTimeFormat());
            hashTableWindowDisplay.put(ConstantsUtils.EnrollmentDate, ConstantsUtils.convertDateFromString(mStrInvoiceDate));

            addMultipleImages(finalImageBeanList, guid.toString(), arrWinDocType[0][1], remarks);

            try {
                OfflineManager.createSchemeCPs(hashTableWindowDisplay, this);
                Constants.onVisitActivityUpdate(mStrBundleCPGUID32, loginIdVal,
                        guid.toString36().toUpperCase(), Constants.WindowDisplayID, Constants.WindowDisplayValueHelp);

            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        } else if (TextUtils.isEmpty(arrSchemeCps[7][1])) {
            GUID sdGUID = GUID.newRandom();

            headerTable.clear();
            headerTable.put(ConstantsUtils.ClaimGUID, sdGUID.toString36().toUpperCase());
            headerTable.put(ConstantsUtils.ClaimDate, UtilConstants.getNewDate());
            headerTable.put(Constants.SchemeGUID, mStrSchemeGUID);
            headerTable.put(Constants.CPTypeID, Constants.str_02);
            headerTable.put(Constants.CPTypeDesc, mArrayDistributors[9][0]);
            headerTable.put(Constants.CPGUID, mStrBundleCPGUID32.toUpperCase());
            headerTable.put(ConstantsUtils.SchemeNo, mStrSchemeId);


            try {
                addMultipleImages(finalImageBeanList, sdGUID.toString(), ConstantsUtils.ZDMS_SCCLM, arrSchemeCps[5][1]);
                OfflineManager.createClaimHeader(headerTable, this);
                Constants.onVisitActivityUpdate(mStrBundleCPGUID32, loginIdVal,
                        sdGUID.toString36().toUpperCase(), Constants.WindowDisplayClaimID, Constants.WindowDisplayValueHelp);
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }


        } else {
                /*visit activity started*/
            String claimId = arrSchemeCps[7][1];


            addMultipleImages(finalImageBeanList, claimId, ConstantsUtils.ZDMS_SCCLM, arrSchemeCps[5][1]);
            mBoolHeaderPosted = true;
            saveImagesToDB();
            Constants.onVisitActivityUpdate(mStrBundleCPGUID32, loginIdVal,
                    claimId, Constants.WindowDisplayClaimID, Constants.WindowDisplayValueHelp);


        }
    }

    private void addMultipleImages(ArrayList<ExpenseImageBean> finalImageBeanList, String sdGUID, String docTypeId, String remarks) {
        arrImageItemTable.clear();
        for (ExpenseImageBean pictureImageBean : finalImageBeanList) {
            GUID imgGuid = GUID.newRandom();
            HashMap<String, String> schemeDocumentHashTable = new HashMap<>();
            if (!mBlIsSecondTime) {
                schemeDocumentHashTable.put(Constants.SchemeCPDocumentID, imgGuid.toString());
                schemeDocumentHashTable.put(Constants.SchemeCPGUID, sdGUID);
            }else {
                schemeDocumentHashTable.put(ConstantsUtils.ClaimDocumentID, imgGuid.toString());
                schemeDocumentHashTable.put(ConstantsUtils.ClaimGUID, sdGUID);
            }
            schemeDocumentHashTable.put(Constants.DocumentStore, Constants.A);
            schemeDocumentHashTable.put(Constants.DocumentTypeID, docTypeId);
            schemeDocumentHashTable.put(Constants.FileName, pictureImageBean.getFileName() + "." + pictureImageBean.getImageExtensions());
            schemeDocumentHashTable.put(Constants.DocumentMimeType, pictureImageBean.getDocumentMimeType());
            schemeDocumentHashTable.put(Constants.DocumentSize, pictureImageBean.getDocumentSize());
            schemeDocumentHashTable.put(Constants.ImagePath, pictureImageBean.getImagePath());
            schemeDocumentHashTable.put(Constants.Remarks, remarks);
            arrImageItemTable.add(schemeDocumentHashTable);
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        Log.d(TAG, "onRequestError: " + e);
    }

    @Override
    public void onRequestSuccess(int operation, String key) {
        if (!mBlIsSecondTime) {
            Log.d(TAG, "onRequestSuccess: ");
            if (operation == Operation.Create.getValue() && mBoolHeaderPosted) {
                Log.d(TAG, "Document posted Success: ");
//            backToVisit();
            } else if (operation == Operation.Create.getValue() && !mBoolHeaderPosted) {
                mBoolHeaderPosted = true;
                saveImagesToDB();
            }
        } else {
            Log.d(TAG, "onRequestSuccess: ");
            if (operation == Operation.Create.getValue() && mBoolHeaderPosted) {
                Log.d(TAG, "Document posted Success: ");
            } else if (operation == Operation.Create.getValue() && !mBoolHeaderPosted) {
                mBoolHeaderPosted = true;
                saveImagesToDB();
            }
        }

    }

    private void saveImagesToDB() {
        mBoolHeaderPosted = true;
        if (!arrImageItemTable.isEmpty()) {
            if (!mBlIsSecondTime) {
                for (HashMap<String, String> schemeDocumentHashTable : arrImageItemTable) {
                    try {
                        OfflineManager.createSchemeCPDocument(schemeDocumentHashTable, this);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (HashMap<String, String> schemeDocumentHashTable : arrImageItemTable) {
                    try {
                        OfflineManager.createClaimDocuments(schemeDocumentHashTable, this);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        String message = "";
        if (mBlIsSecondTime) {
            message = getString(R.string.window_display_second_created_success);
        } else {
            message = getString(R.string.window_display_created_success);
        }
        finalDialogBox(message);

    }

    private void finalDialogBox(String message) {
        Constants.dialogBoxWithButton(WindowDisplayActivity.this, "", message, getString(R.string.ok), "", new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                if (clickedStatus) {
                    navigateToRetDetailsActivity();
                }
            }
        });
    }

    /**
     * get distributor value
     */
    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
    }

    private void navigateToRetDetailsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(WindowDisplayActivity.this, RetailersDetailsActivity.class);
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
        startActivity(intentNavPrevScreen);
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
                Constants.incrementTextValues(focusEditText, Constants.N);
                break;
            case 69:
                //Minus
                Constants.decrementEditTextVal(focusEditText, Constants.N);
                break;
            case 1:
                changeCursor(0, focusEditText);
                break;
            case 2:
                changeCursor(1, focusEditText);
                break;
            case 56:
                KeyEvent event = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(event);
                break;

            default:
                //default numbers
                KeyEvent events = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
                dispatchKeyEvent(events);
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

    private void changeCursor(int fromTop, EditText selectedEditText) {
        switch (selectedEditText.getId()) {
            case R.id.edit_window_size_l:
                if (fromTop == 1) {
                    edit_window_size_b.requestFocus();
                }
                break;
            case R.id.edit_window_size_b:
                if (fromTop == 1) {
                    edit_window_size_h.requestFocus();
                } else {
                    edit_window_size_l.requestFocus();
                }
                break;
            case R.id.edit_window_size_h:
                if (fromTop == 1) {
                } else {
                    edit_window_size_b.requestFocus();
                }
                break;
        }
    }

    public void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(WindowDisplayActivity.this, R.xml.ll_plus_minuus_updown_keyboard);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /*Initialize tab for collections*/
    private void tabInitialize() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager, !mBlIsSecondTime);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager, boolean isFirstTimes) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
//
        Fragment contractFragment = new ContractFragment();
        Fragment claimsImage = new SelfDisplay();

        Bundle contractBundle = new Bundle();
        contractBundle.putSerializable(Constants.EXTRA_ARRAY_LIST, contractImageList);
        contractBundle.putBoolean(Constants.EXTRA_SCHEME_IS_SECONDTIME, isFirstTimes);
        contractFragment.setArguments(contractBundle);
        Bundle claimsBundle = new Bundle();
        claimsBundle.putSerializable(Constants.EXTRA_ARRAY_LIST, selfImageBeanList);
        claimsBundle.putBoolean(Constants.EXTRA_SCHEME_IS_SECONDTIME, isFirstTimes);
        if (isFirstTimes) {
            isFirstTimes = false;
        } else {
            isFirstTimes = true;
        }
        claimsBundle.putBoolean(Constants.EXTRA_SCHEME_IS_SECONDTIME, isFirstTimes);
        claimsImage.setArguments(claimsBundle);

        viewPagerAdapter.addFrag(contractFragment, getString(R.string.lbl_contract_form));
        viewPagerAdapter.addFrag(claimsImage, getString(R.string.window_claims));
        viewPager.setAdapter(viewPagerAdapter);

    }
}
