package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.TOutgoingListDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bangd ON 29/11/2019
 * @project com.kahago.kahagoservice.repository
 */
@Repository
public interface TOutgoingListDetailRepo extends JpaRepository<TOutgoingListDetailEntity,Integer> {
    List<TOutgoingListDetailEntity> findAllByOutgoingListId(Integer listId);
    @Query("SELECT SUM(T.bookingCode.grossWeight) FROM TOutgoingListDetailEntity T WHERE T.outgoingListId=?1")
    Integer sumGrossWeight(Integer idOutgoing);
    @Query("SELECT SUM(T.bookingCode.volume) FROM TOutgoingListDetailEntity T WHERE T.outgoingListId=?1")
    Integer sumVolume(Integer idOutgoing);
    @Query("SELECT COUNT (T.bookingCode) FROM TOutgoingListDetailEntity T WHERE T.outgoingListId=?1")
    Integer countItemBook(Integer idOutgoing);
    TOutgoingListDetailEntity findByBookingCodeBookingCodeAndAndOutgoingListId(String bookingCode,Integer listId);
    List<TOutgoingListDetailEntity> findAllByTisCable(String tisCable);




}
