package com.mombaby.radar.agent;

import com.mombaby.radar.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 评论分析/回复 Agent（M3，对应 #14 comment-ops）。
 * 由 Agent 编排引擎按 agentType="comment-reply" 调度，payload 需含 commentId。
 */
@Component
public class CommentReplyAgent implements AgentOrchestrator.AgentHandler {

    private static final Logger log = LoggerFactory.getLogger(CommentReplyAgent.class);

    private final CommentService commentService;

    public CommentReplyAgent(CommentService commentService) {
        this.commentService = commentService;
    }

    @Override
    public void handle(Map<String, Object> payload) {
        Object idObj = payload.get("commentId");
        if (idObj == null) {
            log.warn("comment-reply 缺少 commentId，跳过");
            return;
        }
        Long commentId = Long.valueOf(idObj.toString());
        try {
            commentService.analyze(commentId);
            log.info("comment-reply 分析完成: commentId={}", commentId);
        } catch (Exception e) {
            log.error("comment-reply 分析失败: commentId={}, err={}", commentId, e.getMessage());
        }
    }
}
