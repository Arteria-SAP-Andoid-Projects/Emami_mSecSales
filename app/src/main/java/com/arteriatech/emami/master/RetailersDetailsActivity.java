package com.arteriatech.emami.master;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.FragmentWithTitleBean;
import com.arteriatech.emami.adapter.RetailerDetailPagetTabAdapter;
import com.arteriatech.emami.adapter.ViewPagerTabAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.MSFAApplication;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.mbo.RemarkReasonBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.routeplan.CustomerListActivity;
import com.arteriatech.emami.routeplan.OtherBeatListActivity;
import com.arteriatech.emami.routeplan.RoutePlanListActivity;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.visit.VisitFragment;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataGuidDefaultImpl;
import com.sap.xscript.core.CharBuffer;
import com.sap.xscript.core.StringFunction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by ${e10526} on ${16-11-2016}.
 */
@SuppressLint("NewApi")
public class RetailersDetailsActivity extends AppCompatActivity implements View.OnClickListener, UIListener {
    String mStrCPTypeId = "";
    ImageView iv_visit_status;
    Map<String, String> startParameterMap;
    TabLayout tabLayout;
    Spinner spVisitEndReason;
    boolean isValid = true;
    boolean mBoolBackBtnPressed = false;
    boolean wantToCloseDialog = false;
    private String mStrCustomerName = "";
    private String mStrUID = "";
    private int totalRetailers = 0;
    private String mStrCustomerId = "", mStrVisitSeqNo = "", mStrVisitActSeqNo = "";
    private String mStrBundleCpGuid = "";
    private String mStrComingFrom = "";
    private String mStrRouteGuid = "";
    private String mStrRouteName = "";
    private ODataGuid mCpGuid;
    private String mStrPopUpText = "";
    private String mStrCustNo = "";
    private String mStrOtherRetailerGuid = "";
    //new
    private String mStrVisitEndRemarks = "";
    private ODataPropMap oDataProperties;
    private ODataProperty oDataProperty;
    private ODataGuid mStrVisitId = null;
    private boolean mBooleanVisitStarted = false;
    private ProgressDialog pdLoadDialog;
    private boolean mBooleanNavPrvVisitClosed = false;
    private boolean mBooleanSaveStart = false;
    private boolean mBooleanVisitStartDialog = false, mBooleanVisitEndDialog = false;
    private String mStrVisitCatId = "";
    //This is our viewPager
    private ViewPager viewPager;
    private boolean mBoolMsg = false;
    private String selectedReasonDesc = "";
    private String selectedReasonCode = "";
    private ArrayList<RemarkReasonBean> reasonCodedesc = new ArrayList<>();
    private ArrayList<RemarkReasonBean> seqDevCodeDesc = new ArrayList<>();
    private String noOfOutlet = "";
    private String mStrVisitSeqNoFromEntity = "";
    SharedPreferences sharedPreferences;
    String beatOptmEnabled = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.lbl_retailer_details));
        setContentView(R.layout.activity_retailer_detail);
        sharedPreferences = this.getSharedPreferences(Constants.PREFS_NAME, 0);
        beatOptmEnabled = sharedPreferences.getString(Constants.isBeatOptmKey, "");
        if (!Constants.restartApp(RetailersDetailsActivity.this)) {
            Constants.mApplication = (MSFAApplication) getApplication();
            Constants.parser = Constants.mApplication.getParser();

            TextView tvCustomerID = (TextView) findViewById(R.id.tv_RetailerID);
            TextView tvCustomerName = (TextView) findViewById(R.id.tv_RetailerName);

            startParameterMap = new HashMap<String, String>();

            iv_visit_status = (ImageView) findViewById(R.id.iv_visit_status);
            iv_visit_status.setOnClickListener(this);

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mStrCustomerName = extras.getString(Constants.RetailerName);
                mStrUID = extras.getString(Constants.CPUID);
                mStrCustomerId = extras.getString(Constants.CPNo);
                mStrVisitSeqNo = extras.getString(Constants.VisitSeq);
                mStrComingFrom = extras.getString(Constants.comingFrom);
                totalRetailers = extras.getInt(Constants.TotalRetalierCount);
                mStrBundleCpGuid = extras.getString(Constants.CPGUID) != null ? extras.getString(Constants.CPGUID) : "";
                mStrRouteName = extras.getString(Constants.OtherRouteName) != null ? extras.getString(Constants.OtherRouteName) : "";
                mStrRouteGuid = extras.getString(Constants.OtherRouteGUID) != null ? extras.getString(Constants.OtherRouteGUID) : "";
                mStrVisitCatId = extras.getString(Constants.VisitCatID);
                noOfOutlet = extras.getString(Constants.NoOfOutlet);
                Constants.VisitNavigationFrom = mStrComingFrom;

            }


            mStrCustNo = mStrCustomerId;
            tvCustomerName.setText(mStrCustomerName);

            tvCustomerID.setText(mStrUID);


            mCpGuid = getCPGUID();

            displayVisitIcon();

            tabIntilize();
            getReasonValues();
            getSeqDevDesc();
        }
    }

    /*
        TODO Get Retailer CpGuiD Value
     */
    private ODataGuid getCPGUID() {
        String cpGuidQry = Constants.ChannelPartners + "(guid'" + mStrBundleCpGuid.toUpperCase() + "') ";
        ODataGuid cpGuid = null;

        try {
            ODataEntity retilerEntity = OfflineManager.getRetDetails(cpGuidQry);
            oDataProperties = retilerEntity.getProperties();
            oDataProperty = oDataProperties.get(Constants.CPGUID);
            cpGuid = (ODataGuid) oDataProperty.getValue();
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        return cpGuid;
    }

    /*
       TODO Display Visit Status Icon
    */
    private void displayVisitIcon() {
        /*if(!mStrComingFrom.equalsIgnoreCase(Constants.RouteList)){
            Constants.Route_Plan_Key = "";
        }*/
        if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList)
                || mStrComingFrom.equalsIgnoreCase(Constants.CustomerList)
                || mStrComingFrom.equalsIgnoreCase(Constants.RouteList)
                || mStrComingFrom.equalsIgnoreCase(Constants.OtherRouteList)) {
            iv_visit_status.setVisibility(View.VISIBLE);
        } else {
            iv_visit_status.setVisibility(View.GONE);
        }

        mBooleanVisitStartDialog = false;
        mBooleanVisitEndDialog = false;

        String mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "'" +
                "and CPGUID eq '" + mCpGuid.guidAsString32().toUpperCase() + "' and " + Constants.StatusID + " eq '01'";


        String mStrVisitStartedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq null " +
                "and CPGUID eq '" + mCpGuid.guidAsString32().toUpperCase() + "'and " + Constants.StatusID + " eq '01'";

        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)) {
                iv_visit_status.setImageResource(R.drawable.stop);
                mBooleanVisitStarted = true;
            } else if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                iv_visit_status.setImageResource(R.drawable.ic_done);
                mBooleanVisitStarted = false;
            } else {
                Constants.MapEntityVal.clear();

                String qry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mCpGuid.guidAsString32().toUpperCase() + "' " +
                        "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01'";

                try {
                    mStrVisitId = OfflineManager.getVisitDetails(qry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                if (!Constants.MapEntityVal.isEmpty()) {
                    iv_visit_status.setImageResource(R.drawable.stop);
                    mBooleanVisitStarted = true;
                } else {
                    iv_visit_status.setImageResource(R.drawable.start);
                    mBooleanVisitStarted = false;
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /*
      TODO Navigate to Previous List Screens
   */
    private void NavigateToListScreen() {
        if (mStrComingFrom.equalsIgnoreCase(Constants.CustomerList)) {
            Intent intRouteList = new Intent(RetailersDetailsActivity.this,
                    CustomerListActivity.class);
            intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intRouteList.putExtra(Constants.RetailerName, Constants.Route_Plan_Desc);
            intRouteList.putExtra(Constants.CPNo, Constants.Route_Plan_No);
            intRouteList.putExtra(Constants.VISITTYPE, Constants.Visit_Type);
            startActivity(intRouteList);
        } else if (mStrComingFrom.equalsIgnoreCase(Constants.RouteList)) {
            Intent intRouteList = new Intent(RetailersDetailsActivity.this,
                    RoutePlanListActivity.class);
            intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intRouteList);
        } else if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList)) {
            Intent intRouteList = new Intent(RetailersDetailsActivity.this,
                    AdhocListActivity.class);
            intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intRouteList);
        } else if (mStrComingFrom.equalsIgnoreCase(Constants.OtherRouteList)) {
            Intent intRouteList = new Intent(RetailersDetailsActivity.this,
                    OtherBeatListActivity.class);
            intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intRouteList.putExtra(Constants.OtherRouteGUID, mStrRouteGuid);
            intRouteList.putExtra(Constants.OtherRouteName, mStrRouteName);
            if (Constants.Visit_Cat_ID.equalsIgnoreCase(Constants.str_02)) {
                intRouteList.putExtra(Constants.RouteType, getString(R.string.lbl_other_beats));
            } else {
                intRouteList.putExtra(Constants.RouteType, getString(R.string.lbl_today_beats));
            }
            startActivity(intRouteList);
        } else {
            Intent intRouteList = new Intent(RetailersDetailsActivity.this,
                    RetailersListActivity.class);
            intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intRouteList);
        }
    }

    /*
         TODO Dismiss Progress Dialog

      */
    private void dismissProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     TODO Display error message
  */
    @Override
    public void onRequestError(int operation, Exception exception) {
        dismissProgressDialog();
        String err_msg = "";
        try {
            if (operation == Operation.Create.getValue()) {
                try {
                    err_msg = getString(R.string.err_msg_concat, getString(R.string.lbl_visit_start), exception.getMessage());
                } catch (Exception ex) {
                    err_msg = getString(R.string.err_visit_start);
                }
                UtilConstants.showAlert(err_msg, RetailersDetailsActivity.this);
            } else if (operation == Operation.Update.getValue()) {
                try {
                    err_msg = getString(R.string.err_msg_concat, getString(R.string.lbl_visit_end), exception.getMessage());
                } catch (Exception ex) {
                    err_msg = getString(R.string.err_visit_end);
                }
                UtilConstants.showAlert(err_msg, RetailersDetailsActivity.this);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        Toast.makeText(RetailersDetailsActivity.this, getString(R.string.err_odata_unexpected, exception.getMessage()),
                Toast.LENGTH_LONG).show();
    }

    /*
    TODO Display Success message
 */
    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {

        if (operation == Operation.Create.getValue()) {
            alertPopupMessage();
        } else if (operation == Operation.Update.getValue()) {
            Constants.TodayActualVisitRetailersCount = Constants.getTodayActualVisitedRetCount("");
            alertPopupMessage();
        } else if (operation == Operation.OfflineFlush.getValue()) {
            if (!UtilConstants.isNetworkAvailable(RetailersDetailsActivity.this)) {
                dismissProgressDialog();
                onNoNetwork();
            } else {
                OfflineManager.refreshRequests(getApplicationContext(), Constants.Visits, RetailersDetailsActivity.this);
            }

        } else if (operation == Operation.OfflineRefresh.getValue()) {

            dismissProgressDialog();

            if (mBooleanNavPrvVisitClosed) {
                NavigateToListScreen();
            } else {
                if (mBooleanSaveStart) {
                }
            }
        }
    }

    /*
   TODO Display Alert message regarding visit started or visit ended.
    */
    private void alertPopupMessage() {
        dismissProgressDialog();
        if (mBooleanVisitStartDialog) {
            mStrPopUpText = getString(R.string.visit_started);
        }
        if (mBooleanVisitEndDialog)
            mStrPopUpText = getString(R.string.visit_ended);


        AlertDialog.Builder builder = new AlertDialog.Builder(
                RetailersDetailsActivity.this, R.style.MyTheme);
        builder.setMessage(mStrPopUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();

                                    if (mBooleanNavPrvVisitClosed) {
                                        NavigateToListScreen();
                                    } else {
                                        if (mBooleanSaveStart) {
                                            mBooleanVisitStarted = true;
                                            setupViewPagerWithVisit();
                                            tabLayout.setupWithViewPager(viewPager);
                                            viewPager.setCurrentItem(1);
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();


    }

    private void onSaveClose() {
        mStrPopUpText = getString(R.string.marking_visit_end_plz_wait);
        try {
            new ClosingVisit().execute();
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void onSaveStart() {
        mStrPopUpText = getString(R.string.marking_visit_start_plz_wait);
        try {
            String cpId = UtilConstants.removeLeadingZeros(mStrCustomerId);
            startParameterMap.put(Constants.CPNo, cpId);
            startParameterMap.put(Constants.CPName, mStrCustomerName);
            startParameterMap.put(Constants.CPTypeID, Constants.str_02);
            startParameterMap.put(Constants.VisitCatID, mStrVisitCatId);
            startParameterMap.put(Constants.StatusID, "01");
            startParameterMap.put(Constants.PlannedDate, null);
            startParameterMap.put(Constants.PlannedStartTime, null);
            startParameterMap.put(Constants.PlannedEndTime, null);
            startParameterMap.put(Constants.VisitTypeID, "");
            startParameterMap.put(Constants.VisitTypeDesc, "");
            startParameterMap.put(Constants.Remarks, "");
            startParameterMap.put(Constants.VisitSeq, mStrVisitSeqNo);
            startParameterMap.put(Constants.ActualSeq, mStrVisitActSeqNo);
            startParameterMap.put(Constants.DeviationReasonID, selectedReasonCode);
            startParameterMap.put(Constants.DeviationRemarks, mStrVisitEndRemarks);

            try {
                startParameterMap.put(Constants.BeatGUID, mStrRouteGuid);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (noOfOutlet != null) {
                startParameterMap.put(Constants.NoOfOutlet, noOfOutlet);
            } else {
                String routeId = Constants.getNameByCPGUID(Constants.CPDMSDivisions, Constants.RouteID, Constants.CPGUID, mStrBundleCpGuid);
                if (!TextUtils.isEmpty(routeId)) {
                    String qryForTodaysBeat = Constants.RouteSchedulePlans + "?$filter=" + Constants.RouteID + " eq '" + routeId + "' &$orderby=" + Constants.SequenceNo + "";
                    ArrayList<CustomerBean> alRetailerList = OfflineManager.getRetailerListForOtherBeats(qryForTodaysBeat);
                    startParameterMap.put(Constants.NoOfOutlet, String.valueOf(alRetailerList.size()));
                }
            }
            startParameterMap.put(Constants.VisitDate, UtilConstants.getNewDateTimeFormat());


            Constants.createVisit(startParameterMap, mCpGuid, RetailersDetailsActivity.this, this);
//            new StartVisit().execute();
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_visit_status:
                checkVisitStartLocPermission();
                break;
        }
    }

    // Reason for Deviation Remarks

    private void startVisitBaseOnOutletSeq() {
        selectedReasonCode = "";
        selectedReasonDesc = "";
        mStrVisitEndRemarks = "";
        int actualSeqNo = 0;
        String strLastVisitSeq = "";
       /* String noVisitTodayQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "'";
        int noVisitToday = 0;
        try {
            noVisitToday  = OfflineManager.getNoVisitToday(noVisitTodayQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }*/
        String lastVisitSeqQry = "";
        String mStrRouteGuidFormat = "";
        lastVisitSeqQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.VisitCatID + " eq '" + mStrVisitCatId + "' &$orderby=ActualSeq desc";

        String mStrRoutePlanKey = Constants.Route_Plan_Key;
        if (!mStrRoutePlanKey.equalsIgnoreCase("")) {
            mStrRouteGuidFormat = CharBuffer.join9(StringFunction.substring(mStrRoutePlanKey, 0, 8), "-", StringFunction.substring(mStrRoutePlanKey, 8, 12), "-", StringFunction.substring(mStrRoutePlanKey, 12, 16), "-", StringFunction.substring(mStrRoutePlanKey, 16, 20), "-", StringFunction.substring(mStrRoutePlanKey, 20, 32));
        }

        if (!mStrComingFrom.equalsIgnoreCase(Constants.AdhocList)) {
            lastVisitSeqQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.VisitCatID + " eq '" + mStrVisitCatId + "' and " + Constants.ROUTEPLANKEY + " eq guid'" + mStrRouteGuidFormat + "' &$orderby=ActualSeq desc";
        }
        String[] lastVisitSeq = null;
        try {
            lastVisitSeq = OfflineManager.getLastVisitSeq(lastVisitSeqQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        try {
            strLastVisitSeq = lastVisitSeq[0];//backend sequence
            actualSeqNo = Integer.parseInt(lastVisitSeq[1]);//mobile visited sequence
        } catch (Exception e) {
            e.printStackTrace();
        }
        mStrVisitActSeqNo = String.valueOf(actualSeqNo + 1);//TODO need to check for empty sequence what is the value

        /*if(OfflineManager.getAuthorization(Constants.UserProfiles)) {
            if (mStrComingFrom.equalsIgnoreCase(Constants.RouteList)) {
                if (!mStrVisitSeqNo.equalsIgnoreCase("")) {
                    int lastSeqNo = 0;
                    if (!TextUtils.isEmpty(strLastVisitSeq)) {
                        lastSeqNo = Integer.parseInt(strLastVisitSeq);
                    }
                    if ((Integer.parseInt(mStrVisitSeqNo) - 1) != lastSeqNo) {
                        onAlertDialogForOutletVisit();
                    } else {
                        startVisit();
                    }
                } else {
                    startVisit();
                }
            } else {
                startVisit();
            }
        }else {
            startVisit();
        }*/

        String visitCountQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.VisitCatID + " eq '" + mStrVisitCatId + "' and " + Constants.VisitSeq + " ne ''";
        int visitCount = 0;
        try {
            visitCount = OfflineManager.getVisitCount(visitCountQry);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
//        int totalRetailers = Constants.getTotalRetailerCount(Constants.alTempRouteList);
        /*int tempVisitSeqNo = 0;
        if(!TextUtils.isEmpty(mStrVisitSeqNo)) {
            tempVisitSeqNo = Integer.parseInt(mStrVisitSeqNo);
            if (tempVisitSeqNo > 1) {
                tempVisitSeqNo = Integer.parseInt(mStrVisitSeqNo) - 1;
            }
        }*/
        String retailerVisitedQry = "";
//        boolean checkOutletSeq = false;
        try {
            if (OfflineManager.getAuthorization(Constants.UserProfiles)) {
                if (mStrComingFrom.equalsIgnoreCase(Constants.RouteList)) {
                    if (!mStrVisitSeqNo.equalsIgnoreCase("") && !mStrVisitSeqNo.equalsIgnoreCase("0") && !mStrVisitSeqNo.equalsIgnoreCase("000000")) {
                        retailerVisitedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.VisitCatID + " eq '" + mStrVisitCatId + "' and (" + Constants.getVisitSeqQry(Integer.parseInt(mStrVisitSeqNo)) + ")";
//                        checkOutletSeq = OfflineManager.getLastVisitedRetailers(retailerVisitedQry,Integer.parseInt(mStrVisitSeqNo));
                        int lastSeqNo = 0;
                        if (!TextUtils.isEmpty(strLastVisitSeq)) {
                            lastSeqNo = Integer.parseInt(strLastVisitSeq);
                        }
                        /*if((visitCount <= totalRetailers) && checkOutletSeq){
                            startVisit();
                        } else {
                            if ((Integer.parseInt(mStrVisitSeqNo) - 1) != lastSeqNo) {
                                onAlertDialogForOutletVisit();
                            } else {
                                startVisit();
                            }
                        }*/
                        if ((visitCount <= totalRetailers) && ((Integer.parseInt(mStrVisitSeqNo) - 1) != lastSeqNo)) {
                            if (OfflineManager.getLastVisitedRetailers(retailerVisitedQry, Integer.parseInt(mStrVisitSeqNo))) {
                                if (lastSeqNo <= Integer.parseInt(mStrVisitSeqNo) || totalRetailers - lastSeqNo == 0) {
                                    startVisit();
                                } else {
                                    onAlertDialogDeviationRemarks();
                                }
                            } else {
                                if (totalRetailers - lastSeqNo == 0) {
                                    startVisit();
                                } else {
                                    onAlertDialogDeviationRemarks();
                                }
                            }
                        } else {
                            startVisit();
                        }
                    } else {
                        if (visitCount == totalRetailers) {
                            startVisit();
                        } else {
                            onAlertDialogDeviationRemarks();
                        }
                    }
                } else {
                    startVisit();
                }
            } else {
                startVisit();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void selectPage(int pageIndex) {
        tabLayout.setScrollPosition(pageIndex, 0f, false);
        viewPager.setCurrentItem(pageIndex, false);
    }

    private void checkVisitStartLocPermission() {
        pdLoadDialog = Constants.showProgressDialog(RetailersDetailsActivity.this, "", getString(R.string.checking_pemission));
        LocationUtils.checkLocationPermission(RetailersDetailsActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                dismissProgressDialog();
                if (status) {
                    onVisitAction();
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
                    LocationUtils.checkLocationPermission(RetailersDetailsActivity.this, new LocationInterface() {
                        @Override
                        public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                            if (status) {
                                if (mBoolBackBtnPressed) {
                                    onVisitClosingAction();
                                } else {
                                    onVisitAction();
                                }
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
                if (mBoolBackBtnPressed) {
                    onVisitClosingAction();
                } else {
                    onVisitAction();
                }
            }
        }
    }


    private void onVisitAction() {
        mBoolBackBtnPressed = false;
        if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList) ||
                mStrComingFrom.equalsIgnoreCase(Constants.CustomerList)
                || mStrComingFrom.equalsIgnoreCase(Constants.RouteList)
                || mStrComingFrom.equalsIgnoreCase(Constants.OtherRouteList)) {

            if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList)) {
                try {
                    String cpguid = mStrBundleCpGuid.replaceAll("-", "");
                    String routeGuidQry = Constants.RouteSchedulePlans + "?$filter=VisitCPGUID eq '" + cpguid + "'";
                    mStrRouteGuid = OfflineManager.getRouteScheduleGuid(routeGuidQry);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            Constants.MapEntityVal.clear();

            String qry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mCpGuid.guidAsString32().toUpperCase() + "' " +
                    "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01'";

            try {
                mStrVisitId = OfflineManager.getVisitDetails(qry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }

            if (!Constants.MapEntityVal.isEmpty()) {
                mBooleanVisitStarted = true;
            } else {
                mBooleanVisitStarted = false;
            }
            try {
                mStrVisitSeqNoFromEntity = Constants.MapEntityVal.get(Constants.VisitSeq).toString();
            } catch (Exception e) {
                mStrVisitSeqNoFromEntity = "";
                e.printStackTrace();
            }

            mBooleanSaveStart = false;
            mBooleanNavPrvVisitClosed = false;
            Constants.MapEntityVal.clear();

            String attdIdStr = "";
            String attnQry = Constants.Attendances + "?$filter=EndDate eq null and StartDate eq datetime'" + UtilConstants.getNewDate() + "' ";
            try {
                attdIdStr = OfflineManager.getAttendance(attnQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            if (!attdIdStr.equalsIgnoreCase("")) {
                if (!mBooleanVisitStarted) {
                    mStrOtherRetailerGuid = "";

                    //new 28112016
                    String otherRetVisitQuery = Constants.Visits + "?$filter=EndDate eq null and CPGUID ne '" + mCpGuid.guidAsString32().toUpperCase() + "' " +
                            "and StartDate eq datetime'" + UtilConstants.getNewDate() + "'and " + Constants.StatusID + " eq '01'";

                    String[] otherRetDetails = new String[3];
                    try {
                        otherRetDetails = OfflineManager.checkVisitForOtherRetailer(otherRetVisitQuery);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }

                    if (otherRetDetails[0] == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                RetailersDetailsActivity.this, R.style.MyTheme);

                        builder.setMessage(R.string.alert_start_visit)
                                .setCancelable(false)
                                .setPositiveButton(
                                        R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                dialog.cancel();
//                                                startVisit();
                                                startVisitBaseOnOutletSeq();
                                            }
                                        });
                        builder.setNegativeButton(R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        onRefreshVisitIcon();
                                    }

                                });

                        builder.show();
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                RetailersDetailsActivity.this, R.style.MyTheme);
                        final String[] finalOtherRetDetails = otherRetDetails;

                        builder.setMessage(getString(R.string.visit_end_not_marked_for_specific_retailer, otherRetDetails[0]))
                                .setCancelable(false)
                                .setPositiveButton(
                                        R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int id) {
                                                dialog.cancel();
                                                pdLoadDialog = Constants.showProgressDialog(RetailersDetailsActivity.this, "", getString(R.string.gps_progress));
                                                Constants.getLocation(RetailersDetailsActivity.this, new LocationInterface() {
                                                    @Override
                                                    public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                        dismissProgressDialog();
                                                        if (status) {
                                                            mStrOtherRetailerGuid = finalOtherRetDetails[1];
                                                            mStrVisitSeqNoFromEntity = finalOtherRetDetails[2];

                                                            boolean isVisitActivities = false;
                                                            try {
                                                                isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrOtherRetailerGuid + "'");
                                                            } catch (OfflineODataStoreException e) {
                                                                e.printStackTrace();
                                                            }
                                                            mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                                                            if (isVisitActivities) {
                                                                mBooleanVisitEndDialog = true;
                                                                onSaveClose();
                                                            } else if (beatOptmEnabled.equalsIgnoreCase(Constants.isBeatOptmTcode)) {
                                                                if (mStrVisitSeqNoFromEntity.equalsIgnoreCase("")) {
                                                                    mBooleanVisitEndDialog = true;
                                                                    onSaveClose();
                                                                }
                                                            } else {
                                                                wantToCloseDialog = false;
                                                                onAlertDialogForVisitDayEndRemarks();
                                                            }
                                                        }
                                                    }
                                                });

                                            }
                                        });
                        builder.setNegativeButton(R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        onRefreshVisitIcon();
                                    }

                                });

                        builder.show();

                    }
                } else {
                    mBoolMsg = false;
                    onVisitClosingAction();
                }
            } else {
//                    String mStrAttendanceId = "",mStrPreviousDate="";
//                    String prvDayQry = Constants.Attendances + "?$filter=EndDate eq null and StartDate ne datetime'" + UtilConstants.getNewDate() + "' ";
//                    try {
//                        mStrAttendanceId = OfflineManager.getAttendance(prvDayQry);
//                        if (!mStrAttendanceId.equalsIgnoreCase("")) {
//                            mStrPreviousDate = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate));
//                        } else {
//                            mStrPreviousDate = "";
//                        }
//
//                    } catch (OfflineODataStoreException e) {
//                        mStrPreviousDate="";
//                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
//                    }
//                    if(mStrPreviousDate.equalsIgnoreCase("")){
                UtilConstants.showAlert(getString(R.string.alert_plz_start_day), RetailersDetailsActivity.this);
//                    }else{
//                        UtilConstants.showAlert(getString(R.string.msg_close_previous_day_end), RetailersDetailsActivity.this);
//                    }

            }

        }
    }

    private void startVisit() {
        pdLoadDialog = Constants.showProgressDialog(RetailersDetailsActivity.this, "", getString(R.string.gps_progress));
        Constants.getLocation(RetailersDetailsActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                dismissProgressDialog();
                if (status) {
                    mBooleanSaveStart = true;
                    mBooleanVisitStartDialog = true;
                    mBooleanVisitEndDialog = false;
                    onSaveStart();
                    iv_visit_status.setImageResource(R.drawable.stop);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        mBoolMsg = true;
        mBoolBackBtnPressed = true;
        checkVisitEndLocPermission();
    }

    private void checkVisitEndLocPermission() {
        pdLoadDialog = Constants.showProgressDialog(RetailersDetailsActivity.this, "", getString(R.string.checking_pemission));
        LocationUtils.checkLocationPermission(RetailersDetailsActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                dismissProgressDialog();
                if (status) {
                    onVisitClosingAction();
                }
            }
        });
    }

    private void onVisitClosingAction() {
        if (mStrComingFrom.equalsIgnoreCase(Constants.RetailerList)) {
            Intent intRouteList = new Intent(RetailersDetailsActivity.this,
                    RetailersListActivity.class);
            intRouteList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intRouteList);
        } else {

            mStrVisitId = null;

            String visitQry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" +
                    mCpGuid.guidAsString32().toUpperCase() + "' and StartDate eq datetime'" +
                    UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01'";


            try {
                mStrVisitId = OfflineManager.getVisitDetails(visitQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }

            if (mStrVisitId != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        RetailersDetailsActivity.this, R.style.MyTheme);

                String mStrEndDilaog = "", mStrPostive = "", mStrNegative = "";

                try {
                    mStrVisitSeqNoFromEntity = Constants.MapEntityVal.get(Constants.VisitSeq).toString();
                } catch (Exception e) {
                    mStrVisitSeqNoFromEntity = "";
                    e.printStackTrace();
                }

                if (mBoolMsg) {
                    mStrEndDilaog = getString(R.string.alert_visit_pause);
                    mStrPostive = getString(R.string.mark_now);
                    mStrNegative = getString(R.string.later);
                } else {
                    mStrEndDilaog = getString(R.string.alert_end_visit);
                    mStrPostive = getString(R.string.yes);
                    mStrNegative = getString(R.string.no);
                }


                builder.setMessage(mStrEndDilaog)
                        .setCancelable(false)
                        .setPositiveButton(mStrPostive,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        pdLoadDialog = Constants.showProgressDialog(RetailersDetailsActivity.this, "", getString(R.string.gps_progress));
                                        Constants.getLocation(RetailersDetailsActivity.this, new LocationInterface() {
                                            @Override
                                            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                dismissProgressDialog();
                                                if (status) {

                                                    boolean isVisitActivities = false;
                                                    try {
                                                        isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrVisitId.guidAsString36() + "'");
                                                    } catch (OfflineODataStoreException e) {
                                                        e.printStackTrace();
                                                    }
                                                    if (isVisitActivities) {
                                                        mBooleanNavPrvVisitClosed = true;
                                                        mBooleanVisitStartDialog = false;
                                                        mBooleanVisitEndDialog = true;
                                                        iv_visit_status.setImageResource(R.drawable.start);
                                                        onSaveClose();
                                                    } else if (beatOptmEnabled.equalsIgnoreCase(Constants.isBeatOptmTcode)) {
                                                        if (mStrVisitSeqNoFromEntity.equalsIgnoreCase("")) {
                                                            mBooleanNavPrvVisitClosed = true;
                                                            mBooleanVisitStartDialog = false;
                                                            mBooleanVisitEndDialog = true;
                                                            iv_visit_status.setImageResource(R.drawable.start);
                                                            onSaveClose();
                                                        }
                                                    } else {
                                                        wantToCloseDialog = false;
                                                        onAlertDialogForVisitDayEndRemarks();
                                                    }

                                                }
                                            }
                                        });
                                    }
                                });
                builder.setNegativeButton(mStrNegative,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                                NavigateToListScreen();

                            }

                        });


                builder.show();
            } else {
                NavigateToListScreen();
            }
        }
    }

    /*
       TODO Check Network available or not
       */
    private void onNoNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                RetailersDetailsActivity.this, R.style.MyTheme);
        builder.setMessage(
                R.string.alert_sync_cannot_be_performed)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (mBooleanNavPrvVisitClosed) {
                            NavigateToListScreen();
                        } else {
                            if (mBooleanSaveStart) {
                            }
                        }


                    }
                });

        builder.show();
    }



 /*
           TODO Refresh Visit Status Icon
           */

    private void onRefreshVisitIcon() {

        String mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "' " +
                "and CPGUID eq '" + mCpGuid.guidAsString32().toUpperCase() + "' and " + Constants.StatusID + " eq '01'";
        try {
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                iv_visit_status.setImageResource(R.drawable.ic_done);
                mBooleanVisitStarted = false;
            } else {
                Constants.MapEntityVal.clear();
                String qry = Constants.Visits + "?$filter=EndDate eq null and CPGUID eq '" + mCpGuid.guidAsString32().toUpperCase() + "' " +
                        "and StartDate eq datetime'" + UtilConstants.getNewDate() + "' and " + Constants.StatusID + " eq '01' ";
                try {
                    mStrVisitId = OfflineManager.getVisitDetails(qry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                if (!Constants.MapEntityVal.isEmpty()) {
                    iv_visit_status.setImageResource(R.drawable.stop);
                    mBooleanVisitStarted = true;
                } else {
                    iv_visit_status.setImageResource(R.drawable.start);
                    mBooleanVisitStarted = false;
                }
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /*
              TODO Initialize Tab
              */
    private void tabIntilize() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        tabLayout.setupWithViewPager(viewPager);
    }

    /*
             TODO Set up fragments into adapter

             */
    private void setupViewPager() {
        ViewPagerTabAdapter adapter = new ViewPagerTabAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putString(Constants.CPGUID32, mCpGuid.guidAsString32().toUpperCase());
        bundle.putString(Constants.RetailerName, mStrCustomerName);
        bundle.putString(Constants.CPNo, mStrCustomerId);
        bundle.putString(Constants.CPUID, mStrUID);
        bundle.putString(Constants.CPGUID, mCpGuid.guidAsString36().toUpperCase());

        AddressFragment addressFragment = AddressFragment.newInstance(mStrCustNo, mStrCustomerName, mStrBundleCpGuid);

        ReportsFragment reportsFragment = new ReportsFragment();
        reportsFragment.setArguments(bundle);

//        SummaryFragment summaryFragment = new SummaryFragment();
//        summaryFragment.setArguments(bundle);

        adapter.addFrag(addressFragment, Constants.Address);
        if (mBooleanVisitStarted) {
            if (mStrComingFrom.equalsIgnoreCase(Constants.AdhocList) ||
                    mStrComingFrom.equalsIgnoreCase(Constants.CustomerList)
                    || mStrComingFrom.equalsIgnoreCase(Constants.RouteList)
                    || mStrComingFrom.equalsIgnoreCase(Constants.OtherRouteList)) {

                VisitFragment visitFragment = new VisitFragment();

                Bundle bundleVisit = new Bundle();
                bundleVisit.putString(Constants.CPGUID32, mCpGuid.guidAsString32().toUpperCase());
                bundleVisit.putString(Constants.RetailerName, mStrCustomerName);
                bundleVisit.putString(Constants.CPNo, mStrCustomerId);
                bundleVisit.putString(Constants.CPUID, mStrUID);
                bundleVisit.putString(Constants.CPGUID, mCpGuid.guidAsString36().toUpperCase());
                bundleVisit.putString(Constants.comingFrom, mStrComingFrom);
                visitFragment.setArguments(bundleVisit);
                adapter.addFrag(visitFragment, Constants.Visit);
            }
        }

        adapter.addFrag(reportsFragment, Constants.Reports);

//        adapter.addFrag(summaryFragment, Constants.Summary);

        viewPager.setAdapter(adapter);


        if (Constants.ComingFromCreateSenarios.equalsIgnoreCase(Constants.X))
            viewPager.setCurrentItem(1);
        Constants.ComingFromCreateSenarios = "";
    }

    private void setupViewPagerWithVisit() {
        viewPager.setAdapter(null);
        ArrayList<FragmentWithTitleBean> fragmentWithTitleBeanArrayList = new ArrayList<>();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.CPGUID32, mCpGuid.guidAsString32().toUpperCase());
        bundle.putString(Constants.RetailerName, mStrCustomerName);
        bundle.putString(Constants.CPNo, mStrCustomerId);
        bundle.putString(Constants.CPUID, mStrUID);
        bundle.putString(Constants.CPGUID, mCpGuid.guidAsString36().toUpperCase());

        AddressFragment addressFragment = AddressFragment.newInstance(mStrCustNo, mStrCustomerName, mStrBundleCpGuid);

        ReportsFragment reportsFragment = new ReportsFragment();
        reportsFragment.setArguments(bundle);

//        SummaryFragment summaryFragment = new SummaryFragment();
//        summaryFragment.setArguments(bundle);

        fragmentWithTitleBeanArrayList.add(new FragmentWithTitleBean(addressFragment, Constants.Address));

        VisitFragment visitFragment = new VisitFragment();

        Bundle bundleVisit = new Bundle();
        bundleVisit.putString(Constants.CPGUID32, mCpGuid.guidAsString32().toUpperCase());
        bundleVisit.putString(Constants.RetailerName, mStrCustomerName);
        bundleVisit.putString(Constants.CPNo, mStrCustomerId);
        bundleVisit.putString(Constants.CPUID, mStrUID);
        bundleVisit.putString(Constants.CPGUID, mCpGuid.guidAsString36().toUpperCase());
        bundleVisit.putString(Constants.comingFrom, mStrComingFrom);
        visitFragment.setArguments(bundleVisit);
        fragmentWithTitleBeanArrayList.add(new FragmentWithTitleBean(visitFragment, Constants.Visit));

        fragmentWithTitleBeanArrayList.add(new FragmentWithTitleBean(reportsFragment, Constants.Reports));

//        fragmentWithTitleBeanArrayList.add(new FragmentWithTitleBean(summaryFragment, Constants.Summary));

        RetailerDetailPagetTabAdapter visitAdapter = new RetailerDetailPagetTabAdapter(getSupportFragmentManager(), fragmentWithTitleBeanArrayList);
        viewPager.setAdapter(visitAdapter);

        viewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


        if (Constants.ComingFromCreateSenarios.equalsIgnoreCase(Constants.X))
            viewPager.setCurrentItem(1);
        Constants.ComingFromCreateSenarios = "";
    }

    private void onAlertDialogForVisitDayEndRemarks() {

        int MAX_LENGTH = 205;
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.visit_remarks, null, true);
        final EditText etVisitEndRemarks = (EditText) dialogView.findViewById(R.id.etremarks);
        spVisitEndReason = (Spinner) dialogView.findViewById(R.id.spReason);


        ArrayAdapter<RemarkReasonBean> reasonadapter = new ArrayAdapter<>(RetailersDetailsActivity.this, R.layout.custom_textview, reasonCodedesc);
        reasonadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVisitEndReason.setAdapter(reasonadapter);
        spVisitEndReason.setBackgroundResource(R.drawable.spinner_bg);
        spVisitEndReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedReasonDesc = reasonCodedesc.get(i).getReasonDesc();
                selectedReasonCode = reasonCodedesc.get(i).getReasonCode();
                // if(selectedReasonCode.equalsIgnoreCase("00")){
                if (isValid == false) {
                    if (selectedReasonCode.equalsIgnoreCase("00")) {
                        spVisitEndReason.setBackgroundResource(R.drawable.error_spinner);
                    } else {
                        spVisitEndReason.setBackgroundResource(R.drawable.spinner_bg);
                        isValid = true;
                    }
                } else {
                    spVisitEndReason.setBackgroundResource(R.drawable.spinner_bg);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (isValid == false) {
            spVisitEndReason.setBackgroundResource(R.drawable.error_spinner);
        }

        if (wantToCloseDialog) {
            etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);

        } else {
            etVisitEndRemarks.setBackgroundResource(R.drawable.edittext);
        }


        etVisitEndRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (wantToCloseDialog) {
                    etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);
                    wantToCloseDialog = false;
                } else {
                    etVisitEndRemarks.setBackgroundResource(R.drawable.edittext);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        InputFilter[] FilterArray = new InputFilter[2];
        FilterArray[0] = new InputFilter.LengthFilter(MAX_LENGTH);
        FilterArray[1] = Constants.getNumberAlphabetOnly();
        etVisitEndRemarks.setFilters(FilterArray);

        etVisitEndRemarks.setText(mStrVisitEndRemarks.equalsIgnoreCase("") ? mStrVisitEndRemarks : "");

        AlertDialog.Builder alertDialogVisitEndRemarks = new AlertDialog.Builder(RetailersDetailsActivity.this, R.style.MyTheme);
        alertDialogVisitEndRemarks.setMessage(R.string.alert_plz_enter_remarks);
        alertDialogVisitEndRemarks.setCancelable(false);
        alertDialogVisitEndRemarks.setView(dialogView);
        alertDialogVisitEndRemarks.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        if (selectedReasonCode.equalsIgnoreCase("00")) {
                            // error


                            mStrPopUpText = getString(R.string.alert_please_select_remarks);

                            wantToCloseDialog = false;
                            isValid = false;
                            UtilConstants.dialogBoxWithCallBack(RetailersDetailsActivity.this, "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
                                @Override
                                public void clickedStatus(boolean b) {

                                    onAlertDialogForVisitDayEndRemarks();
                                }
                            });
                        } else if (selectedReasonCode.equalsIgnoreCase(Constants.str_06)) {

                            try {
                                mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                                mStrVisitEndRemarks = "";
                            }

                            if (mStrVisitEndRemarks.equalsIgnoreCase("")) {
                                mStrPopUpText = getString(R.string.alert_please_enter_remarks);
                                etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);
                                wantToCloseDialog = true;
                                UtilConstants.dialogBoxWithCallBack(RetailersDetailsActivity.this, "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
                                    @Override
                                    public void clickedStatus(boolean b) {

                                        onAlertDialogForVisitDayEndRemarks();
                                    }
                                });
                            } else {
                                if (mStrVisitEndRemarks.equalsIgnoreCase("")) {
                                    mStrVisitEndRemarks = selectedReasonDesc;
                                } else {
                                    mStrVisitEndRemarks = selectedReasonDesc + " " + mStrVisitEndRemarks;
                                }

                                pdLoadDialog = Constants.showProgressDialog(RetailersDetailsActivity.this, "", getString(R.string.gps_progress));
                                Constants.getLocation(RetailersDetailsActivity.this, new LocationInterface() {
                                    @Override
                                    public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                        dismissProgressDialog();
                                        if (status) {
                                            if (mStrOtherRetailerGuid.equalsIgnoreCase(""))
                                                mBooleanNavPrvVisitClosed = true;
                                            else
                                                mBooleanNavPrvVisitClosed = false;

                                            mBooleanVisitStartDialog = false;
                                            mBooleanVisitEndDialog = true;

                                            wantToCloseDialog = false;
                                            onSaveClose();
                                            onRefreshVisitIcon();
                                        }
                                    }
                                });
                            }

                        } else {
                            try {
                                mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                                mStrVisitEndRemarks = "";
                            }
                            if (mStrVisitEndRemarks.equalsIgnoreCase("")) {
                                mStrVisitEndRemarks = selectedReasonDesc;
                            } else {
                                mStrVisitEndRemarks = selectedReasonDesc + " " + mStrVisitEndRemarks;
                            }

                            if (mStrVisitEndRemarks.equalsIgnoreCase("")) {
                                etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);
                                wantToCloseDialog = true;
                                onAlertDialogForVisitDayEndRemarks();
                            } else {
                                pdLoadDialog = Constants.showProgressDialog(RetailersDetailsActivity.this, "", getString(R.string.gps_progress));
                                Constants.getLocation(RetailersDetailsActivity.this, new LocationInterface() {
                                    @Override
                                    public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                        dismissProgressDialog();
                                        if (status) {
                                            if (mStrOtherRetailerGuid.equalsIgnoreCase(""))
                                                mBooleanNavPrvVisitClosed = true;
                                            else
                                                mBooleanNavPrvVisitClosed = false;

                                            mBooleanVisitStartDialog = false;
                                            mBooleanVisitEndDialog = true;

                                            wantToCloseDialog = false;
                                            onSaveClose();
                                            onRefreshVisitIcon();
                                        }
                                    }
                                });

                            }
                        }


                    }
                });

        alertDialogVisitEndRemarks.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                    }
                });

        final AlertDialog alertDialog = alertDialogVisitEndRemarks.create();
        alertDialog.show();


    }

    private void getReasonValues() {

        String query = Constants.ValueHelps + "?$filter= PropName eq '" + "Reason" + "' and EntityType eq 'Visit' &$orderby=" + Constants.ID + "";
        try {
            reasonCodedesc = OfflineManager.getRemarksReason(query);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }

    private void getSeqDevDesc() {

        String query = Constants.TypeSetSEQDEV;
        try {
            seqDevCodeDesc = OfflineManager.getVisitDeviationRemarks(query);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    //DeviationalRemarks Alter Dialog

    private void onAlertDialogDeviationRemarks() {

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.visit_remarks, null, true);
        final EditText etVisitEndRemarks = (EditText) dialogView.findViewById(R.id.etremarks);
        spVisitEndReason = (Spinner) dialogView.findViewById(R.id.spReason);

        ArrayAdapter<RemarkReasonBean> reasonadapter = new ArrayAdapter<>(RetailersDetailsActivity.this, R.layout.custom_textview, seqDevCodeDesc);
        reasonadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVisitEndReason.setAdapter(reasonadapter);
        spVisitEndReason.setBackgroundResource(R.drawable.spinner_bg);
        spVisitEndReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedReasonDesc = seqDevCodeDesc.get(i).getReasonDesc();
                selectedReasonCode = seqDevCodeDesc.get(i).getReasonCode();
                // if(selectedReasonCode.equalsIgnoreCase("00")){
                if (isValid == false) {
                    if (selectedReasonCode.equalsIgnoreCase("00")) {
                        spVisitEndReason.setBackgroundResource(R.drawable.error_spinner);
                    } else {
                        spVisitEndReason.setBackgroundResource(R.drawable.spinner_bg);
                        isValid = true;
                    }
                } else {
                    spVisitEndReason.setBackgroundResource(R.drawable.spinner_bg);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (isValid == false) {
            spVisitEndReason.setBackgroundResource(R.drawable.error_spinner);
        }


        etVisitEndRemarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


//        etVisitEndRemarks.setText(mStrVisitEndRemarks.equalsIgnoreCase("") ? mStrVisitEndRemarks : "");

        AlertDialog.Builder alertDialogVisitEndRemarks = new AlertDialog.Builder(RetailersDetailsActivity.this, R.style.MyTheme);
        alertDialogVisitEndRemarks.setMessage(R.string.alert_plz_enter_deviation_reason);
        alertDialogVisitEndRemarks.setCancelable(false);
        alertDialogVisitEndRemarks.setView(dialogView);
        alertDialogVisitEndRemarks.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        if (selectedReasonCode.equalsIgnoreCase("00")) {
                            // error


                            mStrPopUpText = getString(R.string.alert_please_select_remarks);
                            isValid = false;

                            UtilConstants.dialogBoxWithCallBack(RetailersDetailsActivity.this, "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
                                @Override
                                public void clickedStatus(boolean b) {

                                    onAlertDialogDeviationRemarks();
                                }
                            });
                        } else {

                            try {
                                mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                                mStrVisitEndRemarks = "";
                            }


                            mStrVisitEndRemarks = selectedReasonDesc + " " + mStrVisitEndRemarks;


//                            pdLoadDialog = Constants.showProgressDialog(RetailersDetailsActivity.this, "", getString(R.string.gps_progress));
//                            Constants.getLocation(RetailersDetailsActivity.this, new LocationInterface() {
//                                @Override
//                                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
//                                    dismissProgressDialog();
//                                    if (status) {
//                                        if (mStrOtherRetailerGuid.equalsIgnoreCase(""))
//                                            mBooleanNavPrvVisitClosed = true;
//                                        else
//
//                                            mBooleanSaveStart = true;
//                                        mBooleanVisitStartDialog = true;
//                                        mBooleanVisitEndDialog = false;
//
////                                        wantToCloseDialog = false;
//                                        onSaveStart();
//                                        onRefreshVisitIcon();
//                                    }
//                                }
//                            });

                            startVisit();
                        }

                    }
                });


        alertDialogVisitEndRemarks.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        onRefreshVisitIcon();
//                        mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                    }
                });

        final AlertDialog alertDialog = alertDialogVisitEndRemarks.create();
        alertDialog.show();
    }

    /*
    TODO Async task for Closing Visit End
    */
    private class ClosingVisit extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(RetailersDetailsActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(100);

                Hashtable table = new Hashtable();

                try {

                    if (!mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                        mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                    }
                    ODataEntity visitEntity;
                    visitEntity = OfflineManager.getVisitDetailsByKey(mStrVisitId);

                    if (visitEntity != null) {
                        oDataProperties = visitEntity.getProperties();
                        oDataProperty = oDataProperties.get(Constants.StartLat);
                        //noinspection unchecked
                        table.put(Constants.StartLat, oDataProperty.getValue());
                        oDataProperty = oDataProperties.get(Constants.StartLong);
                        //noinspection unchecked
                        table.put(Constants.StartLong, oDataProperty.getValue());
                        oDataProperty = oDataProperties.get(Constants.STARTDATE);
                        //noinspection unchecked
                        table.put(Constants.STARTDATE, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.STARTTIME);
                        //noinspection unchecked
                        table.put(Constants.STARTTIME, oDataProperty.getValue());
                        oDataProperty = oDataProperties.get(Constants.NoOfOutlet);
                        try {
                            if (oDataProperty != null && oDataProperty.getValue() != null) {
                                table.put(Constants.NoOfOutlet, (String) oDataProperty.getValue());
                            }
                        } catch (Exception e) {
                            table.put(Constants.NoOfOutlet, "");
                            e.printStackTrace();
                        }
                        oDataProperty = oDataProperties.get(Constants.VisitCatID);
                        //noinspection unchecked
                        table.put(Constants.VisitCatID, oDataProperty.getValue());

                        //noinspection unchecked
                        table.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.latitude));
                        //noinspection unchecked
                        table.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.longitude));
                        //noinspection unchecked
                        table.put(Constants.ENDDATE, UtilConstants.getNewDateTimeFormat());

                        //noinspection unchecked
                        oDataProperty = oDataProperties.get(Constants.CPNo);
                        table.put(Constants.CPNo, UtilConstants.removeLeadingZeros((String) (oDataProperty.getValue())));
                        try {
                            oDataProperty = oDataProperties.get(Constants.CPName);
                            table.put(Constants.CPName, UtilConstants.removeLeadingZeros((String) (oDataProperty.getValue())));
                        } catch (Exception e) {
                            table.put(Constants.CPName, "");
                            e.printStackTrace();
                        }
                        //noinspection unchecked
                        table.put(Constants.VISITKEY, mStrVisitId.guidAsString36().toUpperCase());
                        //noinspection unchecked
                        table.put(Constants.Remarks, mStrVisitEndRemarks);

                        table.put(Constants.REASON, selectedReasonCode);

                        //noinspection unchecked
                        oDataProperty = oDataProperties.get(Constants.SPGUID);
                        ODataGuid mSPGUID = null;
                        try {
                            mSPGUID = (ODataGuid) oDataProperty.getValue();
                            table.put(Constants.SPGUID, mSPGUID.guidAsString36().toUpperCase());
                        } catch (Exception e) {
                            table.put(Constants.SPGUID, Constants.getSPGUID());
                        }


                        oDataProperty = oDataProperties.get(Constants.ROUTEPLANKEY);

                        //noinspection unchecked
                        if (oDataProperty.getValue() == null) {
                            table.put(Constants.ROUTEPLANKEY, "");
                        } else {
                            ODataGuid mRouteGuid = (ODataGuid) oDataProperty.getValue();

                            table.put(Constants.ROUTEPLANKEY, mRouteGuid.guidAsString36().toUpperCase());
                        }


                        oDataProperty = oDataProperties.get(Constants.StatusID);
                        table.put(Constants.StatusID, oDataProperty.getValue());

                        oDataProperty = oDataProperties.get(Constants.CPTypeID);
                        table.put(Constants.CPTypeID, oDataProperty.getValue());


                        try {
                            oDataProperty = oDataProperties.get(Constants.VisitSeq);
                            table.put(Constants.VisitSeq, oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        oDataProperty = oDataProperties.get(Constants.CPGUID);
                        table.put(Constants.CPGUID, oDataProperty.getValue());

                        try {
                            oDataProperty = oDataProperties.get(Constants.VisitDate);
                            table.put(Constants.VisitDate, oDataProperty != null ? oDataProperty.getValue() : null);
                        } catch (Exception e) {
                            oDataProperty = null;
                            table.put(Constants.VisitDate, "");
                        }

                        table.put(Constants.ENDTIME, UtilConstants.getOdataDuration());

                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                        String loginIdVal = sharedPreferences.getString("username", "");
                        //noinspection unchecked
                        table.put(Constants.LOGINID, loginIdVal);

                        table.put(Constants.SetResourcePath, Constants.Visits + "(guid'" + mStrVisitId.guidAsString36().toUpperCase() + "')");

                        if (visitEntity.getEtag() != null) {
                            table.put(Constants.Etag, visitEntity.getEtag());
                        } else {
                        }

                        oDataProperty = oDataProperties.get(Constants.CreatedOn);
                        //noinspection unchecked
                        try {
                            table.put(Constants.CreatedOn, oDataProperty.getValue() == null ? UtilConstants.convertDateFormat(UtilConstants.getNewDateTimeFormat()) : oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        oDataProperty = oDataProperties.get(Constants.CreatedBy);
                        //noinspection unchecked
                        try {
                            table.put(Constants.CreatedBy, oDataProperty.getValue() == null ? loginIdVal : oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            oDataProperty = oDataProperties.get(Constants.ActualSeq);
                            table.put(Constants.ActualSeq, oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            oDataProperty = oDataProperties.get(Constants.DeviationReasonID);
                            table.put(Constants.DeviationReasonID, oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            oDataProperty = oDataProperties.get(Constants.DeviationRemarks);
                            table.put(Constants.DeviationRemarks, oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            oDataProperty = oDataProperties.get(Constants.BeatGUID);
                            table.put(Constants.BeatGUID, oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                try {
                    //noinspection unchecked
                    OfflineManager.updateVisit(table, RetailersDetailsActivity.this);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }


            } catch (InterruptedException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }
}
