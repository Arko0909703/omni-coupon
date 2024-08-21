package com.project.coupon.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document("batch_coupons")
public class BatchCoupon {

    @Id
    private String batchId; //Will be made using postfix and prefix provided
    private String lotName;
    private String uniqueCouponId;
    private String mobileNumber;
    private String status;
    private LocalDate dateCreated;
    private LocalDate endDate;
}
