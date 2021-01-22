package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MBankDepositEntity;
import com.kahago.kahagoservice.entity.TDepositEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author bangd ON 01/12/2019
 * @project com.kahago.kahagoservice.repository
 */
@Repository
public interface TDepositRepo extends JpaRepository<TDepositEntity,String>, JpaSpecificationExecutor<TDepositEntity> {
	@Query("SELECT D FROM TDepositEntity D "
			+ "WHERE D.bankDepCode.isRobot=1 AND D.userId.userId=?1 AND D.status=?2 And DATE_FORMAT(D.trxRequest,'%Y-%m-%d')=curdate() ")
    TDepositEntity findFirstByUserIdAndStatus(String userId,Integer status);
	@Query("SELECT D FROM TDepositEntity D "
			+ "WHERE D.bankDepCode.isRobot=1 AND D.userId.userId=?1 AND D.status=?2 "
			+ "And DATE_FORMAT(D.trxRequest,'%Y-%m-%d')=curdate() "
			+ "And D.nominal=?3")
    TDepositEntity findFirstByUserIdAndStatusAndTrxRequestAndNominal(String userId,Integer status,BigDecimal nominal);
    @Query("SELECT T FROM TDepositEntity T WHERE T.tiketNo LIKE ?1 order by T.tiketNo DESC")
    TDepositEntity findFirstByTiketNoLike(String tiket);
    @Query("select t from TDepositEntity t  where t.userId.userId=:userId "
    		+ "and t.status < 4 and date(t.trxRequest) between :start and :end order by t.trxRequest desc")
    List<TDepositEntity> findAllByUserIdAndTrxRequestAndStatus(String userId,Date start,Date end);
    @Query("SELECT T FROM TDepositEntity T "
    		+ "WHERE T.bankDepCode.isRobot=1 AND (T.isConfirmTransfer=2 or T.status=0) "
    		+ "AND DATE_FORMAT(T.trxRequest,'%Y-%m-%d') = curdate() And length(T.description) <= 2")
    List<TDepositEntity> findAllByInsufficientAndIsConfirm();
    
    @Query("SELECT T FROM TDepositEntity T "
    		+ "WHERE T.insufficientFund > 0 AND (T.isConfirmTransfer=2 or T.status=0) "
    		+ "AND DATE_FORMAT(T.trxRequest,'%Y-%m-%d') = curdate() AND timediff(curtime(),date_format(T.trxRequest,'%T')) > '00:10:00'")
    List<TDepositEntity> findAllByInsufficientAndIsConfirmAndTimeDiff();
    
    @Query("SELECT T FROM TDepositEntity T "
    		+ "WHERE T.bankDepCode=?1 AND (T.isConfirmTransfer=2 and T.status=0) "
    		+ "AND DATE_FORMAT(T.trxRequest,'%Y-%m-%d') = curdate() "
    		+ "AND timediff(curtime(),date_format(T.trxRequest,'%T')) > '00:00:10'")
    List<TDepositEntity> findByBankDepCode(MBankDepositEntity bankDepCode);
    
    
    /**
     * @param nominal
     * @param insufficientFund
     * @param isConfirmTransfer
     * @param userid
     * @return
     */
    @Query("SELECT D FROM TDepositEntity D "
    		+ "WHERE D.nominal=?1 And D.insufficientFund=?2 And D.isConfirmTransfer=?3 "
    		+ "And D.userId.userId=?4 And DATE_FORMAT(D.trxServer, '%Y-%m-%d') = CURRENT_DATE")
    List<TDepositEntity> findAllByListDepo(BigDecimal nominal,Integer insufficientFund,Byte isConfirmTransfer,String userid);
    
    List<TDepositEntity> findByTiketNoStartingWithOrderByTiketNoDesc(String tiketNo);
    @Query("SELECT D FROM TDepositEntity D where (?1 is null or D.userId.depositType = ?1) AND (?3 is null or D.status = ?3) "
    		+ "AND (?2 is null or D.bankDepCode.bankId.bankId = ?2) AND (?4 is null or D.tiketNo like %?4% or D.userId.userId like %?4% or D.description like %?4% ) "
    		+ "AND D.bankDepCode.isBank=1 ORDER BY D.trxRequest DESC")
    Page<TDepositEntity> findAllByUserIdDepositeType(String depositeType,String bankId,Integer status,String tiketNo,Pageable pageable);
    
    @Query("SELECT D FROM TDepositEntity D where (?1 is null or D.userId.depositType = ?1) AND (?3 is null or D.status = ?3) "
    		+ "AND (?2 is null or D.bankDepCode.bankId.bankId = ?2) AND (?4 is null or D.tiketNo like %?4% or D.userId.userId like %?4% or D.description like %?4% ) "
    		+ "AND (?5 is null OR (DATE_FORMAT(D.trxRequest,'%Y-%m-%d') between STR_TO_DATE(?5,'%Y-%m-%d') AND STR_TO_DATE(?6,'%Y-%m-%d'))) "
    		+ "AND D.bankDepCode.isBank=1 ORDER BY D.trxRequest DESC")
    Page<TDepositEntity> findAllByUserIdDepositeTypeAndTgl(String depositeType,String bankId,Integer status,String tiketNo,LocalDate startDate,LocalDate endDate,Pageable pageable);
    
    @Query("SELECT D FROM TDepositEntity D WHERE D.description=?1 AND D.description <> ''")
    Optional<TDepositEntity> findByDescription(String description);
    @Modifying
    @Query("UPDATE TDepositEntity D SET D.status=3 WHERE D.description=?1 and D.userId.userId=?2 and D.status=-1")
    void updateByDescription(String desc,String userid);
}
