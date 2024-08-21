package com.project.coupon.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
@Entity
@Table(name="base_coupon")
@Data
public class MySQLBaseCouponEntity {
@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
private long id;
@Column(name="coupon_name", length=30,unique=true)
private String couponName;
@Column(name="description")
private String description;
@Column(name="base_coupon_id")
private String baseCouponId;
@Column(name="start_date")
private LocalDate startDate;
@Column(name="end_date")
private LocalDate endDate;
@Column(name="status")
private boolean status=false;
@Column(name="tnc")
private String tnc;
@Column(name="tnc_end_date")
private LocalDate tncEndDate;
@Column(name="sequence")
public Integer sequence;
@Column(name="create_date")
private LocalDateTime createDate;
@Column(name="modified_date")
private LocalDateTime modifiedDate;

}