package com.mombaby.radar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色表（3.2 + P1-5 补充）。
 * permissions 存储为 JSON：["dashboard","products","content_factory","ai_ops","private_ops","analytics"]
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", length = 50, nullable = false, unique = true)
    private String roleName;

    @Column(name = "permissions", columnDefinition = "JSON")
    private String permissions;        // JSON 数组，如 ["dashboard","products"]

    @Column(name = "status", length = 20)
    private String status;             // active / disabled
}
