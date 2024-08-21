package com.project.coupon.repository;

import com.project.coupon.entity.MilestoneDetails;
import com.project.coupon.entity.Segment;
import com.project.coupon.entity.SegmentDetails;
import com.project.coupon.entity.SegmentMilestoneMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SegmentMilestoneMappingRepository  extends MongoRepository<SegmentMilestoneMapping,String> {

//    List<SegmentMilestoneMapping> findBySegment(Segment segment);
//
//    List<SegmentMilestoneMapping> findBySegmentIn(List<SegmentDetails> segments);

    List<SegmentMilestoneMapping> findByMilestoneDetailsIn(List<MilestoneDetails> segments);

}
