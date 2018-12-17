package com.android.jakchang.foodtruck.DAO;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest{

    final static private String URL = "http://ckdrb7067.cafe24.com/login.php";
    private Map<String,String> parameters;

    public LoginRequest(String user_id, String user_pw, Response.Listener<String> listener){

        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("user_id",user_id);
        parameters.put("user_pw",user_pw);


    }

    public Map<String,String> getParams(){

        return parameters;
    }

}
