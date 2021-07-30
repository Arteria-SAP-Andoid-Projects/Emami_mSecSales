package com.arteriatech.emami.reports;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by e10604 on 27/4/2016.
 */
public class InvoiceDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private String mStrBundleRetName = "", mStrBundleRetID = "";
    private String mStrBundleInvoiceNo = "", mStrBundleCPGUID = "";
    private String mStrBundleInvoiceGuid = "", mStrStatus = "", mStrInvDate = "", mStrInvAmount = "",
            mStrBundleDeviceStatus = "", mStrDeviceNo = "", mStrCollAmount = "", mStrPendingAmount = "",
            mStrInvAmountCurr = "", mCollectionAmount = "";

    //new 28112016
    private String mStrBundleRetUID = "";


    TextView tv_invoice_document_number;
    TextView tv_inv_date;
    private ArrayList<InvoiceHistoryBean> alInvoiceBean;
    private LinearLayout llDetailLayout;
    boolean flag = true;
    int cursorLength = 0;
    TextView[] matDesc, matCode, netAmount, invQty, itemNo;
    TextView[] matDesc_ex, matCode_ex, netAmount_ex, invQty_ex, itemNo_ex;
    ImageView iv_expand_icon;



    //new
    TextView tvBillValue = null;
    TextView tvBillOutDays = null;
    TextView tvBillPaid = null;
    TextView tvBalanceAmount = null;
    LinearLayout llBillOutDays = null;
    private double deviceCollAmount =0.0;


    private String typevalue="";
    TextView tv_crs_sku_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_BillItemDetails));
        setContentView(R.layout.activity_invoice_history_details);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleInvoiceNo = bundleExtras.getString(Constants.InvoiceNo);
            mStrBundleInvoiceGuid = bundleExtras.getString(Constants.InvoiceGUID);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrInvAmountCurr = bundleExtras.getString(Constants.Currency);
            mStrStatus = bundleExtras.getString(Constants.STATUS);
            mStrInvDate = bundleExtras.getString(Constants.InvDate);
            mStrInvAmount = bundleExtras.getString(Constants.InvAmount);
            mCollectionAmount = bundleExtras.getString(Constants.CollectionAmount);
            mStrBundleDeviceStatus = bundleExtras.getString(Constants.DeviceStatus);
            mStrDeviceNo = bundleExtras.getString(Constants.DeviceNo);

        }
        if (!Constants.restartApp(InvoiceDetailsActivity.this)) {
            initUI();
        }
    }

    /*Initialize UI*/
    void initUI() {

        tvBillValue = (TextView) findViewById(R.id.tv_bill_val);
        tvBillOutDays = (TextView) findViewById(R.id.tv_bill_out_date);
        tvBillPaid = (TextView) findViewById(R.id.tv_bill_paid);
        tvBalanceAmount = (TextView) findViewById(R.id.tv_balance_amount);
        llBillOutDays = (LinearLayout) findViewById(R.id.ll_bill_out_days);

        tvBillValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mStrInvAmount) + " " + mStrInvAmountCurr);
        tvBillPaid.setText(mCollectionAmount);
        Float balanceAmount = Float.parseFloat(mStrInvAmount) - Float.parseFloat(mCollectionAmount);
        tvBalanceAmount.setText(UtilConstants.removeLeadingZerowithTwoDecimal(balanceAmount.toString()) + " " + mStrInvAmountCurr);

        if (!mStrStatus.equalsIgnoreCase(Constants.statusID_03)) {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.dtFormat_ddMMyyyywithslash);
            Date date = new Date();
            try {
                date = sdf.parse(mStrInvDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long timeDifferenceInMIliSecond = (new Date().getTime()) - date.getTime();
            int billOutDays = (int) (timeDifferenceInMIliSecond / (1000 * 60 * 60 * 24));
            tvBillOutDays.setText(String.valueOf(billOutDays));
            llBillOutDays.setVisibility(View.VISIBLE);
        } else
            llBillOutDays.setVisibility(View.GONE);

        tv_inv_date = (TextView) findViewById(R.id.tv_inv_date);


        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);

        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetUID);
        tv_inv_date.setText(mStrInvDate);


        deviceCollAmount = OfflineManager.getDeviceCollAmt(InvoiceDetailsActivity.this,mStrBundleRetUID,
                mStrBundleInvoiceGuid.toUpperCase().replace("-",""));



        try {
            double doublePenAmt = Double.parseDouble(mStrInvAmount) - (Double.parseDouble(mCollectionAmount) + deviceCollAmount);
            mStrPendingAmount = doublePenAmt + "";
        } catch (NumberFormatException e) {
            mStrPendingAmount = "0";
            e.printStackTrace();
        }
        tvBillValue.setText( UtilConstants.removeLeadingZero(mStrInvAmount)  + " " + mStrInvAmountCurr);
        tvBillPaid.setText( UtilConstants.removeLeadingZerowithTwoDecimal( (Double.parseDouble(mCollectionAmount) + deviceCollAmount)+"")  + " " + mStrInvAmountCurr);
        tvBalanceAmount.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mStrPendingAmount) + " " + mStrInvAmountCurr);

        tv_invoice_document_number = (TextView) findViewById(R.id.tv_invoice_document_number);

        tv_invoice_document_number.setText(mStrBundleInvoiceNo);

        TextView tvInvStatus = (TextView) findViewById(R.id.tv_inv_history_status);

        if (mStrStatus.toString().equals("01")) {
            tvInvStatus.setBackgroundResource(R.color.RED);
        } else if (mStrStatus.toString().equals("02")) {
            tvInvStatus.setBackgroundResource(R.color.YELLOW);
        } else if (mStrStatus.toString().equals("03")) {
            tvInvStatus.setBackgroundResource(R.color.GREEN);
        }

        //Load Invoices
        getInvoiceDetails();

    }


    /*Gets Invoice details*/
    private void getInvoiceDetails() {
        try {
            if (!mStrBundleDeviceStatus.equalsIgnoreCase(Constants.X)) {
                alInvoiceBean = OfflineManager.getInvoiceHistoryDetails(Constants.SSInvoices + "(guid'" + mStrBundleInvoiceGuid + "')/" + Constants.SSInvoiceItemDetails);
            } else {
                String store = null;
                try {
                    store = LogonCore.getInstance().getObjectFromStore(mStrDeviceNo);
                } catch (LogonCoreException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject fetchJsonHeaderObject = new JSONObject(store);
                    ArrayList<HashMap<String, String>> arrtable = new ArrayList<>();

                    String itemsString = fetchJsonHeaderObject.getString(Constants.ItemsText);

                    ArrayList<InvoiceHistoryBean> alInvoiceHisDetails = new ArrayList<InvoiceHistoryBean>();
                    InvoiceHistoryBean invoiceHisBean;
                    arrtable = UtilConstants.convertToArrayListMap(itemsString);
                    for (int i = 0; i < arrtable.size(); i++) {
                        HashMap<String, String> singleRow = arrtable.get(i);

                        invoiceHisBean = new InvoiceHistoryBean();

                        invoiceHisBean.setUom(singleRow.get(Constants.UOM));
                        invoiceHisBean.setMatCode(singleRow.get(Constants.MaterialNo));
                        invoiceHisBean.setMatDesc(singleRow.get(Constants.MaterialDesc));
                        invoiceHisBean.setCurrency(singleRow.get(Constants.Currency));
                        invoiceHisBean.setItemNo("" + (i + 1));
                        invoiceHisBean.setIsDetailEnabled(false);
                        invoiceHisBean.setInvoiceAmount(singleRow.get(Constants.NetAmount));
                        invoiceHisBean.setInvQty(singleRow.get(Constants.Quantity));
                        invoiceHisBean.setOrderMaterialGroup(singleRow.get(Constants.OrderMaterialGrpDesc));
                        alInvoiceHisDetails.add(invoiceHisBean);

                    }

                    if (alInvoiceHisDetails != null && alInvoiceHisDetails.size() > 0) {
                        alInvoiceBean = alInvoiceHisDetails;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            getDisplayedValues(alInvoiceBean);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    /*Display invoices as List*/
    private void getDisplayedValues(final ArrayList<InvoiceHistoryBean> arrayList) {
        if (!flag) {
            llDetailLayout.removeAllViews();
        }
        flag = false;
        llDetailLayout = (LinearLayout) findViewById(R.id.ll_invoice_detail_list);

        TableLayout tableHeading = (TableLayout) LayoutInflater.from(this)
                .inflate(R.layout.item_table, null);
        cursorLength = arrayList.size();
        matDesc = new TextView[cursorLength];
        matCode = new TextView[cursorLength];
        netAmount = new TextView[cursorLength];
        invQty = new TextView[cursorLength];
        itemNo = new TextView[cursorLength];
        matDesc_ex = new TextView[cursorLength];
        matCode_ex = new TextView[cursorLength];
        netAmount_ex = new TextView[cursorLength];
        invQty_ex = new TextView[cursorLength];
        itemNo_ex = new TextView[cursorLength];


        if (cursorLength > 0) {
            for (int i = 0; i < cursorLength; i++) {
                final InvoiceHistoryBean lvdbean = arrayList.get(i);
                final int selvalue = i;
                LinearLayout rowRelativeLayout = (LinearLayout) LayoutInflater
                        .from(this).inflate(
                                R.layout.invoice_history_details_list, null);
                tv_crs_sku_label= (TextView) rowRelativeLayout.findViewById(R.id.tv_crs_sku_label);
                getTypeValue();
                iv_expand_icon = (ImageView) rowRelativeLayout.findViewById(R.id.iv_expand_icon);
                itemNo[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_ivoice_details_item_no);

                matDesc[i] = (TextView) rowRelativeLayout
                        .findViewById(R.id.tv_invoice_details_mat_desc);
                matCode[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_invoice_details_mat_code);
                netAmount[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_invoice_details_net_amt);
                invQty[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_invoice_details_inv_qty);


                itemNo[i].setText("" + UtilConstants.removeLeadingZeros(arrayList.get(i).getItemNo()));
                matDesc[i].setText("" + arrayList.get(i).getMatDesc());

                netAmount[i].setText(UtilConstants.removeLeadingZerowithTwoDecimal(arrayList.get(i).getInvoiceAmount())
                        + " " + arrayList.get(i).getCurrency());
                invQty[i].setText("" + arrayList.get(i).getInvQty() + " " + arrayList.get(i).getUom());

                itemNo_ex[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_ivoice_details_item_no_ex);

                matDesc_ex[i] = (TextView) rowRelativeLayout
                        .findViewById(R.id.tv_invoice_details_mat_desc_ex);
                matCode_ex[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_invoice_details_mat_code_ex);
                netAmount_ex[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_invoice_details_net_amt_ex);
                invQty_ex[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_invoice_details_inv_qty_ex);


                itemNo_ex[i].setText("" + UtilConstants.removeLeadingZeros(arrayList.get(i).getItemNo()));
                matDesc_ex[i].setText("" + arrayList.get(i).getMatDesc());
                matCode_ex[i].setText(arrayList.get(i).getOrderMaterialGroup()+"");

                netAmount_ex[i].setText(UtilConstants.removeLeadingZerowithTwoDecimal(arrayList.get(i).getInvoiceAmount())
                        + " " + arrayList.get(i).getCurrency());
                invQty_ex[i].setText("" +arrayList.get(i).getInvQty() + " " + arrayList.get(i).getUom());

                final View expandView = rowRelativeLayout;
                iv_expand_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView img = (ImageView) v;
                        LinearLayout lt_last_line = (LinearLayout) expandView.findViewById(R.id.lt_lastLine_ex);
                        LinearLayout ll_down_colour = (LinearLayout) expandView.findViewById(R.id.ll_down_color);
                        Log.d(Constants.STATUS, arrayList.get(selvalue).getIsDetailEnabled() + "");

                        if (arrayList.get(selvalue).getIsDetailEnabled()) {
                            arrayList.get(selvalue).setIsDetailEnabled(false);
                            img.setImageResource(R.drawable.down);
                            lt_last_line.setVisibility(View.GONE);
                            ll_down_colour.setVisibility(View.GONE);
                        } else {
                            arrayList.get(selvalue).setIsDetailEnabled(true);
                            img.setImageResource(R.drawable.up);
                            lt_last_line.setVisibility(View.VISIBLE);

                            ll_down_colour.setVisibility(View.VISIBLE);
                        }
                    }
                });

                tableHeading.addView(rowRelativeLayout);
            }

            llDetailLayout.addView(tableHeading);

        } else {

            matDesc = new TextView[1];
            matCode = new TextView[1];
            netAmount = new TextView[1];
            invQty = new TextView[1];
            itemNo = new TextView[1];

            LinearLayout rowRelativeLayout = (LinearLayout) LayoutInflater
                    .from(this).inflate(
                            R.layout.invoice_history_details_list, null);
            tv_crs_sku_label= (TextView) rowRelativeLayout.findViewById(R.id.tv_crs_sku_label);
            getTypeValue();
            itemNo[0] = (TextView) rowRelativeLayout.findViewById(R.id.tv_ivoice_details_item_no);

            matDesc[0] = (TextView) rowRelativeLayout
                    .findViewById(R.id.tv_invoice_details_mat_desc);
            matCode[0] = (TextView) rowRelativeLayout.findViewById(R.id.tv_invoice_details_mat_code);
            netAmount[0] = (TextView) rowRelativeLayout.findViewById(R.id.tv_invoice_details_net_amt);
            invQty[0] = (TextView) rowRelativeLayout.findViewById(R.id.tv_invoice_details_inv_qty);


            itemNo[0].setText("");
            matDesc[0].setText("");
            matCode[0].setText("");
            netAmount[0].setText(getString(R.string.str_rupee_symbol) + " 0");
            invQty[0].setText("0");

            tableHeading.addView(rowRelativeLayout);

            llDetailLayout.addView(tableHeading);
        }

    }


    private void getTypeValue() {




        typevalue=Constants.getTypesetValueForSkugrp(InvoiceDetailsActivity.this);
        if(typevalue.equalsIgnoreCase(Constants.SKUGROUP)){
            tv_crs_sku_label.setText(Constants.SKUGROUP);
            // etSKUDescSearch.setHint(R.string.lbl_Search_by_skugroup);
        }else{
            tv_crs_sku_label.setText(Constants.CRSSKUGROUP);
            //  etSKUDescSearch.setHint(R.string.lbl_Search_by_crsskugroup);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                onBackPressed();
                break;

        }
    }

}
