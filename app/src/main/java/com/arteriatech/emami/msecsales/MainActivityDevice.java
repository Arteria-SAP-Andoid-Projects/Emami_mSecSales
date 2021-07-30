package com.arteriatech.emami.msecsales;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.DeviceAdminUtil;
import com.arteriatech.emami.main.MainMenu;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.interfaces.DialogCallBack;

public class MainActivityDevice extends AppCompatActivity {

    private boolean isResumed = false;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private int requestPermissionCode = 1;
    private String androidId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActionBarView.initActionBarView(this, false, "");
        setContentView(R.layout.activity_main_device);
        verifyStoragePermissions(this);
    }
    public void verifyStoragePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check if we have write permission
            int storage = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int location = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION);
            int camera = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA);
            int telephone = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
            if (storage != PackageManager.PERMISSION_GRANTED || location != PackageManager.PERMISSION_GRANTED || camera != PackageManager.PERMISSION_GRANTED || telephone != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        requestPermissionCode
                );
            } else {
                init();
            }
        }
    }




    public void init() {
        androidId = Settings.Secure.getString(MainActivityDevice.this.getContentResolver(), Settings.Secure.ANDROID_ID);
       // androidId="175605848f3bf89f";
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        boolean isFirstValidation = sharedPreferences.getBoolean(Constants.isFirstTimeValidation, false);
        if (!isFirstValidation) {
            UtilConstants.dialogBoxWithCallBack(MainActivityDevice.this, "Device ID", androidId, "Validate", "Share Id", false, new DialogCallBack() {
                @Override
                public void clickedStatus(boolean b) {
                    if (b) {
                        Intent intentLogView = new Intent(MainActivityDevice.this, ValidateIMEIActivity.class);
                        startActivity(intentLogView);
                        finish();
                    } else {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Device ID");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Device ID - " + androidId);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                }
            });

        } else {
            Intent intentLogView = new Intent(this, MainMenu.class);
            startActivity(intentLogView);
            finish();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        isResumed = true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isResumed) {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            boolean isFirstValidation = sharedPreferences.getBoolean(Constants.isFirstTimeValidation, false);
            if (!isFirstValidation) {
                UtilConstants.dialogBoxWithCallBack(MainActivityDevice.this, "Device ID", androidId, "Validate", "Share Id", false, new DialogCallBack() {
                    @Override
                    public void clickedStatus(boolean b) {
                        if (b) {
                            Intent intentLogView = new Intent(MainActivityDevice.this, ValidateIMEIActivity.class);
                            startActivity(intentLogView);
                            finish();
                        } else {
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Device ID");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Device ID - " + androidId);
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        }
                    }
                });

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    verifyStoragePermissions(this);
                }
                return;
            }
        }
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        switch (requestCode) {
            case DeviceAdminUtil.DEVICE_ADMIN_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i("CNAME", "Administration enabled!");
                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                    boolean isFirstValidation = sharedPreferences.getBoolean(Constants.isFirstTimeValidation, false);
                    if (!isFirstValidation) {
                        UtilConstants.dialogBoxWithCallBack(MainActivityDevice.this, "Device ID", androidId, "Validate", "Share Id", false, new DialogCallBack() {
                            @Override
                            public void clickedStatus(boolean b) {
                                if (b) {
                                    Intent intentLogView = new Intent(MainActivityDevice.this, ValidateIMEIActivity.class);
                                    startActivity(intentLogView);
                                    finish();
                                } else {
                                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Device ID");
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Device ID - " + androidId);
                                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                                }
                            }
                        });
                    } else {
                        Intent intentLogView = new Intent(this, MainMenu.class);
                        startActivity(intentLogView);
                        finish();
                    }
                } else {
                    Log.i("CNAME", "Administration enable FAILED!");
                }

                return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }*/
}
