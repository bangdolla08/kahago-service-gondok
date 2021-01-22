package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MUserCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MUserCategoryRepo extends PagingAndSortingRepository<MUserCategoryEntity, Integer> {

    List<MUserCategoryEntity> findByseqid(Integer seqId);
    List<MUserCategoryEntity> findAll();
    MUserCategoryEntity findBySeqid(Integer seqId);
    List<MUserCategoryEntity> findBySeqidIn(List<Integer> idUserCategory);
}
