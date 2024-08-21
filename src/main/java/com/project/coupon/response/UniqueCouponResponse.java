package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@ToString
public class UniqueCouponResponse extends BaseResponse{
	 @JsonProperty("unique_coupon_details")
	 private UniqueCouponDetails uniqueCouponDetails;
}
