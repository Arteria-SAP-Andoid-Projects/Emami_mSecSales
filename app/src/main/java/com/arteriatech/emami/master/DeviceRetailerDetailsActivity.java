package com.arteriatech.emami.master;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.ODataPropMap;
import com.sap.smp.client.odata.ODataProperty;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

/**
 * Created by e10526 on 14-07-2016.
 */
public class DeviceRetailerDetailsActivity extends AppCompatActivity {
    private String dateOfBirth = "";

    private String[][] arrayRetProfileVal;
    private String selRetTypeDesc = "", selRetProfileCode = "", selRetProfileDesc = "", selStateDesc = "",
            selDistributorDesc = "", selCityDesc = "", selDistrictDesc = "";

    private String selAddress = "";
    private String selLandMark = "";
    private String selPinCode = "";
    private String selMobileNo = "";
    private String selEmailID = "";
    private String selPan = "";
    private String selVatNo = "";
    private String selOutletName = "";
    private String selOwnerName = "";

    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "",
            mStrEtag = "", mStrResourcePath = "";

    private ODataPropMap oDataProperties;
    private ODataProperty oDataProperty;
    BigDecimal mDecimalLatitude, mDecimalLongitude;
    double mDoubleLatitude = 0.0, mDoubleLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.title_retailer_details));

        setContentView(R.layout.activity_device_retailer_details);

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrEtag = bundleExtras.getString(Constants.Etag);
            mStrResourcePath = bundleExtras.getString(Constants.ResourcePath);
        }

        if (!Constants.restartApp(DeviceRetailerDetailsActivity.this)) {
            initUI();
        }
    }

    /*Initializes UI*/
    void initUI() {

        TextView tv_retailer_code = (TextView) findViewById(R.id.tv_retailer_code);

        getRetailerDetails();
        getRetailerProfile();

        tv_retailer_code.setText(selDistributorDesc);

        if (arrayRetProfileVal == null) {
            arrayRetProfileVal = new String[2][1];
            arrayRetProfileVal[0][0] = "";
            arrayRetProfileVal[1][0] = "";
        }

        for (int i = 0; i < arrayRetProfileVal[1].length; i++) {
            if (selRetProfileCode.equalsIgnoreCase(arrayRetProfileVal[0][i])) {
                selRetProfileDesc = arrayRetProfileVal[1][i];
                break;
            }
        }

        TextView tv_date_of_birth = (TextView) findViewById(R.id.tv_date_of_birth);
        tv_date_of_birth.setText(dateOfBirth);
        TextView tv_retailer_type = (TextView) findViewById(R.id.tv_retailer_type);
        tv_retailer_type.setText(selRetTypeDesc);
        TextView tv_retailer_profile = (TextView) findViewById(R.id.tv_retailer_profile);
        tv_retailer_profile.setText(selRetProfileDesc);
        TextView tv_state = (TextView) findViewById(R.id.tv_state);
        tv_state.setText(selStateDesc);
        TextView tv_outlet_name = (TextView) findViewById(R.id.tv_outlet_name);
        tv_outlet_name.setText(selOutletName);
        TextView tv_owner_name = (TextView) findViewById(R.id.tv_owner_name);
        tv_owner_name.setText(selOwnerName);
        TextView tv_address = (TextView) findViewById(R.id.tv_address);
        tv_address.setText(selAddress);
        TextView tv_district = (TextView) findViewById(R.id.tv_district);
        tv_district.setText(selDistrictDesc);
        TextView tv_city = (TextView) findViewById(R.id.tv_city);
        tv_city.setText(selCityDesc);
        TextView tv_land_mark = (TextView) findViewById(R.id.tv_land_mark);
        tv_land_mark.setText(selLandMark);
        TextView tv_pin_code = (TextView) findViewById(R.id.tv_pin_code);
        tv_pin_code.setText(selPinCode);
        TextView tv_mobile_number = (TextView) findViewById(R.id.tv_mobile_number);
        tv_mobile_number.setText(selMobileNo);
        TextView tv_email_id = (TextView) findViewById(R.id.tv_email_id);
        tv_email_id.setText(selEmailID);
        TextView tv_pan_no = (TextView) findViewById(R.id.tv_pan_no);
        tv_pan_no.setText(selPan);
        TextView tv_vat_no = (TextView) findViewById(R.id.tv_vat_no);
        tv_vat_no.setText(selVatNo);
        TextView tv_latitude = (TextView) findViewById(R.id.tv_latitude);
        TextView tv_longitude = (TextView) findViewById(R.id.tv_longitude);
        tv_latitude.setText(String.valueOf(mDoubleLatitude));
        tv_longitude.setText(String.valueOf(mDoubleLongitude));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    /*Gets detail of retailer*/
    private void getRetailerDetails() {
        String retDetgry = Constants.ChannelPartners + "(" + mStrBundleCPGUID + ") ";

        try {
            ODataEntity retilerEntity = OfflineManager.getRetDetails(retDetgry);

            oDataProperties = retilerEntity.getProperties();

            oDataProperty = oDataProperties.get(Constants.CPTypeDesc);
            selRetTypeDesc = (String) oDataProperty.getValue();

            oDataProperty = oDataProperties.get(Constants.OutletName);

            selOutletName = (String) oDataProperty.getValue();

            oDataProperty = oDataProperties.get(Constants.OwnerName);

            selOwnerName = (String) oDataProperty.getValue();

            oDataProperty = oDataProperties.get(Constants.RetailerProfile);

            selRetProfileCode = (String) oDataProperty.getValue();

            oDataProperty = oDataProperties.get(Constants.Address1);

            selAddress = (String) oDataProperty.getValue();

            oDataProperty = oDataProperties.get(Constants.Landmark);

            selLandMark = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";


            oDataProperty = oDataProperties.get(Constants.PostalCode);

            selPinCode = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.DistrictDesc);

            selDistrictDesc = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.CityDesc);

            selCityDesc = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.Latitude);
            mDecimalLatitude = (BigDecimal) oDataProperty.getValue();  //---------> Decimal property

            if (mDecimalLatitude != null) {
                mDoubleLatitude = mDecimalLatitude.doubleValue();
            } else {
                mDoubleLatitude = 0.0;
            }

            oDataProperty = oDataProperties.get(Constants.Longitude);
            mDecimalLongitude = (BigDecimal) oDataProperty.getValue();  //---------> Decimal property

            if (mDecimalLongitude != null) {
                mDoubleLongitude = mDecimalLongitude.doubleValue();
            } else {
                mDoubleLongitude = 0.0;
            }

            oDataProperty = oDataProperties.get(Constants.MobileNo);

            selMobileNo = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.EmailID);
            selEmailID = (String) oDataProperty.getValue() != null ? (String) oDataProperty.getValue() : "";

            oDataProperty = oDataProperties.get(Constants.DOB);
            dateOfBirth = UtilConstants.convertCalenderToStringFormat((GregorianCalendar) oDataProperty.getValue());

            oDataProperty = oDataProperties.get(Constants.PAN);
            selPan = (String) oDataProperty.getValue();

            oDataProperty = oDataProperties.get(Constants.VATNo);
            selVatNo = (String) oDataProperty.getValue();

            oDataProperty = oDataProperties.get(Constants.StateDesc);
            selStateDesc = (String) oDataProperty.getValue();

        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

    /*Gets profile types for retailer from value helps*/
    private void getRetailerProfile() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq '" + Constants.RetailerProfile + "'";
            arrayRetProfileVal = OfflineManager.getConfigList(mStrConfigQry);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.strErrorWithColon + e.getMessage());
        }
    }

}
