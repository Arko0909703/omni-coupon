package com.project.coupon.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class LotNameAndQuantity {
    String lotName;
    Long quantity;
}
