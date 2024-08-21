package com.project.coupon.jpaRepositories;

import com.project.coupon.jpaEntities.ProductMaster;
import com.project.coupon.jpaEntities.StoreMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductMasterRepository extends JpaRepository<ProductMaster,String> {

    @Query("SELECT u FROM ProductMaster u WHERE LOWER(u.itemCode) = LOWER(:id)")
    Optional<ProductMaster> findByIdIgnoreCase(@Param("id") String id);

    @Query("SELECT p FROM ProductMaster p ORDER BY p.category")
    List<ProductMaster> findAllOrderedByCategory();
}
