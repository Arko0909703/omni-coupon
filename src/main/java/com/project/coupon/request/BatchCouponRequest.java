package com.project.coupon.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BatchCouponRequest {

    @JsonProperty("unique_prefix")
    public String uniquePrefix;
    @JsonProperty("unique_postfix")
    public String uniquePostfix;
}
