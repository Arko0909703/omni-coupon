package com.project.coupon.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessageForNotActivatedCoupon extends ResponseMessage {

    private LocalDate active_date;
}
