package com.project.coupon.service.implementation;

import com.project.coupon.constants.CouponConstant;
import com.project.coupon.entity.BaseCoupon;
import com.project.coupon.entity.BatchCoupon;
import com.project.coupon.entity.UniqueCoupon;
import com.project.coupon.exceptions.BadApiRequestException;
import com.project.coupon.repository.BaseCouponRepository;
import com.project.coupon.repository.BatchCouponRepository;
import com.project.coupon.repository.UniqueCouponRepository;
import com.project.coupon.request.PosInputEnquiry;
import com.project.coupon.response.PosOutputEnquire;
import com.project.coupon.service.CouponEnquiry;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class CouponEnquiryImplementation implements CouponEnquiry {

    @Autowired
    private UniqueCouponRepository uniqueCouponRepository;

    @Autowired
    private BaseCouponRepository baseCouponRepository;

    @Autowired
    private BatchCouponRepository batchCouponRepository;
    @Override
    public PosOutputEnquire enquireCoupon(PosInputEnquiry posInputRequest) {
        log.info("Starting coupon enquiry for coupon code: {}", posInputRequest.getCouponCode());
        UniqueCoupon uniqueCoupon=null;
        BaseCoupon baseCoupon=null;

        if(uniqueCouponRepository.existsByIdIgnoreCase(posInputRequest.getCouponCode()))
        {
            log.debug("Coupon code found in UniqueCoupon repository: {}", posInputRequest.getCouponCode());
            uniqueCoupon=uniqueCouponRepository.findByIdIgnoreCase(posInputRequest.getCouponCode()).get();
            baseCoupon=baseCouponRepository.findByIdIgnoreCase(uniqueCoupon.getBaseCouponCode()).get();

        }
        else if(batchCouponRepository.existsByIdIgnoreCase(posInputRequest.getCouponCode()))
        {
            log.debug("Coupon code found in BatchCoupon repository: {}", posInputRequest.getCouponCode());
            BatchCoupon batchCoupon=batchCouponRepository.findByIdIgnoreCase(posInputRequest.getCouponCode()).get();
            uniqueCoupon=uniqueCouponRepository.findByIdIgnoreCase(batchCoupon.getUniqueCouponId()).get();
            baseCoupon=baseCouponRepository.findByIdIgnoreCase(uniqueCoupon.getBaseCouponCode()).get();
        }
        else{
            log.error("Invalid Coupon Code: {}", posInputRequest.getCouponCode());
            throw new BadApiRequestException("Invalid Coupon Code");
        }

        PosOutputEnquire posOutputEnquire=PosOutputEnquire.builder().status(CouponConstant.SUCCESS).message("Coupon enquired successfully").discountMax(uniqueCoupon.getDiscountCap())
                .mov(uniqueCoupon.getMov()).discountType(uniqueCoupon.getCouponType()).couponSource(baseCoupon.getBaseCouponCode()).channel(uniqueCoupon.getChannel())
                .fullfillmentMode(uniqueCoupon.getChannelFullfillmentType()).franchise(uniqueCoupon.getFranchise()).store(uniqueCoupon.getStores())
                .netAmount(posInputRequest.getNetAmount()).couponCode(posInputRequest.getCouponCode()).receiptNumber(posInputRequest.getReceiptNumber())
                .startDate(uniqueCoupon.getStartDate()).endDate(uniqueCoupon.getEndDate()).timeslot(uniqueCoupon.getTimeslot()).couponInfo(uniqueCoupon.getTermsAndConditions())
                .build();


        double finalDiscountAmount=0;

        if(uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_PERCENTAGE))
        {
            log.debug("Coupon type is PERCENTAGE_DISCOUNT");
            posOutputEnquire.setDiscountMax(uniqueCoupon.getDiscountCap());
            posOutputEnquire.setCouponValue(uniqueCoupon.getDiscountPercentage());

        }
        else if(uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_FLAT))
        {
            log.debug("Coupon type is FLAT_DISCOUNT");
            finalDiscountAmount=uniqueCoupon.getFlatDiscount();
            posOutputEnquire.setDiscountMax(finalDiscountAmount);
            posOutputEnquire.setCouponValue(finalDiscountAmount);

        }
        else if(uniqueCoupon.getCouponType().equalsIgnoreCase(CouponConstant.COUPONTYPE_FREEBIE))
        {
            log.debug("Coupon type is FREEBIE");
            finalDiscountAmount=0;
            posOutputEnquire.setFreebieItems(uniqueCoupon.getFreebieItems());
            posOutputEnquire.setDiscountMax(finalDiscountAmount);
            posOutputEnquire.setCouponValue(finalDiscountAmount);
        }
        else {
            log.warn("Unknown coupon type: {}", uniqueCoupon.getCouponType());
        }

        log.info("Coupon enquiry completed successfully for coupon code: {}", posInputRequest.getCouponCode());

        return posOutputEnquire;

    }

}
