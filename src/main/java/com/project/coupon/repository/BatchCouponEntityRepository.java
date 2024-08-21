package com.project.coupon.repository;

import com.project.coupon.entity.BatchCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchCouponEntityRepository extends JpaRepository<BatchCouponEntity, Long> {

}