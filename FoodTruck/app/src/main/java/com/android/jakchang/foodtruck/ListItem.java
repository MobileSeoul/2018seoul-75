package com.android.jakchang.foodtruck;

import android.graphics.Bitmap;

public class ListItem {

    Bitmap image;
    String user_id,name,kind,distance;
    String lat,lng;

    public ListItem(Bitmap image,String user_id,String name, String kind, String distance,String lat, String lng){
        this.image = image;
        this.user_id = user_id;
        this.name = name;
        this.kind = kind;
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
    }


    public ListItem(Bitmap image, String name, String kind, String distance){
        this.image = image;
        this.name = name;
        this.kind = kind;
        this.distance = distance;

    }

    public ListItem(String name, String kind, String distance){
        this.name = name;
        this.kind = kind;
        this.distance = distance;

    }
    public Bitmap getImage() { return image;}
    public void setImage(Bitmap image) {this.image = image; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }
    public String getLat() { return lat; }
    public void setLat(String lat) { this.lat = lat; }
    public String getLng() { return lng; }
    public void setLng(String lng) { this.lng = lng; }
    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) {this.user_id = user_id; }
}
