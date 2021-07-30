package com.arteriatech.emami.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by e10742 on 11-12-2016.
 */

public class RetailerDetailPagetTabAdapter extends FragmentStatePagerAdapter {
    ArrayList<FragmentWithTitleBean> fragmentList = new ArrayList<>();
    public RetailerDetailPagetTabAdapter(FragmentManager fm, ArrayList<FragmentWithTitleBean> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentList.get(position).getTitle();
    }
}
