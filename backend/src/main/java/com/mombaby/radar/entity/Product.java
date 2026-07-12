package com.mombaby.radar.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品表（34.2.1）。
 * A7 约束：无 tenant_id（单团队内部工具）。
 */
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", length = 200, nullable = false)
    private String productName;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "current_price", precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "lowest_price", precision = 10, scale = 2)
    private BigDecimal lowestPrice;

    @Column(name = "recommend_score")
    private Integer recommendScore;      // 1-10，AI 推荐指数

    @Column(name = "suitable_age", length = 50)
    private String suitableAge;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== 省略 getter/setter（Lombok 可选）=====
}
