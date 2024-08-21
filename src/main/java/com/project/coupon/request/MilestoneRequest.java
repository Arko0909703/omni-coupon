package com.project.coupon.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MilestoneRequest {

    @JsonProperty("journey_name")
    private String journeyName;
    @JsonProperty("start_date")
    private LocalDate startDate;
    @JsonProperty("expiry_date")
    private LocalDate expiryDate;
    @JsonProperty("last_updated_by")
    private String lastUpdatedBy;
    private boolean sequential;
    @JsonProperty("milestone_data")
    List<MilestoneCouponRequest> milestoneCouponRequestList;
}
