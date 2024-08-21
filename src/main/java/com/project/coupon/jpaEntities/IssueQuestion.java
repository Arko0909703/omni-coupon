package com.project.coupon.jpaEntities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
public class IssueQuestion {

    @Id
    private String id;

    private String question;

    private boolean status;
    private LocalDateTime dateTime;
}
