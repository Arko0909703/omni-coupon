package com.project.coupon.user.request;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.coupon.request.PosInputSettle;

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
public class CreateUserRequest {

	@NotEmpty(message = "First Name can not be empty")
    @NotNull(message = "First Name cannot be null" )
    @JsonProperty("first_name")
    private String firstName;
	
	@NotEmpty(message = "Last Name can not be empty")
    @NotNull(message = "Last Name cannot be null" )
    @JsonProperty("last_name")
    private String lastName;
	
	@NotEmpty(message = "Phone can not be empty")
    @NotNull(message = "Phone cannot be null" )
    @JsonProperty("phone")
    private String phone;
	
	@Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")
    @JsonProperty("email")
    private String email;
	
	@NotEmpty(message = "Password can not be empty")
    @NotNull(message = "Password cannot be null" )
    @JsonProperty("password")
    private String password;
	
	@NotEmpty(message = "Status can not be empty")
    @NotNull(message = "status cannot be null" )
	@Pattern(regexp = "true|false", message = "Status must be either true or false")
    @JsonProperty("status")
    private Boolean status;
	
    @NotNull(message = "Role cannot be null" )
    @JsonProperty("role_id")
    private Integer roleId;
	
    @JsonProperty("email_notification")
    private Boolean emailNotification;
    
    @JsonProperty("sms_notification")
    private Boolean smsNotification;
}
