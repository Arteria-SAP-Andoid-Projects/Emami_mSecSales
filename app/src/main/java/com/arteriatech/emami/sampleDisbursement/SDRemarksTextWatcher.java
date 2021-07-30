package com.arteriatech.emami.sampleDisbursement;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.retailerStock.RetailerStockBean;

import java.util.List;

/**
 * Created by e10769 on 18-02-2017.
 */

public class SDRemarksTextWatcher implements TextWatcher {
    private int position;
    private List<RetailerStockBean> retailerStockBeanList;
    private EditText remarks;

    public SDRemarksTextWatcher(List<RetailerStockBean> retailerStockBeanList) {
        this.retailerStockBeanList = retailerStockBeanList;
    }

    public void updatePosition(int position, EditText remarks) {
        this.position = position;
        this.remarks = remarks;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        retailerStockBeanList.get(position).setRemarks(charSequence.toString());
        if (!charSequence.toString().isEmpty()) {
            remarks.setBackgroundResource(R.drawable.edittext);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }
}
