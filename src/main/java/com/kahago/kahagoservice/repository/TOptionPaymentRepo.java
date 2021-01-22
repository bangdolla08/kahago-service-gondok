package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MOptionPaymentEntity;
import com.kahago.kahagoservice.entity.MUserCategoryEntity;
import com.kahago.kahagoservice.entity.TOptionPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * @author bangd ON 22/11/2019
 * @project com.kahago.kahagoservice.repository
 */
@Repository
public interface TOptionPaymentRepo extends JpaRepository<TOptionPaymentEntity,Integer> {
    @Query("SELECT T FROM TOptionPaymentEntity T " +
            " WHERE T.userCategory.seqid=?1  " +
            " AND ((T.optionPayment.offTimeStart> ?2 AND T.optionPayment.offTimeEnd<?2) OR (T.optionPayment.offTimeEnd=?3 AND T.optionPayment.offTimeStart=?3)) " +
            " AND T.isPayment=true ORDER BY T.optionPayment.seqid")
    List<TOptionPaymentEntity> finOptionPaymentPayment(Integer userCategory,  LocalTime nowTime, LocalTime noTimeLimit);
    @Query("SELECT T FROM TOptionPaymentEntity T " +
            " WHERE T.userCategory.seqid=?1  " +
            " AND ((T.optionPayment.offTimeStart> ?2 AND T.optionPayment.offTimeEnd<?2) OR (T.optionPayment.offTimeEnd=?3 AND T.optionPayment.offTimeStart=?3)) " +
            " AND T.isDeposit=true ORDER BY T.optionPayment.seqid")
    List<TOptionPaymentEntity> finOptionPaymentTopup(Integer userCategory, LocalTime nowTime,LocalTime noTimeLimit);
    @Query("SELECT T FROM TOptionPaymentEntity T "
    		+ "WHERE T.userCategory=?1 and T.optionPayment.code=?2")
    Optional<TOptionPaymentEntity> findByUserCategoryAndOP(MUserCategoryEntity userCategory,String option);
    
    @Query("SELECT T FROM TOptionPaymentEntity T WHERE T.isPayment = true AND T.code = ?1 AND T.userCategory = ?2")
    List<TOptionPaymentEntity> findOptionPaymentPaymentByCode(String code, MUserCategoryEntity userCategory);
    
    @Query("SELECT T FROM TOptionPaymentEntity T WHERE T.isDeposit=true AND T.code = ?1 AND T.userCategory = ?2")
    List<TOptionPaymentEntity> findOptionPaymentTopUpByCode(String code, MUserCategoryEntity userCategory);
    @Modifying
    @Query(value="DELETE FROM t_option_payment  WHERE option_payment_id = ?1",nativeQuery=true)
    void deleteByOptionPayment(Integer mOptionPaymentEntity);
    
    List<TOptionPaymentEntity> findByUserCategory(MUserCategoryEntity userCategory);
    
    TOptionPaymentEntity findByUserCategoryAndOptionPayment(MUserCategoryEntity userCategory,MOptionPaymentEntity optionPayment);
}
