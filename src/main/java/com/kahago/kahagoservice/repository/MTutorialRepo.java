package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MTutorialEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Hendro yuwono
 */
public interface MTutorialRepo extends PagingAndSortingRepository<MTutorialEntity, Integer> {

    List<MTutorialEntity> findByJenisTutorialOrderByStepAsc(Integer jenisTutorial);
    Page<MTutorialEntity> findByJenisTutorialOrderByStepAsc(Integer jenisTutorial, Pageable pageable);
    List<MTutorialEntity> findByJenisTutorialAndShowDashboardOrderByStepAsc(Integer jenisTutorial, Integer showDashboard);
    @Query("select t from MTutorialEntity t where t.jenisTutorial = 1 order by t.step asc")
    List<MTutorialEntity> findAllOrderByStepAsc();
    
    MTutorialEntity findBySeqid(Integer seqid);
    Optional<MTutorialEntity> findFirstByOrderBySeqidDesc();
    boolean existsByStepAndJenisTutorial(Integer step, Integer jenisTutorial);
}
