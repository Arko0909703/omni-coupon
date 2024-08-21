package com.project.coupon.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document("segments")
public class Segment {

    @Id
    private String segmentId;
    private String segmentName;
//    @DBRef
//    private List<Users> usersList;
    private String mobileNumber;
    private String campaignName;
}
