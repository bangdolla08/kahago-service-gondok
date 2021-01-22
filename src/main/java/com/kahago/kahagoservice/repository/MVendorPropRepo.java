package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.MVendorPropEntity;

import java.util.Optional;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface MVendorPropRepo extends JpaRepository<MVendorPropEntity, Integer>{
	MVendorPropEntity findAllBySwitcherCodeAndActionAndOrigin(MSwitcherEntity switcher,String action, String origin);
	Optional<MVendorPropEntity> findBySwitcherCodeSwitcherCodeAndActionAndOrigin(Integer switcherCode, String action, String origin);
}
