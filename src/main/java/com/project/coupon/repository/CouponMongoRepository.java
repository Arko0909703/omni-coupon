package com.project.coupon.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.coupon.entity.CouponMongoEntity;
@Repository
public interface CouponMongoRepository extends MongoRepository<CouponMongoEntity, String>{

}
