package com.project.coupon.repository;

import com.project.coupon.entity.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsersRepository extends MongoRepository<Users,String> {

    Users findByMobileNumber(String mobileNumber);
}
