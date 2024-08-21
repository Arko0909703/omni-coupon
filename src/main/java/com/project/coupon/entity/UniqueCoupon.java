package com.project.coupon.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document("unique_coupons")
public class UniqueCoupon {
    @Id
    private String uniqueCouponCode;
    private String baseCouponCode;
    private String displayName;
    private String description;
    private String termsAndConditions;
    public LocalDate tncEndDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String imageName;  //This will be set when image is uploaded
    private Long totalUsage;
    private Long numberOfUniqueUsers;
    private int weightage;
    private String applicableForValue;     //All , New , User Segment
    @DBRef
    private SegmentDetails applicableForSegments;       //It will contain segment from applicableFOr parameter
    private String type;       //Normal or dynamic
    @DBRef
    private List<Constructs> typeConstructs;    // If type is dynamic , then segment will be set
    private String couponUsage;
    private String couponPrefix;
    private String couponSuffix;
    private String usageType;         //System generated or Single USE
    private String lotName;
    private int lotQuantity;
    private String couponType;
    @DBRef
    private List<Items> freebieItems;
    private double discountPercentage;
    private double flatDiscount;
    private double mov;
    private double discountCap;
    private List<String> productInclusion;
    private List<String> categoryInclusion;
    private List<String> productExclusion;
    private List<String> categoryExclusion;
    private int numberOfDays;
    private int extendedNumberOfDays;
    private LocalDate firstExpiryDate;
    private LocalDate extendedExpiryDate;
    private int numberOfTimesApplicablePerUser;
    private List<String> channel;
    private List<String> channelFullfillmentType;
    private boolean hiddenUI;
    private List<String>  franchise;
    private List<String> stores;
    private List<String> cities;
    private List<String> clusters;
    private List<String> dayApplicability;
    private List<Integer>  monthApplicability;
    private Timeslot timeslot;
    private LocalDate userAttachedDate;          //Date when user will be attached and will act as an activation date for this coupon
    private String status;
    private LocalDate dateCreated;
    private int sequence;
    private boolean isMilestone;

    private boolean userSpecificConstraints;
    private String constraintType;
    private long batchCouponLastIndex;

}
