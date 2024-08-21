package com.project.coupon.repository;

import com.project.coupon.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreEntityRepository extends JpaRepository<StoreEntity, Long> {
    List<StoreEntity> findByCouponName(String couponName);
}
