package com.kahago.kahagoservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MCouponOptionPaymentEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface MCouponOptionPaymentRepo extends JpaRepository<MCouponOptionPaymentEntity, Integer> {
	List<MCouponOptionPaymentEntity> findAllByIdCoupon(Integer idCoupon);
	@Modifying
	@Query(value="insert into m_coupon_option_payment (id_coupon,option_payment_id) values (?1,?2)",nativeQuery=true)
	void insertToCouponOption(Integer idCoupon,Integer optionPayment);
	@Modifying
	@Query(value="delete from m_coupon_option_payment where id_coupon=?1",nativeQuery=true)
	void deleteByIdCoupon(Integer idCoupon);
}
