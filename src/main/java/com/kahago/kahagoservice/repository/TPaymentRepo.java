package com.kahago.kahagoservice.repository;

import com.kahago.kahagoservice.entity.TPickupAddressEntity;
import com.kahago.kahagoservice.entity.TPickupEntity;
import com.kahago.kahagoservice.model.projection.CountingProductProj;
import com.kahago.kahagoservice.model.projection.ItemPickup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kahago.kahagoservice.entity.MAreaKotaEntity;
import com.kahago.kahagoservice.entity.MSwitcherEntity;
import com.kahago.kahagoservice.entity.MUserCategoryEntity;
import com.kahago.kahagoservice.entity.TPaymentEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * @author Riszkhy
 * @Project kahago-service
 * @CreatedDate 18 Nov 2019
 */
@Repository
public interface TPaymentRepo extends JpaRepository<TPaymentEntity, String> {

    TPaymentEntity findByBookingCodeIgnoreCaseContaining(String bookingCode);

    @Query("SELECT T FROM TPaymentEntity T WHERE T.bookingCode IN (?1)")
    List<TPaymentEntity> findByBookingCodeInIgnoreCaseContaining(List<String> bookingCode);

    TPaymentEntity findTopByOrderByBookingCodeDesc();

    @Query("SELECT T FROM TPaymentEntity T WHERE T.qrcodeExt=?1  AND T.status=?2")
    TPaymentEntity findByQrcode(String qrCode, Integer statusPayment);

    @Query("SELECT T FROM TPaymentEntity T WHERE (T.qrcode=?1 OR T.qrcodeExt=?1 OR T.bookingCode=?1) AND T.status=?2")
    TPaymentEntity findByQrcodeOrBookingCode(String parameter, Integer statusPayment);

    @Query("SELECT T FROM TPaymentEntity T WHERE (T.stt=?1 OR T.qrcodeExt=?1 OR T.bookingCode=?1) ")
    TPaymentEntity findByQrcodeOrBookingCode(String parameter);

    List<TPaymentEntity> findByBookingCodeInAndStatusIn(List<String> bookingCode, List<Integer> status);

    @Query("SELECT T FROM TPaymentEntity T WHERE (T.bookingCode=?1 OR T.stt=?1 OR T.qrcode=?1 OR T.qrcodeExt=?1) AND T.status=?2 AND T.officeCode=?3")
    Optional<TPaymentEntity> findByBookingCodeAndStatusAndOfficeCode(String bookingCode, Integer status, String officeCode);

    @Query("SELECT T "
            + "FROM TPaymentEntity T "
            + "LEFT JOIN T.pickupTimeId S "
            + "WHERE T.status in :status  "
            + "and T.userId.userId=:userid "
            + "order by T.statusPay asc,T.trxDate desc")
    List<TPaymentEntity> findPaylater(@Param("status") List<Integer> status, @Param("userid") String userid);

    @Query("SELECT p FROM TPaymentEntity p LEFT JOIN FETCH " +
            "p.productSwCode ps LEFT JOIN FETCH " +
            "ps.switcherEntity s LEFT JOIN FETCH " +
            "s.vendorProperties vp " +
            "WHERE (p.bookingCode = ?1 OR p.stt = ?1) AND vp.action = ?2 "
            + "order by p.trxDate desc")
    Optional<TPaymentEntity> validateTracking(String bookingCode, String action);

    @Query("select t from TPaymentEntity t where t.qrcodeExt = ?1 OR t.bookingCode=?1 OR t.qrcode=?1")
    TPaymentEntity findByQrCodeExtOrQrcodeOrBookingCode(String qrCodeExt);

    @Query("select t from TPaymentEntity t where t.qrcodeExt = ?1")
    TPaymentEntity findFirstByQrCodeExt(String qrCodeExt);

    List<TPaymentEntity> findByIdTicketAndStatusIn(String idTicket, List<Integer> status);

    @Query("select t from TPaymentEntity t  where t.status in (:status) "
            + "and (:userId is null or (lower(t.userId.userId)=:userId or lower(t.senderEmail) = :userId or lower(t.receiverEmail) = :userId)) "
            + "and (:cari is null or (lower(t.senderName) like %:cari% "
            + "or lower(t.receiverName) like %:cari% "
            + "or lower(t.bookingCode) like %:cari% "
            + "or lower(t.stt) like %:cari%)) "
            + "and (:idSearch is null or lower(t.userId.accountNo) like %:idSearch%) "
            + "and (:sender is null or lower(t.userId.userId)= :sender or lower(t.senderEmail) = :sender) "
            + "and (:receiver is null or lower(t.receiverEmail) = :receiver) order by t.trxDate desc,trxTime desc")
    Page<TPaymentEntity> findByStatusAndUserIdReceiverIdSenderId(List<Integer> status, String userId, String cari, String idSearch, String sender, String receiver, Pageable pageable);


    @Query(value = "select p.* from t_payment p left join m_user u on p.user_id = u.user_id where p.status in (:status) and p.user_id=:userId order by p.trx_date desc,p.trx_time desc", nativeQuery = true)
    List<TPaymentEntity> findByStatusAndUserIdReceiverIdSenderIdLimit(List<Integer> status, String userId);

    @Query("SELECT T from TPaymentEntity T WHERE (:search IS NULL OR (T.receiverName  LIKE %:search% OR T.senderName LIKE %:search% OR T.bookingCode LIKE %:search% OR T.stt LIKE %:search%)) AND T.status IN :listApprove "
            + "AND (T.senderEmail=:userEmail OR T.receiverEmail=:userEmail OR T.userId.userId=:userEmail) AND T.trxDate between :startDate AND :endDate order by T.trxDate desc,T.trxTime desc")
    Page<TPaymentEntity> findByHistory(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("search") String search,
            @Param("userEmail") String userEmail,
            @Param("listApprove") List<Integer> listApprove,
            Pageable pageable);


    @Query("SELECT T from TPaymentEntity T WHERE (:search IS NULL OR (T.receiverName  LIKE %:search% OR T.senderName LIKE %:search% OR T.bookingCode LIKE %:search% OR T.stt LIKE %:search%)) "
            + "AND T.status IN :listApprove  AND (T.receiverEmail=:userEmail) AND T.trxDate between :startDate AND :endDate order by T.trxDate desc,T.trxTime desc")
    Page<TPaymentEntity> findByHistorySelfReceive(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("search") String search,
            @Param("userEmail") String userEmail,
            @Param("listApprove") List<Integer> listApprove,
            Pageable pageable);

    @Query("SELECT T from TPaymentEntity T WHERE (:search IS NULL OR (T.receiverName  LIKE %:search% OR T.senderName LIKE %:search% OR T.bookingCode LIKE %:search% OR T.stt LIKE %:search%)) "
            + "AND (T.senderEmail=:userEmail OR T.userId.userId=:userEmail) AND T.status IN :listApprove AND T.trxDate between :startDate AND :endDate order by T.trxDate desc,T.trxTime desc")
    Page<TPaymentEntity> findByHistorySelfSender(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 @Param("search") String search,
                                                 @Param("userEmail") String userEmail,
                                                 @Param("listApprove") List<Integer> listApprove,
                                                 Pageable pageable);

    @Query("select t from TPaymentEntity t where t.userId.userId=:userId and t.status=:status")
    List<TPaymentEntity> findPendingPaymentByUserId(@Param("userId") String userId, @Param("status") Integer status);

    @Query("SELECT COUNT(T) FROM TPaymentEntity T WHERE T.userId.userId=:userId AND T.trxDate between :startDate AND :endDate "
            + "AND T.status IN (1,2,3,4,5,6,7,8,9,11,20,22,23,24,25) GROUP BY T.userId")
    Integer countByUserIdUserId(@Param("userId") String userId,
                                @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(T.amount) FROM TPaymentEntity T WHERE T.userId.userId=:userId AND T.trxDate between :startDate AND :endDate "
            + "AND T.status IN (1,2,3,4,5,6,7,8,9,11,20,22,23,24,25) GROUP BY T.userId")
    BigDecimal countTotalAmountByUserIdUserId(@Param("userId") String userId,
                                              @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("select T from TPaymentEntity T where T.userId.userId = :userId and T.status=:status and T.pickupDate between :startDate and :endDate")
    List<TPaymentEntity> findByUserIdAndStatusAndPickupDate(String userId, Integer status, LocalDate startDate, LocalDate endDate);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.status=?1 And "
//			+ "(T.pickupDate<=?3 OR "
//			+ "(T.pickupTimeId.timeFrom <= subtime(?2,'00:10:00') "
            + "CURRENT_TIMESTAMP >=  SUBTIME(STR_TO_DATE(CONCAT(T.pickupDate,' ',T.pickupTimeId.timeFrom),'%Y-%m-%d %H:%i:%s'),'-00:10:00') "
            + "AND T.pickupDate<=?2 And (T.statusPay=0)")
    List<TPaymentEntity> findAllByStatusAndTimeExp(Integer status, LocalDate date);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.status=?1 And "
            + "SUBTIME(STR_TO_DATE(CONCAT(T.pickupDate,' ',T.pickupTimeId.timeTo),'%Y-%m-%d %H:%i:%s'),'-00:30:00') >= CURRENT_TIMESTAMP "
            + "AND T.pickupDate<=?2 ORDER BY T.pickupTimeId ")
    List<TPaymentEntity> findAllByStatusAndTime(Integer status, LocalDate date);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.status=?1 And (T.pickupDate<=?3 OR "
            + "(T.pickupTimeId.timeFrom <= subtime(?2,'00:10:00') "
            + "AND T.pickupDate<=?3)) And (T.statusPay=0 or T.statusPay is null) "
            + "And T.paymentOption=?4 And T.countPawoon <=?5 ")
    List<TPaymentEntity> findAllByStatusAndTimeAndPaymentOption(Integer status, LocalTime time, LocalDate date, String paymentoption, Integer count);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.status=?1 "
            + "And SUBTIME(STR_TO_DATE(CONCAT(T.pickupDate,' ',T.pickupTimeId.timeFrom),'%Y-%m-%d %H:%i:%s'),'00:10:00') >= CURRENT_TIMESTAMP "
            + "And (T.statusPay=2) "
            + "And T.paymentOption=?2 And T.countPawoon <=?3 Group By T.idTicket")
    List<TPaymentEntity> findAllByStatusAndTimeAndPaymentOptionGopay(Integer status, String paymentoption, Integer count);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.status IN (?1) "
//			+ "And SUBTIME(STR_TO_DATE(CONCAT(T.pickupDate,' ',T.pickupTimeId.timeTo),'%Y-%m-%d %H:%i:%s'),'00:10:00') >= CURRENT_TIMESTAMP "
            + "And (T.statusPay=2) "
            + "And T.paymentOption=?2 And (T.countPawoon <=?3) Group By T.idTicket")
    List<TPaymentEntity> findAllByStatusAndTimeAndPaymentOption(List<Integer> status, String paymentoption, Integer count);

    /**
     * @param status
     * @param isconfirm
     * @param tgl
     * @return
     */
    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.amountUniq > 0 "
            + "And (T.status IN ?1 and (T.isConfirmTransfer=?2 or T.statusPay=?2)) "
            + "And (T.pickupDate >= ?3) GROUP BY T.amountUniq")
    List<TPaymentEntity> findByStatusAndIsConfirmTransfer(List<Integer> status, Byte isconfirm, LocalDate tgl);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.amountUniq > 0 "
            + "And T.status IN :status and T.statusPay IN (0,2) "
            + "And (T.pickupDate >= :tgl) GROUP BY T.amountUniq")
    List<TPaymentEntity> findByStatusAndPickupDate(List<Integer> status, LocalDate tgl);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.amountUniq > 0 "
            + "And T.status IN :status and T.statusPay IN (0,2) "
            + "GROUP BY T.amountUniq")
    List<TPaymentEntity> findByStatusAndGroupByAmountUniq(List<Integer> status);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.insufficientFund > 0 "
            + "And (T.status=?1 And (T.isConfirmTransfer=?2)) "
            + "And (T.pickupDate <= ?3) And TIMEDIFF(curtime(),date_format(T.trxServer,'%T')) > '00:10:00' GROUP BY T.amountUniq")
    List<TPaymentEntity> findByStatusAndIsConfirmTransferAndTimeDiff(Integer status, Byte isconfirm, LocalDate tgl);

    @Query("Select T FROM TPaymentEntity T WHERE T.noTiket=?1 AND T.noTiket is not null")
    List<TPaymentEntity> findByNoTiket(String noTiket);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.discountCode=?1 AND T.userId.userId=?2 "
            + "AND T.status IN (1,2,3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,29,30) ")
    List<TPaymentEntity> findByDiscountCodeAndUserId(String discountCode, String userid);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE  T.userId.userId=?1 "
            + "AND T.status between 1 AND 9 GROUP BY T.noTiket")
    List<TPaymentEntity> findByUserIdGroupbyNoTiket(String userid);

    @Query("SELECT T FROM TPaymentEntity T WHERE (?1 IS NULL OR T.pickupTimeId.idPickupTime=?1) AND T.status=?2 AND (?3 IS NULL OR T.pickupDate=?3) "
            + "AND (?4 IS NULL OR T.pickupAddrId.postalCode.kecamatanEntity.kotaEntity.areaKotaId = ?4) "
            + "AND T.pickupDate = ?5")
    List<TPaymentEntity> findByPickupTimeIdAndStatus(Integer idPickupTime, Integer status, LocalDate trxDate, Integer areaKotaId, LocalDate pickupDate);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.userId.userId=?1 And T.insufficientFund=?2 And T.isConfirmTransfer=?3 "
            + "And T.status=?4 And T.amountUniq=?5")
    List<TPaymentEntity> findByUserIdAndInsuficientFundAndIsConfirmTransferAndStatusAndAmountUniq(String userid, BigDecimal insufficientFund, Byte isConfirmTransfer, Integer status, BigDecimal amountUniq);

    List<TPaymentEntity> findByPaymentOptionAndStatus(String paymentOption, Integer status);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.status=?2 "
            + "And SUBTIME(STR_TO_DATE(CONCAT(T.pickupDate,' ',T.pickupTimeId.timeFrom),'%Y-%m-%d %H:%i:%s'),'00:10:00') >= CURRENT_TIMESTAMP "
            + "And (T.statusPay=2) "
            + "And T.paymentOption=?1 And T.isConfirmTransfer=?3")
    List<TPaymentEntity> findByPaymentOptionAndStatusAndIsConfirmTransfer(String paymentOption, Integer status, Byte isConfirmTransfer);

    @Query("Select T FROM TPaymentEntity T WHERE T.status=?1 And T.userId.userId=?2")
    List<TPaymentEntity> findByStatusAndUserId(Integer status, String userId);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.ItemPickup(p.bookingCode, cast(null as int), p.senderName, p.receiverName, " +
            "p.receiverAddress, p.jumlahLembar, p.grossWeight, cast(null as int), s.img, cast(false as boolean), cast(null as int), concat(cast(p.pickupDate as string), ', ', p.pickupTime)) " +
            "FROM TPaymentEntity p LEFT JOIN " +
            "p.productSwCode ps LEFT JOIN " +
            "ps.switcherEntity s LEFT JOIN " +
            "p.userId u " +
            "WHERE p.status IN ?1 AND u.userId = ?2 ORDER BY p.pickupDate ASC")
    List<ItemPickup> findByStatusAndUserId(List<Integer> listOfStatus, String userId);

    @Query("SELECT p FROM TPaymentEntity p LEFT JOIN FETCH " +
            "p.productSwCode ps LEFT JOIN FETCH " +
            "ps.switcherEntity se LEFT JOIN FETCH " +
            "p.idPostalCode pc LEFT JOIN FETCH " +
            "pc.kecamatanEntity kec LEFT JOIN FETCH " +
            "kec.kotaEntity kota LEFT JOIN FETCH " +
            "kota.provinsiEntity prov " +
            "WHERE p.bookingCode = ?1 AND p.userId.userId = ?2")
    Optional<TPaymentEntity> findByBookingCodeAndUserIdForResiKahago(String bookingCode, String userId);

    @Query("SELECT p FROM TPaymentEntity p LEFT JOIN FETCH " +
            "p.productSwCode ps LEFT JOIN FETCH " +
            "ps.switcherEntity se LEFT JOIN FETCH " +
            "se.vendorProperties vp LEFT JOIN FETCH " +
            "p.idPostalCode pc LEFT JOIN FETCH " +
            "pc.kecamatanEntity kec LEFT JOIN FETCH " +
            "kec.kotaEntity kota LEFT JOIN FETCH " +
            "kota.provinsiEntity prov " +
            "WHERE p.bookingCode = ?1 AND p.userId.userId = ?2 AND vp.action = ?3 AND vp.origin = p.origin")
    Optional<TPaymentEntity> findByBookingCodeAndUserIdForResiTiki(String bookingCode, String userId, String action);

    @Query("select T FROM TPaymentEntity T where (?1 is null or T.userId.userId = ?1) and (?2 is null or T.status = ?2) and (?3 is null or T.bookingCode = ?3) "
            + "AND (?4 IS NULL OR T.userId LIKE %?4% OR T.stt LIKE %?4% OR T.receiverAddress LIKE %?4% OR T.receiverName LIKE %?4% "
            + "OR T.receiverName LIKE %?4% OR T.receiverEmail LIKE %?4% OR T.senderAddress LIKE %?4% OR T.senderTelp LIKE %?4%) "
            + "order by T.trxDate desc,T.trxTime desc")
    Page<TPaymentEntity> findAllByUserIdAndStatusAndBookingCode(String userId, Integer status, String bookingCode, String filter, Pageable pageable);


    @Query("select T FROM TPaymentEntity T where " +
            " (?1 is null or T.userId.userId = ?1) and (COALESCE(?2) IS NULL OR T.status IN ?2) and (?3 is null or T.bookingCode = ?3 or T.stt=?3 or T.qrcode=?3 or T.qrcodeExt=?3) "
            + "AND (?4 IS NULL OR T.userId LIKE CONCAT('%',?4,'%') OR T.stt LIKE CONCAT('%',?4,'%') OR T.receiverAddress LIKE CONCAT('%',?4,'%') "
            + "OR T.receiverTelp LIKE CONCAT('%',?4,'%') "
            + "OR T.receiverName LIKE CONCAT('%',?4,'%') OR T.receiverEmail LIKE CONCAT('%',?4,'%') OR T.senderAddress LIKE CONCAT('%',?4,'%') "
            + "OR T.senderTelp LIKE CONCAT('%',?4,'%')) And (COALESCE(?5) IS NULL OR T.origin IN (?5)) "
            + "AND (COALESCE(?6) IS NULL OR T.productSwCode.switcherEntity.switcherCode IN (?6))  "
            + "AND (?7 IS NULL OR T.origin = ?7) "
            + "order by T.trxDate desc,T.trxTime desc")
    Page<TPaymentEntity> findAllByUserIdAndStatusInAndBookingCode(String userId, List<Integer> status, String bookingCode, String filter, List<String> officeCode, List<Integer> switcherCode, String origin, Pageable pageable);

    @Query("select T FROM TPaymentEntity T where " +
            " (?1 is null or T.userId.userId = ?1) and (COALESCE(?2) IS NULL OR T.status IN ?2) and (?3 is null or T.bookingCode = ?3 or T.stt=?3 or T.qrcode=?3 or T.qrcodeExt=?3) "
            + "AND (?4 IS NULL OR T.userId LIKE CONCAT('%',?4,'%') OR T.stt LIKE CONCAT('%',?4,'%') OR T.receiverAddress LIKE CONCAT('%',?4,'%') "
            + "OR T.receiverTelp LIKE CONCAT('%',?4,'%') "
            + "OR T.receiverName LIKE CONCAT('%',?4,'%') OR T.receiverEmail LIKE CONCAT('%',?4,'%') OR T.senderAddress LIKE CONCAT('%',?4,'%') "
            + "OR T.senderTelp LIKE CONCAT('%',?4,'%')) And (COALESCE(?5) IS NULL OR T.origin IN (?5)) "
            + "AND (COALESCE(?6) IS NULL OR T.productSwCode.switcherEntity.switcherCode IN (?6))  "
            + "AND (?7 IS NULL OR T.origin = ?7) AND T.bookingCode IN (?8) "
            + "order by T.trxDate desc,T.trxTime desc")
    List<TPaymentEntity> findAllByUserIdAndStatusInAndBookingCode(String userId, List<Integer> status, String bookingCode, String filter, List<String> officeCode, List<Integer> switcherCode, String origin, List<String> bookingList);

    @Query("select T FROM TPaymentEntity T where (?1 is null or T.userId.userId = ?1) and (COALESCE(?2) IS NULL OR T.status IN ?2) and (?3 is null or T.bookingCode = ?3 or T.stt=?3 or T.qrcode=?3 or T.qrcodeExt=?3) "
            + "AND (?4 IS NULL OR T.userId LIKE CONCAT('%',?4,'%') OR T.stt LIKE CONCAT('%',?4,'%') OR T.receiverAddress LIKE CONCAT('%',?4,'%') "
            + "OR T.receiverTelp LIKE CONCAT('%',?4,'%') "
            + "OR T.receiverName LIKE CONCAT('%',?4,'%') OR T.receiverEmail LIKE CONCAT('%',?4,'%') OR T.senderAddress LIKE CONCAT('%',?4,'%') "
            + "OR T.senderTelp LIKE CONCAT('%',?4,'%'))  "
            + "AND (?5 IS NULL OR T.productSwCode.switcherEntity.switcherCode=?5)  "
            + "order by T.trxDate desc,T.trxTime desc")
    Page<TPaymentEntity> findAllByUserIdAndStatusInAndBookingCode(String userId, List<Integer> status, String bookingCode, String filter, Integer switcherCode, Pageable pageable);


    @Query("SELECT T FROM TPickupDetailEntity TP " +
            "JOIN TP.bookId T where (?1 is null or T.userId.userId = ?1) and (COALESCE(?2) IS NULL OR T.status IN ?2) and (?3 is null or T.bookingCode = ?3 or T.stt=?3 or T.qrcode=?3 or T.qrcodeExt=?3) "
            + "AND (?4 IS NULL OR T.userId LIKE CONCAT('%',?4,'%') OR T.stt LIKE CONCAT('%',?4,'%') OR T.receiverAddress LIKE CONCAT('%',?4,'%') "
            + "OR T.receiverTelp LIKE CONCAT('%',?4,'%') "
            + "OR T.receiverName LIKE CONCAT('%',?4,'%') OR T.receiverEmail LIKE CONCAT('%',?4,'%') OR T.senderAddress LIKE CONCAT('%',?4,'%') "
            + "OR T.senderTelp LIKE CONCAT('%',?4,'%')) And (COALESCE(?5) IS NULL OR T.officeCode IN (?5)) "
            + "AND (COALESCE(?6) IS NULL OR T.productSwCode.switcherEntity.switcherCode IN (?6))  "
            + "AND SUBTIME(STR_TO_DATE(CONCAT(T.pickupDate,' ',T.pickupTimeId.timeTo),'%Y-%m-%d %H:%i:%s'),'-01:00:00') "
            + "< STR_TO_DATE(CONCAT(?8,' ',?7), '%Y-%m-%d %H:%i:%s') "
            + "order by T.trxDate desc,T.trxTime desc")
    List<TPaymentEntity> findAllByUserIdAndStatusInAndBookingCodeLate(String userId, List<Integer> status, String bookingCode, String filter, List<String> officeCode, List<Integer> switcherCode, LocalTime pickupTime, LocalDate processdate);

    @Query("select T FROM TPaymentEntity T where (?1 is null or T.userId.userId = ?1) and (COALESCE(?2) IS NULL OR T.status IN ?2) and (?3 is null or T.bookingCode = ?3 or T.stt=?3 or T.qrcode=?3 or T.qrcodeExt=?3) "
            + "AND (?4 IS NULL OR T.userId LIKE CONCAT('%',?4,'%') OR T.stt LIKE CONCAT('%',?4,'%') OR T.receiverAddress LIKE CONCAT('%',?4,'%') "
            + "OR T.receiverTelp LIKE CONCAT('%',?4,'%') "
            + "OR T.receiverName LIKE CONCAT('%',?4,'%') OR T.receiverEmail LIKE CONCAT('%',?4,'%') OR T.senderAddress LIKE CONCAT('%',?4,'%') "
            + "OR T.senderTelp LIKE CONCAT('%',?4,'%'))  "
            + "AND (COALESCE(?5) IS NULL OR T.productSwCode.switcherEntity.switcherCode IN ?5) "
            + "AND (COALESCE(?6) IS NULL OR T.pickupAddrId.postalCode.kecamatanEntity.kotaEntity IN (?6)) "
            + "order by T.trxDate desc,T.trxTime desc")
    List<TPaymentEntity> findAllByUserIdAndStatusInAndBookingCode(String userId, List<Integer> status, String bookingCode, String filter, List<Integer> switcherCode, List<MAreaKotaEntity> areaKotaId);

    @Query("select count(T.bookingCode) from TPaymentEntity T where T.status in ?1 and T.trxDate between ?2 and ?3")
    Integer countByUserIdAndStatusAndTrxDate(List<Integer> status, LocalDate startDate, LocalDate endDate);

    @Query("select T FROM TPaymentEntity T where (?1 is null or T.userId.userId = ?1) and (?2 is null or T.status = ?2) and (?3 is null or T.bookingCode = ?3 or T.qrcodeExt = ?3 or T.qrcode = ?3) "
            + "AND (?4 IS NULL OR T.userId LIKE %?4% OR T.stt LIKE %?4% OR T.receiverAddress LIKE %?4% OR T.receiverName LIKE %?4% "
            + "OR T.receiverName LIKE %?4% OR T.receiverEmail LIKE %?4% OR T.senderAddress LIKE %?4% OR T.senderTelp LIKE %?4%) "
            + "AND (?5 IS NULL OR T.productSwCode.switcherEntity.switcherCode = ?5) "
            + "order by T.trxDate desc,T.trxTime desc")
    Page<TPaymentEntity> findAllByUserIdAndStatusAndBookingCode(String userId, Integer status, String bookingCode, String filter, Integer switcherCode, Pageable pageable);

    @Query("select count(T.bookingCode) from TPaymentEntity T where T.status in (?1) and (T.officeCode in (?4) or T.officeCode is null) and T.trxDate between ?2 and ?3 ")
    Integer countByUserIdAndStatusAndTrxDate(List<Integer> status, LocalDate startDate, LocalDate endDate, List<String> officeCode);

    @Query("select T FROM TPaymentEntity T where T.status = ?2 and (?1 is null or T.userId.userId = ?1) "
            + "and (?3 is null or T.pickupTimeId.idPickupTime = ?3) "
            + "and (?4 is null or T.pickupAddrId.postalCode.kecamatanEntity.areaDetailId = ?4) "
            + "and (?5 is null or T.pickupAddrId.postalCode.kecamatanEntity.kotaEntity.areaKotaId = ?5) "
            + "and (?6 is null or T.pickupDate = ?6) "
            + "order by T.userId.userId ")
    List<TPaymentEntity> findAllByUserAndStatusAndTimePickup(String userId, Integer status, Integer timePickupId, Integer areaDetailId, Integer areaKotaId, LocalDate pickupDate);

    //	@Query("SELECT T FROM TPaymentEntity  T WHERE T.pickupAddrId=?1")
    List<TPaymentEntity> findByPickupAddrIdInAndStatus(List<TPickupAddressEntity> pickupAddressId, Integer status);

    List<TPaymentEntity> findByPickupAddrIdPickupAddrIdAndStatus(Integer pickupAddressId, Integer status);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.pickupAddrId.pickupAddrId=?1 "
            + "AND T.status=?2 "
            + "AND (?3 IS NULL OR T.userId.userId=?3) "
            + "AND (?4 IS NULL OR T.pickupAddrId.postalCode.kecamatanEntity.areaDetailId=?4) "
            + "AND (?5 IS NULL OR T.pickupAddrId.postalCode.kecamatanEntity.kotaEntity.areaKotaId=?5) "
            + "AND (?6 IS NULL OR T.pickupTimeId.idPickupTime=?6) "
            + "AND (?7 IS NULL OR T.pickupDate=?7)")
    List<TPaymentEntity> findByPickupAddrIdPickupAddrIdAndStatusAndUserIdUserIdAndIdPostalCodeKecamatanEntityAreaDetailIdAndIdPostalCodeKecamatanEntityKotaEntityAreaKotaIdAndPickupTimeIdIdPickupTimeAndPickupDate(Integer pickupAddressId, Integer status, String userid, Integer areaDetailId, Integer areaKotaId, Integer pickupTimeId, LocalDate pickupDate);

    //	@Query("select T.pickupAddrId FROM TPaymentEntity T where T.status = ?2 and (?1 is null or T.userId.userId = ?1) "
//			+ "and (?3 is null or T.pickupTimeId.idPickupTime = ?3) "
//			+ "and (?4 is null or T.pickupAddrId.postalCode.kecamatanEntity.areaDetailId = ?4) "
//			+ "and (?5 is null or T.pickupAddrId.postalCode.kecamatanEntity.kotaEntity.areaKotaId = ?5) "
//			+ "and (?6 is null or T.pickupDate = ?6) "
//			+ " GROUP BY T.pickupAddrId order by T.userId.userId ")
    @Query(value = "SELECT tpa.pickup_addr_id FROM t_payment TP " +
            "INNER JOIN m_pickup_time mpt ON TP.pickup_time_id=mpt.id_pickup_time " +
            "INNER JOIN t_pickup_address tpa on TP.pickup_addr_id = tpa.pickup_addr_id " +
            "INNER JOIN m_postal_code mpc on tpa.id_postal_code = mpc.id_postal_code " +
            "INNER JOIN m_area_detail mad on mpc.area_detail_id = mad.area_detail_id " +
            "INNER JOIN m_area_kota mak on mad.area_kota_id = mak.area_kota_id " +
            "WHERE TP.status=?2 " +
            "AND (?1 IS NULL OR TP.user_id=?1) " +
            "AND (?3 IS NULL OR mpt.id_pickup_time=?3) " +
            "AND (?4 IS NULL OR mad.area_detail_id=?4) " +
            "AND (?5 IS NULL OR mak.area_kota_id=?5) " +
            "AND (?6 IS NULL OR TP.pickup_date=?6) GROUP BY TP.pickup_addr_id", nativeQuery = true)
    List<Integer> findPickupAddrIdAllByUserAndStatusAndTimePickup(String userId, Integer status, Integer timePickupId, Integer areaDetailId, Integer areaKotaId, LocalDate pickupDate);

    List<TPaymentEntity> findAllByNoTiket(String noTiket);

    @Query("SELECT T FROM TPaymentEntity T WHERE T.noTiket=?1 AND T.noTiket IS NOT NULL GROUP BY T.noTiket")
    List<TPaymentEntity> findAllByNoTiketGroupBy(String noTiket);

    @Query("SELECT new com.kahago.kahagoservice.model.projection.CountingProductProj(" +
            "COUNT(t.bookingCode), SUM(t.grossWeight), psc.name, psc.operatorSw, t.kantongPos) " +
            "FROM TPaymentEntity t LEFT JOIN t.productSwCode psc LEFT JOIN psc.switcherEntity sw " +
            "WHERE sw.switcherCode = 309 AND t.status BETWEEN 4 AND 9 AND t.kantongPos <> '0' AND t.datarekon LIKE ?1% " +
            "GROUP BY psc.name, psc.operatorSw, t.kantongPos")
    List<CountingProductProj> findManifest(String manifest);

    @Query("SELECT T FROM TPaymentEntity T "
            + "WHERE T.status = :status And "
            + "( :time between subtime(T.pickupTimeId.timeFrom,'00:10:00') and subtime(T.pickupTimeId.timeFrom,'00:07:00') "
            + "AND T.pickupDate<= :date) And (T.statusPay=0 or T.statusPay is null)")
    List<TPaymentEntity> findAllByStatusAndTimePickup(@Param("status") Integer status, @Param("time") LocalTime time, @Param("date") LocalDate date);

    @Query("SELECT COUNT(t.status) " +
            "FROM TPaymentEntity t WHERE t.trxDate = ?1 AND t.status IN (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,29,30) and (?2 IS NULL OR lower(t.origin) = ?2)")
    Long countStatusByDay(LocalDate date, String origin);

    @Query("SELECT COUNT(t.status) " +
            "FROM TPaymentEntity t WHERE MONTH(t.trxDate) = ?1 AND YEAR(t.trxDate) = ?2 " +
            "AND t.status IN (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,29,30) " +
            "AND (?3 IS NULL OR lower(t.origin) IN ?3)")
    Long countStatusByMonth(Integer month, Integer year, String origin);

    @Query("SELECT T FROM TPaymentEntity T WHERE (T.qrcode=?1 OR T.qrcodeExt=?1 OR T.bookingCode=?1 OR T.stt=?1) AND T.status in (?2) ")
    TPaymentEntity findByQrcodeAndStatus(String qrCode, List<Integer> status);

    @Query("SELECT T FROM TPaymentEntity T WHERE (T.qrcode=?1 OR T.qrcodeExt=?1 OR T.bookingCode=?1 OR T.stt=?1) AND T.origin=?3 AND T.status in (?2) ")
    TPaymentEntity findByQrcodeAndStatusAndOrigin(String qrCode, List<Integer> status, String origin);

    @Query("SELECT T FROM TPaymentEntity T where T.status = ?1 or T.status = ?2")
    List<TPaymentEntity> findAllByOfficeCode(Integer status, Integer statusPayment);

    Page<TPaymentEntity> findAllByStatus(Integer status, Pageable pageable);

    @Query("SELECT T FROM TPaymentEntity T WHERE T.status IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 23, 24, 25) AND T.trxDate BETWEEN ?1 and ?2")
    List<TPaymentEntity> getPaymentBytrxDate(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(t.grossWeight) " +
            "FROM TPaymentEntity t WHERE t.trxDate = ?1 AND t.status IN (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25) "
            + "AND (?2 IS NULL OR t.productSwCode.switcherEntity = ?2)")
    Long totalWeightByDay(LocalDate date, MSwitcherEntity switcherEntity);

    @Query("SELECT COUNT(t.status) " +
            "FROM TPaymentEntity t WHERE t.trxDate = ?1 AND t.status IN (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25) "
            + "AND (?2 IS NULL OR t.productSwCode.switcherEntity = ?2)")
    Long countStatusAndSwitcherByDay(LocalDate date, MSwitcherEntity switcherEntity);

    @Query("SELECT COUNT(t.status) " +
            "FROM TPaymentEntity t WHERE t.trxDate = ?1 AND t.status IN (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25)")
    Long countStatus(LocalDate date);

    @Query("SELECT T FROM TPaymentEntity T WHERE (?3 IS NULL OR T.status IN (?3)) AND T.trxDate BETWEEN ?1 and ?2 AND (?4 IS NULL OR T.userId.userId IN (?4)) "
            + "AND (?5 IS NULL OR T.productSwCode.switcherEntity.switcherCode IN (?5))")
    List<TPaymentEntity> getPaymentByVendor(LocalDate startDate, LocalDate endDate, List<Integer> status, List<String> userId, List<Integer> vendor);

    TPaymentEntity findByBookingCodeAndStatusAndUserIdUserId(String bookingCode, Integer status, String userId);

    @Query("SELECT count(T.bookingCode) FROM TPaymentEntity T WHERE T.status in (?1) AND (?2 IS NULL OR T.productSwCode.switcherEntity.switcherCode = ?2) AND (T.officeCode in (?3) OR T.officeCode IS NULL) ")
    Integer countByStatusAndSwitcherCodeAndOfficeCode(List<Integer> status, Integer switcherCode, List<String> officeCode);

    @Query("SELECT count(T.bookingCode) FROM TPickupDetailEntity TP "
            + "JOIN TP.bookId T "
            + "WHERE T.status in (?1) AND (?2 IS NULL OR T.productSwCode.switcherEntity.switcherCode = ?2) "
            + "AND (T.officeCode in (?3) OR T.officeCode IS NULL) "
            + "AND SUBTIME(STR_TO_DATE(CONCAT(T.pickupDate,' ',T.pickupTimeId.timeTo),'%Y-%m-%d %H:%i:%s'),'-01:00:00') "
            + "< STR_TO_DATE(CONCAT(?4,' ',?5), '%Y-%m-%d %H:%i:%s')")
    Integer countByStatusAndSwitcherCodeAndOfficeCodeAndProcessTime(List<Integer> status, Integer switcherCode, List<String> officeCode, LocalDate procesdate, LocalTime processtime);

    @Query("SELECT SUM(T.discountValue) FROM TPaymentEntity T WHERE T.discountCode = ?1")
    Double totalDiscountValueByDiscounCodeAndUserId(String discountCode);

    @Query(value = "SELECT SUM(d.discount_value) FROM t_discount d inner join t_payment p on d.no_tiket=p.no_tiket where d.discount_code=?1", nativeQuery = true)
    Double totalDiscountValueByDiscountCode(String discountCode);

    @Query("select t from TPaymentEntity t  where t.status in (:status) "
            + "and (:userId is null or (lower(t.userId.userId)=:userId or lower(t.senderEmail) = :userId or lower(t.receiverEmail) = :userId)) "
            + "and (:cari is null or (lower(t.senderName) like %:cari% "
            + "or lower(t.receiverName) like %:cari% "
            + "or lower(t.bookingCode) like %:cari% "
            + "or lower(t.stt) like %:cari%)) "
            + "and (:idSearch is null or lower(t.userId.accountNo) like %:idSearch%) "
            + "and (:sender is null or lower(t.userId.userId)= :sender or lower(t.senderEmail) = :sender) "
            + "and (:receiver is null or lower(t.receiverEmail) = :receiver) order by t.trxDate desc,trxTime desc")
    List<TPaymentEntity> findByStatusAndUserIdReceiverIdSenderIdNoPage(List<Integer> status, String userId, String cari, String idSearch, String sender, String receiver);

    @Query("SELECT T FROM TPaymentEntity T Where T.discountCode=?1 And T.userId.userId=?2 Group By T.noTiket")
    List<TPaymentEntity> findByDiscountCode(String discountCode, String userId);

    @Query("SELECT SUM(T.amount) FROM TPaymentEntity T WHERE T IN (?1)")
    Integer sumByBookList(List<TPaymentEntity> list);

    @Query("SELECT T FROM TPaymentEntity T WHERE "
            + " (T.bookingCode IN (?1) OR T.stt IN (?1)) AND T.status IN (?2) ")
    List<TPaymentEntity> findByBookingCodeOrStt(List<String> books, List<Integer> status);

    @Query(value = "select tp.* from t_lead_time lt " +
            "join t_payment tp on lt.booking_code = tp.booking_code " +
            "join m_product_switcher mp on tp.product_sw_code = mp.product_sw_code " +
            "where tp.trx_date between ?1 and ?2 and mp.switcher_code = ?4 and (?3 is null or mp.product_sw_code = ?3) " +
            "and lt.status = ?5 and (?6 is null or tp.user_id = ?6 ) and (?7 is null or tp.origin = ?7) ",
            countQuery = "select tp.* from t_lead_time lt " +
                    "join t_payment tp on lt.booking_code = tp.booking_code " +
                    "join m_product_switcher mp on tp.product_sw_code = mp.product_sw_code " +
                    "where tp.trx_date between ?1 and ?2 and mp.switcher_code = ?4 and (?3 is null or mp.product_sw_code = ?3) " +
                    "and lt.status = ?5 and (?6 is null or tp.user_id = ?6 ) and (?7 is null or tp.origin = ?7)", nativeQuery = true)
    Page<TPaymentEntity> getListBookingByLeadTimeStatus(LocalDate startDate, LocalDate endDate, Integer productSwCode, Integer switcherCode, String status, String userId, String areaId, Pageable pageable);

    @Query("SELECT CASE WHEN (COUNT(T) > 0) Then True ELSE False END FROM TPaymentEntity T "
            + "Where T.qrcodeExt=?1")
    boolean existByQRCodeExt(String qrcode);


    List<TPaymentEntity> findByStatusIn(List<Integer> status);

    @Query("SELECT COUNT(T.bookingCode) FROM TPaymentEntity T WHERE T.status IN (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25) AND (?1 IS NULL OR T.userId.userCategory = ?1) "
            + "AND T.trxDate = ?2 ")
    Integer countByStatusAndUserCategoryAndTrxDate(MUserCategoryEntity userCategory, LocalDate startDate);

    @Query("SELECT COUNT(t.status) " +
            "FROM TPaymentEntity t WHERE MONTH(t.trxDate) = ?1 AND YEAR(t.trxDate) = ?2 " +
            "AND t.status IN (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,29,30) " +
            "AND (?3 IS NULL OR t.userId.userCategory = ?3)")
    Integer countByStatusAndUserCategoryAndMonth(Integer month, Integer year, MUserCategoryEntity userCategory);

    @Query(value = "select count(booking_code) as total_trx from t_payment tp " +
            "join (select p.user_id as user_id,min(trx_date) as trx_date from t_payment p " +
            "	   group by p.user_id) mu on tp.user_id = mu.user_id " +
            "where MONTH(tp.trx_date) = ?1 and YEAR(tp.trx_date) = ?2 and tp.status in (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,29,30) and (?3 is null or MONTH(mu.trx_date) < ?3) " +
            "group by tp.trx_date", nativeQuery = true)
    List<Integer> getAverageTrxByTrxDateAndOldUser(Integer month, Integer year, Integer month1);

    @Query(value = "select count(tp.booking_code) from t_payment tp " +
            "inner join  (select p.user_id as user_id,min(p.trx_date) as trx_date from t_payment p " +
            "							group by p.user_id) mu on tp.user_id = mu.user_id " +
            "inner join m_user us on tp.user_id = us.user_id " +
            "where (?1 is null or MONTH(mu.trx_date) = ?1) and (?7 is null or YEAR(mu.trx_date) = ?7) " +
            "and (?3 is null or us.ref_num = ?3 ) " +
            "and (COALESCE(?4) is null or tp.trx_date in (?4)) " +
            "and (?5 is null or MONTH(tp.trx_date) = ?5 ) " +
            "and YEAR (tp.trx_date) = ?2 " +
            "and (COALESCE(?6) is null or us.user_category IN (?6)) " +
            "and (COALESCE(?8) is null or us.user_category NOT IN (?8)) " +
            "and tp.status in (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,30)", nativeQuery = true)
    Integer getTotalTrxByStatusUserAndRefNumAndTrxDate(Integer month, Integer year, String refNum, List<LocalDate> trxDate, Integer month1, List<Integer> userCategoryId, Integer year1, List<Integer> notUserCategory);

    /**
     * Count Data untuk
     * @param userCategoryId
     * @param notUserCategory
     * @param refNum
     * @param trxDate
     * @param startDate
     * @param endDate
     * @return
     */
    @Query("SELECT COUNT(tp) FROM TPaymentEntity tp " +
            "WHERE tp.status IN (3,4,5,6,7,8,9,11,20,22,24,25,26,27,28,30) " +
            "AND (COALESCE(?1) IS NULL OR tp.userId.userCategory.seqid IN (?1) ) " +
            "AND (COALESCE(?2) IS NULL OR tp.userId.userCategory.seqid NOT IN (?2) ) " +
            "AND (?3 IS NULL OR tp.userId.refNum=?3) " +
            "AND (?4 IS NULL OR tp.trxDate IN (?4)) AND (?5 IS NULL OR ?6 IS NULL OR tp.trxDate BETWEEN ?5 AND ?6)" +
            "")
    Integer countTPayment(List<Integer> userCategoryId, List<Integer> notUserCategory, String refNum, List<LocalDate> trxDate, LocalDate startDate, LocalDate endDate);

    //    Integer countTPayment(String month,Integer year,String refNum,List<LocalDate> trxDate,Integer month1,List<Integer> userCategoryId,Integer year1,List<Integer> notUserCategory,LocalDate minDate,LocalDate maxDate);
    @Query(value = "select count(tp.booking_code) from t_payment tp " +
            "inner join  (select p.user_id as user_id,min(p.trx_date) as trx_date from t_payment p " +
            "							group by p.user_id) mu on tp.user_id = mu.user_id " +
            "inner join m_user us on tp.user_id = us.user_id " +
            "where (?1 is null or DATE_FORMAT(mu.trx_date,'%Y-%m') < ?1) " +
            "and (?3 is null or us.ref_num = ?3 ) " +
            "and (COALESCE(?4) is null or tp.trx_date in (?4)) " +
            "and (?5 is null or MONTH(tp.trx_date) = ?5 ) " +
            "and YEAR (tp.trx_date) = ?2 " +
            "and (COALESCE(?6) is null or us.user_category IN (?6)) " +
            "and tp.status in (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,29,30)", nativeQuery = true)
    Integer getTotalTrxByStatusUserOldAndRefNumAndTrxDate(String monthYear, Integer year, String refNum, List<LocalDate> trxDate, Integer month1, List<Integer> userCategoryId);

    @Query(value = "select tp.user_id," +
            "tp.trx_date," +
            "sum(tp.amount) as revenue," +
            "count(tp.booking_code) as total_trx," +
            "tp.office_code from t_payment tp " +
            "inner join  (select p.user_id as user_id,min(p.trx_date) as trx_date from t_payment p  \r\n" +
            "				group by p.user_id) mu on tp.user_id = mu.user_id " +
            "inner join m_user us on tp.user_id = us.user_id " +
            "inner join m_product_switcher mp on tp.product_sw_code = mp.product_sw_code " +
            "where (?1 is null or MONTH(mu.trx_date) = ?1) and (?7 is null or YEAR(mu.trx_date) = ?7) " +
            "and (?3 is null or us.ref_num = ?3 ) " +
            "and (COALESCE(?4) is null or tp.trx_date in (?4)) " +
            "and (?5 is null or MONTH(tp.trx_date) = ?5 ) " +
            "and YEAR (tp.trx_date) = ?2 " +
            "and (COALESCE(?6) is null or us.user_category IN (?6)) " +
            "and tp.status in (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,29,30) " +
            "and (COALESCE(?8) is null or tp.office_code IN (?8)) " +
            "group by tp.user_id,tp.trx_date,tp.office_code",
            countQuery = "select tp.user_id,tp.trx_date," +
                    "sum(tp.amount) as revenue," +
                    "count(tp.booking_code) as total_trx,tp.office_code from t_payment tp " +
                    "inner join  (select p.user_id as user_id,min(p.trx_date) as trx_date from t_payment p  \r\n" +
                    "				group by p.user_id) mu on tp.user_id = mu.user_id " +
                    "inner join m_user us on tp.user_id = us.user_id " +
                    "inner join m_product_switcher mp on tp.product_sw_code = mp.product_sw_code " +
                    "where (?1 is null or MONTH(mu.trx_date) = ?1) and (?7 is null or YEAR(mu.trx_date) = ?7) " +
                    "and (?3 is null or us.ref_num = ?3 ) " +
                    "and (COALESCE(?4) is null or tp.trx_date in (?4)) " +
                    "and (?5 is null or MONTH(tp.trx_date) = ?5 ) " +
                    "and YEAR (tp.trx_date) = ?2 " +
                    "and (COALESCE(?6) is null or us.user_category IN (?6)) " +
                    "and tp.status in (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,30) " +
                    "and (COALESCE(?8) is null or tp.office_code IN (?8)) " +
                    "group by tp.user_id,tp.trx_date,tp.office_code", nativeQuery = true)
    Page<Object[]> getListPaymentByRefNumAndTrxDate(Integer month, Integer year, String refNum, List<LocalDate> trxDate, Integer month1, List<Integer> userCategoryId, Integer year1, List<String> officeCode, Pageable pageable);

    @Query("SELECT tp FROM TPaymentEntity tp ")
    Page<Object[]> getListPaymentWithFilter(String refNum, List<LocalDate> trxDate, List<Integer> userCategoryId, List<String> officeCode, Pageable pageable);

    @Query(value = "select tp.user_id,tp.trx_date," +
            "sum(tp.amount) as revenue," +
            "count(tp.booking_code) as total_trx,tp.office_code from t_payment tp " +
            "inner join  (select p.user_id as user_id,min(p.trx_date) as trx_date from t_payment p  " +
            "				group by p.user_id) mu on tp.user_id = mu.user_id " +
            "inner join m_user us on tp.user_id = us.user_id " +
            "inner join m_product_switcher mp on tp.product_sw_code = mp.product_sw_code " +
            "where (?1 is null or DATE_FORMAT(mu.trx_date,'%Y-%m') < ?1) " +
            "and (?3 is null or us.ref_num = ?3 ) " +
            "and (COALESCE(?4) is null or tp.trx_date in (?4)) " +
            "and (?5 is null or MONTH(tp.trx_date) = ?5 ) " +
            "and YEAR (tp.trx_date) = ?2 " +
            "and (COALESCE(?6) is null or us.user_category IN (?6)) " +
            "and tp.status in (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,29,30) " +
            "and (COALESCE(?7) is null or tp.office_code IN (?7)) " +
            "group by tp.user_id,tp.trx_date,tp.office_code",
            countQuery = "select tp.user_id,tp.trx_date," +
                    "sum(tp.amount) as revenue," +
                    "count(tp.booking_code) as total_trx,tp.office_code from t_payment tp " +
                    "inner join  (select p.user_id as user_id,min(p.trx_date) as trx_date from t_payment p  " +
                    "				group by p.user_id) mu on tp.user_id = mu.user_id " +
                    "inner join m_user us on tp.user_id = us.user_id " +
                    "inner join m_product_switcher mp on tp.product_sw_code = mp.product_sw_code " +
                    "where (?1 is null or DATE_FORMAT(mu.trx_date,'%Y-%m') < ?1)  " +
                    "and (?3 is null or us.ref_num = ?3 ) " +
                    "and (COALESCE(?4) is null or tp.trx_date in (?4)) " +
                    "and (?5 is null or MONTH(tp.trx_date) = ?5 ) " +
                    "and YEAR (tp.trx_date) = ?2 " +
                    "and (COALESCE(?6) is null or us.user_category IN (?6)) " +
                    "and tp.status in (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,29,30) " +
                    "and (COALESCE(?7) is null or tp.office_code IN (?7)) " +
                    "group by tp.user_id,tp.trx_date,tp.office_code", nativeQuery = true)
    Page<Object[]> getListPaymentByRefNumAndTrxDateUserOld(String dateMonth, Integer year, String refNum, List<LocalDate> trxDate, Integer month1, List<Integer> userCategoryId, List<String> officeCode, Pageable pageable);

    @Query(value = "select mp.switcher_code from t_payment tp " +
            "join m_product_switcher mp on tp.product_sw_code = mp.product_sw_code " +
            "where trx_date = ?2 and user_id= ?1 AND (?3 is null or office_code = ?3) "+
            "and tp.status in (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,29,30) " +
            "group by mp.switcher_code", nativeQuery = true)
    List<Integer> getTotalVendorByUserIdAndTrxDate(String userId, LocalDate trxDate,String office_code);

    @Query(value = "select tp.trx_date," +
            "sum(tp.amount) as revenue," +
            "count(tp.booking_code) as total_trx,tp.office_code from t_payment tp " +
            "inner join  (select p.user_id as user_id,min(p.trx_date) as trx_date from t_payment p  " +
            "				group by p.user_id) mu on tp.user_id = mu.user_id " +
            "inner join m_user us on tp.user_id = us.user_id " +
            "inner join m_product_switcher mp on tp.product_sw_code = mp.product_sw_code " +
            "where (?1 is null or MONTH(mu.trx_date) = ?1) and (?7 is null or YEAR(mu.trx_date) = ?7) " +
            "and (?3 is null or us.ref_num = ?3 ) " +
            "and (COALESCE(?4) is null or mu.trx_date in (?4)) " +
            "and (?5 is null or MONTH(tp.trx_date) = ?5 ) " +
            "and YEAR (tp.trx_date) = ?2 " +
            "and (COALESCE(?6) is null or us.user_category IN (?6)) " +
            "and tp.status in (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,29,30) " +
            "and (COALESCE(?8) is null or tp.office_code IN (?8)) " +
            "group by tp.trx_date,tp.office_code",
            countQuery = "select tp.trx_date," +
                    "sum(tp.amount) as revenue," +
                    "count(tp.booking_code) as total_trx,tp.office_code from t_payment tp " +
                    "inner join  (select p.user_id as user_id,min(p.trx_date) as trx_date from t_payment p  " +
                    "				group by p.user_id) mu on tp.user_id = mu.user_id " +
                    "inner join m_user us on tp.user_id = us.user_id " +
                    "inner join m_product_switcher mp on tp.product_sw_code = mp.product_sw_code " +
                    "where (?1 is null or MONTH(mu.trx_date) = ?1) and (?7 is null or YEAR(mu.trx_date) = ?7) " +
                    "and (?3 is null or us.ref_num = ?3 ) " +
                    "and (COALESCE(?4) is null or mu.trx_date in (?4)) " +
                    "and (?5 is null or MONTH(tp.trx_date) = ?5 ) " +
                    "and YEAR (tp.trx_date) = ?2 " +
                    "and (COALESCE(?6) is null or us.user_category IN (?6)) " +
                    "and tp.status in (3, 4, 5, 6, 7, 8, 9, 11, 20, 22, 24, 25,26,27,28,29,30) " +
                    "and (COALESCE(?8) is null or tp.office_code IN (?8)) " +
                    "group by tp.trx_date,tp.office_code", nativeQuery = true)
    Page<Object[]> getDetailTotalTrxFromNewUser(Integer month, Integer year, String refNum, List<LocalDate> trxDate, Integer month1, List<Integer> userCategoryId, Integer year1, List<String> officeCode, Pageable pageable);
    
    @Query("SELECT TP FROM TPaymentEntity TP WHERE (?1 IS NULL OR TP.bookingCode = ?1 OR TP.qrcodeExt = ?1) AND (?2 IS NULL OR TP.userId.userId = ?2) "
    		+ "AND TP.status IN ?3 AND TP.officeCode = ?4 AND (?5 IS NULL OR TP.productSwCode.switcherEntity.switcherCode = ?5) "
    		+ "ORDER BY TP.bookingCode")
    Page<TPaymentEntity> getPaymentByListStatusAndVendorAndUserId(String bookingCode,String userId,List<Integer> status,String officeCode,Integer switcherCode,Pageable pageable);
    @Query("SELECT TP FROM TPaymentEntity TP WHERE (?1 IS NULL OR TP.bookingCode = ?1 OR TP.qrcodeExt = ?1) AND (?2 IS NULL OR TP.userId.userId = ?2) "
    		+ "AND TP.status IN ?3 AND TP.officeCode = ?4 AND (?5 IS NULL OR TP.productSwCode.switcherEntity.switcherCode = ?5) "
    		+ "ORDER BY TP.bookingCode")
    TPaymentEntity getPaymentByListStatusAndVendorAndUserId(String bookingCode,String userId,List<Integer> status,String officeCode,Integer switcherCode);
    
    List<TPaymentEntity> findAllByTrxDateAndStatusAndProductSwCodeSwitcherEntity(LocalDate trxDate,Integer status,MSwitcherEntity switcherEntity);
}


