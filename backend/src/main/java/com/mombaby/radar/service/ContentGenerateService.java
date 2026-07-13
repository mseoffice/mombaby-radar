package com.mombaby.radar.service;

import com.mombaby.radar.agent.AgentTaskProducer;
import com.mombaby.radar.dto.ContentGenerateRequest;
import com.mombaby.radar.dto.ContentGenerateResponse;
import com.mombaby.radar.entity.AgentTask;
import com.mombaby.radar.entity.Content;
import com.mombaby.radar.entity.ContentStatus;
import com.mombaby.radar.repository.AgentTaskRepository;
import com.mombaby.radar.repository.ContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 内容生成服务（12.3 内容生成规格 + 第 18 章 Agent2）。
 * 编排 5 平台并行内容生成，写入 content 表并触发状态流转。
 */
@Service
public class ContentGenerateService {

    private static final Logger log = LoggerFactory.getLogger(ContentGenerateService.class);

    private final ChatClient chatClient;
    private final PromptTemplateService promptService;
    private final ContentRepository contentRepository;
    private final AgentTaskRepository agentTaskRepository;
    private final AgentTaskProducer agentTaskProducer;

    public ContentGenerateService(ChatClient chatClient,
                                   PromptTemplateService promptService,
                                   ContentRepository contentRepository,
                                   AgentTaskRepository agentTaskRepository,
                                   AgentTaskProducer agentTaskProducer) {
        this.chatClient = chatClient;
        this.promptService = promptService;
        this.contentRepository = contentRepository;
        this.agentTaskRepository = agentTaskRepository;
        this.agentTaskProducer = agentTaskProducer;
    }

    /**
     * 为指定商品生成多平台内容。
     * 1. 创建 AgentTask（running）
     * 2. 并行调用 LLM 生成各平台内容
     * 3. 写入 content 表（status=PENDING_REVIEW）
     * 4. 更新 AgentTask（success）+ 发送消息到队列
     */
    public ContentGenerateResponse generate(ContentGenerateRequest request) {
        String productName = request.getProductName();
        List<String> platforms = request.getPlatforms() != null && !request.getPlatforms().isEmpty()
                ? request.getPlatforms()
                : List.copyOf(promptService.supportedPlatforms());

        // 1. 创建 AgentTask
        AgentTask task = new AgentTask();
        task.setAgentType("content-generate");
        task.setStatus("running");
        task.setCreatedAt(LocalDateTime.now());
        task = agentTaskRepository.save(task);

        long start = System.currentTimeMillis();

        // 2. 并行生成（Mock 模式下 ChatClient 同步返回示例）
        List<ContentGenerateResponse.PlatformContent> results = new ArrayList<>();
        for (String platform : platforms) {
            try {
                String prompt = promptService.buildPrompt(platform, productName);
                String aiText = chatClient.prompt().user(prompt).call().content();
                String[] parts = parseAiResponse(aiText, platform);

                // 3. 写入 content 表
                Content content = new Content();
                content.setProductId(request.getProductId());
                content.setPlatform(platform);
                content.setStatus(ContentStatus.PENDING_REVIEW);
                content.setTitle(parts[0]);
                content.setBody(parts[1]);
                content.setHashtags(parts[2]);
                content.setAiGenerated(true);      // 合规：AI 生成内容在生成即标注（附录C/35.2）
                content.setCreatedAt(LocalDateTime.now());
                content.setUpdatedAt(LocalDateTime.now());
                content = contentRepository.save(content);

                results.add(new ContentGenerateResponse.PlatformContent(
                        platform, parts[0], parts[1], parts[2]));
                log.info("内容生成完成: platform={} contentId={}", platform, content.getId());
            } catch (Exception e) {
                log.error("内容生成失败: platform={}, error={}", platform, e.getMessage());
            }
        }

        // 4. 更新 AgentTask
        long elapsed = System.currentTimeMillis() - start;
        task.setStatus("success");
        task.setExecutionTimeMs(elapsed);
        task.setCompletedAt(LocalDateTime.now());
        agentTaskRepository.save(task);

        // 5. 发送消息到 Agent 队列（供后续异步处理）
        agentTaskProducer.send("content-generate",
                Map.of("productId", request.getProductId(), "count", results.size()));

        return new ContentGenerateResponse(task.getId(), results);
    }

    /** 简易解析 AI 返回文本：第一行=标题，最后一行=标签，其余=正文 */
    private String[] parseAiResponse(String text, String platform) {
        String[] lines = text.split("\n");
        String title = lines.length > 0 ? lines[0].replaceAll("^#+\\s*", "").trim() : platform + "推荐";
        // 取最后一行含 # 的作为标签
        String hashtags = "";
        for (int i = lines.length - 1; i >= 0; i--) {
            if (lines[i].contains("#")) { hashtags = lines[i].trim(); break; }
        }
        String body = text; // Mock 模式全文当 body
        return new String[]{title, body, hashtags};
    }
}
