package com.androsov.groupjournal;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Student implements Serializable {
    public String id;
    public String firstName;
    public String lastName;
    public String secondName;
    public String latitude;
    public String longitude;
    public ArrayList<String> images;
    public Date birthday;
    public String videoUrl;

    public Student(String id, String firstName, String lastName, String secondName, String latitude, String longitude, ArrayList<String> images, Date birthday, String videoUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.secondName = secondName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.images = images;
        this.birthday = birthday;
        this.videoUrl = videoUrl;
    }

    public Student() {}

    @Override
    public String toString() {
        return id;
    }
}