package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProduct_ProductId(Integer productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductImage pi WHERE pi.product.productId = :productId")
    void deleteByProductId(Integer productId);
}

