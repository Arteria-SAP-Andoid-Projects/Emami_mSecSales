package com.arteriatech.emami.dbstock;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.DbStockDetailAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

public class DBStockDetails extends AppCompatActivity {

    private ListView listDbStock;
    private ArrayList<DBStockBean> alDBStockList;
    private String mStrMaterialNo = "",mStrQuantity = "",mStrMaterialDesc="",mStrQuantityUnit,mStrMFD,mStrCPStockItemGUID,mStrCPStockGrp="",mStrCPStockGrpID="",mStrDBStockType="",mStrSelDMSDIVID="",mStrStkOwner="",mStrCPGUID="";
    private DbStockDetailAdapter stockAdapter;
    private TextView tvMaterialDesc,tvMaterialNo,tvTotalQuantity,tv_crs_sku_qry;
    private ProgressDialog pdLoadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbstock_details);
        ActionBarView.initActionBarView(this,true,getString(R.string.title_dbstoxk_and_price));
        if (!Constants.restartApp(DBStockDetails.this)) {
            initUI();
            loadAsyncTask();
//        getDBStockDetails();
        }

    }

    private void initUI()
    {
        listDbStock = (ListView)findViewById(R.id.listDbStock);
        alDBStockList = new ArrayList<>();
        mStrMaterialNo = getIntent().getExtras().getString(Constants.MaterialNo);
        mStrMaterialDesc = getIntent().getExtras().getString(Constants.MaterialDesc);
        mStrQuantity = getIntent().getExtras().getString(Constants.QAQty);
        mStrQuantityUnit = getIntent().getExtras().getString(Constants.UOM);
        mStrMFD = getIntent().getExtras().getString(Constants.ManufacturingDate);
        mStrCPStockItemGUID = getIntent().getExtras().getString(Constants.CPStockItemGUID);
        mStrCPStockGrp = getIntent().getExtras().getString(Constants.OrderMaterialGroupDesc);
        mStrCPStockGrpID = getIntent().getExtras().getString(Constants.OrderMaterialGroupID);
        mStrDBStockType=getIntent().getExtras().getString(Constants.DBSTKTYPE);
        mStrSelDMSDIVID=getIntent().getExtras().getString(Constants.DMSDivision);
        mStrStkOwner=getIntent().getExtras().getString(Constants.StockOwner);
        mStrCPGUID=getIntent().getExtras().getString(Constants.CPGUID);

        tvMaterialDesc = (TextView)findViewById(R.id.tv_material_desc);
        tvMaterialNo = (TextView)findViewById(R.id.tv_material_number);
        tvTotalQuantity = (TextView)findViewById(R.id.tv_total_quantity);
        tv_crs_sku_qry= (TextView)findViewById(R.id.tv_crs_sku_qry);

        if(mStrDBStockType.equalsIgnoreCase(Constants.str_01)){
            tvMaterialDesc.setText(mStrMaterialDesc);
            tvMaterialNo.setText(mStrMaterialNo);
        }else{
            tvMaterialDesc.setText(mStrCPStockGrp);
            tvMaterialNo.setText(mStrCPStockGrpID);
        }


        if(mStrQuantityUnit.equalsIgnoreCase(Constants.PC)){
            tv_crs_sku_qry.setText(UtilConstants.removeLeadingZeroVal(mStrQuantity)+" "+mStrQuantityUnit);
        }else{
            tv_crs_sku_qry.setText(UtilConstants.removeLeadingZeroQuantity(mStrQuantity)+" "+mStrQuantityUnit);
        }

    }
    private String getDBStockItemQryByOrderMatGrp(){
        String mStrItemQry="";

            try {
                if(mStrDBStockType.equalsIgnoreCase(Constants.str_01)){
                    mStrItemQry= OfflineManager.makeCPStockItemQryByOrderMatGrp(Constants.CPStockItems+"?$filter="+ Constants.Material_No+" eq '"+mStrMaterialNo+"'" +
                            " and StockTypeID eq '1' and ("+mStrSelDMSDIVID+") and "+ Constants.CPGUID+" eq '"+mStrCPGUID+"' and "+Constants.StockOwner+" eq '"+mStrStkOwner+"'",Constants.CPStockItemGUID);

                }else{
                     mStrItemQry= OfflineManager.makeCPStockItemQryByOrderMatGrp(Constants.CPStockItems+"?$filter="+ Constants.OrderMaterialGroupID+" eq '"+mStrCPStockGrpID+"'" +
                             " and StockTypeID eq '1' and ("+mStrSelDMSDIVID+") and "+ Constants.CPGUID+" eq '"+mStrCPGUID+"' and "+Constants.StockOwner+" eq '"+mStrStkOwner+"'",Constants.CPStockItemGUID);

                }
              } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }



        return mStrItemQry;
    }
    private void getDBStockDetails()
    {
        try {

            String mStrItemGuidQry = getDBStockItemQryByOrderMatGrp();
            if(!mStrItemGuidQry.equalsIgnoreCase("")){
                String mStrMyStockQry= Constants.CPStockItemSnos+"?$filter="+mStrItemGuidQry;
                alDBStockList = OfflineManager.getCPStockSNosList(mStrMyStockQry);
            }

//            displayDBStockList();
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    void displayDBStockList(){
        stockAdapter = new DbStockDetailAdapter(getApplicationContext(), alDBStockList);
        listDbStock.setEmptyView(findViewById(R.id.tv_empty_lay) );
        listDbStock.setAdapter(stockAdapter);
        this.stockAdapter.notifyDataSetChanged();
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

    private void loadAsyncTask(){
        try {
            new GetDBStockData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*AsyncTask to get DBStock*/
    private class GetDBStockData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(DBStockDetails.this,R.style.ProgressDialogTheme);
            pdLoadDialog.setMessage(getString(R.string.app_loading));
            pdLoadDialog.setCancelable(false);
            pdLoadDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            getDBStockDetails();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            closingProgressDialog();
            displayDBStockList();
        }
    }

    private void closingProgressDialog(){
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
