package com.arteriatech.emami.sampleDisbursement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.arteriatech.emami.retailerStock.RetailerStockBean;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10769 on 04-04-2017.
 */

public class AddSampleDisbursementActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout showAllDropDown;
    private LinearLayout llCategory;
    private Spinner spBrand;
    private Spinner spCrsSkuGrp;
    private RecyclerView dialogRecyclerView;
    private ArrayList<RetailerStockBean> retailerStockBeanPopupArrayList = new ArrayList<>();
    private ArrayList<RetailerStockBean> finalRetailerStockBean = new ArrayList<>();
    private SampleDisbursementDialogAdapter sampleDisbursementDialogAdapter;
    private EditText edSearch;
    private TextView dialogEmptyTextView;
    private String[][] mArrayBrandTypeVal = null;
    private ArrayAdapter<String> brandAdapter = null;
    private String previousBrandId = "";
    private String[][] mArrayOrderedGroup = null;
    private String mStrSelOrderMaterialID = "";
    private String finalBrandId = "";
    private String finalOrderMaterialId = "";
    private String finalSearchData = "";
    public static final int SD_RESULT_ID=6778;
    private Button btn_ok,btn_cancel;


    private String typevalue="";
    TextView tv_crs_sku_label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_up_window_sample_disb_sel);
        ActionBarView.initActionBarView(this, false, getString(R.string.sample_disbursement_title));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (!Constants.restartApp(AddSampleDisbursementActivity.this)) {
            Intent intent = getIntent();
            if (intent != null) {
                ArrayList<RetailerStockBean> returnOrderBeanArrayList = (ArrayList<RetailerStockBean>) intent.getSerializableExtra(ConstantsUtils.EXTRA_ARRAY_LIST);
                retailerStockBeanPopupArrayList.addAll(returnOrderBeanArrayList);
            }
            showAllDropDown = (LinearLayout) findViewById(R.id.show_all_drop_down);
            llCategory = (LinearLayout) findViewById(R.id.ll_category);
            showAllDropDown.setVisibility(View.VISIBLE);
            llCategory.setVisibility(View.GONE);
            spBrand = (Spinner) findViewById(R.id.sp_brand);
            btn_ok = (Button) findViewById(R.id.btn_ok);
            btn_ok.setOnClickListener(this);
            btn_cancel = (Button) findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(this);
            spCrsSkuGrp = (Spinner) findViewById(R.id.sp_crs_sku_group);
            dialogEmptyTextView = (TextView) findViewById(R.id.no_record_found);
            dialogRecyclerView = (RecyclerView) findViewById(R.id.dialog_recycler_view);
            dialogRecyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            dialogRecyclerView.setLayoutManager(linearLayoutManager);
            sampleDisbursementDialogAdapter = new SampleDisbursementDialogAdapter(AddSampleDisbursementActivity.this, retailerStockBeanPopupArrayList, 1);
            dialogRecyclerView.setAdapter(sampleDisbursementDialogAdapter);
            edSearch = (EditText) findViewById(R.id.et_dbstk_search);
            edSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    finalSearchData = s + "";
                    sampleDisbursementDialogAdapter.filterSampleDisbursement(finalSearchData, dialogEmptyTextView, dialogRecyclerView, finalBrandId, finalOrderMaterialId);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            getBrandList();
        }
        getTypeValue();
    }
    private void getTypeValue() {


        tv_crs_sku_label= (TextView) findViewById(R.id.tv_crs_sku_label);

        typevalue=Constants.getTypesetValueForSkugrp(AddSampleDisbursementActivity.this);
        if(typevalue.equalsIgnoreCase(Constants.SKUGROUP)){
            tv_crs_sku_label.setText(Constants.SKUGROUP);
            // etSKUDescSearch.setHint(R.string.lbl_Search_by_skugroup);
        }else{
            tv_crs_sku_label.setText(Constants.CRSSKUGROUP);
            //  etSKUDescSearch.setHint(R.string.lbl_Search_by_crsskugroup);
        }
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
                updateOrderMaterialGroups();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateOrderMaterialGroups() {
        String orderMatGrpQuery = "";
        if (!TextUtils.isEmpty(previousBrandId) && !previousBrandId.equalsIgnoreCase(Constants.None)) {
            orderMatGrpQuery = Constants.BrandID + " eq '" + previousBrandId + "'";
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
        if (!TextUtils.isEmpty(mStrSelOrderMaterialID) && !mStrSelOrderMaterialID.equalsIgnoreCase(Constants.None)) {
            finalOrderMaterialId = mStrSelOrderMaterialID;
        } else {
            finalOrderMaterialId = "";
        }
        sampleDisbursementDialogAdapter.filterSampleDisbursement(finalSearchData, dialogEmptyTextView, dialogRecyclerView, finalBrandId, finalOrderMaterialId);
        edSearch.setText("");
    }

  /*  @Override
    public void onBackPressed() {
        sendResultToOtherActivity(AddSampleDisbursementActivity.this);
    }*/
    private void sendResultToOtherActivity(final Context mContext) {
        finalRetailerStockBean.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (RetailerStockBean retailerStockBean : retailerStockBeanPopupArrayList) {
                    if (retailerStockBean.getSelected()) {
                        finalRetailerStockBean.add(retailerStockBean);
                    }
                }
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(finalRetailerStockBean.size()>0) {
                            Intent intent = new Intent(mContext, SampleDisbursementActivity.class);
                            intent.putExtra(ConstantsUtils.EXTRA_ARRAY_LIST, finalRetailerStockBean);
                            setResult(SD_RESULT_ID, intent);
                            finish();
                        }else{
                            UtilConstants.showAlert(getString(R.string.validation_sel_atlest_one_material), AddSampleDisbursementActivity.this);
                        }

                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.btn_ok:
                sendResultToOtherActivity(AddSampleDisbursementActivity.this);
                break;
        }
    }
}
