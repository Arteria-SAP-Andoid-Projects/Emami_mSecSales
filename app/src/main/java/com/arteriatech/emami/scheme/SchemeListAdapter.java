package com.arteriatech.emami.scheme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.emami.msecsales.R;

import java.util.ArrayList;

/**
 * Created by e10769 on 27-03-2017.
 */

class SchemeListAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<SchemeListBean> schemeListBeanArrayList;

    public SchemeListAdapter(Context mContext, ArrayList<SchemeListBean> schemeListBeanArrayList) {
        this.mContext = mContext;
        this.schemeListBeanArrayList = schemeListBeanArrayList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scheme_list_item_header, parent, false);
        viewHolder = new ViewHolderHeader(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SchemeListBean schemeListBean = schemeListBeanArrayList.get(position);
        ViewHolderHeader viewHolderHeader = ((ViewHolderHeader) holder);

        viewHolderHeader.tvSchemeNameTitle.setText(schemeListBean.getSchemeNameTitle());
        viewHolderHeader.tvSchemeName.setText(schemeListBean.getSchemeName() + " (" + schemeListBean.getSchemeId() + ")");
        viewHolderHeader.tvSchemeValidTo.setText(schemeListBean.getValidDate());
        viewHolderHeader.tvSchemeValidFrom.setText(schemeListBean.getValidFrom());
        viewHolderHeader.tvSchemeType.setText(schemeListBean.getSchemeTypeName());
        viewHolderHeader.tvSchemeTypeDesc.setText(schemeListBean.getSchemeDesc());
//        viewHolderHeader.tvSchemeSalesOfDesc.setText(schemeListBean.getOnSaleOfCatDesc());
        viewHolderHeader.tvSchemeBenefitDesc.setText(schemeListBean.getSlabRuleDesc());
        viewHolderHeader.tvSchemeBenefitTo.setText(schemeListBean.getSlabRuleType());
        viewHolderHeader.tvSchemeSlabs.setText(schemeListBean.getSlabTitle());
        if (schemeListBean.getSalesAreaBeanArrayList() != null) {
            boolean isFirstTime = true;
            try {
                viewHolderHeader.llSalesAreaView.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (SchemeSalesAreaBean schemeSalesAreaBean : schemeListBean.getSalesAreaBeanArrayList()) {
                LinearLayout salesAreaLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.scheme_item_applicable, null, false);
//                TextView tvSchemeName = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_name);
                TextView tvSchemeDesc = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_desc);
                View vLines = (View) salesAreaLayout.findViewById(R.id.view_line);
                if (isFirstTime) {
                    vLines.setVisibility(View.GONE);
                    isFirstTime = false;
                } else {
                    vLines.setVisibility(View.VISIBLE);
                }
                tvSchemeDesc.setText(schemeSalesAreaBean.getFinalGroupDesc());
                viewHolderHeader.llSalesAreaView.addView(salesAreaLayout);
            }
        }
        try {
            viewHolderHeader.llItemDetailsList.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (schemeListBean.getItemListBeanArrayList() != null) {
            for (SchemeItemListBean schemeItemListBean : schemeListBean.getItemListBeanArrayList()) {
                LinearLayout salesAreaLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.scheme_list_item_sale_of, null, false);
                TextView tvSchemeDesc = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_desc);
                TextView tvSchemeTo = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_to);
                tvSchemeDesc.setText(schemeItemListBean.getOnSalesDesc());
                if (!schemeItemListBean.getUOM().equalsIgnoreCase(""))
                    tvSchemeTo.setText(schemeItemListBean.getItemMin() + " " + schemeItemListBean.getUOM());
                else
                    tvSchemeTo.setText(schemeItemListBean.getItemMin());
                viewHolderHeader.llItemDetailsList.addView(salesAreaLayout);
            }
        }
        if (schemeListBean.getSchemeSlabBeanArrayList() != null) {
            boolean isFirstTime = true;
            try {
                viewHolderHeader.llSchemeSlab.removeAllViews();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (SchemeSlabBean schemeSlabBean : schemeListBean.getSchemeSlabBeanArrayList()) {
                LinearLayout salesAreaLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.scheme_list_item_sub_header, null, false);
                TextView tvSchemeName = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_name);
                TextView tvSchemeDesc = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_desc);
                TextView tvSchemeTo = (TextView) salesAreaLayout.findViewById(R.id.tv_scheme_to);
//                if (isFirstTime) {
//                    tvSchemeName.setText(schemeListBean.getSlabTitle());
                tvSchemeName.setText(schemeSlabBean.getToQty());
//                    isFirstTime = false;
//                }
                tvSchemeDesc.setText(schemeSlabBean.getMaterialDesc());
                tvSchemeTo.setText(schemeSlabBean.getPayoutAmount());
                viewHolderHeader.llSchemeSlab.addView(salesAreaLayout);
            }
        }


    }

    @Override
    public int getItemCount() {
        return schemeListBeanArrayList.size();
    }
}
