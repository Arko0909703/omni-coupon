package com.project.coupon.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponTypeSelectionRequest {
    @JsonProperty("coupon_name")
    private String couponName;
    @JsonProperty("coupon_type")
    private String couponType; // "multi-use" or "single-use"
}