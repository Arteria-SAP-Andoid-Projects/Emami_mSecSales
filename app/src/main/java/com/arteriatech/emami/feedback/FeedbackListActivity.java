package com.arteriatech.emami.feedback;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.ViewPagerTabAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.UpdateListener;
import com.arteriatech.emami.mbo.FeedbackBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

/**
 *
 * Created by ${e10526} on ${11-07-2016}.
 *
 */
public class FeedbackListActivity extends AppCompatActivity implements UIListener, UpdateListener {

    String concatCollectionStr = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    ProgressDialog syncProgDialog = null;
    boolean dialogCancelled = false;
    private TabLayout tabLayout = null;

    //This is viewPager for collection sections device/normal
    private ViewPager viewPager;
    public TextView tv_last_sync_time_value = null;

    MenuItem menu_refresh,menu_sync =null;
    Menu menu;
    DeviceFeedbackListFragment deviceFragment = null;
    FeedbackListFragment historyFragment = null;
    ViewPagerTabAdapter viewPagerAdapter = null;

    //new
    public static UpdateListener updateListener = null;

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "",mStrCPGUID="";
    TextView retId,retName =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_feed_back_list));

        setContentView(R.layout.activity_feedback_list);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrCPGUID = bundleExtras.getString(Constants.CPGUID);
        }
        if (!Constants.restartApp(FeedbackListActivity.this)) {
            initUI();
        }
    }

    void initUI(){

        tv_last_sync_time_value = (TextView)findViewById(R.id.tv_last_sync_time_value);
        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Feedbacks, Constants.TimeStamp,this));

        retName = (TextView) findViewById(R.id.tv_reatiler_name);
        retId = (TextView) findViewById(R.id.tv_reatiler_id);

        retName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetID);

        if( menu_refresh!=null && menu_sync!=null){
            menu_sync.setVisible(false);
            menu_refresh.setVisible(true);

        }

        tabInitialize();
    }
    /*Initialize tab for collections*/
    private  void tabInitialize(){

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        tabLayout.setupWithViewPager(viewPager);
    }

    /*Setting up ViewPager for Tabs*/
    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());

        historyFragment = new FeedbackListFragment();
        deviceFragment = new DeviceFeedbackListFragment();

        Bundle bundleVisit = new Bundle();
        bundleVisit.putString(Constants.CPGUID, mStrCPGUID);
        bundleVisit.putString(Constants.CPNo, mStrBundleRetID);
        bundleVisit.putString(Constants.RetailerName, mStrBundleRetName);
        historyFragment.setArguments(bundleVisit);
        deviceFragment.setArguments(bundleVisit);

        viewPagerAdapter.addFrag(historyFragment,Constants.History);
        viewPagerAdapter.addFrag(deviceFragment,Constants.PendingSync);
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (menu_refresh != null && menu_sync != null) {
                    if (position == 0) {
                        menu_sync.setVisible(false);
                        menu_refresh.setVisible(true);
                    } else if (position == 1) {
                        if (checkDeviceFeedbacksAvailable()) {
                            menu_sync.setVisible(true);
                            menu_refresh.setVisible(false);
                        } else {
                            menu_sync.setVisible(false);
                            menu_refresh.setVisible(false);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_coll_his_list, menu);

        menu_refresh = menu.findItem(R.id.menu_refresh_coll);
        menu_sync = menu.findItem(R.id.menu_sync_coll);

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_coll_his_list, menu);
        menu_refresh = menu.findItem(R.id.menu_refresh_coll);
        menu_sync = menu.findItem(R.id.menu_sync_coll);
        menu_refresh.setVisible(true);
        return true;
    }

    //Check Device Collection is Available Or Null
    public boolean checkDeviceFeedbacksAvailable() {
        ArrayList<FeedbackBean> alFeedbackBean = new ArrayList<>();
        try {
            alFeedbackBean = OfflineManager.getDeviceFeedBackList(FeedbackListActivity.this, mStrCPGUID);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
        if (alFeedbackBean != null && alFeedbackBean.size() > 0) {

            return true;
        } else {

            return false;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.menu_sync_coll:
                onSync();
                break;
            case R.id.menu_refresh_coll:
                onRefresh();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }
    /*onSync of post device collection and refresh collections*/
    private void onSync(){
        if(deviceFragment.tempFeedbackDevList !=null && deviceFragment.tempFeedbackDevList.length>0) {

            if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                updateListener = FeedbackListActivity.this;
                //post device collections
                deviceFragment.postDeviceCollections();
            } else {
                UtilConstants.showAlert(getString(R.string.no_network_conn),FeedbackListActivity.this);
            }



        }
    }

    /*refresh collections*/
    void onRefresh()
    {

        if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
            alAssignColl.clear();
            concatCollectionStr="";
            alAssignColl.add(Constants.Feedbacks);
            alAssignColl.add(Constants.FeedbackItemDetails);
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

            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress),FeedbackListActivity.this);
            } else {
                try {
                    Constants.isSync = true;
                    dialogCancelled = false;
                    new LoadingData().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            UtilConstants.showAlert(getString(R.string.no_network_conn),FeedbackListActivity.this);
        }

    }

    /*AsyncTask for refresh collections*/
    public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(FeedbackListActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    FeedbackListActivity.this, R.style.MyTheme);
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

                    OfflineManager.refreshStoreSync(getApplicationContext(), FeedbackListActivity.this, Constants.Fresh, concatCollectionStr);
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
                try {
                    String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                    for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {

                        String colName = alAssignColl.get(incReq);
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }
                        if(colName.contains("(")){
                            String splitCollName[] = colName.split("\\(");
                            colName = splitCollName[0];
                        }
                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName, Constants.TimeStamp, syncTime
                        );
                    }
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                }

                syncProgDialog.dismiss();
                Constants.isSync = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            FeedbackListActivity.this, R.style.MyTheme);
                    builder.setMessage(getString(R.string.msg_error_occured_during_sync))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {

                                            dialog.cancel();
                                        }
                                    });

                    builder.show();


            }


    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {

            if (operation == Operation.OfflineRefresh.getValue()) {
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }


                try {
                    String syncTime = UtilConstants.getSyncHistoryddmmyyyyTime();
                    for (int incReq = 0; incReq < alAssignColl.size(); incReq++) {
                        String colName = alAssignColl.get(incReq);
                        if (colName.contains("?$")) {
                            String splitCollName[] = colName.split("\\?");
                            colName = splitCollName[0];
                        }

                        if(colName.contains("(")){
                            String splitCollName[] = colName.split("\\(");
                            colName = splitCollName[0];
                        }

                        Constants.events.updateStatus(Constants.SYNC_TABLE,
                                colName, Constants.TimeStamp, syncTime
                        );
                    }
                } catch (Exception exce) {
                    LogManager.writeLogError(Constants.SyncTableHistory + exce.getMessage());
                }


                tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.FinancialPostings, Constants.TimeStamp,this));


                try {
                    syncProgDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Constants.isSync = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            FeedbackListActivity.this, R.style.MyTheme);
                    builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {

                                            getRefreshFragment(0);

                                        }
                                    });

                    builder.show();
            }
    }

    /*refresh pager and fragments*/
    public void getRefreshFragment(int position){

        viewPagerAdapter = new ViewPagerTabAdapter(getSupportFragmentManager());
        historyFragment = new FeedbackListFragment();
        deviceFragment = new DeviceFeedbackListFragment();

        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.Feedbacks, Constants.TimeStamp,this));

        Bundle bundleVisit = new Bundle();
        bundleVisit.putString(Constants.CPGUID, mStrCPGUID);
        bundleVisit.putString(Constants.CPNo, mStrBundleRetID);
        bundleVisit.putString(Constants.RetailerName, mStrBundleRetName);
        historyFragment.setArguments(bundleVisit);
        deviceFragment.setArguments(bundleVisit);

        viewPagerAdapter.addFrag(historyFragment,Constants.History);
        viewPagerAdapter.addFrag(deviceFragment,Constants.PendingSync);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(position);

    }

    @Override
    public void onUpdate() {
        getRefreshFragment(1);
    }
}
