package com.project.coupon.exceptions;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserExpiredException extends RuntimeException{

    private LocalDate user_expiry_date;
    public UserExpiredException()
    {
        super("Coupon expired for the current user");
    }

    public UserExpiredException(String message, LocalDate date)
    {
        super(message);
        this.user_expiry_date=date;
    }
}
