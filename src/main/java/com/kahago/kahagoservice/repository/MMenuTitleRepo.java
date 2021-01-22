package com.kahago.kahagoservice.repository;


import com.kahago.kahagoservice.entity.MMenuTitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author BangDolla08
 * @created 14/09/20-September-2020 @at 09.09
 * @project kahago-service
 */
@Repository
public interface MMenuTitleRepo extends JpaRepository<MMenuTitleEntity, Integer> {
}
