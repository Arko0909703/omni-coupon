package com.project.coupon.user.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.coupon.user.response.RoleDetails;

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
public class PageDetails {

	@JsonProperty("page_id")
    private String pageId;
	
	@JsonProperty("page_name")
    private String pageName;
}
