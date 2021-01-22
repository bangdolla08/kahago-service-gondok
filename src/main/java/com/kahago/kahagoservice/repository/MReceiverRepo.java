package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MReceiverEntity;

import java.util.List;

@Repository
public interface MReceiverRepo extends JpaRepository<MReceiverEntity, Integer> {
    @Query("SELECT T FROM MReceiverEntity T where T.userId.userId=?1 AND (?2=null OR T.idPostalCode.kecamatanEntity.areaDetailId=?2) AND T.status=1")
    List<MReceiverEntity> findAllByUserIdAndAreaId(String userId,Integer areaId);
}
