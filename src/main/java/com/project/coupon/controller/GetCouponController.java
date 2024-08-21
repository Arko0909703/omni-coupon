package com.project.coupon.controller;

import com.project.coupon.constants.CouponConstant;
import com.project.coupon.exceptions.ResponseMessage;
import com.project.coupon.request.UpdateStatusRequest;
import com.project.coupon.response.*;
import com.project.coupon.service.GetCoupons;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/getCoupons")
@Log4j2
public class GetCouponController {

    @Autowired
    private GetCoupons getCouponsService;

    @GetMapping("/number")
    public ResponseEntity<List<CouponResponseWithNumber>> getCouponUsingMobileNumber(@RequestParam("number") String mobileNumber)
    {
        List<CouponResponseWithNumber> couponResponseWithNumberList= getCouponsService.getCouponUsingMobileNumber(mobileNumber);
        return new ResponseEntity<>(couponResponseWithNumberList, HttpStatus.OK);
    }

    @GetMapping("/couponCode/{couponCode}")
    public ResponseEntity<CouponResponseWithCode> getCouponUsingCouponCode(@PathVariable String couponCode)
    {

        log.info("Inside getCouponUsingCouponCode Controller method");
        CouponResponseWithCode couponResponseWithCode=getCouponsService.getCouponUsingCouponCode(couponCode);
        log.info("Exiting getCouponUsingCouponCode Controller method");
        return new ResponseEntity<>(couponResponseWithCode,HttpStatus.OK);

    }

    @GetMapping("/filter/{couponCategory}")
    public ResponseEntity<Page<CouponResponseForFilterCoupons>> getFilteredCoupons(@RequestParam(required = false) String dateCreated,
                                                                                   @RequestParam(required = false) String startDate,
                                                                                   @RequestParam(required = false) String endDate,
                                                                                   @RequestParam(required = false) String franchise,
                                                                                   @RequestParam(required = false) String city,
                                                                                   @RequestParam(required = false) String storeCode,
                                                                                   @RequestParam(required = false) String couponType,
                                                                                   @RequestParam(required = false) String couponStatus,
                                                                                   @RequestParam int page,
                                                                                   @RequestParam int size,
                                                                                   @PathVariable String couponCategory)
    {
            log.info("Inside getFilteredCoupons controller method");
            LocalDate parsedDateCreated = dateCreated != null ? LocalDate.parse(dateCreated) : null;
            LocalDate parsedStartDate = startDate != null ? LocalDate.parse(startDate) : null;
            LocalDate parsedEndDate = endDate != null ? LocalDate.parse(endDate) : null;
            Page<CouponResponseForFilterCoupons> couponResponseForFilterCoupons=getCouponsService.getFilteredCoupons(parsedDateCreated, parsedStartDate, parsedEndDate, franchise, city, storeCode, couponType, couponStatus,page,size,couponCategory);
            return new ResponseEntity<>(couponResponseForFilterCoupons,HttpStatus.OK);
    }

    @GetMapping("/search/{couponCategory}")
    ResponseEntity<Page<CouponResponseForFilterCoupons>> searchCoupons(@RequestParam String code,@PathVariable String couponCategory,
                                                                       @RequestParam int page ,@RequestParam int size)
    {
         Page<CouponResponseForFilterCoupons> couponResponseForFilterCoupons= getCouponsService.searchCoupons(code,couponCategory,page,size);
        return new ResponseEntity<>(couponResponseForFilterCoupons,HttpStatus.OK);
    }

    @GetMapping("/getbasecoupons")
    ResponseEntity<Page<CouponResponseForFilterCoupons>> getBaseCoupons(@RequestParam int page ,@RequestParam int size)
    {
        Page<CouponResponseForFilterCoupons> baseCouponResponse=getCouponsService.getBaseCoupons(page,size);
        return new ResponseEntity<>(baseCouponResponse,HttpStatus.OK);
    }

    @GetMapping("/getuniquecoupons")
    ResponseEntity<Page<CouponResponseForFilterCoupons>> getUniqueCoupons(@RequestParam int page ,@RequestParam int size)
    {
        Page<CouponResponseForFilterCoupons> uniqueCouponResponse=getCouponsService.getUniqueCoupons(page,size);
        return new ResponseEntity<>(uniqueCouponResponse,HttpStatus.OK);
    }

    @GetMapping("/getbatchcoupons")
    ResponseEntity<Page<CouponResponseForFilterCoupons>> getBatchCoupons(@RequestParam int page ,@RequestParam int size)
    {
        Page<CouponResponseForFilterCoupons> batchCouponResponse=getCouponsService.getBatchCoupons(page,size);
        return new ResponseEntity<>(batchCouponResponse,HttpStatus.OK);
    }

    @GetMapping("/getmilestonecoupons")
    ResponseEntity<MilestoneResponse> getMilestonesCoupons(@RequestParam("number") String mobileNumber){

        return new ResponseEntity<>(getCouponsService.getMilestoneCoupons(mobileNumber),HttpStatus.OK);
    }

    @GetMapping("/getmilestonedetails")
    ResponseEntity<Page<MilestoneDetailsResponse>> getMilestoneDetails(@RequestParam int page ,@RequestParam int size)
    {
        return new ResponseEntity<>(getCouponsService.getMilestoneDetails(page,size),HttpStatus.OK);
    }

    @PostMapping("/updateStatus/{couponCategory}")
    ResponseEntity<ResponseMessage> updateStatus(@PathVariable String couponCategory, @Valid @RequestBody UpdateStatusRequest updateStatusRequest) throws Exception {
        updateStatusRequest.setCouponCategory(couponCategory);
        return new ResponseEntity<>(getCouponsService.setCouponsStatus(updateStatusRequest),HttpStatus.OK);
    }

    @GetMapping("/getcouponusage/{code}")
    ResponseEntity<GetCouponUsageResponse> getgetUniqueCouponUsage(@PathVariable String code){

        return new ResponseEntity<>(getCouponsService.getUniqueCouponUsage(code),HttpStatus.OK);
    }

    @GetMapping("/getcouponapplicability/{code}")
    ResponseEntity<GetCouponApplicabilityResponse> getUniqueCouponApplicability(@PathVariable String code){

        return new ResponseEntity<>(getCouponsService.getUniqueCouponApplicability(code),HttpStatus.OK);
    }

    @GetMapping("/getuserspecificconstraints/{code}")
    ResponseEntity<CouponUserSpecificConstraintsResponse> getUniqueCouponUserSpecificConstraints(@PathVariable String code){

        return new ResponseEntity<>(getCouponsService.getUniqueCouponUserSpecificConstraints(code),HttpStatus.OK);
    }

    @GetMapping("/getcouponconstructs/{code}")
    ResponseEntity<UniqueCouponConstructResponse> getUniqueCouponConstruts(@PathVariable String code){

        return new ResponseEntity<>(getCouponsService.getUniqueCouponConstruts(code),HttpStatus.OK);
    }

    @GetMapping("/getproductmaster")
    ResponseEntity<ProductMasterResponse> getProductMaster()
    {
        List<CategoryProductResponse> responses = getCouponsService.getProductsGroupedByCategory();
        ProductMasterResponse productMasterResponse=ProductMasterResponse.builder().status(CouponConstant.SUCCESS).content(responses).build();
        return new ResponseEntity<>(productMasterResponse,HttpStatus.OK);
    }

    @GetMapping("/getstoremaster")
    ResponseEntity<StoreMasterResponse> getStoreMaster(){

        return new ResponseEntity<>(getCouponsService.getOperationalStores(),HttpStatus.OK);
    }






}
