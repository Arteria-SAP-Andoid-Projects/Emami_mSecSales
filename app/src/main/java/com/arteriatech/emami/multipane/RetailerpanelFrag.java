package com.arteriatech.emami.multipane;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.RetailerListAdapter;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;
import java.util.HashMap;


public class RetailerpanelFrag extends Fragment implements TextWatcher{
    View myInflatedView = null;
    private RetailerListAdapter retailerAdapter = null;
    ListView lv_route_ret_list = null;
    Spinner spBeatsPlan;
    private ProgressDialog pdLoadDialog;
    ArrayList<CustomerBean> alRetailerList;
    EditText edNameSearch;
    private String[][] arrayRouteVal;
    private String mStrRouteID = "", mStrRouteSchGuid = "";
    public static Context context;
    HashMap<String,String> mapCPGrp3Desc = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        myInflatedView = inflater.inflate(R.layout.fragment__retail_list, container, false);

        onInitUI();
        setValuesToUI();

        return myInflatedView;
    }



    private void onInitUI() {

        lv_route_ret_list = (ListView) myInflatedView.findViewById(R.id.lv_route_ret_list);
        spBeatsPlan = (Spinner) myInflatedView.findViewById(R.id.spnr_beat_list);
        edNameSearch = (EditText) myInflatedView.findViewById(R.id.et_name_search);
        edNameSearch.addTextChangedListener(this);
    }
    private void setValuesToUI() {
        getRouteNames();

        ArrayAdapter<String> spBeatAdapter = new ArrayAdapter<>(getActivity(),
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

    private void loadAsyncTask() {

        try {
            new GetRetailerList().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearEditTextSearchBox() {

        if(edNameSearch!=null && edNameSearch.getText().toString().length()>0)
            edNameSearch.setText("");
    }

    private void getRouteNames() {
        try{
            arrayRouteVal = OfflineManager.getBeatPlanArray(Constants.RouteSchedules + "?$filter=" + Constants.StatusID + " eq '01'");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error+" : " + e.getMessage());
        }

        if(arrayRouteVal ==null){
            arrayRouteVal = new String[3][1];
            arrayRouteVal[0][0]="";
            arrayRouteVal[1][0]="";
            arrayRouteVal[2][0]="";
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private class GetRetailerList extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(getActivity(),R.style.ProgressDialogTheme);
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

        this.retailerAdapter = new RetailerListAdapter(getActivity(), alRetailerList,mapCPGrp3Desc);
        lv_route_ret_list.setEmptyView(myInflatedView.findViewById(R.id.tv_empty_lay));
        lv_route_ret_list.setAdapter(this.retailerAdapter);
        this.retailerAdapter.notifyDataSetChanged();
    }

    private void getRetailerList() {

        try {
            if(mStrRouteID.equalsIgnoreCase(Constants.All)){
                alRetailerList = OfflineManager.getRetailerList(Constants.ChannelPartners + "?$filter=(" + Constants.CPNo + " ne '' and " + Constants.CPNo + " ne null)" +
                        " and " + Constants.StatusID + " eq '01' and " + Constants.ApprvlStatusID + " eq '03'" +
                        " &$orderby=" + Constants.RetailerName + "%20asc","");
            }else{
                alRetailerList = OfflineManager.getRetListByRouteSchudule(Constants.RouteSchedulePlans+ "?$filter="
                        +Constants.RouteSchGUID+" eq guid'"+mStrRouteSchGuid.toUpperCase()+"'");
            }
            mapCPGrp3Desc = Constants.getCPGrp3Desc(alRetailerList);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }
}
