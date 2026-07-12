package com.mombaby.radar.entity;

import java.util.Arrays;

/**
 * 平台枚举（D2 多平台接入）。
 * label 与内容生成使用的平台名保持一致（小红书/抖音/微博/公众号/微信群）。
 */
public enum Platform {
    XIAOHONGSHU("小红书"),
    DOUYIN("抖音"),
    WEIBO("微博"),
    GONGZHONGHAO("公众号"),
    WEIXIN_GROUP("微信群");

    private final String label;

    Platform(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** 按中文 label 解析；找不到返回 null */
    public static Platform fromLabel(String label) {
        if (label == null) return null;
        return Arrays.stream(values())
                .filter(p -> p.label.equals(label.trim()))
                .findFirst()
                .orElse(null);
    }
}
