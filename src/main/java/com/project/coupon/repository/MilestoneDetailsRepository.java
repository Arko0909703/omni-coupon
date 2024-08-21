package com.project.coupon.repository;

import com.project.coupon.entity.MilestoneDetails;
import com.project.coupon.entity.SegmentDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MilestoneDetailsRepository  extends MongoRepository<MilestoneDetails,String> {
    @Query("{ '_id' : { $regex: ?0, $options: 'i' } }")
     Optional<MilestoneDetails> findByIdIgnoreCase(String journeyName);

    List<MilestoneDetails> findBySegmentIn(List<SegmentDetails> segments);


}
