package com.onmyway.model;

import com.google.android.gms.location.places.Place;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by Marco on 14/05/2015.
 */
public class AppointmentBase implements Serializable {
    private String id;
    private String title;
    private Location location;
    private Calendar startDateTime;
    private Calendar trackingDateTime;
    private String authorPhoneNumber;

    private String formattedStartDateTime;
    private String formattedTrackingDateTime;

    public AppointmentBase(){
        startDateTime = Calendar.getInstance();
        trackingDateTime = Calendar.getInstance();
        location = new Location();
        title = "";
        authorPhoneNumber = "";
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
        DateTime start = ISODateTimeFormat.dateTimeParser().parseDateTime(formattedStartDateTime);
        DateTime tracking = ISODateTimeFormat.dateTimeParser().parseDateTime(formattedTrackingDateTime);

        startDateTime = start.toCalendar(null);
        trackingDateTime = tracking.toCalendar(null);
    }

    public void calendarsToStrings()
    {
        DateTimeFormatter formatter = ISODateTimeFormat.dateTimeNoMillis();

        formattedStartDateTime = formatter.print(new DateTime(startDateTime));
        formattedTrackingDateTime = formatter.print(new DateTime(trackingDateTime));
    }

    public String getAuthorPhoneNumber() {
        return authorPhoneNumber;
    }

    public String getFormattedStartDateTime() {
        calendarsToStrings();
        return formattedStartDateTime;
    }

    public String getFormattedTrackingDateTime() {
        calendarsToStrings();
        return formattedTrackingDateTime;
    }

    public void setStartDateTime(Calendar startDate, Calendar startTime)
    {
        startTime.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
        startDateTime = startTime;
    }

    public void setTrackingDateTime(Calendar trackDate, Calendar trackTime)
    {
        trackTime.set(trackDate.get(Calendar.YEAR), trackDate.get(Calendar.MONTH), trackDate.get(Calendar.DAY_OF_MONTH));
        trackingDateTime = trackTime;
    }

    public static class TrackingTimeComparator implements Comparator<AppointmentBase> {

        @Override
        public int compare(AppointmentBase lhs, AppointmentBase rhs) {
            return lhs.getTrackingDateTime().compareTo(rhs.getTrackingDateTime());
        }
    }
}
