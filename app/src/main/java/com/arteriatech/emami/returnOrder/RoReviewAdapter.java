package com.arteriatech.emami.returnOrder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arteriatech.emami.msecsales.R;

import java.util.ArrayList;

/**
 * Created by e10769 on 31-03-2017.
 */

class RoReviewAdapter extends RecyclerView.Adapter<ROReviewViewHolder>{
    private ArrayList<ReturnOrderBean> filteredArrayList;
    private Context mContext;
    public RoReviewAdapter(Context mContext, ArrayList<ReturnOrderBean> filteredArrayList) {
        this.mContext=mContext;
        this.filteredArrayList=filteredArrayList;
    }

    @Override
    public ROReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ro_review_item,parent,false);
        return new ROReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ROReviewViewHolder holder, int position) {
        ReturnOrderBean returnOrderBean = filteredArrayList.get(position);
        holder.item_dbstk_sku_desc.setText(returnOrderBean.getMaterialDesc());
        holder.item_dbstk_selected_desc.setText(returnOrderBean.getReturnDesc());
        if(!returnOrderBean.getUom().equalsIgnoreCase(""))
            holder.tvQuantity.setText(returnOrderBean.getReturnQty()+" " +returnOrderBean.getUom());
        else
            holder.tvQuantity.setText(String.format("%.3f", Double.parseDouble(returnOrderBean.getReturnQty())));
        holder.tvMrp.setText(returnOrderBean.getReturnMrp());
        holder.tvBatchNumber.setText(returnOrderBean.getReturnBatchNumber().toUpperCase());
    }

    @Override
    public int getItemCount() {
        return filteredArrayList.size();
    }
}
