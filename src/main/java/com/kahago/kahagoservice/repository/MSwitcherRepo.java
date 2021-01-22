package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MSwitcherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Hendro yuwono
 */
public interface MSwitcherRepo extends JpaRepository<MSwitcherEntity, Integer> {
    @Query("SELECT s FROM MSwitcherEntity s LEFT JOIN FETCH s.vendorProperties vp " +
            "WHERE s.switcherCode = ?1 AND vp.action = ?2")
    Optional<MSwitcherEntity> validateTracking(Integer idVendor, String action);
    
    List<MSwitcherEntity> findAllBySwitcherCodeNotIn(List<Integer> switcherCode);
}
