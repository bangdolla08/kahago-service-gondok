package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.TWarehouseReceiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author bangd ON 27/11/2019
 * @project com.kahago.kahagoservice.repository
 */
@Repository
public interface TWarehouseReceiveRepo extends JpaRepository<TWarehouseReceiveEntity,Integer> {

    @Query(value = "SELECT code from t_warehouse_receive where code like concat('%',:code,'%') order by code desc limit 1",nativeQuery = true)
    String getCodeCount(@Param("code") String code);

    TWarehouseReceiveEntity findFirstByPickupIdOrderByIdWarehouseReceiveDesc(Integer pickupId);
    
    
}
