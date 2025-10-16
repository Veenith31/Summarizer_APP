package com.summarizer.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiApiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public String getSummary(String text) {
        RestTemplate restTemplate = new RestTemplate();

        // Build request body
        Map<String, Object> message = new HashMap<>();
        message.put("parts", Collections.singletonList(Collections.singletonMap("text", "Summarize briefly:\n" + text)));
        message.put("role", "user");

        Map<String, Object> payload = new HashMap<>();
        payload.put("contents", Collections.singletonList(message));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        // Fix the URL properly by adding ?key=
        String finalUrl = GEMINI_API_URL + "?key=" + apiKey;

        ResponseEntity<Map> response = restTemplate.exchange(
                finalUrl, HttpMethod.POST, request, Map.class);

        try {
            Map content = (Map) ((Map) ((List) response.getBody().get("candidates")).get(0)).get("content");
            Map part = (Map) ((List) content.get("parts")).get(0);
            return part.get("text").toString();
        } catch (Exception e) {
            return "Failed to summarize: " + e.getMessage();
        }
    }

    // ✅ 1. Extract Keywords
    public List<String> extractKeywords(String text) {
        String prompt = "Extract the top 5-10 keywords from this text. Return only a comma-separated list:\n\n" + text;
        String responseText = callGeminiTextOnly(prompt);

        if (responseText == null || responseText.isEmpty()) return Collections.emptyList();

        return Arrays.stream(responseText.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    // ✅ 2. Generate Related Questions
    public List<String> generateRelatedQueries(String summaryText) {
        String prompt = "Here is a summary:\n" + summaryText +
                "\n\nGenerate 5 relevant user questions to explore the key concepts more deeply.";
        String responseText = callGeminiTextOnly(prompt);

        if (responseText == null || responseText.isEmpty()) return Collections.emptyList();

        return Arrays.stream(responseText.split("\n"))
                .map(line -> line.replaceAll("^[0-9]+[.)\\-\\s]*", "").trim())
                .filter(s -> !s.isEmpty())
                .toList();
    }

    // ✅ 3. Reusable method for plain text Gemini calls
    private String callGeminiTextOnly(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> message = new HashMap<>();
        message.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));
        message.put("role", "user");

        Map<String, Object> payload = new HashMap<>();
        payload.put("contents", Collections.singletonList(message));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String finalUrl = GEMINI_API_URL + "?key=" + apiKey;

        try {
            ResponseEntity<Map> response = restTemplate.exchange(finalUrl, HttpMethod.POST, request, Map.class);
            Map content = (Map) ((Map) ((List) response.getBody().get("candidates")).get(0)).get("content");
            Map part = (Map) ((List) content.get("parts")).get(0);
            return part.get("text").toString();
        } catch (Exception e) {
            return null;
        }
    }
}

//    public String extractKeywords(String text) {
//        return callGemini("Extract the most important keywords from the following text:\n" + text);
//    }
//
//    public String generateRelatedQueries(String summary) {
//        return callGemini("Based on this summary, generate 3 user-friendly related search queries:\n" + summary);
//    }
//
//    private String callGemini(String prompt) {
//        RestTemplate restTemplate = new RestTemplate();
//
//        Map<String, Object> message = new HashMap<>();
//        message.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));
//        message.put("role", "user");
//
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("contents", Collections.singletonList(message));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
//        String finalUrl = GEMINI_API_URL + "?key=" + apiKey;
//
//        ResponseEntity<Map> response = restTemplate.exchange(finalUrl, HttpMethod.POST, request, Map.class);
//
//        try {
//            Map content = (Map) ((Map) ((List) response.getBody().get("candidates")).get(0)).get("content");
//            Map part = (Map) ((List) content.get("parts")).get(0);
//            return part.get("text").toString();
//        } catch (Exception e) {
//            return "Failed to extract: " + e.getMessage();
//        }
//    }



