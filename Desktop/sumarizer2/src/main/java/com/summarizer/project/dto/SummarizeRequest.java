package com.summarizer.project.dto;

public class SummarizeRequest {
    private String userId;
    private String text;

    private String mode; // ✅ Add this line

    // getter/setter for mode
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
