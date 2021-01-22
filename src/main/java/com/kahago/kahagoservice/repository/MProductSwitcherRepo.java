package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.entity.MSwitcherEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MProductSwitcherRepo extends JpaRepository<MProductSwitcherEntity, Long> {
	@Query("select max(ps.productSwCode) from MProductSwitcherEntity ps WHERE ps.status IN ?1 group by ps.productSwCode.switcherEntity.switcherCode,ps.displayName")
	List<Long> getMaxSwitcherCode(List<Byte> status);
		
	@Query("select ps from MProductSwitcherEntity ps where  ps.status =?2 and ps.isLeadtime=?1 order by ps.prioritySeq")
	List<MProductSwitcherEntity> getByProductSWAndSwitcherCodeAndActive(Boolean isleadtime,Byte status);
	@Query("select ps from MProductSwitcherEntity ps where  ps.isLeadtime=?1 order by ps.prioritySeq")
	List<MProductSwitcherEntity> getByProductSWAndSwitcherCode(Boolean isLeadTime);
	@Query("SELECT P FROM MProductSwitcherEntity P "
			+ "WHERE P.switcherEntity.switcherCode=?1")
	List<MProductSwitcherEntity> findBySwictherCode(Integer swcode);
	@Query("SELECT P FROM MProductSwitcherEntity P WHERE (?1 IS NULL OR upper(P.name) LIKE %?1% OR upper(P.displayName) LIKE %?1% OR upper(P.switcherEntity.name) LIKE %?1%) "
			+ "ORDER BY P.prioritySeq asc")
	Page<MProductSwitcherEntity> findAllCodeOrName(String cari,Pageable pageable);
	
	MProductSwitcherEntity findByProductSwCode(Long productSwCode);
	@Query("select ps from MProductSwitcherEntity ps where ps.status = 0 order by ps.prioritySeq")
	List<MProductSwitcherEntity> getAllProductActive();
	
	MProductSwitcherEntity findByName(String name);
}
