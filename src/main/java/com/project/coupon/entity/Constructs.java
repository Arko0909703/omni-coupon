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
@Document("constructs")
public class Constructs {

    @Id
    private String construct_id;
    @DBRef
    private SegmentDetails uploadSegment;
    private String coupon_usage="Multiuse";
    //private String mobileNumber;
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
    private int lotQuantity;
    private List<String> channelFullfillmentType;
    private boolean hiddenUI;
    private List<String>  franchise;
    private List<String> channel;
    private List<String> stores;
    private List<String> cities;
    private List<String> clusters;
    private List<String> dayApplicability;
    private List<Integer>  monthApplicability;
    private Timeslot timeslot;
    private LocalDate userAttachedDate;
    private String status;
    private LocalDate dateCreated;
}
