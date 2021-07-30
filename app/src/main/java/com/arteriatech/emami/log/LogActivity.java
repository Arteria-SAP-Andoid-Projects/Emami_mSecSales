package com.arteriatech.emami.log;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.arteriatech.mutils.log.LogListFragment;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;

/**
 * Created by e10742 on 29-11-2016.
 */
public class LogActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar without back button(false)
        ActionBarView.initActionBarView(this, true, getString(R.string.log_menu));
        setContentView(R.layout.activity_log);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        String strPref = sharedPreferences.getString(Constants.UserName_Key, null);

        try {
            if (!TextUtils.isEmpty(strPref)) {
                if (!Constants.restartApp(LogActivity.this)) {
                    //Calling LogList fragment
                    getFragmentManager().beginTransaction().replace(R.id.fl_log_view,new LogListFragment()).commit();
                }
            } else {
                getFragmentManager().beginTransaction().replace(R.id.fl_log_view,new LogListFragment()).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
