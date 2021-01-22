package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.TPaymentEntity;
import com.kahago.kahagoservice.entity.TPaymentHistoryEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author bangd ON 27/11/2019
 * @project com.kahago.kahagoservice.repository
 */
@Repository
public interface TPaymentHistoryRepo extends JpaRepository<TPaymentHistoryEntity,Integer> {
    Integer countAllByBookingCodeBookingCode(String bookingCode);
    @Query("select hp from TPaymentHistoryEntity hp where hp.bookingCode.bookingCode = :bookingCode order by hp.seqid desc")
    List<TPaymentHistoryEntity> findHistoryByBookingCodeLimit(String bookingCode);
    
    @Query("select hp from TPaymentHistoryEntity hp where hp.bookingCode.bookingCode = :bookingCode AND hp.lastStatus NOT IN (:status) order by hp.seqid desc")
    List<TPaymentHistoryEntity> findHistoryByBookingCodeAndLastStatusNotInLimit(String bookingCode,List<Integer> status);
    
    TPaymentHistoryEntity findFirstByBookingCodeAndLastStatusOrderByTrxServerDesc(TPaymentEntity bookingCode,Integer lastStatus);
    
    List<TPaymentHistoryEntity> findFirstByBookingCodeAndStatusAndLastStatusOrderByTrxServerDesc(TPaymentEntity bookingCode,Integer status,Integer lastStatus);
    
    List<TPaymentHistoryEntity> findByBookingCodeBookingCodeOrderByLastUpdateAsc(String bookingCode);
    
    TPaymentHistoryEntity findFirstByBookingCodeAndLastStatusOrderByLastUpdateDesc(TPaymentEntity bookingCode,Integer lastStatus);

}
