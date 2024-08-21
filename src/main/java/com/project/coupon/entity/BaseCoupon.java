package com.project.coupon.entity;

import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document(collection="base_coupons")
public class BaseCoupon {

    @Id
    private String baseCouponCode;
    private String couponName;
    private String description;
    private String termsAndConditions;
    private LocalDate termsAndConditionsExpiryDate;
    private Integer sequence;
    private String status;
    private LocalDate dateCreated;
}
