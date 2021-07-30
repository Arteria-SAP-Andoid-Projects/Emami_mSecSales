package com.arteriatech.emami.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;

import java.util.ArrayList;

/**
 * Created by e10526 on 2/3/2017.
 *
 */

public class BehaviourListAdapter extends ArrayAdapter<CustomerBean> {
    private ArrayList<CustomerBean> retDisplayValues;
    private Context context;
    private String mStrBehaviuorTypeCode;

    public BehaviourListAdapter(Context context, ArrayList<CustomerBean> items,String mStrBehaviuorTypeCode) {
        super(context, R.layout.behaviour_list_item, items);
        this.retDisplayValues = items;
        this.context = context;
        this.mStrBehaviuorTypeCode = mStrBehaviuorTypeCode;
    }

    @Override
    public int getCount() {
        return this.retDisplayValues != null ? this.retDisplayValues.size() : 0;
    }

    @Override
    public CustomerBean getItem(int item) {
        CustomerBean retListBean;
        retListBean = this.retDisplayValues != null ? this.retDisplayValues.get(item) : null;
        return retListBean;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.behaviour_list_item, parent, false);
        }


        TextView tv_serial_no = (TextView) view.findViewById(R.id.tv_serial_no);
        TextView tvRetailerName = (TextView) view.findViewById(R.id.tv_RetailerName);
        TextView tv_mtd_value = (TextView) view.findViewById(R.id.tv_mtd_value);
        TextView tv_Retailer_no = (TextView) view.findViewById(R.id.tv_Retailer_no);

        if(mStrBehaviuorTypeCode.equalsIgnoreCase(Constants.NotPurchasedType)){

            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT,1f);
            llp.setMargins(1, 0, 1, 1); // llp.setMargins(left, top, right, bottom);
            tv_serial_no.setLayoutParams(llp);


            tv_mtd_value.setVisibility(View.GONE);
        }else{
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(20, ViewGroup.LayoutParams.MATCH_PARENT,1f);
            llp.setMargins(1, 0, 1, 1); // llp.setMargins(left, top, right, bottom);
            tv_serial_no.setLayoutParams(llp);
            tv_mtd_value.setVisibility(View.VISIBLE);
        }

        CustomerBean retailerListBean = retDisplayValues.get(position);

        tvRetailerName.setText(retailerListBean.getRetailerName());

        tv_Retailer_no.setText(context.getString(R.string.str_concat_two_texts_colon,
                context.getString(R.string.lbl_uid), retailerListBean.getUID()));

        tv_mtd_value.setText(UtilConstants.removeLeadingZerowithTwoDecimal(retailerListBean.getMtdValue()));

        tv_serial_no.setText((position+1)+"");

        view.setId(position);
        return view;
    }





}