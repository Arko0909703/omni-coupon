package com.project.coupon.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class StoreMasterData {
    String storeCode;
    String city;
    String cluster;
}
