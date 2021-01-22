package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MOfficeEntity;
import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.TOutgoingListEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author bangd ON 29/11/2019
 * @project com.kahago.kahagoservice.repository
 */
public interface TOutgoingListRepo extends JpaRepository<TOutgoingListEntity,Integer> {
    @Query(value = "SELECT count(t_outgoing_list.code) from t_outgoing_list where t_outgoing_list.create_date = CURRENT_DATE",nativeQuery = true)
    String getCodeCount(@Param("code") String code);

    @Query("SELECT T FROM TOutgoingListEntity T WHERE T.switcherEntity=?1 AND T.status=0 And T.officeCode.officeCode=?2 and T.createDate = ?3")
    TOutgoingListEntity findFirstBySwitcherEntity(MSwitcherEntity switcherEntity,String officeCode,LocalDate date);

    @Query("SELECT T FROM TOutgoingListEntity T WHERE (?1 IS NULL OR T.officeCode=?1) AND (?2 IS NULL OR T.switcherEntity.switcherCode = ?2) AND T.status IN (?3) AND (?4 IS NULL OR T.code LIKE %?4%) ORDER BY T.createDate DESC")
    Page<TOutgoingListEntity>findAllBy(String officeCode, Integer switcherCode, List<Integer> status, String outgoingNumber, Pageable pageable);

    TOutgoingListEntity findByCode(String code);
    
    @Query("SELECT T FROM TOutgoingListEntity T WHERE T.switcherEntity.switcherCode IN ?1 AND T.officeCode.officeCode IN ?2 ORDER BY T.idOutgoingList DESC")
    List<TOutgoingListEntity> findByStatusLate(List<Integer> switcherCode,List<String> officeCode);
    
    TOutgoingListEntity findFirstBySwitcherEntitySwitcherCodeAndOfficeCodeOfficeCodeOrderByIdOutgoingListDesc(Integer switcherCode,String officeCode);
    
    @Query("SELECT CASE WHEN (COUNT(T) >= ?2) Then True ELSE False end From TOutgoingListEntity T "
    		+ "WHERE T.status=1 AND T.officeCode.officeCode=?1 And T.processDate=?3 And T.switcherEntity.switcherCode=?4")
    boolean checkCount(String officeCode,Long count,LocalDate localDate,Integer switcherCode);

}
