package com.arteriatech.emami.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.reports.InvoiceDetailsActivity;
import com.arteriatech.emami.reports.InvoiceHistoryBean;

import java.util.ArrayList;

/**
 * Created by e10526 on 09-12-2016.
 *
 */

public class InvoiceHisListAdapter extends ArrayAdapter<InvoiceHistoryBean> {
    private ArrayList<InvoiceHistoryBean> InvHisOriginalValues;
    private ArrayList<InvoiceHistoryBean> InvHisDisplayValues;
    private InvoiceHisListAdapter.InvoiceListFilter filter;
    private ArrayList<InvoiceHistoryBean> alInvoiceBean;
    private Context context;
    private InvoiceHistoryBean selectedList;
    private String mStrCPGUID,mStrCPNO,mStrCpName,mStrCPUID;

    public InvoiceHisListAdapter(Context context, int textViewResourceId,
                                 ArrayList<InvoiceHistoryBean> items,Bundle bundle) {
        super(context, R.layout.activity_invoice_history_list, items);
        this.InvHisOriginalValues = items;
        this.InvHisDisplayValues = items;
        alInvoiceBean = items;
        this.context = context;
        this.mStrCpName = bundle.getString(Constants.RetailerName);
        this.mStrCPNO = bundle.getString(Constants.CPNo);
        this.mStrCPGUID= bundle.getString(Constants.CPGUID);
        this.mStrCPUID= bundle.getString(Constants.CPUID);
    }

    @Override
    public int getCount() {
        return InvHisDisplayValues != null ? InvHisDisplayValues.size() : 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context. getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.invoice_history_item_list, null);
        }
        InvoiceHistoryBean lb = InvHisDisplayValues.get(position);

        if (lb != null) {

            TextView invNO = (TextView) v.findViewById(R.id.tv_in_history_no);
            TextView invDate = (TextView) v.findViewById(R.id.tv_in_history_date);
            TextView invAmount = (TextView) v.findViewById(R.id.tv_in_history_amt);
            TextView tvStatusIndicator = (TextView) v.findViewById(R.id.tv_status_indicator);
            invNO.setText(lb.getInvoiceNo());
            invDate.setText(UtilConstants.convertDateIntoDeviceFormat(context,lb.getInvoiceDate()));

            invAmount.setText((UtilConstants.removeLeadingZerowithTwoDecimal(lb.getInvoiceAmount())+" "+ lb.getCurrency()).trim());

            if(lb.getInvoiceStatus().toString().equals("01")){
                tvStatusIndicator.setBackgroundResource(R.color.RED);

            }else if(lb.getInvoiceStatus().toString().equals("02")){
                tvStatusIndicator.setBackgroundResource(R.color.YELLOW);
            }
            else if(lb.getInvoiceStatus().toString().equals("03")){
                tvStatusIndicator.setBackgroundResource(R.color.GREEN);
            }

            v.setId(position);
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedList = new InvoiceHistoryBean();
                selectedList = alInvoiceBean.get(v.getId());

                Intent toInvoiceHisdetails = new Intent(context, InvoiceDetailsActivity.class);
                toInvoiceHisdetails.putExtra(Constants.CPNo, mStrCPNO);
                toInvoiceHisdetails.putExtra(Constants.RetailerName, mStrCpName);
                toInvoiceHisdetails.putExtra(Constants.CPUID, mStrCPUID!=null?mStrCPUID:mStrCPNO);
                toInvoiceHisdetails.putExtra(Constants.InvoiceNo, selectedList.getInvoiceNo());
                toInvoiceHisdetails.putExtra(Constants.InvoiceGUID, selectedList.getInvoiceGuid());
                toInvoiceHisdetails.putExtra(Constants.CPGUID, mStrCPGUID.toUpperCase());
                toInvoiceHisdetails.putExtra(Constants.Currency, selectedList.getCurrency());
                toInvoiceHisdetails.putExtra(Constants.DeviceNo, selectedList.getDeviceNo());
                toInvoiceHisdetails.putExtra(Constants.CollectionAmount, selectedList.getCollectionAmount()!=null?selectedList.getCollectionAmount():"0.00");
                toInvoiceHisdetails.putExtra(Constants.STATUS, selectedList.getInvoiceStatus());
                toInvoiceHisdetails.putExtra(Constants.DeviceStatus, selectedList.getDeviceStatus());
                toInvoiceHisdetails.putExtra(Constants.InvDate, selectedList.getInvoiceDate());
                toInvoiceHisdetails.putExtra(Constants.InvAmount, selectedList.getInvoiceAmount());

                context.startActivity(toInvoiceHisdetails);
            }
        });

        return v;
    }

    public android.widget.Filter getFilter() {
        if (filter == null) {
            filter = new InvoiceHisListAdapter.InvoiceListFilter();
        }
        return filter;
    }

    /**
     * This class search invoices based on invoice number from list.
     */
    private class InvoiceListFilter extends android.widget.Filter {
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (InvHisOriginalValues == null) {
                if(InvHisDisplayValues==null){
                    InvHisDisplayValues=new ArrayList<>();
                }
                InvHisOriginalValues = new ArrayList<>(InvHisDisplayValues);
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = InvHisOriginalValues;
                results.count = InvHisOriginalValues.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<InvoiceHistoryBean> filteredItems = new ArrayList<>();
                int count = InvHisOriginalValues.size();

                for (int i = 0; i < count; i++) {
                    InvoiceHistoryBean item = InvHisOriginalValues.get(i);
                    String mSirSchemeDescription = item.getInvoiceNo().toLowerCase();
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
            InvHisDisplayValues = (ArrayList<InvoiceHistoryBean>) results.values; // has the filtered values
            notifyDataSetChanged();
            alInvoiceBean = InvHisDisplayValues;
        }
    }
}
