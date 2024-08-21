package com.project.coupon.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class SystemGeneratedCouponRequest {
    @JsonProperty("prefix")
    private String prefix;

    @JsonProperty("suffix")
    private String suffix;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("batch_lot_name")
    private String batchLotName;

    @JsonProperty("coupon_name")
    private String couponName;
}
