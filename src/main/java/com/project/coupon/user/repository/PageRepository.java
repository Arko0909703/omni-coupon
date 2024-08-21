package com.project.coupon.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.coupon.user.entity.PageEntity;

public interface PageRepository extends JpaRepository <PageEntity, Integer>{
	public PageEntity findByPageName(String pageName);
	public List<PageEntity> findAllByPageIdIn(List<Integer> pageId);
}
