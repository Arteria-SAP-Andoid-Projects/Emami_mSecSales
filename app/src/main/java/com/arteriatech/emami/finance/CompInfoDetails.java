package com.arteriatech.emami.finance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10526 on 13-07-2016.
 */
public class CompInfoDetails extends AppCompatActivity {
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrUpdatedOn="",mStrCompGuid="",mStrCompName="";

    TextView retId,retName;
    private boolean mBooleanRemoveScrollViews = true;
    private ArrayList<CompetitorInfoBean> alCompInfo =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_comp_info_details);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrUpdatedOn= bundleExtras.getString(Constants.UpdatedOn);
            mStrCompGuid= bundleExtras.getString(Constants.CompetitorGUID);
            mStrCompName= bundleExtras.getString(Constants.CompName);
        }
        if (!Constants.restartApp(CompInfoDetails.this)) {
            initUI();
        }

    }

    /*Initializes UI*/
    void initUI(){

        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retId = (TextView) findViewById(R.id.tv_reatiler_id);
        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);

        getCompInfo();
    }

    /*Gets info for competitor*/
    void getCompInfo(){
        try {
            String compInfoQry = Constants.CompetitorInfos + "?$filter=" + Constants.UpdatedOn
                    + " eq datetime'" + mStrUpdatedOn + "' and "+ Constants.CompInfoGUID+
                    " eq guid'"+mStrCompGuid+"' ";
            alCompInfo = OfflineManager.getCompInfoListDetails(compInfoQry);
            displyCompInfoDetails();
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }
    }

    /*Displays info for Competitors*/
    private  void displyCompInfoDetails() {

        try {
            ScrollView scroll_my_stock_list = (ScrollView) findViewById(R.id.scroll_com_list);
            if (!mBooleanRemoveScrollViews) {
                scroll_my_stock_list.removeAllViews();
            }

            mBooleanRemoveScrollViews = false;
            TableLayout table = (TableLayout) LayoutInflater.from(this).inflate(
                    R.layout.item_table, null);

            if(alCompInfo !=null && alCompInfo.size()>0) {

                for (int i = 0; i < alCompInfo.size(); i++) {

                    TableRow trInvoice = (TableRow) LayoutInflater.from(this).inflate(
                            R.layout.item_row, null);
                    ((TextView) trInvoice.findViewById(R.id.item_lable)).setText(getString(R.string.lbl_comp_name));
                    ((TextView) trInvoice.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
                    ((TextView) trInvoice.findViewById(R.id.item_value)).setText(mStrCompName);
                    table.addView(trInvoice);

                    TableRow trGA = (TableRow) LayoutInflater.from(this).inflate(
                            R.layout.item_row, null);
                    ((TextView) trGA.findViewById(R.id.item_lable)).setText(Constants.GA);
                    ((TextView) trGA.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
                    ((TextView) trGA.findViewById(R.id.item_value)).setText(alCompInfo.get(i).getMatGrp1Amount());
                    table.addView(trGA);

                    TableRow trHFRAC = (TableRow) LayoutInflater.from(this).inflate(
                            R.layout.item_row, null);
                    ((TextView) trHFRAC.findViewById(R.id.item_lable)).setText(Constants.H_FRC);
                    ((TextView) trHFRAC.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
                    ((TextView) trHFRAC.findViewById(R.id.item_value)).setText(alCompInfo.get(i).getMatGrp2Amount());
                    table.addView(trHFRAC);

                    TableRow trHSRAC = (TableRow) LayoutInflater.from(this).inflate(
                            R.layout.item_row, null);
                    ((TextView) trHSRAC.findViewById(R.id.item_lable)).setText(Constants.H_SRC);
                    ((TextView) trHSRAC.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
                    ((TextView) trHSRAC.findViewById(R.id.item_value)).setText(alCompInfo.get(i).getMatGrp3Amount());
                    table.addView(trHSRAC);

                    TableRow trEraningPerMonth = (TableRow) LayoutInflater.from(this).inflate(
                            R.layout.item_row, null);
                    ((TextView) trEraningPerMonth.findViewById(R.id.item_lable)).setText(Constants.Earning_per_Month);
                    ((TextView) trEraningPerMonth.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
                    ((TextView) trEraningPerMonth.findViewById(R.id.item_value)).setText(alCompInfo.get(i).getEarnings());
                    table.addView(trEraningPerMonth);

                    TableRow trSchemeAmt = (TableRow) LayoutInflater.from(this).inflate(
                            R.layout.item_row, null);
                    ((TextView) trSchemeAmt.findViewById(R.id.item_lable)).setText(Constants.Schemes);
                    ((TextView) trSchemeAmt.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
                    ((TextView) trSchemeAmt.findViewById(R.id.item_value)).setText(alCompInfo.get(i).getSchemeAmount());
                    table.addView(trSchemeAmt);

                    TableRow trTerritory = (TableRow) LayoutInflater.from(this).inflate(
                            R.layout.item_row, null);
                    ((TextView) trTerritory.findViewById(R.id.item_lable)).setText(Constants.Territory);
                    ((TextView) trTerritory.findViewById(R.id.item_blank)).setText(getString(R.string.lbl_semi_colon));
                    ((TextView) trTerritory.findViewById(R.id.item_value)).setText(alCompInfo.get(i).getMatGrp4Amount());
                    table.addView(trTerritory);
                }
            }

            scroll_my_stock_list.addView(table);
            scroll_my_stock_list.requestLayout();
        } catch (Exception e) {
            e.printStackTrace();
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
