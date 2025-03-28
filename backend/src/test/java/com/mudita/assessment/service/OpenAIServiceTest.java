package com.mudita.assessment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenAIServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OpenAIService openAIService;

    private static final String TEST_API_KEY = "test-api-key";
    private static final String TEST_CHAT_MODEL = "gpt-3.5-turbo";
    private static final String TEST_IMAGE_MODEL = "dall-e-3";
    private static final String TEST_BASE_URL = "https://api.openai.com/v1";

    private static final ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE = 
        new ParameterizedTypeReference<Map<String, Object>>() {};

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(openAIService, "apiKey", TEST_API_KEY);
        ReflectionTestUtils.setField(openAIService, "chatModel", TEST_CHAT_MODEL);
        ReflectionTestUtils.setField(openAIService, "imageModel", TEST_IMAGE_MODEL);
        ReflectionTestUtils.setField(openAIService, "baseUrl", TEST_BASE_URL);
    }

    @Test
    void sendPrompt_ValidRequest_ReturnsResponse() {
        // Arrange
        String prompt = "Test prompt";
        Map<String, Object> mockResponse = createMockChatResponse("Test response");
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(mockResponse);

        when(restTemplate.exchange(
            eq(TEST_BASE_URL + "/chat/completions"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(RESPONSE_TYPE)
        )).thenReturn(responseEntity);

        List<Map<String, Object>> choices = Arrays.asList(createMockChoice("Test response"));
        TypeFactory typeFactory = mock(TypeFactory.class);
        CollectionType listType = mock(CollectionType.class);
        MapType mapType = mock(MapType.class);

        when(objectMapper.getTypeFactory()).thenReturn(typeFactory);
        when(typeFactory.constructCollectionType(List.class, Map.class)).thenReturn(listType);
        when(typeFactory.constructMapType(Map.class, String.class, Object.class)).thenReturn(mapType);
        when(objectMapper.convertValue(any(), eq(listType))).thenReturn(choices);
        when(objectMapper.convertValue(any(), eq(mapType))).thenReturn(choices.get(0).get("message"));

        // Act
        String result = openAIService.sendPrompt(prompt);

        // Assert
        assertEquals("Test response", result);
        verify(restTemplate).exchange(
            eq(TEST_BASE_URL + "/chat/completions"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(RESPONSE_TYPE)
        );
    }

    @Test
    void sendPrompt_InvalidResponse_ReturnsError() {
        // Arrange
        String prompt = "Test prompt";
        when(restTemplate.exchange(
            eq(TEST_BASE_URL + "/chat/completions"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(RESPONSE_TYPE)
        )).thenThrow(new RuntimeException("API Error"));

        // Act
        String result = openAIService.sendPrompt(prompt);

        // Assert
        assertTrue(result.startsWith("Error processing request:"));
        verify(restTemplate).exchange(
            eq(TEST_BASE_URL + "/chat/completions"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(RESPONSE_TYPE)
        );
    }

    @Test
    void generateImage_ValidRequest_ReturnsImageUrl() {
        // Arrange
        String prompt = "Test image prompt";
        Map<String, Object> mockResponse = createMockImageResponse("https://example.com/image.jpg");
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(mockResponse);

        when(restTemplate.exchange(
            eq(TEST_BASE_URL + "/images/generations"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(RESPONSE_TYPE)
        )).thenReturn(responseEntity);

        List<Map<String, Object>> data = Arrays.asList(createMockImageData("https://example.com/image.jpg"));
        TypeFactory typeFactory = mock(TypeFactory.class);
        CollectionType listType = mock(CollectionType.class);

        when(objectMapper.getTypeFactory()).thenReturn(typeFactory);
        when(typeFactory.constructCollectionType(List.class, Map.class)).thenReturn(listType);
        when(objectMapper.convertValue(any(), eq(listType))).thenReturn(data);

        // Act
        String result = openAIService.generateImage(prompt);

        // Assert
        assertEquals("https://example.com/image.jpg", result);
        verify(restTemplate).exchange(
            eq(TEST_BASE_URL + "/images/generations"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(RESPONSE_TYPE)
        );
    }

    @Test
    void generateImage_InvalidResponse_ReturnsNull() {
        // Arrange
        String prompt = "Test image prompt";
        when(restTemplate.exchange(
            eq(TEST_BASE_URL + "/images/generations"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(RESPONSE_TYPE)
        )).thenThrow(new RuntimeException("API Error"));

        // Act
        String result = openAIService.generateImage(prompt);

        // Assert
        assertNull(result);
        verify(restTemplate).exchange(
            eq(TEST_BASE_URL + "/images/generations"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(RESPONSE_TYPE)
        );
    }

    @Test
    void generateImage_EmptyResponse_ReturnsNull() {
        // Arrange
        String prompt = "Test image prompt";
        Map<String, Object> mockResponse = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(mockResponse);

        when(restTemplate.exchange(
            eq(TEST_BASE_URL + "/images/generations"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(RESPONSE_TYPE)
        )).thenReturn(responseEntity);

        // Act
        String result = openAIService.generateImage(prompt);

        // Assert
        assertNull(result);
        verify(restTemplate).exchange(
            eq(TEST_BASE_URL + "/images/generations"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(RESPONSE_TYPE)
        );
    }

    private Map<String, Object> createMockChatResponse(String content) {
        Map<String, Object> response = new HashMap<>();
        response.put("choices", Arrays.asList(createMockChoice(content)));
        return response;
    }

    private Map<String, Object> createMockChoice(String content) {
        Map<String, Object> choice = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("content", content);
        choice.put("message", message);
        return choice;
    }

    private Map<String, Object> createMockImageResponse(String url) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", Arrays.asList(createMockImageData(url)));
        return response;
    }

    private Map<String, Object> createMockImageData(String url) {
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        return data;
    }
} 