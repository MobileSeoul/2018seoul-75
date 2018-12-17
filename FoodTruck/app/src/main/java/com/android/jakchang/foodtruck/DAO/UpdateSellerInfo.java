package com.android.jakchang.foodtruck.DAO;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UpdateSellerInfo extends StringRequest {

    final static private String URL = "http://ckdrb7067.cafe24.com/sellerinfoupdate.php";
    private Map<String,String> parameters;

    public UpdateSellerInfo(String user_id,String title,String kind, String description, String latitude, String longitude, Response.Listener<String> listener){

        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("user_id",user_id);
        parameters.put("title",title);
        parameters.put("kind",kind);
        parameters.put("description",description);
        parameters.put("latitude",latitude);
        parameters.put("longitude",longitude);


    }

    public Map<String,String> getParams(){

        return parameters;
    }

}