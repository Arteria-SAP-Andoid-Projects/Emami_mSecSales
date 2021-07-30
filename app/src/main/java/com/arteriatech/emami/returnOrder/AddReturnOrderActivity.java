package com.arteriatech.emami.returnOrder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

public class AddReturnOrderActivity extends AppCompatActivity implements View.OnClickListener{

    public final static int RETURN_ORDER_RESULT_ID = 867;
    private TextView dialogEmptyTextView;
    private RecyclerView dialogRecyclerView;
    private Spinner spCategory;
    private ReturnOrderDialogAdapter returnOrderDialogAdapter;
    private EditText edSearch;
    private ArrayList<ReturnOrderBean> distStockList = new ArrayList<>();
    private ArrayList<ReturnOrderBean> finalSendResult = new ArrayList<>();
    private Spinner spBrand;
    private Spinner spCrsSkuGrp;
    private LinearLayout showAllDropDown;
    private String[][] mArrayCateogryTypeVal = null;
    private ArrayAdapter<String> productCategoryAdapter;
    private String previousCategoryId = "";
    private boolean isCatFirstTime = true;
    private String previousBrandId = "";
    private String[][] mArrayBrandTypeVal = null;
    private ArrayAdapter<String> brandAdapter;
    private boolean isBrandFirstTime = true;
    private String[][] mArrayOrderedGroup = null;
    private String mStrSelOrderMaterialID = "";
    private String finalBrandId = "";
    private String finalProductCatId = "";
    private String finalOrderMaterialId = "";
    private String finalSearchData = "";
    private ProgressDialog pdLoadDialog = null;
    private Button btn_ok,btn_cancel;

    private String typevalue="",mStrStockOwner="";
    TextView tv_crs_sku_label;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_window_sample_disb_sel);
        ActionBarView.initActionBarView(this, false, getString(R.string.title_return_order_create));
        if (!Constants.restartApp(AddReturnOrderActivity.this)) {
            Bundle bundleExtras = getIntent().getExtras();
            if (bundleExtras != null) {
                mStrStockOwner = bundleExtras.getString(Constants.StockOwner);
            }
            showAllDropDown = (LinearLayout) findViewById(R.id.show_all_drop_down);
            showAllDropDown.setVisibility(View.VISIBLE);
            dialogEmptyTextView = (TextView) findViewById(R.id.no_record_found);
            dialogRecyclerView = (RecyclerView) findViewById(R.id.dialog_recycler_view);
            spCategory = (Spinner) findViewById(R.id.sp_category);
            spBrand = (Spinner) findViewById(R.id.sp_brand);
            spCrsSkuGrp = (Spinner) findViewById(R.id.sp_crs_sku_group);
            dialogRecyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            dialogRecyclerView.setLayoutManager(linearLayoutManager);
            returnOrderDialogAdapter = new ReturnOrderDialogAdapter(AddReturnOrderActivity.this, distStockList);
            dialogRecyclerView.setAdapter(returnOrderDialogAdapter);
            edSearch = (EditText) findViewById(R.id.et_dbstk_search);
            btn_ok = (Button) findViewById(R.id.btn_ok);
            btn_ok.setOnClickListener(this);
            btn_cancel = (Button) findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(this);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            edSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    finalSearchData = s + "";
                    returnOrderDialogAdapter.filter(finalSearchData, dialogEmptyTextView, dialogRecyclerView, finalBrandId, finalProductCatId, finalOrderMaterialId);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            new GetReturnOrderList().execute();
        }

    }
    private void getTypeValue() {


        tv_crs_sku_label= (TextView) findViewById(R.id.tv_crs_sku_label);

        typevalue=Constants.getTypesetValueForSkugrp(AddReturnOrderActivity.this);
        if(typevalue.equalsIgnoreCase(Constants.SKUGROUP)){
            tv_crs_sku_label.setText(Constants.SKUGROUP);
            // etSKUDescSearch.setHint(R.string.lbl_Search_by_skugroup);
        }else{
            tv_crs_sku_label.setText(Constants.CRSSKUGROUP);
            //  etSKUDescSearch.setHint(R.string.lbl_Search_by_crsskugroup);
        }
    }
    private void getDistributorStock(String querys) {
        try {
            distStockList.clear();
            distStockList = OfflineManager.getReturnOrderList(querys, distStockList);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }

    }

    private void getCategoryList() {
        try {
            String mStrConfigQry = Constants.MaterialCategories+"?$orderby="+Constants.MaterialCategoryDesc;
            mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (mArrayCateogryTypeVal == null) {
            mArrayCateogryTypeVal = new String[2][0];
            mArrayCateogryTypeVal[0][0] = "";
            mArrayCateogryTypeVal[1][0] = "";
        }


        productCategoryAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayCateogryTypeVal[1]);
        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spCategory.setAdapter(productCategoryAdapter);
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                previousCategoryId = mArrayCateogryTypeVal[0][position];
                if (isCatFirstTime) {
                    isCatFirstTime = false;
                } else if (previousCategoryId.equalsIgnoreCase(Constants.None) && previousBrandId.equalsIgnoreCase(Constants.None)) {
                    resetSpinnervalues();
                } else {
//                    updateBrandValuesInSpinner();

                    if(previousCategoryId.equalsIgnoreCase(Constants.None)){
                        resetSpinnervalues();
                    }else{
                        updateBrandValuesInSpinner();
                    }

                }

                updateOrderMaterialGroups();

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    public void getBrandList() {
        try {
            String mStrConfigQry = Constants.Brands+"?$orderby="+Constants.BrandDesc;
            mArrayBrandTypeVal = OfflineManager.getBrandListValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (mArrayBrandTypeVal == null) {
            mArrayBrandTypeVal = new String[4][1];
            mArrayBrandTypeVal[0][0] = "";
            mArrayBrandTypeVal[1][0] = "";
        }

        brandAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayBrandTypeVal[1]);
        brandAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spBrand.setAdapter(brandAdapter);
        spBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                previousBrandId = mArrayBrandTypeVal[0][position];
                if (isBrandFirstTime) {
                    isBrandFirstTime = false;

                } else {

                    updateCategoryValuesInSpinner();

                }
                updateOrderMaterialGroups();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void updateCategoryValuesInSpinner() {

        if (!previousBrandId.equalsIgnoreCase(Constants.None)) {
            try {
                String mStrConfigQry = Constants.BrandsCategories + "?$orderby="+Constants.MaterialCategoryDesc+" &$filter= " + Constants.BrandID + " eq '" + previousBrandId + "'";
                mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
            if (mArrayCateogryTypeVal == null) {
                mArrayCateogryTypeVal = new String[2][1];
                mArrayCateogryTypeVal[0][0] = "";
                mArrayCateogryTypeVal[1][0] = "";
            }


            productCategoryAdapter = new ArrayAdapter<>(this,
                    R.layout.custom_textview, mArrayCateogryTypeVal[1]);
            productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
            spCategory.setAdapter(productCategoryAdapter);
            isCatFirstTime = true;
            spCategory.setSelection(getCategoryValueIndexKey());


        } else {
            if (previousCategoryId.equalsIgnoreCase(Constants.None) && !isCatFirstTime) {
                isCatFirstTime = true;
                try {
                    String mStrConfigQry = Constants.MaterialCategories+"?$orderby="+Constants.MaterialCategoryDesc;
                    mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
                } catch (OfflineODataStoreException e) {
                    LogManager.writeLogError(Constants.error_txt + e.getMessage());
                }
                if (mArrayCateogryTypeVal == null) {
                    mArrayCateogryTypeVal = new String[2][1];
                    mArrayCateogryTypeVal[0][0] = "";
                    mArrayCateogryTypeVal[1][0] = "";
                }


                productCategoryAdapter = new ArrayAdapter<>(this,
                        R.layout.custom_textview, mArrayCateogryTypeVal[1]);
                productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
                spCategory.setAdapter(productCategoryAdapter);

            }

        }


    }

    private int getCategoryValueIndexKey() {
        int index = -1;
        try {
            for (int i = 0; i < mArrayCateogryTypeVal[0].length; i++) {
                if (mArrayCateogryTypeVal[0][i].equals(previousCategoryId)) {
                    index = i;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }

    private void resetSpinnervalues() {
        try {
            String mStrConfigQry = Constants.Brands+"?$orderby="+Constants.BrandDesc;
            mArrayBrandTypeVal = OfflineManager.getBrandListValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (mArrayBrandTypeVal == null) {
            mArrayBrandTypeVal = new String[4][1];
            mArrayBrandTypeVal[0][0] = "";
            mArrayBrandTypeVal[1][0] = "";
        }

        brandAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayBrandTypeVal[1]);
        brandAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spBrand.setAdapter(brandAdapter);

        try {
            String mStrConfigQry = Constants.MaterialCategories+"?$orderby="+Constants.MaterialCategoryDesc;
            mArrayCateogryTypeVal = OfflineManager.getCategoryListValues(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (mArrayCateogryTypeVal == null) {
            mArrayCateogryTypeVal = new String[2][0];
            mArrayCateogryTypeVal[0][0] = "";
            mArrayCateogryTypeVal[1][0] = "";
        }


        productCategoryAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayCateogryTypeVal[1]);
        productCategoryAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spCategory.setAdapter(productCategoryAdapter);
    }

    private void updateBrandValuesInSpinner() {
        if (!previousCategoryId.equalsIgnoreCase(Constants.None)) {
            try {
                mArrayBrandTypeVal = OfflineManager.getBrandListValues(Constants.BrandsCategories + "?$orderby="+Constants.BrandDesc+" &$filter= " + Constants.MaterialCategoryID + " eq '" + previousCategoryId + "'");
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
            spBrand.setAdapter(brandAdapter);

            isBrandFirstTime = true;

            spBrand.setSelection(getBrandValueIndexKey());


        } else {
            if (previousBrandId.equalsIgnoreCase(Constants.None) && !isBrandFirstTime) {
                isBrandFirstTime = true;
                try {
                    mArrayBrandTypeVal = OfflineManager.getBrandListValues(Constants.Brands+"?$orderby="+Constants.BrandDesc);
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
                spBrand.setAdapter(brandAdapter);
            }
        }
    }

    private int getBrandValueIndexKey() {

        int index = -1;
        try {
            for (int i = 0; i < mArrayBrandTypeVal[0].length; i++) {
                if (mArrayBrandTypeVal[0][i].equals(previousBrandId)) {
                    index = i;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }

    private void updateOrderMaterialGroups() {
        String orderMatGrpQuery = "";
        if (!TextUtils.isEmpty(previousBrandId) && !previousBrandId.equalsIgnoreCase(Constants.None)) {
            orderMatGrpQuery = Constants.BrandID + " eq '" + previousBrandId + "'";
        }
        if (!TextUtils.isEmpty(previousCategoryId) && !previousCategoryId.equalsIgnoreCase(Constants.None)) {
            if (!TextUtils.isEmpty(orderMatGrpQuery))
                orderMatGrpQuery = orderMatGrpQuery + " and " + Constants.MaterialCategoryID + " eq '" + previousCategoryId + "'";
            else
                orderMatGrpQuery = Constants.MaterialCategoryID + " eq '" + previousCategoryId + "'";
        }

        try {
            if (!TextUtils.isEmpty(orderMatGrpQuery)) {
                orderMatGrpQuery = Constants.OrderMaterialGroups + "?$orderby="+Constants.OrderMaterialGroupDesc+" &$filter=" + orderMatGrpQuery;
            } else {
                orderMatGrpQuery = Constants.OrderMaterialGroups+"?$orderby="+Constants.OrderMaterialGroupDesc;
            }
            mArrayOrderedGroup = OfflineManager.getOrderedMaterialGroups(orderMatGrpQuery);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (mArrayOrderedGroup == null) {
            mArrayOrderedGroup = new String[2][0];
            mArrayOrderedGroup[0][0] = "";
            mArrayOrderedGroup[1][0] = "";
        }


        ArrayAdapter<String> productOrderGroupAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayOrderedGroup[1]);
        productOrderGroupAdapter.setDropDownViewResource(R.layout.spinnerinside);
        spCrsSkuGrp.setAdapter(productOrderGroupAdapter);
        spCrsSkuGrp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {
                mStrSelOrderMaterialID = mArrayOrderedGroup[0][position];
                getDBStockDetails();
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    private void getDBStockDetails() {
        if (!TextUtils.isEmpty(previousBrandId) && !previousBrandId.equalsIgnoreCase(Constants.None)) {
            finalBrandId = previousBrandId;
        } else {
            finalBrandId = "";
        }
        if (!TextUtils.isEmpty(previousCategoryId) && !previousCategoryId.equalsIgnoreCase(Constants.None)) {
            finalProductCatId = previousCategoryId;
        } else {
            finalProductCatId = "";
        }
        if (!TextUtils.isEmpty(mStrSelOrderMaterialID) && !mStrSelOrderMaterialID.equalsIgnoreCase(Constants.None)) {
            finalOrderMaterialId = mStrSelOrderMaterialID;
        } else {
            finalOrderMaterialId = "";
        }

        returnOrderDialogAdapter.filter(finalSearchData, dialogEmptyTextView, dialogRecyclerView, finalBrandId, finalProductCatId, finalOrderMaterialId);
        edSearch.setText("");
    }

   /* @Override
    public void onBackPressed() {
        sendResultToOtherActivity(AddReturnOrderActivity.this);

    }*/

    private void sendResultToOtherActivity(final Context mContext) {
        finalSendResult.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (ReturnOrderBean returnOrderBean : distStockList) {
                    if (returnOrderBean.getSelected()) {
                        finalSendResult.add(returnOrderBean);
                    }
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(finalSendResult.size()>0) {
                            Intent intent = new Intent(mContext, ReturnOrderCreate.class);
                            intent.putExtra(ConstantsUtils.EXTRA_ARRAY_LIST, finalSendResult);
                            setResult(RETURN_ORDER_RESULT_ID, intent);
                            finish();
                        }else{
                            UtilConstants.showAlert(getString(R.string.validation_sel_atlest_one_material), AddReturnOrderActivity.this);
                        }
                    }
                });
            }
        }).start();
    }

    private class GetReturnOrderList extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadDialog = Constants.showProgressDialog(AddReturnOrderActivity.this, "", getString(R.string.app_loading));
        }

        @Override
        protected Void doInBackground(String... params) {
            String query = Constants.CPStockItems + "?$orderby="+Constants.MaterialDesc+" &$filter=" + Constants.StockOwner + " eq '"+mStrStockOwner+"' and " + Constants.MaterialNo + " ne '' ";
            getDistributorStock(query);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoadDialog != null) {
                Constants.hideProgressDialog(pdLoadDialog);
            }
            getCategoryList();
            getBrandList();
        }
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.btn_ok:
                sendResultToOtherActivity(AddReturnOrderActivity.this);
                break;
        }
    }
}
