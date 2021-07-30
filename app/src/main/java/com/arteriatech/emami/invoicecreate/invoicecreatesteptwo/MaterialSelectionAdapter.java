package com.arteriatech.emami.invoicecreate.invoicecreatesteptwo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.sampleDisbursement.SampleDisbursementDialogVH;

import java.util.ArrayList;

/**
 * Created by e10526 on 20-07-2018.
 */

public class MaterialSelectionAdapter extends RecyclerView.Adapter<SampleDisbursementDialogVH> {
    private ArrayList<StockBean> returnOrderBeanArrayList;
    private ArrayList<StockBean> searchReturnOrderBeanArrayList;
    private Context mContext;

    public MaterialSelectionAdapter(Context context, ArrayList<StockBean> returnOrderBeanArrayList) {
        this.mContext = context;
        this.returnOrderBeanArrayList = returnOrderBeanArrayList;
        this.searchReturnOrderBeanArrayList = new ArrayList<>();
        this.searchReturnOrderBeanArrayList.addAll(this.returnOrderBeanArrayList);
    }

    @Override
    public SampleDisbursementDialogVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drop_down_check_box_item, parent, false);
        return new SampleDisbursementDialogVH(view);
    }

    @Override
    public void onBindViewHolder(SampleDisbursementDialogVH holder, int position) {
        try {
            final StockBean retailerStockBean = searchReturnOrderBeanArrayList.get(position);
            holder.tvName.setText(retailerStockBean.getMaterialDESC());
            holder.tvSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    retailerStockBean.setSelected(isChecked);

                }
            });
            holder.tvSelected.setChecked(retailerStockBean.getSelected());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return searchReturnOrderBeanArrayList.size();
    }

    public void filter(final String text, final TextView tvEmptyRecord, final RecyclerView recyclerView,
                       final String brand, final String productCatId, final String orderMatGrpId) {
        searchReturnOrderBeanArrayList.clear();
        if (TextUtils.isEmpty(text) && TextUtils.isEmpty(brand) && TextUtils.isEmpty(productCatId) && TextUtils.isEmpty(orderMatGrpId)) {
            searchReturnOrderBeanArrayList.addAll(returnOrderBeanArrayList);
        } else {
            for (StockBean item : returnOrderBeanArrayList) {
                StockBean returnOrderBeanTwo = null;
                if (!brand.isEmpty()) {
                    if (item.getBrand().equalsIgnoreCase(brand)) {
                        returnOrderBeanTwo = item;
                    } else {
                        returnOrderBeanTwo = null;
                        continue;
                    }
                }
                if (!productCatId.isEmpty()) {
                    if (item.getProductCategoryID().equalsIgnoreCase(productCatId)) {
                        returnOrderBeanTwo = item;
                    } else {
                        returnOrderBeanTwo = null;
                        continue;
                    }
                }
                if (!orderMatGrpId.isEmpty()) {
                    if (item.getOrderMaterialGroupID().equalsIgnoreCase(orderMatGrpId)) {
                        returnOrderBeanTwo = item;
                    } else {
                        returnOrderBeanTwo = null;
                        continue;
                    }
                }
                if (!TextUtils.isEmpty(text)) {
                    if (item.getMaterialDESC().toLowerCase().contains(text.toLowerCase())) {
                        returnOrderBeanTwo = item;
                    } else {
                        returnOrderBeanTwo = null;
                        continue;
                    }
                }
                if (returnOrderBeanTwo != null) {
                    searchReturnOrderBeanArrayList.add(item);

                }


            }
        }
        notifyDataSetChanged();
        if (searchReturnOrderBeanArrayList.isEmpty()) {
            tvEmptyRecord.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyRecord.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

}
