package com.android.jakchang.foodtruck;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ImageLoader {

    private final String serverUrl = "http://ckdrb7067.cafe24.com/images/";

    public ImageLoader() {

        new ThreadPolicy();
    }

    public Bitmap getBitmapImg(String str) {

        Bitmap bitmapImg = null;

        try {
            URL url = new URL(serverUrl + URLEncoder.encode(str, "utf-8"));
            // Character is converted to 'UTF-8' to prevent broken

            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            bitmapImg = BitmapFactory.decodeStream(is);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmapImg;
    }
}