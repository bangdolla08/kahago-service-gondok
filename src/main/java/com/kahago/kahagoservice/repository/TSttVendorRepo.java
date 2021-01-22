package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MAreaDetailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.TSttVendorEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface TSttVendorRepo extends JpaRepository<TSttVendorEntity, Integer> {
    TSttVendorEntity findFirstBySwitcherCodeAndFlagAndOrigin(Integer switcherCode, Integer flag, String origin);

    @Query(value = "SELECT t.origin,t.switcher_code,count(t.seq) from t_stt_vendor as t WHERE t.flag=?1 group by t.origin,t.switcher_code", nativeQuery = true)
    Object[][] countGroup(Integer status);

    @Query("SELECT stt FROM TSttVendorEntity stt WHERE " +
            "(?1 IS NULL OR stt.flag=?1) " +
            "AND (?2 IS NULL OR stt.switcherCode=?2) " +
            "AND (?3 IS NULL OR stt.origin=?3) " +
            "AND (?4 IS NULL OR stt.stt LIKE %?4%)")
    Page<TSttVendorEntity> findByFlagAndSwitcherCodeAndOrigin(Integer flag, Integer switcherCode, String origin, String stt, Pageable pageable);
}
