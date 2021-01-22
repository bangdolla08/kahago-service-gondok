package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.TPickupOrderRequestDetailEntity;
import com.kahago.kahagoservice.entity.TPickupOrderRequestEntity;
import java.util.List;

import com.kahago.kahagoservice.model.projection.IncomingOfGood;
import com.kahago.kahagoservice.model.projection.ItemPickup;
import com.kahago.kahagoservice.model.projection.PiecesOfItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * @author bangd ON 16/12/2019
 * @project com.kahago.kahagoservice.repository
 */
@Repository
public interface TPickupOrderRequestDetailRepo extends JpaRepository<TPickupOrderRequestDetailEntity,Integer> {
	List<TPickupOrderRequestDetailEntity> findAllByOrderRequestEntity(TPickupOrderRequestEntity orderRequestEntity);
	
	TPickupOrderRequestDetailEntity findByOrderRequestEntityAndQrCode(TPickupOrderRequestEntity orderRequestEntity,String qrCode);
	@Query("SELECT T FROM TPickupOrderRequestDetailEntity T left join T.productSwitcherEntity P " +
            "WHERE " +
            "(?2 IS null OR T.qrcodeExt=?2 OR T.qrCode=?2) " +
            "AND (COALESCE(?1) IS NULL OR T.status IN (?1) ) " +
            "AND (COALESCE(?3) IS NULL OR P.switcherEntity.switcherCode IN (?3) ) "+
            "AND T.orderRequestEntity = ?4 AND (T.bookCode IS NULL OR T.bookCode = ?5) ")
    List<TPickupOrderRequestDetailEntity> findByIdAndStatusOrQrCode(List<Integer> status,
                                                                    String bookingCode,
                                                                    List<Integer> switcherCode,
                                                                    TPickupOrderRequestEntity orderRequestEntity, String bookCode);
	@Query(value="Select T.* FROM t_pickup_order_request_detail T INNER JOIN "
			+ "t_pickup_order_request TP ON TP.pickup_order_id = T.pickup_order_id "
			+ "INNER JOIN t_warehouse_receive_detail TWD ON TWD.qrcode_request = T.qrcode_ext "
			+ "INNER JOIN t_warehouse_receive TW ON TW.id_warehouse_receive = TWD.warehouse_receive_id "
			+ "WHERE T.status=?2 "
			+ "AND (?1 IS NULL OR DATE_FORMAT(TWD.create_at,'%Y-%m-%d')=?1) "
			+ "AND ((?3 IS NULL OR TP.user_id like concat('%',?3,'%')) "
			+ "OR TW.code like concat('%',?3,'%') "
			+ "OR TWD.create_by like concat('%',?3,'%') "
			+ "OR T.qrcode_ext like concat('%',?3,'%') "
			+ "OR T.pickup_order_id like concat('%',?3,'%')) AND (?4 IS NULL OR TW.office_code = ?4) Group By T.qrcode_ext order by TP.order_date ", 
			countQuery = "Select T.* FROM t_pickup_order_request_detail T INNER JOIN "
					+ "t_pickup_order_request TP ON TP.pickup_order_id = T.pickup_order_id "
					+ "INNER JOIN t_warehouse_receive_detail TWD ON TWD.qrcode_request = T.qrcode_ext "
					+ "INNER JOIN t_warehouse_receive TW ON TW.id_warehouse_receive = TWD.warehouse_receive_id "
					+ "WHERE T.status=?2 "
					+ "AND (?1 IS NULL OR DATE_FORMAT(TWD.create_at,'%Y-%m-%d')=?1) "
					+ "AND ((?3 IS NULL OR TP.user_id like concat('%',?3,'%')) "
					+ "OR TW.code like concat('%',?3,'%') "
					+ "OR TWD.create_by like concat('%',?3,'%') "
					+ "OR T.qrcode_ext like concat('%',?3,'%') "
					+ "OR T.pickup_order_id like concat('%',?3,'%')) AND (?4 IS NULL OR TW.office_code = ?4) Group By T.qrcode_ext", nativeQuery = true)
    Page<TPickupOrderRequestDetailEntity> findByOrderDateAndPickupTimeEntity(LocalDate orderDate, Integer status, String stringSearch,String officeCode, Pageable pageable);
//    @Query(value = "SELECT TP FROM  TPickupOrderRequestDetailEntity TP WHERE (?1 IS NULL OR TP.orderRequestEntity.pickupEntity.pickupId.pickupDate=?1) AND (?2 IS NULL OR TP.orderRequestEntity.pickupTimeEntity.idPickupTime=?2) AND TP.status=?3 AND (?4 IS NULL OR TP.orderRequestEntity.userEntity.userId like concat('%',?4,'%') OR TP.orderRequestEntity.pickupEntity.pickupId.courierId.userId like concat('%',?4,'%') OR TP.qrCode like concat('%',?4,'%') OR TP.orderRequestEntity.pickupOrderId like concat('%',?4,'%') )")
//    Page<TPickupOrderRequestDetailEntity> findByOrderDateAndPickupTimeEntityIdPickupTime(LocalDate orderDate, Integer idPickupTime, Integer status, String stringSearch, Pageable pageable);
    TPickupOrderRequestDetailEntity findByQrCode(String qrcode);
    List<TPickupOrderRequestDetailEntity> findByOrderRequestEntityPickupOrderId(String pickupOrderId);
    Integer countByOrderRequestEntityAndStatus(TPickupOrderRequestEntity pickupOrderRequestEntity,Integer status);
    Integer countByOrderRequestEntity(TPickupOrderRequestEntity pickupOrderRequestEntity);
    
    TPickupOrderRequestDetailEntity findByQrcodeExt(String qrcodeExt);
    boolean existsByQrcodeExt(String qrCode);
    
    List<TPickupOrderRequestDetailEntity> findByQrcodeExtIn(List<String> qrcodeExt);
    
    TPickupOrderRequestDetailEntity findByQrCodeOrQrcodeExt(String qrCode,String qrcodeExt);
    @Query("select tp from TPickupOrderRequestDetailEntity tp right join tp.orderRequestEntity tr where tr.userEntity.userId = :userId and (tp.status in :status or tr.status in :status) order by tr.orderDate desc")
    List<TPickupOrderRequestDetailEntity> findByUserIdAndStatus(String userId,List<Integer> status);
    @Query("select tp from TPickupOrderRequestDetailEntity tp where tp.orderRequestEntity = :orderRequestEntity and (tp.qrCode = :qrCode or tp.qrcodeExt = :qrcodeExt)")
    TPickupOrderRequestDetailEntity findByOrderRequestEntityAndQrCodeOrQrcodeExt(TPickupOrderRequestEntity orderRequestEntity,String qrCode,String qrcodeExt);
    @Query("select tp from TPickupOrderRequestDetailEntity tp where tp.orderRequestEntity.userEntity.userId = :userId and tp.isPay in :isPay and tp.status = :status")
    List<TPickupOrderRequestDetailEntity> findByUserIdAndStatusPay(String userId,List<Integer> isPay,Integer status);
    
    @Query("select count(tp) from TPickupOrderRequestDetailEntity tp where tp.orderRequestEntity = :orderPickupId")
    Integer countTpickupDetailByPickupOrderId(TPickupOrderRequestEntity orderPickupId);
    @Query("select tp from TPickupOrderRequestDetailEntity tp where tp.orderRequestEntity = :orderRequestEntity and tp.status in :status ")
    List<TPickupOrderRequestDetailEntity> findAllByOrderRequestEntityAndStatus(TPickupOrderRequestEntity orderRequestEntity,List<Integer> status);
    @Query("SELECT T FROM TPickupOrderRequestDetailEntity T WHERE T.isPay=?1 AND T.paymentOption=?2 Group By T.idTicket")
    List<TPickupOrderRequestDetailEntity> findAllByStatusPayAndPaymentOption(Integer statusPay,String paymentOption);
    @Query("SELECT T FROM TPickupOrderRequestDetailEntity T WHERE T.noTiket=?1 AND T.noTiket IS NOT NULL")
    List<TPickupOrderRequestDetailEntity> findAllByNoTicket(String noTiket);
    List<TPickupOrderRequestDetailEntity> findAllByIdTicket(String idTicket);
    @Query("select tp from TPickupOrderRequestDetailEntity tp where tp.status = ?3 and (tp.qrCode = ?1 or tp.qrcodeExt = ?2)")
    TPickupOrderRequestDetailEntity findByQrCodeOrQrcodeExtAndStatus(String qrcode,String qrcodeExt,Integer status);
    @Query("select tp from TPickupOrderRequestDetailEntity tp where tp.status = ?1 "
    		+ "and (tp.orderRequestEntity.pickupTimeEntity.timeFrom <= subtime(?3,'00:10:00') "
    		+ "and tp.orderRequestEntity.orderDate = ?2) and (tp.isPay = 3 or tp.isPay is null)")
    List<TPickupOrderRequestDetailEntity> findAllByStatusAndIsPay(Integer status,LocalDate date,LocalTime time);
    @Query("select tp from TPickupOrderRequestDetailEntity tp where tp.status = ?1 "
    		+ "and (?3 between subtime(tp.orderRequestEntity.pickupTimeEntity.timeFrom,'00:10:00') and subtime(tp.orderRequestEntity.pickupTimeEntity.timeFrom,'00:07:00') "
    		+ "and tp.orderRequestEntity.orderDate = ?2) and tp.isPay = 3 ")
    List<TPickupOrderRequestDetailEntity> findAllByStatusPending(Integer status,LocalDate date,LocalTime time);
    
    List<TPickupOrderRequestDetailEntity> findAllByOrderRequestEntityAndStatus(TPickupOrderRequestEntity orderRequestEntity,Integer status);
    
    Integer countByOrderRequestEntityStatusIn(List<Integer> status);
    @Query(value="select count(*) from t_pickup_order_request_detail a " + 
    		"inner join t_pickup_order_request b on b.pickup_order_id = a.pickup_order_id " + 
    		"inner join t_warehouse_receive_detail c on c.qrcode_request = a.qrcode_ext " + 
    		"inner join t_warehouse_receive d on d.id_warehouse_receive = c.warehouse_receive_id " + 
    		"where a.status in (?1)",nativeQuery=true)
    Integer countTotalBookingUnComplete(List<Integer> status);

    Boolean existsByQrCodeOrQrcodeExtAndStatus(String qrcode,String qrcodeExt, Integer status);
    @Query("select tp from TPickupOrderRequestDetailEntity tp where tp.status in :status "
    		+ "and (:userId is null or tp.orderRequestEntity.userEntity.userId = :userId) "
    		+ "and (:idSearch is null or lower(tp.orderRequestEntity.userEntity.accountNo) like %:idSearch% ) "
    		+ "and (:cari is null or (lower(tp.orderRequestEntity.userEntity.name) like %:cari% or lower(tp.orderRequestEntity.pickupOrderId) like %:cari%)) "
    		+ "and (tp.isPay in (0,1) or tp.isPay is null) "
    		+ "order by tp.orderRequestEntity.orderDate desc")
    Page<TPickupOrderRequestDetailEntity> findAllByUserIdAndStatus(List<Integer> status, String userId, String cari, String idSearch,Pageable pageable);

    Boolean existsByQrCodeAndStatus(String qrcode, Integer status);

    List<TPickupOrderRequestDetailEntity> findAllByOrderRequestEntityAndIsPayIn(TPickupOrderRequestEntity OrderRequestEntity , List<Integer> isPay);

    Integer countAllByOrderRequestEntityUserEntityUserId(String userId);
    @Query("SELECT COUNT(T) FROM TPickupOrderRequestDetailEntity T "
    		+ "WHERE T.orderRequestEntity.userEntity.userId=?1 "
    		+ "AND T.status = ?2 "
    		+ "AND T.orderRequestEntity.pickupAddressEntity IS NULL")
    Integer countAllByOrderRequestEntityUserEntityUserIdAndStatusEqual(String userId,Integer status);
    @Query("SELECT T.bookCode FROM TPickupOrderRequestDetailEntity T WHERE T.orderRequestEntity=?1")
    List<String> findByOrderRequest(TPickupOrderRequestEntity tpickupOrderRequest);
    @Query("SELECT T.bookCode FROM TPickupOrderRequestDetailEntity T WHERE T.orderRequestEntity IN (?1) AND T.orderRequestEntity.status=?2")
    List<String> countBook(List<TPickupOrderRequestEntity> requestEntityList,Integer status);
    @Query("SELECT COUNT(T.seq) FROM TPickupOrderRequestDetailEntity T WHERE T.status = ?1 AND T.createDate BETWEEN ?1 AND ?2 ")
    Integer countByStatusAndCreateDate(Integer status,LocalDateTime start,LocalDateTime end);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.ItemPickup(por.pickupOrderId, pd.idPickupDetail, cast(null as string), cast(null as string), " +
            "cast(null as string), por.qty, cast(null as long), pd.status, cast(null as string), cast(false as boolean), pord.seq, cast(null as string) ) " +
            "FROM TPickupOrderRequestEntity por LEFT JOIN " +
            "por.pickupEntity pd LEFT JOIN " +
            "pd.pickupId p LEFT JOIN " +
            "pd.pickupAddrId pa LEFT JOIN " +
            "por.pickupOrderRequestDetails pord " +
            "WHERE p.idPickup = ?1 AND pa.pickupAddrId = ?2 GROUP BY por.pickupOrderId")
    List<ItemPickup> findReqPickupByPickIdAndPickAddrId(Integer pickupId, Integer pickupAddressId);
    @Query("SELECT new com.kahago.kahagoservice.model.projection.PiecesOfItem(pord.seq, pord.namaPenerima, ad.kecamatan, k.name, " +
            "pord.qty, pord.weight, pord.qrcodeExt, pord.pathPic, s.img, ps.displayName, pord.status) FROM " +
            "TPickupOrderRequestDetailEntity pord LEFT JOIN " +
            "pord.orderRequestEntity por LEFT JOIN " +
            "pord.productSwitcherEntity ps LEFT JOIN " +
            "ps.switcherEntity s LEFT JOIN " +
            "pord.areaId ad LEFT JOIN " +
            "ad.kotaEntity k " +
            "WHERE por.pickupOrderId = ?1")
    List<PiecesOfItem> findByPickupOrderId(String pickupOrderId);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.IncomingOfGood(pord.qrCode, cust.name, courier.name, cast(null as string), " +
            "cast(null as string), cast(null as string), cast(null as string), cast(pord.weight as string), prodswitcher.displayName, switcher.displayName, pord.status, cast(false as boolean), por.pickupOrderId) " +
            "FROM TPickupDetailEntity tpd LEFT JOIN " +
            "tpd.pickupOrderRequestEntity por LEFT JOIN " +
            "por.pickupOrderRequestDetails pord LEFT JOIN " +
            "por.userEntity cust LEFT JOIN " +
            "tpd.pickupId pick LEFT JOIN " +
            "pick.courierId courier LEFT JOIN " +
            "pord.productSwitcherEntity prodswitcher LEFT JOIN " +
            "prodswitcher.switcherEntity switcher LEFT JOIN " +
            "por.pickupAddressEntity tpa LEFT JOIN " +
            "tpa.postalCode mpc LEFT JOIN " +
            "mpc.kecamatanEntity mad LEFT JOIN " +
            "mad.kotaEntity mak " +
            "WHERE pord.status = ?1 AND tpd.status = 1 AND mak.areaKotaId IN ?2")
    List<IncomingOfGood> findByStatusAndCityIds(int status, Set<Integer> cityIds);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.IncomingOfGood(pord.qrCode, cust.name, courier.name, cast(null as string), " +
            "cast(null as string), cast(null as string), cast(null as string), cast(pord.weight as string), prodswitcher.displayName, switcher.displayName, pord.status, cast(false as boolean), por.pickupOrderId) " +
            "FROM TPickupDetailEntity tpd LEFT JOIN " +
            "tpd.pickupOrderRequestEntity por LEFT JOIN " +
            "por.pickupOrderRequestDetails pord LEFT JOIN " +
            "por.userEntity cust LEFT JOIN " +
            "tpd.pickupId pick LEFT JOIN " +
            "pick.courierId courier LEFT JOIN " +
            "pord.productSwitcherEntity prodswitcher LEFT JOIN " +
            "prodswitcher.switcherEntity switcher LEFT JOIN " +
            "por.pickupAddressEntity tpa LEFT JOIN " +
            "tpa.postalCode mpc LEFT JOIN " +
            "mpc.kecamatanEntity mad LEFT JOIN " +
            "mad.kotaEntity mak " +
            "WHERE pord.status = ?1 AND tpd.status = 1 AND mak.areaKotaId IN ?2 AND pord.qrCode = ?3")
    IncomingOfGood findByStatusAndCityIdsAndQrCode(int status, Set<Integer> cityIds, String qrCode);
}
