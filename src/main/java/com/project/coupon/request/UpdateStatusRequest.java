package com.project.coupon.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UpdateStatusRequest {

    @JsonProperty("coupon_code")
    List<String> couponCode;
    String status;
    String couponCategory;
}
