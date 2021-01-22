package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MReasonPickupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Hendro yuwono
 */
public interface MReasonPickupRepository extends JpaRepository<MReasonPickupEntity, Integer> {
    List<MReasonPickupEntity> findByCategory(MReasonPickupEntity.Category category);
}
