package com.mombaby.radar.dashboard;

import com.mombaby.radar.analytics.AnalyticsService;
import com.mombaby.radar.entity.Comment;
import com.mombaby.radar.entity.CommentStatus;
import com.mombaby.radar.entity.Content;
import com.mombaby.radar.entity.ContentStatus;
import com.mombaby.radar.service.CommentService;
import com.mombaby.radar.service.ContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Dashboard 主页接口（#17）。
 * 端点（含 context-path /api）：GET /api/dashboard
 * 返回：6 数据卡片 + 今日待办（含评论/好价）+ 运营趋势 + 昨爆文 + 可折叠 AI 建议。
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final AnalyticsService analyticsService;
    private final ContentService contentService;
    private final CommentService commentService;

    public DashboardController(AnalyticsService analyticsService,
                               ContentService contentService,
                               CommentService commentService) {
        this.analyticsService = analyticsService;
        this.contentService = contentService;
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> dashboard() {
        Map<String, Object> daily = analyticsService.dailyReport();

        // 6 卡片
        List<Map<String, Object>> cards = new ArrayList<>();
        cards.add(card("商品总数", daily.get("totalProducts")));
        cards.add(card("内容总数", daily.get("totalContents")));
        cards.add(card("已发布", daily.get("publishedContents")));
        cards.add(card("待审核", daily.get("pendingReviewContents")));
        cards.add(card("评论总数", daily.get("totalComments")));
        cards.add(card("待处理评论", daily.get("pendingComments")));

        // 今日待办：待审核内容 + 待分析评论
        List<Content> pendingContents = contentService.list(org.springframework.data.domain.Pageable.ofSize(20))
                .getContent().stream()
                .filter(c -> c.getStatus() == ContentStatus.PENDING_REVIEW)
                .collect(Collectors.toList());
        List<Comment> newComments = commentService.listByStatus(CommentStatus.NEW);

        Map<String, Object> todos = new LinkedHashMap<>();
        todos.put("pendingReviewContents", pendingContents);
        todos.put("newComments", newComments);

        // 昨爆文：最近发布的 1 条
        Optional<Content> yesterdayHot = contentService.list(org.springframework.data.domain.Pageable.ofSize(50))
                .getContent().stream()
                .filter(c -> c.getStatus() == ContentStatus.PUBLISHED && c.getUpdatedAt() != null)
                .max(Comparator.comparing(Content::getUpdatedAt));

        // 可折叠 AI 建议（基于指标生成）
        List<String> aiSuggestions = buildSuggestions(daily, newComments.size());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cards", cards);
        result.put("todos", todos);
        result.put("trend", daily.get("trend7d"));
        result.put("yesterdayHot", yesterdayHot.orElse(null));
        result.put("aiSuggestions", aiSuggestions);
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> card(String label, Object value) {
        Map<String, Object> c = new LinkedHashMap<>();
        c.put("label", label);
        c.put("value", value);
        return c;
    }

    private List<String> buildSuggestions(Map<String, Object> daily, int newComments) {
        List<String> s = new ArrayList<>();
        long pendingComments = ((Number) daily.getOrDefault("pendingComments", 0)).longValue();
        long pendingReview = ((Number) daily.getOrDefault("pendingReviewContents", 0)).longValue();
        if (pendingReview > 0) s.add("当前有 " + pendingReview + " 条内容待审核，建议优先处理以释放发布节奏。");
        if (pendingComments > 0) s.add("评论收件箱有 " + pendingComments + " 条新评论待分析，可用 AI 一键生成回复。");
        if (pendingComments == 0 && pendingReview == 0) s.add("暂无明显运营瓶颈，可关注高潜商品的内容覆盖。");
        s.add("建议对负面评论优先人工跟进，AI 生成回复发布前需人工审核（合规：AI 生成须标注）。");
        return s;
    }
}
