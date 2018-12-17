package com.android.jakchang.foodtruck;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jakchang.foodtruck.DAO.Menu3Request;
import com.android.jakchang.foodtruck.DAO.StartEndRequest;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment3 extends Fragment {

    View v;
    private RecyclerView mRecyclerView;
    private ArrayList<ListItem> listItems;
    //private double my_lat=37.5520991,my_lng=127.0794181;
    private double my_lat,my_lng;
    private static String TAG = "phpquerytest";
    private static final String TAG_JSON="response";
    String mJsonString;
    String url = "http://ckdrb7067.cafe24.com/images/";
    private Context context = null;


    String user_id,title,kind,lat,lng;
    AscendingObj ascendingObj;
    public BlankFragment3() {

    }


    @SuppressLint("ValidFragment")
    public BlankFragment3(Context context,double my_lat, double my_lng) {

        this.context = context;
        this.my_lat = my_lat;
        this.my_lng = my_lng;

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_fragment03,container,false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.fragment3_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(),listItems);
        mRecyclerView.setAdapter(recyclerViewAdapter);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //listItems = new ArrayList<>();
        //GetData task = new GetData();
        //task.execute("");
        //ascendingObj = new AscendingObj();

        listItems = ((MainActivity)context).getLists();
        //Collections.sort(listItems,ascendingObj);
    }

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

    public String mToKm(double distance){
        String dis;
        if(distance>=1000){
            int div = (int)distance/1000;
            int rest = (int)distance%1000;
            //Toast.makeText(getContext(),"div : "+div+"rest : "+rest,Toast.LENGTH_LONG).show();
            dis = div+"."+rest+"km";
        }else{
            dis = Integer.toString((int)distance)+"m";
        }
        return dis;
    }


    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

        Context mContext;
        List<ListItem> mListItem;
        int position;


        public RecyclerViewAdapter(Context mContext, List<ListItem> mData){
            this.mContext = mContext;
            this.mListItem = mData;
        }

        @Override
        public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {

            View v;
            v = LayoutInflater.from(mContext).inflate(R.layout.item_frame, parent,false);
            final RecyclerViewAdapter.MyViewHolder vHolder = new RecyclerViewAdapter.MyViewHolder(v);


            vHolder.listImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = vHolder.getAdapterPosition();
                    Intent intent = new Intent(getContext(),SeeSellerInfo.class);
                    intent.putExtra("user_id",listItems.get(position).getUser_id()+"");
                    startActivity(intent);
                }
            });

            vHolder.roadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = vHolder.getAdapterPosition();
                    Intent intent = new Intent(getContext(),MapsActivity.class);
                    intent.putExtra("user_id",listItems.get(position).getUser_id()+"");
                    intent.putExtra("mylat",my_lat+"");
                    intent.putExtra("mylng",my_lng+"");
                    intent.putExtra("otlat",listItems.get(position).getLat()+"");
                    intent.putExtra("otlng",listItems.get(position).getLng()+"");
                    intent.putExtra("title",listItems.get(position).getName());
                    intent.putExtra("kind",listItems.get(position).getKind());
                    intent.putExtra("distance",listItems.get(position).getDistance());
                    startActivity(intent);


                }
            });



            return vHolder;
        }


         @Override
        public void onBindViewHolder(RecyclerViewAdapter.MyViewHolder holder, int position) {
            ListItem listItem = mListItem.get(position);

            holder.nameTV.setText(mListItem.get(position).getName());
            holder.kindTV.setText(mListItem.get(position).getKind());
            holder.distanceTV.setText("거리 : "+mToKm(Double.parseDouble(mListItem.get(position).getDistance())));
            Bitmap bitmap = Bitmap.createScaledBitmap(mListItem.get(position).getImage(), 80, 80,true);
            holder.listImage.setImageBitmap(bitmap);
            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.road);
            holder.roadImage.setImageDrawable(drawable);
            //Glide.with(mContext).load(listItem.getImage()).into(holder.listImage);

        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }

        public int getPosition(){
            return position;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder{

            private ImageView listImage;
            private ImageView roadImage;
            private TextView nameTV;
            private TextView kindTV;
            private TextView distanceTV;



            public MyViewHolder(View view) {
                super(view);
                listImage = (ImageView)view.findViewById(R.id.listImage);
                roadImage = (ImageView)view.findViewById(R.id.roadImage);
                nameTV = (TextView)view.findViewById(R.id.nameTV);
                kindTV = (TextView)view.findViewById(R.id.kindTV);
                distanceTV = (TextView)view.findViewById(R.id.distanceTV);

            }


        }//class MyViewHolder


    }//class RecyclerViewAdapte

    private class GetData extends AsyncTask<String, Void, String> {

        String errorString = null;

        @Override

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null){
            }
            else {
                mJsonString = result;
                Log.d(TAG, "mJsonString - " + mJsonString);
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String searchKeyword = params[0];
            String serverURL = "http://ckdrb7067.cafe24.com/menu1.php";
            String postParameters = "";
            try {
                URL url = new URL(serverURL);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);
                InputStream inputStream;

                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;

            }

        }

    }//GetData

    private void showResult(){

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

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
            Log.d(TAG, "showResult : ", e);
        }
    }//showResult





}

