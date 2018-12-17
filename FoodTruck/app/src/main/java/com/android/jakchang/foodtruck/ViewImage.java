package com.android.jakchang.foodtruck;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class ViewImage extends AppCompatActivity{

    Intent intent;
    Bitmap bitmap[];
    ImageView imageView;
    ViewPagerAdapter viewPagerAdapter;
    ViewPager viewPager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.viewimage);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imageView = findViewById(R.id.extendedImg);
        bitmap = new Bitmap[4];
        intent = getIntent();

        String main = intent.getStringExtra("main");
        bitmap[0] = (Bitmap)DataHolder.popDataHolder(main);
        String sub1 = intent.getStringExtra("sub1");
        bitmap[1] = (Bitmap)DataHolder.popDataHolder(sub1);
        String sub2 = intent.getStringExtra("sub2");
        bitmap[2] = (Bitmap)DataHolder.popDataHolder(sub2);
        String sub3 = intent.getStringExtra("sub3");
        bitmap[3] = (Bitmap)DataHolder.popDataHolder(sub3);

        int count = Integer.parseInt(intent.getStringExtra("index"));

        for(int i=0;i<4;i++){
            bitmap[i] = Bitmap.createScaledBitmap(bitmap[i],386,308,true);
        }

        //imageView.setImageBitmap(bitmap[number]);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(this,bitmap,count);
        viewPager.setAdapter(viewPagerAdapter);


    }//onCreaet


}
