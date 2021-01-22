package com.kahago.kahagoservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MUserEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MUserRepo extends JpaRepository<MUserEntity,String> {

    Long countByRefNum(String refNumber);
    boolean existsByAccountNo(String random);
    @Query("SELECT U FROM TPaymentEntity T inner join T.userId U WHERE T.userId.refNum=:userRef AND T.trxDate between :startDate AND :endDate GROUP BY T.userId")
    Page<MUserEntity> getPaymentByReferance(@Param("userRef") String userRef,@Param("startDate")  LocalDate startDate,@Param("endDate")  LocalDate endDate, Pageable pageable);
    @Query("SELECT M FROM MUserEntity M WHERE M.refNum=:userRef ")
    Page<MUserEntity> getUserReferance(@Param("userRef") String userRef, Pageable pageable);
    @Query("SELECT M FROM MUserEntity M WHERE M.userId=?1 OR M.refNum=?1")
    MUserEntity getMUserEntitiesBy(String userSearch);
    @Query("SELECT M FROM  MUserEntity M WHERE M.courierFlag=1")
    List<MUserEntity> findDriver();
    @Query("SELECT M FROM  MUserEntity M WHERE M.userCategory.accountType in (?1) AND M.statusLayanan=?2")
    List<MUserEntity> findByAccountTypeAndStatusLayanan(List<Integer> accountType,String statusLayanan);
    
    MUserEntity findByPassSession(String passSession);
    
    MUserEntity findByAccountNo(String accountNo);
    
    @Query(value="select count(mu.user_id) from m_user mu " + 
    		"join (select p.user_id as user_id,min(p.trx_date) as trx_date from t_payment p " + 
    		"	group by p.user_id) pu on pu.user_id = mu.user_id " + 
    		"where (?1 is null or MONTH(pu.trx_date) = ?1) " + 
    		"and (?2 is null or YEAR(pu.trx_date) = ?2) " + 
    		"and (COALESCE(?3) is null or pu.trx_date in (?3)) "+
    		"and (?4 is null or mu.ref_num = ?4)",nativeQuery=true)
    Integer getTotalUserByMinTrxDate(Integer month,Integer year,List<LocalDate> lDate,String refNum);
    @Query("SELECT M FROM  MUserEntity M WHERE M.userCategory.seqid IN (?1) ORDER BY M.name")
    List<MUserEntity> findAllByUserCategory(List<Integer> userCategoryId);
    
    @Query("SELECT U FROM TOfficeEntity TO right join TO.userId U WHERE (?1 IS NULL OR U.userId LIKE %?1%) AND (COALESCE(?2) IS NULL OR U.userCategory.seqid IN (?2)) "
    		+ "AND (?3 IS NULL OR U.refNum = ?3) AND (?4 IS NULL OR U.depositType = ?4) "
    		+ "AND (?5 IS NULL OR U.accountNo LIKE %?5% OR U.name LIKE %?5% OR U.hp LIKE %?5%) "
    		+ "AND (?6 IS NULL OR TO.officeCode.officeCode = ?6) "
    		+ "ORDER BY U.registerDate DESC")
    Page<MUserEntity> findAllBySearchString(String userId, List<Integer> userCategory, String reference,Integer userType, String search,String officeCode,Pageable pageable);
}
