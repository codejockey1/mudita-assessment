package com.mudita.assessment.model;

public class TaskAnalysisResponse {
    private String analysis;
    private String imageUrl;

    public TaskAnalysisResponse() {
    }

    public TaskAnalysisResponse(String analysis, String imageUrl) {
        this.analysis = analysis;
        this.imageUrl = imageUrl;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}