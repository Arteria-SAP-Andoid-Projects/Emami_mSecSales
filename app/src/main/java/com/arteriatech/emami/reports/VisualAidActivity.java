package com.arteriatech.emami.reports;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.arteriatech.emami.adapter.VisualPagerTabAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.visualaid.BrouchersFragment;
import com.arteriatech.emami.visualaid.VideosFragment;

import static com.arteriatech.emami.common.Constants.deleteFolder;

/**
 * Created by e10526 on 2/16/2017.
 *
 */

public class VisualAidActivity extends AppCompatActivity {

    private ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar without back button(false)
        ActionBarView.initActionBarView(this, true, getString(R.string.lbl_visual_aid));
        setContentView(R.layout.activity_visual_aid);
        // to get document list

        if (!Constants.restartApp(VisualAidActivity.this)) {
            tabIntilize();
            Constants.deleteFolder();
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        deleteFolder();
    }

    /*
        TODO Initialize Tab
        */
    private void tabIntilize() {

        try {
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            setupViewPager();
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            tabLayout.setupWithViewPager(viewPager);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }


    }

     /*
           TODO Set up fragments into adapter

             */

    private void setupViewPager() {

        VisualPagerTabAdapter adapter = new VisualPagerTabAdapter(getSupportFragmentManager());
        BrouchersFragment brouchersFragment = new BrouchersFragment();
        adapter.addFrag(brouchersFragment, "Brochures");


        VideosFragment videosFragment = new VideosFragment();
        adapter.addFrag(videosFragment, "Videos");

        viewPager.setAdapter(adapter);


    }



}
