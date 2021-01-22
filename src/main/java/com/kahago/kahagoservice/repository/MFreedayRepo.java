package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MFreedayEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface MFreedayRepo extends JpaRepository<MFreedayEntity, Integer>{
	MFreedayEntity findByDayNameIgnoreCaseContainingAndIsActive(String dayName,int isActive);
	
	MFreedayEntity findByDayNameIgnoreCaseContaining(String dayName);
}
