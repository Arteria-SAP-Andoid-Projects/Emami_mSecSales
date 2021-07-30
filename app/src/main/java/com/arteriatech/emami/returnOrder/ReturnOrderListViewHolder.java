package com.arteriatech.emami.returnOrder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.emami.msecsales.R;

/**
 * Created by e10769 on 21-02-2017.
 */
public class ReturnOrderListViewHolder extends RecyclerView.ViewHolder {
    public TextView invNO,invDate,invAmount,tvStatusIndicator,tv_distance;
    public ReturnOrderListViewHolder(View itemView) {
        super(itemView);
        invNO = (TextView) itemView.findViewById(R.id.tv_in_history_no);
        invDate = (TextView) itemView.findViewById(R.id.tv_in_history_date);
        invAmount = (TextView) itemView.findViewById(R.id.tv_in_history_amt);
        tvStatusIndicator = (TextView) itemView.findViewById(R.id.tv_status_indicator);
        tv_distance = (TextView) itemView.findViewById(R.id.tv_distance);

    }
}
