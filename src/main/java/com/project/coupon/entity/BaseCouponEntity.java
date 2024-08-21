package com.project.coupon.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;
@Data
@Document("basecoupon")
public class BaseCouponEntity {
	@Id
	private String couponName;
	private String baseCouponId;
	private boolean status;
	private String tnc;
	private String description;
	private LocalDate tncEndDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}