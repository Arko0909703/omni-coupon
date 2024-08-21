package com.project.coupon.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.coupon.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer>{

	public UserEntity findByEmail(String email);
}
