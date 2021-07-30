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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.log.LogActivity;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.registration.RegistrationActivity;
import com.arteriatech.emami.store.OnlineManager;
import com.arteriatech.emami.store.OnlineODataStoreException;
import com.arteriatech.emami.store.OnlineStoreListener;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.xscript.core.GUID;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by e10526 on 22-07-2016.
 */
public class ForgotPasswordActivity extends AppCompatActivity implements UIListener {
    private String mStrPopUpText = "";
    private ProgressDialog pdLoadDialog;
    private String mStrResourcePath = "", mStrOTP = "", mStrGuidValue = "";
    EditText et_otp;
    boolean mBoolOtpStatus = false;
    boolean mBoolOtpStatusSuccess = false;
    boolean flagforexportDB = true;
    boolean flagforexportDataVaultData = true;
    boolean flagforimportDataVaultData = true;
    boolean flagforimportDB = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.title_forgot_password));
        if (!Constants.restartApp(ForgotPasswordActivity.this)) {
            TextView tv_generate_otp = (TextView) findViewById(R.id.tv_generate_otp);
            et_otp = (EditText) findViewById(R.id.et_otp);
            tv_generate_otp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getOTP();
                }
            });

            Button bt_forgot_submit = (Button) findViewById(R.id.bt_forgot_submit);
            bt_forgot_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitOTP();
                }
            });

            Button bt_forgot_cancel = (Button) findViewById(R.id.bt_forgot_cancel);
            bt_forgot_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

    }


    private void submitOTP() {
        if (et_otp.getText().toString() != null && !et_otp.getText().toString().equalsIgnoreCase("")) {
            if (!UtilConstants.isNetworkAvailable(ForgotPasswordActivity.this)) {
                UtilConstants.onNoNetwork(ForgotPasswordActivity.this);
            } else {
                mBoolOtpStatusSuccess = false;
                mBoolOtpStatus = false;
                Constants.ERROR_MSG = "";
                mStrOTP = et_otp.getText().toString();
                GUID guid = GUID.newRandom();
                mStrGuidValue = guid.toString();

                mStrResourcePath = Constants.Passwords + "(guid'" + mStrGuidValue + "')";
                onLoadDialog();
            }
        } else {
            UtilConstants.showAlert(getString(R.string.val_plz_enter_otp), ForgotPasswordActivity.this);
        }

    }

    private void getOTP() {
        if (!UtilConstants.isNetworkAvailable(ForgotPasswordActivity.this)) {
            UtilConstants.onNoNetwork(ForgotPasswordActivity.this);
        } else {
            mBoolOtpStatus = false;
            Constants.ERROR_MSG = "";
            mStrOTP = "";
            mStrGuidValue = "";
            mStrResourcePath = Constants.Passwords;
            onLoadDialog();
        }
    }

    private void onLoadDialog() {
        if (mStrOTP.equalsIgnoreCase("")) {
            mStrPopUpText = getString(R.string.msg_otp_generating);
        } else {
            mStrPopUpText = getString(R.string.msg_otp_submitting);
        }

        try {
            new onRetriveOTPAsyncTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class onRetriveOTPAsyncTask extends AsyncTask<Void, Boolean, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(ForgotPasswordActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean onlineStoreOpen = false;
            try {
                Thread.sleep(1000);
                try {
                    Constants.onlineStore = null;
                    OnlineStoreListener.instance = null;
                    Constants.IsOnlineStoreFailed = false;
                    Constants.ErrorCode = 0;
                    Constants.ErrorNo = 0;
                    Constants.ErrorName = "";

                    onlineStoreOpen = OnlineManager.openOnlineStoreForForgetPassword(ForgotPasswordActivity.this, "");

                    if (onlineStoreOpen) {
                        if (mStrOTP.equalsIgnoreCase("")) {
                            mBoolOtpStatus = OnlineManager.getOTP(mStrResourcePath, ForgotPasswordActivity.this);
                        } else {


                            mBoolOtpStatus = OnlineManager.sendResetPassword(mStrResourcePath, mStrGuidValue, mStrOTP, ForgotPasswordActivity.this);
                        }
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
//                UtilConstants.showAlert(getString(R.string.msg_online_store_failure), ForgotPasswordActivity.this);
                if (Constants.ErrorNo == Constants.Network_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                    UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), ForgotPasswordActivity.this);

                } else if (Constants.ErrorNo == Constants.UnAuthorized_Error_Code && Constants.ErrorName.equalsIgnoreCase(Constants.NetworkError_Name)) {
                    UtilConstants.showAlert(getString(R.string.auth_fail_plz_contact_admin, Constants.ErrorNo + ""), ForgotPasswordActivity.this);
                } else if (Constants.ErrorNo == Constants.Comm_Error_Code) {
                    UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), ForgotPasswordActivity.this);
                } else {
                    UtilConstants.showAlert(getString(R.string.data_conn_lost_during_sync_error_code, Constants.ErrorNo + ""), ForgotPasswordActivity.this);
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

            try {
                pdLoadDialog.dismiss();
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            if (mStrOTP.equalsIgnoreCase("")) {
                UtilConstants.showAlert(e.getMessage(), ForgotPasswordActivity.this);
            } else {
                UtilConstants.showAlert(e.getMessage(), ForgotPasswordActivity.this);
            }

        }


    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {

        if (operation == Operation.GetRequest.getValue()) {
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mStrOTP.equalsIgnoreCase("")) {
                if (!mBoolOtpStatusSuccess) {
                    mBoolOtpStatusSuccess = true;
                    UtilConstants.showAlert(getString(R.string.alert_otp_generated_succefully), ForgotPasswordActivity.this);
                }

            } else {
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.isForgotPwdActivated, true);
                editor.putString(Constants.ForgotPwdOTP, mStrOTP);
                editor.putString(Constants.ForgotPwdGUID, mStrGuidValue);
                editor.putBoolean(Constants.isUserIsLocked, false);
                editor.commit();
                Constants.isBoolPwdgenerated = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this, R.style.MyTheme);
                builder.setMessage(R.string.alert_pwd_created_succefully).setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                finishAffinity();
                                exitApp();
                            }
                        });

                builder.show();
            }

        }

    }

    /*private void exitApp(){
        try {
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void exitApp() {
        try {
            Intent mStartActivity = new Intent(ForgotPasswordActivity.this, RegistrationActivity.class);
            int mPendingIntentId = 123456;
            PendingIntent mPendingIntent = PendingIntent.getActivity(ForgotPasswordActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) ForgotPasswordActivity.this.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 50, mPendingIntent);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intentNavLoginScreen = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        intentNavLoginScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentNavLoginScreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_mainmenu, menu);
        menu.removeItem(R.id.menu_mainmenu_export);
        menu.removeItem(R.id.menu_mainmenu_import);
        menu.removeItem(R.id.menu_mainmenu_settings);
        menu.removeItem(R.id.menu_mainmenu_aboutus);
        menu.removeItem(R.id.menu_re_register);
        menu.removeItem(R.id.menu_importdatavault);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_mainmenu_log:
                onLog();
                break;
            case R.id.menu_exportcrashlog:
                onCrashlog();
                break;
            case R.id.menu_exportdatavault:
                if (Constants.isReadWritePermissionEnabled(ForgotPasswordActivity.this, ForgotPasswordActivity.this)) {
                    exportDatavaultData();
                }
                break;
            case R.id.menu_importdatavault:
                if (Constants.isReadWritePermissionEnabled(ForgotPasswordActivity.this, ForgotPasswordActivity.this)) {
                    importDatavaultData();
                }
                break;
        }
        return true;
    }

    private void onLog() {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
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
                ForgotPasswordActivity.this, R.style.MyTheme);
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
        ForgotPasswordActivity.this.runOnUiThread(new Runnable() {
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
        ForgotPasswordActivity.this.runOnUiThread(new Runnable() {
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
                                    Constants.removePendingList(ForgotPasswordActivity.this);
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
            Constants.setJsonStringDataToDataVault(jsonStr, ForgotPasswordActivity.this);
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
                jsonData = Constants.makePendingDataToJsonString(ForgotPasswordActivity.this);
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
}
