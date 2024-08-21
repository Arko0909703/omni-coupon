package com.project.coupon.jpaRepositories;

import com.project.coupon.jpaEntities.StoreMaster;
import com.project.coupon.jpaEntities.TransactionTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreMasterRepository extends JpaRepository<StoreMaster,String> {

    @Query("SELECT u FROM StoreMaster u WHERE LOWER(u.storeCode) = LOWER(:id)")
    Optional<StoreMaster> findByIdIgnoreCase(@Param("id") String id);

    @Query("SELECT s FROM StoreMaster s WHERE LOWER(s.operationalStatus) = 'operational'")
    List<StoreMaster> findAllOperationalStores();
}
