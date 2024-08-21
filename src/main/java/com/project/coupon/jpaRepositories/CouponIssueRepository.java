package com.project.coupon.jpaRepositories;

import com.project.coupon.jpaEntities.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssueRepository extends JpaRepository<CouponIssue,String> {
}
