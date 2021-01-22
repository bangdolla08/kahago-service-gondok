package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.TPickupAddressEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author bangd ON 18/11/2019
 * @project com.kahago.kahagoservice.repository
 */
@Repository
public interface TPickupAddressRepo extends JpaRepository<TPickupAddressEntity,Integer> {

    @Query(value = "SELECT T FROM TPickupAddressEntity T WHERE T.userId.userId=?1 " +
            "AND (T.postalCode.kecamatanEntity.kotaEntity.areaKotaId=?2 OR ?2 = null) AND T.flag=1")
    List<TPickupAddressEntity> findAllUserIdAndOrigin(String userId, Integer origin);
    @Query(value = "SELECT T FROM TPickupAddressEntity T WHERE T.userId.userId=?1 " +
            "AND (T.postalCode.kecamatanEntity.kotaEntity.areaKotaId=?2 OR ?2 = null) AND T.flag=1")
    Page<TPickupAddressEntity> findAllUserIdAndOrigin(Pageable pageable,String userId, Integer origin);
    @Query(value = "SELECT T FROM TPickupAddressEntity T WHERE T.userId.userId=?1 OR T.userId.refNum=?1")
    List<TPickupAddressEntity> getByIdOrRef(String userSearch);
    
    @Query(value = "SELECT T FROM TPickupAddressEntity T WHERE T.userId.userId=?1 " +
            "AND (T.postalCode.kecamatanEntity.kotaEntity.areaKotaId=?2 OR ?2 = null)")
    List<TPickupAddressEntity> findAllUserIdAndOriginId(String userId, Integer origin);

}
