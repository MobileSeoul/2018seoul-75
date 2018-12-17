package com.android.jakchang.foodtruck;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.jakchang.foodtruck.DAO.ImageUploadRequest;
import com.android.jakchang.foodtruck.DAO.StartEndRequest;
import com.android.jakchang.foodtruck.DAO.UpdateSellerInfo;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SellerActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    Switch aSwitch;
    Spinner spinner;
    Button editBtn, submitBtn;
    String kind="고기",description = "",des = "";
    EditText title,explainText;
    ImageView mainImg, subImg1,subImg2,subImg3;
    ScrollView scrollView2;
    int editFlag = 0,switchFlag = 0;
    Intent intent;
    Bitmap main_img,sub1_img,sub2_img,sub3_img,sub4_img;

    Resources res;
    String[] resList;

    private static String TAG = "phpquerytest";
    private static final String TAG_JSON="response";
    String mJsonString;

    final int MAIN_IMAGE = 1000;
    final int SUB1_IMAGE = 1001;
    final int SUB2_IMAGE = 1002;
    final int SUB3_IMAGE = 1003;
    final int SUB4_IMAGE = 1004;

    double lat,lng;
    String sellerTitle="";
    String latitude,longitude;
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    String user_id = "";
    BitmapDrawable drawable;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_seller_info);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        aSwitch = (Switch)findViewById(R.id.switch1);
        spinner = (Spinner)findViewById(R.id.spinner);
        editBtn = (Button)findViewById(R.id.editBtn);
        submitBtn = (Button)findViewById(R.id.submitBtn);
        title = (EditText)findViewById(R.id.title);
        mainImg = (ImageView)findViewById(R.id.mainImg);
        subImg1 = (ImageView)findViewById(R.id.subImg1);
        subImg2 = (ImageView)findViewById(R.id.subImg2);
        subImg3 = (ImageView)findViewById(R.id.subImg3);
        explainText = (EditText)findViewById(R.id.explainText);
        scrollView2 = (ScrollView)findViewById(R.id.scrollView2);

        intent = getIntent();
        user_id = intent.getStringExtra("user_id");

        explainText.setMaxLines(14);

        res = getResources();
        resList = res.getStringArray(R.array.data_spinner);
        for(int i=0;i<resList.length;i++){
            if(resList[i].equals(kind)){
                spinner.setSelection(i);
                break;
            }
        }

        setUpGClient();

        title.setEnabled(false);
        mainImg.setEnabled(false);
        subImg1.setEnabled(false);
        subImg2.setEnabled(false);
        subImg3.setEnabled(false);
        explainText.setEnabled(false);
        spinner.setEnabled(false);


        GetData task = new GetData();
        task.execute(user_id);
        imageLoadFromServer(user_id);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               kind =  (String)adapterView.getItemAtPosition(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Response.Listener startEndListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(switchFlag==1) {
                            Toast.makeText(getApplicationContext(), "장사가 시작되었습니다!", Toast.LENGTH_LONG).show();
                        }else if(switchFlag==0){
                            Toast.makeText(getApplicationContext(), "장사가 종료되었습니다!", Toast.LENGTH_LONG).show();
                        }
                    }
                };

                if(switchFlag==0){
                    switchFlag=1;
                    editBtn.setEnabled(false);
                    submitBtn.setEnabled(false);

                    StartEndRequest startEndRequest = new StartEndRequest(user_id, "1", startEndListener);
                    RequestQueue enrollQueue = Volley.newRequestQueue(SellerActivity.this);
                    enrollQueue.add(startEndRequest);
                }
                else if(switchFlag==1){
                    switchFlag=0;
                    editBtn.setEnabled(true);
                    submitBtn.setEnabled(true);

                    StartEndRequest startEndRequest = new StartEndRequest(user_id, "0", startEndListener);
                    RequestQueue enrollQueue = Volley.newRequestQueue(SellerActivity.this);
                    enrollQueue.add(startEndRequest);
                }
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("");
            }
        });

        mainImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent,"select Image"),MAIN_IMAGE);
            }
        });

        subImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent,"select Image"),SUB1_IMAGE);
            }
        });

        subImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent,"select Image"),SUB2_IMAGE);
            }
        });

        subImg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent,"select Image"),SUB3_IMAGE);
            }
        });



        explainText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


       explainText.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {

               scrollView2.requestDisallowInterceptTouchEvent(true);

               return false;
           }
       });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editFlag ==0 && switchFlag==0){
                    editFlag = 1;
                    title.setEnabled(true);
                    mainImg.setEnabled(true);
                    subImg1.setEnabled(true);
                    subImg2.setEnabled(true);
                    subImg3.setEnabled(true);
                    explainText.setEnabled(true);
                    spinner.setEnabled(true);
                    aSwitch.setEnabled(false);
                    submitBtn.setEnabled(false);
                    editBtn.setText("편집중");
                    submitBtn.setBackgroundResource(R.drawable.buttonborder2);

                }
                else if(editFlag == 1){
                    editFlag = 0;
                    title.setEnabled(false);
                    mainImg.setEnabled(false);
                    subImg1.setEnabled(false);
                    subImg2.setEnabled(false);
                    subImg3.setEnabled(false);
                    explainText.setEnabled(false);
                    spinner.setEnabled(false);
                    aSwitch.setEnabled(true);
                    submitBtn.setEnabled(true);
                    editBtn.setText("편집");
                    submitBtn.setBackgroundResource(R.drawable.buttonborder);
                }
                else{
                    Toast.makeText(getApplicationContext(),"장사를 종료후 수정해주세요!",Toast.LENGTH_SHORT).show();
                }
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Response.Listener updateSellerListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                };

                Response.Listener imageUploadListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                };

                if(editFlag==1){
                    Toast.makeText(getApplicationContext(),"수정을 종료 후 등록해주세요!",Toast.LENGTH_SHORT).show();
                }
                else if(switchFlag==1){
                    Toast.makeText(getApplicationContext(),"장사를 종료후 수정해주세요!",Toast.LENGTH_SHORT).show();
                }else if(title.getText().toString().length()<2){
                    Toast.makeText(getApplicationContext(),"상호명을 입력해주세요!",Toast.LENGTH_SHORT).show();
                }else{
                    try {

                        sellerTitle = title.getText().toString();
                        drawable = (BitmapDrawable)mainImg.getDrawable();
                        main_img = drawable.getBitmap();
                        drawable = (BitmapDrawable)subImg1.getDrawable();
                        sub1_img = drawable.getBitmap();
                        drawable = (BitmapDrawable)subImg2.getDrawable();
                        sub2_img = drawable.getBitmap();
                        drawable = (BitmapDrawable)subImg3.getDrawable();
                        sub3_img = drawable.getBitmap();
                        kind = spinner.getSelectedItem().toString();
                        description = explainText.getText().toString();
                        lat = mylocation.getLatitude();
                        lng = mylocation.getLongitude();
                        latitude = lat +"";
                        longitude = lng + "";

                        UpdateSellerInfo getSellerUpdateRequest = new UpdateSellerInfo(user_id,sellerTitle,kind,description,latitude,longitude,updateSellerListener);
                        RequestQueue enrollQueue = Volley.newRequestQueue(SellerActivity.this);
                        enrollQueue.add(getSellerUpdateRequest);

                        ImageUploadRequest imageUploadRequest = new ImageUploadRequest(user_id,imageToString(main_img),imageToString(sub1_img),imageToString(sub2_img),imageToString(sub3_img),imageUploadListener);
                        enrollQueue = Volley.newRequestQueue(SellerActivity.this);
                        enrollQueue.add(imageUploadRequest);

                        Toast.makeText(getApplicationContext(),"정보가 등록되었습니다!",Toast.LENGTH_SHORT).show();


                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //등록하는 부분
                }

            }
        });

    }//onCreate


    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

        if(requestCode==MAIN_IMAGE && resultCode== Activity.RESULT_OK){
            Uri selectImageUri = data.getData();

            try {
                InputStream is = getContentResolver().openInputStream(selectImageUri);
                main_img = BitmapFactory.decodeStream(is);
                main_img = Bitmap.createScaledBitmap(main_img, mainImg.getWidth(),mainImg.getHeight(),true);
                mainImg.setImageBitmap(main_img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }//if - Main

        else if(requestCode==SUB1_IMAGE && resultCode== Activity.RESULT_OK){
            Uri selectImageUri = data.getData();

            try {
                InputStream is = getContentResolver().openInputStream(selectImageUri);
                sub1_img = BitmapFactory.decodeStream(is);
                sub1_img = Bitmap.createScaledBitmap(sub1_img, subImg1.getWidth(),subImg1.getHeight(),true);
                subImg1.setImageBitmap(sub1_img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }//else if - Sub1


        else if(requestCode==SUB2_IMAGE && resultCode== Activity.RESULT_OK){
            Uri selectImageUri = data.getData();

            try {
                InputStream is = getContentResolver().openInputStream(selectImageUri);
                sub2_img = BitmapFactory.decodeStream(is);
                sub2_img = Bitmap.createScaledBitmap(sub2_img, subImg2.getWidth(),subImg2.getHeight(),true);
                subImg2.setImageBitmap(sub2_img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }//else if - Sub2

        else if(requestCode==SUB3_IMAGE && resultCode== Activity.RESULT_OK){
            Uri selectImageUri = data.getData();

            try {
                InputStream is = getContentResolver().openInputStream(selectImageUri);
                sub3_img = BitmapFactory.decodeStream(is);
                sub3_img = Bitmap.createScaledBitmap(sub3_img, subImg3.getWidth(),subImg3.getHeight(),true);
                subImg3.setImageBitmap(sub3_img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }//else if - Sub3







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
                int permissionLocation = ContextCompat.checkSelfPermission(SellerActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation =  LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
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
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(SellerActivity.this,
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);

                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(SellerActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(SellerActivity.this,
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
        int permissionLocation = ContextCompat.checkSelfPermission(SellerActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
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
            String serverURL = "http://ckdrb7067.cafe24.com/getsellerinfo.php";
            String postParameters = "user_id=" + searchKeyword;
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
                sellerTitle = item.getString("title");
                kind = item.getString("kind");
                des = item.getString("description");

            }

            for(int i=0;i<resList.length;i++){
                if(resList[i].equals(kind)){
                    spinner.setSelection(i);
                    break;
                }
            }
            explainText.setText(des);
            title.setText(sellerTitle);

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }//showResult


    public String imageToString(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }//imageToString

    public void imageLoadFromServer(String user_id){
       main_img = new ImageLoader().getBitmapImg(user_id+"-mainimage.jpg");
       sub1_img = new ImageLoader().getBitmapImg(user_id+"-subimage1.jpg");
       sub2_img = new ImageLoader().getBitmapImg(user_id+"-subimage2.jpg");
       sub3_img = new ImageLoader().getBitmapImg(user_id+"-subimage3.jpg");


       mainImg.setImageBitmap(main_img);
       subImg1.setImageBitmap(sub1_img);
       subImg2.setImageBitmap(sub2_img);
       subImg3.setImageBitmap(sub3_img);


    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if((keyCode ==KeyEvent.KEYCODE_BACK)){
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("프로그램을 종료하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Response.Listener startEndListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                }
                            };
                            StartEndRequest startEndRequest = new StartEndRequest(user_id, "0", startEndListener);
                            RequestQueue enrollQueue = Volley.newRequestQueue(SellerActivity.this);
                            enrollQueue.add(startEndRequest);
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
