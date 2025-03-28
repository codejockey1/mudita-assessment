package com.mudita.assessment.controller;

import com.mudita.assessment.model.TaskListRequest;
import com.mudita.assessment.model.TaskAnalysisResponse;
import com.mudita.assessment.service.TaskAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskAnalysisService taskAnalysisService;

    public TaskController(TaskAnalysisService taskAnalysisService) {
        this.taskAnalysisService = taskAnalysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<TaskAnalysisResponse> analyzeTasks(@RequestBody TaskListRequest request) {
        try {
            TaskAnalysisResponse response = taskAnalysisService.getTaskAnalysis(request.getTasks());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 