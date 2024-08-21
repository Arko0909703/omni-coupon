package com.project.coupon.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PosInputSettle {

    @NotEmpty(message = "Channel cannot be empty")
    @NotNull(message = "Channel cannot be null" )
    @Pattern(regexp = "ONLINE|OFFLINE", message = "Channel must be either ONLINE or OFFLINE")
    @JsonProperty("channel")
    private String channel;

    @NotNull(message = "Fullfillment Mode cannot be null" )
    @NotEmpty(message = "Fullfillment Mode cannot be empty")
    @Pattern(regexp = "DINEIN|TAKEAWAY|DELIVERY", message = "Fullfillment Mode must be DINEIN,TAKEAWAY or DELIVERY")
    @JsonProperty("fulfillment_mode")
    private String fullfillmentMode;

    @NotNull(message = "Store code cannot be null" )
    @NotEmpty(message = "Store code cannot be empty")
    @JsonProperty("store_code")
    private String storeCode;

    @NotNull(message = "Net Amount cannot be empty")
    @Min(value = 1,message = "Net amount must be greater than 0.0")
    @JsonProperty("net_amount")
    private double netAmount;

    //    @NotNull(message = "Coupon code cannot be null" )
//    @NotEmpty(message = "Coupon code cannot be empty")
    @JsonProperty("coupon_code")
    private String couponCode;

    @NotNull(message = "Receipt number cannot be null" )
    @NotEmpty(message = "Receipt number cannot be empty")
    @JsonProperty("receipt_no")
    private String receiptNumber;

    @NotNull(message = "Date cannot be null" )
    private LocalDate date;

    @NotNull(message = "Time cannot be null" )
    private LocalTime time;

    @NotNull(message = "POS Terminal number cannot be null" )
    @NotEmpty(message = "POS Terminal number cannot be empty")
    @JsonProperty("pos_terminal_no")
    private String posTerminalNumber;

    @JsonProperty("discount_amount")
    private double discountAmount;
    private String name;

    @NotNull(message = "Number cannot be null" )
    @NotEmpty(message = "Number cannot be empty")
    private String number;

    @NotNull(message = "Transaction Type cannot be null" )
    @NotEmpty(message = "Transaction Type cannot be empty")
    @JsonProperty("transaction_type")
    private String transactionType;

    @JsonProperty("entry_status")
    private String entryStatus;

    @NotNull(message = "Franchise cannot be null" )
    @NotEmpty(message = "Franchise cannot be empty")
    @Pattern(regexp = "DIL|SAPPHIRE|PANINDIA", message = "Franchise must be DIL , SAPPHIRE or PANINDIA")
    private String franchise;

    private String secret;
}
