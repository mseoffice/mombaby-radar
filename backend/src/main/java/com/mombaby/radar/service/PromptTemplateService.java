package com.mombaby.radar.service;

import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Prompt 模板管理（12.3 各平台内容规格）。
 * 5 平台差异化 Prompt，确保内容符合各平台调性。
 */
@Service
public class PromptTemplateService {

    private static final Map<String, String> TEMPLATES = Map.of(
        "小红书", """
            你是母婴好物推荐博主。请为商品「{productName}」撰写一篇小红书种草笔记。
            要求：
            - 标题：15-20字，含emoji和关键词
            - 正文：200-300字，分3-4段，使用真实体验口吻
            - 语气：像朋友分享好物，不像商家推销
            - 标签：3-5个热门母婴标签
            - 标注：文末标注「AI辅助生成」
            """,
        "抖音", """
            你是抖音母婴带货达人。请为商品「{productName}」撰写短视频口播脚本。
            要求：
            - 口播：30-50字，口语化、有冲击力
            - 卖点：突出1-2个核心卖点
            - 号召：引导点击购物车
            - 标注：末尾标注「AI辅助生成」
            """,
        "微博", """
            你是母婴好物分享博主。请为商品「{productName}」撰写一条微博。
            要求：
            - 正文：100-150字，简洁有力
            - 配图建议：1-2张商品图
            - 话题：带1-2个热门母婴话题
            - 链接引导：引导查看评论区链接
            - 标注：末尾标注「AI辅助生成」
            """,
        "公众号", """
            你是母婴产品测评公众号编辑。请为商品「{productName}」撰写一篇公众号推文。
            要求：
            - 标题：吸引点击，含数字或疑问
            - 正文：800-1200字，分章节（开箱/使用/对比/建议）
            - 排版：含小标题、引用、要点列表
            - 标注：文首标注「本文由AI辅助生成，人工审核后发布」
            """,
        "微信群", """
            你是母婴社群运营。请为商品「{productName}」撰写一条微信群推送消息。
            要求：
            - 文案：50-80字，亲切口语化
            - 包含：商品名+到手价+购买链接引导
            - 互动：引导群友提问或晒单
            - 标注：末尾标注「AI辅助生成」
            """
    );

    /** 获取指定平台的 Prompt 模板，自动替换商品名 */
    public String buildPrompt(String platform, String productName) {
        String template = TEMPLATES.getOrDefault(platform,
            "请为商品「{productName}」撰写一段营销内容。");
        return template.replace("{productName}", productName);
    }

    /** 获取所有支持的平台 */
    public java.util.Set<String> supportedPlatforms() {
        return TEMPLATES.keySet();
    }
}
