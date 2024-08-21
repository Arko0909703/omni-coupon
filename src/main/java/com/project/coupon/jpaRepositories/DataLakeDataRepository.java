package com.project.coupon.jpaRepositories;

import com.project.coupon.jpaEntities.DataLakeTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataLakeDataRepository extends JpaRepository<DataLakeTable,Integer> {

    List<DataLakeTable> findByMobileNumber(String number);

}
