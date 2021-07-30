package com.arteriatech.emami.returnOrder;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.interfaces.OnClickInterface;
import com.arteriatech.emami.msecsales.R;

import java.util.List;

/**
 * Created by e10769 on 21-02-2017.
 */

public class ReturnOrderListAdapter extends RecyclerView.Adapter<ReturnOrderListViewHolder> {
    private final Context mContext;
    private List<ReturnOrderBean> returnOrderBeanList, returnOrderBeanSearchList;
    private OnClickInterface onClickInterface = null;

    public ReturnOrderListAdapter(Context mContext, List<ReturnOrderBean> returnOrderBeanList, List<ReturnOrderBean> returnOrderBeanSearchList) {
        this.returnOrderBeanList = returnOrderBeanList;
        this.mContext = mContext;
        this.returnOrderBeanSearchList = returnOrderBeanSearchList;
        this.returnOrderBeanSearchList.addAll(this.returnOrderBeanList);
    }

    @Override
    public ReturnOrderListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_history_item_list, parent, false);
        return new ReturnOrderListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReturnOrderListViewHolder holder, final int position) {
        ReturnOrderBean returnOrderBean = returnOrderBeanSearchList.get(position);
        holder.invNO.setText(returnOrderBean.getOrderDate());
        holder.invDate.setText(returnOrderBean.getOrderNo());
        holder.tv_distance.setText(returnOrderBean.getDistance());
        holder.invAmount.setText(UtilConstants.removeLeadingZerowithTwoDecimal(returnOrderBean.getNetAmount())+" "+returnOrderBean.getCurrency());
        if (returnOrderBean.getStatusID().equals("000001")) {
            holder.tvStatusIndicator.setBackgroundResource(R.color.RED);
        } else {
            holder.tvStatusIndicator.setBackgroundResource(android.R.color.transparent);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickInterface != null) {
                    onClickInterface.onItemClick(v, position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return returnOrderBeanSearchList.size();
    }

    public void onItemClick(OnClickInterface onClickInterface) {
        this.onClickInterface = onClickInterface;
    }

    /*search filter*/
    public void filter(final String text, final TextView tvEmptyRecord, final RecyclerView recyclerView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                returnOrderBeanSearchList.clear();
                if (TextUtils.isEmpty(text)) {
                    returnOrderBeanSearchList.addAll(returnOrderBeanList);
                } else {
                    for (ReturnOrderBean item : returnOrderBeanList) {

                        if (item.getOrderNo().toLowerCase().contains(text.toLowerCase())) {
                            returnOrderBeanSearchList.add(item);
                        }

                    }
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        if (returnOrderBeanSearchList.isEmpty()) {
                            tvEmptyRecord.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvEmptyRecord.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        }).start();

    }
}
