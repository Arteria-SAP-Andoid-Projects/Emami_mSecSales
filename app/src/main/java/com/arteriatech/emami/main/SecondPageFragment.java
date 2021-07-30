package com.arteriatech.emami.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arteriatech.emami.msecsales.R;

/**
 * Created by e10742 on 05-12-2016.
 */
public class SecondPageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.fragment_ret_summary, container, false);

        return myInflatedView;
    }
}
