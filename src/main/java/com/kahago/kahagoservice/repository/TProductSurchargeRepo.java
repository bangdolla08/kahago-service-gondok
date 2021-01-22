package com.kahago.kahagoservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.TProductSurchargeEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface TProductSurchargeRepo extends JpaRepository<TProductSurchargeEntity, Integer>{
	List<TProductSurchargeEntity> findAllByProductSwCodeAndStatus(Integer productSwCode,Boolean status);
	List<TProductSurchargeEntity> findAllByProductSwCode(Integer productSwCode);
}
