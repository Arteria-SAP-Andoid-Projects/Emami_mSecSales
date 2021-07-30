package com.arteriatech.emami.returnOrder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.emami.msecsales.R;

/**
 * Created by e10769 on 31-03-2017.
 */

class ROReviewViewHolder extends RecyclerView.ViewHolder{
    public final TextView item_dbstk_sku_desc;
    public final TextView item_dbstk_selected_desc;
    public final TextView tvQuantity;
    public final TextView tvMrp;
    public final TextView tvBatchNumber;

    public ROReviewViewHolder(View itemView) {
        super(itemView);
        item_dbstk_sku_desc = (TextView)itemView.findViewById(R.id.item_dbstk_sku_desc);
        item_dbstk_selected_desc = (TextView)itemView.findViewById(R.id.item_dbstk_selected_desc);
        tvQuantity = (TextView)itemView.findViewById(R.id.edit_quantity);
        tvMrp= (TextView)itemView.findViewById(R.id.edit_mrp);
        tvBatchNumber= (TextView)itemView.findViewById(R.id.edit_batch_number);
    }
}
