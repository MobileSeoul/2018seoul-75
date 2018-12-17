package com.android.jakchang.foodtruck.DAO;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest{

    final static private String URL = "http://ckdrb7067.cafe24.com/register.php";
    private Map<String,String> parameters;

    public RegisterRequest(String user_id, String user_pw, String user_email, String user_phone, Response.Listener<String> listener){

        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("user_id",user_id);
        parameters.put("user_pw",user_pw);
        parameters.put("user_email",user_email);
        parameters.put("user_phone",user_phone);

    }

    public Map<String,String> getParams(){

        return parameters;
    }

}
