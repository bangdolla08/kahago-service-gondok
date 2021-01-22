package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MUserPriorityEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface MUserPriorityRepo extends JpaRepository<MUserPriorityEntity, Integer>{
	MUserPriorityEntity findByUserCategory(Integer userCategory);
}
