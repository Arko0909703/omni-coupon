package com.project.coupon.jpaEntities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name="transactiontable")
public class TransactionTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String receiptNumber;
    private String redemptionNumber;
    private String couponCode;
    private String mobileNumber; //encrypted AES256
    private String email; //encrypted AES256
    private String name;
    private LocalDateTime dateTime;
    private String store;
    private double netAmount;
    private String channelFullfillment;   //??
    private String franchise;
    private String channel;
    private String transactionType;
    private String dayApplicability;
    private int dateApplicability;

    @OneToMany(mappedBy = "transactionTable",cascade = CascadeType.ALL)
    private List<JpaItems> orderItems;

    private String baseCouponCode;
    private String termsAndConditions;
    private LocalDate startDate;
    private LocalDate endDate;

    private String couponType;
    private String couponUsage;
    private double mov;
    private double discountAmount;
    private double couponValue;

    private String status;

    private String ipAddress;

    private LocalDate settleDate;
    private LocalTime settleTime;
    private LocalDate cancelDate;
    private LocalTime cancelTime;

}
