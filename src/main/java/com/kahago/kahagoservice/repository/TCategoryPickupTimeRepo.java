package com.kahago.kahagoservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MPickupTimeEntity;
import com.kahago.kahagoservice.entity.MUserCategoryEntity;
import com.kahago.kahagoservice.entity.TCategoryPickupTimeEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface TCategoryPickupTimeRepo extends JpaRepository<TCategoryPickupTimeEntity, Integer>{
	List<TCategoryPickupTimeEntity> findByIdUserCategoryAndActived(MUserCategoryEntity idUserCategory,Boolean actived);
	
	List<TCategoryPickupTimeEntity> findByIdUserCategory(MUserCategoryEntity idUserCategory);
	
	TCategoryPickupTimeEntity findByIdUserCategoryAndIdPickupTime(MUserCategoryEntity idUserCategory,MPickupTimeEntity idPickupTime);
}
