package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MPickupTimeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Hendro yuwono
 */
public interface MPickupTimeRepo extends PagingAndSortingRepository<MPickupTimeEntity, Integer> {
    List<MPickupTimeEntity> findByIsActive(Integer status);
    MPickupTimeEntity findByIdPickupTime(Integer id);

}
