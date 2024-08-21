package com.project.coupon.exceptions;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NotActiveCouponException extends RuntimeException{

    private LocalDate active_date;
    public NotActiveCouponException()
    {
        super("Coupon Expired");
    }

    public NotActiveCouponException(String message, LocalDate date)
    {
        super(message);
        this.active_date=date;
    }

}
