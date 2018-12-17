package com.android.jakchang.foodtruck;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment2 extends Fragment {

    double lat, lng;
    Intent intent;
    public BlankFragment2() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public BlankFragment2(double lat, double lng) {
        // Required empty public constructor
        this.lat = lat;
        this.lng = lng;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_fragment02,container,false);
        Button button1 = (Button)v.findViewById(R.id.menu1Btn);
        Button button2 = (Button)v.findViewById(R.id.menu2Btn);
        Button button3 = (Button)v.findViewById(R.id.menu3Btn);
        Button button4 = (Button)v.findViewById(R.id.menu4Btn);
        Button button5 = (Button)v.findViewById(R.id.menu5Btn);
        Button button6 = (Button)v.findViewById(R.id.menu6Btn);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(),SelectedMenuActivity.class);
                intent.putExtra("kind","고기");
                intent.putExtra("latitude",lat+"");
                intent.putExtra("longitude",lng+"");
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(),SelectedMenuActivity.class);
                intent.putExtra("kind","해물");
                intent.putExtra("latitude",lat+"");
                intent.putExtra("longitude",lng+"");
                startActivity(intent);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(),SelectedMenuActivity.class);
                intent.putExtra("kind","패스트푸드");
                intent.putExtra("latitude",lat+"");
                intent.putExtra("longitude",lng+"");
                startActivity(intent);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(),SelectedMenuActivity.class);
                intent.putExtra("kind","면요리");
                intent.putExtra("latitude",lat+"");
                intent.putExtra("longitude",lng+"");
                startActivity(intent);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(),SelectedMenuActivity.class);
                intent.putExtra("kind","분식");
                intent.putExtra("latitude",lat+"");
                intent.putExtra("longitude",lng+"");
                startActivity(intent);
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(),SelectedMenuActivity.class);
                intent.putExtra("kind","디저트");
                intent.putExtra("latitude",lat+"");
                intent.putExtra("longitude",lng+"");
                startActivity(intent);
            }
        });



        return v;
    }

}
