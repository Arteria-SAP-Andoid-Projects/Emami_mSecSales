package com.arteriatech.emami.routeplan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.ViewPagerTabAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.master.MapActivity;
import com.arteriatech.emami.msecsales.R;

/**
 * Created by ${e10526} on ${17-11-2016}.
 *
 */
public class RoutePlanListActivity extends AppCompatActivity {

    //This is our viewPager
    private ViewPager viewPager;
    TextView tv_retailer_header;
    public TextView tv_last_sync_time_value;

    Menu menu = null;

    RoutePlanFragment beatPlanFragment;
    OtherRoutePlanFragment nonFiledWorkFragment;
    ViewPagerTabAdapter adapter;
    Bundle bundleBeat;
    Bundle bundleNonFieldWork;

    String dateInDeviceFormat;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan_list);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            if (!Constants.restartApp(RoutePlanListActivity.this)){
                onInitUI();
                setValuesToUI();
                //Initialize action bar with back button(true)
                ActionBarView.initActionBarView(this, true,getString(R.string.str_concat_two_texts,getString(R.string.lbl_beat_paln), dateInDeviceFormat));
                tabInitialize();
            }


    }


    /*
         * TODO This method initialize UI
         */
    private void onInitUI(){

        try {
            tv_last_sync_time_value = (TextView)findViewById(R.id.tv_last_sync_time_value);
            tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE,Constants.Collections,Constants.RoutePlans,Constants.TimeStamp,this));

            tv_retailer_header = (TextView) findViewById(R.id.tv_retailer_header);
            Constants.BoolTodayBeatLoaded = false;
            Constants.BoolOtherBeatLoaded = false;
            Constants.BoolMoreThanOneRoute = false;
            Constants.mSetTodayRouteSch.clear();
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(this.getClass().getSimpleName() + ".onInitUI: " + e.getMessage());
        }
    }
    /*
     TODO This method set values to UI
    */
    private void setValuesToUI(){
        try {
            String todaysDate = UtilConstants.getDate1();
            dateInDeviceFormat = UtilConstants.convertDateIntoDeviceFormat(this,todaysDate);
            tv_retailer_header.setText(getString(R.string.str_concat_two_texts,getString(R.string.lbl_beat_paln), dateInDeviceFormat));
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(this.getClass().getSimpleName() + ".setValuesToUI: " + e.getMessage());
        }
    }


    /*
                 TODO Initialize Tab
                 */
    private  void tabInitialize(){

        try {
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
                    switch (position){
                        case 0 : showOption(R.id.menu_map);
                            break;
                        case 1:  hideOption(R.id.menu_map);
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(this.getClass().getSimpleName() + ".tabInitialize: " + e.getMessage());
        }
    }



    /*
            TODO Set up fragments into adapter

            */
    private void setupViewPager(ViewPager viewPager) {
        try {
            adapter = new ViewPagerTabAdapter(getSupportFragmentManager());

            bundleBeat = new Bundle();
            bundleBeat.putString(Constants.RouteType, Constants.BeatPlan);

            bundleNonFieldWork = new Bundle();
            bundleNonFieldWork.putString(Constants.RouteType, Constants.NonFieldWork);


            beatPlanFragment = new RoutePlanFragment();
            beatPlanFragment.setArguments(bundleBeat);

            nonFiledWorkFragment = new OtherRoutePlanFragment();
            nonFiledWorkFragment.setArguments(bundleNonFieldWork);

            adapter.addFrag(beatPlanFragment, getString(R.string.lbl_today_beats));
            adapter.addFrag(nonFiledWorkFragment, getString(R.string.lbl_other_beats));
            viewPager.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(this.getClass().getSimpleName() + ".setupViewPager: " + e.getMessage());
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            this.menu = menu;
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_map_back, menu);
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(this.getClass().getSimpleName() + ".onCreateOptionsMenu: " + e.getMessage());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    break;
                case R.id.menu_map:
                    displyLocation();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(this.getClass().getSimpleName() + ".onOptionsItemSelected: " + e.getMessage());
        }
        return true;
    }
    /*
        TODO disable menu option
             */
    public void hideOption(int id)
    {
        try {
            MenuItem item = menu.findItem(id);
            item.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(this.getClass().getSimpleName() + ".hideOption: " + e.getMessage());
        }
    }
    /*
     TODO enable menu option
          */
    private void showOption(int id)
    {
        try {
            MenuItem item = menu.findItem(id);
            item.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(this.getClass().getSimpleName() + ".showOption: " + e.getMessage());
        }
    }

    /*
                TODO Navigate to Map Activity

                */
    private void displyLocation() {
        try {
//            if(OfflineManager.getMapAuthorization(Constants.MapAuth)){
//                Intent i = new Intent(this, MapRouteActivity.class);
//                i.putExtra(Constants.NAVFROM, Constants.BeatPlan);
////            i.putExtra(Constants.OtherRouteGUID, "");
//                i.putExtra(Constants.OtherRouteGUID, Constants.Route_Schudle_GUID);
//                startActivity(i);
//            }else {
                Intent i = new Intent(this, MapActivity.class);
                i.putExtra(Constants.NAVFROM, Constants.BeatPlan);
                i.putExtra(Constants.OtherRouteGUID, "");
//                i.putExtra(Constants.OtherRouteGUID, Constants.Route_Schudle_GUID);
                startActivity(i);
//            }
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(this.getClass().getSimpleName() + ".displyLocation: " + e.getMessage());
        }
    }



}