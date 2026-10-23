package com.example.weatherreportspring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(OutputCaptureExtension.class)
class ControllerTest {

    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";

    private MockRestServiceServer server;
    private Controller controller;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        controller = new Controller(builder);
        ReflectionTestUtils.setField(controller, "apiKey", "test-key");
    }

    private ResponseEntity<WeatherResponse> getWeatherWithApiKey(String apiKey) {
        ReflectionTestUtils.setField(controller, "apiKey", apiKey);
        return controller.getWeather("London");
    }

    @Test
    void returnsServerErrorWhenApiKeyIsNull() {
        ResponseEntity<WeatherResponse> response = getWeatherWithApiKey(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
        server.verify();
    }

    @Test
    void returnsServerErrorWhenApiKeyIsEmpty() {
        ResponseEntity<WeatherResponse> response = getWeatherWithApiKey("");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
        server.verify();
    }

    @Test
    void returnsServerErrorWhenApiKeyIsPlaceholder() {
        ResponseEntity<WeatherResponse> response = getWeatherWithApiKey("your_api_key_here");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNull();
        server.verify();
    }

    @Test
    void returnsWeatherWhenApiCallSucceeds() {
        server.expect(requestTo(API_URL + "?q=London&appid=test-key&units=metric"))
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

    @Test
    void returnsBadRequestWhenLocationIsNotFound() {
        server.expect(requestTo(API_URL + "?q=Nowhereville&appid=test-key&units=metric"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{ \"cod\": \"404\", \"message\": \"city not found\" }"));

        ResponseEntity<WeatherResponse> response = controller.getWeather("Nowhereville");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
        server.verify();
    }

    @Test
    void returnsBadRequestWhenApiKeyIsRejected() {
        server.expect(requestTo(API_URL + "?q=London&appid=test-key&units=metric"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{ \"cod\": 401, \"message\": \"Invalid API key\" }"));

        ResponseEntity<WeatherResponse> response = controller.getWeather("London");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
        server.verify();
    }

    @Test
    void doesNotLogApiKeyWhenRequestFails(CapturedOutput output) {
        server.expect(requestTo(API_URL + "?q=London&appid=test-key&units=metric"))
                .andRespond(request -> {
                    throw new IOException("Connection refused");
                });

        ResponseEntity<WeatherResponse> response = controller.getWeather("London");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(output).contains("OpenWeatherMap request failed");
        assertThat(output).doesNotContain("test-key");
        server.verify();
    }

    @Test
    void doesNotLogApiKeyWhenApiReturnsError(CapturedOutput output) {
        server.expect(requestTo(API_URL + "?q=London&appid=test-key&units=metric"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        controller.getWeather("London");

        assertThat(output).contains("OpenWeatherMap returned 401");
        assertThat(output).doesNotContain("test-key");
        server.verify();
    }

    @Test
    void encodesLocationInRequestUrl() {
        server.expect(requestTo(API_URL + "?q=New%20York&appid=test-key&units=metric"))
                .andRespond(withSuccess("{ \"name\": \"New York\" }", MediaType.APPLICATION_JSON));

        ResponseEntity<WeatherResponse> response = controller.getWeather("New York");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("New York");
        server.verify();
    }
}
