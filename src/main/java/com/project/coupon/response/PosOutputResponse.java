package com.project.coupon.response;


import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.coupon.entity.Items;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PosOutputResponse {

    @JsonProperty("store_code")
    private String store;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("franchise")
    private String franchise;

    @JsonProperty("net_amount")
    private double netAmount;

    @JsonProperty("fulfilment_mode")
    private String fullfillmentMode;

    @JsonProperty("discount_amount")
    private double discountAmount;

    @JsonProperty("redemption_ref_no")
    private String redemptionRefNumber;

    @JsonProperty("coupon_type")
    private String discountType;

    @JsonProperty("receipt_no")
    private String receiptNumber;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("time")
    private LocalTime time;

    @JsonProperty("coupon_code")
    private String couponCode;

    @JsonProperty("coupon_value")
    private double couponValue;

    @JsonProperty("discount_max")
    private double discountMax;

    @JsonProperty("mov")
    private double mov;

    @JsonProperty("coupon_source")
    private String couponSource;

    @JsonProperty("coupon_info")
    private String couponInfo;
    @JsonProperty("status")
    private String status;
    @JsonProperty("message")
    private String message;

    @JsonProperty("applied_coupon_item_details")
    private List<ItemResponseInfo> appliedCouponItemDetails;
}
