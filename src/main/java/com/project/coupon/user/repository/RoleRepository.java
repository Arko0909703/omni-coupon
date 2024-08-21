package com.project.coupon.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.coupon.user.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
	public RoleEntity findByRoleName(String roleName);
}
