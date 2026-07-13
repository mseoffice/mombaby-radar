package com.mombaby.radar.aop;

import java.lang.annotation.*;

/**
 * 操作审计注解（#19 compliance）。
 * 标注在 Service 方法上，由 AuditAspect 自动写入 operation_log。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {
    String action();
    String target() default "";
}
