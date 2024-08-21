package com.project.coupon.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document("users")
public class Users {

    @Id
    private String mobileNumber;
//    private String username;
//    private String email;
    private String campaignName;
}
