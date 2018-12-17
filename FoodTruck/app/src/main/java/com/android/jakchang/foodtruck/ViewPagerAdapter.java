package com.android.jakchang.foodtruck;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ViewPagerAdapter extends PagerAdapter {

    Bitmap[] bitmaps;
    private LayoutInflater inflater;
    private Context context;
    private int count;


    public ViewPagerAdapter(Context context,Bitmap[] bitmaps,int count){
        this.context = context;
        this.bitmaps = bitmaps;
        this.count = count;
    }


    @Override
    public int getCount() {
        return bitmaps.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==((RelativeLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.imageslide,container,false);
        ImageView imageView = (ImageView)v.findViewById(R.id.slideImg);
        imageView.setImageBitmap(bitmaps[(position+count)%4]);

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}
