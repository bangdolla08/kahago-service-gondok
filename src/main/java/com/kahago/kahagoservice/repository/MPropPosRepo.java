package com.kahago.kahagoservice.repository;
/**
 * @author Ibnu Wasis
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MPropPosEntity;

@Repository
public interface MPropPosRepo extends JpaRepository<MPropPosEntity, Integer>{
	MPropPosEntity findByOfficeCode(String officeCode);
}
