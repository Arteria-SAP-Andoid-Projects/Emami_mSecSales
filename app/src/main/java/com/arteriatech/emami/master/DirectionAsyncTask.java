package com.arteriatech.emami.master;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e10763 on 10-07-2018.
 */

class DirectionAsyncTask extends AsyncTask<Void, Void, String> {

    private ProgressDialog progressDialog;
    private Context context;
    String url;
    Polyline line;
    GoogleMap googleMap;
    LatLng startPoint;
    LatLng endPoint;
    int routeReq;
    int totalRouteList;

    public DirectionAsyncTask(GoogleMap myMap, MapRouteActivity routeActivity,
                              String urlToPass, LatLng startLatLng,
                              LatLng endLatLng, ProgressDialog progressDialog, int routeReq, int totalRouteList) {
        url = urlToPass;
        context = routeActivity;
        googleMap = myMap;
        startPoint = startLatLng;
        endPoint = endLatLng;
        this.routeReq = routeReq;
        this.progressDialog = progressDialog;
        this.totalRouteList = totalRouteList;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        ParserClass jParser = new ParserClass();
        String json = jParser.getJSONFromUrl(url);
        return json;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (routeReq == totalRouteList) {
            if (progressDialog != null)
                progressDialog.dismiss();
        }
        if (result != null) {
            drawPath(result);
        }
    }

    private void drawPath(String result) {

        if (line != null) {
            googleMap.clear();
        }
      /*  moveToCurrentLocation(startPoint);
        googleMap.addMarker(new MarkerOptions().position(endPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.ic_mark)));
        googleMap.addMarker(new MarkerOptions().position(startPoint).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.ic_mark_green)));*/
        try {
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            // JSONArray distanceArray = json.getJSONArray("legs");

            for (int z = 0; z < list.size() - 1; z++) {

                LatLng src = list.get(z);
                LatLng dest = list.get(z + 1);
                line = googleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude, dest.longitude))
                        .width(5).color(Color.BLUE).geodesic(true));


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private List<LatLng> decodePoly(String encoded) {


        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}
