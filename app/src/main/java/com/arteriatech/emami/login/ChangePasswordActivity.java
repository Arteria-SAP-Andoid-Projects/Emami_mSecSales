package com.arteriatech.emami.login;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.registration.RegistrationActivity;
import com.arteriatech.emami.store.OnlineManager;
import com.arteriatech.emami.store.OnlineODataStoreException;
import com.arteriatech.emami.store.OnlineStoreListener;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreContext;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.smp.client.odata.exception.ODataException;

/**
 * Created by e10526 on 22-07-2016.
 */
public class ChangePasswordActivity extends AppCompatActivity implements UIListener, View.OnClickListener {
    private String mStrPopUpText = "";
    private ProgressDialog pdLoadDialog;
    private String mStrResourcePath = "", mStrOldPwd = "", mStrNewPwd = "", mStrOtp = "", mStrGuid = "";
    EditText et_old_password, et_new_password;
    CheckBox ch_change_show_pass;
    boolean mBoolOtpStatus = false;
    private Bundle bundleExtra = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.title_change_password));

        Bundle bundleExtras = getIntent().getExtras();
        bundleExtra = bundleExtras.getBundle(UtilRegistrationActivity.EXTRA_BUNDLE_REGISTRATION);
        mStrOtp = bundleExtra.getString(Constants.OTP);
        mStrGuid = bundleExtra.getString(Constants.GUIDVal);
        et_old_password = (EditText) findViewById(R.id.et_old_password);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        ch_change_show_pass = (CheckBox) findViewById(R.id.ch_change_show_pass);
        ch_change_show_pass.setOnClickListener(this);
        if (!Constants.restartApp(ChangePasswordActivity.this)) {
            Button bt_forgot_submit = (Button) findViewById(R.id.bt_forgot_submit);
            Button bt_forgot_cancel = (Button) findViewById(R.id.bt_forgot_cancel);
            bt_forgot_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            bt_forgot_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitOTP();
                }
            });
        }


    }

    private void submitOTP() {
        mStrOldPwd = et_old_password.getText().toString() != null ? et_old_password.getText().toString() : "";
        mStrNewPwd = et_new_password.getText().toString() != null ? et_new_password.getText().toString() : "";

        if (!mStrOldPwd.equalsIgnoreCase("") && !mStrNewPwd.equalsIgnoreCase("")) {

            if (!UtilConstants.isNetworkAvailable(ChangePasswordActivity.this)) {
                UtilConstants.onNoNetwork(ChangePasswordActivity.this);
            } else {
                Constants.ERROR_MSG = "";
                mBoolOtpStatus = false;
                mStrResourcePath = Constants.Passwords + "(guid'" + mStrGuid.toUpperCase() + "')";
                onLoadDialog();
            }
        } else {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_valid_credetials), ChangePasswordActivity.this);
        }

    }


    private void onLoadDialog() {
        mStrPopUpText = "Changing Password, please wait";
        try {
            new onChangePwdAsyncTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ch_change_show_pass:
                if (ch_change_show_pass.isChecked()) {

                    et_old_password.setTransformationMethod(null);
                    et_new_password.setTransformationMethod(null);

                    et_old_password.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);

                    et_new_password.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);

                    et_new_password.setRawInputType(InputType.TYPE_CLASS_TEXT);
                    et_old_password.setRawInputType(InputType.TYPE_CLASS_TEXT);

                } else {
                    et_old_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_old_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_new_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et_new_password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                break;
        }

    }

    public class onChangePwdAsyncTask extends AsyncTask<Void, Boolean, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(ChangePasswordActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(true);
            pdLoadDialog.show();

            pdLoadDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    ChangePasswordActivity.this, R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    closeProgressDilog();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    try {
                                                        pdLoadDialog
                                                                .show();
                                                        pdLoadDialog
                                                                .setCancelable(true);
                                                        pdLoadDialog
                                                                .setCanceledOnTouchOutside(false);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }


                                                }
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean onlineStoreOpen = false;
            try {
                Thread.sleep(1000);
                try {

                    LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
                    if (!mStrOldPwd.equalsIgnoreCase("")) {
                        try {
                            lgCtx.setBackendPassword(mStrOldPwd);
                        } catch (LogonCoreException e) {
                            e.printStackTrace();
                        }
                    }
                    Constants.onlineStore = null;
                    OnlineStoreListener.instance = null;
                    Constants.IsOnlineStoreFailed = false;
                    Constants.ErrorCode = 0;
                    Constants.ErrorNo = 0;
                    Constants.ErrorName = "";

                    onlineStoreOpen = OnlineManager.openOnlineStoreForForgetPassword(ChangePasswordActivity.this, mStrOldPwd);

                    if (onlineStoreOpen) {

                        boolean mBoolOtpStatus = OnlineManager.changePassword(mStrResourcePath, mStrOldPwd, mStrNewPwd, mStrOtp, mStrGuid, ChangePasswordActivity.this);
                    }

                } catch (OnlineODataStoreException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return onlineStoreOpen;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {
                closingProgressDialog();
                pdLoadDialog = null;
//                UtilConstants.showAlert(getString(R.string.msg_online_store_failure), ChangePasswordActivity.this);
                if (Constants.ErrorNo == Constants.Network_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                    UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), ChangePasswordActivity.this);

                } else if (Constants.ErrorNo == Constants.UnAuthorized_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                    UtilConstants.showAlert(getString(R.string.auth_fail_plz_contact_admin, Constants.ErrorNo + ""), ChangePasswordActivity.this);
                } else if (Constants.ErrorNo == Constants.Comm_Error_Code) {
                    UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), ChangePasswordActivity.this);
                } else {
                    UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), ChangePasswordActivity.this);
                }
            }
        }
    }

    private void closingProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        if (operation == Operation.GetRequest.getValue()) {
            closeProgressDilog();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            closeProgressDilog();
        }
    }

    private void closeProgressDilog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {

        if (operation == Operation.GetRequest.getValue()) {
            closeProgressDilog();
            setPwdInDataVault();
            alertDialogPwdCreatedSuccessfully();
        } else {
            closeProgressDilog();
        }

    }

    @SuppressLint("NewApi")
    private void alertDialogPwdCreatedSuccessfully() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_pwd_chnaged_succefully).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        setValuesToSharedPref();
                        exitApp();
                    }
                });

        builder.show();
    }

   /* private void exitApp(){
        try {
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void exitApp() {
        try {
            Intent mStartActivity = new Intent(ChangePasswordActivity.this, RegistrationActivity.class);
            int mPendingIntentId = 1234567;
            PendingIntent mPendingIntent = PendingIntent.getActivity(ChangePasswordActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) ChangePasswordActivity.this.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 50, mPendingIntent);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setValuesToSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.isForgotPwdActivated, false);
        editor.putString(Constants.ForgotPwdOTP, "");
        editor.putString(Constants.ForgotPwdGUID, "");
        editor.putString(Constants.Password_Key, mStrNewPwd);
        editor.commit();
    }

    private void setPwdInDataVault() {
        try {
            // get Application Connection ID
            LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
            lgCtx.setBackendPassword(mStrNewPwd);
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
    }


}
