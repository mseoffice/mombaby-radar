package com.mombaby.radar.analytics;

import com.mombaby.radar.entity.Comment;
import com.mombaby.radar.entity.CommentStatus;
import com.mombaby.radar.entity.Content;
import com.mombaby.radar.entity.ContentStatus;
import com.mombaby.radar.repository.CommentRepository;
import com.mombaby.radar.repository.ContentRepository;
import com.mombaby.radar.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据中心服务（#16）。
 * 聚合商品/内容/评论三维指标，输出日报/周报与趋势（T+1 更新，研发期实时聚合）。
 */
@Service
public class AnalyticsService {

    private final ProductRepository productRepository;
    private final ContentRepository contentRepository;
    private final CommentRepository commentRepository;

    public AnalyticsService(ProductRepository productRepository,
                            ContentRepository contentRepository,
                            CommentRepository commentRepository) {
        this.productRepository = productRepository;
        this.contentRepository = contentRepository;
        this.commentRepository = commentRepository;
    }

    /** 日报指标 */
    public Map<String, Object> dailyReport() {
        List<Content> contents = contentRepository.findAll();
        List<Comment> comments = commentRepository.findAll();
        LocalDate today = LocalDate.now();

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("date", today.toString());
        m.put("totalProducts", productRepository.count());
        m.put("totalContents", (long) contents.size());
        m.put("publishedContents", contents.stream().filter(c -> c.getStatus() == ContentStatus.PUBLISHED).count());
        m.put("pendingReviewContents", contents.stream().filter(c -> c.getStatus() == ContentStatus.PENDING_REVIEW).count());
        m.put("aiGeneratedContents", contents.stream().filter(c -> Boolean.TRUE.equals(c.getAiGenerated())).count());
        m.put("totalComments", (long) comments.size());
        m.put("pendingComments", comments.stream().filter(c -> c.getStatus() == CommentStatus.NEW).count());
        m.put("analyzedComments", comments.stream().filter(c -> c.getStatus() == CommentStatus.ANALYZED).count());
        m.put("trend7d", buildContentTrend(contents, 7));
        return m;
    }

    /** 周报指标（7 日趋势 + 汇总） */
    public Map<String, Object> weeklyReport() {
        List<Content> contents = contentRepository.findAll();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("weekStart", LocalDate.now().minusDays(6).toString());
        m.put("weekEnd", LocalDate.now().toString());
        m.put("trend7d", buildContentTrend(contents, 7));
        m.put("totalContents", (long) contents.size());
        return m;
    }

    /** 近 n 日每日内容生成数（按 createdAt 分桶） */
    private List<Map<String, Object>> buildContentTrend(List<Content> contents, int days) {
        LocalDate end = LocalDate.now();
        Map<LocalDate, Long> byDate = contents.stream()
                .filter(c -> c.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getCreatedAt().toLocalDate(),
                        Collectors.counting()));

        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate d = end.minusDays(i);
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", d.toString());
            point.put("count", byDate.getOrDefault(d, 0L));
            trend.add(point);
        }
        return trend;
    }
}
