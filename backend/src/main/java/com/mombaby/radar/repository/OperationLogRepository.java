package com.mombaby.radar.repository;

import com.mombaby.radar.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    /** 清理 6 个月前的过期日志（35.2） */
    void deleteByCreatedAtBefore(LocalDateTime before);
}
