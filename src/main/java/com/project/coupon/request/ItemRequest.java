package com.project.coupon.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ItemRequest {
    @JsonProperty("item_code")
    private String itemCode;

    @JsonProperty("price")
    private double itemPrice;

    @JsonProperty("quantity")
    private int itemQuantity;

    @JsonProperty("item_description")
    private String itemBase;

    @JsonProperty("size")
    private String itemSize;
}
