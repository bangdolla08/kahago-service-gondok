package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MAreaDetailEntity;
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
public interface MAreaDetailRepo extends JpaRepository<MAreaDetailEntity,Integer> {
	@Query("SELECT M FROM MAreaDetailEntity M WHERE (?1 IS NULL OR M.kotaEntity.areaKotaId=?1)")
    List<MAreaDetailEntity> findAllByKotaEntityAreaKotaId(Integer areaKotaId);
    MAreaDetailEntity findByKecamatanAndKota(String kecamatan,String kota);
    MAreaDetailEntity findByAreaDetailId(Integer areaDetailId);
    @Query("SELECT MAD FROM MAreaDetailEntity MAD WHERE (?1 IS NULL OR MAD.kecamatan LIKE %?1% OR " +
            "MAD.kotaEntity.name LIKE %?1% OR MAD.kotaEntity.provinsiEntity.name  LIKE %?1%)")
    Page<MAreaDetailEntity> findBy(String search, Pageable pageable) ;
}
