package com.arteriatech.emami.registration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.MSFAApplication;
import com.arteriatech.emami.interfaces.AsyncTaskCallBack;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.mutils.actionbar.ActionBarView;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.registration.RegistrationModel;
import com.arteriatech.mutils.registration.SupportActivity;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreContext;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.maf.tools.logon.core.LogonCoreListener;
import com.sap.maf.tools.logon.logonui.api.LogonListener;
import com.sap.maf.tools.logon.logonui.api.LogonUIFacade;
import com.sap.maf.tools.logon.manager.LogonContext;
import com.sybase.persistence.DataVault;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class RegistrationCustActivity extends AppCompatActivity implements LogonCoreListener, LogonListener {
    /*sharedPreference key don't change*/
    public static final String KEY_AppName = "AppName";
    public static final String KEY_username = "username";
    public static final String KEY_password = "password";
    public static final String KEY_serverHost = "serverHost";
    public static final String KEY_serverPort = "serverPort";
    public static final String KEY_serverClient = "serverClient";
    public static final String KEY_companyid = "companyid";
    public static final String KEY_securityConfig = "securityConfig";
    public static final String KEY_appConnID = "appConnID";
    public static final String KEY_appID = "appID";
    public static final String KEY_isPasswordSaved = "isPasswordSaved";
    public static final String KEY_isDeviceRegistered = "isDeviceRegistered";
    public static final String KEY_isFirstTimeReg = "isFirstTimeReg";
    public static final String KEY_isForgotPwdActivated = "isForgotPwdActivated";
    public static final String KEY_isManadtoryUpdate = "isManadtoryUpdate";
    public static final String KEY_isUserIsLocked = "isUserIsLocked";
    public static final String KEY_ForgotPwdOTP = "ForgotPwdOTP";
    public static final String KEY_ForgotPwdGUID = "ForgotPwdGUID";
    public static final String KEY_isFOSUserRole = "isFOSUserRole";
    public static final String KEY_MaximumAttemptKey = "MaximumAttemptKey";
    public static final String KEY_VisitSeqId = "VisitSeqId";
    public static final String KEY_appEndPoint = "appEndPoint";
    public static final String KEY_pushEndPoint = "pushEndPoint";
    public static final String KEY_SalesPersonName = "SalesPersonName";
    public static final String KEY_SalesPersonMobileNo = "SalesPersonMobileNo";
    public static final String KEY_BirthDayAlertsDate = "BirthDayAlertsDate";

    public static final String EXTRA_IS_FROM_REGISTRATION = "isRegistrationExtra";
    public static final String EXTRA_BUNDLE_REGISTRATION = "isRegBundleExtra";


    public static String loginUser_Text = "";
    public static String login_pwd = "";


    LogonCore logonCore = null;
    AlertDialog progressDialog=null;
    private LogonUIFacade mLogonUIFacade;
    private RegistrationModel registrationModel = null;
    private EditText txtusername,
            txtLoginPassword;
    private CheckBox savePass;
    private Button register, support, clear;
    private TextView tvVersion;
    private ImageView ivLogo;
    private Handler mHandler = null;
    private int mCurrentAttempt = 0;
    private int totalAttempt = 3;
    private SharedPreferences sharedPreferences;
    private Context mContext;
    private Bundle bundleExtra = null;
    private Toolbar toolbar;
    private TextInputLayout ilPassword;
    private TextInputLayout ilUserName;
    String userName;
    String password;
//    private RelativeLayout rlRegistration;
//    private View progressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            registrationModel = (RegistrationModel) bundleExtras.getSerializable(UtilConstants.RegIntentKey);
            bundleExtra = bundleExtras.getBundle(UtilRegistrationActivity.EXTRA_BUNDLE_REGISTRATION);
        }
        LogManager.initialize(RegistrationCustActivity.this);
        mContext = RegistrationCustActivity.this;
        mHandler = new Handler();
        if (registrationModel != null) {
            int icon = 0;
            if (registrationModel.getAppActionBarIcon() != 0) {
//                icon = AppCompatResources.getDrawable(UtilRegistrationActivity.this, registrationModel.getAppActionBarIcon());
                icon = registrationModel.getAppActionBarIcon();
            }

//            ActionBarView.initActionBarView(this, false, getString(R.string.app_name), null, icon);
            sharedPreferences = getSharedPreferences(registrationModel.getShredPrefKey(), 0);
            String strPref = sharedPreferences.getString(KEY_username, null);
            // show login form
            setContentView(R.layout.activity_registration_customize);
            toolbar = (Toolbar) findViewById(com.arteriatech.mutils.R.id.toolbar);
//            rlRegistration = (RelativeLayout) findViewById(R.id.rlRegistration);
//            progressView = (View) findViewById(R.id.progressBarView);
            if (!TextUtils.isEmpty(strPref)) {
                initLogonCore(RegistrationCustActivity.this, registrationModel);

                try {
                    String mStrUserName = sharedPreferences.getString(KEY_username, "");
                    logonCore.unlockStore(mStrUserName);
                } catch (LogonCoreException e) {
                    LogManager.writeLogError("unlockStore with login ID"+e.getLocalizedMessage());
                    e.printStackTrace();
                }

                try {
                    try {
                        String mUserName = logonCore.getLogonContext().getBackendUser();
                        LogManager.writeLogDebug("getLogonContext() UserName: "+mUserName);
                    } catch (LogonCoreException e) {
                        LogManager.writeLogError("logonCore.getLogonContext() "+e.getLocalizedMessage());
                        try {
                            logonCore.unlockStore(null);
                        } catch (LogonCoreException ex) {
                            LogManager.writeLogError("unlockStore with null"+ex.getLocalizedMessage());
                            ex.printStackTrace();
                        }
                        try {
                            String mUserName = logonCore.getLogonContext().getBackendUser();
                            LogManager.writeLogDebug("getLogonContext() UserName: "+mUserName);
                        } catch (LogonCoreException exc) {
                            LogManager.writeLogError("logonCore.getLogonContext() "+exc.getLocalizedMessage());
                            exc.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (registrationModel.getLogInActivity() != null) {
                    Intent intentNavChangePwdScreen = new Intent(RegistrationCustActivity.this, registrationModel.getLogInActivity());
                    intentNavChangePwdScreen.putExtra(EXTRA_IS_FROM_REGISTRATION, true);
                    if (bundleExtra != null)
                        intentNavChangePwdScreen.putExtra(EXTRA_BUNDLE_REGISTRATION, bundleExtra);
                    startActivity(intentNavChangePwdScreen);
                    finish();
                }
            } else {

                ActionBarView.initActionBarView(this,toolbar,false,getString(com.arteriatech.mutils.R.string.app_name),icon,0);
                initializeVariables();
            }
        }
        String androidId = Settings.Secure.getString(RegistrationCustActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        init(androidId);
       // initDialog(androidId);
    }

    private void initializeVariables() {
        register = (Button) findViewById(com.arteriatech.mutils.R.id.btRegister);
        tvVersion = (TextView) findViewById(com.arteriatech.mutils.R.id.tv_version);
        tvVersion.setText(registrationModel.getAppVersionName());
        ivLogo = (ImageView) findViewById(com.arteriatech.mutils.R.id.ivLogo);
        txtusername = (EditText) findViewById(com.arteriatech.mutils.R.id.et_username);
        ilUserName = (TextInputLayout) findViewById(com.arteriatech.mutils.R.id.ilUserName);
        ilPassword = (TextInputLayout) findViewById(com.arteriatech.mutils.R.id.ilPassword);
        txtLoginPassword = (EditText) findViewById(com.arteriatech.mutils.R.id.et_password);
        if (registrationModel.getAppLogo() != 0)
           // ivLogo.setImageDrawable(AppCompatResources.getDrawable(RegistrationCustActivity.this, registrationModel.getAppLogo()));
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = UtilConstants.showProgressDialogs(RegistrationCustActivity.this, getString(R.string.register_with_server_plz_wait), false);
                String androidId = Settings.Secure.getString(RegistrationCustActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                String host = "https://" + Configuration.server_Text + "/" + Configuration.APP_ID;
                //String URL = host+"/"+Constants.UserProfileAuthSet+"?$filter=Application eq 'MSEC' and AuthOrgTypeID eq '000038' and AuthOrgValue eq '"+androidId.toUpperCase()+"'";//not working for lower device.
                String URL = host+"/"+Constants.UserProfileAuthSet+"?$filter=Application%20eq%20%27MSEC%27%20and%20AuthOrgTypeID%20eq%20%27000038%27%20and%20AuthOrgValue%20eq%20%27"+androidId.toUpperCase()+"%27";//working for both.
                userName=txtusername.getText().toString();
                password=txtLoginPassword.getText().toString();
                String userCredentials = userName + ":" + password;
                String Auth = "Basic " + Base64.encodeToString(userCredentials.getBytes(StandardCharsets.UTF_8), 2);
                if(UtilConstants.isNetworkAvailable(mContext)) {
                    if (URL != null && !URL.isEmpty() && Auth != null && !Auth.isEmpty()) {

                        validateIMEILocal(new AsyncTaskCallBack() {
                            @Override
                            public void onStatus(boolean status, String values) {
                                if (status) {
                                    onRegister(RegistrationCustActivity.this);
                                } else {
                                    closeProgDialog();
                                    diaplyErrorMsg(values, 401);
                                }
                            }
                        }, Auth, URL);
                    } else {
                        onRegister(RegistrationCustActivity.this);
                    }
                }
                else
                {
                    try {
                        Toast.makeText(getApplicationContext(),"Registration cannot be performed due to network unavailability",Toast.LENGTH_LONG).show();
                        UtilConstants.hideProgressDialog(progressDialog);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        clear = (Button) findViewById(com.arteriatech.mutils.R.id.btClear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                onClear();
            }
        });
        support = (Button) findViewById(com.arteriatech.mutils.R.id.btSupport);
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                support();
            }
        });

        savePass = (CheckBox) findViewById(com.arteriatech.mutils.R.id.ch_save_pass);
        savePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savePass.isChecked()) {

                } else {

                }
            }
        });
        //TODO need to enable for validation
//        resetEditTextBackground(txtusername);
//        resetEditTextBackground(txtLoginPassword);
    }



    @Override
    public void registrationFinished(boolean success, String s, int errorCode, DataVault.DVPasswordPolicy dvPasswordPolicy) {
        if (success) {
            try {
                // if successful, persist registration
                // DO NOT follow the manual storage of APPCID as shown in SMP docs
                logonCore.persistRegistration();
                onSaveConfig(registrationModel);
//                registrationModel.getRegistrationCallBack().onRegistrationCallBack(success,"","0");
                if (registrationModel.getRegisterSuccessActivity() != null) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgDialog();
                            try {
                                Toast.makeText(mContext, mContext.getString(com.arteriatech.mutils.R.string.registration_success), Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Intent intentNavChangePwdScreen = new Intent(RegistrationCustActivity.this, registrationModel.getRegisterSuccessActivity());
                            intentNavChangePwdScreen.putExtra(EXTRA_IS_FROM_REGISTRATION, true);
                            if (bundleExtra != null)
                                intentNavChangePwdScreen.putExtra(EXTRA_BUNDLE_REGISTRATION, bundleExtra);
                            startActivity(intentNavChangePwdScreen);
                            finish();
                        }
                    });

                } else {
                    try {
                        logonCore.removeStore();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    closeProgDialog();
                }

            } catch (LogonCoreException e) {
                e.printStackTrace();
                try {
                    logonCore.removeStore();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                closeProgDialog();
            }
        } else {
            try {
                logonCore.removeStore();
            } catch (Exception e) {
                e.printStackTrace();
            }
            closeProgDialog();
            String err_msg = UtilConstants.getErrorMsg(errorCode, RegistrationCustActivity.this);


            diaplyErrorMsg(err_msg, errorCode);
        }
    }

    /**
     * This method user credential saved into shared preferences.
     */
    private void onSaveConfig(RegistrationModel registrationModel) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
        String mStrBackEndUser = "";
        String mStrBackEndPWD = "";
        String appConnID = "";
        try {
            appConnID = LogonCore.getInstance().getLogonContext()
                    .getConnId();
            mStrBackEndUser = lgCtx.getBackendUser();
            mStrBackEndPWD = lgCtx.getBackendPassword();
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
        editor.putString(KEY_username, mStrBackEndUser);
        editor.putString(KEY_password, mStrBackEndPWD);
        editor.putString(KEY_serverHost, registrationModel.getServerText());
        editor.putString(KEY_serverPort, registrationModel.getPort());
        editor.putString(KEY_securityConfig, registrationModel.getSecConfig());
        editor.putString(KEY_appConnID, appConnID);
        editor.putString(KEY_appEndPoint, "");
        editor.putString(KEY_pushEndPoint, "");
        editor.putString(KEY_SalesPersonName, "");
        editor.putString(KEY_SalesPersonMobileNo, "");
        editor.putBoolean(KEY_isPasswordSaved, true);
        editor.putBoolean(KEY_isDeviceRegistered, true);
        editor.putBoolean(KEY_isFirstTimeReg, true);
        editor.putBoolean(KEY_isForgotPwdActivated, false);
        editor.putBoolean(KEY_isManadtoryUpdate, true);
        editor.putBoolean(KEY_isUserIsLocked, false);
        editor.putString(KEY_ForgotPwdOTP, "");
        editor.putString(KEY_ForgotPwdGUID, "");
        editor.putString(KEY_isFOSUserRole, "");
        editor.putInt(KEY_MaximumAttemptKey, 0);
        editor.putInt(KEY_VisitSeqId, 0);
        editor.putString(KEY_BirthDayAlertsDate, UtilConstants.getDate1());
        editor.apply();
    }

    @Override
    public void deregistrationFinished(boolean b) {

    }

    @Override
    public void backendPasswordChanged(boolean b) {

    }

    @Override
    public void applicationSettingsUpdated() {

    }

    @Override
    public void traceUploaded() {

    }

    @Override
    public void onLogonFinished(String s, boolean b, LogonContext logonContext) {

    }

    @Override
    public void onSecureStorePasswordChanged(boolean b, String s) {

    }

    @Override
    public void onBackendPasswordChanged(boolean b) {

    }

    @Override
    public void onUserDeleted() {

    }

    @Override
    public void onApplicationSettingsUpdated() {

    }

    @Override
    public void registrationInfo() {

    }

    @Override
    public void objectFromSecureStoreForKey() {

    }

    @Override
    public void onRefreshCertificate(boolean b, String s) {

    }

    private void initLogonCore(Context mContext, RegistrationModel registrationModel) {
        try {
            // get instance
            logonCore = LogonCore.getInstance();

            // get an instance of the LogonUIFacade
            mLogonUIFacade = LogonUIFacade.getInstance();

            mLogonUIFacade.init(this, mContext, registrationModel.getAppID());

            // set listener for LogonCore
            logonCore.setLogonCoreListener(this);

            // boot up
            logonCore.init(this, registrationModel.getAppID());

            // check if store is open and available
            // open and unlock if not
            try {
                if (!logonCore.isStoreAvailable()) {
                    logonCore.createStore(null, false);
                }
            } catch (LogonCoreException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            LogManager.writeLogError(this.getClass().getSimpleName() + ".initLogonCore: " + e.getMessage());
        }
    }

    /**
     * This method clear user credential.
     */
    private void onClear() {
        ilUserName.setErrorEnabled(false);
        ilPassword.setErrorEnabled(false);
        txtusername.setText("");
        txtLoginPassword.setText("");
    }

    /**
     * This method navigates to AboutUs activity.
     */
    private void support() {
        Intent intent = new Intent(RegistrationCustActivity.this, SupportActivity.class);
        intent.putExtra(UtilConstants.RegIntentKey, registrationModel);
        intent.putExtra(EXTRA_IS_FROM_REGISTRATION, true);
        if (bundleExtra != null)
            intent.putExtra(EXTRA_BUNDLE_REGISTRATION, bundleExtra);
        startActivity(intent);
    }

    public void onDeviceReg() {
        // create LogonCoreContext
        // this is used for registration

        loginUser_Text = txtusername.getText().toString().trim();
        login_pwd = txtLoginPassword.getText().toString().trim();

        LogonCoreContext context = null;
        try {
            context = logonCore.getLogonContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // set configuration
        try {
            context.setHost(registrationModel.getServerText());
            context.setPort(Integer.parseInt(registrationModel.getPort()));
            context.setHttps(registrationModel.getHttps());
            if (!registrationModel.getFormID().equalsIgnoreCase("")) {
                context.setFarmId(registrationModel.getFormID());
                context.setResourcePath(registrationModel.getSuffix());
            }
            context.setSecurtityConfig(registrationModel.getSecConfig());
            context.setBackendUser(loginUser_Text);
            context.setBackendPassword(login_pwd);


        } catch (LogonCoreException e) {
            e.printStackTrace();
        }

        try {
            // call registration
            logonCore.register(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method register values into server.
     */
    private void onRegister(Context mContext) {
        if (!this.getValues()) {
            if (this.mCurrentAttempt + 1 <= this.totalAttempt) {
                this.requestOnline(mContext);
            } else {
                UtilConstants.showAlert("[A1000] " + this.getString(R.string.wrong_psw_error_msg_3, new Object[]{String.valueOf(this.totalAttempt)}), this);
            }
        }

    }

    private void requestOnline(final Context mContext) {
        if (UtilConstants.isNetworkAvailable(mContext)) {
            //this.progressDialog = UtilConstants.showProgressDialogs(this, this.getString(R.string.register_with_server_plz_wait), false);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    RegistrationCustActivity.this.initLogonCore(mContext, RegistrationCustActivity.this.registrationModel);
                    (RegistrationCustActivity.this.new AsynTaskRegistration()).execute(new Void[0]);
                }
            });

        } else {
            UtilConstants.dialogBoxWithCallBack(this, "", this.getString(R.string.reg_no_network_conn, new Object[]{"A3000"}), this.getString(R.string.network_retry), this.getString(R.string.cancel), false, new DialogCallBack() {
                public void clickedStatus(boolean clickedStatus) {
                    if (clickedStatus) {
                        RegistrationCustActivity.this.requestOnline(RegistrationCustActivity.this);
                    }

                }
            });
            LogManager.writeLogError(this.getString(R.string.reg_no_network_conn, new Object[]{"A3000"}));
        }

    }

    /**
     * This method checks weather fill all required fields or not.
     */
    private boolean getValues() {
        int isValidMandotry = 0;
        ilUserName.setErrorEnabled(false);
        ilPassword.setErrorEnabled(false);
         userName = txtusername.getText().toString();
         password = txtLoginPassword.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            isValidMandotry = 1;
            ilUserName.setErrorEnabled(true);
            ilUserName.setError(getString(com.arteriatech.mutils.R.string.validation_plz_enter_user_name));
        }else {
            boolean areSpaces = checkIfSpaces(userName);
            if (areSpaces) {
                isValidMandotry = 3;
                ilUserName.setErrorEnabled(true);
                ilUserName.setError(getString(com.arteriatech.mutils.R.string.validation_user_name_space));
            }
        }
        if (TextUtils.isEmpty(password)) {
            isValidMandotry = 1;
            ilPassword.setErrorEnabled(true);
            ilPassword.setError(getString(com.arteriatech.mutils.R.string.validation_plz_enter_psw));
        } else {
            boolean areSpaces = checkIfSpaces(password);
            if (areSpaces) {
                ilPassword.setErrorEnabled(true);
                ilPassword.setError(getString(com.arteriatech.mutils.R.string.validation_psw_space));
                isValidMandotry = 3;
            }
        }
        return isValidMandotry != 0;
    }

    /**
     * This method checks spaces available or not.
     */
    public boolean checkIfSpaces(String str) {
        boolean result = false;

        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(str);
        result = matcher.find();

        return result;

    }

    private void closeProgDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    UtilConstants.hideProgressDialog(progressDialog);
//            progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void diaplyErrorMsg(final String err_msg, final int err_code) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String mStrAttemptText = "";
                String errorMsg = err_msg;
                if (err_code == UtilConstants.ERROR_UN_AUTH) {
                    mCurrentAttempt++;
                    mStrAttemptText = getString(com.arteriatech.mutils.R.string.attempt) + " " + mCurrentAttempt + "/" + totalAttempt + "";
                    if (mCurrentAttempt >= totalAttempt) {
                        errorMsg = "[" + UtilConstants.ERROR_CODE_REGISTRATION_USER_LOCKED + "] " + getString(com.arteriatech.mutils.R.string.wrong_psw_error_msg_3, String.valueOf(totalAttempt));//String.format(getString(R.string.wrong_psw_error_msg_3),String.valueOf(totalAttempt));
                    } else {
                        int numberOfAttempt = totalAttempt - mCurrentAttempt;
                        String stAtmt = " attempts";
                        if (numberOfAttempt == 1) {
                            stAtmt = " attempt";
                        }
                        errorMsg = "[" + err_code + "] " + getString(com.arteriatech.mutils.R.string.error_un_autorized, String.valueOf(numberOfAttempt) + stAtmt);
                    }
                }
                LogManager.writeLogError(errorMsg);
                UtilConstants.showAlertWithHeading(errorMsg, RegistrationCustActivity.this, mStrAttemptText);
            }
        }, 100);

    }

    @SuppressLint("LongLogTag")
    private class AsynTaskRegistration extends AsyncTask<Void, Boolean, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isException = false;
            try {
                onDeviceReg();
            } catch (Exception e) {
                isException = true;
                LogManager.writeLogError(this.getClass().getSimpleName() + ".doInBackground: " + e.getMessage());
            }
            return isException;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                closeProgDialog();
            }
        }
    }


    public static void validateIMEILocal(final AsyncTaskCallBack asyncTaskCallBack, final String BasicAuthHeaderValue, final String URL){
        new Thread(new Runnable() {
            @Override
            public void run() {


                HttpsURLConnection connection = null;
                String resultJson = "";
                //String host = "https://" + Configuration.server_Text + "/" + Configuration.APP_ID;


              //  String URL = ""+host+"UserProfileAuthSet?$filter=Application eq 'MSEC' and AuthOrgTypeID eq '000038' and AuthOrgValue eq '"+ MSFAApplication.APP_DEVICEID+"'";
                int responseCode = 0;//put break point to check here.
                try {
                    URL url = new URL(URL);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setReadTimeout(Configuration.connectionTimeOut);
                    connection.setConnectTimeout(Configuration.connectionTimeOut);
                    connection.setRequestProperty("Authorization", BasicAuthHeaderValue);
                    connection.setRequestProperty("x-smp-appid", "com.arteriatech.mSecSales");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    connection.setDefaultUseCaches(false);
                    connection.setAllowUserInteraction(false);
                    connection.connect();
                    responseCode = connection.getResponseCode();
                    connection.getResponseMessage();
                    InputStream stream = null;

                    if (responseCode == 200) {
                        stream = connection.getInputStream();
                        if (stream != null) {
                            resultJson = readResponse(stream);
                        }
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(resultJson);
                            JSONObject objectD = jsonObj.optJSONObject("d");
                            String value = "";
                            JSONArray jsonArray = objectD.getJSONArray("results");
                            if (jsonArray.length() > 0) {
                                asyncTaskCallBack.onStatus(true, "");
                            } else {
                                asyncTaskCallBack.onStatus(false, "Device-ID not mapped. Please retry after mapping..!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            asyncTaskCallBack.onStatus(false, e.getMessage());
                        }
                    } else {
                        asyncTaskCallBack.onStatus(false, String.valueOf(responseCode) + " " + connection.getResponseMessage());
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }




    static String readResponse(InputStream stream) throws IOException {
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder buffer = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append('\n');
        }

        return buffer.toString();
    }

    private  void init(final String device_id)
    {
        UtilConstants.dialogBoxWithCallBack(RegistrationCustActivity.this, "Share Your Device Id and Register,Ignore if Already Shared.", device_id.toUpperCase(), "Share Id", "Ignor", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (b) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Device ID");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Device ID - " + device_id.toUpperCase());
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
                else {

                }
            }
        });
    }

    private void initDialog( final String device_id)
    {

         final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
         alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setMessage("Share Your Device Id and Register,Ignore if Already Shared.");
                alertDialogBuilder.setPositiveButton("Share id",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Device ID");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Device ID - " + device_id);
                                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                            }
                        });

       alertDialogBuilder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {

               dialogInterface.cancel();

           }
       });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }





}
