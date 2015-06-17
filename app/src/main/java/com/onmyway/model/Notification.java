package com.onmyway.model;

/**
 * Created by Marco on 08/06/2015.
 */
public class Notification {
    public static final int TYPE_None = 0;
    public static final int TYPE_Urgent = 1;
    public static final int TYPE_VeryUrgent = 2;
    public static final int TYPE_Arrived = 3;

    public String title;
    public String content;
    public String subjectPhoneNumber;
    public int type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubjectPhoneNumber() {
        return subjectPhoneNumber;
    }

    public void setSubjectPhoneNumber(String subjectPhoneNumber) {
        this.subjectPhoneNumber = subjectPhoneNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
