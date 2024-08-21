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
public class BaseCouponResponse extends BaseResponse{
	 @JsonProperty("basecoupon_details")
	 private BaseCouponDetails baseCouponDetails;
	 
}
