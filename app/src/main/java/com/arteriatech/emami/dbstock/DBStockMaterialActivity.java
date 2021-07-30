package com.arteriatech.emami.dbstock;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.mbo.ErrorBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.exception.ODataException;

import java.util.ArrayList;

/**
 * Created by e10742 on 7/20/2017.
 */

public class DBStockMaterialActivity extends AppCompatActivity implements UIListener {

    private ArrayList<DBStockBean> alDBStockList = new ArrayList<>();
    ListView lvDBStock = null;
    EditText etSKUDescSearch = null;
    LinearLayout ltNoRecords = null;

    private String[][] mArrayBrandTypeVal,mArrayCateogryTypeVal,mArrayOrderedGroup;
    DBStockMaterialActivity.DBStockAdapter stockAdapter = null;
    TextView tv_last_sync_time_value;
    LinearLayout ll_dist_layout;
    Menu menu = null;
    ProgressDialog syncProgDialog = null;
    boolean dialogCancelled = false;
    private Spinner sp_category, sp_brand, sp_crs_sku_group,sp_distributor;
    String concatCollectionStr = "";
    private Boolean isCatFirstTime = true,isBrandFirstTime=true;
    private String previousCategoryId = "",previousBrandId = "";
    private String mStrSelOrderMaterialID = "";
    ArrayList<String> alAssignColl = new ArrayList<>();
    ArrayAdapter<String> productCategoryAdapter;
    ArrayAdapter<String> brandAdapter;

    private String[][] distList=null;
    private String mStrSelDistGuid = Constants.None,mStrSelDMSDIVID="";
    private ProgressDialog pdLoadDialog;
    private boolean mBoolFirstTime = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_dbstock_material);

        //Initialize action bar (without back button(false)/with back button(true))
        ActionBarView.initActionBarView(this, true,getString(R.string.title_dist_stock));
        if (!Constants.restartApp(DBStockMaterialActivity.this)) {
            initUI();
            getDistributor();

            getCategoryList();
            getBrandList();
//        displayDistributorVal();
//        getOrderedMaterials();

//        getDBStockDetails();
            loadAsyncTask();
        }


    }
    //Lists the order materials from the CPSTOCKITEMS
    private void getOrderedMaterials() {

        try {
            String mStrConfigQry = Constants.OrderMaterialGroups+"?$orderby="+ Constants.MaterialCategoryDesc+" &$filter=startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"') ";
            mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if(mArrayOrderedGroup==null)
        {
            mArrayOrderedGroup = new String[2][1];
            mArrayOrderedGroup[0][0]="";
            mArrayOrderedGroup[1][0]="";
        }


        ArrayAdapter<String> productOrderGroupAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayOrderedGroup[1]);
        productOrderGroupAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_crs_sku_group.setAdapter(productOrderGroupAdapter);
        sp_crs_sku_group
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                    @Override
                    public void onItemSelected(AdapterView<?> parent, View arg1,
                                               int position, long arg3) {

                        mStrSelOrderMaterialID = mArrayOrderedGroup[0][position];
                    }
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
    }
    private void updateBrandValuesInSpinner()
    {
        if(!previousCategoryId.equalsIgnoreCase(Constants.None))
        {
            try {
                mArrayBrandTypeVal = OfflineManager.getBrandListValues(Constants.BrandsCategories + "?$orderby="+ Constants.BrandDesc+" &$filter= " + Constants.MaterialCategoryID + " eq '" + previousCategoryId + "' and startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"') ");
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            if (mArrayBrandTypeVal == null) {
                mArrayBrandTypeVal = new String[4][1];
                mArrayBrandTypeVal[0][0] = "";
                mArrayBrandTypeVal[1][0] = "";
            }
            brandAdapter = new ArrayAdapter<>(this,
                    R.layout.custom_textview, mArrayBrandTypeVal[1]);
            brandAdapter.setDropDownViewResource(R.layout.spinnerinside);
            sp_brand.setAdapter(brandAdapter);

            isBrandFirstTime = true;

            sp_brand.setSelection(getBrandValueIndexKey());



        }
        else
        {
            if(previousBrandId.equalsIgnoreCase(Constants.None) && !isBrandFirstTime)
            {
                isBrandFirstTime = true;
                try {
                    mArrayBrandTypeVal = OfflineManager.getBrandListValues(Constants.Brands+"?$orderby="+ Constants.BrandDesc+" &$filter=startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"')");
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
                if (mArrayBrandTypeVal == null) {
                    mArrayBrandTypeVal = new String[4][1];
                    mArrayBrandTypeVal[0][0] = "";
                    mArrayBrandTypeVal[1][0] = "";
                }
                brandAdapter = new ArrayAdapter<>(this,
                        R.layout.custom_textview, mArrayBrandTypeVal[1]);
                brandAdapter.setDropDownViewResource(R.layout.spinnerinside);
                sp_brand.setAdapter(brandAdapter);

            }


        }


    }
    private void updateCategoryValuesInSpinner()
    {

        if(!previousBrandId.equalsIgnoreCase(Constants.None))
        {
            try {
                String mStrConfigQry = Constants.BrandsCategories+"?$orderby="+ Constants.MaterialCategoryDesc+" &$filter= "+ Constants.BrandID+" eq '"+previousBrandId+"' and startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"') ";
                mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            if(mArrayCateogryTypeVal==null)
            {
                mArrayCateogryTypeVal = new String[2][1];
                mArrayCateogryTypeVal[0][0]="";
                mArrayCateogryTypeVal[1][0]="";
            }


            productCategoryAdapter = new ArrayAdapter<>(this,
                    R.layout.custom_textview, mArrayCateogryTypeVal[1]);
            productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
            sp_category.setAdapter(productCategoryAdapter);
            isCatFirstTime = true;
            sp_category.setSelection(getCategoryValueIndexKey());




        }
        else
        {
            if(previousCategoryId.equalsIgnoreCase(Constants.None) && !isCatFirstTime)
            {
                isCatFirstTime =true;
                try
                {
                    String mStrConfigQry = Constants.MaterialCategories+"?$orderby="+ Constants.MaterialCategoryDesc+" &$filter=startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"') ";
                    mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                if(mArrayCateogryTypeVal==null)
                {
                    mArrayCateogryTypeVal = new String[2][1];
                    mArrayCateogryTypeVal[0][0]="";
                    mArrayCateogryTypeVal[1][0]="";
                }


                productCategoryAdapter = new ArrayAdapter<>(this,
                        R.layout.custom_textview, mArrayCateogryTypeVal[1]);
                productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
                sp_category.setAdapter(productCategoryAdapter);

            }

        }



    }
    private int getBrandValueIndexKey()
    {

        int index = -1;
        for (int i=0;i<mArrayBrandTypeVal.length;i++)
        {
            if (mArrayBrandTypeVal[0][i].equals(previousBrandId)) {
                index = i;
                break;
            }
        }
        return index;
    }
    private int getCategoryValueIndexKey()
    {
        int index = -1;
        for (int i=0;i<mArrayCateogryTypeVal.length;i++)
        {
            if (mArrayCateogryTypeVal[0][i].equals(previousCategoryId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    //Update order materials based on the brands and category
    private void updateOrderMaterialGroups()
    {
        if(previousCategoryId.equalsIgnoreCase(Constants.None) && previousBrandId.equalsIgnoreCase(Constants.None))
        {
            mArrayOrderedGroup=null;
            try {
                String mStrConfigQry = Constants.OrderMaterialGroups+"?$orderby="+ Constants.OrderMaterialGroupDesc+" &$filter=startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"') ";
                mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        }
        else if(previousCategoryId.equalsIgnoreCase(Constants.None))
        {
            try {
                String mStrConfigQry = Constants.OrderMaterialGroups+"?$orderby="+ Constants.OrderMaterialGroupDesc+" &$filter="+ Constants.BrandID+" eq '"+previousBrandId+"' and startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"') ";
                mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        }
        else if(previousBrandId.equalsIgnoreCase(Constants.None))
        {
            try {
                String mStrConfigQry = Constants.OrderMaterialGroups+"?$orderby="+ Constants.OrderMaterialGroupDesc+" &$filter="+ Constants.MaterialCategoryID+" eq '"+previousCategoryId+"' and startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"') ";
                mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        }
        else
        {
            try {
                String mStrConfigQry = Constants.OrderMaterialGroups+"?$orderby="+ Constants.OrderMaterialGroupDesc+" &$filter="+ Constants.MaterialCategoryID+" eq '"+previousCategoryId+"' and "+ Constants.BrandID+" eq '"+previousBrandId+"' and startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"') ";
                mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        }
        if(mArrayOrderedGroup==null)
        {
            mArrayOrderedGroup = new String[2][1];
            mArrayOrderedGroup[0][0]="";
            mArrayOrderedGroup[1][0]="";
        }


        ArrayAdapter<String> productOrderGroupAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayOrderedGroup[1]);
        productOrderGroupAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_crs_sku_group.setAdapter(productOrderGroupAdapter);
        sp_crs_sku_group
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                    @Override
                    public void onItemSelected(AdapterView<?> parent, View arg1,
                                               int position, long arg3) {

                        mStrSelOrderMaterialID = mArrayOrderedGroup[0][position];
//                        getDBStockDetails();

                        if(mBoolFirstTime && !pdLoadDialog.isShowing()) {
                            loadAsyncTask();
                        }
                    }
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });

    }

    //Lists of Category from the Material Categories to the Spinner Category list
    private void getCategoryList()
    {
        try {
            String mStrConfigQry = Constants.MaterialCategories+"?$orderby="+ Constants.MaterialCategoryDesc+" &$filter=startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"')";
            mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if(mArrayCateogryTypeVal==null)
        {
            mArrayCateogryTypeVal = new String[2][1];
            mArrayCateogryTypeVal[0][0]="";
            mArrayCateogryTypeVal[1][0]="";
        }


        productCategoryAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayCateogryTypeVal[1]);
        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_category.setAdapter(productCategoryAdapter);
        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3)
            {
                previousCategoryId = mArrayCateogryTypeVal[0][position];
                if(isCatFirstTime)
                {
                    isCatFirstTime = false;
                }
                else if(previousCategoryId.equalsIgnoreCase(Constants.None) && previousBrandId.equalsIgnoreCase(Constants.None))
                {
                    resetSpinnervalues();
                }
                else
                {
                    if(previousCategoryId.equalsIgnoreCase(Constants.None)){
                        resetSpinnervalues();
                    }else{
                        updateBrandValuesInSpinner();
                    }


                }

//                updateOrderMaterialGroups();

            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    //Lists of Category from the Brands to the Spinner List
    public void getBrandList()
    {
        try {
            String mStrConfigQry = Constants.Brands+"?$orderby="+ Constants.BrandDesc+" &$filter=startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"') ";
            mArrayBrandTypeVal = OfflineManager.getBrandListValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if(mArrayBrandTypeVal==null)
        {
            mArrayBrandTypeVal = new String[4][1];
            mArrayBrandTypeVal[0][0]="";
            mArrayBrandTypeVal[1][0]="";
        }

        brandAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayBrandTypeVal[1]);
        brandAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_brand.setAdapter(brandAdapter);
        sp_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                previousBrandId = mArrayBrandTypeVal[0][position];
                if(isBrandFirstTime)
                {
                    isBrandFirstTime = false;

                }
                else
                {

                    updateCategoryValuesInSpinner();

                }
//                updateOrderMaterialGroups();
                if(mBoolFirstTime && !pdLoadDialog.isShowing()) {
                    loadAsyncTask();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


    }
    //DBStock details from CPStockItems
    private void getDBStockDetails()
    {
        String mStrMyStockQry="";
        try {
            String additionalQuery="";
            if(!TextUtils.isEmpty(mStrSelDistGuid) && !mStrSelDistGuid.equalsIgnoreCase(Constants.None)){
                additionalQuery=additionalQuery+" and "+ Constants.CPGUID+" eq '"+mStrSelDistGuid+"'";
            }
            if(!TextUtils.isEmpty(previousBrandId) && !previousBrandId.equalsIgnoreCase(Constants.None)){
                additionalQuery=additionalQuery+" and "+ ConstantsUtils.Brand+" eq '"+previousBrandId+"'";
            }
            if(!TextUtils.isEmpty(previousCategoryId) && !previousCategoryId.equalsIgnoreCase(Constants.None)){
                additionalQuery=additionalQuery+" and "+ ConstantsUtils.ProductCategoryID+" eq '"+previousCategoryId+"'";
            }
            if(!TextUtils.isEmpty(mStrSelOrderMaterialID) && !mStrSelOrderMaterialID.equalsIgnoreCase(Constants.None)){
                additionalQuery=additionalQuery+" and "+ Constants.OrderMaterialGroupID+" eq '"+mStrSelOrderMaterialID+"'";
            }

            mStrMyStockQry= Constants.CPStockItems+"?$orderby="+ Constants.OrderMaterialGroupDesc+" &$filter="+ Constants.StockOwner+" eq '01' " +
                    " and startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"') "+additionalQuery;
            alDBStockList = OfflineManager.getDBStockMatList(mStrMyStockQry,mStrSelDMSDIVID);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.Error+" : " + e.getMessage());
        }

    }

    //Distributors from CPSPRelations
    private void getDistributor()
    {
        try {
            String mStrDistQry= Constants.CPSPRelations+" ?$filter="+ Constants.CPTypeID+" eq '01' ";
            distList = OfflineManager.getDistributors(mStrDistQry);
            if(distList==null){
                distList =new String[4][1];
                distList[0][0] = "";
                distList[1][0] = "";
                distList[2][0] = "";
                distList[3][0] = "";
            }


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }
    private void resetSpinnervalues()
    {
        try {
            String mStrConfigQry = Constants.Brands+"?$orderby="+ Constants.BrandDesc+" &$filter=startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"') ";
            mArrayBrandTypeVal = OfflineManager.getBrandListValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if(mArrayBrandTypeVal==null)
        {
            mArrayBrandTypeVal = new String[4][1];
            mArrayBrandTypeVal[0][0]="";
            mArrayBrandTypeVal[1][0]="";
        }

        brandAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayBrandTypeVal[1]);
        brandAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_brand.setAdapter(brandAdapter);

        try {
            String mStrConfigQry = Constants.MaterialCategories+"?$orderby="+ Constants.MaterialCategoryDesc+" &$filter=startswith("+ Constants.DMSDivision+",'"+mStrSelDMSDIVID+"')";
            mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if(mArrayCateogryTypeVal==null)
        {
            mArrayCateogryTypeVal = new String[2][1];
            mArrayCateogryTypeVal[0][0]="";
            mArrayCateogryTypeVal[1][0]="";
        }


        productCategoryAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayCateogryTypeVal[1]);
        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_category.setAdapter(productCategoryAdapter);
    }
    //Set updated brand values in the spinner





    void initUI(){
        ll_dist_layout = (LinearLayout) findViewById(R.id.ll_dist_layout);
        tv_last_sync_time_value = (TextView) findViewById(R.id.tv_last_sync_time_value);
        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.CPStockItems, Constants.TimeStamp,this));
        lvDBStock = (ListView) findViewById(R.id.lv_dbstk);
        etSKUDescSearch = (EditText)findViewById(R.id.et_dbstk_search);
        sp_category = (Spinner) findViewById(R.id.sp_dbskt_cat);
        sp_brand = (Spinner) findViewById(R.id.sp_dbskt_brand);
        sp_crs_sku_group = (Spinner) findViewById(R.id.sp_dbskt_crs_sku_group);
        sp_distributor = (Spinner) findViewById(R.id.sp_distributor);
        ltNoRecords = (LinearLayout)findViewById(R.id.lay_no_records);

    }

    private void displayDistributorVal(){
        ArrayAdapter<String> productCategoryAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, distList[2]);
        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_distributor.setAdapter(productCategoryAdapter);

        sp_distributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                mStrSelDistGuid = distList[1][position];
                mStrSelDMSDIVID =   distList[3][position];
                if(mBoolFirstTime) {
                    loadAsyncTask();
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        if(distList[1].length==1)
        {
            mStrSelDistGuid = distList[1][0];
            mStrSelDMSDIVID =   distList[3][0];
            ll_dist_layout.setVisibility(View.GONE);
            sp_distributor.setVisibility(View.GONE);
        }
    }




    void displayDBStockList(){
        if(alDBStockList.size()==0)
            ltNoRecords.setVisibility(View.VISIBLE);
        else
            ltNoRecords.setVisibility(View.GONE);
        stockAdapter = new DBStockMaterialActivity.DBStockAdapter(getApplicationContext(), alDBStockList);
        lvDBStock.setEmptyView(findViewById(R.id.tv_empty_lay) );
        lvDBStock.setAdapter(stockAdapter);
        etSKUDescSearch = (EditText) findViewById(R.id.et_dbstk_search);
        etSKUDescSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                stockAdapter.getFilter().filter(s);
                stockAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    void onRefresh() {
        alAssignColl.clear();
        concatCollectionStr = "";
        alAssignColl.add(Constants.CPStockItems);
        concatCollectionStr = UtilConstants.getConcatinatinFlushCollectios(alAssignColl);

        try {
            Constants.isSync = true;
            dialogCancelled = false;
            new DBStockMaterialActivity.LoadingData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onRequestError(int operation, Exception exception) {
        ErrorBean errorBean = Constants.getErrorCode(operation, exception,DBStockMaterialActivity.this);
        if (errorBean.hasNoError()) {
            if (operation == Operation.OfflineRefresh.getValue()) {
                closePrgDialog();
                Constants.isSync = false;
                    UtilConstants.showAlert(errorBean.getErrorMsg(), DBStockMaterialActivity.this);
            }
        }else{
            closePrgDialog();
            Constants.isSync = false;
            Constants.displayMsgReqError(errorBean.getErrorCode(), DBStockMaterialActivity.this);
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
    public void onRequestSuccess(int operation, String s) throws ODataException, OfflineODataStoreException
    {

        if (operation == Operation.OfflineRefresh.getValue()) {
            try {
                OfflineManager.getAuthorizations(getApplicationContext());
            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }
            Constants.updateLastSyncTimeToTable(alAssignColl);



            closePrgDialog();
            Constants.isSync = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        DBStockMaterialActivity.this, R.style.MyTheme);
                builder.setMessage(getString(R.string.msg_sync_successfully_completed))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        tv_last_sync_time_value.setText(Constants.getLastSyncTime(Constants.SYNC_TABLE, Constants.Collections, Constants.CPStockItems, Constants.TimeStamp,DBStockMaterialActivity.this));
//                                        getDBStockDetails();
//                                        loadAsyncTask();

                                    }
                                });

                builder.show();
        }

    }

    /**
     * Adapter for displaying retailerList in ListView
     *
     */
    public class DBStockAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;
        DBStockBean stock;
        private DBStockMaterialActivity.DBStockAdapter.RetailerListFilter filter;
        private ArrayList<DBStockBean> dbStockOriginalValues = new ArrayList<>();
        private ArrayList<DBStockBean> dbStockDisplayValues = new ArrayList<>();

        DBStockAdapter(Context context, ArrayList<DBStockBean> items) {

            this.context = context;
            this.dbStockOriginalValues = items;
            this.dbStockDisplayValues = items;
        }

        @Override
        public int getCount() {
            return dbStockDisplayValues.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int pos, View view, ViewGroup arg2) {
            if (inflater == null) {
                inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if (view == null) {
                view = inflater
                        .inflate(R.layout.item_dbstock, null, true);
            }
            stock = dbStockDisplayValues.get(pos);
            final TextView tvSKUDesc = (TextView) view
                    .findViewById(R.id.item_dbstk_sku_desc);
            TextView tvCRSSKUGroup = (TextView) view
                    .findViewById(R.id.item_dbstk_crs_sku_group);
            final TextView tvDBStock = (TextView) view
                    .findViewById(R.id.item_dbstk_dbstock);
            ImageView expandIcon = (ImageView)view.findViewById(R.id.iv_expand_icon);

            expandIcon.setVisibility(View.GONE);
            tvCRSSKUGroup.setVisibility(View.GONE);
            tvSKUDesc.setVisibility(View.VISIBLE);

            tvSKUDesc.setText(stock.getMaterialDesc());
            tvCRSSKUGroup.setText(stock.getOrderMaterialGroupDesc());
            tvDBStock.setText(stock.getQAQty()+" "+stock.getUom());

//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    goToDbStockDetails(pos);
//                }
//            });



            return view;
        }

        public android.widget.Filter getFilter() {
            if (filter == null) {
                filter = new DBStockMaterialActivity.DBStockAdapter.RetailerListFilter();
            }
            return filter;
        }

        private void goToDbStockDetails(int pos)
        {
            Intent intent =  new Intent(context,DBStockDetails.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.MaterialNo,dbStockDisplayValues.get(pos).getMaterialNo());
            intent.putExtra(Constants.QAQty,dbStockDisplayValues.get(pos).getQAQty());
            intent.putExtra(Constants.UOM,dbStockDisplayValues.get(pos).getUom());
            intent.putExtra(Constants.MaterialDesc,dbStockDisplayValues.get(pos).getMaterialDesc());
            intent.putExtra(Constants.ManufacturingDate,dbStockDisplayValues.get(pos).getMFD());
            intent.putExtra(Constants.CPStockItemGUID,dbStockDisplayValues.get(pos).getCPStockItemGUID());
            intent.putExtra(Constants.OrderMaterialGroupID,dbStockDisplayValues.get(pos).getOrderMaterialGroupID());
            intent.putExtra(Constants.OrderMaterialGroupDesc,dbStockDisplayValues.get(pos).getOrderMaterialGroupDesc());
            context.startActivity(intent);

        }

        /**
         * This class search name based on Retailer name from list.
         */
        private class RetailerListFilter extends android.widget.Filter {
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults results = new FilterResults();
                if (dbStockOriginalValues == null) {
                    dbStockOriginalValues = new ArrayList<>(dbStockDisplayValues);
                }
                if (prefix == null || prefix.length() == 0) {
                    results.values = dbStockOriginalValues;
                    results.count = dbStockOriginalValues.size();
                } else {
                    String prefixString = prefix.toString().toLowerCase();
                    ArrayList<DBStockBean> filteredItems = new ArrayList<>();
                    int count = dbStockOriginalValues.size();

                    for (int i = 0; i < count; i++) {
                        DBStockBean item = dbStockOriginalValues.get(i);
//                        String mStrRetName = item.getOrderMaterialGroupDesc().toLowerCase();
                        String mStrRetName = item.getMaterialDesc().toLowerCase();
                        if (mStrRetName.contains(prefixString)) {
                            filteredItems.add(item);
                        }
                    }
                    results.values = filteredItems;
                    results.count = filteredItems.size();
                }
                return results;
            }
            @Override
            protected void publishResults(CharSequence prefix, FilterResults results) {
                //noinspection unchecked
                dbStockDisplayValues = (ArrayList<DBStockBean>) results.values; // has the filtered values
                notifyDataSetChanged();
                alDBStockList = dbStockDisplayValues;
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_invoice_his_list, menu);
        MenuItem menu_refresh = menu.findItem(R.id.menu_refresh_inv);
        if(!Constants.isSpecificCollTodaySyncOrNot(Constants.getLastSyncDate(Constants.SYNC_TABLE, Constants.Collections,
                Constants.CPStockItems, Constants.TimeStamp,DBStockMaterialActivity.this)))
        {
            menu_refresh.setVisible(true);
        }
        else
            menu_refresh.setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_refresh_inv:

                onRefresh();
                break;
        }
        return false;
    }

    public class LoadingData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            syncProgDialog = new ProgressDialog(DBStockMaterialActivity.this, R.style.ProgressDialogTheme);
            syncProgDialog.setMessage(getString(R.string.msg_sync_progress_msg_plz_wait));
            syncProgDialog.setCancelable(true);
            syncProgDialog.setCanceledOnTouchOutside(false);
            syncProgDialog.show();

            syncProgDialog
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface Dialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    DBStockMaterialActivity.this, R.style.MyTheme);
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

                    OfflineManager.refreshStoreSync(getApplicationContext(), DBStockMaterialActivity.this, Constants.Fresh, concatCollectionStr);
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


    private void loadAsyncTask(){
        try {
            new DBStockMaterialActivity.GetDBStockData().execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*AsyncTask to get DBStock*/
    private class GetDBStockData extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = new ProgressDialog(DBStockMaterialActivity.this, R.style.ProgressDialogTheme);
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
            if(!mBoolFirstTime){
                mBoolFirstTime = true;
            }
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