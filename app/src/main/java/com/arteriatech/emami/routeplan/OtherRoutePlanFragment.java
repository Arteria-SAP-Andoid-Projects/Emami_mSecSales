package com.arteriatech.emami.routeplan;

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
import android.widget.TextView;

import com.arteriatech.emami.distributor.DistributorBean;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.emami.adapter.OtherRoutePlanAdapter;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10526 on 11-12-2016.
 *
 */

public class OtherRoutePlanFragment extends Fragment implements TextWatcher {
    String  mStrRouteType ="";
    EditText et_name_search;
    private ArrayList<CustomerBean> alOtherRSCHList =new ArrayList<>();
    private OtherRoutePlanAdapter retailerAdapter = null;
    ListView lv_route_ret_list = null;
    TextView tvEmptyLay_other = null;
    View myInflatedView;
    private Spinner sprDistributor;
    ArrayList<DistributorBean> alDistributorBeans=new ArrayList<>();
    String cpNo="";
    public OtherRoutePlanFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mStrRouteType = getArguments().getString(Constants.RouteType);
        myInflatedView = inflater.inflate(R.layout.fragment_other_beat_plan, container,false);
        onInitUI(myInflatedView);

        LoadingData();

        return myInflatedView;
    }

    /*
     * TODO This method initialize UI
     */
    private void onInitUI(View myInflatedView){
        sprDistributor =(Spinner) myInflatedView.findViewById(R.id.sp_distributor);
        et_name_search = (EditText)myInflatedView.findViewById(R.id.et_name_search_route_paln);
        et_name_search.setHint(getResources().getString(R.string.lbl_search_by_beat_name));
        lv_route_ret_list = (ListView) myInflatedView.findViewById(R.id.lv_route_ret_list);
        tvEmptyLay_other = (TextView)myInflatedView.findViewById(R.id.tv_empty_lay_other_beat);
        et_name_search.addTextChangedListener(this);
    }
    /*
     * TODO Get Route List
     */
    private  void getRouteList(String cpNo){
        try {
            String routeQry = Constants.RouteSchedules + "?$filter=" + Constants.StatusID + " eq '01' and CPGUID eq '" + cpNo + "'";
            alOtherRSCHList = OfflineManager.getRetailerListForOtherRoute1(routeQry);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        Constants.BoolOtherBeatLoaded = true;
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

    private void onDisplyOtherRoute(){
        retailerAdapter = new OtherRoutePlanAdapter( myInflatedView, alOtherRSCHList,mStrRouteType, tvEmptyLay_other);
        lv_route_ret_list.setEmptyView(getActivity().findViewById(R.id.tv_empty_lay_other_beat) );
        lv_route_ret_list.setAdapter(retailerAdapter);
        retailerAdapter.notifyDataSetChanged();

        if(alOtherRSCHList !=null && alOtherRSCHList.size()>0) {
            if (alOtherRSCHList.size() < 1) {
                lv_route_ret_list.setVisibility(View.GONE);
                tvEmptyLay_other.setVisibility(View.VISIBLE);
            } else
                lv_route_ret_list.setVisibility(View.VISIBLE);
            tvEmptyLay_other.setVisibility(View.GONE);
        }else{
            tvEmptyLay_other.setVisibility(View.VISIBLE);
        }
    }

    private void LoadingData() {
        try {
            new AsynLoadOtherBeats().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void LoadingBeatData(String cpNo) {
        try {
            new AsynLoadSpinner(cpNo).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    /*AsyncTask to get Route Plans*/
    private class AsynLoadSpinner extends AsyncTask<Void,Void,Void> {
        String cpno="";
        public AsynLoadSpinner(String cpNo) {
            this.cpno=cpNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            getRouteList(cpno);

            //getRouteList();

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(alOtherRSCHList.size()>0) {
                onDisplyOtherRoute();
            }else{
                lv_route_ret_list.setVisibility(View.GONE);
            }


        }
    }
    private class AsynLoadOtherBeats extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            getDistributorList();
            //getRouteList();

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            populateData();


        }
    }
    private void getDistributorList() {
        String qry = Constants.CPSPRelations;
        alDistributorBeans = OfflineManager.getDistributorsList(qry);
    }

    private void populateData() {
        try {
            ArrayAdapter<DistributorBean> adapter = new ArrayAdapter<DistributorBean>(getActivity(), android.R.layout.simple_spinner_item, alDistributorBeans);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sprDistributor.setAdapter(adapter);

            sprDistributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    DistributorBean distributorBean = alDistributorBeans.get(position);
                    cpNo = distributorBean.getcPGUID();
                    LoadingBeatData(cpNo);

                    //  distributorViewPresenter.getDistributorData(cpNo);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
