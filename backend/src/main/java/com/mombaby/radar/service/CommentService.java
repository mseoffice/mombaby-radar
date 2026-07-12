package com.mombaby.radar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mombaby.radar.collector.Collector;
import com.mombaby.radar.collector.ManualPasteCollector;
import com.mombaby.radar.collector.PlatformApiCollector;
import com.mombaby.radar.entity.Comment;
import com.mombaby.radar.entity.CommentSource;
import com.mombaby.radar.entity.CommentStatus;
import com.mombaby.radar.entity.Product;
import com.mombaby.radar.repository.CommentRepository;
import com.mombaby.radar.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 评论/私信服务（D3 / 13 章 AI 运营中心）。
 * <ul>
 *   <li>采集：手动粘贴（主路径）/ 平台 API（可选项，预留）</li>
 *   <li>分析：调用 LLM（DeepSeek）做分类/情绪/意图 + 2–3 条回复方案（SLA ≤5s）</li>
 *   <li>私信商品匹配：关键词匹配商品（ES 后续增强）</li>
 * </ul>
 */
@Service
public class CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final ChatClient chatClient;
    private final ManualPasteCollector manualPasteCollector;
    private final PlatformApiCollector platformApiCollector;

    public CommentService(CommentRepository commentRepository,
                          ProductRepository productRepository,
                          ChatClient chatClient,
                          ManualPasteCollector manualPasteCollector,
                          PlatformApiCollector platformApiCollector) {
        this.commentRepository = commentRepository;
        this.productRepository = productRepository;
        this.chatClient = chatClient;
        this.manualPasteCollector = manualPasteCollector;
        this.platformApiCollector = platformApiCollector;
    }

    /** 手动粘贴录入（V1.0 主路径） */
    public Comment ingestManual(String platform, String rawText) {
        return save(manualPasteCollector.collect(platform, rawText));
    }

    /** 平台 API 自动采集（可选项，预留；未启用抛异常） */
    public Comment ingestViaApi(String platform, String rawText) {
        return save(platformApiCollector.collect(platform, rawText));
    }

    private Comment save(Comment c) {
        if (c.getRawText() == null || c.getRawText().isBlank()) {
            throw new IllegalArgumentException("评论文本不能为空");
        }
        return commentRepository.save(c);
    }

    public Page<Comment> list(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    public List<Comment> listByStatus(CommentStatus status) {
        return commentRepository.findByStatus(status);
    }

    public Optional<Comment> getById(Long id) {
        return commentRepository.findById(id);
    }

    /**
     * 分析评论：分类/情绪/意图 + 生成 2–3 条回复方案。SLA ≤5s（34 章）。
     */
    public Comment analyze(Long id) {
        Comment c = getOrThrow(id);
        if (!c.canTransitionTo(CommentStatus.ANALYZED)) {
            throw new IllegalStateException("评论当前状态不可分析: " + c.getStatus());
        }

        String prompt = "你是母婴电商的评论分析助手。请对以下评论进行分类（咨询/投诉/好评/负面）、"
                + "判断情绪（正面/中性/负面）、提取用户意图，并给出 2-3 条回复建议。"
                + "严格以 JSON 返回，不要多余说明："
                + "{\"type\":\"\",\"sentiment\":\"\",\"intent\":\"\",\"replies\":[\"\",\"\",\"\"]}。"
                + "评论内容：" + c.getRawText();

        String content;
        try {
            content = chatClient.prompt().user(prompt).call().content();
        } catch (Exception e) {
            log.warn("LLM 分析失败，使用兜底规则: {}", e.getMessage());
            content = null;
        }

        AnalysisResult result = parseAnalysis(content, c.getRawText());
        c.setType(result.type);
        c.setSentiment(result.sentiment);
        c.setIntent(result.intent);
        c.setReplyOptions(result.replyOptionsJson);
        c.setMatchedProductId(matchProduct(c.getRawText()));
        c.transitionTo(CommentStatus.ANALYZED);
        return commentRepository.save(c);
    }

    /** 选定某条回复（标记已回复） */
    public Comment reply(Long id, String replyText) {
        Comment c = getOrThrow(id);
        if (!c.canTransitionTo(CommentStatus.REPLIED)) {
            throw new IllegalStateException("评论当前状态不可标记已回复: " + c.getStatus());
        }
        c.transitionTo(CommentStatus.REPLIED);
        return commentRepository.save(c);
    }

    /** 解析 LLM 输出；失败则走兜底规则 */
    private AnalysisResult parseAnalysis(String llmOutput, String rawText) {
        if (llmOutput != null && llmOutput.contains("{")) {
            try {
                String json = llmOutput.substring(llmOutput.indexOf('{'),
                        llmOutput.lastIndexOf('}') + 1);
                JsonNode node = objectMapper.readTree(json);
                String type = node.path("type").asText("咨询");
                String sentiment = node.path("sentiment").asText("中性");
                String intent = node.path("intent").asText("");
                List<String> replies = new ArrayList<>();
                node.path("replies").forEach(r -> replies.add(r.asText()));
                if (replies.isEmpty()) {
                    replies.add("感谢您的关注，已为您记录，我们会尽快回复～");
                    replies.add("宝子可以看看详情页说明，有疑问随时私信我哦～");
                }
                return new AnalysisResult(type, sentiment, intent,
                        objectMapper.writeValueAsString(replies));
            } catch (Exception e) {
                log.warn("评论分析 JSON 解析失败，走兜底: {}", e.getMessage());
            }
        }
        // 兜底：简单关键词规则
        String sentiment = rawText.contains("差评") || rawText.contains("投诉") || rawText.contains("负面")
                ? "负面" : (rawText.contains("好评") || rawText.contains("喜欢") || rawText.contains("谢谢")) ? "正面" : "中性";
        List<String> replies = List.of(
                "感谢宝妈反馈～我们会重视您的体验，已为您转交专员跟进。",
                "宝子别着急，方便的话私信我订单/详情，帮您一对一解决～");
        try {
            return new AnalysisResult("咨询", sentiment, "", objectMapper.writeValueAsString(replies));
        } catch (Exception e) {
            return new AnalysisResult("咨询", sentiment, "", "[\"感谢反馈～\",\"已为您转交专员跟进\"]");
        }
    }

    /** 关键词匹配商品（ES 后续增强） */
    private Long matchProduct(String rawText) {
        if (rawText == null || rawText.isBlank()) return null;
        List<Product> products = productRepository.findAll(Pageable.ofSize(50)).getContent();
        for (Product p : products) {
            if (p.getProductName() != null &&
                (rawText.contains(p.getProductName()) || p.getProductName().contains(rawText.trim()))) {
                return p.getId();
            }
        }
        return null;
    }

    private Comment getOrThrow(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评论不存在: " + id));
    }

    /** 内部分析结果载体 */
    private static class AnalysisResult {
        final String type;
        final String sentiment;
        final String intent;
        final String replyOptionsJson;

        AnalysisResult(String type, String sentiment, String intent, String replyOptionsJson) {
            this.type = type;
            this.sentiment = sentiment;
            this.intent = intent;
            this.replyOptionsJson = replyOptionsJson;
        }
    }
}
