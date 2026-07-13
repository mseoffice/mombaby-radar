package com.mombaby.radar.scheduler;

import com.mombaby.radar.repository.OperationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 操作日志定期清理（#19 compliance，35.2）。
 * 保留 6 个月，每日凌晨 2:00 清理过期记录。
 */
@Component
public class OperationLogCleaner {

    private static final Logger log = LoggerFactory.getLogger(OperationLogCleaner.class);

    private final OperationLogRepository operationLogRepository;

    public OperationLogCleaner(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨 2:00
    public void clean() {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        operationLogRepository.deleteByCreatedAtBefore(sixMonthsAgo);
        log.info("已清理 {} 前的操作日志（保留 6 个月）", sixMonthsAgo.toLocalDate());
    }
}
