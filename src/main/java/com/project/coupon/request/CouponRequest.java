package com.project.coupon.request;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class CouponRequest {
	
	@JsonProperty("basecoponcode")
	public String baseCouponCode;
@JsonProperty("no_of_coupons")
public int noOfCoupons;

@JsonProperty("Start_date")
public LocalDateTime startDate;
@JsonProperty("end_date")
public LocalDateTime endDate;
@JsonProperty("status")
public boolean status;
}