package com.mombaby.radar.entity;

/**
 * Content 状态机枚举（34.2.6）：
 *   GENERATING → PENDING_REVIEW → APPROVED → PUBLISHED → ARCHIVED
 *   PENDING_REVIEW → REJECTED → GENERATING
 */
public enum ContentStatus {

    GENERATING {
        @Override
        public boolean canTransitionTo(ContentStatus target) {
            return target == PENDING_REVIEW;
        }
    },
    PENDING_REVIEW {
        @Override
        public boolean canTransitionTo(ContentStatus target) {
            return target == APPROVED || target == REJECTED;
        }
    },
    APPROVED {
        @Override
        public boolean canTransitionTo(ContentStatus target) {
            return target == PUBLISHED;
        }
    },
    PUBLISHED {
        @Override
        public boolean canTransitionTo(ContentStatus target) {
            return target == ARCHIVED;
        }
    },
    REJECTED {
        @Override
        public boolean canTransitionTo(ContentStatus target) {
            return target == GENERATING;
        }
    },
    ARCHIVED {
        @Override
        public boolean canTransitionTo(ContentStatus target) {
            return false; // 终态
        }
    };

    public abstract boolean canTransitionTo(ContentStatus target);
}
