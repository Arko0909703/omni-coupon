package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ItemResponseInfo {

    @JsonProperty("line_id")
    private int lineId;

    @JsonProperty("item_code")
    private String itemCode;

    @JsonProperty("price")
    private double orderItemPrice;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("item_size")
    private String itemSize;

    @JsonProperty("base")
    private String itemBase;

    @JsonProperty("deal_id")
    private String dealId;
    @JsonProperty("discount")
    private double discount;

    @JsonProperty("is_freebie")
    private Boolean isFreebie;


}
