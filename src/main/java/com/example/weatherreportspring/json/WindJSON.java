package com.example.weatherreportspring.json;

import org.springframework.stereotype.Component;

@Component
public class WindJSON {

    private double speed;
    private Integer deg;
    private double gust;

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Integer getDeg() {
        return deg;
    }

    public void setDeg(Integer deg) {
        this.deg = deg;
    }

    public double getGust() {
        return gust;
    }

    public void setGust(double gust) {
        this.gust = gust;
    }

    @Override
    public String toString() {
        return getSpeed() + " " + getDeg() + " " + getGust();
    }
}