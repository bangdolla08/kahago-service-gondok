package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.TLeadTimeHistoryEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface TLeadTimeHistoryRepo extends JpaRepository<TLeadTimeHistoryEntity, Integer>{

}
