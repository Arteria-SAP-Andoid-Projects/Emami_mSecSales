package com.arteriatech.emami.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.routeplan.OtherBeatListActivity;

import java.util.ArrayList;

/**
 * Created by e10526 on 1/2/2017.
 *
 */

public class TodayRoutePlanAdapter extends ArrayAdapter<CustomerBean> {
    private ArrayList<CustomerBean> retDisplayValues;
    private ArrayList<CustomerBean> retOriginalValues;
    private RetailerListFilter filter;
    private ArrayList<CustomerBean> alRetailerList = null;
    private View context;
    private String mStrRouteType;
    TextView tvEmptyLay;

    public TodayRoutePlanAdapter(View context, ArrayList<CustomerBean> items, String mStrRouteType, TextView tvEmptyLay) {
        super(context.getContext(), R.layout.beat_plan_line_item, items);
        this.retDisplayValues = items;
        alRetailerList = items;
        this.context=context;
        this.mStrRouteType = mStrRouteType;
        this.tvEmptyLay = tvEmptyLay;
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

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {

        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.other_beat_list_adapter, parent,false);

        }



        TextView tvBeatName = (TextView) view.findViewById(R.id.tv_BeatName);
        TextView tvROUTEID = (TextView) view.findViewById(R.id.tv_ROUTEID);
        final CustomerBean retailerListBean = retDisplayValues.get(position);
        tvBeatName.setText(retailerListBean.getRouteDesc());
        tvROUTEID.setText(retailerListBean.getRouteID());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.Route_Plan_Key = retailerListBean.getRoutePlanKey();
                Constants.Route_Schudle_GUID = retailerListBean.getRschGuid();
                Intent todays_beat_list = new Intent(context.getContext(), OtherBeatListActivity.class);
                todays_beat_list.putExtra(Constants.OtherRouteGUID,retailerListBean.getRschGuid());
                todays_beat_list.putExtra(Constants.OtherRouteName,retailerListBean.getRouteDesc());
                todays_beat_list.putExtra(Constants.RouteType,context.getContext().getString(R.string.lbl_today_beats));

                context.getContext().startActivity(todays_beat_list);
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
                    String mStrRetName = item.getRouteDesc().toLowerCase();
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


}