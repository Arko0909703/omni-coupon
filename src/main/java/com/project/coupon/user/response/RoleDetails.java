package com.project.coupon.user.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.coupon.user.request.CreateRoleRequest;
import com.project.coupon.user.request.PageDetails;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

public class RoleDetails {
	
	@JsonProperty("role_id")
    private Long roleId;
	
	@JsonProperty("role_name")
    private String roleName;
	
    @JsonProperty("status")
    private Boolean status;
		
    @JsonProperty("pages")
    private List<PageDetails> pages;

}
