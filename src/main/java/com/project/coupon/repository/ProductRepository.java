package com.project.coupon.repository;

import com.project.coupon.entity.Products;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Products,Long> {
    Products findByProductCode(String productCode);
}
