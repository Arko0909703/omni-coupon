package com.project.coupon.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CouponApplicabilityResponse {
    private String status;
    private String message;
}
