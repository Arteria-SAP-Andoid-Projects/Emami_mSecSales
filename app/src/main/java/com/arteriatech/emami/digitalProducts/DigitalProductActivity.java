package com.arteriatech.emami.digitalProducts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;

public class DigitalProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_product);
        ActionBarView.initActionBarView(this, true, getString(R.string.title_digital_product));
        if (!Constants.restartApp(DigitalProductActivity.this)) {
            Fragment fragment = new DigitalProductFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_container, fragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        Constants.deleteFolder();
   //     Toast.makeText(DigitalProductActivity.this,"Deleted",Toast.LENGTH_LONG).show();
    }
}
