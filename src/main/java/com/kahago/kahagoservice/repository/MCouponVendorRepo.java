package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MCouponCategoryUserEntity;
import com.kahago.kahagoservice.entity.MCouponDiscountEntity;
import com.kahago.kahagoservice.entity.MCouponVendorEntity;
import com.kahago.kahagoservice.entity.MSwitcherEntity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 16 Des 2019
 */
public interface MCouponVendorRepo extends PagingAndSortingRepository<MCouponVendorEntity, Integer> {
	@Query("SELECT C FROM MCouponVendorEntity C "
			+ "WHERE C.idCoupon=?1 AND C.switcherCode=?2")
	List<MCouponVendorEntity> findByIdCouponAndVendorEntities(Integer id,MSwitcherEntity switcherCode);
	
	List<MCouponVendorEntity> findAllByIdCoupon(Integer idCoupon);
	@Modifying
	@Query(value="insert into m_coupon_vendor (id_coupon,switcher_code) values (?1,?2)",nativeQuery=true)
	void insertToCouponVendor(Integer idCoupon,Integer switcherCode);
	
	@Modifying
	@Query(value="delete from m_coupon_vendor where id_coupon = ?1",nativeQuery=true)
	void deleteByIdCoupon(Integer idCoupon);
}