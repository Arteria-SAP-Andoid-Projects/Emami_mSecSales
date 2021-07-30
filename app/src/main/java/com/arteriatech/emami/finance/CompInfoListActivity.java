package com.arteriatech.emami.finance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10526 on 13-07-2016.
 */
public class CompInfoListActivity extends AppCompatActivity {

    private CompetitorListAdapter compInfoListAdapter = null;
    private ArrayList<CompetitorInfoBean> alCompInfoBean;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "",mStrBundleCPGUID="";
    TextView retId,retName;
    private CompetitorInfoBean selectedList;

    ListView lvRetailerList = null;
    TextView tvEmptyLay = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_comp_info_list));

        setContentView(R.layout.activity_comp_info_list);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID= bundleExtras.getString(Constants.CPGUID);
        }
        if (!Constants.restartApp(CompInfoListActivity.this)) {
            initUI();
        }
    }

    /*Initializes UI*/
    void initUI(){

        lvRetailerList = (ListView) findViewById(R.id.lv_compInfo);
        tvEmptyLay = (TextView) findViewById(R.id.tv_empty_lay);

        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retId = (TextView) findViewById(R.id.tv_reatiler_id);

        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);

        getCompInfoList();
    }

    /*Gets list of competitor info list for retailer*/
    private void getCompInfoList(){
        try {
            alCompInfoBean = OfflineManager.getCompInfoList(Constants.CompetitorInfos + "?$filter= sap.islocal() and "+ Constants.CPGUID+" eq '"+mStrBundleCPGUID+"' ");

            CompInfoListActivity.this.compInfoListAdapter = new CompetitorListAdapter( CompInfoListActivity.this, R.layout.activity_invoice_history_list, alCompInfoBean);
            lvRetailerList.setAdapter(compInfoListAdapter);
            CompInfoListActivity.this.compInfoListAdapter.notifyDataSetChanged();

            if (alCompInfoBean != null && alCompInfoBean.size() > 0)
                tvEmptyLay.setVisibility(View.GONE);
            else
                tvEmptyLay.setVisibility(View.VISIBLE);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    class CompetitorListAdapter extends ArrayAdapter<CompetitorInfoBean> {
        private ArrayList<CompetitorInfoBean> compInfoDisplayValues;

        public CompetitorListAdapter(Context context, int textViewResourceId, ArrayList<CompetitorInfoBean> items) {
            super(context, R.layout.comp_list_item, items);
            this.compInfoDisplayValues = items;
            alCompInfoBean = items;
        }

        @Override
        public int getCount() {
            return compInfoDisplayValues != null ? compInfoDisplayValues.size() : 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.comp_list_item, null);
            }
            CompetitorInfoBean lb = compInfoDisplayValues.get(position);

            if (lb != null) {

                try {
                    TextView tv_comp_date = (TextView) v.findViewById(R.id.tv_comp_date);
                    TextView tv_comp_name = (TextView) v.findViewById(R.id.tv_comp_name);

                    tv_comp_date.setText(UtilConstants.getConvetDDMMYYYYY(lb.getUpdatedOn()));
                    tv_comp_name.setText(lb.getCompetitorName());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                v.setId(position);
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedList = new CompetitorInfoBean();
                    selectedList = alCompInfoBean.get(v.getId());

                    Intent toCompInfodetails = new Intent(CompInfoListActivity.this, CompInfoDetails.class);
                    toCompInfodetails.putExtra(Constants.CPNo, mStrBundleRetID);
                    toCompInfodetails.putExtra(Constants.RetailerName, mStrBundleRetName);
                    toCompInfodetails.putExtra(Constants.CPGUID, mStrBundleCPGUID);
                    toCompInfodetails.putExtra(Constants.UpdatedOn, selectedList.getUpdatedOn());
                    toCompInfodetails.putExtra(Constants.CompetitorGUID, selectedList.getCompetitorGUID());
                    toCompInfodetails.putExtra(Constants.CompName, selectedList.getCompetitorName());
                    startActivity(toCompInfodetails);
                }
            });

            return v;
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
