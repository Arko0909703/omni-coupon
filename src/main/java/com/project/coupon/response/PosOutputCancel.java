package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.coupon.entity.Timeslot;
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
public class PosOutputCancel {


    @JsonProperty("channel")
    private String channel;

    @JsonProperty("fulfilment_mode")
    private String fullfillmentMode;

    @JsonProperty("franchise")
    private String franchise;

    @JsonProperty("store_code")
    private String store;

    @JsonProperty("coupon_code")
    private String couponCode;

    @JsonProperty("receipt_no")
    private String receiptNumber;

    private LocalDate date;
    private LocalTime time;


    private String status;
    private String message;

    @JsonProperty("coupon_info")
    private String couponInfo;
}
