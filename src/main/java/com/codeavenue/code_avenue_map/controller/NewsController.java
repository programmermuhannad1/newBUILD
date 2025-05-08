package com.codeavenue.code_avenue_map.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Value("${newsapi.apikey}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public NewsController(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    @GetMapping("/api/news")
    public ResponseEntity<JsonNode> getNews(@RequestParam String category) {
        try {
            String url = "https://newsapi.org/v2/top-headlines?country=us&category=" + category + "&apiKey=" + apiKey;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode json = objectMapper.readTree(response);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            logger.error("Error fetching news:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}