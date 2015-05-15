package com.marco.onmyway.model;

import java.util.Calendar;

/**
 * Created by Marco on 14/05/2015.
 */
public class AppointmentBase {
    private String id;
    private String title;
    private Location location;
    private Calendar startDateTime;
    private Calendar trackingDateTime;

    public AppointmentBase(){
        startDateTime = Calendar.getInstance();
        trackingDateTime = Calendar.getInstance();
        location = new Location();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getTrackingDateTime() {
        return trackingDateTime;
    }

    public void setTrackingDateTime(Calendar dateTime) {
        this.trackingDateTime = dateTime;
    }

    public Calendar getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Calendar dateTime) {
        this.startDateTime = dateTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
