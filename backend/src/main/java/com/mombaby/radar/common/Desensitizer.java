package com.mombaby.radar.common;

/**
 * PII 脱敏工具（#19 compliance，对齐 35.2 / 附录C）。
 * V1.0 简化：手机号/身份证号用占位替换，昵称/评论文本不做深度 NLP 脱敏（保留可读性），
 * 后续版本可按需接入正则+NLP 引擎。
 */
public final class Desensitizer {

    private Desensitizer() {}

    /** 手机号脱敏：前3后4保留，中间4位星号 */
    public static String mobile(String mobile) {
        if (mobile == null || mobile.length() < 7) return mobile;
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }

    /** 身份证号脱敏：前1后1保留，中间星号 */
    public static String idCard(String idCard) {
        if (idCard == null || idCard.length() < 4) return idCard;
        return idCard.charAt(0) + "***************" + idCard.charAt(idCard.length() - 1);
    }

    /** 评论文本脱敏：替换手机号/身份证号模式（简单正则，不破坏可读性） */
    public static String comment(String text) {
        if (text == null || text.isBlank()) return text;
        return text
                .replaceAll("1[3-9]\\d{9}", "<手机号已脱敏>")
                .replaceAll("\\d{15}|\\d{17}[0-9Xx]", "<身份证已脱敏>");
    }
}
