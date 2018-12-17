package com.android.jakchang.foodtruck.DAO;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Menu3Request extends StringRequest{
    final static private String URL = "http://ckdrb7067.cafe24.com/menu3.php";
    private Map<String,String> parameters;

    public Menu3Request(Response.Listener<String> listener){

        super(Request.Method.POST,URL,listener,null);
        parameters = new HashMap<>();


    }

    public Map<String,String> getParams(){

        return parameters;
    }

}
