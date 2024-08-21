package com.project.coupon.service;

import com.project.coupon.request.PosInputCancel;
import com.project.coupon.request.PosInputRequest;
import com.project.coupon.response.PosOutputCancel;
import com.project.coupon.response.PosOutputEnquire;

public interface CouponCancellation {

    PosOutputCancel cancelCoupon(PosInputCancel posInputDto) throws Exception;
}
