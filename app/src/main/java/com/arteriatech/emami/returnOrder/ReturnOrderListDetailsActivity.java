package com.arteriatech.emami.returnOrder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.SOTempBean;
import com.arteriatech.emami.mbo.SSOItemBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;
import java.util.List;

public class ReturnOrderListDetailsActivity extends AppCompatActivity {
    TextView[] matDesc, matCode, netAmount, invQty, itemNo;
    TextView[] matDesc_ex, matCode_ex, netAmount_ex, invQty_ex, itemNo_ex;
    ImageView iv_expand_icon;
    private String mStrBundleRetName = "";
    private String mStrBundleRetID = "";
    private String mStrBundleCPGUID = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleSSROGUID = "";
    private List<ReturnOrderBean> returnOrderBeanList = new ArrayList<>();
    private LinearLayout llDetailLayout;
    private boolean flag = true;
    private int cursorLength = 0;
    private int comingFrom = 0;
    private String actionBarTitle = "";
    private int tabPosition = 0;
    private String mDeviceNo = "";
    private TextView tvHistoryStatus;
    private TextView tvInvDate;
    private TextView tvInvNo;
    private TextView tvOutStandValue;
    private String mStrBundleDate = "";
    private String mStrBundleIds = "";
    private String mStrBundleAmount = "";
    private String mStrBundleStatus = "",mBundleTempStatus = "";
    private String mStrBundleCurrency = "";

    private String typevalue="";
    TextView tv_crs_sku_label;
    private ArrayList<SSOItemBean> alStockBean = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_order_list_details);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleSSROGUID = bundleExtras.getString(Constants.EXTRA_SSRO_GUID);
            mStrBundleDate = bundleExtras.getString(Constants.EXTRA_ORDER_DATE);
            mStrBundleIds = bundleExtras.getString(Constants.EXTRA_ORDER_IDS);
            mStrBundleAmount = bundleExtras.getString(Constants.EXTRA_ORDER_AMOUNT);
            mStrBundleStatus = bundleExtras.getString(Constants.EXTRA_ORDER_SATUS);
            mBundleTempStatus = bundleExtras.getString(Constants.EXTRA_TEMP_STATUS)!=null?bundleExtras.getString(Constants.EXTRA_TEMP_STATUS):"";
            mStrBundleCurrency = bundleExtras.getString(Constants.EXTRA_ORDER_CURRENCY);
            mDeviceNo = bundleExtras.getString(Constants.DeviceNo);
            comingFrom = bundleExtras.getInt(Constants.comingFrom, 0);
            tabPosition = bundleExtras.getInt(Constants.EXTRA_TAB_POS, 0);
        }
        if (!Constants.restartApp(ReturnOrderListDetailsActivity.this)) {
            //declare UI
            setUI();
            if (comingFrom == Constants.RETURN_ORDER_POS) {
                actionBarTitle = getString(R.string.title_ac_return_order_list);
            } else if (comingFrom == Constants.SSS_ORDER_POS) {
                actionBarTitle = getString(R.string.title_ssso_order_list);
            }
            ActionBarView.initActionBarView(this, true, actionBarTitle);
            //get data from offline db
            if (comingFrom == Constants.RETURN_ORDER_POS && tabPosition == Constants.TAB_POS_1) {
                getRODataFromOfflineDb();
            } else if (comingFrom == Constants.RETURN_ORDER_POS && tabPosition == Constants.TAB_POS_2) {
                getRODataFromDataValt();
            } else if (comingFrom == Constants.SSS_ORDER_POS && tabPosition == Constants.TAB_POS_1) {
                getSSSODataFromOfflineDb();
            } else if (comingFrom == Constants.SSS_ORDER_POS && tabPosition == Constants.TAB_POS_2) {
                getSSSODataFromDataValt();
            }
        }
    }

    private void getTypeValue() {




        typevalue=Constants.getTypesetValueForSkugrp(ReturnOrderListDetailsActivity.this);
        if(typevalue.equalsIgnoreCase(Constants.SKUGROUP)){
            tv_crs_sku_label.setText(Constants.SKUGROUP);
            // etSKUDescSearch.setHint(R.string.lbl_Search_by_skugroup);
        }else{
            tv_crs_sku_label.setText(Constants.CRSSKUGROUP);
            //  etSKUDescSearch.setHint(R.string.lbl_Search_by_crsskugroup);
        }
    }

    /*get ssso data from data valt*/
    private void getSSSODataFromDataValt() {
        try {
            returnOrderBeanList = OfflineManager.getSSSODetailsListFromDataValt(ReturnOrderListDetailsActivity.this, mDeviceNo, mStrBundleSSROGUID, returnOrderBeanList);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        /*display value*/
        getDisplayedValues(returnOrderBeanList);
    }

    /*get ssso data from offlene database*/
    private void getSSSODataFromOfflineDb() {
        String qry = Constants.SSSoItemDetails + "?$filter= SSSOGuid eq guid'" + mStrBundleSSROGUID + "' ";
        try {
            returnOrderBeanList = OfflineManager.getSecondarySalesOrderDetailsList(qry, returnOrderBeanList);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        /*display value*/
        getDisplayedValues(returnOrderBeanList);
    }

    /*get RO data from data valt*/
    private void getRODataFromDataValt() {
        try {
            returnOrderBeanList = OfflineManager.getRODetailsListFromDataValt(ReturnOrderListDetailsActivity.this, mDeviceNo, mStrBundleSSROGUID, returnOrderBeanList);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        /*display value*/
        getDisplayedValues(returnOrderBeanList);
    }

    /**
     * declare UI
     */
    private void setUI() {
        llDetailLayout = (LinearLayout) findViewById(R.id.ll_invoice_detail_list);
        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);
        tvHistoryStatus = (TextView) findViewById(R.id.tv_inv_history_status);
        tvInvDate = (TextView) findViewById(R.id.tv_inv_date);
        tvInvNo = (TextView) findViewById(R.id.tv_invoice_document_number);
        tvOutStandValue = (TextView) findViewById(R.id.tv_bill_out_date);

        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetUID);
        tvInvDate.setText(mStrBundleDate);
        tvInvNo.setText(mStrBundleIds);
        tvOutStandValue.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mStrBundleAmount) + " " + mStrBundleCurrency);
        Constants.ssoItemBeans.clear();
        if (mStrBundleStatus.equals("000001")) {
            tvHistoryStatus.setBackgroundResource(R.color.RED);
        } else {
            tvHistoryStatus.setBackgroundResource(android.R.color.transparent);
        }
    }

    /**
     * get data from offline db
     */
    private void getRODataFromOfflineDb() {
        String qry = Constants.SSROItemDetails + "?$filter= SSROGUID eq guid'" + mStrBundleSSROGUID + "' ";
        try {
            returnOrderBeanList = OfflineManager.getReturnOrderDetailsList(qry, returnOrderBeanList);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        /*display value*/
        getDisplayedValues(returnOrderBeanList);

    }

    /**
     * get data and set it into linearlayout
     *
     * @param arrayList
     */
    private void getDisplayedValues(final List<ReturnOrderBean> arrayList) {
        if (!flag) {
            llDetailLayout.removeAllViews();
        }
        flag = false;
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
                final ReturnOrderBean bean = arrayList.get(i);
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


                itemNo[i].setText("" + UtilConstants.removeLeadingZeros(bean.getsItemNo()));
                matDesc[i].setText("" + bean.getMaterialDesc());

                netAmount[i].setText(UtilConstants.removeLeadingZerowithTwoDecimal(bean.getNetAmount())
                        + " " + bean.getCurrency());
                invQty[i].setText("" + bean.getQAQty() + " " + bean.getUom());

                itemNo_ex[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_ivoice_details_item_no_ex);

                matDesc_ex[i] = (TextView) rowRelativeLayout
                        .findViewById(R.id.tv_invoice_details_mat_desc_ex);
                matCode_ex[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_invoice_details_mat_code_ex);
                netAmount_ex[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_invoice_details_net_amt_ex);
                invQty_ex[i] = (TextView) rowRelativeLayout.findViewById(R.id.tv_invoice_details_inv_qty_ex);


                itemNo_ex[i].setText("" + UtilConstants.removeLeadingZeros(bean.getsItemNo()));
                matDesc_ex[i].setText("" + bean.getMaterialDesc());
                matCode_ex[i].setText("" + bean.getOrderMaterialGroupDesc());

                SSOItemBean soItemBean = new SSOItemBean();
                soItemBean.setBatchNo(bean.getBatch());
                soItemBean.setMaterialNo(bean.getMaterialNo());
                soItemBean.setSOItemGuid(bean.getSSSOItemGUID());
                soItemBean.setQty(bean.getQAQty());
                soItemBean.setMRP(bean.getMRP());
                alStockBean.add(soItemBean);

                netAmount_ex[i].setText(UtilConstants.removeLeadingZerowithTwoDecimal(bean.getNetAmount())
                        + " " + bean.getCurrency());
                if(!bean.getUom().equalsIgnoreCase(""))
                    invQty_ex[i].setText(bean.getQAQty() + " " + bean.getUom());
                else
                    invQty_ex[i].setText(bean.getQAQty() );

                final View expandView = rowRelativeLayout;
                iv_expand_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView img = (ImageView) v;
                        LinearLayout lt_last_line = (LinearLayout) expandView.findViewById(R.id.lt_lastLine_ex);
                        LinearLayout ll_down_colour = (LinearLayout) expandView.findViewById(R.id.ll_down_color);
                        if (bean.getDetailEnabled()) {
                            bean.setDetailEnabled(false);
                            img.setImageResource(R.drawable.down);
                            lt_last_line.setVisibility(View.GONE);
                            ll_down_colour.setVisibility(View.GONE);
                        } else {
                            bean.setDetailEnabled(true);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_back_delete, menu);
        menu.findItem(R.id.menu_back).setVisible(false);
        if (comingFrom == Constants.RETURN_ORDER_POS && tabPosition == Constants.TAB_POS_2) {
            menu.findItem(R.id.menu_delete).setVisible(true);
            menu.findItem(R.id.menu_invoice_create).setVisible(false);
        }else{
            menu.findItem(R.id.menu_delete).setVisible(false);
            menu.findItem(R.id.menu_invoice_create).setVisible(true);
        }
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        String sharedVal = sharedPreferences.getString(Constants.isInvoiceCreateKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isInvoiceTcode)) {
            menu.findItem(R.id.menu_invoice_create).setVisible(true);
        }else {
            menu.findItem(R.id.menu_invoice_create).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_delete:
                onDelete();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_invoice_create:
                onNavigateToInvCreate();
                break;
        }
        return false;
    }

    private void onNavigateToInvCreate(){
        if( (mStrBundleStatus.equalsIgnoreCase("") || mStrBundleStatus.equalsIgnoreCase("000001")) && mBundleTempStatus.equalsIgnoreCase("")){
            Constants.ssoItemBeans.clear();
            Constants.ssoItemBeans.addAll(alStockBean);
            Constants.SOBundleValue =new SOTempBean();
            Constants.SOBundleValue.setRetcomingFrom(comingFrom);
            Constants.SOBundleValue.setRetTabPos(tabPosition);
            Constants.SOBundleValue.setRetName(mStrBundleRetName);
            Constants.SOBundleValue.setRetNo(mStrBundleRetID);
            Constants.SOBundleValue.setRetUID(mStrBundleRetUID);
            try {
                Constants.SOBundleValue.setRetCPGUID(Constants.convertStrGUID32to36(mStrBundleCPGUID));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Constants.SOBundleValue.setRetCPGUID32(mStrBundleCPGUID);
            Constants.SOBundleValue.setRetSSROGUID(mStrBundleSSROGUID);
            Constants.SOBundleValue.setRetSSSONo(mStrBundleIds);
            Constants.SOBundleValue.setRetSODate(mStrBundleDate);
            Constants.SOBundleValue.setRetSOAmount(mStrBundleAmount);
            Constants.SOBundleValue.setRetSOStatus(mStrBundleStatus);
            Constants.SOBundleValue.setRetSOCurrency(mStrBundleCurrency);
            Constants.SOBundleValue.setRetSODeviceNo(mDeviceNo);

            String mStrMatQry = Constants.makeMaterialQry(alStockBean,Constants.MaterialNo,Constants.Batch);
            Intent intentFeedBack = new Intent(this, com.arteriatech.emami.invoicecreate.InvoiceCreateActivity.class);
            intentFeedBack.putExtra(Constants.CPNo, mStrBundleRetID);
            intentFeedBack.putExtra(Constants.CPUID, mStrBundleRetUID);
            intentFeedBack.putExtra(Constants.RetailerName, mStrBundleRetName);
            intentFeedBack.putExtra(Constants.CPGUID,Constants.convertStrGUID32to36(mStrBundleCPGUID));
            intentFeedBack.putExtra(Constants.CPGUID32, mStrBundleCPGUID.replace("-","").toUpperCase());
            intentFeedBack.putExtra(Constants.comingFrom, "SOList");
            intentFeedBack.putExtra(Constants.OrderNo, mStrBundleIds);
            intentFeedBack.putExtra(Constants.SSSOGuid, mStrBundleSSROGUID);
            intentFeedBack.putExtra(Constants.MateralQry, mStrMatQry);
            startActivity(intentFeedBack);
        }else{
            UtilConstants.showAlert(getString(R.string.alert_invoice_create_open_status),ReturnOrderListDetailsActivity.this);
        }

    }
    private  void onDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ReturnOrderListDetailsActivity.this,R.style.MyTheme);
        builder.setMessage(R.string.alert_delete_retailer_invoice).setCancelable(false)
                .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                        Constants.removeDeviceDocNoFromSharedPref(ReturnOrderListDetailsActivity.this, Constants.ROList,mDeviceNo);
                        UtilDataVault.storeInDataVault(mDeviceNo, "");
                        Constants.Is_Return_Order_Tab_Delete = true;
                        onBackPressed();
                    }
                })
                .setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }
}
