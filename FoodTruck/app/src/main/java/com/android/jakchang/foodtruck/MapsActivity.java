package com.android.jakchang.foodtruck;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    SupportMapFragment mapFragment;

    private double mylat,mylng,otlat,otlng;

    private LatLng mylocation,otlocation;
    Intent intent;
    String markerUserId = "",title,distance,kind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        intent = getIntent();
        mylat = Double.parseDouble(intent.getStringExtra("mylat"));
        mylng = Double.parseDouble(intent.getStringExtra("mylng"));
        otlat = Double.parseDouble(intent.getStringExtra("otlat"));
        otlng = Double.parseDouble(intent.getStringExtra("otlng"));
        markerUserId = intent.getStringExtra("user_id");
        kind =  intent.getStringExtra("kind");
        distance = intent.getStringExtra("distance");
        title = intent.getStringExtra("title");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mylocation = new LatLng(mylat,mylng);
        otlocation= new LatLng(otlat,otlng);

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
        mMap.addMarker(new MarkerOptions().position(otlocation).title(title).snippet("종류 : "+kind+"\n"+"거리 :"+mToKm(Double.parseDouble(distance))).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));


        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(googleMap.getCameraPosition().target)
                        .zoom(17).build()));

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getApplicationContext(), SeeSellerInfo.class);
                intent.putExtra("user_id", markerUserId);
                startActivity(intent);
            }
        });


    }

    public String mToKm(double distance){
        String dis;
        if(distance>=1000){
            int div = (int)distance/1000;
            int rest = (int)distance%1000;
            //Toast.makeText(getContext(),"div : "+div+"rest : "+rest,Toast.LENGTH_LONG).show();
            dis = div+"."+rest+"km";
        }else{
            dis = Integer.toString((int)distance)+"m";
        }
        return dis;
    }


}
