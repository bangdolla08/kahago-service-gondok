package com.kahago.kahagoservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MMarketingTreeEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface MMarketingTreeRepo extends JpaRepository<MMarketingTreeEntity, Integer>{
	List<MMarketingTreeEntity> findAllByUserIdParent(String userIdParent);
	
	MMarketingTreeEntity findAllByUserId(String userId);
}
