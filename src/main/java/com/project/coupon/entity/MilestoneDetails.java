package com.project.coupon.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document(collection="milestone_details")
public class MilestoneDetails {

    @Id
    private String journeyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate createdDate;
    private String status;
    private String lastUpdatedBy;
    @DBRef
    private SegmentDetails segment;
    private boolean sequential;
}
