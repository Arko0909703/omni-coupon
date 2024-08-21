package com.project.coupon.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ItemsData {

    String itemCode;
    String itemName;
    String size;
}
