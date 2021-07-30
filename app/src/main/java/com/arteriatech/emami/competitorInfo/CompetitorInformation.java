package com.arteriatech.emami.competitorInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.xscript.core.GUID;

import java.util.Calendar;
import java.util.Hashtable;


public class CompetitorInformation extends AppCompatActivity implements UIListener, KeyboardView.OnKeyboardActionListener {

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "",mStrBundleCPGUID32="";
    private String mStrBundleRetailerUID = "",mStrComingFrom="";
    private EditText etProductName,etMRP,etRetailerMargin,etSchemeDetails,etLandingprice,etWholesaleLandingRate,
            etConsumerOffer,etTradeOffer,etShelfLife,etOtherInformation,edit_other_comp_name;
    private Spinner spSchemeLaunched,spCompetitorName;
    private String[] mArraySchemeLaunchValues;
    private LinearLayout ltSchemeRemarks,ll_other_comp_name;

    private String[][] mArrayCompNames;
    private String[][]  mArraySPValues = null;
    private String mStrCompName ="";
    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private EditText focusEditText;
    private String mStrVisitActRefID ="",LoginVal = "";
    private String[][] mArrayDistributors = null;
    private ProgressDialog pdLoadDialog=null;
    private MenuItem menuSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_competitor_information);
        ActionBarView.initActionBarView(this,true,getString(R.string.title_competitor_information));
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null)
        {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32= bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }
        if (!Constants.restartApp(CompetitorInformation.this)) {
            initUI();
            getDistributorValues();
            getCompetitorName();
            getSalesPersonValues();
            initializeKeyboardDependencies();
        }
    }

    /**
     * get salesPerson values
     */
    private void getSalesPersonValues() {
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(mStrBundleCPGUID);
    }
    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
    }

    private void getCompetitorName()
    {
        try {
            mArrayCompNames = OfflineManager.getCompetitorNames(Constants.CompetitorMasters);
            if(mArrayCompNames==null)
            {
                mArrayCompNames = new String[2][1];
                mArrayCompNames[0][0] = Constants.None;
                mArrayCompNames[1][0] = Constants.None;
            }
            else
            {
                mArrayCompNames = Constants.CheckForOtherInConfigValue(mArrayCompNames);
                ArrayAdapter<String> competitorAdapter = new ArrayAdapter<>(this,
                        R.layout.custom_textview, mArrayCompNames[1]);
                competitorAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spCompetitorName.setAdapter(competitorAdapter);
            }

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_competitor_info_create, menu);
        menuSave = menu.findItem(R.id.menu_collection_save);


        return true;
    }
    private void initUI()
    {
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        TextView etMrpCur = (TextView)findViewById(R.id.tv_mrp_currency);
        TextView etLandPriceCur = (TextView)findViewById(R.id.tv_landing_price_currency);
        TextView etWholeSalePriceCur = (TextView)findViewById(R.id.tv_wholesale_landing_rate_currency);
        etMrpCur.setText(Constants.getCurrency());
        etLandPriceCur.setText(Constants.getCurrency());
        etWholeSalePriceCur.setText(Constants.getCurrency());

        ltSchemeRemarks = (LinearLayout)findViewById(R.id.ll_scheme_remarks);
        ll_other_comp_name= (LinearLayout)findViewById(R.id.ll_other_comp_name);
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);
        edit_other_comp_name= (EditText)findViewById(R.id.edit_other_comp_name);
        spCompetitorName = (Spinner)findViewById(R.id.sp_competitor_name);
        etProductName = (EditText)findViewById(R.id.edit_product_name);
        etMRP = (EditText)findViewById(R.id.edit_mrp);
        etRetailerMargin = (EditText)findViewById(R.id.edit_retailer_margin);
        etSchemeDetails = (EditText)findViewById(R.id.edit_scheme_details);
        etLandingprice = (EditText)findViewById(R.id.edit_retailer_landing_rate);
        etWholesaleLandingRate = (EditText)findViewById(R.id.edit_wholesaler_landing_rate);
        etConsumerOffer = (EditText)findViewById(R.id.edit_consumer_offer);
        etTradeOffer = (EditText)findViewById(R.id.edit_trade_offer);
        etShelfLife = (EditText)findViewById(R.id.edit_shelf_life);
        etOtherInformation = (EditText)findViewById(R.id.edit_other_information);


        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();
        UtilConstants.editTextDecimalFormat(etMRP,13,2);
        UtilConstants.editTextDecimalFormat(etLandingprice,13,2);
        UtilConstants.editTextDecimalFormat(etWholesaleLandingRate,13,2);
        UtilConstants.editTextDecimalFormat(etRetailerMargin,3,2);
        resetEditTextBackground(etProductName);
        resetEditTextBackground(etMRP);
        resetEditTextBackground(etLandingprice);
        resetEditTextBackground(etWholesaleLandingRate);
        resetEditTextBackground(etRetailerMargin);
        resetEditTextBackground(etSchemeDetails);
        resetEditTextBackground(edit_other_comp_name);

        spSchemeLaunched = (Spinner)findViewById(R.id.sp_scheme_launched);
        mArraySchemeLaunchValues = new String[3];
        mArraySchemeLaunchValues[0] = Constants.None;
        mArraySchemeLaunchValues[1] = getString(R.string.yes);
        mArraySchemeLaunchValues[2] = getString(R.string.no);
        ArrayAdapter<String> schemeLaunchAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArraySchemeLaunchValues);
        schemeLaunchAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spSchemeLaunched.setAdapter(schemeLaunchAdapter);
        spSchemeLaunched.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1)
                {
                    ltSchemeRemarks.setVisibility(View.VISIBLE);
                }
                else
                {
                    ltSchemeRemarks.setVisibility(View.GONE);
                }
                if(position!=0)
                    spSchemeLaunched.setBackgroundResource(R.drawable.spinner_bg);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spCompetitorName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!mArrayCompNames[1][position].equalsIgnoreCase(Constants.None))
                {
                  spCompetitorName.setBackgroundResource(R.drawable.spinner_bg);
                }

                mStrCompName = mArrayCompNames[1][position];

                if(mStrCompName.equalsIgnoreCase(Constants.Others)){
                    edit_other_comp_name.setBackgroundResource(R.drawable.edittext);
                    edit_other_comp_name.setText("");
                    ll_other_comp_name.setVisibility(View.VISIBLE);
                }else{
                    ll_other_comp_name.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        etMRP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusEditText=etMRP;
                    Constants.showCustomKeyboard(v, keyboardView, CompetitorInformation.this);
                }else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        etMRP.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, CompetitorInformation.this);
                Constants.setCursorPostion(etMRP,v,event);
                return true;
            }
        });
        etRetailerMargin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusEditText=etRetailerMargin;
                    Constants.showCustomKeyboard(v, keyboardView, CompetitorInformation.this);
                }else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        etRetailerMargin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, CompetitorInformation.this);
                Constants.setCursorPostion(etRetailerMargin,v,event);
                return true;
            }
        });
        etLandingprice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusEditText=etLandingprice;
                    Constants.showCustomKeyboard(v, keyboardView, CompetitorInformation.this);
                }else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        etLandingprice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, CompetitorInformation.this);
                Constants.setCursorPostion(etLandingprice,v,event);
                return true;
            }
        });
        etWholesaleLandingRate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusEditText=etWholesaleLandingRate;
                    Constants.showCustomKeyboard(v, keyboardView, CompetitorInformation.this);
                }else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        etWholesaleLandingRate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, CompetitorInformation.this);
                Constants.setCursorPostion(etWholesaleLandingRate,v,event);
                return true;
            }
        });
        etShelfLife.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    focusEditText=etShelfLife;
                    Constants.showCustomKeyboard(v, keyboardView, CompetitorInformation.this);
                }else {
                    Constants.hideCustomKeyboard(keyboardView);
                }
            }
        });
        etShelfLife.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocus();
                Constants.showCustomKeyboard(v, keyboardView, CompetitorInformation.this);
                Constants.setCursorPostion(etShelfLife,v,event);
                return true;
            }
        });


    }
    private  void resetEditTextBackground(final EditText selEditText)
    {
        selEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                selEditText.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_collection_save:
                menuSave.setEnabled(false);
                onSave();
                break;
            case android.R.id.home:
                 onBackPressed();
                break;
        }
        return true;
    }

    private void onSave()
    {
//        if(Constants.isValidTime( UtilConstants.convertTimeOnly(Constants.mStartTimeDuration.toString()),
//                UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString())) && Constants.isValidDate()){
        if(isValidationSuccess())
        {
            /*if (!Constants.onGpsCheck(CompetitorInformation.this)) {
                return;
            }
            if (!UtilConstants.getLocation(CompetitorInformation.this)) {
                return;
            }*/

            String mrpTemp  = etMRP.getText().toString();
            float marginPercentage  = Float.parseFloat(etRetailerMargin.getText().toString());
           if(mrpTemp.equals("0.0")||mrpTemp.equals("0.00") ||mrpTemp.equals("0") ||mrpTemp.equals("0.") )
           {
               Constants.customAlertMessage(this,getString(R.string.validation_plz_enter_valid_mrp));
               etMRP.setBackgroundResource(R.drawable.edittext_border);
               menuSave.setEnabled(true);
           }
           else if(marginPercentage>100)
           {
               Constants.customAlertMessage(this,getString(R.string.validation_plz_enter_valid_margin));
               etRetailerMargin.setBackgroundResource(R.drawable.edittext_border);
               menuSave.setEnabled(true);
           }
            else
           {
//               createCompetitorInformation();
               pdLoadDialog = Constants.showProgressDialog(CompetitorInformation.this, "", getString(R.string.checking_pemission));
               LocationUtils.checkLocationPermission(CompetitorInformation.this, new LocationInterface() {
                   @Override
                   public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                       closingProgressDialog();
                       if (status) {
                           locationPerGranted();
                       }else{
                           menuSave.setEnabled(true);
                       }
                   }
               });
           }

        }
        else
        {

            Constants.customAlertMessage(this,getString(R.string.validation_plz_enter_mandatory_flds));
            menuSave.setEnabled(true);
        }

//        }else{
//            UtilConstants.showAlert(getString(R.string.validation_app_time_incorrect), CompetitorInformation.this);
//        }
    }

    private void closingProgressDialog(){
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void locationPerGranted(){
        pdLoadDialog = Constants.showProgressDialog(CompetitorInformation.this,"",getString(R.string.gps_progress));
        Constants.getLocation(CompetitorInformation.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closingProgressDialog();
                if(status){
                    createCompetitorInformation();
                }else{
                    menuSave.setEnabled(true);
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
                    LocationUtils.checkLocationPermission(CompetitorInformation.this, new LocationInterface() {
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

        // Check which request we're responding to
        if(requestCode==LocationUtils.REQUEST_CHECK_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                locationPerGranted();
            }
        }
    }

    private void createCompetitorInformation()
    {
//        GUID guid = GUID.newRandom();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        String loginIdVal = sharedPreferences.getString(Constants.username, "");
        LoginVal= loginIdVal;

        Hashtable<String, String> singleItem = new Hashtable<>();
        GUID guidItem = GUID.newRandom();
        mStrVisitActRefID = guidItem.toString36().toUpperCase();
        singleItem.put(Constants.CompInfoGUID, guidItem.toString36().toUpperCase());
        singleItem.put(Constants.CPTypeID, Constants.str_02);
        singleItem.put(Constants.CPGUID, mStrBundleCPGUID32);
        singleItem.put(Constants.SPGUID, mArraySPValues[4][0].toUpperCase());
        if(!mStrCompName.equalsIgnoreCase(Constants.Others)) {
            singleItem.put(Constants.CompName, mArrayCompNames[1][spCompetitorName.getSelectedItemPosition()]);
        }else{
            singleItem.put(Constants.CompName, edit_other_comp_name.getText().toString().trim());
        }
        singleItem.put(Constants.CompGUID, mArrayCompNames[0][spCompetitorName.getSelectedItemPosition()]);
        singleItem.put(Constants.MatGrp1Amount,"");
        singleItem.put(Constants.MatGrp2Amount,"");
        singleItem.put(Constants.MatGrp3Amount,"");

        singleItem.put(Constants.MatGrp4Amount,"");
        singleItem.put(Constants.Earnings, "1");
        if(mArraySchemeLaunchValues[spSchemeLaunched.getSelectedItemPosition()].equalsIgnoreCase(getString(R.string.yes))){
            singleItem.put(Constants.SchemeName,etSchemeDetails.getText().toString());
        }else{
            singleItem.put(Constants.SchemeName,"");
        }


        singleItem.put(Constants.UpdatedOn, UtilConstants.getNewDateTimeFormat());

        if(etMRP.getText().toString().equalsIgnoreCase(""))
            singleItem.put(Constants.MRP,"0.0");
        else
            singleItem.put(Constants.MRP,etMRP.getText().toString());
        singleItem.put(Constants.MaterialDesc,etProductName.getText().toString());
        if(etRetailerMargin.getText().toString().equalsIgnoreCase(""))
            singleItem.put(Constants.Margin,"0.0");
        else
            singleItem.put(Constants.Margin,etRetailerMargin.getText().toString());
        if(etLandingprice.getText().toString().equalsIgnoreCase(""))
            singleItem.put(Constants.LandingPrice,"0.0");
        else
            singleItem.put(Constants.LandingPrice,etLandingprice.getText().toString());
        if(etWholesaleLandingRate.getText().toString().equalsIgnoreCase(""))
            singleItem.put(Constants.WholeSalesLandingPrice,"0.0");
        else
             singleItem.put(Constants.WholeSalesLandingPrice,etWholesaleLandingRate.getText().toString());
        singleItem.put(Constants.ConsumerOffer,etConsumerOffer.getText().toString().trim());
        singleItem.put(Constants.TradeOffer,etTradeOffer.getText().toString().trim());
        if(etShelfLife.getText().toString().equals(""))
            singleItem.put(Constants.ShelfLife,"0.0");
        else
        singleItem.put(Constants.ShelfLife,etShelfLife.getText().toString());
        singleItem.put(Constants.Remarks,etOtherInformation.getText().toString().trim());
        singleItem.put(Constants.Currency, mArrayDistributors[10][0]);
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        month++;
        singleItem.put(Constants.Period,String.valueOf(month));

        singleItem.put(Constants.LOGINID,loginIdVal);
        try {
            //noinspection unchecked
            OfflineManager.createCompetitorInfo(singleItem, CompetitorInformation.this);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error+" : " + e.getMessage());
        }
    }

    private boolean isValidationSuccess()
    {
        int validCount = 0;
        if(etProductName.getText().toString().trim().equals(""))
        {
            etProductName.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }
        if(mStrCompName.equalsIgnoreCase(Constants.Others)){
            if(edit_other_comp_name.getText().toString().trim().equals(""))
            {
                edit_other_comp_name.setBackgroundResource(R.drawable.edittext_border);
                validCount++;
            }
        }

        if(etMRP.getText().toString().equals(""))
        {
            etMRP.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        } else if(etMRP.getText().toString().equals(".")) {
            etMRP.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }else if (Double.parseDouble(etMRP.getText().toString().equalsIgnoreCase("")?"0":etMRP.getText().toString()) <= 0) {
            etMRP.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }


        if(etRetailerMargin.getText().toString().equals(""))
        {
            etRetailerMargin.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }
        else if(etRetailerMargin.getText().toString().equals("."))
        {
            etRetailerMargin.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }else if (Double.parseDouble(etRetailerMargin.getText().toString().equalsIgnoreCase("")?"0":etRetailerMargin.getText().toString()) <= 0) {
            etRetailerMargin.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }

         if(etLandingprice.getText().toString().equals("."))
        {
            etLandingprice.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }
         if(etWholesaleLandingRate.getText().toString().equals("."))
        {
            etWholesaleLandingRate.setBackgroundResource(R.drawable.edittext_border);
            validCount++;
        }




        if(mArrayCompNames[1][spCompetitorName.getSelectedItemPosition()].equalsIgnoreCase(Constants.None))
        {
            validCount++;
            spCompetitorName.setBackgroundResource(R.drawable.error_spinner);
        }
        if(mArraySchemeLaunchValues[spSchemeLaunched.getSelectedItemPosition()].equalsIgnoreCase(getString(R.string.yes)))
        {
            String schemeRemarks = etSchemeDetails.getText().toString().trim();
            if(schemeRemarks.equalsIgnoreCase(""))
            {
               validCount++;
                etSchemeDetails.setBackgroundResource(R.drawable.edittext_border);
            }
        }
        else if(mArraySchemeLaunchValues[spSchemeLaunched.getSelectedItemPosition()].equalsIgnoreCase(Constants.None))
        {
            validCount++;
           spSchemeLaunched.setBackgroundResource(R.drawable.error_spinner);
        }
        if(validCount>0)
            return false;
        else
            return true;
    }



    @Override
    public void onRequestError(int i, Exception e)
    {

        Constants.customAlertMessage(this,e.getMessage());
        LogManager.writeLogError(Constants.error_in_collection+ e.getMessage());
        menuSave.setEnabled(true);

    }

    @Override
    public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException
    {
        menuSave.setEnabled(true);
        Constants.onVisitActivityUpdate(mStrBundleCPGUID32, LoginVal,
                mStrVisitActRefID, "04", Constants.CompetitorInfos);

        AlertDialog.Builder builder = new AlertDialog.Builder(CompetitorInformation.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.comp_info_created_success,mStrBundleRetName)).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        clearDatas();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        CompetitorInformation.this.finish();
                    }

                });
        builder.show();




    }

    private void clearDatas()
    {
        etProductName.setText("");
        etSchemeDetails.setText("");
        spSchemeLaunched.setSelection(0);
        etMRP.setText("");
        etRetailerMargin.setText("");
        etProductName.setBackgroundResource(R.drawable.edittext);
        etSchemeDetails.setBackgroundResource(R.drawable.edittext);
        etMRP.setBackgroundResource(R.drawable.edittext);
        etRetailerMargin.setBackgroundResource(R.drawable.edittext);
        spCompetitorName.setBackgroundResource(R.drawable.spinner_bg);
        etOtherInformation.setText("");
        etWholesaleLandingRate.setText("");
        etLandingprice.setText("");
        etConsumerOffer.setText("");
        etTradeOffer.setText("");
        etShelfLife.setText("");
        edit_other_comp_name.setText("");


    }

    @Override
    public void onBackPressed() {
        if (Constants.isCustomKeyboardVisible(keyboardView)) {
            Constants.hideCustomKeyboard(keyboardView);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CompetitorInformation.this, R.style.MyTheme);
            builder.setMessage(R.string.alert_exit_competition_information).setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            CompetitorInformation.this.finish();
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

    public void initializeKeyboardDependencies() {
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_custom_invoice_sel);
        keyboard = new Keyboard(CompetitorInformation.this, R.xml.ll_plus_minuus_updown_keyboard);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
                Constants.incrementTextValues(focusEditText, Constants.Y);
                break;
            case 69:
                //Minus
                Constants.decrementEditTextVal(focusEditText, Constants.Y);
                break;
            case 1:
                changeCursor(0,focusEditText);
                break;
            case 2:
                changeCursor(1,focusEditText);
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
    private void changeCursor(int fromTop,EditText selectedEditText){
        switch (selectedEditText.getId()){
            case R.id.edit_mrp:
                if(fromTop==1){
                    etRetailerMargin.requestFocus();
                }
                break;
            case R.id.edit_retailer_margin:
                if(fromTop==1){
                    etLandingprice.requestFocus();
                }else {
                    etMRP.requestFocus();
                }
                break;
            case R.id.edit_retailer_landing_rate:
                if(fromTop==1){
                    etWholesaleLandingRate.requestFocus();
                }else {
                    etRetailerMargin.requestFocus();
                }
                break;
            case R.id.edit_wholesaler_landing_rate:
                if(fromTop==1){
                    etShelfLife.requestFocus();
                }else {
                    etLandingprice.requestFocus();
                }
                break;
            case R.id.edit_shelf_life:
                if(fromTop==1){
                }else {
                    etWholesaleLandingRate.requestFocus();
                }
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
}
