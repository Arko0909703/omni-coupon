package com.project.coupon.exceptions;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage {

    private String status;
    private String message;

}
