package com.project.coupon.jpaRepositories;

import com.project.coupon.jpaEntities.JpaItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaItemsRepository  extends JpaRepository<JpaItems,String> {
}
