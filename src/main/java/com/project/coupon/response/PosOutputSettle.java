package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PosOutputSettle {


    @JsonProperty("channel")
    private String channel;

    @JsonProperty("franchise")
    private String franchise;

    @JsonProperty("fulfilment_mode")
    private String fullfillmentMode;

    @JsonProperty("store_code")
    private String store;

    @JsonProperty("net_amount")
    private double netAmount;

    @JsonProperty("coupon_code")
    private String couponCode;

    @JsonProperty("receipt_no")
    private String receiptNumber;

    private LocalDate date;
    private LocalTime time;

    private String status;
    private String message;

    @JsonProperty("discount_amount")
    private double discountAmount;

}
