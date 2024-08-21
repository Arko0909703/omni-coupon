package com.project.coupon.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.coupon.response.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RoleResponse extends BaseResponse{
	 @JsonProperty("role_details")
	 private RoleDetails roleDetails;
}
