package com.android.jakchang.foodtruck;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.jakchang.foodtruck.DAO.LoginRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SelectedMenuActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    SupportMapFragment mapFragment;

    private double mylat,mylng;
    private String kind = "";
    private LatLng mylocation,mylocation2;
    Intent intent;

    private String ULR = "http://ckdrb7067.cafe24.com/menu2.php";
    private static String TAG = "phpquerytest";
    private static final String TAG_JSON="response";
    String mJsonString;
    ArrayList<ListItem> listItems;
    ListItem listItem;
    String markerLat,markerLng,markerTitle,markerUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        intent = getIntent();
        kind = intent.getStringExtra("kind");

        listItems = new ArrayList<>();
        GetData task = new GetData();
        task.execute(kind);
        mylat = Double.parseDouble(intent.getStringExtra("latitude"));
        mylng = Double.parseDouble(intent.getStringExtra("longitude"));
        mylocation = new LatLng(mylat,mylng);



    }//onCreate

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);

        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);


        int permissionLocation = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }
        mMap.setMyLocationEnabled(true);

        mMap.addMarker(new MarkerOptions().position(mylocation).title("mylocation"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(googleMap.getCameraPosition().target)
                        .zoom(17).build()));


        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.getPosition().latitude!=mylat&&marker.getPosition().longitude!=mylng) {
                    Intent intent = new Intent(getApplicationContext(), SeeSellerInfo.class);
                    markerLat = marker.getPosition().latitude + "";
                    markerLng = marker.getPosition().longitude + "";
                    markerTitle = marker.getTitle();
                    markerUserId = "";

                    for (ListItem item : listItems) {
                        if (item.getLat().equals(markerLat) && item.getLng().equals(markerLng) && item.getName().equals(markerTitle)) {
                            markerUserId = item.getUser_id();
                        }
                    }
                    //Toast.makeText(getContext(), markerUserId, Toast.LENGTH_LONG).show();
                    intent.putExtra("user_id", markerUserId);
                    startActivity(intent);

                }
            }
        });

    }


    private class GetData extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null){
            }
            else {
                mJsonString = result;
                Log.d(TAG, "mJsonString - " + mJsonString);
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String searchKeyword = params[0];
            String serverURL = "http://ckdrb7067.cafe24.com/menu2.php";
            String postParameters ="kind="+searchKeyword;
            try {
                URL url = new URL(serverURL);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);
                InputStream inputStream;

                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;

            }

        }

    }//GetData

    private void showResult(){

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);

                String user_id = item.getString("user_id");
                String title = item.getString("title");
                String kind = item.getString("kind");
                String lat = item.getString("latitude");
                String lng = item.getString("longitude");
                //Bitmap bitmap =  new ImageLoader().getBitmapImg(user_id+"-mainimage.jpg");
                listItem = new ListItem(null, user_id, title, kind, distanceByDegree(mylat,mylng, Double.parseDouble(lat), Double.parseDouble(lng)), lat, lng);
                listItems.add(listItem);
                LatLng location = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                mMap.addMarker(new MarkerOptions().position(location).title(title).snippet("종류 : "+kind+"\n"+"거리 :"+distanceByDegree(mylat,mylng, Double.parseDouble(lat), Double.parseDouble(lng))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));


            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }//showResult

    public String distanceByDegree(double my_lat, double my_lng, double lat2, double lng2){
        Location startPos = new Location("PointA");
        Location endPos = new Location("PointB");
        String dis="";
        startPos.setLatitude(my_lat);
        startPos.setLongitude(my_lng);
        endPos.setLatitude(lat2);
        endPos.setLongitude(lng2);

        double distance = startPos.distanceTo(endPos);
        if(distance>=1000){

            int div = (int)distance/1000;
            int rest = (int)distance%1000;
            //Toast.makeText(getContext(),"div : "+div+"rest : "+rest,Toast.LENGTH_LONG).show();
            dis = div+"."+rest+"km";
        }else{
            dis = Integer.toString((int)distance)+"m";
        }
        return dis;

    }//DistanceByDegree


}
