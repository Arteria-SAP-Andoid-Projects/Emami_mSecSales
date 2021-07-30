package com.arteriatech.emami.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ExpandAnimation;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by e10526 on 09-12-2016.
 */

public class RoutePlanAdapter extends ArrayAdapter<CustomerBean> {
    private ArrayList<CustomerBean> retDisplayValues;
    private ArrayList<CustomerBean> retOriginalValues;
    private RetailerListFilter filter;
    private ArrayList<CustomerBean> alRetailerList = null;
    private View context;
    private String mStrRouteType;
    TextView tvEmptyLay;
    HashMap<String,String> mapCPGrp3Desc;
    String mStrGeoLoc = "";
    SharedPreferences sharedPreferences ;
    String beatOptmEnabled = "";
    public RoutePlanAdapter(View context, ArrayList<CustomerBean> items, String mStrRouteType, TextView tvEmptyLay, HashMap<String,String> mapCPGrp3Desc,String mStrGeoLoc) {
        super(context.getContext(), R.layout.beat_plan_line_item, items);
        this.retDisplayValues = items;
        alRetailerList = items;
        this.context=context;
        this.mStrRouteType = mStrRouteType;
        this.tvEmptyLay = tvEmptyLay;
        this.mStrGeoLoc = mStrGeoLoc;
        this.mapCPGrp3Desc = mapCPGrp3Desc;
        sharedPreferences =  getContext().getSharedPreferences(Constants.PREFS_NAME, 0);
        beatOptmEnabled = sharedPreferences.getString(Constants.isBeatOptmKey, "");
    }

    @Override
    public int getCount() {
        return this.retDisplayValues != null ? this.retDisplayValues.size() : 0;
    }

    @Override
    public CustomerBean getItem(int item) {
        CustomerBean retListBean;
        retListBean = this.retDisplayValues != null ? this.retDisplayValues.get(item) : null;
        return retListBean;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

      //  View view = convertView;
      //  if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
        View view  = inflater.inflate(R.layout.adhoc_list_adapter, parent,false);

      //  }

        TextView tvRetailerName = (TextView) view.findViewById(R.id.tv_RetailerName);
        ImageView ivMobileNo = (ImageView) view.findViewById(R.id.iv_mobile);
        TextView tv_retailer_mob_no = (TextView) view.findViewById(R.id.tv_retailer_mob_no);
        TextView tvRetailerCatTypeDesc = (TextView) view.findViewById(R.id.tv_retailer_cat_type_desc);
        TextView tv_status_color = (TextView) view.findViewById(R.id.tv_status_color);
        TextView tv_down_color = (TextView) view.findViewById(R.id.tv_down_color);
        LinearLayout ll_retailer = (LinearLayout) view.findViewById(R.id.ll_retailer);



        TextView tv_address2 = (TextView) view.findViewById(R.id.tv_address2);
        ImageView iv_expand_icon = (ImageView) view.findViewById(R.id.iv_expand_icon);


        final CustomerBean retailerListBean = retDisplayValues.get(position);
        try {
           if (beatOptmEnabled.equalsIgnoreCase(Constants.isBeatOptmTcode)) {

                if (retailerListBean.getSeqNo().equalsIgnoreCase("") || (retailerListBean.getSeqNo().equalsIgnoreCase("0")) || (retailerListBean.getSeqNo().equalsIgnoreCase("000000"))) {
                    ll_retailer.setBackgroundColor(R.color.LBL_BACKGROUND_ASH_COLOR);
                }
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvRetailerName.setText(retailerListBean.getRetailerName());
        tvRetailerCatTypeDesc.setText(mapCPGrp3Desc.get(retailerListBean.getCPGUID())!=null?mapCPGrp3Desc.get(retailerListBean.getCPGUID()):"");
        tv_retailer_mob_no.setText(retailerListBean.getUID());

        if(mStrRouteType.equalsIgnoreCase(Constants.BeatPlan)) {

            try {
                if (!OfflineManager.getAtleastOneEntity(Constants.getCurrentMonthInvoiceQry(retailerListBean.getCPNo()))){
                    tvRetailerName.setTextColor(Color.RED);
                }else{
                    tvRetailerName.setTextColor(Color.BLACK);
                }
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
        }
        String cityVal = "";

        if(!retailerListBean.getLandMark().equalsIgnoreCase("") && !retailerListBean.getCity().equalsIgnoreCase("")){
            cityVal = retailerListBean.getLandMark()+","+retailerListBean.getCity();
        }else if(!retailerListBean.getLandMark().equalsIgnoreCase("") && retailerListBean.getCity().equalsIgnoreCase("")){
            cityVal = retailerListBean.getLandMark();
        }else if(retailerListBean.getLandMark().equalsIgnoreCase("") && !retailerListBean.getCity().equalsIgnoreCase("")){
            cityVal =retailerListBean.getCity();
        }else{
            cityVal = "";
        }

        String disticVal = "";

        if(!retailerListBean.getDistrictDesc().equalsIgnoreCase("") && !retailerListBean.getPostalCode().equalsIgnoreCase("")){
            disticVal = retailerListBean.getDistrictDesc()+" "+retailerListBean.getPostalCode();
        }else if(!retailerListBean.getDistrictDesc().equalsIgnoreCase("") && retailerListBean.getPostalCode().equalsIgnoreCase("")){
            disticVal = retailerListBean.getDistrictDesc();
        }else if(retailerListBean.getDistrictDesc().equalsIgnoreCase("") && !retailerListBean.getPostalCode().equalsIgnoreCase("")){
            disticVal =retailerListBean.getPostalCode();
        }else{
            disticVal = "";
        }

        String addressVa ="";
        try {
            if(!retailerListBean.getAddress1().equalsIgnoreCase("")){
                addressVa = retailerListBean.getAddress1();
            }

            if(!retailerListBean.getAddress2().equalsIgnoreCase("")){
                addressVa = addressVa+","+retailerListBean.getAddress2();
            }

            if(!retailerListBean.getAddress3().equalsIgnoreCase("")){
                addressVa = addressVa+","+retailerListBean.getAddress3();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        double mDouLatVal = retailerListBean.getLatVal();
        double mDouLongVal = retailerListBean.getLongVal();

        //        LinearLayout ll_lat_long = (LinearLayout) view.findViewById(R.id.ll_lat_long);
        ImageView iv_lat_long_icon = (ImageView) view.findViewById(R.id.iv_lat_long_icon);

        if(mDouLatVal==0.0 || mDouLongVal==0.0){

            iv_lat_long_icon.setImageResource(R.drawable.ic_loca_red_mark_small);
        }else{
            iv_lat_long_icon.setImageResource(R.drawable.ic_loca_green_mark_small);
        }

        if(mStrGeoLoc.equalsIgnoreCase(Constants.X)){
            iv_lat_long_icon.setVisibility(View.VISIBLE);
        }else{
            iv_lat_long_icon.setVisibility(View.GONE);
        }

        tv_address2.setText(context.getContext().getString(R.string.str_concat_two_texts_with_coma,addressVa , "\n"+cityVal +"\n"+ disticVal));



        final View testView = view;
        iv_expand_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView img = (ImageView)v;

                if (retailerListBean.isAddressEnabled()) {
                    retailerListBean.setIsAddressEnabled(false);
                    img.setImageResource(R.drawable.down);

                } else {
                    retailerListBean.setIsAddressEnabled(true);
                    img.setImageResource(R.drawable.up);

                }

                View toolbarEmptyText = testView.findViewById(R.id.tv_empty_text);
                ExpandAnimation expandemptytext = new ExpandAnimation(toolbarEmptyText, 50);
                toolbarEmptyText.startAnimation(expandemptytext);

                View toolbar = testView.findViewById(R.id.tv_address2);
                ExpandAnimation expandAni = new ExpandAnimation(toolbar, 50);
                toolbar.startAnimation(expandAni);

                View toolbarSpace = testView.findViewById(R.id.tv_down_color);
                ExpandAnimation expandAniSpace = new ExpandAnimation(toolbarSpace, 50);
                toolbarSpace.startAnimation(expandAniSpace);
            }
        });


        try {
            String mStrVisitStartEndQry = "";
            String mStrVisitStartedQry = "";
            if(mStrRouteType.equalsIgnoreCase(Constants.BeatPlan)) {
                mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "' " +
                        "and CPGUID eq '" + retailerListBean.getCpGuidStringFormat().toUpperCase() + "'and StatusID eq '01'";

                mStrVisitStartedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq null " +
                        "and CPGUID eq '" + retailerListBean.getCpGuidStringFormat().toUpperCase() + "'and StatusID eq '01'";
            }
            else
            {
                mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "' " +
                        "and CPGUID eq '" + retailerListBean.getCpGuidStringFormat().toUpperCase() + "' and "+Constants.StatusID+" eq '01'";

                mStrVisitStartedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq null " +
                        "and CPGUID eq '" + retailerListBean.getCpGuidStringFormat().toUpperCase() + "' and "+Constants.StatusID+" eq '01'";
            }

            if(OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)){
                tv_status_color.setBackgroundResource(R.color.YELLOW);
                tv_down_color.setBackgroundResource(R.color.YELLOW);
            } else if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                tv_status_color.setBackgroundResource(R.color.GREEN);
                tv_down_color.setBackgroundResource(R.color.GREEN);
            }else{
                tv_status_color.setBackgroundResource(R.color.RED);
                tv_down_color.setBackgroundResource(R.color.RED);
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
            tv_status_color.setBackgroundResource(R.color.RED);
        }

        ivMobileNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!retailerListBean.getMobileNumber().equalsIgnoreCase("")) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt+ (retailerListBean.getMobileNumber())));
                    context.getContext().startActivity(dialIntent);
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onListDetails(v.getId());
            }
        });
        view.setId(position);
        return view;
    }


    public android.widget.Filter getFilter() {
        if (filter == null) {
            filter = new RetailerListFilter();
        }
        return filter;
    }

    /**
     * This class search name based on customer name from list.
     */
    private class RetailerListFilter extends android.widget.Filter {
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (retOriginalValues == null) {
                if(retDisplayValues==null){
                    retDisplayValues=new ArrayList<>();
                }
                retOriginalValues = new ArrayList<>(retDisplayValues);
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = retOriginalValues;
                results.count = retOriginalValues.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<CustomerBean> filteredItems = new ArrayList<>();
                int count = retOriginalValues.size();

                for (int i = 0; i < count; i++) {
                    CustomerBean item = retOriginalValues.get(i);
                    String mStrRetName = item.getRetailerName().toLowerCase();
                    if (mStrRetName.contains(prefixString)) {
                        filteredItems.add(item);
                    }
                }
                results.values = filteredItems;
                results.count = filteredItems.size();
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence prefix, FilterResults results) {
            //noinspection unchecked
            retDisplayValues = (ArrayList<CustomerBean>) results.values; // has the filtered values
            notifyDataSetChanged();
            alRetailerList = retDisplayValues;
            if(alRetailerList.size()>0){
                tvEmptyLay.setVisibility(View.GONE);
            } else
                tvEmptyLay.setVisibility(View.VISIBLE);

        }
    }

    private void onListDetails(int id) {
        CustomerBean retailerLB = alRetailerList.get(id);
        try {
            Intent intent=null;
            intent =new Intent(context.getContext(), RetailersDetailsActivity.class);
            intent.putExtra(Constants.RetailerName, retailerLB.getRetailerName());
            intent.putExtra(Constants.CPUID, retailerLB.getUID());
            intent.putExtra(Constants.CPNo, retailerLB.getCPNo());
            intent.putExtra(Constants.CPGUID, retailerLB.getCPGUID());
            intent.putExtra(Constants.BeatType, Constants.RouteList);
            intent.putExtra(Constants.VisitType, retailerLB.getVisitType());
            intent.putExtra(Constants.comingFrom, Constants.RouteList);
            intent.putExtra(Constants.VisitCatID,"01");
            intent.putExtra(Constants.VisitSeq,retailerLB.getSeqNo());
            intent.putExtra(Constants.OtherRouteGUID,Constants.Route_Schudle_GUID);
            intent.putExtra(Constants.TotalRetalierCount,Constants.getTotalRetailerCount(retDisplayValues));
            Constants.Route_Plan_No = retailerLB.getRouteID();
            Constants.Route_Plan_Desc = retailerLB.getRouteDesc();
//            Constants.Route_Plan_Key =retailerLB.getRoutePlanKey();
            Constants.Visit_Type = retailerLB.getVisitType();

            Constants.VisitNavigationFrom = "";
            context.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
