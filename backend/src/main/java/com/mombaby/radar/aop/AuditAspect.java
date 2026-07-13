package com.mombaby.radar.aop;

import com.mombaby.radar.entity.OperationLog;
import com.mombaby.radar.repository.OperationLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 操作审计切面（#19 compliance，35.2）。
 * 对标注 @AuditLog 的 Service 方法自动写入 operation_log。
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    private final OperationLogRepository operationLogRepository;

    public AuditAspect(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Around("@annotation(auditLog)")
    public Object audit(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        Object result;
        boolean success = true;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            if (success) {
                writeLog(auditLog.action(), auditLog.target(), joinPoint);
            }
        }
        return result;
    }

    private void writeLog(String action, String target, ProceedingJoinPoint joinPoint) {
        try {
            OperationLog opLog = new OperationLog();
            opLog.setAction(action);
            opLog.setTarget(target);
            opLog.setIp(getClientIp());
            opLog.setCreatedAt(LocalDateTime.now());
            // 尝试从方法参数获取 userId（第一参为 userId 或 Long id）
            Long userId = extractUserId(joinPoint);
            opLog.setUserId(userId);
            operationLogRepository.save(opLog);
        } catch (Exception e) {
            log.warn("操作审计写入失败: action={} target={} err={}", action, target, e.getMessage());
        }
    }

    private Long extractUserId(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) return null;
        for (Object arg : args) {
            if (arg instanceof Long) return (Long) arg;
        }
        return null;
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "unknown";
            HttpServletRequest req = attrs.getRequest();
            String ip = req.getHeader("X-Forwarded-For");
            if (ip == null || ip.isBlank()) ip = req.getRemoteAddr();
            return ip;
        } catch (Exception e) {
            return "unknown";
        }
    }
}
