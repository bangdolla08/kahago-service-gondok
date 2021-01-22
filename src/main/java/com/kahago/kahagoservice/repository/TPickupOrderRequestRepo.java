package com.kahago.kahagoservice.repository;

import com.beust.jcommander.internal.Lists;
import com.kahago.kahagoservice.entity.MPickupTimeEntity;
import com.kahago.kahagoservice.entity.MUserEntity;
import com.kahago.kahagoservice.entity.TPickupAddressEntity;
import com.kahago.kahagoservice.entity.TPickupEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author bangd ON 16/12/2019
 * @project com.kahago.kahagoservice.repository
 */
@Repository
public interface TPickupOrderRequestRepo extends JpaRepository<TPickupOrderRequestEntity,String> {
    TPickupOrderRequestEntity findByPickupOrderId(String pickupOrderId);
    @Query(value = "SELECT pickup_order_id from t_pickup_order_request where pickup_order_id like concat('%',:code,'%') order by pickup_order_id desc limit 1",nativeQuery = true)
    String getCodeCount(@Param("code") String code);
    @Query(value = "SELECT TP FROM  TPickupOrderRequestEntity TP WHERE (?1 IS NULL OR TP.orderDate=?1) AND (?2 IS NULL OR TP.pickupTimeEntity.idPickupTime=?2) AND TP.status=?3 "
    		+ "AND TP.pickupAddressEntity.postalCode.kecamatanEntity.kotaEntity.areaKotaId = ?4 "
    		+ "AND TP.orderDate = ?5")
    List<TPickupOrderRequestEntity> findByOrderDateAndPickupTimeEntityIdPickupTime(LocalDate orderDate,Integer idPickupTime,Integer status, Integer areaKotaId, LocalDate pickupDate);
    @Query(value = "SELECT TP FROM  TPickupOrderRequestEntity TP WHERE (?1 IS NULL OR TP.orderDate=?1) AND (?2 IS NULL OR TP.pickupTimeEntity.idPickupTime=?2) AND TP.status=?3 AND (?4 IS NULL OR TP.userEntity.userId=?4)")
    Page<TPickupOrderRequestEntity> findByOrderDateAndPickupTimeEntityIdPickupTime(LocalDate orderDate, Integer idPickupTime, Integer status, String userId, Pageable pageable);
    
    Page<TPickupOrderRequestEntity> findAllByUserEntityOrderByPickupOrderId(MUserEntity userId,Pageable pageable);
    
    @Query("SELECT P.orderRequestEntity FROM TPickupOrderRequestDetailEntity P JOIN P.orderRequestEntity PO "
    		+ "WHERE (?1 IS NULL OR PO.userEntity.userId like %?1%) AND (?2 IS NULL OR PO.orderDate=?2) AND (COALESCE(?3) IS NULL OR PO.status IN (?3)) "
    		+ "AND (?4 IS NULL OR PO.pickupOrderId like %?4% OR P.qrcodeExt like %?4% OR P.bookCode like %?4% ) "
    		+ "AND (?5 IS NULL OR PO.pickupAddressEntity.address like %?5% OR PO.createBy like %?5%) AND PO.status NOT IN (6) "
    		+ "GROUP BY P.orderRequestEntity "
    		+ "ORDER BY PO.orderDate DESC")
    Page<TPickupOrderRequestEntity> findAllByOrderByOrderDateDesc(String userid,String orderDate,List<Integer> status,String qrcode,String filter,Pageable pageable);
    @Query("SELECT PO FROM  TPickupOrderRequestEntity PO "
    		+ "WHERE (?1 IS NULL OR PO.userEntity.userId like %?1%) AND (?2 IS NULL OR PO.orderDate=?2) AND (COALESCE(?3) IS NULL OR PO.status IN (?3)) "
    		+ "AND (?4 IS NULL OR PO.pickupAddressEntity.address like %?4% OR PO.createBy like %?4% ) AND PO.status NOT IN (6) "
    		+ "ORDER BY PO.orderDate DESC")
    Page<TPickupOrderRequestEntity> findAllByOrderIdByOrderDateDesc(String userid,String orderDate,List<Integer> status,String filter,Pageable pageable);
    
    @Query("select tr from TPickupOrderRequestEntity tr where tr.userEntity.userId = :userId and tr.status in :status")
    List<TPickupOrderRequestEntity> findAllByUserIdAndStatus(String userId,List<Integer> status);
    
    TPickupOrderRequestEntity findByUserEntityAndPickupOrderId(MUserEntity userEntity,String pickupOrderId);
    
    TPickupOrderRequestEntity findTopByOrderByPickupOrderIdDesc();
    @Query("select count(TP.pickupOrderId) from TPickupOrderRequestEntity TP where TP.status = ?1 and TP.createDate between ?2 and ?3")
    Integer countByUserAndStatusAndCreateDate(Integer status, LocalDateTime startdate,LocalDateTime endDate);
    @Query("SELECT T FROM TPickupOrderRequestEntity T WHERE T.orderDate=?1 AND T.status=?2 " +
            "AND SUBTIME(STR_TO_DATE(CONCAT(T.orderDate,' ',T.pickupTimeEntity.timeTo),'%Y-%m-%d %H:%i:%s'),'-00:30:00') >= CURRENT_TIMESTAMP " +
            " GROUP BY T.pickupTimeEntity ORDER BY T.pickupTimeEntity")
    List<TPickupOrderRequestEntity> findAllByOrderDateAndStatus(LocalDate orderDate,Integer status);
    
    Integer countByStatusIn(List<Integer> status);

    @Query("select TP from TPickupOrderRequestEntity TP where TP.status = ?2 and (?1 is null or TP.userEntity.userId = ?1) "
    		+ "and (?3 is null or TP.pickupTimeEntity.idPickupTime = ?3) "
    		+ "and (?4 is null or TP.pickupAddressEntity.postalCode.kecamatanEntity.areaDetailId = ?4) "
    		+ "and (?5 is null or TP.pickupAddressEntity.postalCode.kecamatanEntity.kotaEntity.areaKotaId = ?5) "
    		+ "and (?6 is null or TP.orderDate = ?6) "
    		+ "order by TP.userEntity.userId")
    List<TPickupOrderRequestEntity> findAllByUserIdAndPickupAddress(String userId,Integer status,Integer timePickupId,Integer areaDetailId,Integer areaKotaId,LocalDate pickupDate);

    List<TPickupOrderRequestEntity> findByPickupAddressEntityPickupAddrIdAndStatus(Integer pickupAddress,Integer status);
    
    List<TPickupOrderRequestEntity> findByPickupAddressEntityInAndStatus(List<TPickupAddressEntity> pickupAddress,Integer status);
//    @Query("select TP.pickupAddressEntity from TPickupOrderRequestEntity TP where TP.status = ?2 and (?1 is null or TP.userEntity.userId = ?1) "
//            + "and (?3 is null or TP.pickupTimeEntity.idPickupTime = ?3) "
//            + "and (?4 is null or TP.pickupAddressEntity.postalCode.kecamatanEntity.areaDetailId = ?4) "
//            + "and (?5 is null or TP.pickupAddressEntity.postalCode.kecamatanEntity.kotaEntity.areaKotaId = ?5) "
//            + "and (?6 is null or TP.orderDate = ?6) "
//            + "GROUP BY TP.pickupAddressEntity order by TP.userEntity.userId")
    @Query(value="SELECT tpa.pickup_addr_id FROM t_pickup_order_request  tpor " +
            "INNER JOIN m_pickup_time  mpt ON tpor.pickup_time_id = mpt.id_pickup_time " +
            "INNER JOIN t_pickup_address  tpa ON tpor.pickup_address_id = tpa.pickup_addr_id " +
            "INNER JOIN m_postal_code  mpc ON tpa.id_postal_code = mpc.id_postal_code " +
            "INNER JOIN m_area_detail  mad ON mpc.area_detail_id = mad.area_detail_id " +
            "INNER JOIN m_area_kota  mak ON mad.area_kota_id = mak.area_kota_id " +
            "WHERE tpor.status=?2 " +
            "AND (?1 IS NULL OR tpor.user_id=?1) " +
            "AND (?3 IS NULL OR tpor.pickup_time_id=?3) " +
            "AND (?4 IS NULL OR mad.area_detail_id=?4) " +
            "AND (?5 IS NULL OR mak.area_kota_id=?5) " +
            "AND (?6 IS NULL OR tpor.order_date=?6) " +
            "GROUP BY tpor.pickup_address_id",
            nativeQuery=true)
    List<Integer> findTPickupAddressEntityAllByUserIdAndPickupAddress(String userId, Integer status, Integer timePickupId, Integer areaDetailId, Integer areaKotaId, LocalDate pickupDate);

    @Query("select tp from TPickupOrderRequestEntity tp join tp.pickupOrderRequestDetails tpd where tp.status in :status " + 
    		"and (:userId is null or tp.userEntity.userId = :userId) " + 
    		"and (:idSearch is null or lower(tp.userEntity.accountNo) like %:idSearch% ) " + 
    		"and (:cari is null or (lower(tp.userEntity.name) like %:cari% or lower(tp.pickupOrderId) like %:cari%)) and tpd.isPay in (0,1) " + 
    		"order by tp.orderDate desc")
    Page<TPickupOrderRequestEntity> findByStatusAndUserId(List<Integer> status, String userId, String cari, String idSearch,Pageable pageable);
    @Query("select tp from TPickupOrderRequestEntity tp left join tp.pickupOrderRequestDetails tpd where tp.status in :status " + 
    		"and (:userId is null or tp.userEntity.userId = :userId) " + 
    		"and (:idSearch is null or lower(tp.userEntity.accountNo) like %:idSearch% ) " + 
    		"and (:cari is null or (lower(tp.userEntity.name) like %:cari% or lower(tp.pickupOrderId) like %:cari%)) and tpd.isPay in (0,1) " + 
    		"order by tp.orderDate desc")
    List<TPickupOrderRequestEntity> findByStatusAndUserIdNoPage(List<Integer> status, String userId, String cari, String idSearch);
    
    Optional<TPickupOrderRequestEntity> findByPickupOrderIdAndStatusIn(String pickupOrderId,List<Integer> status);
    List<TPickupOrderRequestEntity> findByPickupOrderIdIn(List<String> ids);
    @Query("SELECT T FROM TPickupOrderRequestEntity T "
    		+ "WHERE T.status IN (?1) AND T.pickupAddressEntity.pickupAddrId=?2 and T.pickupEntity.pickupId=?3")
    List<TPickupOrderRequestEntity> findByStatusAndPickupAddressEntityPickupAddrIdAndPickupEntityPickupId(List<Integer> status,Integer pickupAddrId,TPickupEntity pickupId);
    
    @Query("SELECT T FROM TPickupOrderRequestEntity T "
    		+ "WHERE T.status=?1 AND (?2 IS NULL OR T.pickupAddressEntity.pickupAddrId=?2) "
    		+ "AND (?3 IS NULL OR T.userEntity.userId=?3) "
    		+ "AND (?4 IS NULL OR T.pickupTimeEntity.idPickupTime=?4) "
    		+ "AND (?5 IS NULL OR T.orderDate=?5)")
    List<TPickupOrderRequestEntity> findByStatusAndPickupAddressEntityPickupAddrIdAndUserEntityUserIdAndPickupTimeEntityIdPickupTimeAndOrderDate(Integer status,Integer pickupAddrId,String userId,Integer idPickupTime,LocalDate orderDate);
    
    TPickupOrderRequestEntity findByUserEntityAndPickupTimeEntityAndStatusAndOrderDateAndPickupAddressEntity(MUserEntity userEntity, MPickupTimeEntity pickupTimeEnitity, Integer status, LocalDate orderDate, TPickupAddressEntity pickupAddressEntity);
}
