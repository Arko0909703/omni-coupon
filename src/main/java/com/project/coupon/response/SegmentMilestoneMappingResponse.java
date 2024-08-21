package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class SegmentMilestoneMappingResponse {

    private boolean consumed=false;
    @JsonProperty("coupon_code")
    private String couponCode;
    @JsonProperty("milestone_number")
    private int milestoneNumber;
    @JsonProperty("is_locked")
    private boolean isLocked=true;
    @JsonProperty("milestone_name")
    private String milestoneName;

    @JsonProperty("coupon_data")
    private CouponResponseWithCode couponData;
}
