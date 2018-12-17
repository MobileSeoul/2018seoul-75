package com.android.jakchang.foodtruck.DAO;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CheckID extends StringRequest {

    final static private String URL = "http://ckdrb7067.cafe24.com/check_id.php";
    private Map<String,String> parameters;

    public CheckID(String user_id, Response.Listener<String> listener){

        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("user_id",user_id);

    }

    public Map<String,String> getParams(){

        return parameters;
    }

}