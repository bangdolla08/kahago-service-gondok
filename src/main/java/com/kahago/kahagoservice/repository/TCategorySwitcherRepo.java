package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.TCategorySwitcherEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Hendro yuwono
 */
public interface TCategorySwitcherRepo extends JpaRepository<TCategorySwitcherEntity, Integer> {
	TCategorySwitcherEntity findByIdUserCategoryAndSwitcherCode(Integer idUserCategory,MSwitcherEntity switcherCode);
	
	TCategorySwitcherEntity findByIdUserCategoryAndSwitcherCodeSwitcherCode(Integer idUserCategory,Integer switcherCode);
	
	List<TCategorySwitcherEntity> findAllByIdUserCategory(Integer idUserCategory);
}
