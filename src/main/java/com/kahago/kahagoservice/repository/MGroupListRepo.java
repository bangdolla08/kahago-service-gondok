package com.kahago.kahagoservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MGroupListEntity;

@Repository
public interface MGroupListRepo extends JpaRepository<MGroupListEntity, Integer>{
	@Query("SELECT new com.kahago.kahagoservice.entity.MGroupListEntity(G) " +
			"FROM MGroupListEntity G WHERE G.userCategory=?1 AND G.menuId.bonew=1 AND " +
			"(G.isDelete=true OR G.isRead=true OR G.isWrite=true )")
	List<MGroupListEntity> findAllByUserCategory(Integer userCategory);
	MGroupListEntity findByMenuIdMenuIdAndUserCategory(Integer menuId,Integer userCategory);
}
