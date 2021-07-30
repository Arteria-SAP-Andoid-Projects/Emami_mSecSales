package com.arteriatech.emami.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.login.AboutUsActivity;
import com.arteriatech.emami.msecsales.R;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout ll_AccessPin, ll_Aboutus;
    Switch enableSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarView.initActionBarView(this, true, "Settings");
        setContentView(R.layout.activity_settings);
        enableSwitch = (Switch) findViewById(R.id.pinSwitch);
        if (!Constants.restartApp(SettingsActivity.this)) {
            checkEnableAccess();
            ll_AccessPin = (LinearLayout) findViewById(R.id.ll_accesspin);
            ll_AccessPin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pinIntent = new Intent(SettingsActivity.this, AccesspinActivity.class);
                    startActivity(pinIntent);
                }
            });
            ll_Aboutus = (LinearLayout) findViewById(R.id.ll_about);
            ll_Aboutus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent aboutIntent = new Intent(SettingsActivity.this, AboutUsActivity.class);
                    startActivity(aboutIntent);
                }
            });
            enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.ENABLE_ACCESS, "yes");
                        editor.apply();
                        if (TextUtils.isEmpty(sharedPreferences.getString(Constants.QUICK_PIN, ""))) {
                            Intent pinIntent = new Intent(SettingsActivity.this, AccesspinActivity.class);
                            startActivity(pinIntent);
                        }
                   /* String x = sharedPreferences.getString(Constants.ENABLE_ACCESS, "");
                    Toast.makeText(SettingsActivity.this, x, Toast.LENGTH_LONG).show();*/
                    } else {
                        SharedPreferences sharedPreferencess = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                        SharedPreferences.Editor editors = sharedPreferencess.edit();
                        editors.putString(Constants.ENABLE_ACCESS, "no");
                        editors.apply();
                    }
                }
            });
        }
    }

    private void checkEnableAccess() {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
//        String x = shared.getString(Constants.ENABLE_ACCESS, "");
        if ("yes".equalsIgnoreCase(shared.getString(Constants.ENABLE_ACCESS, ""))) {
            enableSwitch.setChecked(true);
        } else {
            enableSwitch.setChecked(false);
        }

    }
}
