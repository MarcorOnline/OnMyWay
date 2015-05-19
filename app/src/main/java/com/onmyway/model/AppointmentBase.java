package com.onmyway.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

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

    private String formattedstartDateTime;
    private String formattedtrackingDateTime;

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

    public void calendarsFromStrings()
    {
        DateTime start = ISODateTimeFormat.dateTimeParser().parseDateTime(formattedstartDateTime);
        DateTime tracking = ISODateTimeFormat.dateTimeParser().parseDateTime(formattedtrackingDateTime);

        startDateTime = start.toCalendar(null);
        trackingDateTime = tracking.toCalendar(null);
    }

    public void calendarsToStrings()
    {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();

        formattedstartDateTime = formatter.print(new DateTime(startDateTime));
        formattedtrackingDateTime = formatter.print(new DateTime(trackingDateTime));
    }
}
