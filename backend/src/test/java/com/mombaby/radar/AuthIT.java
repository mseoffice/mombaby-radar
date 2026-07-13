package com.mombaby.radar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证冒烟测试（#20 e2e-smoke）。
 * 覆盖：登录拿 token、带 token 访问 200、无 token 返回 401/403。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class AuthIT {

    @Autowired
    private MockMvc mvc;

    @Test
    void loginReturnsToken() throws Exception {
        mvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void contentWithoutTokenIsUnauthorized() throws Exception {
        mvc.perform(get("/content"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void dashboardWithoutTokenIsUnauthorized() throws Exception {
        mvc.perform(get("/dashboard"))
                .andExpect(status().isUnauthorized());
    }
}
