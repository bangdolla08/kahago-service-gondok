package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MPostalCodeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bangd ON 17/11/2019
 * @project com.kahago.kahagoservice.repository
 */
@Repository
public interface MPostalCodeRepo extends JpaRepository<MPostalCodeEntity,Integer> {
	@Query("SELECT M FROM MPostalCodeEntity M WHERE (?1 IS NULL OR M.kecamatanEntity.areaDetailId=?1)")
    List<MPostalCodeEntity> findAllByKecamatanEntityAreaDetailId(Integer areaDetailId);
    List<MPostalCodeEntity> findByPostalCodeLike(String postalCode);
    MPostalCodeEntity findByIdPostalCode(Integer idPostalCode);
    @Query("SELECT MP FROM MPostalCodeEntity MP WHERE (?1 IS NULL OR " +
            "MP.kelurahan LIKE %?1% OR " +
            "MP.kecamatanEntity.kecamatan LIKE %?1% OR " +
            "MP.kecamatanEntity.kotaEntity.name LIKE %?1% OR " +
            "MP.kecamatanEntity.kotaEntity.provinsiEntity.name LIKE %?1%) "+
            "AND (?2 IS NULL OR MP.idPostalCode = ?2)")
    Page<MPostalCodeEntity> findBySearch(String search, Integer idPostalCode, Pageable pageable);
}
