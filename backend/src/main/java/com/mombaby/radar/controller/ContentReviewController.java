package com.mombaby.radar.controller;

import com.mombaby.radar.entity.ContentReviewLog;
import com.mombaby.radar.repository.ContentReviewLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/{contentId}/review-logs")
public class ContentReviewController {

    private final ContentReviewLogRepository reviewLogRepository;

    public ContentReviewController(ContentReviewLogRepository reviewLogRepository) {
        this.reviewLogRepository = reviewLogRepository;
    }

    @GetMapping
    public ResponseEntity<List<ContentReviewLog>> list(@PathVariable Long contentId) {
        return ResponseEntity.ok(
                reviewLogRepository.findByContentIdOrderByCreatedAtDesc(contentId));
    }
}
