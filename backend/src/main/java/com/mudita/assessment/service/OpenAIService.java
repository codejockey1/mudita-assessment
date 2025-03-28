package com.mudita.assessment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {
    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.model}")
    private String chatModel;

    @Value("${openai.api.image-model}")
    private String imageModel;

    @Value("${openai.api.base-url}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String CHAT_PATH = "/chat/completions";
    private static final String DALLE_PATH = "/images/generations";

    public OpenAIService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String sendPrompt(String prompt) {
        try {
            HttpEntity<Map<String, Object>> request = createChatRequest(prompt);
            ResponseEntity<Map<String, Object>> response = sendChatRequest(request);
            return extractChatResponse(response);
        } catch (Exception e) {
            return "Error processing request: " + e.getMessage();
        }
    }

    public String generateImage(String prompt) {
        try {
            HttpEntity<Map<String, Object>> request = createDalleRequest(prompt);
            ResponseEntity<Map<String, Object>> response = sendDalleRequest(request);
            return extractImageUrl(response);
        } catch (Exception e) {
            return null;
        }
    }

    private HttpEntity<Map<String, Object>> createChatRequest(String prompt) {
        HttpHeaders headers = createHeaders();
        Map<String, Object> message = createMessage(prompt);
        Map<String, Object> requestBody = createChatRequestBody(message);
        return new HttpEntity<>(requestBody, headers);
    }

    private HttpEntity<Map<String, Object>> createDalleRequest(String prompt) {
        HttpHeaders headers = createHeaders();
        Map<String, Object> requestBody = createDalleRequestBody(prompt);
        return new HttpEntity<>(requestBody, headers);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        return headers;
    }

    private Map<String, Object> createMessage(String prompt) {
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);
        return message;
    }

    private Map<String, Object> createChatRequestBody(Map<String, Object> message) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", chatModel);
        requestBody.put("messages", List.of(message));
        return requestBody;
    }

    private Map<String, Object> createDalleRequestBody(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", imageModel);
        requestBody.put("prompt", prompt);
        requestBody.put("n", 1);
        requestBody.put("size", "1024x1024");
        requestBody.put("quality", "standard");
        return requestBody;
    }

    private ResponseEntity<Map<String, Object>> sendRequest(HttpEntity<Map<String, Object>> request, String path) {
        return restTemplate.exchange(
            baseUrl + path,
            org.springframework.http.HttpMethod.POST,
            request,
            new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
        );
    }

    private ResponseEntity<Map<String, Object>> sendChatRequest(HttpEntity<Map<String, Object>> request) {
        return sendRequest(request, CHAT_PATH);
    }

    private ResponseEntity<Map<String, Object>> sendDalleRequest(HttpEntity<Map<String, Object>> request) {
        return sendRequest(request, DALLE_PATH);
    }

    private String extractChatResponse(ResponseEntity<Map<String, Object>> responseEntity) {
        Map<String, Object> response = responseEntity.getBody();
        if (response == null || !response.containsKey("choices")) {
            return "Unable to process request at this time.";
        }

        List<Map<String, Object>> choices = objectMapper.convertValue(
            response.get("choices"),
            objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
        );

        if (choices.isEmpty()) {
            return "No response received from ChatGPT.";
        }

        Map<String, Object> choice = choices.get(0);
        Map<String, Object> messageResponse = objectMapper.convertValue(
            choice.get("message"),
            objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)
        );

        return (String) messageResponse.get("content");
    }

    private String extractImageUrl(ResponseEntity<Map<String, Object>> responseEntity) {
        Map<String, Object> response = responseEntity.getBody();
        if (response == null || !response.containsKey("data")) {
            return null;
        }

        List<Map<String, Object>> data = objectMapper.convertValue(
            response.get("data"),
            objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
        );

        if (data.isEmpty()) {
            return null;
        }

        return (String) data.get(0).get("url");
    }
} 