package com.arteriatech.emami.finance;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.ODataGuid;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.xscript.core.GUID;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

/**
 * Created by e10604 on 29/4/2016.
 */
@SuppressLint("NewApi")
public class CompetitorInfoActivity extends AppCompatActivity implements UIListener {

    Spinner spin1, spin2, spin3;
    String mStrSpinnerOneVal = "", mStrSpinnerTwoVal = "", mStrSpinnerThreeVal = "";
    String mStrSpinnerGuidOneVal = "", mStrSpinnerGuidTwoVal = "", mStrSpinnerGuidThreeVal = "";
    String[] comRet = new String[3];
    String[] comRetGuid = new String[3];
    String[][] comRetailer;
    String competitor_nameDesc[];
    boolean flag = true;
    int cursorLength = 0;
    TextView[] comDesc;
    EditText[] inputOne, inputTwo, inputThree;
    String[] comRetailerTag = Constants.comRetailerTag;
    String[][] mArrayCompetitorVal;
    String[][] mArrayTempCompetitorVal;
    ArrayList<Hashtable<String, String>> arrtable;
    String selDistributorCode = "", mStrCPTypeID = "", mStrSPGuid = "", mStrSPNO = "";
    String curr_month_No = "";
    String mStrCpTypeID = "";
    String mStrComingFrom = "";
    private ScrollView scroll_com_list;
    private String mStrBundleRetID = "", mStrBundleCPGUID = "";
    private String mStrBundleRetName = "";
    private ProgressDialog pdLoadDialog;
    private String mStrPopUpText = "";
    private int mIntSuccessCount = 0;
    private String popUpText = "";
    private String[][] mArrayDistributors;
    private boolean mBoolFirstComp = false, mBoolSecondComp = false, mBoolThirdComp = false;
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.title_competitor_info));

        setContentView(R.layout.activity_competitor_info);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);

            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }
        if (!Constants.restartApp(CompetitorInfoActivity.this)) {
            mStrCpTypeID = Constants.getName(Constants.ChannelPartners, Constants.CPTypeID, Constants.CPNo, mStrBundleRetID);

            initUI();
        }
    }

    /*Initializes UI*/
    void initUI() {
        Calendar cal = Calendar.getInstance();

        int intFromMnt = cal.get(Calendar.MONTH) + 1;
        if (intFromMnt < 10)
            curr_month_No = getString(R.string.Zero_0) + intFromMnt;
        else
            curr_month_No = "" + intFromMnt;

        spin1 = (Spinner) findViewById(R.id.sp_competitor_spinner1);
        spin2 = (Spinner) findViewById(R.id.sp_competitor_spinner2);
        spin3 = (Spinner) findViewById(R.id.sp_competitor_spinner3);

        getDistributors();
        loadCompetitor();
        displayCompInfoValues(6);
    }

    /*Load data for competitors*/
    private void loadCompetitor() {
        try {
            String mStrCompQry = Constants.CompetitorMasters;
            comRetailer = OfflineManager.getCompMaster(mStrCompQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError("Error : " + e.getMessage());
        }

        if (comRetailer == null) {
            comRetailer = new String[2][1];
            comRetailer[0][0] = "";
            comRetailer[1][0] = "";
        }

        if (comRetailer != null) {

            ArrayAdapter<String> competitorAdapter = new ArrayAdapter<String>(
                    this, R.layout.custom_textview, comRetailer[1]);
            competitorAdapter
                    .setDropDownViewResource(R.layout.spinnerinside);
            spin1.setAdapter(competitorAdapter);
            spin2.setAdapter(competitorAdapter);
            spin3.setAdapter(competitorAdapter);


            spin1
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View arg1, int pos, long id) {
                        }

                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });
            spin2
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View arg1, int pos, long id) {
                        }

                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });
            spin3
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View arg1, int pos, long id) {
                        }

                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });

        } else {
            competitor_nameDesc = new String[0];

            ArrayAdapter<String> competitorAdapter = new ArrayAdapter<String>(
                    this, R.layout.custom_textview, competitor_nameDesc);
            competitorAdapter
                    .setDropDownViewResource(R.layout.spinnerinside);
            spin1.setAdapter(competitorAdapter);

            spin1
                    .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View arg1, int pos, long id) {

                        }

                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });

        }
    }


    /*Displays competitors*/
    private void displayCompInfoValues(int col) {

        if (!flag) {
            scroll_com_list.removeAllViews();
        }
        flag = false;
        scroll_com_list = (ScrollView) findViewById(R.id.scroll_com_list);

        TableLayout tableHeading = (TableLayout) LayoutInflater.from(this)
                .inflate(R.layout.item_table, null);
        cursorLength = col;
        comDesc = new TextView[cursorLength];
        inputOne = new EditText[cursorLength];
        inputTwo = new EditText[cursorLength];
        inputThree = new EditText[cursorLength];


        if (cursorLength > 0) {
            for (int i = 0; i < cursorLength; i++) {

                final int selvalue = i;
                LinearLayout rowRelativeLayout = (LinearLayout) LayoutInflater
                        .from(this).inflate(
                                R.layout.competitor_info_list, null);

                comDesc[i] = (TextView) rowRelativeLayout
                        .findViewById(R.id.tv_com_text);
                inputOne[i] = (EditText) rowRelativeLayout.findViewById(R.id.ed_com_text1);
                inputTwo[i] = (EditText) rowRelativeLayout.findViewById(R.id.ed_com_text2);
                inputThree[i] = (EditText) rowRelativeLayout.findViewById(R.id.ed_com_text3);

                if (!comRetailerTag[i].equalsIgnoreCase(Constants.SchemeName)) {

                    inputOne[i].setInputType(InputType.TYPE_CLASS_NUMBER);

                    inputTwo[i].setInputType(InputType.TYPE_CLASS_NUMBER);

                    inputThree[i].setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    inputOne[i].setGravity(Gravity.LEFT);
                    inputTwo[i].setGravity(Gravity.LEFT);
                    inputThree[i].setGravity(Gravity.LEFT);
                    UtilConstants.setAlphanumeric(inputOne[i], 50);
                    UtilConstants.setAlphanumeric(inputTwo[i], 50);
                    UtilConstants.setAlphanumeric(inputThree[i], 50);
                }
                comDesc[i].setText(comRetailerTag[i]);


                tableHeading.addView(rowRelativeLayout);
            }

            scroll_com_list.addView(tableHeading);
            scroll_com_list.requestLayout();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_comp_info, menu);

        return true;
    }

    /*Validate values for creation of competitor inf*/
    private void ValidationCheck() {
        boolean compVisit = false;
        try {
            compVisit = OfflineManager.getVisitActivitiesDoneForDay(Constants.CompetitorInfos + "?$filter=" + Constants.UpdatedOn + " eq datetime'" + UtilConstants.getNewDate() + "' " +
                    "and " + Constants.CPGUID + " eq '" + mStrBundleCPGUID32.toUpperCase() + "' ");
        } catch (OfflineODataStoreException e) {
            compVisit = false;
            LogManager.writeLogError("Error : " + e.getMessage());
        }

        if (compVisit) {
            alertDialogCompInfoDone();
        } else {
            onSave();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_save:
                ValidationCheck();

                break;
            case R.id.menu_comp_info_list:
                compInfoList();
                break;
        }
        return true;
    }

    /*Display alert for competitor info creation*/
    private void alertDialogCompInfoDone() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                CompetitorInfoActivity.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.alert_comp_info_already_updated))
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();
    }

    /*Navigates to Competitor info list*/
    private void compInfoList() {
        Intent intentCreateCollection = new Intent(CompetitorInfoActivity.this, CompInfoListActivity.class);
        intentCreateCollection.putExtra(Constants.CPNo, mStrBundleRetID);
        intentCreateCollection.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentCreateCollection.putExtra(Constants.CPGUID, mStrBundleCPGUID32.toUpperCase());
        startActivity(intentCreateCollection);
    }

    /*Saves data for competitor info in offline DB*/
    private void onSave() {
        boolean errorFlag = false;
        mBoolFirstComp = false;
        mBoolSecondComp = false;
        mBoolThirdComp = false;
        if (comRetailerTag != null && comRetailerTag.length > 0) {
            mStrSpinnerOneVal = spin1.getSelectedItem().toString();
            mStrSpinnerTwoVal = spin2.getSelectedItem().toString();
            mStrSpinnerThreeVal = spin3.getSelectedItem().toString();

            mStrSpinnerOneVal = comRetailer[1][spin1.getSelectedItemPosition()];
            mStrSpinnerTwoVal = comRetailer[1][spin2.getSelectedItemPosition()];
            mStrSpinnerThreeVal = comRetailer[1][spin3.getSelectedItemPosition()];

            mStrSpinnerGuidOneVal = comRetailer[0][spin1.getSelectedItemPosition()];
            mStrSpinnerGuidTwoVal = comRetailer[0][spin2.getSelectedItemPosition()];
            mStrSpinnerGuidThreeVal = comRetailer[0][spin3.getSelectedItemPosition()];

            mArrayCompetitorVal = new String[comRetailerTag.length][3];
            mArrayTempCompetitorVal = new String[comRetailerTag.length][3];
            for (int i = 0; i < comRetailerTag.length; i++) {

                mArrayCompetitorVal[i][0] = inputOne[i].getText().toString().equalsIgnoreCase("") ? "0" : inputOne[i].getText().toString();

                mArrayTempCompetitorVal[i][0] = inputOne[i].getText().toString();
                if (!inputOne[i].getText().toString().equalsIgnoreCase("") && !mBoolFirstComp) {
                    mBoolFirstComp = true;
                }

                mArrayCompetitorVal[i][1] = inputTwo[i].getText().toString().equalsIgnoreCase("") ? "0" : inputTwo[i].getText().toString();

                mArrayTempCompetitorVal[i][1] = inputTwo[i].getText().toString();
                if (!inputTwo[i].getText().toString().equalsIgnoreCase("") && !mBoolSecondComp) {
                    mBoolSecondComp = true;
                }


                mArrayCompetitorVal[i][2] = inputThree[i].getText().toString().equalsIgnoreCase("") ? "0" : inputThree[i].getText().toString();

                mArrayTempCompetitorVal[i][2] = inputThree[i].getText().toString();
                if (!inputThree[i].getText().toString().equalsIgnoreCase("") && !mBoolThirdComp) {
                    mBoolThirdComp = true;
                }

            }
        } else {
            displayError(getString(R.string.alert_please_select_comp));
            errorFlag = true;
        }


        if (!errorFlag) {
            //TODO Here check validation enter at least one competitor values
            if (mBoolFirstComp || mBoolSecondComp || mBoolThirdComp) {

                //TODO Here check validation either competitor values are filled or not.If competitor values filled partially its showing error message //changed date 22092016


                if (!errorFlag) {

                    if (mBoolFirstComp && mBoolSecondComp && mBoolThirdComp) {
                        if (!mStrSpinnerOneVal.equalsIgnoreCase(mStrSpinnerTwoVal) && !mStrSpinnerOneVal.equalsIgnoreCase(mStrSpinnerThreeVal)
                                && !mStrSpinnerTwoVal.equalsIgnoreCase(mStrSpinnerThreeVal)) {
                            loadData();
                        } else {
                            displayError(getString(R.string.alert_please_select_diff_comp));
                        }

                    } else if (mBoolFirstComp && mBoolSecondComp && !mBoolThirdComp) {
                        if (!mStrSpinnerOneVal.equalsIgnoreCase(mStrSpinnerTwoVal)) {
                            loadData();
                        } else {
                            displayError(getString(R.string.alert_please_select_diff_comp));
                        }
                    } else if (mBoolFirstComp && !mBoolSecondComp && mBoolThirdComp) {
                        if (!mStrSpinnerOneVal.equalsIgnoreCase(mStrSpinnerThreeVal)) {
                            loadData();
                        } else {
                            displayError(getString(R.string.alert_please_select_diff_comp));
                        }
                    } else if (!mBoolFirstComp && mBoolSecondComp && mBoolThirdComp) {
                        if (!mStrSpinnerTwoVal.equalsIgnoreCase(mStrSpinnerThreeVal)) {
                            loadData();
                        } else {
                            displayError(getString(R.string.alert_please_select_diff_comp));
                        }
                    } else if (mBoolFirstComp || mBoolSecondComp || mBoolThirdComp) {
                        loadData();
                    } else {
                        displayError(getString(R.string.alert_please_select_comp));
                    }


                } else {
                    displayError(getString(R.string.alert_enter_values_for_selected_comp));
                }

            } else {
                displayError(getString(R.string.alert_fill_atleast_one_comp));
            }

        }

    }

    /*Gets data from dropdown*/
    private void loadData() {
        comRet[0] = comRetailer[1][spin1.getSelectedItemPosition()];
        comRet[1] = comRetailer[1][spin2.getSelectedItemPosition()];
        comRet[2] = comRetailer[1][spin3.getSelectedItemPosition()];

        comRetGuid[0] = comRetailer[0][spin1.getSelectedItemPosition()];
        comRetGuid[1] = comRetailer[0][spin2.getSelectedItemPosition()];
        comRetGuid[2] = comRetailer[0][spin3.getSelectedItemPosition()];
        onLoadProgressDialog();
    }

    /*Displays error message in alert box*/
    public void displayError(String errorMessage) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(
                CompetitorInfoActivity.this, R.style.MyTheme);
        dialog.setMessage(errorMessage)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        dialog.show();
    }

    /**/
    private void onLoadProgressDialog() {
        mStrPopUpText = Constants.SubmittingCompetitorInfosmsg;
        try {
            new onCreateRetailerAsyncTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(int operation, Exception e) {
        LogManager.writeLogError(Constants.Error_in_Competitor_information + e.getMessage());
        pdLoadDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(
                CompetitorInfoActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.error_occured_during_post)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    onNavigateToRetDetilsActivity();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {


        if (operation == Operation.Create.getValue()) {
            if (++mIntSuccessCount == arrtable.size()) {
                pdLoadDialog.dismiss();
                backToPrevScreenDialog();
            }
        } else if (operation == Operation.Update.getValue()) {
            if (!UtilConstants.isNetworkAvailable(CompetitorInfoActivity.this)) {
                pdLoadDialog.dismiss();
                onNoNetwork();
            } else {
                OfflineManager.flushQueuedRequests(CompetitorInfoActivity.this);
            }
        } else if (operation == Operation.OfflineFlush.getValue()) {
            if (!UtilConstants.isNetworkAvailable(CompetitorInfoActivity.this)) {
                pdLoadDialog.dismiss();
                onNoNetwork();
            } else {
                OfflineManager.refreshRequests(getApplicationContext(), Constants.CompetitorInfos + "," + Constants.VisitActivities, CompetitorInfoActivity.this);
            }
        } else if (operation == Operation.OfflineRefresh.getValue()) {
            pdLoadDialog.dismiss();
            popUpText = getString(R.string.Competitor_information_created);

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    CompetitorInfoActivity.this, R.style.MyTheme);
            builder.setMessage(popUpText)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface Dialog,
                                        int id) {
                                    try {

                                        Dialog.cancel();
                                        onNavigateToRetDetilsActivity();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            });
            builder.show();
        }

    }

    /*Navigate to previous screen dialog*/
    private void backToPrevScreenDialog() {
        pdLoadDialog.dismiss();
        popUpText = getString(R.string.Competitor_information_created);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                CompetitorInfoActivity.this, R.style.MyTheme);
        builder.setMessage(popUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {

                                    Dialog.cancel();
                                    onNavigateToRetDetilsActivity();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });
        builder.show();
    }

    /*on No Network displays alert dialog*/
    private void onNoNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                CompetitorInfoActivity.this, R.style.MyTheme);
        builder.setMessage(
                R.string.alert_sync_cannot_be_performed)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onNavigateToRetDetilsActivity();
                    }
                });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CompetitorInfoActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_create_competitor_info).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onNavigateToRetDetilsActivity();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    /*Gets details for sales person*/
    private void getDistributors() {

        String qryStr = Constants.SalesPersons ;
        try {
            mArrayDistributors = OfflineManager.getDistributorList(qryStr);

        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

        if (mArrayDistributors == null) {
            mArrayDistributors = new String[7][1];
            mArrayDistributors[0][0] = "";
            mArrayDistributors[1][0] = "";
            mArrayDistributors[2][0] = "";
            mArrayDistributors[3][0] = "";
            mArrayDistributors[4][0] = "";
            mArrayDistributors[5][0] = "";
            mArrayDistributors[6][0] = "";
        } else {
            if (mArrayDistributors[0].length > 0) {
                selDistributorCode = mArrayDistributors[0][0];
                mStrCPTypeID = mArrayDistributors[3][0];
                mStrSPGuid = mArrayDistributors[4][0];
                mStrSPNO = mArrayDistributors[6][0];
            }
        }
    }

    /*Navigates to Retailer details*/
    private void onNavigateToRetDetilsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(CompetitorInfoActivity.this, RetailersDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        startActivity(intentNavPrevScreen);
    }

    /*AsyncTask to create retailer*/
    public class onCreateRetailerAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(CompetitorInfoActivity.this, R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(mStrPopUpText);
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
                GUID guid = GUID.newRandom();

                arrtable = new ArrayList<Hashtable<String, String>>();


                int incrementVal;
                mIntSuccessCount = 0;
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);

                String loginIdVal = sharedPreferences.getString(Constants.username, "");


                //========>Start VisitActivity
                Hashtable visitActivityTable = new Hashtable();
                String getVisitGuidQry = Constants.Visits + "?$filter=" + Constants.EndDate + " eq null and "
                        + Constants.CPGUID + " eq '" + mStrBundleCPGUID32 + "' " +
                        "and " + Constants.StartDate + " eq datetime'" + UtilConstants.getNewDate() + "' ";
                ODataGuid mGuidVisitId = null;
                try {
                    mGuidVisitId = OfflineManager.getVisitDetails(getVisitGuidQry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError("Error : " + e.getMessage());
                }

                //========>End VisitActivity

                for (int i = 0; i < 3; i++) {
                    incrementVal = 0;
                    Hashtable<String, String> singleItem = new Hashtable<String, String>();


                    GUID mStrGuide = GUID.newRandom();
                    GUID guidItem = GUID.newRandom();
                    visitActivityTable.put(Constants.VisitActivityGUID, mStrGuide.toString());
                    visitActivityTable.put(Constants.LOGINID, loginIdVal);
                    visitActivityTable.put(Constants.VisitGUID, mGuidVisitId.guidAsString36());
                    visitActivityTable.put(Constants.ActivityType, "04");
                    visitActivityTable.put(Constants.ActivityTypeDesc, Constants.Competitor_Information);

                    visitActivityTable.put(Constants.ActivityRefID, guidItem.toString());


                    singleItem.put(Constants.CompInfoGUID, guidItem.toString());
                    singleItem.put(Constants.CPGUID, mStrBundleCPGUID32);
                    singleItem.put(Constants.SPGUID, mStrSPGuid.toUpperCase());
                    singleItem.put(Constants.CompName, comRet[i]);
                    singleItem.put(Constants.CompGUID, comRetGuid[i]);
                    singleItem.put(Constants.MatGrp1Amount, mArrayCompetitorVal[incrementVal++][i]);
                    singleItem.put(Constants.MatGrp2Amount, mArrayCompetitorVal[incrementVal++][i]);
                    singleItem.put(Constants.MatGrp3Amount, mArrayCompetitorVal[incrementVal++][i]);
                    singleItem.put(Constants.Earnings, mArrayCompetitorVal[incrementVal++][i]);
                    singleItem.put(Constants.MatGrp4Amount, mArrayCompetitorVal[incrementVal++][i]);
                    singleItem.put(Constants.SchemeName, mArrayCompetitorVal[incrementVal++][i]);
                    singleItem.put(Constants.UpdatedOn, UtilConstants.getNewDateTimeFormat());
                    singleItem.put(Constants.CPTypeID, mStrCpTypeID);

                    singleItem.put(Constants.Period, curr_month_No);
                    singleItem.put(Constants.LOGINID, loginIdVal);

                    if (i == 0 && mBoolFirstComp) {
                        try {
                            OfflineManager.createVisitActivity(visitActivityTable);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                        arrtable.add(singleItem);
                    } else if (i == 1 && mBoolSecondComp) {
                        try {
                            OfflineManager.createVisitActivity(visitActivityTable);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                        arrtable.add(singleItem);
                    } else if (i == 2 && mBoolThirdComp) {
                        try {
                            OfflineManager.createVisitActivity(visitActivityTable);
                        } catch (OfflineODataStoreException e) {
                            e.printStackTrace();
                        }
                        arrtable.add(singleItem);
                    }

                }

                for (int i = 0; i < arrtable.size(); i++) {
                    Hashtable<String, String> hashMapCompInfo = arrtable.get(i);
                    try {
                        //noinspection unchecked
                        OfflineManager.createCompetitorInfo(hashMapCompInfo, CompetitorInfoActivity.this);

                    } catch (OfflineODataStoreException e) {
                        LogManager.writeLogError("Error : " + e.getMessage());
                    }
                }


            } catch (Exception e) {
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
