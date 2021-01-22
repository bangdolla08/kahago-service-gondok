package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.MGoodsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Hendro yuwono
 */
public interface MGoodsRepo extends PagingAndSortingRepository<MGoodsEntity, Long> {

    List<MGoodsEntity> findAll();

    @Query("SELECT T FROM MGoodsEntity T INNER JOIN T.goodsEntityList L WHERE L.productSwCode.switcherEntity.switcherCode=?1")
    List<MGoodsEntity> findAllBySwitcherCode(Long swticherCode);
    @Query("SELECT T FROM MGoodsEntity T INNER JOIN T.goodsEntityList L WHERE L.productSwCode.productSwCode=?1")
    List<MGoodsEntity> findAllByProductCode(Long productCode);
    
    MGoodsEntity findByGoodsId(Long goodsId);
}
