package com.project.coupon.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class PosInputEnquiry {
    @Pattern(regexp = "ONLINE|OFFLINE", message = "Channel must be either ONLINE or OFFLINE")
    @JsonProperty("channel")
    private String channel;

    @Pattern(regexp = "DINEIN|TAKEAWAY|DELIVERY", message = "Fullfillment Mode must be DINEIN,TAKEAWAY or DELIVERY")
    @JsonProperty("fulfillment_mode")
    private String fullfillmentMode;

    @JsonProperty("store_code")
    private String storeCode;

    @JsonProperty("net_amount")
    private double netAmount;

    @JsonProperty("coupon_code")
    private String couponCode;

    @JsonProperty("coupon_already_applied")
    private String ifCouponAlreadyApplied;

    @JsonProperty("receipt_no")
    private String receiptNumber;

    @NotNull(message = "Date cannot be null" )
    @JsonProperty("date")
    private LocalDate date;

    @NotNull(message = "Time cannot be null" )
    @JsonProperty("time")
    private LocalTime time;

    @JsonProperty("pos_terminal_no")
    private String posTerminalNumber;

    @JsonProperty("discount_amount")
    private double discountAmount;

    @JsonProperty("name")
    private String name;

    @JsonProperty("number")
    private String number;

    @JsonProperty("transaction_type")
    private String transactionType;

    @JsonProperty("entry_status")
    private String entryStatus;

    @Pattern(regexp = "DIL|SAPPHIRE|PANINDIA", message = "Franchise must be DIL , SAPPHIRE or PANINDIA")
    private String franchise;

    @JsonProperty("secret")
    private String secret;
}
