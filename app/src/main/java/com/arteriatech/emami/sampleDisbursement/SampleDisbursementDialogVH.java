package com.arteriatech.emami.sampleDisbursement;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.arteriatech.emami.msecsales.R;

/**
 * Created by e10769 on 23-03-2017.
 */

public class SampleDisbursementDialogVH extends RecyclerView.ViewHolder{
    public CheckBox tvSelected;
    public TextView tvName;
    public SampleDisbursementDialogVH(View itemView) {
        super(itemView);
        tvName = (TextView) itemView.findViewById(R.id.tv_dropdown);
        tvSelected = (CheckBox) itemView.findViewById(R.id.cb_mat_grp_sel);
    }
}
