package com.example.weatherreportspring;

import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;

@SpringBootApplication
public class WeatherReportSpringApplication {

    private static final Logger log = LoggerFactory.getLogger(WeatherReportSpringApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WeatherReportSpringApplication.class, args);
    }


    public ApplicationRunner run(RestClient.Builder builder) throws IOException {


        RestClient restClient = builder.baseUrl("http://localhost:8080").build();

        User currentUser = new User();

        String fileName = "config.properties"; //file name for API_KEY

        Properties properties = new Properties();
        properties.load(new FileInputStream(fileName));

        String apiKey = properties.getProperty("API_KEY");


        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + currentUser.getLocation() + "&appid=" + apiKey + "&units=metric";


        return args -> {
            WeatherResponse weather = restClient
                    .get().uri(url)
                    .retrieve()
                    .body(WeatherResponse.class);
            assert weather != null;
            log.info(weather.toString());
        };
    }
}


