package com.summarizer.project.dto;

public class FollowUpRequest {
    private String userId;
    private String query;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
}
