package com.mombaby.radar.controller;

import com.mombaby.radar.dto.ContentGenerateRequest;
import com.mombaby.radar.dto.ContentGenerateResponse;
import com.mombaby.radar.entity.Content;
import com.mombaby.radar.service.ContentGenerateService;
import com.mombaby.radar.service.ContentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content")
public class ContentController {

    private final ContentService contentService;
    private final ContentGenerateService contentGenerateService;

    public ContentController(ContentService contentService,
                             ContentGenerateService contentGenerateService) {
        this.contentService = contentService;
        this.contentGenerateService = contentGenerateService;
    }

    @GetMapping
    public ResponseEntity<Page<Content>> list(Pageable pageable) {
        return ResponseEntity.ok(contentService.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Content> get(@PathVariable Long id) {
        return contentService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Content> create(@RequestBody Content content) {
        return ResponseEntity.ok(contentService.create(content));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Content> approve(@PathVariable Long id,
                                           @RequestParam Long reviewerId,
                                           @RequestParam(required = false, defaultValue = "") String opinion) {
        return ResponseEntity.ok(contentService.approve(id, reviewerId, opinion));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Content> reject(@PathVariable Long id,
                                          @RequestParam Long reviewerId,
                                          @RequestParam(required = false, defaultValue = "") String opinion) {
        return ResponseEntity.ok(contentService.reject(id, reviewerId, opinion));
    }

    @PostMapping("/{id}/regenerate")
    public ResponseEntity<Content> regenerate(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.regenerate(id));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Content> publish(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.publish(id));
    }

    @PostMapping("/generate")
    public ResponseEntity<ContentGenerateResponse> generate(@RequestBody ContentGenerateRequest request) {
        return ResponseEntity.ok(contentGenerateService.generate(request));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Content> archive(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.archive(id));
    }
}
