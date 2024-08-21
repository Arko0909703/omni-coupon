package com.project.coupon.request;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StoreRequest {

    private String storeCode;
    private String storeName;
}