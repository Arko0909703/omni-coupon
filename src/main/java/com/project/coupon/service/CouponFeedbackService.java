package com.project.coupon.service;

import com.project.coupon.request.CouponFeedbackRequest;
import com.project.coupon.response.CouponFeedbackIssueResponse;

public interface CouponFeedbackService {

    CouponFeedbackIssueResponse saveCouponFeedback(CouponFeedbackRequest couponFeedbackRequest) throws Exception;

}
