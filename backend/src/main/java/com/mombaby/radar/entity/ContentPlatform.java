package com.mombaby.radar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "content_platform")
public class ContentPlatform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(name = "platform", length = 20, nullable = false)
    private String platform;           // 小红书/抖音/微博/公众号/微信群

    @Column(name = "publish_url", length = 500)
    private String publishUrl;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}
