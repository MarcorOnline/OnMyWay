package com.onmyway.model;

/**
 * Created by Marco on 08/06/2015.
 */
public class Notification {
    public String title;
    public String content;
    public String subjectPhoneNumber;

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
}
