package com.android.jakchang.foodtruck;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.android.jakchang.foodtruck.DAO.Menu3Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GetAllList extends AppCompatActivity{
    private ArrayList<ListItem> lists;
    AscendingObj ascendingObj;
    Context context;

    public GetAllList(Context context){
        this.lists = getAllList();
        this.context = context;
    }
    public GetAllList(ArrayList<ListItem> arrayList){
        this.lists = arrayList;
        this.lists = getAllList();
    }

    public ArrayList<ListItem> getAllList(){
        final ArrayList<ListItem> listItems = new ArrayList<>();
        Response.Listener menu3Listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ascendingObj = new AscendingObj();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    for(int i=0;i<jsonArray.length();i++){

                        JSONObject item = jsonArray.getJSONObject(i);

                        String user_id = item.getString("user_id");
                        String title = item.getString("title");
                        String kind = item.getString("kind");
                        String lat = item.getString("latitude");
                        String lng = item.getString("longitude");
                        //Toast.makeText(getApplicationContext(), "id : "+user_id+"lat : "+lat+"lng : "+lng, Toast.LENGTH_SHORT).show();
                        Bitmap bitmap =  new ImageLoader().getBitmapImg(user_id+"-mainimage.jpg");
                        ListItem listItem = new ListItem(bitmap,user_id,title,kind,distanceByDegree(37, 127, 38, 128),lat,lng);
                        listItems.add(listItem);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Collections.sort(listItems,ascendingObj);
            }
        };


        Menu3Request menu3Request = new Menu3Request(menu3Listener);
        RequestQueue enrollQueue = Volley.newRequestQueue(context);
        enrollQueue.add(menu3Request);
        return listItems;
    }//getAllList

    public String distanceByDegree(double my_lat, double my_lng, double lat2, double lng2){
        Location startPos = new Location("PointA");
        Location endPos = new Location("PointB");

        startPos.setLatitude(my_lat);
        startPos.setLongitude(my_lng);
        endPos.setLatitude(lat2);
        endPos.setLongitude(lng2);

        double distance = startPos.distanceTo(endPos);

        //Toast.makeText(getContext(),"my lat : "+my_lat+"my lng : "+my_lng+"lat2 : "+lat2+"lng2 : "+lng2,Toast.LENGTH_LONG).show();
        return Integer.toString((int)distance)+"m";
    }//DistanceByDegree
    class AscendingObj implements Comparator<ListItem> {

        @Override
        public int compare(ListItem t1, ListItem t2) {
            return t1.getDistance().compareTo(t2.getDistance());
        }
    }

    public  ArrayList<ListItem> getLists(){
        if(lists == null) lists = getAllList();
        return lists;
    }

}
