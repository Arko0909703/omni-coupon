package com.project.coupon.repository;


import com.project.coupon.entity.BaseCoupon;
import com.project.coupon.entity.BatchCoupon;
import com.project.coupon.entity.UniqueCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BatchCouponRepository extends MongoRepository<BatchCoupon,String> {

    @Query("{ '_id' : { $regex: ?0, $options: 'i' } }")
    Optional<BatchCoupon> findByIdIgnoreCase(String id);
    @Query("{ 'lotName' : { $regex: ?0, $options: 'i' } }")
    List<BatchCoupon> findByLotNameIgnoreCase(String lotName);

    @Query("{ '_id' : { $regex: ?0, $options: 'i' } }")
   Page<BatchCoupon> findByBatchIdContainingIgnoreCase(String code, Pageable pageable);

    @Query("{ 'uniqueCouponId' : { $regex: ?0, $options: 'i' } }")
   List<BatchCoupon> findByUniqueCouponIdIgnoreCase(String code);


    @Query(value = "{ '_id' : { $regex: ?0, $options: 'i' } }", exists = true)
    boolean existsByIdIgnoreCase(String id);

    List<BatchCoupon> findAllByBatchIdIn(List<String> code);

    List<BatchCoupon> findByMobileNumber(String number);


}
