package com.project.coupon.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UserSpecificConstraintsRequest {

    @JsonProperty("enable_constraints")
    private boolean enableConstraints;

    @JsonProperty("constraint_type")
    private String constraintType;

    @JsonProperty("number_of_days")
    private Integer numberOfDays;

    @JsonProperty("extended_number_of_days")
    private Integer extendedNumberOfDays;

    @JsonProperty("first_expiry_date")
    private LocalDate firstExpiryDate;

    @JsonProperty("extended_expiry_date")
    private LocalDate extendedExpiryDate;

    @JsonProperty("number_of_times_applicable_per_user")
    private Integer numberOfTimesApplicablePerUser;

    @JsonProperty("coupon_name")
    private String couponName;


}
