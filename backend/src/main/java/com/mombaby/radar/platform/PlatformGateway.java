package com.mombaby.radar.platform;

import com.mombaby.radar.entity.Platform;

/**
 * 平台接入网关（D2）。
 * 统一封装多平台（小红书/抖音/微博/公众号/微信群）的评论/数据同步、鉴权、限流退避与降级。
 *
 * <p>V1.0 实现：开发期不接真实平台 Key，内部走可降级 Mock（{@code degraded=true} 表示未接入真实 API）；
 * 测试期在 {@code application-prod.yml} / 环境变量中填各平台真实凭据后，替换 {@link #doSync} 内部实现即可，
 * 接口契约与上层调用方不变。
 */
public interface PlatformGateway {

    /** 同步指定平台的评论（D3 可选 API 采集路径依赖此能力） */
    PlatformSyncResult syncComments(Platform platform);

    /** 同步指定平台的公开数据（好价/爆文等） */
    PlatformSyncResult syncData(Platform platform);
}
