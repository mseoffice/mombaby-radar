package com.mombaby.radar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 评论/私信实体（13 章 AI 运营中心 / D3 采集）。
 *
 * <p>状态机（CommentStatus）：NEW → ANALYZED → REPLIED / PENDING。
 * 采集来源（CommentSource）：MANUAL（手动粘贴，V1.0 主路径）/ PLATFORM_API（可选，后续迭代）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联内容（可选） */
    @Column(name = "content_id")
    private Long contentId;

    /** 平台（小红书/抖音/微博/公众号/微信群） */
    @Column(name = "platform", length = 20)
    private String platform;

    /** 原始文本（用户粘贴或 API 拉取） */
    @Lob
    @Column(name = "raw_text", columnDefinition = "TEXT")
    private String rawText;

    /** 采集来源 */
    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 20, nullable = false)
    private CommentSource source;

    /** 分类（咨询/投诉/好评/负面…） */
    @Column(name = "type", length = 30)
    private String type;

    /** 情绪（正面/中性/负面） */
    @Column(name = "sentiment", length = 20)
    private String sentiment;

    /** 意图 */
    @Column(name = "intent", length = 50)
    private String intent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private CommentStatus status;

    /** 回复方案（JSON 数组，2–3 条），由 analyze 生成 */
    @Lob
    @Column(name = "reply_options", columnDefinition = "TEXT")
    private String replyOptions;

    /** 私信/评论匹配到的商品 ID（关键词匹配） */
    @Column(name = "matched_product_id")
    private Long matchedProductId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean canTransitionTo(CommentStatus target) {
        return this.status != null && this.status.canTransitionTo(target);
    }

    public void transitionTo(CommentStatus target) {
        if (!canTransitionTo(target)) {
            throw new IllegalStateException(
                "非法的 comment 状态流转: " + this.status + " → " + target);
        }
        this.status = target;
        this.updatedAt = LocalDateTime.now();
    }
}
