package com.project.coupon.service;

import com.project.coupon.request.PosInputEnquiry;
import com.project.coupon.request.PosInputRequest;
import com.project.coupon.response.PosOutputEnquire;
import com.project.coupon.response.PosOutputResponse;

public interface CouponEnquiry {

    PosOutputEnquire enquireCoupon(PosInputEnquiry posInputDto);


}
