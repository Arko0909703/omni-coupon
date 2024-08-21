package com.project.coupon.jpaEntities;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
public class QuestionAnswer {

    @Id
    String id;

    private String question;
    private String answer;
    @ManyToOne
    @JoinColumn(name="coupon_feedback")
    private CouponFeedback couponFeedback;
    @ManyToOne
    @JoinColumn(name="coupon_issue")
    private CouponIssue couponIssue;

}
