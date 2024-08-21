package com.project.coupon.jpaRepositories;

import com.project.coupon.jpaEntities.CouponFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponFeedbackRepository extends JpaRepository<CouponFeedback,String> {
}
