package com.project.coupon.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class BaseCouponDetails {

	@JsonProperty("coupon_name")
	public String couponName;
	@JsonProperty("basecoupon_id")
	public String baseCouponId;
	@JsonProperty("start_date")
	public LocalDate startDate;
	@JsonProperty("end_date")
	public LocalDate endDate;
	@JsonProperty("status")
	public boolean status;
	@JsonProperty("description")
	public String description;
	@JsonProperty("tnc")
	public String tnc;
	@JsonProperty("tnc_end_date")
	public LocalDate tncEndDate;
	@JsonProperty("create_date")
	public LocalDateTime createDate;
	@JsonProperty("modified_date")
	public LocalDateTime modifiedDate;
	@JsonProperty("sequence")
	public Integer sequence;
}
