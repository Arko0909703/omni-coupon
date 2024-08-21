package com.project.coupon.controller;

import com.project.coupon.jpaEntities.IssueQuestion;
import com.project.coupon.request.*;
import com.project.coupon.response.*;
import com.project.coupon.service.*;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@Log4j2
public class RedeemCouponController {
    @Autowired
    CouponRedeem couponRedeemService;

    @Autowired
    CouponEnquiry couponEnquiryService;

    @Autowired
    CouponCancellation couponCancellationService;

    @Autowired
    CouponSettlement couponSettlementService;

    @Autowired
    CouponIssueService couponIssueService;

    @Autowired
    CouponFeedbackService couponFeedbackService;

    @Autowired
    IssueQuestionService issueQuestionService;

    @PostMapping("/redeem-coupon/{couponCode}")
    public ResponseEntity<PosOutputResponse> redeemCoupon(@Valid @RequestBody PosInputRequest posInputRequest,@PathVariable String couponCode) throws Exception {
        posInputRequest.setCouponCode(couponCode);
        //System.out.println("Pos Input Request data: "+ posInputRequest.toString());
        PosOutputResponse posOutputResponse= couponRedeemService.redeemCoupon(posInputRequest);
        return new ResponseEntity<>(posOutputResponse, HttpStatus.OK);
    }

    @PostMapping("/redeemcoupon")
    public ResponseEntity<PosOutputResponse> redeemCouponForSapphire(@Valid @RequestBody PosInputRequest posInputRequest) throws Exception {
        PosOutputResponse posOutputResponse= couponRedeemService.redeemCoupon(posInputRequest);
        return new ResponseEntity<>(posOutputResponse, HttpStatus.OK);
    }


    @PostMapping("/enquiry-coupon/{couponCode}")
    public ResponseEntity<PosOutputEnquire> enquireCoupon(@Valid @RequestBody PosInputEnquiry posInputRequest, @PathVariable String couponCode)
    {
        posInputRequest.setCouponCode(couponCode);
        //System.out.println("Pos Input Request data: "+ posInputRequest.toString());
        PosOutputEnquire posOutputEnquire= couponEnquiryService.enquireCoupon(posInputRequest);
        return new ResponseEntity<>(posOutputEnquire, HttpStatus.OK);
    }

    @PostMapping("/enquirycoupon")
    public ResponseEntity<PosOutputEnquire> enquireCouponForSapphire(@Valid @RequestBody PosInputEnquiry posInputRequest)
    {
        PosOutputEnquire posOutputEnquire= couponEnquiryService.enquireCoupon(posInputRequest);
        return new ResponseEntity<>(posOutputEnquire, HttpStatus.OK);
    }

    @PostMapping("cancellation-coupon/{couponCode}")
    public ResponseEntity<PosOutputCancel> settleCoupon(@Valid @RequestBody PosInputCancel posInputRequest, @PathVariable String couponCode) throws Exception {
        posInputRequest.setCouponCode(couponCode);
        //System.out.println("Pos Input Request data: "+ posInputRequest.toString());
        PosOutputCancel posOutputCancel= couponCancellationService.cancelCoupon(posInputRequest);
        return new ResponseEntity<>(posOutputCancel, HttpStatus.OK);
    }

    @PostMapping("/cancellationcoupon")
    public ResponseEntity<PosOutputCancel> settleCouponForSapphire(@Valid @RequestBody PosInputCancel posInputRequest) throws Exception {
        PosOutputCancel posOutputCancel= couponCancellationService.cancelCoupon(posInputRequest);
        return new ResponseEntity<>(posOutputCancel, HttpStatus.OK);
    }

    @PostMapping("settlement-coupon/{couponCode}")
    public ResponseEntity<PosOutputSettle> cancelCoupon(@Valid @RequestBody PosInputSettle posInputRequest, @PathVariable String couponCode) throws Exception {
        posInputRequest.setCouponCode(couponCode);
        //System.out.println("Pos Input Request data: "+ posInputRequest.toString());
        PosOutputSettle posOutputSettle= couponSettlementService.settleCoupon(posInputRequest);
        return new ResponseEntity<>(posOutputSettle, HttpStatus.OK);
    }

    @PostMapping("/settlementcoupon")
    public ResponseEntity<PosOutputSettle> cancelCouponForSapphire(@Valid @RequestBody PosInputSettle posInputRequest) throws Exception {
        PosOutputSettle posOutputSettle= couponSettlementService.settleCoupon(posInputRequest);
        return new ResponseEntity<>(posOutputSettle, HttpStatus.OK);
    }

    @PostMapping("feedback/{couponCode}")
    public ResponseEntity<CouponFeedbackIssueResponse> couponFeedback(@RequestBody  CouponFeedbackRequest couponFeedbackRequest,@PathVariable String couponCode) throws Exception {
        couponFeedbackRequest.setCouponCode(couponCode);
        log.info("CouponFeedbackRequest value in CouponFeedback() method: "+couponFeedbackRequest);
        CouponFeedbackIssueResponse couponFeedbackIssueResponse=couponFeedbackService.saveCouponFeedback(couponFeedbackRequest);
        return new ResponseEntity<>(couponFeedbackIssueResponse,HttpStatus.OK);
    }

    @PostMapping("issue/{couponCode}")
    public ResponseEntity<CouponFeedbackIssueResponse> couponIssue(@RequestBody CouponIssueRequest couponIssueRequest,@PathVariable String couponCode) throws Exception {
        couponIssueRequest.setCouponCode(couponCode);
        log.info("CouponIssueRequest value in CouponIssue() method: "+couponIssueRequest);
        CouponFeedbackIssueResponse couponFeedbackIssueResponse=couponIssueService.saveCouponIssue(couponIssueRequest);
        return new ResponseEntity<>(couponFeedbackIssueResponse,HttpStatus.OK);
    }

    @PostMapping("issue-question")
    public ResponseEntity<CouponFeedbackIssueResponse> saveIssueQuestion(@RequestBody IssueQuestionRequest issueQuestionRequest) throws Exception {
        log.info("CouponIssueRequest value in SaveIssueQuestion() method: "+issueQuestionRequest);
        CouponFeedbackIssueResponse couponFeedbackIssueResponse= issueQuestionService.saveIssueQuestion(issueQuestionRequest);
        return new ResponseEntity<>(couponFeedbackIssueResponse,HttpStatus.OK);
    }

    @GetMapping("issue-question")
    public ResponseEntity<IssueQuestionGetResponse> getIssueQuestion() throws Exception {
        IssueQuestionGetResponse issueQuestionGetResponseList= issueQuestionService.getIssueQuestion();
        return new ResponseEntity<>(issueQuestionGetResponseList,HttpStatus.OK);
    }



    



}
