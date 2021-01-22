package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MAreaProvinsiEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author bangd ON 17/11/2019
 */
@Repository
public interface MAreaProvinsiRepo extends JpaRepository<MAreaProvinsiEntity,Integer> {
    MAreaProvinsiEntity findByAreaProvinsiId(Integer areaProvinsiId);
    @Query("SELECT P FROM MAreaProvinsiEntity P WHERE (?1 IS NULL OR P.name LIKE %?1%)")
    Page<MAreaProvinsiEntity> findAll(String filter, Pageable pageable);
}
