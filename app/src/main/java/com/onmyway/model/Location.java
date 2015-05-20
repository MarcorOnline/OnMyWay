package com.onmyway.model;

/**
 * Created by Marco on 14/05/2015.
 */
public class Location {
    private String title = "";
    private double latitude = 0;
    private double longitude = 0;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
