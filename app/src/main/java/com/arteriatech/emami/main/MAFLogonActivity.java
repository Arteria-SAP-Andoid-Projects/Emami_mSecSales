/*
package com.arteriatech.ss.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.ss.alerts.AlertsActivity;
import com.arteriatech.ss.common.Constants;
import com.arteriatech.ss.common.MSFAApplication;
import com.arteriatech.ss.database.EventDataSqlHelper;
import com.arteriatech.ss.database.EventUserDetail;
import com.arteriatech.ss.login.ChangePasswordActivity;
import com.arteriatech.ss.login.LoginActivity;
import com.arteriatech.ss.login.PinLoginActivity;
import com.arteriatech.ss.msecsales.R;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreContext;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.maf.tools.logon.logonui.api.LogonListener;
import com.sap.maf.tools.logon.logonui.api.LogonUIFacade;
import com.sap.maf.tools.logon.manager.LogonContext;
import com.sap.maf.tools.logon.manager.LogonManager;

import java.util.Hashtable;

public class MAFLogonActivity extends Activity implements LogonListener {
    private final String TAG = MAFLogonActivity.class.getSimpleName();
    private LogonUIFacade mLogonUIFacade;
    public Context mContext;
    public MSFAApplication mApplication;
    private boolean isNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        if(!Constants.CustomRegEnabled) {
            EventUserDetail eventDataSqlHelper = new EventUserDetail(this);
            Constants.EventUserHandler = eventDataSqlHelper.getWritableDatabase();
            Constants.events = new EventDataSqlHelper(getApplicationContext());
            LogManager.initialize(MAFLogonActivity.this);
            mApplication = (MSFAApplication) getApplication();
            Intent intent = getIntent();
            if (intent != null) {
                isNotification = intent.getBooleanExtra(Constants.EXTRA_OPEN_NOTIFICATION, false);
            }

            //Initialize LOGONCORE for MAF LOGON
//            initializeLogonCore();




            //STEP1: Hide MobilePlace window
            SharedPreferences prefs = getSharedPreferences(LogonCore.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor pEditor = prefs.edit();
            pEditor.putBoolean(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_MOBILEPLACE.toString(), false);
            pEditor.commit();

            // get an instance of the LogonUIFacade
            mLogonUIFacade = LogonUIFacade.getInstance();

            // set context reference
            mContext = this;

            mLogonUIFacade.init(this, this, Constants.APP_ID);

            //hide below two line if Relay server Configuration
            if (!Constants.IS_RELAY_SERVER) {
                mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_SUPSERVERFARMID, true);
                mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_URLSUFFIX, true);
            }

            mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_MOBILEUSER, true);
            mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_ACTIVATIONCODE, true);
            mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_GATEWAYCLIENT, true);
            mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_SUPSERVERDOMAIN, true);
            mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_PINGPATH, true);
            mLogonUIFacade.isFieldHidden(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_GWONLY, true);

            mLogonUIFacade.setDefaultValue(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_SUPSERVERURL, Constants.server_Text);
            mLogonUIFacade.setDefaultValue(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_SUPSERVERPORT, Constants.port_Text);
            mLogonUIFacade.setDefaultValue(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_SECCONFIG, Constants.secConfig_Text);
            mLogonUIFacade.setDefaultValue(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_PASSWORD, Constants.pwd_text);


            // Relay server Configuration formid and url suffix
            if (Constants.IS_RELAY_SERVER) {
                mLogonUIFacade.setDefaultValue(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_URLSUFFIX, Constants.suffix);
                mLogonUIFacade.setDefaultValue(LogonCore.SharedPreferenceKeys.PREFERENCE_ID_SUPSERVERFARMID, Constants.farm_ID);
            }

            this.showLogonScreen();

//        }else{
//            Intent goToNextActivity = new Intent(this, RegistrationActivity.class);
//            startActivity(goToNextActivity);
//            finish();
//        }
    }

    */
/*Initializes LogonCore for MAFLOGON*//*

    void initializeLogonCore() {
        try {
            LogonCore lgCore = LogonCore.getInstance();
            if (lgCore != null && lgCore.isStoreAvailable()) {
                LogonCoreContext lgCtx = lgCore.getLogonContext();
                if (lgCtx != null && lgCtx.isSecureStoreOpen()) {
                    try {
                        if (!TextUtils.isEmpty(lgCtx.getConnId())) {
                            Intent intent = new Intent();
                            intent.setClass(this, MainMenu.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (LogonCoreException e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void objectFromSecureStoreForKey() {

    }

    @Override
    public void onApplicationSettingsUpdated() {

    }

    @Override
    public void onBackendPasswordChanged(boolean arg0) {

    }

    */
/*on logon finish this method executes*//*

    @Override
    public void onLogonFinished(String message, boolean isSuccess,
                                LogonContext lgContext) {
        // Logon successful - setup global request manager
        Log.v(TAG, message);

        if (isSuccess) {
            try {
                LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
                String mStrBackEndUser = lgCtx.getBackendUser();
                String pwd = lgCtx.getBackendPassword();
                mApplication.getParameters(mStrBackEndUser, pwd);



                // get Application Connection ID
                String appConnID = LogonCore.getInstance().getLogonContext()
                        .getConnId();
                Log.d(TAG, Constants.logon_finished_appcid + appConnID);
                Log.d(TAG, Constants.logon_finished_aendpointurl + lgContext.getEndPointUrl());


                SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME,
                        0);
                String strPref = settings.getString(Constants.username, null);
                if (strPref == null) {
                    boolean isFromNotification = getIntent().getBooleanExtra(Constants.isFromNotification, false);

                    LogManager.writeLogInfo(getString(R.string.msg_success_registration));

                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.AppName_Key, Constants.APPS_NAME);
                    editor.putString(Constants.UserName_Key, mStrBackEndUser);
                    editor.putString(Constants.Password_Key, Constants.pwd_text);
                    editor.putString(Constants.serverHost_key, Constants.server_Text);
                    editor.putString(Constants.serverPort_key, Constants.port_Text);
                    editor.putString(Constants.serverClient_key, Constants.client_Text);
                    editor.putString(Constants.companyid_key, Constants.cmpnyId_Text);
                    editor.putString(Constants.securityConfig_key, Constants.secConfig_Text);
                    editor.putString(Constants.appConnID_key, Constants.appConID_Text);
                    editor.putString(Constants.appID_key, Constants.appID_Text);
                    editor.putString(Constants.appEndPoint_Key, Constants.appEndPoint_Text);
                    editor.putString(Constants.pushEndPoint_Key, Constants.pushEndPoint_Text);
                    editor.putString(Constants.SalesPersonName, "");
                    editor.putString(Constants.SalesPersonMobileNo, "");
                    editor.putString(Constants.BirthDayAlertsDate, UtilConstants.getDate1());
                    editor.putBoolean(Constants.isPasswordSaved, true);
                    editor.putBoolean(Constants.isDeviceRegistered, true);
                    editor.putBoolean(Constants.isFirstTimeReg, true);
                    editor.putBoolean(Constants.isForgotPwdActivated, false);
                    editor.putBoolean(Constants.isUserIsLocked, false);
                    editor.putString(Constants.ForgotPwdOTP, "");
                    editor.putString(Constants.ForgotPwdGUID, "");
                    editor.putInt(Constants.VisitSeqId, 0);

                    editor.putInt(Constants.BirthdayAlertsCount, 0);
                    editor.putInt(Constants.TextAlertsCount, 0);
                    editor.putInt(Constants.AppointmentAlertsCount, 0);
                    editor.commit();
                    alertsKeyExistOrNot();
                    //Create database for sync history table
                    createSyncDatabase();
                    addAlertsKeyValueInDataVault();

                    Intent goToNextActivity = new Intent(this, MainMenu.class);
                    goToNextActivity.putExtra(Constants.isFromNotification, isFromNotification);
                    //Starting Main Menu
                    startActivity(goToNextActivity);
                    finish();
                } else {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                    sharedPreferences.getString(Constants.QUICK_PIN, "");
                    String permission = sharedPreferences.getString(Constants.QUICK_PIN_ACCESS, "");
                    String enablePermission = sharedPreferences.getString(Constants.ENABLE_ACCESS, "");


                    if ("yes".equalsIgnoreCase(permission) && "yes".equalsIgnoreCase(enablePermission)) {
                        Intent pinIntent = new Intent(this, PinLoginActivity.class);
                        startActivity(pinIntent);
                        finish();

                    } else {
                        SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_NAME, 0);
                        if (!sharedPref.getBoolean(Constants.isForgotPwdActivated, false)) {
                            Constants.isBoolPwdgenerated = false;
                            if (isNotification) {
                                Intent intentAlert = new Intent(this,
                                        AlertsActivity.class);
                                startActivity(intentAlert);
                            } else {
                                Intent goToNextActivity = new Intent(this, LoginActivity.class);
                                startActivity(goToNextActivity);
                            }
                            finish();
                        } else {
                            Intent intentNavChangePwdScreen = new Intent(MAFLogonActivity.this, ChangePasswordActivity.class);
                            intentNavChangePwdScreen.putExtra(Constants.OTP, sharedPref.getString(Constants.ForgotPwdOTP, ""));
                            intentNavChangePwdScreen.putExtra(Constants.GUIDVal, sharedPref.getString(Constants.ForgotPwdGUID, ""));
                            startActivity(intentNavChangePwdScreen);
                            finish();
                        }
                    }
                }

            } catch (LogonManager.LogonManagerException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                LogManager.writeLogError(Constants.device_reg_failed_txt, e);
            } catch (LogonCoreException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                LogManager.writeLogError(Constants.device_reg_failed_txt, e);
            }
        }
    }

    */
/*
    TODO First time registration add birthday alerts key and value in data vault
     *//*

    private void addAlertsKeyValueInDataVault() {
        try {
            //noinspection deprecation
            LogonCore.getInstance().addObjectToStore(Constants.BirthDayAlertsKey, "");
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
    }

    */
/*Creates table for Sync history in SQLite DB*//*

    private void createSyncDatabase() {
        Hashtable hashtable = new Hashtable<>();
        hashtable.put(Constants.SyncGroup, "");
        hashtable.put(Constants.Collections, "");
        hashtable.put(Constants.TimeStamp, "");
        try {
            Constants.events.crateTableConfig(Constants.SYNC_TABLE, hashtable);
            getSyncHistoryTable();
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_creating_sync_db
                    + e.getMessage());
        }
    }

    */
/*Sync History table for Sync*//*

    private void getSyncHistoryTable() {
        String[] definingReqArray = Constants.getDefinigReq(getApplicationContext());
        for (int i = 0; i < definingReqArray.length; i++) {
            String colName = definingReqArray[i];
            if (colName.contains("?$")) {
                String splitCollName[] = colName.split("\\?");
                colName = splitCollName[0];
            }
            try {
                Constants.events.inserthistortTable(Constants.SYNC_TABLE, "",
                        Constants.Collections, colName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSecureStorePasswordChanged(boolean arg0, String arg1) {

    }

    @Override
    public void onUserDeleted() {

    }

    @Override
    public void registrationInfo() {

    }

    */
/*Displays MAF LOGON Screen*//*

    private void showLogonScreen() {
        // ask LogonUIFacede to present the logon screen
        // set the resulting view as the content view for this activity
        setContentView(mLogonUIFacade.logon());

        mLogonUIFacade.showSplashScreen(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.showLogonScreen();
    }

    @Override
    public void onRefreshCertificate(boolean arg0, String arg1) {

    }

    public void alertsKeyExistOrNot() {
        try {
            SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_NAME, 0);
            if (!sharedPref.contains(Constants.AppointmentAlertsCount)) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(Constants.BirthdayAlertsCount, 0);
                editor.putInt(Constants.TextAlertsCount, 0);
                editor.putInt(Constants.AppointmentAlertsCount, 0);

                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
*/
