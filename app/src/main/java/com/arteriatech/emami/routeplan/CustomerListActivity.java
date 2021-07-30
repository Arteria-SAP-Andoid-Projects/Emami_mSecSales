package com.arteriatech.emami.routeplan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.CustomerListAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;
import java.util.List;


public class CustomerListActivity extends AppCompatActivity implements TextWatcher {
    private CustomerListAdapter retailerAdapter = null;
    ListView lv_route_ret_list = null;
    private String mStrRetNo = "",mStrVisitType="",mStrRouteKey="",mStrBeatType="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.lbl_retailer_list));
        setContentView(R.layout.activity_retailer_list);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mStrRetNo = extras.getString(Constants.CPNo);
            mStrVisitType = extras.getString(Constants.VisitType);
            mStrBeatType = extras.getString(Constants.BeatType);

        }
        onInitUI();
        getRetailerList();
    }

    /*
              * TODO This method initialize UI
              */
    private void onInitUI(){
        lv_route_ret_list = (ListView)findViewById(R.id.lv_route_ret_list);
        EditText edNameSearch = (EditText) findViewById(R.id.et_name_search);
        edNameSearch.addTextChangedListener(this);


    }

    /*
       TODO Get Retailer List Based On Route
    */
    private void getRetailerList(){
        try {
            String routeQry="";
            if(mStrVisitType.equalsIgnoreCase(Constants.RouteBased)){
                routeQry = Constants.ChannelPartners+"?$filter="+ Constants.RouteID+" eq '"+mStrRetNo+"' " +
                        "&$orderby="+ Constants.RetailerName+"%20asc";
            }else{
                routeQry = Constants.ChannelPartners+"?$filter="+ Constants.CPNo+" eq '"+mStrRetNo+"' " +
                        "&$orderby="+ Constants.RetailerName+"%20asc";

            }


            List<CustomerBean> listRetailers= OfflineManager.getRetailerList(routeQry,"");
            ArrayList<CustomerBean> alRetailerList;
            alRetailerList = (ArrayList<CustomerBean>) listRetailers;
            this.retailerAdapter = new CustomerListAdapter(this, alRetailerList);
            lv_route_ret_list.setEmptyView(findViewById(R.id.tv_empty_lay) );
            lv_route_ret_list.setAdapter(this.retailerAdapter);
            this.retailerAdapter.notifyDataSetChanged();
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back:
                onBackPressed();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence cs, int start, int before, int count) {
        retailerAdapter.getFilter().filter(cs); //Filter from my adapter
        retailerAdapter.notifyDataSetChanged(); //Update my view
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
    }


