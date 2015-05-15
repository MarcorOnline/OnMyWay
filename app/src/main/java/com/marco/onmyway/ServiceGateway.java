package com.marco.onmyway;

import com.marco.onmyway.model.*;

import java.util.ArrayList;

/**
 * Created by Marco on 15/05/2015.
 */

// onmywayapp.azurewebsites.net
public class ServiceGateway {

    private static void CheckResponse(Object response)
    {
        //TODO check object != null && Error string
    }

    // api/users/login
    public static User Login(String phoneNumber)
    {
        //TODO
        return new User();
    }

    // api/appointments/base/get
    public static ArrayList<AppointmentBase> GetAppointmentsPreview(String phoneNumber)
    {
        //TODO
        return new ArrayList<>();
    }

    // api/appointments/add
    public static Appointment UploadAppointment(String phoneNumber, Appointment appointment)
    {
        //TODO
        return new Appointment();
    }

    // api/appointments/delete
    public static boolean RemoveAppointment(String phoneNumber, String appointmentId)
    {
        //TODO
        return false;
    }

    // api/appointments/get
    public static Appointment GetFullAppointment(String appointmentId)
    {
        //TODO
        return new Appointment();
    }

    // api/appointments/users/status
    public static ArrayList<UserStatus> GetUsersStatus(String appointmentId)
    {
        //TODO
        return new ArrayList<>();
    }

    // api/users/status
    public static boolean UpdateUserStatus(String phoneNumber, String status)
    {
        //TODO
        return false;
    }
}
