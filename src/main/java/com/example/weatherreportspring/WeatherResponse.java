package com.example.weatherreportspring;

import com.example.weatherreportspring.json.*;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;

@JsonIgnoreProperties(ignoreUnknown = true)
@Component
public class WeatherResponse {

//Class for corresponding to returned Java Response


    /*
    EXAMPLE API RESPONSE
    {
      "coord": {
        "lon": -2.0833,
        "lat": 52.5
      },
      "weather": [
        {
          "id": 501,
          "main": "Rain",
          "description": "moderate rain",
          "icon": "10d"
        }
      ],
      "base": "stations",
      "main": {
        "temp": 282.79,
        "feels_like": 282.1,
        "temp_min": 282.42,
        "temp_max": 283.12,
        "pressure": 1004,
        "humidity": 80,
        "sea_level": 1004,
        "grnd_level": 989
      },
      "visibility": 10000,
      "wind": {
        "speed": 1.79,
        "deg": 83,
        "gust": 5.36
      },
      "rain": {
        "1h": 2.61
      },
      "clouds": {
        "all": 100
      },
      "dt": 1773315172,
      "sys": {
        "type": 2,
        "id": 2089968,
        "country": "GB",
        "sunrise": 1773297014,
        "sunset": 1773338773
      },
      "timezone": 0,
      "id": 2650839,
      "name": "Dudley",
      "cod": 200
    }
     */




    //single variables
    private String base;
    private Integer visibility;
    private long dt;
    private Integer timezone;
    private Integer id;
    private String name;
    private Integer cod;

    //Separate inner classes
    private CoordJSON coord;
    private WeatherJSON[] weather;
    private MainJSON main;
    private WindJSON wind;
    private CloudsJSON clouds;
    private SysJSON sys;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public Integer getTimezone() {
        return timezone;
    }

    public void setTimezone(Integer timezone) {
        this.timezone = timezone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public CoordJSON getCoord() {
        return coord;
    }

    public void setCoord(CoordJSON coord) {
        this.coord = coord;
    }

    public WeatherJSON[] getWeather() {
        return weather;
    }

    public void setWeather(WeatherJSON[] weather) {
        this.weather = weather;
    }

    public MainJSON getMain() {
        return main;
    }

    public void setMain(MainJSON main) {
        this.main = main;
    }

    public WindJSON getWind() {
        return wind;
    }

    public void setWind(WindJSON wind) {
        this.wind = wind;
    }

    public CloudsJSON getClouds() {
        return clouds;
    }

    public void setClouds(CloudsJSON clouds) {
        this.clouds = clouds;
    }

    public SysJSON getSys() {
        return sys;
    }

    public void setSys(SysJSON sys) {
        this.sys = sys;
    }

    @Override
    public String toString() {
        return getBase() + ", " + getVisibility() + ", " + getDt() + "," + getTimezone() + ", " + getId() +
                getName() + ", " + getCod() + ", " + getCoord() + ", " + Arrays.toString(getWeather()) + ", " + getMain() +
                ", " + getWind() + ", " + getClouds() + ", " + getSys();


    }
}
