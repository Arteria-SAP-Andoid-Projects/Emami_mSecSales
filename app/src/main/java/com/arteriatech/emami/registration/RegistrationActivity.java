package com.arteriatech.emami.registration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;


import com.arteriatech.emami.msecsales.BuildConfig;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.registration.MainMenuBean;
import com.arteriatech.mutils.registration.RegistrationModel;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.arteriatech.emami.alerts.AlertsActivity;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.log.LogActivity;
import com.arteriatech.emami.login.ChangePasswordActivity;
import com.arteriatech.emami.login.LoginActivity;
import com.arteriatech.emami.main.MainMenu;
import com.arteriatech.emami.msecsales.R;

import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity {
    private boolean isNotification = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getNotfication();

        RegistrationModel registrationModel = new RegistrationModel();
        registrationModel.setAppID(Configuration.APP_ID);
        registrationModel.setHttps(Configuration.IS_HTTPS);
        registrationModel.setPassword(Configuration.pwd_text);
        registrationModel.setPort(Configuration.port_Text);
        registrationModel.setSecConfig(Configuration.secConfig_Text);
        registrationModel.setServerText(Configuration.server_Text);
        registrationModel.setShredPrefKey(Constants.PREFS_NAME);
        registrationModel.setFormID(Configuration.farm_ID);
        registrationModel.setSuffix(Configuration.suffix);
        SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_NAME, 0);
        registrationModel.setRegisterSuccessActivity(MainMenu.class);
        if (!sharedPref.getBoolean(Constants.isForgotPwdActivated, false)) {
            Constants.isBoolPwdgenerated = false;
            if (!isNotification) {
                registrationModel.setLogInActivity(LoginActivity.class);
            } else {
                registrationModel.setLogInActivity(AlertsActivity.class);
            }
        }else{
            registrationModel.setLogInActivity(ChangePasswordActivity.class);
        }
        registrationModel.setAppActionBarIcon(R.drawable.ic_emami_icon);
        registrationModel.setAppLogo(R.drawable.emami);
        registrationModel.setAppVersionName(BuildConfig.VERSION_NAME);
        registrationModel.setEmainId(getString(R.string.register_support_email));
        registrationModel.setPhoneNo(Constants.TOLLFREE_NO);
        registrationModel.setEmailSubject("");

        ArrayList<MainMenuBean> mainMenuBeanArrayList = new ArrayList<>();
        MainMenuBean mainMenuBean = new MainMenuBean();
        mainMenuBean.setActivityRedirect(LogActivity.class);
        mainMenuBean.setMenuImage(R.drawable.ic_log_list);
        mainMenuBean.setMenuName("View");
        mainMenuBeanArrayList.add(mainMenuBean);
        registrationModel.setMenuBeen(mainMenuBeanArrayList);
//        registrationModel.setMainMenuActivity(LoginActivity.class);

        SharedPreferences sharedpre = this.getSharedPreferences(Constants.PREFS_NAME,0);
        String userName = sharedpre.getString(UtilRegistrationActivity.KEY_username,(String)null);
        if(TextUtils.isEmpty(userName)){
            SharedPreferences.Editor editor = sharedpre.edit();
            try {
                editor.putInt(Constants.CURRENT_VERSION_CODE, Constants.NewDefingRequestVersion);
                editor.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       // Intent intent = new Intent(RegistrationActivity.this, UtilRegistrationActivity.class);
        Intent intent = new Intent(RegistrationActivity.this, RegistrationCustActivity.class);
        intent.putExtra(UtilConstants.RegIntentKey,registrationModel);

        if (sharedPref.getBoolean(Constants.isForgotPwdActivated, false)) {
            bundleExtra = new Bundle();
            bundleExtra.putString(Constants.OTP, sharedPref.getString(Constants.ForgotPwdOTP, ""));
            bundleExtra.putString(Constants.GUIDVal, sharedPref.getString(Constants.ForgotPwdGUID, ""));
        }

        intent.putExtra(UtilRegistrationActivity.EXTRA_BUNDLE_REGISTRATION,bundleExtra);
        startActivity(intent);
        finish();
    }
    private Bundle bundleExtra = null;
    private void getNotfication(){
        try {
            Intent intentNotfication = getIntent();
            if (intentNotfication != null) {
                isNotification = intentNotfication.getBooleanExtra(Constants.EXTRA_OPEN_NOTIFICATION, false);
            }
        } catch (Exception e) {
            isNotification = false;
        }
    }

}
