package com.arteriatech.emami.reports;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.adapter.BehaviourListAdapter;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.common.ConstantsUtils;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;

import java.util.ArrayList;

/**
 * Created by e10526 on 03-02-2017.
 *
 */
public class BehaviourListActivity extends AppCompatActivity {
    private ArrayList<CustomerBean> alBehaviourList = null;
    private String[][] mArrayRetailerCategory =null;
    private  String mStrBehaviuorTypeCode ="";
    private ListView lv_behaviour_ret_list;
    Spinner sp_behaviour_type;

    TextView tv_mtd_heading,tv_sno_headig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true,getString(R.string.title_behaviour_list));
        setContentView(R.layout.activity_behaviour_list);
        if (!Constants.restartApp(BehaviourListActivity.this)) {
            initUI();
            setValuesToUI();
        }

    }

    private  void initUI(){
        tv_mtd_heading = (TextView) findViewById(R.id.tv_mtd_heading_val);
        tv_sno_headig = (TextView) findViewById(R.id.tv_sno_headig);
        lv_behaviour_ret_list = (ListView)findViewById(R.id.lv_behaviour_ret_list);
        sp_behaviour_type = (Spinner) findViewById(R.id.sp_behaviour_type);
    }

    private void setValuesToUI(){
        getRetailerCategory();
        setSpinnerValues();
    }

    private void setSpinnerValues(){

        if (mArrayRetailerCategory == null || mArrayRetailerCategory[0].length==0) {
            mArrayRetailerCategory = new String[4][1];
            mArrayRetailerCategory[0][0] = "";
            mArrayRetailerCategory[1][0] = "";
            mArrayRetailerCategory[2][0] = "";
            mArrayRetailerCategory[3][0] = "";
        }

        ArrayAdapter<String> distributorNameAdapter = new ArrayAdapter<>(this,
                R.layout.custom_textview, mArrayRetailerCategory[1]);
        distributorNameAdapter.setDropDownViewResource(R.layout.spinnerinside);
        sp_behaviour_type.setAdapter(distributorNameAdapter);
        try {
            sp_behaviour_type.setSelection(ConstantsUtils.getSelectionPosition(mArrayRetailerCategory, "000001", 0));
        }catch (Exception e){
            e.printStackTrace();
        }
        sp_behaviour_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mStrBehaviuorTypeCode = mArrayRetailerCategory[0][position];

                if(mStrBehaviuorTypeCode.equalsIgnoreCase(Constants.NotPurchasedType)){
                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT,1f);
                    llp.setMargins(1, 0, 1, 0); // llp.setMargins(left, top, right, bottom);
                    tv_sno_headig.setLayoutParams(llp);
                    tv_mtd_heading.setVisibility(View.GONE);
                }else {
                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(20, ViewGroup.LayoutParams.MATCH_PARENT,1f);
                    llp.setMargins(1, 0, 1, 0); // llp.setMargins(left, top, right, bottom);
                    tv_sno_headig.setLayoutParams(llp);
                    tv_mtd_heading.setVisibility(View.VISIBLE);
                }

                getBehaviourList(mStrBehaviuorTypeCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }



    private void getBehaviourList(String retailerCategory){
        try {
            String query = Constants.SPChannelEvaluationList + "?$filter="
                    + Constants.EvaluationTypeID + " eq '" + retailerCategory + "'";
            if(retailerCategory.equalsIgnoreCase(Constants.NotPurchasedType)){
                query=query+" &$orderby = "+Constants.CPName+" asc";
            }else {
                query=query+" &$orderby = "+Constants.SequenceNo+" asc";
            }
            alBehaviourList= OfflineManager.getBehavoiurList(query);


        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        displayBehaviuorList();
    }

    private void displayBehaviuorList(){
        BehaviourListAdapter behaviourListAdapter = new BehaviourListAdapter(this, alBehaviourList,mStrBehaviuorTypeCode);
        lv_behaviour_ret_list.setEmptyView(findViewById(R.id.tv_empty_lay) );
        lv_behaviour_ret_list.setAdapter(behaviourListAdapter);
        behaviourListAdapter.notifyDataSetChanged();
    }



    private  void getRetailerCategory(){

        try{
            String mStrConfigQry = Constants.ValueHelps + "?$filter="+ Constants.EntityType+" eq 'Evaluation'";
            mArrayRetailerCategory = OfflineManager.getConfigListWithDefultVal(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

    }


}
