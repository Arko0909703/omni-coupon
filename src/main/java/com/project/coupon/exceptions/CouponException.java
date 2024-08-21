package com.project.coupon.exceptions;

import lombok.Builder;
import lombok.Data;


@Data
public class CouponException extends RuntimeException{
	
	public CouponException()
	{
		super("Coupon exception occurred");
	}
	
	public CouponException(String message)
	{
		super(message);
	}
}
