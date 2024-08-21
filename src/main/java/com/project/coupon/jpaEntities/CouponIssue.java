package com.project.coupon.jpaEntities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class CouponIssue {

    @Id
    private String id;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name="mobile_number")
    private String mobileNumber;
    @Column(name="customer_name")
    private String customerName;
    private String channel;

    @OneToMany(mappedBy = "couponIssue",cascade = CascadeType.ALL)
    private List<QuestionAnswer> issue;

    private String comments;
}
