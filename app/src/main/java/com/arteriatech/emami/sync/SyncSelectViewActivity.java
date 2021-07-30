package com.arteriatech.emami.sync;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.upgrade.AppUpgradeConfig;
import com.arteriatech.emami.asyncTask.SyncMustSellAsyncTask;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.interfaces.MessageWithBooleanCallBack;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.notification.NotificationSetClass;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

/**
 * This class displays check box type sync selections.User select particular check boxes and
 * press sync button it navigates to Sync activity.
 */
@SuppressLint("NewApi")
public class SyncSelectViewActivity extends AppCompatActivity implements UIListener, View.OnClickListener {

    ProgressDialog syncProgDialog;
    String concatCollectionStr = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    boolean backButtonPressed = false;
    private CheckBox ch_all, ch_sales_persons, chAttendeces, ch_outstanding,
            chAuth, ch_visits,
            ch_route_plan, ch_channel_partner, ch_financial_postings,
            ch_ss_invoices, ch_value_helps, ch_ss_targets, ch_db_stock, ch_ss_orders, ch_visits_act, ch_focused_prd,
            ch_merch_reivew, ch_comp_info, ch_trends, ch_ret_behaviour, ch_my_stk, ch_return_order, ch_complaints, ch_alerts;
    private boolean dialogCancelled = false;
    private CheckBox ch_documents, ch_schemes;
    private CheckBox ch_expense;
    String sharedVal = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.lbl_sync_sel));
        setContentView(R.layout.activity_sync_select_view);
        if (!Constants.restartApp(SyncSelectViewActivity.this)) {
            onInitUI();
            setValuesToUI();
        }


    }

    private void setValuesToUI() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);

        ch_all.setOnClickListener(this);
        ch_all.setVisibility(View.VISIBLE);
        chAuth.setVisibility(View.VISIBLE);
        sharedVal = sharedPreferences.getString("isStartCloseEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/MC_ATTND")) {
            chAttendeces.setVisibility(View.VISIBLE);
        } else {
            chAttendeces.setVisibility(View.GONE);
        }


        ch_channel_partner.setVisibility(View.VISIBLE);


        sharedVal = sharedPreferences.getString("isRouteEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_ROUTPLAN")) {
            ch_route_plan.setVisibility(View.VISIBLE);
        } else {
            ch_route_plan.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString("isCollHistory", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_COLLHIS")) {
            ch_financial_postings.setVisibility(View.VISIBLE);
        } else {
            ch_financial_postings.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isSOCreateKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isSOCreateTcode)) {
            ch_ss_invoices.setVisibility(View.VISIBLE);
        } else {
            ch_ss_invoices.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString("isVisitCreate", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_VST")) {
            ch_visits.setVisibility(View.VISIBLE);
        } else {
            ch_visits.setVisibility(View.GONE);
        }

        ch_sales_persons.setVisibility(View.VISIBLE);

        ch_value_helps.setVisibility(View.VISIBLE);

        ch_visits_act.setVisibility(View.VISIBLE);

        sharedVal = sharedPreferences.getString("iFocusedProductEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_FOCPROD")) {
            ch_focused_prd.setVisibility(View.VISIBLE);
        } else {
            ch_focused_prd.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isMerchReviewKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isMerchReviewTcode)) {
            ch_merch_reivew.setVisibility(View.VISIBLE);
        } else {
            ch_merch_reivew.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString("isMyTargetsEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_MYTRGTS")) {
            ch_ss_targets.setVisibility(View.VISIBLE);
        } else {
            ch_ss_targets.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString("isCompInfoEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_COMPINFO")) {
            ch_comp_info.setVisibility(View.VISIBLE);
        } else {
            ch_comp_info.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString("isTrends", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_TRENDS")) {
            ch_trends.setVisibility(View.VISIBLE);
        } else {
            ch_trends.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString("isBehaviourEnabled", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_SPCP_EVAL")) {
            ch_ret_behaviour.setVisibility(View.VISIBLE);
        } else {
            ch_ret_behaviour.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString("isMyStock", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SF_MYSTK")) {
            ch_my_stk.setVisibility(View.VISIBLE);
        } else {
            ch_my_stk.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isReturnOrderCreateEnabled, "");
        if (sharedVal.equalsIgnoreCase(Constants.isReturnOrderTcode)) {
            ch_return_order.setVisibility(View.VISIBLE);
        } else {
            ch_return_order.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isComplintsListKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isComplintsListTcode)) {
            ch_complaints.setVisibility(View.VISIBLE);
        } else {
            ch_complaints.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isVisualAidKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isVisualAidTcode)) {
            ch_documents.setVisibility(View.VISIBLE);
        } else {
            ch_documents.setVisibility(View.GONE);
        }

        ch_alerts.setVisibility(View.VISIBLE);

        sharedVal = sharedPreferences.getString(Constants.isExpenseEntryKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isExpenseEntryTcode)) {
            ch_expense.setVisibility(View.VISIBLE);
        } else {
            ch_expense.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isDBStockKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isDBStockTcode)) {
            // Todo check current day cpstock items synced or not
//        if(!Constants.isSpecificCollTodaySyncOrNot(Constants.getLastSyncDate(Constants.SYNC_TABLE, Constants.Collections,
//                Constants.CPStockItems, Constants.TimeStamp,SyncSelectViewActivity.this))) {
            ch_db_stock.setVisibility(View.VISIBLE);
//        }else{
//            ch_db_stock.setVisibility(View.GONE);
//        }
        } else {
            ch_db_stock.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isSchemeKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isSchemeTcode)) {
            ch_schemes.setVisibility(View.VISIBLE);
        } else {
            ch_schemes.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString("isOutstandingHistory", "");
        if (sharedVal.equalsIgnoreCase("/ARTEC/SS_OUTSTND")) {
            ch_outstanding.setVisibility(View.VISIBLE);
        } else {
            ch_outstanding.setVisibility(View.GONE);
        }

        sharedVal = sharedPreferences.getString(Constants.isSecondarySalesListKey, "");
        if (sharedVal.equalsIgnoreCase(Constants.isSecondarySalesListTcode)) {
            ch_ss_orders.setVisibility(View.VISIBLE);
        } else {
            ch_ss_orders.setVisibility(View.GONE);
        }

    }

    /*
     * TODO This method initialize UI
     */
    private void onInitUI() {
        ch_all = (CheckBox) findViewById(R.id.ch_all);
        chAuth = (CheckBox) findViewById(R.id.ch_authorization);
        ch_visits_act = (CheckBox) findViewById(R.id.ch_visits_act);
        ch_sales_persons = (CheckBox) findViewById(R.id.ch_sales_persons);
        chAttendeces = (CheckBox) findViewById(R.id.ch_attendances_lists);
        ch_visits = (CheckBox) findViewById(R.id.ch_visits);
        ch_route_plan = (CheckBox) findViewById(R.id.ch_route_plan);
        ch_channel_partner = (CheckBox) findViewById(R.id.ch_channel_partner);
        ch_focused_prd = (CheckBox) findViewById(R.id.ch_focused_prd);
        ch_financial_postings = (CheckBox) findViewById(R.id.ch_financial_postings);
        ch_ss_invoices = (CheckBox) findViewById(R.id.ch_ss_invoices);
        ch_ss_orders = (CheckBox) findViewById(R.id.ch_ss_orders);
        ch_value_helps = (CheckBox) findViewById(R.id.ch_value_helps);
        ch_outstanding = (CheckBox) findViewById(R.id.ch_outstanding);
        ch_merch_reivew = (CheckBox) findViewById(R.id.ch_merch_reivew);
        ch_db_stock = (CheckBox) findViewById(R.id.ch_db_stock);
        ch_ss_targets = (CheckBox) findViewById(R.id.ch_ss_targets);
        ch_comp_info = (CheckBox) findViewById(R.id.ch_comp_info);
        ch_ret_behaviour = (CheckBox) findViewById(R.id.ch_ret_behaviour);
        ch_trends = (CheckBox) findViewById(R.id.ch_trends);
        ch_my_stk = (CheckBox) findViewById(R.id.ch_my_stk);
        ch_return_order = (CheckBox) findViewById(R.id.ch_return_order);
        ch_complaints = (CheckBox) findViewById(R.id.ch_complaints);
        ch_documents = (CheckBox) findViewById(R.id.ch_documents);
        ch_schemes = (CheckBox) findViewById(R.id.ch_schemes);
        ch_alerts = (CheckBox) findViewById(R.id.ch_alerts);
        ch_expense = (CheckBox) findViewById(R.id.ch_expense);
        backButtonPressed = false;
    }

    @Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception, SyncSelectViewActivity.this);
        if (errorBean.hasNoError()) {
            if (operation == Operation.OfflineRefresh.getValue()) {
                closingProgressDialog();
                SyncSelectionActivity.stopService(SyncSelectViewActivity.this);
                Constants.isSync = false;
                syncCompletedWithErrorDialog(errorBean.getErrorMsg());
            } else if (operation == Operation.GetStoreOpen.getValue()) {
                closingProgressDialog();
                SyncSelectionActivity.stopService(SyncSelectViewActivity.this);
                Constants.isSync = false;
                syncCompletedWithErrorDialog(errorBean.getErrorMsg());
            }

        } else {
            Constants.isSync = false;
            SyncSelectionActivity.stopService(SyncSelectViewActivity.this);


            if (errorBean.isStoreFailed()) {
                OfflineManager.offlineStore = null;
                OfflineManager.options = null;

                if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                    try {
                        closingProgressDialog();
                        Constants.Entity_Set.clear();
                        Constants.AL_ERROR_MSG.clear();
                        Constants.isSync = true;
                        dialogCancelled = false;
                        new AsyncSyncData().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    closingProgressDialog();
                    Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectViewActivity.this);
                }
            } else {
                closingProgressDialog();
                Constants.displayMsgReqError(errorBean.getErrorCode(), SyncSelectViewActivity.this);
            }


        }
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {
        if (operation == Operation.OfflineRefresh.getValue()) {
            if (alAssignColl.contains(Constants.UserProfileAuthSet)) {
                try {
                    OfflineManager.getAuthorizations(getApplicationContext());
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }
            if (alAssignColl.contains(Constants.RoutePlans) || alAssignColl.contains(Constants.ChannelPartners) || alAssignColl.contains(Constants.Visits)) {
                Constants.alTodayBeatRet.clear();
                Constants.TodayTargetRetailersCount = Constants.getVisitTargetForToday();
                Constants.TodayActualVisitRetailersCount = Constants.getVisitedRetailerCount(Constants.alTodayBeatRet);
            }
            if (alAssignColl.contains(Constants.SSSOs) || alAssignColl.contains(Constants.Targets)) {
                Constants.loadingTodayAchived(SyncSelectViewActivity.this, Constants.alTodayBeatRet);
            }
            if (alAssignColl.contains(Constants.Visits) || alAssignColl.contains(Constants.ChannelPartners)) {
                Constants.setBirthdayListToDataValut(SyncSelectViewActivity.this);
                Constants.setBirthDayRecordsToDataValut(SyncSelectViewActivity.this);
                setAppointmentNotification();
            }
            if (alAssignColl.contains(Constants.Alerts)) {
                Constants.setAlertsRecordsToDataValut(SyncSelectViewActivity.this);
            }

            try {
                new SyncMustSellAsyncTask(SyncSelectViewActivity.this, new MessageWithBooleanCallBack() {
                    @Override
                    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
                        Log.d("clickedStatus Req", clickedStatus + "");
                        setUI();
                    }
                }, Constants.Fresh).execute();
            } catch (Exception e) {
                setUI();
                e.printStackTrace();
            }

               /* Constants.updateLastSyncTimeToTable(alAssignColl);
                closingProgressDialog();
                Constants.isSync = false;
                new SyncSelectionActivity().stopService(SyncSelectViewActivity.this);
                syncCompletedDialog();*/
        } else if (operation == Operation.GetStoreOpen.getValue() && OfflineManager.isOfflineStoreOpen()) {
//                Constants.ReIntilizeStore =false;
            new SyncSelectionActivity().stopService(SyncSelectViewActivity.this);
            try {
                OfflineManager.getAuthorizations(getApplicationContext());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.setSyncTime(SyncSelectViewActivity.this);

            // Staring MustSell Code Snippet
            try {
                new SyncMustSellAsyncTask(SyncSelectViewActivity.this, new MessageWithBooleanCallBack() {
                    @Override
                    public void clickedStatus(boolean clickedStatus, String errorMsg, ErrorBean errorBean) {
                        Log.d("clickedStatus Req", clickedStatus + "");
                        setStoreOpenUI();
                    }
                }, Constants.All).execute();
            } catch (Exception e) {
                setStoreOpenUI();
                e.printStackTrace();
            }
            // Ending MustSell Code Snippet


        }
    }

    private void setStoreOpenUI() {
        closingProgressDialog();
        syncCompletedDialog();
    }

    private void setUI() {
        Constants.updateLastSyncTimeToTable(alAssignColl);
        closingProgressDialog();
        Constants.isSync = false;
        new SyncSelectionActivity().stopService(SyncSelectViewActivity.this);
        syncCompletedDialog();
    }

    @Override
    protected void onDestroy() {
        new SyncSelectionActivity().stopService(SyncSelectViewActivity.this);
        super.onDestroy();

    }

    private void setAppointmentNotification() {
        new NotificationSetClass(this);

    }

    private void syncCompletedWithErrorDialog(String err_msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                SyncSelectViewActivity.this, R.style.MyTheme);
        builder.setMessage(err_msg)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                onBackPressed();

                            }
                        });

        builder.show();
    }

    private void syncCompletedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                SyncSelectViewActivity.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                if (!AppUpgradeConfig.getUpdateAvailability(OfflineManager.offlineStore, SyncSelectViewActivity.this, "", true)) {
                                    onBackPressed();
                                }

                            }
                        });

        builder.show();
    }

    private void closingProgressDialog() {
        try {
            syncProgDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sync_back, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:
                onSync();
                break;
            case R.id.menu_back:
                backButtonPressed = true;
                onBackPressed();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void onSync() {
        if (!ch_visits_act.isChecked() && !chAuth.isChecked() && !chAttendeces.isChecked() && !ch_sales_persons.isChecked()
                && !ch_visits.isChecked() && !ch_route_plan.isChecked()
                && !ch_channel_partner.isChecked() && !ch_financial_postings.isChecked() && !ch_ss_invoices.isChecked()
                && !ch_value_helps.isChecked() && !ch_ss_targets.isChecked()
                && !ch_outstanding.isChecked() && !ch_db_stock.isChecked()
                && !ch_focused_prd.isChecked() && !ch_merch_reivew.isChecked()
                && !ch_comp_info.isChecked() && !ch_trends.isChecked()
                && !ch_ret_behaviour.isChecked() && !ch_my_stk.isChecked() && !ch_return_order.isChecked() && !ch_complaints.isChecked() && !ch_documents.isChecked()
                && !ch_expense.isChecked() && !ch_alerts.isChecked() && !ch_schemes.isChecked() && !ch_ss_orders.isChecked()) {
            UtilConstants.showAlert(getString(R.string.plz_select_one_coll), SyncSelectViewActivity.this);
        } else {
            if (Constants.iSAutoSync) {
                UtilConstants.showAlert(getString(R.string.alert_auto_sync_is_progress), SyncSelectViewActivity.this);
            } else {
                if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                    try {
                        Constants.SyncTypeID = Constants.str_02;
                        Constants.Entity_Set.clear();
                        Constants.AL_ERROR_MSG.clear();
                        Constants.isSync = true;
                        dialogCancelled = false;
                        SyncSelectionActivity.startService(this);
                        SyncSelectionActivity.startServiceMustSells(this);// MustSell
                        new AsyncSyncData().execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    UtilConstants.onNoNetwork(SyncSelectViewActivity.this);
                }
            }
        }
    }

    private void assignCollToArrayList() {
        alAssignColl.clear();
        concatCollectionStr = "";

        if (chAuth.isChecked() ? true : false) {
            alAssignColl.add(Constants.UserProfileAuthSet);
        }
        if (ch_sales_persons.isChecked() ? true : false) {
            alAssignColl.add(Constants.SalesPersons);
            alAssignColl.add(Constants.CPSPRelations);

        }
        if (chAttendeces.isChecked() ? true : false) {
            alAssignColl.add(Constants.Attendances);
        }

        if (ch_visits.isChecked() ? true : false) {
            alAssignColl.add(Constants.Visits);
        }


        if (ch_route_plan.isChecked() ? true : false) {
            alAssignColl.add(Constants.RoutePlans);
            alAssignColl.add(Constants.RouteSchedulePlans);
            alAssignColl.add(Constants.RouteSchedules);
        }


        if (ch_channel_partner.isChecked() ? true : false) {
            alAssignColl.add(Constants.ChannelPartners);
            alAssignColl.add(Constants.CPDMSDivisions);
        }

        if (ch_ss_invoices.isChecked() ? true : false) {
            alAssignColl.add(Constants.SSInvoiceItemDetails);
            alAssignColl.add(Constants.SSINVOICES);
        }

        if (ch_ss_orders.isChecked() ? true : false) {
            alAssignColl.add(Constants.SSSOs);
            alAssignColl.add(Constants.SSSoItemDetails);
        }


        if (ch_financial_postings.isChecked() ? true : false) {
            alAssignColl.add(Constants.FinancialPostingItemDetails);
            alAssignColl.add(Constants.FinancialPostings);
        }

        if (ch_visits_act.isChecked() ? true : false) {
            alAssignColl.add(Constants.VisitActivities);
        }


        if (ch_value_helps.isChecked() ? true : false) {
            alAssignColl.add(Constants.Brands);
            alAssignColl.add(Constants.MaterialCategories);
            alAssignColl.add(Constants.BrandsCategories);
            alAssignColl.add(Constants.OrderMaterialGroups);
            alAssignColl.add(Constants.ValueHelps);
            alAssignColl.add(Constants.ConfigTypsetTypeValues);
            alAssignColl.add(Constants.ConfigTypesetTypes);
            alAssignColl.add(Constants.PricingConditions);
            alAssignColl.add(Constants.ExpenseConfigs);
            alAssignColl.add(Constants.ExpenseAllowances);
            alAssignColl.add(Constants.SSInvoiceTypes);

        }

        if (ch_outstanding.isChecked() ? true : false) {
            alAssignColl.add(Constants.OutstandingInvoices);
            alAssignColl.add(Constants.OutstandingInvoiceItemDetails);
        }
        if (ch_focused_prd.isChecked() ? true : false) {
            alAssignColl.add(Constants.SegmentedMaterials);
        }
        if (ch_merch_reivew.isChecked() ? true : false) {
            alAssignColl.add(Constants.MerchReviews);
            alAssignColl.add(Constants.MerchReviewImages);
        }

        if (ch_db_stock.isChecked() ? true : false) {
            alAssignColl.add(Constants.CPStockItems);
            alAssignColl.add(Constants.CPStockItemSnos);
        }

        if (ch_ss_targets.isChecked() ? true : false) {
            alAssignColl.add(Constants.KPISet);
            alAssignColl.add(Constants.Targets);
            alAssignColl.add(Constants.TargetItems);
            alAssignColl.add(Constants.KPIItems);

        }

        if (ch_comp_info.isChecked() ? true : false) {
            alAssignColl.add(Constants.CompetitorInfos);
            alAssignColl.add(Constants.CompetitorMasters);
        }

        if (ch_trends.isChecked() ? true : false) {
            alAssignColl.add(Constants.Performances);
        }
        if (ch_ret_behaviour.isChecked() ? true : false) {
            alAssignColl.add(Constants.SPChannelEvaluationList);
        }
        if (ch_my_stk.isChecked()) {
            alAssignColl.add(Constants.SPStockItems);
            alAssignColl.add(Constants.SPStockItemSNos);
        }
        if (ch_return_order.isChecked()) {
            alAssignColl.add(Constants.SSROs);
            alAssignColl.add(Constants.SSROItemDetails);
        }
        if (ch_complaints.isChecked()) {
            alAssignColl.add(Constants.Complaints);
        }
        if (ch_documents.isChecked()) {
            alAssignColl.add(Constants.Documents);
        }
        if (ch_alerts.isChecked()) {
            alAssignColl.add(Constants.Alerts);
        }
        if (ch_expense.isChecked()) {
            alAssignColl.add(Constants.Expenses);
            alAssignColl.add(Constants.ExpenseItemDetails);
            alAssignColl.add(Constants.ExpenseDocuments);
        }
        if (ch_schemes.isChecked()) {
            alAssignColl.add(Constants.Schemes);
            alAssignColl.add(Constants.SchemeItemDetails);
            alAssignColl.add(Constants.SchemeGeographies);
            alAssignColl.add(Constants.SchemeCPs);
            alAssignColl.add(Constants.SchemeSalesAreas);
            alAssignColl.add(Constants.SchemeCostObjects);
            alAssignColl.add(Constants.CPGeoClassifications);
            alAssignColl.add(Constants.SchemeCPDocuments);
            alAssignColl.add(Constants.SchemeSlabs);
            alAssignColl.add(Constants.Claims);
            alAssignColl.add(Constants.ClaimItemDetails);
            alAssignColl.add(Constants.ClaimDocuments);
            alAssignColl.add(Constants.SchemeFreeMatGrpMaterials);
        }

        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ch_all:
                if (ch_all.isChecked()) {
                    checkAll();
                } else {
                    unCheckAll();
                }
                break;
            case R.id.tv_back:
                onBackPressed();
                break;
            case R.id.tv_submit:
                onSync();
                break;

        }

    }

    private void checkAll() {
        if (ch_sales_persons.getVisibility() == View.VISIBLE)
            ch_sales_persons.setChecked(true);
        if (chAttendeces.getVisibility() == View.VISIBLE)
            chAttendeces.setChecked(true);
        if (ch_outstanding.getVisibility() == View.VISIBLE)
            ch_outstanding.setChecked(true);
        if (chAuth.getVisibility() == View.VISIBLE)
            chAuth.setChecked(true);
        if (ch_visits.getVisibility() == View.VISIBLE)
            ch_visits.setChecked(true);
        if (ch_route_plan.getVisibility() == View.VISIBLE)
            ch_route_plan.setChecked(true);
        if (ch_channel_partner.getVisibility() == View.VISIBLE)
            ch_channel_partner.setChecked(true);
        if (ch_financial_postings.getVisibility() == View.VISIBLE)
            ch_financial_postings.setChecked(true);
        if (ch_ss_invoices.getVisibility() == View.VISIBLE)
            ch_ss_invoices.setChecked(true);
        if (ch_value_helps.getVisibility() == View.VISIBLE)
            ch_value_helps.setChecked(true);
        if (ch_visits_act.getVisibility() == View.VISIBLE)
            ch_visits_act.setChecked(true);
        if (ch_focused_prd.getVisibility() == View.VISIBLE)
            ch_focused_prd.setChecked(true);
        if (ch_merch_reivew.getVisibility() == View.VISIBLE)
            ch_merch_reivew.setChecked(true);
        if (ch_db_stock.getVisibility() == View.VISIBLE)
            ch_db_stock.setChecked(true);
        if (ch_ss_targets.getVisibility() == View.VISIBLE)
            ch_ss_targets.setChecked(true);
        if (ch_comp_info.getVisibility() == View.VISIBLE)
            ch_comp_info.setChecked(true);
        if (ch_trends.getVisibility() == View.VISIBLE)
            ch_trends.setChecked(true);
        if (ch_ret_behaviour.getVisibility() == View.VISIBLE)
            ch_ret_behaviour.setChecked(true);
        if (ch_my_stk.getVisibility() == View.VISIBLE)
            ch_my_stk.setChecked(true);
        if (ch_return_order.getVisibility() == View.VISIBLE)
            ch_return_order.setChecked(true);
        if (ch_complaints.getVisibility() == View.VISIBLE)
            ch_complaints.setChecked(true);
        if (ch_documents.getVisibility() == View.VISIBLE)
            ch_documents.setChecked(true);
        if (ch_alerts.getVisibility() == View.VISIBLE)
            ch_alerts.setChecked(true);
        if (ch_expense.getVisibility() == View.VISIBLE)
            ch_expense.setChecked(true);
        if (ch_schemes.getVisibility() == View.VISIBLE)
            ch_schemes.setChecked(true);
        if (ch_ss_orders.getVisibility() == View.VISIBLE)
            ch_ss_orders.setChecked(true);


    }

    private void unCheckAll() {
        if (ch_sales_persons.getVisibility() == View.VISIBLE)
            ch_sales_persons.setChecked(false);
        if (chAttendeces.getVisibility() == View.VISIBLE)
            chAttendeces.setChecked(false);
        if (ch_outstanding.getVisibility() == View.VISIBLE)
            ch_outstanding.setChecked(false);
        if (chAuth.getVisibility() == View.VISIBLE)
            chAuth.setChecked(false);
        if (ch_visits.getVisibility() == View.VISIBLE)
            ch_visits.setChecked(false);
        if (ch_route_plan.getVisibility() == View.VISIBLE)
            ch_route_plan.setChecked(false);
        if (ch_channel_partner.getVisibility() == View.VISIBLE)
            ch_channel_partner.setChecked(false);
        if (ch_financial_postings.getVisibility() == View.VISIBLE)
            ch_financial_postings.setChecked(false);
        if (ch_ss_invoices.getVisibility() == View.VISIBLE)
            ch_ss_invoices.setChecked(false);
        if (ch_value_helps.getVisibility() == View.VISIBLE)
            ch_value_helps.setChecked(false);
        if (ch_visits_act.getVisibility() == View.VISIBLE)
            ch_visits_act.setChecked(false);
        if (ch_focused_prd.getVisibility() == View.VISIBLE)
            ch_focused_prd.setChecked(false);
        if (ch_merch_reivew.getVisibility() == View.VISIBLE)
            ch_merch_reivew.setChecked(false);
        if (ch_db_stock.getVisibility() == View.VISIBLE)
            ch_db_stock.setChecked(false);
        if (ch_ss_targets.getVisibility() == View.VISIBLE)
            ch_ss_targets.setChecked(false);
        if (ch_comp_info.getVisibility() == View.VISIBLE)
            ch_comp_info.setChecked(false);
        if (ch_trends.getVisibility() == View.VISIBLE)
            ch_trends.setChecked(false);
        if (ch_ret_behaviour.getVisibility() == View.VISIBLE)
            ch_ret_behaviour.setChecked(false);
        if (ch_my_stk.getVisibility() == View.VISIBLE)
            ch_my_stk.setChecked(false);
        if (ch_return_order.getVisibility() == View.VISIBLE)
            ch_return_order.setChecked(false);
        if (ch_complaints.getVisibility() == View.VISIBLE)
            ch_complaints.setChecked(false);
        if (ch_documents.getVisibility() == View.VISIBLE)
            ch_documents.setChecked(false);
        if (ch_alerts.getVisibility() == View.VISIBLE)
            ch_alerts.setChecked(false);
        if (ch_expense.getVisibility() == View.VISIBLE)
            ch_expense.setChecked(false);
        if (ch_schemes.getVisibility() == View.VISIBLE)
            ch_schemes.setChecked(false);
        if (ch_ss_orders.getVisibility() == View.VISIBLE)
            ch_ss_orders.setChecked(false);
    }

    public class AsyncSyncData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(SyncSelectViewActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(false);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SyncSelectViewActivity.this, R.style.MyTheme);
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
                                                                .setCancelable(false);
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
                Thread.sleep(100);
                assignCollToArrayList();

                if (!OfflineManager.isOfflineStoreOpen()) {
                    try {
                        OfflineManager.openOfflineStore(SyncSelectViewActivity.this, SyncSelectViewActivity.this);
                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError(Constants.error_txt + e.getMessage());
                    }
                } else {

                    try {

                        OfflineManager.refreshStoreSync(getApplicationContext(), SyncSelectViewActivity.this, Constants.Fresh, concatCollectionStr);
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


}
