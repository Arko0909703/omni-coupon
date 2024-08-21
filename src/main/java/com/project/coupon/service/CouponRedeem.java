package com.project.coupon.service;


import com.project.coupon.request.PosInputRequest;
import com.project.coupon.response.PosOutputResponse;

public interface CouponRedeem {

    PosOutputResponse redeemCoupon(PosInputRequest posInputDto) throws Exception;

}
