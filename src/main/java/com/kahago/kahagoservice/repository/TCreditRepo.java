package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.TCreditEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Hendro yuwono
 */
public interface TCreditRepo extends PagingAndSortingRepository<TCreditEntity, Integer> {

    @Query("SELECT t FROM TCreditEntity t WHERE t.userId = ?1 AND t.flag = ?2 AND t.nominal >= 0 ORDER BY t.seq ASC")
    List<TCreditEntity> findByUserAndNominalGraterZero(String userId, String flag);
    
    @Query("SELECT t FROM TCreditEntity t WHERE t.userId = ?1 AND t.flag = ?2 AND t.nominal >= 0 And (t.tiketNo is NULL OR LENGTH(t.tiketNo) = 0) ORDER BY t.seq ASC")
    List<TCreditEntity> findByUserAndNominalGraterZeroAndTiketNo(String userId, String flag);
    
    TCreditEntity findByTglAndUserIdAndFlag(LocalDate tgl, String userId, String flag);
    
    TCreditEntity findFirstByUserIdAndTglAndFlagOrderBySeqDesc(String userId,LocalDate tgl, String flag);
    
    List<TCreditEntity> findFirstByUserIdAndTglOrderBySeqDesc(String userId,LocalDate tgl);
//    @Query("SELECT T FROM TCreditEntity T WHERE T.userId=?1 AND T.flag=?2")
    Optional<TCreditEntity> findByUserIdAndFlag(String userId,String flag);
//    @Query("SELECT T FROM TCreditEntity T WHERE T.userId=?1 AND T.flag=?2")
Optional<TCreditEntity> findByUserIdAndFlagAndTglOrderBySeqDesc(String userId,String flag,LocalDate tgl);
    
    List<TCreditEntity> findAllByUserIdAndFlagAndTiketNo(String userId,String flag,String tiketNo);
    
    List<TCreditEntity> findAllByUserIdAndFlag(String userId,String flag);
    

}
