package com.project.coupon.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class UserDetails {

	@JsonProperty("first_name")
    private String firstName;
		
    @JsonProperty("last_name")
    private String lastName;
	
    @JsonProperty("phone")
    private String phone;
    
    @JsonProperty("email")
    private String email;
	
    @JsonProperty("role_id")
    private Integer roleId;
    
    @JsonProperty("status")
    private Boolean status;
    
    @JsonProperty("email_notification")
    private Boolean emailNotification;
    
    @JsonProperty("sms_notification")
    private Boolean smsNotification;
}
