package com.arteriatech.emami.visit;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.NewLaunchedProductAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.Config;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

/**
 * Created by ${e10526} on ${17-12-2016}.
 */
@SuppressLint("NewApi")
public class NewProductListActivity extends AppCompatActivity implements UIListener {
    private String mStrStatus = "", mStrDescription = "";

    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";

    ArrayList<Config> visitMatList;
    ListView lv_focused_prod_list = null;
    TextView tvRetName = null, tvUID = null, tv_focused_product_header = null;
    EditText edMaterialNameSearch;
    private NewLaunchedProductAdapter newLaunchedProductAdapter;

    String concatCollectionStr = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    ProgressDialog syncProgDialog = null;
    boolean dialogCancelled = false;
    TextView tv_last_sync_time_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_focused_product_list);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrStatus = bundleExtras.getString(Constants.ID);
            mStrDescription = bundleExtras.getString(Constants.Description);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
        }
        if (!Constants.restartApp(NewProductListActivity.this)) {
            initUI();
            getSegmentedMaterials();
            setValuesIntoUI();
        }


        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, mStrDescription);
    }

    private void initUI() {
        tvRetName = (TextView) findViewById(R.id.tv_reatiler_name);
        tvUID = (TextView) findViewById(R.id.tv_reatiler_id);
        tv_last_sync_time_value = (TextView) findViewById(R.id.tv_last_sync_time_value);

        tv_focused_product_header = (TextView) findViewById(R.id.tv_focused_product_header);
        lv_focused_prod_list = (ListView) findViewById(R.id.lv_focused_prod_list);
        edMaterialNameSearch = (EditText) findViewById(R.id.ed_mat_name_search);
        edMaterialNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                newLaunchedProductAdapter.getFilter().filter(cs); //Filter from my adapter
                newLaunchedProductAdapter.notifyDataSetChanged(); //Update my view
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    private void clearEditTextSearchBox() {
        if (edMaterialNameSearch != null && edMaterialNameSearch.getText().toString().length() > 0)
            edMaterialNameSearch.setText("");
    }

    private void getSegmentedMaterials() {
        visitMatList = new ArrayList<>();
        String mStrFocusedProductQry = Constants.SegmentedMaterials + "?$filter="
                + Constants.SegmentId + " eq '" + mStrStatus + "' &$orderby=" + Constants.MaterialDesc + "%20asc";
        try {
            visitMatList = OfflineManager.getFocusedProdList(mStrFocusedProductQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    private void setValuesIntoUI() {
        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SegmentedMaterials, Constants.TimeStamp, this));
        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetUID);
        tv_focused_product_header.setText(mStrDescription);

        loadAdapter();
    }

    private void loadAdapter() {
        newLaunchedProductAdapter = new NewLaunchedProductAdapter(this, visitMatList);
        lv_focused_prod_list.setEmptyView(findViewById(R.id.tv_empty_lay));
        lv_focused_prod_list.setAdapter(newLaunchedProductAdapter);
        newLaunchedProductAdapter.notifyDataSetChanged();
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

    /*Refresh segmented materials from backEnd*/
    void onRefresh() {
        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            alAssignColl.clear();
            concatCollectionStr = "";
            alAssignColl.add(Constants.SegmentedMaterials);
            for (int incVal = 0; incVal < alAssignColl.size(); incVal++) {
                if (incVal == 0 && incVal == alAssignColl.size() - 1) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                } else if (incVal == 0) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                } else if (incVal == alAssignColl.size() - 1) {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal);
                } else {
                    concatCollectionStr = concatCollectionStr + alAssignColl.get(incVal) + ", ";
                }
            }

          /*  if (Constants.iSAutoSync) {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress),NewProductListActivity.this);
            } else {*/
            try {
                Constants.isSync = true;
                dialogCancelled = false;
                new LoadingData().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            /* }*/

        } else {
            UtilConstants.showAlert(getString(R.string.no_network_conn), NewProductListActivity.this);
        }
    }

    /*AsyncTask to refresh segmented materials from backend*/
    public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(NewProductListActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    NewProductListActivity.this, R.style.MyTheme);
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
                try {

                    OfflineManager.refreshStoreSync(getApplicationContext(), NewProductListActivity.this, Constants.Fresh, concatCollectionStr);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
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
    public void onRequestError(int operation, Exception e) {
        if (operation == Operation.OfflineRefresh.getValue()) {
            closeProgressDialog();
            UtilConstants.showAlert(getString(R.string.msg_error_occured_during_sync), NewProductListActivity.this);

        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineRefresh.getValue()) {
            Constants.updateLastSyncTimeToTable(alAssignColl);
            closeProgressDialog();
            tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.SegmentedMaterials, Constants.TimeStamp, this));

            clearEditTextSearchBox();
            UtilConstants.showAlert(getString(R.string.msg_sync_successfully_completed), NewProductListActivity.this);
            getSegmentedMaterials();
            loadAdapter();
        }
    }

    private void closeProgressDialog() {
        try {
            syncProgDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
