package com.arteriatech.emami.master;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.RetailerListAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ${e10526} on ${15-11-2016}.
 */
public class RetailersListActivity extends AppCompatActivity implements TextWatcher {
    private RetailerListAdapter retailerAdapter = null;
    ListView lv_route_ret_list = null;
    Spinner spBeatsPlan;
    private ProgressDialog pdLoadDialog;
    ArrayList<CustomerBean> alRetailerList;
    EditText edNameSearch;
    private String[][] arrayRouteVal;
    private String mStrRouteID = "", mStrRouteSchGuid = "";
    HashMap<String, String> mapCPGrp3Desc = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.lbl_retailer_list));
        setContentView(R.layout.activity_retailer_list_for_ret_activity);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!Constants.restartApp(RetailersListActivity.this)) {
            onInitUI();
            setValuesToUI();
        }
    }

    private void loadAsyncTask() {
        try {
            new GetRetailerList().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*get Route Names*/
    private void getRouteNames() {
        try {
            arrayRouteVal = OfflineManager.getBeatPlanArray(Constants.RouteSchedules + "?$filter=" + Constants.StatusID + " eq '01'");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error + " : " + e.getMessage());
        }

        if (arrayRouteVal == null) {
            arrayRouteVal = new String[3][1];
            arrayRouteVal[0][0] = Constants.All;
            arrayRouteVal[1][0] = Constants.All;
            arrayRouteVal[2][0] = "";
        }
    }

    /*
     * TODO This method initialize UI
     */
    private void onInitUI() {
        lv_route_ret_list = (ListView) findViewById(R.id.lv_route_ret_list);
        spBeatsPlan = (Spinner) findViewById(R.id.spnr_beat_list);
        edNameSearch = (EditText) findViewById(R.id.et_name_search);
        edNameSearch.addTextChangedListener(this);
    }

    private void clearEditTextSearchBox() {
        if (edNameSearch != null && edNameSearch.getText().toString().length() > 0)
            edNameSearch.setText("");
    }

    /*
    TODO This method set values to UI
    */
    private void setValuesToUI() {
        getRouteNames();
        ArrayAdapter<String> spBeatAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrayRouteVal[1]);
        spBeatAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spBeatsPlan.setAdapter(spBeatAdapter);

        spBeatsPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                clearEditTextSearchBox();
                mStrRouteID = arrayRouteVal[0][position];
                mStrRouteSchGuid = arrayRouteVal[2][position];
                loadAsyncTask();

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    /*
        TODO Get Retailer List Based On Route
     */
    private void getRetailerList() {
        try {
            if (mStrRouteID.equalsIgnoreCase(Constants.All)) {
                alRetailerList = OfflineManager.getRetailerList(Constants.ChannelPartners + "?$select=" + Constants.CPNo + ","
                        + Constants.RetailerName + "," + Constants.Address1 + "," + Constants.Address2 + "," + Constants.Address3 + "," + Constants.TownDesc + ","
                        + Constants.DistrictDesc + "," + Constants.Landmark + "," + Constants.Latitude + "," + Constants.Longitude + "," + Constants.CityDesc + ","
                        + Constants.PostalCode + "," + Constants.MobileNo + "," + Constants.CPUID + "," + Constants.CPGUID + "," + Constants.DOB + ","
                        + Constants.Anniversary + "," + Constants.OwnerName + " " +
                        "&$filter=(" + Constants.CPNo + " ne '' and " + Constants.CPNo + " ne null)" +
                        " and " + Constants.StatusID + " eq '01' and " + Constants.ApprvlStatusID + " eq '03'" +
                        " &$orderby=" + Constants.RetailerName + "%20asc", "");
            } else {
                alRetailerList = OfflineManager.getRetListByRouteSchudule(Constants.RouteSchedulePlans + "?$filter="
                        + Constants.RouteSchGUID + " eq guid'" + mStrRouteSchGuid.toUpperCase() + "''");
            }

            mapCPGrp3Desc = Constants.getCPGrp3Desc(alRetailerList);

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
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

    /*AsyncTask to get Retailers List*/
    private class GetRetailerList extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(RetailersListActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            getRetailerList();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            displayRetailerList();
        }
    }

    private void displayRetailerList() {
        this.retailerAdapter = new RetailerListAdapter(this, alRetailerList, mapCPGrp3Desc);
        lv_route_ret_list.setEmptyView(findViewById(R.id.tv_empty_lay));
        lv_route_ret_list.setAdapter(this.retailerAdapter);
        this.retailerAdapter.notifyDataSetChanged();
    }
}


