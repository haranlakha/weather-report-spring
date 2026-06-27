package com.example.weatherreportspring.json;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class MainJSON {

    private double temp;
    private double feels_like;
    private double temp_main;
    private double temp_min;
    private double temp_max;
    private Integer pressure;
    private Integer humidity;
    private Integer sea_level;
    private Integer grnd_level;

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(double feels_like) {
        this.feels_like = feels_like;
    }

    public double getTemp_main() {
        return temp_main;
    }

    public void setTemp_main(double temp_main) {
        this.temp_main = temp_main;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(double temp_min) {
        this.temp_min = temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(double temp_max) {
        this.temp_max = temp_max;
    }

    public Integer getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getSea_level() {
        return sea_level;
    }

    public void setSea_level(Integer sea_level) {
        this.sea_level = sea_level;
    }

    public Integer getGrnd_level() {
        return grnd_level;
    }

    public void setGrnd_level(Integer grnd_level) {
        this.grnd_level = grnd_level;
    }

    @Override
    public String toString() {
        return getTemp() + ", " + getFeels_like() + ", " + getTemp_main() + ", " + getTemp_min() + ", " + getTemp_max() +
                ", " + getPressure() + ", " + getHumidity() + ", " + getSea_level() + ", " + getGrnd_level();
    }
}
