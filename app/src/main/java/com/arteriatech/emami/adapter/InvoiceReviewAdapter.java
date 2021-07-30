package com.arteriatech.emami.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by e10742 on 7/31/2017.
 */

public class InvoiceReviewAdapter extends RecyclerView.Adapter<InvoiceReviewAdapter.ViewHolder> {
    private ArrayList<HashMap<String, String>> alInvProdList = new ArrayList<>();
    private Context context;
    private TextView tvEmptyListLay;

    public InvoiceReviewAdapter(ArrayList<HashMap<String, String>> alInvProdList, Context context, TextView tvEmptyListLay) {
        this.alInvProdList = alInvProdList;
        this.context = context;
        this.tvEmptyListLay = tvEmptyListLay;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_invoice_review,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final HashMap<String, String> productListItem = alInvProdList.get(position);

        viewHolder.tvItemDesc.setText(productListItem.get(Constants.MatDesc));
        viewHolder.tvMatNo.setText(productListItem.get(Constants.MatCode));
//        viewHolder.tvStockQty.setText(String.valueOf(productListItem.getStockQty()) + " " + productListItem.getUOM());
        viewHolder.tvStockQty.setText(productListItem.get(Constants.UnrestrictedQty) + " " + productListItem.get(Constants.UOM));
        viewHolder.tvItemQty.setText(String.valueOf(productListItem.get(Constants.Qty)) + " " + productListItem.get(Constants.UOM));

//        viewHolder.tvItemDiscount.setText((UtilConstants.removeLeadingZerowithTwoDecimal(
//                String.valueOf(productListItem.getDiscountAmt())) + " " + productListItem.getCurrency()).trim());
//        viewHolder.tvItemTax.setText((UtilConstants.removeLeadingZerowithTwoDecimal(
//                String.valueOf(productListItem.getTaxAmt())) + " " + productListItem.getCurrency()).trim());
//        viewHolder.tvItemTax.setText((UtilConstants.removeLeadingZerowithTwoDecimal(
//                String.valueOf(productListItem.getTaxAmt())) + " " + productListItem.getCurrency()).trim());
//        viewHolder.tvItemFreight.setText((UtilConstants.removeLeadingZerowithTwoDecimal(
//                String.valueOf(productListItem.getFreight())) + " " + productListItem.getCurrency()).trim());
//        viewHolder.tvItemTotal.setText((UtilConstants.removeLeadingZerowithTwoDecimal(
//                String.valueOf(productListItem.getInvoiceAmt())) + " " + productListItem.getCurrency()).trim());
        viewHolder.tvUnitPrice.setText((UtilConstants.removeLeadingZerowithTwoDecimal(
                String.valueOf(productListItem.get(Constants.UnitPrice))) + " " +productListItem.get(Constants.Currency)));
        if(productListItem.get(Constants.UnitPrice).length()>6)
            viewHolder.tvUnitPrice.setTextSize(R.dimen.small_text);
//        totalAmt = totalAmt + productListItem.getInvoiceAmt();
//        tvTotalOrderVal.setText((UtilConstants.removeLeadingZerowithTwoDecimal(String.valueOf(totalAmt))
//                + " " + productListItem.getCurrency()).trim());

//        viewHolder.tvItemNo.setText(String.valueOf(position + 1));

//        if(checkSerialNo(productListItem.get(Constants.StockGuid))){
//            viewHolder.ivSNo.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public int getItemCount() {
        if (alInvProdList.size() == 0) {
            tvEmptyListLay.setVisibility(View.VISIBLE);
        } else {
            tvEmptyListLay.setVisibility(View.GONE);
        }
        return alInvProdList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemDesc = null, tvItemQty = null, tvUnitPrice = null, tvItemDiscount = null, tvItemTax = null,
                tvItemTotal = null, tvItemFreight = null, tvItemNo = null, tvMatNo = null, tvStockQty = null;
        ImageView ivSNo = null;

        public ViewHolder(View view) {
            super(view);
            tvItemDesc = (TextView) view.findViewById(R.id.tv_invoice_matdesc);
            tvMatNo = (TextView) view.findViewById(R.id.tv_invoice_matcode);
            tvItemQty = (TextView) view.findViewById(R.id.tv_inv_qty);
            tvStockQty = (TextView) view.findViewById(R.id.tv_invoice_last_qty);
//            tvItemDiscount = (TextView) view.findViewById(R.id.tv_item_so_review_discount);
//            tvItemTax = (TextView) view.findViewById(R.id.tv_item_so_review_tax_amt);
//            tvItemTotal = (TextView) view.findViewById(R.id.tv_item_so_review_net_amt);
            tvUnitPrice = (TextView) view.findViewById(R.id.tv_mat_rate);
            ivSNo = (ImageView) view.findViewById(R.id.iv_invoice_serial_no_sel);
//            tvItemFreight = (TextView) view.findViewById(R.id.tv_item_so_review_freight_amt);
//            tvItemNo = (TextView) view.findViewById(R.id.tv_item_so_review_item_no);
        }
    }

    public boolean checkSerialNo(String spStockItemGuid) {
        String mStrVisitStartedQry = Constants.CPStockItems
                + "?$filter=" + Constants.CPStockItemGUID + " eq guid'"
                + spStockItemGuid.toUpperCase() + "' and " + Constants.BatchOrSerial + " eq 'X'";
        try {
            // ToDo serial numbers available or not validation
            if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)) {
                return true;
            }
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        return false;
    }
}
