package com.arteriatech.emami.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.feedback.FeedbackDetails;
import com.arteriatech.emami.mbo.FeedbackBean;
import com.arteriatech.emami.msecsales.R;

import java.util.ArrayList;

/**
 * Created by e10742 on 06-01-2017.
 *
 */

/*Adapter for Feedback List*/
public class FeedbackListAdapter extends ArrayAdapter<FeedbackBean> {
    private ArrayList<FeedbackBean> FeedbackHisOriginalValues;
    private ArrayList<FeedbackBean> FeedbackHisDisplayValues;
    private ArrayList<FeedbackBean> alFeedBackBean;
    private FeedbackListFilter filter = null;
    Context context = null;
    TextView tvEmptyLay = null;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";

    public FeedbackListAdapter(Context context, int textViewResourceId, ArrayList<FeedbackBean> items,
                               TextView tvEmptyLay, String mStrBundleRetID, String mStrBundleRetName) {
        super(context, R.layout.activity_invoice_history_list, items);
        this.FeedbackHisOriginalValues = items;
        this.FeedbackHisDisplayValues = items;
        alFeedBackBean = items;
        this.context = context;
        this.tvEmptyLay = tvEmptyLay;
        this.mStrBundleRetID = mStrBundleRetID;
        this.mStrBundleRetName = mStrBundleRetName;
    }

    @Override
    public int getCount() {
        return FeedbackHisDisplayValues != null ? FeedbackHisDisplayValues.size() : 0;
    }


    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.feed_back_list_item, null);
        }
        FeedbackBean lb = FeedbackHisDisplayValues.get(position);

        if (lb != null) {

            TextView tv_feed_back_no = (TextView) v.findViewById(R.id.tv_feed_back_no);
            TextView tvFeedbackType = (TextView) v.findViewById(R.id.tv_feedback_type_val);

            tv_feed_back_no.setText(lb.getFeedbackNo());
            tvFeedbackType.setText(lb.getFeedbackTypeDesc());



            v.setId(position);
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedbackBean selectedList = new FeedbackBean();
                selectedList = alFeedBackBean.get(v.getId());

                Intent toInvoiceHisdetails = new Intent(context, FeedbackDetails.class);
                toInvoiceHisdetails.putExtra(Constants.CPNo, mStrBundleRetID);
                toInvoiceHisdetails.putExtra(Constants.RetailerName, mStrBundleRetName);
                toInvoiceHisdetails.putExtra(Constants.FeedbackNo, selectedList.getFeedbackNo());
                toInvoiceHisdetails.putExtra(Constants.FeedBackGuid, selectedList.getFeebackGUID());

                toInvoiceHisdetails.putExtra(Constants.FeedbackDesc, selectedList.getFeedbackTypeDesc());
                toInvoiceHisdetails.putExtra(Constants.BTSID, selectedList.getBTSID());
                toInvoiceHisdetails.putExtra(Constants.Location, selectedList.getLocation1());
                toInvoiceHisdetails.putExtra(Constants.Remarks, selectedList.getRemarks());

                toInvoiceHisdetails.putExtra(Constants.DeviceStatus, selectedList.getDeviceStatus());
                toInvoiceHisdetails.putExtra(Constants.DeviceNo, selectedList.getDeviceNo());
                context.startActivity(toInvoiceHisdetails);
            }
        });

        return v;
    }

    public android.widget.Filter getFilter() {
        if (filter == null) {
            filter = new FeedbackListFilter();
        }
        return filter;
    }

    /**
     * This class search name based on customer name from list.
     */
    private class FeedbackListFilter extends android.widget.Filter {
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (FeedbackHisOriginalValues == null) {
                if(FeedbackHisDisplayValues==null){
                    FeedbackHisDisplayValues=new ArrayList<>();
                }
                FeedbackHisOriginalValues = new ArrayList<>(FeedbackHisDisplayValues);
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = FeedbackHisOriginalValues;
                results.count = FeedbackHisOriginalValues.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<FeedbackBean> filteredItems = new ArrayList<>();
                int count = FeedbackHisOriginalValues.size();

                for (int i = 0; i < count; i++) {
                    FeedbackBean item = FeedbackHisOriginalValues.get(i);
                    String mSirSchemeDescription = item.getLocation1().toLowerCase();
                    if (mSirSchemeDescription.contains(prefixString)) {
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
            FeedbackHisDisplayValues = (ArrayList<FeedbackBean>) results.values; // has the filtered values
            notifyDataSetChanged();
            alFeedBackBean = FeedbackHisDisplayValues;

            if (alFeedBackBean != null && alFeedBackBean.size() > 0)
                tvEmptyLay.setVisibility(View.GONE);
            else
                tvEmptyLay.setVisibility(View.VISIBLE);
        }
    }
}
