package com.project.coupon.repository;

import com.project.coupon.entity.Items;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemsRepository extends MongoRepository<Items,String> {
}
