package com.project.coupon.exceptions;

import com.project.coupon.constants.CouponConstant;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {


    Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(CouponException.class)
    ResponseEntity<ResponseMessage> genericCouponExceptionHandler(CouponException ex)
    {
        ResponseMessage responseMessage=ResponseMessage.builder().status(CouponConstant.FAILURE).message(ex.getMessage()) .build();
        return new ResponseEntity<ResponseMessage>(responseMessage, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BadApiRequestException.class)
    ResponseEntity<ResponseMessage> badApiRequestExceptionHandler(BadApiRequestException ex)
    {
        ResponseMessage responseMessage=ResponseMessage.builder().status(CouponConstant.FAILURE).message(ex.getMessage()) .build();
        return new ResponseEntity<ResponseMessage>(responseMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredCouponException.class)
    ResponseEntity<ResponseMessageForExpiredCoupon> expiredCouponExceptionHandler(ExpiredCouponException ex)
    {
        ResponseMessageForExpiredCoupon responseMessageForExpiredCoupon=new ResponseMessageForExpiredCoupon();
        responseMessageForExpiredCoupon.setStatus(CouponConstant.FAILURE);
        responseMessageForExpiredCoupon.setMessage(ex.getMessage());
        responseMessageForExpiredCoupon.setExpiry_date(ex.getExpiry_date());
        return new ResponseEntity<ResponseMessageForExpiredCoupon>(responseMessageForExpiredCoupon,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotActiveCouponException.class)
    ResponseEntity<ResponseMessageForNotActivatedCoupon> notActiveCouponExceptionHandler(ExpiredCouponException ex)
    {
        ResponseMessageForNotActivatedCoupon responseMessageForExpiredCoupon=new ResponseMessageForNotActivatedCoupon();
        responseMessageForExpiredCoupon.setStatus(CouponConstant.FAILURE);
        responseMessageForExpiredCoupon.setMessage(ex.getMessage());
        responseMessageForExpiredCoupon.setActive_date(ex.getExpiry_date());
        return new ResponseEntity<ResponseMessageForNotActivatedCoupon>(responseMessageForExpiredCoupon,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserExpiredException.class)
    ResponseEntity<ResponseMessageForUserExpired> userExpiredExceptionHandler(UserExpiredException ex)
    {
        ResponseMessageForUserExpired responseMessageForExpiredCoupon=new ResponseMessageForUserExpired();
        responseMessageForExpiredCoupon.setStatus(CouponConstant.FAILURE);
        responseMessageForExpiredCoupon.setMessage(ex.getMessage());
        responseMessageForExpiredCoupon.setUser_expiry_date (ex.getUser_expiry_date());
        return new ResponseEntity<ResponseMessageForUserExpired>(responseMessageForExpiredCoupon,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MovInvalidException.class)
    ResponseEntity<ResponseMessageForMov> MovInvalidExceptionHandler(MovInvalidException ex)
    {
        ResponseMessageForMov responseMessageForExpiredCoupon=new ResponseMessageForMov();
        responseMessageForExpiredCoupon.setStatus(CouponConstant.FAILURE);
        responseMessageForExpiredCoupon.setMessage(ex.getMessage()+". Eligible items_code for applying in coupon are: "+ex.getProductInclusion()+". Eligible categories for applying in coupon are: "+ex.getCategoryInclusion()+". Non-eligible items_code for this coupons are : "+ex.getProductExclusion()+". Non-eligible categories for this coupons are : "+ex.getCategoryExclusion());
        responseMessageForExpiredCoupon.setMov(ex.getMov());
        return new ResponseEntity<ResponseMessageForMov>(responseMessageForExpiredCoupon,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String , Object>> methodArgumentNotValidExceptionhandler(MethodArgumentNotValidException ex)
    {
        List<ObjectError> allErrors=ex.getBindingResult().getAllErrors(); //This method return all error in a method as a list.
        Map<String,Object> response=new HashMap<>();
        response.put("status",CouponConstant.FAILURE);
       // response.put("message","Input data is not valid");
        StringBuilder errorFields=new StringBuilder();
        allErrors.stream().forEach(objectError->{
            String message=objectError.getDefaultMessage();
            String field=((FieldError) objectError).getField();
            errorFields.append(field).append(", ");
            //response.put(field,message);
        });

        int length = errorFields.length();
        if (length > 2) {
            errorFields.setLength(length - 2); // Remove the trailing ", "
        }

        errorFields.append(" data is not valid");
        response.put("message",errorFields.toString());

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String , Object>> httpMessageNotReadableException(HttpMessageNotReadableException ex)
    {
        logger.info("HttpMessageNotReadableException: "+ex.toString());

        Map<String,Object> response=new HashMap<>();
        response.put("status",CouponConstant.FAILURE);
        response.put("message",ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error(ex.getMessage() +" "+ Arrays.toString(ex.getStackTrace()));

        Map<String, Object> response = new HashMap<>();
        response.put("status", CouponConstant.FAILURE);
        response.put("message", "An unexpected error occurred. Please try again later.");

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
