package com.project.coupon.jpaEntities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
public class StoreMaster {

    private String champsId;
    private String bu;
    private String storeCoden1;
    private String storeCoden2;
    private String zomato;
    private String swiggy;
    private String storeNamePratap;
    private String maStoreName;
    private String bdName;
    private String pAndLName;
    private String state;
    private String operationalStatus;
    @Id
    private String storeCode;
    private String name;
    private String city;
    private String franchise;
    private String zone;
    private String format;
    private String region;
    private String tier;
    private String cluster;
}
