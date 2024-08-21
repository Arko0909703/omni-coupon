package com.project.coupon.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;


import com.project.coupon.constants.CouponConstant;
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
public class UniqueCouponRequest {

	@JsonProperty("coupon_name")
	public String couponName;
	@JsonProperty("base_coupon_code")
	public String baseCouponCode;
	@JsonProperty("Start_date")
	public LocalDate startDate;
	@JsonProperty("end_date")
	public LocalDate endDate;
	@JsonProperty("status")
	public String status= CouponConstant.CREATED;
	@JsonProperty("description")
	public String description;
	@JsonProperty("display_name")
	private String displayName;
	@JsonProperty("tnc")
	public String tnc;
	@JsonProperty("tnc_end_date")
	public LocalDate tncEndDate;
	@JsonProperty("sequence")
	public Integer sequence;
	@JsonProperty("total_usage")
	public Long totalUsage;
	@JsonProperty("no_of_unique_users")
	public Long noOfUniqueUsers;
	@JsonProperty("applicable_value")
	private String applicableForValue;

}
