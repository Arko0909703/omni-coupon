package com.project.coupon.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document(collection="milestones_segment_mapping")
public class SegmentMilestoneMapping {

    @Id
    private String customerSegmentId;
    private String couponCode;
    private String couponDisplayName;
    private String milestoneName;
    private int milestoneNumber;      //For checking , if it is 1 milestone coupon , 2 or 3rd one
    @DBRef
    private MilestoneDetails milestoneDetails;
    private String status;
    private LocalDate journeyDateCreated;
    private int numbersOfOrders;       //This is a condition for unlocking the milestone coupon
//    private List<String> orderSource;     //numberOdOrders to consider from which source like DINEIN , ONLINE , Aggregator

}
