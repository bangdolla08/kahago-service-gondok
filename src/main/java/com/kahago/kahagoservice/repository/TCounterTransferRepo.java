package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.TCounterTransferEntity;
import com.kahago.kahagoservice.entity.TCreditEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 20 Des 2019
 */
public interface TCounterTransferRepo extends JpaRepository<TCounterTransferEntity, Integer> {
	Optional<TCounterTransferEntity> findByTrxDate(LocalDate trxDate);
}
