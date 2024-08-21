package com.project.coupon.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity(name = "batch_coupon")// Specifies the table name in MySQL
public class BatchCouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // MySQL usually uses a Long ID with auto-increment

    @Column(name = "batch_id", unique = true)
    private String batchId; // Will be made using postfix and prefix provided

    @Column(name = "lot_name")
    private String lotName;

    @Column(name = "unique_coupon_id")
    private String uniqueCouponId;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "status")
    private String status;

    @Column(name = "date_created")
    private LocalDate dateCreated;

    @Column(name = "end_date")
    private LocalDate endDate;
}
