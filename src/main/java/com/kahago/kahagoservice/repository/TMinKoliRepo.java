package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.TMinKoliEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface TMinKoliRepo extends JpaRepository<TMinKoliEntity, Long>{
	TMinKoliEntity findByProductSwCodeAndIdUserCategory(Long productSwCode,Integer idUserCategory);
}
