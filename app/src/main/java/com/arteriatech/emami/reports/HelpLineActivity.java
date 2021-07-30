package com.arteriatech.emami.reports;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by ${e10604} on ${12/4/2016}.
 *
 */
public class HelpLineActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<HelpLineBean> alHelpLine=null;
    private boolean mBooleanRemoveScrollViews = true;
    private  String mStrHelpLine = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_helpline));

        setContentView(R.layout.activity_helpline_list);
        if (!Constants.restartApp(HelpLineActivity.this)) {
            initUI();
        }
    }

    /*Initialize UI*/
    void initUI(){
        Button bt_helpline_call = (Button) findViewById(R.id.bt_helpline_call);
        bt_helpline_call.setOnClickListener(this);

        getHelpLineNo();
        try {
            String mStrHelpLineQry= Constants.TEXT_CATEGORY_SET+"?$filter="+Constants.TextCategoryID+
                    " eq '"+Constants.HLPLNE+"'";
            alHelpLine= OfflineManager.getHelpLineList(mStrHelpLineQry);
            displayHelpLineValues();
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }
    }

    /*Gets helpline number*/
    private  void getHelpLineNo(){

        String qryStr = Constants.ConfigTypsetTypeValues + "?$filter="+ Constants.Typeset+" eq '"+
                Constants.MSEC+"' and "+ Constants.Types+" eq '"+Constants.HLPLNEPHN+"' ";
        try {
            mStrHelpLine = OfflineManager.getConfigValue(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /*Display helpline values */
    @SuppressLint("InflateParams")
    private void displayHelpLineValues() {

        ScrollView scroll_helpline_list = (ScrollView) findViewById(R.id.scroll_helpline_list);
        if (!mBooleanRemoveScrollViews) {
            scroll_helpline_list.removeAllViews();
        }

        mBooleanRemoveScrollViews = false;

        @SuppressLint("InflateParams") TableLayout tlHelpLine = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);

        if(alHelpLine!=null){
            if (!alHelpLine.isEmpty()
                    && alHelpLine.size() > 0 ) {

                LinearLayout llHelpLine;

                for (int i = 0; i < alHelpLine.size(); i++) {
                    llHelpLine = (LinearLayout) LayoutInflater.from(this)
                            .inflate(R.layout.helpline_category,
                                    null,false);

                    ((TextView) llHelpLine.findViewById(R.id.tv_helpline_category))
                            .setText(alHelpLine.get(i).getTextCategoryTypeDesc());

                    ((TextView) llHelpLine
                            .findViewById(R.id.tv_helpline_content))
                            .setText(alHelpLine.get(i).getText());

                    tlHelpLine.addView(llHelpLine);
                }

            }else{

                TextView tvNoDataFound = new TextView(this);
                tvNoDataFound.setText(R.string.alert_no_data_found);
                tvNoDataFound.setTextSize(R.dimen.medium_text);
                tvNoDataFound.setTextColor(ContextCompat.getColor(this, R.color.BLACK));
                tvNoDataFound.setGravity(Gravity.CENTER);
                LinearLayout llNoDataFound = new LinearLayout(this);
                llNoDataFound.addView(tvNoDataFound);
                tlHelpLine.addView(llNoDataFound);
            }
        }else{

            TextView tvNoDataFound = new TextView(this);
            tvNoDataFound.setText(R.string.alert_no_data_found);
            tvNoDataFound.setTextSize(R.dimen.medium_text);
            tvNoDataFound.setTextColor(ContextCompat.getColor(this, R.color.BLACK));
            tvNoDataFound.setGravity(Gravity.CENTER);
            LinearLayout llNoDataFound = new LinearLayout(this);
            llNoDataFound.addView(tvNoDataFound);
            tlHelpLine.addView(llNoDataFound);
        }

        scroll_helpline_list.addView(tlHelpLine);
        scroll_helpline_list.requestLayout();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_helpline_call:
                onCall();
                break;
        }
    }

    /*Open dial screen with helpline number*/
    private void onCall() {
        try {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + (mStrHelpLine)));
                startActivity(dialIntent);
        } catch (Exception e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
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
