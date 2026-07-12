package com.mombaby.radar.platform;

import com.mombaby.radar.entity.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 平台接入网关实现（D2）。
 *
 * <p>限流退避：对每平台做简单令牌桶式限流（最小调用间隔），避免开发/测试期高频打真实 API。
 * 降级：{@code platform.api.enabled=false}（默认，开发期）时所有平台走 Mock；非核心平台（如微信群）即便开启也主动降级跳过。
 */
@Service
public class PlatformGatewayImpl implements PlatformGateway {

    private static final Logger log = LoggerFactory.getLogger(PlatformGatewayImpl.class);

    /** 是否接入真实平台 API；默认 false（开发期 Mock）。测试期置 true 并填各平台凭据。 */
    @Value("${platform.api.enabled:false}")
    private boolean apiEnabled;

    /** 非核心平台（即便开启真实 API 也降级跳过，保证主链路可用） */
    private static final Platform NON_CORE = Platform.WEIXIN_GROUP;

    /** 每平台最小调用间隔（ms），用于限流退避 */
    private static final long MIN_INTERVAL_MS = 1000;
    private final Map<Platform, Long> lastCallAt = new ConcurrentHashMap<>();

    @Override
    public PlatformSyncResult syncComments(Platform platform) {
        return doSync(platform, "评论");
    }

    @Override
    public PlatformSyncResult syncData(Platform platform) {
        return doSync(platform, "数据");
    }

    private PlatformSyncResult doSync(Platform platform, String kind) {
        // 限流退避：低于最小间隔则降级，避免打爆平台
        long now = System.currentTimeMillis();
        Long last = lastCallAt.get(platform);
        if (last != null && now - last < MIN_INTERVAL_MS) {
            return new PlatformSyncResult(platform.getLabel(), 0, true,
                    "限流退避中（" + kind + "），稍后重试");
        }
        lastCallAt.put(platform, now);

        // 非核心平台降级跳过
        if (platform == NON_CORE) {
            return new PlatformSyncResult(platform.getLabel(), 0, true,
                    "非核心平台降级跳过（" + kind + "）");
        }

        // 未开启真实 API：返回 Mock 同步结果
        if (!apiEnabled) {
            int mockCount = 8; // 示例值
            log.info("[platform-gateway] {} {} 同步（Mock 模式）count={}", platform.getLabel(), kind, mockCount);
            return new PlatformSyncResult(platform.getLabel(), mockCount, true,
                    "Mock 模式（未接入真实平台 API）；测试期置 platform.api.enabled=true 并填凭据");
        }

        // TODO: 测试期接入真实平台开放 API（小红书/抖音/微博/微信），替换下方占位
        int realCount = fetchFromPlatform(platform, kind);
        return new PlatformSyncResult(platform.getLabel(), realCount, false, "真实 API 同步");
    }

    /** 真实平台拉取占位（测试期实现） */
    private int fetchFromPlatform(Platform platform, String kind) {
        // 各平台开放 API 鉴权/分页/限流处理在测试期补齐
        throw new UnsupportedOperationException(
                "平台真实 API 拉取未实现（" + platform.getLabel() + "/" + kind + "）；请置 platform.api.enabled=false 走 Mock");
    }
}
