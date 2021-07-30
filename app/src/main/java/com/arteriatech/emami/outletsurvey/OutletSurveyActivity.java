package com.arteriatech.emami.outletsurvey;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.common.MultipleSelectionSpinner;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.mbo.CPBusinessSet;
import com.arteriatech.emami.mbo.RemarkReasonBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataParserException;
import com.sap.xscript.core.GUID;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class OutletSurveyActivity extends AppCompatActivity implements OutletSurveyView, View.OnClickListener{

    Spinner Spn_size_area_outlet, Spn_FormatOutlet, Spn_home_delivery, Spn_outlet_take_order, spn_Outlet_Billing_System, spn_location_outlet, Spn_hospital_nearby, Spn_universityNearbyDesc, Spn_Outlet_manager, spnr_retailer_list;
    EditText et_man_power, et_check_counter_outlet, et_window_display_outlet;
    TextView et_opening_time, et_closing_time, et_lunch_time;
    private ArrayList<RemarkReasonBean> sizeAreaOutletDesc = new ArrayList<>();
    private ArrayList<RemarkReasonBean> formatOutletDesc = new ArrayList<>();
    private ArrayList<RemarkReasonBean> locationOutletDesc = new ArrayList<>();
    private ArrayList<String> homeDeliveryDesc = new ArrayList<>();
    private ArrayList<String> outletTakeOrderDesc = new ArrayList<>();
    private ArrayList<String> hospitalNearbyDesc = new ArrayList<>();
    private ArrayList<String> universityNearbyDesc = new ArrayList<>();
    private ArrayList<String> outletManagerDesc = new ArrayList<>();
    MultipleSelectionSpinner multipleSelectionSpinner;
    private String selectedSizeAreaOutletDesc = "";
    private String selectedormatOutletDesc = "";
    private String selectedhomeDeliveryDesc = "";
    private String selectedOutletTakeOrderDesc = "";
    private String selectedlocationOutletDesc = "";
    private String selectedhospitalNearbyDesc = "";
    private String selectedOutletManagerDesc = "";
    private String selectedSizeAreaOutletCode = "";
    private String selectedormatOutletCode = "";
    private String selectedhomeDeliveryCode = "";
    private String selectedlocationOutletCode = "";
    private String selectedhospitalNearbyCode = "";
    private String selectedOutletManagerCode = "";
    private ArrayList<String> outletBillingSystemDesc = new ArrayList<>();
    private String selectedOutletBillingSystemDesc = "";
    private String selectedOutletBillingSystemCode = "";
    private String selecteduniversityNearbyDesc = "";
    private String selecteduniversityNearbyCode = "";
    List<String> list = new ArrayList<String>();
    List<RemarkReasonBean> listCat = new ArrayList<RemarkReasonBean>();
    List<String> selectedList = new ArrayList<String>();
    private int mHour;
    private int mMinute;
    private TimePickerDialog timePickerDialog;
    private String CPNo = "";
    ODataPropMap oDataProperties;
    ODataProperty oDataProperty;
    private String selectOpeningTime = "";
    private String selectClosingTime = "";
    private String selectLunchTime = "";
    private ProgressDialog pdLoadDialog = null;
    ArrayList<CPBusinessSet> arrayListSelectedold = new ArrayList<>();
    private OutletSurveyBean outletDefaultSurveyBean=null;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "", mStrBundleCPGUID32 = "";
    private String mStrBundleRetailerUID = "", mStrComingFrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_survey);
        ActionBarView.initActionBarView(this, true, getString(R.string.title_OutletSurvey));
        initUI();

    }

    private void initUI() {
        Spn_size_area_outlet = (Spinner) findViewById(R.id.Spn_size_area_outlet);
        Spn_FormatOutlet = (Spinner) findViewById(R.id.Spn_FormatOutlet);
        Spn_home_delivery = (Spinner) findViewById(R.id.Spn_home_delivery);
        spnr_retailer_list = (Spinner) findViewById(R.id.spnr_retailer_list);
        Spn_outlet_take_order = (Spinner) findViewById(R.id.Spn_outlet_take_order);
        spn_Outlet_Billing_System = (Spinner) findViewById(R.id.spn_Outlet_Billing_System);
        spn_location_outlet = (Spinner) findViewById(R.id.spn_location_outlet);
        Spn_hospital_nearby = (Spinner) findViewById(R.id.Spn_hospital_nearby);
        Spn_universityNearbyDesc = (Spinner) findViewById(R.id.Spn_universityNearbyDesc);
        Spn_Outlet_manager = (Spinner) findViewById(R.id.Spn_Outlet_manager);
        multipleSelectionSpinner = (MultipleSelectionSpinner) findViewById(R.id.questionSp13);
        et_man_power = (EditText) findViewById(R.id.et_man_power);
        et_opening_time = (TextView) findViewById(R.id.et_opening_time);
        et_opening_time.setOnClickListener(this);
        et_opening_time.setInputType(InputType.TYPE_NULL);
        et_closing_time = (TextView) findViewById(R.id.et_closing_time);
        et_closing_time.setOnClickListener(this);
        et_closing_time.setInputType(InputType.TYPE_NULL);
        et_lunch_time = (TextView) findViewById(R.id.et_lunch_time);
        et_lunch_time.setOnClickListener(this);
        et_lunch_time.setInputType(InputType.TYPE_NULL);
        et_check_counter_outlet = (EditText) findViewById(R.id.et_check_counter_outlet);
        et_window_display_outlet = (EditText) findViewById(R.id.et_window_display_outlet);
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
        }
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);
        getDataFromTable();
        getCatigery();
        OutletSurveyPresenterImpl outletSurveyPresenter = new OutletSurveyPresenterImpl(this, this, this,mStrBundleRetID);
        outletSurveyPresenter.start();



       /* list.add("Balms");
        list.add("Deodorants");
        list.add("Antiseptic Creams");
        list.add("Hair Care Product");
        list.add("Cosmetic Products");*/
//        multipleSelectionSpinner.setItems(list);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.survey_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_survey_save:
                updateDialogue();
                break;
        }
        return false;
    }

    private void updateDialogue(){
        String mStrAlertMsg = getString(R.string.update_retailer);
        alertMSG(mStrAlertMsg);
    }

    private void alertMSG(String mStrAlertMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
        builder.setMessage(mStrAlertMsg).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onSave();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    private void onSave() {
        Hashtable table = new Hashtable();
        try {
            String retDetgry = Constants.ChannelPartners+"(guid'"+ CPNo.toUpperCase()+"')" ;
            ODataEntity retilerEntity = OfflineManager.getRetDetails(retDetgry);
            oDataProperties = retilerEntity.getProperties();
            oDataProperty = oDataProperties.get(Constants.CPGUID);
            table.put(Constants.CPGUID,CPNo.toUpperCase());
            //noinspection unchecked
            try {
                oDataProperty = oDataProperties.get(Constants.Anniversary);
                //noinspection unchecked
                    table.put(Constants.Anniversary, oDataProperties.get(Constants.Anniversary).getValue() != null ? (String)oDataProperties.get(Constants.Anniversary).getValue() : "");


                oDataProperty = oDataProperties.get(Constants.DOB);
                //noinspection unchecked
                    table.put(Constants.DOB, oDataProperties.get(Constants.DOB).getValue() != null ? (String)oDataProperties.get(Constants.DOB).getValue() : "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            oDataProperty = oDataProperties.get(Constants.MobileNo);
            table.put(Constants.MobileNo,(String) oDataProperty.getValue()!=null?(String) oDataProperty.getValue():"");
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
            table.put(Constants.Latitude, oDataProperties.get(Constants.Latitude).getValue()!=null?oDataProperties.get(Constants.Latitude).getValue():0.0);
            table.put(Constants.Longitude, oDataProperties.get(Constants.Longitude).getValue()!=null?oDataProperties.get(Constants.Longitude).getValue():0.0);
            table.put(Constants.ParentID, (String) oDataProperties.get(Constants.ParentID).getValue() != null ? (String) oDataProperties.get(Constants.ParentID).getValue() : "");
            table.put(Constants.ParentTypeID, (String) oDataProperties.get(Constants.ParentTypeID).getValue() != null ? (String) oDataProperties.get(Constants.ParentTypeID).getValue() : "");
            table.put(Constants.ParentName, (String) oDataProperties.get(Constants.ParentName).getValue() != null ? (String) oDataProperties.get(Constants.ParentName).getValue() : "");
            table.put(Constants.StateID, (String) oDataProperties.get(Constants.StateID).getValue() != null ? (String) oDataProperties.get(Constants.StateID).getValue() : "");

            table.put(Constants.PartnerMgrGUID, (ODataGuid)oDataProperties.get(Constants.PartnerMgrGUID).getValue() != null ? (ODataGuid)  oDataProperties.get(Constants.PartnerMgrGUID).getValue() : "");
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
            try {
                table.put(Constants.Latitude, oDataProperties.get(Constants.Latitude).getValue().toString() != null ? oDataProperties.get(Constants.Latitude).getValue().toString() : "");
                table.put(Constants.Longitude, oDataProperties.get(Constants.Longitude).getValue().toString() != null ?  oDataProperties.get(Constants.Longitude).getValue().toString() : "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            table.put(Constants.Source,oDataProperties.get(Constants.Source).getValue());

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
                table.put(Constants.CreatedBy,(String) oDataProperties.get(Constants.CreatedBy).getValue() != null ? (String)oDataProperties.get(Constants.CreatedBy).getValue() : "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                table.put(Constants.RouteID,(String) oDataProperties.get(Constants.RouteID).getValue() != null ? (String)oDataProperties.get(Constants.RouteID).getValue() : "");
            } catch (Exception e) {
                e.printStackTrace();
            }


            table.put(Constants.IsLatLongUpdate, Constants.X);


            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            table.put(Constants.LOGINID, loginIdVal);

            table.put(Constants.SetResourcePath, Constants.ChannelPartners + "(guid'" + CPNo.toUpperCase() + "')");
            if (retilerEntity.getEtag() != null) {
                table.put(Constants.Etag, retilerEntity.getEtag());
            }
            table.put(Constants.comingFrom, "");
            table.put(Constants.OutletSizeID,selectedSizeAreaOutletCode);
            table.put(Constants.OutletSizeDesc,selectedSizeAreaOutletDesc);
            table.put(Constants.OutletShapeId,selectedormatOutletCode);
            table.put(Constants.OutletShapeDesc,selectedormatOutletDesc);
            table.put(Constants.NoOfEmployee,et_man_power.getText().toString().trim());
            table.put(Constants.IsHomeDeliveryAvl,selectedhomeDeliveryDesc);
            table.put(Constants.IsPhOrderAvl,selectedOutletTakeOrderDesc);
            table.put(Constants.IsCompBillAvl,selectedOutletBillingSystemDesc);
            table.put(Constants.NoOfCounters,et_check_counter_outlet.getText().toString().trim());
            table.put(Constants.OutletLocId,selectedlocationOutletCode);
            table.put(Constants.OutletLocDesc,selectedlocationOutletDesc);
            table.put(Constants.IsEduInstNrby,selecteduniversityNearbyDesc);
            table.put(Constants.IsHsptlNearBy,selectedhospitalNearbyDesc);
            table.put(Constants.NoOfWindowDisp,et_window_display_outlet.getText().toString().trim());
            table.put(Constants.IsSmartPhAvl,selectedOutletManagerDesc);
            ODataDuration startOpeningDuration = null;
            try {
                if (!selectOpeningTime.isEmpty()) {
                    startOpeningDuration = UtilConstants.getTimeAsODataDuration(selectOpeningTime);
                    table.put(Constants.OpeningTime, startOpeningDuration);
                }/* else {
                    table.put(Constants.OpeningTime, startOpeningDuration);
                }*/


            } catch (Exception e) {
                e.printStackTrace();
            }

            ODataDuration startClosingDuration = null;
            try {
                if (!selectClosingTime.isEmpty()) {
                    startClosingDuration = UtilConstants.getTimeAsODataDuration(selectClosingTime);
                    table.put(Constants.ClosingTime, startClosingDuration);
                }/* else {
                    table.put(Constants.ClosingTime, startClosingDuration);
                }*/


            } catch (Exception e) {
                e.printStackTrace();
            }

            ODataDuration startLunchDuration = null;
            try {
                if (!selectLunchTime.isEmpty()) {
                    startLunchDuration = UtilConstants.getTimeAsODataDuration(selectLunchTime);
                    table.put(Constants.LunchTime, startLunchDuration);
                } /*else {
                    table.put(Constants.LunchTime, startLunchDuration);
                }*/


            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        try {
            OfflineManager.updateRetilerBatchOutlet(table,"");
        } catch (ODataParserException e) {
            e.printStackTrace();
        }


        try {
            String CpMarketQry = Constants.CPMarketSet + "?$filter="+Constants.CPGUID + " eq '"+CPNo.replaceAll("-","").toUpperCase()+"'";
            if(!OfflineManager.getCPMarketDetails(CpMarketQry)){
                onSaveValesToDataVault();
//                new SaveValToDataVault().execute();
            }else {
                UpdateCPMarketDeatils();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        showAlertMsg(getString(R.string.retailer_update),this);
    }

    private void UpdateCPMarketDeatils() {
        String CpMarketQry = Constants.CPMarketSet + "?$filter="+Constants.CPGUID + " eq '"+CPNo.replaceAll("-","").toUpperCase()+"'";
        ODataEntity retilerEntity = null;
        Hashtable hashtable = new Hashtable();
        try {
            retilerEntity = OfflineManager.getRetDetails(CpMarketQry);
            if(retilerEntity!=null){
                oDataProperties = retilerEntity.getProperties();
                try {
                    oDataProperty = oDataProperties.get(Constants.CPGUID);
                    hashtable.put(Constants.CPGUID,CPNo.replaceAll("-","").toUpperCase());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    oDataProperty = oDataProperties.get(Constants.CPMKTGUID);
                    ODataGuid oDataGuid = (ODataGuid) oDataProperty.getValue();
                    hashtable.put(Constants.CPMKTGUID,oDataGuid.guidAsString36().toUpperCase());
                    hashtable.put(Constants.SetResourcePath, Constants.CPMarketSet + "(guid'" + oDataGuid.guidAsString36().toUpperCase() + "')");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ArrayList<HashMap<String, String>> bussinessItems = new ArrayList<>();
                try {
                    selectedList = multipleSelectionSpinner.getSelectedStrings();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String cpBussinessgry = Constants.CPBusinessSet + "?$filter=" + Constants.CPMKTGUID + " eq guid'" + hashtable.get(Constants.CPMKTGUID) + "'";
                List<ODataEntity> entities = null;

                try {
                    entities = OfflineManager.getEntities(cpBussinessgry);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                if (entities!=null && entities.size()>0) {
                    ArrayList<String> duplicateList = new ArrayList<>();
                    ArrayList<String> newList = new ArrayList<>();

                    for (int i = 0; i < selectedList.size(); i++) {
                        boolean isPresentInList = false;
                        for (int j = 0; j < arrayListSelectedold.size(); j++) {
                            CPBusinessSet cpBusinessSet = arrayListSelectedold.get(j);
                            if (selectedList.get(i).contains(cpBusinessSet.getAllBusinessDesc())) {
                                duplicateList.add(cpBusinessSet.getAllBusinessDesc());
                                isPresentInList = true;
                                arrayListSelectedold.remove(j);
                                break;
                            }
                        }
                        if (!isPresentInList) {
                            newList.add(selectedList.get(i));
                        }
                    }
                    //update
                    for (int i = 0; i < duplicateList.size(); i++) {
                        HashMap<String, String> singleItem = new HashMap();
                        String catCode = "";
                        String catDesc = duplicateList.get(i);
                        for (int j = 0; j < listCat.size(); j++) {
                            RemarkReasonBean remarkReasonBean = listCat.get(j);
                            if (catDesc.equalsIgnoreCase(remarkReasonBean.getReasonDesc())) {
                                catCode = remarkReasonBean.getReasonCode();
                                break;
                            }
                        }
                        for (int k = 0; k < entities.size(); k++) {
                            ODataEntity entity = entities.get(k);
                            ODataPropMap oDataProperties;
                            ODataProperty oDataProperty;
                            oDataProperties = entity.getProperties();
                            if (((String) oDataProperties.get(Constants.AllBusinessDesc).getValue()).equalsIgnoreCase(catDesc)) {
                                oDataProperty = oDataProperties.get(Constants.CPBUSGUID);
                                ODataGuid oDataGuid = (ODataGuid) oDataProperty.getValue();
                                singleItem.put(Constants.CPBUSGUID, oDataGuid.guidAsString36().toUpperCase());
                                singleItem.put(Constants.CPGUID, (String) oDataProperties.get(Constants.CPGUID).getValue() != null ? (String) oDataProperties.get(Constants.CPGUID).getValue() : "");
                                oDataProperty = oDataProperties.get(Constants.CPMKTGUID);
                                oDataGuid = (ODataGuid) oDataProperty.getValue();
                                singleItem.put(Constants.CPMKTGUID, oDataGuid.guidAsString36().toUpperCase());
                                singleItem.put(Constants.AllBusinessDesc, catDesc);
                                singleItem.put(Constants.AllBusinessID, catCode);
                                singleItem.put(Constants.AnnualTurnover, "1");
                                singleItem.put(Constants.IS_NEW, "");
                                bussinessItems.add(singleItem);
                                break;
                            }
                        }
                    }
                    //delete
                    for (int i = 0; i < arrayListSelectedold.size(); i++) {
                        HashMap<String, String> singleItem = new HashMap();
                        String catCode = "";
                        CPBusinessSet cpBusinessSet = arrayListSelectedold.get(i);
                        String catDesc = cpBusinessSet.getAllBusinessDesc();
                        for (int j = 0; j < listCat.size(); j++) {
                            RemarkReasonBean remarkReasonBean = listCat.get(j);
                            if (catDesc.equalsIgnoreCase(remarkReasonBean.getReasonDesc())) {
                                catCode = remarkReasonBean.getReasonCode();
                                break;
                            }
                        }
                        for (int k = 0; k < entities.size(); k++) {
                            ODataEntity entity = entities.get(k);
                            ODataPropMap oDataProperties;
                            ODataProperty oDataProperty;
                            oDataProperties = entity.getProperties();
                            if (((String) oDataProperties.get(Constants.AllBusinessDesc).getValue()).equalsIgnoreCase(catDesc)) {
                                oDataProperty = oDataProperties.get(Constants.CPBUSGUID);
                                ODataGuid oDataGuid = (ODataGuid) oDataProperty.getValue();
                                singleItem.put(Constants.CPBUSGUID, oDataGuid.guidAsString36().toUpperCase());
                                singleItem.put(Constants.CPGUID, (String) oDataProperties.get(Constants.CPGUID).getValue() != null ? (String) oDataProperties.get(Constants.CPGUID).getValue() : "");
                                oDataProperty = oDataProperties.get(Constants.CPMKTGUID);
                                oDataGuid = (ODataGuid) oDataProperty.getValue();
                                singleItem.put(Constants.CPMKTGUID, oDataGuid.guidAsString36().toUpperCase());
                                singleItem.put(Constants.AllBusinessDesc, catDesc);
                                singleItem.put(Constants.AllBusinessID, catCode);
                                singleItem.put(Constants.AnnualTurnover, "0");
                                singleItem.put(Constants.IS_NEW, "");
                                bussinessItems.add(singleItem);
                                break;
                            }
                        }
                    }
                    //New
                    for (int k = 0; k < newList.size(); k++) {
                        HashMap<String, String> singleItem1 = new HashMap();
                        String catCode = "";
                        String catDesc = newList.get(k);
                        for (int j = 0; j < listCat.size(); j++) {
                            RemarkReasonBean remarkReasonBean = listCat.get(j);
                            if (catDesc.equalsIgnoreCase(remarkReasonBean.getReasonDesc())) {
                                catCode = remarkReasonBean.getReasonCode();
                                break;
                            }
                        }
                        GUID itemGuid = GUID.newRandom();
                        singleItem1.put(Constants.CPBUSGUID, itemGuid.toString36().toUpperCase());
                        try {
                            singleItem1.put(Constants.CPGUID, CPNo.replaceAll("-", "").toUpperCase());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            oDataProperty = oDataProperties.get(Constants.CPMKTGUID);
                            ODataGuid oDataGuid = (ODataGuid) oDataProperty.getValue();
                            singleItem1.put(Constants.CPMKTGUID, oDataGuid.guidAsString36().toUpperCase());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        singleItem1.put(Constants.AnnualTurnover, "1");
                        singleItem1.put(Constants.AllBusinessDesc, catDesc);
                        singleItem1.put(Constants.AllBusinessID, catCode);
                        singleItem1.put(Constants.IS_NEW, "X");
                        bussinessItems.add(singleItem1);
                    }
                    hashtable.put(Constants.entityType, Constants.OutletSurveyUpdate);
                    hashtable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(bussinessItems));
                    Constants.saveDeviceDocNoToSharedPref(OutletSurveyActivity.this, Constants.OutletSurveyUpdate, String.valueOf(hashtable.get(Constants.CPMKTGUID)));
                    JSONObject jsonHeaderObject = new JSONObject(hashtable);
                    UtilDataVault.storeInDataVault(String.valueOf(hashtable.get(Constants.CPMKTGUID)), jsonHeaderObject.toString());

//                    OfflineManager.updateCPMarketBatch(hashtable, bussinessItems);
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        } /*catch (ODataParserException e) {
            e.printStackTrace();
        }*/

    }

    private void showAlertMsg(String message, Context context){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, com.arteriatech.mutils.R.style.MyTheme);
            builder.setMessage(message).setCancelable(false).setPositiveButton(context.getString(com.arteriatech.mutils.R.string.msg_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    finish();
                }
            });
            builder.show();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    private void getAreaSizeOutlet() {
        ArrayAdapter<RemarkReasonBean> sizeAreaOutletadapter = new ArrayAdapter<>(this, R.layout.custom_textview, sizeAreaOutletDesc);
        sizeAreaOutletadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spn_size_area_outlet.setAdapter(sizeAreaOutletadapter);
        if (outletDefaultSurveyBean!=null){
            Spn_size_area_outlet.setSelection(ConstantsUtils.getAreaOutletPosition(sizeAreaOutletDesc,outletDefaultSurveyBean.getOutletSizeID()));
        }
        Spn_size_area_outlet.setBackgroundResource(R.drawable.spinner_bg);
        Spn_size_area_outlet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!sizeAreaOutletDesc.get(i).getReasonDesc().equalsIgnoreCase("None")) {
                    selectedSizeAreaOutletDesc = sizeAreaOutletDesc.get(i).getReasonDesc();
                    selectedSizeAreaOutletCode = sizeAreaOutletDesc.get(i).getReasonCode();
                }else {
                    selectedSizeAreaOutletDesc = "";
                    selectedSizeAreaOutletCode = "";
                }
                // if(selectedReasonCode.equalsIgnoreCase("00")){
               /* if (selectedSizeAreaOutletCode.equalsIgnoreCase("00")) {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.error_spinner);
                } else {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.spinner_bg);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getLocationOutlet() {


        ArrayAdapter<RemarkReasonBean> sizeAreaOutletadapter = new ArrayAdapter<>(this, R.layout.custom_textview, locationOutletDesc);
        sizeAreaOutletadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_location_outlet.setAdapter(sizeAreaOutletadapter);
        if (outletDefaultSurveyBean!=null){
            spn_location_outlet.setSelection(ConstantsUtils.getAreaOutletPosition(locationOutletDesc,outletDefaultSurveyBean.getOutletLocId()));
        }
        spn_location_outlet.setBackgroundResource(R.drawable.spinner_bg);
        spn_location_outlet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                selectedlocationOutletDesc = locationOutletDesc.get(i).getReasonDesc();
//                selectedlocationOutletCode = locationOutletDesc.get(i).getReasonCode();
                if(!locationOutletDesc.get(i).getReasonDesc().equalsIgnoreCase("None")) {
                    selectedlocationOutletDesc = locationOutletDesc.get(i).getReasonDesc();
                    selectedlocationOutletCode = locationOutletDesc.get(i).getReasonCode();
                }else {
                    selectedlocationOutletDesc = "";
                    selectedlocationOutletCode = "";
                }
                // if(selectedReasonCode.equalsIgnoreCase("00")){
               /* if (selectedSizeAreaOutletCode.equalsIgnoreCase("00")) {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.error_spinner);
                } else {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.spinner_bg);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getHomeDelivery() {


        ArrayAdapter<String> homeDeliveryadapter = new ArrayAdapter<>(this, R.layout.custom_textview, homeDeliveryDesc);
        homeDeliveryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spn_home_delivery.setAdapter(homeDeliveryadapter);
        if (outletDefaultSurveyBean!=null){
            String defaultValue=Constants.None;
            if (outletDefaultSurveyBean.getIsHomeDeliveryAvl().equalsIgnoreCase("X")){
                defaultValue = "Yes";
            }else if (outletDefaultSurveyBean.getIsHomeDeliveryAvl().equalsIgnoreCase("-")){
                defaultValue = "No";
            }
            Spn_home_delivery.setSelection(ConstantsUtils.getStringPosition(homeDeliveryDesc,defaultValue));
        }
        Spn_home_delivery.setBackgroundResource(R.drawable.spinner_bg);
        Spn_home_delivery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedhomeDeliveryDesc = homeDeliveryDesc.get(i);
                if(selectedhomeDeliveryDesc.equalsIgnoreCase("Yes")){
                    selectedhomeDeliveryDesc = "X";
                }else if(selectedhomeDeliveryDesc.equalsIgnoreCase("No")){
                    selectedhomeDeliveryDesc = "-";
                }else {
                    selectedhomeDeliveryDesc="";
                }
                // if(selectedReasonCode.equalsIgnoreCase("00")){
               /* if (selectedSizeAreaOutletCode.equalsIgnoreCase("00")) {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.error_spinner);
                } else {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.spinner_bg);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getHospitalNearby() {


        ArrayAdapter<String> homeDeliveryadapter = new ArrayAdapter<>(this, R.layout.custom_textview, hospitalNearbyDesc);
        homeDeliveryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spn_hospital_nearby.setAdapter(homeDeliveryadapter);
        if (outletDefaultSurveyBean!=null){
            String defaultValue=Constants.None;
            if (outletDefaultSurveyBean.getIsHsptlNearBy().equalsIgnoreCase("X")){
                defaultValue = "Yes";
            }else if (outletDefaultSurveyBean.getIsHsptlNearBy().equalsIgnoreCase("-")){
                defaultValue = "No";
            }
            Spn_hospital_nearby.setSelection(ConstantsUtils.getStringPosition(hospitalNearbyDesc,defaultValue));
        }
        Spn_hospital_nearby.setBackgroundResource(R.drawable.spinner_bg);
        Spn_hospital_nearby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedhospitalNearbyDesc = hospitalNearbyDesc.get(i);
                if(selectedhospitalNearbyDesc.equalsIgnoreCase("Yes")){
                    selectedhospitalNearbyDesc="X";
                }else if(selectedhospitalNearbyDesc.equalsIgnoreCase("No")){
                    selectedhospitalNearbyDesc = "-";
                }else {
                    selectedhospitalNearbyDesc="";
                }
                // if(selectedReasonCode.equalsIgnoreCase("00")){
               /* if (selectedSizeAreaOutletCode.equalsIgnoreCase("00")) {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.error_spinner);
                } else {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.spinner_bg);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getUniversityNearby() {


        ArrayAdapter<String> homeDeliveryadapter = new ArrayAdapter<>(this, R.layout.custom_textview, universityNearbyDesc);
        homeDeliveryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spn_universityNearbyDesc.setAdapter(homeDeliveryadapter);
        if (outletDefaultSurveyBean!=null){
            String defaultValue=Constants.None;
            if (outletDefaultSurveyBean.getIsEduInstNrby().equalsIgnoreCase("X")){
                defaultValue = "Yes";
            }else if (outletDefaultSurveyBean.getIsEduInstNrby().equalsIgnoreCase("-")){
                defaultValue = "No";
            }
            Spn_universityNearbyDesc.setSelection(ConstantsUtils.getStringPosition(universityNearbyDesc,defaultValue));
        }
        Spn_universityNearbyDesc.setBackgroundResource(R.drawable.spinner_bg);
        Spn_universityNearbyDesc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selecteduniversityNearbyDesc = universityNearbyDesc.get(i);
                if(selecteduniversityNearbyDesc.equalsIgnoreCase("Yes")){
                    selecteduniversityNearbyDesc = "X";
                }else if(selecteduniversityNearbyDesc.equalsIgnoreCase("No")){
                    selecteduniversityNearbyDesc = "-";
                }else {
                    selecteduniversityNearbyDesc="";
                }
                // if(selectedReasonCode.equalsIgnoreCase("00")){
               /* if (selectedSizeAreaOutletCode.equalsIgnoreCase("00")) {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.error_spinner);
                } else {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.spinner_bg);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getOutletManager() {


        ArrayAdapter<String> homeDeliveryadapter = new ArrayAdapter<>(this, R.layout.custom_textview, outletManagerDesc);
        homeDeliveryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spn_Outlet_manager.setAdapter(homeDeliveryadapter);
        if (outletDefaultSurveyBean!=null){
            String defaultValue=Constants.None;
            if (outletDefaultSurveyBean.getIsSmartPhAvl().equalsIgnoreCase("X")){
                defaultValue = "Yes";
            }else if (outletDefaultSurveyBean.getIsSmartPhAvl().equalsIgnoreCase("-")){
                defaultValue = "No";
            }
            Spn_Outlet_manager.setSelection(ConstantsUtils.getStringPosition(outletManagerDesc,defaultValue));
        }
        Spn_Outlet_manager.setBackgroundResource(R.drawable.spinner_bg);
        Spn_Outlet_manager.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedOutletManagerDesc = outletManagerDesc.get(i);
                if(selectedOutletManagerDesc.equalsIgnoreCase("Yes")){
                    selectedOutletManagerDesc ="X";
                }else if(selectedOutletManagerDesc.equalsIgnoreCase("No")){
                    selectedOutletManagerDesc = "-";
                }else {
                    selectedOutletManagerDesc ="";
                }
                // if(selectedReasonCode.equalsIgnoreCase("00")){
               /* if (selectedSizeAreaOutletCode.equalsIgnoreCase("00")) {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.error_spinner);
                } else {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.spinner_bg);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getOutletTakeOrder() {


        ArrayAdapter<String> homeDeliveryadapter = new ArrayAdapter<>(this, R.layout.custom_textview, outletTakeOrderDesc);
        homeDeliveryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spn_outlet_take_order.setAdapter(homeDeliveryadapter);
        if (outletDefaultSurveyBean!=null){
            String defaultValue=Constants.None;
            if (outletDefaultSurveyBean.getIsPhOrderAvl().equalsIgnoreCase("X")){
                defaultValue = "Yes";
            }else if (outletDefaultSurveyBean.getIsPhOrderAvl().equalsIgnoreCase("-")){
                defaultValue = "No";
            }
            Spn_outlet_take_order.setSelection(ConstantsUtils.getStringPosition(outletTakeOrderDesc,defaultValue));
        }
        Spn_outlet_take_order.setBackgroundResource(R.drawable.spinner_bg);
        Spn_outlet_take_order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedOutletTakeOrderDesc = outletTakeOrderDesc.get(i);
                if(selectedOutletTakeOrderDesc.equalsIgnoreCase("Yes")){
                    selectedOutletTakeOrderDesc = "X";
                }else if(selectedOutletTakeOrderDesc.equalsIgnoreCase("No")){
                    selectedOutletTakeOrderDesc = "-";
                }else {
                    selectedOutletTakeOrderDesc="";
                }
                // if(selectedReasonCode.equalsIgnoreCase("00")){
               /* if (selectedSizeAreaOutletCode.equalsIgnoreCase("00")) {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.error_spinner);
                } else {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.spinner_bg);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getOutletBillingSystem() {


        ArrayAdapter<String> homeDeliveryadapter = new ArrayAdapter<>(this, R.layout.custom_textview, outletBillingSystemDesc);
        homeDeliveryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_Outlet_Billing_System.setAdapter(homeDeliveryadapter);
        if (outletDefaultSurveyBean!=null){
            String defaultValue=Constants.None;
            if (outletDefaultSurveyBean.getIsCompBillAvl().equalsIgnoreCase("X")){
                defaultValue = "Yes";
            }else if (outletDefaultSurveyBean.getIsCompBillAvl().equalsIgnoreCase("-")){
                defaultValue = "No";
            }
            spn_Outlet_Billing_System.setSelection(ConstantsUtils.getStringPosition(outletBillingSystemDesc,defaultValue));
        }
        spn_Outlet_Billing_System.setBackgroundResource(R.drawable.spinner_bg);
        spn_Outlet_Billing_System.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedOutletBillingSystemDesc = outletBillingSystemDesc.get(i);
                if(selectedOutletBillingSystemDesc.equalsIgnoreCase("Yes")){
                    selectedOutletBillingSystemDesc="X";
                }else if(selectedOutletBillingSystemDesc.equalsIgnoreCase("No")){
                    selectedOutletBillingSystemDesc = "-";
                }else {
                    selectedOutletBillingSystemDesc="";
                }
                // if(selectedReasonCode.equalsIgnoreCase("00")){
               /* if (selectedSizeAreaOutletCode.equalsIgnoreCase("00")) {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.error_spinner);
                } else {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.spinner_bg);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getFormatOutlet() {

        ArrayAdapter<RemarkReasonBean> formatOutletadapter = new ArrayAdapter<>(this, R.layout.custom_textview, formatOutletDesc);
        formatOutletadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spn_FormatOutlet.setAdapter(formatOutletadapter);
        if (outletDefaultSurveyBean!=null){
            Spn_FormatOutlet.setSelection(ConstantsUtils.getAreaOutletPosition(formatOutletDesc,outletDefaultSurveyBean.getOutletShapeId()));
        }
        Spn_FormatOutlet.setBackgroundResource(R.drawable.spinner_bg);
        Spn_FormatOutlet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                selectedormatOutletDesc = formatOutletDesc.get(i).getReasonDesc();
//                selectedormatOutletCode = formatOutletDesc.get(i).getReasonCode();

                if(!formatOutletDesc.get(i).getReasonDesc().equalsIgnoreCase("None")) {
                    selectedormatOutletDesc = formatOutletDesc.get(i).getReasonDesc();
                    selectedormatOutletCode = formatOutletDesc.get(i).getReasonCode();
                }else {
                    selectedormatOutletDesc = "";
                    selectedormatOutletCode = "";
                }
                // if(selectedReasonCode.equalsIgnoreCase("00")){
                /*if (selectedSizeAreaOutletCode.equalsIgnoreCase("00")) {
                    Spn_FormatOutlet.setBackgroundResource(R.drawable.error_spinner);
                } else {
                    Spn_FormatOutlet.setBackgroundResource(R.drawable.spinner_bg);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getCatigery() {


       /* if(arrayList.size()>0){
            multipleSelectionSpinner.setSelection(arrayList);
        }*/
        String query = Constants.TypeSetALLBSN;
        try {
            listCat = OfflineManager.getCatigeryRemarks(query);
            for(int i =0;i<listCat.size();i++){
                RemarkReasonBean remarkReasonBean = listCat.get(i);
                list.add(remarkReasonBean.getReasonDesc());
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getRetailerList(OutletSurveyBean alRetailerData) {
        try {

            outletDefaultSurveyBean =alRetailerData;
            CPNo = outletDefaultSurveyBean.getCPGUID();
            try {
                multipleSelectionSpinner.getList((ArrayList<RemarkReasonBean>) listCat);
                multipleSelectionSpinner.setItems(list);
                multipleSelectionSpinner.setSelection(0);

                String CpMarketQry = Constants.CPMarketSet + "?$filter="+Constants.CPGUID + " eq '"+CPNo.replaceAll("-","").toUpperCase()+"'";
                ODataGuid cpMarketGuid = OfflineManager.getCPMarketGuid(CpMarketQry);

                if(cpMarketGuid!=null) {
                    String CpBussinesQry = Constants.CPBusinessSet + "?$filter=" + Constants.CPMKTGUID + " eq guid'" + cpMarketGuid.guidAsString36().toUpperCase() + "'";
                    arrayListSelectedold = OfflineManager.getCPBussinessList(CpBussinesQry);
                }
                if(arrayListSelectedold.size()>0){
                    ArrayList<String> selectedList  = new ArrayList<>();
                    for (CPBusinessSet cpBusinessSet : arrayListSelectedold){
                        if (cpBusinessSet.getAnnualTurnover()>0){
                            selectedList.add(cpBusinessSet.getAllBusinessDesc());
                        }
                    }
                    multipleSelectionSpinner.setDefaultSelection(selectedList);
                }else {
                    ArrayList<String> selectedList  = new ArrayList<>();
                    multipleSelectionSpinner.setDefaultSelection(selectedList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            displayDefaultData();

           /* ArrayAdapter<OutletSurveyBean> adapter = new ArrayAdapter<OutletSurveyBean>(this, android.R.layout.simple_spinner_item, alRetailerList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnr_retailer_list.setAdapter(adapter);

            spnr_retailer_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                    distributorViewPresenter.getDistributorData(cpNo);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }


        /*ArrayAdapter<RemarkReasonBean> sizeAreaOutletadapter = new ArrayAdapter<>(this, R.layout.custom_textview, sizeAreaOutletDesc);
        sizeAreaOutletadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spn_size_area_outlet.setAdapter(sizeAreaOutletadapter);
        Spn_size_area_outlet.setBackgroundResource(R.drawable.spinner_bg);
        Spn_size_area_outlet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSizeAreaOutletDesc = sizeAreaOutletDesc.get(i).getReasonDesc();
                selectedSizeAreaOutletCode = sizeAreaOutletDesc.get(i).getReasonCode();
                // if(selectedReasonCode.equalsIgnoreCase("00")){
                if (selectedSizeAreaOutletCode.equalsIgnoreCase("00")) {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.error_spinner);
                    } else {
                    Spn_size_area_outlet.setBackgroundResource(R.drawable.spinner_bg);
                    }
                }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        /*ArrayAdapter<RemarkReasonBean> formatOutletadapter = new ArrayAdapter<>(this, R.layout.custom_textview, sizeAreaOutletDesc);
        formatOutletadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spn_FormatOutlet.setAdapter(formatOutletadapter);
        Spn_FormatOutlet.setBackgroundResource(R.drawable.spinner_bg);
        Spn_FormatOutlet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSizeAreaOutletDesc = sizeAreaOutletDesc.get(i).getReasonDesc();
                selectedSizeAreaOutletCode = sizeAreaOutletDesc.get(i).getReasonCode();
                // if(selectedReasonCode.equalsIgnoreCase("00")){
                if (selectedSizeAreaOutletCode.equalsIgnoreCase("00")) {
                    Spn_FormatOutlet.setBackgroundResource(R.drawable.error_spinner);
                    } else {
                    Spn_FormatOutlet.setBackgroundResource(R.drawable.spinner_bg);
                    }
                }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

    }

    @Override
    public void showProgressDialog() {
        pdLoadDialog = new ProgressDialog(OutletSurveyActivity.this, R.style.ProgressDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.app_loading));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void displayDefaultData() {
        getAreaSizeOutlet();
        getFormatOutlet();
        getHomeDelivery();
        getOutletTakeOrder();
        getLocationOutlet();
        getOutletBillingSystem();
        getHospitalNearby();
        getOutletManager();
        getUniversityNearby();



        et_opening_time.setText("");
        selectOpeningTime="";
        et_closing_time.setText("");
        selectClosingTime="";
        et_lunch_time.setText("");
        selectLunchTime="";

        if (outletDefaultSurveyBean!=null){
            et_check_counter_outlet.setText(outletDefaultSurveyBean.getNoOfCounters());
            et_man_power.setText(outletDefaultSurveyBean.getNoOfEmployee());
            et_window_display_outlet.setText(outletDefaultSurveyBean.getNoOfWindowDisp());
            if (!TextUtils.isEmpty(outletDefaultSurveyBean.getOpeningTime())) {
                selectOpeningTime = outletDefaultSurveyBean.getOpeningTime();
                et_opening_time.setText(outletDefaultSurveyBean.getOpeningTime());
            }
            if (!TextUtils.isEmpty(outletDefaultSurveyBean.getClosingTime())) {
                selectClosingTime = outletDefaultSurveyBean.getClosingTime();
                et_closing_time.setText(outletDefaultSurveyBean.getClosingTime());
            }
            if (!TextUtils.isEmpty(outletDefaultSurveyBean.getLunchTime())) {
                selectLunchTime = outletDefaultSurveyBean.getLunchTime();
                et_lunch_time.setText(outletDefaultSurveyBean.getLunchTime());
            }
        }

    }

    private void getDataFromTable() {
        String query = Constants.TypeSetCPOLSZ;
        try {
            sizeAreaOutletDesc = OfflineManager.getVisitDeviationRemarks(query);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        query = Constants.TypeSetCPOLSP;
        try {
            formatOutletDesc = OfflineManager.getVisitDeviationRemarks(query);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        try {
            homeDeliveryDesc = Constants.getYesNoDesc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            outletTakeOrderDesc = Constants.getYesNoDesc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        query = Constants.TypeSetCPOLLC;
        try {
            locationOutletDesc = OfflineManager.getVisitDeviationRemarks(query);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        try {
            outletBillingSystemDesc = Constants.getYesNoDesc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            hospitalNearbyDesc = Constants.getYesNoDesc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            outletManagerDesc = Constants.getYesNoDesc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            universityNearbyDesc = Constants.getYesNoDesc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class SaveValToDataVault extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(OutletSurveyActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.saving_data_plz_wait));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

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

        }
    }
    private Hashtable<String, String> headerTable = new Hashtable<>();
    private void onSaveValesToDataVault() {
        String doc_no = (System.currentTimeMillis() + "");
        GUID headerGuid = GUID.newRandom();
        headerTable.put(Constants.CPMKTGUID, headerGuid.toString36().toUpperCase());
        String mStrGuidFormat = "";
        try {
            if (!CPNo.toUpperCase().toString().equalsIgnoreCase("")) {
                mStrGuidFormat =  CPNo.toUpperCase().replaceAll("-","");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        headerTable.put(Constants.CPGUID,mStrGuidFormat);
        try {
            selectedList = multipleSelectionSpinner.getSelectedStrings();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<HashMap<String, String>> bussinessItems = new ArrayList<>();
        for (int i = 0;i<selectedList.size();i++) {
            String catCode = "";
            String catDesc = selectedList.get(i);
            /*RemarkReasonBean remarkReasonBean = listCat.get(i);
            if(catDesc.equalsIgnoreCase(remarkReasonBean.getReasonDesc())){
                catCode = remarkReasonBean.getReasonCode();
            }*/
            for(int j=0;j<listCat.size();j++){
                RemarkReasonBean remarkReasonBean = listCat.get(j);
                if(catDesc.equalsIgnoreCase(remarkReasonBean.getReasonDesc())){
                    catCode = remarkReasonBean.getReasonCode();break;
                }
            }
            HashMap<String, String> singleItem = new HashMap<>();
            GUID itemGuid = GUID.newRandom();
            singleItem.put(Constants.CPBUSGUID, itemGuid.toString36().toUpperCase());
            singleItem.put(Constants.CPGUID, mStrGuidFormat);
            singleItem.put(Constants.CPMKTGUID, headerGuid.toString36().toUpperCase());
            singleItem.put(Constants.AllBusinessDesc, catDesc);
            singleItem.put(Constants.AllBusinessID, catCode);
            bussinessItems.add(singleItem);
        }
        ArrayList<HashMap<String, String>> SalesItems = new ArrayList<>();
//        for (String ItemString : selectedList) {
            HashMap<String, String> singleItem = new HashMap<>();
            GUID itemGuid = GUID.newRandom();
            singleItem.put(Constants.CompSalesGUID, itemGuid.toString36().toUpperCase());
            singleItem.put(Constants.CPGUID,mStrGuidFormat);
            singleItem.put(Constants.CPMKTGUID, headerGuid.toString36().toUpperCase());
            SalesItems.add(singleItem);
//        }
        headerTable.put(Constants.entityType, Constants.OutletSurveyCreate);
        headerTable.put(Constants.ITEM_TXT, UtilConstants.convertArrListToGsonString(bussinessItems));
        headerTable.put(Constants.ITEM_TXT1, UtilConstants.convertArrListToGsonString(SalesItems));
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        Constants.saveDeviceDocNoToSharedPref(OutletSurveyActivity.this, Constants.outletSurvery, doc_no);
        headerTable.put(Constants.LOGINID, sharedPreferences.getString(Constants.username, "").toUpperCase());
        JSONObject jsonHeaderObject = new JSONObject(headerTable);
        UtilDataVault.storeInDataVault(doc_no, jsonHeaderObject.toString());

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_outlet_survey).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onNavigateToRetDetilsActivity();
                       /* Intent intBack = new Intent(OutletSurveyActivity.this, MainMenu.class);
                        intBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intBack);*/
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    private void onNavigateToRetDetilsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(OutletSurveyActivity.this, RetailersDetailsActivity.class);
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
    public void onClick(View v) {
        if (v == et_opening_time) {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    selectOpeningTime=""+selectedHour + "-" + selectedMinute;
                    et_opening_time.setText(""+selectedHour + "-" + selectedMinute);
                }
            }, mHour, mMinute, true);//Yes 24 hour time
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {

                }
            });
            timePickerDialog.show();
        }else if (v == et_closing_time) {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    selectClosingTime=""+selectedHour + "-" + selectedMinute;
                    et_closing_time.setText(""+selectedHour + "-" + selectedMinute);
                }
            }, mHour, mMinute, true);//Yes 24 hour time
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {

                }
            });
            timePickerDialog.show();
        }else if (v == et_lunch_time) {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    selectLunchTime=""+selectedHour + "-" + selectedMinute;
                    et_lunch_time.setText(""+selectedHour + "-" + selectedMinute);
                }
            }, mHour, mMinute, true);//Yes 24 hour time
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {

                }
            });
            timePickerDialog.show();
        }

    /*else if (v == et_closing_time) {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");
        }else if (v == et_lunch_time) {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");
        }*/
    }
}
