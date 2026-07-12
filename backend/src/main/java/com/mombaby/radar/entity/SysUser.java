package com.mombaby.radar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户表（3.2）。
 * A7 约束：无 tenant_id。
 * 角色映射（3.2）：系统拥有者 / 运营 / 分析师 / 访客。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sys_user")
public class SysUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", length = 200, nullable = false)
    private String passwordHash;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "status", length = 20)
    private String status;             // active / disabled

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
