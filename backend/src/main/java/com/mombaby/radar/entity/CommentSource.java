package com.mombaby.radar.entity;

/**
 * 评论/私信采集来源（D3）。
 * <ul>
 *   <li>MANUAL：手动粘贴录入（V1.0 主路径）</li>
 *   <li>PLATFORM_API：平台 API 自动采集（可选项，后续迭代，依赖 platform-gateway）</li>
 * </ul>
 */
public enum CommentSource {
    MANUAL,
    PLATFORM_API
}
