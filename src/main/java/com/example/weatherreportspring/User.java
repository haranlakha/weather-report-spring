package com.example.weatherreportspring;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


public class User {

    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
