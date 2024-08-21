package com.project.coupon.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.coupon.entity.Timeslot;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CouponApplicabilityRequest {
    @JsonProperty("coupon_name")
    private String couponName;
    @JsonProperty("channel")
    private List<String> channel;
    @JsonProperty("channel_fulfillment_type")
    private List<String> channelFulfillmentType;
    @JsonProperty("franchise")
    private List<String> franchise;
    @JsonProperty("stores")
    private List<String> stores;
    @JsonProperty("cities")
    private List<String> cities;
    @JsonProperty("clusters")
    private List<String> clusters;
    @JsonProperty("day_applicability")
    private List<String> dayApplicability;
    @JsonProperty("month_applicability")
    private List<Integer> monthApplicability;
    @JsonProperty("timeslot")
    private Timeslot timeslot;
    @JsonProperty("hidden_ui")
   private boolean hiddenUI;

    ;

}
