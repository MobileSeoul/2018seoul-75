package com.android.jakchang.foodtruck;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.jakchang.foodtruck.DAO.CheckID;
import com.android.jakchang.foodtruck.DAO.LoginRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SellerLogin extends AppCompatActivity {

    Button loginBtn, enrollBtn;
    Intent intent;
    EditText login_id,login_pw;
    String user_id="",user_pw="";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.seller_login);

        loginBtn = (Button)findViewById(R.id.loginBtn);
        enrollBtn  = (Button)findViewById(R.id.enrollBtn);
        login_id = (EditText)findViewById(R.id.login_id);
        login_pw = (EditText)findViewById(R.id.login_pw);

        login_id.setPrivateImeOptions("defaultInputmode=english;");


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user_id = login_id.getText().toString();
                user_pw = login_pw.getText().toString();

                Response.Listener idCheckListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");



                            if(success){
                                user_id = jsonObject.getString("user_id");
                                intent = new Intent(getApplicationContext(),SellerActivity.class);
                                intent.putExtra("user_id",user_id);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),"아이디 또는 비밀번호가 잘못되었습니다.",Toast.LENGTH_LONG).show();
                            }

                        }
                        catch (JSONException e){
                            e.printStackTrace();

                        }
                    }
                };

                LoginRequest loginRequestn = new LoginRequest(user_id,user_pw,idCheckListener);
                RequestQueue queue = Volley.newRequestQueue(SellerLogin.this);
                queue.add(loginRequestn);

            }
        });

        enrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(),Enroll_Seller.class);
                startActivity(intent);
                finish();
            }
        });

    }

}
