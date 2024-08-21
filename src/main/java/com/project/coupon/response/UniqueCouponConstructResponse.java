package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.coupon.entity.Items;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UniqueCouponConstructResponse {

    @JsonProperty("coupon_code")
    private String couponCode;

    @JsonProperty("construct_type")
    private String constructType;

    @JsonProperty("freebie_item_mongo")
    private List<Items> freebieItemMongo; // For MongoDB


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
    @JsonProperty("freebie_item_file")
    private String freebieItemFile;  // URL to download the freebie item file

    @JsonProperty("product_inclusion_file")
    private String productInclusionFile;  // URL to download the product inclusion file

    @JsonProperty("product_exclusion_file")
    private String productExclusionFile;
}
