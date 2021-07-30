package com.arteriatech.emami.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.reports.OutstandingBean;
import com.arteriatech.emami.reports.OutstandingDetailActivity;
import com.arteriatech.emami.reports.OutstandingHistoryActivity;

import java.util.ArrayList;

/**
 * Created by e10526 on 09-12-2016.
 *
 */

public class OutstandingListAdapter extends ArrayAdapter<OutstandingBean> {
    private ArrayList<OutstandingBean> InvHisOriginalValues;
    private ArrayList<OutstandingBean> InvHisDisplayValues;
    private OutstandingListAdapter.OutstandingInvListFilter filter;
    private ArrayList<OutstandingBean> alOutstandingsBean;
    private Context context;
    private OutstandingBean selectedList;
    private String mStrCPGUID,mStrCPNO,mStrCpName;

    public OutstandingListAdapter(Context context, int textViewResourceId,
                                  ArrayList<OutstandingBean> items, Bundle bundle) {
        super(context, R.layout.activity_invoice_history_list, items);
        this.InvHisOriginalValues = items;
        this.InvHisDisplayValues = items;
        alOutstandingsBean = items;
        this.context = context;
        this.mStrCpName = bundle.getString(Constants.RetailerName);
        this.mStrCPNO = bundle.getString(Constants.CPNo);
        this.mStrCPGUID= bundle.getString(Constants.CPGUID);
    }

    @Override
    public int getCount() {
        return InvHisDisplayValues != null ? InvHisDisplayValues.size() : 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_out_hist, null);
        }
        OutstandingBean lb = InvHisDisplayValues.get(position);

        if (lb != null) {

            TextView invNO = (TextView) v.findViewById(R.id.tv_in_history_no);
            TextView invDate = (TextView) v.findViewById(R.id.tv_in_history_date);
            TextView invAmount = (TextView) v.findViewById(R.id.tv_in_history_amt);
            TextView invOutAmt = (TextView) v.findViewById(R.id.tv_in_hist_out_amt);
            TextView invBillAge = (TextView) v.findViewById(R.id.tv_in_hist_bill_age);

            invNO.setText(lb.getInvoiceNo());
            invDate.setText(UtilConstants.convertDateIntoDeviceFormat(context,lb.getInvoiceDate()));

            invAmount.setText(UtilConstants.removeLeadingZerowithTwoDecimal(lb.getInvoiceAmount()) +" "+(lb.getCurrency().equalsIgnoreCase("") ? "" : " " + lb.getCurrency()));

            double outAmt = Double.parseDouble(lb.getInvoiceAmount()) - (Double.parseDouble(lb.getCollectionAmount()) + Double.parseDouble(lb.getDevCollAmount()));
            invOutAmt.setText(UtilConstants.removeLeadingZerowithTwoDecimal(String.valueOf(outAmt)) +" "+(lb.getCurrency().equalsIgnoreCase("") ? "" : " " + lb.getCurrency()));

            if (outAmt >= Double.parseDouble(lb.getInvoiceAmount()))
                invOutAmt.setTextColor(Color.RED);
            else if (outAmt < Double.parseDouble(lb.getInvoiceAmount()))
                invOutAmt.setTextColor(Color.parseColor(Constants.FFDA33));
            else if (outAmt <= 0)
                invOutAmt.setTextColor(Color.GREEN);


            invBillAge.setText(String.valueOf(OutstandingHistoryActivity.getBillAge(lb)));
            v.setId(position);
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navigates to Outstanding history
                selectedList = new OutstandingBean();
                selectedList = alOutstandingsBean.get(v.getId());

                Intent toInvoiceHisdetails = new Intent(context, OutstandingDetailActivity.class);
                toInvoiceHisdetails.putExtra(Constants.CPNo, mStrCPNO);
                toInvoiceHisdetails.putExtra(Constants.RetailerName, mStrCpName);
                toInvoiceHisdetails.putExtra(Constants.CPUID, mStrCPNO);
                toInvoiceHisdetails.putExtra(Constants.CPGUID, mStrCPGUID.toUpperCase());

                toInvoiceHisdetails.putExtra(Constants.InvoiceNo, selectedList.getInvoiceNo());
                toInvoiceHisdetails.putExtra(Constants.InvoiceGUID, selectedList.getInvoiceGuid());
                toInvoiceHisdetails.putExtra(Constants.InvoiceStatus, selectedList.getInvoiceStatus());
                toInvoiceHisdetails.putExtra(Constants.DeviceStatus, selectedList.getDeviceStatus());
                toInvoiceHisdetails.putExtra(Constants.InvDate, selectedList.getInvoiceDate());
                toInvoiceHisdetails.putExtra(Constants.InvAmount, selectedList.getInvoiceAmount());
                toInvoiceHisdetails.putExtra(Constants.Currency, selectedList.getCurrency());
                toInvoiceHisdetails.putExtra(Constants.DeviceNo, selectedList.getDeviceNo());
                double collAmt = 0.0;
                try {
                    collAmt = Double.parseDouble(selectedList.getCollectionAmount()) + Double.parseDouble(selectedList.getDevCollAmount());
                } catch (NumberFormatException e) {
                    collAmt = 0.0;
                }
                toInvoiceHisdetails.putExtra(Constants.CollectionAmount, collAmt+"");

                context.startActivity(toInvoiceHisdetails);
            }
        });

        return v;
    }

    public android.widget.Filter getFilter() {
        if (filter == null) {
            filter = new OutstandingListAdapter.OutstandingInvListFilter();
        }
        return filter;
    }

    /**
     * This class search invoices based on invoice number from list.
     */
    private class OutstandingInvListFilter extends android.widget.Filter {
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
                ArrayList<OutstandingBean> filteredItems = new ArrayList<>();
                int count = InvHisOriginalValues.size();

                for (int i = 0; i < count; i++) {
                    OutstandingBean item = InvHisOriginalValues.get(i);
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
            InvHisDisplayValues = (ArrayList<OutstandingBean>) results.values; // has the filtered values
            notifyDataSetChanged();
            alOutstandingsBean = InvHisDisplayValues;
        }
    }
}
