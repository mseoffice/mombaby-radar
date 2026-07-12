package com.mombaby.radar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 内容审核记录表（P1-5 补充）。
 * 记录每次审核人/动作/意见，content.status 仅记录最终结果。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content_review_log")
public class ContentReviewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    @Column(name = "action", length = 20, nullable = false)
    private String action;             // approved / rejected

    @Column(name = "opinion", columnDefinition = "TEXT")
    private String opinion;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
