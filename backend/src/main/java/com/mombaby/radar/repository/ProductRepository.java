package com.mombaby.radar.repository;

import com.mombaby.radar.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByBrandId(Long brandId);

    List<Product> findByProductNameContaining(String keyword);
}
