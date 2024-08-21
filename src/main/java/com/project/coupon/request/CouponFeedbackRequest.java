package com.project.coupon.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CouponFeedbackRequest {

    @JsonProperty("coupon_code")
    private String couponCode;
    @JsonProperty("mobile_number")
    private String mobileNumber;
    @JsonProperty("customer_name")
    private String customerName;
    private String channel;
    @JsonProperty("date_time")
    private LocalDateTime dateTime;

    @JsonProperty("question_answer")
    private List<QuestionAnswerRequest> questionAnswer;

    private String comments;


}
