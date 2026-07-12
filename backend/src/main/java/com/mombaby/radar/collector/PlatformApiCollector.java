package com.mombaby.radar.collector;

import com.mombaby.radar.entity.Comment;
import com.mombaby.radar.entity.Platform;
import com.mombaby.radar.platform.PlatformGateway;
import org.springframework.stereotype.Component;

/**
 * 平台 API 自动采集器（D3，可选项 / 预留）。
 *
 * <p>依赖 {@link PlatformGateway} 拉取真实平台评论/私信。V1.0 不启用（默认走手动粘贴）；
 * 测试期接入真实平台 API 后，将 {@code doFetch} 替换为平台开放 API 调用并逐条构建 Comment 即可。
 */
@Component("platformApiCollector")
public class PlatformApiCollector implements Collector {

    private final PlatformGateway platformGateway;

    public PlatformApiCollector(PlatformGateway platformGateway) {
        this.platformGateway = platformGateway;
    }

    @Override
    public Comment collect(String platform, String rawText) {
        // 预留：用 platformGateway.syncComments(Platform.fromLabel(platform)) 拉取并逐条构建 Comment
        throw new UnsupportedOperationException(
                "平台 API 自动采集为可选项，V1.0 未启用；请使用手动粘贴录入，或测试期接入真实平台 API 后启用本采集器");
    }
}
