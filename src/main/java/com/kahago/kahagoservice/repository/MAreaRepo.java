package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.*;
import java.util.List;
import java.util.Set;

/**
 * @author bangd ON 17/11/2019
 * @project KahaGo Service
 */
public interface MAreaRepo extends JpaRepository<MAreaEntity,String> {
    List<MAreaEntity> findAllByStatus(Boolean status);
    @Query("SELECT MA FROM MAreaEntity MA WHERE MA.areaId IN (?1) AND MA.status=?2 ")
    List<MAreaEntity> findAllByAreaIdAndStatus(List<String> areaId,Boolean status);
    MAreaEntity findByKotaEntityAreaKotaId(Integer areaKotaId);
    @Query("SELECT A FROM MAreaEntity A WHERE A.areaId=?1 OR A.areaName=?1")
    MAreaEntity findByAreaName(String areaName);
    List<MAreaEntity> findAllByAreaIdIn(List<String> areaId);
    List<MAreaEntity> findByAreaIdIn(Set<String> areaId);
}
