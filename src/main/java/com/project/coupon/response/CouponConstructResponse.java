package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CouponConstructResponse extends BaseCouponResponse {
    @JsonProperty("coupon_construct_details")
    private CouponConstructDetails couponConstructDetails;


}

