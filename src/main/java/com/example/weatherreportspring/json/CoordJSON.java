package com.example.weatherreportspring.json;

import org.springframework.stereotype.Component;

@Component
public class CoordJSON {

    private Double lon;
    private Double lat;

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    @Override
    public String toString(){
        return getLon() + ", " + getLat();
    }
}
