package com.arteriatech.emami.alerts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.arteriatech.emami.adapter.ViewPagerTabAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.main.MainMenu;
import com.arteriatech.emami.msecsales.R;

public class AlertsActivity extends AppCompatActivity {


    //This is our viewPager
    private ViewPager viewPager;
    TextView tv_retailer_header;
    public TextView tv_last_sync_time_value;

    Menu menu = null;

    AlertsListFragment alertListFragment;
    AlertsHistoryFrgment alertHistoryFragment;
    ViewPagerTabAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarView.initActionBarView(this, true, getString(R.string.lbl_alerts));
        setContentView(R.layout.activity_alerts_main);
//        onInitUI();
        if (!Constants.restartApp(AlertsActivity.this)) {
            setAlertsCountZero();
            tabInitialize();
        }

    }

    /*
               TODO Initialize Tab
               */
    private void tabInitialize() {
        Constants.BoolAlertsLoaded = false;
        Constants.BoolAlertsHistoryLoaded = false;
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(Color.BLUE);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment currentFragment = getSupportFragmentManager()
                        .findFragmentByTag(
                                "android:switcher:" + R.id.viewpager + ":"
                                        + viewPager.getCurrentItem());
                if (currentFragment instanceof AlertsHistoryFrgment) {
                    ((AlertsHistoryFrgment) currentFragment).updateFragment();
                }
               /* switch (position){
                    case 0 : showOption(R.id.menu_map);
                        break;
                    case 1:  hideOption(R.id.menu_map);
                        break;
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /*
            TODO Set up fragments into adapter

            */
    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerTabAdapter(getSupportFragmentManager());


        alertListFragment = new AlertsListFragment();

        alertHistoryFragment = new AlertsHistoryFrgment();

        adapter.addFrag(alertListFragment, getString(R.string.lbl_alerts));
        adapter.addFrag(alertHistoryFragment, getString(R.string.lbl_alerts_history));
        viewPager.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intentMainmenu = new Intent(AlertsActivity.this,
                MainMenu.class);
        intentMainmenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentMainmenu);
    }

    private void setAlertsCountZero() {
        SharedPreferences sharedPref = getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Constants.BirthdayAlertsCount, 0);
        editor.putInt(Constants.TextAlertsCount, 0);
        editor.putInt(Constants.AppointmentAlertsCount, 0);
        editor.commit();
    }

}
