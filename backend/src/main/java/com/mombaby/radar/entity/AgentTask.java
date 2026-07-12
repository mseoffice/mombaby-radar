package com.mombaby.radar.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Agent 任务表（34.2.3）。
 * 建议补充状态（草案）：pending → running → success / failed / partial
 */
@Entity
@Table(name = "agent_task")
public class AgentTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_type", length = 30, nullable = false)
    private String agentType;          // 爆款发现/内容生成/图片生成/评论/微信群/运营分析

    @Column(name = "status", length = 20, nullable = false)
    private String status;             // pending / running / success / failed / partial

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;      // 第 34 章 SLA：内容生成≤3min、评论分析≤5s 等

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
