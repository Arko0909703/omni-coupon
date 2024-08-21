package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GetCouponUsageResponse {

    @JsonProperty("coupon_name")
    private String couponName;
    @JsonProperty("coupon_usage")
    private String couponUsage;
    @JsonProperty("usage_type")
    private String usageType;

    @JsonProperty("unique_coupon_prefix")
    private String uniqueCouponPrefix;

    @JsonProperty("unique_coupon_suffix")
    private String uniqueCouponSuffix;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("batch_name")
    private String batchName;
}
