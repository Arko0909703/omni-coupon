package com.project.coupon.repository;

import com.project.coupon.entity.SegmentData;
import com.project.coupon.entity.SegmentDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SegmentDataRepository extends MongoRepository<SegmentData,String> {

    List<SegmentData> findByMobileNumber(String mobilenumber);

    boolean existsBySegmentAttached(SegmentDetails segmentAttached);

    List<SegmentData> findBySegmentAttached(SegmentDetails segmentDetails);

}
