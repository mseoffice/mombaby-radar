package com.mombaby.radar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 内容表（34.2.2）。
 * content 状态机（34.2.6）：
 *   GENERATING → PENDING_REVIEW → APPROVED → PUBLISHED → ARCHIVED
 *   PENDING_REVIEW → REJECTED → GENERATING
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentStatus status;

    @Column(name = "platform", length = 20)
    private String platform;           // 小红书/抖音/微博/公众号/微信群

    @Lob
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "hashtags", length = 500)
    private String hashtags;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "ai_generated")
    private Boolean aiGenerated = false;   // 合规：AI 生成内容标识（附录C/附录B）

    /**
     * 状态流转校验。返回 true 表示允许从当前状态迁移到目标状态。
     */
    public boolean canTransitionTo(ContentStatus target) {
        return this.status != null && this.status.canTransitionTo(target);
    }

    /**
     * 执行状态流转（含校验）。
     * @throws IllegalStateException 非法流转
     */
    public void transitionTo(ContentStatus target) {
        if (!canTransitionTo(target)) {
            throw new IllegalStateException(
                "非法的 content 状态流转: " + this.status + " → " + target);
        }
        this.status = target;
        this.updatedAt = LocalDateTime.now();
    }
}
