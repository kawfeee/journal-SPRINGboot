package com.journal.journalApp.service;

import com.journal.journalApp.api.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${weather.api.key:}")
    private String apiKey;

    private static final String API = "https://api.weatherstack.com/current?access_key=API_KEY&query=CITY";

    @Autowired
    private RestTemplate restTemplate;

    public WeatherResponse getWeather(String city){
        if(apiKey == null || apiKey.isBlank()){
            // no API key configured, skip external call
            return null;
        }
        try {
            String finalApi = API.replace("API_KEY", apiKey).replace("CITY", city);
            ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalApi, HttpMethod.GET, null, WeatherResponse.class);
            return response.getBody();
        } catch (RestClientException e) {
            // Log and return null so controller can return greeting without weather
            return null;
        }
    }
}
