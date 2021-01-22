package com.kahago.kahagoservice.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MUniqTransferEntity;

@Repository
public interface MUniqTransferRepo extends JpaRepository<MUniqTransferEntity, Integer>{

	@Query("SELECT M FROM MUniqTransferEntity M "
			+ "WHERE M.status=?1 and M.nominal <=?2 ORDER BY M DESC")
	public List<MUniqTransferEntity> findAllByStatusNominal(Integer status,BigDecimal nominal);
}
