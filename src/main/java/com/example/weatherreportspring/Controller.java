package com.example.weatherreportspring;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@PropertySource("file:config.properties")
public class Controller {
    @Value("${API_KEY:}")
    private String apiKey;
    @GetMapping("/api/weather")
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam String location) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_api_key_here")) {
            return ResponseEntity.internalServerError().body(null);
        }
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey + "&units=metric";

        try {
            RestClient restClient = RestClient.create();
            WeatherResponse response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(WeatherResponse.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
}