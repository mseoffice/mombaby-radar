package com.mombaby.radar.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 内容表（34.2.2）。
 * content 状态机（34.2.6）：
 *   generating → pending_review → approved → published → archived
 *   pending_review → rejected → generating
 */
@Entity
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    /**
     * 状态枚举（34.2.6）：
     * GENERATING, PENDING_REVIEW, APPROVED, PUBLISHED, REJECTED, ARCHIVED
     */
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "platform", length = 20)
    private String platform;           // 小红书/抖音/微博/公众号/微信群

    @Lob
    @Column(name = "body")
    private String body;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "hashtags", length = 500)
    private String hashtags;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
