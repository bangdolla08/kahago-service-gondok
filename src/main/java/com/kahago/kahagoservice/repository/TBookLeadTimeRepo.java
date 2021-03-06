package com.kahago.kahagoservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.TBookLeadTime;

@Repository
public interface TBookLeadTimeRepo extends JpaRepository<TBookLeadTime, String>{

}
