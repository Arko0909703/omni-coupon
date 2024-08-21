package com.project.coupon.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "product_exclusion")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductEntityExlcusion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_code")
    private String productCode;
    @Column(name = "product_name")
    private String productName;
    @Column(name = "category_code")
    private String categoryCode;
    @Column(name = "coupon_name")
    private String couponName;
}
