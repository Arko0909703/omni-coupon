package com.project.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.coupon.entity.MySQLBaseCouponEntity;
import com.project.coupon.user.entity.UserEntity;

import java.util.List;

public interface CouponRepository extends JpaRepository<MySQLBaseCouponEntity, Long>{
	public MySQLBaseCouponEntity findByCouponName(String couponName);
	public MySQLBaseCouponEntity findByBaseCouponId(String baseCouponId);
	List<MySQLBaseCouponEntity> findAllByBaseCouponIdIn(List<String> baseCouponIds);
	
}