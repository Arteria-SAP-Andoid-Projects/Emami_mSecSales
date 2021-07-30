package com.arteriatech.emami.main;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.emami.alerts.AlertsActivity;
import com.arteriatech.emami.asyncTask.OpenOfflineStoreAsyncTask;
import com.arteriatech.emami.asyncTask.RefreshAsyncTask;
import com.arteriatech.emami.asyncTask.SyncMustSellAsyncTask;
import com.arteriatech.emami.attendance.CreateAttendanceActivity;
import com.arteriatech.emami.attendance.DayEndRemarksActivity;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.common.RequestBuilder;
import com.arteriatech.emami.dbstock.DBStockActivity;
import com.arteriatech.emami.digitalProducts.DigitalProductActivity;
import com.arteriatech.emami.distributor.DistributorListActivity;
import com.arteriatech.emami.expense.ExpenseEntryActivity;
import com.arteriatech.emami.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.emami.interfaces.PasswordDialogCallbackInterface;
import com.arteriatech.emami.log.LogActivity;
import com.arteriatech.emami.master.AdhocListActivity;
import com.arteriatech.emami.master.CreateRetailerActivity;
import com.arteriatech.emami.master.RetailersListActivity;
import com.arteriatech.emami.master.UpdateRetailerListActivity;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.mbo.RemarkReasonBean;
import com.arteriatech.emami.msecsales.BuildConfig;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.notification.NotificationSetClass;
import com.arteriatech.emami.outletsurvey.OutletSurveyActivity;
import com.arteriatech.emami.reports.BehaviourListActivity;
import com.arteriatech.emami.reports.FOSTargetsActivity;
import com.arteriatech.emami.reports.HelpLineActivity;
import com.arteriatech.emami.reports.MyPerformanceActivity;
import com.arteriatech.emami.reports.MyStockActivity;
import com.arteriatech.emami.reports.MyTargetsActivity;
import com.arteriatech.emami.reports.SchemesActivity;
import com.arteriatech.emami.reports.VisitSummaryActivity;
import com.arteriatech.emami.reports.VisualAidActivity;
import com.arteriatech.emami.routeplan.RoutePlanListActivity;
import com.arteriatech.emami.scheme.SchemeListActivity;
import com.arteriatech.emami.service.MyWebService;
import com.arteriatech.emami.socreate.DaySummaryActivity;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.sync.SyncSelectionActivity;
import com.arteriatech.emami.sync.UpdatePendingRequest;
import com.arteriatech.emami.ui.CircularTextView;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;

import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreContext;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.smp.client.odata.impl.ODataGuidDefaultImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by e10742 on 05-12-2016.
 */
public class MainMenuFragment extends Fragment implements UIListener, MessageWithBooleanCallBack {
    TextView tv_today_achieved;
    public static Context context;
    private final String[] mArrStrTodayIconName = Constants.todayIconArray;
    private final String[] mArrStrReportsIconName = Constants.reportIconArray;
    private final String[] mArrStrAdminTextName = Constants.admintIconArray;
    private final int[] mArrIntMainMenuTempStatus = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
    private final int[] mArrIntMainMenuReportsTempStatus = {1, 1, 1};
    private final int[] mArrIntAdminOriginalStatus = {0, 0, 0};
    private final int[] mArrIntAdminTempStatus = {0, 1, 2};
    String mStrOtherRetailerGuid = "";
    String[][] delList = null;
    View myInflatedView = null;
    ODataGuid mStrVisitId = null;
    String mStrVisitEndRemarks = "";
    boolean wantToCloseDialog = false;
    private int[] mArrIntMainMenuOriginalStatus;
    private int[] mArrIntMainMenuReportsOriginalStatus = {0, 0, 0};
    private boolean mBooleanEndFlag = false;
    private boolean mBooleanStartFalg = false;
    private boolean mBooleanCompleteFlag = false;
    private String mStrPreviousDate = "";
    private String mStrPopUpText = "";
    private ProgressDialog pdLoadDialog, pdLoadAchivedDialog;
    private String mStrAttendanceId = "";
    private ODataPropMap oDataProperties;
    private ODataProperty oDataProperty;
    private boolean mBooleanDayStartDialog = false, mBooleanDayEndDialog = false, mBooleanDayResetDialog = false;
    private String stAttendanceConfigValue = "";
    private ArrayList<RemarkReasonBean> reasonCodedesc = new ArrayList<>();
    private String mStrVisitSeqNoFromEntity = "";
    private SharedPreferences sharedPerf;
    String strLocalVersionNumber = "";
    public MyWebReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = MainMenu.context;
       registerReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        myInflatedView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        /*Initialize user interfaces*/
       initUI();
        return myInflatedView;
    }


    private void registerReceiver() {
        try {
            IntentFilter filter = new IntentFilter(MyWebReceiver.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            receiver = new MyWebReceiver();
            context.registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Initializes user interfaces*/
    void initUI() {
        tv_today_achieved = (TextView) myInflatedView.findViewById(R.id.tv_today_achieved);
        sharedPerf = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        openStore();
//        setSharedPerfVal();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (pdLoadDialog == null) {
                setIconVisibility();
            } else if (!pdLoadDialog.isShowing()) {
                setIconVisibility();
            }
        } catch (Exception e) {
            setIconVisibility();
        }
    }

    private void onCreateRetailer() {
        Intent syncSelection = new Intent(getActivity(),
                CreateRetailerActivity.class);
        startActivity(syncSelection);
    }

    private void onUpdateRetailerList() {
        Intent syncSelection = new Intent(getActivity(),
                UpdateRetailerListActivity.class);
        startActivity(syncSelection);
    }

    private void onHelpLine() {
        Intent intentHelpLineActivity = new Intent(getActivity(),
                HelpLineActivity.class);
        startActivity(intentHelpLineActivity);
    }

    private void onDBStock() {
        Intent intentDBStockActivity = new Intent(getActivity(),
                DBStockActivity.class);
//        Intent intentDBStockActivity = new Intent(getActivity(),
//                DBStockMaterialActivity.class);
        startActivity(intentDBStockActivity);
    }

    private void onSchemes() {
        Intent intentSchemeActivity = new Intent(getActivity(),
                SchemesActivity.class);
        startActivity(intentSchemeActivity);
    }

    private void onBehaviourList() {
        Intent intentCollHisActivity = new Intent(getActivity(),
                BehaviourListActivity.class);
        startActivity(intentCollHisActivity);
    }


    private void onTargetList() {
        Intent intentCollHisActivity = new Intent(getActivity(),
                FOSTargetsActivity.class);
        startActivity(intentCollHisActivity);
    }

    private void onMyStock() {
        Constants.RCVStockValueDouble = 0.0;
        Constants.SIMStockValue = 0.0;
        Constants.SIMStockUOM = "";
        Intent intentCollHisActivity = new Intent(getActivity(),
                MyStockActivity.class);
        startActivity(intentCollHisActivity);
    }

    private void onMyPerformnce() {
        Intent intentCollHisActivity = new Intent(getActivity(),
                MyPerformanceActivity.class);
        startActivity(intentCollHisActivity);

    }


    /*
     * This method navigates to log view
     *
     */
    private void onLogView() {
        Intent intentLogView = new Intent(getActivity(), LogActivity.class);
        startActivity(intentLogView);
    }

    /*Navigates to Sync view*/
    private void onSyncView() {
        Intent intentSyncView = new Intent(getActivity(), SyncSelectionActivity.class);
        startActivity(intentSyncView);
    }

    /*Navigates to Retailer Behaviour*/
    private void onRetailerBehaviurList() {
        Intent intentSyncView = new Intent(getActivity(), BehaviourListActivity.class);
        startActivity(intentSyncView);
    }

    /*Navigates to Beat Plan*/
    private void onBeatPlan() {
        Constants.BoolOtherBeatLoaded = false;
        Constants.BoolTodayBeatLoaded = false;
        Intent intentBeatPlanActivity = new Intent(getActivity(),
                RoutePlanListActivity.class);
        startActivity(intentBeatPlanActivity);
    }

    /*Navigates to Distributor*/
    private void onDistributor() {
        Constants.BoolOtherBeatLoaded = false;
        Constants.BoolTodayBeatLoaded = false;
        Intent intentDistributorActivity = new Intent(getActivity(),
                DistributorListActivity.class);
        startActivity(intentDistributorActivity);
    }

    /*Navigates to Distributor*/
    private void onOutletSurvey() {
        Constants.BoolOtherBeatLoaded = false;
        Constants.BoolTodayBeatLoaded = false;
        Intent intentOutletSurveyActivity = new Intent(getActivity(),
                OutletSurveyActivity.class);
        startActivity(intentOutletSurveyActivity);
    }

    /*Navigates to My Targets*/
    private void onMyTargets() {
        Intent intentMyTargertActivity = new Intent(getActivity(),
                MyTargetsActivity.class);
        startActivity(intentMyTargertActivity);
    }

    /*Navigates to Adhoc List*/
    private void onAdhocList() {
        Intent syncSelection = new Intent(getActivity(),
                AdhocListActivity.class);
        startActivity(syncSelection);
    }

    /*Navigates to DB Stock List*/
    private void onDBStockList() {
        Intent syncSelection = new Intent(getActivity(),
                DBStockActivity.class);
        startActivity(syncSelection);
    }

    /*Navigates to Day Summary*/
    private void onDaySummary() {
        Intent syncSelection = new Intent(getActivity(),
                DaySummaryActivity.class);
        startActivity(syncSelection);
    }

    /*Navigates to Visit Summary*/
    private void onVisitSummary() {
        Intent syncSelection = new Intent(getActivity(),
                VisitSummaryActivity.class);
        startActivity(syncSelection);
    }

    /*Navigates to Visit Summary*/
    private void onVisualAid() {
        Intent syncSelection = new Intent(getActivity(),
                VisualAidActivity.class);
        startActivity(syncSelection);
    }

    /*navigates to expense entry*/
    private void onExpense() {
        Intent syncSelection = new Intent(getActivity(),
                ExpenseEntryActivity.class);
        startActivity(syncSelection);
    }

    /*navigates to Digital product*/
    private void onDigitalProduct() {
        Intent syncSelection = new Intent(getActivity(),
                DigitalProductActivity.class);
        startActivity(syncSelection);
    }

    /*navigates to Scheme product*/
    private void onScheme() {
        Constants.CPGUIDVAL = "";
        Intent syncSelection = new Intent(getActivity(),
                SchemeListActivity.class);
        startActivity(syncSelection);
    }

    /*Navigates to Alerts Screen*/
    private void onAlerts() {

        Intent syncSelection = new Intent(getActivity(),
                AlertsActivity.class);
        startActivity(syncSelection);
    }

    /*Ends day*/
    private void onSaveClose() {
        try {
            new ClosingDate().execute();
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

    }

    /*resets day*/
    private void onCloseUpdate() {
        mStrPopUpText = getString(R.string.msg_resetting_day_end);
        try {
            new ResettingDate().execute();
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

    }


    @Override
    public void onRequestError(int operation, Exception exception) {
//        LogManager.writeLogError("onRequestError(MainMenu) : "+exception.getMessage());
        ErrorBean errorBean = Constants.getErrorCode(operation, exception, getActivity());
        if (errorBean.hasNoError()) {
            Toast.makeText(getActivity(), getString(R.string.err_odata_unexpected, exception.getMessage()),
                    Toast.LENGTH_LONG).show();

            if (mBooleanDayStartDialog) {
                try {
                    mStrPopUpText = getString(R.string.err_msg_concat, getString(R.string.lbl_attence_start), exception.getMessage());
                } catch (Exception e) {
                    mStrPopUpText = getString(R.string.msg_start_upd_sync_error);
                }
            }
            if (mBooleanDayEndDialog) {
                try {
                    mStrPopUpText = getString(R.string.err_msg_concat, getString(R.string.lbl_attence_end), exception.getMessage());
                } catch (Exception e) {
                    mStrPopUpText = getString(R.string.msg_end_upd_sync_error);
                }
            }
            if (mBooleanDayResetDialog) {
                try {
                    mStrPopUpText = getString(R.string.err_msg_concat, getString(R.string.lbl_attence_reset), exception.getMessage());
                } catch (Exception e) {
                    mStrPopUpText = getString(R.string.msg_reset_upd_sync_error);
                }
            }

            if (mStrPopUpText.equalsIgnoreCase("")) {
                try {
                    mStrPopUpText = errorBean.getErrorMsg();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (operation == Operation.Create.getValue()) {
                closeProgressDialog();
                displayPopUpMsg();
            } else if (operation == Operation.Update.getValue()) {
                closeProgressDialog();
                displayPopUpMsg();
            } else if (operation == Operation.OfflineFlush.getValue()) {
                closeProgressDialog();

                displayPopUpMsg();
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                closeProgressDialog();
                displayPopUpMsg();
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                try {
                    closeProgressDialog();
                    UtilConstants.showAlert(getString(R.string.msg_offline_store_failure), getActivity());
                    setIconVisibility();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            } else {
                closeProgressDialog();
                displayPopUpMsg();
            }
        } else {
//            LogManager.writeLogError("onRequestError(MainMenu) : Check Store Failed Or not");
            if (errorBean.isStoreFailed()) {
//                LogManager.writeLogError("onRequestError(MainMenu) : Check Store Failed");
                OfflineManager.offlineStore = null;
                OfflineManager.options = null;
                closeProgressDialog();
                try {
                    new OpenOfflineStore().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
//                LogManager.writeLogError("onRequestError(MainMenu) : Check Store Not Failed");
                closeProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), getActivity());
                setIconVisibility();
            }


        }

    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.Create.getValue()) {
            if (Constants.getSyncType(getActivity(), Constants.Attendances, Constants.CreateOperation).equalsIgnoreCase("4")) {
                closeProgressDialog();
                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);
                if (!mStrOtherRetailerGuid.equalsIgnoreCase(""))
                    mStrPopUpText = getString(R.string.visit_ended);

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(getActivity())) {
                    closeProgressDialog();
                    UtilConstants.onNoNetwork(getActivity());
                } else {
                    OfflineManager.flushQueuedRequests(MainMenuFragment.this);
                }
            }
        } else if (operation == Operation.Update.getValue()) {
            if (Constants.getSyncType(getActivity(), Constants.Attendances, Constants.UpdateOperation).equalsIgnoreCase("4")) {

                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);

                if (!mBooleanDayEndDialog) {
                    closeProgressDialog();
                    try {
                        if (!mStrOtherRetailerGuid.equalsIgnoreCase(""))
                            mStrPopUpText = getString(R.string.visit_ended);

                        Constants.TodayActualVisitRetailersCount = Constants.getTodayActualVisitedRetCount("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    displayPopUpMsg();
                } else {
                    if (!UtilConstants.isNetworkAvailable(getActivity())) {
                        closeProgressDialog();
                        UtilConstants.onNoNetwork(getActivity());
                    } else {
                        if (Constants.iSAutoSync) {
                            closeProgressDialog();
                            mStrPopUpText = getString(R.string.alert_auto_sync_is_progress);
                            displayPopUpMsg();
                        } else {
                            Constants.isSync = true;
                            OfflineManager.flushQueuedRequests(MainMenuFragment.this);
                        }
                    }
                }


            } else {
                if (!UtilConstants.isNetworkAvailable(getActivity())) {
                    closeProgressDialog();
                    UtilConstants.onNoNetwork(getActivity());
                } else {
                    OfflineManager.flushQueuedRequests(MainMenuFragment.this);
                }
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {

            if (!UtilConstants.isNetworkAvailable(getActivity())) {
                closeProgressDialog();
                UtilConstants.onNoNetwork(getActivity());
            } else {

                String allCollection = "";
                if (mBooleanDayStartDialog) {
                    allCollection = Constants.Attendances + "," + Constants.SPStockItems + "," + Constants.SPStockItemDetails + "," + Constants.SPStockItemSNos + "," + Constants.SSINVOICES + "," + Constants.SSInvoiceItemDetails
                            + "," + Constants.SSInvoiceItemSerials + "," + Constants.FinancialPostings
                            + "," + Constants.FinancialPostingItemDetails
                            + "," + Constants.CPStockItems + "," + Constants.CPStockItemDetails + "," + Constants.CPStockItemSnos + "," + Constants.Schemes + "," + Constants.Tariffs + "," + Constants.SegmentedMaterials;
                } else {
                    allCollection = Constants.Attendances;
                }


                OfflineManager.refreshRequests(getActivity(), allCollection, MainMenuFragment.this);
            }


        } else if (operation == Operation.OfflineRefresh.getValue()) {

            if (!isDayStartSyncEnbled) {
                closeProgressDialog();
                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);
                UtilConstants.dialogBoxWithCallBack(getActivity(), "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        setIconVisibility();
                        AppUpgradeConfig.getUpdateAvailability(OfflineManager.offlineStore, getActivity(), "", false);
                    }
                });

            } else {
                getVisitData();
                closeProgressDialog();
                Constants.updateLastSyncTimeToTable(alAssignColl);
                mStrPopUpText = getString(R.string.msg_sync_successfully_completed);
                displayPopUpMsg();
            }

        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
   /*         new NotificationSetClass(getContext());
            try {
                OfflineManager.getAuthorizations(getActivity());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME,
                    0);
            if (settings.getBoolean(Constants.isFirstTimeReg, false)) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(Constants.isFirstTimeReg, false);
                editor.commit();
                try {
                    OfflineManager.getAuthorizations(getActivity());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setBirthDayRecordsToDataValut(getActivity());
                Constants.setAlertsRecordsToDataValut(getActivity());
                try {
                    String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                    String[] DEFINGREQARRAY = Constants.getDefinigReq(getActivity());


                    for (int incReq = 0; incReq < DEFINGREQARRAY.length; incReq++) {
                        String colName = DEFINGREQARRAY[incReq];
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }

                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName, Constants.TimeStamp, syncTime
                        );
                    }
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
                }
                LogManager.writeLogInfo(getString(R.string.msg_success_store_open));
            }
            Constants.setSyncTime(getActivity());

            try {
                new SyncMustSellAsyncTask(getActivity(), new MessageWithBooleanCallBack() {
                    @Override
                    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
                        Log.d("clickedStatus Req", clickedStatus+"");
                        setUI();
                    }
                }, "").execute();
            } catch (Exception e) {
                setUI();
                e.printStackTrace();
            }*/

            if (sharedPerf.getInt(Constants.CURRENT_VERSION_CODE, 0) == Constants.NewDefingRequestVersion) {
                refreshStore();
            } else {
                if (UtilConstants.isNetworkAvailable(getActivity())) {
                    if (!OfflineManager.offlineStore.getRequestQueueIsEmpty()) {
                        try {
                            OfflineManager.flushQueuedRequests(new UIListener() {
                                @Override
                                public void onRequestError(int i, Exception e) {
                                    refreshStore();
                                }

                                @Override
                                public void onRequestSuccess(int i, String s) throws ODataException, OfflineODataStoreException {
                                    increaseSharedPerfVal();
                                    Constants.closeStore(getActivity());
                                    new RefreshAsyncTask(getActivity(), "", MainMenuFragment.this).execute();
                                }
                            }, "");
                        } catch (OfflineODataStoreException e) {
                            refreshStore();
                            e.printStackTrace();
                        }
                    } else {
                        if (UtilConstants.isNetworkAvailable(getActivity())) {
                            Constants.isSync = true;
                            increaseSharedPerfVal();
                            Constants.closeStore(getActivity());
                            new RefreshAsyncTask(getActivity(), "", MainMenuFragment.this).execute();
                        } else {
                            refreshStore();
                        }
                    }
                } else {
                    refreshStore();
                }
            }

        }

    }

    private void increaseSharedPerfVal() {
        SharedPreferences.Editor editor = sharedPerf.edit();
        try {
            editor.putInt(Constants.CURRENT_VERSION_CODE, Constants.NewDefingRequestVersion);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* private void setSharedPerfVal(){
        SharedPreferences.Editor editor = sharedPerf.edit();
        try {
            if(!sharedPerf.contains(Constants.CURRENT_VERSION_CODE)){
                editor.putInt(Constants.CURRENT_VERSION_CODE, Constants.NewDefingRequestVersion);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void refreshStore() {
        new NotificationSetClass(getContext());
        try {
            OfflineManager.getAuthorizations(getActivity());
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME,
                0);
        if (settings.getBoolean(Constants.isFirstTimeReg, false)) {
          /*  SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.isFirstTimeReg, false);
            editor.commit();*/
            try {
                OfflineManager.getAuthorizations(getActivity());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setBirthDayRecordsToDataValut(getActivity());
            Constants.setAlertsRecordsToDataValut(getActivity());
            try {
                String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                String[] DEFINGREQARRAY = Constants.getDefinigReq(getActivity());


                for (int incReq = 0; incReq < DEFINGREQARRAY.length; incReq++) {
                    String colName = DEFINGREQARRAY[incReq];
                    if (colName.contains("?$")) {
                        String splitCollName[] = colName.split("\\?");
                        colName = splitCollName[0];
                    }

                    if (colName.contains("(")) {
                        String splitCollName[] = colName.split("\\(");
                        colName = splitCollName[0];
                    }

                    Constants.events.updateStatus(Constants.SYNC_TABLE,
                            colName, Constants.TimeStamp, syncTime
                    );
                }
            } catch (Exception exce) {
                LogManager.writeLogError(Constants.sync_table_history_txt + exce.getMessage());
            }
            LogManager.writeLogInfo(getString(R.string.msg_success_store_open));
        }
        Constants.setSyncTime(getActivity());

        try {
            new SyncMustSellAsyncTask(getActivity(), new MessageWithBooleanCallBack() {
                @Override
                public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
                    Log.d("clickedStatus Req", clickedStatus + "");
                    setUI();
                }
            }, "").execute();
        } catch (Exception e) {
            setUI();
            e.printStackTrace();
        }
    }

    private void setUI() {
        if (!Constants.isSpecificCollTodaySyncOrNot(Constants.getLastSyncDate(Constants.SYNC_TABLE, Constants.Collections,
                Constants.Attendances, Constants.TimeStamp, getActivity()))) {
            if (UtilConstants.isNetworkAvailable(getActivity())) {
                try {
                    isDayStartSyncEnbled = true;
                    pdLoadDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
                    Constants.FlagForUpdate = false;
                    Constants.isDayStartSyncEnbled = true;
                    Constants.isSync = false;
                    Constants.SyncTypeID = Constants.str_01;
                    SyncSelectionActivity.startService(getActivity());
                    SyncSelectionActivity.startServiceMustSells(getActivity());
                    UpdatePendingRequest.getInstance(this).callScheduleFirstLoginSync();

                } catch (Exception e) {
                    e.printStackTrace();
                    updateUI();
                }
            } else {
                isDayStartSyncEnbled = false;
                UtilConstants.dialogBoxWithCallBack(getActivity(), "", getString(R.string.msg_no_network), getString(R.string.ok), "", false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        AppUpgradeConfig.getUpdateAvailability(OfflineManager.offlineStore, getActivity(), "", false);
                    }
                });
                updateUI();
            }

        } else {
            updateUI();
            AppUpgradeConfig.getUpdateAvailability(OfflineManager.offlineStore, getActivity(), "", false);

        }

    }

    private void updateUI() {
        isDayStartSyncEnbled = false;
        try {
            getVisitData();
            startBackgroundService(getActivity());
            closeProgressDialog();
            setIconVisibility();
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    public static void startBackgroundService(Context mContext) {
//        UpdatePendingRequest.getInstance(null).callSchedule(Integer.toString(Constants.autoSyncDur));
    }

    private boolean isDayStartSyncEnbled = false;
    ArrayList<String> alAssignColl = new ArrayList<>();
    String concatCollectionStr = "";

    private void getAllSyncColl() {
        alAssignColl.clear();
        concatCollectionStr = "";
        alAssignColl.addAll(Constants.getDefinigReqList(getActivity()));
        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
    }

    private void closeProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void getVisitData() {
        Constants.setBirthDayRecordsToDataValut(getActivity());
        Constants.setAlertsRecordsToDataValut(getActivity());
        Constants.alTodayBeatRet.clear();
        Constants.TodayTargetRetailersCount = Constants.getVisitTargetForToday();
        Constants.TodayActualVisitRetailersCount = Constants.getVisitedRetailerCount(Constants.alTodayBeatRet);
        Constants.loadingTodayAchived(getActivity(), Constants.alTodayBeatRet);
    }

    public void displayPopUpMsg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
        builder.setMessage(mStrPopUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    setIconVisibility();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();
    }

    /*Sets icons visibility in screen for different T-Codes*/
    private void setIconVisibility() {


        mArrIntMainMenuOriginalStatus = new int[]{0, 0, 0, 0, 0
                , 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0};
        TextView tv_visit_achieved = (TextView) myInflatedView.findViewById(R.id.tv_visit_achieved);
        tv_visit_achieved.setText(getString(R.string.str_concat_visit_achieved, Constants.TodayActualVisitRetailersCount, Constants.TodayTargetRetailersCount));
        mBooleanEndFlag = false;
        mBooleanStartFalg = false;
        mBooleanCompleteFlag = false;

        //Initialize action bar with back button(false)
        ActionBarView.initActionBarView((MainMenu) getActivity(), false, Constants.getSalesPersonName());

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

        Constants.setIconVisibilty(sharedPreferences, mArrIntMainMenuOriginalStatus, mArrIntMainMenuReportsOriginalStatus);
        stAttendanceConfigValue = ConstantsUtils.getDayConfigs();
        if (stAttendanceConfigValue.equalsIgnoreCase(ConstantsUtils.C)) {
            Constants.MapEntityVal.clear();
            String dayEndClosedqry = Constants.Attendances + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' ";
            try {
                mStrAttendanceId = OfflineManager.getAttendance(dayEndClosedqry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            String startDateStr = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate));
            if (startDateStr.equalsIgnoreCase(UtilConstants.getNewDate())) {
                mArrIntMainMenuOriginalStatus[0] = 0;
            }
        }

        int arrInc = 0;
        int len = mArrIntMainMenuOriginalStatus.length;
        for (int incVal = 0; incVal < len; incVal++) {
            if (mArrIntMainMenuOriginalStatus[incVal] == 1) {
                mArrIntMainMenuTempStatus[arrInc] = incVal;
                arrInc++;
            }
        }


        arrInc = 0;
        len = mArrIntMainMenuReportsOriginalStatus.length;
        for (int incVal = 0; incVal < len; incVal++) {
            if (mArrIntMainMenuReportsOriginalStatus[incVal] == 1) {
                mArrIntMainMenuReportsTempStatus[arrInc] = incVal;
                arrInc++;
            }
        }

        mArrIntAdminOriginalStatus[0] = 1;
        mArrIntAdminOriginalStatus[1] = 1;
        mArrIntAdminOriginalStatus[2] = 1;
        arrInc = 0;
        len = mArrIntAdminOriginalStatus.length;
        for (int incVal = 0; incVal < len; incVal++) {
            if (mArrIntAdminOriginalStatus[incVal] == 1) {
                mArrIntAdminTempStatus[arrInc] = incVal;
                arrInc++;
            }
        }
        tv_today_achieved.setText(getString(R.string.str_concat_today_achieved, "" + UtilConstants.removeLeadingZerowithTwoDecimal(Constants.TodayAchivedPer + "") + " %"));
        GridView gvTodayAchieved = (GridView) getActivity().findViewById(R.id.gv_today_view);
        gvTodayAchieved.setAdapter(new TodayAchievedImageAdapter(getActivity()));

        GridView gvReportsView = (GridView) getActivity().findViewById(R.id.gv_reports);
        gvReportsView.setAdapter(new ReportsImageAdapter(getActivity()));

        GridView gvAdminView = (GridView) getActivity().findViewById(R.id.gv_admin);
        gvAdminView.setAdapter(new AdminImageAdapter(getActivity()));


        SharedPreferences settings = getActivity().getSharedPreferences(Constants.PREFS_NAME,
                0);
        if (settings.getBoolean(Constants.isFirstTimeReg, false))
        {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.isFirstTimeReg, false);
            editor.commit();
            showConformationDialogExtendPassword(context);
        }
        /*else
        {
            OpenOfflineStoreInitializeDB();
        }*/



    }

    private boolean checkIsTablet() {

        return (this.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void retailerList(Context mContext) {


        boolean status = checkIsTablet();

//        if (!status) {


        Intent retList = new Intent(getActivity(),
                RetailersListActivity.class);
        retList.putExtra(Constants.PassedFrom, 100);
        startActivity(retList);
//        } else {
//            /**
//             * Multi pane code for Retailers list
//             */
//            Intent retList = new Intent(getActivity(),
//                    RetailerActivity.class);
//            retList.putExtra(Constants.PassedFrom, 100);
//            startActivity(retList);
//        }

    }

    /*checks whether store open or not and if not opened opens store*/
    private void openStore() {
        if (OfflineManager.offlineStore != null) {
            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.openOfflineStore(getActivity(), MainMenuFragment.this);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            } else {
                if (!Constants.isSpecificCollTodaySyncOrNot(Constants.getLastSyncDate(Constants.SYNC_TABLE, Constants.Collections,
                        Constants.Attendances, Constants.TimeStamp, getActivity()))) {
                    if (UtilConstants.isNetworkAvailable(getActivity())) {

                        try {
                            syncSllCollection();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        isDayStartSyncEnbled = false;
                        UtilConstants.onNoNetwork(getActivity());
                    }

                } else {
                    AppUpgradeConfig.getUpdateAvailability(OfflineManager.offlineStore, getActivity(), "", false);
                }
            }
        } else {
            try {
                new OpenOfflineStore().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }


            getVersionCheck();
        }
    }

    private void syncSllCollection() {


//        if(!Constants.iSAutoSync) {
        try {
            Log.d("initialUpdateSync", "start calling call schedule");
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
            isDayStartSyncEnbled = true;
            Constants.FlagForUpdate = false;
            Constants.isDayStartSyncEnbled = true;
            Constants.isSync = false;
            UpdatePendingRequest.getInstance(MainMenuFragment.this).callScheduleFirstLoginSync();


        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
            updateUI();
        }
//        }
    }

    @Override
    public void onDestroy() {
        SyncSelectionActivity.stopService(getActivity());
        try {
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void clickedStatus(boolean clickedStatus, String err_msg, ErrorBean errorBean) {
        if (clickedStatus) {
            SyncSelectionActivity.stopService(getActivity());
            startBackgroundService(getActivity());
            Constants.isDayStartSyncEnbled = false;
            getVisitData();
            closeProgressDialog();
            Constants.iSAutoSync = false;
            if (Constants.mErrorCount == 0) {
                mStrPopUpText = getString(R.string.msg_sync_successfully_completed);
                UtilConstants.dialogBoxWithCallBack(getActivity(), "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        setIconVisibility();
                        AppUpgradeConfig.getUpdateAvailability(OfflineManager.offlineStore, getActivity(), "", false);
                    }
                });
            } else {
                try {
                    if (err_msg.equalsIgnoreCase("")) {
                        mStrPopUpText = getString(R.string.msg_error_occured_during_sync);
                    } else {
                        mStrPopUpText = err_msg;
                    }
                } catch (Exception e) {
                    mStrPopUpText = getString(R.string.msg_error_occured_during_sync);
                }
                displayPopUpMsg();
            }

        }
    }

    private void showConformationDialogExtendPassword(final Context context) {

        ConstantsUtils.showPasswordRemarksDialog((Activity) context, new PasswordDialogCallbackInterface() {
            @Override
            public void clickedStatus(boolean clickedStatus, String text) {
                if (clickedStatus)
                    extendPassword(context, text);
            }
        }, getString(R.string.alert_plz_enter_password));
    }

    private void extendPassword(Context context, String password) {
        if (UtilConstants.isNetworkAvailable(context)) {
            extendPWD(context, password);
        } else {
            try {
                Toast.makeText(context, getString(R.string.no_network_conn), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            UtilConstants.showErrorMsgSnackbar(contextView, getString(com.arteriatech.mutils.R.string.no_network_conn),getActivity());
        }

    }

    private void extendPWD(final Context mContext, String pUserPwd) {
        String pUserName = "";
        try {
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
            pUserName = lgCtx.getBackendUser();
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(pUserName) && !TextUtils.isEmpty(pUserPwd)) {
            extendPassword(mContext, com.arteriatech.emami.registration.Configuration.IDPURL, com.arteriatech.emami.registration.Configuration.IDPTUSRNAME, com.arteriatech.emami.registration.Configuration.IDPTUSRPWD, pUserName, pUserPwd);
        } else {
            try {
                Toast.makeText(mContext, "Unable to get Username and Password", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            UtilConstants.showErrorMsgSnackbar(contextView, "Unable to get Username and Password",mContext);
        }

    }

    private void extendPassword(final Context mContext, final String domineUrl, final String tUserName, final String tPsw, final String pUserID, final String password) {
        pdLoadDialog = new ProgressDialog(mContext, R.style.ProgressDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.extend_pwd_please_wait));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = domineUrl + "/service/scim/Users?filter=userName%20eq%20'" + pUserID + "'";
                String puserID = pUserID;

                try {
                    String jsonValue = ConstantsUtils.getPuserIdUtilsReponse(new URL(url), tUserName, tPsw);
                    if (!TextUtils.isEmpty(jsonValue)) {
                        JSONObject jsonObject = new JSONObject(jsonValue);
                        JSONArray jsonArray = jsonObject.optJSONArray("Resources");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            puserID = jsonArray.getJSONObject(0).getString("id");
                        }

                        if (!TextUtils.isEmpty(puserID)) {
                            String url1 = domineUrl + "/service/scim/Users/" + puserID;
                            String validatePuser = ConstantsUtils.getPuserIdUtilsReponse(new URL(url1), tUserName, tPsw);
                            if (!TextUtils.isEmpty(validatePuser)) {
                                JSONObject userObject = new JSONObject(validatePuser);
                                String userStatus = userObject.optString("passwordStatus");
                                JSONObject metaObject = userObject.getJSONObject("meta");
                                JSONArray schemasArray = userObject.optJSONArray("schemas");
                                JSONObject bodyObject = new JSONObject();
                                bodyObject.put("id", puserID);
                                bodyObject.put("password", password);
                                bodyObject.put("passwordStatus", "enabled");
                                bodyObject.put("meta", metaObject);
                                bodyObject.put("schemas", schemasArray);
                                String changePassword = ConstantsUtils.getPswResetUtilsReponse(new URL(url1), tUserName, tPsw, bodyObject.toString());
                                if (!TextUtils.isEmpty(changePassword)) {
                                    try {
                                        JSONObject userPObject = new JSONObject(changePassword);
                                        String userPStatus = userPObject.optString("passwordStatus");
                                        if (!TextUtils.isEmpty(userPStatus) && userPStatus.equalsIgnoreCase("enabled")) {
                                            setPwdInDataVault(mContext, password);
                                            displayErrorMessage(mContext.getString(R.string.extend_pwd_finish_success), mContext);
                                        } else {
                                            displayErrorMessage(mContext.getString(R.string.extend_pwd_error_occured) + " " + userPStatus, mContext);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        displayErrorMessage(changePassword + " Please use different password", mContext);
                                    }
                                } else {
                                    displayErrorMessage(mContext.getString(R.string.extend_pwd_error_occured), mContext);
                                }
                            } else {
                                displayErrorMessage(mContext.getString(R.string.extend_pwd_error_occured), mContext);
                            }
                        } else {
                            displayErrorMessage(mContext.getString(R.string.extend_pwd_error_occured), mContext);
                        }
                    } else {
                        displayErrorMessage(mContext.getString(R.string.no_network_conn), mContext);
                    }
                } catch (IOException var16) {
                    var16.printStackTrace();
                    displayErrorMessage(var16.getMessage(), mContext);
                } catch (JSONException var17) {
                    var17.printStackTrace();
                    displayErrorMessage(var17.getMessage(), mContext);
                } catch (Exception var17) {
                    var17.printStackTrace();
                    displayErrorMessage(var17.getMessage(), mContext);
                }
            }
        }).start();
    }
    private void displayErrorMessage(final String strMsg, final Context mContext) {
        try {
           getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        pdLoadDialog.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (strMsg.contains("successfully")) {
                        exitAPP();
                    }
                    Toast.makeText(mContext, strMsg, Toast.LENGTH_LONG).show();
//                    UtilConstants.showErrorMsgSnackbar(contextView, strMsg,mContext);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void exitAPP() {
        UtilConstants.dialogBoxWithCallBack(getContext(), "", getString(R.string.extend_pwd_updated_succefully), getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                getActivity().finishAffinity();
                System.exit(0);
            }
        });
    }

    private void setPwdInDataVault(Context mContext, String password) {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFS_NAME, 0);
            SharedPreferences.Editor spEditer = sharedPreferences.edit();
            spEditer.putString(Constants.Password_Key, password);
            spEditer.apply();
            // get Application Connection ID
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
            lgCtx.setBackendPassword(password);
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
    }

    private void openOfflineStore() {
        if (UtilConstants.isNetworkAvailable(getActivity())) {
            try {
                Constants.isSync = true;
                pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.app_loading));
                new OpenOfflineStoreAsyncTask(getActivity(), this).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            UtilConstants.onNoNetwork(getActivity());
        }
    }

    /*AsyncTask to get Achieved Percentage*/
    private class GetTodayAchivedData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadAchivedDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadAchivedDialog.setMessage(getString(R.string.app_loading));
            pdLoadAchivedDialog.setCancelable(false);
            pdLoadAchivedDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

//            getVisitData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                pdLoadAchivedDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setIconVisibility();
        }
    }

   /* private void onAlertDialogForVisitDayEndRemarks() {
        AlertDialog.Builder alertDialogVisitEndRemarks = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
        alertDialogVisitEndRemarks.setMessage(R.string.alert_plz_enter_remarks);
        alertDialogVisitEndRemarks.setCancelable(false);
        int MAX_LENGTH = 255;

        final EditText etVisitEndRemarks = new EditText(getActivity());

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
        FilterArray[1] = Constants.getNumberAlphabetOnly();;
        etVisitEndRemarks.setFilters(FilterArray);

        etVisitEndRemarks.setText(mStrVisitEndRemarks.equalsIgnoreCase("") ? mStrVisitEndRemarks : "");
        etVisitEndRemarks.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        etVisitEndRemarks.setLayoutParams(lp);
        alertDialogVisitEndRemarks.setView(etVisitEndRemarks);
        alertDialogVisitEndRemarks.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mStrVisitEndRemarks = etVisitEndRemarks.getText().toString();
                        if (mStrVisitEndRemarks.equalsIgnoreCase("")) {
                            wantToCloseDialog = true;
                            onAlertDialogForVisitDayEndRemarks();
                        } else {
                            wantToCloseDialog = false;
                            onSaveVisitClose();
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
        AlertDialog alertDialog = alertDialogVisitEndRemarks.create();
        alertDialog.show();

    }*/


    private String selectedReasonDesc = "";
    private String selectedReasonCode = "";
    Spinner spVisitEndReason;
    boolean isValid = true;

    private void onAlertDialogForVisitDayEndRemarks() {

        selectedReasonCode = "";
        selectedReasonDesc = "";
        reasonCodedesc.clear();
        getReasonValues();

        int MAX_LENGTH = 255;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.visit_remarks, null, true);
        final EditText etVisitEndRemarks = (EditText) dialogView.findViewById(R.id.etremarks);
        spVisitEndReason = (Spinner) dialogView.findViewById(R.id.spReason);


        ArrayAdapter<RemarkReasonBean> reasonadapter = new ArrayAdapter<>(getActivity(), R.layout.custom_textview, reasonCodedesc);
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

        AlertDialog.Builder alertDialogVisitEndRemarks = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
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
                            UtilConstants.dialogBoxWithCallBack(getActivity(), "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
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
                                etVisitEndRemarks.setBackgroundResource(R.drawable.edittext_border);
                                wantToCloseDialog = true;
                                mStrPopUpText = getString(R.string.alert_please_enter_remarks);
                                UtilConstants.dialogBoxWithCallBack(getActivity(), "", mStrPopUpText, getString(R.string.ok), "", false, new DialogCallBack() {
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

                                wantToCloseDialog = false;
                                onSaveVisitClose();
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
                                wantToCloseDialog = false;
                                onSaveVisitClose();
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

    private void onSaveVisitClose() {
        mStrPopUpText = getString(R.string.marking_visit_end_plz_wait);
        try {
            new ClosingVisit().execute();
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void openOffStore() {
        if (UtilConstants.isNetworkAvailable(getActivity())) {
            try {
                Constants.isSync = true;
                pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.app_loading));
                new OpenOfflineStoreAsyncTask(getActivity(), this).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            UtilConstants.onNoNetwork(getActivity());
        }
    }

    private void attendanceFunctionality(final ImageView ivIcon, final TextView tvIconName) {

        if (!OfflineManager.isOfflineStoreOpen()) {
            openOffStore();
        } else {
            if (mBooleanEndFlag) {

                if (Constants.isEndateAndEndTimeValid(UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate)), Constants.MapEntityVal.get(Constants.StartTime) + "")) {

                    String message = "";
                    if (mStrPreviousDate.equalsIgnoreCase("")) {
                        //For Today
                        mStrOtherRetailerGuid = "";
                        String otherRetVisitQuery = Constants.Visits + "?$filter=EndDate eq null " +
                                "and StartDate eq datetime'" + UtilConstants.getNewDate() + "'and " + Constants.StatusID + " eq '01'";

                        String[] otherRetDetails = new String[3];
                        try {
                            otherRetDetails = OfflineManager.checkVisitForOtherRetailer(otherRetVisitQuery);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                        final String[] finalOtherRetDetails = otherRetDetails;

                        mStrOtherRetailerGuid = finalOtherRetDetails[1];
                        try {
                            mStrVisitSeqNoFromEntity = finalOtherRetDetails[2];
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (mStrOtherRetailerGuid != null && !mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                                         /*
                                         ToDo display alert dialog for visit started but not ended retailer
                                          */
                            AlertDialog.Builder alertDialogVisitEnd = new AlertDialog.Builder(
                                    getActivity(), R.style.MyTheme);

                            alertDialogVisitEnd.setMessage(getString(R.string.visit_end_not_marked_for_specific_retailer, otherRetDetails[0]))
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    dialog.cancel();

                                                    pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                    Constants.getLocation(getActivity(), new LocationInterface() {
                                                        @Override
                                                        public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                            closeProgressDialog();
                                                            if (status) {
                                                                boolean isVisitActivities = false;
                                                                try {
                                                                    isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrOtherRetailerGuid + "'");
                                                                } catch (OfflineODataStoreException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                                                                if (isVisitActivities) {
                                                                    onSaveVisitClose();
                                                                } else if (mStrVisitSeqNoFromEntity.equalsIgnoreCase("")) {
                                                                    onSaveVisitClose();
                                                                } else {
                                                                    wantToCloseDialog = false;
                                                                    onAlertDialogForVisitDayEndRemarks();
                                                                }
                                                            }
                                                        }
                                                    });

                                                }
                                            });
                            alertDialogVisitEnd.setNegativeButton(R.string.no,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }

                                    });

                            alertDialogVisitEnd.show();
                        } else {
                            //if condition is equal to A or B then the user can able to end the day else no
                            if (!stAttendanceConfigValue.equalsIgnoreCase(ConstantsUtils.C)) {

                                String alrtConfMsg = "", alrtNegtiveMsg = "";

                                if (delList == null) {

                                    message = getString(R.string.msg_confirm_day_end);
                                    alrtConfMsg = getString(R.string.yes);
                                    alrtNegtiveMsg = getString(R.string.no);

                                } else {
                                    message = getString(R.string.msg_confirm_day_end);
                                    alrtConfMsg = getString(R.string.ok);
                                    alrtNegtiveMsg = getString(R.string.cancel);
                                }

                                         /*
                                           ToDo display alert dialog for Day end or non visited retailers
                                         */

                                AlertDialog.Builder alertDialogDayEnd = new AlertDialog.Builder(
                                        getActivity(), R.style.MyTheme);
                                alertDialogDayEnd.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton(
                                                alrtConfMsg,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int id) {
                                                        dialog.cancel();
                                                        if (delList == null) {
                                                            pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                            Constants.getLocation(getActivity(), new LocationInterface() {
                                                                @Override
                                                                public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                                    closeProgressDialog();
                                                                    if (status) {
                                                                        mBooleanEndFlag = false;
                                                                        tvIconName
                                                                                .setText(R.string.tv_complete);
                                                                        mBooleanStartFalg = true;
                                                                        mBooleanCompleteFlag = true;
                                                                        mStrPopUpText = getString(R.string.msg_update_end);
                                                                        mBooleanDayStartDialog = false;
                                                                        mBooleanDayEndDialog = true;
                                                                        mBooleanDayResetDialog = false;
                                                                        onSaveClose();
                                                                    }
                                                                }
                                                            });
                                                        } else {


                                                            Intent intentNavEndRemarksScreen = new Intent(getActivity(), DayEndRemarksActivity.class);
                                                            intentNavEndRemarksScreen.putExtra(Constants.ClosingeDayType, Constants.Today);
                                                            intentNavEndRemarksScreen.putExtra(Constants.ClosingeDay, UtilConstants.getNewDate());
                                                            startActivity(intentNavEndRemarksScreen);
                                                        }

                                                    }

                                                })
                                        .setNegativeButton(alrtNegtiveMsg,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int id) {
                                                        dialog.cancel();
                                                    }

                                                });
                                alertDialogDayEnd.show();
                            }
                        }
                    } else {
                        //if condition is equal to A or B then the user can able to end the day else no
                        if (!stAttendanceConfigValue.equalsIgnoreCase(ConstantsUtils.C)) {
                            message = getString(R.string.msg_previous_day_end);
                            /*
                             *ToDo display alert dialog for previous day is not ended.
                             */
                            AlertDialog.Builder alertDialogPreviousDay = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
                            alertDialogPreviousDay.setMessage(
                                    message)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            getString(R.string.yes),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    dialog.cancel();
                                                    mStrOtherRetailerGuid = "";

                                                    String otherRetVisitQuery = Constants.Visits + "?$filter=EndDate eq null " +
                                                            "and StartDate eq datetime'" + mStrPreviousDate + "'and " + Constants.StatusID + " eq '01'";

                                                    String[] otherRetDetails = new String[3];
                                                    try {
                                                        otherRetDetails = OfflineManager.checkVisitForOtherRetailer(otherRetVisitQuery);
                                                    } catch (OfflineODataStoreException e) {
                                                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                                                    }
                                                    final String[] finalOtherRetDetails = otherRetDetails;

                                                    mStrOtherRetailerGuid = finalOtherRetDetails[1];

                                                    try {
                                                        mStrVisitSeqNoFromEntity = finalOtherRetDetails[2];
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    if (mStrOtherRetailerGuid != null && !mStrOtherRetailerGuid.equalsIgnoreCase("")) {
                                                        /*
                                                         *ToDo display alert dialog for visit started but not ended retailer
                                                         */
                                                        AlertDialog.Builder alertDialogVisitEnd = new AlertDialog.Builder(
                                                                getActivity(), R.style.MyTheme);

                                                        alertDialogVisitEnd.setMessage(getString(R.string.visit_end_not_marked_for_specific_retailer, otherRetDetails[0]))
                                                                .setCancelable(false)
                                                                .setPositiveButton(
                                                                        R.string.yes,
                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(
                                                                                    DialogInterface dialog,
                                                                                    int id) {
                                                                                dialog.cancel();
                                                                                pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                                                Constants.getLocation(getActivity(), new LocationInterface() {
                                                                                    @Override
                                                                                    public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                                                        closeProgressDialog();
                                                                                        if (status) {
                                                                                            boolean isVisitActivities = false;
                                                                                            try {
                                                                                                isVisitActivities = OfflineManager.checkVisitActivitiesForRetailer(Constants.VisitActivities + "?$filter=" + Constants.VISITKEY + " eq guid'" + mStrOtherRetailerGuid + "'");
                                                                                            } catch (OfflineODataStoreException e) {
                                                                                                e.printStackTrace();
                                                                                            }
                                                                                            mStrVisitId = ODataGuidDefaultImpl.initWithString36(mStrOtherRetailerGuid);
                                                                                            if (isVisitActivities) {
                                                                                                onSaveVisitClose();
                                                                                            } else if (mStrVisitSeqNoFromEntity.equalsIgnoreCase("")) {
                                                                                                onSaveVisitClose();
                                                                                            } else {
                                                                                                wantToCloseDialog = false;
                                                                                                onAlertDialogForVisitDayEndRemarks();
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                });

                                                                            }
                                                                        });
                                                        alertDialogVisitEnd.setNegativeButton(R.string.no,
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog,
                                                                                        int id) {
                                                                        dialog.cancel();
                                                                    }

                                                                });

                                                        alertDialogVisitEnd.show();
                                                    } else {
                                                        String msg = "";

                                                        String alrtConfMsg = "", alrtNegtiveMsg = "";

                                                        if (delList == null) {
                                                            msg = getString(R.string.msg_confirm_day_end);
                                                            alrtConfMsg = getString(R.string.yes);
                                                            alrtNegtiveMsg = getString(R.string.no);
                                                        } else {
                                                            msg = getString(R.string.msg_remarks_pending_visit);
                                                            alrtConfMsg = getString(R.string.ok);
                                                            alrtNegtiveMsg = getString(R.string.cancel);
                                                        }

                                                                     /*
                                                                     ToDo display alert dialog for Day end  and non visited retailers
                                                                       */
                                                        AlertDialog.Builder alertDialogDayEnd = new AlertDialog.Builder(
                                                                getActivity(), R.style.MyTheme);
                                                        alertDialogDayEnd.setMessage(msg)
                                                                .setCancelable(false)
                                                                .setPositiveButton(
                                                                        alrtConfMsg,
                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(
                                                                                    DialogInterface dialog,
                                                                                    int id) {
                                                                                dialog.cancel();

                                                                                if (delList == null) {
                                                                                    pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                                                    Constants.getLocation(getActivity(), new LocationInterface() {
                                                                                        @Override
                                                                                        public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                                                            closeProgressDialog();
                                                                                            if (status) {
                                                                                                mStrPopUpText = getString(R.string.msg_update_previous_day_end);
                                                                                                mBooleanDayStartDialog = false;
                                                                                                mBooleanDayEndDialog = true;
                                                                                                mBooleanDayResetDialog = false;

                                                                                                onSaveClose();
                                                                                                mBooleanEndFlag = false;
                                                                                                tvIconName
                                                                                                        .setText(R.string.tv_start);
                                                                                                mBooleanStartFalg = false;
                                                                                                mBooleanCompleteFlag = false;
                                                                                                ivIcon.setImageResource(R.drawable.stop);
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                } else {
                                                                                    Intent intentNavEndRemarksScreen = new Intent(getActivity(), DayEndRemarksActivity.class);
                                                                                    intentNavEndRemarksScreen.putExtra(Constants.ClosingeDayType, Constants.PreviousDay);
                                                                                    intentNavEndRemarksScreen.putExtra(Constants.ClosingeDay, mStrPreviousDate);
                                                                                    startActivity(intentNavEndRemarksScreen);
                                                                                }
//                                                                                                    }
//                                                                                                }
                                                                            }

                                                                        })
                                                                .setNegativeButton(alrtNegtiveMsg,
                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(
                                                                                    DialogInterface dialog,
                                                                                    int id) {
                                                                                dialog.cancel();
                                                                            }

                                                                        });
                                                        alertDialogDayEnd.show();
                                                    }
//                                                                }
//                                                            }
                                                }

                                            })
                                    .setNegativeButton(
                                            getString(R.string.no),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    dialog.cancel();
                                                }

                                            });
                            alertDialogPreviousDay.show();
                        }
                    }
                } else {
                    // display error pop up
                    String mStrDate = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) Constants.MapEntityVal.get(Constants.StartDate));
                    UtilConstants.showAlert(getString(R.string.msg_end_date_should_be_greterthan_startdate, mStrDate), getActivity());
                }

            } else {

                if (!mBooleanStartFalg) {

                    Constants.HashMapEntityVal.clear();
                    try {
                        OfflineManager.getMaxStartDateTime(Constants.Attendances + " ?$orderby=" + Constants.StartDate + "%20desc &$top=1");
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }

                    if (!Constants.HashMapEntityVal.isEmpty()) {
                        if (Constants.isEndateAndEndTimeValid(UtilConstants.getConvertCalToStirngFormat((Calendar)
                                Constants.HashMapEntityVal.get(Constants.StartDate)), Constants.HashMapEntityVal.get(Constants.StartTime) + "")) {

                            Intent intentNavPrevScreen = new Intent(getActivity(), CreateAttendanceActivity.class);
                            startActivity(intentNavPrevScreen);

                        } else {
                            // display error pop up
                            UtilConstants.showAlert(getString(R.string.msg_start_date_should_not_be_past_date), getActivity());
                        }
                    } else {
                        Intent intentNavPrevScreen = new Intent(getActivity(), CreateAttendanceActivity.class);
                        startActivity(intentNavPrevScreen);
                    }

                }

                if (mBooleanCompleteFlag) {
                                    /*
                                    ToDo display alert dialog for Day end reset

                                     */
                    //if condition is equal to A then the user can able to reset the day else no
                    if (!stAttendanceConfigValue.equalsIgnoreCase(ConstantsUtils.B) && !stAttendanceConfigValue.equalsIgnoreCase(ConstantsUtils.C)) {
                        Constants.HashMapEntityVal.clear();
                        try {
                            OfflineManager.getMaxEndDateTime(Constants.Attendances + "?$filter=EndDate ne null &$orderby=" + Constants.StartDate + "%20desc &$top=1");
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                        if (!Constants.HashMapEntityVal.isEmpty()) {
                            if (Constants.isEndateValid(UtilConstants.getConvertCalToStirngFormat((Calendar)
                                    Constants.HashMapEntityVal.get(Constants.EndDate)), Constants.HashMapEntityVal.get(Constants.EndTime) + "")) {
                                AlertDialog.Builder alertDialogDayEndReset = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
                                alertDialogDayEndReset.setMessage(
                                        getString(R.string.msg_reset_day_end))
                                        .setCancelable(false)
                                        .setPositiveButton(
                                                getString(R.string.yes),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int id) {
                                                        dialog.cancel();
                                                        pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                        Constants.getLocation(getActivity(), new LocationInterface() {
                                                            @Override
                                                            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                                closeProgressDialog();
                                                                if (status) {
                                                                    ivIcon.setImageResource(R.drawable.stop);
                                                                    tvIconName
                                                                            .setText(R.string.tv_end);
                                                                    mBooleanEndFlag = true;
                                                                    mBooleanCompleteFlag = false;
                                                                    mBooleanStartFalg = true;


                                                                    mBooleanDayStartDialog = false;
                                                                    mBooleanDayEndDialog = false;
                                                                    mBooleanDayResetDialog = true;
                                                                    onCloseUpdate();
                                                                }
                                                            }
                                                        });
                                                    }
                                                })
                                        .setNegativeButton(
                                                getString(R.string.no),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int id) {
                                                        dialog.cancel();
                                                    }

                                                });
                                alertDialogDayEndReset.show();
                            } else {
                                // display error pop up
                                UtilConstants.showAlert(getString(R.string.msg_reset_date_should_not_be_past_date), getActivity());
                            }
                        } else {
                            AlertDialog.Builder alertDialogDayEndReset = new AlertDialog.Builder(getActivity(), R.style.MyTheme);
                            alertDialogDayEndReset.setMessage(
                                    getString(R.string.msg_reset_day_end))
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            getString(R.string.yes),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    dialog.cancel();
                                                    pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.gps_progress));
                                                    Constants.getLocation(getActivity(), new LocationInterface() {
                                                        @Override
                                                        public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                                                            closeProgressDialog();
                                                            if (status) {
                                                                ivIcon.setImageResource(R.drawable.stop);
                                                                tvIconName
                                                                        .setText(R.string.tv_end);
                                                                mBooleanEndFlag = true;
                                                                mBooleanCompleteFlag = false;
                                                                mBooleanStartFalg = true;


                                                                mBooleanDayStartDialog = false;
                                                                mBooleanDayEndDialog = false;
                                                                mBooleanDayResetDialog = true;
                                                                onCloseUpdate();
                                                            }
                                                        }
                                                    });
                                                }
                                            })
                                    .setNegativeButton(
                                            getString(R.string.no),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    dialog.cancel();
                                                }

                                            });
                            alertDialogDayEndReset.show();
                        }


                    }

                }

            }
        }


    }

    private void getNonVisitedDealers(String strDate) {
        try {
            new GetNonVistedRetailers().execute(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isWaitClosePrgDialog = false;

    /*
     *
     * AsyncTask for Non Visited Dealers
     *
     */
    private class GetNonVistedRetailers extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String mStrDate = params[0];
            try {
                Thread.sleep(1000);

                delList = DayEndRemarksActivity.getDealer(mStrDate);

            } catch (InterruptedException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
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
            attendanceFunctionality(imageView, textView);
        }
    }

    ImageView imageView = null;
    TextView textView = null;

    /*
     *
     * This class display icons in grid view manner for today's activities
     *
     */
    public class TodayAchievedImageAdapter extends BaseAdapter {
        @SuppressWarnings("unused")
        final Context mContext;

        public TodayAchievedImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            int mCountTemp = 0;
            for (int mainMenuOriginalStatu : mArrIntMainMenuOriginalStatus) {
                if (mainMenuOriginalStatu == 1) {
                    mCountTemp++;
                }
            }
            return mCountTemp;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, final View convertView, ViewGroup parent) {
            int Position = mArrIntMainMenuTempStatus[position];
            View view;
            if (convertView == null) {
                LayoutInflater liAdmin = LayoutInflater.from(getActivity());
                view = liAdmin.inflate(R.layout.mainmenu_inside, parent, false);
//                view.requestFocus();


                final TextView tvIconName = (TextView) view
                        .findViewById(R.id.icon_text);

                final LinearLayout ll_icon_area_sel = (LinearLayout) view
                        .findViewById(R.id.ll_main_menu_icon_sel);

                tvIconName.setText(mArrStrTodayIconName[Position]);
                final ImageView ivIcon = (ImageView) view
                        .findViewById(R.id.ib_must_sell);
                final CircularTextView tv_alerts_count = (CircularTextView) view
                        .findViewById(R.id.tv_alerts_count);

                if (Position == 0) {
                    Constants.MapEntityVal.clear();
                    ivIcon.setVisibility(View.VISIBLE);
                    tvIconName.setVisibility(View.VISIBLE);
                    mStrPreviousDate = "";

                    String prvDayQry = Constants.Attendances + "?$filter=EndDate eq null and StartDate ne datetime'" + UtilConstants.getNewDate() + "' ";
                    try {
                        mStrAttendanceId = OfflineManager.getAttendance(prvDayQry);
                        if (!mStrAttendanceId.equalsIgnoreCase("")) {
                            mStrPreviousDate = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate));
                        } else {
                            mStrPreviousDate = "";
                        }

                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }

//          Code added in 06-08-2018
//                    String dayEndqry = Constants.Attendances + "?$filter=EndDate eq null ";
                    String dayEndqry = Constants.Attendances + "?$filter=EndDate eq null and StartDate eq datetime'" + UtilConstants.getNewDate() + "'";
                    try {
                        mStrAttendanceId = OfflineManager.getAttendance(dayEndqry);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }

                    String startDateStr;
                    String endDateStr;
                    if (Constants.MapEntityVal.isEmpty()) {

                        String dayEndClosedqry = Constants.Attendances + "?$filter=EndDate ne null and StartDate eq datetime'" + UtilConstants.getNewDate() + "' ";
                        try {
                            mStrAttendanceId = OfflineManager.getAttendance(dayEndClosedqry);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }

                        startDateStr = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate));
                        endDateStr = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.EndDate));

                        if (mStrAttendanceId != null && !mStrAttendanceId.equalsIgnoreCase("")) {
                            ivIcon.setImageResource(R.drawable.stop);
                            tvIconName.setText(R.string.tv_complete);
                            mBooleanCompleteFlag = true;
                            mBooleanEndFlag = false;
                            mBooleanStartFalg = true;
                        } else {
                            ivIcon.setImageResource(R.drawable.start);
                        }

                    } else {

                        // If config ask to display previous day end
                        if (false) {
                            if (Constants.MapEntityVal.get(Constants.EndDate) == null) {
                                ivIcon.setImageResource(R.drawable.stop);
                                tvIconName.setText(R.string.tv_end);
                                mBooleanEndFlag = true;
                            } else {
                                ivIcon.setImageResource(R.drawable.start);
                            }
                        } else {
                            Constants.MapEntityVal.clear();
                            String dayEndClsqry = Constants.Attendances + "?$filter=EndDate eq null and StartDate eq datetime'" + UtilConstants.getNewDate() + "' ";
                            try {
                                mStrAttendanceId = OfflineManager.getAttendance(dayEndClsqry);
                            } catch (OfflineODataStoreException e) {
                                LogManager.writeLogError(Constants.error_txt + e.getMessage());
                            }
                            if (mStrAttendanceId != null && !mStrAttendanceId.equalsIgnoreCase("")) {
                                ivIcon.setImageResource(R.drawable.stop);
                                tvIconName.setText(R.string.tv_end);
                                mBooleanEndFlag = true;
                                mStrPreviousDate = "";
                            } else {
                                String dayEndClosedqry = Constants.Attendances + "?$filter=EndDate ne null and StartDate eq datetime'" + UtilConstants.getNewDate() + "' ";
                                try {
                                    mStrAttendanceId = OfflineManager.getAttendance(dayEndClosedqry);
                                } catch (OfflineODataStoreException e) {
                                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                                }

                                startDateStr = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.StartDate));
                                endDateStr = UtilConstants.getConvertCalToStirngFormat((Calendar) Constants.MapEntityVal.get(Constants.EndDate));

                                if (mStrAttendanceId != null && !mStrAttendanceId.equalsIgnoreCase("")) {
                                    ivIcon.setImageResource(R.drawable.stop);
                                    tvIconName.setText(R.string.tv_complete);
                                    mBooleanCompleteFlag = true;
                                    mBooleanEndFlag = false;
                                    mBooleanStartFalg = true;
                                } else {
                                    ivIcon.setImageResource(R.drawable.start);
                                }
                            }
                        }

                    }

                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            imageView = ivIcon;
                            textView = tvIconName;

                            pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.checking_pemission));

                            LocationUtils.checkLocationPermission(getActivity(), new LocationInterface() {
                                @Override
                                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                                    closeProgressDialog();
                                    if (status) {
                                        onDayStartOrEnd();
                                    }
                                }
                            });


                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            imageView = ivIcon;
                            textView = tvIconName;

                            pdLoadDialog = Constants.showProgressDialog(getActivity(), "", getString(R.string.checking_pemission));

                            LocationUtils.checkLocationPermission(getActivity(), new LocationInterface() {
                                @Override
                                public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                                    closeProgressDialog();
                                    if (status) {
                                        onDayStartOrEnd();
                                    }
                                }
                            });
                        }
                    });
                } else if (Position == 1) {
                    ivIcon.setImageResource(R.drawable.ic_beat_plan);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onBeatPlan();

                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onBeatPlan();
                        }
                    });
                } else if (Position == 2) {
                    int alertCount = getAlertsCountFromSharedPref();
                    if (alertCount > 0) {
                        tv_alerts_count.setVisibility(View.VISIBLE);
                        tv_alerts_count.setStrokeWidth(1);
                        tv_alerts_count.setStrokeColor("#f20000");
                        tv_alerts_count.setSolidColor("#f20000");
                        try {
                            tv_alerts_count.setText(alertCount + "");
                        } catch (Exception e) {
                            tv_alerts_count.setText("0");
                        }
                    } else {
                        tv_alerts_count.setVisibility(View.GONE);
                    }

//                ivIcon.setImageResource(R.drawable.ic_route_plan);
                    ivIcon.setImageResource(R.drawable.ic_alerts_bell_icon);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onAlerts();

                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onAlerts();
                        }
                    });
                } else if (Position == 3) {
                    ivIcon.setImageResource(R.drawable.ic_adhoc_visit);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onAdhocList();

                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onAdhocList();

                        }
                    });
                } else if (Position == 4) {
                    ivIcon.setImageResource(R.drawable.ic_retailer);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onCreateRetailer();

                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onCreateRetailer();

                        }
                    });
                } else if (Position == 5) {
                    ivIcon.setImageResource(R.drawable.ic_retailer);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onUpdateRetailerList();

                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onUpdateRetailerList();

                        }
                    });
                } else if (Position == 6) {
                    ivIcon.setImageResource(R.drawable.ic_db_stock);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onMyStock();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onMyStock();
                        }
                    });
                } else if (Position == 7) {
                    ivIcon.setImageResource(R.drawable.ic_my_targets);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {

                            onTargetList();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {

                            onTargetList();
                        }
                    });
                } else if (Position == 8) {

                    ivIcon.setImageResource(R.drawable.ic_db_stock);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onDBStock();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onDBStock();
                        }
                    });

                } else if (Position == 9) {

                    ivIcon.setImageResource(R.drawable.helpline);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onHelpLine();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onHelpLine();
                        }
                    });

                } else if (Position == 10) {

                    ivIcon.setImageResource(R.drawable.ic_dealer_behaviour);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onBehaviourList();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onBehaviourList();
                        }
                    });
                } else if (Position == 11) {

                    ivIcon.setImageResource(R.drawable.ic_schemes);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onSchemes();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onSchemes();
                        }
                    });
                } else if (Position == 12) {

                    ivIcon.setImageResource(R.drawable.myperformance);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onMyPerformnce();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onMyPerformnce();
                        }
                    });


                } else if (Position == 13) {
                    ivIcon.setImageResource(R.drawable.ic_summary);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onDaySummary();

                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onDaySummary();

                        }
                    });
                } else if (Position == 14) {
                    ivIcon.setImageResource(R.drawable.ic_summary);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onVisitSummary();

                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onVisitSummary();

                        }
                    });
                } else if (Position == 15) {
                    ivIcon.setImageResource(R.drawable.ic_visualnew);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onVisualAid();

                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onVisualAid();

                        }
                    });
                } else if (Position == 16) {
                    ivIcon.setImageResource(R.drawable.ic_summary);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onExpense();

                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onExpense();

                        }
                    });
                } else if (Position == 17) {
                    ivIcon.setImageResource(R.drawable.ic_digital_prod);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onDigitalProduct();

                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onDigitalProduct();

                        }
                    });

                } else if (Position == 18) {
                    ivIcon.setImageResource(R.drawable.ic_schemes);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onScheme();

                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onScheme();

                        }
                    });
                } else if (Position == 19) {
                    ivIcon.setImageResource(R.drawable.ic_dist_upd);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onDistributor();

                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onDistributor();
                        }
                    });
                } else if (Position == 20) {
                    ivIcon.setImageResource(R.drawable.survey_icon);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onOutletSurvey();

                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onOutletSurvey();
                        }
                    });
                }


                view.setId(Position);
            } else {
                view = convertView;
            }

            return view;
        }


    }

    private void onDayStartOrEnd() {
        if (mBooleanEndFlag) {
            if (mStrPreviousDate.equalsIgnoreCase("")) {
                getNonVisitedDealers(UtilConstants.getNewDate());
            } else {
                getNonVisitedDealers(mStrPreviousDate);
            }
        } else {
            attendanceFunctionality(imageView, textView);
        }
    }

    private boolean isClossDayError = false;

    /*AsyncTask to Close Attendance for day*/
    private class ClosingDate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            isClossDayError = false;
            try {
                Thread.sleep(1000);

                Constants.MapEntityVal.clear();

                String qry = Constants.Attendances + "?$filter=EndDate eq null ";
                try {
                    mStrAttendanceId = OfflineManager.getAttendance(qry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                Hashtable hashTableAttendanceValues;

                hashTableAttendanceValues = new Hashtable();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

                String loginIdVal = sharedPreferences.getString(Constants.username, "");
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.AttendanceGUID, Constants.MapEntityVal.get(Constants.AttendanceGUID));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartDate, Constants.MapEntityVal.get(Constants.StartDate));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartTime, Constants.MapEntityVal.get(Constants.StartTime));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartLat, Constants.MapEntityVal.get(Constants.StartLat));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartLong, Constants.MapEntityVal.get(Constants.StartLong));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndLat, BigDecimal.valueOf(UtilConstants.latitude));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndLong, BigDecimal.valueOf(UtilConstants.longitude));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndDate, UtilConstants.getNewDateTimeFormat());

                hashTableAttendanceValues.put(Constants.SPGUID, Constants.getSPGUID(Constants.SalesPersons, Constants.SPGUID));

                try {
                    hashTableAttendanceValues.put(Constants.SetResourcePath, Constants.MapEntityVal.get(Constants.SetResourcePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Constants.MapEntityVal.get(Constants.Etag) != null) {
                    hashTableAttendanceValues.put(Constants.Etag, Constants.MapEntityVal.get(Constants.Etag));
                } else {
                    hashTableAttendanceValues.put(Constants.Etag, "");
                }

                hashTableAttendanceValues.put(Constants.Remarks, Constants.MapEntityVal.get(Constants.Remarks));
                hashTableAttendanceValues.put(Constants.AttendanceTypeH1, Constants.MapEntityVal.get(Constants.AttendanceTypeH1));
                hashTableAttendanceValues.put(Constants.AttendanceTypeH2, Constants.MapEntityVal.get(Constants.AttendanceTypeH2));

                final Calendar calCurrentTime = Calendar.getInstance();
                int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                int minute = calCurrentTime.get(Calendar.MINUTE);
                int second = calCurrentTime.get(Calendar.SECOND);
                ODataDuration oDataDuration = null;
                try {
                    oDataDuration = new ODataDurationDefaultImpl();
                    oDataDuration.setHours(hourOfDay);
                    oDataDuration.setMinutes(minute);
                    oDataDuration.setSeconds(BigDecimal.valueOf(second));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndTime, oDataDuration);

                //noinspection unchecked

               /* SharedPreferences sharedPreferencesVal = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                SharedPreferences.Editor editor = sharedPreferencesVal.edit();
                editor.putInt("VisitSeqId", 0);
                editor.commit();*/

                try {
                    //noinspection unchecked
                    OfflineManager.updateAttendance(hashTableAttendanceValues, MainMenuFragment.this);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
                isClossDayError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (isClossDayError) {
                pdLoadDialog.dismiss();
            }
        }
    }

    /*gets service document for */
    private boolean isError = false;

    /*AsyncTask to reset attendance for day*/
    private class ResettingDate extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            isError = false;
            try {
                Thread.sleep(1000);

                Constants.MapEntityVal.clear();

                String dayEndClosedqry = Constants.Attendances + "?$filter=EndDate eq datetime'" + UtilConstants.getNewDate() + "' and StartDate eq datetime'" + UtilConstants.getNewDate() + "' ";
                try {
                    mStrAttendanceId = OfflineManager.getAttendance(dayEndClosedqry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }

                Hashtable hashTableAttendanceValues;


                hashTableAttendanceValues = new Hashtable();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);

                String loginIdVal = sharedPreferences.getString(Constants.username, "");
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.LOGINID, loginIdVal);
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.AttendanceGUID, Constants.MapEntityVal.get(Constants.AttendanceGUID));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartDate, Constants.MapEntityVal.get(Constants.StartDate));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartTime, Constants.MapEntityVal.get(Constants.StartTime));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartLat, Constants.MapEntityVal.get(Constants.StartLat));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.StartLong, Constants.MapEntityVal.get(Constants.StartLong));
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndLat, "");
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndLong, "");
                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndDate, "");

                hashTableAttendanceValues.put(Constants.Remarks, Constants.MapEntityVal.get(Constants.Remarks));
                hashTableAttendanceValues.put(Constants.AttendanceTypeH1, Constants.MapEntityVal.get(Constants.AttendanceTypeH1));
                hashTableAttendanceValues.put(Constants.AttendanceTypeH2, Constants.MapEntityVal.get(Constants.AttendanceTypeH2));

                hashTableAttendanceValues.put(Constants.SPGUID, Constants.getSPGUID(Constants.SalesPersons, Constants.SPGUID));

                try {
                    hashTableAttendanceValues.put(Constants.SetResourcePath, Constants.MapEntityVal.get(Constants.SetResourcePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Constants.MapEntityVal.get(Constants.Etag) != null) {
                    hashTableAttendanceValues.put(Constants.Etag, Constants.MapEntityVal.get(Constants.Etag));
                } else {
                    hashTableAttendanceValues.put(Constants.Etag, "");
                }

                final Calendar calCurrentTime = Calendar.getInstance();
                int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                int minute = calCurrentTime.get(Calendar.MINUTE);
                int second = calCurrentTime.get(Calendar.SECOND);
                ODataDuration oDataDuration = null;
                try {
                    oDataDuration = new ODataDurationDefaultImpl();
                    oDataDuration.setHours(hourOfDay);
                    oDataDuration.setMinutes(minute);
                    oDataDuration.setSeconds(BigDecimal.valueOf(second));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //noinspection unchecked
                hashTableAttendanceValues.put(Constants.EndTime, "");

                try {
                    //noinspection unchecked
                    OfflineManager.resetAttendanceEntity(hashTableAttendanceValues, MainMenuFragment.this);
                } catch (OfflineODataStoreException e) {
//					e.printStackTrace();
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
            } catch (Exception e) {
                isError = true;
                e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (isError) {
                pdLoadDialog.dismiss();
            }
        }
    }
    /*
             TODO Enter remarks in visit table if activity is not done.

             */

    /*
     *
     * This class display admin icons in grid view manner
     *
     */
    public class AdminImageAdapter extends BaseAdapter {
        @SuppressWarnings("unused")
        final Context mContext;

        public AdminImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            int counttemp = 0;
            for (int adminOriginalStatu : mArrIntAdminOriginalStatus) {
                if (adminOriginalStatu == 1) {
                    counttemp++;
                }
            }
            return counttemp;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int Position = mArrIntAdminTempStatus[position];
            View view;
            if (convertView == null) {
                LayoutInflater liAdmin = LayoutInflater.from(getActivity());
                view = liAdmin.inflate(R.layout.mainmenu_inside, parent, false);
                final TextView tvIconName = (TextView) view
                        .findViewById(R.id.icon_text);
                final LinearLayout ll_icon_area_sel = (LinearLayout) view
                        .findViewById(R.id.ll_main_menu_icon_sel);
                tvIconName.setTextColor(getResources().getColor(R.color.icon_text_blue));
                tvIconName.setText(mArrStrAdminTextName[Position]);
                final ImageView ivIcon = (ImageView) view
                        .findViewById(R.id.ib_must_sell);
                if (Position == 0) {
                    ivIcon.setImageResource(R.drawable.ic_sync);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onSyncView();
                        }
                    });
                    tvIconName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSyncView();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onSyncView();
                        }
                    });
                }
                if (Position == 1) {
                    ivIcon.setImageResource(R.drawable.ic_log_list);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onLogView();
                        }
                    });
                    tvIconName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onLogView();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onLogView();
                        }
                    });
                }
                view.setId(Position);
            } else {
                view = convertView;
            }

            return view;
        }
    }

    /*
     *
     * This class display admin icons in grid view manner
     *
     */
    public class ReportsImageAdapter extends BaseAdapter {
        @SuppressWarnings("unused")
        final Context mContext;

        public ReportsImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            int counttemp = 0;
            for (int adminOriginalStatu : mArrIntMainMenuReportsOriginalStatus) {
                if (adminOriginalStatu == 1) {
                    counttemp++;
                }
            }
            return counttemp;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int Position = mArrIntMainMenuReportsTempStatus[position];
            View view;
            if (convertView == null) {
                LayoutInflater liAdmin = LayoutInflater.from(getActivity());
                view = liAdmin.inflate(R.layout.mainmenu_inside, parent, false);
                final TextView tvIconName = (TextView) view
                        .findViewById(R.id.icon_text);
                final LinearLayout ll_icon_area_sel = (LinearLayout) view
                        .findViewById(R.id.ll_main_menu_icon_sel);
                tvIconName.setTextColor(getResources().getColor(R.color.icon_text_blue));
                tvIconName.setText(mArrStrReportsIconName[Position]);
                final ImageView ivIcon = (ImageView) view
                        .findViewById(R.id.ib_must_sell);

                if (Position == 0) {
                    ivIcon.setImageResource(R.drawable.ic_retailer);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            retailerList(mContext);
                        }
                    });
                    tvIconName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            retailerList(mContext);
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            retailerList(mContext);
                        }
                    });
                } else if (Position == 1) {

                    ivIcon.setImageResource(R.drawable.ic_behaviour_list);
                    ivIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onRetailerBehaviurList();
                        }
                    });
                    ll_icon_area_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onRetailerBehaviurList();
                        }
                    });
                    tvIconName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onRetailerBehaviurList();
                        }
                    });

                }

                view.setId(Position);
            } else {
                view = convertView;
            }

            return view;
        }
    }

    /*
     *
     * AsyncTask for opening offline store
     *
     */
    private class OpenOfflineStore extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);

                try {
                    OfflineManager.openOfflineStore(getActivity(), MainMenuFragment.this);
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



    /*  *//*
     *
     * AsyncTask for opening offline store
     *
     *//*
    private class SyncAllCollection extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }*/

    /*
    TODO Async task for Closing Visit End
    */
    private class ClosingVisit extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
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
                        try {
                            table.put(Constants.REASON, selectedReasonCode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


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

                        oDataProperty = oDataProperties.get(Constants.VisitCatID);
                        table.put(Constants.VisitCatID, oDataProperty.getValue());

                        try {
                            oDataProperty = oDataProperties.get(Constants.VisitDate);
                            table.put(Constants.VisitDate, oDataProperty != null ? oDataProperty.getValue() : null);
                        } catch (Exception e) {
                            oDataProperty = null;
                            table.put(Constants.VisitDate, "");
                        }


                        oDataProperty = oDataProperties.get(Constants.VisitSeq);
                        try {
                            table.put(Constants.VisitSeq, oDataProperty.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        oDataProperty = oDataProperties.get(Constants.CPGUID);
                        table.put(Constants.CPGUID, oDataProperty.getValue());


                        final Calendar calCurrentTime = Calendar.getInstance();
                        int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
                        int minute = calCurrentTime.get(Calendar.MINUTE);
                        int second = calCurrentTime.get(Calendar.SECOND);
                        ODataDuration oDataDuration = null;
                        try {
                            oDataDuration = new ODataDurationDefaultImpl();
                            oDataDuration.setHours(hourOfDay);
                            oDataDuration.setMinutes(minute);
                            oDataDuration.setSeconds(BigDecimal.valueOf(second));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        table.put(Constants.ENDTIME, oDataDuration);

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
                        String loginIdVal = sharedPreferences.getString("username", "");
                        //noinspection unchecked
                        table.put(Constants.LOGINID, loginIdVal);

                        table.put(Constants.SetResourcePath, Constants.Visits + "(guid'" + mStrVisitId.guidAsString36().toUpperCase() + "')");

                        if (visitEntity.getEtag() != null) {
                            table.put(Constants.Etag, visitEntity.getEtag());
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
                    OfflineManager.updateVisit(table, MainMenuFragment.this);
                } catch (OfflineODataStoreException e) {
//                    e.printStackTrace();
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

    private int getAlertsCountFromSharedPref() {
        int mIntAlertsCount = 0;
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.PREFS_NAME, 0);
        try {
            mIntAlertsCount = sharedPref.getInt(Constants.BirthdayAlertsCount, 0) +
                    sharedPref.getInt(Constants.TextAlertsCount, 0) +
                    sharedPref.getInt(Constants.AppointmentAlertsCount, 0);
        } catch (Exception e) {
            mIntAlertsCount = 0;
        }
        return mIntAlertsCount;
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
                                onDayStartOrEnd();
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
                onDayStartOrEnd();
            }
        }
    }

    private void dismissProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class MyWebReceiver extends BroadcastReceiver {

        public static final String PROCESS_RESPONSE = "com.as400samplecode.intent.action.PROCESS_RESPONSE";
        int versionNameFromPlayStore=0;
        int versionNamefromlocal=0;
        @Override
        public void onReceive(final Context context, Intent intent) {
            try {
            String reponseMessage = intent.getStringExtra(MyWebService.RESPONSE_MESSAGE);
            Log.v("MainMenu", reponseMessage);
            PackageInfo packageInfo = null;
            String version_code="";
            try {
                packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                 version_code =String.valueOf( packageInfo.versionCode);
                 strLocalVersionNumber = packageInfo.versionName;
                 strLocalVersionNumber = BuildConfig.VERSION_NAME;;
                 strLocalVersionNumber=strLocalVersionNumber.replace(".","");
                 versionNameFromPlayStore=Integer.parseInt(reponseMessage);
                 versionNamefromlocal=Integer.parseInt(strLocalVersionNumber);
                // strLocalVersionNumber = "1.7";
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

           // if (!TextUtils.isEmpty(version_code) && !TextUtils.isEmpty(reponseMessage) && Double.valueOf(version_code) < Double.valueOf(reponseMessage)) {
            //comment for MDM user...and uncomment for google play store...
            if (versionNameFromPlayStore!=0 && versionNamefromlocal!=0 && versionNamefromlocal < versionNameFromPlayStore)
            {
                final String packageName = getActivity().getApplicationContext().getPackageName();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("There is newer version of this application available, click OK to upgrade now?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                //if the user agrees to upgrade
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName + "&hl=en"));
                                    context.startActivity(intent);

                                }
                            })
                            .setNegativeButton("Remind Later", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    //show the alert message
                    builder.create().show();


                    // }

            }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void getVersionCheck() {
        if (UtilConstants.isNetworkAvailable(getActivity())) {
            Intent msgIntent = new Intent(getActivity(), MyWebService.class);
            msgIntent.putExtra(MyWebService.REQUEST_STRING, "https://play.google.com/store/apps/details?id=com.arteriatech.emami.msecsales&hl=en");
            context.startService(msgIntent);
        }
    }


    //my changes-11/06/2020

    private void OpenOfflineStoreInitializeDB() {
        if (UtilConstants.isNetworkAvailable(getActivity())) {
            try {
              new  OpenOfflineStoreInitializeDB().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            UtilConstants.onNoNetwork(getActivity());
        }
    }

    private class OpenOfflineStoreInitializeDB extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                closeStore();
//                OfflineManager.offlineStore =null;
                try {
                    OfflineManager.openOfflineStore(getActivity(), MainMenuFragment.this);
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

    private void closeStore() {
        try {
            OfflineManager.closeOfflineStore(getActivity(), OfflineManager.options);
            OfflineManager.closeOfflineStoreMustSell(getActivity(), OfflineManager.optionsMustSell);
            LogManager.writeLogInfo(getString(R.string.store_removed));
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_during_offline_close + e.getMessage());
        }
    }

}
