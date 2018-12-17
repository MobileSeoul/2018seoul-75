package com.android.jakchang.foodtruck.DAO;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SellerInfoRegisterRequest extends StringRequest{

    final static private String URL = "http://ckdrb7067.cafe24.com/sellerinforegister.php";
    private Map<String,String> parameters;

    public SellerInfoRegisterRequest(String user_id,String title, String kind, String mainimage, String subimage1, String subimage2, String subimage3
            , String subimage4, String description, double latitude, double longitude, String str_end,Response.Listener<String> listener){

        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("user_id",user_id);
        parameters.put("title",title);
        parameters.put("kind",kind);
        parameters.put("mainimage",mainimage);
        parameters.put("subimage1",subimage1);
        parameters.put("subimage2",subimage2);
        parameters.put("subimage3",subimage3);
        parameters.put("subimage4",subimage4);
        parameters.put("description",description);
        parameters.put("latitude",latitude+"");
        parameters.put("longitude",longitude+"");
        parameters.put("str_end",str_end);

    }

    public Map<String,String> getParams(){

        return parameters;
    }

}
