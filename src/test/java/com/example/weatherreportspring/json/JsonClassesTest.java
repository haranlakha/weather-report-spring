package com.example.weatherreportspring.json;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonClassesTest {

    @Test
    void coordToStringShowsLonThenLat() {
        CoordJSON coord = new CoordJSON();
        coord.setLon(-0.1257);
        coord.setLat(51.5085);

        assertThat(coord.toString()).isEqualTo("-0.1257, 51.5085");
    }

    @Test
    void weatherToStringShowsAllFields() {
        WeatherJSON weather = new WeatherJSON();
        weather.setId(800);
        weather.setMain("Clear");
        weather.setDescription("clear sky");
        weather.setIcon("01d");

        assertThat(weather.toString()).isEqualTo("800, Clear, clear sky, 01d");
    }

    @Test
    void windToStringShowsSpeedDegAndGust() {
        WindJSON wind = new WindJSON();
        wind.setSpeed(3.5);
        wind.setDeg(270);
        wind.setGust(6.2);

        assertThat(wind.toString()).isEqualTo("3.5 270 6.2");
    }

    @Test
    void cloudsToStringShowsCoverage() {
        CloudsJSON clouds = new CloudsJSON();
        clouds.setAll("40");

        assertThat(clouds.toString()).isEqualTo("40");
    }

    @Test
    void sysToStringShowsCountrySunriseAndSunset() {
        SysJSON sys = new SysJSON();
        sys.setCountry("GB");
        sys.setSunrise(1700000000L);
        sys.setSunset(1700030000L);

        assertThat(sys.toString()).isEqualTo("GB,1700000000,1700030000");
    }

    @Test
    void mainToStringShowsAllFieldsInOrder() {
        MainJSON main = new MainJSON();
        main.setTemp(13.0);
        main.setFeels_like(12.1);
        main.setTemp_main(0.0);
        main.setTemp_min(11.5);
        main.setTemp_max(14.2);
        main.setPressure(1015);
        main.setHumidity(70);
        main.setSea_level(1015);
        main.setGrnd_level(1010);

        assertThat(main.toString())
                .isEqualTo("13.0, 12.1, 0.0, 11.5, 14.2, 1015, 70, 1015, 1010");
    }
}
