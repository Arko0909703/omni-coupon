package com.project.coupon.jpaEntities;


import jakarta.persistence.Column;
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
public class ProductMaster {

    @Id
    @Column(name="item_code")
    private String itemCode;
    private String description;
    private String category;
    @Column(name = "sub_category")
    private String subCategory;
    private String name;
    @Column(name = "product_range")
    private String productRange;
    private String size;
    @Column(name = "diet_preference")
    private String dietPreference;
    @Column(name = "old_or_fresh")
    private String oldOrFresh;


}
