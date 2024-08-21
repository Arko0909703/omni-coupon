package com.project.coupon.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.coupon.response.BaseCouponDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BaseCouponRequest {
	@JsonProperty("coupon_name")
	public String couponName;
	@JsonProperty("basecoupon_id")
	public String baseCouponId;
	@JsonProperty("Start_date")
	public LocalDate startDate;
	@JsonProperty("end_date")
	public LocalDate endDate;
	@JsonProperty("status")
	public String status;
	@JsonProperty("description")
	public String description;
	@JsonProperty("tnc")
	public String tnc;
	@JsonProperty("tnc_end_date")
	public LocalDate tncEndDate;
	@JsonProperty("sequence")
	public Integer sequence;
}