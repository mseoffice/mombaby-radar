package com.mombaby.radar.service;

import com.mombaby.radar.config.JwtUtil;
import com.mombaby.radar.entity.SysUser;
import com.mombaby.radar.repository.SysUserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final SysUserRepository sysUserRepository;
    private final JwtUtil jwtUtil;

    public AuthService(SysUserRepository sysUserRepository, JwtUtil jwtUtil) {
        this.sysUserRepository = sysUserRepository;
        this.jwtUtil = jwtUtil;
    }

    public record AuthResponse(String token, Long userId, String username) {}

    /** 登录：验密成功后返回 JWT Token（2h 有效） */
    public Optional<AuthResponse> login(String username, String password) {
        Optional<SysUser> userOpt = sysUserRepository.findByUsername(username);
        if (userOpt.isEmpty()) return Optional.empty();

        SysUser user = userOpt.get();
        // V1.0 简化：明文比对（生产须 BCrypt）
        if (!user.getPasswordHash().equals(password)) return Optional.empty();

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return Optional.of(new AuthResponse(token, user.getId(), user.getUsername()));
    }

    /** 刷新 Token：用旧 Token 换新 Token */
    public Optional<AuthResponse> refresh(String oldToken) {
        if (!jwtUtil.validate(oldToken)) return Optional.empty();
        var claims = jwtUtil.parseToken(oldToken);
        Long userId = Long.valueOf(claims.getSubject());
        String username = claims.get("username", String.class);
        String newToken = jwtUtil.generateToken(userId, username);
        return Optional.of(new AuthResponse(newToken, userId, username));
    }
}
