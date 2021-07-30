package com.arteriatech.emami.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.log.TraceLog;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.arteriatech.mutils.upgrade.ApplicationLifecycleHandler;
import com.arteriatech.emami.adapter.MainMenuPagerAdapter;
import com.arteriatech.emami.asyncTask.SyncMustSellAsyncTask;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.common.MSFAApplication;
import com.arteriatech.emami.common.RequestBuilder;
import com.arteriatech.emami.database.EventDataSqlHelper;
import com.arteriatech.emami.database.EventUserDetail;
import com.arteriatech.emami.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.emami.interfaces.PasswordDialogCallbackInterface;
import com.arteriatech.emami.log.LogActivity;
import com.arteriatech.emami.login.AboutUsActivity;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.notification.NotificationSetClass;
import com.arteriatech.emami.registration.Configuration;
import com.arteriatech.emami.registration.RegistrationActivity;
import com.arteriatech.emami.settings.SettingsActivity;
import com.arteriatech.emami.store.OfflineManager;
import com.arteriatech.emami.store.OnlineStoreListener;
import com.arteriatech.emami.sync.UpdatePendingRequest;
import com.arteriatech.emami.updatepassword.UpdatePasswordActivity;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreContext;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.mobile.lib.parser.IODataSchema;
import com.sap.mobile.lib.parser.Parser;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.online.OnlineODataStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;

/**
 * After successfully logged in user navigates this activity.This activity
 * arrange icons grid view manner based on Authorization T codes.Every icon
 * maintain separate functionality.
 */
@SuppressLint("NewApi")
public class MainMenu extends AppCompatActivity implements UIListener, OnClickListener {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static Context context;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
    };
    boolean flagforexportDB = true;
    boolean flagforexportDataVaultData = true;
    boolean flagforimportDataVaultData = true;
    boolean flagforimportDB = true;
    MainMenuPagerAdapter mainMenuPagerAdapter = null;
    ViewPager pagerMainMenu;
    private String mStrPopUpText = "";
    private boolean mBooleanIsApplicationExited = false;
    private ProgressDialog pdLoadDialog = null;
    private boolean mBooleanDayStartDialog = false, mBooleanDayEndDialog = false,
            mBooleanDayResetDialog = false;
    private ImageView ivIndicatorPage1, ivIndicatorPage2;
    private boolean isReReister = false;
    private MenuItem menu_init_db;
    private boolean isFromRegistration;

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int storage = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int location = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int camera = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (storage != PackageManager.PERMISSION_GRANTED || location != PackageManager.PERMISSION_GRANTED || camera != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(false)
        ActionBarView.initActionBarView(this, false, "");
        setContentView(R.layout.activity_main_menu);
       if (!Constants.restartApp(MainMenu.this)) {
            verifyStoragePermissions(this);
            ApplicationLifecycleHandler.isInLoginPage = false;
            context = MainMenu.this;
            Constants.mApplication = (MSFAApplication) getApplication();
            Bundle bundleExtras = getIntent().getExtras();
            if (bundleExtras != null) {
                isFromRegistration = bundleExtras.getBoolean(UtilRegistrationActivity.EXTRA_IS_FROM_REGISTRATION, false);
            }
            EventUserDetail eventDataSqlHelper = new EventUserDetail(this);
            Constants.EventUserHandler = eventDataSqlHelper.getWritableDatabase();
            Constants.events = new EventDataSqlHelper(getApplicationContext());

            if (isFromRegistration) {
                createSyncHistoryTable();
            }
            String endPointURL = "";
            String appConnID = "", userName = "", pwd = "";
            try {
                // get Application Connection ID
                LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
                endPointURL = lgCtx.getAppEndPointUrl();
                userName = lgCtx.getBackendUser();
                pwd = lgCtx.getBackendPassword();
                appConnID = LogonCore.getInstance().getLogonContext()
                        .getConnId();
            } catch (Exception e) {
                LogManager.writeLogError("LogonCoreContext", e);
            }

            try {
                Constants.mApplication.getParameters(userName, pwd);

                Parser parser = Constants.mApplication.getParser();
                IODataSchema schema = Constants.mApplication.getODataSchema();
                RequestBuilder.getInstance().initialize(schema, parser, appConnID,
                        endPointURL);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //initializing viewpager
            setUpViewPager();

            //initializing indicator
            setIndicator();

            //Initializing Log Trace
            TraceLog.initialize(this, context.getString(R.string.app_name));
            TraceLog.scoped(this).d(getString(R.string.msg_on_create));
        }
    }

    private void createSyncHistoryTable() {
        try {

            Constants.createSyncDatabase(MainMenu.this);  // create sync history table
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Setting Viewpager*/
    private void setUpViewPager() {
        mainMenuPagerAdapter = new MainMenuPagerAdapter(getSupportFragmentManager());
        mainMenuPagerAdapter.addFrag(new MainMenuFragment());
//        mainMenuPagerAdapter.addFrag(new SecondPageFragment());
        pagerMainMenu = (ViewPager) findViewById(R.id.pager_main_menu);
        pagerMainMenu.setAdapter(mainMenuPagerAdapter);
        pagerMainMenu.setCurrentItem(0);
        initPagerIndicator();
    }

    /*Initializing Pager indicator*/
    private void initPagerIndicator() {

        try {
            ivIndicatorPage1 = (ImageView) findViewById(R.id.iv_indicator1);
            ivIndicatorPage1.setImageResource(R.drawable.fill_circle);
            ivIndicatorPage2 = (ImageView) findViewById(R.id.iv_indicator2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Setting indicator for selected page*/
    private void setIndicator() {

        pagerMainMenu.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int position) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            /*executes when page changed on viewpager*/
            @Override
            public void onPageSelected(int position) {
                ivIndicatorPage1.setImageResource(R.drawable.holo_circle);
                ivIndicatorPage2.setImageResource(R.drawable.holo_circle);
                indicatorPagerAction(position);
            }

        });
    }

    /*Setting Icon to indicator for selected page*/
    private void indicatorPagerAction(int position) {
        switch (position) {
            case 0:
                ivIndicatorPage1.setImageResource(R.drawable.fill_circle);
                break;

            case 1:
                ivIndicatorPage2.setImageResource(R.drawable.fill_circle);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBooleanIsApplicationExited) {
            mBooleanIsApplicationExited = false;
        }
    }

    /*UIListener on Error*/
    @Override
    public void onRequestError(int operation, Exception e) {

        ErrorBean errorBean = Constants.getErrorCode(operation, e, MainMenu.this);
        if (errorBean.hasNoError()) {
            Toast.makeText(MainMenu.this, getString(R.string.err_odata_unexpected, e.getMessage()),
                    Toast.LENGTH_LONG).show();

            if (mBooleanDayStartDialog)
                mStrPopUpText = getString(R.string.msg_start_upd_sync_error);
            if (mBooleanDayEndDialog)
                mStrPopUpText = getString(R.string.msg_end_upd_sync_error);
            if (mBooleanDayResetDialog)
                mStrPopUpText = getString(R.string.msg_reset_upd_sync_error);

            if (operation == Operation.Create.getValue()) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                displayPopUpMsg();
            } else if (operation == Operation.Update.getValue()) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                displayPopUpMsg();
            } else if (operation == Operation.OfflineFlush.getValue()) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                displayPopUpMsg();
            } else if (operation == Operation.OfflineRefresh.getValue()) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                displayPopUpMsg();
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                try {
                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    UtilConstants.showAlert(getString(R.string.msg_offline_store_failure),
                            MainMenu.this);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        } else {
            closeProgressDialog();
            if (operation == Operation.GetStoreOpen.getValue()) {
                if (errorBean.getErrorCode() == Constants.Resource_not_found) {
                    Constants.ReIntilizeStore = true;
                }
                Constants.displayMsgReqError(errorBean.getErrorCode(), MainMenu.this);
            } else {
                Constants.displayMsgReqError(errorBean.getErrorCode(), MainMenu.this);
            }
        }

    }

    private void closeProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /*UIListener on Success*/
    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException,
            OfflineODataStoreException {
        if (operation == Operation.Create.getValue()) {
            if (Constants.getSyncType(getApplicationContext(), Constants.Attendances,
                    Constants.CreateOperation).equalsIgnoreCase("4")) {

                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(MainMenu.this)) {
                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    UtilConstants.onNoNetwork(MainMenu.this);
                } else {
                    OfflineManager.flushQueuedRequests(MainMenu.this);
                }
            }
        } else if (operation == Operation.Update.getValue()) {
            if (Constants.getSyncType(getApplicationContext(), Constants.Attendances,
                    Constants.UpdateOperation).equalsIgnoreCase("4")) {

                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(MainMenu.this)) {
                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    UtilConstants.onNoNetwork(MainMenu.this);
                } else {
                    OfflineManager.flushQueuedRequests(MainMenu.this);
                }
            }

        } else if (operation == Operation.OfflineFlush.getValue()) {

            if (Constants.getSyncType(getApplicationContext(), Constants.Attendances,
                    Constants.ReadOperation).equalsIgnoreCase("4")) {
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                if (mBooleanDayStartDialog)
                    mStrPopUpText = getString(R.string.dialog_day_started);
                if (mBooleanDayEndDialog)
                    mStrPopUpText = getString(R.string.dialog_day_ended);
                if (mBooleanDayResetDialog)
                    mStrPopUpText = getString(R.string.dialog_day_reset);

                displayPopUpMsg();
            } else {
                if (!UtilConstants.isNetworkAvailable(MainMenu.this)) {
                    try {
                        pdLoadDialog.dismiss();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    UtilConstants.onNoNetwork(MainMenu.this);
                } else {

                    String allCollection = "";
                    if (mBooleanDayStartDialog) {
                        allCollection = Constants.Attendances + "," + Constants.SPStockItems + ","
                                + Constants.SPStockItemDetails + "," + Constants.SPStockItemSNos + "," + Constants.SSINVOICES + "," + Constants.SSInvoiceItemDetails
                                + "," + Constants.SSInvoiceItemSerials + "," + Constants.FinancialPostings
                                + "," + Constants.FinancialPostingItemDetails
                                + "," + Constants.CPStockItems + "," + Constants.CPStockItemDetails + "," + Constants.CPStockItemSnos + "," + Constants.Schemes + "," + Constants.Tariffs + "," + Constants.SegmentedMaterials;
                    } else {
                        allCollection = Constants.Attendances;
                    }


                    OfflineManager.refreshRequests(getApplicationContext(), allCollection, MainMenu.this);
                }
            }


        } else if (operation == Operation.OfflineRefresh.getValue()) {
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            if (mBooleanDayStartDialog)
                mStrPopUpText = getString(R.string.dialog_day_started);
            if (mBooleanDayEndDialog)
                mStrPopUpText = getString(R.string.dialog_day_ended);
            if (mBooleanDayResetDialog)
                mStrPopUpText = getString(R.string.dialog_day_reset);

            displayPopUpMsg();
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {

            new NotificationSetClass(getApplicationContext());
            try {
                OfflineManager.getAuthorizations(getApplicationContext());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(MainMenu.this);
            LogManager.writeLogInfo(getString(R.string.store_opened));

            try {
                new SyncMustSellAsyncTask(MainMenu.this, new MessageWithBooleanCallBack() {
                    @Override
                    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
                        Log.d("clickedStatus Req", clickedStatus + "");
                        setUI();
                    }
                }, Constants.All).execute();
            } catch (Exception e) {
                setUI();
                e.printStackTrace();
            }
           /* try {
                getVisitData();
                try {
                    pdLoadDialog.dismiss();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                setUpViewPager();

            } catch (Exception e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }*/

        }

    }

    public void setUI() {
        try {
            getVisitData();
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            setUpViewPager();

        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    public void getVisitData() {
        Constants.setBirthDayRecordsToDataValut(MainMenu.this);
        Constants.setAlertsRecordsToDataValut(MainMenu.this);
        Constants.alTodayBeatRet.clear();
        Constants.TodayTargetRetailersCount = Constants.getVisitTargetForToday();
        Constants.TodayActualVisitRetailersCount = Constants.getVisitedRetailerCount(Constants.alTodayBeatRet);
        Constants.loadingTodayAchived(MainMenu.this, Constants.alTodayBeatRet);
    }

    /*Displays message in alert dialog*/
    public void displayPopUpMsg() {
        UtilConstants.showAlert(mStrPopUpText, MainMenu.this);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);

        builder.setMessage(R.string.do_u_want_exit_app)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Constants.iSAutoSync = false;
                                UpdatePendingRequest.instance = null;
                                if (OfflineManager.offlineStore != null) {
                                    try {
                                        OfflineManager.offlineStore.closeStore();
                                    } catch (ODataException e) {
                                        e.printStackTrace();
                                    }
                                }
                                OfflineManager.offlineStore = null;
                                OfflineManager.options = null;
                                OfflineManager.optionsMustSell = null;
                                OfflineManager.offlineStoreMustSell = null;
                                try {
                                    finishAffinity();
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                                finishAffinity();
                            }
                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


        builder.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_mainmenu, menu);
        menu.removeItem(R.id.menu_mainmenu_log);
        menu_init_db = menu.findItem(R.id.menu_re_register);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_mainmenu_export:
                Constants.Exportdbflag = true;
                pdLoadDialog = ProgressDialog.show(this, "",
                        getString(R.string.export_databse_to_sdcard), true);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            exportDB();
                            int count = 0;
                            while (Constants.Exportdbflag) {
                                pdLoadDialog.setProgress(++count);
                                Thread.sleep(100);
                                if (count == 100) {
                                    count = 0;
                                }
                            }
                            if (!Constants.Exportdbflag) {
                                pdLoadDialog.cancel();
                                onExportLogFinishRunnableThread();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


                break;
            case R.id.menu_mainmenu_import:

                Constants.importdbflag = true;
                pdLoadDialog = ProgressDialog.show(this, "",
                        getString(R.string.import_databse_from_sdcard), true);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            importDB();
                            int count = 0;
                            while (Constants.importdbflag) {
                                pdLoadDialog.setProgress(++count);
                                Thread.sleep(100);
                                if (count == 100) {
                                    count = 0;
                                }
                            }
                            if (!Constants.importdbflag) {
                                pdLoadDialog.cancel();
                                ondevicelogfinishRunnableThread();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
            case R.id.menu_mainmenu_aboutus:
                onAboutUs();
                break;

            case R.id.menu_mainmenu_log:
                onLog();
                break;
            case R.id.menu_mainmenu_settings:
                onSettings();
                break;
            case R.id.menu_mainmenu_update_pwd:
                onUpdatePwd();
                break;

            case R.id.menu_extend_pwd:
                showConformationDialogExtendPassword();
                break;

            case R.id.menu_exportcrashlog:
                onCrashlog();
                break;
            case R.id.menu_re_register:
                onReOpenStore();
                break;
            case R.id.menu_exportdatavault:
                if (Constants.isReadWritePermissionEnabled(MainMenu.this, MainMenu.this)) {
                    exportDatavaultData();
                }
                break;
            case R.id.menu_importdatavault:
                if (Constants.isReadWritePermissionEnabled(MainMenu.this, MainMenu.this)) {
                    importDatavaultData();
                }
                break;
            case R.id.menu_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        MainMenu.this, R.style.MyTheme);
                builder.setMessage(getString(R.string.logout_conformation))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        callLogout();

                                    }
                                });
                builder.setNegativeButton(getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }

                        });
                builder.show();

                break;
        }
        return true;
    }

    private void callLogout() {
        try {
            LogonCore.getInstance().removeStore();
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
        LogonCore.getInstance().deregister();
        if (OfflineManager.isOfflineStoreOpen()) {
            try {
                OfflineManager.closeOfflineStore(this, OfflineManager.options);
                OfflineManager.offlineStore = null;
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        OnlineStoreListener openListener = OnlineStoreListener.getInstance();
        OnlineODataStore store = openListener.getStore();
        if (store != null) {
            openListener.closeStore();
        }
        SharedPreferences preferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        //for passCode
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.clear();
        editor1.commit();

        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(MainMenu.this, RegistrationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    String datavaultData = "";

    private void importDatavaultData() {
        flagforimportDataVaultData = true;
        Constants.ImportDataVaultflag = true;
        datavaultData = "";
        pdLoadDialog = ProgressDialog.show(this, "",
                getString(R.string.import_datavault_data_from_sdcard), true);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Constants.ImportDataFailedErrorMsg = "";
                    boolean isFileExists = Constants.isFileExits(Constants.DataVaultFileName);
                    if (isFileExists) {
                        datavaultData = Constants.getTextFileData(Constants.DataVaultFileName);
                        putDataToDataVault(datavaultData);
                    } else {
                        flagforimportDataVaultData = false;
                        Constants.ImportDataFailedErrorMsg = getString(R.string.file_not_exist);
                        Constants.ImportDataVaultflag = false;
                    }
                    int count = 0;
                    while (Constants.ImportDataVaultflag) {
                        pdLoadDialog.setProgress(++count);
                        Thread.sleep(100);
                        if (count == 100) {
                            count = 0;
                        }
                    }
                    if (!Constants.ImportDataVaultflag) {
                        pdLoadDialog.cancel();
                        onImportDataVaultFinishRunnableThread();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogManager.writeLogError("importDatavaultData() (InterruptedException): " + e.getMessage());
                }
            }
        }).start();

    }

    /**
     * This method is runnable thread it stops progress dialog pop up.
     */
    private void onImportDataVaultFinishRunnableThread() {
        MainMenu.this.runOnUiThread(new Runnable() {
            public void run() {
                onImportdatavaultfinish();
            }
        });
    }

    private void onImportdatavaultfinish() {
        if (flagforimportDataVaultData) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
            builder.setMessage(R.string.import_datavault_from_sdcard_finish)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    flagforimportDataVaultData = true;
                                    Constants.deleteFileFromDeviceStorage(Constants.DataVaultFileName);
                                    dialog.cancel();
                                }
                            });
            builder.show();
        } else {
            String mStrErrMsg = Constants.ImportDataFailedErrorMsg.equalsIgnoreCase("") ? getString(R.string.import_datavault_from_sdcard_error_occurred) : Constants.ImportDataFailedErrorMsg;
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
            builder.setMessage(mStrErrMsg)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    flagforimportDataVaultData = true;
                                    dialog.cancel();
                                }
                            });
            builder.show();
        }
    }

    private void exportDatavaultData() {

        Constants.ExportDataVaultflag = true;
        pdLoadDialog = ProgressDialog.show(this, "",
                getString(R.string.export_datavault_data_to_storage), true);
        new Thread(new Runnable() {
            public void run() {
                try {
                    flagforexportDataVaultData = exportDataVault();
                    int count = 0;
                    while (Constants.ExportDataVaultflag) {
                        pdLoadDialog.setProgress(++count);
                        Thread.sleep(100);
                        if (count == 100) {
                            count = 0;
                        }
                    }
                    if (!Constants.ExportDataVaultflag) {
                        pdLoadDialog.cancel();
                        onExportDataVaultFinishRunnableThread();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * This method is runnable thread it stops progress dialog pop up.
     */
    private void onExportDataVaultFinishRunnableThread() {
        MainMenu.this.runOnUiThread(new Runnable() {
            public void run() {
                onexportdatavaultfinish();
            }
        });
    }

    private void onexportdatavaultfinish() {
        if (flagforexportDataVaultData) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
            builder.setMessage(R.string.export_datavault_to_sdcard_finish)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    flagforexportDataVaultData = true;
                                    Constants.removePendingList(MainMenu.this);
                                    dialog.cancel();
                                }
                            });
            builder.show();
        } else {
            String mStrErrMsg = Constants.ExportDataFailedErrorMsg.equalsIgnoreCase("") ? getString(R.string.export_datavault_to_sdcard_error_occurred) : Constants.ExportDataFailedErrorMsg;
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
            builder.setMessage(mStrErrMsg)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    flagforexportDataVaultData = true;
                                    dialog.cancel();
                                }
                            });
            builder.show();
        }
    }

    private void putDataToDataVault(String jsonStr) {
        try {
            Constants.setJsonStringDataToDataVault(jsonStr, MainMenu.this);
        } catch (Exception e) {
            e.printStackTrace();
            flagforimportDataVaultData = false;
        }
        Constants.ImportDataVaultflag = false;
    }

    private boolean exportDataVault() {
        boolean flagforlog = false;
        mStrFilePath = "";
        Constants.ExportDataFailedErrorMsg = "";
        try {
            FileWriter fileWriter = null;
            String jsonData = null;
            try {
                jsonData = Constants.makePendingDataToJsonString(MainMenu.this);
            } catch (Exception e) {
                e.printStackTrace();
                LogManager.writeLogError("exportDataVault() : " + e.getMessage());
                jsonData = "";
            }
            if (jsonData != null && !jsonData.equalsIgnoreCase("")) {
                fileWriter = new FileWriter(Environment.getExternalStorageDirectory()
                        + "/" + Constants.DataVaultFileName + "");
                fileWriter.write(jsonData);
                fileWriter.close();
                flagforlog = true;
            } else {
                Constants.ExportDataFailedErrorMsg = "No Pending Requests Available";
                flagforlog = false;
            }


        } catch (IOException e) {
            flagforlog = false;
            e.printStackTrace();
            LogManager.writeLogError("exportDataVault() (IOException) : " + e.getMessage());
        }
        Constants.ExportDataVaultflag = false;
        return flagforlog;
    }

    private void onReOpenStore() {
        try {
            LogManager.writeLogInfo(getString(R.string.db_reinit_request));
            showPendingReqAvailablePopUP();
        } catch (Exception e) {
            openOfflineStore();
        }
    }

    private void showPendingReqAvailablePopUP() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainMenu.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.pending_req_aval_do_want_reopen_store))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                LogManager.writeLogInfo(getString(R.string.db_reinit_confirmed));
                                openOfflineStore();

                            }
                        });
        builder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        dialog.cancel();
                    }

                });
        builder.show();
    }

    private void openOfflineStore() {
        if (UtilConstants.isNetworkAvailable(this)) {
            try {
                new OpenOfflineStore().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            UtilConstants.onNoNetwork(this);
        }
    }

    private void closeStore() {
        try {
            OfflineManager.closeOfflineStore(MainMenu.this, OfflineManager.options);
            OfflineManager.closeOfflineStoreMustSell(MainMenu.this, OfflineManager.optionsMustSell);
            LogManager.writeLogInfo(getString(R.string.store_removed));
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_during_offline_close + e.getMessage());
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
            pdLoadDialog = new ProgressDialog(MainMenu.this, R.style.ProgressDialogTheme);
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
                    OfflineManager.openOfflineStore(MainMenu.this, MainMenu.this);
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


    /**
     * This method calls Export Device log method later it shows alert dialog
     * message.
     */
    private void onCrashlog() {
        final boolean logflag = Exportlog();
        if (logflag) {
            alertDialogSendFileToChannelTeam();

//            UtilConstants.showAlert(getString(R.string.crash_log_to_sdcard_finish), MainMenu.this);
        }
    }

    private void alertDialogSendFileToChannelTeam() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainMenu.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.crash_log_to_sdcard_finish))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                shareFile();
                            }
                        });

        builder.show();
    }

    private void shareFile() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse("file://" + mStrFilePath);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        startActivity(Intent.createChooser(sharingIntent, "Share file using"));
    }

    /**
     * This method export crash log and create file.
     */
    String mStrFilePath = "";

    private boolean Exportlog() {
        boolean flagforlog;
        mStrFilePath = "";
        try {
            File filename = new File(Environment.getExternalStorageDirectory()
                    + "/mSecSales.log");
            filename.createNewFile();
            mStrFilePath = filename.getAbsolutePath();
            String cmd = "logcat -v long -f " + filename.getAbsolutePath();
            Runtime.getRuntime().exec(cmd);
            flagforlog = true;
        } catch (IOException e) {
            flagforlog = false;
            e.printStackTrace();
        }
        return flagforlog;
    }

    private void onSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void onLog() {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    /*Navigates to About us*/
    private void onAboutUs() {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    /*Navigates to update password*/
    private void onUpdatePwd() {
        Intent intent = new Intent(this, UpdatePasswordActivity.class);
        startActivity(intent);
    }

    /*Exports Offline store database*/
    public void exportDB() {

        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String PACKAGE_NAME;
        PACKAGE_NAME = getApplicationContext().getPackageName();
        String currentDBPath = Constants.offlineDBPath;
        String currentrqDBPath = Constants.offlineReqDBPath;
        String backupDBPath = Constants.backupDBPath;
        String backuprqDBPath = Constants.backuprqDBPath;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        File currentrqDB = new File(data, currentrqDBPath);
        File backuprqDB = new File(sd, backuprqDBPath);
        try {
            // Exporting Offline DB
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            // Exporting Offline rq DB
            source = new FileInputStream(currentrqDB).getChannel();
            destination = new FileOutputStream(backuprqDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();

            Constants.Exportdbflag = false;
            flagforexportDB = true;
        } catch (IOException e) {
            flagforexportDB = false;
            Constants.Exportdbflag = false;
        }
    }

    /*Import Offline DB into application*/
    public void importDB() {
        if (OfflineManager.isOfflineStoreOpen()) {

            try {
                OfflineManager.closeOfflineStore();
                LogManager.writeLogError(getString(R.string.msg_sync_terminated));
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_during_offline_close + e.getMessage());
            }
        }

        File isd = Environment.getExternalStorageDirectory();
        File idata = Environment.getDataDirectory();
        FileChannel isource = null;
        FileChannel idestination = null;
        File ibackupDB = new File(idata, Constants.icurrentDBPath);
        File icurrentDB = new File(isd, Constants.ibackupDBPath);

        File ibackupRqDB = new File(idata, Constants.icurrentRqDBPath);
        File icurrentRqDB = new File(isd, Constants.ibackupRqDBPath);
        try {
            isource = new FileInputStream(icurrentDB).getChannel();
            idestination = new FileOutputStream(ibackupDB).getChannel();
            idestination.transferFrom(isource, 0, isource.size());

            isource = new FileInputStream(icurrentRqDB).getChannel();
            idestination = new FileOutputStream(ibackupRqDB).getChannel();
            idestination.transferFrom(isource, 0, isource.size());

            isource.close();
            Constants.importdbflag = false;
            flagforimportDB = true;

            if (!OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.openOfflineStore(getApplicationContext(), MainMenu.this);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            flagforimportDB = false;
            Constants.importdbflag = false;
            e.printStackTrace();
        }


    }

    /**
     * This method is runnable thread it stops progress dialog pop up.
     */
    private void onExportLogFinishRunnableThread() {
        MainMenu.this.runOnUiThread(new Runnable() {
            public void run() {
                onexportfinish();
            }
        });
    }

    /**
     * This method is runnable thread it stops progress dialog pop up.
     */
    private void ondevicelogfinishRunnableThread() {
        MainMenu.this.runOnUiThread(new Runnable() {
            public void run() {
                onimportfinish();
            }
        });
    }

    /**
     * This method shows dialog message for export complete.
     */
    private void onexportfinish() {
        if (flagforexportDB) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
            builder.setMessage(R.string.export_databse_to_sdcard_finish)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    flagforexportDB = true;
                                    dialog.cancel();
                                }
                            });
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
            builder.setMessage(R.string.export_databse_to_sdcard_error_occurred)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    flagforexportDB = true;
                                    dialog.cancel();
                                }
                            });
            builder.show();
        }
    }

    /**
     * This method shows dialog message for import complete.
     */
    private void onimportfinish() {
        if (flagforimportDB) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
            builder.setMessage(R.string.import_databse_from_sdcard_finish)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    flagforimportDB = true;
                                    dialog.cancel();
                                }
                            });
            builder.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
            builder.setMessage(
                    R.string.import_databse_from_sdcard_error_occurred)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    flagforimportDB = true;
                                    dialog.cancel();
                                }
                            });
            builder.show();
        }
    }

    private void showConformationDialogExtendPassword() {

        ConstantsUtils.showPasswordRemarksDialogSetting(MainMenu.this, new PasswordDialogCallbackInterface() {
            @Override
            public void clickedStatus(boolean clickedStatus, String text) {
                if (clickedStatus)
                    extendPassword(MainMenu.this, text);
            }
        }, getString(R.string.alert_plz_enter_password));
    }

    private void extendPassword(Context context, String password) {
        if (UtilConstants.isNetworkAvailable(context)) {
            extendPWD(context, password);
        } else {
            Toast.makeText(MainMenu.this, getString(R.string.no_network_conn), Toast.LENGTH_LONG).show();
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
            extendPassword(mContext, Configuration.IDPURL, Configuration.IDPTUSRNAME, Configuration.IDPTUSRPWD, pUserName, pUserPwd);
        } else {
            Toast.makeText(MainMenu.this, "Unable to get Username and Password", Toast.LENGTH_LONG).show();
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

    private void displayErrorMessage(final String strMsg, final Context mContext) {
        try {
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        pdLoadDialog.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (strMsg.contains("successfully")) {
                        exitAPP();
                    }
                    Toast.makeText(MainMenu.this, strMsg, Toast.LENGTH_LONG).show();
//                    UtilConstants.showErrorMsgSnackbar(contextView, strMsg,mContext);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exitAPP() {
        UtilConstants.dialogBoxWithCallBack(MainMenu.this, "", getString(R.string.extend_pwd_updated_succefully), getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean clickedStatus) {
                finishAffinity();
                System.exit(0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                onBackPressed();
                break;

        }
    }
}
