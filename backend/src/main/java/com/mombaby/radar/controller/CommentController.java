package com.mombaby.radar.controller;

import com.mombaby.radar.entity.Comment;
import com.mombaby.radar.entity.CommentSource;
import com.mombaby.radar.entity.CommentStatus;
import com.mombaby.radar.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论/私信接口（D3 / 13 章 AI 运营中心）。
 * 端点（含 context-path /api）：
 *   POST /api/comments/ingest          手动粘贴录入（或 source=api 走预留采集器）
 *   GET  /api/comments                 列表（可选 ?status=NEW）
 *   POST /api/comments/{id}/analyze    分析+生成回复（≤5s）
 *   POST /api/comments/{id}/reply       选定回复
 */
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<Comment> ingest(@RequestParam String platform,
                                          @RequestParam String rawText,
                                          @RequestParam(defaultValue = "MANUAL") CommentSource source) {
        Comment c = source == CommentSource.PLATFORM_API
                ? commentService.ingestViaApi(platform, rawText)
                : commentService.ingestManual(platform, rawText);
        return ResponseEntity.ok(c);
    }

    @GetMapping
    public ResponseEntity<Page<Comment>> list(@RequestParam(required = false) CommentStatus status,
                                              Pageable pageable) {
        if (status != null) {
            List<Comment> list = commentService.listByStatus(status);
            return ResponseEntity.ok(new org.springframework.data.domain.PageImpl<>(list, pageable, list.size()));
        }
        return ResponseEntity.ok(commentService.list(pageable));
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<Comment> analyze(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.analyze(id));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<Comment> reply(@PathVariable Long id,
                                         @RequestParam(required = false) String replyText) {
        return ResponseEntity.ok(commentService.reply(id, replyText));
    }
}
