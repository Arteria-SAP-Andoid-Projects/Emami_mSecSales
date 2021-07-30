package com.arteriatech.emami.reports;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ${e10526} on ${27-04-2016}.
 *
 */
public class CollectionDetailsActivity extends AppCompatActivity implements View.OnClickListener{

      String    mStrFISDocNo="",
                mStrBundleCPGUID="",
                mStrFIPGUID="",
                mStrBundleDeviceStatus="",
                mStrDeviceNo="",
                mStrBundleRetName="",
                mStrBundleInstNo="",
                mStrBundleCollAmt="",
                mStrBundleCollDate="",
                mStrBundleRetID="",
                mStrBundleCollAmtCurr="",
                mPaymentMode="";
    private TextView tv_collection_document_number, tv_coll_amount,tv_coll_date;
    private ArrayList<CollectionHistoryBean> alCollHistBean =null;
    private ImageView iv_expand_icon;


    private boolean mBooleanRemoveScrollViews = true;

    private String fipGuid = "";

    //new28112016
    private String mStrBundleRetUID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_collection_details));
        setContentView(R.layout.activity_collection_details);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        tv_collection_document_number = (TextView) findViewById(R.id.tv_coll_doc_num);

        tv_coll_amount = (TextView) findViewById(R.id.tv_coll_val);
        tv_coll_date= (TextView) findViewById(R.id.tv_coll_date);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            mStrFISDocNo = extra.getString(Constants.FISDocNo);
            mStrFIPGUID = extra.getString(Constants.FIPGUID);
            mStrBundleDeviceStatus = extra.getString(Constants.DeviceStatus);
            mStrDeviceNo = extra.getString(Constants.DeviceNo);
            mStrBundleCollAmt = extra.getString(Constants.CollAmount);
            mStrBundleCollDate = extra.getString(Constants.CollDate);
            mStrBundleRetName = extra.getString(Constants.RetailerName);
            mStrBundleInstNo = extra.getString(Constants.InstrumentNo);
            mStrBundleCollAmtCurr = extra.getString(Constants.Currency);
            mStrBundleRetID = extra.getString(Constants.CPNo);
            mStrBundleCPGUID= extra.getString(Constants.CPGUID);
            mStrBundleRetUID = extra.getString(Constants.CPUID);
            mPaymentMode = extra.getString(Constants.PaymentMode);
        }
        if (!Constants.restartApp(CollectionDetailsActivity.this)) {
            initUI();
        }
    }

    /*initializes UI for screen*/
    void initUI(){
        tv_collection_document_number.setText(mStrFISDocNo);

        tv_coll_date.setText(UtilConstants.convertDateIntoDeviceFormat(CollectionDetailsActivity.this,mStrBundleCollDate));
        tv_coll_amount.setText(UtilConstants.removeLeadingZerowithTwoDecimal(mStrBundleCollAmt)
                +" "+mStrBundleCollAmtCurr);

        TextView retName = (TextView) findViewById(R.id.tv_reatiler_name);
        TextView retId = (TextView) findViewById(R.id.tv_reatiler_id);

        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetUID);
        getItems();
    }

    /*get collection items*/
    private void getItems() {

        try {
            String mStrCollectionItemQry = Constants.FinancialPostingItemDetails+"?$filter="+Constants.FIPGUID+" eq "+mStrFIPGUID+" &$orderby="+Constants.FIPItemNo+" asc" ;
            if(mStrBundleDeviceStatus.equalsIgnoreCase("")) {
                alCollHistBean = OfflineManager.getCollectionItemDetails(mStrCollectionItemQry);
            }else{
                String store=null;
                try {
                    store = LogonCore.getInstance().getObjectFromStore(mStrDeviceNo);
                } catch (LogonCoreException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject fetchJsonHeaderObject = new JSONObject(store);
                    ArrayList<HashMap<String, String>>  arrtable = new ArrayList<>();

                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);

                    fipGuid =  fetchJsonHeaderObject.getString(Constants.FIPGUID);

                    String fipDocType = fetchJsonHeaderObject.getString(Constants.FIPDocType);
                    String fipDocDate = fetchJsonHeaderObject.getString(Constants.FIPDate);

                    arrtable= UtilConstants.convertToArrayListMap(itemsString);

                    ArrayList<CollectionHistoryBean> alCollHisDetails = new ArrayList<CollectionHistoryBean>();
                    CollectionHistoryBean collHisBean;
                    arrtable= UtilConstants.convertToArrayListMap(itemsString);
                    for (int i = 0; i < arrtable.size();i++) {
                        HashMap<String, String> singleRow = arrtable.get(i);

                        collHisBean = new CollectionHistoryBean();
                        collHisBean.setFIPItemNo("" + (i + 1));
                        collHisBean.setCurrency(singleRow.get(Constants.Currency));
                        collHisBean.setInvoiceDate(UtilConstants.getConvetDDMMYYYYY(fipDocDate));
                        if(fipDocType.equalsIgnoreCase("01")){
                                    collHisBean.setInvoiceNo(singleRow.get(Constants.InvoiceNo));
                            collHisBean.setInvoiceAmount(singleRow.get(Constants.Amount));
                            collHisBean.setInvoiceClearedAmount(singleRow.get(Constants.FIPAmount));


                            double balanceAmout=0.0,mDouTotBalAmt=0.0;
                            try {
                               /* balanceAmout = Double.parseDouble(singleRow.get(Constants.Amount))
                                        -Double.parseDouble(singleRow.get(Constants.FIPAmount))-
                                        Double.parseDouble(singleRow.get(Constants.OutstandingAmt));*/

                                balanceAmout =   Double.parseDouble(singleRow.get(Constants.OutstandingAmt))
                                        -Double.parseDouble(singleRow.get(Constants.FIPAmount)) ;

                                mDouTotBalAmt = Double.parseDouble(singleRow.get(Constants.Amount)) - balanceAmout;
                            } catch (NumberFormatException e) {

                                e.printStackTrace();
                            }


                            collHisBean.setTotalPaidAmount(mDouTotBalAmt+"");
                            collHisBean.setInvoiceBalanceAmount(balanceAmout+"");
                            collHisBean.setIsDetailEnabled(false);
                        }else{
                            collHisBean.setInvoiceNo("");
                            collHisBean.setInvoiceAmount("");
                            collHisBean.setInvoiceClearedAmount(singleRow.get(Constants.FIPAmount));
                            collHisBean.setInvoiceBalanceAmount("");
                            collHisBean.setTotalPaidAmount("");
                            collHisBean.setIsDetailEnabled(false);
                        }
                        collHisBean.setInstrumentNo(singleRow.get(Constants.InstrumentNo));
                        collHisBean.setPaymentModeDesc(singleRow.get(Constants.PaymetModeDesc));
                        collHisBean.setIsDetailEnabled(false);
                        alCollHisDetails.add(collHisBean);
                    }

                    if(alCollHisDetails!=null && alCollHisDetails.size()>0){
                        alCollHistBean = alCollHisDetails;
                    }

                }  catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error+" : " + e.getMessage());
        }
        displayCollValues(alCollHistBean);
    }

    /*Displays collection items*/
    private void displayCollValues(final ArrayList<CollectionHistoryBean> filteredArraylist) {

        ScrollView scroll_retailer_summary_list = (ScrollView)findViewById(R.id.so_status_scroll);
        if (!mBooleanRemoveScrollViews) {
            scroll_retailer_summary_list.removeAllViews();
        }

        mBooleanRemoveScrollViews = false;

        TableLayout tlRetSummary = (TableLayout) LayoutInflater.from(this).inflate(
                R.layout.item_table, null, false);

        LinearLayout llCollItem;

        if(filteredArraylist!=null){
            if (!filteredArraylist.isEmpty()
                    && filteredArraylist.size() > 0 ) {

                for (int i = 0; i < filteredArraylist.size(); i++) {
                    final int selValue = i;
                    llCollItem = (LinearLayout) LayoutInflater.from(this)
                            .inflate(R.layout.coll_his_list_item,
                                    null, false);
                    iv_expand_icon = (ImageView)llCollItem.findViewById(R.id.iv_expand_icon);
                    ((TextView)llCollItem.findViewById(R.id.tv_coll_det_item_no))
                            .setText(UtilConstants.removeLeadingZeros(filteredArraylist.get(i).getFIPItemNo()));

                    ((TextView)llCollItem.findViewById(R.id.tv_coll_det_bill_num))
                            .setText(filteredArraylist.get(i).getInvoiceNo());

                    ((TextView)llCollItem.findViewById(R.id.tv_coll_det_bill_date))
                            .setText(filteredArraylist.get(i).getInvoiceDate());


                    ((TextView) llCollItem.findViewById(R.id.tv_coll_det_inv_amt))
                            .setText(UtilConstants.removeLeadingZerowithTwoDecimal(filteredArraylist.get(i).getInvoiceAmount())
                                    +" "+filteredArraylist.get(i).getCurrency());

                    ((TextView) llCollItem.findViewById(R.id.tv_coll_det_bal_amt))
                            .setText(UtilConstants.removeLeadingZerowithTwoDecimal(filteredArraylist.get(i).getInvoiceBalanceAmount())
                                    +" "+filteredArraylist.get(i).getCurrency());


                    ((TextView) llCollItem.findViewById(R.id.tv_coll_det_paid_amt))
                            .setText(UtilConstants.removeLeadingZerowithTwoDecimal(filteredArraylist.get(i).getInvoiceClearedAmount())
                                    +" "+filteredArraylist.get(i).getCurrency());

                    ((TextView)llCollItem.findViewById(R.id.tv_coll_det_item_no_ex))
                            .setText(filteredArraylist.get(i).getFIPItemNo());

                    ((TextView)llCollItem.findViewById(R.id.tv_coll_det_bill_num_ex))
                            .setText(filteredArraylist.get(i).getInvoiceNo());

                    ((TextView)llCollItem.findViewById(R.id.tv_coll_det_bill_date_ex))
                            .setText(filteredArraylist.get(i).getInvoiceDate());

                    ((TextView) llCollItem.findViewById(R.id.tv_coll_det_inv_amt_ex))
                            .setText(UtilConstants.removeLeadingZerowithTwoDecimal(filteredArraylist.get(i).getInvoiceAmount())
                                    +" "+filteredArraylist.get(i).getCurrency());

                            ((TextView) llCollItem.findViewById(R.id.tv_coll_total_paid_amt))
                            .setText(UtilConstants.removeLeadingZerowithTwoDecimal(filteredArraylist.get(i).getTotalPaidAmount())
                                    +" "+filteredArraylist.get(i).getCurrency());

                    ((TextView) llCollItem.findViewById(R.id.tv_coll_det_bal_amt_ex))
                            .setText(UtilConstants.removeLeadingZerowithTwoDecimal(filteredArraylist.get(i).getInvoiceBalanceAmount())
                                    +" "+filteredArraylist.get(i).getCurrency());


                            ((TextView) llCollItem.findViewById(R.id.tv_coll_det_inst_num))
                            .setText(filteredArraylist.get(i).getInstrumentNo());

                    ((TextView) llCollItem.findViewById(R.id.tv_coll_payment_mode_ex))
                            .setText(filteredArraylist.get(i).getPaymentModeDesc());

                    ((TextView) llCollItem.findViewById(R.id.tv_coll_det_paid_amt_ex))
                            .setText(UtilConstants.removeLeadingZerowithTwoDecimal(filteredArraylist.get(i).getInvoiceClearedAmount())
                                    +" "+filteredArraylist.get(i).getCurrency());

                    final View testView = llCollItem;
                    iv_expand_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ImageView img = (ImageView)v;
                            LinearLayout lt_last_line  =  (LinearLayout)testView.findViewById(R.id.lt_lastLine_ex);
                            LinearLayout ll_down_colour  =  (LinearLayout)testView.findViewById(R.id.ll_down_color);

                            if(filteredArraylist.get(selValue).getIsDetailEnabled())
                            {
                                filteredArraylist.get(selValue).setIsDetailEnabled(false);
                                img.setImageResource(R.drawable.down);
                                lt_last_line.setVisibility(View.GONE);
                                ll_down_colour.setVisibility(View.GONE);
                            }
                            else
                            {
                                filteredArraylist.get(selValue).setIsDetailEnabled(true);
                                img.setImageResource(R.drawable.up);
                                lt_last_line.setVisibility(View.VISIBLE);
                                ll_down_colour.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    tlRetSummary.addView(llCollItem);
                }
            }else{

                llCollItem = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.no_data_found_ll,
                                null,false);

                tlRetSummary.addView(llCollItem);
            }
        }else{

            llCollItem = (LinearLayout) LayoutInflater.from(this)
                    .inflate(R.layout.no_data_found_ll,
                            null,false);

            tlRetSummary.addView(llCollItem);
        }



        scroll_retailer_summary_list.addView(tlRetSummary);
        scroll_retailer_summary_list.requestLayout();
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
