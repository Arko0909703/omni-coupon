package com.project.coupon.repository;

import com.project.coupon.entity.SegmentDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SegmentDetailsRepository extends MongoRepository<SegmentDetails,String> {

        boolean existsBySegmentNameIgnoreCase(String name);



}
