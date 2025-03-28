package com.mudita.assessment.controller;

import com.mudita.assessment.model.TaskListRequest;
import com.mudita.assessment.model.TaskAnalysisResponse;
import com.mudita.assessment.service.TaskAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @Mock
    private TaskAnalysisService taskAnalysisService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void analyzeTasks_ValidRequest_ReturnsAnalysisResponse() {
        // Arrange
        List<String> tasks = Arrays.asList("Task 1", "Task 2");
        TaskListRequest request = new TaskListRequest();
        request.setTasks(tasks);
        
        TaskAnalysisResponse expectedResponse = new TaskAnalysisResponse(
            "Analysis result",
            "https://example.com/image.jpg"
        );
        
        when(taskAnalysisService.getTaskAnalysis(tasks)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<TaskAnalysisResponse> response = taskController.analyzeTasks(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(taskAnalysisService, times(1)).getTaskAnalysis(tasks);
    }

    @Test
    void analyzeTasks_EmptyTasks_ReturnsBadRequest() {
        // Arrange
        TaskListRequest request = new TaskListRequest();
        request.setTasks(Arrays.asList());
        
        when(taskAnalysisService.getTaskAnalysis(any())).thenThrow(new IllegalArgumentException("Task list cannot be empty"));

        // Act
        ResponseEntity<TaskAnalysisResponse> response = taskController.analyzeTasks(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(taskAnalysisService, times(1)).getTaskAnalysis(any());
    }

    @Test
    void analyzeTasks_NullTasks_ReturnsBadRequest() {
        // Arrange
        TaskListRequest request = new TaskListRequest();
        request.setTasks(null);
        
        when(taskAnalysisService.getTaskAnalysis(any())).thenThrow(new IllegalArgumentException("Task list cannot be null"));

        // Act
        ResponseEntity<TaskAnalysisResponse> response = taskController.analyzeTasks(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(taskAnalysisService, times(1)).getTaskAnalysis(any());
    }
} 