package com.kahago.kahagoservice.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.TRespTransferEntity;

@Repository
public interface TRespTransferRepo extends JpaRepository<TRespTransferEntity, Integer> {
	
	Optional<TRespTransferEntity> findByKreditAndLastUpdateOrderByIdDesc(BigDecimal kredit,LocalDate lastUpdate);
	@Query("select TR from TRespTransferEntity TR where date(TR.lastTime) between ?1 and ?2 order by TR.lastTime desc ")
	Page<TRespTransferEntity> findAllByLastTime(Date startDate,Date endDate,Pageable pageable);
	@Query("select TR from TRespTransferEntity TR order by TR.lastTime desc")
	Page<TRespTransferEntity> findAll(Pageable pageable);
	
}
