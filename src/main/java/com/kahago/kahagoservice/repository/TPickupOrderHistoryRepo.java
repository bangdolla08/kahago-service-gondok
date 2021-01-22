package com.kahago.kahagoservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.TPickupOrderHistoryEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface TPickupOrderHistoryRepo extends JpaRepository<TPickupOrderHistoryEntity, Integer>{
	List<TPickupOrderHistoryEntity> findAllByPickupOrderDetailId(Integer pickupOrderDetailId);
	
	List<TPickupOrderHistoryEntity> findAllByPickupOrderIdAndPickupOrderDetailId(String pickupOrderId,Integer pickupOrderDetailId);
}
