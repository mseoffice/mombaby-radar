package com.mombaby.radar.repository;

import com.mombaby.radar.entity.ContentReviewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentReviewLogRepository extends JpaRepository<ContentReviewLog, Long> {
    List<ContentReviewLog> findByContentIdOrderByCreatedAtDesc(Long contentId);
}
