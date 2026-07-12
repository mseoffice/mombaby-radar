package com.mombaby.radar.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Agent 编排引擎（9.1）。
 * 负责：任务调度、上下文管理、模型路由、结果聚合。
 * V1.0 简化版：按 agentType 路由到对应处理器。
 */
@Service
public class AgentOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AgentOrchestrator.class);
    private final Map<String, AgentHandler> handlers;

    public AgentOrchestrator(ContentGenerateAgent contentGenerateAgent) {
        // 注册 Agent 处理器（M2 先注册内容生成，后续 Agent 逐步接入）
        this.handlers = Map.of(
            "content-generate", contentGenerateAgent
            // "hot-product", hotProductAgent,       // M3
            // "comment-reply", commentReplyAgent,    // M3
            // "image-generate", imageGenerateAgent,  // M3
            // "wechat-group", wechatGroupAgent,      // M3
            // "analytics", analyticsAgent            // M3
        );
    }

    /** 分发任务到对应 Agent 处理器 */
    public void dispatch(String agentType, Map<String, Object> payload) {
        AgentHandler handler = handlers.get(agentType);
        if (handler == null) {
            log.warn("未知 Agent 类型: {}", agentType);
            return;
        }
        handler.handle(payload);
    }

    /** Agent 处理器接口 */
    public interface AgentHandler {
        void handle(Map<String, Object> payload);
    }
}
