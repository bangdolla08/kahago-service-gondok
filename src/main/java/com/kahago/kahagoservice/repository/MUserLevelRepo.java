package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MUserLevelEntity;

@Repository
public interface MUserLevelRepo extends JpaRepository<MUserLevelEntity, Integer>{

}
