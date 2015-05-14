package com.marco.onmyway.model;

import java.util.ArrayList;

/**
 * Created by Marco on 14/05/2015.
 */
public class GlobalData {
    private static ArrayList<AppointmentBase> appointments = new ArrayList<>();

    private static Appointment currentAppointment;

    public static ArrayList<AppointmentBase> getAppointments() {
        return appointments;
    }

    public static void setAppointments(ArrayList<AppointmentBase> appointments) {
        GlobalData.appointments = appointments;
    }

    public static Appointment getCurrentAppointment() {
        return currentAppointment;
    }

    public static void setCurrentAppointment(Appointment currentAppointment) {
        GlobalData.currentAppointment = currentAppointment;
    }
}
