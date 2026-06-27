package com.example.weatherreportspring.json;

import org.springframework.stereotype.Component;

@Component
public class CloudsJSON {

    private String all;

    public String getAll() {
        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }

    @Override
    public String toString() {
        return getAll();
    }
}