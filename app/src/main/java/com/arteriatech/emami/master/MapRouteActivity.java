package com.arteriatech.emami.master;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;

import com.arteriatech.mutils.common.OfflineODataStoreException;
import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.emami.common.Constants;
import com.arteriatech.emami.mbo.CustomerBean;
import com.arteriatech.emami.msecsales.R;
import com.arteriatech.emami.store.OfflineManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MapRouteActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap mymap;
    PolylineOptions rectOptions;
    ArrayList<LatLng> routeList = new ArrayList<>();
    LatLng startLatLng = null;
    String starLatLong = "";
    String enLatLong = "";
    LatLng endLatLng = null;
    LatLng engLatLong = null;
    LatLng dest = null;
    GoogleApiClient googleApiClient;
    ArrayList<CustomerBean> listRetailers;
    ArrayList<CustomerBean> listRetailersTemp = new ArrayList<>();
    ArrayList<CustomerBean> listDistributors = new ArrayList<>();
    private String mStrComingFrom = "";
    private String mStrOtherRouteguid = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_route);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mStrComingFrom = extras.getString(Constants.NAVFROM);
            mStrOtherRouteguid = extras.getString(Constants.OtherRouteGUID);
        }

        if (!Constants.restartApp(MapRouteActivity.this)) {
            initializeMap();
        }

    }

    private void initializeMap() {
        rectOptions = new PolylineOptions();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLatLongValues();
        showProgressDiag();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mymap = googleMap;

        for (int k = 0; k < listRetailers.size(); k++) {
            CustomerBean customerBean = listRetailers.get(k);
            routeList.add(new LatLng(customerBean.getLatVal(), customerBean.getLongVal()));
        }

        if (googleMap != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                    googleMap.setMyLocationEnabled(true);
                }
            } else {
                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);
            }

            int totalSize = routeList.size();
            int routeReq = 0;
            for (int i = 0; i <= totalSize; i++) {
                if (i != totalSize) {

                    int addMarkerValue = i + 1;

                    String markerValue = String.valueOf(addMarkerValue);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(routeList.get(i).latitude, routeList.get(i).longitude));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(routeList.get(i).latitude, routeList.get(i).longitude)));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    CustomerBean retailerBean;
                    try {
                        retailerBean = listRetailers.get(i);
                        String mStrVisitStartEndQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq datetime'" + UtilConstants.getNewDate() + "' " +
                                "and CPGUID eq '" + retailerBean.getCpGuidStringFormat().toUpperCase() + "' ";

                        String mStrVisitStartedQry = Constants.Visits + "?$filter=StartDate eq datetime'" + UtilConstants.getNewDate() + "' and EndDate eq null " +
                                "and CPGUID eq '" + retailerBean.getCpGuidStringFormat().toUpperCase() + "' ";

                        if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartedQry)) {
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.ic_mark_yellow, markerValue)));
                        } else if (OfflineManager.getVisitStatusForCustomer(mStrVisitStartEndQry)) {
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.ic_mark_green_new, markerValue)));
                        } else {
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.ic_mark, markerValue)));
                        }
                    } catch (OfflineODataStoreException e) {
                        e.printStackTrace();
                    }
//                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.ic_mark, markerValue)));
                    googleMap.addMarker(markerOptions);
                    starLatLong = routeList.get(i).latitude + "," + routeList.get(i).longitude;
                    startLatLng = new LatLng(routeList.get(i).latitude, routeList.get(i).longitude);
                    rectOptions.add(startLatLng);


                }

                try {
//                    if (totalSize == i) {
//                        enLatLong = samplepoints.get(0).latitude + "," + samplepoints.get(0).longitude;
//                        endLatLng = new LatLng(samplepoints.get(0).latitude, samplepoints.get(0).longitude);
//                    } else {
                    enLatLong = routeList.get(i + 1).latitude + "," + routeList.get(i + 1).longitude;
                    endLatLng = new LatLng(routeList.get(i + 1).latitude, routeList.get(i + 1).longitude);
                    // }
                } catch (Exception ex) {

                }

                String urlToPass = urlBuild(starLatLong, enLatLong);
                new DirectionAsyncTask(googleMap, MapRouteActivity.this, urlToPass, startLatLng, endLatLng, progressDialog, routeReq, totalSize).execute();
                routeReq++;
            }


        }

    }

    private void showProgressDiag() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching route, Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private String urlBuild(String origin, String destination) {

        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(origin);
        urlString.append("&destination=");// to
        urlString.append(destination);
        urlString.append("&key=AIzaSyDn0UXnJVGOZjwKJeI8uMAC4rOoluRs050");
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();

    }

    private Bitmap writeTextOnDrawable(int drawableId, String mapMarkerValue) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Gill Sans Ultra Bold", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(MapRouteActivity.this, 11));

        Rect textRect = new Rect();
        paint.getTextBounds(mapMarkerValue, 0, mapMarkerValue.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if (textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(MapRouteActivity.this, 10));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 3;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 3) - ((paint.descent() + paint.ascent()) / 3));

        canvas.drawText(mapMarkerValue, xPos, yPos, paint);

        return bm;
    }

    public static int convertToPixels(MapRouteActivity mapActivity, int i) {
        final float conversionScale = mapActivity.getResources().getDisplayMetrics().density;

        return (int) ((i * conversionScale) + 0.5f);
    }

    private void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(MapRouteActivity.this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
                CustomerBean customerBean = OfflineManager.getDistributorListData(distLatLongQry);
                if (customerBean != null) {
                    if (customerBean.getLatVal() != 0.00 && customerBean.getLongVal() != 0.00) {
                        listRetailersTemp.add(customerBean);
                    }
                }
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
                if (listRetailersTemp != null && listRetailersTemp.size() > 0) {
                    listRetailers.addAll(listRetailersTemp);
                }
                Collections.sort(listRetailers, new Comparator<CustomerBean>() {
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
                });
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
