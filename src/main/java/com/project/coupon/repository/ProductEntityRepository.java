package com.project.coupon.repository;

import com.project.coupon.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductEntityRepository extends JpaRepository<ProductEntity, Long> {
    ProductEntity findByProductCode(String productCode);
}
