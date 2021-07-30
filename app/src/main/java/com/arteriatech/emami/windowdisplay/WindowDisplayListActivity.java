package com.arteriatech.emami.windowdisplay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.asyncTask.ValidateWindowDisplayAsyncTask;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.interfaces.AsyncTaskCallBack;
import com.arteriatech.emami.interfaces.OnClickInterface;
import com.arteriatech.emami.mbo.DmsDivQryBean;
import com.arteriatech.emami.mbo.SchemeBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

public class WindowDisplayListActivity extends AppCompatActivity implements OnClickInterface, AsyncTaskCallBack {

    private RecyclerView recyclerView;
    private ArrayList<SchemeBean> schemeModelArrayList = new ArrayList<>();
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrSelWinDispType = "";
    private String mStrBundleCPGUID32 = "";
    private String mStrBundleRetailerUID = "";
    private String mStrComingFrom = "";
    private TextView tvNoRecordFound;
    private String[][] arrWinDispType = null;
    private SchemeBean nextScreenBean = null;
    private ProgressDialog prgressDialog = null;
    private int numberOfDays = 0;
    private String[][] mArrayDistributors = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_display_list);
        ActionBarView.initActionBarView(this, true, getString(R.string.title_window_display));
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }
        if (!Constants.restartApp(WindowDisplayListActivity.this)) {
            TextView tvRetName = (TextView) findViewById(R.id.tv_reatiler_name);
            TextView tvUID = (TextView) findViewById(R.id.tv_reatiler_id);
            tvRetName.setText(mStrBundleRetName);
            tvUID.setText(mStrBundleRetID);
            tvNoRecordFound = (TextView) findViewById(R.id.no_record_found);
            tvNoRecordFound.setVisibility(View.GONE);
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            getWindowDispType();
            getDataFromOfflineDB();
        }

    }


    /*get data from offline db*/
    private void getDataFromOfflineDB() {
        try {
            DmsDivQryBean dmsDivQryBean = Constants.getDMSDIV("");
            schemeModelArrayList = OfflineManager.getSchemeWindowDisplay(arrWinDispType[0][1], mStrBundleCPGUID32.toUpperCase(),
                    mArrayDistributors[4][0], mArrayDistributors[5][0], mArrayDistributors[8][0], schemeModelArrayList,dmsDivQryBean.getDMSDivisionQry(),dmsDivQryBean.getDMSDivisionIDQry());
            WindowDisplayListAdapter windowDisplayListAdapter = new WindowDisplayListAdapter(WindowDisplayListActivity.this, schemeModelArrayList);
            recyclerView.setAdapter(windowDisplayListAdapter);
            windowDisplayListAdapter.onItemClickListener(this);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.writeLogError(Constants.error_txt + " window display no data from RegSchemeCat " + e.getMessage());
        }
        if (schemeModelArrayList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoRecordFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        nextScreenBean = schemeModelArrayList.get(position);
        prgressDialog = Constants.showProgressDialog(WindowDisplayListActivity.this, "", "Please wait...");
        new ValidateWindowDisplayAsyncTask(this, nextScreenBean.getSchemeGUID(), numberOfDays, mStrBundleCPGUID).execute();

    }

    /*get window display scheme type*/
    private void getWindowDispType() {
        String id = "";
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.RegSchemeCat + "'";
            arrWinDispType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, "");
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        String qryStr = Constants.ConfigTypsetTypeValues + "?$filter=" + Constants.Typeset + " eq '" +
                Constants.SC + "' and " + Constants.Types + " eq '" + Constants.WDSPINVDTR + "' ";
        try {
            String mStrDaysBefore = OfflineManager.getConfigValue(qryStr);
            numberOfDays = Integer.parseInt(mStrDaysBefore);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
    }


    @Override
    public void onStatus(boolean status, String values) {
        if (prgressDialog != null)
            Constants.hideProgressDialog(prgressDialog);
        if (status && nextScreenBean != null) {
            Intent sampleCollection = new Intent(WindowDisplayListActivity.this, WindowDisplayActivity.class);
            sampleCollection.putExtra(Constants.CPNo, mStrBundleRetID);
            sampleCollection.putExtra(Constants.CPUID, mStrBundleRetailerUID);
            sampleCollection.putExtra(Constants.RetailerName, mStrBundleRetName);
            sampleCollection.putExtra(Constants.CPGUID, mStrBundleCPGUID.toUpperCase());
            sampleCollection.putExtra(Constants.CPGUID32, mStrBundleCPGUID32.toUpperCase());
            sampleCollection.putExtra(Constants.comingFrom, mStrComingFrom);
            sampleCollection.putExtra(Constants.EXTRA_SCHEME_GUID, nextScreenBean.getSchemeGUID());
            sampleCollection.putExtra(Constants.EXTRA_SCHEME_NAME, nextScreenBean.getSchemeDesc());
            sampleCollection.putExtra(Constants.EXTRA_SCHEME_IS_SECONDTIME, nextScreenBean.isSecondTime());
            sampleCollection.putExtra(Constants.EXTRA_SCHEME_TYPE_ID, nextScreenBean.getSchemeTypeID());
            sampleCollection.putExtra(Constants.EXTRA_INVOICE_DATE, values);
            sampleCollection.putExtra(Constants.EXTRA_SCHEME_ID, nextScreenBean.getSchemeID());
            startActivity(sampleCollection);
        } else {
            Constants.dialogBoxWithButton(WindowDisplayListActivity.this, "", getString(R.string.window_display_not_valid), getString(R.string.ok), "", null);
        }
    }
}
