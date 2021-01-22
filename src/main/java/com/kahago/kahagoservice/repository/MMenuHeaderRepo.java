package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MMenuParentEntity;

@Repository
public interface MMenuHeaderRepo extends JpaRepository<MMenuParentEntity, Integer>{

}
