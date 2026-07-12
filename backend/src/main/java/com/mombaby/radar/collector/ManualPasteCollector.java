package com.mombaby.radar.collector;

import com.mombaby.radar.entity.Comment;
import com.mombaby.radar.entity.CommentSource;
import com.mombaby.radar.entity.CommentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 手动粘贴录入采集器（D3，V1.0 主路径）。
 * 运营在「评论收件箱」手动粘贴评论/私信文本，不走任何外部平台 API，合规与外部依赖风险最低。
 */
@Component("manualPasteCollector")
public class ManualPasteCollector implements Collector {

    @Override
    public Comment collect(String platform, String rawText) {
        Comment c = new Comment();
        c.setPlatform(platform);
        c.setRawText(rawText);
        c.setSource(CommentSource.MANUAL);
        c.setStatus(CommentStatus.NEW);
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        return c;
    }
}
