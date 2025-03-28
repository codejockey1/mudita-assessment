package com.mudita.assessment.model;

import java.util.List;

public class TaskListRequest {
    private List<String> tasks;

    public TaskListRequest() {
    }

    public TaskListRequest(List<String> tasks) {
        this.tasks = tasks;
    }

    public List<String> getTasks() {
        return tasks;
    }

    public void setTasks(List<String> tasks) {
        this.tasks = tasks;
    }
} 