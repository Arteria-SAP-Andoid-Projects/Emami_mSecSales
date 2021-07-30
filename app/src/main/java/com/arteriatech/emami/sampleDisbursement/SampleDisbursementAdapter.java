package com.arteriatech.emami.sampleDisbursement;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.interfaces.FocusOnTextChangeInterface;
import com.arteriatech.emami.interfaces.OnClickInterface;
import com.arteriatech.emami.interfaces.TextWatcherInterface;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.retailerStock.RetailerStockBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10769 on 17-02-2017.
 */

public class SampleDisbursementAdapter extends RecyclerView.Adapter<SampleDisbursementViewHolder> {
    private static int lastSelectedEditText = 0;
    SampleDisbursementViewHolder holderTemp;
    private Context mContext;
    private List<RetailerStockBean> retailerStockBeanList, searchStockBeanList;
    private TextWatcherInterface textWatcherInterface = null;
    private OnClickInterface onClickInterface = null;
    private FocusOnTextChangeInterface focusOnTextChangeInterface = null;

    public SampleDisbursementAdapter(Context context, List<RetailerStockBean> retailerStockBeanList) {
        this.mContext = context;
        this.retailerStockBeanList = retailerStockBeanList;
        this.searchStockBeanList = new ArrayList<>();
        this.searchStockBeanList.addAll(this.retailerStockBeanList);
    }

    @Override
    public SampleDisbursementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_collection_item, parent, false);
        return new SampleDisbursementViewHolder(view, new SampleDisbursementTextWatcher(searchStockBeanList, textWatcherInterface), new SDRemarksTextWatcher(searchStockBeanList));
    }

    @Override
    public void onBindViewHolder(final SampleDisbursementViewHolder holder, final int position) {
        final RetailerStockBean retailerStockBean = searchStockBeanList.get(position);
        holder.tvMaterailName.setText(retailerStockBean.getOrderMaterialGroupDesc());
        holder.tvDBStock.setText(retailerStockBean.getUnrestrictedQty() + " " + retailerStockBean.getUom());
        holder.tvMaterialDesc.setText(retailerStockBean.getMaterialDesc());
        holder.sampleDisbursementTextWatcher.updatePosition(position, holder.edMaterialQty);
        holder.edMaterialQty.setText(retailerStockBean.getQAQty());
        holder.edMaterialQty.setVisibility(View.VISIBLE);
        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickInterface != null) {
                    onClickInterface.onItemClick(v, position);
                }
            }
        });
        holder.edMaterialQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (focusOnTextChangeInterface != null) {
                    focusOnTextChangeInterface.onTextChange(v, hasFocus, position, holder.edMaterialQty);
                }
            }
        });
        holder.edMaterialQty.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (focusOnTextChangeInterface != null) {
                    focusOnTextChangeInterface.setOnTouch(v, position);
                    Constants.setCursorPostion(holder.edMaterialQty,v,event);
                }
                return true;
            }
        });
        UtilConstants.editTextDecimalFormat(holder.edMaterialQty, 13, 3);
        holderTemp = holder;
        holder.remarksTextWatcher.updatePosition(position, holder.etRemarks);
        holder.etRemarks.setText(retailerStockBean.getRemarks());
        holder.ibDelete.setVisibility(View.VISIBLE);
    }

    public void textWatcher(TextWatcherInterface textWatcherInterface) {
        this.textWatcherInterface = textWatcherInterface;
    }

    public void onClickDeleteItemListener(OnClickInterface onClickInterface) {
        this.onClickInterface = onClickInterface;
    }

    public void onFocusOn(FocusOnTextChangeInterface focusOnTextChangeInterface) {
        this.focusOnTextChangeInterface = focusOnTextChangeInterface;
    }


    @Override
    public int getItemCount() {
        return searchStockBeanList.size();
    }

    /*search filter*/
    public void filter(final String text, final TextView tvEmptyRecord, final RecyclerView recyclerView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchStockBeanList.clear();
                if (TextUtils.isEmpty(text)) {
                    searchStockBeanList.addAll(retailerStockBeanList);
                } else {
                    for (RetailerStockBean item : retailerStockBeanList) {

                        if (item.getMaterialDesc().toLowerCase().contains(text.toLowerCase())) {
                            searchStockBeanList.add(item);
                        }

                    }
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        if (searchStockBeanList.isEmpty()) {
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
