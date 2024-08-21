package com.project.coupon.jpaEntities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
public class JpaItems {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_code")
    private String itemCode;

    private String base;

    private String size;

    private int quantity;

    private double price;

    @Column(name = "deal_id")
    private String dealId;
    @ManyToOne
    private TransactionTable transactionTable;

}
