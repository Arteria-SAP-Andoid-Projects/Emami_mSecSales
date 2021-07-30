package com.arteriatech.emami.routeplan;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.distributor.DistributorBean;
import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.adapter.RoutePlanAdapter;
import com.arteriatech.emami.adapter.TodayRoutePlanAdapter;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by e10526 on 27-10-2016.
 *
 */
public class RoutePlanFragment extends Fragment implements TextWatcher {
    String mStrRouteType = "";
    EditText et_name_search;
    private RoutePlanAdapter retailerAdapter = null;
    private TodayRoutePlanAdapter routeAdapter = null;
    ListView lv_route_ret_list = null;
    TextView tvEmptyLay = null,tv_route_name=null;
    LinearLayout ll_route_name_line;
    ProgressDialog pdLoadDialog;
    View myInflatedView;
    ArrayList<CustomerBean> alRSCHList = null,alRouteName=null,alRetailerList=null;
    String routeQry=null,routeName = "";
    HashMap<String,String> mapCPGrp3Desc = new HashMap<>();
    private Spinner sprDistributor;
    ArrayList<DistributorBean> alDistributorBeans=new ArrayList<>();
    String cpNo="";


    public RoutePlanFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mStrRouteType = getArguments().getString(Constants.RouteType);
        myInflatedView = inflater.inflate(R.layout.fragment_route_plan, container, false);
        onInitUI(myInflatedView);
        LoadingData();
        return myInflatedView;
    }

    /*
           * TODO This method initialize UI
           */
    private void onInitUI(View myInflatedView) {

        sprDistributor =(Spinner) myInflatedView.findViewById(R.id.sp_distributor);

        et_name_search = (EditText) myInflatedView.findViewById(R.id.et_name_search_route_paln);
        tv_route_name = (TextView) myInflatedView.findViewById(R.id.tv_route_name);
        ll_route_name_line= (LinearLayout) myInflatedView.findViewById(R.id.ll_route_name_line);
        tvEmptyLay = (TextView) myInflatedView.findViewById(R.id.tv_empty_lay_today_beat);
        lv_route_ret_list = (ListView) myInflatedView.findViewById(R.id.lv_route_ret_list);
        et_name_search.addTextChangedListener(this);

    }

    /*
              * TODO Get Route List
              */


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence cs, int start, int before, int count) {
        if(alRSCHList!=null && alRSCHList.size()>0) {
            if(alRSCHList.size()>1){
                if (routeAdapter != null) {
                    routeAdapter.getFilter().filter(cs); //Filter from my adapter
                    routeAdapter.notifyDataSetChanged(); //Update my view
                }
            }else{
                if (retailerAdapter != null) {
                    retailerAdapter.getFilter().filter(cs); //Filter from my adapter
                    retailerAdapter.notifyDataSetChanged(); //Update my view
                }
            }

        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    private void LoadingData() {
        try {
            new AsynLoadTodaysBeat().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /*AsyncTask to get Route Plans*/
    private class AsynLoadTodaysBeat extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pdLoadDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
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
    public void checkListLoaded() {
        if (Constants.BoolTodayBeatLoaded && Constants.BoolOtherBeatLoaded) {
            pdLoadDialog.dismiss();
            onDisplyTodaysRoute();

        } else {
            try {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkListLoaded();
                    }
                }, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void onDisplyTodaysRoute() {

        String mStrDisplayGeoLoc="";
        try {
            mStrDisplayGeoLoc = OfflineManager.getValueByColumnName(Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                    Constants.SSCP + "' and " + Constants.Types + " eq '" + Constants.GEOLOCUPD + "' &$top=1", Constants.TypeValue);

        } catch (OfflineODataStoreException e) {
            mStrDisplayGeoLoc = "";
        }catch (Exception e){
            mStrDisplayGeoLoc = "";
        }


        if(alRSCHList==null){
            alRSCHList =new ArrayList<>();
        }
        if(alRSCHList.size()>=1){
            et_name_search.setHint(getResources().getString(R.string.lbl_search_by_beat_name));
            ll_route_name_line.setVisibility(View.GONE);
            tv_route_name.setVisibility(View.GONE);

            routeAdapter = new TodayRoutePlanAdapter(myInflatedView, alRSCHList, mStrRouteType, tvEmptyLay);
            lv_route_ret_list.setEmptyView(getActivity().findViewById(R.id.tv_empty_lay_today_beat) );
            lv_route_ret_list.setAdapter(routeAdapter);
            routeAdapter.notifyDataSetChanged();
        }else{
            et_name_search.setHint(getResources().getString(R.string.lbl_search_by_retailer_name));
            ll_route_name_line.setVisibility(View.VISIBLE);
            tv_route_name.setVisibility(View.VISIBLE);

            retailerAdapter = new RoutePlanAdapter(myInflatedView, alRetailerList, mStrRouteType, tvEmptyLay, mapCPGrp3Desc,mStrDisplayGeoLoc);
            lv_route_ret_list.setEmptyView(getActivity().findViewById(R.id.tv_empty_lay_today_beat) );
            lv_route_ret_list.setAdapter(retailerAdapter);
            retailerAdapter.notifyDataSetChanged();
        }

        tv_route_name.setText(getString(R.string.lbl_beat_name)+" "+getString(R.string.str_colon)+" "+routeName);




        if(alRSCHList !=null && alRSCHList.size()>0) {
            if (alRSCHList.size() < 1) {
                tvEmptyLay.setVisibility(View.VISIBLE);
            } else
                tvEmptyLay.setVisibility(View.GONE);
        }else{
            tvEmptyLay.setVisibility(View.VISIBLE);
        }
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

    private void LoadingBeatData(String cpNo) {
        try {
            new AsynLoadSpinner(cpNo).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

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
            checkListLoaded();

        }
    }

    private void getRouteList(String cpNo) {


        /*if (cpNo.length() > 5)
        {
            cpNo = cpNo.substring(cpNo.length() - 5);
        }*/

        try{
            cpNo= ConstantsUtils.removeZero(cpNo.toString());
        }catch (Exception e)
        {
            e.printStackTrace();
        }


        try {

            if (mStrRouteType.equalsIgnoreCase(Constants.BeatPlan))
            {


              //  routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "' and CustomerNo eq '" + cpNo + "'";

                routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "'";



                /* Commented to check performance 2-5-2017 // Ramakrishna
                Constants.BoolMoreThanOneRoute = OfflineManager.getCheckMoreThanOneRoute(routeQry);

                if(Constants.BoolMoreThanOneRoute){
                    alRSCHList = OfflineManager.getTodayRoutes(routeQry);
                }else{

                    alRSCHList = OfflineManager.getRetailerListForRoute(routeQry);

                    alRouteName =  OfflineManager.getTodayRoutesName(routeQry);
                }



            } else
            {
                String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "'";
                alRSCHList = OfflineManager.getRetailerListForOtherRoute(routeQry);

                */

                alRSCHList = OfflineManager.getTodayRoutes1(routeQry,cpNo);

                if(alRSCHList!=null && alRSCHList.size()>0){
                    // Based on RouteScope value need to decide where to fetch applicable retailers for the beat
                    // RouteScope = 000001, Get from RoutePlanSchedules
                    // RouteScope = 000002, Get from Routeplans


                /* if(alRSCHList.size()>0)

                 {
                        for(int i=0;i<alRSCHList.size();i++)
                        {
                            if(alRSCHList.get(i).getCustomerNo().equals(cpNo)){



                            }

                        }

                 }*/

                    if(alRSCHList.size()>=1)
                    {


                    }
                    else
                    {
                        String routeSchopeVal = alRSCHList.get(0).getRoutSchScope();
                        Constants.Route_Plan_Key = alRSCHList.get(0).getRoutePlanKey();
                        routeName = alRSCHList.get(0).getRouteDesc();
                        Constants.Route_Schudle_GUID = alRSCHList.get(0).getRschGuid();

                        if(routeSchopeVal.equalsIgnoreCase("000001"))
                        {
                            // Get the list of retailers from RouteSchedulePlans
                            String qryForTodaysBeat = Constants.RouteSchedulePlans + "?$filter=" + Constants.RouteSchGUID + " eq guid'"
                                    + alRSCHList.get(0).getRschGuid().toUpperCase() + "' &$orderby=" + Constants.SequenceNo + "";
                            // Prepare Today's beat Retailer Query
                            String mCPGuidQry = OfflineManager.getBeatList(qryForTodaysBeat);
                            // Get Today's Retailer Details
                            if(!mCPGuidQry.equalsIgnoreCase(""))
                            {
                                List<CustomerBean> listRetailers = OfflineManager.getTodayBeatRetailer(mCPGuidQry,Constants.mMapCPSeqNo);
                                alRetailerList = (ArrayList<CustomerBean>) listRetailers;

                                mapCPGrp3Desc = Constants.getCPGrp3Desc(alRetailerList);
                            }




                        }
                        else if(routeSchopeVal.equalsIgnoreCase("000002"))
                        {
                            // Get the list of retailers from RoutePlans
                        }
                    }
                }


            }



        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        Constants.BoolTodayBeatLoaded = true;
    }


}