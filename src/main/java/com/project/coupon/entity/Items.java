package com.project.coupon.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document(collection = "items_data")
public class Items {


    @Id
    private String id;

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
    @JsonProperty("coupon_code")
    private String couponCode;
}
