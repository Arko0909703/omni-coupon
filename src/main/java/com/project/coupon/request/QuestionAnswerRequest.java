package com.project.coupon.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class QuestionAnswerRequest {

    private String question;
    private String answer;

}
