package com.project.coupon.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.coupon.entity.Items;
import com.project.coupon.entity.ItemsEntity;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CouponConstructRequest {

    @JsonProperty("coupon_name")
    private String couponName;

    @JsonProperty("construct_type")
    private String constructType;

//    @JsonProperty("freebie_item_mongo")
//    private List<Items> freebieItemMongo; // For MongoDB
//
//    @JsonProperty("freebie_item_mysql")
//    private List<ItemsEntity> freebieItemMySQL; // For MySQL

    @JsonProperty("freebie_items")
    private List<ItemRequest> freebieItems;

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


}
