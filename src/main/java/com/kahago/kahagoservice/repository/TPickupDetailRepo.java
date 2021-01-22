

package com.kahago.kahagoservice.repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.model.projection.PickupAddress;
import com.kahago.kahagoservice.model.projection.ItemPickup;
import com.kahago.kahagoservice.model.projection.StatusPickupCourier;

import org.apache.commons.collections4.SetUtils.SetView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Ibnu Wasis
 */
@Repository
public interface TPickupDetailRepo extends JpaRepository<TPickupDetailEntity, Integer> {

    List<TPickupDetailEntity> findByPickupIdStatus(Integer status);

    @Query("select pd from TPickupDetailEntity pd where pd.pickupId.courierId.userId = :courierId and pd.pickupId.status = :status group by pd.pickupId.code")
    List<TPickupDetailEntity> findByCourierIdAndStatus(String courierId, Integer status);

    @Query("select pd from TPickupDetailEntity pd where pd.pickupId.courierId.userId = :courierId and pd.pickupId.status = :status and pd.pickupId.code= :noManifest  group by pd.pickupId.code")
    List<TPickupDetailEntity> findByCourierIdAndStatusAndCode(String courierId, Integer status, String noManifest);

    @Query("select pd from TPickupDetailEntity pd where pd.pickupId.courierId.userId = :courierId and pd.status = :status and pd.pickupId.code= :noManifest  group by pd.bookId")
    List<TPickupDetailEntity> findByCorierAndStatusDetailAndCode(String courierId, Integer status, String noManifest);

    @Query(value = "SELECT new com.kahago.kahagoservice.model.projection.StatusPickupCourier(" +
            "pde.idPickupDetail, pai.pickupAddrId, CASE WHEN (pde.status = 0) THEN 'Not Finish' ELSE 'Finish' END) " +
            "FROM TPickupDetailEntity pde LEFT JOIN " +
            "pde.pickupId pi LEFT JOIN " +
            "pde.pickupAddrId pai " +
            "WHERE pi.code = ?1 " +
            "GROUP BY pai")
    List<StatusPickupCourier> courierStatusPickupPerPickAddr(String manifest);

    @Query("SELECT COUNT(pd) FROM TPickupDetailEntity pd WHERE pd.pickupId.courierId.userId = :courierId AND pd.bookId.status = :statusPayment")
    Integer countByCourierAndStatus(String courierId, Integer statusPayment);

    @Query("SELECT SUM(pd.bookId.jumlahLembar) FROM TPickupDetailEntity pd WHERE pd.pickupId.courierId.userId = :courierId AND pd.bookId.status = :statusPayment")
    Integer sumItemByCourierAndStatus(String courierId, Integer statusPayment);

    @Query("SELECT SUM(pd.bookId.jumlahLembar) FROM TPickupDetailEntity pd WHERE pd.pickupId.idPickup = :pickupId AND pd.bookId.status = :statusPayment")
    Integer sumItemByManifestIdAndStatus(Integer pickupId, Integer statusPayment);

    @Query("SELECT COUNT(pd) FROM TPickupDetailEntity pd WHERE pd.pickupId.idPickup = :pickupId AND pd.bookId.status = :statusPayment")
    Integer countByManifestIdAndStatus(Integer pickupId, Integer statusPayment);

    @Query("SELECT SUM(pd.bookId.jumlahLembar) FROM TPickupDetailEntity pd WHERE pd.pickupId.idPickup = :pickupId")
    Integer sumItemByManifestIdAndStatus(Integer pickupId);

    @Query("SELECT SUM(pd.pickupOrderRequestEntity.qty) FROM TPickupDetailEntity pd WHERE pd.pickupId.idPickup = :pickupId")
    Integer sumQtyPickupRequestByManifestId(Integer pickupId);

    @Query("SELECT COUNT(pd.pickupAddrId) FROM TPickupDetailEntity pd WHERE pd.pickupId.idPickup = :pickupId GROUP By pd.pickupAddrId")
    List<Integer> countQtyPointPickup(Integer pickupId);

    @Query("SELECT SUM(pd.bookId.amount) FROM TPickupDetailEntity pd WHERE pd.pickupId.idPickup = :pickupId")
    Integer sumAmountBookByManifestId(Integer pickupId);

    @Query("SELECT pd.pickupOrderRequestEntity FROM TPickupDetailEntity pd WHERE pd.pickupId.idPickup = :pickupId")
    List<TPickupOrderRequestEntity> getTPickupDetailEntityBy(Integer pickupId);

    @Query("SELECT COUNT(pd.pickupOrderRequestEntity.pickupAddressEntity) FROM TPickupDetailEntity pd WHERE pd.pickupId.idPickup = :pickupId GROUP By pd.pickupOrderRequestEntity.pickupAddressEntity")
    List<Integer> countQtyPointPickupOrder(Integer pickupId);

    TPickupDetailEntity findFirstByBookIdBookingCode(String bookingCode);

    @Query("SELECT T FROM TPickupDetailEntity T WHERE (T.bookId.bookingCode=?1 OR T.bookId.qrcodeExt=?1 OR T.bookId.qrcode=?1) AND T.pickupId.courierId.userId=?2")
    TPickupDetailEntity findFirstByBookIdBookingCodeAndPickupIdCourierIdUserId(String bookingCode, String kuririd);

    TPickupDetailEntity findFirstByPickupOrderRequestEntityPickupOrderId(String PickupOrderId);

    @Query("SELECT pd FROM TPickupDetailEntity pd inner join pd.pickupId p right join pd.bookId tp where (p.officeCode=?1 OR p.officeCode is null OR tp.officeCode = ?1) AND tp.status in (22)")
    List<TPickupDetailEntity> findAllByPickupIdOfficeCodeCourierTask(String officeCode);

    @Query("select count(pd.idPickupDetail) from TPickupDetailEntity pd where pd.pickupId.idPickup = ?1 and status not in (?2)")
    Integer countByPickupIdIdPickupAndStatus(Integer pickupId, List<Integer> statusPickup);

    Integer countByPickupIdIdPickup(Integer pickupId);

    //	@Query("select pd from TPickupDetailEntity pd where pd.pickupId.code = :code")
    List<TPickupDetailEntity> findAllByPickupId(TPickupEntity pickupId);

    TPickupDetailEntity findFirstByPickupIdAndBookIdBookingCode(TPickupEntity pickup, String bookingCode);

    @Query("select pd from TPickupDetailEntity pd inner join pd.bookId tp "
            + "where pd.pickupId.code = :code and tp.userId.userId = :customerId")
    List<TPickupDetailEntity> findByNoManifestAndCustomerId(String code, String customerId);

    @Query("select pd from TPickupDetailEntity pd inner join pd.pickupId tp "
            + "where tp.code =:code ")
    List<TPickupDetailEntity> findAllByPickupCode(String code);

    @Query("select pd from TPickupDetailEntity pd inner join pd.bookId tp "
            + "where pd.pickupId.pickupDate >= :pickupDate AND pd.pickupId.code = :code and tp.userId.userId = :customerId")
    List<TPickupDetailEntity> findByNoManifestAndCustomerIdAndPickupDate(String code, String customerId, LocalDate pickupDate);

    @Query("select pd from TPickupDetailEntity pd inner join pd.pickupOrderRequestEntity po "
            + "where pd.pickupId.code = :code and po.userEntity.userId = :customerId")
    List<TPickupDetailEntity> findByNoManifestAndCustomerIdPickupRequest(String code, String customerId);

    @Query("select pd from TPickupDetailEntity pd inner join pd.bookId tp "
            + "where pd.pickupId.code = :code and tp.userId.userId = :customerId "
            + "and pd.bookId.pickupAddrId.pickupAddrId = :idPickupAddres")
    List<TPickupDetailEntity> findByNoManifestAndCustomerIdAndIdPickupAddres(String code, String customerId, Integer idPickupAddres);

    @Query("select pd from TPickupDetailEntity pd inner join pd.bookId tp "
            + "where pd.pickupId.pickupDate >= :pickupDate AND pd.pickupId.code = :code and tp.userId.userId = :customerId "
            + "and pd.bookId.pickupAddrId.pickupAddrId = :idPickupAddres")
    List<TPickupDetailEntity> findByNoManifestAndCustomerIdAndIdPickupAddresAndPickupDate(String code, String customerId, Integer idPickupAddres, LocalDate pickupDate);

    @Query("select pd from TPickupDetailEntity pd inner join pd.pickupOrderRequestEntity po "
            + "where pd.pickupId.code = :code and  po.userEntity.userId = :customerId "
            + "and pd.pickupOrderRequestEntity.pickupAddressEntity.pickupAddrId = :idPickupAddres")
    List<TPickupDetailEntity> findByNoManifestAndCustomerIdAndIdPickupAddresPickupRequest(String code, String customerId, Integer idPickupAddres);

    @Query("select pd from TPickupDetailEntity pd left join pd.bookId p left join " +
            "pd.pickupOrderRequestEntity pr where (p.bookingCode = :bookingCode or p.qrcode = :qrCode or pr.pickupOrderId = :orderRequestId) "
            + "and pd.pickupId.courierId.userId =:userId")
    TPickupDetailEntity findByQrCodeAndUserId(String qrCode, String bookingCode, String orderRequestId, String userId);

    @Query("SELECT pd from TPickupDetailEntity pd inner join pd.bookId p where " +
            "(p.bookingCode = :qrCode or p.qrcode = :qrCode) and pd.pickupId.courierId.userId =:userId")
    TPickupDetailEntity findByBookByParam(String qrCode, String userId);

    @Query("SELECT pd from TPickupDetailEntity pd inner join pd.pickupOrderRequestEntity pr where " +
            "(pr.pickupOrderId = :orderRequestId) and pd.pickupId.courierId.userId =:userId")
    TPickupDetailEntity findByBookByParamRequest(String orderRequestId, String userId);

    @Query("select pd from TPickupDetailEntity pd "
            + "left join pd.bookId tp "
            + "left join pd.pickupOrderRequestEntity pr "
            + "where (?1 is null or lower(pd.pickupId.code) = ?1) "
            + "and pd.pickupId.courierId.userId= ?2 order by pd.pickupId.code asc")
    List<TPickupDetailEntity> findByNoManifest(String code, String courierId);

    @Query("select pd from TPickupDetailEntity pd left join pd.bookId tp left join pd.bookId.productSwCode where pd.pickupId.courierId.userId=:courierId and pd.pickupId.pickupDate "
            + "between :startDate and :endDate order by pd.pickupId.code asc")
    List<TPickupDetailEntity> findByPickupDate(LocalDate startDate, LocalDate endDate, String courierId);

    @Query("select pd from TPickupDetailEntity pd where (pd.bookId.bookingCode=:bookId or pd.pickupOrderRequestEntity.pickupOrderId = :bookId) And pd.pickupId.code=:code")
    TPickupDetailEntity findByBookIdAndCode(String bookId, String code);

    List<TPickupDetailEntity> findByBookId(TPaymentEntity bookId);

    @Query("select pd from TPickupDetailEntity pd left join pd.bookId p left join pd.pickupOrderRequestEntity pr where (p.bookingCode = :bookingCode or p.qrcode = :qrCode or pr.pickupOrderId = :orderRequestId)")
    TPickupDetailEntity findByQrCode(String qrCode, String bookingCode, String orderRequestId);

    @Query("select pd from TPickupDetailEntity pd where pd.pickupId.courierId.userId = :courierId and pd.bookId.userId.userId = :userId "
            + "and pd.pickupId.pickupDate = :pickupDate order by pd.pickupId.createAt desc")
    List<TPickupDetailEntity> findByCourierIdAndUserId(String courierId, String userId, LocalDate pickupDate);

    @Query("SELECT COUNT(TPD) FROM TPickupDetailEntity TPD WHERE TPD.pickupId.idPickup=:pickupId AND TPD.status IN :pickupStatus")
    Integer countByPickupPickupStatusIN(Integer pickupId, List<Integer> pickupStatus);

    @Query(value = "SELECT count(pd.book_id) as jmlBook, count(pod.seq) as jmlPickup "
            + "FROM t_pickup_detail pd "
            + "left join t_payment p on pd.book_id = p.booking_code "
            + "left join t_pickup_order_request_detail pod on pod.pickup_order_id = pd.pickup_order_id "
            + "where pd.pickup_id =?1 And (pd.status IN (?2)) ", nativeQuery = true)
    Map<String, BigInteger> countByPickupPickupOrderStatusIN(Integer pickupId, List<Integer> pickupStatus);

    @Query("SELECT SUM(TPD.bookId.jumlahLembar) FROM TPickupDetailEntity TPD WHERE TPD.pickupId.idPickup=:pickupId")
    Integer countItemInBooking(Integer pickupId);

    List<TPickupDetailEntity> findByPickupIdIdPickup(Integer idPickup);

    @Query("select td from TPickupDetailEntity td where td.pickupId.courierId.userId = ?2 and td.pickupOrderRequestEntity.pickupOrderId = ?1")
    TPickupDetailEntity findByPickupOrdeIdAndCourierId(String orderId, String userId);

    @Query("select td from TPickupDetailEntity td where td.pickupId.courierId.userId = ?2 and (td.pickupOrderRequestEntity.pickupOrderId = ?1 or td.bookId.bookingCode = ?3)")
    TPickupDetailEntity findByPickupOrdeIdOrbookIdAndCourierId(String orderId, String userId, String bookingCode);

    List<TPickupDetailEntity> findByPickupId(TPickupEntity pickupId);

    TPickupDetailEntity findByPickupOrderRequestEntity(TPickupOrderRequestEntity pickupOrderRequestEntity);

    @Query("SELECT pd FROM TPickupDetailEntity pd join pd.pickupOrderRequestEntity po where (pd.pickupId.officeCode=?1 OR pd.pickupId.officeCode is null) AND po.status = 2")
    List<TPickupDetailEntity> findAllByPickupIdOfficeCodeCourier(String officeCode);

    @Query("SELECT pd FROM TPickupDetailEntity pd join pd.pickupOrderRequestEntity po where (COALESCE(?1) IS NULL OR pd.pickupAddrId.postalCode.kecamatanEntity.kotaEntity IN (?1)) AND po.status = 2")
    List<TPickupDetailEntity> findAllByPickupIdOfficeCodeCourier(List<MAreaKotaEntity> areaKotaId);
//	List<TPickupDetailEntity> findAllByPickupIdIdPickup(Integer idPickup)

    @Query("select pd from TPickupDetailEntity pd left join pd.bookId tp "
            + "left join pd.pickupOrderRequestEntity pr "
            + "where pd.pickupId.courierId.userId= ?1 order by pd.pickupId.code asc")
    List<TPickupDetailEntity> findByNoManifestByName(String courierId);

    @Query("select pd from TPickupDetailEntity pd left join pd.bookId tp "
            + "left join pd.pickupOrderRequestEntity pr "
            + "left join pd.bookId.userId u where (?1 is null or lower(u.name) = ?1) "
            + "and ?2 is null or lower(pd.pickupId.code) = ?2 "
            + "and pd.pickupId.courierId.userId= ?3 order by pd.pickupId.code asc")
    List<TPickupDetailEntity> findByNoManifestByNameAndCode(String name, String code, String courierId);

    @Query("SELECT PD.bookId FROM TPickupDetailEntity  PD WHERE PD.pickupId.idPickup=?1 AND PD.pickupOrderRequestEntity IS null ")
    List<TPaymentEntity> findPaymentEntityByPickupId(Integer idPickup);

    @Query("SELECT PD.pickupAddrId FROM TPickupDetailEntity  PD WHERE PD.pickupId.idPickup=?1 AND PD.pickupOrderRequestEntity IS null GROUP BY PD.pickupAddrId")
    List<TPickupAddressEntity> findPickupAddressByPickupId(Integer idPickup);

    @Query("SELECT PD.bookId FROM TPickupDetailEntity  PD WHERE PD.pickupId.idPickup=?1 AND PD.pickupAddrId.pickupAddrId=?2 AND PD.pickupOrderRequestEntity IS null ")
    List<TPaymentEntity> findPaymentEntityByPickupId(Integer idPickup, Integer pickupAddrId);

    @Query("SELECT PD.pickupOrderRequestEntity FROM TPickupDetailEntity  PD WHERE PD.pickupId.idPickup=?1 AND PD.bookId IS null ")
    List<TPickupOrderRequestEntity> findRequestPickpuPickupByPickupId(Integer idPickup);

    @Query("SELECT PD.pickupAddrId FROM TPickupDetailEntity  PD WHERE PD.pickupId.idPickup=?1 AND PD.bookId IS null GROUP BY PD.pickupAddrId")
    List<TPickupAddressEntity> findRequestPickpuAddressPickupByPickupId(Integer idPickup);

    @Query("SELECT PD.pickupOrderRequestEntity FROM TPickupDetailEntity  PD WHERE PD.pickupId.idPickup=?1 AND PD.pickupAddrId.pickupAddrId=?2 AND PD.bookId IS null ")
    List<TPickupOrderRequestEntity> findRequestPickpuPickupByPickupId(Integer idPickup, Integer pickupAddrId);

    List<TPickupDetailEntity> findByBookIdIn(List<TPaymentEntity> bookingCode);

    @Query("SELECT PD FROM TPickupDetailEntity PD LEFT JOIN PD.pickupId P WHERE P.status IN (:status) "
            + "AND UPPER(PD.bookId.origin)=UPPER(:origin)  AND P.code NOT IN(:noManifest) "
            + "AND SUBTIME(STR_TO_DATE(CONCAT(P.pickupDate,' ',P.timePickupTo),'%Y-%m-%d %H:%i:%s'),'-00:30:00') >= CURRENT_TIMESTAMP")
    List<TPickupDetailEntity> findByPickupIdStatusInAndBookIdOriginIgnoreCase(List<Integer> status, String origin, String noManifest);

    @Query("select count(PD.idPickupDetail) from TPickupDetailEntity PD where PD.bookId.status in (?1) and (?2 is null or PD.bookId.productSwCode.switcherEntity.switcherCode = ?2) and PD.bookId.officeCode in (?3)")
    Integer countByBookIdStatusInAndBookIdProductSwCodeSwitcherEntity(List<Integer> status, Integer switcherCode, List<String> officeCode);

    @Query("select sum(P.jumlahLembar) from TPickupDetailEntity PD inner join PD.bookId P where (?1 IS NULL OR PD.status = ?1) and PD.pickupId.idPickup = ?2")
    Integer countQuantityBookId(Integer status, Integer pickupId);

    @Query("select sum(P.qty) from TPickupDetailEntity PD inner join PD.pickupOrderRequestEntity P where (?1 IS NULL OR PD.status = ?1) and PD.pickupId.idPickup = ?2")
    Integer countQuantityPickupReq(Integer status, Integer pickupId);

    Integer countByPickupId(TPickupEntity pickupId);

    Optional<TPickupDetailEntity> findByBookIdBookingCode(String bookingCode);
    Optional<TPickupDetailEntity> findByPickupOrderRequestEntityPickupOrderId(String pickupOrderId);
    @Query("SELECT pd FROM TPickupDetailEntity pd LEFT JOIN FETCH " +
            "pd.pickupId p LEFT JOIN FETCH " +
            "pd.pickupAddrId pa " +
            "WHERE p.idPickup = ?1 AND pa.pickupAddrId = ?2")
    List<TPickupDetailEntity> findByPickupIdAndPickupAddr(Integer pickupId, Integer pickupAddrId);
    @Query("SELECT pd FROM TPickupDetailEntity pd LEFT JOIN FETCH " +
            "pd.pickupId p LEFT JOIN FETCH " +
            "pd.pickupAddrId pa " +
            "WHERE p.idPickup = ?1 AND pa.pickupAddrId = ?2 AND pd.status <> ?3")
    List<TPickupDetailEntity> findByPickupIdAndPickupAddrAndStatus(Integer pickupId, Integer pickupAddrId, Integer status);

    @Query("SELECT T.bookId FROM TPickupDetailEntity T  "
            + "WHERE T.pickupId=?1 AND T.pickupAddrId.pickupAddrId=?2")
    List<TPaymentEntity> findByPickupAddridAndPickupCode(TPickupEntity pickupId, Integer pickupAddressId);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.PickupAddress(p.idPickup, c.userId, pa.pickupAddrId, p.status) " +
            "FROM TPickupDetailEntity pd LEFT JOIN  " +
            "pd.pickupId p LEFT JOIN " +
            "p.courierId c LEFT JOIN " +
            "pd.pickupAddrId pa " +
            "WHERE p.idPickup = ?1 AND pd.status = 0 GROUP BY pa.pickupAddrId")
    List<PickupAddress> courierStatusInAssignPickup(int pickupId);

    @Query("SELECT T FROM TPickupDetailEntity T join T.bookId TP WHERE T.status IN (0,1) AND TP.status IN (1,2,3,23) "
            + "GROUP BY T.pickupId.pickupDate, T.pickupId.timePickupId,T.pickupId.timePickupFrom,T.pickupId.timePickupTo")
    List<TPickupDetailEntity> getPendingManifestByPickupDate();

    @Query("SELECT COUNT(T.idPickupDetail) FROM TPickupDetailEntity T join T.bookId TP WHERE TP.status IN (1,2,3,23) AND T.pickupId.pickupDate = ?1 AND T.pickupId.timePickupId.idPickupTime = ?2")
    Integer getTotalPendingManifestByPickupDateAndPickupTimeId(LocalDate pickupDate, Integer pickupTimeId);

    @Query("SELECT COUNT(T.idPickupDetail) FROM TPickupDetailEntity T join T.pickupOrderRequestEntity TP WHERE TP.status IN (0,1,2) AND T.pickupId.pickupDate = ?1 AND T.pickupId.timePickupId.idPickupTime = ?2")
    Integer getTotalPendingManifestReqByPickupDateAndPickupTimeId(LocalDate pickupDate, Integer pickupTimeId);

    @Query("SELECT T FROM TPickupDetailEntity T join T.pickupOrderRequestEntity TP WHERE T.status IN (0,1) AND TP.status IN (0,1,2) "
            + "GROUP BY T.pickupId.pickupDate, T.pickupId.timePickupId,T.pickupId.timePickupFrom,T.pickupId.timePickupTo order by T.pickupId.timePickupId")
    List<TPickupDetailEntity> getPendingManifestReq();

    @Query("SELECT new com.kahago.kahagoservice.model.projection.ItemPickup(p.bookingCode, pd.idPickupDetail, p.senderName, p.receiverName, " +
            "p.receiverAddress, p.jumlahLembar, p.grossWeight, pd.status, s.img, cast(false as boolean), cast(null as int), cast(null as string) ) " +
            "FROM TPickupDetailEntity pd LEFT JOIN " +
            "pd.pickupId pi LEFT JOIN " +
            "pd.pickupAddrId pa LEFT JOIN " +
            "pd.bookId p LEFT JOIN " +
            "p.productSwCode ps LEFT JOIN " +
            "ps.switcherEntity s " +
            "WHERE pi.idPickup = ?1 AND pa.pickupAddrId = ?2 AND p.bookingCode IS NOT NULL")
    List<ItemPickup> findBookingByPickIdAndPickAddrId(Integer pickupId, Integer pickupAddressId);

    List<TPickupDetailEntity> findAllByPickupIdAndStatus(TPickupEntity pickupId, Integer status);

    @Query("SELECT T FROM TPickupDetailEntity T WHERE T.pickupId.courierId.userId=?1 AND T.status IN ?2")
    List<TPickupDetailEntity> findByCourier(String userCourier,List<Integer> status);

    @Query("SELECT T FROM TPickupDetailEntity T WHERE T.pickupId.courierId.userId=?1 AND T.status IN ?2")
    List<TPickupDetailEntity> findAllByCourier(String userCourier,List<Integer> status);
}