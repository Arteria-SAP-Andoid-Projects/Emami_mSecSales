package com.arteriatech.emami.scheme;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.emami.msecsales.R;

/**
 * Created by e10769 on 27-03-2017.
 */

public class ViewHolderHeader extends RecyclerView.ViewHolder {
    public final TextView tvSchemeNameTitle, tvSchemeName, tvSchemeValidTo, tvSchemeType,
            tvSchemeTypeDesc, tvSchemeBenefitDesc;
    public final TableLayout llSalesAreaView;
    public final TableLayout llItemDetailsList;
    public final TableLayout llSchemeSlab;
    public final TextView tvSchemeBenefitTo;
    public final TextView tvSchemeSlabs;
    public final TextView tvSchemeValidFrom;

    public ViewHolderHeader(View itemView) {
        super(itemView);
        tvSchemeNameTitle = (TextView) itemView.findViewById(R.id.tv_scheme_name_title);
        tvSchemeName = (TextView) itemView.findViewById(R.id.tv_scheme_name);
        tvSchemeValidTo = (TextView) itemView.findViewById(R.id.tv_scheme_valid_to);
        tvSchemeValidFrom = (TextView) itemView.findViewById(R.id.tv_scheme_from_date);
        tvSchemeType = (TextView) itemView.findViewById(R.id.tv_scheme_type);
        tvSchemeTypeDesc = (TextView) itemView.findViewById(R.id.tv_scheme_type_desc);
//        tvSchemeSalesOfDesc = (TextView) itemView.findViewById(R.id.tv_scheme_sales_of_desc);
        tvSchemeBenefitDesc = (TextView) itemView.findViewById(R.id.tv_scheme_benefit_desc);
        tvSchemeBenefitTo = (TextView) itemView.findViewById(R.id.tv_scheme_benefit_to);
        tvSchemeSlabs = (TextView) itemView.findViewById(R.id.tv_scheme_slabs);
        llSalesAreaView = (TableLayout) itemView.findViewById(R.id.ll_sales_area);
        llItemDetailsList = (TableLayout) itemView.findViewById(R.id.ll_scheme_item_details_list);
        llSchemeSlab = (TableLayout) itemView.findViewById(R.id.ll_scheme_slabs);

    }
}
