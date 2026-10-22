package com.example.weatherreportspring;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class WeatherResponseTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    // Example response from the OpenWeatherMap current weather API
    private static final String EXAMPLE_JSON = """
            {
              "coord": { "lon": -2.0833, "lat": 52.5 },
              "weather": [
                { "id": 501, "main": "Rain", "description": "moderate rain", "icon": "10d" }
              ],
              "base": "stations",
              "main": {
                "temp": 9.64,
                "feels_like": 8.95,
                "temp_min": 9.27,
                "temp_max": 9.97,
                "pressure": 1004,
                "humidity": 80,
                "sea_level": 1004,
                "grnd_level": 989
              },
              "visibility": 10000,
              "wind": { "speed": 1.79, "deg": 83, "gust": 5.36 },
              "rain": { "1h": 2.61 },
              "clouds": { "all": 100 },
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
            """;

    @Test
    void mapsTopLevelFields() {
        WeatherResponse response = mapper.readValue(EXAMPLE_JSON, WeatherResponse.class);

        assertThat(response.getBase()).isEqualTo("stations");
        assertThat(response.getVisibility()).isEqualTo(10000);
        assertThat(response.getDt()).isEqualTo(1773315172L);
        assertThat(response.getTimezone()).isEqualTo(0);
        assertThat(response.getId()).isEqualTo(2650839);
        assertThat(response.getName()).isEqualTo("Dudley");
        assertThat(response.getCod()).isEqualTo(200);
    }

    @Test
    void mapsNestedObjects() {
        WeatherResponse response = mapper.readValue(EXAMPLE_JSON, WeatherResponse.class);

        assertThat(response.getCoord().getLon()).isEqualTo(-2.0833);
        assertThat(response.getCoord().getLat()).isEqualTo(52.5);

        assertThat(response.getWeather()).hasSize(1);
        assertThat(response.getWeather()[0].getId()).isEqualTo(501);
        assertThat(response.getWeather()[0].getMain()).isEqualTo("Rain");
        assertThat(response.getWeather()[0].getDescription()).isEqualTo("moderate rain");
        assertThat(response.getWeather()[0].getIcon()).isEqualTo("10d");

        assertThat(response.getMain().getTemp()).isEqualTo(9.64);
        assertThat(response.getMain().getFeels_like()).isEqualTo(8.95);
        assertThat(response.getMain().getTemp_min()).isEqualTo(9.27);
        assertThat(response.getMain().getTemp_max()).isEqualTo(9.97);
        assertThat(response.getMain().getPressure()).isEqualTo(1004);
        assertThat(response.getMain().getHumidity()).isEqualTo(80);
        assertThat(response.getMain().getSea_level()).isEqualTo(1004);
        assertThat(response.getMain().getGrnd_level()).isEqualTo(989);

        assertThat(response.getWind().getSpeed()).isEqualTo(1.79);
        assertThat(response.getWind().getDeg()).isEqualTo(83);
        assertThat(response.getWind().getGust()).isEqualTo(5.36);

        assertThat(response.getClouds().getAll()).isEqualTo("100");

        assertThat(response.getSys().getType()).isEqualTo(2);
        assertThat(response.getSys().getId()).isEqualTo(2089968);
        assertThat(response.getSys().getCountry()).isEqualTo("GB");
        assertThat(response.getSys().getSunrise()).isEqualTo(1773297014L);
        assertThat(response.getSys().getSunset()).isEqualTo(1773338773L);
    }

    @Test
    void ignoresUnknownFields() {
        String json = """
                { "name": "London", "unexpected": { "nested": true } }
                """;

        WeatherResponse response = mapper.readValue(json, WeatherResponse.class);

        assertThat(response.getName()).isEqualTo("London");
    }

    @Test
    void leavesMissingFieldsAsDefaults() {
        WeatherResponse response = mapper.readValue("{}", WeatherResponse.class);

        assertThat(response.getName()).isNull();
        assertThat(response.getMain()).isNull();
        assertThat(response.getWeather()).isNull();
        assertThat(response.getDt()).isZero();
    }
}
