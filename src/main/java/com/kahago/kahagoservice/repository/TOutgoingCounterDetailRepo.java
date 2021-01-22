package com.kahago.kahagoservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kahago.kahagoservice.entity.TOutgoingCounterDetailEntity;
import com.kahago.kahagoservice.entity.TOutgoingCounterEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.TOutgoingCounterDetailEntity;
import com.kahago.kahagoservice.entity.TOutgoingCounterEntity;

@Repository
public interface TOutgoingCounterDetailRepo extends JpaRepository<TOutgoingCounterDetailEntity, Integer>{
	TOutgoingCounterDetailEntity findByBookingCode(TPaymentEntity bookingCode);
	Integer countByOutgoingCounterIdAndStatus(TOutgoingCounterEntity outgoingCounterId, Integer status);
	@Query("SELECT O FROM TOutgoingCounterDetailEntity O "
			+ "WHERE O.outgoingCounterId.createBy=?1 AND (?2 IS NULL OR O.bookingCode.bookingCode=?2) "
			+ "And (?3 IS NULL OR O.outgoingCounterId.codeCounter=?3) ")
	Page<TOutgoingCounterDetailEntity> findAllBySeacrh(String userid,String bookingCode,String code,Pageable pageable);
	
	List<TOutgoingCounterDetailEntity> findByOutgoingCounterId(TOutgoingCounterEntity outgoingCounterId);
	
	TOutgoingCounterDetailEntity findByBookingCodeBookingCodeAndOutgoingCounterId(String bookingCode,TOutgoingCounterEntity outgoingCounterId);
	@Query("SELECT CASE WHEN (COUNT(T) > 0) Then True ELSE False END FROM TOutgoingCounterDetailEntity T "
			+ "Where T.bookingCode=?1")
	boolean existByBookingCode(TPaymentEntity pay);
	
	
}
