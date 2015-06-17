package com.onmyway.responses;

import com.onmyway.model.Notification;
import com.onmyway.model.User;
import com.onmyway.model.UserStatus;

import java.util.ArrayList;

/**
 * Created by Marco on 20/05/2015.
 */
public class SyncResponse extends BaseRestResponse {
    public SyncResponseData Data;

    public class SyncResponseData
    {
        public String AppointmentId;
        public ArrayList<UserStatus> AttendeeStates;
        public ArrayList<Notification> Notifications;
    }
}
