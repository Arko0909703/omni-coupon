package com.project.coupon.repository;

import com.project.coupon.entity.Store;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StoreRepository extends MongoRepository<Store, String> {
    List<Store> findByCouponCode(String couponCode);
}