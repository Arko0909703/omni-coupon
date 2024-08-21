package com.project.coupon.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessageForExpiredCoupon extends ResponseMessage {

    private LocalDate expiry_date;
}
