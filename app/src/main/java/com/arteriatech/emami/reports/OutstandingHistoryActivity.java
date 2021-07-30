package com.arteriatech.emami.reports;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.OutstandingListAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by e10604 on 27/11/2016.
 *
 */
public class OutstandingHistoryActivity extends AppCompatActivity implements View.OnClickListener, UIListener {


    private OutstandingListAdapter outstandingListAdapter = null;
    private ArrayList<OutstandingBean> alOutstandingsBean;
    private ArrayList<OutstandingBean> alMainInvoiceBean = new ArrayList<>();
    private String mStrBundleRetID = "", mStrBundleCPGUID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";

    Spinner spOutstandingStatus;
    String selectedStatus;

    //new
    TextView tvEmptyLay = null;
    TextView tvTotalOutValCurr = null;

    //new
    TextView tvRetName = null, tvUID = null;
    TextView tvTotalOutVal = null;
    ListView lv_out_his_list = null;

    String concatCollectionStr = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    ProgressDialog syncProgDialog = null;
    boolean dialogCancelled = false;
    TextView tv_last_sync_time_value;
    private Bundle bundleExtras;
    EditText etInvoiceSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_OutstandingHistory));

        setContentView(R.layout.activity_outstanding_hist);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
        }
        if (!Constants.restartApp(OutstandingHistoryActivity.this)) {
            initUI();
        }
    }

    void initUI() {
        tv_last_sync_time_value = (TextView) findViewById(R.id.tv_last_sync_time_value);
        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.OutstandingInvoices, Constants.TimeStamp,this));
        lv_out_his_list = (ListView) findViewById(R.id.lv_out_standing_list);

        //new
        tvRetName = (TextView) findViewById(R.id.tv_bill_hist_ret_name);
        tvUID = (TextView) findViewById(R.id.tv_bill_hist_uid);
        tvTotalOutVal = (TextView) findViewById(R.id.tv_total_out_val);
        tvTotalOutValCurr = (TextView) findViewById(R.id.tv_total_out_val_currency);

        spOutstandingStatus = (Spinner) findViewById(R.id.spin_invoice_his_status_id);

        //new
        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetUID);

        tvEmptyLay = (TextView) findViewById(R.id.tv_empty_lay);
        etInvoiceSearch = (EditText) findViewById(R.id.ed_invoice_search);
        etInvoiceSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                outstandingListAdapter.getFilter().filter(cs); //Filter from my adapter
                outstandingListAdapter.notifyDataSetChanged(); //Update my view
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });

        getStatus();



    }

    /*get different status for outstanding*/
    private void getStatus() {
        try {
            alMainInvoiceBean = OfflineManager.getOutstandingList(Constants.OutstandingInvoices + "?$filter="
                    + Constants.SoldToID + " eq '" + mStrBundleRetID + "'" + " and "
                    + Constants.PaymentStatusID + " ne '" + "03" + "'", getApplicationContext(), "", mStrBundleCPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> productCategoryAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_textview, Constants.billAges[1]);
        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spOutstandingStatus.setAdapter(productCategoryAdapter);

        spOutstandingStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                selectedStatus = Constants.billAges[0][position];

                clearEditTextSearchBox();
                if (selectedStatus.equalsIgnoreCase("00")) {

                    getInvoiceList("");
                } else {
                    getInvoiceList(selectedStatus);
                }

            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    private void clearEditTextSearchBox(){
        if(etInvoiceSearch!=null && etInvoiceSearch.getText().toString().length()>0)
            etInvoiceSearch.setText("");
    }

    /*Get List of outstanding invoices */
    private void getInvoiceList(String status) {
        try {

            if (status.equalsIgnoreCase("")) {
                alOutstandingsBean = OfflineManager.getOutstandingList(Constants.OutstandingInvoices
                                + "?$filter=" + Constants.SoldToID + " eq '" + mStrBundleRetID + "'"
                                + " and " + Constants.PaymentStatusID + " ne '" + "03" + "'",
                        getApplicationContext(), "", mStrBundleCPGUID);

                OutstandingHistoryActivity.this.outstandingListAdapter = new OutstandingListAdapter(OutstandingHistoryActivity.this, R.layout.activity_invoice_history_list,
                        alOutstandingsBean,bundleExtras);
                lv_out_his_list.setEmptyView(findViewById(R.id.tv_empty_lay) );
                lv_out_his_list.setAdapter(outstandingListAdapter);
                OutstandingHistoryActivity.this.outstandingListAdapter.notifyDataSetChanged();

            } else {
                alOutstandingsBean.clear();
                switch (status) {
                    case "01": {
                        for (OutstandingBean item : alMainInvoiceBean) {
                            if (getBillAge(item) >= 0 && getBillAge(item) <= 30)
                                alOutstandingsBean.add(item);
                        }
                    }
                    break;

                    case "02": {
                        for (OutstandingBean item : alMainInvoiceBean) {
                            if (getBillAge(item) > 30 && getBillAge(item) <= 60)
                                alOutstandingsBean.add(item);
                        }
                    }
                    break;

                    case "03": {
                        for (OutstandingBean item : alMainInvoiceBean) {
                            if (getBillAge(item) > 60 && getBillAge(item) <= 90)
                                alOutstandingsBean.add(item);
                        }
                    }
                    break;

                    case "04": {
                        for (OutstandingBean item : alMainInvoiceBean) {
                            if (getBillAge(item) > 90)
                                alOutstandingsBean.add(item);
                        }
                    }
                    break;
                }

                OutstandingHistoryActivity.this.outstandingListAdapter = new OutstandingListAdapter(OutstandingHistoryActivity.this,
                        R.layout.activity_invoice_history_list, alOutstandingsBean,bundleExtras);
                lv_out_his_list.setAdapter(outstandingListAdapter);
                OutstandingHistoryActivity.this.outstandingListAdapter.notifyDataSetChanged();

            }
            if (alOutstandingsBean.size() < 1) {
                tvEmptyLay.setVisibility(View.VISIBLE);
            } else
                tvEmptyLay.setVisibility(View.GONE);
            double totalOutVal = 0.00;
            String currencyText = "";
            for (OutstandingBean invoice : alOutstandingsBean) {
                totalOutVal = totalOutVal + (Double.parseDouble(invoice.getInvoiceAmount()) -
                        (Double.parseDouble(invoice.getCollectionAmount()) + Double.parseDouble(invoice.getDevCollAmount())));
            }
            for (OutstandingBean invoice : alOutstandingsBean) {
                currencyText = invoice.getCurrency();
                break;
            }

            tvTotalOutVal.setText(" " + UtilConstants.removeLeadingZerowithTwoDecimal(String.valueOf(totalOutVal)));
            tvTotalOutValCurr.setText(currencyText);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_invoice_his_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_refresh_inv:
                onRefresh();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                onBackPressed();
                break;
        }
    }

    public static int getBillAge(OutstandingBean item) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.dtFormat_ddMMyyyywithslash);
        Date date = new Date();
        try {
            date = sdf.parse(item.getInvoiceDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timeDifferenceInMIliSecond = (new Date().getTime()) - date.getTime();
        int billOutDays = (int) (timeDifferenceInMIliSecond / (1000 * 60 * 60 * 24));
        return billOutDays;
    }

    /*Refreshes outstanding invoice list from backend*/
    void onRefresh() {


        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
            alAssignColl.add(Constants.OutstandingInvoices);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress),OutstandingHistoryActivity.this);
            } else {
                try {
                    Constants.isSync = true;
                    dialogCancelled = false;
                    new LoadingData().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            UtilConstants.showAlert(getString(R.string.no_network_conn),OutstandingHistoryActivity.this);
        }

    }

    /*AsyncTask to refresh outstanding invoices*/
    public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(OutstandingHistoryActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    OutstandingHistoryActivity.this, R.style.MyTheme);
                            builder.setMessage(R.string.do_want_cancel_sync)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                            R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {
                                                    dialogCancelled = true;

                                                    onBackPressed();
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface Dialog,
                                                        int id) {

                                                    try {
                                                        syncProgDialog
                                                                .show();
                                                        syncProgDialog
                                                                .setCancelable(true);
                                                        syncProgDialog
                                                                .setCanceledOnTouchOutside(false);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    dialogCancelled = false;

                                                }
                                            });
                            builder.show();
                        }
                    });
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                if(!OfflineManager.isOfflineStoreOpen()) {
                    try {
                        OfflineManager.openOfflineStore(OutstandingHistoryActivity.this, OutstandingHistoryActivity.this);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                }else {
                    try {

                        OfflineManager.refreshStoreSync(getApplicationContext(), OutstandingHistoryActivity.this, Constants.Fresh, concatCollectionStr);
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    @Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception,OutstandingHistoryActivity.this);
        if (errorBean.hasNoError()) {
                if (operation == Operation.OfflineRefresh.getValue()) {
                    closingProgressDialog();
                    Constants.isSync = false;
                        UtilConstants.showAlert(errorBean.getErrorMsg(), OutstandingHistoryActivity.this);
                }else if (operation == Operation.GetStoreOpen.getValue()) {
                    Constants.isSync = false;
                    closingProgressDialog();
                    UtilConstants.showAlert(getString(R.string.msg_offline_store_failure),
                            OutstandingHistoryActivity.this);
                }
        }else{
            Constants.isSync = false;
            closingProgressDialog();
            if(errorBean.isStoreFailed()) {
                if (!OfflineManager.isOfflineStoreOpen()) {
                    try {
                        Constants.isSync = true;
                        dialogCancelled = false;
                        new LoadingData().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Constants.displayMsgReqError(errorBean.getErrorCode(), OutstandingHistoryActivity.this);
                }
            }else{
                Constants.displayMsgReqError(errorBean.getErrorCode(), OutstandingHistoryActivity.this);
            }

        }
    }

    private void closingProgressDialog(){
        try {
            syncProgDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
            if (operation == Operation.OfflineRefresh.getValue()) {
                Constants.updateLastSyncTimeToTable(alAssignColl);
                closingProgressDialog();
                tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.OutstandingInvoices, Constants.TimeStamp,this));


                Constants.isSync = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            OutstandingHistoryActivity.this, R.style.MyTheme);
                    builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            getStatus();
                                        }
                                    });

                    builder.show();
            }else if (operation == Operation.GetStoreOpen.getValue() &&   OfflineManager.isOfflineStoreOpen()) {
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(OutstandingHistoryActivity.this);
                closingProgressDialog();
                UtilConstants.showAlert(getString(R.string.msg_offline_store_success),
                        OutstandingHistoryActivity.this);
            }
    }
}
