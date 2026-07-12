package com.mombaby.radar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 操作日志表（P1-5 补充，对应 35.2 操作审计）。
 * 保留 6 个月，通过定时任务清理过期记录。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "operation_log")
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "action", length = 100, nullable = false)
    private String action;

    @Column(name = "target", length = 200)
    private String target;

    @Column(name = "ip", length = 50)
    private String ip;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
