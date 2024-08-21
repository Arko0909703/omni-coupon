package com.project.coupon.exceptions;

import lombok.*;

import java.time.LocalDate;

@Data
public class ExpiredCouponException extends RuntimeException{

    private LocalDate expiry_date;
    public ExpiredCouponException()
    {
        super("Coupon Expired");
    }

    public ExpiredCouponException(String message, LocalDate date)
    {
        super(message);
        this.expiry_date=date;
    }

}
