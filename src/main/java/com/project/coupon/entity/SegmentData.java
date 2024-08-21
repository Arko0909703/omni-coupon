package com.project.coupon.entity;


import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document("segments_data")
public class SegmentData {

    @Id
    private String id;

    private String mobileNumber;

    @DBRef
    private SegmentDetails segmentAttached;

    private LocalDate segmentAttachedDate;

}
