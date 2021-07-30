package com.arteriatech.emami.login;


import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.BuildConfig;
import com.arteriatech.emami.msecsales.R;

/**
 * This class checks weather when apk is released date and company information.
 */
public class AboutUsActivity extends AppCompatActivity {

    TextView aboutDate, aboutversion, aboutph;
    private String appVersionName = "";
    private String dateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.title_about_us));
        setContentView(R.layout.aboutus_activity);

        aboutDate = (TextView) findViewById(R.id.aboutdate);
        aboutversion = (TextView) findViewById(R.id.aboutversion);
        aboutph = (TextView) findViewById(R.id.aboutph);
        Button back = (Button) findViewById(R.id.back);

        try {
            appVersionName = BuildConfig.VERSION_NAME;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dateTime = BuildConfig.BUILD_TIME;
        } catch (Exception e) {
            e.printStackTrace();
        }
        aboutversion.setText(appVersionName);
        aboutDate.setText(dateTime);
        aboutph.setText(Constants.TOLLFREE_NO);
        aboutph.setPaintFlags(aboutph.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        aboutph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constants.TOLLFREE_NO.equalsIgnoreCase("")) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (Constants.TOLLFREE_NO)));
                    startActivity(dialIntent);
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


}
