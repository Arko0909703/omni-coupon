package com.project.coupon.repository;
import com.project.coupon.entity.ProductsExclusion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductExclusionRepository extends MongoRepository<ProductsExclusion,Long> {

}
