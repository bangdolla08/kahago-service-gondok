package com.kahago.kahagoservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.TDiscountEntity;

@Repository
public interface TDiscountRepo extends JpaRepository<TDiscountEntity, Integer>{

	Optional<TDiscountEntity> findByNoTiket(String noTiket);
	
	List<TDiscountEntity> findByNoTiketIn(List<String> noTiket);
}
