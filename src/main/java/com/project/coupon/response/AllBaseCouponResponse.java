package com.project.coupon.response;

import java.util.List;

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
public class AllBaseCouponResponse extends BaseResponse{
	 @JsonProperty("basecoupon_details")
	 private List<BaseCouponDetails> baseCouponDetails;
}
