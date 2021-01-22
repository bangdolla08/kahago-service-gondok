package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.TOfficeEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Hendro yuwono
 */
public interface TOfficeRepo extends PagingAndSortingRepository<TOfficeEntity, Long> {
    List<TOfficeEntity> findByUserIdUserId(String userId);
    
    Optional<TOfficeEntity> findByUserIdAndOfficeCodeOfficeCode(String userid,String officeCode);
}
