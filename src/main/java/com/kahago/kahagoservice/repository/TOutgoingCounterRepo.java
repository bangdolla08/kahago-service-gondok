package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kahago.kahagoservice.entity.TOutgoingCounterEntity;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.TOutgoingCounterEntity;

@Repository
public interface TOutgoingCounterRepo extends JpaRepository<TOutgoingCounterEntity, Integer>{
	@Query("SELECT O FROM TOutgoingCounterEntity O WHERE (O.createBy=?1 OR O.officeCode.officeCode=?2) AND O.status=?3 AND O.createDate=?4")
	Optional<TOutgoingCounterEntity> findFirstByCreateByAndStatusAndCreateDate(String createBy,String officeCode,Integer status,LocalDate localDate);
	
	@Query("SELECT O FROM TOutgoingCounterEntity O WHERE (O.createBy=?1 OR O.officeCode.officeCode=?2) ORDER BY O.createDate DESC")
	Page<TOutgoingCounterEntity> findByCreateBy(String createBy,String officeCode,Pageable pageable);
	
	Optional<TOutgoingCounterEntity> findByCodeCounterAndStatus(String codeCounter,Integer status);
	
	Optional<TOutgoingCounterEntity> findByCodeCounter(String codeCounter);
	
	
}
