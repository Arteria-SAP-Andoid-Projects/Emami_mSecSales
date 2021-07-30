package com.arteriatech.emami.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.returnOrder.ReturnOrderBean;

import java.util.ArrayList;

/**
 * Created by e10762 on 09-01-2017.
 *
 */




public class ReturnOrderDetailAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ReturnOrderBean stock;


    private ArrayList<ReturnOrderBean> dbStockDisplayValues = new ArrayList<ReturnOrderBean>();

    public ReturnOrderDetailAdapter(Context context, ArrayList<ReturnOrderBean> items) {

        this.context = context;

        this.dbStockDisplayValues = items;
    }

    @Override
    public int getCount() {

        return dbStockDisplayValues.size();
    }

    @Override
    public Object getItem(int arg0) {

        return null;
    }

    @Override
    public long getItemId(int arg0) {

        return 0;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup arg2) {
        if (inflater == null) {
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = inflater
                    .inflate(R.layout.item_detail_return_order, null, true);
        }
        stock = dbStockDisplayValues.get(pos);
        TextView tvSkuDesc = (TextView) view
                .findViewById(R.id.tv_list_skudesc);
        TextView tvOrderReason = (TextView) view
                .findViewById(R.id.tv_list_order_reason);
        TextView tvQty = (TextView) view
                .findViewById(R.id.tv_list_quantity);

        TextView tvMrp = (TextView) view
                .findViewById(R.id.tv_list_mrp);
        TextView tvBatch = (TextView) view
                .findViewById(R.id.tv_list_batch);
        tvSkuDesc.setText(stock.getMaterialDesc());
        tvOrderReason.setText(stock.getReturnReason());
        tvQty.setText(stock.getReturnQty());
        tvMrp.setText(stock.getReturnMrp());
        tvBatch.setText(stock.getReturnBatchNumber());


        return view;
    }




}





