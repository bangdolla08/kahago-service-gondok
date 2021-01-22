package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.TBookDetailHistoryEntity;
import com.kahago.kahagoservice.entity.TPaymentHistoryEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author bangd ON 28/11/2019
 * @project com.kahago.kahagoservice.repository
 */
@Repository
public interface TBookDetailHistoryRepo extends JpaRepository<TBookDetailHistoryEntity,Integer> {
	TBookDetailHistoryEntity findFirstByBookingCode(String bookingCode);
	
	List<TBookDetailHistoryEntity> findByBookingCode(String bookingCode);
	
	List<TBookDetailHistoryEntity> findByPaymentHistory(TPaymentHistoryEntity tHistoryEntity);
	
}
