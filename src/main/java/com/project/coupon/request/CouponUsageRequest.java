package com.project.coupon.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CouponUsageRequest {
    @JsonProperty("coupon_name")
    private String couponName;
    @JsonProperty("coupon_type")
    private String couponType;

    @JsonProperty("unique_coupon_prefix")
    private String uniqueCouponPrefix;

    @JsonProperty("unique_coupon_suffix")
    private String uniqueCouponSuffix;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("batch_name")
    private String batchName;
}
