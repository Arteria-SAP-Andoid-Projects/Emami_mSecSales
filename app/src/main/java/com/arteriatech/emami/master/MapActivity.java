package com.arteriatech.emami.master;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapActivity extends Activity {

    public static final int REQ_PERMISSION = 1;
    // Google Map
    GoogleMap googleMap;
    ArrayList<LatLng> points;
    ArrayList<CustomerBean> listRetailers;
    ArrayList<CustomerBean> listRetailersTemp = new ArrayList<>();
    ArrayList<CustomerBean> listDistributors = new ArrayList<>();
    private String mStrComingFrom = "";
    private String mStrOtherRouteguid = "";
    private CustomerBean customerBean;

    public static int convertToPixels(MapActivity mapActivity, int i) {
        final float conversionScale = mapActivity.getResources().getDisplayMetrics().density;

        return (int) ((i * conversionScale) + 0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mStrComingFrom = extras.getString(Constants.NAVFROM);
            mStrOtherRouteguid = extras.getString(Constants.OtherRouteGUID);
        }
        if (!Constants.restartApp(MapActivity.this)) {
            initializeMap();
        }


    }

    private void initializeMap() {
        if (googleMap == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.map)).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMapLocal) {
                        googleMap = googleMapLocal;
                        getLatLongValues();
                        displayMap();
                    }
                });
            }
        }
    }

    /*
            Initialize map
                */
    private void displayMap() {
        points = new ArrayList<>();
        try {
            // Changing map type
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Showing / hiding your current location
            //check permission for location enabled
            if (checkLocationPermission()) {
                googleMap.setMyLocationEnabled(true);
            } else {
                askLocationPermission();
            }

            // Enable / Disable zooming controls
            googleMap.getUiSettings().setZoomControlsEnabled(false);

            // Enable / Disable my location button
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            googleMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            googleMap.getUiSettings().setZoomGesturesEnabled(true);

            CustomerBean retailerBean;
            int addMarkerValue = 0;
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            if (listRetailers != null && listRetailers.size() > 0) {
                for (int k = 0; k < listRetailers.size(); k++) {
                    String markerValue = "";
                    retailerBean = listRetailers.get(k);

                    if (retailerBean.isDealer()) {
                        markerValue = "Start";
                    } else {
                        addMarkerValue++;
                        markerValue = String.valueOf(addMarkerValue);
                    }

                    System.out.println("Marker Value:" + markerValue);


                    try {
                        String mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "' " +
                                "and CPGUID eq '" + retailerBean.getCpGuidStringFormat().toUpperCase() + "' ";

                        String mStrVisitStartedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq null " +
                                "and CPGUID eq '" + retailerBean.getCpGuidStringFormat().toUpperCase() + "' ";

//                    String mStrVisitStartedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "'  " +
//                            "and CPGUID eq '" + retailerBean.getCpGuidStringFormat().toUpperCase() + "'";

                        String mStrLatVal = Constants.addZerosAfterDecimal(retailerBean.getLatVal() + "", 12);
                        String mStrLongVal = Constants.addZerosAfterDecimal(retailerBean.getLongVal() + "", 12);
                        if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)) {
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(retailerBean.getLatVal(), retailerBean.getLongVal()))
                                    .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.ic_mark_yellow, markerValue))).
                                            title("" + retailerBean.getRetailerName()).snippet("" + retailerBean.getMobileNumber()
                                            + "\n " + mStrLatVal + "\n " + mStrLongVal));
                        } else if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(retailerBean.getLatVal(), retailerBean.getLongVal()))
                                    .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.ic_mark_green_new, markerValue))).
                                            title("" + retailerBean.getRetailerName()).snippet("" + retailerBean.getMobileNumber()
                                            + "\n " + mStrLatVal + "\n " + mStrLongVal));
                        } else {
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(retailerBean.getLatVal(), retailerBean.getLongVal())).
                                    icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.ic_mark_red, markerValue))).title("" + retailerBean.getRetailerName())
                                    .snippet("" + retailerBean.getMobileNumber()
                                            + "\n " + mStrLatVal + "\n " + mStrLongVal));
                        }
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }


                    options.add(new LatLng(retailerBean.getLatVal(), retailerBean.getLongVal()));
                }
                googleMap.addPolyline(options);

                if (listRetailers != null && listRetailers.size() > 0) {
                    CustomerBean customerBean = listRetailers.get(0);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(customerBean.getLatVal(), customerBean.getLongVal())).zoom(18).build();

                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }

               /* retailerBean = listRetailers.get(listRetailers.size() / 2);
               if (retailerBean.getLatVal() == 0 && retailerBean.getLongVal() == 0) {
                    for (int k = 0; k < listRetailers.size(); k++) {
                        retailerBean = listRetailers.get(k);


                        if (retailerBean.getLatVal() != 0 && retailerBean.getLongVal() != 0) {

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(retailerBean.getLatVal(), retailerBean.getLongVal())).zoom(12).build();

                            googleMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));
                            break;
                        }

                        if (k == listRetailers.size() - 1) {
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(28.753189,
                                            77.056377)).zoom(8).build();

                            googleMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));
                        }


                    }
                } else {

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(retailerBean.getLatVal(), retailerBean.getLongVal())).zoom(12).build();

                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }*/
            } else {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(28.753189,
                                77.056377)).zoom(8).build();

                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    /*
           If location is not enabled this method ask permission to enabled
     */
    private void askLocationPermission() {
        ActivityCompat.requestPermissions(
                MapActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQ_PERMISSION
        );
    }

    /*
    When user requests the permission to enable the Location
     */

    /*
    To check location permission
     */
    private boolean checkLocationPermission() {

        return (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    if (checkLocationPermission())
                        googleMap.setMyLocationEnabled(true);

                } else {
                    // Permission denied
                    Toast.makeText(MapActivity.this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private Bitmap writeTextOnDrawable(int drawableId, String mapMarkerValue) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeResource(getResources(), drawableId)
                    .copy(Bitmap.Config.ARGB_8888, true);

            Typeface tf = Typeface.create("Gill Sans Ultra Bold", Typeface.BOLD);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            paint.setTypeface(tf);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(convertToPixels(MapActivity.this, 11));

            Rect textRect = new Rect();
            paint.getTextBounds(mapMarkerValue, 0, mapMarkerValue.length(), textRect);
            Canvas canvas = new Canvas(bitmap);

            //If the text is bigger than the canvas , reduce the font size
            if (textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
                paint.setTextSize(convertToPixels(MapActivity.this, 10));        //Scaling needs to be used for different dpi's

            //Calculate the positions
            int xPos = (canvas.getWidth() / 2) - 3;     //-2 is for regulating the x position offset

            //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
            int yPos = (int) ((canvas.getHeight() / 3) - ((paint.descent() + paint.ascent()) / 3));

            canvas.drawText(mapMarkerValue, xPos, yPos, paint);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;


    }

    /*
            Get Latitude and longitude values from Channel partner tables based on today route.
        */
    private void getLatLongValues() {
        if (mStrComingFrom.equalsIgnoreCase(Constants.BeatPlan)) {

            String distRouteQry = Constants.RouteSchedules + "?$filter=" + Constants.RouteSchGUID + " eq guid'" + mStrOtherRouteguid.toUpperCase() + "'and " + Constants.StatusID + " eq '01'";
            try {
                String distID = OfflineManager.getDistributorID(distRouteQry);
                String distLatLongQry = Constants.Customers + "?$filter=" + Constants.CustomerNo + " eq '" + distID + "'";
                customerBean = OfflineManager.getDistributorListData(distLatLongQry);

            } catch (OfflineODataStoreException e) {
                e.printStackTrace();
            }

            if (mStrOtherRouteguid.equalsIgnoreCase("")) {
                String routeQry = Constants.RoutePlans + "?$filter=" + Constants.VisitDate + " eq datetime'" + UtilConstants.getNewDate() + "'";
                listRetailers = Constants.getTodaysBeatRetailers();
            } else {
                String qryForTodaysBeat = Constants.RouteSchedulePlans + "?$filter=" + Constants.RouteSchGUID + " eq guid'" + mStrOtherRouteguid.toUpperCase() + "' &$orderby=" + Constants.SequenceNo + "";

                try {
                    listRetailers = OfflineManager.getRetailerListForOtherBeats(qryForTodaysBeat);
                } catch (OfflineODataStoreException e) {
                    e.printStackTrace();
                }
            }


            if (listRetailers != null && listRetailers.size() > 0) {
                for (CustomerBean customerBean : listRetailers) {
                    if (customerBean.getLatVal() != 0.00 && customerBean.getLongVal() != 0.00) {
                        listRetailersTemp.add(customerBean);
                    }
                }
                listRetailers.clear();
                if (customerBean != null) {
                    if (customerBean.getLatVal() != 0.00 && customerBean.getLongVal() != 0.00) {
                        listRetailers.add(customerBean);
                    }
                }
                if (listRetailersTemp != null && listRetailersTemp.size() > 0) {
                    listRetailers.addAll(listRetailersTemp);
                }
                /*Collections.sort(listRetailers, new Comparator<CustomerBean>() {
                    public int compare(CustomerBean one, CustomerBean other) {
                        BigInteger i1 = null;
                        BigInteger i2 = null;

                        try {
                            i1 = BigInteger.valueOf(Long.parseLong(one.getSeqNo()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            i2 = BigInteger.valueOf(Long.parseLong(one.getSeqNo()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (i1 != null && i2 != null) {
                            return i1.compareTo(i2);
                        } else {
                            return one.getSeqNo().compareTo(other.getSeqNo());
                        }
                    }
                });*/
            }

        } else {
            try {

                listRetailers = OfflineManager.getRetailerLatLongValues(Constants.ChannelPartners + "?$filter=(" + Constants.CPNo + " ne '' and " + Constants.CPNo + " ne null)" +
                        " &$orderby=" + Constants.RetailerName + "%20asc");

            } catch (OfflineODataStoreException e) {
                LogManager.writeLogError(Constants.error_txt + e.getMessage());
            }

            if (listRetailers != null && listRetailers.size() > 0) {
                for (CustomerBean customerBean : listRetailers) {
                    if (customerBean.getLatVal() != 0.00 && customerBean.getLongVal() != 0.00) {
                        listRetailersTemp.add(customerBean);
                    }
                }
                listRetailers.clear();
                if (listRetailersTemp != null && listRetailersTemp.size() > 0) {
                    listRetailers.addAll(listRetailersTemp);
                }
            }
        }
    }
}
