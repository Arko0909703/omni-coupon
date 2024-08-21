package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CouponConstructDetails {
    @JsonProperty("coupon_name")
    private String couponName;

    @JsonProperty("construct_type")
    private String constructType;

    @JsonProperty("freebie_items")
    private List<ItemResponse> freebieItems;

    @JsonProperty("discount_percentage")
    private Double discountPercentage;

    @JsonProperty("flat_discount")
    private Double flatDiscount;

    @JsonProperty("discount_cap")
    private Double discountCap;

    @JsonProperty("minimum_order_value")
    private Double minimumOrderValue;

    @JsonProperty("product_inclusion")
    private List<String> productInclusion;

    @JsonProperty("product_exclusion")
    private List<String> productExclusion;

    @JsonProperty("category_inclusion")
    private List<String> categoryInclusion;

    @JsonProperty("category_exclusion")
    private List<String> categoryExclusion;

    @JsonProperty("created_date")
    private LocalDateTime createdDate;

    @JsonProperty("modified_date")
    private LocalDateTime modifiedDate;
}