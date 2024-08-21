package com.project.coupon.repository;

import com.project.coupon.entity.Segment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SegmentRepository extends MongoRepository<Segment,String> {

    List<Segment> findByMobileNumber(String mobileNumber);
}
