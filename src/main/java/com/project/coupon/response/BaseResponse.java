package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.coupon.user.request.CreateUserRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BaseResponse {

	    private String status;
	    private String message;
}
