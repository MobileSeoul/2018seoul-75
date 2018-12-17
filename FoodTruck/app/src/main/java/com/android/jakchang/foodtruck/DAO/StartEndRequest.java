package com.android.jakchang.foodtruck.DAO;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class StartEndRequest extends StringRequest{
    final static private String URL = "http://ckdrb7067.cafe24.com/startend.php";
    private Map<String,String> parameters;

    public StartEndRequest(String user_id,String str_end, Response.Listener<String> listener){

        super(Request.Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("user_id",user_id);
        parameters.put("str_end",str_end);

    }

    public Map<String,String> getParams(){

        return parameters;
    }

}
