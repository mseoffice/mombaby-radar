package com.mombaby.radar.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Spring AI 配置（D2 落地）。
 *
 * <p>环境分层策略：
 * <ul>
 *   <li>开发期（profile=dev，默认）：启用 {@link #mockChatModel()}（@Primary），不依赖任何外部 Key，全流程本地可跑通。</li>
 *   <li>测试/预发期（profile=prod + DEEPSEEK_API_KEY）：{@code spring.ai.openai.chat.enabled=true}，
 *       由 Spring AI 自动配置基于 DeepSeek 兼容端点的真实 {@link ChatModel}，Mock Bean 不加载。</li>
 * </ul>
 *
 * <p>生产主选 LLM 为 DeepSeek（兼容 OpenAI 协议），通过 {@code spring.ai.openai.base-url/api-key/model} 接入，
 * 复用 spring-ai-openai starter，无需更换框架。百度/阿里等多提供方留作后续扩展点。
 */
@Configuration
public class AiConfig {

    /**
     * Mock ChatModel — 仅在 dev profile 激活，返回示例母婴内容/评论分析，便于开发不依赖真实 API。
     */
    @Bean
    @Primary
    @Profile("dev")
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

                // 评论分析类 prompt：返回结构化 Mock，便于 dev 联调 analyze 接口
                if (text.contains("评论分析") || text.contains("analyze") || text.contains("分类")) {
                    String mockAnalysis = "{"
                            + "\"type\":\"咨询\","
                            + "\"sentiment\":\"中性\","
                            + "\"intent\":\"了解商品用法/价格\","
                            + "\"replies\":["
                            + "\"感谢宝妈关注～这款产品适合0-3岁宝宝，安全性已通过检测，可以放心入手哦。\","
                            + "\"宝子可以看下详情页的适龄说明，有疑问随时私信我，帮你一对一解答。\","
                            + "\"已私信您专属优惠链接，到手价更划算，记得领取～\"]"
                            + "}";
                    var gen = new org.springframework.ai.chat.model.Generation(
                            new org.springframework.ai.chat.messages.AssistantMessage(mockAnalysis));
                    return new ChatResponse(List.of(gen));
                }

                String mockBody = switch (platform) {
                    case "小红书" -> "✨ 宝妈必入！${商品名}真的太香了！\n\n用了两周来反馈：\n① 材质超柔软，宝宝不抗拒\n② 性价比绝了，比旗舰店便宜30+\n③ 回购指数⭐⭐⭐⭐⭐\n\n#母婴好物 #育儿必备 #真实测评";
                    case "抖音"   -> "姐妹们！这个${商品名}我真的吹爆！🔥🔥🔥\n点击购物车，手慢无！#母婴 #好物推荐";
                    case "微博"   -> "【好物分享】${商品名}用了一周的真实感受：适合0-3岁宝宝，安全无味。链接见评论区👇";
                    case "公众号" -> "## ${商品名}深度测评\n\n> 本文由AI辅助生成，人工审核后发布\n\n### 一、开箱体验\n...\n### 二、使用对比\n...\n### 三、购买建议\n性价比极高，推荐入手。";
                    case "微信群" -> "📢 今日好价：${商品名}\n💰 到手价：XX元（历史最低YY元）\n🔗 购买链接见群公告\n💬 有疑问随时@我～";
                    default      -> "[Mock] AI 内容生成中...";
                };

                var gen = new org.springframework.ai.chat.model.Generation(
                        new org.springframework.ai.chat.messages.AssistantMessage(mockBody));
                return new ChatResponse(List.of(gen));
            }

            @Override public Flux<ChatResponse> stream(Prompt prompt) { return Flux.just(call(prompt)); }
            @Override public org.springframework.ai.chat.prompt.ChatOptions getDefaultOptions() { return null; }
        };
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
