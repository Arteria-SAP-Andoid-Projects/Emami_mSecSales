package com.arteriatech.emami.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.dbstock.DBStockBean;
import com.arteriatech.emami.msecsales.R;

import java.util.ArrayList;

/**
 * Created by e10762 on 09-01-2017.
 *
 */




public class DbStockDetailAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    DBStockBean stock;


    private ArrayList<DBStockBean> dbStockDisplayValues = new ArrayList<DBStockBean>();

    public DbStockDetailAdapter(Context context, ArrayList<DBStockBean> items) {

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
                    .inflate(R.layout.item_detail_dbstock, null, true);
        }
        stock = dbStockDisplayValues.get(pos);

        TextView tv_mat_desc = (TextView) view
                .findViewById(R.id.tv_mat_desc);
        TextView tv_mat_code = (TextView) view
                .findViewById(R.id.tv_mat_code);

        TextView tvBatch = (TextView) view
                .findViewById(R.id.item_detail_dbstk_batch);
        TextView tvMFD = (TextView) view
                .findViewById(R.id.item_detail_dbstk_mfd);
        TextView tvQuantity = (TextView) view
                .findViewById(R.id.item_detail_dbstk_quantity);

        TextView tvMRP = (TextView) view
                .findViewById(R.id.item_dbstk_mrp);
        TextView tvRetPrice = (TextView) view
                .findViewById(R.id.item_dbstk_ret_price);

        tv_mat_desc.setText(stock.getMaterialDesc());
        tv_mat_code.setText(stock.getMaterialNo());
        tvBatch.setText(stock.getBatch());
        tvMFD.setText(stock.getMFD());
        tvQuantity.setText(stock.getQAQty()+" "+stock.getUom());
        Double mDouRetLanPriceCal= 0.0;
        try {
            mDouRetLanPriceCal = Double.parseDouble(stock.getFirstMrpLandingPrice())/Double.parseDouble(stock.getFirstMrpQty());
        } catch (NumberFormatException e) {
            mDouRetLanPriceCal = 0.0;
        }
        if(mDouRetLanPriceCal.isNaN() || mDouRetLanPriceCal.isInfinite()){
            mDouRetLanPriceCal = 0.0;
        }

        tvMRP.setText(UtilConstants.removeLeadingZerowithTwoDecimal(stock.getMRP())+" "+stock.getCurrency());
        tvRetPrice.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mDouRetLanPriceCal.toString())+" "+stock.getCurrency());

        return view;
    }




}





