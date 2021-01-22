package com.kahago.kahagoservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MProductSwitcherEntity;
import com.kahago.kahagoservice.entity.TGoodsEntity;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface TGoodsRepo extends JpaRepository<TGoodsEntity, Integer>{
	List<TGoodsEntity> findAll();
	
	@Query("SELECT TG FROM TGoodsEntity TG WHERE (?1 IS NULL OR TG.productSwCode.productSwCode = ?1) AND TG.productSwCode.switcherEntity.switcherCode = ?2 "
			+ "ORDER BY TG.productSwCode.productSwCode ASC")
	List<TGoodsEntity> findAllByswitcherCode(Integer productSwCode,Integer switcherCode);
	
	List<TGoodsEntity> findAllByProductSwCode(MProductSwitcherEntity productSwCode);
	@Query("SELECT TG FROM TGoodsEntity TG WHERE TG.goodsId.goodsId = ?1 AND TG.productSwCode = ?2")
	TGoodsEntity findAllByGoodsIdAndProductSwCode(Long goodsId,MProductSwitcherEntity productSwCode);
}
