package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.THistoryBookEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Hendro yuwono
 */
public interface THistoryBookRepo extends PagingAndSortingRepository<THistoryBookEntity, Integer> {
    List<THistoryBookEntity> findByBookingCode(String bookingCode);
}
