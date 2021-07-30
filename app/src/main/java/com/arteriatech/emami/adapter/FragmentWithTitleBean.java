package com.arteriatech.emami.adapter;

import android.support.v4.app.Fragment;

/**
 * Created by e10742 on 11-12-2016.
 */

public class FragmentWithTitleBean {
    Fragment fragment = null;
    String title = "";

    public FragmentWithTitleBean(Fragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
