package com.mombaby.radar.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Spring AI 配置。
 * Mock 模式：ChatModel 返回预设示例内容。
 * TODO: API Key 就绪后删除 Mock Bean，改用 OpenAI 自动配置。
 */
@Configuration
public class AiConfig {

    /**
     * Mock ChatModel — 返回示例母婴内容，便于 M2 开发不依赖真实 API。
     */
    @Bean
    @Primary
    public ChatModel mockChatModel() {
        return new ChatModel() {
            @Override
            public ChatResponse call(Prompt prompt) {
                String text = prompt.getInstructions().stream()
                        .map(Object::toString)
                        .reduce("", (a, b) -> a + b);
                String platform = text.contains("小红书") ? "小红书" :
                                  text.contains("抖音")   ? "抖音"   :
                                  text.contains("微博")   ? "微博"   :
                                  text.contains("公众号") ? "公众号" :
                                  text.contains("微信群") ? "微信群" : "通用";

                String mockBody = switch (platform) {
                    case "小红书" -> "✨ 宝妈必入！${商品名}真的太香了！\n\n用了两周来反馈：\n① 材质超柔软，宝宝不抗拒\n② 性价比绝了，比旗舰店便宜30+\n③ 回购指数⭐⭐⭐⭐⭐\n\n#母婴好物 #育儿必备 #真实测评";
                    case "抖音"   -> "姐妹们！这个${商品名}我真的吹爆！🔥🔥🔥\n点击购物车，手慢无！#母婴 #好物推荐";
                    case "微博"   -> "【好物分享】${商品名}用了一周的真实感受：适合0-3岁宝宝，安全无味。链接见评论区👇";
                    case "公众号" -> "## ${商品名}深度测评\n\n> 本文由AI辅助生成，人工审核后发布\n\n### 一、开箱体验\n...\n### 二、使用对比\n...\n### 三、购买建议\n性价比极高，推荐入手。";
                    case "微信群" -> "📢 今日好价：${商品名}\n💰 到手价：XX元（历史最低YY元）\n🔗 购买链接见群公告\n💬 有疑问随时@我～";
                    default      -> "[Mock] AI 内容生成中...";
                };

                var gen = new org.springframework.ai.chat.model.Generation(
                        new org.springframework.ai.chat.message.AssistantMessage(mockBody));
                return new ChatResponse(List.of(gen));
            }

            @Override public Flux<ChatResponse> stream(Prompt prompt) { return Flux.just(call(prompt)); }
            @Override public org.springframework.ai.chat.model.ChatOptions getDefaultOptions() { return null; }
        };
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
