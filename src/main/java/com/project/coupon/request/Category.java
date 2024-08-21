package com.project.coupon.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private String categoryCode;
    private String categoryName;
}