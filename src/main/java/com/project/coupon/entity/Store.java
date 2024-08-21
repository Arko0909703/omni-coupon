package com.project.coupon.entity;


import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "stores_data")
public class Store {

    @Id
    private String id;

    private String storeCode;
    private String storeName;
    private String couponCode; // To associate with UniqueCoupon in MongoDB
}
