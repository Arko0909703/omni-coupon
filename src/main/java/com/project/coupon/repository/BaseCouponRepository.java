package com.project.coupon.repository;

import com.project.coupon.entity.BaseCoupon;
import com.project.coupon.entity.UniqueCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BaseCouponRepository extends MongoRepository<BaseCoupon,String> {

    @Query("{ '_id' : { $regex: ?0, $options: 'i' } }")
    Optional<BaseCoupon> findByIdIgnoreCase(String id);
    @Query("{ '_id': { $regex: ?0, $options: 'i' } }")
    Page<BaseCoupon> findByBaseCouponCodeContainingIgnoreCase(String code, Pageable pageable);

    List<BaseCoupon> findAllByBaseCouponCodeIn(List<String> code);
}
