package com.mombaby.radar.analytics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 数据中心接口（#16）。
 * 端点（含 context-path /api）：
 *   GET /api/dashboards/daily   日报（T+1 更新）
 *   GET /api/dashboards/weekly  周报
 */
@RestController
@RequestMapping("/dashboards")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/daily")
    public ResponseEntity<Map<String, Object>> daily() {
        return ResponseEntity.ok(analyticsService.dailyReport());
    }

    @GetMapping("/weekly")
    public ResponseEntity<Map<String, Object>> weekly() {
        return ResponseEntity.ok(analyticsService.weeklyReport());
    }
}
