package com.project.coupon.repository;

import com.project.coupon.entity.BaseCoupon;
import com.project.coupon.entity.Segment;
import com.project.coupon.entity.SegmentDetails;
import com.project.coupon.entity.UniqueCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UniqueCouponRepository extends MongoRepository<UniqueCoupon,String> {

    @Query("{ '_id' : { $regex: ?0, $options: 'i' } }")
    Optional<UniqueCoupon> findByIdIgnoreCase(String id);
    @Query(value = "{ '_id' : { $regex: ?0, $options: 'i' } }", exists = true)
    boolean existsByIdIgnoreCase(String id);
    @Query("{ 'applicableForValue' : { $regex: ?0, $options: 'i' } }")
    List<UniqueCoupon> findByApplicableForValueIgnoreCaseAndHiddenUI(String applicableForValue, boolean hiddenUI);

    List<UniqueCoupon> findByApplicableForSegmentsInAndCouponUsageIgnoreCaseAndHiddenUI(List<SegmentDetails> segments, String couponUsage, boolean hiddenUI);
    @Query("{ 'type' : { $regex: ?0, $options: 'i' } }")
    List<UniqueCoupon> findByTypeIgnoreCase(String type);

    @Query("{ '_id' : { $regex: ?0, $options: 'i' } }")
    Page<UniqueCoupon> findByIdContainingIgnoreCase(String code, Pageable pageable);
    @Query("{ 'baseCouponCode' : { $regex: ?0, $options: 'i' } }")
    List<UniqueCoupon> findByBaseCouponCodeIgnoreCase(String baseCouponCode);

    List<UniqueCoupon> findAllByUniqueCouponCodeIn(List<String> code);


    UniqueCoupon findByUniqueCouponCode(String couponName);
}
