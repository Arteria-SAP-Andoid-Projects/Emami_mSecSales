package com.arteriatech.emami.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.dbstock.DBStockDetails;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.returnOrder.ReturnOrderBean;

import java.util.ArrayList;

/**
 * Created by e10762 on 14-02-2017.
 */

public class ReturnOrderAdapter extends BaseAdapter {


        Context context;
        LayoutInflater inflater;
        private ReturnOrderBean stock;
        private String[][] mArrayReasons;

        private ArrayList<ReturnOrderBean> dbStockOriginalValues = new ArrayList<>();
        private ArrayList<ReturnOrderBean> dbStockDisplayValues = new ArrayList<>();

    public ReturnOrderAdapter(Context context, ArrayList<ReturnOrderBean> items, String[][] mArrayReasons) {

            this.context = context;
            this.dbStockOriginalValues = items;
            this.dbStockDisplayValues = items;
            this.mArrayReasons = mArrayReasons;
        }

        @Override
        public int getCount() {
            return dbStockDisplayValues.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int pos, View convertView, ViewGroup arg2) {
             final ViewHolder holder;


            if (convertView == null) {
                holder = new ViewHolder();
                if (inflater == null) {

                    inflater = (LayoutInflater) context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                }

                convertView = inflater
                        .inflate(R.layout.item_return_order, null, true);
                holder.tvSkuDesc = (TextView) convertView
                        .findViewById(R.id.item_dbstk_sku_desc);
                holder.edQty = (EditText)convertView.findViewById(R.id.edit_quantity);
                holder.edBatch = (EditText)convertView.findViewById(R.id.edit_batch_number);
                holder.edMRP = (EditText)convertView.findViewById(R.id.edit_mrp);
                holder.spReason = (Spinner)convertView.findViewById(R.id.sp_select_reason);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ref = pos;
            stock = dbStockDisplayValues.get(pos);
            holder.tvSkuDesc.setText(stock.getMaterialDesc());
             ArrayAdapter<String> spOrderReasonAdapter = new ArrayAdapter<>(context,R.layout.custom_textview,mArrayReasons[1]);
            spOrderReasonAdapter.setDropDownViewResource(R.layout.spinnerinside);
            holder.spReason.setAdapter(spOrderReasonAdapter);
            holder.spReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    dbStockDisplayValues.get(holder.ref).setReturnReason(mArrayReasons[0][i]);
                    dbStockDisplayValues.get(holder.ref).setReturnDesc(mArrayReasons[1][i]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            holder.edMRP.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    dbStockDisplayValues.get(holder.ref).setReturnMrp(editable.toString());
                }
            });
            holder.edQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    dbStockDisplayValues.get(holder.ref).setReturnQty(editable.toString());
                }
            });
            holder.edBatch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    dbStockDisplayValues.get(holder.ref).setReturnBatchNumber(editable.toString());
                }
            });




            return convertView;
        }


    public ArrayList<ReturnOrderBean> newReturnList()
    {
        return dbStockDisplayValues;
    }

    private void goToDbStockDetails(int pos)
        {
            Intent intent =  new Intent(context,DBStockDetails.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.MaterialNo,dbStockDisplayValues.get(pos).getMaterialNo());
            intent.putExtra(Constants.QAQty,dbStockDisplayValues.get(pos).getQAQty());
            intent.putExtra(Constants.UOM,dbStockDisplayValues.get(pos).getUom());
            intent.putExtra(Constants.MaterialDesc,dbStockDisplayValues.get(pos).getMaterialDesc());
            intent.putExtra(Constants.ManufacturingDate,dbStockDisplayValues.get(pos).getMFD());
            intent.putExtra(Constants.CPStockItemGUID,dbStockDisplayValues.get(pos).getCPStockItemGUID());
            context.startActivity(intent);

        }
    private class ViewHolder {
        TextView tvSkuDesc;
        EditText edQty,edMRP,edBatch;
        Spinner spReason;
        int ref;
    }




    }

