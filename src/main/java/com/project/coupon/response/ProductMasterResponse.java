package com.project.coupon.response;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProductMasterResponse {

    String status;
    List<CategoryProductResponse> content;

}
