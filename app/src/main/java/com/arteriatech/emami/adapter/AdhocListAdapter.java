package com.arteriatech.emami.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

public class AdhocListAdapter extends ArrayAdapter<CustomerBean> {
    private ArrayList<CustomerBean> retOriginalValues;
    private ArrayList<CustomerBean> retDisplayValues;
    private ArrayList<CustomerBean> alRetailerList = null;
    private RetailerListFilter filter;
    private Context context;
    HashMap<String, String> mapCPGrp3Desc;
    String mStrGeoLoc = "";

    public AdhocListAdapter(Context context, ArrayList<CustomerBean> items, HashMap<String, String> mapCPGrp3Desc, String mStrGeoLoc) {
        super(context, R.layout.adhoc_list_adapter, items);
        this.retOriginalValues = items;
        this.retDisplayValues = items;
        alRetailerList = items;
        this.context = context;
        this.mStrGeoLoc = mStrGeoLoc;
        this.mapCPGrp3Desc = mapCPGrp3Desc;
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

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = null;
        //    if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.adhoc_list_adapter, parent, false);
        //   }

        TextView tvRetailerName = (TextView) view.findViewById(R.id.tv_RetailerName);
        ImageView ivMobileNo = (ImageView) view.findViewById(R.id.iv_mobile);
        TextView tv_retailer_mob_no = (TextView) view.findViewById(R.id.tv_retailer_mob_no);
        TextView tv_status_color = (TextView) view.findViewById(R.id.tv_status_color);
        TextView tv_down_color = (TextView) view.findViewById(R.id.tv_down_color);

        TextView tvRetailerCatTypeDesc = (TextView) view.findViewById(R.id.tv_retailer_cat_type_desc);


        TextView tv_address2 = (TextView) view.findViewById(R.id.tv_address2);
        ImageView iv_expand_icon = (ImageView) view.findViewById(R.id.iv_expand_icon);


        final CustomerBean retailerListBean = retDisplayValues.get(position);

        tvRetailerName.setText(retailerListBean.getRetailerName());
        tvRetailerCatTypeDesc.setText(mapCPGrp3Desc.get(retailerListBean.getCPGUID()) != null ? mapCPGrp3Desc.get(retailerListBean.getCPGUID()) : "");
        tv_retailer_mob_no.setText(retailerListBean.getUID());

        String cityVal;

        if (!retailerListBean.getLandMark().equalsIgnoreCase("") && !retailerListBean.getCity().equalsIgnoreCase("")) {
            cityVal = retailerListBean.getLandMark() + "," + retailerListBean.getCity();
        } else if (!retailerListBean.getLandMark().equalsIgnoreCase("") && retailerListBean.getCity().equalsIgnoreCase("")) {
            cityVal = retailerListBean.getLandMark();
        } else if (retailerListBean.getLandMark().equalsIgnoreCase("") && !retailerListBean.getCity().equalsIgnoreCase("")) {
            cityVal = retailerListBean.getCity();
        } else {
            cityVal = "";
        }

        String disticVal;

        if (!retailerListBean.getDistrictDesc().equalsIgnoreCase("") && !retailerListBean.getPostalCode().equalsIgnoreCase("")) {
            disticVal = retailerListBean.getDistrictDesc() + " " + retailerListBean.getPostalCode();
        } else if (!retailerListBean.getDistrictDesc().equalsIgnoreCase("") && retailerListBean.getPostalCode().equalsIgnoreCase("")) {
            disticVal = retailerListBean.getDistrictDesc();
        } else if (retailerListBean.getDistrictDesc().equalsIgnoreCase("") && !retailerListBean.getPostalCode().equalsIgnoreCase("")) {
            disticVal = retailerListBean.getPostalCode();
        } else {
            disticVal = "";
        }

        String addressVa = "";
        try {
            if (!retailerListBean.getAddress1().equalsIgnoreCase("")) {
                addressVa = retailerListBean.getAddress1();
            }

            if (!retailerListBean.getAddress2().equalsIgnoreCase("")) {
                addressVa = addressVa + "," + retailerListBean.getAddress2();
            }

            if (!retailerListBean.getAddress3().equalsIgnoreCase("")) {
                addressVa = addressVa + "," + retailerListBean.getAddress3();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        double mDouLatVal = retailerListBean.getLatVal();
        double mDouLongVal = retailerListBean.getLongVal();

        //        LinearLayout ll_lat_long = (LinearLayout) view.findViewById(R.id.ll_lat_long);
        ImageView iv_lat_long_icon = (ImageView) view.findViewById(R.id.iv_lat_long_icon);

        if (mDouLatVal == 0.0 || mDouLongVal == 0.0) {

            iv_lat_long_icon.setImageResource(R.drawable.ic_loca_red_mark_small);
        } else {
            iv_lat_long_icon.setImageResource(R.drawable.ic_loca_green_mark_small);
        }

        if (mStrGeoLoc.equalsIgnoreCase(Constants.X)) {
            iv_lat_long_icon.setVisibility(View.VISIBLE);
        } else {
            iv_lat_long_icon.setVisibility(View.GONE);
        }

        tv_address2.setText(context.getString(R.string.str_concat_two_texts_with_coma, addressVa, "\n" + cityVal + "\n" + disticVal));


        final View testViews = view;
        if (retailerListBean.isAddressEnabled()) {
            //  retailerListBean.setIsAddressEnabled(false);
            iv_expand_icon.setImageResource(R.drawable.up);
            View toolbarEmptyText = testViews.findViewById(R.id.tv_empty_text);
            ExpandAnimation expandemptytext = new ExpandAnimation(toolbarEmptyText, 50);
            toolbarEmptyText.startAnimation(expandemptytext);

            View toolbar = testViews.findViewById(R.id.tv_address2);
            ExpandAnimation expandAni = new ExpandAnimation(toolbar, 50);
            toolbar.startAnimation(expandAni);

            View toolbarSpace = testViews.findViewById(R.id.tv_down_color);
            ExpandAnimation expandAniSpace = new ExpandAnimation(toolbarSpace, 50);
            toolbarSpace.startAnimation(expandAniSpace);
        } else {
            //  retailerListBean.setIsAddressEnabled(true);

            iv_expand_icon.setImageResource(R.drawable.down);

        }


        final View testView = view;
        iv_expand_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView img = (ImageView) v;

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

            String mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "' " +
                    "and CPGUID eq '" + retailerListBean.getCpGuidStringFormat().toUpperCase() + "' and " + Constants.StatusID + " eq '01'";

            String mStrVisitStartedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq null " +
                    "and CPGUID eq '" + retailerListBean.getCpGuidStringFormat().toUpperCase() + "' and " + Constants.StatusID + " eq '01'";

            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)) {
                tv_status_color.setBackgroundResource(R.color.YELLOW);
                tv_down_color.setBackgroundResource(R.color.YELLOW);
            } else if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                tv_status_color.setBackgroundResource(R.color.GREEN);
                tv_down_color.setBackgroundResource(R.color.GREEN);
            } else {
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
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(Constants.tel_txt + (retailerListBean.getMobileNumber())));
                    context.startActivity(dialIntent);
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

    @NonNull
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
                if (retDisplayValues == null) {
                    retDisplayValues = new ArrayList<>();
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
        }
    }

    private void onListDetails(int id) {
        CustomerBean retailerLB = alRetailerList.get(id);

        Intent intentRetailerDetails = new Intent(context, RetailersDetailsActivity.class);
        intentRetailerDetails.putExtra(Constants.RetailerName, retailerLB.getRetailerName());
        intentRetailerDetails.putExtra(Constants.CPUID, retailerLB.getUID());
        intentRetailerDetails.putExtra(Constants.CPNo, retailerLB.getCPNo());
        intentRetailerDetails.putExtra(Constants.comingFrom, Constants.AdhocList);
        intentRetailerDetails.putExtra(Constants.CPGUID, retailerLB.getCPGUID());
        intentRetailerDetails.putExtra(Constants.VisitCatID, "02");
        Constants.VisitNavigationFrom = "";
        Constants.Route_Schudle_GUID = "";
        context.startActivity(intentRetailerDetails);
    }
}
