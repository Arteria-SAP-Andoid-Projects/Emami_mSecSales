package com.arteriatech.emami.customerComplaints;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.returnOrder.ReturnOrderListFragment;

public class CustomerListActivity extends AppCompatActivity {

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "";
    private String mStrBundleRetUID = "";
    private String mStrBundleCPGUID = "";
    private int comingFrom = 0;
    private TextView etName;
    private TextView retId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);
        ActionBarView.initActionBarView(this, true, getString(R.string.customer_complaints_list_title));
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleRetUID = bundleExtras.getString(Constants.CPUID);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            comingFrom = bundleExtras.getInt(Constants.comingFrom, 0);
        }
        etName = (TextView) findViewById(R.id.tv_reatiler_name);
        retId = (TextView) findViewById(R.id.tv_reatiler_id);
        etName.setText(mStrBundleRetName);
        retId.setText(mStrBundleRetUID);
        Bundle bundleLeft = new Bundle();
        bundleLeft.putString(Constants.CPGUID, mStrBundleCPGUID);
        bundleLeft.putString(Constants.CPNo, mStrBundleRetID);
        bundleLeft.putString(Constants.RetailerName, mStrBundleRetName);
        bundleLeft.putString(Constants.CPUID, mStrBundleRetUID);
        bundleLeft.putInt(Constants.comingFrom, comingFrom);
        if (!Constants.restartApp(CustomerListActivity.this)) {
            if (savedInstanceState == null) {
                Fragment fragment = new ReturnOrderListFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragment.setArguments(bundleLeft);
                fragmentTransaction.replace(R.id.fl_container, fragment);
                fragmentTransaction.commit();
            }
        }
    }
}
