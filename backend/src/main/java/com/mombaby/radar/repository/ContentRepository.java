package com.mombaby.radar.repository;

import com.mombaby.radar.entity.Content;
import com.mombaby.radar.entity.ContentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByProductId(Long productId);

    List<Content> findByStatus(ContentStatus status);

    List<Content> findByPlatformAndStatus(String platform, ContentStatus status);
}
