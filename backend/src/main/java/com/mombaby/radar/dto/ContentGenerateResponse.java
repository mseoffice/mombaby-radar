package com.mombaby.radar.dto;

import java.util.List;

public class ContentGenerateResponse {
    private Long taskId;
    private List<PlatformContent> contents;

    public ContentGenerateResponse() {}
    public ContentGenerateResponse(Long taskId, List<PlatformContent> contents) {
        this.taskId = taskId;
        this.contents = contents;
    }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public List<PlatformContent> getContents() { return contents; }
    public void setContents(List<PlatformContent> contents) { this.contents = contents; }

    public static class PlatformContent {
        private String platform;
        private String title;
        private String body;
        private String hashtags;

        public PlatformContent() {}
        public PlatformContent(String platform, String title, String body, String hashtags) {
            this.platform = platform; this.title = title; this.body = body; this.hashtags = hashtags;
        }
        public String getPlatform() { return platform; }
        public void setPlatform(String platform) { this.platform = platform; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
        public String getHashtags() { return hashtags; }
        public void setHashtags(String hashtags) { this.hashtags = hashtags; }
    }
}
