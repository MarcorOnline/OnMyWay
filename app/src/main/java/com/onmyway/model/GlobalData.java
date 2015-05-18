package com.onmyway.model;

import com.onmyway.utils.ApiCallback;
import com.onmyway.utils.ServiceGateway;

import java.util.ArrayList;

/**
 * Created by Marco on 14/05/2015.
 */
public class GlobalData {
    private static User loggedUser;

    private static ArrayList<AppointmentBase> appointments = new ArrayList<>();

    //GET SET
    public static ArrayList<AppointmentBase> getAppointments() {
        return appointments;
    }

    public static void setAppointments(ArrayList<AppointmentBase> appointments) {
        GlobalData.appointments = appointments;
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    public static void setLoggedUser(User loggedUser) {
        GlobalData.loggedUser = loggedUser;
    }

}
