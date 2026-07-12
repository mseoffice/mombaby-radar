package com.mombaby.radar.service;

import com.mombaby.radar.entity.Content;
import com.mombaby.radar.entity.ContentReviewLog;
import com.mombaby.radar.entity.ContentStatus;
import com.mombaby.radar.repository.ContentRepository;
import com.mombaby.radar.repository.ContentReviewLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentReviewLogRepository reviewLogRepository;

    public ContentService(ContentRepository contentRepository,
                          ContentReviewLogRepository reviewLogRepository) {
        this.contentRepository = contentRepository;
        this.reviewLogRepository = reviewLogRepository;
    }

    public Page<Content> list(Pageable pageable) {
        return contentRepository.findAll(pageable);
    }

    public Optional<Content> getById(Long id) {
        return contentRepository.findById(id);
    }

    public Content create(Content content) {
        content.setId(null);
        content.setStatus(ContentStatus.GENERATING);
        content.setCreatedAt(LocalDateTime.now());
        content.setUpdatedAt(LocalDateTime.now());
        return contentRepository.save(content);
    }

    /** 人工通过：PENDING_REVIEW → APPROVED，写入审核日志 */
    public Content approve(Long id, Long reviewerId, String opinion) {
        Content c = getOrThrow(id);
        c.transitionTo(ContentStatus.APPROVED);
        Content saved = contentRepository.save(c);
        writeReviewLog(id, reviewerId, "approved", opinion);
        return saved;
    }

    /** 人工驳回：PENDING_REVIEW → REJECTED，写入审核日志 */
    public Content reject(Long id, Long reviewerId, String opinion) {
        Content c = getOrThrow(id);
        c.transitionTo(ContentStatus.REJECTED);
        Content saved = contentRepository.save(c);
        writeReviewLog(id, reviewerId, "rejected", opinion);
        return saved;
    }

    /** 重新生成：REJECTED → GENERATING */
    public Content regenerate(Long id) {
        Content c = getOrThrow(id);
        c.transitionTo(ContentStatus.GENERATING);
        return contentRepository.save(c);
    }

    /** 标记已发布：APPROVED → PUBLISHED */
    public Content publish(Long id) {
        Content c = getOrThrow(id);
        c.transitionTo(ContentStatus.PUBLISHED);
        return contentRepository.save(c);
    }

    /** 下线/归档：PUBLISHED → ARCHIVED */
    public Content archive(Long id) {
        Content c = getOrThrow(id);
        c.transitionTo(ContentStatus.ARCHIVED);
        return contentRepository.save(c);
    }

    private Content getOrThrow(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + id));
    }

    private void writeReviewLog(Long contentId, Long reviewerId, String action, String opinion) {
        ContentReviewLog log = new ContentReviewLog();
        log.setContentId(contentId);
        log.setReviewerId(reviewerId);
        log.setAction(action);
        log.setOpinion(opinion);
        log.setCreatedAt(LocalDateTime.now());
        reviewLogRepository.save(log);
    }
}
