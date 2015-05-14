package com.marco.onmyway.model;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Marco on 14/05/2015.
 */
public class AppointmentBase {
    private String title;
    private Location location;
    private Calendar startTime;
    private Calendar trackTime;

    public AppointmentBase(){
        startTime = Calendar.getInstance();
        trackTime = Calendar.getInstance();
        location = new Location();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getTrackTime() {
        return trackTime;
    }

    public void setTrackTime(Calendar dateTime) {
        this.trackTime = dateTime;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar dateTime) {
        this.startTime = dateTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
