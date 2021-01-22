package com.kahago.kahagoservice.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TMutasiEntity;

import java.util.List;

/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 19 Nov 2019
 */
@Repository
public interface TMutasiRepo extends JpaRepository<TMutasiEntity, Integer>{
    @Query("SELECT T FROM TMutasiEntity T WHERE T.userId.userId=?1 AND T.trxDate BETWEEN ?2 AND ?3 AND T.trxType in (?4) ORDER BY T.counterMutasi ASC ")
    Page<TMutasiEntity> findAllByParameter(String userId, LocalDate dateStart, LocalDate dateEnd, List<Integer> trxType, Pageable pageable);
	List<TMutasiEntity> findByUserIdOrderByCounterMutasiDesc(String userId);
	List<TMutasiEntity> findTopByUserIdOrderByCounterMutasiDesc(MUserEntity userId);
	
	TMutasiEntity findFirstByUserIdOrderByCounterMutasiDesc(MUserEntity userId);
}
