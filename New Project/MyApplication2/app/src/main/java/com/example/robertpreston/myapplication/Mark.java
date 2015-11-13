package com.example.robertpreston.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Adam on 11/2/2015.
 */
//class to store info about markes in list sad that I needed a class
//but json and markers didn't get along well
public class Mark implements Parcelable {
    private String name;
    private double lat;
    private double lon;

    public Mark() {
        name = "default";
        lat = 0;
        lon = 0;

    }

    public Mark(Parcel in){
        this.name = in.readString();
        this.lat = in.readDouble();
        this.lon = in.readDouble();
    }

    public Mark(String nme, double latitude, double longitude) {
        name = nme;
        lat = latitude;
        lon = longitude;
    }

    public void setName(String nme) {
        name = nme;
    }
    public void setLat(double lati) {
        lat = lati;
    }

    public void setLon(double longi) {
        lon = longi;
    }

    public String getName() {
        return name;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLong() {
        return lon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /*Previous parcel attempt
    public void readFromParcel(Parcel in) {
        name = in.readString();
        lat = in.readDouble();
        lon = in.readDouble();
    }*/

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(name);
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);

    }

    public static final Parcelable.Creator<Mark> CREATOR = new Parcelable.Creator<Mark>(){
        public Mark createFromParcel(Parcel in){
            return new Mark(in);
        }
        public Mark[] newArray(int size){
            return new Mark[size];
        }
    };
}

