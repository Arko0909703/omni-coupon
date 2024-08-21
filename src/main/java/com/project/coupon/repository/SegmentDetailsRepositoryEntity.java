package com.project.coupon.repository;

import com.project.coupon.entity.SegmentDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SegmentDetailsRepositoryEntity extends JpaRepository<SegmentDetailsEntity, Long> {
    boolean existsBySegmentNameIgnoreCase(String name);
}
