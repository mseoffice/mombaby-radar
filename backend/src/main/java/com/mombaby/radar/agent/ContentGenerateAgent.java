package com.mombaby.radar.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 内容生成 Agent（Agent2，第 18 章）。
 * 接收生成任务，调用 LLM 生成 5 平台内容。
 * M2 Mock 模式：占位处理器，实际生成由 ContentGenerateService 完成。
 */
@Component
public class ContentGenerateAgent implements AgentOrchestrator.AgentHandler {

    private static final Logger log = LoggerFactory.getLogger(ContentGenerateAgent.class);

    @Override
    public void handle(Map<String, Object> payload) {
        Long productId = Long.valueOf(payload.get("productId").toString());
        log.info("内容生成 Agent 收到任务: productId={}", productId);
        // 实际生成由 ContentGenerateService 同步调用完成
        // 此处为异步入口，可用于后续消息驱动生成
    }
}
