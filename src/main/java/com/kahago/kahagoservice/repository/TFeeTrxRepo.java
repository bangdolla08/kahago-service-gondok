package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.TFeeTrxEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface TFeeTrxRepo extends JpaRepository<TFeeTrxEntity, Integer>{
	TFeeTrxEntity findByIdMUserCategoryAndFee(Integer IdUserCategory,Integer fee);
}
