package com.onmyway.model;

/**
 * Created by Marco on 14/05/2015.
 */
public class User extends UserStatus {
    private String name;
    private String avatar;

    public User(){}

    public User(String phoneNumber){
        this.setPhoneNumber(phoneNumber);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
