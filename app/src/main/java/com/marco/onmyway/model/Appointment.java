package com.marco.onmyway.model;

import java.util.ArrayList;

/**
 * Created by Marco on 14/05/2015.
 */
public class Appointment extends AppointmentBase {
    private ArrayList<User> users;

    public Appointment(){
        super();
        users = new ArrayList<>();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
