package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MPropEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface MPropRepo extends JpaRepository<MPropEntity, Long>{
	MPropEntity findFirstByStatusOrderByReleaseDateDesc(Byte status);
	MPropEntity findFirstByStatusAndPlatformOrderByReleaseDateDesc(Byte status,Integer platform);
}
