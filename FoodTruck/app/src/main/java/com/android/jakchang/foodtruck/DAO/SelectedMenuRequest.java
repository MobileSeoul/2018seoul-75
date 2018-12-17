package com.android.jakchang.foodtruck.DAO;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SelectedMenuRequest extends StringRequest{

    final static private String URL = "http://ckdrb7067.cafe24.com/menu2.php";
    private Map<String,String> parameters;

    public SelectedMenuRequest(String kind, Response.Listener<String> listener){

        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("kind",kind);


    }

    public Map<String,String> getParams(){

        return parameters;
    }

}
