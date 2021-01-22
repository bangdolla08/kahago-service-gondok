package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MSenderEntity;

import java.util.List;

@Repository
public interface MSenderRepo extends JpaRepository<MSenderEntity, Integer>{
    @Query("SELECT T FROM MSenderEntity T WHERE T.userId.userId=?1 AND T.status=1")
    List<MSenderEntity> findAllByUserIdUserId(String userId);
}
