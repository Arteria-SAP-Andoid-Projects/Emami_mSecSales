package com.arteriatech.emami.expense;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.interfaces.OnClickInterface;
import com.arteriatech.emami.msecsales.R;

import java.util.ArrayList;

/**
 * Created by e10769 on 07-03-2017.
 */
public class AddExpenseImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private ArrayList<ExpenseImageBean> imageBeanArrayList;
    private int ITEM_TYPE_HEADER=1;
    private int ITEM_TYPE_NORMAL=2;
    private OnClickInterface onClickInterface=null;
    public AddExpenseImageAdapter(Context mContext, ArrayList<ExpenseImageBean> imageBeanList) {
        this.mContext=mContext;
        this.imageBeanArrayList=imageBeanList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_NORMAL) {
            View normalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_expense_image_item, null);
            return new ExpenseImageViewHolder(normalView);
        } else {
            View headerRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_expense_header_item, null);
            return new ExpenseImageHeaderViewHolder(headerRow);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ExpenseImageViewHolder) {
            final ExpenseImageBean expenseImageBean = imageBeanArrayList.get(position);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            Bitmap bitmap = BitmapFactory.decodeFile(expenseImageBean.getImagePath(), options);
            ((ExpenseImageViewHolder) holder).ivThumb.setImageBitmap(bitmap);
            ((ExpenseImageViewHolder) holder).ivThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constants.openImageInGallery(mContext, expenseImageBean.getImagePath());
                }
            });
        }else if(holder instanceof ExpenseImageHeaderViewHolder){
            ((ExpenseImageHeaderViewHolder) holder).ivThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onClickInterface!=null){
                        onClickInterface.onItemClick(v,position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        ExpenseImageBean expenseImageBean = imageBeanArrayList.get(position);
        if (expenseImageBean.getImagePath().equals("")) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_NORMAL;
        }
    }


    @Override
    public int getItemCount() {
        return imageBeanArrayList.size();
    }

    public void onImageAddClick(OnClickInterface onClickInterface){
        this.onClickInterface=onClickInterface;
    }
}
