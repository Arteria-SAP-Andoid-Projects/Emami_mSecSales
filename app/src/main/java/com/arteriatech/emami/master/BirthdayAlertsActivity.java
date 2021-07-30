package com.arteriatech.emami.master;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.BirthdayListAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.DividerItemDecoration;
import com.arteriatech.emami.main.MainMenu;
import com.arteriatech.emami.mbo.BirthdaysBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.google.gson.Gson;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

public class BirthdayAlertsActivity extends AppCompatActivity {
    String[][] oneWeekDay;
    TextView tvEmptyListLay = null;
    String splitDayMonth[] = null;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private Paint p = new Paint();
    BirthdayListAdapter adapter;

    //ToDO Alerts pending
    ArrayList<BirthdaysBean> alRetBirthDayList = null;

    ArrayList<BirthdaysBean> alDataValutBirthDayList = null;

    ArrayList<BirthdaysBean> alDataValutList = null;

    ArrayList<BirthdaysBean> alAppointmentList = null;

    ArrayList<BirthdaysBean> alertsOrderBeanList = null;
    private ProgressDialog pdLoadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar without back button(false)
        ActionBarView.initActionBarView(this, true, getString(R.string.lbl_alerts));
        setContentView(R.layout.activity_alerts);
        if (!Constants.restartApp(BirthdayAlertsActivity.this)) {
            tvEmptyListLay = (TextView) findViewById(R.id.tv_empty_lay);
            oneWeekDay = UtilConstants.getOneweekValues(1);

            onInitUI();
            loadAsyncTask();
        }

    }

    private void loadAsyncTask() {
        try {
            new GetBirthdayAlertsData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*AsyncTask to get Achieved Percentage*/
    private class GetBirthdayAlertsData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(BirthdayAlertsActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            getTodayBirthDayList();
            getAlerts();
            onDataVaultValidation();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                pdLoadDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setValuesToUI();
        }
    }

    private void getAlerts() {

        String query = Constants.ALERTS + Constants.isNonLocalFilterQry;
        try {
            alertsOrderBeanList = OfflineManager.getAlerts(query, alertsOrderBeanList);
        } catch (OfflineODataStoreException s) {
            s.printStackTrace();
        }
    }

    private void setCurrentDateTOSharedPerf() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME,
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.BirthDayAlertsDate, UtilConstants.getDate1());
        editor.commit();

    }

    private void onDataVaultValidation() {
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME,
                0);
        String mStrBirthdayDate = settings.getString(Constants.BirthDayAlertsDate, "");

        if (mStrBirthdayDate.equalsIgnoreCase(UtilConstants.getDate1())) {
            // ToDO check birthday records available  in data vault
            String mStrDataAval = null;
            try {
                mStrDataAval = LogonCore.getInstance().getObjectFromStore(Constants.BirthDayAlertsKey);
            } catch (LogonCoreException e) {
                e.printStackTrace();
                mStrDataAval = "";
            }
            if (!mStrDataAval.equalsIgnoreCase("")) {
                // ToDO data vault data convert into json object
                try {
                    JSONObject fetchJsonHeaderObject = new JSONObject(mStrDataAval);
                    String itemsString = fetchJsonHeaderObject.getString(Constants.ITEM_TXT);
                    alDataValutBirthDayList = new ArrayList<>();
                    alDataValutBirthDayList = Constants.convertToBirthDayArryList(itemsString);
                    alRetBirthDayList = new ArrayList<>();
                    alDataValutList = alDataValutBirthDayList;
                    if (alDataValutBirthDayList != null && alDataValutBirthDayList.size() > 0) {
                        for (int k = 0; k < alDataValutBirthDayList.size(); k++) {

                            if (alDataValutBirthDayList.get(k).getAlertGUID().equalsIgnoreCase("")) {
                                if (!alDataValutBirthDayList.get(k).getAppointmentAlert()) {
                                    if ((alDataValutBirthDayList.get(k).getDOBStatus().equalsIgnoreCase("")
                                            && alDataValutBirthDayList.get(k).getDOB().contains(splitDayMonth[1] + "/" + splitDayMonth[0]))
                                            || (alDataValutBirthDayList.get(k).getAnniversaryStatus().equalsIgnoreCase("")
                                            && alDataValutBirthDayList.get(k).getAnniversary().contains(splitDayMonth[1] + "/" + splitDayMonth[0]))) {
                                        alRetBirthDayList.add(alDataValutBirthDayList.get(k));
                                    }
                                } else {

                                    if (alDataValutBirthDayList.get(k).getAppointmentStatus().equalsIgnoreCase("")) {
                                        alRetBirthDayList.add(alDataValutBirthDayList.get(k));
                                    }
                                }
                            }


                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // TODO add values into data vault
                assignValuesIntoDataVault();
            }
        } else {
            // ToDO delete old date birthday records from data vault
            try {
                //noinspection deprecation
                LogonCore.getInstance().addObjectToStore(Constants.BirthDayAlertsKey, "");
            } catch (LogonCoreException e) {
                e.printStackTrace();
            }
            setCurrentDateTOSharedPerf();
            // TODO add values into data vault
            assignValuesIntoDataVault();
        }

    }

    // TODO add values into data vault
    private void assignValuesIntoDataVault() {

        Gson gson = new Gson();
        Hashtable dbHeaderTable = new Hashtable();
        try {
            String jsonFromMap = gson.toJson(alRetBirthDayList);
            alDataValutBirthDayList = new ArrayList<>();
            alDataValutBirthDayList = alRetBirthDayList;
            alDataValutList = alRetBirthDayList;
            //noinspection unchecked
            dbHeaderTable.put(Constants.ITEM_TXT, jsonFromMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);
        //noinspection deprecation
        try {
            //noinspection deprecation
            LogonCore.getInstance().addObjectToStore(Constants.BirthDayAlertsKey, jsonHeaderObject.toString());
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
    }


    /*
     * TODO This method initialize UI
     */
    private void onInitUI() {
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        alDataValutList = new ArrayList<>();
    }

    /*
     TODO This method set values to UI
    */
    private void setValuesToUI() {
        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
        }
        if (recyclerView != null) {
            recyclerView.setLayoutManager(layoutManager);
        }

        if (alertsOrderBeanList != null && alertsOrderBeanList.size() > 0) {
            if (alRetBirthDayList == null) {
                alRetBirthDayList = new ArrayList<>();
            }
            alRetBirthDayList.addAll(alRetBirthDayList.size(), alertsOrderBeanList);
            alDataValutBirthDayList = alRetBirthDayList;
        }
        if (alRetBirthDayList != null && alRetBirthDayList.size() > 0) {
            adapter = new BirthdayListAdapter(alRetBirthDayList, BirthdayAlertsActivity.this, splitDayMonth, recyclerView, tvEmptyListLay);
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvEmptyListLay.setVisibility(View.VISIBLE);
        }

        initSwipe();
    }

    /*
       TODO Get Current Day Birthdays list
    */
    private void getTodayBirthDayList() {
        if (oneWeekDay != null && oneWeekDay.length > 0) {
            for (int i = 0; i < oneWeekDay[0].length; i++) {

                splitDayMonth = oneWeekDay[0][i].split("-");

                String mStrBirthdayAvlQry = Constants.ChannelPartners + "?$filter=(month%28" + Constants.DOB + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.DOB + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") or (month%28" + Constants.Anniversary + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.Anniversary + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ") ";
                try {
                    if (OfflineManager.getVisitStatusForCustomer(mStrBirthdayAvlQry)) {

                        try {
                            alRetBirthDayList = OfflineManager.getTodayBirthDayList(mStrBirthdayAvlQry);
                        } catch (OfflineODataStoreException e) {
                            LogManager.writeLogError(Constants.error_txt + e.getMessage());
                        }
                    }
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }


                String mStrAppointmentListQuery = Constants.Visits + "?$filter=" + Constants.StatusID + " eq '00' and (month%28" + Constants.PlannedDate + "%29%20eq " + splitDayMonth[0] + " " +
                        "and day%28" + Constants.PlannedDate + "%29%20eq " + UtilConstants.removeLeadingZeros(splitDayMonth[1]) + ")";
                try {
                    alAppointmentList = OfflineManager.getAppointmentListForAlert(mStrAppointmentListQuery);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }


                if (alRetBirthDayList == null)
                    alRetBirthDayList = new ArrayList<>();
                for (int j = 0; j < alAppointmentList.size(); j++) {
                    alRetBirthDayList.add(alAppointmentList.get(j));
                }

            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intentMainmenu = new Intent(BirthdayAlertsActivity.this,
                MainMenu.class);
        intentMainmenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentMainmenu);
    }

    private void setIntoDataVault() {
        Hashtable dbHeaderTable = new Hashtable();
        Gson gson = new Gson();

        try {
            String jsonFromMap = gson.toJson(alDataValutList);
            //noinspection unchecked
            dbHeaderTable.put(Constants.ITEM_TXT, jsonFromMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonHeaderObject = new JSONObject(dbHeaderTable);
        //noinspection deprecation
        try {
            //noinspection deprecation
            LogonCore.getInstance().addObjectToStore(Constants.BirthDayAlertsKey, jsonHeaderObject.toString());
        } catch (LogonCoreException e) {
            e.printStackTrace();
        }
    }


    private void updateDataVaultRecord(int position) {
        if (alDataValutBirthDayList.size() > 1) {
            for (int l = 0; l < alDataValutBirthDayList.size(); l++) {
                BirthdaysBean birthdaysBean = alDataValutBirthDayList.get(l);

                if (alRetBirthDayList.get(position).getAlertGUID().equalsIgnoreCase("")) {
                    if (alRetBirthDayList.get(position).getCPUID().equalsIgnoreCase(birthdaysBean.getCPUID())
                            && !alRetBirthDayList.get(position).getAppointmentAlert()) {
                        if (birthdaysBean.getDOB().contains(splitDayMonth[1] + "/" + splitDayMonth[0])) {
                            birthdaysBean.setDOBStatus(Constants.X);
                        } else {
                            birthdaysBean.setDOBStatus("");
                        }
                        if (birthdaysBean.getAnniversary().contains(splitDayMonth[1] + "/" + splitDayMonth[0])) {
                            birthdaysBean.setAnniversaryStatus(Constants.X);
                        } else {
                            birthdaysBean.setAnniversaryStatus("");
                        }
                        alDataValutBirthDayList.set(l, birthdaysBean);
                        alDataValutList = alDataValutBirthDayList;
                        setIntoDataVault();
                        break;
                    } else {
                        if (alRetBirthDayList.get(position).getCPUID().equalsIgnoreCase(birthdaysBean.getCPUID())
                                && alRetBirthDayList.get(position).getAppointmentAlert()) {
                            birthdaysBean.setAppointmentStatus(Constants.X);

                            alDataValutBirthDayList.set(l, birthdaysBean);
                            alDataValutList = alDataValutBirthDayList;
                            setIntoDataVault();
                            break;
                        }
                    }
                } else {
                    if (alRetBirthDayList.get(position).getAlertGUID().equalsIgnoreCase(birthdaysBean.getAlertGUID())) {
                        birthdaysBean.setAlertStatus(Constants.X);
                        alDataValutBirthDayList.set(l, birthdaysBean);
                        alDataValutList = alDataValutBirthDayList;
                        updateAlertsRecord(birthdaysBean);
                        break;
                    }
                }

            }
        } else {


            BirthdaysBean birthdaysBean = alDataValutBirthDayList.get(position);
            if (birthdaysBean.getAlertGUID().equalsIgnoreCase("")) {
                if (birthdaysBean.getAppointmentAlert()) {
                    birthdaysBean.setAppointmentStatus(Constants.X);
                } else {
                    if (birthdaysBean.getDOB().contains(splitDayMonth[1] + "/" + splitDayMonth[0])) {
                        birthdaysBean.setDOBStatus(Constants.X);
                    } else {
                        birthdaysBean.setDOBStatus("");
                    }
                    if (birthdaysBean.getAnniversary().contains(splitDayMonth[1] + "/" + splitDayMonth[0])) {
                        birthdaysBean.setAnniversaryStatus(Constants.X);
                    } else {
                        birthdaysBean.setAnniversaryStatus("");
                    }
                }

                alDataValutBirthDayList.set(position, birthdaysBean);
                alDataValutList = alDataValutBirthDayList;
                setIntoDataVault();

            } else {
                updateAlertsRecord(birthdaysBean);
                birthdaysBean.setAlertStatus(Constants.X);

                alDataValutBirthDayList.set(position, birthdaysBean);
                alDataValutList = alDataValutBirthDayList;
            }

        }


    }

    /*
       TODO Swipe left or right side record delete from list.
    */
    private void initSwipe() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    updateDataVaultRecord(position);
                    try {
                        Thread.sleep(200);
                        adapter.removeItem(position);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (direction == ItemTouchHelper.RIGHT) {
                    updateDataVaultRecord(position);
                    try {
                        Thread.sleep(200);
                        adapter.removeItem(position);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor(Constants.red_hex_color_code)); //#D32F2F
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor(Constants.red_hex_color_code)); //#D32F2F
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void updateAlertsRecord(BirthdaysBean birthdaysBean) {
        try {
            OfflineManager.updateAlert(birthdaysBean);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

}
