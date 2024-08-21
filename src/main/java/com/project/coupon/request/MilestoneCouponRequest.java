package com.project.coupon.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MilestoneCouponRequest {

    @JsonProperty("milestone_name")
    private String milestoneName;
    @JsonProperty("coupon_id")
    private String couponId;
    @JsonProperty("coupon_name")
    private String couponName;
    @JsonProperty("milestone_number")
    private int milestoneNumber;
    @JsonProperty("number_of_orders")
    private int numberOfOrders;
}
