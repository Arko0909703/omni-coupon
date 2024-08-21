package com.project.coupon.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OrderItems {

    @JsonProperty("line_id")
    private int lineId;

    @NotEmpty(message = "Item Code cannot be empty")
    @JsonProperty("item_code")
    private String itemCode;

    @JsonProperty("base")
    private String base;

    @NotEmpty(message = "Item Size cannot be empty")
    @JsonProperty("size")
    private String size;

    @NotEmpty(message = "Item Quantity cannot be empty")
    @JsonProperty("quantity")
    private int quantity;

    @NotEmpty(message = "Item price cannot be empty")
    @JsonProperty("price")
    private double price;

    @JsonProperty("deal_id")
    private String dealId;

}
