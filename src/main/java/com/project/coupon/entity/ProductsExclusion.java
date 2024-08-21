package com.project.coupon.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products_exclusion")
@Builder
@ToString
public class ProductsExclusion {
    @Id
    private String id;
    @JsonProperty("product_code")
    private String productCode;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("category")
    private String category;
    @JsonProperty("coupon_code")
    private String couponCode;
}
