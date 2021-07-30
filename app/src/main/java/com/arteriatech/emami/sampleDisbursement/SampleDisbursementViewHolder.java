package com.arteriatech.emami.sampleDisbursement;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arteriatech.emami.msecsales.R;

/**
 * Created by e10769 on 17-02-2017.
 */
public class SampleDisbursementViewHolder extends RecyclerView.ViewHolder {
    public final TextView tvMaterialDesc;
    public final ImageButton ibDelete;
    public final EditText etRemarks;
    public TextView tvMaterailName, tvDBStock;
    public EditText edMaterialQty;
    public SampleDisbursementTextWatcher sampleDisbursementTextWatcher;
    public SDRemarksTextWatcher remarksTextWatcher;

    public SampleDisbursementViewHolder(View itemView, SampleDisbursementTextWatcher sampleDisbursementTextWatcher,SDRemarksTextWatcher remarksTextWatcher) {
        super(itemView);
        tvMaterailName = (TextView) itemView.findViewById(R.id.tv_material_name);
        tvDBStock = (TextView) itemView.findViewById(R.id.tv_db_stk);
        tvMaterialDesc = (TextView) itemView.findViewById(R.id.tv_material_desc);
        edMaterialQty = (EditText) itemView.findViewById(R.id.et_material_qty);
        etRemarks = (EditText) itemView.findViewById(R.id.et_remarks);
        ibDelete = (ImageButton) itemView.findViewById(R.id.ib_delete_item);


        this.sampleDisbursementTextWatcher = sampleDisbursementTextWatcher;
        this.remarksTextWatcher = remarksTextWatcher;
        edMaterialQty.addTextChangedListener(sampleDisbursementTextWatcher);
        etRemarks.addTextChangedListener(remarksTextWatcher);
    }
}
