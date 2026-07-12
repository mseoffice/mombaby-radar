package com.mombaby.radar.collector;

import com.mombaby.radar.entity.Comment;

/**
 * 采集器抽象（D3）。
 * 不同采集方式实现同一接口，便于 V1.0 以手动粘贴为主、后续平滑接入平台 API 自动采集。
 */
public interface Collector {

    /**
     * 采集一条评论/私信。
     * @param platform 平台中文名
     * @param rawText  原始文本
     * @return 尚未持久化的 Comment（由调用方保存）
     */
    Comment collect(String platform, String rawText);
}
