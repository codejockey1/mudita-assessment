package com.mudita.assessment.service;

import com.mudita.assessment.model.TaskAnalysisResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskAnalysisServiceTest {

    @Mock
    private OpenAIService openAIService;

    @InjectMocks
    private TaskAnalysisService taskAnalysisService;

    private static final String TEST_PROMPT_TEMPLATE = "Please analyze these tasks and optimize their order.\n" +
        "Provide a comical but accurate and concise summary explaining why you chose this order.\n\n" +
        "An example of a good summary:\n" +
        "\"We think preparing for the Jessica meeting and helping your daughter with the science project are both tasks that can't be skipped. " +
        "We also assume since it's a school day that daughter won't be home until late afternoon. " +
        "We also bundled the two outdoor activities together. As such, we recommend something like this:\n\n" +
        "11AM: Prepare for meeting with Jessica at work\n" +
        "1PM: Go for a 20 minute run\n" +
        "2PM: Mow the lawn\n" +
        "4PM: Help daughter with Science project\"\n\n" +
        "Then, create a detailed prompt for DALL-E to generate an image that visualizes the optimized task sequence.\n" +
        "The prompt should be clear and specific to help generate a funny and engaging visual representation of the tasks to be completed " +
        "using the style of a cartoon, SNL sketch, the muppets, or a popular sitcom.\n" +
        "Format the DALL-E prompt in a section starting with '%s' on a new line.\n\n" +
        "Tasks to analyze:\n%s";

    private static final String TEST_DALLE_MARKER = "DALL-E PROMPT:";
    private static final String TEST_FALLBACK_PROMPT = "A colorful and engaging visualization of task management and organization, showing tasks being arranged in an optimal sequence";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(taskAnalysisService, "promptTemplate", TEST_PROMPT_TEMPLATE);
        ReflectionTestUtils.setField(taskAnalysisService, "dallePromptMarker", TEST_DALLE_MARKER);
        ReflectionTestUtils.setField(taskAnalysisService, "fallbackDallePrompt", TEST_FALLBACK_PROMPT);
    }

    @Test
    void getTaskAnalysis_ValidTasks_ReturnsCompleteAnalysis() {
        // Arrange
        List<String> tasks = Arrays.asList("Task 1", "Task 2");
        String analysis = "Analysis of tasks";
        String dallePrompt = "A beautiful task management illustration";
        String chatResponse = analysis + "\n\n" + TEST_DALLE_MARKER + " " + dallePrompt;
        String imageUrl = "https://example.com/image.jpg";

        when(openAIService.sendPrompt(anyString())).thenReturn(chatResponse);
        when(openAIService.generateImage(dallePrompt)).thenReturn(imageUrl);

        // Act
        TaskAnalysisResponse response = taskAnalysisService.getTaskAnalysis(tasks);

        // Assert
        assertNotNull(response);
        assertEquals(analysis, response.getAnalysis());
        assertEquals(imageUrl, response.getImageUrl());
        verify(openAIService).sendPrompt(anyString());
        verify(openAIService).generateImage(dallePrompt);
    }

    @Test
    void getTaskAnalysis_NoDallePrompt_ReturnsFallbackPrompt() {
        // Arrange
        List<String> tasks = Arrays.asList("Task 1", "Task 2");
        String chatResponse = "Analysis of tasks without DALL-E prompt";
        String imageUrl = "https://example.com/image.jpg";

        when(openAIService.sendPrompt(anyString())).thenReturn(chatResponse);
        when(openAIService.generateImage(TEST_FALLBACK_PROMPT)).thenReturn(imageUrl);

        // Act
        TaskAnalysisResponse response = taskAnalysisService.getTaskAnalysis(tasks);

        // Assert
        assertNotNull(response);
        assertEquals(chatResponse, response.getAnalysis());
        assertEquals(imageUrl, response.getImageUrl());
        verify(openAIService).sendPrompt(anyString());
        verify(openAIService).generateImage(TEST_FALLBACK_PROMPT);
    }

    @Test
    void getTaskAnalysis_EmptyTasks_ThrowsException() {
        // Arrange
        List<String> tasks = Arrays.asList();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskAnalysisService.getTaskAnalysis(tasks));
        assertEquals("Task list cannot be empty", exception.getMessage());
        verify(openAIService, never()).sendPrompt(anyString());
        verify(openAIService, never()).generateImage(anyString());
    }

    @Test
    void getTaskAnalysis_NullTasks_ThrowsException() {
        // Arrange
        List<String> tasks = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskAnalysisService.getTaskAnalysis(tasks));
        assertEquals("Task list cannot be null", exception.getMessage());
        verify(openAIService, never()).sendPrompt(anyString());
        verify(openAIService, never()).generateImage(anyString());
    }
} 