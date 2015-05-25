package com.onmyway.model;

import android.support.annotation.Nullable;

/**
 * Created by Marco on 14/05/2015.
 */
public class Location
{
    private String title = "";
    private String address = "";
    private double latitude = 0;
    private double longitude = 0;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public Location(){}

    public Location(String title, @Nullable String address, double latitude, double longitude)
    {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}