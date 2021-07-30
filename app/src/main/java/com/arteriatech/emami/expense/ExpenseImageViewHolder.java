package com.arteriatech.emami.expense;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.arteriatech.emami.msecsales.R;

/**
 * Created by e10769 on 04-03-2017.
 */
public class ExpenseImageViewHolder extends RecyclerView.ViewHolder{
    public ImageView ivThumb;
    public ExpenseImageViewHolder(View itemView) {
        super(itemView);
        ivThumb=(ImageView)itemView.findViewById(R.id.imageView);
        itemView.setClickable(true);
    }
}