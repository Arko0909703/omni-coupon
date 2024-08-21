package com.project.coupon.service;

import com.project.coupon.entity.UniqueCoupon;
import com.project.coupon.exceptions.ResponseMessage;
import com.project.coupon.request.CouponUsageRequest;
import com.project.coupon.request.UpdateStatusRequest;
import com.project.coupon.response.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface GetCoupons {

    List<CouponResponseWithNumber> getCouponUsingMobileNumber(String mobileNumber);
    CouponResponseWithCode getCouponUsingCouponCode(String couponCode);

    Page<CouponResponseForFilterCoupons> getFilteredCoupons(LocalDate dateCreated, LocalDate startDate, LocalDate endDate,
                                                            String franchise, String city, String storeCode,
                                                            String couponType, String couponStatus, int page , int size,String couponCategory);

    Page<CouponResponseForFilterCoupons> searchCoupons(String code,String couponCategory,int page ,int size);

    Page<CouponResponseForFilterCoupons> getBaseCoupons(int page,int size);

    Page<CouponResponseForFilterCoupons> getUniqueCoupons(int page,int size);
    Page<CouponResponseForFilterCoupons> getBatchCoupons(int page,int size);

    MilestoneResponse getMilestoneCoupons(String number);

    Page<MilestoneDetailsResponse> getMilestoneDetails(int page,int size);

    ResponseMessage setCouponsStatus(UpdateStatusRequest updateStatusRequest) throws Exception;

    GetCouponUsageResponse getUniqueCouponUsage(String code);

    GetCouponApplicabilityResponse getUniqueCouponApplicability(String code);

    CouponUserSpecificConstraintsResponse getUniqueCouponUserSpecificConstraints(String code);

    UniqueCouponConstructResponse getUniqueCouponConstruts(String code);

    List<CategoryProductResponse> getProductsGroupedByCategory();

    public StoreMasterResponse getOperationalStores();






}
