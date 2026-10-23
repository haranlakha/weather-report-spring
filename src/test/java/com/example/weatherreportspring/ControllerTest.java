package com.example.weatherreportspring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(OutputCaptureExtension.class)
class ControllerTest {

    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY = "test-key";

    private RestClient.Builder builder;
    private MockRestServiceServer server;
    private Controller controller;

    @BeforeEach
    void setUp() {
        builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        controller = new Controller(builder, API_KEY);
    }

    // The OpenWeatherMap URL the controller should call for a location
    private static String expectedUrl(String location) {
        return API_URL + "?q=" + UriUtils.encodeQueryParam(location, StandardCharsets.UTF_8)
                + "&appid=" + API_KEY + "&units=metric";
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "your_api_key_here")
    void returnsServerErrorWhenApiKeyIsMissing(String apiKey) {
        Controller controllerWithoutKey = new Controller(builder, apiKey);

        ResponseEntity<WeatherResponse> response = controllerWithoutKey.getWeather("London");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
        server.verify();
    }

    @Test
    void returnsWeatherWhenApiCallSucceeds() {
        server.expect(requestTo(expectedUrl("London")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {
                          "name": "London",
                          "sys": { "country": "GB" },
                          "main": { "temp": 13.2 },
                          "weather": [ { "description": "clear sky" } ]
                        }
                        """, MediaType.APPLICATION_JSON));

        ResponseEntity<WeatherResponse> response = controller.getWeather("London");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("London");
        assertThat(response.getBody().getSys().getCountry()).isEqualTo("GB");
        assertThat(response.getBody().getMain().getTemp()).isEqualTo(13.2);
        assertThat(response.getBody().getWeather()[0].getDescription()).isEqualTo("clear sky");
        server.verify();
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"NOT_FOUND", "BAD_REQUEST"})
    void returnsBadRequestWhenLocationIsInvalid(HttpStatus apiStatus) {
        server.expect(requestTo(expectedUrl("Nowhereville")))
                .andRespond(withStatus(apiStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{ \"cod\": \"" + apiStatus.value() + "\", \"message\": \"city not found\" }"));

        ResponseEntity<WeatherResponse> response = controller.getWeather("Nowhereville");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
        server.verify();
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"UNAUTHORIZED", "TOO_MANY_REQUESTS", "INTERNAL_SERVER_ERROR", "SERVICE_UNAVAILABLE"})
    void returnsBadGatewayWhenApiFails(HttpStatus apiStatus) {
        server.expect(requestTo(expectedUrl("London")))
                .andRespond(withStatus(apiStatus));

        ResponseEntity<WeatherResponse> response = controller.getWeather("London");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNull();
        server.verify();
    }

    @Test
    void returnsBadGatewayAndDoesNotLogApiKeyWhenRequestFails(CapturedOutput output) {
        server.expect(requestTo(expectedUrl("London")))
                .andRespond(request -> {
                    throw new IOException("Connection refused");
                });

        ResponseEntity<WeatherResponse> response = controller.getWeather("London");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(output).contains("OpenWeatherMap request failed");
        assertThat(output).doesNotContain(API_KEY);
        server.verify();
    }

    @Test
    void doesNotLogApiKeyWhenApiReturnsError(CapturedOutput output) {
        server.expect(requestTo(expectedUrl("London")))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        controller.getWeather("London");

        assertThat(output).contains("OpenWeatherMap returned 401");
        assertThat(output).doesNotContain(API_KEY);
        server.verify();
    }

    @Test
    void encodesLocationInRequestUrl() {
        server.expect(requestTo(API_URL + "?q=New%20York&appid=" + API_KEY + "&units=metric"))
                .andRespond(withSuccess("{ \"name\": \"New York\" }", MediaType.APPLICATION_JSON));

        ResponseEntity<WeatherResponse> response = controller.getWeather("New York");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("New York");
        server.verify();
    }
}
