package com.project.coupon.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ItemsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_code")
    private String itemCode;

    @Column(name = "price")
    private double itemPrice;

    @Column(name = "quantity")
    private int itemQuantity;

    @Column(name = "item_description")
    private String itemBase;

    @Column(name = "size")
    private String itemSize;

    @Column(name = "coupon_name")
    private String couponName;
}
