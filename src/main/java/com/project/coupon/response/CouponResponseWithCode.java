package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CouponResponseWithCode {

    @JsonProperty("coupon_code")
    private String couponCode;
    @JsonProperty("coupon_name")
    private String couponName;
    @JsonProperty("coupon_description")
    private String couponDescription;
    @JsonProperty("coupon_type")
    private String couponType;
    @JsonProperty("expiry_date")
    private LocalDate expiryDate;
    private List<String> channel;
    @JsonProperty("terms_and_conditions")
    private String termsAndCondition;
    private String status;
    @JsonProperty("start_date")
    private LocalDate startDate;
    @JsonProperty("img_url")
    private String imgUrl;

}
