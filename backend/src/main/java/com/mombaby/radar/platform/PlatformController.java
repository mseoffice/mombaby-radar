package com.mombaby.radar.platform;

import com.mombaby.radar.entity.Platform;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 平台接入网关接口（D2）。
 * 端点（含 context-path /api）：POST /api/platforms/{plat}/sync
 */
@RestController
@RequestMapping("/platforms")
public class PlatformController {

    private final PlatformGateway platformGateway;

    public PlatformController(PlatformGateway platformGateway) {
        this.platformGateway = platformGateway;
    }

    @PostMapping("/{plat}/sync")
    public ResponseEntity<PlatformSyncResult> sync(
            @PathVariable("plat") String plat,
            @RequestParam(defaultValue = "comments") String type) {
        Platform platform = Platform.fromLabel(plat);
        if (platform == null) {
            return ResponseEntity.badRequest().body(
                    new PlatformSyncResult(plat, 0, true, "未知平台：" + plat));
        }
        PlatformSyncResult result = "data".equalsIgnoreCase(type)
                ? platformGateway.syncData(platform)
                : platformGateway.syncComments(platform);
        return ResponseEntity.ok(result);
    }
}
