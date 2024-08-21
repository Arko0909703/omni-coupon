package com.project.coupon.repository;

import com.project.coupon.entity.ItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemsEntityRepository extends JpaRepository<ItemsEntity, Long> {
    ItemsEntity findByItemCode(String itemCode);
}
