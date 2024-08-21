package com.project.coupon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "segment_details")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class SegmentDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "segment_name", unique = true)
    private String segmentName;

    @OneToMany(mappedBy = "segmentAttached", cascade = CascadeType.ALL)
    private List<SegmentDataEntity> segmentDataList;
}