package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MCouponCategoryUserEntity;
import com.kahago.kahagoservice.entity.MCouponDiscountEntity;
import com.kahago.kahagoservice.entity.MCouponProductEntity;
import com.kahago.kahagoservice.entity.MCouponVendorEntity;
import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
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
public interface MCouponProductRepo extends PagingAndSortingRepository<MCouponProductEntity, Integer> {
	@Query("SELECT C FROM MCouponProductEntity C "
			+ "WHERE C.idCoupon=?1 AND C.productSwCode=?2")
	List<MCouponProductEntity> findByIdCouponAndProduct(Integer id,MProductSwitcherEntity switcherCode);
	List<MCouponProductEntity> findAllByIdCoupon(Integer idCoupon);
	
	@Modifying
	@Query(value="insert into m_coupon_product (id_coupon,product_sw_code) values (?1,?2)",nativeQuery=true)
	void insertToCouponProduct(Integer idCoupon,Integer productSwCode);
	
	@Modifying
	@Query(value="delete from m_coupon_product where id_coupon=?1",nativeQuery=true)
	void deleteByIdCoupon(Integer idCoupon);
}