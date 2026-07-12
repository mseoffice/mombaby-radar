package com.mombaby.radar.service;

import com.mombaby.radar.entity.Product;
import com.mombaby.radar.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> list(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    public Product create(Product product) {
        product.setId(null);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    public Product update(Long id, Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + id));
        existing.setProductName(product.getProductName());
        existing.setBrandId(product.getBrandId());
        existing.setCategoryId(product.getCategoryId());
        existing.setCurrentPrice(product.getCurrentPrice());
        existing.setOriginalPrice(product.getOriginalPrice());
        existing.setLowestPrice(product.getLowestPrice());
        existing.setRecommendScore(product.getRecommendScore());
        existing.setSuitableAge(product.getSuitableAge());
        existing.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(existing);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
