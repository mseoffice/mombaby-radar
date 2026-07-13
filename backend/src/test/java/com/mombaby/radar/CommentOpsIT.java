package com.mombaby.radar;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 评论/私信冒烟测试（#20 e2e-smoke）。
 * 覆盖：ingest → analyze → reply（dev Mock profile）。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class CommentOpsIT {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void ingestAndAnalyzeAndReply() throws Exception {
        // 登录
        String resp = mvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token = (String) objectMapper.readValue(resp, Map.class).get("token");

        // 录入评论
        String ingestResp = mvc.perform(post("/comments/ingest")
                .header("Authorization", "Bearer " + token)
                .param("platform", "小红书")
                .param("rawText", "这个商品适合1岁宝宝吗？求推荐"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.source").value("MANUAL"))
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> ingested = objectMapper.readValue(ingestResp, Map.class);
        long id = ((Number) ingested.get("id")).longValue();

        // 分析评论
        String analyzeResp = mvc.perform(post("/comments/" + id + "/analyze")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ANALYZED"))
                .andExpect(jsonPath("$.type").isNotEmpty())
                .andExpect(jsonPath("$.sentiment").isNotEmpty())
                .andExpect(jsonPath("$.replyOptions").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> analyzed = objectMapper.readValue(analyzeResp, Map.class);
        String replyOptions = (String) analyzed.get("replyOptions");
        // 确认回复方案非空
        assert replyOptions != null && !replyOptions.equals("[]") : "回复方案不应为空";

        // 标记已回复
        mvc.perform(post("/comments/" + id + "/reply")
                .header("Authorization", "Bearer " + token)
                .param("replyText", "感谢反馈～"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REPLIED"));
    }
}
