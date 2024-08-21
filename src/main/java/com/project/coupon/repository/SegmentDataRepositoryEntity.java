package com.project.coupon.repository;

import com.project.coupon.entity.SegmentDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SegmentDataRepositoryEntity extends JpaRepository<SegmentDataEntity, Long> {
}
