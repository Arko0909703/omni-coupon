package com.project.coupon.service;

import com.project.coupon.request.PosInputRequest;
import com.project.coupon.request.PosInputSettle;
import com.project.coupon.response.PosOutputEnquire;
import com.project.coupon.response.PosOutputSettle;

public interface CouponSettlement {

    PosOutputSettle settleCoupon(PosInputSettle posInputDto) throws Exception;

}
