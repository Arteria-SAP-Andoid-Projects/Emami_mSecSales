package com.arteriatech.emami.msecsales;



import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Window;
import android.widget.Toast;


import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.main.MainMenu;
import com.arteriatech.emami.registration.Configuration;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;



public class ValidateIMEIActivity extends AppCompatActivity {
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_PHONE_STATE
    };
    private int requestPermissionCode = 1;
    HttpsURLConnection connection = null;
    private ProgressDialog pdLoadDialog = null;
    private SharedPreferences sharedPerf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActionBarView.initActionBarView(this, false, "");

        setContentView(R.layout.activity_validate_imei);
        sharedPerf = getSharedPreferences(Constants.PREFS_NAME, 0);
       // verifyStoragePermissions(this);
        validateImeiFromServer();



    }

    public void verifyStoragePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if we have write permission
            int storage = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int location = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
            int camera = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            int telephone = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
            if (storage != PackageManager.PERMISSION_GRANTED || location != PackageManager.PERMISSION_GRANTED || camera != PackageManager.PERMISSION_GRANTED || telephone != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        requestPermissionCode
                );
            } else {
                validateImeiFromServer();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int reqcode = requestCode;
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    validateImeiFromServer();

                } else {
                    verifyStoragePermissions(this);
                }
                return;
            }
        }
    }


    private void validateImeiFromServer() {
        if (UtilConstants.isNetworkAvailable(this)) {
            showProgressDialog();

            (new Thread(new Runnable() {
                public void run() {
                    String androidId = "";
                    String host = "https://" + Configuration.server_Text + "/" + Configuration.APP_ID;
                    //commented for testing...
                    androidId = Settings.Secure.getString(ValidateIMEIActivity.this.getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    //androidId="175605848f3bf89f";
                    //androidId="68d15deee322a68e";
                    String url = host + "/ValidateSPIMEI/?$format=json&IMEI1='" + androidId.toUpperCase() + "'&IMEI2='" + androidId.toUpperCase() + "'";
                    //String url = host + "/ValidateSPIMEI/?$format=json&IMEI NO='" + androidId.toUpperCase() + "'&IMEI NO='" + androidId.toUpperCase() + "'";

                    try {
                        boolean isValidUser = false;
                       // isValidUser = validateDeviceIMEINo(new URL(url), Constants.USER_NAME, Constants.Passwords);
                        isValidUser = validateDeviceIMEINo(new URL(url), "P002353", "Welcome@2021");

                        hideProgressDialog();
                        if (!isValidUser) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showExitAlertMesssage(getString(R.string.androidid_error));
                                }
                            });

                        } else {
                            Intent intentLogView = new Intent(ValidateIMEIActivity.this, MainMenu.class);
                            startActivity(intentLogView);
                            finish();
                        }

                    } catch (IOException var17) {
                        var17.printStackTrace();
                    } catch (Exception var18) {
                        var18.printStackTrace();
                    }
                }
            })).start();
        } else {
            Toast.makeText(this, "Network not available ,You'r in Offline", Toast.LENGTH_LONG).show();
        }
    }

    private void showExitAlertMesssage(String message) {
        UtilConstants.dialogBoxWithCallBack(ValidateIMEIActivity.this, "", message, getString(R.string.ok), "", false, new DialogCallBack() {
            @Override
            public void clickedStatus(boolean b) {
                if (b) {
                    finishAffinity();
                }
            }
        });
    }

    private void hideProgressDialog() {
        if (pdLoadDialog != null && pdLoadDialog.isShowing()) pdLoadDialog.dismiss();
    }

    private void showProgressDialog() {
        pdLoadDialog = new ProgressDialog(this, R.style.UtilsDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.validation_androidID));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
    }

    private boolean validateDeviceIMEINo(URL url, String userName, String psw) {
        boolean isVaildUser = false;
        String resultJson = "";
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(Configuration.connectionTimeOut);
            connection.setConnectTimeout(Configuration.connectionTimeOut);
            String userCredentials = userName + ":" + psw;
            String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes("UTF-8"), 2);
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setRequestProperty("x-smp-appid", "com.arteriatech.mSecSales");
            connection.setRequestProperty("x-smp-enduser", userName);
            connection.setRequestProperty("X-CSRF-Token", "Fetch");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();

            connection.getResponseMessage();
            InputStream stream = null;

            if (responseCode != 200) {
                throw new IOException("HTTP error code: " + responseCode);
            } else if (responseCode == 200) {
                if (responseCode == 200) {
                    stream = connection.getInputStream();
                    if (stream != null) {
                        resultJson = readResponse(stream);
                    }
                } else {
                    stream = connection.getErrorStream();
                    if (stream != null) {
                        resultJson = readResponse(stream);
                    }
                }
                if (!TextUtils.isEmpty(resultJson)) {
                    JSONObject jsonObject = new JSONObject(resultJson);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("d"));
                    JSONArray jsonArray = jsonObject1.optJSONArray("results");
                    if (jsonArray != null && jsonArray.length() > 0) {
                        String spGuid = jsonArray.getJSONObject(0).getString("SPGuid");
                        String mobileNo = jsonArray.getJSONObject(0).getString("MobileNo");
                        if (!TextUtils.isEmpty(spGuid)) {
                            try {
                                SharedPreferences.Editor editor = sharedPerf.edit();
                                editor.putString("SPGUID", spGuid);
                                editor.putString("MobileNo", mobileNo);
                                editor.putBoolean(Constants.isFirstTimeValidation, true);
                                editor.apply();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            isVaildUser = true;
                        } else isVaildUser = false;
                    }
                }


            }

        } catch (Exception var12) {
            var12.printStackTrace();
        } finally {
			/*if (connection != null) {
				connection.disconnect();
			}*/

        }


        return isVaildUser;
    }

    private static String readResponse(InputStream stream) throws IOException {
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder buffer = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append('\n');
        }

        return buffer.toString();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int code = requestCode;
    }

    private List<String> getNetworkOperator(final Context context) {
        // Get System TELEPHONY service reference
        List<String> carrierNames = new ArrayList<>();
        try {
            final String permission = Manifest.permission.READ_PHONE_STATE;
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) && (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)) {
                final List<SubscriptionInfo> subscriptionInfos;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                    subscriptionInfos = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
                    for (int i = 0; i < subscriptionInfos.size(); i++) {
                        carrierNames.add(subscriptionInfos.get(i).getCarrierName().toString());
                    }
                }
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                // Get carrier name (Network Operator Name)
                carrierNames.add(telephonyManager.getNetworkOperatorName());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return carrierNames;
    }
}
