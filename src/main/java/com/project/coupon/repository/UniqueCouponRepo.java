package com.project.coupon.repository;

import com.project.coupon.entity.ItemsEntity;
import com.project.coupon.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import com.project.coupon.entity.UniqueCouponEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
@Repository
public interface UniqueCouponRepo extends JpaRepository<UniqueCouponEntity, Integer>{

	public UniqueCouponEntity findByCouponName(String couponName);
	List<UniqueCouponEntity> findAllByBaseCouponCodeIn(List<String> baseCouponCodes); // Use baseCouponCode


    List<ProductEntity> findProductInclusionsByCouponName(String couponName);

	List<ItemsEntity> findFreebieItemsByCouponName(String couponName);


}
