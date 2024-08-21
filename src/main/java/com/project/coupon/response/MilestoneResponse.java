package com.project.coupon.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MilestoneResponse {

    private List<SegmentMilestoneMappingResponse> milestones;
    private String status;
    @JsonProperty("present_milestone")
    private int presentMilestone;

    @JsonProperty("journey_name")
    private String journeyName;


}
