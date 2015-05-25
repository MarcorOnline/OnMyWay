package com.onmyway.model;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

/**
 * Created by Marco on 14/05/2015.
 */
public class Appointment extends AppointmentBase
{
    private ArrayList<User> validUsers;         //users registered to OnMyWay
    private ArrayList<User> invalidUsers;       //users that are not registered to OnMyWay

    public Appointment(){
        super();
        validUsers = new ArrayList<>();
        invalidUsers = new ArrayList<>();
    }

    public ArrayList<User> getValidUsers() {
        return validUsers;
    }

    public void setValidUsers(ArrayList<User> users) {
        this.validUsers = users;
    }

    public ArrayList<User> getInvalidUsers() {
        return invalidUsers;
    }

    public void setInvalidUsers(ArrayList<User> users)
    {
        this.invalidUsers = users;
    }
}
