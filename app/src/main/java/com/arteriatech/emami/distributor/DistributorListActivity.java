package com.arteriatech.emami.distributor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.location.LocationUtils;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;
import com.sap.smp.client.odata.impl.ODataPropertyDefaultImpl;

import java.math.BigDecimal;
import java.util.ArrayList;

public class DistributorListActivity extends AppCompatActivity implements DistributorView, View.OnClickListener {

    private ProgressDialog pdLoadDialog = null;
    private Spinner sprDistributor;
    private DistributorViewPresenterImpl distributorViewPresenter = null;
    private TextView tv_address;
    private TextView tv_contact_num;
    private TextView tv_gst_in;
    private TextView tv_lat_value;
    private TextView tv_long_value;
    private ImageView iv_lat_long_icon;
    private ImageView iv_call;
    private ImageView iv_sms;
    private ImageView iv_mail;
    private ImageView iv_whatsApp;
    private LinearLayout ll_gst;
    private LinearLayout ll_mobile;

    private String mStrAddress;
    private String mStrMobile;
    private String mStrGstIN;
    private double mStrLongVal;
    private double mStrLatVal;
    private String mStrEmailID;
    private String cpNo;
    private ODataPropMap oDataProperties;
    private ODataProperty oDataProperty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_distributor_list);
        ActionBarView.initActionBarView(this, true, getString(R.string.title_distributor));
        initz();
    }

    private void initz() {
        iv_call = findViewById(R.id.call);
        iv_call.setOnClickListener(this);

        iv_sms = findViewById(R.id.sms);
        iv_sms.setOnClickListener(this);

        iv_mail = findViewById(R.id.mail);
        iv_mail.setOnClickListener(this);

        iv_whatsApp = findViewById(R.id.whats_app);
        iv_whatsApp.setOnClickListener(this);

        sprDistributor = findViewById(R.id.sp_distributor);
        tv_address = findViewById(R.id.tv_address);
        tv_contact_num = findViewById(R.id.tv_contact_num);
        tv_gst_in = findViewById(R.id.tv_gst_in);
        tv_lat_value = findViewById(R.id.tv_lat_value);
        tv_long_value = findViewById(R.id.tv_long_value);
        ll_gst = findViewById(R.id.ll_gst);
        ll_mobile = findViewById(R.id.ll_mobile);
        iv_lat_long_icon = findViewById(R.id.iv_lat_long_icon);
        iv_lat_long_icon.setOnClickListener(this);
        distributorViewPresenter = new DistributorViewPresenterImpl(this, this, this);
        distributorViewPresenter.distributorList();
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
    public void hideProgress() {
        try {
            if (pdLoadDialog != null) {
                pdLoadDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showProgress() {
        pdLoadDialog = new ProgressDialog(DistributorListActivity.this, R.style.ProgressDialogTheme);
        pdLoadDialog.setMessage(getString(R.string.app_loading));
        pdLoadDialog.setCancelable(false);
        pdLoadDialog.show();
    }

    @Override
    public void spdistributorData(final ArrayList<DistributorBean> alDistributorBeans) {
        try {
            ArrayAdapter<DistributorBean> adapter = new ArrayAdapter<DistributorBean>(this, android.R.layout.simple_spinner_item, alDistributorBeans);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sprDistributor.setAdapter(adapter);

            sprDistributor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    DistributorBean distributorBean = alDistributorBeans.get(position);
                    cpNo = distributorBean.getcPNo();
                    distributorViewPresenter.getDistributorData(cpNo);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setdistributorData(CustomerBean customerBean) {
        mStrAddress = Constants.getAddress(customerBean);
        tv_address.setText(mStrAddress);
        mStrMobile = customerBean.getMobileNumber();
        mStrGstIN = customerBean.getGstin();
        mStrLongVal = customerBean.getLongVal();
        mStrLatVal = customerBean.getLatVal();
        mStrEmailID = customerBean.getEmail();

        if(!TextUtils.isEmpty(mStrMobile) && mStrMobile.length()>0){
            tv_contact_num.setText(mStrMobile);
            ll_mobile.setVisibility(View.VISIBLE);
        }else {
            ll_mobile.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(mStrGstIN) && mStrGstIN.length()>0){
            tv_gst_in.setText(mStrGstIN);
            ll_gst.setVisibility(View.VISIBLE);
        }else {
            ll_gst.setVisibility(View.GONE);
        }

        tv_long_value.setText(Constants.addZerosAfterDecimal(mStrLongVal + "", 12));

        tv_lat_value.setText(Constants.addZerosAfterDecimal(mStrLatVal + "", 12));

        if (mStrLongVal == 0.0 || mStrLatVal == 0.0) {
            iv_lat_long_icon.setImageResource(R.drawable.ic_loca_red_mark_small);
        } else {
            iv_lat_long_icon.setImageResource(R.drawable.ic_loca_green_mark_small);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call:
                onCall();
                break;
            case R.id.sms:
                onSMS();
                break;
            case R.id.mail:
                onMail();
                break;
            case R.id.whats_app:
                whatsAppCall();
                break;
            case R.id.iv_lat_long_icon:
                setLatLongVal();
                break;
        }
    }

    /*
     * TODO This method make a call.
     */
    private void onCall() {
        try {
            if (!mStrMobile.equalsIgnoreCase("")) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse(Constants.tel_txt + (mStrMobile)));
                startActivity(dialIntent);
            } else {
                Constants.customAlertMessage(this, getString(R.string.alert_mobile_no_maintend));
            }
        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    /*
     * TODO This method make a sms.
     */
    private void onSMS() {
        if (!mStrMobile.equalsIgnoreCase("")) {
            Uri smsUri = Uri.parse(Constants.sms_txt + mStrMobile);
            Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
            startActivity(intent);
        } else {
            Constants.customAlertMessage(this, getString(R.string.alert_mobile_no_maintend));

        }
    }

    /*
     * TODO This method make a whats up call.
     */
    private void whatsAppCall() {
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.setComponent(new ComponentName(Constants.whatsapp_packagename, Constants.whatsapp_conv_packagename));
            sendIntent.putExtra(Constants.jid, PhoneNumberUtils.stripSeparators(mStrMobile) + Constants.whatsapp_domainname);
            startActivity(sendIntent);

        } catch (Exception e) {
            Constants.customAlertMessage(this, getString(R.string.alert_whatsapp_not_installed));
        }
    }

    private void onPermissionLatLong() {
        pdLoadDialog = Constants.showProgressDialog(this, "", getString(R.string.checking_pemission));

        LocationUtils.checkLocationPermission(this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel location, String errorMsg, int errorCode) {
                closeProgressDialog();
                if (status) {
                    locationPerGranted();
                }
            }
        });
    }

    private void setLatLonValues() {
        tv_lat_value.setText(Constants.addZerosAfterDecimal(UtilConstants.latitude + "", 12));
        tv_long_value.setText(Constants.addZerosAfterDecimal(UtilConstants.longitude + "", 12));
        iv_lat_long_icon.setImageResource(R.drawable.ic_loca_green_mark_small);
    }

    private void locationPerGranted() {
        pdLoadDialog = Constants.showProgressDialog(this, "", getString(R.string.gps_progress));
        Constants.getLocation(this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closeProgressDialog();
                if (status) {
                    setLatLonValues();
                    updateLatAndLonToCustomer();
                }
            }
        });
    }

    private void updateLatAndLonToCustomer() {
        ODataEntity retilerEntity = null;
        try {
            String retDetgry = Constants.Customers + "?$filter=CustomerNo eq '" + cpNo + "'";
            retilerEntity = OfflineManager.getRetDetails(retDetgry);
            try {
                if(retilerEntity != null) {
                    retilerEntity.getProperties().put(Constants.Latitude,
                            new ODataPropertyDefaultImpl(Constants.Latitude, BigDecimal.valueOf(UtilConstants.latitude)));
                    retilerEntity.getProperties().put(Constants.Longitude,
                            new ODataPropertyDefaultImpl(Constants.Longitude, BigDecimal.valueOf(UtilConstants.longitude)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }

        try {
            OfflineManager.updateDistributorLatLong(retilerEntity);
        } catch (OfflineODataStoreException e) {
            e.printStackTrace();
        }
    }

    private void closeProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void alertMSGLatAndLong(String mAlertMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
        builder.setMessage(mAlertMsg).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onPermissionLatLong();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }

    /*
     * TODO This method make a MAil.
     */
    private void onMail() {
        try {
            if (!mStrEmailID.equalsIgnoreCase("")) {
                Intent email = new Intent(Intent.ACTION_SEND);
                String[] emailList = {mStrEmailID};
                email.putExtra(Intent.EXTRA_EMAIL, emailList);
                email.setType(Constants.plain_text);
                startActivity(Intent
                        .createChooser(email, Constants.send_email));
            } else {
                Constants.customAlertMessage(this, getString(R.string.alert_mail_id_not_maintend));
            }

        } catch (Exception e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }

    /*
     * TODO This method to Set Lant long Update.
     */
    private void setLatLongVal() {
        String mStrAlertMsg = getString(R.string.update_lat_long_retailer);
        alertMSGLatAndLong(mStrAlertMsg);
    }
}
