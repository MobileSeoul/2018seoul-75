package com.android.jakchang.foodtruck;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.jakchang.foodtruck.DAO.CheckID;
import com.android.jakchang.foodtruck.DAO.GetSellerRequest;
import com.android.jakchang.foodtruck.DAO.ImageUploadRequest;
import com.android.jakchang.foodtruck.DAO.SellerInfoRegisterRequest;
import com.android.jakchang.foodtruck.DAO.SellerRegisterRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Enroll_Seller extends AppCompatActivity {


    Button checkID,certSend,enrollBtn;
    int MY_PERMISSIONS_REQUEST_SEND_SMS =1;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    EditText certNum,enroll_id,enroll_pw,enroll_pw2,enroll_email,enroll_phone;
    int cert;
    boolean checked=false;
    //ArrayList<String> name;
    String id,pw,email,phone;
    boolean enrollkey=false;

    private static String TAG = "phpquerytest";
    private static final String TAG_JSON="response";
    String mJsonString;
    Bitmap mainimage, subimage1, subimage2, subimage3;
    Drawable drawable;
    int idCheck=0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_enroll);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }

        certNum = (EditText)findViewById(R.id.certNum);
        enroll_id = (EditText)findViewById(R.id.enroll_id);
        enroll_pw = (EditText)findViewById(R.id.enroll_pw);
        enroll_pw2 = (EditText)findViewById(R.id.enroll_pw2);
        enroll_email= (EditText)findViewById(R.id.enroll_email);
        enroll_phone= (EditText)findViewById(R.id.enroll_phone);

        checkID = (Button)findViewById(R.id.checkID);
        certSend = (Button)findViewById(R.id.certSend);
        enrollBtn = (Button)findViewById(R.id.enrollBtn);

        sentPI = PendingIntent.getBroadcast(this,0,new Intent(SENT),0);
        deliveredPI = PendingIntent.getBroadcast(this,0,new Intent(DELIVERED),0);

        enroll_id.setPrivateImeOptions("defaultInputmode=english;");
        enroll_email.setPrivateImeOptions("defaultInputmode=english;");

        checkID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = enroll_id.getText().toString();
                if(!id.equals("") && id.length() >=6) {
                    Response.Listener idCheckListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                checked = jsonObject.getBoolean("success");

                                if (checked == true) {
                                    Toast.makeText(getApplicationContext(), "사용할 수 있는 아이디입니다.", Toast.LENGTH_SHORT).show();
                                    idCheck =1;

                                } else {
                                    String id2 = jsonObject.getString("user_id");
                                    Toast.makeText(getApplicationContext(), id2 + " 아이디가 이미 존재합니다.", Toast.LENGTH_SHORT).show();

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                    };

                    CheckID checkID = new CheckID(id, idCheckListener);
                    RequestQueue queue = Volley.newRequestQueue(Enroll_Seller.this);
                    queue.add(checkID);
                }else{
                    Toast.makeText(getApplicationContext(), "6글자 이상 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        certSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    sendSMS(view);

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "연락처를 입력해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        enrollBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    email= enroll_email.getText().toString();

                    enrollkey = true;

                    if (Integer.parseInt(certNum.getText().toString()) != cert) {
                        Toast.makeText(getApplicationContext(), "인증번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
                        certNum.setText("");
                        enrollkey = false;
                    }
                    if (!enroll_pw.getText().toString().equals(enroll_pw2.getText().toString())||enroll_pw.getText().length()<8) {
                        Toast.makeText(getApplicationContext(), "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                        enroll_pw.setText("");
                        enroll_pw2.setText("");
                        enrollkey = false;
                    }
                    if(enroll_id.getText().toString().length()<6){
                        enrollkey = false;
                        Toast.makeText(getApplicationContext(), "6글자 이상 아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                    }if(idCheck==0){
                        Toast.makeText(getApplicationContext(), "아이디 중복을 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                    if(enrollkey==true&&checked==true&&idCheck==1) {
                        Response.Listener sellerEnrollListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        };

                        Response.Listener sellerInfoEnrollListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    checked = jsonObject.getBoolean("success");
                                    if (checked == true) {
                                        Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(),SellerLogin.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        Response.Listener imageUploadListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                            }
                        };


                        pw = enroll_pw.getText().toString();
                        phone = enroll_phone.getText().toString();

                        SellerRegisterRequest enrollRequest = new SellerRegisterRequest(id, pw, email, phone, sellerEnrollListener);
                        RequestQueue enrollQueue = Volley.newRequestQueue(Enroll_Seller.this);
                        enrollQueue.add(enrollRequest);

                        SellerInfoRegisterRequest sellerInfoRegisterRequest = new SellerInfoRegisterRequest
                                (id, "", "", id + "-mainimage.jpg", id + "-subimage1.jpg", id + "-subimage2.jpg"
                                        , id + "-subimage3.jpg", id + "-subimage4.jpg", "", 0, 0, "0", sellerInfoEnrollListener);
                        RequestQueue enrollQueue2 = Volley.newRequestQueue(Enroll_Seller.this);
                        enrollQueue2.add(sellerInfoRegisterRequest);

                        drawable = getResources().getDrawable(R.drawable.seller_image);
                        mainimage = ((BitmapDrawable) drawable).getBitmap();
                        drawable = getResources().getDrawable(android.R.drawable.ic_menu_report_image);
                        subimage1 = ((BitmapDrawable) drawable).getBitmap();
                        subimage2 = ((BitmapDrawable) drawable).getBitmap();
                        subimage3 = ((BitmapDrawable) drawable).getBitmap();


                        ImageUploadRequest imageUploadRequest = new ImageUploadRequest(id, imageToString(mainimage), imageToString(subimage1), imageToString(subimage2), imageToString(subimage3), imageUploadListener);
                        RequestQueue enrollQueue3 = Volley.newRequestQueue(Enroll_Seller.this);
                        enrollQueue3.add(imageUploadRequest);


                    }//try
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }//onCreate



    @Override
    protected void onResume() {
        super.onResume();

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(Enroll_Seller.this,"SMS sent",Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(Enroll_Seller.this,"Generic Failure",Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(Enroll_Seller.this,"No Service",Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(Enroll_Seller.this,"Null PDU",Toast.LENGTH_SHORT).show();
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(Enroll_Seller.this,"Radio Off",Toast.LENGTH_SHORT).show();
                        break;

                }

            }
        };


        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(Enroll_Seller.this,"SMS deliverd",Toast.LENGTH_SHORT).show();
                        break;

                    case Activity.RESULT_CANCELED:
                        Toast.makeText(Enroll_Seller.this,"SMS not delivered",Toast.LENGTH_SHORT).show();
                        break;

                }

            }
        };

        registerReceiver(smsSentReceiver,new IntentFilter(SENT));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));

    }//onResume

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(smsDeliveredReceiver);
        unregisterReceiver(smsSentReceiver);

    }//onPause

    public void sendSMS(View v){

        String userPhone = enroll_phone.getText().toString();
        cert = (int)(Math.random()*9999)+1000;
        //Toast.makeText(Enroll_Seller.this,""+userPhone,Toast.LENGTH_SHORT).show();
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(userPhone,null,"인증번호는 "+cert+"입니다.",sentPI,deliveredPI);


    }//SendSMS


    public String imageToString(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }


}