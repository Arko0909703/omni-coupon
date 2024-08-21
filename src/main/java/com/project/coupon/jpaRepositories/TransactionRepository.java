package com.project.coupon.jpaRepositories;

import com.project.coupon.jpaEntities.TransactionTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionTable,String> {

    @Query("SELECT u FROM TransactionTable u WHERE LOWER(u.receiptNumber) = LOWER(:id)")
    Optional<TransactionTable> findByIdIgnoreCase(@Param("id") String id);
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END FROM TransactionTable t WHERE LOWER(t.status) IN ('settled', 'redeemed') AND t.mobileNumber = :mobileNumber AND LOWER(t.couponCode) = LOWER(:couponCode)")
    Boolean existsByCouponCodeAndMobileNumber(String couponCode,String mobileNumber);

    @Query("SELECT t FROM TransactionTable t WHERE LOWER(t.status) IN ('settled', 'redeemed') AND t.mobileNumber = :mobileNumber AND LOWER(t.couponCode) = LOWER(:couponCode)")
    List<TransactionTable> findByStatusAndCouponCodeAndMobileNumber(String couponCode, String mobileNumber);

    @Query(value = "Select count(*) from transactiontable t Where LOWER(t.status) IN ('settled', 'redeemed') AND LOWER(t.coupon_code)=LOWER(:couponCode)",nativeQuery = true)
    int getNumberOfColumnsWithCouponCode(String couponCode);

    @Query(value = "SELECT COUNT(DISTINCT mobile_number) FROM transactiontable t Where LOWER(t.status) IN ('settled', 'redeemed') AND LOWER(t.coupon_code)=LOWER(:couponCode)", nativeQuery = true)
    int getNumberOfColumnswithDistintMobileNumber(String couponCode);

    @Query("SELECT t FROM TransactionTable t WHERE LOWER(t.status) IN ('settled', 'redeemed') AND LOWER(t.receiptNumber) = LOWER(:receiptNumber)")
    List<TransactionTable> findByReceiptNumberIgnoreCase(String receiptNumber);

}
