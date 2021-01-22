package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MBankDepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author bangd ON 22/11/2019
 * @project com.kahago.kahagoservice.repository
 */
public interface MBankDepositRepo extends JpaRepository<MBankDepositEntity,Integer> {
    @Query("SELECT M FROM MBankDepositEntity M WHERE M.isRobot=true")
    List<MBankDepositEntity> findAllByIsRobot();
    @Query("SELECT M FROM MBankDepositEntity M "
    		+ "WHERE M.isRobot=?1 and M.bankId.bankCode=?2")
    List<MBankDepositEntity> findAllByIsRobotAndBankCode(Boolean isRobot,String bankcode);
    @Query("SELECT M FROM MBankDepositEntity M "
    		+ "WHERE M.bankId.bankCode=?1")
    List<MBankDepositEntity> findByBankId(String bankCode);
    
    List<MBankDepositEntity> findAllByStatusAndDepositType(String status, String depositType);
    
    List<MBankDepositEntity> findAllByStatusAndIsBank(String status, Boolean isBank);
    
}
