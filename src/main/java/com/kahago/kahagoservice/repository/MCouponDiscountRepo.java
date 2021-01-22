package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MCouponDiscountEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Hendro yuwono
 */
@Repository
public interface MCouponDiscountRepo extends JpaRepository<MCouponDiscountEntity, Integer> {

    Optional<MCouponDiscountEntity> findByCouponCode(String couponCode);
    List<MCouponDiscountEntity> findByShowDashboard(Boolean showDashboard);

    @Query("SELECT c FROM MCouponDiscountEntity c WHERE c.expiredStartDate <= ?1 AND c.expiredEndDate >= ?2 AND c.isActive = 1 AND c.isPublic = 1")
    List<MCouponDiscountEntity> findCouponActive(LocalDate start, LocalDate end);
    @Query("select c from MCouponDiscountEntity c where c.couponCode=:couponCode")
    MCouponDiscountEntity findByCode(String couponCode);
    
    @Query("SELECT C FROM MCouponDiscountEntity C WHERE (?1 IS NULL OR C.couponCode LIKE %?1%) AND (?2 IS NULL OR C.referenceCoupon = ?2) "
    		+ "ORDER BY C.createdAt DESC")
    Page<MCouponDiscountEntity> findAllByCouponCodeAndReference(String couponCode,Integer reference,Pageable pageable);
    
    List<MCouponDiscountEntity> findAllByReferenceCoupon(Integer id);
    
    MCouponDiscountEntity findAllById(Integer id);
}