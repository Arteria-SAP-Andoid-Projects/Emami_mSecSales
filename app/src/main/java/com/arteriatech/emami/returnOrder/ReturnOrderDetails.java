package com.arteriatech.emami.returnOrder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.datavault.UtilDataVault;
import com.arteriatech.emami.adapter.ReturnOrderDetailAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.msecsales.R;
import com.sap.xscript.core.GUID;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class ReturnOrderDetails extends AppCompatActivity {
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "", mStrComingFrom = "";
    private ReturnOrderDetailAdapter returnOrderDetailAdapter;
    private ArrayList<ReturnOrderBean> alReturnList;
    private ListView lvReturnList;
    private Hashtable<String, String> headerTable = new Hashtable<>();
    private String[][] mArrayDistributors = null, mArrayCPDMSDivisoins = null;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_order_details);
        ActionBarView.initActionBarView(this, true, getString(R.string.title_return_order_create));
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
            alReturnList = getIntent().getParcelableArrayListExtra(Constants.ReturnOrders);
        }
        lvReturnList = (ListView) findViewById(R.id.lv_return_order_details);
        if (!Constants.restartApp(ReturnOrderDetails.this)) {
            //get distributor valus
            getDistributorValues();
            //get cpdms divisions
            getCPDMSDivisions();
            //display return order list
            displayReturnOrderList();
        }

    }

    /*get cpdms divisions value*/
    private void getCPDMSDivisions() {
        mArrayCPDMSDivisoins = Constants.getDMSDivisionByCPGUID(mStrBundleCPGUID);
    }

    /*display return order list*/
    private void displayReturnOrderList() {
        if (alReturnList != null) {
            returnOrderDetailAdapter = new ReturnOrderDetailAdapter(getApplicationContext(), alReturnList);
            lvReturnList.setEmptyView(findViewById(R.id.tv_empty_lay));
            lvReturnList.setAdapter(returnOrderDetailAdapter);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_save:
                onSave();
                break;
        }
        return false;
    }

    /*save data*/
    private void onSave() {
        createDataVaultObjects();
    }

    /*create data into data valt*/
    private void createDataVaultObjects() {
        GUID ssoHeaderGuid = GUID.newRandom();
        String doc_no = (System.currentTimeMillis() + "");
        headerTable.put(Constants.SSROGUID, ssoHeaderGuid.toString36().toUpperCase());
        headerTable.put(Constants.OrderType, "000020");
        headerTable.put(Constants.OrderTypeDesc, "");
        headerTable.put(Constants.OrderNo, doc_no);
        headerTable.put(Constants.OrderDate, UtilConstants.getNewDateTimeFormat());
        headerTable.put(Constants.DmsDivision, mArrayCPDMSDivisoins[0][0] != null ? mArrayCPDMSDivisoins[0][0] : "");
        headerTable.put(Constants.DmsDivisionDesc, mArrayCPDMSDivisoins[1][0] != null ? mArrayCPDMSDivisoins[1][0] : "");
        headerTable.put(Constants.FromCPGUID, mStrBundleCPGUID);
        headerTable.put(Constants.FromCPNo, mArrayDistributors[4][0]);
        headerTable.put(Constants.FromCPName, mArrayDistributors[7][0]);
        headerTable.put(Constants.FromCPTypId, mArrayDistributors[5][0]);
        headerTable.put(Constants.FromCPTypDs, "");
        headerTable.put(Constants.CPGUID, mStrBundleCPGUID);
        headerTable.put(Constants.CPNo, mStrBundleRetID);
        headerTable.put(Constants.CPName, mStrBundleRetName);
        headerTable.put(Constants.CPTypeID, mArrayDistributors[8][0]);
        headerTable.put(Constants.CPTypeDesc, mArrayDistributors[9][0]);
        headerTable.put(Constants.SoldToCPGUID, mStrBundleCPGUID);
        headerTable.put(Constants.SoldToId, mStrBundleRetID);
        headerTable.put(Constants.SoldToUID, mStrBundleCPGUID);
        headerTable.put(Constants.SoldToDesc, mStrBundleRetName);
        headerTable.put(Constants.SoldToTypeID, mArrayDistributors[8][0]);
        headerTable.put(Constants.SoldToTypDesc, mArrayDistributors[9][0]);
        headerTable.put(Constants.SPGUID, mArrayDistributors[0][0]);
        headerTable.put(Constants.SPNo, mArrayDistributors[2][0]);
        headerTable.put(Constants.FirstName, mArrayDistributors[3][0]);
        headerTable.put(Constants.Currency, mArrayDistributors[10][0]);
        headerTable.put(Constants.CreatedOn, UtilConstants.getNewDateTimeFormat());
        headerTable.put(Constants.CreatedAt, Constants.getOdataDuration().toString());
        headerTable.put(Constants.StatusID, "000001");
        headerTable.put(Constants.ApprovalStatusID, "000001");
        headerTable.put(Constants.TestRun, "M");
        ArrayList<HashMap<String, String>> soItems = new ArrayList<HashMap<String, String>>();
        for (int itemIncVal = 0; itemIncVal < alReturnList.size(); itemIncVal++) {
            HashMap<String, String> singleItem = new HashMap<String, String>();
            GUID ssoItemGuid = GUID.newRandom();
            singleItem.put(Constants.SSROItemGUID, ssoItemGuid.toString36().toUpperCase());
            singleItem.put(Constants.SSROGUID, ssoHeaderGuid.toString36().toUpperCase());
            singleItem.put(Constants.ItemNo, (itemIncVal + 1) + "0");
            singleItem.put(Constants.MaterialNo, alReturnList.get(itemIncVal).getMaterialNo());
            singleItem.put(Constants.MaterialDesc, alReturnList.get(itemIncVal).getMaterialDesc());
            singleItem.put(Constants.OrderMatGrp, alReturnList.get(itemIncVal).getOrderMaterialGroupID());
            singleItem.put(Constants.OrderMatGrpDesc, alReturnList.get(itemIncVal).getOrderMaterialGroupDesc());
            singleItem.put(Constants.Quantity, alReturnList.get(itemIncVal).getReturnQty());
            singleItem.put(Constants.Currency, mArrayDistributors[10][0]);
            singleItem.put(Constants.Uom, alReturnList.get(itemIncVal).getUom());
            singleItem.put(Constants.Batch, alReturnList.get(itemIncVal).getReturnBatchNumber());
            singleItem.put(Constants.RejectionReasonID, alReturnList.get(itemIncVal).getReturnReason());
            singleItem.put(Constants.RejectionReasonDesc, alReturnList.get(itemIncVal).getReturnDesc());
            soItems.add(singleItem);

        }
        headerTable.put(Constants.entityType, Constants.ReturnOrderCreate);
        headerTable.put(Constants.ITEM_TXT, Constants.convertArrListToGsonString(soItems));

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
        Constants.onVisitActivityUpdate(mStrBundleCPGUID32, sharedPreferences.getString(Constants.username, ""),
                ssoHeaderGuid.toString36().toUpperCase(), "08", Constants.ReturnOrderCreate);

        Constants.saveDeviceDocNoToSharedPref(ReturnOrderDetails.this, Constants.ROList, doc_no);

        headerTable.put(Constants.LOGINID, sharedPreferences.getString(Constants.username, "").toUpperCase());

        JSONObject jsonHeaderObject = new JSONObject(headerTable);

        UtilDataVault.storeInDataVault(doc_no, jsonHeaderObject.toString());

        navigateToVisit();
    }

    /*Navigate to visit screen*/
    public void navigateToVisit() {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                ReturnOrderDetails.this, R.style.MyTheme);
        builder.setMessage(getString(R.string.msg_return_order_created))
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    Constants.ComingFromCreateSenarios = Constants.X;
                                    navigateToRetDetailsActivity();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
        builder.show();
    }

    /*navigation to another activity*/
    private void navigateToRetDetailsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(ReturnOrderDetails.this, RetailersDetailsActivity.class);
        intentNavPrevScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNavPrevScreen.putExtra(Constants.CPNo, mStrBundleRetID);
        intentNavPrevScreen.putExtra(Constants.RetailerName, mStrBundleRetName);
        intentNavPrevScreen.putExtra(Constants.CPUID, mStrBundleRetailerUID);
        intentNavPrevScreen.putExtra(Constants.comingFrom, mStrComingFrom);
        intentNavPrevScreen.putExtra(Constants.CPGUID, mStrBundleCPGUID);
        if (!Constants.OtherRouteNameVal.equalsIgnoreCase("")) {
            intentNavPrevScreen.putExtra(Constants.OtherRouteGUID, Constants.OtherRouteGUIDVal);
            intentNavPrevScreen.putExtra(Constants.OtherRouteName, Constants.OtherRouteNameVal);
        }
        startActivity(intentNavPrevScreen);
    }

    /*get distributor value from offline database*/
    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back_save, menu);

        return true;
    }
}
