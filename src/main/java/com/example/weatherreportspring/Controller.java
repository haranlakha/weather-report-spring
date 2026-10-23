package com.example.weatherreportspring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@RestController
@PropertySource(value = "file:config.properties", ignoreResourceNotFound = true)
public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    private final RestClient restClient;
    @Value("${API_KEY:}")
    private String apiKey;

    @Autowired
    public Controller() {
        this(RestClient.builder());
    }

    public Controller(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.openweathermap.org/data/2.5")
                .build();
    }

    @GetMapping("/api/weather")
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam String location) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_api_key_here")) {
            return ResponseEntity.internalServerError().body(null);
        }

        try {
            WeatherResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/weather")
                            .queryParam("q", location)
                            .queryParam("appid", apiKey)
                            .queryParam("units", "metric")
                            .build())
                    .retrieve()
                    .body(WeatherResponse.class);
            return ResponseEntity.ok(response);
        } catch (RestClientResponseException e) {
            log.warn("OpenWeatherMap returned {} for location lookup", e.getStatusCode().value());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            // Don't log the exception itself: its message can contain the request URL, which includes the API key
            log.error("OpenWeatherMap request failed: {}", e.getClass().getName());
            return ResponseEntity.badRequest().body(null);
        }
    }
}
