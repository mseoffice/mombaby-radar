package com.mombaby.radar.agent;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Agent 任务生产者 — 发送任务到 RabbitMQ agent.task 队列。
 */
@Component
public class AgentTaskProducer {

    private final RabbitTemplate rabbitTemplate;

    public AgentTaskProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /** 发送 Agent 任务到队列，返回消息 ID */
    public String send(String agentType, Map<String, Object> payload) {
        var msg = new java.util.HashMap<String, Object>();
        msg.put("agentType", agentType);
        msg.put("payload", payload);
        msg.put("timestamp", System.currentTimeMillis());

        var correlationData = new org.springframework.amqp.rabbit.connection.CorrelationData(
                java.util.UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("agent.task.exchange", "agent.task", msg, correlationData);
        return correlationData.getId();
    }
}
