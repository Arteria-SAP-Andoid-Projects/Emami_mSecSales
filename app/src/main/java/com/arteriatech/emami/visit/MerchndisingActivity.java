package com.arteriatech.emami.visit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.Operation;
import com.arteriatech.mutils.common.UIListener;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.location.LocationInterface;
import com.arteriatech.mutils.location.LocationModel;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.ActionBarView;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.interfaces.DialogCallBack;
import com.arteriatech.emami.master.RetailersDetailsActivity;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.sap.smp.client.odata.ODataDuration;
import com.sap.smp.client.odata.exception.ODataException;
import com.sap.smp.client.odata.impl.ODataDurationDefaultImpl;
import com.sap.xscript.core.GUID;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import id.zelory.compressor.Compressor;

/**
 * Created by ${e10526} on ${17-12-2016}.
 */
@SuppressLint("NewApi")
public class MerchndisingActivity extends AppCompatActivity implements View.OnClickListener, UIListener {
    private EditText editRemraks;
    private ImageView ivThumbnailPhoto;
    private static final int TAKE_PICTURE = Constants.TAKE_PICTURE;
    private boolean mBooleanPictureTaken = false;
    private String[][] arrMerchType = null;
    private String mStrBundleRetID = "";
    private String mStrBundleRetName = "", mStrBundleCPGUID = "";
    private String mStrEncodedFont = "";
    private String mStrSelMerchndisingType = "", mStrMerchReviewTypeDesc = "", mStrRemarksMandatoryFlag = "";
    String mStrComingFrom = "";
    private int mLongBitmapSize = 0;
    private String defaultCameraPackage = "";
    private String mStrBundleRetailerUID = "", mStrBundleCPGUID32 = "";
    Spinner spinnerSnapType;
    Button btnClickAction;
    TextView tvRetName = null, tvUID = null, tv_remarks_mandatory = null;

    private boolean mBoolHeaderPosted = false;
    Hashtable tableItm;
    Hashtable tableHdr;
    private String selectedImagePath, filename = "";
    String strMimeType = null;
    String mimeType = null;
    private String[][] mArrayDistributors = null, mArraySPValues = null;
    private String mStrVisitActRefID = "";
    private ProgressDialog pdLoadDialog = null;
    File op = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
        ActionBarView.initActionBarView(this, true, getString(R.string.lbl_merchndising));

        setContentView(R.layout.activity_merchndising_snap);
        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            mStrBundleRetID = bundleExtras.getString(Constants.CPNo);
            mStrBundleRetName = bundleExtras.getString(Constants.RetailerName);
            mStrBundleCPGUID = bundleExtras.getString(Constants.CPGUID);
            mStrBundleCPGUID32 = bundleExtras.getString(Constants.CPGUID32);
            mStrBundleRetailerUID = bundleExtras.getString(Constants.CPUID);
            mStrComingFrom = bundleExtras.getString(Constants.comingFrom);
        }
        if (!Constants.restartApp(MerchndisingActivity.this)) {
            initUI();
            getSalesPersonValues();
            getMerchandisingTypes();
            setViuesIntoUI();
            getDistributorValues();
        }
    }

    /**
     * get salesPerson values
     */
    private void getSalesPersonValues() {
        mArraySPValues = Constants.getSPValesFromCPDMSDivisionByCPGUIDAndDMSDivision(mStrBundleCPGUID);
    }

    // TODO Initialize UI
    private void initUI() {
        tv_remarks_mandatory = (TextView) findViewById(R.id.tv_remarks_mandatory);
        tvRetName = (TextView) findViewById(R.id.tv_reatiler_name);
        tvUID = (TextView) findViewById(R.id.tv_reatiler_id);
        spinnerSnapType = (Spinner) findViewById(R.id.sp_snap_type);
        editRemraks = (EditText) findViewById(R.id.edit_remarks);
        ivThumbnailPhoto = (ImageView) findViewById(R.id.ivThumbnailPhoto);
        btnClickAction = (Button) findViewById(R.id.btn_take_pic);
        Constants.mStartTimeDuration = UtilConstants.getOdataDuration();

    }

    // TODO set values to UI
    private void setViuesIntoUI() {
        tvRetName.setText(mStrBundleRetName);
        tvUID.setText(mStrBundleRetailerUID);

        editRemraks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editRemraks.setBackgroundResource(R.drawable.edittext);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ArrayAdapter<String> arrayAdepterMerchandisingTypeValues = new ArrayAdapter<>(this, R.layout.custom_textview, arrMerchType[1]);
        arrayAdepterMerchandisingTypeValues.setDropDownViewResource(R.layout.spinnerinside);
        spinnerSnapType.setAdapter(arrayAdepterMerchandisingTypeValues);
        spinnerSnapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                mStrSelMerchndisingType = arrMerchType[0][position];
                mStrMerchReviewTypeDesc = arrMerchType[1][position];
                mStrRemarksMandatoryFlag = arrMerchType[2][position];
                spinnerSnapType.setBackgroundResource(R.drawable.spinner_bg);
                if (mStrRemarksMandatoryFlag.equalsIgnoreCase(Constants.X)) {
                    tv_remarks_mandatory.setText(getString(R.string.star));
                } else {
                    editRemraks.setBackgroundResource(R.drawable.edittext);
                    tv_remarks_mandatory.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        btnClickAction.setOnClickListener(this);
    }

    // TODO get Merchandising type values from valuehelps table
    private void getMerchandisingTypes() {
        try {
            String mStrConfigQry = Constants.ValueHelps + "?$filter=" + Constants.PropName + " eq 'MerchReviewType'";
            arrMerchType = OfflineManager.getConfigListWithDefaultValAndNone(mStrConfigQry, Constants.PROP_MER_TYPE);
        } catch (OfflineODataStoreException e) {
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
        if (arrMerchType == null) {
            arrMerchType = new String[4][1];
            arrMerchType[0][0] = "";
            arrMerchType[1][0] = Constants.None;
            arrMerchType[2][0] = Constants.X;
            arrMerchType[3][0] = Constants.str_false;
        } else {
            arrMerchType = Constants.CheckForOtherInConfigValue(arrMerchType);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_take_pic:
                if (Build.VERSION_CODES.M <= android.os.Build.VERSION.SDK_INT) {
                    if (ActivityCompat.checkSelfPermission(MerchndisingActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(MerchndisingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(MerchndisingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.checkSelfPermission(MerchndisingActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(MerchndisingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MerchndisingActivity.this, Manifest.permission.CAMERA)
                                    && ActivityCompat.shouldShowRequestPermissionRationale(MerchndisingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                ActivityCompat.requestPermissions(MerchndisingActivity.this, new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.CAMERA_PERMISSION_CONSTANT);
                            } else if (Constants.getPermissionStatus(MerchndisingActivity.this, Manifest.permission.CAMERA)
                                    && Constants.getPermissionStatus(MerchndisingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                                Constants.dialogBoxWithButton(MerchndisingActivity.this, "",
                                        getString(R.string.this_app_needs_camera_storage_permission), getString(R.string.enable),
                                        getString(R.string.later), new DialogCallBack() {
                                            @Override
                                            public void clickedStatus(boolean clickedStatus) {
                                                if (clickedStatus) {
                                                    Constants.navigateToAppSettingsScreen(MerchndisingActivity.this);
                                                }
                                            }
                                        });
                            } else {
                                ActivityCompat.requestPermissions(MerchndisingActivity.this, new String[]{Manifest.permission.CAMERA,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                        Constants.CAMERA_PERMISSION_CONSTANT);
                            }
                            Constants.setPermissionStatus(MerchndisingActivity.this, Manifest.permission.CAMERA, true);
                            Constants.setPermissionStatus(MerchndisingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                        } else if (ActivityCompat.checkSelfPermission(MerchndisingActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MerchndisingActivity.this, Manifest.permission.CAMERA)) {
                                ActivityCompat.requestPermissions(MerchndisingActivity.this, new String[]{Manifest.permission.CAMERA}, Constants.CAMERA_PERMISSION_CONSTANT);
                            } else if (Constants.getPermissionStatus(MerchndisingActivity.this, Manifest.permission.CAMERA)) {

                                Constants.dialogBoxWithButton(MerchndisingActivity.this, "",
                                        getString(R.string.this_app_needs_camera_permission), getString(R.string.enable),
                                        getString(R.string.later), new DialogCallBack() {
                                            @Override
                                            public void clickedStatus(boolean clickedStatus) {
                                                if (clickedStatus) {
                                                    Constants.navigateToAppSettingsScreen(MerchndisingActivity.this);
                                                }
                                            }
                                        });
                            } else {
                                ActivityCompat.requestPermissions(MerchndisingActivity.this, new String[]{Manifest.permission.CAMERA},
                                        Constants.CAMERA_PERMISSION_CONSTANT);
                            }
                            Constants.setPermissionStatus(MerchndisingActivity.this, Manifest.permission.CAMERA, true);
                        } else if (ActivityCompat.checkSelfPermission(MerchndisingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(MerchndisingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(MerchndisingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                ActivityCompat.requestPermissions(MerchndisingActivity.this, new String[]{
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.CAMERA_PERMISSION_CONSTANT);
                            } else if (Constants.getPermissionStatus(MerchndisingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                Constants.dialogBoxWithButton(MerchndisingActivity.this, "",
                                        getString(R.string.this_app_needs_storage_permission), getString(R.string.enable),
                                        getString(R.string.later), new DialogCallBack() {
                                            @Override
                                            public void clickedStatus(boolean clickedStatus) {
                                                if (clickedStatus) {
                                                    Constants.navigateToAppSettingsScreen(MerchndisingActivity.this);
                                                }
                                            }
                                        });

                            } else {
                                ActivityCompat.requestPermissions(MerchndisingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                        Constants.CAMERA_PERMISSION_CONSTANT);
                            }
                            Constants.setPermissionStatus(MerchndisingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                        }
                    } else {
                        //You already have the permission, just go ahead.
                        callCameraIntent();
                    }
                } else {
                    callCameraIntent();
                }
                break;
            case R.id.tv_back:
                onBackPressed();
                break;
            case R.id.tv_submit:
                onValidationCheck();
                break;
            default:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
           /* case Constants.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    UtilConstants.showAlert(getString(R.string.permission_denied_u_can_access_location_data), MerchndisingActivity.this);
                    checkGPS();
                } else {
                    UtilConstants.showAlert(getString(R.string.permission_denied_u_cannot_access_location_data), MerchndisingActivity.this);
                }
                break;*/
        }
    }

    private void callCameraIntent() {
        try {
            PackageManager packageManager = getPackageManager();
            List<ApplicationInfo> list = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            for (int n = 0; n < list.size(); n++) {
                if ((list.get(n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    if (list.get(n).loadLabel(packageManager).toString().equalsIgnoreCase("Camera")) {
                        defaultCameraPackage = list.get(n).packageName;
                        break;
                    }
                }
            }

            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File out = Environment.getExternalStorageDirectory();
            filename = (System.currentTimeMillis() + ".jpg");
            out = new File(out, filename);
            op = out;

            if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
                Uri photoURI = FileProvider.getUriForFile(MerchndisingActivity.this,
                        getApplicationContext().getPackageName() + ".provider",
                        out);
                i.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            } else {
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
            }
            startActivityForResult(i, TAKE_PICTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK) {
            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals(filename)) {
                    f = temp;
                    break;
                }
            }
            try {
                final Bitmap bitMap = Compressor.getDefault(this).compressToBitmap(f);
//                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

//                final Bitmap bitMap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                assert bitMap != null;
                bitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                bitMap.compress(Bitmap.CompressFormat.JPEG, 150, stream);
                final byte[] imageInByte = stream.toByteArray();
                mLongBitmapSize = imageInByte.length;

//                yourimageview.setImageBitmap(bitmap);
                ivThumbnailPhoto.setImageBitmap(bitMap);
                ivThumbnailPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        openImagePopUp(bitMap);
                        final Dialog dialog = new Dialog(MerchndisingActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.img_expand);
                        // set the custom dialog components - text, image and
                        // button
                        ImageView image = (ImageView) dialog.findViewById(R.id.imageView1);

                        image.setImageBitmap(BitmapFactory.decodeByteArray(imageInByte, 0,
                                imageInByte.length));
                        dialog.show();
                    }
                });
                String[] projection = {MediaStore.Images.Media.DATA};
//            @SuppressWarnings("deprecation")
                Cursor cursorMediaValue = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
                cursorMediaValue.moveToLast();

                mBooleanPictureTaken = true;
//                mStrEncodedFont = BitMapToString(bitMap);

//                filename = (System.currentTimeMillis() + "");

                File fileName = Constants.SaveImageInDevice(filename, bitMap);
                selectedImagePath = fileName.getPath();
//                selectedImagePath = filename;

                //mime


                strMimeType = MimeTypeMap.getFileExtensionFromUrl(selectedImagePath);
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        strMimeType);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    /**
     * get distributor value
     */
    private void getDistributorValues() {
        mArrayDistributors = Constants.getDistributorsByCPGUID(mStrBundleCPGUID);
    }


    // TODO check validation
    private void onValidationCheck() {
        if ((mStrRemarksMandatoryFlag.equalsIgnoreCase(Constants.X) &&
                editRemraks.getText().toString().trim().equalsIgnoreCase("")) || mStrSelMerchndisingType.equalsIgnoreCase("")) {
            if (mStrSelMerchndisingType.equalsIgnoreCase("")) {
                spinnerSnapType.setBackgroundResource(R.drawable.error_spinner);
            }

            if (mStrRemarksMandatoryFlag.equalsIgnoreCase(Constants.X)) {
                if (editRemraks.getText() == null || editRemraks.getText().toString().trim().equalsIgnoreCase("")) {
                    editRemraks.setBackgroundResource(R.drawable.edittext_border);
                }
            }

            UtilConstants.showAlert(getString(R.string.validation_plz_enter_mandatory_flds), MerchndisingActivity.this);
        } else {
            if (mBooleanPictureTaken) {
                if (Constants.checkPermission(MerchndisingActivity.this)) {
                    checkGPS();
                } else {
                    requestPermission(MerchndisingActivity.this);
                }

            } else {
                UtilConstants.showAlert(getString(R.string.take_pic), MerchndisingActivity.this);

            }
        }
    }

    private void closingProgressDialog() {
        try {
            pdLoadDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkGPS() {
       /* if (Constants.onGpsCheck(MerchndisingActivity.this)) {
            if (UtilConstants.getLocation(MerchndisingActivity.this)) {
                mBoolHeaderPosted = false;
                onSaveDB();
            }
        }*/
        pdLoadDialog = Constants.showProgressDialog(MerchndisingActivity.this, "", getString(R.string.gps_progress));
        Constants.getLocation(MerchndisingActivity.this, new LocationInterface() {
            @Override
            public void location(boolean status, LocationModel locationModel, String errorMsg, int errorCode) {
                closingProgressDialog();
                if (status) {
                    mBoolHeaderPosted = false;
                    onSaveDB();
                }
            }
        });
    }

    public void requestPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(
                    activity,
                    Constants.PERMISSIONS_LOCATION, Constants.PERMISSION_REQUEST_CODE
            );
        } else if (Constants.getPermissionStatus(MerchndisingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                && Constants.getPermissionStatus(MerchndisingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Constants.dialogBoxWithButton(MerchndisingActivity.this, "",
                    getString(R.string.this_app_needs_location_permission), getString(R.string.enable),
                    getString(R.string.later), new DialogCallBack() {
                        @Override
                        public void clickedStatus(boolean clickedStatus) {
                            if (clickedStatus) {
                                Constants.navigateToAppSettingsScreen(MerchndisingActivity.this);
                            }
                        }
                    });

        } else {
            ActivityCompat.requestPermissions(MerchndisingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.PERMISSION_REQUEST_CODE);
        }

        Constants.setPermissionStatus(MerchndisingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, true);
        Constants.setPermissionStatus(MerchndisingActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_merchindising, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_save:
                onValidationCheck();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MerchndisingActivity.this, R.style.MyTheme);
        builder.setMessage(R.string.alert_exit_create_merchndising).setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onNavigateToRetDetilsActivity();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                });
        builder.show();
    }


    // TODO save values into offline store(DataBase)
    private void onSaveDB() {
//        if (Constants.isValidTime(UtilConstants.convertTimeOnly(Constants.mStartTimeDuration.toString()),
//                UtilConstants.convertTimeOnly(UtilConstants.getOdataDuration().toString())) && Constants.isValidDate()) {
       /* if (!Constants.onGpsCheck(MerchndisingActivity.this)) {
            return;
        }
        if(!UtilConstants.getLocation(MerchndisingActivity.this)){
            return;
        }*/
        try {
            GUID mStrGuide = GUID.newRandom();
            tableHdr = new Hashtable();
            Hashtable visitActivityTable = new Hashtable();
            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewGUID, mStrGuide.toString());
            //noinspection unchecked
            tableHdr.put(Constants.Remarks, editRemraks.getText().toString().trim());
            //noinspection unchecked
            tableHdr.put(Constants.CPNo, UtilConstants.removeLeadingZeros(mStrBundleRetID));
            //noinspection unchecked
            tableHdr.put(Constants.CPGUID, mStrBundleCPGUID32);

            tableHdr.put(Constants.SPGUID, mArraySPValues[4][0].toUpperCase());
            tableHdr.put(Constants.ParentID, mArrayDistributors[4][0]);
            tableHdr.put(Constants.ParentNo, mArrayDistributors[4][0]);
            tableHdr.put(Constants.ParentName, mArrayDistributors[7][0]);
            tableHdr.put(Constants.ParentTypeID, mArrayDistributors[5][0]);
            tableHdr.put(Constants.ParentTypeDesc, mArrayDistributors[6][0]);

            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewType, mStrSelMerchndisingType);


            tableHdr.put(Constants.MerchReviewTypeDesc, mStrMerchReviewTypeDesc);
            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewDate, UtilConstants.getNewDateTimeFormat());
            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewLat, BigDecimal.valueOf(UtilConstants.latitude));
            //noinspection unchecked
            tableHdr.put(Constants.MerchReviewLong, BigDecimal.valueOf(UtilConstants.longitude));

            String mRouteSchGuid = Constants.getRouteSchGUID(Constants.RouteSchedulePlans, Constants.RouteSchGUID, Constants.VisitCPGUID, mStrBundleCPGUID32, mArrayDistributors[5][0]);
            if (!mRouteSchGuid.equalsIgnoreCase("")) {
                tableHdr.put(Constants.RouteGUID, mRouteSchGuid);
            } else {
                tableHdr.put(Constants.RouteGUID, "");
            }


            tableHdr.put(Constants.CPTypeID, Constants.str_02);
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);

            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            //noinspection unchecked
            tableHdr.put(Constants.LOGINID, loginIdVal);

            final Calendar calCurrentTime = Calendar.getInstance();
            int hourOfDay = calCurrentTime.get(Calendar.HOUR_OF_DAY); // 24 hour clock
            int minute = calCurrentTime.get(Calendar.MINUTE);
            int second = calCurrentTime.get(Calendar.SECOND);
            ODataDuration oDataDuration = null;
            try {
                oDataDuration = new ODataDurationDefaultImpl();
                oDataDuration.setHours(hourOfDay);
                oDataDuration.setMinutes(minute);
                oDataDuration.setSeconds(BigDecimal.valueOf(second));
            } catch (Exception e) {
                e.printStackTrace();
            }

            tableHdr.put(Constants.MerchReviewTime, oDataDuration);

            //Todo set values to data vault
            Constants.saveDeviceDocNoToSharedPref(MerchndisingActivity.this, Constants.MerchList, mStrGuide.toString().toUpperCase());
            Constants.storeInDataVault(mStrGuide.toString().toUpperCase(), filename + "." + strMimeType);

            tableItm = new Hashtable();

            try {
                //noinspection unchecked
                tableItm.put(Constants.MerchReviewGUID, mStrGuide.toString());
                GUID mStrImgGuide = GUID.newRandom();
                //noinspection unchecked
                tableItm.put(Constants.MerchImageGUID, mStrImgGuide.toString());
                //noinspection unchecked
                tableItm.put(Constants.ImageMimeType, mimeType);
                //noinspection unchecked
                tableItm.put(Constants.ImageSize, mLongBitmapSize);
                //noinspection unchecked
                tableItm.put(Constants.Image, mStrEncodedFont);

                tableItm.put(Constants.ImagePath, selectedImagePath);
                tableItm.put(Constants.FileName, filename + "." + strMimeType);


            } catch (Exception exception) {
                exception.printStackTrace();
            }

            mStrVisitActRefID = mStrGuide.toString36().toUpperCase();


            try {
                //noinspection unchecked
                OfflineManager.createMerChndisingHeader(tableHdr, MerchndisingActivity.this);
            } catch (OfflineODataStoreException e) {
                //                    e.printStackTrace();
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        }else{
//            UtilConstants.showAlert(getString(R.string.validation_app_time_incorrect), MerchndisingActivity.this);
//        }

    }

    private void saveItemEntityToTable() {
        try {
            //noinspection unchecked
            OfflineManager.createMerChndisingItem(tableItm, tableHdr, MerchndisingActivity.this);
        } catch (OfflineODataStoreException e) {
            //                    e.printStackTrace();
            LogManager.writeLogError(Constants.error_txt + e.getMessage());
        }
    }


    @Override
    public void onRequestError(int operation, Exception e) {
        UtilConstants.showAlert(getString(R.string.error_occured_during_save), MerchndisingActivity.this);
    }

    @Override
    public void onRequestSuccess(int operation, String key) throws ODataException, OfflineODataStoreException {

        if (operation == Operation.Create.getValue() && mBoolHeaderPosted) {
            //========>Start VisitActivity
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            String loginIdVal = sharedPreferences.getString(Constants.username, "");
            Constants.onVisitActivityUpdate(mStrBundleCPGUID32, loginIdVal,
                    mStrVisitActRefID, "01", Constants.Merchendising_Snap);
            //========>End VisitActivity
            backToVisit();
        } else if (operation == Operation.Create.getValue() && !mBoolHeaderPosted) {
            mBoolHeaderPosted = true;
            saveItemEntityToTable();
        }


    }

    private void backToVisit() {
        String popUpText = getString(R.string.msg_snap_shot_created);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MerchndisingActivity.this, R.style.MyTheme);
        builder.setMessage(popUpText)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface Dialog,
                                    int id) {
                                try {
                                    Dialog.cancel();
                                    onNavigateToRetDetilsActivity();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
        builder.show();
    }

    private void onNavigateToRetDetilsActivity() {
        Constants.ComingFromCreateSenarios = Constants.X;
        Intent intentNavPrevScreen = new Intent(MerchndisingActivity.this, RetailersDetailsActivity.class);
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
}
