package com.mombaby.radar.platform;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 平台同步结果（D2）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformSyncResult {
    /** 平台中文名 */
    private String platform;
    /** 本次同步拉取的条数（Mock 模式为示例值） */
    private int count;
    /** 是否降级：true=未接入真实 API（Mock 占位）/ 非核心平台主动跳过 */
    private boolean degraded;
    /** 说明 */
    private String message;
}
