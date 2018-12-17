package com.android.jakchang.foodtruck;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SeeSellerInfo extends AppCompatActivity {
    Intent intent;
    String user_id="";
    private static String TAG = "phpquerytest";
    private static final String TAG_JSON="response";
    String mJsonString;
    Bitmap main_img,sub1_img,sub2_img,sub3_img;
    String kind="고기",description = "",des = "";
    ImageView mainImg, subImg1,subImg2,subImg3;
    String sellerTitle="",phone="";
    String latitude,longitude;
    BitmapDrawable drawable;
    Resources res;
    String[] resList;
    Spinner spinner;
    TextView title,explainText,kindTV;
    ScrollView scrollView;
    Bitmap bitmap;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_see_seller);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        title = (TextView)findViewById(R.id.see_title);
        kindTV = (TextView)findViewById(R.id.kindTV);

        mainImg = (ImageView)findViewById(R.id.see_mainImg);
        subImg1 = (ImageView)findViewById(R.id.see_subImg1);
        subImg2 = (ImageView)findViewById(R.id.see_subImg2);
        subImg3 = (ImageView)findViewById(R.id.see_subImg3);

        explainText = (TextView) findViewById(R.id.see_explainText);
        scrollView = (ScrollView)findViewById(R.id.see_scrollView2);

        explainText.setMaxLines(14);


        res = getResources();
        resList = res.getStringArray(R.array.data_spinner);


        explainText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                scrollView.requestDisallowInterceptTouchEvent(true);
                return true;
            }
        });


        intent = getIntent();
        user_id = intent.getStringExtra("user_id");

        GetData getData = new GetData();
        getData.execute(user_id);
        imageLoadFromServer(user_id);

        mainImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getImage();
                intent.putExtra("index","0");
                startActivity(intent);
            }
        });

        subImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
                intent.putExtra("index","1");
                startActivity(intent);
            }
        });

        subImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
                intent.putExtra("index","2");
                startActivity(intent);
            }
        });

        subImg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
                intent.putExtra("index","3");
                startActivity(intent);
            }
        });


    }

    public void getImage(){
        intent = new Intent(getApplicationContext(),ViewImage.class);
        drawable = (BitmapDrawable)mainImg.getDrawable();
        bitmap = drawable.getBitmap();
        String main = DataHolder.putDataHolder(bitmap);

        drawable = (BitmapDrawable)subImg1.getDrawable();
        bitmap = drawable.getBitmap();
        String sub1 = DataHolder.putDataHolder(bitmap);

        drawable = (BitmapDrawable)subImg2.getDrawable();
        bitmap = drawable.getBitmap();
        String sub2 = DataHolder.putDataHolder(bitmap);

        drawable = (BitmapDrawable)subImg3.getDrawable();
        bitmap = drawable.getBitmap();
        String sub3 = DataHolder.putDataHolder(bitmap);

        intent.putExtra("main",main);
        intent.putExtra("sub1",sub1);
        intent.putExtra("sub2",sub2);
        intent.putExtra("sub3",sub3);
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
                    kindTV.setText("종류 : "+kind);
                    break;
                }
            }
            explainText.setText(des);
            title.setText(sellerTitle);

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }//showResult


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


}
