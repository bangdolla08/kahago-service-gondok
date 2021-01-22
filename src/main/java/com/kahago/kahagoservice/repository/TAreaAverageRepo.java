package com.kahago.kahagoservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.TAverageArea;

@Repository
public interface TAreaAverageRepo extends JpaRepository<TAverageArea, Integer>{
	@Query("SELECT COUNT(T) FROM TAverageArea T WHERE T.isCheck=0")
	Integer countBySelisih();
	@Query("SELECT T FROM TAverageArea T WHERE (?1 IS NULL OR T.area.status=?1) "
			+ "AND (?2 IS NULL OR T.area.vendor.switcherCode=?2) "
			+ "AND (?3 IS NULL OR T.area.seqid=?3) "
			+ "AND (?4 IS NULL OR T.isCheck=?4)")
	Page<TAverageArea> findAllAVG(Integer status,Integer switcherCode,Integer areacode,Boolean flagArea,Pageable pageable);
	
	@Query("SELECT T FROM TAverageArea T WHERE T.area.vendor.switcherCode=?1 AND T.area.areaId.idPostalCode=?2 ")
	List<TAverageArea> findByAreaIdAndVendorCode(Integer switcherCode,Integer areaId );
}
