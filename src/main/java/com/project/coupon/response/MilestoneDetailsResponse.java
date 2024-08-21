package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MilestoneDetailsResponse {

    @JsonProperty("journey_name")
    private String journeyName;
    @JsonProperty("date_created")
    private LocalDate dateCreated;
    @JsonProperty("end_date")
    private LocalDate endDate;
    @JsonProperty("last_updated_by")
    private String lastUpdatedBy;
    private String status;


}
