package com.project.coupon.exceptions;

import java.time.LocalDate;

public class BadApiRequestException extends RuntimeException{

    public BadApiRequestException()
    {
        super("Invalid Value passed");
    }

    public BadApiRequestException(String message)
    {
        super(message);
    }

}
