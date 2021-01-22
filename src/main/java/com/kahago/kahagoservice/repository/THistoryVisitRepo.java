package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.THistoryVisitEntity;

import java.util.List;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface THistoryVisitRepo extends JpaRepository<THistoryVisitEntity, Integer> {
    THistoryVisitEntity findByUserId(String userId);
    THistoryVisitEntity findByParamAndUrlAndActionAndUserIdNotAndFlag(String param, String url, String action, String userId,Integer flag);
}
