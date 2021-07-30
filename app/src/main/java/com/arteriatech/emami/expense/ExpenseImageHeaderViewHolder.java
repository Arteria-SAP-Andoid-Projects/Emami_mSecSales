package com.arteriatech.emami.expense;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.arteriatech.emami.msecsales.R;

/**
 * Created by e10769 on 07-03-2017.
 */

public class ExpenseImageHeaderViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivThumb;
    public ExpenseImageHeaderViewHolder(View itemView) {
        super(itemView);
        ivThumb=(ImageView)itemView.findViewById(R.id.imageView);
        itemView.setClickable(true);
    }
}
