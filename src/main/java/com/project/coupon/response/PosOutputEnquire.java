package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.coupon.entity.Items;
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
public class PosOutputEnquire {

    @JsonProperty("applicable_store_code")
    private List<String> store;

    @JsonProperty("applicable_channel")
    private List<String> channel;

    @JsonProperty("applicable_fulfilment_mode")
    private List<String> fullfillmentMode;

    @JsonProperty("applicable_franchise")
    private List<String> franchise;

    @JsonProperty("coupon_type")
    private String discountType;

    @JsonProperty("net_amount")
    private double netAmount;

    @JsonProperty("start_date")
    private LocalDate startDate;
    @JsonProperty("end_date")
    private LocalDate endDate;
    @JsonProperty("timeslot")
    private Timeslot timeslot;
    @JsonProperty("receipt_no")
    private String receiptNumber;

    @JsonProperty("coupon_code")
    private String couponCode;

    @JsonProperty("coupon_value")
    private double couponValue;

    @JsonProperty("discount_max")
    private double discountMax;
    private double mov;

    @JsonProperty("coupon_source")
    private String couponSource;

    @JsonProperty("coupon_info")
    private String couponInfo;
    private String status;
    private String message;

    @JsonProperty("free_item")
    private List<Items> freebieItems;

}
