package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MAreaKotaEntity;
import com.sun.mail.imap.protocol.INTERNALDATE;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bangd ON 17/11/2019
 */
@Repository
public interface MAreaKotaRepo extends JpaRepository<MAreaKotaEntity,Integer> {
	@Query("SELECT M FROM MAreaKotaEntity M WHERE (?1 IS NULL OR M.provinsiEntity.areaProvinsiId=?1)")
    List<MAreaKotaEntity> findAllByProvinsiEntityAreaProvinsiId(Integer provinsiId);
    MAreaKotaEntity findByAreaKotaId(Integer areaKotaId);
    @Query("SELECT K FROM MAreaKotaEntity K WHERE (?1 IS NULL OR K.name like %?1% OR K.provinsiEntity.name LIKE %?1%)")
    Page<MAreaKotaEntity> findBy(String filter, Pageable pageable);
}
