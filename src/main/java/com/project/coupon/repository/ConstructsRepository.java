package com.project.coupon.repository;

import com.project.coupon.entity.Constructs;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConstructsRepository extends MongoRepository<Constructs,String> {
}
