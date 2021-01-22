package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MManifestPosEntity;

import java.util.Optional;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface MManifestPosRepo extends JpaRepository<MManifestPosEntity, Integer>{
	Optional<MManifestPosEntity> findByUseridAndStatus(String userid, Integer status);
	Boolean existsByUseridAndStatus(String clientCode, Integer status);
}
