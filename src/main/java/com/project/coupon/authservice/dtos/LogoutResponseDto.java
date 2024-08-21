package com.project.coupon.authservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LogoutResponseDto {
    private String token;
    private String expiresAt;
}
