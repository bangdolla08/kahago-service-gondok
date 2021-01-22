package com.kahago.kahagoservice.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kahago.kahagoservice.entity.TFeeReferenceEntity;

/**
 * @author Ibnu Wasis
 */
public interface TFeeReferenceRepo extends JpaRepository<TFeeReferenceEntity, Long>{
	@Query("select T from TFeeReferenceEntity T where T.userId = :userId and date(T.tglTrx) between :startDate "
			+ "and :endDate")
	List<TFeeReferenceEntity> findByUserIdAndTglTrx(String userId,Date startDate,Date endDate);
}
