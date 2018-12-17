package com.android.jakchang.foodtruck;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.jakchang.foodtruck.DAO.Menu3Request;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    Button mapBtn, searchBtn, listBtn;
    BlankFragment1 blankFragment1;
    BlankFragment2 blankFragment2;
    BlankFragment3 blankFragment3;

    double lat,lng,mylat,mylng;

    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    private ArrayList<ListItem> lists;
    Intent intent;

    ArrayList<ListItem> listItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        mapBtn = (Button) findViewById(R.id.mapBtn);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        listBtn = (Button) findViewById(R.id.listBtn);

        //lng = mylocation.getLongitude();
        //lat = mylocation.getLatitude();
        setUpGClient();
        intent = getIntent();
        lat = Double.parseDouble(intent.getStringExtra("latitude"));
        lng = Double.parseDouble(intent.getStringExtra("longitude"));
        lists = getAllList(lat,lng);

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    lng = mylocation.getLongitude();
                    lat = mylocation.getLatitude();
                    blankFragment1 = new BlankFragment1(lat, lng);
                    changeFragment(blankFragment1);
                    mapBtn.setSelected(true);
                    searchBtn.setSelected(false);
                    listBtn.setSelected(false);
                }catch (Exception e){

                }
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    lng = mylocation.getLongitude();
                    lat = mylocation.getLatitude();
                    blankFragment2 = new BlankFragment2(lat, lng);
                    changeFragment(blankFragment2);
                    mapBtn.setSelected(false);
                    searchBtn.setSelected(true);
                    listBtn.setSelected(false);
                }catch (Exception e){

                }
            }
        });

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    lng = mylocation.getLongitude();
                    lat = mylocation.getLatitude();
                    blankFragment3 = new BlankFragment3(MainActivity.this,lat, lng);
                    changeFragment(blankFragment3);
                    mapBtn.setSelected(false);
                    searchBtn.setSelected(false);
                    listBtn.setSelected(true);
                }catch (Exception e){


                }
            }
        });

    }

    @Override
    protected void onStart() {

        super.onStart();

    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onStop() {
        //checkGPSService();

        super.onStop();
    }

    public void changeFragment(Fragment fragment) {
        //FragmentTransaction의 API를 사용하면 Fragment의 추가, 변경 ,제거 작업 가능
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //위에서 가져온 Transaction을 이용해 밑에 3가지 기능 가능
        //add() : Fragment 추가
        //remove() : Fragment 제거
        //replace() : Fragment 변경
        fragmentTransaction.replace(R.id.fragment_container, fragment);

        //Transaction 작업 후 마지막에 commit()를 호출 후 적용
        fragmentTransaction.commit();
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            Double latitude=mylocation.getLatitude();
            Double longitude=mylocation.getLongitude();
            lng = mylocation.getLongitude();
            lat = mylocation.getLatitude();
            //Or Do whatever you want with your location
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Do whatever you need
        //You can display a message here
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }

    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation =  LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    //Toast.makeText(getApplicationContext(),"lat : "+mylocation.getLatitude()+"lng : "+mylocation.getLongitude(),Toast.LENGTH_LONG).show();
                    @SuppressLint("RestrictedApi") LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:

                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(MainActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);

                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                    try {
                                        status.startResolutionForResult(MainActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                                    break;
                            }
                        }
                    });
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    public ArrayList<ListItem> getAllList(double latitude, double longitude){
        listItems = new ArrayList<>();
        mylat = latitude;
        mylng = longitude;
        Response.Listener menu3Listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    for(int i=0;i<jsonArray.length();i++){
                        ListItem listItem;
                        JSONObject item = jsonArray.getJSONObject(i);

                        String user_id = item.getString("user_id");
                        String title = item.getString("title");
                        String kind = item.getString("kind");
                        String lat2 = item.getString("latitude");
                        String lng2 = item.getString("longitude");
                        //Toast.makeText(getApplicationContext(), "id : "+user_id+"lat : "+lat+"lng : "+lng, Toast.LENGTH_SHORT).show();
                        Bitmap bitmap =  new ImageLoader().getBitmapImg(user_id+"-mainimage.jpg");

                        listItem = new ListItem(bitmap, user_id, title, kind, distanceByDegree(mylat,mylng, Double.parseDouble(lat2), Double.parseDouble(lng2)), lat2, lng2);
                        listItems.add(listItem);


                    }
                    //Collections.sort(listItems,ascendingObj);
                   Collections.sort(listItems, new Comparator<ListItem>() {
                       @Override
                       public int compare(ListItem t1, ListItem t2) {
                           if(Double.parseDouble(t1.getDistance())>Double.parseDouble(t2.getDistance())){
                               return 1;
                           }else if(Double.parseDouble(t1.getDistance())<Double.parseDouble(t2.getDistance())){
                               return -1;
                           }else{
                               return 0;
                           }
                       }
                   });

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };


        Menu3Request menu3Request = new Menu3Request(menu3Listener);
        RequestQueue enrollQueue = Volley.newRequestQueue(this);
        enrollQueue.add(menu3Request);

        return listItems;
    }//getAllList

    public String distanceByDegree(double my_lat, double my_lng, double lat2, double lng2){
        String dis="";
        Location startPos = new Location("PointA");
        Location endPos = new Location("PointB");

        startPos.setLatitude(my_lat);
        startPos.setLongitude(my_lng);
        endPos.setLatitude(lat2);
        endPos.setLongitude(lng2);

        double distance = startPos.distanceTo(endPos);

        return Integer.toString((int)distance);
    }//DistanceByDegree


    public  ArrayList<ListItem> getLists(){
        if(lists == null) lists = getAllList(lat,lng);
        return lists;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if((keyCode ==KeyEvent.KEYCODE_BACK)){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("프로그램을 종료하시겠습니까?")
                    .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).show();
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }
}