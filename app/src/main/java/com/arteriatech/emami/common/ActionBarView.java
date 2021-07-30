package com.arteriatech.emami.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.emami.msecsales.R;

/**
 * Created by e10742 on 28-10-2016.
 */
public class ActionBarView {

    public static void initActionBarView(final AppCompatActivity mActivity, boolean homeUpEnabled, String title) {

        if (homeUpEnabled)
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        else
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_action_bar, null);
        TextView textView = (TextView) view.findViewById(R.id.txtTitle);
        LinearLayout ll_area_back_sel = (LinearLayout) view.findViewById(R.id.ll_area_back_sel);
        ImageView backImg = (ImageView) view.findViewById(R.id.img_back);
        textView.setText(title);
        if (mActivity.getString(R.string.lbl_main_menu).equalsIgnoreCase(title)
               /* || mActivity.getString(R.string.title_forgot_password).equalsIgnoreCase(title)
                || mActivity.getString(R.string.lbl_retailer_list).equalsIgnoreCase(title)
                || mActivity.getString(R.string.lbl_alerts).equalsIgnoreCase(title)
                || mActivity.getString(R.string.title_my_targets).equalsIgnoreCase(title)
                || mActivity.getString(R.string.title_dbstoxk_and_price).equalsIgnoreCase(title)
                || mActivity.getString(R.string.lbl_day_summary).equalsIgnoreCase(title)
                || mActivity.getString(R.string.sync_menu).equalsIgnoreCase(title)
                || mActivity.getString(R.string.log_menu).equalsIgnoreCase(title)*/) {
            backImg.setVisibility(View.GONE);
        } else {
            backImg.setVisibility(View.VISIBLE);
        }
        if (homeUpEnabled) {
            backImg.setVisibility(View.VISIBLE);
            ll_area_back_sel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.onBackPressed();
                }
            });
        } else {
            backImg.setVisibility(View.GONE);
        }

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });
        //  displayBackButton(backImg,title);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.START);
        mActivity.getSupportActionBar().setDisplayShowCustomEnabled(true);
        mActivity.getSupportActionBar().setCustomView(view, params);
    }

    public static TextView initActionBarReturnView(final AppCompatActivity mActivity, boolean homeUpEnabled, String title) {

        if (homeUpEnabled)
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        else
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_action_bar, null);
        TextView textView = (TextView) view.findViewById(R.id.txtTitle);
        LinearLayout ll_area_back_sel = (LinearLayout) view.findViewById(R.id.ll_area_back_sel);
        ImageView backImg = (ImageView) view.findViewById(R.id.img_back);
        textView.setText(title);
        if (mActivity.getString(R.string.lbl_main_menu).equalsIgnoreCase(title)
               /* || mActivity.getString(R.string.title_forgot_password).equalsIgnoreCase(title)
                || mActivity.getString(R.string.lbl_retailer_list).equalsIgnoreCase(title)
                || mActivity.getString(R.string.lbl_alerts).equalsIgnoreCase(title)
                || mActivity.getString(R.string.title_my_targets).equalsIgnoreCase(title)
                || mActivity.getString(R.string.title_dbstoxk_and_price).equalsIgnoreCase(title)
                || mActivity.getString(R.string.lbl_day_summary).equalsIgnoreCase(title)
                || mActivity.getString(R.string.sync_menu).equalsIgnoreCase(title)
                || mActivity.getString(R.string.log_menu).equalsIgnoreCase(title)*/) {
            backImg.setVisibility(View.GONE);
        } else {
            backImg.setVisibility(View.VISIBLE);
        }
        if (homeUpEnabled) {
            backImg.setVisibility(View.VISIBLE);
            ll_area_back_sel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.onBackPressed();
                }
            });
        } else {
            backImg.setVisibility(View.GONE);
        }
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.onBackPressed();
            }
        });
        //  displayBackButton(backImg,title);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.START);
        mActivity.getSupportActionBar().setDisplayShowCustomEnabled(true);
        mActivity.getSupportActionBar().setCustomView(view, params);
        return textView;
    }
}
