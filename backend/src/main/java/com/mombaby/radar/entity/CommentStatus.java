package com.mombaby.radar.entity;

/**
 * 评论状态机（设计视角 2.2）。
 * NEW → ANALYZED → REPLIED / PENDING
 */
public enum CommentStatus {
    NEW,
    ANALYZED,
    REPLIED,
    PENDING;

    /** 是否允许从当前状态迁移到目标状态 */
    public boolean canTransitionTo(CommentStatus target) {
        return switch (this) {
            case NEW       -> target == ANALYZED;
            case ANALYZED  -> target == REPLIED || target == PENDING;
            case PENDING   -> target == ANALYZED;     // 重新分析
            case REPLIED   -> false;                   // 终态
        };
    }
}
