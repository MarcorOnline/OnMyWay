package com.marco.onmyway;

import com.marco.onmyway.model.*;

import java.util.ArrayList;

/**
 * Created by Marco on 15/05/2015.
 */
public class ServiceGateway {

    private static void CheckResponse(Object response)
    {
        //TODO check object != null && Error string
    }

    public static User Login(String phoneNumber)
    {
        //TODO
        return new User();
    }

    public static ArrayList<AppointmentBase> GetAppointmentsPreview(String userId)
    {
        //TODO
        return new ArrayList<>();
    }

    public static AppointmentBase UploadAppointment(String userId, AppointmentBase appointment)
    {
        //TODO
        return new AppointmentBase();
    }

    public static boolean RemoveAppointment(String userId, String appointmentId)
    {
        //TODO
        return false;
    }

    public static Appointment GetFullAppointment(String appointmentId)
    {
        //TODO
        return new Appointment();
    }

    public static ArrayList<UserStatus> GetUsersStatus(String appointmentId)
    {
        //TODO
        return new ArrayList<>();
    }

    public static boolean UpdateUserStatus(String userId, String status)
    {
        //TODO
        return false;
    }
}
