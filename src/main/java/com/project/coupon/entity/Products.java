package com.project.coupon.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document(collection = "products_data")
public class Products {
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
