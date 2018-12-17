package com.android.jakchang.foodtruck.DAO;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ImageUploadRequest extends StringRequest{

    final static private String URL = "http://ckdrb7067.cafe24.com/uploadimage.php";
    private Map<String,String> parameters;

    public ImageUploadRequest(String user_id, String mainimage, String subimage1, String subimage2, String subimage3, Response.Listener<String> listener){

        super(Method.POST,URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("user_id",user_id);
        parameters.put("mainimage",mainimage);
        parameters.put("subimage1",subimage1);
        parameters.put("subimage2",subimage2);
        parameters.put("subimage3",subimage3);



    }

    public Map<String,String> getParams(){

        return parameters;
    }

}
