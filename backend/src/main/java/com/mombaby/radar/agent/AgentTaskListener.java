package com.mombaby.radar.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Agent 任务消费者 — 监听 agent.task 队列，分发到对应 Agent 处理。
 */
@Component
public class AgentTaskListener {

    private static final Logger log = LoggerFactory.getLogger(AgentTaskListener.class);
    private final AgentOrchestrator orchestrator;

    public AgentTaskListener(AgentOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @RabbitListener(queues = "agent.task.queue")
    public void handle(Map<String, Object> message) {
        String agentType = (String) message.get("agentType");
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) message.get("payload");

        log.info("收到 Agent 任务: type={}", agentType);
        try {
            orchestrator.dispatch(agentType, payload);
        } catch (Exception e) {
            log.error("Agent 任务处理失败: type={}, error={}", agentType, e.getMessage());
            // 降级：连续失败 3 次后通知运营（M3 实现）
        }
    }
}
