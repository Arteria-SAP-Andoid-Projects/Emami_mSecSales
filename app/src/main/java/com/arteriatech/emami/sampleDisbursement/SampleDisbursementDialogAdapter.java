package com.arteriatech.emami.sampleDisbursement;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.retailerStock.RetailerStockBean;

import java.util.ArrayList;

/**
 * Created by e10769 on 23-03-2017.
 */

public class SampleDisbursementDialogAdapter extends RecyclerView.Adapter<SampleDisbursementDialogVH> {
    private ArrayList<RetailerStockBean> retailerStockBeanArrayList;
    private ArrayList<RetailerStockBean> searchStockBeanList;
    private Context mContext;
    private int displayType = 0;

    public SampleDisbursementDialogAdapter(Context context, ArrayList<RetailerStockBean> retailerStockBeanArrayList, int displayType) {
        this.mContext = context;
        this.retailerStockBeanArrayList = retailerStockBeanArrayList;
        this.searchStockBeanList = new ArrayList<>();
        this.searchStockBeanList.addAll(this.retailerStockBeanArrayList);
        this.displayType = displayType;
    }

    @Override
    public SampleDisbursementDialogVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drop_down_check_box_item, parent, false);
        return new SampleDisbursementDialogVH(view);
    }

    @Override
    public void onBindViewHolder(SampleDisbursementDialogVH holder, int position) {
        try {
            final RetailerStockBean retailerStockBean = searchStockBeanList.get(position);
            if (displayType == 1) {
                holder.tvName.setText(retailerStockBean.getMaterialDesc());
            } else {
                holder.tvName.setText(retailerStockBean.getOrderMaterialGroupDesc());
            }
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
        return searchStockBeanList.size();
    }

    public void filter(final String text, final TextView tvEmptyRecord, final RecyclerView recyclerView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchStockBeanList.clear();
                if (TextUtils.isEmpty(text)) {
                    searchStockBeanList.addAll(retailerStockBeanArrayList);
                } else {
                    for (RetailerStockBean item : retailerStockBeanArrayList) {
                        if (displayType == 1) {
                            if (item.getMaterialDesc().toLowerCase().contains(text.toLowerCase())) {
                                searchStockBeanList.add(item);
                            }
                        } else {
                            if (item.getOrderMaterialGroupDesc().toLowerCase().contains(text.toLowerCase())) {
                                searchStockBeanList.add(item);
                            }
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

    public void filterSampleDisbursement(final String text, final TextView tvEmptyRecord, final RecyclerView recyclerView,
                                         final String brand, final String orderMatGrpId) {
      /*  new Thread(new Runnable() {
            @Override
            public void run() {*/
                searchStockBeanList.clear();
                if (TextUtils.isEmpty(text) && TextUtils.isEmpty(brand) && TextUtils.isEmpty(orderMatGrpId)) {
                    searchStockBeanList.addAll(retailerStockBeanArrayList);
                } else {
                    for (RetailerStockBean item : retailerStockBeanArrayList) {
//                        if(displayType==1) {
                        RetailerStockBean retailerStockBean = null;
                        if (!brand.isEmpty()) {
                            if (item.getBrand().equalsIgnoreCase(brand)) {
                                retailerStockBean = item;
                            } else {
                                retailerStockBean = null;
                                continue;
                            }
                        }
                        if (!TextUtils.isEmpty(orderMatGrpId)) {
                            if (item.getOrderMaterialGroupID().equalsIgnoreCase(orderMatGrpId)) {
                                retailerStockBean = item;
                            } else {
                                retailerStockBean = null;
                                continue;
                            }
                        }
                        if (!TextUtils.isEmpty(text)) {
                            if (item.getMaterialDesc().toLowerCase().contains(text.toLowerCase())) {
                                retailerStockBean = item;
                            } else {
                                retailerStockBean = null;
                                continue;
                            }
                        }
                        if (retailerStockBean != null) {
                            searchStockBeanList.add(item);

                        }

                    }
                }
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                        notifyDataSetChanged();
                        if (searchStockBeanList.isEmpty()) {
                            tvEmptyRecord.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvEmptyRecord.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
//                    }
//                });

//            }
      /*  }).start();*/

    }
}
