package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.THppPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPermohonanEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;



/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 9 Jun 2020
 */
@Repository
public interface THppPaymentRepo extends JpaRepository<THppPaymentEntity,String> {
	Optional<THppPaymentEntity> findByBookingCode(TPaymentEntity bookingCode);
}
