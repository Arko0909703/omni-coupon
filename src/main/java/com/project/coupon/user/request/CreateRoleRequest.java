package com.project.coupon.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
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
public class CreateRoleRequest {

	@NotEmpty(message = "Role Name can not be empty")
    @NotNull(message = "Role Name cannot be null" )
    @JsonProperty("role_name")
    private String roleName;
	
	@NotEmpty(message = "Status can not be empty")
    @NotNull(message = "status cannot be null" )
	@Pattern(regexp = "true|false", message = "Status must be either true or false")
    @JsonProperty("status")
    private Boolean status;
		
	@NotEmpty(message = "Pages can not be empty")
    @NotNull(message = "Pages cannot be null" )
    @JsonProperty("pages")
    private String pages;
	
	
}
