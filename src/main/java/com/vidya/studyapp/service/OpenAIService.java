package com.vidya.studyapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OpenAIService {

    @Value("${huggingface.api.token}")
    private String apiToken;

    private final String SUMMARY_ENDPOINT = "https://api-inference.huggingface.co/models/facebook/bart-large-cnn";

    private final RestTemplate restTemplate = new RestTemplate();

    public String summarizeText(String inputText) {
        try {
            inputText = inputText.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
            inputText = inputText.replaceAll("[\\n\\r\\t]+", " ");

            String[] words = inputText.trim().split("\\s+");
            if (words.length > 600) {
                inputText = Arrays.stream(words)
                        .limit(600)
                        .collect(Collectors.joining(" "));
            }

            String escapedText = inputText.replace("\\", "\\\\").replace("\"", "\\\"");

            String requestBody = "{\"inputs\": \"" + escapedText + "\"}";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(apiToken);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Object[]> response = restTemplate.exchange(
                    SUMMARY_ENDPOINT,
                    HttpMethod.POST,
                    request,
                    Object[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().length > 0) {
                Map result = (Map) response.getBody()[0];
                return result.get("summary_text").toString();
            } else {
                return "Error during summarization: No output received.";
            }

        } catch (Exception e) {
            return "Error during summarization: " + e.getMessage();
        }
    }

    public String translateWithHF(String englishText, String langCode) {
        String modelUrl = switch (langCode.toLowerCase()) {
            case "hi" -> "https://api-inference.huggingface.co/models/Helsinki-NLP/opus-mt-en-hi";
            case "mr" -> "https://api-inference.huggingface.co/models/Helsinki-NLP/opus-mt-en-mr";
            case "gu" -> "https://api-inference.huggingface.co/models/Helsinki-NLP/opus-mt-en-gu";
            default -> null;
        };

        if (modelUrl == null) return englishText;

        try {
            // Escape characters for JSON
            String escapedText = englishText.replace("\\", "\\\\").replace("\"", "\\\"");

            String requestBody = "{\"inputs\": \"" + escapedText + "\"}";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON)); // ✅ Fix for Accept header
            headers.setBearerAuth(apiToken);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Object[]> response = restTemplate.exchange(
                    modelUrl,
                    HttpMethod.POST,
                    request,
                    Object[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().length > 0) {
                Map result = (Map) response.getBody()[0];
                return result.get("translation_text").toString();
            }

            return "Translation failed: No response body";

        } catch (Exception e) {
            return "Translation failed: " + e.getMessage();
        }
    }
}
