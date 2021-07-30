package com.arteriatech.emami.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.UpdateListener;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.reports.CollectionDetailsActivity;
import com.arteriatech.emami.reports.CollectionHistoryBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 09-12-2016.
 *
 */

public class CollectionHisListAdapter  extends ArrayAdapter<CollectionHistoryBean> {
    private ArrayList<CollectionHistoryBean> CollHisOriginalValues;
    private ArrayList<CollectionHistoryBean> CollHisDisplayValues;
    private CollectionListFilter filter;
    private ArrayList<CollectionHistoryBean> alCollectionBean;
    private Context context;
    private String mStrCPGUID,mStrCPNO,mStrCpName;
    TextView tvEmptyLayCollHis;
    UpdateListener updateListener;

    public CollectionHisListAdapter(Context context, ArrayList<CollectionHistoryBean> items,
                                    String mStrCPGUID,String mStrCPNO,String mStrCpName,
                                    TextView tvEmptyLayCollHis) {
        super(context, R.layout.collection_history_list_item, items);

        this.CollHisOriginalValues = items;
        this.CollHisDisplayValues = items;
        this.alCollectionBean = items;
        this.context = context;
        this.mStrCpName = mStrCpName;
        this.mStrCPNO = mStrCPNO;
        this.mStrCPGUID = mStrCPGUID;
//        this.updateListener = updateListener;
        this.tvEmptyLayCollHis = tvEmptyLayCollHis;
    }

    @Override
    public int getCount() {
        return this.CollHisDisplayValues != null ? this.CollHisDisplayValues.size() : 0;
    }

    @Override
    public CollectionHistoryBean getItem(int item) {
        CollectionHistoryBean collectionHistoryBean;
        collectionHistoryBean = this.CollHisDisplayValues != null ? this.CollHisDisplayValues.get(item) : null;
        return collectionHistoryBean;
    }

    private class ViewHolder {
        TextView tv_collection_history_doc_no;
        TextView tv_collection_history_date;
        TextView tv_collection_history_amt;
        TextView tv_collection_payment;
//        TextView tv_collection_type;

    }

    CollectionHisListAdapter.ViewHolder holder = null;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.collection_history_list_item, parent,false);
            holder = new CollectionHisListAdapter.ViewHolder();
            view.setTag(holder);

            holder.tv_collection_history_doc_no = (TextView) view.findViewById(R.id.tv_collection_history_doc_no);
            holder.tv_collection_history_date = (TextView) view.findViewById(R.id.tv_collection_history_date);
            holder.tv_collection_history_amt = (TextView) view.findViewById(R.id.tv_collection_history_amt);
            holder.tv_collection_payment = (TextView) view.findViewById(R.id.tv_collection_paymode_mode);
//            holder.tv_collection_type = (TextView) view.findViewById(R.id.tv_collection_type);



        } else {
            holder = (CollectionHisListAdapter.ViewHolder) convertView.getTag();
        }

        final CollectionHistoryBean collectionListBean = CollHisDisplayValues.get(position);

        holder.tv_collection_history_doc_no.setText(collectionListBean.getFIPDocNo());
        holder.tv_collection_history_date.setText(UtilConstants.convertDateIntoDeviceFormat(context,collectionListBean.getFIPDate()));

        holder.tv_collection_payment.setText(context.getString(R.string.str_concat_two_texts_black_slash,
                collectionListBean.getReferenceTypeDesc(), collectionListBean.getPaymentModeDesc()));
//        holder.tv_collection_type.setText(collectionListBean.getReferenceTypeDesc());
        holder.tv_collection_history_amt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(collectionListBean.getAmount())
                +" "+collectionListBean.getCurrency());
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
            filter = new CollectionHisListAdapter.CollectionListFilter();
        }
        return filter;
    }

    /**
     * This class search collections based on collection number from list.
     */
    private class CollectionListFilter extends android.widget.Filter {
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (CollHisOriginalValues == null) {
                if(CollHisDisplayValues==null){
                    CollHisDisplayValues=new ArrayList<>();
                }
                CollHisOriginalValues = new ArrayList<>(CollHisDisplayValues);
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = CollHisOriginalValues;
                results.count = CollHisOriginalValues.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<CollectionHistoryBean> filteredItems = new ArrayList<>();
                int count = CollHisOriginalValues.size();

                for (int i = 0; i < count; i++) {
                    CollectionHistoryBean item = CollHisOriginalValues.get(i);
                    String mSirSchemeDescription = item.getFIPDocNo().toLowerCase();
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
            CollHisDisplayValues = (ArrayList<CollectionHistoryBean>) results.values; // has the filtered values
            notifyDataSetChanged();
            alCollectionBean = CollHisDisplayValues;

//            updateListener.onUpdate();
            if(alCollectionBean.size()>0){
                tvEmptyLayCollHis.setVisibility(View.GONE);
            } else
                tvEmptyLayCollHis.setVisibility(View.VISIBLE);
        }

    }
    /*Navigates to collection detail screen*/
    private void onListDetails(int id) {
        CollectionHistoryBean retailerLB = alCollectionBean.get(id);
        Intent intentRetailerDetails =new Intent(context, CollectionDetailsActivity.class);
        intentRetailerDetails.putExtra(Constants.FISDocNo, retailerLB.getFIPDocNo());
        intentRetailerDetails.putExtra(Constants.FIPGUID, retailerLB.getFIPGUID());
        intentRetailerDetails.putExtra(Constants.CollAmount, retailerLB.getAmount());
        intentRetailerDetails.putExtra(Constants.CollDate, retailerLB.getFIPDate());
        intentRetailerDetails.putExtra(Constants.DeviceStatus, retailerLB.getDeviceStatus());
        intentRetailerDetails.putExtra(Constants.DeviceNo, retailerLB.getDeviceNo());

        intentRetailerDetails.putExtra(Constants.CPNo,mStrCPNO);
        intentRetailerDetails.putExtra(Constants.RetailerName,mStrCpName);
        intentRetailerDetails.putExtra(Constants.CPUID,mStrCPNO);
        intentRetailerDetails.putExtra(Constants.InstrumentNo, retailerLB.getInstrumentNo());
        intentRetailerDetails.putExtra(Constants.Currency, retailerLB.getCurrency());
        intentRetailerDetails.putExtra(Constants.CPGUID, mStrCPGUID.toUpperCase());
        intentRetailerDetails.putExtra(Constants.PaymentMode, retailerLB.getPaymentModeDesc());
        context.startActivity(intentRetailerDetails);
    }

}
