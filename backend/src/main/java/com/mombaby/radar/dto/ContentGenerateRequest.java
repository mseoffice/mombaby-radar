package com.mombaby.radar.dto;

import java.util.List;

public class ContentGenerateRequest {
    private Long productId;
    private String productName;
    private List<String> platforms;    // ["小红书","抖音","微博","公众号","微信群"]

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public List<String> getPlatforms() { return platforms; }
    public void setPlatforms(List<String> platforms) { this.platforms = platforms; }
}
