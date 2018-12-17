package com.android.jakchang.foodtruck;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

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

public class BlankFragment1 extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private GoogleMap mGoogleMap;

    MapView mMapView;
    View mView;
    double mylat=37.545983, mylng=126.989656;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    private String ULR = "http://ckdrb7067.cafe24.com/menu1.php";
    String mJsonString = "";
    ArrayList<ListItem> listItems;
    ListItem listItem;
    private static String TAG = "phpquerytest";
    private static final String TAG_JSON="response";

    String markerLat,markerLng,markerTitle,markerUserId;

    public BlankFragment1() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public BlankFragment1(double lat, double lng) {
        // Required empty public constructor
        this.mylat = lat;
        this.mylng = lng;


    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listItems = new ArrayList<>();
        GetData task = new GetData();
        task.execute("");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_fragment01, container, false);


        return mView;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);

        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        for(ListItem item : listItems) {
           LatLng location = new LatLng(Double.parseDouble(item.getLat()),Double.parseDouble(item.getLng()));
            mGoogleMap.addMarker(new MarkerOptions().position(location).title(item.getName()).snippet("종류 : "+item.getKind()+"\n"+"거리 :"+item.getDistance()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        }

        LatLng myLocation = new LatLng(mylat, mylng);
        mGoogleMap.addMarker(new MarkerOptions().position(myLocation).title("MyLocation"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);

        int permissionLocation = ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions((Activity)getContext(),
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }
        mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this.getContext()));
        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.getPosition().latitude!=mylat&&marker.getPosition().longitude!=mylng) {
                    Intent intent = new Intent(getContext(), SeeSellerInfo.class);
                    markerLat = marker.getPosition().latitude + "";
                    markerLng = marker.getPosition().longitude + "";
                    markerTitle = marker.getTitle();
                    markerUserId = "";

                    for (ListItem item : listItems) {
                        if (item.getLat().equals(markerLat) && item.getLng().equals(markerLng) && item.getName().equals(markerTitle)) {
                            markerUserId = item.getUser_id();
                        }
                    }

                    intent.putExtra("user_id", markerUserId);
                    startActivity(intent);

                }
            }
        });

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

    @Override
    public void onLocationChanged(Location location) {

    }


    public String distanceByDegree(double my_lat, double my_lng, double lat2, double lng2){
        String dis="";
        Location startPos = new Location("PointA");
        Location endPos = new Location("PointB");

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
            String serverURL = "http://ckdrb7067.cafe24.com/menu1.php";
            String postParameters = "";
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
                //Toast.makeText(getApplicationContext(), "id : "+user_id+"lat : "+lat+"lng : "+lng, Toast.LENGTH_SHORT).show();
                //Bitmap bitmap =  new ImageLoader().getBitmapImg(user_id+"-mainimage.jpg");
                ListItem listItem = new ListItem(null,user_id,title,kind,distanceByDegree(mylat, mylng, Double.parseDouble(lat), Double.parseDouble(lng)),lat,lng);
                listItems.add(listItem);

            }

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }//showResult



}
