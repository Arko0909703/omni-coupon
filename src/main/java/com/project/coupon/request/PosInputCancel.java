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
public class PosInputCancel {

    @Pattern(regexp = "ONLINE|OFFLINE", message = "Channel must be either ONLINE or OFFLINE")
    @JsonProperty("channel")
    private String channel;

    @Pattern(regexp = "DINEIN|TAKEAWAY|DELIVERY", message = "Fullfillment Mode must be DINEIN,TAKEAWAY or DELIVERY")
    @JsonProperty("fulfillment_mode")
    private String fullfillmentMode;

    @NotNull(message = "Store code cannot be null" )
    @NotEmpty(message = "Store code cannot be empty")
    @JsonProperty("store_code")
    private String storeCode;


    //    @NotNull(message = "Coupon code cannot be null" )
//    @NotEmpty(message = "Coupon code cannot be empty")
    @JsonProperty("coupon_code")
    private String couponCode;


    @JsonProperty("receipt_no")
    private String receiptNumber;

    @NotNull(message = "Date cannot be null" )
    private LocalDate date;

    @NotNull(message = "Time cannot be null" )
    private LocalTime time;

    @JsonProperty("pos_terminal_no")
    private String posTerminalNumber;

    @Pattern(regexp = "DIL|SAPPHIRE|PANINDIA", message = "Franchise must be DIL , SAPPHIRE or PANINDIA")
    private String franchise;
    private String secret;
}
