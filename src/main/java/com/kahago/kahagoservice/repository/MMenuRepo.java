package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MMenuEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface MMenuRepo extends JpaRepository<MMenuEntity, Integer>{
	MMenuEntity findByMenuId(Integer menuId);
}
