package com.project.coupon.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
public class UniqueCouponDetails {

	@JsonProperty("coupon_name")
	public String couponName;
	@JsonProperty("base_coupon_code")
	public String baseCouponCode;
	@JsonProperty("Start_date")
	public LocalDate startDate;
	@JsonProperty("end_date")
	public LocalDate endDate;
	@JsonProperty("status")
	public boolean status;
	@JsonProperty("description")
	public String description;
	@JsonProperty("display_name")
	private String displayName;
	@JsonProperty("tnc")
	public String tnc;
	@JsonProperty("tnc_end_date")
	public LocalDate tncEndDate;
	@JsonProperty("sequence")
	public Integer sequence;
	@JsonProperty("total_usage")
	public Long totalUsage;
	@JsonProperty("no_of_unique_users")
	public Long noOfUniqueUsers;
	@JsonProperty("applicable_value")
	private String applicableForValue;
    @JsonProperty("img_url")
    private String imgUrl;
    @JsonProperty("create_date")
	public LocalDateTime createDate;
    @JsonProperty("modified_date")
	public LocalDateTime modifiedDate;
}
