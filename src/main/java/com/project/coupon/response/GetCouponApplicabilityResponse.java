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
public class GetCouponApplicabilityResponse{
    @JsonProperty("coupon_code")
    private String couponCode;
    private List<String> channel;
    @JsonProperty("channel_fullfillment_type")
    private List<String> channelFulfillmentType;
    private List<String> franchise;
    private List<String> stores;
    private List<String> cities;
    private List<String> clusters;
    @JsonProperty("day_applicability")
    private List<String> dayApplicability;
    @JsonProperty("month_applicability")
    private List<Integer> monthApplicability;
    @JsonProperty("time_slot")
    private TimeSlotApplicability timeslot;
}
