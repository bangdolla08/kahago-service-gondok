package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MCounterEntity;

@Repository
public interface MCounterRepo extends JpaRepository<MCounterEntity, Integer>{

}
