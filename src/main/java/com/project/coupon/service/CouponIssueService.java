package com.project.coupon.service;

import com.project.coupon.request.CouponIssueRequest;
import com.project.coupon.response.CouponFeedbackIssueResponse;

public interface CouponIssueService {

    CouponFeedbackIssueResponse saveCouponIssue(CouponIssueRequest couponIssueRequest) throws Exception;
}
