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
import com.arteriatech.emami.adapter.InvoiceHisListAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

/**
 * Created by e10526 on 2/17/2017.
 *
 */

public class InvoiceHistoryListActivity extends AppCompatActivity implements UIListener {

    private InvoiceHisListAdapter invoiceHisListAdapter = null;
    private ArrayList<InvoiceHistoryBean> alInvoiceBean;
    private ArrayList<InvoiceHistoryBean> alTempInvoiceBean;
    private String mStrBundleRetID = "",mStrBundleCPGUID="";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";

    Spinner spinvHisStatus;

    String selectedStatus;
    //new
    TextView tvEmptyLay = null;
    String concatCollectionStr = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    ProgressDialog syncProgDialog = null;
    boolean dialogCancelled = false;

    private String[][] arrayInvStatusVal;
    EditText edNameSearch;

    //new
    TextView tvRetName = null, tvUID = null;
    ListView lv_inv_his_list = null;
    TextView tv_last_sync_time_value;
    private Bundle bundleExtras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_BillHistory));

        setContentView(R.layout.activity_inv_his_list);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
        }
        if (!Constants.restartApp(InvoiceHistoryListActivity.this)) {
            initUI();
        }
    }

    void initUI(){
        lv_inv_his_list = (ListView)findViewById(R.id.lv_route_ret_list);


        tv_last_sync_time_value = (TextView)findViewById(R.id.tv_last_sync_time_value);
        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SSINVOICES, Constants.TimeStamp,this));

        tvRetName = (TextView) findViewById(R.id.tv_bill_hist_ret_name);
        tvUID = (TextView) findViewById(R.id.tv_bill_hist_uid);

        tvEmptyLay = (TextView)findViewById(R.id.tv_empty_lay);

        spinvHisStatus = (Spinner)findViewById(R.id.spin_invoice_his_status_id);

        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetUID);
        edNameSearch = (EditText) findViewById(R.id.ed_invoice_search);
        edNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                invoiceHisListAdapter.getFilter().filter(cs); //Filter from my adapter
                invoiceHisListAdapter.notifyDataSetChanged(); //Update my view
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
        getInvStatus();
        getStatus();



    }
    private void clearEditTextSearchBox(){
        if(edNameSearch!=null && edNameSearch.getText().toString().length()>0)
            edNameSearch.setText("");
    }

    /*gets status for invoices*/
    private void getStatus(){
        if(arrayInvStatusVal ==null){
            arrayInvStatusVal = new String[2][1];
            arrayInvStatusVal[0][0]="";
            arrayInvStatusVal[1][0]="";
        }

        String[][] tempStatusArray = new String[3][arrayInvStatusVal[0].length+1];
        tempStatusArray[0][0] = Constants.str_00;
        tempStatusArray[1][0] = Constants.All;
        tempStatusArray[2][0] = "";
        for(int i=1; i<arrayInvStatusVal[0].length+1;i++){
            tempStatusArray[0][i] = arrayInvStatusVal[0][i-1];
            tempStatusArray[1][i] = arrayInvStatusVal[1][i-1];
            tempStatusArray[2][i] = arrayInvStatusVal[2][i-1];
        }
        arrayInvStatusVal = tempStatusArray;

        ArrayAdapter<String> productCategoryAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, arrayInvStatusVal[1]);
        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spinvHisStatus.setAdapter(productCategoryAdapter);


        spinvHisStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {

                selectedStatus = arrayInvStatusVal[0][position];

                clearEditTextSearchBox();
                if (selectedStatus.equalsIgnoreCase(Constants.str_00)) {

                    getInvoiceList("");
                }else{
                    getInvoiceList(selectedStatus);
                }

            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }


    /*gets List of invoices*/
    private void getInvoiceList(String status){
        try {
            if(status.equalsIgnoreCase("")){

//                alInvoiceBean = OfflineManager.getInvoiceHistoryList(Constants.SSINVOICES+"?$filter="+ Constants.SoldToID+" eq '"+mStrBundleRetID+"' " +
//                        "and "+ Constants.InvoiceDate+" ge datetime'" + Constants.getLastMonthDate() + "' ",getApplicationContext(),status,mStrBundleCPGUID);


                alInvoiceBean = OfflineManager.getInvoiceHistoryList(Constants.SSINVOICES+"?$filter="+ Constants.SoldToID+" eq '"+mStrBundleRetID+"' " +
                        " ",getApplicationContext(),status,mStrBundleCPGUID);

                alTempInvoiceBean =new ArrayList<>();
                alTempInvoiceBean.addAll(alInvoiceBean);

            }else{
                alInvoiceBean.clear();
                switch (status) {
                    case "01": {
                        for (InvoiceHistoryBean item : alTempInvoiceBean) {
                            if (item.getInvoiceStatus().equalsIgnoreCase("01"))
                                alInvoiceBean.add(item);
                        }
                    }
                    break;

                    case "02": {
                        for (InvoiceHistoryBean item : alTempInvoiceBean) {
                            if (item.getInvoiceStatus().equalsIgnoreCase("02"))
                                alInvoiceBean.add(item);
                        }
                    }
                    break;

                    case "03": {
                        for (InvoiceHistoryBean item : alTempInvoiceBean) {
                            if (item.getInvoiceStatus().equalsIgnoreCase("03"))
                                alInvoiceBean.add(item);
                        }
                    }
                    break;

                }


            }

            InvoiceHistoryListActivity.this.invoiceHisListAdapter = new InvoiceHisListAdapter( InvoiceHistoryListActivity.this, R.layout.activity_invoice_history_list,alInvoiceBean,bundleExtras);
            lv_inv_his_list.setEmptyView(findViewById(R.id.tv_empty_lay) );
            lv_inv_his_list.setAdapter(invoiceHisListAdapter);
            InvoiceHistoryListActivity.this.invoiceHisListAdapter.notifyDataSetChanged();

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }




    /*get different status for invoices*/
    private  void getInvStatus(){
        try{
            String mStrConfigQry = Constants.ValueHelps + "?$filter="+ Constants.PropName+" eq '"+ Constants.PaymentStatusID+"' and " + Constants.EntityType+" eq '"+ Constants.SSInvoice+"' &$orderby="+ Constants.ID+"%20asc";

            arrayInvStatusVal = OfflineManager.getConfigList(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error+" : " + e.getMessage());
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

    /*Refresh Invoice list from backEnd*/
    void onRefresh()
    {

        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            alAssignColl.clear();
            concatCollectionStr="";
            alAssignColl.add(Constants.SSInvoiceItemDetails);
            alAssignColl.add(Constants.SSINVOICES);
            concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress),InvoiceHistoryListActivity.this);
            } else {
                try {
                    Constants.isSync = true;
                    dialogCancelled = false;
                    new InvoiceHistoryListActivity.LoadingData().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            UtilConstants.showAlert(getString(R.string.no_network_conn),InvoiceHistoryListActivity.this);
        }
    }

    /*AsyncTask to refresh Invoices from backend*/
    public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(InvoiceHistoryListActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(false);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();
            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    InvoiceHistoryListActivity.this, R.style.MyTheme);
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
                        OfflineManager.openOfflineStore(InvoiceHistoryListActivity.this, InvoiceHistoryListActivity.this);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                }else {
                    try {
                        OfflineManager.refreshStoreSync(getApplicationContext(), InvoiceHistoryListActivity.this, Constants.Fresh, concatCollectionStr);
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
        ErrorBean errorBean = Constants.getErrorCode(operation, exception,InvoiceHistoryListActivity.this);
        if (errorBean.hasNoError()) {
            if (operation == Operation.OfflineRefresh.getValue()) {
                Constants.isSync = false;
                closePrgDialog();
                UtilConstants.showAlert(errorBean.getErrorMsg(), InvoiceHistoryListActivity.this);
            }else if (operation == Operation.GetStoreOpen.getValue()) {
                Constants.isSync = false;
                closePrgDialog();
                UtilConstants.showAlert(getString(R.string.msg_offline_store_failure),
                        InvoiceHistoryListActivity.this);
            }
        }else{
            closePrgDialog();
            Constants.isSync = false;
            if(errorBean.isStoreFailed()) {
                if (!OfflineManager.isOfflineStoreOpen()) {
                    try {
                        Constants.isSync = true;
                        new LoadingData().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Constants.displayMsgReqError(errorBean.getErrorCode(), InvoiceHistoryListActivity.this);
                }
            }else{
                Constants.displayMsgReqError(errorBean.getErrorCode(), InvoiceHistoryListActivity.this);
            }
        }
    }

    private void closePrgDialog(){
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
                tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SSINVOICES, Constants.TimeStamp,this));
               closePrgDialog();
                Constants.isSync = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            InvoiceHistoryListActivity.this, R.style.MyTheme);
                    builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            getInvStatus();
                                            getStatus();
                                        }
                                    });

                    builder.show();
            }else if (operation == Operation.GetStoreOpen.getValue() &&   OfflineManager.isOfflineStoreOpen()) {
//                Constants.ReIntilizeStore =false;
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                Constants.setSyncTime(InvoiceHistoryListActivity.this);
                closePrgDialog();
                UtilConstants.showAlert(getString(R.string.msg_offline_store_success),
                        InvoiceHistoryListActivity.this);
            }
    }
}
