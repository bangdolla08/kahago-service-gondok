package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MOptionPaymentEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface MOptionPaymentRepo extends JpaRepository<MOptionPaymentEntity, Integer>{
	MOptionPaymentEntity findByCode(String code);
	MOptionPaymentEntity findBySeqid(Integer seqid);
}
