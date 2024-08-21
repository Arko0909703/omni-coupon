package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CouponResponseForFilterCoupons {

    @JsonProperty("coupon_code")
    private String couponCode;
    @JsonProperty("coupon_name")
    private String couponName;
    @JsonProperty("expiry_date")
    private LocalDate endDate;
    private String status;
    @JsonProperty("start_date")
    private LocalDate dateCreated;

}
