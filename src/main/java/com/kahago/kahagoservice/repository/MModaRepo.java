package com.kahago.kahagoservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MModaEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface MModaRepo extends JpaRepository<MModaEntity, Integer>{
	MModaEntity findByIdModa(Integer idModa);
	
	List<MModaEntity> findAllByFlag(Byte flag);
}
