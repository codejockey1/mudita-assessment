package com.mudita.assessment.service;

import com.mudita.assessment.model.TaskAnalysisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskAnalysisService {
    private final OpenAIService openAIService;

    @Value("${openai.api.task-analysis.prompt-template}")
    private String promptTemplate;

    @Value("${openai.api.task-analysis.dalle-prompt-marker}")
    private String dallePromptMarker;

    @Value("${openai.api.task-analysis.fallback-dalle-prompt}")
    private String fallbackDallePrompt;

    public TaskAnalysisService(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    public TaskAnalysisResponse getTaskAnalysis(List<String> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("Task list cannot be null");
        }
        if (tasks.isEmpty()) {
            throw new IllegalArgumentException("Task list cannot be empty");
        }
        String analysis = getChatGPTAnalysis(tasks);
        String dallePrompt = extractDallePrompt(analysis);
        String imageUrl = openAIService.generateImage(dallePrompt);
        String analysisStripped = stripDallEPrompt(analysis);
        
        // Use fallback image URL if generateImage returns null
        if (imageUrl == null) {
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/6/65/No-Image-Placeholder.svg";
        }
        
        return new TaskAnalysisResponse(analysisStripped, imageUrl);
    }

    private String getChatGPTAnalysis(List<String> tasks) {
        String prompt = buildPrompt(tasks);
        return openAIService.sendPrompt(prompt);
    }

    private String buildPrompt(List<String> tasks) {
        String tasksString = formatTasks(tasks);
        return String.format(promptTemplate, dallePromptMarker, tasksString);
    }

    private String formatTasks(List<String> tasks) {
        StringBuilder tasksString = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            tasksString.append((i + 1)).append(". ").append(tasks.get(i)).append("\n");
        }
        return tasksString.toString();
    }

    private String extractDallePrompt(String analysis) {
        int promptStart = analysis.indexOf(dallePromptMarker);
        if (promptStart != -1) {
            promptStart += dallePromptMarker.length();
            int promptEnd = analysis.indexOf("\n", promptStart);
            if (promptEnd == -1 || promptEnd == promptStart) {
                promptEnd = analysis.length();
            }
            return analysis.substring(promptStart, promptEnd).trim();
        }
        return fallbackDallePrompt;
    }

    private String stripDallEPrompt(String analysis) {
        // Strip out the DALL-E prompt marker and everything after it from the analysis
        int markerIndex = analysis.indexOf(dallePromptMarker);
        if (markerIndex != -1) {
            analysis = analysis.substring(0, markerIndex).trim();
        }
        return analysis;
    }
} 