package com.example.weatherreportspring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

@SpringBootApplication
public class WeatherReportSpringApplication {

    private static final Logger log = LoggerFactory.getLogger(WeatherReportSpringApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WeatherReportSpringApplication.class, args);
    }


    @Bean
    public ApplicationRunner run() {

        return args -> {
            try {
                String fileName = "config.properties"; // file name for API_KEY
                Properties properties = new Properties();
                properties.load(new FileInputStream(fileName));
                Scanner scnr  = new Scanner(System.in);

                String apiKey = properties.getProperty("API_KEY");

                if (apiKey == null || apiKey.isEmpty() || apiKey.equals("your_api_key_here")) {
                    log.error("Please set a valid API_KEY in config.properties");
                    System.out.println("Please enter a valid API Key:");
                    apiKey = scnr.nextLine();
                }
                User currentUser = new User();

                System.out.println("---Welcome to the Weather Report Program---");

                System.out.println("Enter location:");
                currentUser.setLocation(scnr.nextLine());

                String url = "https://api.openweathermap.org/data/2.5/weather?q=" + currentUser.getLocation() + "&appid=" + apiKey + "&units=metric";

                RestClient restClient = RestClient.create();

                log.info("Fetching weather data for {}...", currentUser.getLocation());

                WeatherResponse weatherResponse = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(WeatherResponse.class);
                log.info("Weather Report Response: {}", weatherResponse);

                assert weatherResponse != null;
                Date currentDate = new Date(weatherResponse.getDt() * 1000);
                Date sunrise = new Date(weatherResponse.getSys().getSunrise() * 1000);
                Date sunset = new Date(weatherResponse.getSys().getSunset() * 1000);

                System.out.println("Current Weather for " + weatherResponse.getName());

                System.out.println("---" + currentDate + "---");

                System.out.println("Temperature: " + weatherResponse.getMain().getTemp() + "°");
                System.out.println("Sunrise: " + sunrise);
                System.out.println("Sunset: " + sunset);

            } catch (IOException e) {
                log.error("Failed to read {}: {}", "config.properties", e.getMessage());
            } catch (Exception e) {
                log.error("Error making API request: {}", e.getMessage());
            }
        };
    }
}


