package com.project.coupon.entity;


import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
@Document("mongocoupon")
public class CouponMongoEntity {
	@Id
   
	private String couponCode;
	private String baseCouponCode;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
	
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	public LocalDateTime getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}
	public LocalDateTime getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}
	public String getBaseCouponCode() {
		return baseCouponCode;
	}
	public void setBaseCouponCode(String baseCouponCode) {
		this.baseCouponCode = baseCouponCode;
	}
    
   
}
