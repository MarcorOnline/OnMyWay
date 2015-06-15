package com.onmyway.requests;

import com.onmyway.model.Location;
import com.onmyway.model.User;
import com.onmyway.model.UserStatus;

import java.util.ArrayList;

/**
 * Created by Marco on 15/06/2015.
 */
public class UploadAppointmentRequest {
    public String title;
    public String authorPhoneNumber;
    public String formattedStartDateTime;
    public String formattedTrackingDateTime;
    public Location location;
    public ArrayList<User> validUsers;
}
