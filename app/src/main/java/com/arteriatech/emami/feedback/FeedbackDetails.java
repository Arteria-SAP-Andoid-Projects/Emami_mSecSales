package com.arteriatech.emami.feedback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;

/**
 * Created by e10526 on 11-07-2016.
 */
public class FeedbackDetails extends AppCompatActivity {
    private String mStrBundleRetName = "", mStrBundleRetID = "";
    private String mStrBundleFeedbackNo = "", mStrBtsID = "";
    private String mStrBundleFeedbackGuid = "", mStrStatus = "", mStrFeedBackDesc = "",
            mStrRemarks = "", mStrLocation = "",
            mStrBundleDeviceStatus = "", mStrDeviceNo = "";
    TextView tv_invoice_document_number;
    TextView retName, retUid;
    private LinearLayout llDetailLayout;
    boolean flag = true;
    private String feedbackGuid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_feed_back_details));

        setContentView(R.layout.activity_feed_back_details);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleFeedbackNo = bundleExtras.getString(Constants.FeedbackNo);
            mStrBundleFeedbackGuid = bundleExtras.getString(Constants.FeedBackGuid);
            mStrFeedBackDesc = bundleExtras.getString(Constants.FeedbackDesc);
            mStrBtsID = bundleExtras.getString(Constants.BTSID);
            mStrLocation = bundleExtras.getString(Constants.Location);
            mStrRemarks = bundleExtras.getString(Constants.Remarks);
            mStrBundleDeviceStatus = bundleExtras.getString(Constants.DeviceStatus);
            mStrDeviceNo = bundleExtras.getString(Constants.DeviceNo);

        }
        if (!Constants.restartApp(FeedbackDetails.this)) {
            initUI();
        }
    }

    /*Initializes UI*/
    void initUI() {
        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retUid = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(mStrBundleRetName);
        retUid.setText(mStrBundleRetID);

        tv_invoice_document_number = (TextView) findViewById(R.id.tv_invoice_document_number);

        ImageView invStatus = (ImageView) findViewById(R.id.tv_in_history_status);
        tv_invoice_document_number.setText(mStrBundleFeedbackNo);


        invStatus.setImageDrawable(null);

        String store = null;
//        try {
//            store = LogonCore.getInstance().getObjectFromStore(mStrDeviceNo);
//        } catch (LogonCoreException e) {
//            e.printStackTrace();
//        }
//        try {
//            JSONObject fetchJsonHeaderObject = new JSONObject(store);
//            ArrayList<HashMap<String, String>> arrtable = new ArrayList<>();
//
//            feedbackGuid = fetchJsonHeaderObject.getString(Constants.FeebackGUID);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        displayValues();
    }

    /*Display feedback details*/
    private void displayValues() {
        if (!flag) {
            llDetailLayout.removeAllViews();
        }
        flag = false;
        llDetailLayout = (LinearLayout) findViewById(R.id.ll_invoice_detail_list);


        TableLayout table = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null);

        TableRow trFeedbackDesc = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_row, null);
        ((TextView) trFeedbackDesc.findViewById(R.id.item_lable)).setText(getString(R.string.lbl_feed_back_desc));
        ((TextView) trFeedbackDesc.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
        ((TextView) trFeedbackDesc.findViewById(R.id.item_value)).setText(mStrFeedBackDesc);
        table.addView(trFeedbackDesc);

//        TableRow trbtsId = (TableRow) LayoutInflater.from(this).inflate(
//                R.layout.item_row, null);
//        ((TextView) trbtsId.findViewById(R.id.item_lable))
//                .setText(getString(R.string.lbl_bts_id));
//        ((TextView) trbtsId.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
//        ((TextView) trbtsId.findViewById(R.id.item_value)).setText(mStrBtsID);
//        table.addView(trbtsId);

//        TableRow trLoc = (TableRow) LayoutInflater.from(this).inflate(
//                R.layout.item_row, null);
//        ((TextView) trLoc.findViewById(R.id.item_lable))
//                .setText(getString(R.string.lbl_location));
//        ((TextView) trLoc.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
//        ((TextView) trLoc.findViewById(R.id.item_value)).setText(mStrLocation);
//        table.addView(trLoc);

        TableRow trRemarks = (TableRow) LayoutInflater.from(this).inflate(
                R.layout.item_row, null);
        ((TextView) trRemarks.findViewById(R.id.item_lable))
                .setText(getString(R.string.lbl_remarks));
        ((TextView) trRemarks.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
        ((TextView) trRemarks.findViewById(R.id.item_value)).setText(mStrRemarks);
        table.addView(trRemarks);


        llDetailLayout.addView(table);
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
