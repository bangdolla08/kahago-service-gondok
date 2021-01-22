package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MPostalCodeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.kahago.kahagoservice.entity.MVendorAreaEntity;

import java.util.List;

/**
 * @author Ibnu Wasis
 */
public interface MVendorAreaRepo extends JpaRepository<MVendorAreaEntity, Long> {
	@Query("SELECT MV FROM MVendorAreaEntity MV WHERE (?1 IS NULL OR MV.requestName LIKE %?1% OR MV.sendRequest LIKE %?1%) "
			+ "AND (?2 IS NULL OR MV.switcherCode = ?2) "
			+ "AND (?3 IS NULL OR MV.postalCodeId.postalCode LIKE %?3% OR MV.postalCodeId.kelurahan LIKE %?3% OR MV.areaId.kecamatan LIKE %?3% "
			+ "OR MV.areaId.kotaEntity.name LIKE %?3% OR MV.areaId.kotaEntity.provinsiEntity.name LIKE %?3%) "
			+ "ORDER BY MV.postalCodeId ASC,MV.switcherCode ASC")
	Page<MVendorAreaEntity> findAllByRequestName(String areaCode, Integer switcherCode, String search, Pageable pageable);
	@Query("SELECT MV FROM MVendorAreaEntity MV WHERE MV.postalCodeId=?1 AND (?2 IS NULL OR MV.switcherCode=?2)")
	List<MVendorAreaEntity> findAllByPostalCodeIdAndSwitcherCode(MPostalCodeEntity mPostalCodeEntity,Integer switcherCode);
	@Query("SELECT MV FROM MVendorAreaEntity MV WHERE MV.postalCodeId.idPostalCode=?1")
	List<MVendorAreaEntity> findAllByPostalCodeId(Integer postalCodeId);
	@Query("SELECT MV FROM MVendorAreaEntity MV WHERE MV.postalCodeId.idPostalCode=?2 AND MV.areaId.areaDetailId=?1 AND MV.switcherCode=?3")
	MVendorAreaEntity findByAreaIdAndPostalCode(Integer areaId,Integer postalCode,Integer switcherCode);
	@Query("SELECT m.postalCodeId FROM MVendorAreaEntity  m WHERE " +
			"m.switcherCode=?2 " +
			"AND m.status=?3 " +
			"AND (?1 IS NULL " +
			"OR m.postalCodeId.kelurahan LIKE %?1% " +
			"OR m.areaId.kecamatan LIKE %?1% " +
			"OR m.areaId.kotaEntity.name LIKE %?1% " +
			"OR m.areaId.kotaEntity.provinsiEntity.name LIKE %?1% ) GROUP BY m.postalCodeId")
	Page<MPostalCodeEntity> findByPostalCode(String search,Integer switcherCode,Integer status, Pageable pageable);
	@Query("SELECT m FROM MVendorAreaEntity  m WHERE " +
			"(?2 IS NULL OR m.switcherCode=?2 )" +
			"AND (?3 IS NULL  OR m.status=?3) " +
			"AND (?1 IS NULL " +
			"OR m.postalCodeId.kelurahan LIKE %?1% " +
			"OR m.areaId.kecamatan LIKE %?1% " +
			"OR m.areaId.kotaEntity.name LIKE %?1% " +
			"OR m.areaId.kotaEntity.provinsiEntity.name LIKE %?1% ) "+
			"AND (?4 IS NULL OR m.postalCodeId.idPostalCode = ?4)")
	Page<MVendorAreaEntity> findBy(String search,Integer switcherCode,Integer status,Integer idPOstalCode,Pageable pageable);
	@Query("SELECT M FROM MVendorAreaEntity M WHERE M.switcherCode=?1 AND M.postalCodeId.idPostalCode=?2")
	MVendorAreaEntity findBySwitcherAndPostalCode(Integer switcher,Integer postalCodeId);
	
	@Query("SELECT COUNT(M) FROM MVendorAreaEntity M WHERE M.status IN (?1)")
	Integer countVendorAreaByStatus(List<Integer> lsStatus);
}
